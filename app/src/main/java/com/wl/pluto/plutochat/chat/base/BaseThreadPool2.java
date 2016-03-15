package com.wl.pluto.plutochat.chat.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的线程池
 * <p/>
 * Created by pluto on 16-1-17.
 */
public class BaseThreadPool2 {

    /**
     * 任务开始，在Handler 的onHandlerMessage中对任务进行控制
     */
    public static final int TASK_START = 1001;
    public static final int TASK_PROGRESS = 1002;
    public static final int TASK_COMPLETE = 1003;
    public static final int TASK_FAILED = 1004;

    /**
     * Sets the amount of time an idle thread will wait for a task before terminating
     * 当一个空闲的线程被回收之前的等待时间
     */
    private static final int KEEP_ALIVE_TIME = 1;

    /**
     * Sets the Time Unit to seconds
     * 设置时间单位为秒
     */
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    /**
     * cup的　核数
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * Sets the initial thread pool size to
     * 设置线程池的核心线程数(固定员工)，这个线程是不可以被回收的。就算没事做，也需要养着
     */
    private static final int CORE_POOL_SIZE = NUMBER_OF_CORES + 1;

    /**
     * Sets the maximum thread pool size to
     * 设置线程池的最大线程数，这个是临时员工，如果没有活干，空闲１s就会被回收
     */
    private static final int MAX_POOL_SIZE = 2 * NUMBER_OF_CORES + 1;

    /**
     * 线程池的任务队列
     */
    private final BlockingQueue<Runnable> mBlockQueue;

    /**
     * 线程池的管理者
     */
    private final ThreadPoolExecutor mThreadPoolExecutor;

    /**
     * 处理与UI线程通信的handler
     */
    private Handler mHandler;

    /**
     * 使用单例模式
     */
    private static BaseThreadPool2 instance;

    /**
     * 任务栈
     */
    private final Queue<BaseTaskWork> mBaseTaskWorkQueue;

    static {

        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        instance = new BaseThreadPool2();
    }

    private BaseThreadPool2() {

        //初始化任务队列
        mBlockQueue = new LinkedBlockingQueue<>();

        //初始化线程池对象
        mThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, mBlockQueue);

        //初始化
        mBaseTaskWorkQueue = new LinkedBlockingQueue<>();

        //在这里，就可以处理你的UI业务逻辑了。你可以根据具体的UI业务逻辑，在这个地方操作UI控件，实现子线程与UI线程的交换
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_START:
                        break;
                    case TASK_PROGRESS:
                        break;
                    case TASK_COMPLETE:
                        break;
                    case TASK_FAILED:
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };
    }

    /**
     * 启动一个新任务，这是给外界调用的接口
     */
    public void startTask(BaseRunnable runnable) {

        //从任务栈中取一个任务出来，　如果当前的任务栈为空，返回null
        BaseTaskWork taskWork = mBaseTaskWorkQueue.poll();

        if (taskWork == null) {

            //如果当前任务栈为空，那就创建一个新任务
            taskWork = new BaseTaskWork(runnable);
        }

        //初始化新任务
        taskWork.initTaskWork();

        //如果没有完成，那就开始执行这个任务
        if (!taskWork.isComplete()) {
            mThreadPoolExecutor.execute(taskWork.getBaseRunnable());
        } else {
            handleTaskState(taskWork, TASK_COMPLETE);
        }
    }

    private void handleTaskState(BaseTaskWork taskWork, int state) {

        switch (state) {
            case TASK_START:
                break;
            case TASK_COMPLETE:
                break;
            case TASK_FAILED:
                break;
            default:
        }
    }

    public void cancellAllTask() {

    }

    public void removeTaskById(int taskId) {

    }
}
