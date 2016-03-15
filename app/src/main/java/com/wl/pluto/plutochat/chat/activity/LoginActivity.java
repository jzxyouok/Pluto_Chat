package com.wl.pluto.plutochat.chat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.base.BaseApplication;
import com.wl.pluto.plutochat.chat.manager.ChatHelperManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    /**
     * 标示
     */
    private static final String TAG = "--LoginActivity-->";

    /**
     * 用户名
     */
    private TextView mUserNameView;

    /**
     * 登录用户名
     */
    private EditText mLoginUsername;

    /**
     * 密码
     */
    private EditText mPasswordView;

    /**
     * 登录进度条
     */
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLayout();
    }

    private void initLayout() {

        mUserNameView = (TextView) findViewById(R.id.login_username);

        mLoginUsername = (EditText) findViewById(R.id.et_login_username);

        if (!TextUtils.isEmpty(BaseApplication.getInstance().getUserName())) {

            mUserNameView.setText(BaseApplication.getInstance().getUserName());
            mLoginUsername.setVisibility(View.GONE);
        }

        mPasswordView = (EditText) findViewById(R.id.et_login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //登录
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        //登录按钮
        Button mSignInButton = (Button) findViewById(R.id.btn_sign_in_button);
        mSignInButton.setOnClickListener(clickListener);

        mProgressView = findViewById(R.id.login_progress);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_sign_in_button:

                    attemptLogin();
                    break;

            }
        }
    };

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        //判断有没有联网，当前只判断有没有Wifi
        if (!hasWiFiConnected()) {
            toast(R.string.exception_network_not_available);
            return;
        }

        // Reset errors.
        mLoginUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username;
        final String password;

        if (TextUtils.isEmpty(BaseApplication.getInstance().getUserName())) {
            username = mLoginUsername.getText().toString();
        } else {

            username = BaseApplication.getInstance().getUserName();
        }
        if (TextUtils.isEmpty(BaseApplication.getInstance().getPassword())) {

            password = mPasswordView.getText().toString();
        } else {
            password = BaseApplication.getInstance().getPassword();
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mLoginUsername.setError(getString(R.string.error_invalid_email));
            focusView = mLoginUsername;
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

            /**
             * 这是之前的逻辑
             */
            EMChatManager.getInstance().login(username, password, new EMCallBack() {
                @Override
                public void onSuccess() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onLoginSuccess(username, password);
                        }
                    });

                }

                @Override
                public void onError(int i, String s) {

                    Log.i(TAG, getString(R.string.error_login_failed));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });

        }
    }


    private boolean isUsernameValid(String name) {
        //return name.startsWith("136") && name.length() == 6;
        return !TextUtils.isEmpty(name);
    }

    private boolean isPasswordValid(String password) {
        // Replace this with your own logic
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

    private void onLoginSuccess(String chatId, String password) {

        //登录成功，保存用户名和密码
        ChatHelperManager.getInstance().setChatId(chatId);
        ChatHelperManager.getInstance().setPassword(password);
        try {

            //如果第一次登录，或者之前退出了．那就加载所有的本地群组和会话
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();

            //initializeContacts();
        } catch (Exception e) {
            e.printStackTrace();

            //如果获取好友链表或会话失败，那也禁止进入主界面
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast(getString(R.string.error_login_failed));

                    //直接退出登录
                    ChatHelperManager.getInstance().logout(true, null);
                }
            });
        }


        Log.i(TAG, getString(R.string.success_login_success));

        //如果登录成功，跳转到主界面
        //forwardTargetActivity(MainFrameworkActivity.class);
        initAppAndLogin();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

}