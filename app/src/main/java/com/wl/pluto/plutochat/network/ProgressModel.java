package com.wl.pluto.plutochat.network;

import java.io.Serializable;

/**
 * 我们的目的是能够在ＵＩ层进行回调，但是OKHttp是作为传输层的一个网络框架，运行的时候，也是子线程中
 *
 * 于是我们还要实现我们写的接口，进行UI操作的回调。由于涉及到消息机制，我们对之前的两个接口回调传的参数进行封装，封装为一个实体类便于传递
 * Created by pluto on 16-1-11.
 */
public class ProgressModel implements Serializable {

    /**
     * 当前读取或者写入的字节数
     */
    private long currentBytes;

    /**
     * 文件的总长度
     */
    private long contentLength;

    /**
     * 文件是否传输完成
     */
    private boolean isDone;

    public ProgressModel(long currentBytes, long length, boolean isDone) {
        this.currentBytes = currentBytes;
        this.contentLength = length;
        this.isDone = isDone;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "ProgressModel{" +
                "currentBytes=" + currentBytes +
                ", contentLength=" + contentLength +
                ", isDone=" + isDone +
                '}';
    }
}
