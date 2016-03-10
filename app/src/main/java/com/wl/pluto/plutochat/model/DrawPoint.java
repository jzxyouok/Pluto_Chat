package com.wl.pluto.plutochat.model;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 画点
 *
 * @author pluto
 */
public class DrawPoint extends DrawAction {

    public float mx;
    public float my;

    public DrawPoint(int color, float x, float y) {
        super(color);
        this.mx = x;
        this.my = y;
    }

    @Override
    public void draw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawPoint(mx, my, paint);

    }

    @Override
    public void move(float x, float y) {

    }

}
