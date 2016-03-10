package com.wl.pluto.plutochat.manager;

import android.content.Context;
import android.util.Log;

import com.wl.pluto.plutochat.entity.FileData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * 利用URLHttpConnection 来处理网络操作
 * <p>
 * Created by pluto on 15-11-12.
 */
public class HttpConnectionManager {

    public static String TAG = "--HttpConnectionManager-->";

    private Context context;

    private static HttpConnectionManager ourInstance = new HttpConnectionManager();

    public static HttpConnectionManager getInstance() {
        return ourInstance;
    }

    private HttpConnectionManager() {

    }

    /**
     * 通过get方式返回数据
     */
    public byte[] requestDataByGet(String urlPath) throws Exception {

        // 建立url连接
        URL url = new URL(urlPath);

        // 建立http连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 设置连接超时5秒
        conn.setConnectTimeout(5000);

        // 设置连接方式
        conn.setRequestMethod("GET");

        // 如果和服务器连接成功
        if (conn.getResponseCode() == 200) {

            // 得到从服务器返回的数据流
            InputStream inputStream = conn.getInputStream();

            if (inputStream != null) {
                return readInputStream(inputStream);
            } else {
                Log.d(TAG, "数据输入流为空");
            }
        } else {
            Log.d(TAG, "连接网络失败");
        }
        return null;
    }

