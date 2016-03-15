package com.wl.pluto.plutochat.chat.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.wl.pluto.plutochat.chat.model.DefaultLogicModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 该类会初始化环信SDK,并设置初始化参数和初始化相对应的监听器，
 * Created by jeck on 15-11-19.
 */
public abstract class BaseHelper {

    /**
     * 群组更新完成
     */
    public interface ChatSyncListener {

        void onSyncSuccess(boolean success);
    }

    private static final String TAG = "--BaseHelper-->";

    protected Context context;

    protected BaseLogicModel baseLogicModel;

    protected EMConnectionListener connectionListener;

    protected String chatId;

    protected String password;

    protected BaseNotification notification;

    private static BaseHelper instance;

    private List<ChatSyncListener> syncContactsListener;

    private List<ChatSyncListener> syncGroupsListener;

    private List<ChatSyncListener> syncBlacklistListener;

    private boolean isSyncingGroupsWithServer = false;

    private boolean isSyncingContactsWithServer = false;

    private boolean isSyncingBlacklistWithServer = false;

    private boolean isGroupsSyncedWithServer = false;

    private boolean isContactsSyncedWithServer = false;

    private boolean isBlacklistSyncedWithServer = false;

    private boolean alreadyNotified = false;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init again
     */
    private boolean isSDKInited = false;

    /***********************************************************************************/

    protected BaseHelper() {
        instance = this;
    }

    public abstract BaseLogicModel crateBaseLogicModel();

    public synchronized boolean onInit(Context context) {

        if (isSDKInited) {
            return true;
        }
        this.context = context;

        //如果子类靠不住，那还是要父类上
        baseLogicModel = crateBaseLogicModel();

        if (baseLogicModel == null) {
            baseLogicModel = new DefaultLogicModel(context);

        }

        // int pid = android.os.Process.myPid();

        //String appName = getAppName(pid);

        // Log.i(TAG, appName);

        /**
         * 如果app启用了第三方的远程server, 则application:onCreate方法会被调用两次，为了防止环信SDK被初始化两次
         * 架次判断，会保证只被初始化一次
         */
//        if (appName == null || !appName.equalsIgnoreCase(baseLogicModel.getAppProgressName())) {
//            return false;
//        }

        //初始化环信SDK
        EMChat.getInstance().init(context);

        //设置SandBox测试环境
        //if(baseLogicModel.isSandBoxMode()){
        //    EMChat.getInstance().setEnv(EMChatConfig.EMEnvMode.EMDevMode);
        //}

        initChatOptions();

        initListener();

        syncBlacklistListener = new ArrayList<>();
        syncContactsListener = new ArrayList<>();
        syncGroupsListener = new ArrayList<>();

        isSyncingBlacklistWithServer = baseLogicModel.isBlacklistSync();
        isSyncingContactsWithServer = baseLogicModel.isContactSync();
        isSyncingGroupsWithServer = baseLogicModel.isBlacklistSync();

        isSDKInited = true;
        return true;
    }

    public static BaseHelper getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public BaseLogicModel getBaseLogicModel() {
        return baseLogicModel;
    }

    /**
     * 获取Chat Id
     *
     * @return
     */
    public String getChatId() {

        if (chatId == null) {
            chatId = baseLogicModel.getChatID();
        }
        return chatId;
    }

    public String getPassword() {

        if (password == null) {
            password = baseLogicModel.getUserPassword();
        }
        return password;
    }

    public void setChatId(String chatId) {
        if (chatId != null) {
            if (baseLogicModel.saveChatID(chatId)) {
                this.chatId = chatId;
            }
        }
    }

    public void setPassword(String password) {
        if (password != null) {
            if (baseLogicModel.saveUserPassword(password)) {
                this.password = password;
            }
        }
    }

    protected void initChatOptions() {

        //获取EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();

        //默认添加好友是不需要验证的，如果需要验证，那就改为需要验证，现在是不需要
        options.setAcceptInvitationAlways(baseLogicModel.isAcceptInvitationAlways());

        //默认环信是不维护好友关系链表的，如果App依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(baseLogicModel.isUseHXRoster());

        //设置是否需要已读回执
        options.setRequireAck(baseLogicModel.isRequireReadAck());

        //设置是否需要已送达回执
        options.setRequireDeliveryAck(baseLogicModel.isRequireDeliverAck());

        // 设置从ＤＢ加载数据时，每一个conversation需要加载的msg个数
        options.setNumberOfMessagesLoaded(1);

        notification = createNotification();
        notification.init(context);

        //notification.setNotificationInfoProvider(getNotificationProvider());
    }

