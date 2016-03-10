package com.wl.pluto.plutochat.base;

import android.util.Log;

public class BaseTask {

    private static final String TAG = "--BaseTask-->";

    /**
     * 任务完成
     */
    public static final int TASK_COMPLETE = 0;

    /**
     * 网络任务出错
     */
    public static final int TASK_NETWORK_ERROR = 1;

    /**
     * 任务id
     */
    private int id = 0;

    /**
     * 任务名称
     */
    private String name = "";

    public BaseTask() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void onTaskStart() {

    }

    public void onTaskComplete() {

    }

    public void onTaskComplete(String httpResult) {
        Log.d(TAG, httpResult);
    }

    public void onTaskError(String error) {

    }

    public void onTaskStop() throws Exception {

    }
}
