package com.wl.pluto.plutochat.chat.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by jeck on 15-10-29.
 */
public class ImageCacheUtils {

    private static final String TAG = "--ImageCacheUtils-->";

    /**
     * 视力对象
     */
    private static ImageCacheUtils instance;

    /**
     * 图片缓存技术的核心类,在程序内存达到设定值时,会将最近最少使用的图片从缓存中移除
     */
    private LruCache<String, Bitmap> mMemoryCache;

    private static Context mContext;

    /**
     * 单例模式的构造函数
     */
    private ImageCacheUtils(Context context) {

        // 获取应用程序的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.d(TAG, "" + maxMemory);

        // 设置图片缓存为最大可用内存的1/8
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        mContext = context;
    }

    public static ImageCacheUtils getInstance(Context context) {

        if (instance == null) {

            instance = new ImageCacheUtils(context);
        }

        return instance;
    }

    /**
     * 将图片存入缓存中
     *
     * @param key
     * @param bitmap
     */

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {

        if (getBitmapFromCache(key) == null) {

            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中取出一张图片, 如果不存在就返回null
     *
     * @param key
     * @return
     */

    private Bitmap getBitmapFromCache(String key) {
        return mMemoryCache.get(key);
    }

    private int calculateInSimpleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {

        // 图片的原始高度和宽度
        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSmaleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfWidth = width / 2;

            final int halfHeight = height / 2;

            while (((halfWidth / inSmaleSize) > reqWidth)
                    && ((halfHeight / inSmaleSize) > reqHeight)) {
                inSmaleSize *= 2;
            }
        }

        return inSmaleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                   int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);

        // 计算 inSampleSize
        options.inSampleSize = calculateInSimpleSize(options, reqWidth,
                reqHeight);

        // 解析资源中的图片
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {

            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];

            // 返回的是100X100的图片,这个地方需要我们根据自己的图片要求,自定义加载
            return decodeSampledBitmapFromResource(mContext.getResources(),
                    data, 100, 100);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (imageViewReference != null && bitmap != null) {

                // 将解析好的图片先保存在缓存中
                addBitmapToMemoryCache(String.valueOf(data), bitmap);

                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * 为控件加载图片
     *
     * @param resId
     * @param imageView
     */
    public void loadBitmap(int resId, ImageView imageView) {

        Log.d(TAG, "" + resId);

        // 先从缓存中去取图片
        Bitmap bitmap = getBitmapFromCache(String.valueOf(resId));

        // 如果缓存中没有该图片,那就解析
        if (bitmap == null) {

            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        } else {

            // 如果有,就直接为ImageView加载图片
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 根据视频路径获取一张视频缩略图
     *
     * @author jeck
     */
    class BitmapWorkerTask2 extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        private String data = null;

        public BitmapWorkerTask2(ImageView imageView) {

            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];

            // 返回的是视频的缩略图,
            return createVideoThumbnail(data);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (imageViewReference != null && bitmap != null) {

                // 将解析好的图片先保存在缓存中
                addBitmapToMemoryCache(data, bitmap);

                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * 根据视频路径加载一张视频缩略图
     *
     * @param videoPath
     * @param imageView
     */
    public void loadBitmap(String videoPath, ImageView imageView) {

        // 先从缓存中去取图片
        Bitmap bitmap = getBitmapFromCache(videoPath);

        // 如果缓存中没有该图片,那就解析
        if (bitmap == null) {

            // 从视频路径抓一张缩略图出来
            new BitmapWorkerTask2(imageView).execute(videoPath);
        } else {

            // 如果有,就直接为ImageView加载图片
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 创建视频缩略图
     */
    private Bitmap createVideoThumbnail(String videoPath) {

        if (!TextUtils.isEmpty(videoPath)) {

            Bitmap bitmap = null;

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            try {

                retriever.setDataSource(videoPath);

                bitmap = retriever.getFrameAtTime();

                if (bitmap != null) {

                    // 获取视频缩略图之后,再调用系统提供的压缩工具将我们的图片压缩到我们想要的大小
                    return ThumbnailUtils.extractThumbnail(bitmap, 200, 100);
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

        return null;
    }
}
