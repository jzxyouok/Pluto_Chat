package com.wl.pluto.plutochat.chat.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pluto on 16-1-11.
 */
public class ProgressEntity implements Parcelable {

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


    protected ProgressEntity(Parcel in) {
    }

    public static final Creator<ProgressEntity> CREATOR = new Creator<ProgressEntity>() {
        @Override
        public ProgressEntity createFromParcel(Parcel in) {
            return new ProgressEntity(in);
        }

        @Override
        public ProgressEntity[] newArray(int size) {
            return new ProgressEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        return "ProgressEntity{" +
                "currentBytes=" + currentBytes +
                ", contentLength=" + contentLength +
                ", isDone=" + isDone +
                '}';
    }
}
