package com.wl.pluto.plutochat.chat.common_interface;

/**
 * 文件下载的进度监听，OKHttp给我们提供了一个回调，里面有Response返回结果，我们要想对结果进行监听，
 * 就需要做点什么。做什么呢，就是需要对ResponseBody这个类改造一下，让它能符合我的要求。
 * <p>
 * 这里的这个接口，主要用于在监听进度的时候，需要回调
 * <p>
 * Created by pluto on 16-1-11.
 */
public interface IResponseProgressListener {

    /**
     * ResponseBody 的进度回调接口，主要用于下载
     */
    void onResponseProgress(long readBytes, long contentLength, boolean done);
}
