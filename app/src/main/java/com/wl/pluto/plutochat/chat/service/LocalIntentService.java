package com.wl.pluto.plutochat.chat.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


/**
 * Created by pluto on 16-1-5.
 */
public class LocalIntentService extends IntentService {

    private static final String TAG = "--LocalIntentService-->";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LocalIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getStringExtra("task_action");
        Log.i(TAG, "receive task : " + action);

        SystemClock.sleep(3000);

        if ("task2".equals(action)) {
            Log.i(TAG, "handle task : " + action);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
        super.onDestroy();
    }
}
