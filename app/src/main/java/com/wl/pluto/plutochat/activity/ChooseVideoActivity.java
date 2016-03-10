package com.wl.pluto.plutochat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.adapter.ChooseVideoAdapter;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.constant.CommonConstant;
import com.wl.pluto.plutochat.entity.VideoItem;
import com.wl.pluto.plutochat.provider.VideoProvider;

import java.util.ArrayList;

public class ChooseVideoActivity extends BaseActivity {

    private static final String TAG = "--ChooseVideoActivity-->";

    /**
     * 视频链表
     */
    private GridView mVideoGridView;

    /**
     * 视频链表适配器
     */
    private ChooseVideoAdapter mVideoGridViewAdapter;

    /**
     * 异步加载视频对象
     */
    private LoadVideoDataTask mLoadVideoDataTask;

    /**
     * 最大支持１０Ｍ的视频传输
     */
    private static final int MAX_SUPPORT_VIDEO_SIZE = 1024 * 1024 * 10;

    /**
     * 启动系统相机进行视频拍摄
     */
    private static final int REQUEST_CODE_VIDEO_CAPTURE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_video_layout);

        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ImageFetchManager.getInstance(this).onImageResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ImageFetchManager.getInstance(this).onImagePause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ImageFetchManager.getInstance(this).onImageDestroy();
    }

    /**
     * 初始化布局
     */
    private void initLayout() {

        mVideoGridView = (GridView) findViewById(R.id.gv_choose_video_grid);

        mVideoGridViewAdapter = new ChooseVideoAdapter(this);

        mVideoGridView.setAdapter(mVideoGridViewAdapter);

        mVideoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    startCameraActivity();
                } else {

                    VideoItem item = (VideoItem) mVideoGridViewAdapter.getItem(position);

                    String videoPath = item.getPath();
                    String duration = item.getVideoDuration();
                    long videoSize = item.getVideoSize();

                    Log.i(TAG, "videoSize = " + videoSize);
                    if (videoSize > MAX_SUPPORT_VIDEO_SIZE) {
                        toast(R.string.text_notify_support_video_size);
                    } else {

                        Intent intent = new Intent();
                        intent.putExtra(CommonConstant.VIDEO_FILE_PATH_KEY, videoPath);
                        intent.putExtra(CommonConstant.VIDEO_DURATION_KEY, Long.parseLong(duration));

                        ChooseVideoActivity.this.setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

        loadVideoData();
    }

    /**
     * 启动相机进行视频拍摄
     */
    private void startCameraActivity() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadVideoData() {

        mLoadVideoDataTask = new LoadVideoDataTask();
        mLoadVideoDataTask.execute();
    }

    private class LoadVideoDataTask extends AsyncTask<Void, Void, ArrayList<VideoItem>> {

        /**
         * 视频链表提供器
         */
        private VideoProvider videoProvider;

        public LoadVideoDataTask() {

            // 异步线程开始执行前的准备工作工作
            videoProvider = new VideoProvider(ChooseVideoActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<VideoItem> doInBackground(Void... params) {

            // 异步线程执行中
            return videoProvider.getVideoList();
        }

        @Override
        // 异步线程执行完之后的操作
        protected void onPostExecute(ArrayList<VideoItem> result) {

            if (result != null) {

                // 为适配器添加数据源
                mVideoGridViewAdapter.setVideoList(result);

                // 通知适配器数据发生变化，这个一定要调用，不然数据就显示不出来了
                mVideoGridViewAdapter.notifyDataSetChanged();
            } else {
                Log.i(TAG, "获取视频数据失败");
            }
        }
    }
}
