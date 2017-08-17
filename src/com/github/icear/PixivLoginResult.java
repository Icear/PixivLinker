package com.github.icear;

/**
 * Created by icear.
 * 登陆结果封装类
 */
public class PixivLoginResult {
    private PixivUser pixivUser = null;
    private boolean isSucceed = false;
    private String errorMessage = null;

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    void setPixivUser(PixivUser pixivUser) {
        this.pixivUser = pixivUser;
    }

    void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    /**
     * 获得登陆的用户，仅在登陆成功时有值
     * @return 成功返回PixivUser对象，失败返回null
     */
    public PixivUser getPixivUser() {
        return pixivUser;
    }

    /**
     * 获得登陆失败的信息，仅在登陆失败时有值
     * @return 成功时为null，失败时返回输出值
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 检查登陆是否成功
     * @return 成功返回真，否则返回假
     */
    public boolean isSucceed() {
        return isSucceed;
    }
}
