package com.wl.pluto.plutochat.chat.constant;

import com.wl.pluto.plutochat.chat.base.BaseApplication;

/**
 * Created by jeck on 15-11-19.
 */
public class UserConstant {

    /**
     * 从联系人链表点击跳转到该联系人的个人详情界面需要传递的username
     */
    public static final String CHAT_USER_NAME_KEY = "chat_user_name_key";

    /**
     * 从联系人链表点击跳转到该联系人的个人详情界面需要传递的nickname
     */
    public static final String CHAT_USER_NICK_NAME_KEY = "chat_user_nick_name_key";

    /**
     * 从联系人链表点击跳转到该联系人的个人详情界面需要传递的头像地址
     */
    public static final String CHAT_USER_HEAD_IMAGE_URL_KEY = "chat_user_head_image_url_key";

    /**
     * 用户头像的宽度
     */
    public static final int USER_HEAD_IMAGE_WIDTH = 120;

    /**
     * 用户头像的高度
     */
    public static final int USER_HEAD_IMAGE_HEIGHT = 120;

    /**
     * 联系人链表和聊天界面用户头像的宽度
     */
    public static final int CHAT_HEAD_WIDTH = 80;

    /**
     * 联系人链表和聊天界面用户头像的高度
     */
    public static final int CHAT_HEAD_HEIGHT = 80;

    /**
     * 全局唯一的当前用户头像的名称
     */
    public static final String MY_HEAD_IMAGE_NAME = BaseApplication.getInstance().getUserName() + ".png";

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
}
