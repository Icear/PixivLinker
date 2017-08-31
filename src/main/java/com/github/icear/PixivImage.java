package com.github.icear;

/**
 * Created by icear.
 * 储存Pixiv作品图片
 */
public class PixivImage {
    /**
     * 获得作品的大小信息
     * @return ImageRect
     */
    public ImageRect getImageSize(){
        return new ImageRect();
    }

    /**
     * 获得作品的浏览量
     * @return 浏览量
     */
    public long getPageviews(){
        return 0;
    }

    /**
     * 获得作品的图片链接
     * @return URL链接
     */
    public String getImageUrl(){
        return null;
    }

}
