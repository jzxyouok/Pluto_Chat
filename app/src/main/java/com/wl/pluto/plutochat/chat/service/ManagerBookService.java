package com.wl.pluto.plutochat.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.wl.pluto.plutochat.chat.aidl.BookEntity;
import com.wl.pluto.plutochat.chat.aidl.IBookManager;
import com.wl.pluto.plutochat.chat.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManagerBookService extends Service {

    private static final String TAG = "--ManagerBookService-->";

    /**
     * 这个数据结构也是第一次见啊
     */
    private CopyOnWriteArrayList<BookEntity> mBookList = new CopyOnWriteArrayList<>();

    /**
     * 这是一个自带线程同步的数据结构,RemoteCallBackList是系统专门为跨进程的注册或注销而设计的。实现原理就是根据在对象想
     * 反序列化的时候，底层使用的Binder是相同的。更具相同的Binder,就可以找到当时注册是对应的反序列化对象，然后再把这个对象给注销掉
     */
    private RemoteCallbackList<IOnNewBookArrivedListener> mNewBookListener = new RemoteCallbackList<>();

    /**
     * 这估计也是一个自带线程同步的数据
     */
    private AtomicBoolean isServiceDestroyed = new AtomicBoolean(false);

    /**
     * 使用aidl来进行进程间通信，　在这里就需要实现在aidl中定义的同意接口。有了这个接口，在
     * 客户端实例化一个IBookManager的对象，就可以在远程调用这里面实现的方法了。
     */
    private IBookManager.Stub bookManagerAIDL = new IBookManager.Stub() {

        /**
         * 这里需要特别注意，如果这些方法，如getBookList()，在执行的过程中非常耗时，那在客户端进行调用的时候，就不能直接调用，
         * 客户端直接调用，就相当于把这些耗时的方法直接放在主线程中去执行，那是不对的。会出现ANR.所以，如果是耗时操作，那在客户端调用的时候，
         * 就要开起子线程，在子线程中调用这些方法。
         * @return
         * @throws RemoteException
         */
        @Override
        public List<BookEntity> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(BookEntity book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {


            mNewBookListener.register(listener);
            Log.i(TAG, "register a book listener");

        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {


            mNewBookListener.unregister(listener);
            Log.i(TAG, "unregister a book listener");

        }
    };


    public ManagerBookService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        if (bookManagerAIDL != null) {
            return bookManagerAIDL;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");
        BookEntity book1 = new BookEntity(1002, "aaaaaaaaaaaaa");
        mBookList.add(book1);
        BookEntity book2 = new BookEntity(1003, "bbbbbbbbbbbbbbb");
        mBookList.add(book2);

        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 通知所有的观察者
     */
    private void notifyNewBookArrived(BookEntity bookEntity) throws RemoteException {


        final int size = mNewBookListener.beginBroadcast();
        for (int i = 0; i < size; i++) {

            IOnNewBookArrivedListener listener = mNewBookListener.getBroadcastItem(i);

            if (listener != null) {

                Log.i(TAG, "notify new book arrived");

                /**
                 * 同样这个地方也一样，如果这个onNewBookArrived(BookEntity) 被实现的非常耗时，那我们在调用该方法的时候，也需要开启多线程
                 */
                listener.onNewBookArrived(bookEntity);
            }
        }
        mNewBookListener.finishBroadcast();
    }

    /**
     * 没５秒创建一本书
     */
    private class ServiceWorker implements Runnable {

        @Override
        public void run() {

            while (!isServiceDestroyed.get()) {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int id = mBookList.size() + 1;
                BookEntity entity = new BookEntity(id, "NewBook # " + id);
                mBookList.add(entity);
                Log.i(TAG, "create a new book " + entity.toString());

                try {
                    notifyNewBookArrived(entity);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
