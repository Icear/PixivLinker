package com.github.icear;

import com.github.icear.Util.ConvertUtil;
import com.github.icear.Util.NetworkUtil;
import com.sun.istack.internal.NotNull;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

//TODO 修改网络访问返回空值情况的异常处理
class PUser implements PixivUser{

    private static Logger logger = LogManager.getLogger(PUser.class.getName());

    private CookieStore cookieToken;//cookie令牌
    private int id;//PixivId
    private String name;//昵称
    private byte[] image;//头像


    /**
     * 构造一个PUser对象，传入所需的信息
     * @param cookieToken cookie令牌
     * @param id pixivId
     * @param name 昵称
     * @param image 头像
     */
    private PUser(CookieStore cookieToken, @NotNull int id,@NotNull String name, byte[] image){
        this.cookieToken = cookieToken;
        this.id = id;
        this.name = name;
        this.image = image;
    }

    /**
     * 根据用户令牌生成PUser类
     * @param cookieToken cookie令牌
     * @return PUser对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PUser generatePUser(@NotNull CookieStore cookieToken) throws IOException {
        int id = -1;
        String name = null;
        byte[] image = null;

        //使用cookie进入P站首页，即可获得用户信息
        try(CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()){//生成httpClient
            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient,"https://www.pixiv.net/",null);
            String response = EntityUtils.toString(responseEntity, Consts.UTF_8);
            Document document = Jsoup.parse(response);
            Element userProfile = document.getElementsByAttributeValue("class","profile").first();
            Element userImageContainer = userProfile.getElementsByAttributeValue("class","_user-icon size-40 cover-texture js-click-trackable-later").first();
            Element userInfoContainer = userProfile.getElementsByAttributeValue("class","user-name js-click-trackable-later").first();

            //获得用户ID

            id = Integer.parseInt(userInfoContainer.attr("href").substring("https://www.pixiv.net/member.php?id=".length() - 1));//截取id
            //获得用户名
            name = userInfoContainer.html();

            String imageUrlTemp = userImageContainer.attr("style");
            String imageUrl = imageUrlTemp.substring("background-image: url(".length() - 1,imageUrlTemp.length() - 2);//截取
            HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,imageUrl,null);
            if(imageEntity != null){
                //获得用户头像
                image = ConvertUtil.toByteArray(imageEntity.getContent());
            }
        }
        return new PUser(cookieToken,id,name,image);
    }

    @Override
    public CookieStore getCookieToken() {
        return cookieToken;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getImage() {
        return image;
    }

//    @Override
//    public PMember[] readConcernedList() throws IOException {
//        //登陆身份下查看自己的关注列表，公开关注
//        List<PMember> pMembers = new ArrayList<>();
//
//        /*获得公开关注的用户*/
//        readConcernedList(pMembers,true);
//        /*获得隐藏关注的用户*/
//        readConcernedList(pMembers,false);
//
//        /* 返回 */
//        logger.info("get Concerned List result: " + pMembers.size());
//        return (PMember[]) pMembers.toArray();
//    }

//    @Override
//    public PMember[] getShowedConcernedList() throws IOException {
//        //登陆身份下查看自己的关注列表，公开关注
//        List<PMember> pMembers = new ArrayList<>();
//
//        /*获得公开关注的用户*/
//        readConcernedList(pMembers,true);
//
//        /* 返回 */
//        logger.info("get Showed Concerned List result: " + pMembers.size());
//        return (PMember[]) pMembers.toArray();
//    }
//
//    @Override
//    public PMember[] getHiddenConcernedList() throws IOException {
//        //登陆身份下查看自己的关注列表，公开关注
//        List<PMember> pMembers = new ArrayList<>();
//
//        /*获得隐藏关注的用户*/
//        readConcernedList(pMembers,false);
//
//        /* 返回 */
//        logger.info("get Showed Concerned List result: " + pMembers.size());
//        return (PMember[]) pMembers.toArray();
//    }
//
//    @Override
//    public PMember[] getFollowedList() {
//        return new PMember[0];
//    }
//
//    @Override
//    public PWork[] getCollectionList() {
//        return new PWork[0];
//    }
//
//    @Override
//    public PWork[] getWorkList() {
//        return new PWork[0];
//    }

//    /**
//     * 读取用户关注的用户
//     * @param pMembers 容纳结果的List容器
//     * @param isShowedPartion 读取公开关注用户或非公开关注用户
//     * @throws IOException 网络访问或数据解析异常
//     */
//    void readConcernedList(List<PMember> pMembers,boolean isShowedPartion) throws IOException {
//
//        //准备参数
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("type","user"));
//
//        if(isShowedPartion){
//            params.add(new BasicNameValuePair("rest","show"));
//        }else
//        {
//            params.add(new BasicNameValuePair("rest","hide"));
//        }
//
//        try(CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()){
//            //获得数据
//            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient,"https://www.pixiv.net/bookmark.php",params);
//            String response = EntityUtils.toString(responseEntity, Consts.UTF_8);
//
//            parsePMemberFromHtml(pMembers,response);//解析当前页的用户
//
//
//            //检查数据分页，若存在则开始分页
//            Document document = Jsoup.parse(response);//解析html
//            Elements pageIndexElements = document.getElementsByAttributeValue("class","_pager-complex");//尝试获取分页元素
//
//            if (!pageIndexElements.isEmpty()) {//检查是否有分页元素
//                //有分页，获取下一页
//                Element pageIndexElement = pageIndexElements.first();//获取
//                Elements nextPages = pageIndexElement.getElementsByAttributeValue("rel", "next");//尝试获取next按钮元素
//                while(!nextPages.isEmpty())//检查是否有next按钮，有则继续，无则跳出
//                {
//                    Element nextPage = nextPages.first();//获取
//                    String nextPageLink = nextPage.attr("href");//获得链接信息
//                    if(nextPageLink == null){
//                        //没有获得链接，错误处理
//                        logger.error("error during get next page Link, null nextPageLink");
//                        logger.debug("Page Complex Element:");
//                        logger.debug(pageIndexElements.first());
//                        break;
//                    }
//                    responseEntity = NetworkUtil.httpGet(httpClient,nextPageLink,null);//访问下一页
//                    response = EntityUtils.toString(responseEntity,Consts.UTF_8);
//
//                    parsePMemberFromHtml(pMembers,response);//解析当前页的用户
//
//                    //获取下一页
//                    document = Jsoup.parse(response);
//                    pageIndexElements = document.getElementsByAttributeValue("class","_pager-complex");//尝试获取分页元素
//                    pageIndexElement = pageIndexElements.first();//获取
//                    nextPages = pageIndexElement.getElementsByAttributeValue("rel", "next");//尝试获取next按钮元素
//                }
//            }
//        }
//    }
//
//    /**
//     * 解析html将获得的用户转化为PMember数组
//     * @param pMembers 结果容器
//     * @param html 格式化的html数据
//     */
//    void parsePMemberFromHtml(List<PMember> pMembers,String html){
//        Document document = Jsoup.parse(html);
//        Element memberContainer = document.getElementsByAttributeValue("class","members").first();//获得储存有member的容器
//        Elements memebrElements = memberContainer.getElementsByTag("li");//获得member的列
//
//        //循环处理每一个member元素的信息
//        for (Element memberElement :
//               memebrElements ) {
//            Element element = memberElement.getElementsByAttributeValue("name","id[]").first();
//            pMembers.add(new PMember(cookieToken,Integer.parseInt(element.attr("value"))));
//        }
//    }
}
