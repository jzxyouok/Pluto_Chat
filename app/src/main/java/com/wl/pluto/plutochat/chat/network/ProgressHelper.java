package com.wl.pluto.plutochat.chat.network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wl.pluto.plutochat.chat.common_interface.IRequestProgressListener;
import com.wl.pluto.plutochat.chat.common_interface.IResponseProgressListener;

import java.io.IOException;

/**
 * 进度辅助类
 * <p>
 * Created by pluto on 16-1-11.
 */
public class ProgressHelper {

    /**
     * 文件上传的监听方法
     * <p>
     * 将原始的RequestBody和回调接口传进来，我们自己包装一下，然后返回我们加入了上传接听的ProgressRequestBody,
     * 最后用我们处理过的请求来上传数据，就可以了。
     */
    public static ProgressRequestBody addProgressRequestListener(RequestBody body, IRequestProgressListener listener) {
        return new ProgressRequestBody(body, listener);
    }

    /**
     * 文件下载的监听方法
     * <p>
     * 这个就有点复杂了。因为OKHttp 给我们返回的是一个Response, 没有我们需要的ResponseBody,所以这个时候就需要拦截器了。有了源码，那都不是事。
     *
     * @param client
     * @param listener
     * @return
     */
    public static OkHttpClient addProgressResponseListener(OkHttpClient client, final IResponseProgressListener listener) {

        //先克隆一个OKHttpClient 对象
        OkHttpClient httpClient = client.clone();

        //为我们的httpClient 添加拦截器
        httpClient.networkInterceptors().add(new Interceptor() {

            //在这里面来构造我们的下载监听
            @Override
            public Response intercept(Chain chain) throws IOException {

                //拦截
                Response originalResponse = chain.proceed(chain.request());

                //这个时候就可以构造我们的ProgressResponseBody了。
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), listener))
                        .build();
            }
        });
        return httpClient;
    }
}
