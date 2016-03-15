package com.wl.pluto.plutochat.chat.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wl.pluto.plutochat.chat.base.BaseLogicModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于SharedPreference来设置和获取在Setting里面设置的值，同时也保存Chat Id 和 password
 * <p>
 * Created by jeck on 15-11-18.
 */
public class DefaultLogicModel extends BaseLogicModel {

    private static final String PREF_USER_NAME = "user_name";

    private static final String PREF_PASSWORD = "pref_password";

    protected Context context;

    /**
     *
     */
    public enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpeakerOn,
        DisabledGroups,
        DisableIds;
    }

    protected Map<Key, Object> valueCache = new HashMap<>();

    /*************************************************************************************/


    public DefaultLogicModel(Context context) {

        this.context = context;

        //初始化与ｍｏｄｅｌ相对应的preference
        PreferenceForModel.init(context);
    }


    @Override
    public boolean isSettingMsgNotification() {

        Object result = valueCache.get(Key.VibrateAndPlayToneOn);
        if (result == null) {
            result = PreferenceForModel.getInstance().isSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, result);
        }
        return (Boolean) (result != null ? result : true);
    }

    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceForModel.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    @Override
    public boolean isSettingMsgSound() {
        Object result = valueCache.get(Key.PlayToneOn);
        if (result == null) {
            result = PreferenceForModel.getInstance().isSettingMsgSound();
            valueCache.put(Key.PlayToneOn, result);
        }
        return (Boolean) (result != null ? result : true);
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceForModel.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    @Override
    public boolean isSettingMsgVibrate() {
        Object result = valueCache.get(Key.VibrateOn);
        if (result == null) {
            result = PreferenceForModel.getInstance().isSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, result);
        }
        return (Boolean) (result != null ? result : true);
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceForModel.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    @Override
    public boolean isSettingMsgSpeaker() {
        Object result = valueCache.get(Key.SpeakerOn);
        if (result == null) {
            result = PreferenceForModel.getInstance().isSettingMsgSpeaker();
            valueCache.put(Key.SpeakerOn, result);
        }
        return (Boolean) (result != null ? result : true);
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceForModel.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpeakerOn, paramBoolean);
    }

    @Override
    public String getChatID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USER_NAME, null);
    }

    @Override
    public boolean saveChatID(String chatID) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USER_NAME, chatID).commit();
    }

    @Override
    public String getUserPassword() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_PASSWORD, null);
    }

    @Override
    public boolean saveUserPassword(String password) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_PASSWORD, password).commit();
    }

    @Override
    public String getAppProgressName() {
        return null;
    }
}
