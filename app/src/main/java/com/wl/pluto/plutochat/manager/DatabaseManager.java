package com.wl.pluto.plutochat.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wl.pluto.plutochat.database.DBOpenHelper;
import com.wl.pluto.plutochat.entity.UserEntity;
import com.wl.pluto.plutochat.greedao.ContactsUserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 真正操作数据库的类，用于对数据库进行增删改查，为DAO层提供服务
 * <p/>
 * Created by jeck on 15-11-19.
 */
public class DatabaseManager {

    /**
     * 数据库类
     */
    private DBOpenHelper dbOpenHelper;

    /**
     * 这种技巧叫做提前初始化，只有程序一启动，就会实例化该类
     */
    private static DatabaseManager instance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        dbOpenHelper = DBOpenHelper.getInstance(context);
    }

    /**
     * 将好友保存到数据库中
     *
     * @param users
     */
    public synchronized void saveContactList(List<UserEntity> users) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(ContactsUserDao.TABLE_NAME, null, null);

            for (UserEntity item : users) {
                ContentValues values = new ContentValues();
                values.put(ContactsUserDao.COLUMN_NAME_USERNAME, item.getUsername());
                values.put(ContactsUserDao.COLUMN_NAME_NICKNAME, item.getUserNickName());
                values.put(ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL, item.getUserHeadImageUrl());
                values.put(ContactsUserDao.COLUMN_NAME_GENDER, item.getUserGender());
                values.put(ContactsUserDao.COLUMN_NAME_ADDRESS, item.getUserAddress());
                values.put(ContactsUserDao.COLUMN_NAME_SIGNATURE, item.getUserPersonalitySignature());

                db.replace(ContactsUserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取好友链表
     *
     * @return
     */
    public synchronized Map<String, UserEntity> getContactList() {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        String sql = "SELECT * FROM " + ContactsUserDao.TABLE_NAME;

        Map<String, UserEntity> userMap = new HashMap<>();

        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {

                UserEntity user = new UserEntity();
                String username = cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_USERNAME));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_USERNAME)));
                user.setUserNickName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_NICKNAME)));
                user.setUserHeadImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL)));
                user.setUserGender(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_GENDER)));
                user.setUserAddress(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_ADDRESS)));
                user.setUserPersonalitySignature(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_SIGNATURE)));
                userMap.put(username, user);
            }
            cursor.close();
        }
        return userMap;
    }

    /**
     * 删除一个好友
     *
     * @param username
     */
    public synchronized void deleteContact(String username) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(ContactsUserDao.TABLE_NAME, ContactsUserDao.COLUMN_NAME_USERNAME + " = ?", new String[]{username});
        }
    }

    /**
     * 添加一个好友
     *
     * @param user
     */
    public synchronized void addContact(UserEntity user) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {

            ContentValues values = new ContentValues();
            values.put(ContactsUserDao.COLUMN_NAME_USERNAME, user.getUsername());
            values.put(ContactsUserDao.COLUMN_NAME_NICKNAME, user.getUserNickName());
            values.put(ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL, user.getUserHeadImageUrl());
            values.put(ContactsUserDao.COLUMN_NAME_GENDER, user.getUserGender());
            values.put(ContactsUserDao.COLUMN_NAME_ADDRESS, user.getUserAddress());
            values.put(ContactsUserDao.COLUMN_NAME_SIGNATURE, user.getUserPersonalitySignature());

            db.insert(ContactsUserDao.TABLE_NAME, null, values);
        }

    }

    /**
     * 跟新用户数据
     *
     * @param userName
     */
    public synchronized void updateContactByName(String userName, ContentValues updateValues) {

        String where = ContactsUserDao.COLUMN_NAME_USERNAME + "=" + userName;
        String[] whereArgs = null;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {

            db.update(ContactsUserDao.TABLE_NAME, updateValues, where, whereArgs);
        }
    }

    /**
     * 根据名称获取某一个联系人
     *
     * @param username
     */
    public synchronized UserEntity getContactByName(String username) {

        UserEntity user = null;

        //需要返回的字段
        String[] result_columns = new String[]{
                ContactsUserDao.COLUMN_NAME_NICKNAME,
                ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL,
                ContactsUserDao.COLUMN_NAME_ADDRESS,
                ContactsUserDao.COLUMN_NAME_GENDER,
                ContactsUserDao.COLUMN_NAME_SIGNATURE
        };

        //条件语句
        String where = ContactsUserDao.COLUMN_NAME_USERNAME + "=" + username;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(ContactsUserDao.TABLE_NAME, result_columns, where, whereArgs, groupBy, having, order);

        if (cursor != null) {

            while (cursor.moveToNext()) {

                user = new UserEntity();
                user.setUserNickName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_NICKNAME)));
                user.setUserHeadImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL)));
                user.setUserGender(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_GENDER)));
                user.setUserAddress(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_ADDRESS)));
                user.setUserPersonalitySignature(cursor.getString(cursor.getColumnIndexOrThrow(ContactsUserDao.COLUMN_NAME_SIGNATURE)));
            }
            cursor.close();
        }

        return user;
    }
}
