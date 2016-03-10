package com.wl.pluto.plutochat.base;

import android.content.Context;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseTaskPool {

    /**
     * java 内置的线程池之一,共有四种
     */
    private static ExecutorService taskPool;

    /**
     * 在进行网络链接的时候需要context
     */
    private Context context;

    public BaseTaskPool(BaseActivity activity) {

        context = activity.getContext();
        taskPool = Executors.newCachedThreadPool();
    }

    /**
     * 添加自定义任务
     */
    public void addTask(int taskId, BaseTask baseTask, int delayTime) {

        // 设置任务id, 这个地方为什么不在构造BaseTask的时候设置id呢?
        baseTask.setId(taskId);
        try {

            // 执行任务
            taskPool.execute(new TaskThread(context, null, null, baseTask,
                    delayTime));

        } catch (Exception e) {
            e.printStackTrace();
            taskPool.shutdown();
        }
    }

    /**
     * 添加带参数请求的http任务
     */
    public void addTask(int taskId, String httpUrl,
                        HashMap<String, String> taskArgs, BaseTask baseTask, int delayTime) {

        baseTask.setId(taskId);
        try {

            // 执行任务
            taskPool.execute(new TaskThread(context, httpUrl, taskArgs,
                    baseTask, delayTime));
        } catch (Exception e) {
            e.printStackTrace();
            taskPool.shutdown();
        }
    }

    /**
     * 添加不带参数的http任务
     */
    public void addTask(int taskId, String httpUrl, BaseTask baseTask,
                        int delayTime) {

        baseTask.setId(taskId);

        try {

            taskPool.execute(new TaskThread(context, httpUrl, null, baseTask,
                    delayTime));
        } catch (Exception e) {
            e.printStackTrace();
            taskPool.shutdown();
        }
    }

    /**
     * 任务线程
     */
    private class TaskThread implements Runnable {

        private Context context;
        private String httpUrl;
        private HashMap<String, String> taskArgs;
        private BaseTask baseTask;
        private int delayTime;

        public TaskThread(Context context, String httpUrl,
                          HashMap<String, String> taskArgs, BaseTask baseTask,
                          int delayTime) {

            this.context = context;
            this.httpUrl = httpUrl;
            this.taskArgs = taskArgs;
            this.baseTask = baseTask;
            this.delayTime = delayTime;
        }

        @Override
        public void run() {
            try {

                // 开始执行
                baseTask.onTaskStart();

                String httpResult = null;

                if (this.delayTime > 0) {

                    Thread.sleep(this.delayTime);
                }

                try {

                    if (this.httpUrl != null) {

                        // 初始化网络操作

                        if (taskArgs == null) {

                            // 使用get方式请求网络
                            httpResult = "";
                        } else {

                            httpResult = "";
                            // 使用post方式请求网络
                        }

                    }

                    // 网络请求结果
                    if (httpResult == null) {

                        baseTask.onTaskComplete();
                    } else {

                        //
                        baseTask.onTaskComplete(httpResult);
                    }

                } catch (Exception e) {

                    baseTask.onTaskError(e.getMessage());
                }

            } catch (Exception e) {

                e.printStackTrace();
            } finally {

                try {

                    //
                    baseTask.onTaskStop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
