package com.wl.pluto.plutochat.chat.manager;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.wl.pluto.plutochat.chat.cache.ImageCache;
import com.wl.pluto.plutochat.chat.model.LoadBitmapByVideoPathTask;

/**
 * Created by pluto on 15-12-22.
 */
public class ImageFetchManager {

    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize = 200;

    private LoadBitmapByVideoPathTask mImageFetcher;

    private static ImageFetchManager instance;

    private Context context;

    private ImageFetchManager(Context context) {
        this.context = context;
        initImageFetcher();
    }

    public static ImageFetchManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageFetchManager(context);
        }

        return instance;
    }

    private void initImageFetcher() {

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(context, IMAGE_CACHE_DIR);

        // Set memory cache to 25% of app memory
        cacheParams.setMemCacheSizePercent(0.25f);

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new LoadBitmapByVideoPathTask(context);
        mImageFetcher.addImageCache(((Activity) context).getFragmentManager(), cacheParams);
    }

    /**
     * 构造器模式
     */
    public ImageFetchManager reSize(int reqWidth, int reqHeight) {

        mImageFetcher.setImageSize(reqWidth, reqHeight);
        return instance;
    }


    /**
     * 为ImageView加载图像
     *
     * @param data      可以是本地的图片路径，也可以是网络地址
     * @param imageView 目标ImageView
     */
    public void loadBitmapIntoImageView(Object data, ImageView imageView) {
        mImageFetcher.loadImage(data, imageView);
    }

    public void onImageResume() {
        mImageFetcher.setExitTasksEarly(false);
    }

    public void onImagePause() {
        mImageFetcher.setPauseWork(true);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    public void onImageDestroy() {
        mImageFetcher.clearCache();
    }
}
