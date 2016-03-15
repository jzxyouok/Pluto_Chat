package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.wl.pluto.plutochat.chat.eventbus.GestureEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by jeck on 15-11-29.
 */
public class ExpLinearLayout extends LinearLayout {

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

    public ExpLinearLayout(Context context) {
        super(context);
    }

    public ExpLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownPosition = event.getX();
                return true;

            case MotionEvent.ACTION_UP:
                mTouchUpPosition = event.getX();
                if (Math.abs(mTouchUpPosition - mTouchDownPosition) > MIN_SLIDE_DISTANCE) {
                    EventBus.getDefault().post(new GestureEvent((int) mTouchDownPosition, (int) mTouchUpPosition));
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }
}
