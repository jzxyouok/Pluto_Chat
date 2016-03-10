package com.wl.pluto.plutochat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseApplication;

public class SettingActivity extends BaseActivity {

    /**
     * 退出界面
     */
    private TextView mSettingLogoutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initLayout();
    }

    private void initLayout() {

        mSettingLogoutTextView = (TextView) findViewById(R.id.tv_setting_logout);
        mSettingLogoutTextView.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tv_setting_logout:
                    onSettingLogoutClick();
                    break;
            }
        }
    };

    private void onSettingLogoutClick() {

        BaseApplication.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                finish();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
