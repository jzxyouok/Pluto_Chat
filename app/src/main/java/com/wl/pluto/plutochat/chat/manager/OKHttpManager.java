package com.wl.pluto.plutochat.chat.manager;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wl.pluto.plutochat.chat.common_interface.IRequestProgressListener;
import com.wl.pluto.plutochat.chat.common_interface.IResponseProgressListener;
import com.wl.pluto.plutochat.chat.network.ProgressHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * OKHttp 的使用封装，使用单例模式，
 * <p>
 * Created by pluto on 16-1-10.
 */
public class OKHttpManager {

    /**
     * 标示
     */
    private static final String TAG = "--OKHttpManager-->";

    /**
     * 单例模式
     */
    private static OKHttpManager instance;

    /**
     * OKHttpClient
     */
    private OkHttpClient mOkHttpClient;

    /**
     * 线程间通信的handler
     */
    private Handler mHandler;

    private Gson mGson;


    private OKHttpManager() {

        //创建OKHttpClient 对象
        mOkHttpClient = new OkHttpClient();

        //set cookie
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public static OKHttpManager getInstance() {
        if (instance == null) {

            synchronized (OKHttpManager.class) {

                instance = new OKHttpManager();
            }
        }

        return instance;
    }

    /**
     * 所谓的同步：
     * 　１：　需要你自己开一个线程来调用这个函数
     * 　２：　它有返回值，你需要自己处理返回值，因为是在子线程中调用，所以需要你自己处理线程间的通信
     * <p>
     * 同步的　http get请求
     *
     * @param url
     * @return　Rewponse
     */
    private Response doGet(String url) throws IOException {

        final Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * 同步的 http get请求,返回一个字符串
     *
     * @param url 　url地址
     * @throws IOException
     * @return　字符串
     */
    public String doGetAsString(String url) throws IOException {

        Response response = doGet(url);
        return response.body().string();
    }

    /**
     * 异步的http get请求
     */
    public void doAsyncGet(String url, Callback callback) throws IOException {

        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 同步的　http post 请求
     */
    private Response doPost(String url, KeyValueParam... params) throws IOException {

        final Request request = createPostRequest(url, params);

        if (request != null) {

            return mOkHttpClient.newCall(request).execute();
        } else {
            throw new IllegalArgumentException("params is null");
        }
    }

    /**
     * 同步的 http post 请求，返回String 类型
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String doPostAsString(String url, KeyValueParam... params) throws IOException {
        return doPost(url, params).body().string();
    }

    /**
     * 异步的　http post 请求
     *
     * @param url
     * @param callback
     * @param params
     * @throws IOException
     */
    public void doAsyncPost(String url, Callback callback, KeyValueParam... params) throws IOException {

        Log.i(TAG, "doAsyncPost " + url);
        final Request request = createPostRequest(url, params);

        if (request != null) {
            mOkHttpClient.newCall(request).enqueue(callback);

        } else {
            throw new IllegalArgumentException("params is null");
        }
    }

    /**
     * 创建Post请求的参数列表
     */
    private Request createPostRequest(String url, KeyValueParam[] params) {

        if (params == null) {
            return null;
        }

        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
        for (KeyValueParam item : params) {
            Log.i(TAG, item.toString());
            encodingBuilder.add(item.getKey(), item.getValue());
        }
        return new Request.Builder().url(url).post(encodingBuilder.build()).build();
    }

    /**
     * 带有进度的上传,注意，上传操作，默认都是post请求，既然是post请求，那肯定就需要构造请求参数，同时也需要构造上传数据
     *
     * @param url
     */
    public void doAsyncUploadWithProgress(String url, String filePath, IRequestProgressListener listener, Callback callback) {

        //获取上传文件
        File uploadFile = new File(filePath);

        if (!uploadFile.exists()) {
            return;
        }

        //构造上传请求,相当于构造一个上传表达form
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("hello", "android")                                                      //这里就是构造post请求的参数
                .addFormDataPart("photo", uploadFile.getName(), RequestBody.create(null, uploadFile))
                .addPart(Headers.of("Content-Disposition", "form-data; name =\"another\"; filename=\"another.dex\""), //构造上传数据
                        RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
                .build();

        //现在来封装上传请求，使其能够支持上传进度的回调
        final Request request = new Request.Builder()
                .url(url)
                .post(ProgressHelper.addProgressRequestListener(requestBody, listener))
                .build();

        //最后发送请求
        mOkHttpClient.newCall(request).enqueue(callback);

    }

    /**
     * 下载文件到指定的路径
     *
     * @param url
     */
    public void doAsyncDownloadWithProgress(String url, IResponseProgressListener listener, Callback callback) throws IOException {

        final Request request = new Request.Builder().url(url).build();

        //包装Response使其支持进度回调
        ProgressHelper.addProgressResponseListener(mOkHttpClient, listener).newCall(request).enqueue(callback);
    }

    /*******************************************************************************************/


    /**
     * 传递结果的方法
     *
     * @param request  　request
     * @param callBack callback
     */
    private void deliveryResult(final Request request, final ResultCallBack callBack) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

                sendFailedResultCallBack(request, e, callBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {

                    final String result = response.body().string();
                    if (callBack.mType == String.class) {
                        sendSuccessResultCallBack(result, callBack);
                    } else {

                        Object o = mGson.fromJson(result, callBack.mType);
                        sendSuccessResultCallBack(o, callBack);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    sendFailedResultCallBack(response.request(), e, callBack);

                }
            }
        });
    }

    /**
     * 发送错误的消息回调
     *
     * @param request
     * @param e
     * @param callBack
     */
    private void sendFailedResultCallBack(final Request request, final Exception e, final ResultCallBack callBack) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (callBack != null) {
                    callBack.onError(request, e);
                }
            }
        });
    }

    /**
     * 发送成功的消息回调
     *
     * @param object
     * @param callBack
     */
    private void sendSuccessResultCallBack(final Object object, final ResultCallBack callBack) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (callBack != null) {
                    callBack.onResponse(object);
                }
            }
        });
    }

    /**
     * 消息的回调接口
     *
     * @param <T>
     */
    public static abstract class ResultCallBack<T> {

        /**
         * 这个类型看不懂是什么啊。。。。
         */
        private Type mType;

        public ResultCallBack() {
            mType = getSuperClassTypeParameter(getClass());

        }

        public static Type getSuperClassTypeParameter(Class<?> subClass) {

            Type superClass = subClass.getGenericSuperclass();

            if (superClass instanceof Class) {
                throw new RuntimeException("missing type parameter");
            }


            ParameterizedType parameterizedType = (ParameterizedType) superClass;

            return parameterizedType.getActualTypeArguments()[0];
        }


        /**
         * 执行出错
         *
         * @param request
         * @param e
         */
        public abstract void onError(Request request, Exception e);

        /**
         * 执行成功
         *
         * @param response
         */
        public abstract void onResponse(T response);
    }

    /**
     * 键值对参数
     */
    public static class KeyValueParam {

        private String key;
        private String value;

        public KeyValueParam() {

        }

        public KeyValueParam(String key, String value) {

            this.key = key;
            this.value = value;
        }


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "KeyValueParam{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

}
