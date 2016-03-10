package com.wl.pluto.plutochat.model;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawRect extends DrawAction {

    private float mStartX;
    private float mStartY;
    private float mStopX;
    private float mStopY;

    private int mPaintSize;

    public DrawRect(int color, float x, float y, int size) {
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
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mPaintSize);
        canvas.drawRect(mStartX, mStartY, mStopX, mStopY, paint);
    }

    @Override
    public void move(float x, float y) {

        mStopX = x;
        mStopY = y;
    }

}
