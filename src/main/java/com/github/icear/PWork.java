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

    /**
     * 根据用户令牌和目标会员id生成PMember类
     * @param cookieToken cookie令牌
     * @param id 作品id
     * @return PWork对象
     * @throws IOException 网络IO或数据处理异常
     */
    static PWork generatePWork(CookieStore cookieToken, int id) throws IOException{
        return new PWork();
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return null;
    }

    @Override
    public String[] getWorkTag() {
        return new String[0];
    }

    @Override
    public String getCreativeTool() {
        return null;
    }

    @Override
    public PixivImage[] getWorkImage() {
        return new PixivImage[0];
    }
}
