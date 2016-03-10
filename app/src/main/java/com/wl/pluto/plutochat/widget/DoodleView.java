package com.wl.pluto.plutochat.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wl.pluto.plutochat.model.DrawAction;
import com.wl.pluto.plutochat.model.DrawCircle;
import com.wl.pluto.plutochat.model.DrawFillCircle;
import com.wl.pluto.plutochat.model.DrawFillRect;
import com.wl.pluto.plutochat.model.DrawLine;
import com.wl.pluto.plutochat.model.DrawPath;
import com.wl.pluto.plutochat.model.DrawPoint;
import com.wl.pluto.plutochat.model.DrawRect;

public class DoodleView extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * SurfaceHolder
     */
    private SurfaceHolder mSurfaceHolder = null;

    /**
     * 当前画笔的形状
     */
    private DrawAction mCurrentAction;

    /**
     * 当前画笔的颜色
     */
    private int mCurrentColor = Color.GREEN;

    /**
     * 当前画笔的大小
     */
    private int mCurrentSize = 5;

    /**
     * 当前画笔
     */
    private Paint mPaint;

    /**
     * 记录画笔的链表
     */
    private ArrayList<DrawAction> mDrawActions;

    /**
     * 最后保存的位图
     */
    private Bitmap mBitmap;

    private ActionType mActionType = ActionType.Path;

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mCurrentSize);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        mDrawActions = new ArrayList<DrawAction>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                setCurrentAction(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:

                // 获取画布
                Canvas canvas = mSurfaceHolder.lockCanvas();

                // 设置画布为白色
                canvas.drawColor(Color.WHITE);

                for (DrawAction item : mDrawActions) {
                    item.draw(canvas);
                }

                mCurrentAction.move(touchX, touchY);
                mCurrentAction.draw(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                break;

            case MotionEvent.ACTION_UP:

                mDrawActions.add(mCurrentAction);
                mCurrentAction = null;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setCurrentAction(float x, float y) {
        switch (mActionType) {
            case Point:
                mCurrentAction = new DrawPoint(mCurrentColor, x, y);
                break;
            case Path:
                mCurrentAction = new DrawPath(mCurrentColor, x, y, mCurrentSize);
                break;
            case Line:
                mCurrentAction = new DrawLine(mCurrentColor, x, y, mCurrentSize);
                break;
            case Rect:
                mCurrentAction = new DrawRect(mCurrentColor, x, y, mCurrentSize);
                break;
            case Circle:
                mCurrentAction = new DrawCircle(mCurrentColor, x, y, mCurrentSize);
                break;
            case FilledRect:
                mCurrentAction = new DrawFillRect(mCurrentColor, x, y, mCurrentSize);
                break;
            case FilledCircle:
                mCurrentAction = new DrawFillCircle(mCurrentColor, x, y,
                        mCurrentSize);
                break;
        }
    }

    private void setPaintColor(String color) {
        mCurrentColor = Color.parseColor(color);
    }

    private void setPaintSize(int size) {
        mCurrentSize = size;
    }

    private void setPaintType(ActionType type) {
        this.mActionType = type;
    }

    public enum ActionType {
        Point, Path, Line, Rect, Circle, FilledRect, FilledCircle, Rraser
    }
}
