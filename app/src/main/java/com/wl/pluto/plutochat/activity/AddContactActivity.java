package com.wl.pluto.plutochat.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.constant.URLConstant;
import com.wl.pluto.plutochat.entity.UserEntity;
import com.wl.pluto.plutochat.greedao.ContactsUserDao;

import java.util.Map;

public class AddContactActivity extends BaseActivity {

    private static final String TAG = "--AddContactActivity-->";

    /**
     * ChatId 输入框
     */
    private EditText mPeopleUsernameEditText;

    /**
     * 搜索按钮
     */
    private Button mSearchPeopleButton;

    /**
     * username
     */
    private TextView mSearchUsernameTextView;

    /**
     * 添加联系人
     */
    private Button mSearchAddButton;

    /**
     * 添加联系人布局
     */
    private RelativeLayout mNewPeopleLayout;

    /**
     * 进度条
     */
    private ProgressDialog progressDialog;

    /**
     * 联系人数据库
     */
    private ContactsUserDao contactsUserDao;

    /**
     * 联系人数据
     */
    private Map<String, UserEntity> contactsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_people);
        init();
        initLayout();
    }

    private void init() {
        contactsUserDao = new ContactsUserDao(this);
    }

    private void initLayout() {
        mPeopleUsernameEditText = (EditText) findViewById(R.id.edit_people_user_name);
        mSearchPeopleButton = (Button) findViewById(R.id.btn_search_button);
        mSearchPeopleButton.setOnClickListener(clickListener);

        mNewPeopleLayout = (RelativeLayout) findViewById(R.id.rl_search_new_people_layout);
        mSearchUsernameTextView = (TextView) findViewById(R.id.tv_search_user_name);
        mSearchAddButton = (Button) findViewById(R.id.btn_add_new_people);
        mSearchAddButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_search_button:

                    onSearchButtonClick();
                    break;

                case R.id.btn_add_new_people:
                    onAddPeopleClick();
                    break;
            }
        }
    };

    private void onSearchButtonClick() {

        String username = mPeopleUsernameEditText.getText().toString();

        mSearchUsernameTextView.setText(username);

        mNewPeopleLayout.setVisibility(View.VISIBLE);
    }

    private void onAddPeopleClick() {

        String username = mPeopleUsernameEditText.getText().toString();

        //如果添加的是自己
        if (BaseApplication.getInstance().getUserName().equals(username)) {
            toast(getString(R.string.error_text_add_youself));
            return;
        }

        //如果该联系人已经在数据库中
        if (contactsUserDao.getContactsList().containsKey(username)) {
            toast(getString(R.string.text_notify_contact_exist));
            return;
        }

        new AddFriendTask(username).execute();
    }


    private class AddFriendTask extends AsyncTask<Void, Void, Boolean> {

        private String username;

        public AddFriendTask(String name) {

            username = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(AddContactActivity.this);
            String msg = getResources().getString(R.string.text_notify_sedding_request);
            progressDialog.setMessage(msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            String reason = getResources().getString(R.string.text_add_reason);
            try {

                //申请成为联系人
                EMContactManager.getInstance().addContact(username, reason);

                //成功之后，之间添加到联系人数据库中，本来这个地方是等到对方同意之后，才去添加到联系人数据的，但是
                //现在一直收不到消息，让我很蛋疼
                UserEntity userEntity = new UserEntity();
                userEntity.setUsername(username);
                userEntity.setUserNickName(username);
                userEntity.setUserHeadImageUrl(URLConstant.OSS_BASE_URL + username + ".png");
                contactsUserDao.addContact(userEntity);

                return true;
            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                progressDialog.dismiss();
                toast(getString(R.string.success_add_people_success));

            } else {

                toast(getString(R.string.error_add_people_failed));
            }
        }
    }

    private void sendCMDMessage(String username) {

        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

        //自定义action,在广播接受中可以收到
        String action = "add friend action";

        CmdMessageBody messageBody = new CmdMessageBody(action);

        //发送对象
        String toUsername = username;

        //设置接受者
        cmdMsg.setReceipt(toUsername);

        //设置自定义扩展熟悉
        cmdMsg.setAttribute("a", "attr");

        //添加消息body
        cmdMsg.addBody(messageBody);

        //发送消息
        EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
            @Override
            public void onSuccess() {

                Log.i(TAG, "send CMDMessage success");
            }

            @Override
            public void onError(int i, String s) {

                Log.i(TAG, "send CMDMessage failed");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }
}
