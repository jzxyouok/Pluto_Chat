package com.wl.pluto.plutochat.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.util.PathUtil;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.constant.CommonConstant;
import com.wl.pluto.plutochat.chat.utils.DateTimeUtils;
import com.wl.pluto.plutochat.chat.utils.ScreenUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class PlayVideoMessageActivity2 extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = "--PlayVideoMessageActivity2-->";

    /**
     * 视频文件的本地地址
     */
    private String mVideoFilePath;

    /**
     * 视频的远程路径
     */
    private String mVideoRemotePath;

    /**
     * Secret
     */
    private String mVideoSecret;

    /**
     * 视频播放控件
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 视频播放窗口
     */
    private SurfaceView mSurfaceView;

    /**
     * 视频接口
     */
    private SurfaceHolder mSurfaceHolder;

    /**
     * 视频宽度
     */
    private int videoWidth;

    /**
     * 视频高度
     */
    private int videoHeight;

    /**
     * 是否准备就绪
     */
    private boolean isPlaying = false;

    /**
     * 播放暂停按钮
     */
    private ImageView mPlayImageView;

    /**
     * 视频当前时间
     */
    private TextView mVideoCurrentTimeTextView;

    /**
     * 视频的总时长
     */
    private TextView mVideoTotalTimeTextView;

    /**
     * 视频播放进度条
     */
    private SeekBar mVideoControlSeekBar;

    /**
     * 正在播放
     */
    private static final int MESSAGE_MEDIA_PLAYING = 1001;

    /**
     * 暂停中
     */
    private static final int MESSAGE_MEDIA_PAUSE = 1002;

    /**
     * 播放完成
     */
    private static final int MESSAGE_MEDIA_COMPLETE = 1003;

    /**
     *
     */
    private MediaHandler mMediaHandler;

    /**
     * 视频时长
     */
    private int mVideoDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_message2);

        Log.i(TAG, "onCreate");
        initIntent(getIntent());
        initLayout();
        initSurfaceHolder();


    }

    private void initIntent(Intent intent) {

        if (intent != null) {
            mVideoFilePath = intent.getStringExtra(CommonConstant.VIDEO_FILE_PATH_KEY);
            mVideoDuration = intent.getIntExtra(CommonConstant.VIDEO_DURATION_KEY, 0);
            mVideoRemotePath = intent.getStringExtra(CommonConstant.VIDEO_REMOTE_PATH_KEY);
            mVideoSecret = intent.getStringExtra(CommonConstant.VIDEO_SECRET_KEY);

            Log.i(TAG, "videoPath=" + mVideoFilePath);
        }
    }

    /**
     * 初始化布局
     */
    private void initLayout() {

        ScreenUtils.hideSystemUI(getWindow().getDecorView());

        mSurfaceView = (SurfaceView) findViewById(R.id.sv_video_play_surface_view);

        mPlayImageView = (ImageView) findViewById(R.id.iv_media_play_image);
        mPlayImageView.setOnClickListener(clickListener);

        mVideoCurrentTimeTextView = (TextView) findViewById(R.id.tv_media_current_time);

        mVideoTotalTimeTextView = (TextView) findViewById(R.id.tv_media_total_time);
        mVideoTotalTimeTextView.setText(DateTimeUtils.formatTime(mVideoDuration));

        mVideoControlSeekBar = (SeekBar) findViewById(R.id.sb_media_control_seek_bar);
        mVideoControlSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mVideoControlSeekBar.setMax(mVideoDuration);

    }

    /**
     * 初始化视频接口
     */
    private void initSurfaceHolder() {

        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(this);

        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mMediaHandler = new MediaHandler(this);
    }

    /**
     * 初始化视频播放器
     */
    private void initMediaPlayer() {

        try {

            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setOnCompletionListener(completionListener);
            mMediaPlayer.setOnInfoListener(infoListener);
            mMediaPlayer.setOnErrorListener(errorListener);
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
            mMediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(seekCompleteListener);

        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Log.i(TAG, "progress = " + progress);
            Log.i(TAG, "isFromUser = " + fromUser);
            if (fromUser && mMediaPlayer != null) {
                mMediaPlayer.seekTo(progress);
                mVideoCurrentTimeTextView.setText(DateTimeUtils.formatTime(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStartTrackingTouch");
            pauseVideoByMediaPlayer();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            Log.i(TAG, "onStopTrackingTouch");
            playVideoByMediaPlayer();
        }
    };
    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_media_play_image:

                    if (isPlaying) {

                        pauseVideoByMediaPlayer();

                    } else {
                        playVideoByMediaPlayer();
                    }
                    break;
            }
        }
    };

    @Override
    // 当surfaceView的宽度和高度，或者其他参数变化时调用
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    // 当创建surfaceView时，会调用create
    public void surfaceCreated(SurfaceHolder holder) {

        Log.i(TAG, "surfaceCreate");
        // 首先初始化MediaPlayer类
        initMediaPlayer();

        // 当创建surfaceView时，通过调用MediaPlayer的SetDisplay方法传入Holder，可以指定MediaPlayer将该surfaceView
        mMediaPlayer.setDisplay(holder);

        //为播放器初始化数据源
        initVideoData();

    }

    private void initVideoData() {
        if (!TextUtils.isEmpty(mVideoFilePath) && new File(mVideoFilePath).exists()) {

            try {

                mMediaPlayer.setDataSource(mVideoFilePath);

                //用于播放视频
                mMediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!TextUtils.isEmpty(mVideoRemotePath)) {

            HashMap<String, String> header = new HashMap<>();
            if (!TextUtils.isEmpty(mVideoSecret)) {
                header.put("share-secret", mVideoSecret);
            }

            //下载视频
            downloadVideo(mVideoRemotePath, header);

        }
    }

    private void setMediaPlayerData() {
        try {

            mMediaPlayer.setDataSource(mVideoFilePath);

            //用于播放视频
            mMediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载远程视频
     */
    private void downloadVideo(String remotePath, HashMap<String, String> header) {

        if (TextUtils.isEmpty(mVideoFilePath)) {
            mVideoFilePath = getLocalFilePath(remotePath);
        }

        if (new File(mVideoFilePath).exists()) {

            setMediaPlayerData();
        } else {

            //下载远程视频
            EMChatManager.getInstance().downloadFile(remotePath, mVideoFilePath, header, new EMCallBack() {
                @Override
                public void onSuccess() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setMediaPlayerData();
                        }
                    });
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

    private String getLocalFilePath(String remoteUrl) {
        String localPath;
        if (remoteUrl.contains("/")) {
            localPath = PathUtil.getInstance().getVideoPath().getAbsolutePath()
                    + "/" + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1)
                    + ".mp4";
        } else {
            localPath = PathUtil.getInstance().getVideoPath().getAbsolutePath()
                    + "/" + remoteUrl + ".mp4";
        }
        return localPath;
    }

    @Override
    // 当surfaceView销毁时调用，可以做一些清理内存的工作
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mMediaPlayer != null) {

            if (mMediaPlayer.isPlaying()) {

                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }
    };

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };

    /**
     * 当MediaPlayer播放完会调用，可以用来加载另一个视频，或者加载另一个屏幕的操作
     */
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            // 视频播放完成，可以设置暂停按钮为播放状态
            mPlayImageView.setImageResource(R.mipmap.image_media_play);
            isPlaying = false;
            mMediaHandler.sendEmptyMessage(MESSAGE_MEDIA_COMPLETE);
        }
    };

    /**
     * 当在播放过程中发生错误的时候调用,返回false表示有错误发生， 如果注册了onCompletionListener，会调用。
     */
    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, extra + "");
            return false;
        }
    };

    /**
     * 当出现媒体播放的特定信息或者需要警告的时候调用
     */
    private MediaPlayer.OnInfoListener infoListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {

                // 当视频文件中的音频和视频不正确交错时会触发，在一个正确交错的文件中，音频和视频样本都按顺序排列，从而
                // 使得能够有效而和平的进行
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {

                // 缓冲完成时触发

            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {

                // 开始缓冲是触发

            } else if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {

                // 当新的元数据可用是触发

            } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {

                // 当媒体不能正确定位是会触发（这可能意味着这是一个在线流媒体）

            } else if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {

                // 信息尚未指定或未知的时候触发

            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {

                // 当设备无法播放视频时会触发，这可能是将要播放视频，但是该 视频太复杂或者码率太高无法播放

            }
            return false;
        }
    };

    /**
     * 在视频播放器准备完成之后调用。一般就是可以开始播放视频了，在播放之前，应该设置surfaceView的大小，以匹配视频或者显示器的大小。
     */
    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

            // 获取视频的宽高
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            // 如果视频的宽高大于显示屏的大小
            if (videoWidth > ScreenUtils.getScreenWidth(PlayVideoMessageActivity2.this)
                    || videoHeight > ScreenUtils.getScreenHeight(PlayVideoMessageActivity2.this)) {

                // 那就需要找出显示比率了

                float widthRatio = (float) videoWidth / (float) ScreenUtils.getScreenWidth(PlayVideoMessageActivity2.this);

                float heightRatio = (float) videoHeight / (float) ScreenUtils.getScreenHeight(PlayVideoMessageActivity2.this);

                if (widthRatio > 1 || heightRatio > 1) {

                    // 我们将使用较大的比率
                    if (widthRatio > heightRatio) {

                        videoWidth = (int) Math.ceil((float) videoWidth
                                / widthRatio);
                        videoHeight = (int) Math.ceil((float) videoHeight
                                / widthRatio);
                    } else {

                        videoWidth = (int) Math.ceil((float) videoWidth
                                / heightRatio);
                        videoHeight = (int) Math.ceil((float) videoHeight
                                / heightRatio);
                    }
                }
            }

            // 现在可以用来设置显示视频的surfaceView的大小了。它可以是实际尺寸，或者如果视频比显示器大，那就是调整后的尺寸了
            mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(
                    videoWidth, videoHeight));

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        }
    };

    /**
     * 播放视频
     */
    private void playVideoByMediaPlayer() {

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {

            // 最后开始播放视频
            mMediaPlayer.start();

            // 设置播放按钮为暂停状态
            mPlayImageView.setImageResource(R.mipmap.image_media_pause);

            mMediaHandler.sendEmptyMessage(MESSAGE_MEDIA_PLAYING);

            isPlaying = true;
        }
    }

    /**
     * 暂停播放
     */
    private void pauseVideoByMediaPlayer() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();

            // 设置暂停按钮为播放状态
            mPlayImageView.setImageResource(R.mipmap.image_media_play);
            isPlaying = false;
        }
    }

    /**
     * 媒体播放器
     */
    private class MediaHandler extends Handler {

        private WeakReference<Activity> activityWeakReference;

        public MediaHandler(Activity activity) {

            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_MEDIA_PLAYING: {

                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    Log.i(TAG, "currentPosition = " + currentPosition);

                    ((PlayVideoMessageActivity2) activityWeakReference.get()).setSeekBarPosition(currentPosition);
                    ((PlayVideoMessageActivity2) activityWeakReference.get()).setCurrentPosition(currentPosition);
                    mMediaHandler.sendEmptyMessageDelayed(MESSAGE_MEDIA_PLAYING, 500);
                    break;
                }
                case MESSAGE_MEDIA_PAUSE:
                    break;
                case MESSAGE_MEDIA_COMPLETE:
                    mMediaHandler.removeMessages(MESSAGE_MEDIA_PLAYING);
                    break;
            }
        }
    }

    /**
     * 设置SeekBar 的进度
     */
    private void setSeekBarPosition(int position) {
        mVideoControlSeekBar.setProgress(position);
    }

    private void setCurrentPosition(int position) {
        mVideoCurrentTimeTextView.setText(DateTimeUtils.formatTime(position));
    }
}
