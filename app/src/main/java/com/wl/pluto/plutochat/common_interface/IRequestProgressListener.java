package com.wl.pluto.plutochat.common_interface;

/**
 * 上传进度的回调接口
 * <p>
 * Created by pluto on 16-1-11.
 */
public interface IRequestProgressListener {

    /**
     * 上传的回调
     *
     * @param totalByteWrite
     * @param contentLength
     * @param done
     */
    void onRequestProgress(long totalByteWrite, long contentLength, boolean done);
}
