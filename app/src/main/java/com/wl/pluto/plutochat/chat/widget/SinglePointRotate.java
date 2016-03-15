package com.wl.pluto.plutochat.chat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wl.pluto.plutochat.R;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * 单点拖动实现旋转和伸缩
 *
 * @author jeck
 */

public class SinglePointRotate extends View {
    private int shadowColor = 0;
    /**
     * 缩放比例区间
     */
    private final float MAX_SCALE = 2.0f;
    private final float MIN_SCALE = 0.2f;


    /**
     * 旋转之后的图片宽度
     */
    private int mRotatedImageWidth;

    /**
     * 旋转之后图片的高度
     */
    private int mRotatedImageHeight;

    /**
     * 图片的宽度
     */
    private int mImageViewWidth;
    /**
     * 图片的高度
     */
    private int mImageViewHeight;
    /**
     * 图片的左边
     */
    private int mImageViewLeft;
    /**
     * 图片的上边
     */
    private int mImageViewTop;

    /**
     * 当前matrix
     */
    private Matrix mMatrix, tempMatrix;

    /**
     * 图片的初始状态，就是开始运行的时候的原始状态
     */
    private static final int NONE = 0;

    /**
     * 拖动状态
     */
    private static final int DRAG = 1;

    /**
     * 缩放状态
     */
    private static final int ZOOM = 2;
    /**
     * 旋转状态
     */
    private static final int ROTATE = 3;

    /**
     * 缩放和旋转状态
     */
    private static final int ZOOM_ROTATE = 4;

    /**
     * 默认状态为初始状态
     */
    private int mDefautMode = NONE;

    /**
     * 图片控制点的坐标
     */
    private PointF mAPoint = new PointF();
    /**
     * 图片删除点坐标
     */
    private PointF mBPoint = new PointF();

    private float disf = 1f;

    /**
     * 原始图片，需要操作的图片
     */
    private Bitmap mOriginalBitmap;

    /**
     * 删除图片
     */
    private Bitmap mDeleteBitmap;

    /**
     * 可以控制图片旋转伸缩的图片
     */
    private Bitmap mContralBitmap;

    /**
     * 画刷
     */
    private Paint mPaint = new Paint();

    /**
     * 图片中心坐标
     */
    private Point mImageCenterPoint = new Point(0, 0);

    /**
     * 旋转角度
     */
    private float mRotateAngle;

    /**
     * 缩放系数
     */
    private float mZoomFactor;

    /**
     * 缩放框外面放置删除按钮需要的宽度<br>
     * 用于放2个图标
     */
    private int mOutLayoutImageWidth;

    /**
     * 缩放框外面放置删除按钮需要的高度
     */
    private int mOutLayoutImageHeight;

    /**
     * 删除图片中心坐标
     */
    private Point mDeleteImageCenterPoint;

    /**
     * 控制图片中心坐标
     */
    private Point mContralImageCenterPoint;

    /**
     * 边框的左上角顶点坐标
     */
    private Point mPoint1;

    /**
     * 边框的右上角顶点坐标
     */
    private Point mPoint2;

    /**
     * 边框的右下角顶点坐标
     */
    private Point mPoint3;

    /**
     * 边框的左下角顶点坐标
     */
    private Point mPoint4;

    private int dx;
    private int dy;

    /**
     * 字符串
     */
    private String mText = "添加字幕";
    private DisplayMetrics display;
    /**
     * 字符串画刷
     */
    private TextPaint mTextPaint = new TextPaint();
    private Point end;
    private boolean drawControl = false; // 设置是否支持拖拽(编辑模式下、预览模式下的区别)

    /**
     * 是否可以随意拖动
     *
     * @param isControl
     */
    public void setControl(boolean isControl) {
        drawControl = isControl;
        invalidate();
    }

    private int textcolor;

    /**
     * init初始化有完整信息的字幕
     *
     * @param context
     * @param mRotate
     * @param text
     * @param textColor
     * @param ttfLocal
     * @param mdisf
     * @param mend
     * @param center
     */
    public SinglePointRotate(Context context, float mRotate, String text,
                             int textColor, String ttfLocal, float mdisf, Point mend,
                             Point center, int textSize, int shadowColor) {
        super(context);

        this.shadowColor = shadowColor;
        this.end = mend;
        this.mRotateAngle = mRotate;
        this.disf = mdisf;// 初始化要操作的原始图片， 先将图片引入到工程中

        this.textcolor = textColor;
        this.mTextPaint.setTextSize(textSize);
        this.mTextPaint.setAntiAlias(true);
        // 消除锯齿
        this.mPaint.setAntiAlias(true);
        // 初始化删除图片
        mDeleteBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        // 初始化控制图片
        mContralBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.head_image_default2);
        // 删除图片的半宽
        this.mOutLayoutImageWidth = mDeleteBitmap.getWidth() / 2;
        this.mOutLayoutImageHeight = mDeleteBitmap.getHeight() / 2;

