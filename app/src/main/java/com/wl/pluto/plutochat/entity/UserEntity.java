package com.wl.pluto.plutochat.entity;

import android.annotation.SuppressLint;
import android.os.Parcelable;

import com.easemob.chat.EMContact;

/**
 * Created by jeck on 15-11-19.
 */
@SuppressLint("ParcelCreator")
public class UserEntity extends EMContact {

    /**
     * 昵称，全局唯一,可以随时修改
     */
    protected String userNickName = "路飞";

    /**
     * 性别，全局唯一
     */
    protected String userGender = "男";

    /**
     * 地址，　唯一
     */
    protected String userAddress = "东海";

    /**
     * 头像，　唯一
     */
    protected String userHeadImageUrl;

    /**
     * 个性签名，　唯一
     */
    protected String userPersonalitySignature = "我是一个要成为海贼王的男人";

    /**
     * 未读消息数
     */
    private int unReadMsgCount;

    public UserEntity() {
    }

    public UserEntity(String userName) {
        this.username = userName;
    }

    public int getUnReadMsgCount() {
        return unReadMsgCount;
    }

    public void setUnReadMsgCount(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
    }

    public final String getUserNickName() {
        return userNickName;
    }

    public final void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public final String getUserGender() {
        return userGender;
    }

    public final void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public final String getUserAddress() {
        return userAddress;
    }

    public final void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public final String getUserHeadImageUrl() {
        return userHeadImageUrl;
    }

    public final void setUserHeadImageUrl(String userHeadImageUrl) {
        this.userHeadImageUrl = userHeadImageUrl;
    }

    public final String getUserPersonalitySignature() {
        return userPersonalitySignature;
    }

    public final void setUserPersonalitySignature(String userPersonalitySignature) {
        this.userPersonalitySignature = userPersonalitySignature;
    }


    @Override
    public int hashCode() {
        return 31 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !(o instanceof UserEntity)) {
            return false;
        }
        return getUsername().equals(((UserEntity) o).getUsername());
    }

    @Override
    public String toString() {
        return userNickName == null ? username : userNickName;
    }
}
