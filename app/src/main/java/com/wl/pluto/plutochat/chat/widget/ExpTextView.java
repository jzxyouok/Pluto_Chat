package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by pluto on 15-11-28.
 */
public class ExpTextView extends TextView {

    private static final String TAG = "--ExpTextView-->";

    /**
     * 手指滑动的最小距离
     */
    private static final int MIN_SLIDE_DISTANCE = 100;

    /**
     * 手指按下时的位置
     */
    private float mTouchDownPosition;

    /**
     * 手指抬起时的位置
     */
    private float mTouchUpPosition;

    public ExpTextView(Context context) {
        super(context);
    }

    public ExpTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mTouchDownPosition = event.getX();
//                Log.i(TAG, "MotionEvent.ACTION_DOWN");
//                return true;
//
//
//            case MotionEvent.ACTION_UP:
//
//                Log.i(TAG, "MotionEvent.ACTION_UP");
//                mTouchUpPosition = event.getX();
//                if (Math.abs(mTouchUpPosition - mTouchDownPosition) > MIN_SLIDE_DISTANCE) {
//                    EventBus.getDefault().post(new GestureEvent((int) mTouchDownPosition, (int) mTouchUpPosition));
//                    return true;
//                }
//        }
        return super.onTouchEvent(event);
    }
}
