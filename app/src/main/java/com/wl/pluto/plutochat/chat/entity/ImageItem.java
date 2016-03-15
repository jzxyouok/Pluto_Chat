package com.wl.pluto.plutochat.chat.entity;

import android.graphics.Bitmap;

/**
 * 图片文件的实体类
 */
public class ImageItem {

    /**
     * id
     */
    private String id;

    /**
     * 图像名称
     */
    private String name;

    /**
     * 图像标题
     */
    private String title;

    /**
     * 图像描述
     */
    private String description;

    /**
     * 图像生成日期
     */
    private String Date;

    /**
     * 图像存储路径
     */
    private String path;

    /**
     * 图像缩略图
     */
    private Bitmap bitmapThumbnail;

    public ImageItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmapThumbnail() {
        return bitmapThumbnail;
    }

    public void setBitmapThumbnail(Bitmap bitmapThumbnail) {
        this.bitmapThumbnail = bitmapThumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", Date='" + Date + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
