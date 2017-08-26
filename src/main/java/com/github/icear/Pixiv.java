package com.github.icear;

import com.github.icear.Util.NetworkUtil;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;


/**
 * Created by icear
 * 包入口
 *
 * 功能拆分：
 * 1、账号模块
 * 2、排行榜模块
 * 3、作品模块
 * 4、会员模块
 * 5、推荐模块（可选）
 */

//TODO 将读取到的多余数据适时地添加进构造器中，重载generate系列函数

public class Pixiv{
    private static Logger logger = LogManager.getLogger(Pixiv.class.getName());

    private static final int MODE_SHOW = 297;
    private static final int MODE_HIDE = 372;
    private static final int MODE_BOTH_SHOW_AND_HIDE = 887;

    /**
     * 以账号与密码登陆一个用户
     * 函数包含网络访问代码，建议放置在线程中执行
     * @param account 用户名或邮箱
     * @param password 密码
     * @return 返回登陆结果封装类
     */
    public static PixivLoginResult login(@NotNull String account,@NotNull String password){
        /*
         * get前置页，获取相关参数
         * 再post登陆
         * 检查登陆结果并返回
         */
        logger.debug("Start login");

        PixivLoginResult result = new PixivLoginResult();
        CookieStore cookieStore = new BasicCookieStore();//准备CookieStore容器用于储存登陆过程中生成的cookie令牌
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        try {
            /* 初始化Get部分 */
            List<NameValuePair> paramsHttpGet = new ArrayList<>();

            /* 生成Get参数 */
            paramsHttpGet.add(new BasicNameValuePair("lang","zh"));
            paramsHttpGet.add(new BasicNameValuePair("source","pc"));
            paramsHttpGet.add(new BasicNameValuePair("view_type","page"));
            paramsHttpGet.add(new BasicNameValuePair("ref","wwwtop_accounts_index"));

            /* 执行Get请求 */
            HttpEntity responseGet = NetworkUtil.httpGet(httpClient,"https://accounts.pixiv.net/login",paramsHttpGet);

            if(responseGet == null){
                //异常情况，没有接收到返回数据
                result.setSucceed(false);
                result.setErrorMessage("没有接收到返回的数据或Get请求失败");
                logger.info("Login result: failed, null response or Get request failed");
                return result;
            }

            //Get请求成功
            String htmlResponseGet = EntityUtils.toString(responseGet, Consts.UTF_8);
            logger.debug("Response: " + htmlResponseGet);



            /* 解析html */
            Document document = Jsoup.parse(htmlResponseGet);
            Element form = document.body().getElementsByAttributeValue("method","POST").first();//定位form位置
            String postKey = form.getElementsByAttributeValue("name","post_key").first().attr("value");//从form中获取input元素并取得值
            String source = form.getElementsByAttributeValue("name","source").first().attr("value");
            String returnTo = form.getElementsByAttributeValue("name","return_to").first().attr("value");



            /* 初始化Post部分 */
            List<NameValuePair> paramsHttpPost = new ArrayList<>();

            /* 生成Post参数 */
            paramsHttpPost.add(new BasicNameValuePair("lang","zh"));

            /* 生成PostData */
            List<NameValuePair> postList = new ArrayList<>();
            postList.add(new BasicNameValuePair("post_key",postKey));
            postList.add(new BasicNameValuePair("source",source));
            postList.add(new BasicNameValuePair("return_to",returnTo));
            postList.add(new BasicNameValuePair("ref","wwwtop_accounts_index"));
            postList.add(new BasicNameValuePair("pixiv_id",account));
            postList.add(new BasicNameValuePair("password",password));
            postList.add(new BasicNameValuePair("captcha",""));
            postList.add(new BasicNameValuePair("g_recaptcha_response",""));

            HttpEntity responsePost = NetworkUtil.httpPost(httpClient,"https://accounts.pixiv.net/api/login",paramsHttpPost,postList);
            if(responsePost == null){
                //异常情况，没有接收到返回数据
                result.setSucceed(false);
                result.setErrorMessage("没有接收到返回的数据或Post请求失败");
                return result;
            }
            String htmlResponsePost = EntityUtils.toString(responsePost, Consts.UTF_8);
            logger.debug("Response: " + htmlResponsePost);

            /* 分析结果 */
            /*
              json返回格式
              {
                  "error":false,
                  "message":"",
                  "body":{
                      "success":{
                          "return_to":"https:\/\/www.pixiv.net\/"
                      }
                  }
              }
             */
            Gson gson = new Gson();

            /* 将json转化为map操作 */
            //第一层
            Map<String,String> jsonLevel1 = gson.fromJson(htmlResponsePost,new TypeToken<Map<String,String>>(){}.getType());
            if (jsonLevel1 == null) {
                //数据处理异常
                result.setSucceed(false);
                result.setErrorMessage("数据处理失败1");
                logger.error("Fail to decode json response to level 1");
                logger.info("Login result: failed, Fail to decode json response to level 1");
                return null;
            }
            //解析第一层数据
            if(jsonLevel1.get("error").equals("true")){
                //服务器返回错误，登陆失败
                result.setSucceed(false);
                result.setErrorMessage("服务器返回错误：" + jsonLevel1.get("message") + jsonLevel1.getOrDefault("body",null));
                logger.info("Login result: failed, server reply an error with \"error\" = \"true\", " +  jsonLevel1.get("message") + jsonLevel1.getOrDefault("body",null));
                return result;
            }

            //第二层
            Map<String,String> jsonLevel2 = gson.fromJson(jsonLevel1.get("body"),new TypeToken<Map<String,String>>(){}.getType());
            if (jsonLevel2 == null) {
                //数据处理异常
                result.setSucceed(false);
                result.setErrorMessage("数据处理失败2");
                logger.error("Fail to decode json response to level 2");
                logger.debug("jsonLevel1: ");
                logger.debug(jsonLevel1);
                logger.info("Login result: failed, Fail to decode json response to level 2");
                return result;
            }
            //解析第二层数据
            String status = jsonLevel2.keySet().toArray(new String[1])[0];//关键字
            if(status.equals("success")){
                //登陆成功
                result.setSucceed(true);
                result.setPixivUser(PixivBuilder.generatePUser(cookieStore));
                logger.info("Login result: succeed");
            }

            //登陆失败
            Map<String,String> jsonLevel3 = gson.fromJson(jsonLevel2.get(status),new TypeToken<Map<String,String>>(){}.getType());//第三层
            if (jsonLevel3 == null) {
                //数据处理异常
                result.setSucceed(false);
                result.setErrorMessage("数据处理失败3");
                logger.error("Fail to decode json response to level 3");
                logger.debug("jsonLevel2: ");
                logger.debug(jsonLevel2);
                logger.info("Login result: failed, Fail to decode json response to level 3");
                return result;
            }

            result.setSucceed(false);
            String key = jsonLevel3.keySet().toArray(new String[1])[0];
            result.setErrorMessage("登陆失败，" + jsonLevel3.get(key));
            logger.info("Login result: failed, server reply an error , " + key + " " + jsonLevel3.get(key));
            return result;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                //错误处理
                logger.error("Fail to close httpClient object");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 以指定用户的身份读取目标会员的信息
     * @param user 用户
     * @param pixivId 目标会员的ID
     * @return 会员信息
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivMember getMember(@NotNull PixivUser user, @NotNull int pixivId) throws IOException {
        return PixivBuilder.generatePMember(user.getCookieToken(),pixivId);
    }

    /**
     * 获得用户的关注列表
     * @param user 用户
     * @param mode 读取模式,传入预定义的常量，MODE_SHOW表示读取公开的关注列表，
     *             MODE_HIDE表示读取隐藏的关注列表，MODE_BOTH_SHOW_AND_HIDE表示读取两者（公开部分在前）
     * @return 用户关注的会员的列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivMember[] getConcernedMemberList(@NotNull PixivUser user, @NotNull int mode) throws IOException {
        //登陆身份下查看自己的关注列表，公开关注
        List<PMember> pMembers = new ArrayList<>();

        //准备参数
        List<NameValuePair> params = new ArrayList<>();

        switch (mode){
            case MODE_BOTH_SHOW_AND_HIDE:
                logger.info("mode: MODE_BOTH_SHOW_AND_HIDE");
                /*获得公开关注的用户*/
                params.add(new BasicNameValuePair("type", "user"));
                params.add(new BasicNameValuePair("rest", "show"));
                getDataAndParsePMember(user.getCookieToken(), pMembers, params);

                params.clear();

                /*获得隐藏关注的用户*/
                params.add(new BasicNameValuePair("type", "user"));
                params.add(new BasicNameValuePair("rest", "hide"));
                getDataAndParsePMember(user.getCookieToken(), pMembers, params);
                break;
            case MODE_SHOW:
                logger.info("mode: MODE_SHOW");
                /*获得公开关注的用户*/
                params.add(new BasicNameValuePair("type", "user"));
                params.add(new BasicNameValuePair("rest", "show"));
                getDataAndParsePMember(user.getCookieToken(), pMembers, params);
                break;
            case MODE_HIDE:
                logger.info("mode: MODE_HIDE");
                /*获得隐藏关注的用户*/
                params.add(new BasicNameValuePair("type", "user"));
                params.add(new BasicNameValuePair("rest", "hide"));
                getDataAndParsePMember(user.getCookieToken(), pMembers, params);
                break;
        }

        /* 返回 */
        logger.info("get User Concerned List result: " + pMembers.size());
        return (PMember[]) pMembers.toArray();
    }