    /**
     * 以post方式传输数据， 返回字节流
     *
     * @param urlPath 要上传的服务器地址
     * @param params  需要上传的参数链表
     * @param files   需要上传的数据
     * @return 返回从服务器反馈回来的数据流
     * @throws IOException
     */
    public byte[] requestDataByPost(String urlPath, Map<String, String> params,
                                    FileData[] files) throws IOException {

        try {

            // 分割线
            String BOUNDARY = "---------BOUNDARY--------->";

            // 上传文件的数据类型
            String MULTIPART_FORM_DATA = "multipart/form-data";

            // url连接
            URL url = new URL(urlPath);

            // http
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置连接超时
            conn.setConnectTimeout(5000);

            // 允许输入
            conn.setDoInput(true);

            // 允许输出
            conn.setDoOutput(true);

            // 不使用缓存
            conn.setUseCaches(true);

            // 设置请求方式
            conn.setRequestMethod("POST");

            // 连接状态 一直保持
            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Charset", "UTF-8");

            // Content-Type 必须有分割线
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
                    + "; boundary=" + BOUNDARY);

            StringBuilder stringBuilder = new StringBuilder();

            // 构建表单字段内容
            for (Map.Entry<String, String> entry : params.entrySet()) {

                // 构造第一行输出
                stringBuilder.append("--");
                stringBuilder.append(BOUNDARY);
                stringBuilder.append("\r\n");

                // 构造第二行输出 要空两行
                stringBuilder.append("Content-Disposition: form-data; name=\"");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("\"\r\n\r\n");

                // 构造第三行输出 空一行
                stringBuilder.append(entry.getValue());
                stringBuilder.append("\r\n");
            }

            // 输出流
            DataOutputStream outputStream = new DataOutputStream(
                    conn.getOutputStream());

            Log.d(TAG, stringBuilder.toString());

            // 先输出这一次post请求的头部
            outputStream.write(stringBuilder.toString().getBytes());

            if (files != null) {
                // 发送文件数据
                for (FileData file : files) {
                    StringBuilder split = new StringBuilder();

                    // 构造第一行,分割线必须要
                    split.append("--");
                    split.append(BOUNDARY);
                    split.append("\r\n");

                    // 构造第二行，这是对你要上传的文件的描述，里面的formName相当于类型名称
                    split.append("Content-Disposition: form-data; name=\""
                            + file.getFormName() + "\";filename=\""
                            + file.getFileName());
                    split.append("\"\r\n");

                    // 构造第三行
                    split.append("Content-Type: " + file.getContentType());
                    split.append("\r\n\r\n");

                    Log.d(TAG, split.toString());

                    // 输出上传文件的头信息
                    outputStream.write(split.toString().getBytes());

                    // 现在开始正式传输文件内容
                    if (file.getInputStream() != null) {

                        byte[] buffer = new byte[2048];
                        int length = -1;
                        while ((length = file.getInputStream().read(buffer)) != -1) {

                            // 输出二进制的字节流
                            outputStream.write(buffer, 0, length);

                        }

                        // 输出完成之后，关闭数据流
                        file.getInputStream().close();
                    } else {

                        // 输出文件的二进制流
                        outputStream.write(file.getData(), 0,
                                file.getData().length);
                    }

                    // 输出一个换行
                    outputStream.write("\r\n".getBytes());
                }
            }
            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();

            // 输出结束标识，表示这个post请求结束
            outputStream.write(end_data);

            outputStream.flush();

            // post请求成功
            if (conn.getResponseCode() == 200) {

                // 服务器返回的数据流
                InputStream inputStream = conn.getInputStream();
                if (inputStream != null) {
                    return readInputStream(inputStream);
                }
            } else {
                Log.d(TAG, "post请求失败");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 用于传输二进制流的post请求
     */
    public byte[] updataDataByPost(String urlPath, Map<String, String> params,
                                   byte[] data, int dataSize, String fileName) throws IOException {

        try {

            // 分割线
            String BOUNDARY = "---------BOUNDARY--------->";

            // 上传文件的数据类型
            String MULTIPART_FORM_DATA = "multipart/form-data";

            // url连接
            URL url = new URL(urlPath);

            // http
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置连接超时
            conn.setConnectTimeout(5000);

            // 允许输入
            conn.setDoInput(true);

            // 允许输出
            conn.setDoOutput(true);

            // 不使用缓存
            conn.setUseCaches(false);

            // 设置请求方式
            conn.setRequestMethod("POST");

            // 连接状态 一直保持
            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Charset", "UTF-8");

            // Content-Type 必须有分割线
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
                    + "; boundary=" + BOUNDARY);

            StringBuilder stringBuilder = new StringBuilder();

            // 构建表单字段内容
            for (Map.Entry<String, String> entry : params.entrySet()) {

                // 构造第一行输出
                stringBuilder.append("--");
                stringBuilder.append(BOUNDARY);
                stringBuilder.append("\r\n");

                // 构造第二行输出 要空两行
                stringBuilder.append("Content-Disposition: form-data; name=\"");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("\"\r\n\r\n");

                // 构造第三行输出 空一行
                stringBuilder.append(entry.getValue());
                stringBuilder.append("\r\n");
            }

            // 输出流
            DataOutputStream outputStream = new DataOutputStream(
                    conn.getOutputStream());

            Log.d(TAG, "发送的HTTP报文头部数据" + stringBuilder.toString());

            // 先输出这一次post请求的头部
            outputStream.write(stringBuilder.toString().getBytes());

            // 发送文件数据
            if (data != null) {
                StringBuilder split = new StringBuilder();

                split.append("--" + BOUNDARY + "\r\n");
                split.append("Content-Disposition: form-data; name=\""
                        + "upfile" + "\";filename=\"" + fileName);
                split.append("\"\r\n");

                // 构造第三行
                split.append("Content-Type: " + "application/octet-stream");
                split.append("\r\n\r\n");

                Log.d(TAG, "发送的数据部分：" + split.toString());
                // 输出上传文件的头信息
                outputStream.write(split.toString().getBytes());

                // 现在开始正式传输文件内容
                if (data != null) {

                    outputStream.write(data, 0, dataSize);
                    Log.d(TAG, "传输数据：" + dataSize + "字节");
                }

                // 输出一个换行
                outputStream.write("\r\n".getBytes());
            }

            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();

            // 输出结束标识，表示这个post请求结束
            outputStream.write(end_data);

            outputStream.flush();

            // post请求成功
            if (conn.getResponseCode() == 200) {

                // 服务器返回的数据流
                InputStream inputStream = conn.getInputStream();
                if (inputStream != null) {
                    return readInputStream(inputStream);
                }
            } else {
                Log.d(TAG, "post请求失败");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 用于发送参数请求,同时也可以发送一个图片
     */
    public byte[] sendParamsByPost(String urlPath, Map<String, String> params,
                                   byte[] videoThumbByte) {

        try {

            // 分割线
            String BOUNDARY = "---------BOUNDARY--------->";

            // 上传文件的数据类型
            String MULTIPART_FORM_DATA = "multipart/form-data";

            // url连接
            URL url = new URL(urlPath);

            // http
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置连接超时
            conn.setConnectTimeout(5000);

            // 允许输入
            conn.setDoInput(true);

            // 允许输出
            conn.setDoOutput(true);

            // 不使用缓存
            conn.setUseCaches(false);

            // 设置请求方式
            conn.setRequestMethod("POST");

            // 连接状态 一直保持
            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Charset", "UTF-8");

            // Content-Type 必须有分割线
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
                    + "; boundary=" + BOUNDARY);

            StringBuilder stringBuilder = new StringBuilder();

            // 构建表单字段内容
            for (Map.Entry<String, String> entry : params.entrySet()) {

                // 构造第一行输出
                stringBuilder.append("--");
                stringBuilder.append(BOUNDARY);
                stringBuilder.append("\r\n");

                // 构造第二行输出 要空两行
                stringBuilder.append("Content-Disposition: form-data; name=\"");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("\"\r\n\r\n");

                // 构造第三行输出 空一行
                stringBuilder.append(entry.getValue());
                stringBuilder.append("\r\n");
            }

            // 输出流
            DataOutputStream outputStream = new DataOutputStream(
                    conn.getOutputStream());

            Log.d(TAG, "发送的HTTP报文头部数据" + stringBuilder.toString());

            // 先输出这一次post请求的头部
            outputStream.write(stringBuilder.toString().getBytes());

            // 如果videoThumbByte ！= null， 说明有数据需要传输
            if (videoThumbByte != null) {

                StringBuilder split = new StringBuilder();

                split.append("--" + BOUNDARY + "\r\n");
                split.append("Content-Disposition: form-data; name=\"uppic\"; filename=\""
                        + Arrays.toString(videoThumbByte) + ".jpg" + "\"" + "\r\n");

                split.append("Content-Type: application/octet-stream" + "\r\n");
                split.append("\r\n");

                outputStream.write(split.toString().getBytes());

                outputStream.write(videoThumbByte, 0, videoThumbByte.length);

                // 输出完数据，一定要换行
                outputStream.write("\r\n".getBytes());

                Log.d(TAG, "发送的图片数据" + split.toString());
            }

            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();

            // 输出结束标识，表示这个post请求结束
            outputStream.write(end_data);

            outputStream.flush();

            // post请求成功
            if (conn.getResponseCode() == 200) {

                // 服务器返回的数据流
                InputStream inputStream = conn.getInputStream();
                if (inputStream != null) {
                    return readInputStream(inputStream);
                }
            } else {
                Log.d(TAG, "post请求失败");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将输入流转化成字节流
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {

        // 创建ByteArrayOutputStream 对象
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 定义缓存
        byte[] buffer = new byte[2048];
        int length;

        // 循环读取输入流里面的数据，并写入buffer
        while ((length = inputStream.read(buffer)) != -1) {

            byteArrayOutputStream.write(buffer, 0, length);
        }

        byteArrayOutputStream.close();
        inputStream.close();

        // 转化成字节流并返回
        return byteArrayOutputStream.toByteArray();
    }
}
