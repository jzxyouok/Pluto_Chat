package com.wl.pluto.plutochat.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.adapter.ChatMessageAdapter;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.common_interface.OnExpressionPressListener;
import com.wl.pluto.plutochat.constant.CommonConstant;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.utils.ExpressionUtils;
import com.wl.pluto.plutochat.utils.KeyBoardUtils;
import com.wl.pluto.plutochat.utils.SDCardUtils;
import com.wl.pluto.plutochat.widget.ViewFlipperEx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ChatActivity extends BaseActivity implements EMEventListener, View.OnTouchListener,
        OnExpressionPressListener, ChatMessageAdapter.OnRefreshChatMessageListListener {

    private static final String TAG = "--ChatActivity-->";

    /**
     * 选择图片的请求码
     */
    private static final int REQUEST_CODE_CHOOSE_PICTURE = 1001;

    /**
     * 获取位置的请求码
     */
    private static final int REQUEST_CODE_FETCH_LOCATION = 1002;

    /**
     * 选择视频的请求码
     */
    private static final int REQUEST_CODE_CHOOSE_VIDEO = 1003;

    /**
     * 回话对象
     */
    private EMConversation mConversation;

    /**
     * 发送消息对象的名称
     */
    private String mToChatUserNameValue = null;

    /**
     * 发送消息对象的昵称
     */
    private String mToChatUserNickNameValue = null;

    /**
     * 发送消息对象的头像地址
     */
    private String mToChatUserHeadImageUrlValue = null;

    /**
     * 返回
     */
    private ImageView mGoBackImageView;

    /**
     * 聊天的对象
     */
    private TextView mToUserNameTextView;

    /**
     * 右上角聊天对象的头像
     */
    private ImageView mChatFriendHeadImageView;

    /**
     * 聊天链表
     */
    private ListView mChatMessageListView;

    /**
     * 聊天适配器
     */
    private ChatMessageAdapter mChatMessageAdapter;

    /**
     * 左下角的声音按钮，点击可以显示Press to Speak
     */
    private ImageView mChatModeVoiceOrKeyboard;

    /**
     * 按住可以发送语言
     */
    private TextView mPressToSpeakTextView;

    /**
     * 语音显示布局
     */
    private RelativeLayout mMicRecordContainerLayout;

    /**
     * 说话时跳动的麦克风
     */
    private ImageView mMicRecordImageView;

    /**
     * 上滑取消发送的提示
     */
    private TextView mMicRecordTextView;

    /**
     * 输入框布局
     */
    private RelativeLayout mMessageEditLayout;

    /**
     * 输入框
     */
    private EditText mMessageEditText;

    /**
     * 表情按钮
     */
    private ImageView mMessageExpressionImageView;

    /**
     * 更多
     */
    private ImageView mMessageMoreImageView;

    /**
     * 消息发送按钮发送按钮
     */
    private TextView mMessageSendTextView;

    /**
     * Capture
     */
    private TextView mSelectCaptureTextView;

    /**
     * Image
     */
    private TextView mSelectImageTextView;

    /**
     * Location
     */
    private TextView mSelectLocationTextView;

    /**
     * Video
     */
    private TextView mSelectVideoTextView;

    /**
     * File
     */
    private TextView mSelectFileTextView;

    /**
     * Voice Call
     */
    private TextView mSelectVoiceCallTextView;

    /**
     * Video Call
     */
    private TextView mSelectVideoCallTextView;

    /**
     * 更多功能
     */
    private GridLayout mSelectMoreGridLayout;

    /**
     * 语音和文本模式之间的切换
     */
    private boolean isModeVoiceShow = true;

    /**
     * 表情布局
     */
    private ViewFlipperEx mExpressionViewFlipper;

    /**
     * 最小滑动距离
     */
    private static final int MIN_TOUCH_DISTANCE = 100;

    /**
     * 手指按下屏幕的坐标
     */
    private float mTouchDownPosition;

    /**
     * 手指离开屏幕的坐标
     */
    private float mTouchUpPosition;

    /**
     * 删除表情
     */
    private static final String DELETE_EXPRESSION = "delete_expression";

    /**
     * 单聊
     */
    private static final int CHAT_TYPE_SINGLE = 1;

    /**
     * 聊天类型
     */
    public static final String CHAT_TYPE = "chat_type";

    /**
     * 聊天类型
     */
    public int mChatType;

    /**
     * 每一页中消息的最大数量
     */
    public static final int PAGE_MESSAGE_COUNTS = 20;

    /**
     * 说话时的麦克风动画
     */
    private Drawable[] micImages;

    /**
     * 语音记录
     */
    private VoiceRecorder mVoiceRecorder;

    /**
     *
     */
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        onInitIntent();
        onInitConversation();
        initLayout();

    }

    @Override
    public void onResume() {
        super.onResume();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{
                EMNotifierEvent.Event.EventNewMessage,
                EMNotifierEvent.Event.EventOfflineMessage,
                EMNotifierEvent.Event.EventDeliveryAck,
                EMNotifierEvent.Event.EventReadAck});

        if (mChatMessageAdapter != null) {
            mChatMessageAdapter.refreshChatMessageList();
            mChatMessageListView.setSelection(mChatMessageListView.getCount() - 1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyBoardUtils.closeKeybord(mMessageEditText, this);
    }

    private void onInitConversation() {

        mConversation = EMChatManager.getInstance().getConversationByType(mToChatUserNameValue,
                EMConversation.EMConversationType.Chat);

        //将所有的消息设置为已读
        mConversation.markAllMessagesAsRead();

        final List<EMMessage> messages = mConversation.getAllMessages();
        int messageCounts = messages != null ? messages.size() : 0;

        if (messageCounts < mConversation.getAllMsgCount() && messageCounts < PAGE_MESSAGE_COUNTS) {
            String messageId = null;
            if (messages != null && messages.size() > 0) {
                messageId = messages.get(0).getMsgId();
            }

            mConversation.loadMoreMsgFromDB(messageId, PAGE_MESSAGE_COUNTS);
        }
    }

    private void onInitIntent() {


        Intent intent = getIntent();
        if (intent != null) {

            mToChatUserNameValue = intent.getStringExtra(UserConstant.CHAT_USER_NAME_KEY);
            mToChatUserNickNameValue = intent.getStringExtra(UserConstant.CHAT_USER_NICK_NAME_KEY);
            mToChatUserHeadImageUrlValue = intent.getStringExtra(UserConstant.CHAT_USER_HEAD_IMAGE_URL_KEY);

            mChatType = intent.getIntExtra(CHAT_TYPE, CHAT_TYPE_SINGLE);
        }

        mChatMessageAdapter = new ChatMessageAdapter(this, mToChatUserNameValue, mToChatUserHeadImageUrlValue);
        mChatMessageAdapter.setOnRefreshChatMessageListListener(this);
    }

    private void initLayout() {

        mGoBackImageView = (ImageView) findViewById(R.id.iv_chat_top_bar_back);
        mGoBackImageView.setOnClickListener(clickListener);

        mToUserNameTextView = (TextView) findViewById(R.id.tv_chat_top_bar_username);
        if (mToChatUserNickNameValue != null) {
            mToUserNameTextView.setText(mToChatUserNickNameValue);
        } else {
            mToUserNameTextView.setText(mToChatUserNameValue);
        }


        mChatFriendHeadImageView = (ImageView) findViewById(R.id.iv_chat_top_bar_user_head);
        mChatFriendHeadImageView.setOnClickListener(clickListener);

        mChatMessageListView = (ListView) findViewById(R.id.lv_chat_content_list);
        mChatMessageListView.setAdapter(mChatMessageAdapter);

        mChatModeVoiceOrKeyboard = (ImageView) findViewById(R.id.iv_chat_set_mode_voice_or_keyboard);
        mChatModeVoiceOrKeyboard.setOnClickListener(clickListener);

        mPressToSpeakTextView = (TextView) findViewById(R.id.tv_chat_press_to_speak);
        mPressToSpeakTextView.setOnTouchListener(touchListener);

        mMessageEditLayout = (RelativeLayout) findViewById(R.id.rl_message_edit_layout);

        mMessageExpressionImageView = (ImageView) findViewById(R.id.iv_message_expression_image_view);
        mMessageExpressionImageView.setOnClickListener(clickListener);

        mMessageEditText = (EditText) findViewById(R.id.et_message_edit_text);

        //监听文字输入框
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s)) {
                    mMessageSendTextView.setVisibility(View.GONE);
                    mMessageMoreImageView.setVisibility(View.VISIBLE);
                } else {
                    mMessageMoreImageView.setVisibility(View.GONE);
                    mMessageSendTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMessageEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMessageEditText.requestFocusFromTouch()) {

                    //隐藏更多
                    if (mSelectMoreGridLayout.getVisibility() == View.VISIBLE) {
                        mSelectMoreGridLayout.setVisibility(View.GONE);
                    }

                    //隐藏表情
                    if (mExpressionViewFlipper.getVisibility() == View.VISIBLE) {
                        mExpressionViewFlipper.setVisibility(View.GONE);
                    }

                    //将表情按钮设置为默认
                    mMessageExpressionImageView.setImageResource(R.mipmap.chat_expression_n);

                    //将聊天链表显示最后一条信息
                    mChatMessageListView.setSelection(mChatMessageListView.getCount() - 1);
                }
                return false;
            }
        });


        mExpressionViewFlipper = (ViewFlipperEx) findViewById(R.id.vf_expression_layout);
        mExpressionViewFlipper.setOnTouchListener(this);

        mSelectMoreGridLayout = (GridLayout) findViewById(R.id.gl_select_more_layout);

        mMessageMoreImageView = (ImageView) findViewById(R.id.iv_chat_message_more);
        mMessageMoreImageView.setOnClickListener(clickListener);

        mMessageSendTextView = (TextView) findViewById(R.id.tv_chat_send_message);
        mMessageSendTextView.setOnClickListener(clickListener);

        mSelectCaptureTextView = (TextView) findViewById(R.id.tv_chat_select_capture);
        mSelectCaptureTextView.setOnClickListener(clickListener);

        mSelectImageTextView = (TextView) findViewById(R.id.tv_chat_select_image);
        mSelectImageTextView.setOnClickListener(clickListener);

        mSelectLocationTextView = (TextView) findViewById(R.id.tv_chat_select_location);
        mSelectLocationTextView.setOnClickListener(clickListener);

        mSelectVideoTextView = (TextView) findViewById(R.id.tv_chat_select_video);
        mSelectVideoTextView.setOnClickListener(clickListener);

        mSelectFileTextView = (TextView) findViewById(R.id.tv_chat_select_file);
        mSelectFileTextView.setOnClickListener(clickListener);

        mSelectVoiceCallTextView = (TextView) findViewById(R.id.tv_chat_select_voice_call);
        mSelectVoiceCallTextView.setOnClickListener(clickListener);

        mSelectVideoCallTextView = (TextView) findViewById(R.id.tv_chat_select_video_call);
        mSelectVideoCallTextView.setOnClickListener(clickListener);

        //绑定上下文菜单
        registerForContextMenu(mChatMessageListView);

        mMicRecordContainerLayout = (RelativeLayout) findViewById(R.id.rl_chat_recording_container);
        mMicRecordImageView = (ImageView) findViewById(R.id.iv_chat_mic_image);
        mMicRecordTextView = (TextView) findViewById(R.id.tv_chat_mic_hint);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{
                getResources().getDrawable(R.mipmap.record_animate_01),
                getResources().getDrawable(R.mipmap.record_animate_02),
                getResources().getDrawable(R.mipmap.record_animate_03),
                getResources().getDrawable(R.mipmap.record_animate_04),
                getResources().getDrawable(R.mipmap.record_animate_05),
                getResources().getDrawable(R.mipmap.record_animate_06),
                getResources().getDrawable(R.mipmap.record_animate_07),
                getResources().getDrawable(R.mipmap.record_animate_08),
                getResources().getDrawable(R.mipmap.record_animate_09),
                getResources().getDrawable(R.mipmap.record_animate_10),
                getResources().getDrawable(R.mipmap.record_animate_11),
                getResources().getDrawable(R.mipmap.record_animate_12),
                getResources().getDrawable(R.mipmap.record_animate_13),
                getResources().getDrawable(R.mipmap.record_animate_14)};


        mVoiceRecorder = new VoiceRecorder(new MicHandler());

        //获取系统的电源管理
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //使用锁来确保屏幕是常亮的
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
    }

    private class MicHandler extends Handler {

        public MicHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            mMicRecordImageView.setImageDrawable(micImages[msg.what]);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_chat_top_bar_back:
                    onGoBackClick();
                    break;
                case R.id.iv_chat_top_bar_user_head:
                    onChatFriendHeadClick();
                    break;
                case R.id.iv_chat_set_mode_voice_or_keyboard:
                    onModeVoiceClick();
                    break;
                case R.id.iv_message_expression_image_view:
                    onExpressionImageViewClick();
                    break;
                case R.id.iv_chat_message_more:
                    onMessageMoreClick();
                    break;

                case R.id.tv_chat_send_message:
                    onMessageSendClick();
                    break;
                case R.id.tv_chat_select_capture:
                    onChatCaptureClick();
                    break;
                case R.id.tv_chat_select_image:
                    onChatImageClick();
                    break;
                case R.id.tv_chat_select_location:
                    onChatLocationClick();
                    break;
                case R.id.tv_chat_select_video:
                    onChatVideoClick();
                    break;
                case R.id.tv_chat_select_file:
                    onChatFileClick();
                    break;
                case R.id.tv_chat_select_voice_call:
                    onChatVoiceCallClick();
                    break;
                case R.id.tv_chat_select_video_call:
                    onChatVideoCallClick();
                    break;
            }
        }
    };

    /**
     * 返回
     */
    private void onGoBackClick() {

    }

    /**
     * 查看聊天对象的信息
     */
    private void onChatFriendHeadClick() {

    }

    /**
     * 点击mode voice
     */
    private void onModeVoiceClick() {


        //如果当前是Voice,则显示keyboard
        if (isModeVoiceShow) {

            mChatModeVoiceOrKeyboard.setImageResource(R.drawable.chat_set_mode_keyboard_background);
            mMessageEditLayout.setVisibility(View.GONE);
            mPressToSpeakTextView.setVisibility(View.VISIBLE);

            KeyBoardUtils.closeKeybord(mMessageEditText, this);
        } else {

            mChatModeVoiceOrKeyboard.setImageResource(R.drawable.chat_set_mode_voice_background);
            mMessageEditLayout.setVisibility(View.VISIBLE);
            mPressToSpeakTextView.setVisibility(View.GONE);

            KeyBoardUtils.openKeybord(mMessageEditText, this);
        }

        isModeVoiceShow = !isModeVoiceShow;
    }

    /**
     * 点击表情按钮
     */
    private void onExpressionImageViewClick() {

        //先判断自己是否显示， 如果自己显示，那显示键盘
        if (mExpressionViewFlipper.getVisibility() == View.VISIBLE) {
            mExpressionViewFlipper.setVisibility(View.GONE);
            KeyBoardUtils.openKeybord(mMessageEditText, this);
            mMessageExpressionImageView.setImageResource(R.mipmap.chat_expression_n);

        } else {

            //如果自己没显示，再判断是不是more在显示，如果更多在显示，先将其隐藏
            if (mSelectMoreGridLayout.getVisibility() == View.VISIBLE) {
                mSelectMoreGridLayout.setVisibility(View.GONE);
            }

            KeyBoardUtils.closeKeybord(mMessageEditText, this);
            mExpressionViewFlipper.setVisibility(View.VISIBLE);
            mMessageExpressionImageView.setImageResource(R.mipmap.chat_expression_p);

        }
    }

    /**
     * 按住说话
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if (!SDCardUtils.isSDCardEnable()) {
                        toast(R.string.text_notify_sdcard_is_not_avalible);
                        return false;
                    } else {

                        try {
                            v.setPressed(true);
                            mWakeLock.acquire();
                            showRecordContainer();
                            mVoiceRecorder.startRecording(null, mToChatUserNameValue, getApplicationContext());
                            return true;

                        } catch (Exception e) {
                            e.printStackTrace();
                            v.setPressed(false);
                            if (mWakeLock.isHeld()) {
                                mWakeLock.release();
                            }

                            if (mVoiceRecorder != null) {
                                mVoiceRecorder.discardRecording();
                            }
                            hideRecordContainer();
                            toast(R.string.text_error_recording_failed);
                            return false;
                        }
                    }
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    hideRecordContainer();

                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }

                    if (event.getY() < 0) {
                        mVoiceRecorder.discardRecording();
                    } else {

                        try {

                            int recordLength = mVoiceRecorder.stopRecoding();

                            if (recordLength > 0) {
                                sendVoiceRecord(mVoiceRecorder.getVoiceFilePath(), recordLength);
                            } else {
                                toast(R.string.text_error_recording_failed);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            return false;
        }
    };

    /**
     * 发送语音
     */
    private void sendVoiceRecord(String filePath, int voiceLength) {

        if (new File(filePath).exists()) {

            try {

                final EMMessage voiceMessage = EMMessage.createSendMessage(EMMessage.Type.VOICE);
                voiceMessage.setReceipt(mToChatUserNameValue);
                VoiceMessageBody messageBody = new VoiceMessageBody(new File(filePath), voiceLength);
                voiceMessage.addBody(messageBody);
                mConversation.addMessage(voiceMessage);
                mChatMessageAdapter.refreshChatMessageList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    private void showRecordContainer() {
        if (mMicRecordContainerLayout.getVisibility() != View.VISIBLE) {
            mMicRecordContainerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideRecordContainer() {
        mMicRecordContainerLayout.setVisibility(View.GONE);
    }

    /**
     * 点击更多
     */
    private void onMessageMoreClick() {

        //如果当前是显示，则隐藏
        if (mSelectMoreGridLayout.getVisibility() == View.VISIBLE) {

            mSelectMoreGridLayout.setVisibility(View.GONE);
            KeyBoardUtils.openKeybord(mMessageEditText, this);
        } else {

            if (mExpressionViewFlipper.getVisibility() == View.VISIBLE) {
                mExpressionViewFlipper.setVisibility(View.GONE);
            }
            KeyBoardUtils.closeKeybord(mMessageEditText, this);
            mSelectMoreGridLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击者发送文本消息
     */
    private void onMessageSendClick() {

        String textContent = mMessageEditText.getText().toString();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

        TextMessageBody textMessageBody = new TextMessageBody(textContent);

        //添加消息体
        message.addBody(textMessageBody);

        //设置发送对象
        message.setReceipt(mToChatUserNameValue);

        //将message添加到会话中
        mConversation.addMessage(message);

        //通知MessageAdapter数据源有变动，同时调用SDK发送消息
        mChatMessageAdapter.refreshChatMessageList();

        mMessageEditText.setText("");
    }

    /**
     * 点击Capture
     */
    private void onChatCaptureClick() {

    }

    /**
     * 点击Image
     */
    private void onChatImageClick() {

        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, REQUEST_CODE_CHOOSE_PICTURE);
    }

    /**
     * 点击Location
     */
    private void onChatLocationClick() {

        Intent locationIntent = new Intent(this, LocationActivity.class);
        startActivityForResult(locationIntent, REQUEST_CODE_FETCH_LOCATION);
    }

    /**
     * 点击发送视频
     */
    private void onChatVideoClick() {

        Intent intent = new Intent(this, ChooseVideoActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_VIDEO);
    }

    /**
     * 点击发送文件
     */
    private void onChatFileClick() {

    }

    /**
     * 点击开启语音聊天
     */
    private void onChatVoiceCallClick() {

    }

    /**
     * 点击开启视频聊天
     */
    private void onChatVideoCallClick() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PICTURE:
                handleChoosePicture(data);
                break;
            case REQUEST_CODE_FETCH_LOCATION:
                handleFetchLocation(data);
                break;
            case REQUEST_CODE_CHOOSE_VIDEO:
                handleChooseVideo(data);
                break;
        }
    }

    /**
     * @param data
     */
    private void handleChooseVideo(final Intent data) {

        if (data != null) {

            String videoPath = data.getStringExtra(CommonConstant.VIDEO_FILE_PATH_KEY);
            long videoDuration = data.getLongExtra(CommonConstant.VIDEO_DURATION_KEY, 0);

            File videoThumbnailFile = new File(PathUtil.getInstance().getImagePath(),
                    "videoThumbnail" + System.currentTimeMillis());

            Bitmap bitmap = null;

            FileOutputStream fileOutputStream = null;
            try {

                if (!videoThumbnailFile.getParentFile().exists()) {
                    videoThumbnailFile.getParentFile().mkdirs();
                }

                //调用系统的创建视频缩略图的函数，３标示视频的第三帧
                bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);

                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                //输出流
                fileOutputStream = new FileOutputStream(videoThumbnailFile);

                //将bitmap写入到指定的文件中
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }
            }

            sendVideoMessage(videoPath, videoThumbnailFile.getAbsolutePath(), videoDuration);
        }
    }

    /**
     * 发送视频消息
     */
    private void sendVideoMessage(final String videoPath, final String videoThumbnailPath, final long duration) {

        File videoFile = new File(videoPath);

        if (videoFile.exists()) {

            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            VideoMessageBody messageBody = new VideoMessageBody(videoFile, videoThumbnailPath, (int) duration, videoPath.length());
            message.addBody(messageBody);
            message.setReceipt(mToChatUserNameValue);
            mConversation.addMessage(message);
            mChatMessageAdapter.refreshChatMessageList();
        }
    }

    /**
     * 处理获取的位置
     */
    private void handleFetchLocation(final Intent data) {

        if (data != null) {
            double locationLatitude = data.getDoubleExtra(CommonConstant.LOCATION_LATITUDE_KEY, 0);
            double locationLongitude = data.getDoubleExtra(CommonConstant.LOCATION_LONGITUDE_KEY, 0);
            String locationAddress = data.getStringExtra(CommonConstant.LOCATION_ADDRESS_KEY);

            if (!TextUtils.isEmpty(locationAddress)) {

                sendLocationMessage(locationLatitude, locationLongitude, locationAddress);
            }
        }
    }

    /**
     * 发送位置消息
     */
    private void sendLocationMessage(double latitude, double longitude, String address) {

        EMMessage locationMessage = EMMessage.createLocationSendMessage(latitude, longitude, address, mToChatUserNameValue);
        mConversation.addMessage(locationMessage);
        mChatMessageAdapter.refreshChatMessageList();
    }

    /**
     * 处理选择的图片
     */
    private void handleChoosePicture(final Intent data) {

        if (data != null) {
            Uri selectedPictureUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedPictureUri, filePathColumn, null, null, null, null);
            if (cursor != null) {

                cursor.moveToFirst();
                String picturePath = cursor.getString(cursor.getColumnIndexOrThrow(filePathColumn[0]));

                cursor.close();
                if (picturePath == null || picturePath.equals("null")) {
                    Toast toast = Toast.makeText(this, R.string.error_text_file_not_found, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {

                    //发送图片
                    sendPictureByPath(picturePath);
                }
            } else {
                File file = new File(selectedPictureUri.getPath());
                if (!file.exists()) {

                    Toast toast = Toast.makeText(this, R.string.error_text_file_not_found, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    sendPictureByPath(file.getAbsolutePath());
                }
            }

        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPictureByPath(final String filePath) {

        //创建一个message
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.IMAGE);

        //设置发送对象
        sendMessage.setReceipt(mToChatUserNameValue);

        //创建消息体
        ImageMessageBody imageMessageBody = new ImageMessageBody(new File(filePath));

        sendMessage.addBody(imageMessageBody);

        mConversation.addMessage(sendMessage);

        mChatMessageAdapter.refreshChatMessageList();
    }

    /**
     * 消息监听
     *
     * @param
     */
    @Override
    public void onEvent(EMNotifierEvent event) {

        switch (event.getEvent()) {

            case EventNewMessage: {

                Log.i(TAG, "received a new message !");
                //获取消息
                EMMessage message = (EMMessage) event.getData();
                String userName = null;

                //如果是群聊
                if (message.getChatType() == EMMessage.ChatType.ChatRoom
                        || message.getChatType() == EMMessage.ChatType.GroupChat) {

                    userName = message.getTo();
                } else {
                    userName = message.getFrom();
                }

                //如果是当前会话的消息
                if (userName.equals(mToChatUserNameValue)) {
                    refreshUIWithNewMessage();
                }
                break;
            }
            case EventDeliveryAck: {
                break;
            }

            case EventReadAck: {
                break;
            }
            case EventOfflineMessage: {
                break;
            }
        }
    }

    /**
     * 刷新当前聊天界面
     */
    private void refreshUIWithNewMessage() {

        if (mChatMessageAdapter != null) {

            mChatMessageAdapter.refreshChatMessageList();
        }

        if (mChatMessageListView != null) {
            mChatMessageListView.setSelection(mConversation.getAllMsgCount() - 1);
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownPosition = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP: {
                mTouchUpPosition = event.getX();

                //向左滑
                if ((mTouchDownPosition - mTouchUpPosition) > MIN_TOUCH_DISTANCE) {

                    //从左出去
                    mExpressionViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_from_left));

                    //从右进来
                    mExpressionViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right));

                    mExpressionViewFlipper.showNext();
                }

                //向右滑
                if ((mTouchUpPosition - mTouchDownPosition) > MIN_TOUCH_DISTANCE) {

                    //从右出去
                    mExpressionViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_from_right));

                    //从左进来
                    mExpressionViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left));

                    mExpressionViewFlipper.showPrevious();
                }
                break;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onExpressionPress(String fileName) {

        // 文字输入框可见时，才可输入表情.按住说话可见，不让输入表情
        if (mExpressionViewFlipper.getVisibility() == View.VISIBLE) {

            //如果不是删除按钮,那就是添加表情撒
            if (!DELETE_EXPRESSION.endsWith(fileName)) {

                try {
                    Class expressionClass = Class.forName("com.wl.pluto.plutochat.utils.ExpressionUtils");
                    Field field = expressionClass.getField(fileName);

                    mMessageEditText.append(ExpressionUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }


                //删除表情或者文字
            } else {

                if (!TextUtils.isEmpty(mMessageEditText.getText().toString())) {

                    //获取光标的位置
                    int selectionStart = mMessageEditText.getSelectionStart();

                    if (selectionStart > 0) {
                        String body = mMessageEditText.getText().toString();
                        String temp = body.substring(0, selectionStart);

                        //获取最后一个表情的位置
                        int i = temp.lastIndexOf("[");

                        if (i != -1) {
                            CharSequence charSequence = temp.subSequence(i, selectionStart);
                            if (ExpressionUtils.containsKey(charSequence.toString())) {

                                mMessageEditText.getEditableText().delete(i, selectionStart);
                            } else {
                                mMessageEditText.getEditableText().delete(selectionStart - 1, selectionStart);
                            }
                        } else {
                            mMessageEditText.getEditableText().delete(selectionStart - 1, selectionStart);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRefreshChatMessageList(final int listPosition) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mChatMessageListView != null) {
                    Log.i(TAG, "messageSize=" + listPosition);
                    Log.i(TAG, "listSize=" + mChatMessageListView.getCount());
                    mChatMessageListView.setSelection(listPosition);
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_activity_context_menu, menu);
        Log.i(TAG, "onCreateContextMenu");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //这是回话(ChatActivity)链表的数据源
        switch (item.getItemId()) {

            case R.id.menu_chat_activity_copy:
                onConversationCopyClick();
                break;
            case R.id.menu_chat_activity_forward:
                onConversationForwardClick();
                break;
            case R.id.menu_chat_activity_favorite:

                onConversationFavoriteClick();
                break;
            case R.id.menu_chat_activity_translate:
                onConversationTranslateClick();
                break;
            case R.id.menu_chat_activity_delete:
                onConversationDeleteClick();
                break;
            case R.id.menu_chat_activity_more:
                onConversationMoreClick();
                break;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * 聊天界面复制
     */
    private void onConversationCopyClick() {

        Log.i(TAG, "onConversationCopyClick");


    }

    /**
     * 聊天界面转发
     */
    private void onConversationForwardClick() {

        Log.i(TAG, "onConversationForwardClick");


    }

    /**
     * 聊天界面收藏
     */
    private void onConversationFavoriteClick() {

        Log.i(TAG, "onConversationFavoriteClick");

    }


    /**
     * 聊天界面翻译
     */
    private void onConversationTranslateClick() {

        Log.i(TAG, "onConversationTranslateClick");

    }

    /**
     * 聊天界面删除
     */
    private void onConversationDeleteClick() {

        Log.i(TAG, "onConversationDeleteClick");

    }


    /**
     * 聊天界面更多
     */
    private void onConversationMoreClick() {

        Log.i(TAG, "onConversationMoreClick");

    }
}