    /**
     * 以指定用户的身份获得某个成员的关注列表
     * @param user 用户
     * @param member 要获取列表的目标成员
     * @return 用户关注列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivMember[] getConcernedMemberList(@NotNull PixivUser user, @NotNull PixivMember member) throws IOException{
        /* 准备容器 */
        List<PMember> pMembers = new ArrayList<>();

        /* 准备参数 */
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("type","user"));
        params.add(new BasicNameValuePair("id", String.valueOf(member.getId())));

        /* 开始读取 */
        getDataAndParsePMember(user.getCookieToken(), pMembers, params);

        /* 返回 */
        logger.info("get Member Concerned List result: " + pMembers.size());
        return (PMember[]) pMembers.toArray();
    }

    /**
     * 获得用户的粉丝列表
     * @param user 用户
     * @return 用户的粉丝会员的列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivMember[] getFollowList(@NotNull PixivUser user) throws IOException{
        //TODO 未完成
        return new PMember[0];
    }

    /**
     * 以指定用户的身份读取目标会员的粉丝列表
     * @param user 用户
     * @param member 目标会员
     * @return 目标会员的粉丝列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivMember[] getFollowList(@NotNull PixivUser user, @NotNull PixivMember member) throws IOException{
        //TODO 未完成
        return new PMember[0];
    }

    /**
     * 获取用户的收藏列表
     * @param user 用户
     * @return 收藏的作品列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivWork[] getCollectionList(@NotNull PixivUser user) throws IOException{
        //TODO 未完成
        return new PWork[0];
    }

    /**
     * 以指定用户的身份读取获取目标会员的收藏列表
     * @param user 用户
     * @param member 目标会员
     * @return 目标会员的收藏列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivWork[] getCollectionList(@NotNull PixivUser user, @NotNull PixivMember member) throws IOException{
        //TODO 未完成
        return new PWork[0];
    }

    /**
     * 获取用户的作品列表
     * @param user 用户
     * @return 用户的作品列表
     * @throws IOException 网络IO或数据处理异常
     */
    public static PixivWork[] getWorkList(@NotNull PixivUser user) throws IOException{
        //TODO 未完成
        return new PWork[0];
    }

    /**
     * 获得用户作品列表
     * @return 会员
     */
    public static PixivWork[] getWorkList(@NotNull PixivUser user, @NotNull PixivMember member) throws IOException{
        //TODO 未完成
        return new PWork[0];
    }




    /**
     * 携带令牌发送Get请求携带指定params参数，读取数据并解析PMember
     * @param cookieToken cookie令牌
     * @param pMembers PMember结果容器
     * @param params get参数
     * @throws IOException 网络IO或数据处理异常
     */
    private static void getDataAndParsePMember(CookieStore cookieToken, List<PMember> pMembers
            , List<NameValuePair> params) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()) {
            //获得数据
            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient, "https://www.pixiv.net/bookmark.php", params);
            String response = EntityUtils.toString(responseEntity, Consts.UTF_8);

            parsePMemberFromHtml(cookieToken, pMembers, response);//解析当前页的用户


            //检查数据分页，若存在则开始分页
            Document document = Jsoup.parse(response);//解析html
            Elements pageIndexElements = document.getElementsByAttributeValue("class", "_pager-complex");//尝试获取分页元素

            if (!pageIndexElements.isEmpty()) {//检查是否有分页元素
                //有分页，获取下一页
                Element pageIndexElement = pageIndexElements.first();//获取
                Elements nextPages = pageIndexElement.getElementsByAttributeValue("rel", "next");//尝试获取next按钮元素
                while (!nextPages.isEmpty())//检查是否有next按钮，有则继续，无则跳出
                {
                    Element nextPage = nextPages.first();//获取
                    String nextPageLink = nextPage.attr("href");//获得链接信息
                    if (nextPageLink == null) {
                        //没有获得链接，错误处理
                        logger.error("error during get next page Link, null nextPageLink");
                        logger.debug("Page Complex Element:");
                        logger.debug(pageIndexElements.first());
                        break;
                    }
                    responseEntity = NetworkUtil.httpGet(httpClient, nextPageLink, null);//访问下一页
                    response = EntityUtils.toString(responseEntity, Consts.UTF_8);

                    parsePMemberFromHtml(cookieToken, pMembers, response);//解析当前页的用户

                    //获取下一页
                    document = Jsoup.parse(response);
                    pageIndexElements = document.getElementsByAttributeValue("class", "_pager-complex");//尝试获取分页元素
                    pageIndexElement = pageIndexElements.first();//获取
                    nextPages = pageIndexElement.getElementsByAttributeValue("rel", "next");//尝试获取next按钮元素
                }
            }
        }
    }

    /**
     * 解析html将获得的用户转化为PMember数组
     * @param pMembers 结果容器
     * @param html 格式化的html数据
     */
    private static void parsePMemberFromHtml(CookieStore cookieToken, List<PMember> pMembers, String html) throws IOException {
        Document document = Jsoup.parse(html);
        Element memberContainer = document.getElementsByAttributeValue("class", "members").first();//获得储存有member的容器
        Elements memberElements = memberContainer.getElementsByTag("li");//获得member的列

        //循环处理每一个member元素的信息
        for (Element memberElement :
                memberElements) {
            Element userDataElement = memberElement.getElementsByAttributeValue("class", "userdata").first();//获得userData-DIV元素
            Element userDataLinkElement = userDataElement.getElementsByAttributeValue("class","ui-profile-popup").first();
            pMembers.add(PixivBuilder.generatePMember(cookieToken, Integer.parseInt(userDataLinkElement.attr("data-user_id"))));
        }
    }



}
