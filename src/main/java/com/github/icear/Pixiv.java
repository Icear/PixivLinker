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


public class Pixiv{
    private static Logger logger = LogManager.getLogger(Pixiv.class.getName());
    /**
     * 登陆函数
     * 传入用户名与密码执行登陆，函数包含网络访问代码，建议放置在线程中执行
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
                result.setPixivUser(new PUser(cookieStore));
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


}
