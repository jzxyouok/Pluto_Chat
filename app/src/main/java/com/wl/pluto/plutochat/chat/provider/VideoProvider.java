package com.wl.pluto.plutochat.chat.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.wl.pluto.plutochat.chat.entity.VideoItem;

import java.util.ArrayList;


/**
 * 扫描系统视频
 */
public class VideoProvider {

    private Context context;

    public VideoProvider(Context context) {

        this.context = context;
    }

    /**
     * 获取系统视频链表
     */
    public ArrayList<VideoItem> getVideoList() {

        ArrayList<VideoItem> videoList = new ArrayList<VideoItem>();

        if (context != null) {

            // 通过ContentProvider获取本地数据
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);

            if (cursor != null) {

                // 开始遍历cursor
                while (cursor.moveToNext()) {

                    VideoItem videoItem = new VideoItem();

                    // 获取视频ID
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    videoItem.setId(String.valueOf(id));

                    // 获取视频标题
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    videoItem.setTitle(title);

                    // 路径
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                    videoItem.setPath(path);

                    // 时长
                    long duration = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    videoItem.setVideoDuration(String.valueOf(duration));

                    // 大小
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    videoItem.setVideoSize(size);

                    // 添加到链表中
                    videoList.add(videoItem);

                }
            } else {

                Log.d("------->", "获取本地多媒体数据失败");
            }

        } else {
            Log.d("------>", "context 为空");
        }
        return videoList;
    }


}
