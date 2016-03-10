package com.wl.pluto.plutochat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.entity.UserEntity;
import com.wl.pluto.plutochat.greedao.ContactsUserDao;

import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity {

    private static final String TAG = "--RegisterActivity-->";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    /**
     * 注册用户名
     */
    private AutoCompleteTextView mUserNickNameView;

    /**
     * 　注册密码
     */
    private EditText mPasswordView;

    /**
     * 手机号，这是用来注册用的全局唯一
     */
    private EditText mUserName;

    /**
     * 注册
     */
    private Button mRegisterButton;

    /**
     * 　进度条
     */
    private View mProgressView;

    /**
     *
     */
    private ContactsUserDao contactsUserDao;


    private Map<String, UserEntity> contactsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        initLayout();
    }

    private void init() {

        contactsUserDao = new ContactsUserDao(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                contactsMap = contactsUserDao.getContactsList();
            }
        }).start();
    }

    private void initLayout() {

        mUserNickNameView = (AutoCompleteTextView) findViewById(R.id.nickname);
        mPasswordView = (EditText) findViewById(R.id.et_login_password);
        mUserName = (EditText) findViewById(R.id.register_username);
        mProgressView = findViewById(R.id.login_progress);

        mRegisterButton = (Button) findViewById(R.id.btn_user_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNickNameView.setError(null);
        mUserName.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserName.getText().toString();
        String password = mPasswordView.getText().toString();
        String nickName = mUserNickNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(nickName)) {
            mUserNickNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNickNameView;
            cancel = true;
        } else if (!isUsernameValid(nickName)) {
            mUserNickNameView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNickNameView;
            cancel = true;
        }

        //Check for a valid PhoneNumber
        if (TextUtils.isEmpty(userName) || !isPhoneNumberValid(userName)) {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //如果该用户名以存在，那就提示直接登录
            if (contactsMap.containsKey(userName)) {
                toast(R.string.info_username_exist);
                finish();
            } else {

                mAuthTask = new UserRegisterTask(userName, nickName, password);
                mAuthTask.execute();
            }

        }
    }

    /**
     * 不为空就可以
     *
     * @param name
     * @return
     */
    private boolean isUsernameValid(String name) {
        return !TextUtils.isEmpty(name);
    }

    private boolean isPhoneNumberValid(String phoneNumber) {

        return phoneNumber.startsWith("136") && phoneNumber.length() == 6;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserNickNameView.setAdapter(adapter);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mPassword;
        private final String mUserNickName;

        private UserEntity userEntity = new UserEntity();


        UserRegisterTask(String userName, String nickname, String password) {

            mUserName = userName;
            mPassword = password;
            mUserNickName = nickname;

            userEntity.setUsername(mUserName);
            userEntity.setUserNickName(mUserNickName);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //注册
            try {

                //调用SDK进行注册
                EMChatManager.getInstance().createAccountOnServer(mUserName, mPassword);

            } catch (final EaseMobException e) {

                //注册失败
                int errorCode = e.getErrorCode();

                if (errorCode == EMError.NONETWORK_ERROR) {

                    //网络异常，请检查网络
                    toast(getString(R.string.exception_network_connection));
                    Log.i(TAG, getString(R.string.exception_network_connection));
                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {

                    //用户已存在！
                    toast(getString(R.string.exception_user_exists));

                } else if (errorCode == EMError.UNAUTHORIZED) {

                    toast(getString(R.string.exception_register_failed_no_permission));
                    Log.i(TAG, getString(R.string.exception_register_failed_no_permission));
                } else {

                    toast(getString(R.string.exception_register_failed));
                    Log.i(TAG, getString(R.string.exception_register_failed));
                }

                return false;
            }
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            mAuthTask = null;
            showProgress(false);

            if (success) {

                toast("Register success!!!");

                //保存在数据库
                contactsUserDao.addContact(userEntity);

                //内存中也需要保存一份
                BaseApplication.getInstance().setUserName(mUserName);
                BaseApplication.getInstance().setPassword(mPassword);

                forwardTargetActivity(LoginActivity.class);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

