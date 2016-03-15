package com.wl.pluto.plutochat.chat.manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by jeck on 15-10-30.
 */
public class VolleyManager {


    private static VolleyManager mInstance;

    private static final String URL = "http://192.168.1.10:8088/ChatServer/Login?LoginName=aaa@163.com&Password=123456";

    private Context context;

    private VolleyManager(Context context) {

        this.context = context;
    }

    public static VolleyManager getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new VolleyManager(context);
        }

        return mInstance;
    }


    public void testVolley() {

        RequestQueue mQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        mQueue.add(stringRequest);
    }
}