    private String getAppName(int pid) {
        String processName = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = list.iterator();

        PackageManager packageManager = context.getPackageManager();

        while (iterator.hasNext()) {

            ActivityManager.RunningAppProcessInfo info = iterator.next();

            if (info.pid == pid) {

                try {
                    CharSequence charSequence = packageManager.getApplicationLabel(packageManager.getApplicationInfo(info.processName,
                            PackageManager.GET_META_DATA));

                    processName = info.processName;

                    return processName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return processName;
    }

    /**
     * 退出Ｃｈａｔ
     *
     * @param unBindDeviceToken
     */
    public void logout(final boolean unBindDeviceToken, final EMCallBack callBack) {

        setPassword(null);
        reset();

        EMChatManager.getInstance().logout(unBindDeviceToken, new EMCallBack() {
            @Override
            public void onSuccess() {

                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {

                if (callBack != null) {
                    callBack.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {

                if (callBack != null) {
                    callBack.onProgress(i, s);
                }
            }
        });
    }

    /**
     * 是否已经登录
     *
     * @return
     */
    public boolean isLogin() {
        return EMChat.getInstance().isLoggedIn();
    }

    /**
     * 初始化网络连接的监听
     */
    protected void initListener() {
        connectionListener = new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(int i) {

            }
        };
    }

    /**
     * 从服务器获取群组链表，该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callBack) {

        //如果正在获取，直接返回
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroupManager.getInstance().getGroupsFromServer();

                    //in case logout before server return, wo should return immediately
                    //如果在服务器返回数据之前退出了，那我们立刻返回
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    baseLogicModel.setGroupSync(true);


                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    if (callBack != null) {
                        callBack.onSuccess();
                    }

                } catch (EaseMobException e) {

                    baseLogicModel.setGroupSync(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;

                    if (callBack != null) {
                        callBack.onError(e.getErrorCode(), e.toString());
                    }
                }
            }
        }).start();
    }

    public void notifyGroupsSynclistener(boolean success) {
        for (ChatSyncListener item : syncGroupsListener) {
            item.onSyncSuccess(success);
        }
    }

    /**
     * 从服务器获取联系人链表数据，
     */
    public synchronized void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callBack) {

        if (isSyncingContactsWithServer) {
            return;
        }

        isSyncingContactsWithServer = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<String> usernames = null;
                try {

                    //获取联系人链表
                    usernames = EMContactManager.getInstance().getContactUserNames();

                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    baseLogicModel.setContactSync(true);

                    isSyncingContactsWithServer = false;
                    isContactsSyncedWithServer = true;

                    if (callBack != null) {
                        callBack.onSuccess(usernames);
                    }

                } catch (EaseMobException e) {

                    baseLogicModel.setContactSync(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;

                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.onError(e.getErrorCode(), e.toString());
                    }
                }
            }
        }).start();
    }

    public void notifyContactsSyncListener(boolean success) {
        for (ChatSyncListener item : syncContactsListener) {
            item.onSyncSuccess(success);
        }
    }

    /**
     * 从服务器获取获取黑名单
     */
    public synchronized void asyncFetchBlacklistFromServer(final EMValueCallBack<List<String>> callBack) {


        if (isSyncingBlacklistWithServer) {
            return;
        }

        isSyncingBlacklistWithServer = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<String> usernames = null;
                try {

                    //获取联系人链表
                    usernames = EMContactManager.getInstance().getBlackListUsernamesFromServer();

                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    baseLogicModel.setBlacklistSync(true);

                    isSyncingBlacklistWithServer = false;
                    isBlacklistSyncedWithServer = true;

                    if (callBack != null) {
                        callBack.onSuccess(usernames);
                    }

                } catch (EaseMobException e) {

                    baseLogicModel.setBlacklistSync(false);
                    isSyncingBlacklistWithServer = false;
                    isBlacklistSyncedWithServer = false;

                    e.printStackTrace();

                    if (callBack != null) {
                        callBack.onError(e.getErrorCode(), e.toString());
                    }
                }
            }
        }).start();
    }

    public void notifyBlacklistSyncListener(boolean success) {
        for (ChatSyncListener item : syncBlacklistListener) {
            item.onSyncSuccess(success);
        }
    }

    public boolean isBlacklistSyncedWithServer() {
        return isBlacklistSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingBlacklistWithServer() {
        return isSyncingBlacklistWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    /**
     * 通知收到了新的消息
     */
    public synchronized void notifyForReceivedEvents() {

        if (alreadyNotified) {
            return;
        }

        //通知SDK,UI已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();

        alreadyNotified = true;
    }

    private synchronized void reset() {
        isContactsSyncedWithServer = false;
        isGroupsSyncedWithServer = false;
        isBlacklistSyncedWithServer = false;
        isSyncingBlacklistWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingGroupsWithServer = false;
        alreadyNotified = false;

        baseLogicModel.setContactSync(false);
        baseLogicModel.setGroupSync(false);
        baseLogicModel.setBlacklistSync(false);
    }

    protected BaseNotification.NotificationInfoProvider getNotificationProvider() {
        return null;
    }

    protected BaseNotification createNotification() {
        return new BaseNotification();
    }

    public BaseNotification getNotification() {
        return notification;
    }
}
