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

/**
 * Created by icear.
 * 会员模块
 * 负责获取与会员相关的数据以及关联操作
 */

public interface PixivMember{

    /**
     * 获得会员id即pixivID
     * @return pixivID
     */
    int getId();//获得会员id即pixivID

    /**
     * 获得会员昵称
     * @return 会员昵称
     */
    String getName();//获得会员昵称

    /**
     * 获得会员头像
     * @return 会员头像
     */
    byte[] getImage();//获得会员头像

//    /**
//     * 获得会员关注列表
//     * @return 被关注的会员的数组
//     */
//    PixivMember[] readConcernedList() throws IOException;//获得会员关注列表
//
//    /**
//     * 获得会员粉丝列表
//     * @return 粉丝会员的数组
//     */
//    PixivMember[] getFollowedList();//获得会员粉丝列表
//
//    /**
//     * 获得会员收藏作品列表
//     * @return 会员收藏的作品的数组
//     */
//    PixivWork[] getCollectionList();//获得会员收藏作品列表
//
//    /**
//     * 获得用户作品列表
//     * @return 会员
//     */
//    PixivWork[] getWorkList();//获得会员作品列表
}
