package com.wl.pluto.plutochat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wl.pluto.plutochat.greedao.ContactsUserDao;

/**
 * Created by jeck on 15-11-19.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库版本
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 实例
     */
    private volatile static DBOpenHelper instance;

    /**
     * 创建用户表，我现在还不是很清楚这张表是干什么用的，
     */
    private static final String CREATE_USER_TABLE = "create table "
            + ContactsUserDao.TABLE_NAME + " ("
            + ContactsUserDao.COLUMN_NAME_ID + " integer primary key autoincrement,"
            + ContactsUserDao.COLUMN_NAME_USERNAME + " text not null,"
            + ContactsUserDao.COLUMN_NAME_NICKNAME + " text,"
            + ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL + " text,"
            + ContactsUserDao.COLUMN_NAME_GENDER + " text,"
            + ContactsUserDao.COLUMN_NAME_ADDRESS + " text,"
            + ContactsUserDao.COLUMN_NAME_SIGNATURE + " text);";

    private DBOpenHelper(Context context) {
        super(context, "chat_db", null, DATABASE_VERSION);
    }

    public static DBOpenHelper getInstance(Context context) {
        if (instance == null) {

            synchronized (DBOpenHelper.class) {

                if (instance == null) {

                    instance = new DBOpenHelper(context);
                }
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 关闭数据库
     */
    public void closeDatabase() {
        if (instance != null) {
            try {

                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {

                e.printStackTrace();
            }

            instance = null;
        }
    }
}
