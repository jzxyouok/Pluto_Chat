package com.wl.pluto.plutochat.chat.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wl.pluto.plutochat.R;

public class TestAnimatorActivity extends AppCompatActivity {

    private Button mTestButton;
    private ImageView mTestImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_animator);
        initLayout();
    }

    private void initLayout() {
        mTestButton = (Button) findViewById(R.id.btn_test_animator_button);
        mTestButton.setOnClickListener(clickListener);
        mTestImageView = (ImageView) findViewById(R.id.iv_test_animator_image);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_test_animator_button:
                    onTestButtonClick();
                    break;
            }
        }
    };

    private void onTestButtonClick() {
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(mTestImageView, "translationX", -500f, 0f);

        ObjectAnimator ratate = ObjectAnimator.ofFloat(mTestImageView, "rotation", 0f, 360f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mTestImageView, "alpha", 1f, 0f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ratate).with(fadeIn).after(moveIn);
        animatorSet.setDuration(3000);
        animatorSet.start();
    }
}
