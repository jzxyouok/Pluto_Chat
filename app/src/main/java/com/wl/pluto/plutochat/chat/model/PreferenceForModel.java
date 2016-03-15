package com.wl.pluto.plutochat.chat.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jeck on 15-11-18.
 */
public class PreferenceForModel {

    /**
     * 保存preference 的ｎａｍｅ
     */
    public static final String PREFERENCE_NAME = "preference_name";

    private static SharedPreferences mSharedPreference;

    private static PreferenceForModel instance;

    private static SharedPreferences.Editor editor;

    private String PREF_KEY_SETTING_NOTIFICATION = "pref_key_setting_notification";
    private String PREF_KEY_SETTING_SOUND = "pref_key_setting_sound";
    private String PREF_KEY_SETTING_VIBRATE = "pref_key_setting_vibrate";
    private String PREF_KEY_SETTING_SPEAKER = "pref_key_setting_speaker";

    private String PREF_KEY_SETTING_CHAT_ROOM_OWNER_LEVER = "pref_key_setting_chat_room_owner_lever";
    private String PREF_KEY_SETTING_GROUPS_SYNC = "pref_key_setting_groups_sync";
    private String PREF_KEY_SETTING_CONTACT_SYNC = "pref_key_setting_contact_sync";
    private String PREF_KEY_SETTING_BLACKLIST_SYNC = "pref_key_setting_blacklist_sync";

    private String PREF_KEY_CURRENT_USER_NICK_NAME = "pref_key_current_user_nick_name";
    private String PREF_KEY_CURRENT_USER_HEAD_IMAGE = "pref_key_current_user_head_image";

    private PreferenceForModel(Context context) {
        mSharedPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreference.edit();
    }


    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new PreferenceForModel(context);
        }
    }

    public static PreferenceForModel getInstance() {
        if (instance == null) {
            throw new RuntimeException("please init first");
        }
        return instance;
    }


    public boolean isSettingMsgNotification() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_NOTIFICATION, true);
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        editor.putBoolean(PREF_KEY_SETTING_NOTIFICATION, paramBoolean).commit();
    }

    public boolean isSettingMsgSound() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_SOUND, true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        editor.putBoolean(PREF_KEY_SETTING_SOUND, paramBoolean).commit();
    }

    public boolean isSettingMsgVibrate() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_VIBRATE, true);
    }

    public boolean setSettingMsgVibrate(boolean paramBoolean) {
        return editor.putBoolean(PREF_KEY_SETTING_VIBRATE, paramBoolean).commit();
    }

    public boolean isSettingMsgSpeaker() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_SPEAKER, true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        editor.putBoolean(PREF_KEY_SETTING_SPEAKER, paramBoolean).commit();
    }

    /**
     * 设置组同步
     */
    public void setGroupSync(boolean synced) {
        editor.putBoolean(PREF_KEY_SETTING_GROUPS_SYNC, synced).commit();
    }

    /**
     * 是否组同步
     *
     * @return
     */
    public boolean isGroupSync() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_GROUPS_SYNC, false);
    }

    /**
     * 设置联系人同步
     *
     * @param synced
     */
    public void setContactSync(boolean synced) {
        editor.putBoolean(PREF_KEY_SETTING_CONTACT_SYNC, synced).commit();
    }

    /**
     * 联系人是否同步
     *
     * @return
     */
    public boolean isContactSync() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_CONTACT_SYNC, false);
    }

    /**
     * 设置黑名单同步
     */
    public void setBlacklistSync(boolean sync) {

        editor.putBoolean(PREF_KEY_SETTING_BLACKLIST_SYNC, sync).commit();
    }

    /**
     * 黑名单是否同步
     *
     * @return
     */
    public boolean isBlacklistSync() {
        return mSharedPreference.getBoolean(PREF_KEY_SETTING_BLACKLIST_SYNC, false);
    }

    /**
     * 设置当前用户的昵称
     *
     * @param nickName
     */
    public void setCurrentUserNickName(String nickName) {
        editor.putString(PREF_KEY_CURRENT_USER_NICK_NAME, nickName).commit();
    }

    /**
     * 获取当前用户的昵称
     *
     * @return
     */
    public String getCurrentUserNickName() {
        return mSharedPreference.getString(PREF_KEY_CURRENT_USER_NICK_NAME, null);
    }

    /**
     * 设置当前用户的头像
     *
     * @param headImageUrl
     */
    public void setCurrentUserHeadImage(String headImageUrl) {
        editor.putString(PREF_KEY_CURRENT_USER_HEAD_IMAGE, headImageUrl).commit();
    }

    /**
     * 获取当前用户的头像
     *
     * @return
     */
    public String getCurrentUserHeadImage() {
        return mSharedPreference.getString(PREF_KEY_CURRENT_USER_HEAD_IMAGE, null);
    }

    /**
     * 删除当前用户信息
     */
    public void removeCurrentUserInfo() {
        editor.remove(PREF_KEY_CURRENT_USER_NICK_NAME);
        editor.remove(PREF_KEY_CURRENT_USER_HEAD_IMAGE);
        editor.commit();
    }
}
