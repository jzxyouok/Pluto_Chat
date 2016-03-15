package com.wl.pluto.plutochat.chat.base;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.activity.MainFrameworkActivity;
import com.wl.pluto.plutochat.chat.entity.LoginConfig;
import com.wl.pluto.plutochat.chat.entity.UserEntity;
import com.wl.pluto.plutochat.chat.greedao.ContactsUserDao;

import java.util.Map;

/**
 * 该类是一个基础Activity, 提供了Activity中经常需要用到的功能．具体功能如下
 * １：具有跳转功能，
 * ２：统一的Toast提示
 * ３：统一的提示对话框
 * ４：网络检测功能
 * ５：ＵＩ线程与子线程通讯的功能
 * ６：异步任务功能
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "--BaseActivity-->";

    private static final String CHAT_SHARED_PREFERENCE_NAME = "chat_shared_preference_name";

    // whether there is a Wi-Fi connection
    private static boolean isWiFiConnected = false;

    // The BroadcastReceiver that tracks network connectivity changes
    private NetworkReceiver networkReceiver;


    /**
     * NotificationManager
     */
    private NotificationManager notificationManager;

    /**
     * 进度条对话框
     */
    private ProgressDialog progressDialog;


    /**
     * 向Handler发送消息的
     */
    private BaseHandler mBaseHandler;

    /**
     * 线程池
     */
    private BaseTaskPool mBaseTaskPool;

    /**
     * 登录配置
     */
    protected LoginConfig loginConfig;

    /**
     * 联系人集合
     */
    protected static Map<String, UserEntity> mUserEntityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initNetworkReceiver();
    }

    /**
     * =======================================================================
     * 初始化操作
     * =======================================================================
     */
    private void init() {

        loginConfig = BaseApplication.getInstance().getLoginConfig();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        progressDialog = new ProgressDialog(this);

        // 初始化线程间通讯Handler
        mBaseHandler = new BaseHandler(this);

        // 初始化线程池
        mBaseTaskPool = new BaseTaskPool(this);
    }

    private void initNetworkReceiver() {

        // register broadcastReceiver to track connection changes
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);

        networkReceiver = new NetworkReceiver();

        registerReceiver(networkReceiver, filter);
    }


    /**
     * =======================================================================
     * 系统回调函数
     * =======================================================================
     */

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    /**
     * =======================================================================
     * activity 中的工具函数
     * =======================================================================
     */

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }


    public void forwardTargetActivity(Class<?> classObj) {
        Intent intent = new Intent(this, classObj);
        startActivity(intent);
    }

    public void forwardTargetActivity(Class<?> classObj, Bundle bundle) {
        Intent intent = new Intent(this, classObj);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * =======================================================================
     * activity 中的逻辑处理函数,其中包括在BaseHandler中被mBaseActivity的函数
     * =======================================================================
     */
    public void sendMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        mBaseHandler.sendMessage(msg);
    }

    public void sendMessage(int what, String data) {
        Bundle bundle = new Bundle();
        bundle.putString("data", data);

        Message msg = new Message();
        msg.what = what;
        msg.setData(bundle);
        mBaseHandler.sendMessage(msg);
    }

    public void sendMessage(int taskId, int what, String data) {
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putInt("taskId", taskId);

        Message msg = new Message();
        msg.what = what;
        msg.setData(bundle);
        mBaseHandler.sendMessage(msg);
    }

    public void doTaskAsync(int taskId, int delayTime) {

        // 这个地方的BaseTask使用的是类,没有使用接口.我觉得使用类的好处是你需要什么就在这里重写什么方法就可以了
        // 如果是接口的话,不管需不需这个方法,都需要实现里面的方法.
        mBaseTaskPool.addTask(taskId, new BaseTask() {

            // 线程完成之后的操作
            @Override
            public void onTaskComplete() {

                // 发送消息给BaseHandler,在BaseHandler中根据消息id,(即what的值)来选择相应的处理函数,其实处理函数就是本
                // activity的相对应的函数
                sendMessage(this.getId(), BaseTask.TASK_COMPLETE, null);
            }

            // 线程出错时的操作
            @Override
            public void onTaskError(String error) {

                // 这个地方就看具体情况了.具体是什么错误,那就提供相对应的解决方案,(这个地方有点类似于策略模式)
                sendMessage(this.getId(), BaseTask.TASK_NETWORK_ERROR, null);
            }
        }, delayTime);
    }

    public void handleTaskComplete() {

    }

    public void handleTaskNetworkError() {

    }

    /**
     * 我觉得这个地方确实可以用设计模式改造一下,但是我现在把设计模式又忘的差不多了
     *
     * @param taskId
     * @param httpUrl
     */
    public void doTaskAsync(int taskId, String httpUrl) {

        mBaseTaskPool.addTask(taskId, httpUrl, new BaseTask() {

        }, 0);
    }

    public Context getContext() {

        return this.getApplicationContext();
    }


    /**
     * Checks the network connection and sets the wifiConnection and
     * mobileConncection varibles accordingly
     */
    public boolean hasWiFiConnected() {

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            isWiFiConnected = (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);

        } else {
            isWiFiConnected = false;

        }

        return isWiFiConnected;
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

            // check the user prefs and network connection, Based on the result,
            // decides whether to refresh the display or keep the current display

            // if the userpref is Wi-Fi only, checks to see if the divice has a
            // Wi-Fi connection
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                // if device has its Wi-Fi connection, sets isRefreshDisplay to
                // true, this causes the display to be refershed when the user return to the app

                toast(R.string.wifi_connection);

            } else {
                toast(R.string.lose_connection);
            }
        }
    }

    public static Map<String, UserEntity> getUserEntityMap() {
        return mUserEntityMap;
    }

    /**
     * 初始化应用程序，初始完成进入主界面
     */
    private class InitApplicationTask extends AsyncTask<Void, Void, Map<String, UserEntity>> {

        private ContactsUserDao contactsUserDao;

        public InitApplicationTask() {
            contactsUserDao = new ContactsUserDao(BaseApplication.getInstance());
        }

        @Override
        protected Map<String, UserEntity> doInBackground(Void... params) {

            try {
                //先睡一会
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return contactsUserDao.getContactsList();
        }

        @Override
        protected void onPostExecute(Map<String, UserEntity> result) {
            if (result != null) {
                mUserEntityMap = result;

                //如果已经登录了，直接进入主界面
                forwardTargetActivity(MainFrameworkActivity.class);
            }
        }
    }

    protected void initAppAndLogin() {

        //否则初始化应用程序
        new InitApplicationTask().execute();
    }
}
