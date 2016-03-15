package com.wl.pluto.plutochat.chat.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.adapter.ContactsListViewAdapter;
import com.wl.pluto.plutochat.chat.entity.ContactsItemData;
import com.wl.pluto.plutochat.chat.entity.UserEntity;
import com.wl.pluto.plutochat.chat.greedao.ContactsUserDao;
import com.wl.pluto.plutochat.chat.widget.CharacterParser;
import com.wl.pluto.plutochat.chat.widget.GestureListView;
import com.wl.pluto.plutochat.chat.widget.PinYinComparator;
import com.wl.pluto.plutochat.chat.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 联系人界面
 */
public class FrameworkContactsFragment extends Fragment {

    private static final String TAG = "--FrameworkContactsFragment-->";

    /**
     * 联系人链表
     */
    private GestureListView mContactsListView;

    /**
     * 旁边的字母顺序表
     */
    private SideBar mSideBar;

    /**
     * 选中字母被显示在中间的对话框中
     */
    private TextView mDialogTextView;

    /**
     * 联系人适配器
     */
    private ContactsListViewAdapter mContactsListViewAdapter;

    /**
     * 汉字转化成拼音的类
     */
    private CharacterParser mCharacterParser;

    /**
     * 联系人链表的数据源
     */
    private List<ContactsItemData> mContactsList = new ArrayList<>();

    /**
     * 根据拼音来排列ListView里面的数据
     */
    private PinYinComparator mPinyinComparator;


    /**
     * 联系人数据库操作接口
     */
    private ContactsUserDao contactsUserDao;

    /**
     * 联系人链表的头部布局
     */
    private LinearLayout mContactListHeadLayout;

    /**
     * 好友推荐
     */
    private TextView mRecommendFriendsTextView;

    /**
     * 群聊
     */
    private TextView mGroupChatsTextView;

    /**
     * Tags
     */
    private TextView mTagsTextView;

