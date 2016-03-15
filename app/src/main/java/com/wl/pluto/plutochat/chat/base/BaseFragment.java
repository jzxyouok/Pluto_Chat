package com.wl.pluto.plutochat.chat.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {

    private OnHeadlineSelectedLintener mCallBack;

    public interface OnHeadlineSelectedLintener {

        public void onArticleSelected(int positino);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            // 现在就可以用mCallBack来向与该fragment绑定的Activity发送消息了
            mCallBack = (OnHeadlineSelectedLintener) activity;

        } catch (Exception e) {

            throw new ClassCastException(activity.toString()
                    + "must implement OnHeadlineSelectedLintener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     *
     */
    public void onXXXClick(int position) {

        mCallBack.onArticleSelected(position);
    }
}
