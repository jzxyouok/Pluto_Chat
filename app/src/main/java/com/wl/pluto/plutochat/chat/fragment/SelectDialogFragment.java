package com.wl.pluto.plutochat.chat.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.wl.pluto.plutochat.R;

import java.util.Calendar;

/**
 * Created by pluto on 15-10-19.
 */
public class SelectDialogFragment extends DialogFragment {

    /**
     * 对话框类型
     */
    public static final String DIALOG_TYPE = "DIALOG_TYPE";

    /**
     * 提示对话框
     */
    public static final int DIALOG_TYPE_ALERT = 1;

    /**
     * 日期选择对话框
     */
    public static final int DIALOG_TYPE_DATE = 2;

    /**
     * 时间选择对话框
     */
    public static final int DIALOG_TYPE_TIME = 3;

    public static SelectDialogFragment getInstance(int type) {

        SelectDialogFragment fragment = new SelectDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = null;
        int dialogType = getArguments().getInt(DIALOG_TYPE);
        switch (dialogType) {
            case DIALOG_TYPE_ALERT:

                dialog = new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getTag())
                        .setPositiveButton(R.string.text_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(R.string.text_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                break;
            case DIALOG_TYPE_DATE:

                Calendar c1 = Calendar.getInstance();
                int year = c1.get(Calendar.YEAR);
                int monthOfYear = c1.get(Calendar.MONTH);
                int dayOfMonth = c1.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                }, year, monthOfYear, dayOfMonth);
                break;
            case DIALOG_TYPE_TIME:
                Calendar c2 = Calendar.getInstance();
                int hourOfDay = c2.get(Calendar.HOUR_OF_DAY);
                int minute = c2.get(Calendar.MINUTE);

                dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                }, hourOfDay, minute, true);
                break;
        }
        return dialog;
    }
}
