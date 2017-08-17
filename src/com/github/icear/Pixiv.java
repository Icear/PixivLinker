package com.github.icear;

/**
 * Created by icear
 * 包入口
 *
 * 功能拆分：
 * 1、账号模块
 * 2、排行榜模块
 * 3、作品模块
 * 4、会员模块
 * 5、推荐模块（可选）
 */


public class Pixiv{
    /**
     * 登陆函数
     * 传入用户名与密码执行登陆，函数包含网络访问代码，建议放置在线程中执行
     * @param account 用户名或邮箱
     * @param password 密码
     * @return 返回登陆结果封装类
     */
    public static PixivLoginResult login(String account,String password){
        /*
         * get前置页，获取相关参数
         * 再post登陆
         * 检查登陆结果并返回
         */
        PixivLoginResult result = new PixivLoginResult();

        return null;
    }
}
