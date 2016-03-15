package com.wl.pluto.plutochat.chat.entity;

/**
 * 音频文件的实体类
 */
public class AudioItem {

    /**
     * 内部id
     */
    private String id;

    /**
     * 显示名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 存储路径
     */
    private String path;

    /**
     * mime类型
     */
    private String mime_type;

    /**
     * 艺术家
     */
    private String artist;

    /**
     * 唱片集
     */
    private String album;

    /**
     * 添加时间
     */
    private String data_added;

    /**
     * 修改时间
     */
    private String data_modified;

    /**
     * 是否是铃声
     */
    private boolean isRingTong;

    /**
     * 是否是警报
     */
    private boolean iaAlarm;

    /**
     * 是否是音乐
     */
    private boolean isMusic;

    /**
     * 是否是通知
     */
    private boolean isNofification;

    /**
     * 大小
     */
    private String size;

    public AudioItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getData_added() {
        return data_added;
    }

    public void setData_added(String data_added) {
        this.data_added = data_added;
    }

    public String getData_modified() {
        return data_modified;
    }

    public void setData_modified(String data_modified) {
        this.data_modified = data_modified;
    }

    public boolean isRingTong() {
        return isRingTong;
    }

    public void setRingTong(boolean isRingTong) {
        this.isRingTong = isRingTong;
    }

    public boolean isIaAlarm() {
        return iaAlarm;
    }

    public void setIaAlarm(boolean iaAlarm) {
        this.iaAlarm = iaAlarm;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }

    public boolean isNofification() {
        return isNofification;
    }

    public void setNofification(boolean isNofification) {
        this.isNofification = isNofification;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String toString() {
        return "AudioItem [id= " + "id" + "   name=" + name + "   tilte="
                + title + "   artitst=" + artist + "   album=" + album
                + "  path=" + path + "]";
    }
}
