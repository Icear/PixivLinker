package com.github.icear;

import org.apache.http.client.CookieStore;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by icear.
 * 作品模块
 * 负责作品相关的操作
 */
public class PWork implements PixivWork{



    private int id;
    private String name;
    private LocalDateTime createTime;
    private String[] workTag;
    private String creativeTool;
    private PixivImage[] workImage;


    /**
     * 根据用户令牌和目标会员id生成PMember类
     * @param cookieToken cookie令牌
     * @param id 作品id
     * @return PWork对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PWork generatePWork(CookieStore cookieToken, int id) throws IOException{

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
    public String getCreativeTool() {
        return creativeTool;
    }

    @Override
    public PixivImage[] getWorkImage() {
        return workImage;
    }
}
