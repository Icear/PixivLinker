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
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icear.
 * 作品模块
 * 负责作品相关的操作
 */
public class PWork implements PixivWork{




    private int id;//id
    private String name;//作品名
    private LocalDateTime createTime;//创建时间
    private String[] workTag;//作品标签
    private String creativeTool;//创作工具
    private long pageviews;//作品浏览量
    private long thumbsUp;//作品赞数
    private byte[] thumbnail;//预览图
    private PixivImage[] workImage;//图片


    /**
     * 根据用户令牌和目标作品id生成PWork类
     * @param cookieToken cookie令牌
     * @param id 作品id
     * @return PWork对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PWork generatePWork(CookieStore cookieToken, int id) throws IOException{
        PWork pWork = new PWork();


        List<NameValuePair> paramaters = new ArrayList<>();

        paramaters.add(new BasicNameValuePair("mode","medium"));
        paramaters.add(new BasicNameValuePair("illust_id",String.valueOf(id)));

        try(CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieToken).build()){
            HttpEntity responseEntity = NetworkUtil.httpGet(httpClient, "https://www.pixiv.net/member_illust.php", paramaters);
            String response = EntityUtils.toString(responseEntity, Consts.UTF_8);

            /* 解析数据 */
            Document document = Jsoup.parse(response);
            Element workInfo = document.getElementsByAttributeValue("class","work-info").first();
            Element workSpace = document.getElementsByAttributeValue("class","works_display").first();


            //获得作品名
            Element workTitle = workInfo.getElementsByAttributeValue("class","title").first();
            pWork.setName(workTitle.html());

            //获得创建时间
            Element workMeta = workInfo.getElementsByAttributeValue("class","meta").first();
            pWork.setCreateTime(LocalDateTime.parse(workMeta.getAllElements().first().html()));

            //获得创作工具
            Element workTool = workInfo.getElementsByAttributeValue("class","tools").first();
            pWork.setCreativeTool(workTool.getAllElements().first().html());

            //获得作品浏览量
            Element workPageViews = workInfo.getElementsByAttributeValue("class","view-count").first();
            pWork.setPageviews(Long.parseLong(workPageViews.html()));

            //获得赞数
            Element workThumbsUp = workInfo.getElementsByAttributeValue("class","rated-count").first();
            pWork.setThumbsUp(Long.parseLong(workThumbsUp.html()));

            //获得作品标签
            List<String> tagList = new ArrayList<>();
            Element workTagContainer = document.getElementsByAttributeValue("class","tags-container").first();
            Elements tags = workTagContainer.getElementsByAttributeValue("class","text");
            for (Element tag :
                    tags) {
                tagList.add(tag.html());
            }
            pWork.setWorkTag((String[]) tagList.toArray());

            //获得预览图
            Element workThumbnailContainer = workInfo.getElementsByAttributeValue("class","_layout-thumbnail").first();
            Element thumbnail = workThumbnailContainer.getElementsByTag("img").first();
            HttpEntity thumbnailEntity = NetworkUtil.httpGet(httpClient,thumbnail.attr("src"),null);
            pWork.setThumbnail(ConvertUtil.toByteArray(thumbnailEntity.getContent()));

            //获得图片
            List<PixivImage> imageList = new ArrayList<>();

            //判断作品图片数量
            if(workSpace.getElementsByAttributeValue("data-click-category","read-more-to-manga-viewer").isEmpty()){
                //单张图片
                Element originImageContainer = document.getElementsByAttributeValue("class","_illust_modal _hidden ui-modal-close-box").first();
                Element originImage = originImageContainer.getElementsByAttributeValue("class","original-image").first();
                HttpEntity imageEntity = NetworkUtil.httpGet(httpClient,originImage.attr("data-src"),null);
                byte[] image = ConvertUtil.toByteArray(imageEntity.getContent());

                //获得图片尺寸
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image);
                BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                imageList.add(new PixivImage(image, bufferedImage.getWidth(), bufferedImage.getHeight(), originImage.attr("data-src")));

            }else{
                //多张图片
                imageList.add(new PixivImage(null,0,0,null));
            }
            pWork.setWorkImage((PixivImage[]) imageList.toArray());
        }
        return pWork;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    private void setWorkTag(String[] workTag) {
        this.workTag = workTag;
    }

    private void setCreativeTool(String creativeTool) {
        this.creativeTool = creativeTool;
    }

    private void setWorkImage(PixivImage[] workImage) {
        this.workImage = workImage;
    }

    private void setPageviews(long pageviews) {
        this.pageviews = pageviews;
    }

    private void setThumbsUp(long thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    private void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
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
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String[] getWorkTag() {
        return workTag;
    }

    @Override
    public long getPageviews() {
        return pageviews;
    }

    @Override
    public long getThumbsUp() {
        return thumbsUp;
    }

    @Override
    public byte[] getThumbnail() {
        return thumbnail;
    }

    @Override
    public String getCreativeTool() {
        return creativeTool;
    }



    @Override
    public PixivImage[] getWorkImage() {
        return workImage;
    }
}
