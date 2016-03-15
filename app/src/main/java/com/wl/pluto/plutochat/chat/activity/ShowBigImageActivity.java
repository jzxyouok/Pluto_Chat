package com.wl.pluto.plutochat.chat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.constant.CommonConstant;

public class ShowBigImageActivity extends AppCompatActivity {

    /**
     * 显示大图
     */
    private ImageView mShowBigImage;

    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);
        initIntent();
        initLayout();
    }

    private void initIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            mImageUrl = intent.getStringExtra(CommonConstant.SHOW_BIG_IMAGE_URL_KEY);
        }
    }

    private void initLayout() {
        mShowBigImage = (ImageView) findViewById(R.id.iv_show_big_image);
        Glide.with(this).load(mImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(mShowBigImage);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.popin);
        mShowBigImage.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
