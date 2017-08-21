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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class PMember implements PixivMember {

    private int id;
    private String name;
    private byte[] image;

    PMember(CookieStore cookieToken, int id){
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
            this.id = id;
            this.name = userNameElement.html();

            String imageUrl = userImageElement.attr("src");
            HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,imageUrl,null);
            if(imageEntity != null){
                this.image = ConvertUtil.toByteArray(imageEntity.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public PWork[] getCollectionList() {
        return new PWork[0];
    }

    @Override
    public PWork[] getWorkList() {
        return new PWork[0];
    }
}
