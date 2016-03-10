package com.wl.pluto.plutochat.base;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局唯一的线程池
 * Created by pluto on 16-1-6.
 */
public abstract class BaseThreadPool<Params, Progress, Result> {

    // messages of handler
    private static final int MESSAGE_POST_PROGRESS = 0x1;
    private static final InternalHandler sHandler = new InternalHandler();

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);


        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;


        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });

            if (mActive == null) {
                scheduleNext();
            }
        }


        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    /**
     * An {@link Executor} that executes tasks one at a time in serial
     * order.  This serialization is global to a particular process.
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
    private static final Executor PARALLEL_EXECUTOR = THREAD_POOL_EXECUTOR;

    /**
     * Tracks {@link BaseThreadPool}
     */
    public static class Tracker {
        private final LinkedList<BaseThreadPool<?, ?, ?>> mTasks = new LinkedList<BaseThreadPool<?, ?, ?>>();


        private void add(BaseThreadPool<?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.add(task);
            }
        }


        private void remove(BaseThreadPool<?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.remove(task);
            }
        }


        /**
         * Cancel all registered tasks.
         */
        public void cancellAll() {
            synchronized (mTasks) {
                for (BaseThreadPool<?, ?, ?> task : mTasks) {
                    task.cancel(true);
                }
                mTasks.clear();
            }
        }


        /**
         * Cancel all instances of the same class as {@code current} other than
         * {@code current} itself.
         */
        public void cancelOthers(BaseThreadPool<?, ?, ?> current) {
            final Class<?> clazz = current.getClass();
            synchronized (mTasks) {
                final ArrayList<BaseThreadPool<?, ?, ?>> toRemove = new ArrayList<BaseThreadPool<?, ?, ?>>();
                for (BaseThreadPool<?, ?, ?> task : mTasks) {
                    if ((task != current) && task.getClass().equals(clazz)) {
                        task.cancel(true);
                        toRemove.add(task);
                    }
                }
                for (BaseThreadPool<?, ?, ?> task : toRemove) {
                    mTasks.remove(task);
                }
            }
        }


        public int getTaskCount() {
            return mTasks.size();
        }


        public boolean containsTask(BaseThreadPool<?, ?, ?> task) {
            return mTasks.contains(task);
        }
    }


    private final Tracker mTracker;


    private static class InnerTask<Params2, Progress2, Result2> extends AsyncTask<Params2, Progress2, Result2> {
        private final BaseThreadPool<Params2, Progress2, Result2> mOwner;


        public InnerTask(BaseThreadPool<Params2, Progress2, Result2> owner) {
            mOwner = owner;
        }


        @Override
        protected Result2 doInBackground(Params2... params) {
            return mOwner.doInBackground(params);
        }


        @Override
        protected void onProgressUpdate(Progress2... values) {
            mOwner.onProgressUpdate(values);
        }

        @Override
        public void onCancelled(Result2 result) {
            mOwner.unregisterSelf();
            mOwner.onCancelled();
        }

        @Override
        public void onPostExecute(Result2 result) {
            mOwner.unregisterSelf();
            if (mOwner.mCancelled) {
                mOwner.onCancelled();
            } else {
                mOwner.onPostExecute(result);
            }
        }
    }


    private final InnerTask<Params, Progress, Result> mInnerTask;
    private volatile boolean mCancelled;


    /**
     * Construction with what create new instances can be canceled by Tracker.
     *
     * @param tracker can retrieve instance like : <p>
     *                BaseThreadPool.Tracke mTracke = new BaseThreadPool.Tracke();
     */
    public BaseThreadPool(Tracker tracker) {
        mTracker = tracker;
        if (mTracker != null) {
            mTracker.add(this);
        }
        mInnerTask = new InnerTask<Params, Progress, Result>(this);
    }

    /**
     * Construction with what create new instances cannot be canceled later.
     */
    public BaseThreadPool() {
        mTracker = null;
        mInnerTask = new InnerTask<Params, Progress, Result>(this);
    }

    /* package */
    final void unregisterSelf() {
        if (mTracker != null) {
            mTracker.remove(this);
        }
    }


    /**
     * @see AsyncTask#doInBackground
     */
    protected abstract Result doInBackground(Params... params);


    /**
     * @see AsyncTask#cancel(boolean)
     */
    public final void cancel(boolean mayInterruptIfRunning) {
        mCancelled = true;
        mInnerTask.cancel(mayInterruptIfRunning);
    }


    /**
     * @see AsyncTask#onCancelled
     */
    protected void onCancelled() {
    }

    /**
     * @see AsyncTask#onProgressUpdate
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * @see AsyncTask#publishProgress
     */
    protected final void publishProgress(Progress... values) {
        if (!mCancelled) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }


    /**
     * Similar to {@link AsyncTask#onPostExecute}, but this will never be executed if
     * {@link #cancel(boolean)} has been called before its execution, even if
     * {@link #doInBackground(Object...)} has completed when cancelled.
     *
     * @see AsyncTask#onPostExecute
     */
    protected void onPostExecute(Result result) {
    }


    /**
     * execute on {@link #PARALLEL_EXECUTOR}
     *
     * @see AsyncTask#execute
     */
    public final BaseThreadPool<Params, Progress, Result> executeParallel(Params... params) {
        return executeInternal(PARALLEL_EXECUTOR, false, params);
    }


    /**
     * execute on {@link #SERIAL_EXECUTOR}
     *
     * @see AsyncTask#execute
     */
    public final BaseThreadPool<Params, Progress, Result> executeSerial(Params... params) {
        return executeInternal(SERIAL_EXECUTOR, false, params);
    }

    /**
     * default execute on {@link #SERIAL_EXECUTOR}
     */
    public final BaseThreadPool<Params, Progress, Result> executeDefault(Params... params) {
        return executeSerial(params);
    }


    /**
     * Cancel all previously created instances of the same class tracked by the same
     * {@link Tracker}, and then {@link #executeParallel}.
     */
    public final BaseThreadPool<Params, Progress, Result> cancelPreviousAndExecuteParallel(Params... params) {
        return executeInternal(PARALLEL_EXECUTOR, true, params);
    }


    /**
     * Cancel all previously created instances of the same class tracked by the same
     * {@link Tracker}, and then {@link #executeSerial}.
     */
    public final BaseThreadPool<Params, Progress, Result> cancelPreviousAndExecuteSerial(Params... params) {
        return executeInternal(SERIAL_EXECUTOR, true, params);
    }


    private final BaseThreadPool<Params, Progress, Result> executeInternal(Executor executor, boolean cancelPrevious, Params... params) {
        if (cancelPrevious) {
            if (mTracker == null) {
                throw new IllegalStateException();
            } else {
                mTracker.cancelOthers(this);
            }
        }
        mInnerTask.executeOnExecutor(executor, params);
        return this;
    }


    /**
     * Runs a {@link Runnable} in a bg thread, using {@link #PARALLEL_EXECUTOR}.
     */
    public static BaseThreadPool<Void, Void, Void> runAsyncParallel(Runnable runnable) {
        return runAsyncInternal(PARALLEL_EXECUTOR, runnable);
    }


    /**
     * Runs a {@link Runnable} in a bg thread, using {@link #SERIAL_EXECUTOR}.
     */
    public static BaseThreadPool<Void, Void, Void> runAsyncSerial(Runnable runnable) {
        return runAsyncInternal(SERIAL_EXECUTOR, runnable);
    }


    private static BaseThreadPool<Void, Void, Void> runAsyncInternal(Executor executor, final Runnable runnable) {
        BaseThreadPool<Void, Void, Void> task = new BaseThreadPool<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                runnable.run();
                return null;
            }
        };
        return task.executeInternal(executor, false, (Void[]) null);
    }


    /**
     * Run {@code} on a worker thread, return execution result.
     *
     * @param newTask
     * @return execution result
     */
    public static <T> T runCallable(Callable<T> newTask) {
        ExecutorService pool = null;
        try {
            pool = Executors.newSingleThreadExecutor();
            Future<T> future = pool.submit(newTask);
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
        return null;
    }

    /**
     * Wait until {@link #doInBackground} finishes and returns the results of the computation.
     *
     * @see AsyncTask#get
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mInnerTask.get();
    }

    @SuppressWarnings("rawtypes")
    private static class AsyncTaskResult<Data> {
        final BaseThreadPool mTask;
        final Data[] mData;


        AsyncTaskResult(BaseThreadPool task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    private static class InternalHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }
}
