package com.wl.pluto.plutochat.widget;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private final Paint mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mEraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Canvas mLayerCanvas = new Canvas();

    private Bitmap mLayerBitmap;

    private ArrayList<DrawAction> mDrawOps = new ArrayList<DrawAction>();

    private DrawAction mCurrentOp = new DrawAction();

    private ArrayList<DrawAction> mUndoneOps = new ArrayList<DrawAction>();

    public DrawingView(Context context) {
        this(context, null, 0);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);

        mEraserPaint.set(mPathPaint);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraserPaint.setMaskFilter(new BlurMaskFilter(getResources()
                .getDisplayMetrics().density * 4, BlurMaskFilter.Blur.NORMAL));

        mShaderPaint.set(mPathPaint);

    }

    public void setDrawingColor(int color) {
        mCurrentOp.reset();
        mCurrentOp.type = DrawAction.Type.PAINT;
        mCurrentOp.color = color;
    }

    public void setDrawingStroke(int stroke) {
        mCurrentOp.reset();
        mCurrentOp.stroke = stroke;
    }

    public void enableEraser() {
        mCurrentOp.reset();
        mCurrentOp.type = DrawAction.Type.ERASE;
    }

    public void setMosaic() {
        mCurrentOp.reset();
    }

    public void clearDrawing() {
        mDrawOps.clear();
        mUndoneOps.clear();
        mCurrentOp.reset();
        invalidate();
    }

    public void undoOperation() {
        if (mDrawOps.size() > 0) {
            DrawAction last = mDrawOps.remove(mDrawOps.size() - 1);
            mUndoneOps.add(last);
            invalidate();
        }
    }

    public void redoOperation() {
        if (mUndoneOps.size() > 0) {
            DrawAction redo = mUndoneOps.remove(mUndoneOps.size() - 1);
            mDrawOps.add(redo);
            invalidate();
        }
    }

    public void setBitmapShader(int resId) {

        mCurrentOp.reset();
        mCurrentOp.type = DrawAction.Type.SHADER;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.MIRROR,
                Shader.TileMode.REPEAT);

        mCurrentOp.shader = shader;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLayerBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mLayerCanvas.setBitmap(mLayerBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        // Clear software canvas
        mLayerCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // 绘制历史路径
        for (DrawAction op : mDrawOps) {
            drawOperation(mLayerCanvas, op);
        }

        // 绘制当前路径
        drawOperation(mLayerCanvas, mCurrentOp);

        // Draw masked image to view
        canvas.drawBitmap(mLayerBitmap, 0, 0, null);
    }

    private void drawOperation(Canvas canvas, DrawAction action) {
        if (action.path.isEmpty()) {
            return;
        }
        final Paint paint;
        if (action.type == DrawAction.Type.PAINT) {
            paint = mPathPaint;
            paint.setColor(action.color);
            paint.setStrokeWidth(action.stroke);

        } else if (action.type == DrawAction.Type.SHADER) {

            paint = mShaderPaint;
            paint.setShader(action.shader);
            paint.setStrokeWidth(action.stroke);

        } else {
            paint = mEraserPaint;
            paint.setStrokeWidth(action.stroke);
        }
        canvas.drawPath(action.path, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mUndoneOps.clear();
                mCurrentOp.path.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:

                mCurrentOp.path.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentOp.path.lineTo(x, y);
                mDrawOps.add(new DrawAction(mCurrentOp));
                mCurrentOp.path.reset();
                break;
        }

        invalidate();
        return true;
    }

    private static class DrawAction {
        public final Path path = new Path();
        public Type type;
        public int color;
        public int stroke;
        public BitmapShader shader;

        public DrawAction() {
            //
            type = Type.PAINT;
            color = Color.RED;
            stroke = 5;
            shader = null;
        }

        public void reset() {
            this.path.reset();
        }

        public DrawAction(DrawAction op) {
            this.path.set(op.path);
            this.type = op.type;
            this.color = op.color;
            this.stroke = op.stroke;
            this.shader = op.shader;
        }

        public enum Type {
            PAINT, ERASE, SHADER;
        }
    }
}
