package com.wl.pluto.plutochat.chat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.widget.DoodleView;

public class GalleyActivity extends AppCompatActivity {

    private DoodleView mDoodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galley);
        initLayout();
    }

    private void initLayout(){
        mDoodleView = (DoodleView)findViewById(R.id.dv_galley_doodle_view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodleView.onTouchEvent(event);
    }
}
