package com.wl.pluto.plutochat.model;

import android.util.Log;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.common_interface.IUpload;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.utils.SDCardUtils;

import java.io.FileNotFoundException;

/**
 * Created by jeck on 15-12-2.
 */
public class UploadImpl implements IUpload {

    private static final String TAG = "--UploadImpl-->";

    private static final String USER_HEAD_IMAGE = "oss_head_image_path/";

    /**
     * 阿里云Service
     */
    private OSSService ossService;

    /**
     * bucket
     */
    private OSSBucket bucket;

    public UploadImpl() {
        ossService = BaseApplication.getOssService();
        bucket = ossService.getOssBucket(BaseApplication.getOSSBucket());
    }

    @Override
    public void upload(String filePath) {

        if (ossService != null) {

            //上传到我的oss下面的的user_head_image文件夹下面。太给力了
            OSSFile uploadFile = ossService.getOssFile(bucket, USER_HEAD_IMAGE + UserConstant.MY_HEAD_IMAGE_NAME);
            try {
                uploadFile.setUploadFilePath(filePath, "image/jpeg");
                uploadFile.uploadInBackground(new SaveCallback() {

                    @Override
                    public void onSuccess(String objectKey) {
                        Log.d(TAG, "[onSuccess] - " + objectKey + " upload success!");
                    }

                    @Override
                    public void onProgress(String objectKey, int byteCount, int totalSize) {
                        Log.d(TAG, "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
                    }

                    @Override
                    public void onFailure(String objectKey, OSSException ossException) {
                        Log.e(TAG, "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
                        ossException.printStackTrace();
                        ossException.getException().printStackTrace();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "ossService == null");
        }

    }
}
