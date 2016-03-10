package com.wl.pluto.plutochat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.model.ColorPickerDialog;
import com.wl.pluto.plutochat.utils.SDCardUtils;
import com.wl.pluto.plutochat.widget.DrawingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AdvanceEditGalleyActivity extends BaseActivity implements ColorPickerDialog.OnColorChangedListener {

    /**
     * 显示图像
     */
    private DrawingView mDrawingView;

    /**
     * 显示画笔的颜色和大小
     */
    private ImageView mMosaicPaintImageView;

    /**
     * 画笔图像的大小
     */
    private int imageSize;

    /**
     * 画笔的默认大小
     */
    private static final int DEFAULT_IMAGE_SIZE = 20;

    /**
     * 画笔的布局参数
     */
    private RelativeLayout.LayoutParams params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_edit_galley);

        initLayout();
    }

    private void initLayout() {

        mDrawingView = (DrawingView) findViewById(R.id.iv_show_image);

        //橡皮擦
        TextView mEraserTextView = (TextView) findViewById(R.id.tv_doodle_eraser);
        mEraserTextView.setOnClickListener(clickListener);

        //马赛克
        TextView mMosaicTextView = (TextView) findViewById(R.id.tv_doodle_mosaic);
        mMosaicTextView.setOnClickListener(clickListener);

        //上一步
        ImageView mPreviousStep = (ImageView) findViewById(R.id.iv_mosaic_previous);
        mPreviousStep.setOnClickListener(clickListener);

        //下一步
        ImageView mNextStep = (ImageView) findViewById(R.id.iv_mosaic_next);
        mNextStep.setOnClickListener(clickListener);

        //取消
        ImageView mMosaicCancel = (ImageView) findViewById(R.id.iv_doodle_mosaic_cancel);
        mMosaicCancel.setOnClickListener(clickListener);

        //确定
        ImageView mMosaicDone = (ImageView) findViewById(R.id.iv_doodle_mosaic_done);
        mMosaicDone.setOnClickListener(clickListener);

        mMosaicPaintImageView = (ImageView) findViewById(R.id.iv_mosaic_paint_image);

        //选择画笔的样式
        RadioGroup mMosaicRadioGroup = (RadioGroup) findViewById(R.id.rg_mosaic_menu_group);
        mMosaicRadioGroup.setOnCheckedChangeListener(checkedChangeListener);


        //调整画笔的大小
        SeekBar mMosaicPaintSeekBar = (SeekBar) findViewById(R.id.sb_doodle_paint_size_seek_bar);
        mMosaicPaintSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            setMosaicPaintSize(progress);
            mDrawingView.setDrawingStroke(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                case R.id.rb_mosaic_red:
                    mDrawingView.setDrawingColor(Color.RED);
                    mMosaicPaintImageView.setBackgroundColor(Color.RED);
                    break;
                case R.id.rb_mosaic_green:
                    mDrawingView.setDrawingColor(Color.GREEN);
                    mMosaicPaintImageView.setBackgroundColor(Color.GREEN);
                    break;
                case R.id.rb_mosaic_blue:
                    mDrawingView.setDrawingColor(Color.BLUE);
                    mMosaicPaintImageView.setBackgroundColor(Color.BLUE);
                    break;
                case R.id.rb_mosaic_normal:
                    mDrawingView.setBitmapShader(R.mipmap.filter_normal);
                    mMosaicPaintImageView.setBackgroundResource(R.mipmap.filter_normal);
                    break;
                case R.id.rb_mosaic_gray:
                    mDrawingView.setBitmapShader(R.mipmap.filter_gray);
                    mMosaicPaintImageView.setBackgroundResource(R.mipmap.filter_gray);
                    break;
                case R.id.rb_mosaic_sepia:
                    mDrawingView.setBitmapShader(R.mipmap.filter_sepia);
                    mMosaicPaintImageView.setBackgroundResource(R.mipmap.filter_sepia);
                    break;
                case R.id.rb_mosaic_youge:
                    mDrawingView.setBitmapShader(R.mipmap.filter_youge);
                    mMosaicPaintImageView.setBackgroundResource(R.mipmap.filter_youge);
                    break;
            }

        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_mosaic_previous:
                    onPreviousStepClick();
                    break;
                case R.id.iv_mosaic_next:
                    onNextStepClick();
                    break;
                case R.id.tv_doodle_eraser:
                    onSetEraserClick();
                    break;
                case R.id.tv_doodle_mosaic:
                    break;
                case R.id.iv_doodle_mosaic_done:
                    savePicture();
                    break;
                case R.id.iv_doodle_mosaic_cancel:
                    onCleanClick();
                    break;
            }
        }
    };

    private void setMosaicPaintSize(int progress) {

        imageSize = DEFAULT_IMAGE_SIZE + progress;

        params = (RelativeLayout.LayoutParams) mMosaicPaintImageView
                .getLayoutParams();

        params.width = imageSize;
        params.height = imageSize;

        mMosaicPaintImageView.setLayoutParams(params);
    }

    /**
     * 设置橡皮擦
     */
    private void onSetEraserClick() {

        if (mDrawingView != null) {
            mDrawingView.enableEraser();
        }
    }

    private void onCleanClick() {
        if (mDrawingView != null) {
            mDrawingView.setMosaic();
        }
    }

    private void onPreviousStepClick() {
        if (mDrawingView != null) {

            mDrawingView.undoOperation();
        }
    }

    private void onNextStepClick() {
        if (mDrawingView != null) {
            mDrawingView.redoOperation();
        }
    }

    private void savePicture() {

        File file = takeScreenshot(true);
        if (file != null) {

            Toast.makeText(this, "图片保存成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "图片保存失败", Toast.LENGTH_LONG).show();
        }
    }

    private File takeScreenshot(boolean showToast) {

        //获取当前屏幕的DecorView,非常给力的函数
        // View v = getActivity().getWindow().getDecorView();

        //直接将某一个View转化成Bitmap
        mDrawingView.setDrawingCacheEnabled(true);
        Bitmap cachedBitmap = mDrawingView.getDrawingCache();
        Bitmap copyBitmap = cachedBitmap.copy(Bitmap.Config.RGB_565, true);
        FileOutputStream output = null;
        File file = null;
        try {
            File path = SDCardUtils.getScreenshotFolder();
            Calendar cal = Calendar.getInstance();

            file = new File(path, cal.get(Calendar.YEAR)
                    + (1 + cal.get(Calendar.MONTH))
                    + cal.get(Calendar.DAY_OF_MONTH)
                    + cal.get(Calendar.HOUR_OF_DAY)
                    + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND)
                    + ".png");
            output = new FileOutputStream(file);
            copyBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        } catch (FileNotFoundException e) {
            file = null;
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (file != null) {
            if (showToast)
                Toast.makeText(this, "Saved your creation to " + file.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();

            Intent requestScan = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            requestScan.setData(Uri.fromFile(file));
            sendBroadcast(requestScan);

            return file;
        } else {
            return null;
        }
    }

    @Override
    public void colorChanged(int color) {
        mDrawingView.setDrawingColor(color);
    }

    /**
     * 选择颜色
     */
    private void onChooseColorClick() {
        new ColorPickerDialog(this, this, Color.RED).show();
    }
}
