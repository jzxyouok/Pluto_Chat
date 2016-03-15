package com.wl.pluto.plutochat.chat.base;

/**
 * Created by pluto on 16-1-17.
 */
public class BaseTaskWork {

    /**
     * 任务ID
     */
    private int taskId;

    /**
     * 任务名称
     */
    private int taskName;


    /**
     * 需要处理的任务
     */
    private BaseRunnable mRunnable;

    public BaseTaskWork(BaseRunnable runnable) {

        this.mRunnable = runnable;
    }

    /**
     * 初始化任务需要的一些前提工作
     */
    public void initTaskWork() {

    }

    public BaseRunnable getBaseRunnable() {
        if (mRunnable == null) {
            throw new RuntimeException("must implements BaseRunnable !");
        }
        return mRunnable;
    }

    /**
     * 任务是否完成
     *
     * @return
     */
    public boolean isComplete() {
        return false;
    }
}
