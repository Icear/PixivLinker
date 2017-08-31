package com.github.icear;

/**
 * Created by icear.
 * 此类用于储存图片的尺寸等相关信息
 */
public class ImageRect {
    private int width;
    private int length;


    void setLength(int length) {
        this.length = length;
    }

    void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }


}
