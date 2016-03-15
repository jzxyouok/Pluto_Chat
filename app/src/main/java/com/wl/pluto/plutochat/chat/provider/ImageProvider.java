package com.wl.pluto.plutochat.chat.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Images.Media;

import com.wl.pluto.plutochat.chat.entity.ImageItem;

import java.util.ArrayList;


public class ImageProvider {

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 字段名称
     */
    private static final String[] colums = {Media.DISPLAY_NAME, Media.TITLE,
            Media._ID, Media.DATA, Media.DATE_ADDED};

    /**
     * 视频缩略图的默认宽度
     */
    private static final int IMAGE_DEFAULT_WIDTH = 200;

    /**
     * 视频缩略图的默认高度
     */
    private static final int IMAGE_DEFAULT_HEIGHT = 200;

    public ImageProvider(Context context) {
        this.mContext = context;
    }

    /**
     * 获取本地文件中的图像链表
     */
    public ArrayList<ImageItem> getImageList() {

        ArrayList<ImageItem> imageList = new ArrayList<ImageItem>();

        if (mContext != null) {

            Cursor cursor = mContext.getContentResolver().query(
                    Media.EXTERNAL_CONTENT_URI, colums, null, null, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    // 创建图像对象
                    ImageItem image = new ImageItem();

                    // id
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(Media._ID));
                    image.setId(String.valueOf(id));

                    // name 名称
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.DISPLAY_NAME));
                    image.setName(name);

                    // title 标题
                    String title = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.TITLE));
                    image.setTitle(title);

                    // path 存储路径
                    String path = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.DATA));
                    image.setPath(path);

                    // thumbnail 缩略图
                    Bitmap bm = getImageThumbnail(path);
                    image.setBitmapThumbnail(bm);

                    imageList.add(image);
                }
            }

        }

        return imageList;
    }

    /**
     * 按照给定的比率获取图像缩略图
     */
    private Bitmap getImageThumbnail(String imagePath) {

        // 加载的是图像的宽高，不是图像本身
        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();

        //
        bitmapFactoryOptions.inJustDecodeBounds = true;

        // 先加载图像的信息
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactoryOptions);

        // 计算图像相对于我们给的默认宽度的比率
        int widthRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth
                / (float) IMAGE_DEFAULT_WIDTH);

        // 计算图像相对于我们给的默认高度的比率
        int heightRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight
                / (float) IMAGE_DEFAULT_HEIGHT);

        // 如果都大于1.说明比我们默认宽高要大
        if (widthRatio > 1 && heightRatio > 1) {

            // 如果宽比高大
            if (widthRatio > heightRatio) {

                // 那我们的图片将根据宽度进行缩放
                bitmapFactoryOptions.inSampleSize = widthRatio;
            } else {
                bitmapFactoryOptions.inSampleSize = heightRatio;
            }

        }

        //
        bitmapFactoryOptions.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactoryOptions);

        return bitmap;
    }
}
