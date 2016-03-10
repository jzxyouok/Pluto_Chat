package com.wl.pluto.plutochat.entity;

import android.graphics.Bitmap;

/**
 * 视频文件的实体类
 */
public class VideoItem {

    /**
     * 视频id
     */
    private String id;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频描述
     */
    private String description;

    /**
     * 视频缩略图地址
     */
    private String thumbnailUrl;

    /**
     * 视频链接地址
     */
    private String videoUrl;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 视频时长
     */
    private String videoDuration;

    /**
     * 视频大小 （字节）
     */
    private long videoSize;

    /**
     * 视频路径
     */
    private String path;

    /**
     * 视频缩略图
     */
    private Bitmap videoThumbnail;

    /**
     * 视频类型
     */
    private String mimeType;

    /**
     * 视频相关缩略图的路径
     */
    private String videoThumbnailPath;

    public VideoItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(Bitmap videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String toString() {

        return "[VideoItem: " + "id=" + id + ",   fileId= " + fileId
                + ",   title=" + title + ",   description=" + description
                + ",   thumbnailUrl=" + thumbnailUrl + ",   videoUrl="
                + videoUrl + ",   uptime=" + uploadTime + ",   videoDuration"
                + videoDuration + ",   videoSize=" + videoSize
                + ",  videoThumbnailPath= " + videoThumbnailPath + "]";
    }
}
