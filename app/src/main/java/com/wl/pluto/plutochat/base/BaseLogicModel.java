package com.wl.pluto.plutochat.base;

/**
 * 主要用来管理user 和　preference
 * <p/>
 * Created by jeck on 15-11-18.
 */
public abstract class BaseLogicModel {

    /**
     * 震动和声音的总开关，有消息来是，是否允许
     *
     * @return
     */
    public abstract boolean isSettingMsgNotification();

    public abstract void setSettingMsgNotification(boolean paramBoolean);

    /**
     * 是否打开声音
     *
     * @return
     */
    public abstract boolean isSettingMsgSound();

    public abstract void setSettingMsgSound(boolean paramBoolean);

    /**
     * 是否打开震动
     */
    public abstract boolean isSettingMsgVibrate();

    public abstract void setSettingMsgVibrate(boolean paramBoolean);

    /**
     * 是否打开扬声器
     */
    public abstract boolean isSettingMsgSpeaker();

    public abstract void setSettingMsgSpeaker(boolean paramBoolean);

    /**
     * 获取Chat ID
     */
    public abstract String getChatID();

    public abstract boolean saveChatID(String chatID);

    /**
     * 获取密码
     */
    public abstract String getUserPassword();

    public abstract boolean saveUserPassword(String password);

    /**
     * 获取应用程序的进程名称, 默认是包名
     */
    public abstract String getAppProgressName();

    /**
     * 是否总是接受好友请求
     */
    public boolean isAcceptInvitationAlways() {
        return true;
    }

    /**
     * 是否需要环信好友关系，默认是true, 因为我们现在需要依赖环信的SDK来维护好友关系，所以这个地方返回true
     *
     * @return
     */
    public boolean isUseHXRoster() {
        return true;
    }

    /**
     * 是否需要已读回执
     *
     * @return
     */
    public boolean isRequireReadAck() {
        return true;
    }

    /**
     * 是否需要已达回执
     *
     * @return
     */
    public boolean isRequireDeliverAck() {
        return true;
    }

    /**
     * 是否运行在sandbox测试环境. 默认是关掉的
     * 设置sandbox 测试环境
     * 建议开发者开发时设置此模式
     */
    public boolean isSandBoxMode() {
        return false;
    }

    /**
     * 是否设置debug模式
     *
     * @return
     */
    public boolean isDebugMode() {
        return true;
    }

    /**
     * 设置组同步
     */
    public void setGroupSync(boolean synced) {

    }

    /**
     * 是否组同步
     *
     * @return
     */
    public boolean isGroupSync() {
        return false;
    }

    /**
     * 设置联系人同步
     *
     * @param synced
     */
    public void setContactSync(boolean synced) {

    }

    /**
     * 联系人是否同步
     *
     * @return
     */
    public boolean isContactSync() {
        return false;
    }

    /**
     * 设置黑名单同步
     */
    public void setBlacklistSync(boolean synced) {

    }

    /**
     * 黑名单是否同步
     *
     * @return
     */
    public boolean isBlacklistSync() {
        return false;
    }
}
