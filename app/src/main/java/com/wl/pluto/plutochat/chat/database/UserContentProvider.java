package com.wl.pluto.plutochat.chat.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.FileNotFoundException;

/**
 * ContentProvider 框架
 *
 * @author jeck
 */
public class UserContentProvider extends ContentProvider {

    /**
     * SQLiteHelper
     *
     * @author jeck
     */
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "pluto_chat.db";

        public static final String TABLE_NAME = "user_login";

        public static final int DATABASE_VERSION = 1;

        // 创建数据库的SQL语句
        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME + "(" + KEY_ID
                + " integer primary key autoincrement, " + COLUMN_NAME_1
                + " text not null, " + COLUMN_NAME_2 + " text not null "
                + ");";

        public MySQLiteOpenHelper(Context context, String name,
                                  CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // 当磁盘上不存在数据库，辅助类需要创建一个新的数据库时调用
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // 通过比较ｏｌｄ　和ｎｅｗ之间的值，来处理多个版本不同的问题

            // 最简单的方法是将老的数据库直接删除．再重新新建一次
            db.execSQL("DROP TABLE IF IT EXISTS " + TABLE_NAME);

            onCreate(db);
        }

    }

    /**
     * 为该ContentProvider创建一个ＵＲＩ，其他应用程序组件可以通过这个ＵＲＩ来访问该Ｐｒｏｖｉｄｅ
     */
    public static final Uri CONTENT_URI = Uri
            .parse("content://com.wl.pluto.plutochat.chat.provider/elements");


    /**
     * 为数据库的表中的每一列创建一个共有的字段
     * public static final String COLUMN_NAME_3 = "column_name_3";
     */

    /**
     * where 子句中使用的索引列的名称
     */
    public static final String KEY_ID = "_id";

    /**
     * 用户名
     */
    public static final String COLUMN_NAME_1 = "user_name";

    /**
     * 密码
     */
    public static final String COLUMN_NAME_2 = "user_password";

    /**
     * 创建两个不同的常亮来区分不同的ＵＲＩ请求
     */
    private static final int ALL_ROWS = 1;

    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;

    static {


        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 以elements　结尾的ＵＲＩ对应请求全部数据
        uriMatcher.addURI("com.wl.pluto.plutochat.chat.provider", "elements", ALL_ROWS);

        // 以 elements/[rowID]　结尾的ＵＲＩ对应请求单行数据
        uriMatcher.addURI("com.wl.pluto.plutochat.chat.provider", "elements/#", SINGLE_ROW);
    }


    /**
     * SQLiteHelper, 这货的作业就是创建，打开数据库，优点就是可以在你需要的时候打开数据库
     */
    private MySQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {

        // 构造数据库，在你需要的时候，创建，打开数据库
        mOpenHelper = new MySQLiteOpenHelper(getContext(),
                MySQLiteOpenHelper.DATABASE_NAME, null,
                MySQLiteOpenHelper.DATABASE_VERSION);

        // 这个地方有什么玄机吗？
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // 为一个Content Provider Uri 返回一个字符串，它标示了ＭＩＭＥ类型
        switch (uriMatcher.match(uri)) {

            case ALL_ROWS:
                return "vnd.android.cursor.dir/vnd.wl.pluto.plutochat.element";


            case SINGLE_ROW:
                return "vnd.android.cursor.item/vnd.wl.pluto.plutochat.element";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // 打开数据库
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // 必要的话，使用有效的SQL语句代替这些语句
        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.TABLE_NAME);

        // 如果是行查询，　用传入的行限制结构集
        switch (uriMatcher.match(uri)) {

            case SINGLE_ROW:
                String rowId = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowId);

                break;
            case ALL_ROWS:
                break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // 打开数据库
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // 要想通过传入一个空的contentValue 对象的方式向数据库中添加一个空行，必须要使用nullColumnHack参数来
        // 　指定可以设置为null的列名
        String nullColumnHack = null;

        long id = db.insert(MySQLiteOpenHelper.TABLE_NAME, nullColumnHack,
                values);

        // 构造并返回新插入行的ＵＲＩ
        if (id > -1) {

            // 构造并返回新插入行的ＵＲＩ
            Uri insertId = ContentUris.withAppendedId(CONTENT_URI, id);

            // 通知所有观察者，数据集以改变
            getContext().getContentResolver().notifyChange(insertId, null);

            return insertId;
        } else {

            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // 打开数据库
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // 如果是行uri, 限定删除的行为指定的行
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:

                String rowId = uri.getPathSegments().get(1);

                selection = KEY_ID
                        + "+"
                        + rowId
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection
                        + ')' : "");
                break;
        }

        // 要想返回删除项的数量，　必须指定一条ｗｈｅｒｅ字句
        if (selection == null) {
            selection = "1";
        }

        int deleteCount = db.delete(MySQLiteOpenHelper.TABLE_NAME, selection,
                selectionArgs);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        // 打开数据库
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // 如果是行uri, 限定删除的行为指定的行
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:

                String rowId = uri.getPathSegments().get(1);

                selection = KEY_ID
                        + "+"
                        + rowId
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection
                        + ')' : "");
                break;
        }

        // 执行更新操作
        int updateCount = db.update(MySQLiteOpenHelper.TABLE_NAME, values,
                selection, selectionArgs);

        // 通知所有观察者，数据集以改变
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        return super.openFile(uri, mode);
    }
}
