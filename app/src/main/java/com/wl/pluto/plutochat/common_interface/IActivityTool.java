package com.wl.pluto.plutochat.common_interface;

import android.content.SharedPreferences;

import com.wl.pluto.plutochat.entity.LoginConfig;

/**
 * Created by jeck on 15-11-12.
 */
public interface IActivityTool {

    /**
     * 启动服务
     */
    void startService();

    /**
     * 终止服务
     */
    void stopService();

    /**
     * 是否以开启ＧＰＳ
     *
     * @return
     */
    boolean hasLocationGPS();

    /**
     * 是否以开启本地网络
     *
     * @return
     */
    boolean hasLocationｅNetwork();

    /**
     * 获取用户登录的SharedPreferences
     *
     * @return
     */
    SharedPreferences getSharedPreferences();

    /**
     * 保存用户配置
     *
     * @param config
     */
    void saveLoginConfig(LoginConfig config);

    /**
     * 获取用户登录配置
     *
     * @return
     */
    LoginConfig getUserLoginConfig();

    /**
     * 用户是否在线（当前网络是否重连成功）
     *
     * @author shimiso
     * @update 2012-7-6 上午9:59:49
     */
    boolean getUserOnlineState();

    /**
     * 设置用户在线状态 true 在线 false 不在线
     *
     * @param isOnline
     */
    void setUserOnlineState(boolean isOnline);

    /**
     * 是否有可用的网络
     *
     * @return
     */
    boolean hasInternetConnected();

}
