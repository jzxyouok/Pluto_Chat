package com.wl.pluto.plutochat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class PaintView extends View {

    /**
     * 标示
     */
    private static final String TAG = "--PaintView-->";

    /**
     * 画笔
     */
    private Paint mPathPaint;

    /**
     * 画布背景
     */
    private Bitmap mBitmap;

    /**
     * 背景画笔
     */
    private Paint mBitmapPaint;

    /**
     * 手指滑动的路径
     */
    private Path mPath;

    /**
     * 保存之前的路径
     */
    private Path mHistoryPath;

    /**
     * 画布
     */
    private Canvas mCanvas;

    /**
     * 保存每一次滑动的路径
     */
    private ArrayList<Path> mPathList = new ArrayList<>();

    /**
     * 当前链表中保存的路径，也是获取上一步和下一步的指针
     */
    private int currentStep = -1;

    /**
     * 屏幕宽度
     */
    private int screenWidth = 720;

    /**
     * 屏幕高度
     */
    private int screenHeight = 1920;

    /**
     * 当前x坐标
     */
    private float currentX;

    /**
     * 当前y坐标
     */
    private float currentY;

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        initPathPaint();

        initBitmap();

        initPath();
    }

    private void initPath() {

        mHistoryPath = new Path();
        mPath = new Path();
        mPathList.add(mPath);
        currentStep++;
    }

    private void initBitmap() {

        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }

    private void initPathPaint() {

        mPathPaint = new Paint();

        mPathPaint.setColor(Color.GREEN);

        // 抗锯齿
        mPathPaint.setAntiAlias(true);

        // 消除拉动，使画面圓滑
        mPathPaint.setDither(true);

        // 设置画笔为空心，否则会是首尾连起来多边形内一块为透明。
        mPathPaint.setStyle(Paint.Style.STROKE);

        // 结合方式，平滑
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);

        // 圆头
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);

        // 设置空心边框宽
        mPathPaint.setStrokeWidth(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // widthMeasureSpec、heightMeasureSpec是有高16位模式和低16位size,所以看起来会很大
        // 获得模式
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        // AT_MOST限制最大尺寸,EXACTLY则是确定好尺寸了,例如在xml文件中设置了大小
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            // 获得View实际大小
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            init(width, height);
            // 以下方法是设置View大小的方法,除非想在代码中控制大小,不然不应该在这里填写大小
            // 如果希望其生效则不要调用super.onMeasure(widthMeasureSpec,
            // heightMeasureSpec);这句了
            // setMeasuredDimension(400, height);
        } else if (mode == MeasureSpec.UNSPECIFIED) { // View的大小不确定时
            Log.d("WOGU", "mode=UNSPECIFIED");
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(int viewWidth, int viewHeight) {

        screenWidth = viewWidth;
        screenHeight = viewHeight;
    }

    /**
     * 每次调用invalidate,系统都会调用onDraw进行重绘
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPathPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }

        invalidate();
        return true;
    }

    private void touchDown(float x, float y) {

        currentX = x;
        currentY = y;
        mPath.reset();
        mPath.moveTo(currentX, currentY);
    }

    private void touchMove(float x, float y) {

        currentX = x;
        currentY = y;
        mPath.quadTo(currentX, currentY, x, y);
    }

    private void touchUp() {

        Path path = new Path();
        mHistoryPath.addPath(mPath);
        path.addPath(mHistoryPath);
        mPathList.add(path);
        currentStep++;

        mCanvas.drawPath(mPath, mPathPaint);
        mPath.reset();
    }

    /**
     *
     */
    public void clean() {
        if (mCanvas != null) {
            mPath.reset();
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }

    /**
     * 上一步
     */
    public void previousStep() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPath.reset();
        if (currentStep >= 1) {
            mPath.addPath(mPathList.get(--currentStep));
            Log.i(TAG, "currentStep = " + currentStep);
            invalidate();
        }
    }

    /**
     * 下一步
     */
    public void nextStep() {

        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPath.reset();

        if (0 <= currentStep && currentStep < mPathList.size() - 1) {
            mPath.addPath(mPathList.get(++currentStep));
            Log.i(TAG, "currentStep = " + currentStep);
            invalidate();
        }
    }

    /**
     * 设置橡皮擦
     */
    public void setEraser() {

        // 设置两图相交时的模式，那相交处同
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPathPaint.setStrokeWidth(20);

    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {

        mPathPaint.setColor(color);
    }

    /**
     * 设置画笔宽度
     *
     * @param paintWidth
     */
    public void setPaintWidth(int paintWidth) {

    }

    /**
     * 获取画笔的颜色
     *
     * @return
     */
    public int getPaintColor() {
        return mPathPaint.getColor();
    }
}
