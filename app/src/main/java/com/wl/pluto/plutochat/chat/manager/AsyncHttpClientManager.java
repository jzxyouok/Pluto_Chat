package com.wl.pluto.plutochat.chat.manager;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wl.pluto.plutochat.chat.constant.URLConstant;

/**
 * AsyncHttpRequest: 继承自Runnabler，被submit至线程池执行网络请求并发送start，success等消息
 * <p/>
 * AsyncHttpResponseHandler: 接收请求结果，一般重写onSuccess及onFailure接收请求成功或失败的消息，还有onStart，onFinish等消息
 * <p/>
 * TextHttpResponseHandler: 继承自AsyncHttpResponseHandler，只是重写了AsyncHttpResponseHandler的onSuccess和onFailure方法，将请求结果由byte数组转换为String
 * <p/>
 * JsonHttpResponseHandler: 继承自TextHttpResponseHandler，同样是重写onSuccess和onFailure方法，将请求结果由String转换为JSONObject或JSONArray
 * <p/>
 * BaseJsonHttpResponseHandler:继承自TextHttpResponseHandler，是一个泛型类，提供了parseResponse方法，子类需要提供实现，将请求结果解析成需要的类型，子类可以灵活地使用解析方法，可以直接原始解析，使用gson等。
 * <p/>
 * RequestParams: 请求参数，可以添加普通的字符串参数，并可添加File，InputStream上传文件
 * <p/>
 * AsyncHttpClient: 核心类，使用HttpClient执行网络请求，提供了get，put，post，delete，head等请求方法，使用起来很简单，只需以url及RequestParams调用相应的方法即可，还可以选择性地传入Context，用于取消Content相关的请求，同时必须提供ResponseHandlerInterface（AsyncHttpResponseHandler继承自ResponseHandlerInterface）的实现类，一般为AsyncHttpResponseHandler的子类，AsyncHttpClient内部有一个线程池，当使用AsyncHttpClient执行网络请求时，最终都会调用sendRequest方法，在这个方法内部将请求参数封装成AsyncHttpRequest（继承自Runnable）交由内部的线程池执行。
 * <p/>
 * SyncHttpClient: 继承自AsyncHttpClient，同步执行网络请求，AsyncHttpClient把请求封装成AsyncHttpRequest后提交至线程池，SyncHttpClient把请求封装成AsyncHttpRequest后直接调用它的run方法。
 * <p/>
 * <p/>
 * Created by pluto on 15-11-4.
 */
public class AsyncHttpClientManager {

    /**
     * 标示
     */
    private static final String TAG = "--AsyncHttpClientManager-->";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl) {
        return URLConstant.TEST_LOGIN_URL + relativeUrl;
    }

    private static AsyncHttpClientManager instance;
    private static Context context;

    private AsyncHttpClientManager(Context context) {
        this.context = context;
        setTimeout();
    }

    public static AsyncHttpClientManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AsyncHttpClientManager.class) {
                if (instance == null) {
                    instance = new AsyncHttpClientManager(context);
                }
            }
        }
        return instance;
    }

    public static void setTimeout() {
        client.setTimeout(10000);
    }

    public static void get(String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.get(context, url, asyncHttpResponseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        client.post(context, url, params, asyncHttpResponseHandler);
    }

    public static void download(String url, RequestParams params, FileAsyncHttpResponseHandler file) {
        client.post(context, url, params, file);
    }
}
