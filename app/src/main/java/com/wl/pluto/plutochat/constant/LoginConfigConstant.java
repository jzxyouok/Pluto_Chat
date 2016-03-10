package com.wl.pluto.plutochat.constant;

/**
 * 服务器的配置
 * Created by jeck on 15-11-13.
 */
public class LoginConfigConstant {


    /**
     * 登录用户名
     */
    public static final String LOGIN_PHONE_NUMBER = "phone_number";

    /**
     *
     */
    public static final String LOGIN_SET = "chat_login_set";

    /**
     * 登录密码
     */
    public static final String LOGIN_PASSWORD = "password";

    /**
     * 　服务器ｉｐ地址
     */
    public static final String LOGIN_XMPP_HOST = "xmpp_host";

    /**
     * 　服务器端口号，因为用的是openfire, 这里的端口号默认是５２２２
     */
    public static final String LOGIN_XMPP_PORT = "xmpp_port";

    /**
     * 　服务器的名称，就是计算机的名称．在这里是jeck
     */
    public static final String LOGIN_XMPP_SEIVICE_NAME = "xmpp_service_name";

    /**
     * 是否自动登录
     */
    public static final String LOGIN_IS_AUTOLOGIN = "isAutoLogin";

    /**
     * 　是否隐身
     */
    public static final String LOGIN_IS_INVISIBLE = "isInvisible";

    /**
     * 　是否记住密码
     */
    public static final String LOGIN_IS_REMEMBER = "isRemember";

    /**
     * 　是否首次启动
     */
    public static final String LOGIN_IS_FIRSTSTART = "isFirstStart";

    /*******************************************************************************/

    public static final String XMPP_DEFAULT_HOST = "www.google.com";
    public static final int XMPP_DEFAULT_PORT = 5222;
    public static final String XMPP_DEFAULT_SERVER_NAME = "jeck";
    public static final String XMPP_DEFAULT_PHONE_NUMBER = "136000";
    public static final String XMPP_DEFAULT_PASSWORD = "123456";
    public static final boolean XMPP_DEFAULT_IS_AUTOLOGIN = false;
    public static final boolean XMPP_DEFAULT_IS_INVISIBLE = false;
    public static final boolean XMPP_DEFAULT_IS_REMEMBER = false;
    public static final boolean XMPP_DEAULT_IS_FIRSTSTART = false;
}
