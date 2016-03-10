package com.wl.pluto.plutochat.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.aidl.IMusicPlayAIDL;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.service.MusicPlayService;

public class MusicPlayActivity extends BaseActivity {

    private static final String TAG = "--MusicPlayActivity-->";

    /**
     * 播放音乐
     */
    private Button mMusicPlayButton;

    /**
     * 暂停音乐
     */
    private Button mMusicPauseButton;

    /**
     * 播放上一首
     */
    private Button mMusicBeforeButton;

    /**
     * 播放下一首
     */
    private Button mMusicNextButton;

    /**
     * 随机选一首
     */
    private Button mMusicRandomButton;

    /**
     * 加载网络数据
     */
    private Button mLoadHttpResourcesButton;

    /**
     * 上一首歌的路径
     */
    public static final String BEFORE_MUSIC_PATH = "before_music_path";

    /**
     * 当前选择的歌曲路径
     */
    public static final String CURRENT_MUSIC_PATH = "current_music_path";

    /**
     * 下一首歌曲路径
     */
    public static final String NEXT_MUSIC_PATH = "next_music_path";

    /**
     * 随机选的一首歌曲的路径
     */
    public static final String RANDOM_MUSIC_PATH = "random_music_path";

    /**
     * 上一首歌的路径
     */
    private String mBeforeMusicPath;

    /**
     * 当前选择的歌曲路径
     */
    private String mCurrentMusicPath;

    /**
     * 下一首歌曲路径
     */
    private String mNextMusciPath;

    /**
     * 随机选的一首歌曲的路径
     */
    private String mRandomMusicPath;

    /**
     * 服务对象
     */
    private MusicPlayService mMusicPlayService;

    private Button mStartServiceButton;
    private Button mStopServiceButton;
    private Button mBindServiceButton;
    private Button mUnbindServiceButton;

    private IMusicPlayAIDL musicPlayAIDL;

    /**
     * 服务连接
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mMusicPlayService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            musicPlayAIDL = IMusicPlayAIDL.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_palyer_layout);

        initLayout();

        //handlerIntent();

        // startMusicService();
    }

    /**
     * 初始化布局
     */
    private void initLayout() {

        mStartServiceButton = (Button) findViewById(R.id.btn_start_service);
        mStartServiceButton.setOnClickListener(clickListener);

        mStopServiceButton = (Button) findViewById(R.id.btn_stop_service);
        mStopServiceButton.setOnClickListener(clickListener);

        mBindServiceButton = (Button) findViewById(R.id.btn_bind_service);
        mBindServiceButton.setOnClickListener(clickListener);

        mUnbindServiceButton = (Button) findViewById(R.id.btn_unbind_service);
        mUnbindServiceButton.setOnClickListener(clickListener);

        mMusicPlayButton = (Button) findViewById(R.id.btn_music_play);

        mMusicPlayButton.setOnClickListener(clickListener);

        mMusicPauseButton = (Button) findViewById(R.id.btn_music_pause);

        mMusicPauseButton.setOnClickListener(clickListener);

        mLoadHttpResourcesButton = (Button) findViewById(R.id.btn_load_http_sources);

        mLoadHttpResourcesButton.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_music_play:
                    playMusic();
                    break;
                case R.id.btn_music_pause:

                    pauseMusic();
                    break;

                case R.id.btn_load_http_sources:

                    loadHttpResources();
                    break;
                case R.id.btn_start_service:
                    onStartServiceClick();
                    break;
                case R.id.btn_stop_service:
                    onStopServiceClick();
                    break;
                case R.id.btn_bind_service:
                    onBindServiceClick();
                    break;
                case R.id.btn_unbind_service:
                    onUnbindServiceClick();
                    break;
            }
        }
    };

    private void onStartServiceClick() {


        Log.i(TAG, "Activity process = " + android.os.Process.myPid());
        Intent intent = new Intent(this, MusicPlayService.class);
        startService(intent);
    }

    private void onStopServiceClick() {
        Log.i(TAG, "onStopServiceClick");
        Intent intent = new Intent(this, MusicPlayService.class);
        stopService(intent);
    }

    private void onBindServiceClick() {
        Log.i(TAG, "onBindServiceClick");
        Intent intent = new Intent(this, MusicPlayService.class);
        bindService(intent, mServiceConnection, 1);
    }

    private void onUnbindServiceClick() {
        Log.i(TAG, "onUnbindServiceClick");
        unbindService(mServiceConnection);
    }

    /**
     * 处理从intent传递过来的数据
     */
    private void handlerIntent() {

        Intent intent = getIntent();

        mCurrentMusicPath = intent.getStringExtra(CURRENT_MUSIC_PATH);
        // mNextMusciPath = intent.getStringExtra(NEXT_MUSIC_PATH);
        // mRandomMusicPath = intent.getStringExtra(RANDOM_MUSIC_PATH);

    }

    private void loadHttpResources() {

        String httpMusicPath = "http://yinyueshiting.baidu.com/data2/music/31266471/877578151200128.mp3?xcode=a0b9f70a9501e87659372f0cdce98fcb27156954d16602c9";

        Intent i = new Intent(this, MusicPlayService.class);

        i.putExtra(CURRENT_MUSIC_PATH, httpMusicPath);

        startService(i);

        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 启动服务
     */
    private void startMusicService() {

        Intent i = new Intent(this, MusicPlayService.class);

        i.putExtra(CURRENT_MUSIC_PATH, mCurrentMusicPath);

        startService(i);

        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * 播放音乐
     */
    private void playMusic() {
//        if (mMusicPlayService != null) {
//            mMusicPlayService.playMusic();
//        }

        if (musicPlayAIDL != null) {
            try {
                int result = musicPlayAIDL.plus(2, 4);

                Log.i(TAG, "result = " + result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停音乐播放
     */
    private void pauseMusic() {

        if (mMusicPlayService != null) {
            mMusicPlayService.pauseMusic();
        }
    }

    // private class LoadHttpResourecsTask extends AsyncTask<String, String,
    // Result>
}
