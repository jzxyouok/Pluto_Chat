package com.wl.pluto.plutochat.chat.manager;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pluto on 16-1-5.
 */
public class ThreadPoolManager {

    private static ThreadPoolManager instance;

    private ExecutorService executorService;

    private ThreadPoolType mTreadPoolType;

    private ExecutorService mExecutor;

    private enum ThreadPoolType {
        FIXED_THREAD_POOL, CACHED_THREAD_POOL, SCHEDULED_THREAD_POOL, SINGLE_THREAD_POOL
    }

    private ThreadPoolManager(ThreadPoolType type) {

        mTreadPoolType = type;
        switch (mTreadPoolType) {

            case FIXED_THREAD_POOL:
                executorService = Executors.newFixedThreadPool(5);
                break;
            case CACHED_THREAD_POOL:
                executorService = Executors.newCachedThreadPool();
                break;
            case SCHEDULED_THREAD_POOL:

                //这个比较特殊
                executorService = Executors.newScheduledThreadPool(5);
                break;
            case SINGLE_THREAD_POOL:
                executorService = Executors.newSingleThreadExecutor();
                break;
        }
    }

    private ThreadPoolManager() {

        //全是临时工，用完就回收，这非常适合于各种new Thread(new Runnable()).start();这样的线程调用
        mExecutor = Executors.newCachedThreadPool();
    }

    public static ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }

        return instance;
    }

    public void execute(Runnable runnable) {

        switch (mTreadPoolType) {

            case FIXED_THREAD_POOL:

                executorService.execute(runnable);
                break;
            case CACHED_THREAD_POOL:

                executorService.execute(runnable);
                break;
            case SCHEDULED_THREAD_POOL:

                executorService.execute(runnable);
                break;
            case SINGLE_THREAD_POOL:

                executorService.execute(runnable);
                break;
        }
    }

    /**
     * 添加一个普通的Runnable任务
     *
     * @param runnable
     */
    public void addTask(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    /**
     * 添加一个串行之执行的异步任务
     *
     * @param task
     */
    public void addAsyncTask(AsyncTask task) {

        task.execute();
    }

    /**
     * 添加一个并行执行的异步任务
     *
     * @param task
     */
    public void addAsyncTask2(AsyncTask task) {

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }
}
