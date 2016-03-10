package com.wl.pluto.plutochat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.constant.URLConstant;
import com.wl.pluto.plutochat.manager.OKHttpManager;
import com.wl.pluto.plutochat.network.UIResponseProgress;

import java.io.IOException;

public class MyPostsActivity extends AppCompatActivity {

    private static final String TAG = "--MyPostsActivity-->";

    /**
     * 下载按钮
     */
    private Button mDownloadButton;

    /**
     * 下载进度条
     */
    private ProgressBar mDownloadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        initLayout();
    }

    private void initLayout() {

        mDownloadProgressBar = (ProgressBar) findViewById(R.id.pb_download_progress_bar);
        mDownloadButton = (Button) findViewById(R.id.btn_download_button);
        mDownloadButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_download_button:
                    onDownloadClick();
                    break;
            }
        }
    };

    private void onDownloadClick() {

        final UIResponseProgress uiResponseProgress = new UIResponseProgress() {

            @Override
            public void onUIResponseProgress(final long totalBytesRead, final long contentLength, boolean done) {

                Log.i(TAG, "totalBytesRead = " + totalBytesRead);
                Log.i(TAG, "contentLength = " + contentLength);

                if (mDownloadProgressBar != null) {
                    mDownloadProgressBar.setProgress((int) ((100 * totalBytesRead) / contentLength));
                }
            }
        };

        try {

            OKHttpManager.getInstance().doAsyncDownloadWithProgress(URLConstant.QQ_DOWNLOAD_URL, uiResponseProgress, new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {

                    Log.i(TAG, "failure = " + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    Log.i(TAG, "result = " + response.body().string());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
