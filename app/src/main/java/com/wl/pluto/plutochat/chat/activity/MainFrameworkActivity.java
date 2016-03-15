package com.wl.pluto.plutochat.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.base.BaseHelper;
import com.wl.pluto.plutochat.chat.entity.InviteMessageEntity;
import com.wl.pluto.plutochat.chat.entity.UserEntity;
import com.wl.pluto.plutochat.chat.eventbus.BaseEvent;
import com.wl.pluto.plutochat.chat.eventbus.ChatConversationDeleteEvent;
import com.wl.pluto.plutochat.chat.eventbus.GestureEvent;
import com.wl.pluto.plutochat.chat.fragment.FrameworkChatFragment;
import com.wl.pluto.plutochat.chat.fragment.FrameworkContactsFragment;
import com.wl.pluto.plutochat.chat.fragment.FrameworkDiscoverFragment;
import com.wl.pluto.plutochat.chat.fragment.FrameworkMeFragment;
import com.wl.pluto.plutochat.chat.manager.ChatHelperManager;
import com.wl.pluto.plutochat.chat.widget.ViewFlipperEx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainFrameworkActivity extends BaseActivity implements EMEventListener {

    private static final String TAG = "--MainFrameworkActivity-->";
    private static final int FLAG_CHAT_NUM = 0;
    private static final int FLAG_CONTACTS_NUM = 1;
    private static final int FLAG_DISCOVER_NUM = 2;
    private static final int FLAG_ME_NUM = 3;

    private long lastPressBackTime = 0;

    /**
     * 最小滑动距离
     */
    private static final int MIN_TOUCH_DISTANCE = 100;

    /**
     * 从GestureListview传递过来的ｔｏｕｃｈ坐标
     */
    public static final String TOUCH_DOWN_X = "TOUCH_DOWN_X";

    /**
     * 从GestureListview传递过来的ｔｏｕｃｈ坐标
     */
    public static final String TOUCH_UP_X = "TOUCH_UP_X";

    /**
     * 标示广播事件的Ｉｎｔｅｎｔ动作字符串
     */
    public static final String BROAD_CAST_ACTION_GESTURE = "broad_cast_action_gesture";

    /**
     * 广播过滤器
     */
    private IntentFilter intentFilter = new IntentFilter(BROAD_CAST_ACTION_GESTURE);

    /**
     * 广播接受者
     */
    private MyBroadcastReceiver myBroadcastReceiver = null;

    /**
     * LocalBroadcastManager
     */
    private LocalBroadcastManager localBroadcastManager = null;

    /**
     * 手指按下屏幕的坐标
     */
    private int mTouchDownPosition;

    /**
     * 手指离开屏幕的坐标
     */
    private int mTouchUpPosition;

    /**
     * 主界面切换
     */
    private ViewFlipperEx mViewFlipper;

    /**
     * 　chat
     */
    private TextView mMainChatTextView;

    /**
     * chat 消息提示
     */
    private TextView mMainChatNotification;

    /**
     * contacts
     */
    private TextView mMainContactsTextView;

    /**
     * Contacts 消息提示
     */
    private ImageView mMainContactsNotification;

    /**
     * discover
     */
    private TextView mMainDiscoverTextView;

    /**
     * discover 消息提示
     */
    private ImageView mMainDiscoverNotification;

    /**
     * me
     */
    private TextView mMainMeTextView;

    /**
     * me 消息提示
     */
    private ImageView mMainMeNotification;

    /**
     * 每一个item 的选中标记
     */
    private boolean[] mItemSelectedFlag = {true, false, false, false};

    /**
     * 消息透传的广播
     */
    private CMDBroadcastReceiver cmdBroadcastReceiver;

    /**
     * mChatFragment
     */
    private FrameworkChatFragment mChatFragment;

    private FrameworkContactsFragment mContactsFragment;

    private FrameworkDiscoverFragment mDiscoverFragment;

    private FrameworkMeFragment mMeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_framework);
        initLayout();
        init();
    }


    private void init() {

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        myBroadcastReceiver = new MyBroadcastReceiver();
        //注册广播接受者
        localBroadcastManager.registerReceiver(myBroadcastReceiver, intentFilter);

        //AsyncHttpClientManager.getInstance().test();

        //注册一个EventBus
        EventBus.getDefault().register(this);

        //注册联系人变化的监听器
        EMContactManager.getInstance().setContactListener(new ContactsChangeListener());

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{
                EMNotifierEvent.Event.EventNewMessage,
                EMNotifierEvent.Event.EventOfflineMessage,
                EMNotifierEvent.Event.EventConversationListChanged});
    }

    @Override
    protected void onStop() {
        super.onStop();

        EMChatManager.getInstance().unregisterEventListener(this);
    }

    /**
     * 注册消息透传的监听器
     */
    private void registerCMDBroadcastReceiver() {
        // 注册一个cmd消息的BroadcastReceiver
        IntentFilter cmdIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());

        cmdBroadcastReceiver = new CMDBroadcastReceiver();
        registerReceiver(cmdBroadcastReceiver, cmdIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myBroadcastReceiver != null) {

            //注销广播接受者
            localBroadcastManager.unregisterReceiver(myBroadcastReceiver);
        }

        //
        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void onEventMainThread(BaseEvent event) {

        if (event instanceof GestureEvent) {

            onGestureDetectorHandle(((GestureEvent) event).getTouchDownX(), ((GestureEvent) event).getTouchUpX());
        } else if (event instanceof ChatConversationDeleteEvent) {

            FrameworkChatFragment fragment = (FrameworkChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);

            if (fragment != null) {
                fragment.refreshChatList();
            }
        }
    }

    private void initLayout() {

        mViewFlipper = (ViewFlipperEx) findViewById(R.id.vf_main_view_flipper);
        mViewFlipper.setMainFrameworkActivity(this);

        mMainChatTextView = (TextView) findViewById(R.id.tv_main_chat);
        mMainChatTextView.setOnClickListener(clickListener);
        mMainContactsTextView = (TextView) findViewById(R.id.tv_main_contacts);
        mMainContactsTextView.setOnClickListener(clickListener);
        mMainDiscoverTextView = (TextView) findViewById(R.id.tv_main_discover);
        mMainDiscoverTextView.setOnClickListener(clickListener);
        mMainMeTextView = (TextView) findViewById(R.id.tv_main_me);
        mMainMeTextView.setOnClickListener(clickListener);

        mMainChatNotification = (TextView) findViewById(R.id.tv_main_chat_notification_flag);
        mMainContactsNotification = (ImageView) findViewById(R.id.iv_main_contacts_notification_flag);
        mMainDiscoverNotification = (ImageView) findViewById(R.id.iv_main_discover_notification_flag);
        mMainMeNotification = (ImageView) findViewById(R.id.iv_main_me_notification_flag);

        initFragment();

        //默认第一个选中
        onChatClick();
    }

    private void initFragment() {
        mChatFragment = (FrameworkChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
        mContactsFragment = (FrameworkContactsFragment) getFragmentManager().findFragmentById(R.id.fragment_contacts);
        mDiscoverFragment = (FrameworkDiscoverFragment) getFragmentManager().findFragmentById(R.id.fragment_discover);
        mMeFragment = (FrameworkMeFragment) getFragmentManager().findFragmentById(R.id.fragment_me);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //先全部都设置成默认的
            setItemDefaultStatue(getSelectedItem());
            switch (v.getId()) {
                case R.id.tv_main_chat:
                    mViewFlipper.setDisplayedChild(FLAG_CHAT_NUM);
                    onChatClick();
                    break;
                case R.id.tv_main_contacts:
                    mViewFlipper.setDisplayedChild(FLAG_CONTACTS_NUM);
                    onContactsClick();
                    break;
                case R.id.tv_main_discover:
                    mViewFlipper.setDisplayedChild(FLAG_DISCOVER_NUM);
                    onDiscoverClick();
                    break;
                case R.id.tv_main_me:
                    mViewFlipper.setDisplayedChild(FLAG_ME_NUM);
                    onMeClick();
                    break;
            }

        }
    };

    private void onChatClick() {

        Log.i(TAG, "onChatClick");
        mMainChatTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.mipmap.bottom_bar_chat_p), null, null);
        mMainChatTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_selected));

        //设置item的选中标示
        setSelectedItem(FLAG_CHAT_NUM);

    }

    private void onContactsClick() {

        Log.i(TAG, "onContactsClick");

        mMainContactsTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.mipmap.bottom_bar_contacts_p), null, null);
        mMainContactsTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_selected));

        setSelectedItem(FLAG_CONTACTS_NUM);
    }

    private void onDiscoverClick() {

        Log.i(TAG, "onDiscoverClick");

        mMainDiscoverTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.mipmap.bottom_bar_discover_p), null, null);
        mMainDiscoverTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_selected));

        setSelectedItem(FLAG_DISCOVER_NUM);
    }

    private void onMeClick() {

        Log.i(TAG, "onMeClick");

        mMainMeTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.mipmap.bottom_bar_me_p), null, null);
        mMainMeTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_selected));

        setSelectedItem(FLAG_ME_NUM);
    }

    /**
     * 获取被标记的item的索引号
     *
     * @return
     */
    private int getSelectedItem() {

        for (int i = 0; i < 4; i++) {

            if (mItemSelectedFlag[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 设置item被选中的标记
     *
     * @param position
     */
    private void setSelectedItem(int position) {

        for (int j = 0; j < 4; j++) {
            if (position != j) {
                mItemSelectedFlag[j] = false;
            } else {
                mItemSelectedFlag[j] = true;
            }
        }
    }

    /**
     * 将之前选中的item设置成默认的颜色
     *
     * @param position
     */
    private void setItemDefaultStatue(int position) {

        switch (position) {
            case FLAG_CHAT_NUM:
                mMainChatTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.mipmap.bottom_bar_chat_n), null, null);
                mMainChatTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_default));
                break;
            case FLAG_CONTACTS_NUM:

                mMainContactsTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.mipmap.bottom_bar_contacts_n), null, null);
                mMainContactsTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_default));
                break;
            case FLAG_DISCOVER_NUM:

                mMainDiscoverTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.mipmap.bottom_bar_discover_n), null, null);
                mMainDiscoverTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_default));
                break;
            case FLAG_ME_NUM:

                mMainMeTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                        getResources().getDrawable(R.mipmap.bottom_bar_me_n), null, null);
                mMainMeTextView.setTextColor(getResources().getColor(R.color.main_bottom_bar_text_color_default));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_actions, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search:
                onMenuSearch();
                return true;
            case R.id.menu_setting:
                onMenuSetting();
                return true;
            case android.R.id.home:
                onLogoSelected();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void onMenuSearch() {

        Log.i(TAG, "onMenuSearch");
        forwardTargetActivity(AddContactActivity.class);
    }

    private void onMenuSetting() {

        Log.i(TAG, "onMenuSetting");
    }

    private void onLogoSelected() {
        Toast.makeText(this, "Logo is clicked !", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//                mTouchDownPosition = (int) event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//
//                mTouchUpPosition = (int) event.getX();
//                onGestureDetectorHandle(mTouchDownPosition, mTouchUpPosition);
//                break;
//        }
//
//        return true;
//    }

    public void onGestureDetectorHandle(final int touchDownX, final int touchUpX) {

        if ((touchDownX - touchUpX) > MIN_TOUCH_DISTANCE) {
            Log.i(TAG, "向左划");

            //从右进来
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));

            //从左出去
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_from_left));

            mViewFlipper.showNext();

        } else if ((touchUpX - touchDownX) > MIN_TOUCH_DISTANCE) {
            Log.i(TAG, "向右划");

            //从左进来
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left));

            //从右出去
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_from_right));

            mViewFlipper.showPrevious();
        }
        Log.i(TAG, "当前位置" + mViewFlipper.getDisplayedChild());
        setItemOnClick(mViewFlipper.getDisplayedChild());
    }


    /**
     * 在左右滑动的时候，同时将底部的item同步变化
     *
     * @param position
     */
    private void setItemOnClick(int position) {

        //先全部都设置成默认的
        setItemDefaultStatue(getSelectedItem());

        switch (position) {
            case FLAG_CHAT_NUM:
                onChatClick();
                break;
            case FLAG_CONTACTS_NUM:
                onContactsClick();
                break;
            case FLAG_DISCOVER_NUM:
                onDiscoverClick();
                break;
            case FLAG_ME_NUM:
                onMeClick();
                break;
        }

    }

    /**
     * 事件监听函数，主要用于处理全局消息
     *
     * @param emNotifierEvent
     */
    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {

        switch (emNotifierEvent.getEvent()) {

            //新消息
            case EventNewMessage:

                EMMessage message = (EMMessage) emNotifierEvent.getData();

                Log.i(TAG, "onEvent EventNewMessage");
                BaseHelper.getInstance().getNotification().onNewMessage(message);

                //提示有消息到来
                refreshMainFrameworkUI();
                break;

            //离线消息
            case EventOfflineMessage:
                break;

            //会话链表发生改变
            case EventConversationListChanged:
                break;


        }
    }

    /**
     * 刷新主界面
     */
    private void refreshMainFrameworkUI() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //这里只是刷新了主界面下面的Chat提示，
                updateChatItemUI();

                //TODO ChatFragment上面那个联系人发来信息，这个也需要提示
                if (mChatFragment != null) {

                    mChatFragment.refreshChatList();
                }
            }
        });
    }

    /**
     * 刷新聊天选项的消息提示
     */
    private void updateChatItemUI() {

        int conversationCounts = getUnreadMessageCounts();
        if (conversationCounts > 0) {
            mMainChatNotification.setText(String.valueOf(conversationCounts));
            mMainChatNotification.setVisibility(View.VISIBLE);
        } else {
            mMainChatNotification.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    private int getUnreadMessageCounts() {
        int unreadMessageCountTotal;
        int chatRoomUnreadMessageCount = 0;

        unreadMessageCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation item : EMChatManager.getInstance().getAllConversations().values()) {
            if (item.getType() == EMConversation.EMConversationType.ChatRoom) {
                chatRoomUnreadMessageCount += item.getUnreadMsgCount();
            }
        }
        return unreadMessageCountTotal - chatRoomUnreadMessageCount;
    }

    /**
     * 接受滑动屏幕事件的BroadcastReceiver
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //从intent 中获取数据
            Uri data = intent.getData();

            int touchDownX = intent.getIntExtra(TOUCH_DOWN_X, 0);

            int touchUpX = intent.getIntExtra(TOUCH_UP_X, 0);


            if (touchDownX != 0 && touchUpX != 0) {

                onGestureDetectorHandle(touchDownX, touchUpX);
            }
        }
    }

    /**
     * 联系人变化listener
     */
    private class ContactsChangeListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> list) {

            // 添加了联系人时调用该方法
            Log.i(TAG, "onContactAdded");

            for (String item : list) {
                Log.i(TAG, item);
            }
            //保存增加的联系人
            Map<String, UserEntity> localUsers = ((ChatHelperManager) BaseHelper.getInstance()).getContactsList();

            Map<String, UserEntity> toAddUsers = new HashMap<>();

        }

        @Override
        public void onContactDeleted(List<String> list) {

            //被删除时回调该方法
            Log.i(TAG, "onContactDeleted");
        }

        @Override
        public void onContactInvited(String username, String reason) {

            //收到好友邀请
            Log.i(TAG, "onContactInvited");

            InviteMessageEntity inviteMessage = new InviteMessageEntity();
            inviteMessage.setFrom(username);
            inviteMessage.setTime(System.currentTimeMillis());
            inviteMessage.setReason(reason);
            Log.i(TAG, username + "请求加你为好友, reason:" + reason);
            inviteMessage.setStatus(InviteMessageEntity.InviteMessageStatus.BE_INVITED);
            notifyNewInviteMessage(inviteMessage);

        }

        @Override
        public void onContactAgreed(String s) {

            //好友请求被同意
            Log.i(TAG, "onContactAgreed");
        }

        @Override
        public void onContactRefused(String s) {

            //好友请求被拒绝
            Log.i(TAG, "onContactRefused");
        }
    }

    private void notifyNewInviteMessage(InviteMessageEntity inviteMessage) {

        //先要保存消息
        //好友请求被拒绝
        Log.i(TAG, "notifyNewInviteMessage");

        //提示有新消息
        BaseHelper.getInstance().getNotification().vibrateAndPlayTone(null);
    }

    /**
     * 接受透传消息的广播接受器
     */
    private class CMDBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //获取CMD消息对象
            String msgId = intent.getStringExtra("msgid");

            EMMessage message = intent.getParcelableExtra("message");

            //获取消息body
            CmdMessageBody messageBody = (CmdMessageBody) message.getBody();

            //获取自定义的action
            String action = messageBody.action;

            //获取扩展的属性
            try {
                String attr = message.getStringAttribute("a");
            } catch (EaseMobException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "action =" + action);
        }
    }

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPressBackTime < 2500) {
            super.onBackPressed();
        } else {
            lastPressBackTime = currentTime;
            toast(R.string.text_exit_app);
        }
    }
}
