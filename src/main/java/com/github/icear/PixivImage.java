package com.github.icear;

/**
 * Created by icear.
 * 储存Pixiv作品图片
 */
public class PixivImage {

    private byte[] image;//图片二进制数据
    private int width;//图片宽度
    private int length;//图片长度
    private String imageUrl;//图片原始链接

    PixivImage(byte[] image, int width, int length, String imageUrl) {
        this.image = image;
        this.width = width;
        this.length = length;
        this.imageUrl = imageUrl;
    }

    /**
     * 获得图片的原始链接
     * @return URL链接
     */
    public String getImageUrl(){
        return imageUrl;
    }

    /**
     * 获得图片二进制数据
     * @return 图片二进制数据
     */
    public byte[] getImage(){
        return image;
    }

    /**
     * 获得图片宽度
     * @return 图片宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获得图片长度
     * @return 图片长度
     */
    public int getLength() {
        return length;
    }

}
