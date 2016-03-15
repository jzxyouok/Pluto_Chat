package com.wl.pluto.plutochat.chat.entity;

/**
 * 请求消息
 * Created by jeck on 15-11-19.
 */
public class InviteMessageEntity {

    /**
     * 请求ｉｄ
     */
    private int inviteId;

    /**
     * 　请求来自哪里，　一般应该是对方的username
     */
    private String from;

    /**
     * 请求理由
     */
    private String reason;

    /**
     * 时间
     */
    private long time;

    /**
     * 群ｉｄ
     */
    private String groupId;

    /**
     * 群名称
     */
    private String groupName;

    public enum InviteMessageStatus {
        /**
         * 被邀请
         */
        BE_INVITED,

        /**
         * 我邀请，被对方拒绝
         */
        BE_REFUSED,

        /**
         * 我邀请，被对方同意了
         */
        BE_AGREED,

        /**
         * 对方申请
         */
        BE_APPLIED,

        /**
         * 我同意了对方的请求
         */
        AGREED,

        /**
         * 我拒绝了对方的请求
         */
        REFUSED,
    }

    private InviteMessageStatus status;

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public InviteMessageStatus getStatus() {
        return status;
    }

    public void setStatus(InviteMessageStatus status) {
        this.status = status;
    }
}
