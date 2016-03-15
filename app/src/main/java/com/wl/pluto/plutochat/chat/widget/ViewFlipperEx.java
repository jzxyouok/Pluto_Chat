package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;
import android.widget.ViewFlipper;

import com.wl.pluto.plutochat.chat.activity.MainFrameworkActivity;

/**
 * Created by pluto on 16-1-13.
 */
public class ViewFlipperEx extends ViewFlipper {

    /**
     * 上次滑动的Ｘ坐标
     */
    private int mLastX = 0;

    /**
     * 上次滑动的Ｙ坐标
     */
    private int mLastY = 0;

    private int mLastInterceptX = 0;
    private int mLastInterceptY = 0;

    private Scroller mScroller;

    private float mTouchDownX = 0;
    private float mTouchUpX = 0;

    private MainFrameworkActivity mainFrameworkActivity;


    public ViewFlipperEx(Context context) {
        super(context);
        init();
    }

    public ViewFlipperEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMainFrameworkActivity(MainFrameworkActivity activity) {
        mainFrameworkActivity = activity;
    }

    private void init() {
        mScroller = new Scroller(getContext());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {

                    //这个地方返回true, 那就会直接调用onTouchEvent方法来处理该事件
                    intercepted = true;
                } else {

                    //如果返回false, 那就是标示我不拦截该事件，那在dispatchTouchEvent方法中就会直接调用
                    //子View的dispatchTouchEvent方法
                    intercepted = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        mLastInterceptX = x;
        mLastInterceptY = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mTouchUpX = event.getX();

                //左滑
                if (Math.abs(mTouchUpX - mTouchDownX) > 100) {

                    //EventBus.getDefault().post(new GestureEvent((int) mTouchDownX, (int) mTouchUpX));
                    if (mainFrameworkActivity != null) {
                        mainFrameworkActivity.onGestureDetectorHandle((int) mTouchDownX, (int) mTouchUpX);
                    }
                }
                break;
        }
        return true;
    }
}
