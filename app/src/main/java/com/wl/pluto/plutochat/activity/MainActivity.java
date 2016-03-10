package com.wl.pluto.plutochat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.manager.ChatHelperManager;

public class MainActivity extends BaseActivity {

    /**
     * 登录按钮
     */
    private Button mLoginButton;

    /**
     * 注册界面
     */
    private Button mRegisterButton;

    /**
     * 登陆注册界面
     */
    private LinearLayout mLoginOrRegisterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
    }

    private void initLayout() {

        mLoginButton = (Button) findViewById(R.id.btn_main_login);
        mLoginButton.setOnClickListener(clickListener);

        mRegisterButton = (Button) findViewById(R.id.btn_main_Register);
        mRegisterButton.setOnClickListener(clickListener);

        mLoginOrRegisterLayout = (LinearLayout) findViewById(R.id.ll_main_login_or_register_layout);

        //如果该手机尚未注册过，那就先显示登陆或者注册界面
        if (BaseApplication.getInstance().getUserName() == null) {
            mLoginOrRegisterLayout.setVisibility(View.VISIBLE);
        } else {

            //如果注册了但是没有登陆，进入登陆界面
            if (!ChatHelperManager.getInstance().isLogin()) {
                forwardTargetActivity(LoginActivity.class);
                finish();
            } else {

                //否则初始化应用程序
                initAppAndLogin();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        finish();
                    }
                }, 2000);
            }

        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_main_login: {

                    forwardTargetActivity(LoginActivity.class);
                    finish();
                    break;
                }
                case R.id.btn_main_Register: {

                    forwardTargetActivity(RegisterActivity.class);
                    finish();
                    break;
                }
            }
        }
    };
}
