package com.wl.pluto.plutochat.chat.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.util.TextFormater;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.entity.VideoItem;
import com.wl.pluto.plutochat.chat.manager.ImageFetchManager;
import com.wl.pluto.plutochat.chat.utils.DateTimeUtils;
import com.wl.pluto.plutochat.chat.utils.DensityUtils;


public class ChooseVideoAdapter extends BaseAdapter {

    private static final String TAG = "--ChooseVideoAdapter-->";

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 视频列表
     */
    private ArrayList<VideoItem> videoList;

    /**
     * 构造器
     */
    public ChooseVideoAdapter(Context context) {
        mContext = context;
    }

    /**
     * 为适配器添加数据源
     *
     * @param videoList
     */
    public void setVideoList(ArrayList<VideoItem> videoList) {
        this.videoList = videoList;
    }

    @Override
    public int getCount() {
        if (videoList != null && videoList.size() != 0) {
            return videoList.size() + 1;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {

        if (position == 0) {
            return null;
        } else {

            return videoList.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_choose_video_grid_item, null);

            holder.videoThumbnail = (ImageView) convertView.findViewById(R.id.iv_choose_video_thumbnail);
            holder.videoSize = (TextView) convertView.findViewById(R.id.tv_choose_video_size);
            holder.videoDuration = (TextView) convertView.findViewById(R.id.tv_choose_video_duration);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            holder.videoDuration.setVisibility(View.GONE);
            holder.videoThumbnail.setImageResource(R.drawable.choose_video_camera_background);
            holder.videoSize.setText(R.string.text_notify_start_camera);
        } else {

            // 获取一个视频对象
            VideoItem videoItem = (VideoItem) getItem(position);

            //TODO 这个地方需要你用自己写的那个ＩｍａｇｅＦｅｔｃｈ来处理。生存视频缩略图的时候进行二级缓存
            ImageFetchManager.getInstance(mContext)
                    .reSize(DensityUtils.dp2px(mContext, 75), DensityUtils.dp2px(mContext, 75))
                    .loadBitmapIntoImageView(videoItem.getPath(), holder.videoThumbnail);


            holder.videoSize.setText(TextFormater.getDataSize(videoItem.getVideoSize()));
            holder.videoDuration.setText(DateTimeUtils.formatTime(Long.parseLong(videoItem.getVideoDuration())));
        }


        return convertView;
    }

    private static class ViewHolder {

        /**
         * 视频缩略图
         */
        public ImageView videoThumbnail;

        /**
         * 视频大小
         */
        public TextView videoSize;

        /**
         * 视频时长
         */
        public TextView videoDuration;

    }
}
