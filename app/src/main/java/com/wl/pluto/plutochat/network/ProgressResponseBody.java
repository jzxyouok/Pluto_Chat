package com.wl.pluto.plutochat.network;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;
import com.wl.pluto.plutochat.common_interface.IResponseProgressListener;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

/**
 * 进一步封装的ResponseBody, 用了处理进度
 * <p/>
 * Created by pluto on 16-1-11.
 */
public class ProgressResponseBody extends ResponseBody {

    private static final String TAG = "--ProgressResponseBody-->";

    /**
     * 实际的待封装的响应体
     */
    private final ResponseBody responseBody;

    /**
     * 进度的回调接口
     */
    private final IResponseProgressListener progressListener;

    /**
     * 完成封装的BufferedSource
     */
    private BufferedSource bufferedSource;

    /**
     * @param body
     * @param listener
     */
    public ProgressResponseBody(ResponseBody body, IResponseProgressListener listener) {
        this.responseBody = body;
        this.progressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {

        if (bufferedSource == null) {

            bufferedSource = Okio.buffer(getSource(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source
     * @return
     */
    private Source getSource(Source source) {

        return new ForwardingSource(source) {

            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {

                long bytesRead = super.read(sink, byteCount);

                //计算当前已经读取的字节数，如果读取完成，会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                //回调。
                progressListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);

                return bytesRead;
            }

            @Override
            public Timeout timeout() {
                return super.timeout();
            }

            @Override
            public void close() throws IOException {
                super.close();
            }

            @Override
            public String toString() {
                return super.toString();
            }
        };
    }
}