        this.setTTFLocal(ttfLocal, false);
        setCenter(center);

        setInputText(text);

    }

    private float mtextsize = 28;

    public void setCenter(Point center) {

        if (!center.equals(mImageCenterPoint.x, mImageCenterPoint.y)) {
            this.mImageCenterPoint = center;
            this.setImageViewParams(mOriginalBitmap, mImageCenterPoint,
                    mRotateAngle, disf);
        }
    }


    /**
     * 画单独的一帧的画面
     *
     * @param picPath
     */
    private void drawFrame(String picPath) {

        if (null != mOriginalBitmap && !mOriginalBitmap.isRecycled()) {
            mOriginalBitmap.recycle();
        }
        // Log.e("picpath...", picPath + "");
        if (!TextUtils.isEmpty(picPath)) {
            this.mOriginalBitmap = BitmapFactory.decodeFile(picPath);
        } else {
            mOriginalBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.head_image_default2);
        }

        if (null == mOriginalBitmap) { // -1的情况
            GradientDrawable gd = new GradientDrawable();// 创建drawable
            gd.setColor(Color.TRANSPARENT);
            gd.setCornerRadius(5);
            if (drawControl) {
                gd.setStroke(5, Color.parseColor("#85B0E9"));
            } else {
                gd.setStroke(5, Color.TRANSPARENT);
            }

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        mOnDraw(canvas);

    }

    private Point mTemp = new Point(); // 减少获取字体大小
    private HashMap<Long, Bitmap> maps = new HashMap<Long, Bitmap>(),
            mapWords = new HashMap<Long, Bitmap>();

    private void mOnDraw(Canvas canvas) {
        clearSomeBitmap();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        // canvas.drawColor(Color.CYAN);// 测试用。查看区域
        // 设置画笔的颜色
        mPaint.setARGB(255, 138, 43, 226);
        // 设置画笔的宽度
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.WHITE);
        // // 画图片的包围框 ，顺时针画
        if (drawControl) {

            canvas.drawLine(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y,
                    mPaint);
            canvas.drawLine(mPoint2.x, mPoint2.y, mPoint3.x, mPoint3.y,
                    mPaint);
            canvas.drawLine(mPoint3.x, mPoint3.y, mPoint4.x, mPoint4.y,
                    mPaint);
            canvas.drawLine(mPoint4.x, mPoint4.y, mPoint1.x, mPoint1.y,
                    mPaint);

        }
        int bwidth = mOriginalBitmap.getWidth(), bheight = mOriginalBitmap
                .getHeight();
        Bitmap newb = Bitmap.createBitmap(bwidth, bheight, Config.ARGB_8888);

        Canvas canvasTmp = new Canvas();
        // canvasTmp.drawColor(Color.BLUE);
        canvasTmp.setBitmap(newb);
        canvasTmp.setDrawFilter(new PaintFlagsDrawFilter(0,
                Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        canvasTmp.drawBitmap(mOriginalBitmap, 0, 0, null);


        float[] fstart = new float[2];
        float[] fend = new float[2];

        int mleft = (int) (bwidth * fstart[0]);
        int mtop = (int) (bheight * fstart[1]);

        int mright = (int) (bwidth * fend[0]);
        int mbottom = (int) (bheight * fend[1]);

        // 左上角的坐标
        Point mp11 = new Point(mleft, mtop);

        // 右上角的坐标
        Point mp21 = new Point(mright, mtop);

        // 右下角坐标
        Point mp31 = new Point(mright, mbottom);

        // 左下角坐标
        Point mp41 = new Point(mleft, mbottom);

        mPaint.setColor(Color.BLACK);
        // canvasTmp.drawLine(mp11.x, mp11.y, mp21.x, mp21.y, mPaint);
        // canvasTmp.drawLine(mp21.x, mp21.y, mp31.x, mp31.y, mPaint);
        // canvasTmp.drawLine(mp31.x, mp31.y, mp41.x, mp41.y, mPaint);
        // canvasTmp.drawLine(mp41.x, mp41.y, mp11.x, mp11.y, mPaint);
        Point centerPoint = intersects(mp41, mp21, mp11, mp31);

        // 转化坐标系
        canvasTmp.translate(centerPoint.x, centerPoint.y);

        // // // 设置变化（旋转缩放）之后图片的宽高

        setImageViewWH(mRotatedImageWidth, mRotatedImageHeight,
                (mImageCenterPoint.x - mRotatedImageWidth / 2),
                (mImageCenterPoint.y - mRotatedImageHeight / 2));
        canvas.drawBitmap(newb, mMatrix, mPaint);
        maps.put(System.currentTimeMillis(), newb);
        if (!TextUtils.isEmpty(mText)) {

            /**
             * 新增部分 画text
             */
            int mpa = 5;
            mleft = (int) (mleft * disf) - mpa;
            mright = (int) (mright * disf) + mpa;
            mtop = (int) (mtop * disf) - mpa;
            mbottom = (int) (mbottom * disf) + mpa;
            int padding = 0;

            // 左上角的坐标
            Point mp12 = new Point(padding, padding);

            // 右上角的坐标
            Point mp22 = new Point(mright - mleft - padding, padding);

            int mH = mbottom - mtop - padding;
            // 右下角坐标
            Point mp32 = new Point(mp22.x, mH);

            // 左下角坐标
            Point mp42 = new Point(padding, mH);

            mPaint.setColor(Color.BLACK);
            int wi = mright - mleft;
            int mhei = mbottom - mtop;

            Bitmap mword = Bitmap.createBitmap(wi, mhei, Config.ARGB_8888);

            Canvas bword = new Canvas();
            bword.setBitmap(mword);
            // bword.drawColor(Color.RED);
            centerPoint = intersects(mp42, mp22, mp12, mp32);

            mPaint.setColor(Color.RED);

            // bword.drawLine(mp12.x, mp12.y, mp22.x, mp22.y, mPaint);
            // bword.drawLine(mp22.x, mp22.y, mp32.x, mp32.y, mPaint);
            // bword.drawLine(mp32.x, mp32.y, mp42.x, mp42.y, mPaint);
            // bword.drawLine(mp42.x, mp42.y, mp12.x, mp12.y, mPaint);
            // // //
            // bword.drawLine(mp12.x, mp12.y, mp32.x, mp32.y, mPaint);
            // bword.drawLine(mp42.x, mp42.y, mp22.x, mp22.y, mPaint);

            // bword.drawLine(0, 0, 0, mbottom - mtop, mPaint);

            int wwidth = mp22.x - mp12.x;
            int wheight = mp32.y - mp22.y;
            int topy = mpa + mp12.y;

            //TODO
            if (topy == 0l) {

                if (!mTemp.equals(mp42.x, mp42.y)) {
                    mTemp.set(mp42.x, mp42.y);
                    getTSize(wwidth, wheight);
                }


                // mTextPaint.setTypeface(typeface)
                mTextPaint.setColor(textcolor);
                mTextPaint.setStrokeWidth(0);
                mTextPaint.setFakeBoldText(false); // 外层text采用粗体

                StaticLayout myStaticLayout = new StaticLayout(mText,
                        mTextPaint, wwidth, Alignment.ALIGN_CENTER, 1.0f,
                        0.0f, false);

                // Log.e("colrd...." + mtextsize,
                // "...." + wwidth + "*" + wheight + "....." + msi.st
                // + "/////" + shadowColor + " /.."
                // + textcolor + "........" + msi.strokeWidth
                // + "...." + myStaticLayout.getTopPadding()
                // + "....."
                // +
                // myStaticLayout.getBottomPadding()+".........."+myStaticLayout.getHeight()+"...."+wheight);

                myStaticLayout.draw(bword);
            } else {
                if (!mTemp.equals(mp42.x, mp42.y)) {
                    mTemp.set(mp42.x, mp42.y);
                    mtextsize = drawSingle(mText, mTextPaint, wwidth,
                            wheight);
                }
                mTextPaint.setTextSize(mtextsize);
                FontMetrics fm = mTextPaint.getFontMetrics();

                float tHeight = (Math.abs(fm.ascent) + Math.abs(fm.descent));

                float by = 0.5f + (Math.abs(fm.descent) / tHeight);
                int baseY = (int) (wheight * by);

                int centerY = wheight / 2; // 区域的中心点

                int bCenterY = (int) (baseY + Math.abs(fm.descent) - tHeight / 2); // 计算text在编辑区域的中心点

                int bx = (int) (centerPoint.x - (mTextPaint
                        .measureText(mText) / 2));
                int basey = (topy + baseY - (bCenterY - centerY));
                if (shadowColor != 0) {
                    if (shadowColor != textcolor) {
                        mTextPaint.setColor(shadowColor);
                        mTextPaint.setStrokeWidth(3);
                        mTextPaint.setStyle(Style.FILL_AND_STROKE); // 描边种类
                        mTextPaint.setFakeBoldText(true); // 外层text采用粗体
                        bword.drawText(mText, bx, basey, mTextPaint); //
                        // 向上移动

                    }

                } else {

                }

                mTextPaint.setColor(textcolor);
                mTextPaint.setStrokeWidth(0);
                mTextPaint.setStyle(Style.FILL_AND_STROKE); // 描边种类
                mTextPaint.setFakeBoldText(false); // 外层text采用粗体
                bword.drawText(mText, bx, basey, mTextPaint); // 向上移动

            }

            bword.save();

            tempMatrix = new Matrix();

            tempMatrix.setScale(1f, 1f);

            // // 设置移动
            tempMatrix.postTranslate(dx + mOutLayoutImageWidth + mleft, dy
                    + mOutLayoutImageHeight + mtop);
            // // 设置旋转比例
            tempMatrix.postRotate(mRotateAngle % 360, (getWidth() / 2),
                    (getHeight() / 2));

            canvas.drawBitmap(mword, tempMatrix, mPaint);

            mapWords.put(System.currentTimeMillis(), mword);


        }

        if (drawControl) { // 只有在调节字幕界面。画控制器

            // 画控制图片
            canvas.drawBitmap(mContralBitmap, mContralImageCenterPoint.x
                    - mOutLayoutImageWidth, mContralImageCenterPoint.y
                    - mOutLayoutImageHeight, mPaint);

            canvas.drawBitmap(mDeleteBitmap, mDeleteImageCenterPoint.x
                    - mOutLayoutImageWidth, mDeleteImageCenterPoint.y
                    - mOutLayoutImageHeight, mPaint);
        }

    }

    private void getTSize(int wwidth, int wheight) {
        int temp = (int) (mtextsize + 100);
        StaticLayout myStaticLayout = null;
        while (temp > 3) {
            mTextPaint.setTextSize(temp);
            myStaticLayout = new StaticLayout(mText, mTextPaint, wwidth,
                    Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            if (myStaticLayout.getHeight() > wheight) {
                temp -= 2;
            } else {
                break;
            }
        }
        mtextsize = temp;

    }

    private void clearSomeBitmap() {
        if (maps.size() > 0) {
            for (Entry<Long, Bitmap> item : maps.entrySet()) {
                Bitmap b = item.getValue();
                if (null != b && !b.isRecycled()) {
                    b.recycle();
                }
                b = null;
                maps.remove(item.getKey());
            }
            maps.clear();
        }
        if (mapWords.size() > 0) {
            for (Entry<Long, Bitmap> item : mapWords.entrySet()) {
                Bitmap b = item.getValue();
                if (null != b && !b.isRecycled()) {
                    b.recycle();
                }
                b = null;
                mapWords.remove(item.getKey());
            }
            mapWords.clear();
        }

    }

    /**
     * 单行显示
     *
     * @param str
     * @param p
     * @param width
     * @return
     */
    private int drawSingle(String str, Paint p, int width, int height) {
        int i = 150;
        this.mTextPaint.setAntiAlias(true);
        FontMetrics fm;
        while (i > 3) {
            mTextPaint.setTextSize(i);
            fm = mTextPaint.getFontMetrics();

            int theight = (int) (Math.abs(fm.ascent) + Math.abs(fm.descent));
            if ((mTextPaint.measureText(str) + 10 < width && (theight < height))
                    || i <= 3) {
                break;
            } else {
                i -= 3;
            }

        }

        return i;
    }

    /**
     * @param path
     */
    public void save(String path) {

        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        mOnDraw(canvas);

        bm.recycle();
        bm = null;
    }

    public String getText() {
        return mText;
    }

    public float getTextSize() {
        if (null != mTextPaint) {
            try {
                return mTextPaint.getTextSize();
            } catch (Exception e) {
                e.printStackTrace();
                return 28;
            }
        } else
            return 28;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawControl) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // 先获取点击坐标吗？
                    mAPoint.set(event.getX() + mImageViewLeft, event.getY()
                            + mImageViewTop);
                    // 先判断用户点击的是哪个按钮（图片）, 如果是2，表示要旋转和伸缩图片
                    int checkPosition = getClickPosition((int) event.getX(),
                            (int) event.getY());
                    if (checkPosition == 0) {// onClick(this)

                        if (null != listener) {
                            listener.onClick(this);
                        }
                    }

                    if (drawControl) {

                        if (checkPosition == 1) {
                            if (null != onDelListener) {
                                onDelListener.onDelete(SinglePointRotate.this);
                            }

                        } else if (checkPosition == 2) {

                            // 设置操作模式为移动缩放模式
                            mDefautMode = ZOOM_ROTATE;
                        } else {

                            // 设置操作模式为拖动模式
                            mDefautMode = DRAG;
                        }

                    } else {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    // 如果为移动缩放模式
                    if (mDefautMode == ZOOM_ROTATE) {


                        // 记录当前位置
                        mBPoint.set(event.getX() + mImageViewLeft, event.getY()
                                + mImageViewTop);

                        //
                        float realL = (float) Math
                                .sqrt((float) (mOriginalBitmap.getWidth()
                                        * mOriginalBitmap.getWidth() + mOriginalBitmap
                                        .getHeight() * mOriginalBitmap.getHeight()) / 4);

                        //
                        float newL = (float) Math
                                .sqrt((mBPoint.x - (float) mImageCenterPoint.x)
                                        * (mBPoint.x - (float) mImageCenterPoint.x)
                                        + (mBPoint.y - (float) mImageCenterPoint.y)
                                        * (mBPoint.y - (float) mImageCenterPoint.y));

                        // 计算缩放系数，太复杂了。看不懂
                        disf = newL / realL;

                        // 计算旋转角度
                        double a = spacing(mAPoint.x, mAPoint.y,
                                mImageCenterPoint.x, mImageCenterPoint.y);

                        double b = spacing(mAPoint.x, mAPoint.y, mBPoint.x,
                                mBPoint.y);

                        double c = spacing(mBPoint.x, mBPoint.y,
                                mImageCenterPoint.x, mImageCenterPoint.y);

                        double cosB = (a * a + c * c - b * b) / (2 * a * c);

                        if (cosB > 1) {// 浮点运算的时候 cosB 有可能大于1.
                            Log.d("--旋转角度的余弦值-->", " factor:" + disf + " cosB:"
                                    + cosB);
                            cosB = 1f;
                        }

                        double angleB = Math.acos(cosB);

                        // 新的旋转角度
                        float newAngle = (float) (angleB / Math.PI * 180);

                        float p1x = mAPoint.x - (float) mImageCenterPoint.x;
                        float p2x = mBPoint.x - (float) mImageCenterPoint.x;

                        float p1y = mAPoint.y - (float) mImageCenterPoint.y;
                        float p2y = mBPoint.y - (float) mImageCenterPoint.y;

                        // 正反向。
                        if (p1x == 0) {
                            if (p2x > 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                                newAngle = -newAngle;
                            } else if (p2x < 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                                newAngle = -newAngle;
                            }
                        } else if (p2x == 0) {
                            if (p1x < 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                                newAngle = -newAngle;
                            } else if (p1x > 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                                newAngle = -newAngle;
                            }
                        } else if (p1x != 0 && p2x != 0 && p1y / p1x < p2y / p2x) {
                            if (p1x < 0 && p2x > 0 && p1y >= 0 && p2y >= 0) {// 由
                                // 第4-》第3
                                newAngle = -newAngle;
                            } else if (p2x < 0 && p1x > 0 && p1y < 0 && p2y < 0) {// 由
                                // 第2-》第1
                                newAngle = -newAngle;
                            } else {

                            }
                        } else {
                            if (p2x < 0 && p1x > 0 && p1y >= 0 && p2y >= 0) {// 由
                                // 第3-》第4

                            } else if (p2x > 0 && p1x < 0 && p1y < 0 && p2y < 0) {// 由
                                // 第1-》第2

                            } else {
                                newAngle = -newAngle;
                            }
                        }

                        mAPoint.x = mBPoint.x;
                        mAPoint.y = mBPoint.y;
                        // disf = getMaxSacle(mOriginalBitmap, mImageCenterPoint,
                        // disf);
                        if (disf <= MIN_SCALE) {
                            disf = MIN_SCALE;
                        } else if (disf >= MAX_SCALE) {

                        }
                        // 设置图片参数
                        setImageViewParams(mOriginalBitmap, mImageCenterPoint,
                                mRotateAngle + newAngle, disf);

                        // 如果为拖动模式
                    } else if (mDefautMode == DRAG) {

                        // 记录当前位置
                        mBPoint.set(event.getX() + mImageViewLeft, event.getY()
                                + mImageViewTop);

                        // 修改中心坐标
                        mImageCenterPoint.x += mBPoint.x - mAPoint.x;
                        mImageCenterPoint.y += mBPoint.y - mAPoint.y;

                        //
                        mAPoint.x = mBPoint.x;
                        mAPoint.y = mBPoint.y;

                        // 设置中心坐标
                        setCenterPoint(mImageCenterPoint);
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    // 设置操作模式为什么都不做
                    mDefautMode = NONE;
                    break;
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (null != listener) {
                    listener.onClick(this);
                }
            }
            return false;
        }
        return true;
    }

    public void setOnClickListener(onClickListener _listener) {
        listener = _listener;
    }

    private onClickListener listener;

    public interface onClickListener {

        public void onClick(SinglePointRotate view);
    }

    public void onlayout() {
        setImageViewWH(mRotatedImageWidth, mRotatedImageHeight,
                (mImageCenterPoint.x - mRotatedImageWidth / 2),
                (mImageCenterPoint.y - mRotatedImageHeight / 2));

    }

    /**
     * 设置图片的宽和高
     *
     * @param w
     * @param h
     * @param l
     * @param t
     */
    private void setImageViewWH(int w, int h, int l, int t) {
        int imageWidth = w + mOutLayoutImageWidth * 2;
        int imageHeight = h + mOutLayoutImageHeight * 2;
        int imageleft = l - mOutLayoutImageWidth;
        int imageTop = t - mOutLayoutImageHeight;

        mImageViewWidth = imageWidth;
        mImageViewHeight = imageHeight;

        mImageViewLeft = imageleft;

        int mbwidth = mImageViewWidth / 3 * 2;

        if (imageleft < -mbwidth) {
            mImageViewLeft = -mbwidth;
        }
        mImageViewTop = imageTop;
        int mbheight = mImageViewHeight / 3 * 2;
        if (imageTop < -mbheight) {
            mImageViewTop = -mbheight;
        }

        int mright = mImageViewLeft + mImageViewWidth;
        int mbottom = mImageViewTop + mImageViewHeight;
        if (null != end) {
            if (mright > end.x + mbwidth)
                mImageViewLeft = end.x - mbwidth;
            if (mbottom > end.y + mbheight) {
                mImageViewTop = end.y - mbheight;
            }
        }
        // 设置图片的布局
        this.layout(mImageViewLeft, mImageViewTop, mImageViewLeft
                + mImageViewWidth, mImageViewTop + mImageViewHeight);

    }

    private void setCenterPoint(Point c) {
        mImageCenterPoint = c;
        setImageViewWH(mRotatedImageWidth, mRotatedImageHeight,
                (mImageCenterPoint.x - mRotatedImageWidth / 2),
                (mImageCenterPoint.y - mRotatedImageHeight / 2));

    }

    /**
     * 设置图片的中心点，
     */
    public void setImageViewParams(Bitmap bm, Point centerPoint,
                                   float rotateAngle, float zoomFactor) {
        // 要缩放的原始图片
        mOriginalBitmap = bm;

        // 图片的中心坐标
        mImageCenterPoint = centerPoint;
        // 图片旋转的角度
        mRotateAngle = rotateAngle;

        // // 图片缩放系数
        mZoomFactor = zoomFactor;
        // 计算图片的位置
        calculateImagePosition(0, 0,
                ((int) (mOriginalBitmap.getWidth() * mZoomFactor)),
                ((int) (mOriginalBitmap.getHeight() * mZoomFactor)),
                rotateAngle);

        // 开始构造旋转缩放参数
        mMatrix = new Matrix();

        mMatrix.setScale(zoomFactor, zoomFactor);
        // 设置旋转比例
        mMatrix.postRotate(rotateAngle % 360, (mOriginalBitmap.getWidth()
                        * mZoomFactor / 2),
                (mOriginalBitmap.getHeight() * mZoomFactor / 2));
        // 设置移动
        mMatrix.postTranslate(dx + mOutLayoutImageWidth, dy
                + mOutLayoutImageHeight);

        // 设置小图片的宽高
        setImageViewWH(mRotatedImageWidth, mRotatedImageHeight,
                (mImageCenterPoint.x - mRotatedImageWidth / 2),
                (mImageCenterPoint.y - mRotatedImageHeight / 2));
    }

    private Point rotateCenterPoint;

    /**
     * 计算图片的位置
     */
    private void calculateImagePosition(int left, int top, int right,
                                        int bottom, float angle) {
        // 左上角的坐标
        Point p1 = new Point(left, top);

        // 右上角的坐标
        Point p2 = new Point(right, top);

        // 右下角坐标
        Point p3 = new Point(right, bottom);

        // 左下角坐标
        Point p4 = new Point(left, bottom);

        // 需要围绕参考点做旋转
        rotateCenterPoint = new Point((left + right) / 2, (top + bottom) / 2);

        // 旋转之后边框顶点的坐标
        mPoint1 = rotatePoint(rotateCenterPoint, p1, angle);
        mPoint2 = rotatePoint(rotateCenterPoint, p2, angle);
        mPoint3 = rotatePoint(rotateCenterPoint, p3, angle);
        mPoint4 = rotatePoint(rotateCenterPoint, p4, angle);
        int w = 0;
        int h = 0;
        int maxX = mPoint1.x;
        int minX = mPoint1.x;

        // 这是要选出那个坐标点的X坐标最大吗？
        if (mPoint2.x > maxX) {

            maxX = mPoint2.x;
        }

        if (mPoint3.x > maxX) {

            maxX = mPoint3.x;
        }

        if (mPoint4.x > maxX) {

            maxX = mPoint4.x;
        }

        // 这是要选出那个坐标的X坐标最小吗？
        if (mPoint2.x < minX) {
            minX = mPoint2.x;
        }
        if (mPoint3.x < minX) {
            minX = mPoint3.x;
        }

        if (mPoint4.x < minX) {
            minX = mPoint4.x;
        }

        // 计算差值
        w = maxX - minX;

        int maxY = mPoint1.y;
        int minY = mPoint1.y;

        // 选最大的Y坐标
        if (mPoint2.y > maxY) {
            maxY = mPoint2.y;
        }
        if (mPoint3.y > maxY) {
            maxY = mPoint3.y;
        }
        if (mPoint4.y > maxY) {
            maxY = mPoint4.y;
        }

        // 选最小Y坐标
        if (mPoint2.y < minY) {
            minY = mPoint2.y;
        }
        if (mPoint3.y < minY) {
            minY = mPoint3.y;
        }
        if (mPoint4.y < minY) {
            minY = mPoint4.y;
        }

        // 计算差值
        h = maxY - minY;

        // 计算边框的中心坐标
        Point centerPoint = intersects(mPoint4, mPoint2, mPoint1, mPoint3);

        // 这是要计算哪个中心的坐标？
        dx = w / 2 - centerPoint.x;
        dy = h / 2 - centerPoint.y;

        // 加了这么多距离，就相当于向右移动了这么多 的距离
        // mPoint1.x = mPoint1.x + dx ;
        // mPoint2.x = mPoint2.x + dx ;
        // mPoint3.x = mPoint3.x + dx ;
        // mPoint4.x = mPoint4.x + dx ;
        //
        // // 向下移动了这么多的距离
        // mPoint1.y = mPoint1.y + dy ;
        // mPoint2.y = mPoint2.y + dy ;
        // mPoint3.y = mPoint3.y + dy ;
        // mPoint4.y = mPoint4.y + dy ;
        //
        // 加了这么多距离，就相当于向右移动了这么多 的距离
        mPoint1.x = mPoint1.x + dx + mOutLayoutImageWidth;
        mPoint2.x = mPoint2.x + dx + mOutLayoutImageWidth;
        mPoint3.x = mPoint3.x + dx + mOutLayoutImageWidth;
        mPoint4.x = mPoint4.x + dx + mOutLayoutImageWidth;

        // 向下移动了这么多的距离
        mPoint1.y = mPoint1.y + dy + mOutLayoutImageHeight;
        mPoint2.y = mPoint2.y + dy + mOutLayoutImageHeight;
        mPoint3.y = mPoint3.y + dy + mOutLayoutImageHeight;
        mPoint4.y = mPoint4.y + dy + mOutLayoutImageHeight;

        //
        mRotatedImageWidth = w;
        mRotatedImageHeight = h;

        //
        mDeleteImageCenterPoint = mPoint1;
        mContralImageCenterPoint = mPoint3;
    }

    /**
     * 对角线的交点
     *
     * @param sp3
     * @param sp4
     * @param sp1
     * @param sp2
     * @return
     */
    private Point intersects(Point sp3, Point sp4, Point sp1, Point sp2) {
        Point localPoint = new Point(0, 0);
        double num = (sp4.y - sp3.y) * (sp3.x - sp1.x) - (sp4.x - sp3.x)
                * (sp3.y - sp1.y);
        double denom = (sp4.y - sp3.y) * (sp2.x - sp1.x) - (sp4.x - sp3.x)
                * (sp2.y - sp1.y);
        localPoint.x = (int) (sp1.x + (sp2.x - sp1.x) * num / denom);
        localPoint.y = (int) (sp1.y + (sp2.y - sp1.y) * num / denom);
        return localPoint;
    }

    /**
     * 是否点中2个图标， 1点中 delete图片 ；2点中 control图片； 0 没有点中
     */
    private int getClickPosition(int x, int y) {
        int xx = x;
        int yy = y;
        int kk1 = ((xx - mDeleteImageCenterPoint.x)
                * (xx - mDeleteImageCenterPoint.x) + (yy - mDeleteImageCenterPoint.y)
                * (yy - mDeleteImageCenterPoint.y));
        int kk2 = ((xx - mContralImageCenterPoint.x)
                * (xx - mContralImageCenterPoint.x) + (yy - mContralImageCenterPoint.y)
                * (yy - mContralImageCenterPoint.y));

        Log.d("--点击位置-->", "kk1:" + kk1 + "  kk2:" + kk2 + "  x,y" + xx + "|"
                + yy);

        if (kk1 < mOutLayoutImageWidth * mOutLayoutImageWidth) {
            return 1;
        } else if (kk2 < mOutLayoutImageWidth * mOutLayoutImageWidth) {
            return 2;
        }
        return 0;
    }

    /**
     * 旋转顶点坐标
     *
     * @param sourcePoint
     * @param angle
     * @return
     */
    private Point rotatePoint(Point rotateCenterPoint, Point sourcePoint,
                              float angle) {

        // 不明白什么意思
        sourcePoint.x = sourcePoint.x - rotateCenterPoint.x;
        sourcePoint.y = sourcePoint.y - rotateCenterPoint.y;

        // 角度a
        double alpha = 0.0;

        // 角度b
        double bate = 0.0;

        Point resultPoint = new Point();

        // 两点之间的距离
        double distance = Math.sqrt(sourcePoint.x * sourcePoint.x
                + sourcePoint.y * sourcePoint.y);

        // 如果在原点
        if (sourcePoint.x == 0 && sourcePoint.y == 0) {

            return rotateCenterPoint;

            // 在第一象限
        } else if (sourcePoint.x >= 0 && sourcePoint.y >= 0) {

            // 计算与X轴正方向的夹角, 用反三角函数，
            alpha = Math.asin(sourcePoint.y / distance);

            // 第二象限
        } else if (sourcePoint.x <= 0 && sourcePoint.y >= 0) {
            // 计算与X轴正方向的夹角, 用反三角函数，
            alpha = Math.asin(Math.abs(sourcePoint.x) / distance);
            alpha = alpha + Math.PI / 2;
            // 第三象限
        } else if (sourcePoint.x <= 0 && sourcePoint.y <= 0) {

            // 计算与x正方向的夹角
            alpha = Math.asin(Math.abs(sourcePoint.y) / distance);
            alpha = alpha + Math.PI;

            // 第四象限
        } else if (sourcePoint.x >= 0 && sourcePoint.y <= 0) {

            // 计算与x正方向的夹角
            alpha = Math.asin(sourcePoint.x / distance);
            alpha = alpha + Math.PI * 3 / 2;
        }

        // 将弧度换算成角度
        alpha = radianToDegree(alpha);

        // 旋转之后的角度
        bate = alpha + angle;

        // 将角度换算成弧度
        bate = degreeToRadian(bate);

        // 计算旋转之后的坐标点
        resultPoint.x = (int) Math.round(distance * Math.cos(bate));
        resultPoint.y = (int) Math.round(distance * Math.sin(bate));

        resultPoint.x += rotateCenterPoint.x;
        resultPoint.y += rotateCenterPoint.y;
        return resultPoint;
    }

    /**
     * 将弧度换算成角度
     */
    private double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }

    /**
     * 将角度换算成弧度
     *
     * @param degree
     * @return
     */
    private double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /**
     * 两点的距离
     */
    private double spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return Math.sqrt(x * x + y * y);
    }


    /**
     * 设置显示样式
     */
    public void setImageStyle(String picPath, boolean minvalidate) {
        drawFrame(picPath);
        invaView(minvalidate);
    }

    /**
     * 刷新图片信息的配置，缩放比
     */
    private void invaView(boolean minvalidate) {
        setImageViewParams(mOriginalBitmap, mImageCenterPoint, mRotateAngle,
                disf);
        if (minvalidate)
            invalidate();
    }

    /**
     * 设置放大程度
     *
     * @param mdisf
     */
    public void setDisf(float mdisf) {


    }

    /**
     * 设置显示的字符串
     *
     * @param text
     */
    public void setInputText(String text) {
        this.mText = text;
        mTemp.set(0, 0);


        this.invalidate();

    }

    /**
     * 设置字符串颜色
     *
     * @param textColor
     */
    public void setInputTextColor(int textColor) {
        this.textcolor = textColor;
        this.invalidate();

    }

    /**
     * 设置字体的shadow
     */
    public void setShadowColor(int shadow) {
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)

        setShadowColor(shadow, true);
    }

    private void setShadowColor(int shadow, boolean minvalidate) {
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)

        if (shadowColor != shadow) {
            shadowColor = shadow;
        }
        if (minvalidate)
            invalidate();
    }

    public float getRotateAngle() {
        return mRotateAngle;
    }

    private String ttfLocal = null;

    /**
     * 设置字体样式。楷体，宋体
     *
     * @param mttfLocal
     * @param postInvalidateEnable
     */

    private void setTTFLocal(String mttfLocal, boolean postInvalidateEnable) {

    }

    public void setTTFLocal(String mttfLocal) {
        setTTFLocal(mttfLocal, true);
    }

    /**
     * 系统默认的字体
     *
     * @param postInvalidateEnable
     */
    public void setDefualt(boolean postInvalidateEnable) {

        this.ttfLocal = null;
        this.mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT,
                Typeface.NORMAL));
        if (postInvalidateEnable) {
            this.invalidate();
        }

    }

    public String getTTFlocal() {
        return ttfLocal;
    }

    /**
     * 获取字体颜色
     *
     * @return
     */
    public int getTextColor() {
        return textcolor;
    }

    public Point getCenter() {
        return mImageCenterPoint;
    }

    public float getDisf() {
        return disf;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    /**
     * 释放资源
     */
    public void recycle() {

        recycleCommonBmps();

        if (null != mDeleteBitmap && !mDeleteBitmap.isRecycled()) {
            mDeleteBitmap.recycle();
        }
        mDeleteBitmap = null;
        if (null != mContralBitmap && !mContralBitmap.isRecycled()) {
            mContralBitmap.recycle();
        }
        mContralBitmap = null;
        mContralBitmap = null;
        if (null != mTextPaint) {
            mTextPaint.reset();
        }
        if (null != mOriginalBitmap && !mOriginalBitmap.isRecycled()) {
            mOriginalBitmap.recycle();
        }
        mOriginalBitmap = null;

    }

    /**
     * 释放自动手动公共的bmp
     */
    private void recycleCommonBmps() {
        clearSomeBitmap();
    }

    private onDelListener onDelListener;

    /**
     * 删除图标的监听
     *
     * @param listener
     */
    public void setDelListener(onDelListener listener) {
        onDelListener = listener;
    }

    public interface onDelListener {
        /**
         * 删除当前字幕
         */
        public void onDelete(SinglePointRotate single);
    }
}