    /**
     * 关注的公共账号
     */
    private TextView mOfficialAccountsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsUserDao = new ContactsUserDao(getActivity());
    }

    private void init() {

        mCharacterParser = CharacterParser.getInstance();
        mPinyinComparator = new PinYinComparator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_framework_contacts, container, false);
        initLayout(fragmentLayout);
        return fragmentLayout;
    }

    private void initLayout(final View layout) {

        //初始化链表头部布局
        initContactListHead();

        mSideBar = (SideBar) layout.findViewById(R.id.sb_side_bar);
        mDialogTextView = (TextView) layout.findViewById(R.id.tv_letter_dialog);
        mSideBar.setTextDialog(mDialogTextView);

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {

                //该字母首次出现的位置
                int position = mContactsListViewAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mContactsListView.setSelection(position);
                }
            }
        });


        mContactsListView = (GestureListView) layout.findViewById(R.id.lv_fragment_contacts_list);

        //为链表添加头布局
        mContactsListView.addHeaderView(mContactListHeadLayout);

        mContactsListViewAdapter = new ContactsListViewAdapter(getActivity(), getFragmentManager(), mContactsList);

        mContactsListView.setAdapter(mContactsListViewAdapter);

        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "you click item " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initContactListHead() {

        mContactListHeadLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.layout_contacts_list_head, null);

        mRecommendFriendsTextView = (TextView) mContactListHeadLayout.findViewById(R.id.tv_contact_head_recommend_friend);
        mRecommendFriendsTextView.setOnClickListener(clickListener);

        mGroupChatsTextView = (TextView) mContactListHeadLayout.findViewById(R.id.tv_contact_head_group_chat);
        mGroupChatsTextView.setOnClickListener(clickListener);

        mTagsTextView = (TextView) mContactListHeadLayout.findViewById(R.id.tv_contacts_head_tag);
        mTagsTextView.setOnClickListener(clickListener);

        mOfficialAccountsTextView = (TextView) mContactListHeadLayout.findViewById(R.id.tv_contact_head_official_accounts);
        mOfficialAccountsTextView.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.tv_contact_head_recommend_friend:
                    onRecommendFriendClick();
                    break;
                case R.id.tv_contact_head_group_chat:
                    onGroupChatsClick();
                    break;
                case R.id.tv_contacts_head_tag:
                    onTagsClick();
                    break;
                case R.id.tv_contact_head_official_accounts:
                    onOfficialAccountsClick();
                    break;
            }
        }
    };

    private void onRecommendFriendClick() {

    }

    private void onGroupChatsClick() {

    }

    private void onTagsClick() {

    }

    private void onOfficialAccountsClick() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mContactsListViewAdapter.getCount() == 0) {
            //初始化联系人链表
            new LoadDataTask().execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mContactsList != null) {
            mContactsList.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mContactsList != null) {
            mContactsList.clear();
            mContactsList = null;
        }
    }

    /**
     * 为list添加数据
     *
     * @param data
     * @return
     */
    private List<ContactsItemData> addData(String[] data) {

        List<ContactsItemData> itemData = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            ContactsItemData model = new ContactsItemData();
            model.setContactsUserNickname(data[i]);

            //汉字转拼音
            String pinyin = mCharacterParser.getSelling(data[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            //正则表达式，判断首字母是否为英文字母
            if (sortString.matches("[A-Z]")) {
                model.setSortLetters(sortString);
            } else {
                model.setSortLetters("#");
            }

            itemData.add(model);
        }

        return itemData;
    }

    /**
     * 为list添加数据
     *
     * @param userEntity
     * @return
     */
    private void addData(UserEntity userEntity) {

        ContactsItemData data = new ContactsItemData();

        //添加用户名
        data.setContactsUserName(userEntity.getUsername());

        //添加昵称
        data.setContactsUserNickname(userEntity.getUserNickName());

        //添加头像的网络地址
        data.setContactsUserHeadImageURL(userEntity.getUserHeadImageUrl());

        //汉字转拼音
        String pinyin = mCharacterParser.getSelling(userEntity.getUserNickName());
        String sortString = pinyin.substring(0, 1).toUpperCase();

        //正则表达式，判断首字母是否为英文字母
        if (sortString.matches("[A-Z]")) {
            data.setSortLetters(sortString);
        } else {
            data.setSortLetters("#");
        }

        mContactsList.add(data);

    }

    private void filterData(String filterData) {
        List<ContactsItemData> filterDataList = new ArrayList<ContactsItemData>();
        if (TextUtils.isEmpty(filterData)) {
            filterDataList = mContactsList;

        } else {
            filterDataList.clear();

            for (ContactsItemData item : mContactsList) {
                String name = item.getContactsUserNickname();

                if (name.indexOf(filterData) != -1 || mCharacterParser.getSelling(name).startsWith(filterData.toString())) {

                    filterDataList.add(item);
                }
            }
        }

        //根据a-z排序
        Collections.sort(filterDataList, mPinyinComparator);
        mContactsListViewAdapter.updateListView(filterDataList);
    }


    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {

        public LoadDataTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Map<String, UserEntity> userEntityMap = contactsUserDao.getContactsList();

            if (userEntityMap != null) {

                //遍历查询的好友链表
                Iterator iterator = userEntityMap.entrySet().iterator();


                while (iterator.hasNext()) {

                    Map.Entry entry = (Map.Entry) iterator.next();
                    UserEntity entity = (UserEntity) entry.getValue();

                    //为联系人数据源添加数据
                    addData(entity);

                    Log.i(TAG, "nickname = " + entity.getUserNickName());
                }

                Collections.sort(mContactsList, mPinyinComparator);

                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {

                mContactsListViewAdapter.notifyDataSetChanged();
            } else {
                Log.i(TAG, "联系人链表为空");
            }
        }
    }
}
