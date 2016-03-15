package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

import com.wl.pluto.plutochat.chat.eventbus.GestureEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by jeck on 15-11-2.
 */
public class GestureListView extends ListView {

    private static final String TAG = "--GestureListView-->";

    /**
     *
     */
    private float touchDownPosition = 0;

    /**
     *
     */
    private float touchUpPosition = 0;

    /**
     *
     */
    private static final int TOUCH_MOVE_DISTANCE = 150;

    /**
     * 手势检测
     */
    private GestureDetector gestureDetector;

    // private Intent intent = new Intent(MainFrameworkActivity.BROAD_CAST_ACTION_GESTURE);

    public GestureListView(Context context) {
        super(context);
        initGestureDetector(getContext());
    }

    public GestureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGestureDetector(getContext());
    }

    private void initGestureDetector(Context context) {
        gestureDetector = new GestureDetector(context, new SimpleGestureListener());
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    private class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 200;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (e1 != null && e2 != null) {

                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE) {

                    Log.i(TAG, "滑动的距离" + Math.abs(e1.getX() - e2.getX()));
                    EventBus.getDefault().post(new GestureEvent((int) e1.getX(), (int) e2.getX()));

                    return true;
                }
            }
            return false;
        }

    }
}
