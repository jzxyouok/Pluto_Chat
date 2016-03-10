package com.wl.pluto.plutochat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.adapter.ExpressionAdapter;
import com.wl.pluto.plutochat.common_interface.OnExpressionPressListener;
import com.wl.pluto.plutochat.widget.GestureGridView;

import java.util.ArrayList;

/**
 * 表情布局一
 */
public class ExpressionFragment1 extends Fragment {

    /**
     * 点击表情的接口
     */
    private OnExpressionPressListener mExpressionPressListener;

    /**
     * GridView
     */
    private GestureGridView mExpressionGridView;

    /**
     * 适配器
     */
    private ExpressionAdapter mExpressionAdapter;

    /**
     * 适配器的数据源
     */
    private ArrayList<String> expressionList = new ArrayList<>();

    /**
     * 表情个数
     */
    private static final int EXPRESSION_COUNT = 20;

    /**
     * 删除表情
     */
    private static final String DELETE_EXPRESSION = "delete_expression";

    /**
     * 表情字符串前缀
     */
    public static final String EXPRESSION_PREFIX = "ee_";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExpressionList();
    }

    private void initExpressionList() {

        //先选二十个出来看看
        for (int i = 1; i <= EXPRESSION_COUNT; i++) {
            expressionList.add(EXPRESSION_PREFIX + i);
        }
        expressionList.add(DELETE_EXPRESSION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentLayout = inflater.inflate(R.layout.fragment_expression_fragment1, container, false);
        initLayout(fragmentLayout);
        return fragmentLayout;
    }

    /**
     * 初始化布局
     */
    private void initLayout(final View layout) {

        mExpressionGridView = (GestureGridView) layout.findViewById(R.id.gv_expression_grid_view1);

        mExpressionAdapter = new ExpressionAdapter(getActivity(), 1, expressionList);

        mExpressionGridView.setAdapter(mExpressionAdapter);
        mExpressionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String filename = mExpressionAdapter.getItem(position);
                //文本框可见时，才可以输入表情

                if (mExpressionPressListener != null) {
                    mExpressionPressListener.onExpressionPress(filename);
                } else {
                    throw new NullPointerException("mExpressionPressListener is null ");
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExpressionPressListener) {
            mExpressionPressListener = (OnExpressionPressListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExpressionPressListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mExpressionPressListener = null;
    }


}
