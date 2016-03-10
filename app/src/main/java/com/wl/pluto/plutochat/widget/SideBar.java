package com.wl.pluto.plutochat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 右边的字母列表
 * Created by jeck on 15-11-1.
 */
public class SideBar extends View {

    /**
     * 触摸事件
     */
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    /**
     * 26字母
     */
    private static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    /**
     * 选中
     */
    private int mChoice = -1;

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 中间显示的字母
     */
    private TextView textDialog;

    public SideBar(Context context) {
        super(context);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextDialog(TextView textDialog) {
        this.textDialog = textDialog;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //先要画View控件，这个方法是必须要调用的，否则你的View都画不出来
        super.onDraw(canvas);

        //获取焦点，改变背景颜色

        //获取该View既SideBar的高度，这个高度就是你在布局文件中设置的高度．
        int height = getHeight();
        int width = getWidth();

        //计算每个字母的高度
        int singleHeight = height / b.length;

        for (int i = 0; i < b.length; i++) {

            //设置画笔的颜色，这是默认状态的字母颜色
            mPaint.setColor(Color.rgb(33, 65, 98));
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(20);

            //设置选中字母的画笔，　这是选中状态的字母颜色
            if (mChoice == i) {
                mPaint.setColor(Color.parseColor("#3399ff"));
                mPaint.setFakeBoldText(true);
            }

            //x坐标　＝　中间－字符宽带的一半
            float xPos = (width / 2) - (mPaint.measureText(b[i]) / 2);
            float yPos = singleHeight * i + singleHeight;

            //将26个字母画出来
            canvas.drawText(b[i], xPos, yPos, mPaint);

            //重置画笔
            mPaint.reset();
        }
    }

    /**
     * 这个事件分发函数，处理的就是点击SideBar的时候，产生的点击事件，
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoice = mChoice;

        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

        //点击y坐标所占高度的比例再乘以ｂ数组的长度，就等于点击ｂ中的个数
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {

            //up
            case MotionEvent.ACTION_UP:

                //ＵＰ的时候，设置背景颜色为透明
                setBackgroundColor(Color.TRANSPARENT);

                mChoice = -1;

                //刷新画布
                invalidate();

                if (textDialog != null) {
                    textDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:

                //这是按下去的时候，SideBar的背景颜色，这个地方你可以再自己定义一个颜色．或者一个ｓｈａｄｅ
                setBackgroundColor(Color.GRAY);

                if (oldChoice != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            //标示你现在正点中了某个字母
                            listener.onTouchingLetterChanged(b[c]);
                        }

                        if (textDialog != null) {
                            textDialog.setText(b[c]);
                            textDialog.setVisibility(View.VISIBLE);
                        }

                        mChoice = c;
                        invalidate();
                    }
                }
                break;
        }

        //这个事件处理完之后，还可以向下传递
        return true;
    }


    /**
     * 这是对外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     */
    public interface OnTouchingLetterChangedListener {

        void onTouchingLetterChanged(String s);
    }
}
