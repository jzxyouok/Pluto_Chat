package com.wl.pluto.plutochat.entity;

import android.annotation.SuppressLint;

import com.wl.pluto.plutochat.constant.URLConstant;

/**
 * Created by jeck on 15-10-29.
 */
@SuppressLint("ParcelCreator")
public class ChatUserEntity extends UserEntity {

    /**
     * message
     */
    private String chatMessage;

    /**
     * time
     */
    private String chatTime;


    public ChatUserEntity() {
        this.userHeadImageUrl = URLConstant.TEST_IMAGE_URL;
        this.userNickName = "title";
        this.chatMessage = "message";
        this.chatTime = "10:24";
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
