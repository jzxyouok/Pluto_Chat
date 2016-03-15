package com.wl.pluto.plutochat.chat.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wl.pluto.plutochat.chat.common_interface.IRequestProgressListener;

import java.lang.ref.WeakReference;

/**
 * 文件上传的抽象类。
 * <p>
 * Created by pluto on 16-1-11.
 */
public abstract class UIRequestProgress implements IRequestProgressListener {

    private static final int REQUEST_UPDATE = 0x01;

    /**
     * 处理主线程与子线程通信的Handler
     */
    private UIHandler mUIHandler = new UIHandler(Looper.getMainLooper(), this);

    /**
     * 使用static 防止内存泄露，但是需要调用外部类的一个非静态方法，所以需要将外部类的引用由构造方法直接传入。
     */
    private static class UIHandler extends Handler {

        private static final int REQUEST_UPDATE = 0x01;

        private final WeakReference<UIRequestProgress> requestProgressWeakReference;

        public UIHandler(Looper looper, UIRequestProgress progress) {
            super(looper);
            requestProgressWeakReference = new WeakReference<>(progress);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_UPDATE:
                    UIRequestProgress progress = requestProgressWeakReference.get();
                    if (progress != null) {

                        ProgressModel model = (ProgressModel) msg.obj;

                        progress.onUIRequestProgress(model.getCurrentBytes(), model.getContentLength(), model.isDone());
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    @Override
    public void onRequestProgress(long totalByteWrite, long contentLength, boolean done) {

        //通过handler发送进度消息
        Message message = Message.obtain();

        //这里就需要处理线程间通信时需要用到的技术。传递的对象必须序列化
        message.obj = new ProgressModel(totalByteWrite, contentLength, done);
        message.what = REQUEST_UPDATE;
        mUIHandler.sendMessage(message);
    }

    /**
     * 当你在ＵＩ线程中需要给ProgressBar控件设置进度的时候，就需要一个可以直接在UI线程里面运行的方法，那就是该方法了。
     *
     * @param totalByteWrite
     * @param contentLength
     * @param done
     */
    public abstract void onUIRequestProgress(long totalByteWrite, long contentLength, boolean done);
}
