package com.wl.pluto.plutochat.base;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.easemob.EMCallBack;
import com.wl.pluto.plutochat.constant.LoginConfigConstant;
import com.wl.pluto.plutochat.entity.LoginConfig;
import com.wl.pluto.plutochat.manager.ChatHelperManager;

public class BaseApplication extends Application {

    /**
     * 因为每次程序启动，都会执行Application
     */
    private static BaseApplication instance;

    /**
     * 保存一份登录配置信息，因为在很多的地方都要用到
     */
    private LoginConfig loginConfig = new LoginConfig();

    /**
     *
     */
    private static final String LOGIN_CONFIG_PREFERENCES_KEY = "login_config_preferences_key";

    /**
     *
     */
    private SharedPreferences preferences;

    /**
     * 应用程序的辅助类
     */
    private static ChatHelperManager chatHelperManager = new ChatHelperManager();

    /**
     * 阿里云的AK
     */
    private static String accessKey = "zsPj04At9eNhJahw";

    /**
     * 阿里云的AS
     */
    private static String screctKey = "lmacO8vszvIBLgnO6XcErlyNGZ1BsA";

    /**
     * 阿里云bucket
     */
    private static final String bucketname = "pluto8172";

    private static OSSService ossService;


    /*************************************************************************************/

    /**
     *
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }


    private void initApplication() {
        instance = this;
        preferences = getSharedPreferences(LOGIN_CONFIG_PREFERENCES_KEY, Context.MODE_PRIVATE);

        //初始化环信SDK
        chatHelperManager.onInit(this);

        //初始化阿里云ｓｄｋ
        initOSSSDK();

        BaseCrashHandler.getInstance(this).init();
    }


    /**
     * 从应用程序的Application 中的mate 节点中获取相应的值
     */
    private void initAccessKeyAndService() {

        // 测试代码没有考虑AK/SK的安全性，保存在本地
        try {
            accessKey = this.getPackageManager().getApplicationInfo(this.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getString("com.alibaba.app.ossak");
            screctKey = this.getPackageManager().getApplicationInfo(this.getPackageName(),
                    PackageManager.GET_META_DATA).metaData.getString("com.alibaba.app.osssk");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化阿里云OSSSDK
     */
    private void initOSSSDK() {


        //获取ＯＳＳ服务　在应用程序是生命周期中，可以重复获取这个ossservice来使用ＯＳＳ的服务
        ossService = OSSServiceProvider.getService();

        //设置应用程序的context
        ossService.setApplicationContext(instance);


        //设置数据中心域名
        ossService.setGlobalDefaultHostId("oss-cn-shanghai.aliyuncs.com");

        //设置加签器
        ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK);

        // 设置全局默认加签器
        ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() {

            @Override
            public String generateToken(String httpMethod, String md5, String type, String date,
                                        String ossHeaders, String resource) {

                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                        + resource;

                return OSSToolKit.generateToken(accessKey, screctKey, content);
            }
        });

        ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);

        ClientConfiguration conf = new ClientConfiguration();
        // 设置建连超时时间，默认为30s
        conf.setConnectTimeout(15 * 1000);

        // 设置socket超时时间，默认为30s
        conf.setSocketTimeout(15 * 1000);

        // 设置全局最大并发任务数，默认10个
        conf.setMaxConcurrentTaskNum(10);

        ossService.setClientConfiguration(conf);

    }

    /**
     * 获取当前登录用户名
     *
     * @return
     */
    public String getUserName() {
        return chatHelperManager.getChatId();
    }

    /**
     * 设置当前登录的用户名
     *
     * @param userName
     */
    public void setUserName(String userName) {
        chatHelperManager.setChatId(userName);
    }

    /**
     * 获取当前登录的用户密码
     *
     * @return
     */
    public String getPassword() {
        return chatHelperManager.getPassword();
    }

    /**
     * 设置当前的登录用户的密码
     *
     * @param password
     */
    public void setPassword(String password) {
        chatHelperManager.setPassword(password);
    }

