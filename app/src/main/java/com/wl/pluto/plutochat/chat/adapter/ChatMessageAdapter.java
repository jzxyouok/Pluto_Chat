package com.wl.pluto.plutochat.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.TextFormater;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.activity.ChatActivity;
import com.wl.pluto.plutochat.chat.activity.PlayVideoMessageActivity1;
import com.wl.pluto.plutochat.chat.activity.PlayVideoMessageActivity2;
import com.wl.pluto.plutochat.chat.activity.ShowBigImageActivity;
import com.wl.pluto.plutochat.chat.activity.ShowLocationActivity;
import com.wl.pluto.plutochat.chat.constant.CommonConstant;
import com.wl.pluto.plutochat.chat.constant.UserConstant;
import com.wl.pluto.plutochat.chat.model.VoiceMessagePlayer;
import com.wl.pluto.plutochat.chat.utils.DateTimeUtils;
import com.wl.pluto.plutochat.chat.utils.ExpressionUtils;
import com.wl.pluto.plutochat.chat.utils.ImageUtils;
import com.wl.pluto.plutochat.chat.utils.SDCardUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Chat Activity　中的消息链表的适配器
 * Created by jeck on 15-11-30.
 */
public class ChatMessageAdapter extends BaseAdapter {

    /**
     * 标示
     */
    private static final String TAG = "--ChatMessageAdapter-->";

    /**
     * 上下文
     */
    private Context context;

    /**
     *
     */
    private Activity activity;

    /**
     * 聊天对象
     */
    private String username;

    /**
     * 头像地址
     */
    private String toChatUserHeadImageUrl;

    /**
     * 会话
     */
    private EMConversation conversation = null;

    /**
     * 这就是消息适配器的数据源
     */
    private List<EMMessage> messageList = null;

    /**
     * 处理消息的发送和接受的Handler
     */
    private MessageHandler messageHandler = new MessageHandler();

    /**
     * 布局加载器
     */
    private LayoutInflater inflater;

    private static final int VIEW_TYPE_COUNT = 10;
    private static final int VIEW_TYPE_INVALID = -1;
    private static final int VIEW_TYPE_MESSAGE_TXT_RECEIVE = 0;
    private static final int VIEW_TYPE_MESSAGE_TXT_SEND = 1;
    private static final int VIEW_TYPE_MESSAGE_IMAGE_RECEIVE = 2;
    private static final int VIEW_TYPE_MESSAGE_IMAGE_SEND = 3;
    private static final int VIEW_TYPE_MESSAGE_LOCATION_RECEIVE = 4;
    private static final int VIEW_TYPE_MESSAGE_LOCATION_SEND = 5;
    private static final int VIEW_TYPE_MESSAGE_VOICE_RECEIVE = 6;
    private static final int VIEW_TYPE_MESSAGE_VOICE_SEND = 7;
    private static final int VIEW_TYPE_MESSAGE_VIDEO_RECEIVE = 8;
    private static final int VIEW_TYPE_MESSAGE_VIDEO_SEND = 9;


    private static final int HANDLER_MESSAGE_REFRESH_LIST = 2001;
    private static final int HANDLER_MESSAGE_SHOW_LAST_MESSAGE = 2002;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2003;

    public static final String CHAT_IMAGE_DIR = "pluto/chat/image";
    public static final String CHAT_VOICE_DIR = "pluto/chat/voice";
    public static final String CHAT_VIDEO_DIR = "pluto/chat/video";

    public interface OnRefreshChatMessageListListener {
        void onRefreshChatMessageList(final int listPosition);
    }

    private OnRefreshChatMessageListListener listListener;

    /******************************************************************************************/
    public ChatMessageAdapter(Context context, String username, String headImageUrl) {
        this.context = context;
        this.activity = (ChatActivity) context;
        this.username = username;
        this.toChatUserHeadImageUrl = headImageUrl;
        this.inflater = LayoutInflater.from(context);
        this.conversation = EMChatManager.getInstance().getConversation(username);
    }

