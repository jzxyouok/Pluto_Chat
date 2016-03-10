package com.wl.pluto.plutochat.base;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.greedao.ContactsUserDao;

/**
 * 整个系统的提示对话框
 */
public class BaseEditDialog extends Dialog {

    public static final String TAG = "--BaseEditDialog-->";

    /**
     * 单例模式,会出现这个异常Unable to add window — token android.os.BinderProxy is not valid; is your activity running?
     */
    //private static BaseEditDialog instance;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 输入框
     */
    private EditText mDialogEditText;

    /**
     * 确定按钮
     */
    private Button mDialogOkButton;

    /**
     * 取消按钮
     */
    private Button mDialogCancelButton;

    /**
     * 对话框标题
     */
    private String mDialogTitle;

    /**
     * ContactsUserDao
     */
    private ContactsUserDao contactsUserDao;

    /**
     * 要更新的username
     */
    private String mUpdateUserName;


    /*********************************************************************************************/

    private BaseEditDialog(Context context, String dialogTitle, String username) {
        super(context);
        this.context = context;
        this.mDialogTitle = dialogTitle;
        this.mUpdateUserName = username;
        contactsUserDao = new ContactsUserDao(context);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BaseEditDialog getInstance(Context context, String dialogTitle, String username) {

        BaseEditDialog dialog = new BaseEditDialog(context, dialogTitle, username);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_dialog);
        initLayout();
    }


    /**
     * 初始化布局
     */
    private void initLayout() {

        if (mDialogTitle != null) {

            setTitle(mDialogTitle);
        }
        mDialogEditText = (EditText) findViewById(R.id.et_framework_dialog_edit);
        mDialogOkButton = (Button) findViewById(R.id.btn_edit_dialog_ok);
        mDialogOkButton.setOnClickListener(clickListener);
        mDialogCancelButton = (Button) findViewById(R.id.btn_edit_dialog_cancel);
        mDialogCancelButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_edit_dialog_ok:

                    onOKButtonClick();
                    break;
                case R.id.btn_edit_dialog_cancel:

                    onCancelButtonClick();
                    break;
            }
        }
    };

    /**
     * 点击确定按钮
     */
    private void onOKButtonClick() {

        String editString = mDialogEditText.getText().toString();
        if (TextUtils.isEmpty(editString)) {
            Toast.makeText(context, R.string.text_input_is_null, Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, editString);

            new UpdateContactsTask(editString).execute();
        }
    }

    /**
     * 点击取消按钮
     */
    private void onCancelButtonClick() {

        dismiss();
    }

    /**
     * 更新数据库的异步任务
     */
    private class UpdateContactsTask extends AsyncTask<Void, Void, Boolean> {

        private String userNickName;

        public UpdateContactsTask(String nickname) {

            userNickName = nickname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ContentValues updateValues = new ContentValues();
            updateValues.put(ContactsUserDao.COLUMN_NAME_NICKNAME, userNickName);
            contactsUserDao.updateContactByName(mUpdateUserName, updateValues);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                Toast.makeText(context, R.string.text_database_update_success, Toast.LENGTH_LONG).show();
                dismiss();
            }
        }
    }
}
