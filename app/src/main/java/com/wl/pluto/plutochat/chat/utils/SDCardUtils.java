package com.wl.pluto.plutochat.chat.utils;

import android.os.Environment;
import android.os.StatFs;

import com.wl.pluto.plutochat.chat.constant.UserConstant;

import java.io.File;

/**
 * SDCard 辅助类
 * Created by jeck on 15-10-29.
 */
public class SDCardUtils {

    /**
     * 这是本应用程序的最基本的文件夹，所有和本应用程序有关的地址，都在这个文件夹下面
     */
    private static final String BASE_PATH = "PlutoChat/";

    /**
     * 头像保存的路径
     */
    private static final String HEAD_IMAGE_PATH = BASE_PATH + "head_image/";

    /**
     * 保存涂鸦图片的路径
     */
    private static final String DOODLE_FILE_PATH = BASE_PATH + "doodle/";

    /**
     * 下载地址
     */
    private static final String DOWNLOAD_PATH = BASE_PATH + "download/";

    /**
     * 没有捕获的崩溃路径
     */
    private static final String CRASH_PATH = BASE_PATH + "crash/";

    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {

        if (isSDCardEnable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator;
        } else {
            return "/sdcard/";
        }
    }

    public static String getSDCard() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 这是头像保存的路径，也是唯一的
     *
     * @return
     */
    public static String getUserHeadImagePath() {
        return getSDCardPath() + HEAD_IMAGE_PATH;
    }

    /**
     * 获取头像的绝对路径，包含文件名
     *
     * @return
     */
    public static String getUserHeadImageAbsolutePath() {

        return getUserHeadImagePath() + UserConstant.MY_HEAD_IMAGE_NAME;
    }

    /**
     * 获取涂鸦图片保存的路径
     *
     * @return
     */
    public static File getScreenshotFolder() {
        File path = new File(Environment.getExternalStorageDirectory(), DOODLE_FILE_PATH);
        path.mkdirs();
        return path;
    }

    /**
     * 涂鸦的保存地址
     *
     * @return
     */
    public static String getScreenShotPath() {
        return getSDCardPath() + DOODLE_FILE_PATH;
    }

    /**
     * 地图截屏保存地址
     *
     * @param name
     * @return
     */
    public static String getMapShotName(String name) {
        return getScreenShotPath() + name + ".png";
    }

    /**
     * 获取下载地址
     *
     * @return
     */
    public static String getDownloadPath() {
        return getSDCardPath() + DOWNLOAD_PATH;
    }

    /**
     * 获取崩溃路径
     *
     * @return
     */
    public static String getCrashPath() {
        return getSDCardPath() + CRASH_PATH;
    }
}
