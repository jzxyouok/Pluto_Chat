package com.wl.pluto.plutochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.activity.ChatActivity;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseOperationDialog;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.entity.UserEntity;
import com.wl.pluto.plutochat.utils.DateTimeUtils;
import com.wl.pluto.plutochat.utils.ExpressionUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Chat Fragment 里面的聊天记录的链表适配器
 * <p/>
 * Created by jeck on 15-10-29.
 */
public class ChatListViewAdapter extends BaseAdapter {

    public static final String TAG = "--ChatListViewAdapter-->";

    /**
     * 适配器的数据源
     */
    private ArrayList<EMConversation> chatConversationList;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 回话过滤器
     */
    private ConversationFilter mConversationFilter;

    public ChatListViewAdapter(Context context, ArrayList<EMConversation> list) {
        this.context = context;
        this.chatConversationList = list;
    }

    @Override
    public int getCount() {
        return chatConversationList.size();
    }

    @Override
    public EMConversation getItem(int position) {
        return chatConversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.layout_chat_list_view_item, null);

            //listView优化技巧之一，复用控件
            viewHolder = new ViewHolder();
            viewHolder.chatUserHeadImageView = (ImageView) convertView.findViewById(R.id.iv_chat_fragment_head_image);
            viewHolder.chatNotificationFlagImageView = (TextView) convertView.findViewById(R.id.tv_chat_notification_flag);
            viewHolder.chatUserNickNameTextView = (TextView) convertView.findViewById(R.id.tv_chat_fragment_nick_name);
            viewHolder.chatMessageTextView = (TextView) convertView.findViewById(R.id.tv_chat_fragment_message);
            viewHolder.chatTimeTextView = (TextView) convertView.findViewById(R.id.tv_chat_fragment_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取一个会话
        EMConversation conversation = getItem(position);

        //获取当前会话的聊天对象
        final String userName = conversation.getUserName();
        final UserEntity userEntity = BaseActivity.getUserEntityMap().get(userName);


        //处理为读取的消息
        if (conversation.getUnreadMsgCount() > 0) {

            //显示为读取的消息数目
            Log.i(TAG, "unread count = " + conversation.getUnreadMsgCount());

            viewHolder.chatNotificationFlagImageView.setText(String.valueOf(conversation.getUnreadMsgCount()));
            viewHolder.chatNotificationFlagImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.chatNotificationFlagImageView.setVisibility(View.INVISIBLE);
        }

        //如果是聊过天的人，
        if (conversation.getMsgCount() != 0) {

            //取出最后一条消息，并把该消息的内容作为提示信息显示出来
            EMMessage lastMessage = conversation.getLastMessage();

            if (userEntity != null) {

                viewHolder.chatUserNickNameTextView.setText(userEntity.getUserNickName());

                //使用Picasso框架加载网络图片
                Glide.with(context)
                        .load(userEntity.getUserHeadImageUrl())
                        .override(UserConstant.CHAT_HEAD_WIDTH, UserConstant.CHAT_HEAD_HEIGHT)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.head_image_default2)
                        .error(R.mipmap.head_image_default2)
                        .into(viewHolder.chatUserHeadImageView);

                viewHolder.chatMessageTextView.setText(ExpressionUtils.getSmiledText(context,
                        getMessageDigest(lastMessage, context)), TextView.BufferType.SPANNABLE);
                viewHolder.chatTimeTextView.setText(DateTimeUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            }

        }

        //单击跳转到相对应的聊天界面
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserConstant.CHAT_USER_NAME_KEY, userName);
                intent.putExtra(UserConstant.CHAT_USER_NICK_NAME_KEY, userEntity.getUserNickName());
                intent.putExtra(UserConstant.CHAT_USER_HEAD_IMAGE_URL_KEY, userEntity.getUserHeadImageUrl());
                context.startActivity(intent);
            }
        });

        //长按显示对话框
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                BaseOperationDialog.getInstance(context, BaseOperationDialog.DIALOG_CHAT_DATA, userEntity.getUsername()).show();
                return true;
            }
        });

        return convertView;
    }


    /**
     * 根据消息内容和消息类型获取消息的提示内容
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {

        String digest = "";

        switch (message.getType()) {
            case TXT: {

                TextMessageBody messageBody = (TextMessageBody) message.getBody();
                digest = messageBody.getMessage();
                break;
            }
            case IMAGE: {
                ImageMessageBody imageMessageBody = (ImageMessageBody) message.getBody();

                if (imageMessageBody != null) {

                    digest = getString(context, R.string.digest_message_picture) + imageMessageBody.getFileName();
                } else {
                    digest = getString(context, R.string.digest_message_picture);
                }

                break;
            }
            case LOCATION: {

                LocationMessageBody messageBody = (LocationMessageBody) message.getBody();
                digest = getString(context, R.string.digest_message_location) + messageBody.getAddress();
                break;
            }
            case FILE: {

                digest=getString(context, R.string.digest_message_file);
                break;
            }
            case VIDEO: {
                digest=getString(context, R.string.digest_message_video);

                break;
            }
            case VOICE: {
                digest=getString(context, R.string.digest_message_voice);
                break;
            }
        }

        Log.i(TAG, " digest = " + digest);
        return digest;
    }

    private static class ViewHolder {

        /**
         * 头像
         */
        public ImageView chatUserHeadImageView;

        /**
         * 新消息标记
         */
        public TextView chatNotificationFlagImageView;

        /**
         * title
         */
        public TextView chatUserNickNameTextView;

        /**
         * message
         */
        public TextView chatMessageTextView;

        /**
         * time
         */
        public TextView chatTimeTextView;
    }

    String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

//    @Override
//    public Filter getFilter() {
//        if (mConversationFilter == null) {
//            mConversationFilter = new ConversationFilter(chatConversationList);
//        }
//
//        return mConversationFilter;
//    }

    /**
     * 回话过滤器, 完全看不懂上面意思
     */
    private class ConversationFilter extends Filter {

        private ArrayList<EMConversation> mOriginalValues = null;

        public ConversationFilter(ArrayList<EMConversation> conversations) {

            this.mOriginalValues = conversations;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();
            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>();
            }

            if (constraint == null || constraint.length() == 0) {
                filterResults.values = chatConversationList;
                filterResults.count = chatConversationList.size();
            } else {
                String prefixString = constraint.toString();

                final int size = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int count = words.length;

                        for (int k = 0; k < count; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = newValues;
                filterResults.count = newValues.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            chatConversationList.clear();
            chatConversationList.addAll((ArrayList<EMConversation>) results.values);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }
}
