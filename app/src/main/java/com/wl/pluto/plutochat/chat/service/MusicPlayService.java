package com.wl.pluto.plutochat.chat.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.activity.MusicPlayActivity;
import com.wl.pluto.plutochat.chat.aidl.IMusicPlayAIDL;

/**
 * 音乐播放的后台服务
 *
 * @author jeck
 */
public class MusicPlayService extends Service {

    public static final String TAG = "--MusicPlayService-->";

    /**
     * 媒体播放器
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 音乐路径
     */
    public static final String MUSIC_PATH = "music_path";

    /**
     * 内部类对象，这个就不知道是一种什么设计模式了
     */
    private final IBinder baseBinder = new MusicPlayServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return musicPlayAIDL;
    }

    /**
     * 内部类，这样的用法还是第一次见，他返回外部类，然后在与外部类绑定的Activity中使用外部类来操作相关的细节内容
     *
     * @author jeck
     */
    public class MusicPlayServiceBinder extends Binder {

        public MusicPlayService getService() {

            return MusicPlayService.this;
        }
    }

    IMusicPlayAIDL.Stub musicPlayAIDL = new IMusicPlayAIDL.Stub() {
        @Override
        public int plus(int a, int b) throws RemoteException {
            return a + b;
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        Log.i(TAG, "Service process = " + android.os.Process.myPid());

        //执行耗时操作


//        mMediaPlayer = new MediaPlayer();
//
//        mMediaPlayer.setOnCompletionListener(completionListener);
//
//        mMediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
//
//        mMediaPlayer.setOnErrorListener(errorListener);
//
//        mMediaPlayer.setOnPreparedListener(preparedListener);
//
//        mMediaPlayer.setOnInfoListener(infoListener);

        // setForgeService();
    }

    private void setForgeService() {

        Notification.Builder notification = new Notification.Builder(getApplicationContext());
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("hello");
        notification.setContentTitle("ContentTitle");
        notification.setContentText("ContentText");

        //到这来我就会让MusicPlayActivity这个类使用SingleTask启动模式
        Intent intent = new Intent(this, MusicPlayActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setContentIntent(pendingIntent);

        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.notify(0, notification.build());

        startForeground(1, notification.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        //标准的写法,这里是要开线程的。service本来就是用了执行一些需要在后台运行的耗时操作的。所以必须要开线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //执行耗时操作
            }
        }).start();

//        if (!mMediaPlayer.isPlaying()) {
//
//            try {
//                mMediaPlayer.setDataSource(intent
//                        .getStringExtra(MusicPlayActivity.CURRENT_MUSIC_PATH));
//
//                // mMediaPlayer.prepare();
//                mMediaPlayer.prepareAsync();
//
//                // mMediaPlayer.start();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 播放音乐
     */
    public void playMusic() {

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

//        if (mMediaPlayer.isPlaying()) {
//
//            mMediaPlayer.stop();
//        }
//
//        mMediaPlayer.release();
    }

    /**
     * 这是播放器的接口，用于播放结束后的相关处理
     */
    private OnCompletionListener completionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            Log.i(TAG, "OnCompletionListener is called");
            // 把自己(服务)给停掉
            // stopSelf();
        }
    };

    private OnBufferingUpdateListener bufferingUpdateListener = new OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

            Log.i(TAG, "正在缓冲：" + percent);
        }
    };

    private OnErrorListener errorListener = new OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            Log.i(TAG, what + "----" + extra + "");

            switch (what) {
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    break;
                case MediaPlayer.MEDIA_ERROR_IO:
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    break;
            }
            return false;
        }
    };

    private OnInfoListener infoListener = new OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private OnPreparedListener preparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

            Log.i(TAG, "准备完成， 可以播放了");
            if (!mp.isPlaying()) {
                mp.start();
            }
        }
    };
}
