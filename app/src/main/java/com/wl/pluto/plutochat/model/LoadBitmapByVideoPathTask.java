package com.wl.pluto.plutochat.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.Log;

import com.wl.pluto.plutochat.cache.ImageFetcher;
import com.wl.pluto.plutochat.cache.ImageResizer;
import com.wl.pluto.plutochat.cache.ImageWorker;

/**
 * Created by pluto on 15-12-31.
 */
public class LoadBitmapByVideoPathTask extends ImageWorker {


    private int mImageWidth = 0;
    private int mImageHeight = 0;

    public LoadBitmapByVideoPathTask(Context context) {
        super(context);
    }

    @Override
    protected Bitmap processBitmap(Object data) {
        return createVideoThumbnail(String.valueOf(data));
    }

    public void setImageSize(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }

    /**
     * 创建视频缩略图
     */
    private Bitmap createVideoThumbnail(String videoPath) {

        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(videoPath)) {


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            try {

                retriever.setDataSource(videoPath);

                bitmap = retriever.getFrameAtTime();


                if (bitmap != null) {

                    //系统提供的图片压缩工具
                    return ThumbnailUtils.extractThumbnail(bitmap, mImageWidth, mImageHeight);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {

                    retriever.release();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("------>", "视频路径为空");
        }

        return bitmap;
    }

}
