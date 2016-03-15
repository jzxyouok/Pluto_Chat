package com.wl.pluto.plutochat.chat.model;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawCircle extends DrawAction {

    private float mStartX;
    private float mStartY;
    private float mStopX;
    private float mStopY;
    private float mCircleRadius;

    private int mPaintSize;

    public DrawCircle(int color, float x, float y, int size) {
        super(color);
        this.mPaintSize = size;
        this.mStartX = x;
        this.mStartY = y;
        this.mStopX = x;
        this.mStopY = y;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mPaintSize);
        canvas.drawCircle((mStartX + mStopX) / 2, (mStartY + mStopY) / 2,
                mCircleRadius, paint);
    }

    @Override
    public void move(float x, float y) {

        mStopX = x;
        mStopY = y;
        mCircleRadius = (float) (Math.sqrt(((x - mStartX) * (x - mStartX) + (y - mStartY) * (y - mStartY))) / 2.0);
    }
}
