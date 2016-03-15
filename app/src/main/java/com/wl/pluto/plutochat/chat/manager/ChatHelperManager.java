package com.wl.pluto.plutochat.chat.manager;

import android.content.Context;
import android.content.Intent;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.wl.pluto.plutochat.chat.base.BaseHelper;
import com.wl.pluto.plutochat.chat.base.BaseLogicModel;
import com.wl.pluto.plutochat.chat.base.BaseNotification;
import com.wl.pluto.plutochat.chat.entity.UserEntity;
import com.wl.pluto.plutochat.chat.logger.Log;
import com.wl.pluto.plutochat.chat.model.DefaultLogicModel;
import com.wl.pluto.plutochat.chat.utils.AppUtils;

import java.util.Map;

/**
 * 该类主要就是用来管理整个App的消息处理的
 * <p/>
 * Created by jeck on 15-11-20.
 */
public class ChatHelperManager extends BaseHelper {

    private static final String TAG = "--ChatHelperManager-->";

    private EMEventListener eventListener;

    /**
     * 联系人Cache
     */
    private Map<String, UserEntity> contactsList;

    @Override
    public synchronized boolean onInit(Context context) {
        return super.onInit(context);
    }

    @Override
    protected void initChatOptions() {
        super.initChatOptions();
    }

    @Override
    protected void initListener() {
        super.initListener();

        initEventListener();
    }

    /**
     *
     */
    public void initEventListener() {

        Log.i(TAG, "initEventListener");

        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent emNotifierEvent) {

                EMMessage message = null;
                if (emNotifierEvent.getData() instanceof EMMessage) {
                    message = (EMMessage) emNotifierEvent.getData();
                }

                switch (emNotifierEvent.getEvent()) {
                    case EventNewMessage:

                        //如果当前应用程序处于后台，那就需要调用Notification来通知用户有新消息到来
                        if (AppUtils.isBackground(context)) {
                            ChatHelperManager.getInstance().getNotification().onNewMessage(message);
                        }
                        break;
                }

            }
        });
    }

    public ChatHelperManager() {
        super();
    }

    @Override
    public BaseLogicModel crateBaseLogicModel() {
        return new DefaultLogicModel(context);
    }

    @Override
    protected BaseNotification.NotificationInfoProvider getNotificationProvider() {
        return new BaseNotification.NotificationInfoProvider() {
            @Override
            public String getDisplayedText(EMMessage message) {
                return null;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUserNum, int messageNum) {
                return null;
            }

            @Override
            public String getNotificationTitle(EMMessage message) {
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                return 0;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                return null;
            }
        };
    }

    @Override
    protected BaseNotification createNotification() {

        return new BaseNotification() {

            @Override
            public synchronized void onNewMessage(EMMessage message) {
                super.onNewMessage(message);
            }
        };
    }

    public Map<String, UserEntity> getContactsList() {

        if (getChatId() != null && contactsList == null) {
            //contactsList = ((DefaultLogicModel)getBaseLogicModel()).getChatID();
        }
        return contactsList;
    }

    public void setContactsList(Map<String, UserEntity> contactsList) {
        this.contactsList = contactsList;
    }
}
