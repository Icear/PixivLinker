package com.github.icear.Util;

import com.sun.istack.internal.NotNull;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by icear.
 */
public class NetworkUtil {
    private static Logger logger = LogManager.getLogger(NetworkUtil.class.getName());

    public static HttpEntity httpGet(@NotNull CloseableHttpClient httpClient, @NotNull String url, @NotNull Iterable<? extends NameValuePair> parameters) {

        //准备Get参数
        String strParams = null;//转换为键值对
        try {
            strParams = EntityUtils.toString(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO 解码失败错误处理
        }

        //生成GET请求
        HttpGet httpGet = new HttpGet(url + "?" + strParams);

        //执行GET请求
        logger.debug("Execute httpGet to " + httpGet.getURI());
        return executeRequest(httpClient,httpGet);
    }

    public static HttpEntity httpPost(@NotNull CloseableHttpClient httpClient, @NotNull String url, @NotNull Iterable<? extends NameValuePair> parameters, Iterable<? extends NameValuePair> postList){

        //准备POST参数
        String strParam = null;//转化键值对
        try {
            strParam = EntityUtils.toString(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO 解码失败错误处理
        }

        //准备POST数据
        UrlEncodedFormEntity postData = new UrlEncodedFormEntity(postList, Consts.UTF_8);

        //生成POST请求
        HttpPost httpPost = new HttpPost(url + "?" + strParam);
        httpPost.setEntity(postData);//设置postData

        //执行Post请求
        logger.debug("Execute httpPost to " + httpPost.getURI());
        return executeRequest(httpClient,httpPost);
    }

    private static HttpEntity executeRequest(CloseableHttpClient httpClient, HttpUriRequest httpRequest){
        try (CloseableHttpResponse httpPostResponse = httpClient.execute(httpRequest)) {
            if (httpPostResponse.getStatusLine().getStatusCode() != 200) {
                //TODO 请求失败处理
                return null;
            }
            //请求成功
            return httpPostResponse.getEntity();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO 错误处理
            return null;
        }
    }
}
