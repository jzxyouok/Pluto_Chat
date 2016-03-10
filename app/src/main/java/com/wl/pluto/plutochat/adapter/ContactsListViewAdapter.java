package com.wl.pluto.plutochat.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.activity.MyProfileActivity;
import com.wl.pluto.plutochat.activity.ProfileActivity;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.base.BaseOperationDialog;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.entity.ContactsItemData;
import com.wl.pluto.plutochat.greedao.ContactsUserDao;

import java.util.List;

/**
 * Created by jeck on 15-11-1.
 */
public class ContactsListViewAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;

    private ContactsUserDao contactsUserDao;

    private FragmentManager fragmentManager;

    /**
     * 数据源
     */
    private List<ContactsItemData> mContactsDataSourceList;

    public ContactsListViewAdapter(Context context, FragmentManager manager, List<ContactsItemData> list) {
        this.context = context;
        this.fragmentManager = manager;
        this.mContactsDataSourceList = list;
        contactsUserDao = new ContactsUserDao(context);
    }


    public void updateListView(List<ContactsItemData> list) {

        this.mContactsDataSourceList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mContactsDataSourceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactsDataSourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        final ContactsItemData mContent = mContactsDataSourceList.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_contacts_list_view_item, null);

            //头像
            viewHolder.contactsUserHeadImage = (ImageView) convertView.findViewById(R.id.iv_contacts_user_head_image);

            //名称
            viewHolder.contactsUserNickNameTextView = (TextView) convertView.findViewById(R.id.tv_contacts_user_nick_name);

            //分类首字母
            viewHolder.letterTextView = (TextView) convertView.findViewById(R.id.tv_first_letter);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position来获取分类首字母的Ｃｈａｒ
        int section = getSectionForPosition(position);

        //是否显示分类首字母
        if (position == getPositionForSection(section)) {
            viewHolder.letterTextView.setVisibility(View.VISIBLE);
            viewHolder.letterTextView.setText(mContent.getSortLetters());
        } else {

            viewHolder.letterTextView.setVisibility(View.GONE);
        }


        //设置用户名称
        final String name = mContactsDataSourceList.get(position).getContactsUserName();

        //用户昵称
        final String nickName = mContactsDataSourceList.get(position).getContactsUserNickname();
        viewHolder.contactsUserNickNameTextView.setText(nickName);

        //头像地址
        final String headImageUrl = mContactsDataSourceList.get(position).getContactsUserHeadImageURL();

        //使用Picasso框架加载网络图片,为联系人设置头像
        Glide.with(context)
                .load(headImageUrl)
                .override(UserConstant.CHAT_HEAD_WIDTH, UserConstant.CHAT_HEAD_HEIGHT)
                .centerCrop()
                .placeholder(R.mipmap.head_image_default2)
                .into(viewHolder.contactsUserHeadImage);

        //单击跳转到个人主页
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;
                if (name.equals(BaseApplication.getInstance().getUserName())) {

                    intent = new Intent(context, MyProfileActivity.class);
                } else {
                    intent = new Intent(context, ProfileActivity.class);
                }
                intent.putExtra(UserConstant.CHAT_USER_NAME_KEY, name);
                intent.putExtra(UserConstant.CHAT_USER_NICK_NAME_KEY, nickName);
                intent.putExtra(UserConstant.CHAT_USER_HEAD_IMAGE_URL_KEY, headImageUrl);
                context.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                BaseOperationDialog.getInstance(context,BaseOperationDialog.DIALOG_CONTACTS_DATA, name).show();
                return true;
            }
        });

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     *
     * @param sectionIndex
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mContactsDataSourceList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据ListView 的当前位置获取分类的首字母的Char ascii值
     *
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        return mContactsDataSourceList.get(position).getSortLetters().charAt(0);
    }

    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();

        //正则表达式，　判断首字母是否为英文
        if (sortStr.matches("[A-Z]]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    private static class ViewHolder {

        /**
         * 好友用户头像
         */
        public ImageView contactsUserHeadImage;

        /**
         * 好友名称
         */
        public TextView contactsUserNickNameTextView;

        /**
         * 分类首字母
         */
        public TextView letterTextView;
    }
}
