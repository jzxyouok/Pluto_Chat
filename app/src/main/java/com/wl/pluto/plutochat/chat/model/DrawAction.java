package com.wl.pluto.plutochat.chat.model;

import android.graphics.Canvas;

public abstract class DrawAction {

    protected int color;

    public DrawAction(int color) {
        this.color = color;
    }

    public abstract void draw(Canvas canvas);

    public abstract void move(float x, float y);
}