    public void setOnRefreshChatMessageListListener(OnRefreshChatMessageListListener listener) {
        this.listListener = listener;
    }

    @Override
    public int getCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public EMMessage getItem(int position) {

        if (messageList != null && position < messageList.size()) {
            return messageList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        EMMessage message = getItem(position);
        if (message == null) {
            return VIEW_TYPE_INVALID;
        } else {
            switch (message.getType()) {
                case TXT: {

                    if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                        return VIEW_TYPE_MESSAGE_TXT_RECEIVE;
                    } else {
                        return VIEW_TYPE_MESSAGE_TXT_SEND;
                    }
                }
                case IMAGE: {
                    if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                        return VIEW_TYPE_MESSAGE_IMAGE_RECEIVE;
                    } else {
                        return VIEW_TYPE_MESSAGE_IMAGE_SEND;
                    }
                }
                case LOCATION: {
                    if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                        return VIEW_TYPE_MESSAGE_LOCATION_RECEIVE;
                    } else {
                        return VIEW_TYPE_MESSAGE_LOCATION_SEND;
                    }
                }
                case VOICE: {
                    if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                        return VIEW_TYPE_MESSAGE_VOICE_RECEIVE;
                    } else {
                        return VIEW_TYPE_MESSAGE_VOICE_SEND;
                    }
                }

