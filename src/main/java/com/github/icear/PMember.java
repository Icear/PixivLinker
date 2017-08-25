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

    PMember(int id, String name, byte[] image){
        this.id = id;
        this.name = name;
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
