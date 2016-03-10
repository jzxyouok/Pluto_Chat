package com.wl.pluto.plutochat.entity;

import com.wl.pluto.plutochat.constant.URLConstant;

/**
 * Created by jeck on 15-11-1.
 */
public class ContactsItemData {

    /**
     * 联系人头像地址
     */
    private String contactsUserHeadImageURL = URLConstant.TEST_IMAGE_URL;

    /**
     * 用户的昵称，用于在ContactsFragment的链表中显示
     */
    private String contactsUserNickname;

    /**
     * 用户名
     */
    private String contactsUserName;

    /**
     * 显示数据拼音的首字母
     */
    private String sortLetters;

    /**
     * 获取用户的昵称
     *
     * @return
     */
    public String getContactsUserNickname() {
        return contactsUserNickname;
    }

    public void setContactsUserNickname(String contactsUserNickname) {
        this.contactsUserNickname = contactsUserNickname;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getContactsUserHeadImageURL() {
        return contactsUserHeadImageURL;
    }

    public void setContactsUserHeadImageURL(String contactsUserHeadImageURL) {
        this.contactsUserHeadImageURL = contactsUserHeadImageURL;
    }

    public String getContactsUserName() {
        return contactsUserName;
    }

    public void setContactsUserName(String contactsUserName) {
        this.contactsUserName = contactsUserName;
    }
}