                case VIDEO: {
                    if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                        return VIEW_TYPE_MESSAGE_VIDEO_RECEIVE;
                    } else {
                        return VIEW_TYPE_MESSAGE_VIDEO_SEND;
                    }
                }
            }
        }
        return VIEW_TYPE_INVALID;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * 根据不同的消息类型，创建不同的消息布局，因为虽然在同一个聊天界面，但是发送文本消息和发送视频消息肯定是不同的
     */
    private View createViewByMessage(EMMessage message) {

        Log.i(TAG, "createViewByMessage");
        switch (message.getType()) {
            case TXT:
                //如果是接受消息
                if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                    return inflater.inflate(R.layout.layout_message_txt_rece, null);
                } else {
                    return inflater.inflate(R.layout.layout_message_txt_send, null);
                }
            case IMAGE:
                if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                    return inflater.inflate(R.layout.layout_message_image_receive, null);
                } else {
                    return inflater.inflate(R.layout.layout_message_image_send, null);
                }
            case LOCATION:

                if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                    return inflater.inflate(R.layout.layout_message_location_receive, null);
                } else {
                    return inflater.inflate(R.layout.layout_message_location_send, null);
                }
            case VIDEO:
                if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                    return inflater.inflate(R.layout.layout_message_video_receive, null);
                } else {
                    return inflater.inflate(R.layout.layout_message_video_send, null);
                }

            case VOICE:
                if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
                    return inflater.inflate(R.layout.layout_message_voice_receive, null);
                } else {
                    return inflater.inflate(R.layout.layout_message_voice_send, null);
                }
            case FILE:
                break;
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //首先将message从会话中取出．根据会话的方向，是接受，还是发送，来创建不同的布局
        final EMMessage message = getItem(position);

        final ViewHolder viewHolder;

        //每一个convertView都代表一个item布局，那就是说，每一次有新的消息来了，都会创建一个新的布局
        if (convertView == null) {

            viewHolder = new ViewHolder();

            //创建布局文件
            convertView = createViewByMessage(message);

            if (convertView != null) {

                switch (message.getType()) {

                    //文本消息
                    case TXT:
                        try {
                            //时间戳，属于通用型的数据，就直接提出来．我觉得很多都是通用型的，以后再说
                            viewHolder.messageTimeStamp = (TextView) convertView.findViewById(R.id.tv_message_txt_time_stamp);
                            viewHolder.messageUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_message_txt_head_image);
                            viewHolder.messageContent = (TextView) convertView.findViewById(R.id.tv_message_txt_content);

                            //这是发送界面独有的
                            viewHolder.messageStatue = (ImageView) convertView.findViewById(R.id.iv_message_txt_send_statue);
                            viewHolder.messageProgress = (ProgressBar) convertView.findViewById(R.id.pb_message_txt_send_progress);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    //图片消息
                    case IMAGE:

                        try {
                            viewHolder.messageTimeStamp = (TextView) convertView.findViewById(R.id.tv_message_image_time_stamp);
                            viewHolder.messageUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_message_image_head_image);
                            viewHolder.messageImageContent = (ImageView) convertView.findViewById(R.id.iv_message_image_content);
                            viewHolder.messageProgress = (ProgressBar) convertView.findViewById(R.id.pb_message_image_progress_bar);
                            viewHolder.messageProgressTextView = (TextView) convertView.findViewById(R.id.tv_message_image_progress_txt);
                            viewHolder.messageStatue = (ImageView) convertView.findViewById(R.id.iv_message_image_statue);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    //视频消息
                    case VIDEO:

                        try {
                            viewHolder.messageTimeStamp = (TextView) convertView.findViewById(R.id.tv_message_video_time_stamp);
                            viewHolder.messageUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_message_video_head_image);
                            viewHolder.messageImageContent = (ImageView) convertView.findViewById(R.id.iv_message_video_content);
                            viewHolder.messageVideoSize = (TextView) convertView.findViewById(R.id.tv_message_video_size);
                            viewHolder.messageVideoDuration = (TextView) convertView.findViewById(R.id.tv_message_video_duration);
                            viewHolder.messageProgress = (ProgressBar) convertView.findViewById(R.id.pb_message_video_progress_bar);
                            viewHolder.messageProgressTextView = (TextView) convertView.findViewById(R.id.tv_message_video_progress_txt);
                            viewHolder.messageStatue = (ImageView) convertView.findViewById(R.id.iv_message_video_statue);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    //位置消息
                    case LOCATION:

                        try {
                            viewHolder.messageTimeStamp = (TextView) convertView.findViewById(R.id.tv_message_location_time_stamp);
                            viewHolder.messageUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_message_location_head_image);
                            viewHolder.messageImageContent = (ImageView) convertView.findViewById(R.id.iv_message_image_content);
                            viewHolder.messageLocationAddress = (TextView) convertView.findViewById(R.id.tv_message_location_address);
                            viewHolder.messageProgress = (ProgressBar) convertView.findViewById(R.id.pb_message_location_progress_bar);
                            viewHolder.messageProgressTextView = (TextView) convertView.findViewById(R.id.tv_message_location_progress_txt);
                            viewHolder.messageStatue = (ImageView) convertView.findViewById(R.id.iv_message_location_statue);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    //语音消息
                    case VOICE:

                        try {
                            viewHolder.messageTimeStamp = (TextView) convertView.findViewById(R.id.tv_message_voice_time_stamp);
                            viewHolder.messageUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_message_voice_head_image);
                            viewHolder.messageImageContent = (ImageView) convertView.findViewById(R.id.iv_message_voice_content);
                            viewHolder.messageContentLength = (TextView) convertView.findViewById(R.id.tv_message_voice_length);
                            viewHolder.messageStatue = (ImageView) convertView.findViewById(R.id.iv_message_voice_send_statue);
                            viewHolder.messageProgress = (ProgressBar) convertView.findViewById(R.id.pb_message_voice_send_progress);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case FILE:
                        break;
                }

                convertView.setTag(viewHolder);
            } else {
                Log.i(TAG, "contentView.imagelayout == null");
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //先处理时间戳
        if (position == 0) {
            viewHolder.messageTimeStamp.setText(DateTimeUtils.getTimestampString(new Date(message.getMsgTime())));
            viewHolder.messageTimeStamp.setVisibility(View.VISIBLE);
        } else {

            //如果两条消息的时间间隔的比较长，比如十分钟．则显示时间戳
            EMMessage previousMessage = getItem(position - 1);
            if (previousMessage != null && DateTimeUtils.isCloseEnough(
                    previousMessage.getMsgTime(), message.getMsgTime())) {
                viewHolder.messageTimeStamp.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.messageTimeStamp.setText(DateTimeUtils.getTimestampString(new Date(message.getMsgTime())));
                viewHolder.messageTimeStamp.setVisibility(View.VISIBLE);
            }
        }

        //加载头像
        loadUserHeadImage(message, viewHolder.messageUserHeadImage);

        switch (message.getType()) {
            case TXT:
                onHandleTxtMessage(message, viewHolder);
                break;

            case IMAGE:
                onHandleImageMessage(message, viewHolder);
                break;

            case LOCATION:
                onHandleLocationMessage(message, viewHolder);
                break;

            case VOICE:
                onHandleVoiceMessage(message, viewHolder);
                break;

            case VIDEO:
                onHandleVideoMessage(message, viewHolder);
                break;
        }

        return convertView;
    }

    /**
     * 加载用户头像，这里是可以加载头像的，如果有自己的服务器，那就更好了
     *
     * @param message
     * @param imageView
     */
    private void loadUserHeadImage(EMMessage message, ImageView imageView) {

        //如果是发送消息，加载自己的头像
        if (message.direct.equals(EMMessage.Direct.SEND)) {
            Glide.with(context)
                    .load(new File(SDCardUtils.getUserHeadImageAbsolutePath()))
                    .override(UserConstant.CHAT_HEAD_WIDTH, UserConstant.CHAT_HEAD_HEIGHT)
                    .placeholder(R.mipmap.head_image_default2)
                    .into(imageView);

        } else {
            //加载对方的头像，可以通过对方的用户信息中头像地址，使用Picasso直接加载
            Glide.with(context)
                    .load(toChatUserHeadImageUrl)
                    .override(UserConstant.CHAT_HEAD_WIDTH, UserConstant.CHAT_HEAD_HEIGHT)
                    .placeholder(R.mipmap.head_image_default2)
                    .into(imageView);
        }
    }

    /**
     * 处理视频消息
     */
    private void onHandleVideoMessage(final EMMessage message, final ViewHolder holder) {
        final VideoMessageBody messageBody = (VideoMessageBody) message.getBody();


        //视频的缩略图路径
        String videoThumbnailPath = messageBody.getLocalThumb();

        //如果是发送端，那可以直接加载这个图片，如果是接收端，那肯定是需要先从网路上下载，然后加载进来
        if (!TextUtils.isEmpty(videoThumbnailPath)) {
            Glide.with(context).load(videoThumbnailPath).into(holder.messageImageContent);
        }

        //设置视频时长
        holder.messageVideoDuration.setText(DateTimeUtils.formatTime(messageBody.getLength()));

        //设置视频大小
        holder.messageVideoSize.setText(TextFormater.getDataSize(messageBody.getVideoFileLength()));

        if (message.direct.equals(EMMessage.Direct.RECEIVE)) {

            //如果是接受到的视频，用p2播放器
            holder.messageImageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayVideoMessageActivity2.class);
                    intent.putExtra(CommonConstant.VIDEO_FILE_PATH_KEY, messageBody.getLocalUrl());
                    intent.putExtra(CommonConstant.VIDEO_DURATION_KEY, messageBody.getLength());
                    intent.putExtra(CommonConstant.VIDEO_REMOTE_PATH_KEY,messageBody.getRemoteUrl());
                    intent.putExtra(CommonConstant.VIDEO_SECRET_KEY, messageBody.getSecret());
                    context.startActivity(intent);
                }
            });

        } else {

            //如果是本地视频，用p1播放器
            holder.messageImageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PlayVideoMessageActivity1.class);
                    intent.putExtra(CommonConstant.VIDEO_FILE_PATH_KEY, messageBody.getLocalUrl());
                    context.startActivity(intent);
                }
            });
            switch (message.status) {
                case SUCCESS:
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.GONE);
                    break;
                case FAIL:

                    holder.messageStatue.setVisibility(View.VISIBLE);
                    holder.messageProgress.setVisibility(View.GONE);
                    break;
                case INPROGRESS:
                    break;
                default:
                    sendMessageInBackground(message, holder);
            }
        }
    }

    /**
     * 处理语音消息
     */
    private void onHandleVoiceMessage(final EMMessage message, final ViewHolder holder) {

        VoiceMessageBody messageBody = (VoiceMessageBody) message.getBody();

        //点击播放语音
        holder.messageImageContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VoiceMessagePlayer.newInstance(message, holder.messageImageContent).play();
            }
        });

        int voiceLength = messageBody.getLength();

        if (voiceLength > 0) {

            holder.messageContentLength.setText(voiceLength + "\"");
        } else {
            holder.messageContentLength.setVisibility(View.GONE);
        }


        if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
            return;
        } else {

            switch (message.status) {
                case SUCCESS:
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    break;
                default:
                    sendMessageInBackground(message, holder);
            }
        }
    }


    /**
     * 处理位置信息
     */
    private void onHandleLocationMessage(EMMessage message, final ViewHolder holder) {

        final LocationMessageBody messageBody = (LocationMessageBody) message.getBody();
        holder.messageLocationAddress.setText(messageBody.getAddress());
        holder.messageLocationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowLocationActivity.class);
                intent.putExtra(CommonConstant.LOCATION_LATITUDE_KEY, messageBody.getLatitude());
                intent.putExtra(CommonConstant.LOCATION_LONGITUDE_KEY, messageBody.getLongitude());
                intent.putExtra(CommonConstant.LOCATION_ADDRESS_KEY, messageBody.getAddress());

                Log.i(TAG, "lat=" + messageBody.getLatitude() + "lon=" + messageBody.getLongitude());
                context.startActivity(intent);
            }
        });

        if (message.direct.equals(EMMessage.Direct.RECEIVE)) {
            return;
        } else {

            Glide.with(context).load(new File(SDCardUtils.getMapShotName("1234"))).into(holder.messageImageContent);
            switch (message.status) {
                case SUCCESS:
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    break;
                default:
                    sendMessageInBackground(message, holder);
            }
        }
    }

    /**
     * 处理文本消息
     */
    private void onHandleTxtMessage(EMMessage message, final ViewHolder holder) {

        //获取消息体
        TextMessageBody messageBody = (TextMessageBody) message.getBody();
        Spannable spannable = ExpressionUtils.getSmiledText(context, messageBody.getMessage());

        //设置消息，如果是接受到消息，那显示出来就没事了．如果是发送消息，客户端显示完了，还要发送出去．
        holder.messageContent.setText(spannable, TextView.BufferType.SPANNABLE);
        holder.messageContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Log.i(TAG, "setOnLongClickListener");
                return false;
            }
        });


        //如果是发送界面
        if (message.direct.equals(EMMessage.Direct.SEND)) {
            switch (message.status) {
                case SUCCESS: {

                    holder.messageStatue.setVisibility(View.GONE);
                    holder.messageProgress.setVisibility(View.GONE);

                    break;
                }
                case FAIL: {
                    holder.messageProgress.setVisibility(View.GONE);
                    holder.messageStatue.setVisibility(View.VISIBLE);
                    break;
                }
                case INPROGRESS:
                    break;
                default: {
                    sendMessageInBackground(message, holder);
                }
            }
        }
    }

    /**
     * 发送文本消息
     */
    private void sendMessageInBackground(final EMMessage message, final ViewHolder holder) {

        //那这个地方调用notify就是要处理消息发送之后的状态，是发送成功了，还是失败了．根据不同的状态，
        //再显示不同的状态．　如果是理想状态，那就是发送成功．
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.messageProgress.setVisibility(View.GONE);
                        holder.messageStatue.setVisibility(View.GONE);
                        if (holder.messageProgressTextView != null) {
                            holder.messageProgressTextView.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.messageProgress.setVisibility(View.GONE);
                        holder.messageStatue.setVisibility(View.VISIBLE);

                        if (holder.messageProgressTextView != null) {
                            holder.messageProgressTextView.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onProgress(final int i, String s) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.messageProgressTextView != null) {
                            holder.messageProgressTextView.setText(i + "%");
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理图片消息
     *
     * @param message
     * @param viewHolder
     */
    private void onHandleImageMessage(EMMessage message, ViewHolder viewHolder) {

        final ImageMessageBody messageBody = (ImageMessageBody) message.getBody();


        //长按进行拷贝，转发，删除等操作
        viewHolder.messageImageContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        //如果是接受图片
        if (message.direct.equals(EMMessage.Direct.RECEIVE)) {

            //点击查看大图
            viewHolder.messageImageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String imageUrl = messageBody.getRemoteUrl();
                    Intent intent = new Intent(context, ShowBigImageActivity.class);
                    intent.putExtra(CommonConstant.SHOW_BIG_IMAGE_URL_KEY, imageUrl);
                    context.startActivity(intent);
                }
            });

            if (message.status.equals(EMMessage.Status.INPROGRESS)) {
                viewHolder.messageImageContent.setImageResource(R.mipmap.ic_launcher);
                showDownloadImageProgress(message, viewHolder);
            } else {

                viewHolder.messageProgress.setVisibility(View.GONE);
                viewHolder.messageProgressTextView.setVisibility(View.GONE);

                if (messageBody.getLocalUrl() != null) {

                    String remotePath = messageBody.getRemoteUrl();

                    Glide.with(context).load(remotePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.messageImageContent);

                    if (viewHolder.messageImageContent.getWidth() > viewHolder.messageImageContent.getHeight()) {

                        Glide.with(context).load(remotePath)
                                .override(CommonConstant.HORIZONTAL_IMAGE_WIDTH, CommonConstant.HORIZONTAL_IMAGE_HEIGHT)
                                .into(viewHolder.messageImageContent);
                    } else {

                        Glide.with(context).load(remotePath)
                                .override(CommonConstant.VERTICAL_IMAGE_WIDTH, CommonConstant.VERTICAL_IMAGE_HEIGHT)
                                .into(viewHolder.messageImageContent);
                    }
                }
            }

        } else {

            final String filePath = messageBody.getLocalUrl();

            //点击查看大图
            viewHolder.messageImageContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ShowBigImageActivity.class);
                    intent.putExtra(CommonConstant.SHOW_BIG_IMAGE_URL_KEY, filePath);
                    context.startActivity(intent);
                }
            });

            if (filePath != null && new File(filePath).exists()) {

                if (ImageUtils.isImageHorizontal(filePath)) {

                    Glide.with(context).load(new File(filePath))
                            .override(CommonConstant.HORIZONTAL_IMAGE_WIDTH, CommonConstant.HORIZONTAL_IMAGE_HEIGHT)
                            .into(viewHolder.messageImageContent);
                } else {

                    Glide.with(context).load(new File(filePath))
                            .override(CommonConstant.VERTICAL_IMAGE_WIDTH, CommonConstant.VERTICAL_IMAGE_HEIGHT)
                            .into(viewHolder.messageImageContent);
                }
            }


            /**
             * 接下来更具message的状态来显示相应的控件,这里显示的是之前所发送的图片的状态
             */
            switch (message.status) {
                case SUCCESS:
                    viewHolder.messageProgress.setVisibility(View.GONE);
                    viewHolder.messageProgressTextView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    viewHolder.messageProgress.setVisibility(View.GONE);
                    viewHolder.messageProgressTextView.setVisibility(View.GONE);
                    viewHolder.messageStatue.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    viewHolder.messageProgress.setVisibility(View.VISIBLE);
                    viewHolder.messageProgressTextView.setVisibility(View.VISIBLE);
                    viewHolder.messageStatue.setVisibility(View.GONE);
                    break;
                default:

                    //这是当前所发图片的发送方法
                    sendMessageInBackground(message, viewHolder);
            }
        }
    }


    /**
     * 接受图片信息
     *
     * @param message
     * @param viewHolder
     */
    private void showDownloadImageProgress(final EMMessage message, final ViewHolder viewHolder) {

        final FileMessageBody messageBody = (FileMessageBody) message.getBody();
        if (viewHolder.messageProgress != null) {

            viewHolder.messageProgress.setVisibility(View.VISIBLE);
        }

        messageBody.setDownloadCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            viewHolder.messageProgress.setVisibility(View.GONE);
                            viewHolder.messageProgressTextView.setVisibility(View.GONE);
                        }

                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(final int i, String s) {

                if (message.getType() == EMMessage.Type.IMAGE) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.messageProgressTextView.setText(i + "%");
                        }
                    });
                }
            }
        });
    }


    private class ViewHolder {

        /**
         * 处于中间的时间戳
         */
        public TextView messageTimeStamp;

        /**
         * 头像
         */
        public ImageView messageUserHeadImage;

        /**
         * 文本消息内容
         */
        public TextView messageContent;

        /**
         * 图片消息
         */
        public ImageView messageImageContent;

        /**
         * 图片描述信息
         */
        public TextView messageLocationAddress;

        /**
         * 消费发送失败时显示的红色叹号
         */
        public ImageView messageStatue;

        /**
         * 网络不给力时，或者发送大的数据时，用到的进度条
         */
        public ProgressBar messageProgress;

        /**
         * 进度条的数据
         */
        public TextView messageProgressTextView;

        /**
         * 消息时长
         */
        public TextView messageContentLength;

        /**
         * 视频大小
         */
        public TextView messageVideoSize;

        /**
         * 视频时长
         */
        public TextView messageVideoDuration;
    }

    /**
     * 使用内部类来处理消息
     */
    private class MessageHandler extends Handler {

        public MessageHandler() {

        }

        /**
         * 刷新适配器
         */
        private void refreshMessageList() {

            Log.i(TAG, "refreshMessageList");

            //这个地方会将消息从会话中找出来，也就是更新了消息链表的数据源，　
            // 同时这个messageList 也保存了当前所有的聊天记录
            messageList = conversation.getAllMessages();
            for (int i = 0; i < messageList.size(); i++) {
                conversation.getMessage(i);
            }

            for (EMMessage item : messageList) {
                Log.i(TAG, item.toString());
            }

            //这个地方会调用适配器去更新数据．就会调用getView的方法来刷新界面
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE_REFRESH_LIST: {

                    refreshMessageList();
                    break;
                }

                case HANDLER_MESSAGE_SHOW_LAST_MESSAGE: {
                    if (listListener != null) {
                        listListener.onRefreshChatMessageList(messageList.size() - 1);
                    }
                    break;
                }

                case HANDLER_MESSAGE_SEEK_TO: {
                    break;
                }
            }
        }

    }

    public void refreshUI() {

        if (messageHandler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }

        Message message = messageHandler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        messageHandler.sendMessage(message);
    }

    /**
     * 刷新当前的聊天链表
     */
    public void refreshChatMessageList() {
        messageHandler.sendMessage(messageHandler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        messageHandler.sendMessage(messageHandler.obtainMessage(HANDLER_MESSAGE_SHOW_LAST_MESSAGE));
    }
}
