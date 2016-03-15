package com.wl.pluto.plutochat.chat.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawPath extends DrawAction {

    private Path mPath;
    private int mPaintSize;

    public DrawPath(int color, float x, float y, int size) {
        super(color);

        this.mPaintSize = size;
        mPath = new Path();
        mPath.moveTo(x, y);
        mPath.lineTo(x, y);

    }

    @Override
    public void draw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mPaintSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(mPath, paint);

    }

    @Override
    public void move(float x, float y) {
        mPath.lineTo(x, y);
    }

}
