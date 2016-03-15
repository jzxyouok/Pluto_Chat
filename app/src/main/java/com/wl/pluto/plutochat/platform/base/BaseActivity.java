package com.wl.pluto.plutochat.platform.base;

import android.app.Activity;
import android.os.Bundle;

import com.wl.pluto.plutochat.R;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }
}
