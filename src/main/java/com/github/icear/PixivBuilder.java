package com.github.icear;

import com.github.icear.Util.ConvertUtil;
import com.github.icear.Util.NetworkUtil;
import com.sun.istack.internal.NotNull;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
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

/**
 * Created by icear.
 */
class PixivBuilder {

    private static Logger logger = LogManager.getLogger(PixivBuilder.class.getName());

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

    /**
     * 根据用户令牌和目标会员id生成PMember类
     * @param cookieToken cookie令牌
     * @return PUser对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PMember generatePMember(CookieStore cookieToken, int id) throws IOException {
        String name = null;
        byte[] image = null;

        //根据id进行网络访问，获取作者信息
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("id",String.valueOf(id)));

        try(CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()) {
            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient,"https://www.pixiv.net/member.php",parameters);
            String response = EntityUtils.toString(responseEntity, Consts.UTF_8);

            //解析数据
            Document document = Jsoup.parse(response);
            Element userContainer = document.getElementsByAttributeValue("class","_unit profile-unit").first();
            Element userElement = userContainer.getElementsByAttributeValue("class","user-link").first();
            Element userImageElement = userElement.getElementsByAttributeValue("class","user-image").first();
            Element userNameElement = userElement.getElementsByTag("h1").first();
            name = userNameElement.html();

            String imageUrl = userImageElement.attr("src");
            HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,imageUrl,null);
            if(imageEntity != null){
                image = ConvertUtil.toByteArray(imageEntity.getContent());
            }
        }

        return new PMember(id,name,image);
    }







}
