package com.wl.pluto.plutochat.chat.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.adapter.MusicListAdapter;
import com.wl.pluto.plutochat.chat.entity.AudioItem;
import com.wl.pluto.plutochat.chat.provider.AudioProvider;

/**
 * @author jeck
 */
public class AudioSelectedActivity extends Activity {

    /**
     * 加载音乐链表
     */
    private Button mLoadMusicButton;

    /**
     * 音乐列表
     */
    private ListView mMusicListView;

    /**
     * 音乐列表适配器
     */
    private MusicListAdapter mMusicListAdapter;

    /**
     * 音频文件实体对象
     */
    private AudioItem audioItem;

    public static final String TAG = "--AudioSelectedActivity-->";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_selected);

        // 初始化布局
        initlayout();
    }

    private void initlayout() {

        // 加载音乐链表
        mLoadMusicButton = (Button) findViewById(R.id.btn_load_music);

        // 响应点击事件
        mLoadMusicButton.setOnClickListener(listener);

        // 音乐链表
        mMusicListView = (ListView) findViewById(R.id.lv_audio_selected_list);

        mMusicListView.setOnItemClickListener(itemClickListener);

        // 音乐链表适配器
        mMusicListAdapter = new MusicListAdapter(this);

        mMusicListView.setAdapter(mMusicListAdapter);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            // 启动异步线程，加载本地音乐
            new LoadMusicTask().execute("");
        }
    };

    /**
     * 链表响应点击事件
     */
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // 这种方式可以获取与listView相关联的适配器
            MusicListAdapter adapter = (MusicListAdapter) parent.getAdapter();

            // 获取关联适配器之后，就可以调用适配器的方法了
            audioItem = (AudioItem) adapter.getItem(position);

            Log.d(TAG, audioItem.toString());

            // 将对应的项目的信息通过intent传递给目标对象
            startMusicPlayActivity(audioItem.getPath());
        }
    };

    private void startMusicPlayActivity(String musicPath) {

        Intent intent = new Intent(this, MusicPlayActivity.class);

        intent.putExtra(MusicPlayActivity.CURRENT_MUSIC_PATH, musicPath);

        startActivity(intent);
    }

    private class LoadMusicTask extends
            AsyncTask<String, String, ArrayList<AudioItem>> {

        private AudioProvider audioProvider;

        @Override
        // 开始执行前的操作
        protected void onPreExecute() {

            super.onPreExecute();

            audioProvider = new AudioProvider(AudioSelectedActivity.this);
        }

        @Override
        // 开始异步操作
        protected ArrayList<AudioItem> doInBackground(String... params) {
            return audioProvider.getAudioSet();
        }

        @Override
        // 异步操作完成之后的事情
        protected void onPostExecute(ArrayList<AudioItem> result) {
            super.onPostExecute(result);

            if (result != null && result.size() != 0) {

                mMusicListAdapter.setAudioList(result);
                mMusicListAdapter.notifyDataSetChanged();
            }
        }

    }
}
