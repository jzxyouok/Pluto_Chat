package com.wl.pluto.plutochat.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.constant.UserConstant;
import com.wl.pluto.plutochat.chat.entity.NearbyPeopleEntity;

import java.util.ArrayList;

/**
 * Created by jeck on 15-11-10.
 */
public class NearbyPeopleListViewAdapter extends BaseAdapter {


    private ArrayList<NearbyPeopleEntity> nearbyPeopleList;

    private Context context;

    public NearbyPeopleListViewAdapter(Context context, ArrayList<NearbyPeopleEntity> list) {

        this.context = context;
        this.nearbyPeopleList = list;
    }

    @Override
    public int getCount() {
        return nearbyPeopleList.size();
    }

    @Override
    public Object getItem(int position) {
        return nearbyPeopleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_nearby_people_list_item, null);

            holder = new ViewHolder();

            holder.nearbyHeadImageView = (ImageView) convertView.findViewById(R.id.iv_nearby_thumbnail);
            holder.nearbyNickNameTextView = (TextView) convertView.findViewById(R.id.tv_nearby_nick_name);
            holder.nearbyDistanceTextView = (TextView) convertView.findViewById(R.id.tv_nearby_distance);
            holder.nearbySignatureTextView = (TextView) convertView.findViewById(R.id.tv_nearby_personality_signature);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        NearbyPeopleEntity entity = nearbyPeopleList.get(position);

        //使用Picasso框架加载网络图片
        Glide.with(context)
                .load(entity.getUserHeadImageUrl())
                .override(UserConstant.CHAT_HEAD_WIDTH, UserConstant.CHAT_HEAD_HEIGHT)
                .centerCrop()
                .placeholder(R.mipmap.head_image_default2)
                .into(holder.nearbyHeadImageView);

        //使用Picasso加载本地资源文件
        //Picasso.with(context).load(R.mipmap.head_image_default2).into(holder.nearbyHeadImageView);

        holder.nearbyNickNameTextView.setText(entity.getUserNickName());
        holder.nearbyDistanceTextView.setText(entity.getUserDistance());
        holder.nearbySignatureTextView.setText(entity.getUserPersonalitySignature());

        return convertView;
    }

    private static class ViewHolder {

        /**
         * 头像
         */
        public ImageView nearbyHeadImageView;

        /**
         * 昵称
         */
        public TextView nearbyNickNameTextView;

        /**
         * 距离
         */
        public TextView nearbyDistanceTextView;

        /**
         * 签名
         */
        public TextView nearbySignatureTextView;
    }
}
