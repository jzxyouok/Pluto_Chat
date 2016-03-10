package com.wl.pluto.plutochat.greedao;

import android.content.ContentValues;
import android.content.Context;

import com.wl.pluto.plutochat.entity.UserEntity;
import com.wl.pluto.plutochat.manager.DatabaseManager;

import java.util.List;
import java.util.Map;

/**
 * 该类的作用是连接用户和数据库的桥梁，起到桥梁的作用．用户是通过调用该类来实现数据的增删改查的
 * 　而该类有需要DBManager来实现对数据库的增删改查．而DBOpenHelper只是用来创建和更新数据库．
 * Created by jeck on 15-11-19.
 */
public class ContactsUserDao {

    public static final String TABLE_NAME = "contacts_table";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_USERNAME = "userName";
    public static final String COLUMN_NAME_NICKNAME = "nickName";
    public static final String COLUMN_NAME_HEADIMAGE_URL = "headImageUrl";
    public static final String COLUMN_NAME_GENDER = "gender";
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final String COLUMN_NAME_SIGNATURE = "personalitySignature";


    public ContactsUserDao(Context context) {

        //需要调用DBManager来初始化数据库
        DatabaseManager.getInstance().init(context);
    }

    /**
     * 保存好友链表到数据库
     */
    public void saveContactsList(List<UserEntity> contactList) {

        DatabaseManager.getInstance().saveContactList(contactList);
    }

    /**
     * 从数据库中获取好友
     *
     * @return
     */
    public Map<String, UserEntity> getContactsList() {
        return DatabaseManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人, 从现在起，所有的username,都换成chatID
     */
    public void deleteContact(String username) {
        DatabaseManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    public void addContact(UserEntity user) {
        DatabaseManager.getInstance().addContact(user);
    }

    /**
     * 根据用户名跟新用户数据
     *
     * @param userName
     */
    public void updateContactByName(String userName, ContentValues updateValues) {

        DatabaseManager.getInstance().updateContactByName(userName, updateValues);
    }

    /**
     * 根据用户名获取用户数据
     *
     * @param username
     * @return
     */
    public UserEntity getContactByName(String username) {
        return DatabaseManager.getInstance().getContactByName(username);
    }
}
