package com.wl.pluto.plutochat.base;

import android.os.Handler;
import android.os.Message;

/**
 * 主要是用于处理Activities中UI线程与子线程之间的通讯问题
 *
 * @author jeck
 */
public class BaseHandler extends Handler {

    /**
     * 主线程中的Activity引用.用来调用需要处理的子线程返回来的数据
     */
    private BaseActivity mBaseActivity;

    public BaseHandler(BaseActivity activity) {
        this.mBaseActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        try {

            switch (msg.what) {

                case 1:
                    // 处理情况1,这里就需要调用BaseActivity里面的函数了.需要什么,以后再扩展
                    break;
                case 2:
                    // 处理情况2
                    break;
                case 3:
                    // 处理情况3
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
