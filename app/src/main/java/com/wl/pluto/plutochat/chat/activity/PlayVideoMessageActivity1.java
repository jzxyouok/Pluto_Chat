package com.wl.pluto.plutochat.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.constant.CommonConstant;
import com.wl.pluto.plutochat.chat.utils.ScreenUtils;

public class PlayVideoMessageActivity1 extends BaseActivity {

    /**
     * 视频播放控件
     */
    private VideoView mVideoView;

    /**
     * 视频文件的ｕｒｌ
     */
    private String mVideoFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_message1);

        initIntent(getIntent());
        initLayout();
        initVideoView();
    }


    private void initIntent(Intent intent) {

        if (intent != null) {
            mVideoFilePath = intent.getStringExtra(CommonConstant.VIDEO_FILE_PATH_KEY);
        }
    }

    /**
     * 初始化布局
     */
    private void initLayout() {

        //隐藏状态栏，全屏显示
        ScreenUtils.hideSystemUI(getWindow().getDecorView());
        mVideoView = (VideoView) findViewById(R.id.vv_video_play_window);
        mVideoView.setVideoPath(mVideoFilePath);
    }


    /**
     * 初始化VideoView
     */
    private void initVideoView() {

        // 为videoView添加一个播放控制器
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.start();
    }
}
