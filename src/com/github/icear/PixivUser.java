package com.github.icear;

import java.net.CookieStore;

/**
 * Created by icear.
 * 账号模块
 * 负责账号相关的功能
 * 1、个人信息管理
 *      - 昵称
 *      - com.github.icear.Pixiv ID
 *      - 邮箱
 *      ...(与PixivMember同，继承自PixiMember)
 * 2、用户令牌（用与交互中的身份验证）
 */

public interface PixivUser extends PixivMember {
    /**
     * 获得登陆后的Cookie，相当于令牌
     * @return 当前用户登陆身份的cookie
     */
    CookieStore getCookieToken();//获得登陆后的Cookie，相当于令牌
}