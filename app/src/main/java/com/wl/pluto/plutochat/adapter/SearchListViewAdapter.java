package com.wl.pluto.plutochat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.entity.UserEntity;

import java.util.List;

/**
 * Created by jeck on 15-11-15.
 */
public class SearchListViewAdapter extends BaseAdapter {

    private static final String TAG = "--SearchListViewAdapter-->";

    private Context context;

    private List<UserEntity> userEntityList;

    public SearchListViewAdapter(Context context, List<UserEntity> list) {

        this.context = context;
        this.userEntityList = list;
    }

    @Override
    public int getCount() {
        return userEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return userEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_search_list_item, null);

            holder.searchHeadImageView = (ImageView) convertView.findViewById(R.id.iv_search_thumbnail);
            holder.searchUsernameTextView = (TextView) convertView.findViewById(R.id.tv_search_user_name);
            holder.searchAddButton = (Button) convertView.findViewById(R.id.btn_add_new_people);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        UserEntity entity = (UserEntity) getItem(position);

        //使用Picasso框架加载网络图片
//        Picasso.with(context)
//                .load(entity.getUserHeadImageUrl())
//                .resize(80, 80)
//                .centerCrop()
//                .placeholder(R.mipmap.head_image_default2)
//                .into(holder.searchHeadImageView);

        holder.searchUsernameTextView.setText(entity.getUsername());
        holder.searchAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "holder.searchAddButton.setOnClickListener");
            }
        });

        return convertView;
    }


    private static class ViewHolder {

        /**
         * 头像
         */
        public ImageView searchHeadImageView;

        /**
         * username
         */
        public TextView searchUsernameTextView;

        /**
         * 添加联系人
         */
        public Button searchAddButton;
    }
}
