package com.github.icear;

import com.github.icear.Util.ConvertUtil;
import com.github.icear.Util.NetworkUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * Created by icear.
 * 储存Pixiv作品图片
 */
public class PixivImage {


    private byte[] image;//图片二进制数据
    private int width;//图片宽度
    private int height;//图片高度
    private String imageUrl;//图片原始链接

    static PixivImage generatePiivImage(CookieStore cookieToken, String imageUrl) throws IOException {
        PixivImage pixivImage = new PixivImage();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()) {
            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient, imageUrl, null);
            pixivImage.setImageUrl(imageUrl);
            pixivImage.setImage(ConvertUtil.toByteArray(responseEntity.getContent()));
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pixivImage.getImage());
            Image image = ImageIO.read(byteArrayInputStream);
            pixivImage.setWidth(image.getWidth(null));
            pixivImage.setHeight(image.getHeight(null));
            byteArrayInputStream.close();
        }
        return pixivImage;
    }

    private PixivImage() {}

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    /**
     * 获得图片的原始链接
     *
     * @return URL链接
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 获得图片二进制数据
     *
     * @return 图片二进制数据
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * 获得图片宽度
     *
     * @return 图片宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获得图片长度
     *
     * @return 图片长度
     */
    public int getHeight() {
        return height;
    }

}
