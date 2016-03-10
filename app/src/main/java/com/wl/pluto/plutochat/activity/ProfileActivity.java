package com.wl.pluto.plutochat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseEditDialog;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.fragment.SelectDialogFragment;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "--ProfileActivity-->";

    /**
     * 用户名，全局唯一的字段
     */
    private String mUserNameValue = null;

    private String mUserNickNameValue = null;

    private String mUserHeadImageUrlValue = null;

    /**
     * 用户头像
     */
    private ImageView mUserHeadImage;

    /**
     * 用户名
     */
    private TextView mUserNameTextView;

    /**
     * 用户昵称
     */
    private TextView mUserNickNameTextView;

    /**
     * 发起聊天按钮
     */
    private Button mMessageButton;

    /**
     * 修改昵称
     */
    private TextView mSetRemardAndTagTextView;

    /**
     * 相册
     */
    private TextView mAlbumTextView;

    /**
     * 更多
     */
    private TextView mMoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        initLayout();
    }

    private void init() {

        Intent intent = getIntent();

        if (intent != null) {
            mUserNameValue = intent.getStringExtra(UserConstant.CHAT_USER_NAME_KEY);
            mUserNickNameValue = intent.getStringExtra(UserConstant.CHAT_USER_NICK_NAME_KEY);
            mUserHeadImageUrlValue = intent.getStringExtra(UserConstant.CHAT_USER_HEAD_IMAGE_URL_KEY);
        }
    }

    private void initLayout() {

        mUserHeadImage = (ImageView) findViewById(R.id.iv_profile_head_image);
        //使用Picasso框架加载网络图片,为联系人设置头像
        Glide.with(this)
                .load(mUserHeadImageUrlValue)
                .override(UserConstant.USER_HEAD_IMAGE_WIDTH, UserConstant.USER_HEAD_IMAGE_HEIGHT)
                .centerCrop()
                .placeholder(R.mipmap.head_image_default1)
                .into(mUserHeadImage);

        mUserNameTextView = (TextView) findViewById(R.id.tv_profile_user_name);
        mUserNameTextView.setText(mUserNameValue);

        mUserNickNameTextView = (TextView) findViewById(R.id.tv_profile_nick_name);
        mUserNickNameTextView.setText(mUserNickNameValue);

        mMessageButton = (Button) findViewById(R.id.btn_profile_message);
        mMessageButton.setOnClickListener(clickListener);

        mSetRemardAndTagTextView = (TextView) findViewById(R.id.tv_profile_set_remark_and_tag);
        mSetRemardAndTagTextView.setOnClickListener(clickListener);

        mAlbumTextView = (TextView) findViewById(R.id.tv_profile_album);
        mAlbumTextView.setOnClickListener(clickListener);

        mMoreTextView = (TextView) findViewById(R.id.tv_profile_more);
        mMoreTextView.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_profile_message:
                    onSendMessageClick();
                    break;
                case R.id.tv_profile_set_remark_and_tag:
                    onSetRemarkAndTagClick();
                    break;
                case R.id.tv_profile_album:

                    onAlbumClick();
                    break;

                case R.id.tv_profile_more:
                    onMoreClick();
                    break;
            }
        }
    };

    private void onSendMessageClick() {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(UserConstant.CHAT_USER_NAME_KEY, mUserNameValue);
        intent.putExtra(UserConstant.CHAT_USER_NICK_NAME_KEY, mUserNickNameValue);
        intent.putExtra(UserConstant.CHAT_USER_HEAD_IMAGE_URL_KEY, mUserHeadImageUrlValue);
        startActivity(intent);
    }

    private void onSetRemarkAndTagClick() {

        BaseEditDialog.getInstance(this, getResources().getString(R.string.text_input_nick_name), mUserNameValue).show();
    }

    private void onAlbumClick() {


    }

    private void onMoreClick() {

        SelectDialogFragment.getInstance(SelectDialogFragment.DIALOG_TYPE_ALERT).show(
                getFragmentManager(), getResources().getString(R.string.text_dialog_notification));
    }
}
