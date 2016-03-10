package com.wl.pluto.plutochat.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.wl.pluto.plutochat.entity.VideoItem;

import java.util.ArrayList;

public class VideoProviderWithThumbnail {

    private static final String TAG = "--VideoProviderWithThumbnail-->";
    /**
     * 想从MediaStore.View.Thumbnails查询中获得的列的列表
     */
    private String[] thumbnailColumns = {MediaStore.Video.Thumbnails.VIDEO_ID,
            MediaStore.Video.Thumbnails.DATA};

    /**
     * 想从MediaStore.View.Media查询中获得的列的列表
     */
    private String[] mediaColumns = {MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE};

    /**
     * 上下文对象
     */
    private Context mContext;

    public VideoProviderWithThumbnail(Context context) {

        this.mContext = context;
    }

    /**
     * 返回带视频缩略图地址的视频链表
     *
     * @return
     */
    public ArrayList<VideoItem> getVideoListWithThumbnail() {

        ArrayList<VideoItem> videoList = new ArrayList<VideoItem>();

        if (mContext != null) {

            Cursor cursor = mContext.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns,
                    null, null, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    VideoItem video = new VideoItem();

                    // 视频内置id
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                    video.setId(String.valueOf(id));

                    // 视频相关联的缩略图集合, 每一次都是精确匹配缩略图
                    Cursor thumbnailCursor = mContext
                            .getContentResolver()
                            .query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                    thumbnailColumns,
                                    MediaStore.Video.Thumbnails.VIDEO_ID + "="
                                            + id, null, null);

                    if (thumbnailCursor.moveToFirst()) {

                        String VideoThumbnailPath = thumbnailCursor.getString(
                                thumbnailCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                        // 设置视频的缩略图路径
                        video.setVideoThumbnailPath(VideoThumbnailPath);

                        Log.d(TAG, VideoThumbnailPath);
                    }

                    // 视频路径
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    video.setPath(path);

                    // 视频标题
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    video.setTitle(title);

                    // 视频类型
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    video.setMimeType(mimeType);

                    videoList.add(video);
                }
            }
        }
        return videoList;
    }
}
