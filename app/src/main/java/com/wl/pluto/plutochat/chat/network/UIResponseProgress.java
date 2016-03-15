package com.wl.pluto.plutochat.chat.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wl.pluto.plutochat.chat.common_interface.IResponseProgressListener;

import java.lang.ref.WeakReference;

/**
 * 文件下载的抽象类，在文件下载的过程中，我们需要在UI线程中为我们的ProgressBar设置下载进度。
 * 但是我们的下载一般都是放在子线程中进行的。这就需要子线程与ＵＩ线程之间进行通信。该类的作用就是
 * 利用Handler在ＵＩ线程和下载的子线程中实现通信
 * Created by pluto on 16-1-11.
 */
public abstract class UIResponseProgress implements IResponseProgressListener {

    private static final int RESPONSE_UPDATE = 0x02;

    /**
     * handler
     */
    private UIHandler mUIHandler = new UIHandler(Looper.myLooper(), this);

    /**
     * 为了避免内存泄露，使用内部静态类，通过弱引用来引用一个外部类的引用。
     */
    private static class UIHandler extends Handler {

        private final WeakReference<UIResponseProgress> progressWeakReference;

        public UIHandler(Looper looper, UIResponseProgress uiResponseProgress) {
            super(looper);
            progressWeakReference = new WeakReference<>(uiResponseProgress);
        }


        @Override
        public void handleMessage(Message msg) {

            //handler 从子线程把数据发回来，　我们就可以在ＵＩ线程里面更新ＵＩ控件了
            switch (msg.what) {
                case RESPONSE_UPDATE:

                    UIResponseProgress responseProgress = progressWeakReference.get();
                    if (responseProgress != null) {
                        ProgressModel model = (ProgressModel) msg.obj;
                        responseProgress.onUIResponseProgress(model.getCurrentBytes(), model.getContentLength(), model.isDone());
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onResponseProgress(long readBytes, long contentLength, boolean done) {

        //在这里，我们会获得在子线程中下载的时候，给我们反馈回来的下载进度。有了这个值，我们通过Handler将这些值
        //发送到ＵＩ线程中，ＵＩ线程就会利用这些值来更新UI控件
        Message message = Message.obtain();
        message.obj = new ProgressModel(readBytes, contentLength, done);
        message.what = RESPONSE_UPDATE;
        mUIHandler.sendMessage(message);
    }


    /**
     * 在ＵＩ线程里面，需要为我们的ProgressBar控件设置进度，就需要有方法能直接来调用，该方法就是为这个目的准备的。
     *
     * @param totalBytesRead
     * @param contentLength
     * @param done
     */
    public abstract void onUIResponseProgress(long totalBytesRead, long contentLength, boolean done);
}
