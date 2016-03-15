package com.wl.pluto.plutochat.chat.entity;

/**
 * Created by jeck on 15-11-12.
 */
public class LoginConfig {

    /**
     * 地址
     */
    private String xmppHost;

    /**
     * 端口号
     */
    private int xmppPost;

    /**
     * 服务器域名
     */
    private String xmppServerDomainName;

    /**
     * 手机号，就是用户名，也是ChatID, 者三个变量是一个意思．全局唯一
     */
    private String phoneNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 回话ｉｄ
     */
    private String sessionId;

    /**
     * 　是否记住密码
     */
    private boolean isRememberPassword;

    /**
     * 是否自动登录
     */
    private boolean isAutoLogin;

    /**
     * 是否为隐身状态
     */
    private boolean isInvisible;

    /**
     * 是否在线
     */
    private boolean isOnLine;

    /**
     * 是否第一次启动
     */
    private boolean isFirstStart;


    public String getXmppHost() {
        return xmppHost;
    }

    public void setXmppHost(String xmppHost) {
        this.xmppHost = xmppHost;
    }

    public int getXmppPost() {
        return xmppPost;
    }

    public void setXmppPost(int xmppPost) {
        this.xmppPost = xmppPost;
    }

    public String getXmppServerDomainName() {
        return xmppServerDomainName;
    }

    public void setXmppServerDomainName(String xmppServerDomainName) {
        this.xmppServerDomainName = xmppServerDomainName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isRememberPassword() {
        return isRememberPassword;
    }

    public void setIsRememberPassword(boolean isRememberPassword) {
        this.isRememberPassword = isRememberPassword;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setIsAutoLogin(boolean isAutoLogin) {
        this.isAutoLogin = isAutoLogin;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(boolean isOnLine) {
        this.isOnLine = isOnLine;
    }

    public boolean isFirstStart() {
        return isFirstStart;
    }

    public void setIsFirstStart(boolean isFirstStart) {
        this.isFirstStart = isFirstStart;
    }
}
