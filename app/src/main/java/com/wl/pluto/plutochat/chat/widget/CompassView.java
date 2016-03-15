package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wl.pluto.plutochat.R;

/**
 * 指南针的自定义View
 * <p/>
 * Created by pluto on 16-1-13.
 */
public class CompassView extends View {

    /**
     * 方向属性
     */
    private float bearing;

    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;

    private String northString;
    private String eastString;
    private String southString;
    private String westString;

    private int textHeight;

    public CompassView(Context context) {
        super(context);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompassView();
    }

    private void initCompassView() {
        setFocusable(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(R.color.background_color);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        Resources r = this.getResources();

        northString = r.getString(R.string.compass_north);
        eastString = r.getString(R.string.compass_east);
        southString = r.getString(R.string.compass_south);
        westString = r.getString(R.string.compass_west);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));

        textHeight = (int) textPaint.measureText("Y");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        //计算最短的边
        int minSpec = Math.min(measuredWidth, measuredHeight);

        //将我们的自定义View设置成一个以最短边为正方形的控件
        setMeasuredDimension(minSpec, minSpec);
    }

    /**
     * @param measureSpec
     */
    private int measure(int measureSpec) {

        int result = 0;

        //对测量说明进行解码
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        //如果没有指定界限，那就返回一个默认值２００
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }

        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
