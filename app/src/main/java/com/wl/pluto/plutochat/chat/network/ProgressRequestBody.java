package com.wl.pluto.plutochat.chat.network;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.wl.pluto.plutochat.chat.common_interface.IRequestProgressListener;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Sink;
import okio.Timeout;

/**
 * 内部维护了一个原始的RequestBody 以及一个监听器，同样的也是由构造函数传入。
 * 当然也是要重写几个函数调用原始的RequestBody 对应的函数，文件的下载是read函数中进行监听的设置，
 * 毫无疑问文件的上传就是write函数了，我们在write函数中进行了类似的操作，并回调了接口中的函数。
 * 当系统内部调用了RequestBody 的writeTo函数时，我们对BufferedSink 进行了一层包装，
 * 即设置了进度监听，并返回了我们包装的BufferedSink 。于是乎，上传于下载的进度监听就完成了。
 * Created by pluto on 16-1-11.
 */
public class ProgressRequestBody extends RequestBody {

    /**
     * 需要封装的上传对象
     */
    private final RequestBody requestBody;

    /**
     * 　上传回调接口
     */
    private final IRequestProgressListener requestProgressListener;

    public ProgressRequestBody(RequestBody body, IRequestProgressListener listener) {
        this.requestBody = body;
        this.requestProgressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {

    }

    /**
     * @param sink
     * @return
     */
    private Sink getSink(Sink sink) {

        return new ForwardingSink(sink) {

            //当前写入的字节数
            long bytesWrite = 0L;

            //总的字节数，避免重复调用contentLength;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {

                super.write(source, byteCount);

                //首先获取需要写入的总的字节数，避免重复写入
                if (contentLength == 0) {
                    contentLength = contentLength();
                }

                bytesWrite += byteCount;

                //回调
                requestProgressListener.onRequestProgress(bytesWrite, contentLength, bytesWrite == contentLength);

            }

            @Override
            public void flush() throws IOException {
                super.flush();
            }

            @Override
            public Timeout timeout() {
                return super.timeout();
            }

            @Override
            public void close() throws IOException {
                super.close();
            }
        };
    }
}
