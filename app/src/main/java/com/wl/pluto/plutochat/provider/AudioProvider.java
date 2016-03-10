package com.wl.pluto.plutochat.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import com.wl.pluto.plutochat.entity.AudioItem;

import java.util.ArrayList;

public class AudioProvider {

    /**
     * 上下文
     */
    private Context mContext;

    private static final String[] columns = {Media.DATA, Media._ID,
            Media.TITLE, Media.DISPLAY_NAME, Media.MIME_TYPE, Media.ARTIST,
            Media.ALBUM, Media.IS_ALARM, Media.IS_MUSIC, Media.IS_NOTIFICATION,
            Media.IS_RINGTONE};

    public AudioProvider(Context context) {
        this.mContext = context;
    }

    public ArrayList<AudioItem> getAudioSet() {

        ArrayList<AudioItem> audioList = new ArrayList<AudioItem>();

        if (mContext != null) {

            Cursor cursor = mContext.getContentResolver().query(
                    Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    // 创建一个音频对象
                    AudioItem audio = new AudioItem();

                    // id
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(Media._ID));
                    audio.setId(String.valueOf(id));

                    // 显示名称
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.DISPLAY_NAME));
                    audio.setName(name);

                    // 标题
                    String title = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.TITLE));
                    audio.setTitle(title);

                    // 存储路径
                    String path = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.DATA));
                    audio.setPath(path);

                    // 艺术家
                    String artist = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.ARTIST));
                    audio.setArtist(artist);

                    // 唱片集
                    String album = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.ALBUM));
                    audio.setAlbum(album);

                    // 类型
                    String mime_type = cursor.getString(cursor
                            .getColumnIndexOrThrow(Media.MIME_TYPE));
                    audio.setMime_type(mime_type);

                    audioList.add(audio);
                }
            }
        }
        return audioList;
    }
}
