package com.wl.pluto.plutochat.chat.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.entity.AudioItem;

public class MusicListAdapter extends BaseAdapter {

    public static final String TAG = "--MusicListAdapter-->";
    /**
     * 音乐链表
     */
    private ArrayList<AudioItem> audioList;

    private Context mContext;

    private AudioItem audio;

    public MusicListAdapter(Context context) {
        mContext = context;
    }

    public ArrayList<AudioItem> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<AudioItem> audioList) {
        this.audioList = audioList;
    }

    @Override
    public int getCount() {

        if (audioList != null && audioList.size() != 0) {
            return audioList.size();
        } else {

            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return audioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_audio_list_item_layout, null);
        }

        audio = (AudioItem) getItem(position);

        TextView musicName = (TextView) convertView
                .findViewById(R.id.tv_music_name);
        TextView musicTitle = (TextView) convertView
                .findViewById(R.id.tv_music_title);
        TextView musicArtist = (TextView) convertView
                .findViewById(R.id.tv_music_artist);

        if (audio != null) {

            musicName.setText(audio.getName());
            musicTitle.setText(audio.getTitle());
            musicArtist.setText(audio.getArtist());
        }

        return convertView;
    }

}
