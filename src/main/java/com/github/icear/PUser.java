package com.github.icear;

import com.github.icear.Util.ConvertUtil;
import com.github.icear.Util.NetworkUtil;
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
import org.jsoup.select.Elements;

import java.io.*;


class PUser implements PixivUser{

    private static Logger logger = LogManager.getLogger(PUser.class.getName());

    private CookieStore cookieToken;//cookie令牌
    private int id;
    private String name;
    private byte[] image;


    /**
     * 构造函数
     * 接受有效的Cookie表，生成对应用户的对象
     * @param cookieToken 传入登陆成功后的CookieStore
     * @exception IOException 网络访问或数据处理时出现异常
     */
    PUser(CookieStore cookieToken) throws IOException {
        this.cookieToken = cookieToken;
        //使用cookie进入P站首页，即可获得用户信息
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build();//生成httpClient
        HttpEntity responseEntity = NetworkUtil.httpGet(httpClient,"https://www.pixiv.net/",null);
        if(responseEntity == null){
            //空回复或遇到问题
            logger.error("User init failed, null response");
            throw new NullPointerException();
        }

        String response = EntityUtils.toString(responseEntity, Consts.UTF_8);
        Document document = Jsoup.parse(response);
        Element userProfile = document.getElementsByAttributeValue("class","profile").first();
        Element userImageContainer = userProfile.getElementsByAttributeValue("class","_user-icon size-40 cover-texture js-click-trackable-later").first();
        Element userInfoContainer = userProfile.getElementsByAttributeValue("class","user-name js-click-trackable-later").first();

        //获得用户ID
        id = Integer.parseInt(userInfoContainer.attr("href").substring("https://www.pixiv.net/member.php?id=".length() - 1));//截取id
        //获得用户名
        name = userInfoContainer.html();

        String imgaeUrlTemp = userImageContainer.attr("style");
        String imageUrl = imgaeUrlTemp.substring("background-image: url(".length() -1,imgaeUrlTemp.length() - 2);//截取
        HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,imageUrl,null);
        if(imageEntity != null){
            //获得用户头像
            image = ConvertUtil.toByteArray(imageEntity.getContent());
        }
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

    @Override
    public PMember[] getConcernedList() {
        return new PMember[0];
    }

    @Override
    public PMember[] getFollowedList() {
        return new PMember[0];
    }

    @Override
    public PixivWork[] getCollectionList() {
        return new PixivWork[0];
    }

    @Override
    public PixivWork[] getWorkList() {
        return new PixivWork[0];
    }
}
