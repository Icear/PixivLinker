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
//TODO 修正EXCEPTION的抛出位置
/**
 * Created by icear.
 */
public class NetworkUtil {
    private static Logger logger = LogManager.getLogger(NetworkUtil.class.getName());

    public static HttpEntity httpGet(@NotNull CloseableHttpClient httpClient, @NotNull String url, Iterable<? extends NameValuePair> parameters) {

        //准备Get参数
        String strParams = parameters != null?("?" + generateParameters(parameters)):null;//转换为键值对

        //生成Get请求
        HttpGet httpGet = new HttpGet(url + strParams);

        //执行Get请求
        logger.debug("Execute httpGet to " + httpGet.getURI());
        return executeRequest(httpClient,httpGet);
    }

    public static HttpEntity httpPost(@NotNull CloseableHttpClient httpClient, @NotNull String url, Iterable<? extends NameValuePair> parameters, Iterable<? extends NameValuePair> postList){

        //准备Post参数
        String strParam = parameters != null?("?" + generateParameters(parameters)):null;//转换为键值对

        //准备Post数据
        UrlEncodedFormEntity postData = new UrlEncodedFormEntity(postList, Consts.UTF_8);

        //生成Post请求
        HttpPost httpPost = new HttpPost(url + strParam);
        httpPost.setEntity(postData);//设置postData

        //执行Post请求
        logger.debug("Execute httpPost to " + httpPost.getURI());
        return executeRequest(httpClient,httpPost);
    }

    private static String generateParameters(@NotNull Iterable<? extends NameValuePair> parameters){
        try {
            return EntityUtils.toString(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        } catch (IOException e) {
            //解码失败错误处理
            logger.error("Parameter encoding error");
            e.printStackTrace();
            logger.debug("parameter: ");
            logger.debug(parameters);
            return null;
        }
    }

    private static HttpEntity executeRequest(CloseableHttpClient httpClient, HttpUriRequest httpRequest){
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                //请求失败处理
                logger.error("Network request error ,response code: " + httpResponse.getStatusLine().getStatusCode());
                logger.debug("Request: ");
                logger.debug(httpRequest);
                logger.debug("Response: ");
                logger.debug(httpResponse);
                return null;
            }else {
                return httpResponse.getEntity();//请求成功
            }
        } catch (IOException e) {
            //错误处理
            logger.error("Network request error during executing request");
            e.printStackTrace();
            logger.debug("Request: ");
            logger.debug(httpRequest);
            return null;
        }
    }
}
