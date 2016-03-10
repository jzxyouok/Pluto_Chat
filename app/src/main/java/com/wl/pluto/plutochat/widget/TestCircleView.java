package com.wl.pluto.plutochat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pluto on 16-1-14.
 */
public class TestCircleView extends View {

    private static final int VIEW_DEFAULT_SIZE = 200;

    private int mColor = Color.RED;

    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TestCircleView(Context context) {
        super(context);
        init();
    }

    public TestCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint.setColor(mColor);
    }


    /**
     * 如果是直接继承自View,那需要你自己处理wrap_content的情况，这个时候就需要重写onMeasure方法，
     * 在这里面来对AT_MOST模式进行处理
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //有了这一句，march_parent和200dp系统都可以帮你搞定，但是就是wrap_content搞不定，
        //因为系统这个时候也不知道你定义的这个View到底有多大，所有系统默认按照march_parent来处理了。
        //但是　如果你是继承的已经存在的系统控件，比如TextView, Button等，那就不用管了。这些系统控件已经处理了这些情况了
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);

        //这是对应的layout_width="wrap_content"  layout_height="wrap_content"
        if (widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(VIEW_DEFAULT_SIZE, VIEW_DEFAULT_SIZE);

            //这个是layout_width="wrap_content"
        } else if (widthMeasureMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(VIEW_DEFAULT_SIZE, heightMeasureSize);

            //这个是layout_height="wrap_content"
        } else if (heightMeasureMode == MeasureSpec.AT_MOST) {

            setMeasuredDimension(widthMeasureSize, VIEW_DEFAULT_SIZE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - (paddingLeft + paddingRight);
        int height = getHeight() - (paddingTop + paddingBottom);
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(paddingLeft + (width / 2), paddingTop + (height / 2), radius, mCirclePaint);
    }
}
