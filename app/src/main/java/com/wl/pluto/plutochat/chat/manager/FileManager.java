package com.wl.pluto.plutochat.chat.manager;

import android.graphics.Bitmap;

import com.wl.pluto.plutochat.chat.utils.SDCardUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by pluto on 15-12-5.
 */
public class FileManager {

    private static final String TAG = "--FileManager-->";

    /**
     * 将bitmap 保持到本地的SDCard上
     *
     * @param bitmap
     * @param bitmapName
     * @return
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String bitmapName) throws IOException {

        File fileDir = new File(SDCardUtils.getUserHeadImagePath());

        //如果路径不存在，先创建路径
        if (!fileDir.exists()) {
            boolean isSuccess = fileDir.mkdirs();

            if (isSuccess) {

                File imageFile = new File(fileDir, bitmapName);

                //如果文件不存在，创建文件
                if (imageFile.exists()) {
                    imageFile.delete();
                }

                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream outputStream = null;
                try {

                    outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //返回Bitmap保存的绝对路径
                return imageFile.getAbsolutePath();

            } else {

                throw new IOException();
            }
        }

        return null;
    }
}
