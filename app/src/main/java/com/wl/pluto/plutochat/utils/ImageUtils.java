package com.wl.pluto.plutochat.utils;

import android.graphics.BitmapFactory;

import com.easemob.util.PathUtil;

/**
 * Created by pluto on 15-12-22.
 */
public class ImageUtils {

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(
                thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th"
                + thumbImageName;

        return path;
    }


    public static String getImagePath(String remoteUrl) {
        String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1,
                remoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        return path;

    }

    /**
     * 判断照片的方向
     *
     * @param imagePath
     * @return
     */
    public static boolean isImageHorizontal(String imagePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        //最关键在此，把options.inJustDecodeBounds = true;
        // 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        if (options.outWidth > options.outHeight) {
            return true;
        } else {
            return false;
        }
    }
}
