package com.wl.pluto.plutochat.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wl.pluto.plutochat.activity.MainFrameworkActivity;
import com.wl.pluto.plutochat.common_interface.UserService;
import com.wl.pluto.plutochat.constant.URLConstant;
import com.wl.pluto.plutochat.manager.AsyncHttpClientManager;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jeck on 15-11-5.
 */
public class UserLoginImpl implements UserService {

    private static final String TAG = "--UserLoginImpl-->";

    private static final String LOGIN_URL = "Login";

    private static final String LOGIN_NAME = "LoginNname";

    private static final String PASSWORD = "Password";

    private Context context;


    public UserLoginImpl(Context context) {
        this.context = context;

    }

    @Override
    public void userLogin(String loginName, String loginPassword) throws Exception {

        Log.i(TAG, "name=" + loginName + "   password=" + loginPassword);

        AsyncHttpClientManager.getInstance(context).get(URLConstant.TEST_URL, httpResponseHandler);
    }

    private void startMainFrameworkActivity() {

        Log.i(TAG, "success ----> startMainFrameworkActivity");

        Intent intent = new Intent(context, MainFrameworkActivity.class);

        context.startActivity(intent);
    }

    /**
     * 获取给服务器传递的参数值
     *
     * @param name
     * @param password
     * @return
     */
    private RequestParams getParams(String name, String password) {
        RequestParams params = new RequestParams();
        params.add(LOGIN_NAME, name);
        params.add(PASSWORD, password);
        return params;
    }

    /**
     * 这是AsyncHttpClient的回调接口
     */
    AsyncHttpResponseHandler httpResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            //如果登录成功
            startMainFrameworkActivity();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            //这里面处理网络连接的错误
        }
    };
}
