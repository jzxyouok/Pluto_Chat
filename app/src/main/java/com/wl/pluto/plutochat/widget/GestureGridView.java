package com.wl.pluto.plutochat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by pluto on 15-12-18.
 */
public class GestureGridView extends GridView {

    /**
     * 手指滑动的最小距离
     */
    private static final int TOUCH_MOVE_DISTANCE = 100;

    /**
     * 手指按下的位置
     */
    private float touchDownPosition = 0;

    /**
     * 手指抬起的位置
     */
    private float touchUpPosition = 0;


    public GestureGridView(Context context) {
        super(context);
    }

    public GestureGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                touchDownPosition = ev.getX();
//                return true;
//            case MotionEvent.ACTION_UP:
//                touchUpPosition = ev.getX();
//
//                if (Math.abs(touchUpPosition - touchDownPosition) > TOUCH_MOVE_DISTANCE) {
//                    return false;
//                } else {
//
//                    return true;
//                }
//        }
//        return super.onTouchEvent(ev);
//    }
}
