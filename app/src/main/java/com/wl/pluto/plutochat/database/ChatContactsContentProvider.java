package com.wl.pluto.plutochat.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.wl.pluto.plutochat.greedao.ContactsUserDao;

import java.io.FileNotFoundException;

public class ChatContactsContentProvider extends ContentProvider {

    /**
     * SQLiteHelper
     */
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "pluto_chat.db";

        public static final String TABLE_NAME = "contacts_user_table";

        public static final int DATABASE_VERSION = 1;

        // 创建数据库的SQL语句
        /**
         * 创建联系人表，该表是整个项目工程中最重要的表，包含了所有的联系人。
         * 包括自己。添加，删除，聊天都会用到该表
         */
        private static final String CREATE_CONTACTS_TABLE = "create table "
                + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " integer primary key autoincrement,"
                + COLUMN_NAME_USERNAME + " text not null,"
                + COLUMN_NAME_NICKNAME + " text,"
                + COLUMN_NAME_HEADIMAGE_URL + " text,"
                + COLUMN_NAME_GENDER + " text,"
                + COLUMN_NAME_ADDRESS + " text,"
                + COLUMN_NAME_SIGNATURE + " text);";

        public MySQLiteOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // 当磁盘上不存在数据库，辅助类需要创建一个新的数据库时调用
            db.execSQL(CREATE_CONTACTS_TABLE);
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
     * 权限
     */
    public static final String AUTHORITY = "com.wl.pluto.plutochat.ContactsProviderAuthority";

    public static final String URI = "content://" + AUTHORITY + "/elements";

    /**
     * 为该ContentProvider创建一个ＵＲＩ，其他应用程序组件可以通过这个ＵＲＩ来访问该Ｐｒｏｖｉｄｅ
     */
    public static final Uri CONTENT_URI = Uri.parse(URI);

    /**
     * where 子句中使用的索引列的名称
     */
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_USERNAME = "userName";
    public static final String COLUMN_NAME_NICKNAME = "nickName";
    public static final String COLUMN_NAME_HEADIMAGE_URL = "headImageUrl";
    public static final String COLUMN_NAME_GENDER = "gender";
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final String COLUMN_NAME_SIGNATURE = "personalitySignature";

    /**
     * 创建两个不同的常亮来区分不同的ＵＲＩ请求
     */
    private static final int ALL_ROWS = 1;

    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;

    static {


        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 以elements　结尾的ＵＲＩ对应请求全部数据
        uriMatcher.addURI(AUTHORITY, "elements", ALL_ROWS);

        // 以 elements/[rowID]　结尾的ＵＲＩ对应请求单行数据
        uriMatcher.addURI(AUTHORITY, "elements/#", SINGLE_ROW);
    }


    /**
     * SQLiteHelper, 这货就是创建，打开数据库，优点就是可以在你需要的时候打开数据库
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
                queryBuilder.appendWhere(COLUMN_NAME_ID + "=" + rowId);

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

                selection = COLUMN_NAME_ID
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

                selection = COLUMN_NAME_ID
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
