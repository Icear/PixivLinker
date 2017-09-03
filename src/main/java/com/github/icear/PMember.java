package com.github.icear;

import com.github.icear.Util.ConvertUtil;
import com.github.icear.Util.NetworkUtil;
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

class PMember implements PixivMember {

    private Logger logger = LogManager.getLogger(PMember.class.getName());

    private int id;
    private String name;
    private byte[] image;


    /**
     * 根据用户令牌和目标会员id生成PMember类
     * @param cookieToken cookie令牌
     * @param id 会员id
     * @return PUser对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PMember generatePMember(CookieStore cookieToken, int id) throws IOException {
        PMember pMember = new PMember();

        pMember.setId(id);//保存id

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
            pMember.setName(userNameElement.html());//保存name

            String imageUrl = userImageElement.attr("src");
            HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,imageUrl,null);
            if(imageEntity != null){
                pMember.setImage(ConvertUtil.toByteArray(imageEntity.getContent()));//保存image
            }
        }

        return pMember;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setImage(byte[] image) {
        this.image = image;
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
}
