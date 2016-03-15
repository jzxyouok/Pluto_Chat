package com.wl.pluto.plutochat.chat.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.adapter.NearbyPeopleListViewAdapter;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.constant.URLConstant;
import com.wl.pluto.plutochat.chat.entity.NearbyPeopleEntity;
import com.wl.pluto.plutochat.chat.greedao.DaoMaster;
import com.wl.pluto.plutochat.chat.greedao.DaoSession;
import com.wl.pluto.plutochat.chat.greedao.NearbyPeopleEntityDao;

import java.util.ArrayList;
import java.util.Random;

public class PeopleNearbyActivity extends BaseActivity {

    /**
     * 附件的人
     */
    private ListView mNearbyPeopleListView;

    /**
     * 通用链表适配器
     */
    private NearbyPeopleListViewAdapter mListViewAdapter = null;

    /**
     * 适配器数据源
     */
    private ArrayList<NearbyPeopleEntity> mNearbyPeopleEntityList = new ArrayList<>();

    /**
     * 数据库
     */
    private SQLiteDatabase db;

    /**
     * 　DaoMaster
     */
    private DaoMaster daoMaster;

    /**
     * DaoSession
     */
    private DaoSession daoSession;

    /**
     * 　Cursor
     */
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);
        initDatabase();
        initLayout();

        //addNearbyPeopleEntity();

        new LoadDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_people_nearby, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_people:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLayout() {

        mNearbyPeopleListView = (ListView) findViewById(R.id.lv_people_nearby_list);
        mListViewAdapter = new NearbyPeopleListViewAdapter(this, mNearbyPeopleEntityList);
        mNearbyPeopleListView.setAdapter(mListViewAdapter);
    }

    private void initDatabase() {

        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "chats-db", null);

        db = helper.getWritableDatabase();

        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);

        daoSession = daoMaster.newSession();
    }

    private NearbyPeopleEntityDao getNearbyPeopleEntityDao() {
        return daoSession.getNearbyPeopleEntityDao();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 为数据库添加30条记录，只能用一次
     */
    private void addNearbyPeopleEntity() {

        Random random = new Random();

        for (int i = 0; i < 30; i++) {

            //新建一个NearbyPeopleEntity
            NearbyPeopleEntity entity = new NearbyPeopleEntity();


            //设置主键
            entity.setId((long) i);

            //设置昵称
            if (i < 10) {

                //设置ChatID
                entity.setUsername("1364838000" + i);

                entity.setUserNickName("pluto_0" + i);
            } else {

                entity.setUserNickName("pluto_" + i);
                //设置ChatID
                entity.setUsername("136483800" + i);
            }

            //设置性别
            if (i % 2 == 0) {

                entity.setUserGender("female");
            } else {
                entity.setUserGender("male");
            }

            //设置地址
            entity.setUserAddress("重庆");

            entity.setUserHeadImageUrl(URLConstant.TEST_IMAGE_URL);

            //随机生成距离
            double distance = random.nextInt(10000);

            entity.setUserDistance(distance + "m");

            entity.setUserPersonalitySignature("天生我材必有用");

            getNearbyPeopleEntityDao().insert(entity);
        }
    }


    private class LoadDataTask extends AsyncTask<Void, Void, Cursor> {

        private String[] columns = {
                NearbyPeopleEntityDao.Properties.UserHeadImageUrl.columnName,
                NearbyPeopleEntityDao.Properties.UserNickName.columnName,
                NearbyPeopleEntityDao.Properties.UserPersonalitySignature.columnName,
                NearbyPeopleEntityDao.Properties.UserDistance.columnName
        };

        @Override
        protected Cursor doInBackground(Void... params) {

            return cursor = db.query(getNearbyPeopleEntityDao().getTablename(),
                    columns, null, null, null, null, null);

        }

        @Override
        protected void onPostExecute(Cursor cursor) {

            if (cursor != null) {

                while (cursor.moveToNext()) {
                    NearbyPeopleEntity entity = new NearbyPeopleEntity();

                    String imageUrl = NearbyPeopleEntityDao.Properties.UserHeadImageUrl.columnName;
                    entity.setUserHeadImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(imageUrl)));

                    String nickName = NearbyPeopleEntityDao.Properties.UserNickName.columnName;
                    entity.setUserNickName(cursor.getString(cursor.getColumnIndexOrThrow(nickName)));

                    String signature = NearbyPeopleEntityDao.Properties.UserPersonalitySignature.columnName;
                    entity.setUserPersonalitySignature(cursor.getString(cursor.getColumnIndexOrThrow(signature)));

                    String distance = NearbyPeopleEntityDao.Properties.UserDistance.columnName;
                    entity.setUserDistance(cursor.getString(cursor.getColumnIndexOrThrow(distance)));

                    mNearbyPeopleEntityList.add(entity);
                }

                mListViewAdapter.notifyDataSetChanged();
            }
        }
    }

}
