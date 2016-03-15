package com.wl.pluto.plutochat.chat.model;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawLine extends DrawAction {

    private float mStartX;
    private float mStartY;
    private float mStopX;
    private float mStopY;

    private int mPaintSize;

    public DrawLine(int color, float x, float y, int size) {
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
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mPaintSize);
        paint.setAntiAlias(true);
        canvas.drawLine(mStartX, mStartY, mStopX, mStopY, paint);

    }

    @Override
    public void move(float x, float y) {
        mStopX = x;
        mStopY = y;
    }

}
