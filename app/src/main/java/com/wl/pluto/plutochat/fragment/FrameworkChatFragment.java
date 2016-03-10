package com.wl.pluto.plutochat.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.adapter.ChatListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;


public class FrameworkChatFragment extends Fragment {

    private static final String TAG = "--FrameworkChatFragment-->";

    /**
     * 下拉刷新链表
     */
    private ListView mListView;

    /**
     * 链表适配器
     */
    private ChatListViewAdapter mListViewAdapter = null;

    /**
     * 聊天记录数据
     */
    private ArrayList<EMConversation> mChatConversationList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View fragmentLayout = inflater.inflate(R.layout.fragment_framework_chat, container, false);
        initLayout(fragmentLayout);
        return fragmentLayout;
    }

    private void initLayout(final View layout) {


        mListView = (ListView) layout.findViewById(R.id.pull_refresh_list);

        //为会话链表添加数据，居然没有用到子线程
        mChatConversationList.addAll(loadConversationData());
        mListViewAdapter = new ChatListViewAdapter(getActivity(), mChatConversationList);
        mListView.setAdapter(mListViewAdapter);

    }

    /**
     * 获取最近的聊天记录
     *
     * @return
     */
    private ArrayList<EMConversation> loadConversationData() {

        //获取所有会话
        Hashtable<String, EMConversation> conversationHashTable = EMChatManager.getInstance().getAllConversations();

        //过滤到message的size为0 的conversation
        ArrayList<Pair<Long, EMConversation>> messageList = new ArrayList<>();

        synchronized (conversationHashTable) {
            for (EMConversation item : conversationHashTable.values()) {
                if (item.getAllMessages().size() != 0) {
                    messageList.add(new Pair<>(item.getLastMessage().getMsgTime(), item));
                }
            }
        }

        try {

            //排序
            sortMessageByLastTime(messageList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<EMConversation> conversationList = new ArrayList<>();

        for (Pair<Long, EMConversation> item : messageList) {
            conversationList.add(item.second);
        }
        return conversationList;
    }

    /**
     * 根据消息的时间顺序对消息进行排序
     */
    private void sortMessageByLastTime(ArrayList<Pair<Long, EMConversation>> messageList) {
        Collections.sort(messageList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(Pair<Long, EMConversation> lhs, Pair<Long, EMConversation> rhs) {

                if (lhs.first == rhs.first) {

                    return 0;
                } else if (rhs.first > lhs.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    private class LoadDataTask extends AsyncTask<Void, Void, ArrayList<EMConversation>> {

        @Override
        protected ArrayList<EMConversation> doInBackground(Void... params) {
            return loadConversationData();
        }

        @Override
        protected void onPostExecute(ArrayList<EMConversation> conversations) {

            if (conversations != null) {

                //如果数据有变化，有新的聊天对象被加进来了
                mChatConversationList.clear();
                mChatConversationList.addAll(conversations);
                mListViewAdapter.notifyDataSetChanged();

            }
        }
    }

    /**
     * 刷新聊天链表，有新消息提示，就显示红点点
     */
    public void refreshChatList() {

        mChatConversationList.clear();
        mChatConversationList.addAll(loadConversationData());
        if (mListViewAdapter != null) {
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshChatList();
    }


}
