package com.wl.pluto.plutochat.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.manager.ThreadPoolManager;


public class TestNdkActivity extends BaseActivity {

    private static final String TAG = "--TestNdkActivity-->";

    private Button mTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ndk);

        mTestButton = (Button) findViewById(R.id.btn_test_thread_pool);
        mTestButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_test_thread_pool:
                    onTestButtonClick();
                    break;
            }
        }
    };

    private void onTestButtonClick() {
        //ThreadPoolManager.getInstance().addAsyncTask(new DownloadAsyncTask());
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 10; i++) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "i = " + i);
                }
            }
        });
        //new DownloadAsyncTask().execute();
    }

    private class DownloadAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {

            for (int i = 0; i < 20; i++) {

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }
            return "success";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(TAG, "i = " + values[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            if (!TextUtils.isEmpty(s)) {
                Log.i(TAG, "result = " + s);
            }
        }
    }
}
