package com.github.icear;

import java.time.LocalDateTime;

/**
 * Created by icear.
 */
public interface PixivWork {
    /**
     * 获得作品ID
     * @return 作品id
     */
    int getId();

    /**
     * 获得作品名称
     * @return 作品名称
     */
    String getName();

    /**
     * 获得投稿时间
     * @return 投稿时间
     */
    LocalDateTime getCreateTime();

    /**
     * 获得作品标签组
     * @return 作品的标签（不止一个）
     */
    String[] getWorkTag();

    /**
     * 获得创作工具
     * @return 创作工具
     */
    String getCreativeTool();

    /**
     * 获得作品
     * @return 图片组
     */
    PixivImage[] getWorkImage();
}
