package com.wl.pluto.plutochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.constant.NavigationConstant;

public class NavigationActivity extends BaseActivity {

    /**
     * 数据列表
     */
    private ListView mNavigationListView;

    private ArrayAdapter<String> adapter;

    private static final String ACTIVITY_PATH = "com.wl.pluto.plutochat.activity.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        initLayout();
    }

    private void initLayout() {
        mNavigationListView = (ListView) findViewById(R.id.lv_navigation_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                NavigationConstant.NAVIGATION_DATA);

        mNavigationListView.setAdapter(adapter);

        mNavigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectActivity = adapter.getItem(position);
                try {
                    Intent intent = new Intent(NavigationActivity.this, Class.forName(ACTIVITY_PATH + selectActivity));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
