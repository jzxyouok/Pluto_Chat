package com.wl.pluto.plutochat.constant;

import com.wl.pluto.plutochat.base.BaseApplication;

/**
 * 专门存放URL常量的类
 * Created by pluto on 15-11-5.
 */
public class URLConstant {

    /**
     * 测试网址
     */
    public static final String TEST_URL = "http://www.baidu.com";

    /**
     * 这是我自己的服务器的地址，localhost要换成自己电脑的IP地址，否则手机是访问不到的
     */
    public static final String TEST_LOGIN_URL = "http://192.168.1.6:8080/Chat/ChatServlet?LoginName=pluto&LoginPassword=123456";

    /**
     * post 的请求地址
     */
    public static final String BASE_LOGIN_URL = "http://192.168.1.6:8080/Chat/ChatServlet";

    /**
     * 测试图片的地址
     */
    public static final String TEST_IMAGE_URL = "http://i.imgur.com/DvpvklR.png";

    /**
     * 存储头像的阿里云地址
     */
    public static final String OSS_BASE_URL = "http://pluto8172.oss-cn-shanghai.aliyuncs.com/oss_head_image_path/";

    /**
     * 存储当前用户的头像地址
     */
    public static final String OSS_MY_HEAD_IMAGE_RUL = OSS_BASE_URL + BaseApplication.getInstance().getUserName() + ".png";

    /**
     * android QQ 的下载地址
     */
    public static final String QQ_DOWNLOAD_URL = "http://113.207.16.84/sqdd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk?mkey=569387b4d8a6b68e&f=cf87&p=.apk";
}