    /**
     * 退出登录，清空数据
     */
    public void logout(final boolean isGCM, EMCallBack callBack) {
        chatHelperManager.logout(isGCM, callBack);
    }

    /*******************************************************************************************/
    public LoginConfig getLoginConfig() {

        initLoginConfig();
        return loginConfig;
    }


    private void initLoginConfig() {

        loginConfig.setPhoneNumber(preferences.getString(LoginConfigConstant.LOGIN_PHONE_NUMBER,
                LoginConfigConstant.XMPP_DEFAULT_PHONE_NUMBER));
        loginConfig.setPassword(preferences.getString(LoginConfigConstant.LOGIN_PASSWORD,
                LoginConfigConstant.XMPP_DEFAULT_PASSWORD));
        loginConfig.setXmppHost(preferences.getString(LoginConfigConstant.LOGIN_XMPP_HOST,
                LoginConfigConstant.XMPP_DEFAULT_HOST));
        loginConfig.setXmppPost(preferences.getInt(LoginConfigConstant.LOGIN_XMPP_PORT,
                LoginConfigConstant.XMPP_DEFAULT_PORT));
        loginConfig.setXmppServerDomainName(preferences.getString(LoginConfigConstant.LOGIN_XMPP_SEIVICE_NAME,
                LoginConfigConstant.XMPP_DEFAULT_SERVER_NAME));
        loginConfig.setIsAutoLogin(preferences.getBoolean(LoginConfigConstant.LOGIN_IS_AUTOLOGIN,
                LoginConfigConstant.XMPP_DEFAULT_IS_AUTOLOGIN));

        loginConfig.setIsRememberPassword(preferences.getBoolean(LoginConfigConstant.LOGIN_IS_REMEMBER,
                LoginConfigConstant.XMPP_DEFAULT_IS_REMEMBER));

        loginConfig.setIsInvisible(preferences.getBoolean(LoginConfigConstant.LOGIN_IS_INVISIBLE,
                LoginConfigConstant.XMPP_DEFAULT_IS_INVISIBLE));

        loginConfig.setIsFirstStart(preferences.getBoolean(LoginConfigConstant.LOGIN_IS_FIRSTSTART,
                LoginConfigConstant.XMPP_DEAULT_IS_FIRSTSTART));
    }

    public void saveLoginConfig(LoginConfig config) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(LoginConfigConstant.LOGIN_PHONE_NUMBER, config.getPhoneNumber());
        editor.putString(LoginConfigConstant.LOGIN_PASSWORD, config.getPassword());
        editor.putString(LoginConfigConstant.LOGIN_XMPP_HOST, config.getXmppHost());
        editor.putInt(LoginConfigConstant.LOGIN_XMPP_PORT, config.getXmppPost());
        editor.putString(LoginConfigConstant.LOGIN_XMPP_SEIVICE_NAME, config.getXmppServerDomainName());
        editor.putBoolean(LoginConfigConstant.LOGIN_IS_AUTOLOGIN, config.isAutoLogin());
        editor.putBoolean(LoginConfigConstant.LOGIN_IS_INVISIBLE, config.isInvisible());
        editor.putBoolean(LoginConfigConstant.LOGIN_IS_REMEMBER, config.isRememberPassword());
        editor.putBoolean(LoginConfigConstant.LOGIN_IS_FIRSTSTART, config.isFirstStart());

        editor.apply();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void registerActivityLifecycleCallbacks(
            ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(
            ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
    }

    /**
     * 获取阿里云AK
     *
     * @return
     */
    public static String getOSSAccessKey() {
        return accessKey;
    }

    /**
     * 获取阿里云AS
     *
     * @return
     */
    public static String getOSSScrectKey() {
        return screctKey;
    }

    /**
     * 获取阿里云bucket
     *
     * @return
     */
    public static String getOSSBucket() {
        return bucketname;
    }

    public static OSSService getOssService() {
        return ossService;
    }

}
