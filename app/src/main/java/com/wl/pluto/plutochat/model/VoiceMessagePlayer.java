package com.wl.pluto.plutochat.model;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.wl.pluto.plutochat.R;

import java.io.IOException;

/**
 * Created by pluto on 15-12-29.
 */
public class VoiceMessagePlayer {

    private static VoiceMessagePlayer instance;

    /**
     * 媒体播放器
     */
    private MediaPlayer mVoiceMessageMediaPlayer;

    /**
     * 语音消息体
     */
    private VoiceMessageBody mVoiceMessageBody;

    /**
     * ImageView控件
     */
    private ImageView mVoiceMessageImageView;

    /**
     * 语音消息的路径
     */
    private String mVoiceMessagePath;

    /**
     * 动画效果
     */
    private AnimationDrawable mAnimationDrawable;

    /**
     * EMMessage
     */
    private EMMessage mMessage;

    /**
     * 是否正在播放语音
     */
    private boolean isVoiceMessagePlaying = false;

    private VoiceMessagePlayer(EMMessage message, ImageView imageView) {

        this.mMessage = message;
        this.mVoiceMessageBody = (VoiceMessageBody) mMessage.getBody();
        this.mVoiceMessageImageView = imageView;

        if (message.direct.equals(EMMessage.Direct.RECEIVE)) {

            mVoiceMessagePath = mVoiceMessageBody.getRemoteUrl();
        } else {
            mVoiceMessagePath = mVoiceMessageBody.getLocalUrl();
        }
        initMediaPlayer(mVoiceMessagePath);
    }

    public static VoiceMessagePlayer newInstance(EMMessage message, ImageView imageView) {

        if(instance == null){
            instance = new VoiceMessagePlayer(message, imageView);
        }
        return instance;
    }

    public void play() {

        if (isVoiceMessagePlaying) {
            return;
        }

        if (mVoiceMessageMediaPlayer != null) {
            mVoiceMessageMediaPlayer.start();

            isVoiceMessagePlaying = true;
            showAnimation();
        }
    }

    private void initMediaPlayer(String path) {

        try {
            mVoiceMessageMediaPlayer = new MediaPlayer();
            mVoiceMessageMediaPlayer.setDataSource(path);
            mVoiceMessageMediaPlayer.prepare();
            mVoiceMessageMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    stopPlayVoiceMessage();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayVoiceMessage() {
        if (mVoiceMessageMediaPlayer != null) {
            mVoiceMessageMediaPlayer.stop();
            mVoiceMessageMediaPlayer.release();
            mVoiceMessageMediaPlayer = null;

            instance = null;
            isVoiceMessagePlaying = false;

            if(mAnimationDrawable.isRunning()){
                mAnimationDrawable.stop();
            }

            if (mMessage.direct.equals(EMMessage.Direct.RECEIVE)) {
                mVoiceMessageImageView.setImageResource(R.mipmap.chat_message_voice_receive_playing_f3);
            }else {
                mVoiceMessageImageView.setImageResource(R.mipmap.chat_message_voice_send_playing_f3);
            }
        }
    }

    private void showAnimation() {

        if (mMessage.direct.equals(EMMessage.Direct.RECEIVE)) {
            mVoiceMessageImageView.setImageResource(R.anim.animation_voice_message_receive);
        } else {
            mVoiceMessageImageView.setImageResource(R.anim.animation_voice_message_send);
        }

        mAnimationDrawable = (AnimationDrawable) mVoiceMessageImageView.getDrawable();
        mAnimationDrawable.start();
    }
}
