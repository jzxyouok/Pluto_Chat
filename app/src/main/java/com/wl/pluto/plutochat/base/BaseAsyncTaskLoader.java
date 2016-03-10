package com.wl.pluto.plutochat.base;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class BaseAsyncTaskLoader extends AsyncTaskLoader<Object> {

    public BaseAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return null;
    }

}
