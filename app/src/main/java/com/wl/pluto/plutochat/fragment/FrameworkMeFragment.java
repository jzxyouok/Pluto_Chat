package com.wl.pluto.plutochat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.activity.AudioSelectedActivity;
import com.wl.pluto.plutochat.activity.MyPostsActivity;
import com.wl.pluto.plutochat.activity.MyProfileActivity;
import com.wl.pluto.plutochat.activity.NavigationActivity;
import com.wl.pluto.plutochat.activity.SettingActivity;
import com.wl.pluto.plutochat.base.BaseActivity;
import com.wl.pluto.plutochat.base.BaseApplication;
import com.wl.pluto.plutochat.constant.UserConstant;
import com.wl.pluto.plutochat.service.LocalIntentService;
import com.wl.pluto.plutochat.utils.SDCardUtils;

import java.io.File;

/**
 *
 */
public class FrameworkMeFragment extends Fragment {

    private static final String TAG = "--FrameworkMeFragment-->";

    /**
     * 个人主页
     */
    private LinearLayout mPersonProfileLayout;

    /**
     * userNickName
     */
    private TextView mMeFragmentUserNickName;

    /**
     * username
     */
    private TextView mMeFragmentUserName;

    /**
     * Posts
     */
    private TextView mMeFragmentPosts;

    /**
     * favorites
     */
    private TextView mMeFragmentFavorites;

    /**
     * wallet
     */
    private TextView mMeFragmentWallet;

    /**
     * setting
     */
    private TextView mMeFragmentSettings;

    private ImageView mUserHeadImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void init() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_framework_me, container, false);
        initLayout(layout);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        //使用Picasso框架加载网络图片
        Glide.with(getActivity())
                .load(new File(SDCardUtils.getUserHeadImageAbsolutePath()))
                .override(UserConstant.USER_HEAD_IMAGE_WIDTH, UserConstant.USER_HEAD_IMAGE_HEIGHT)
                .placeholder(R.mipmap.head_image_default1)
                .into(mUserHeadImage);
    }

    private void initLayout(View layout) {


        mUserHeadImage = (ImageView) layout.findViewById(R.id.iv_me_head_portrait);

        mPersonProfileLayout = (LinearLayout) layout.findViewById(R.id.ll_person_profile);
        mPersonProfileLayout.setOnClickListener(clickListener);

        mMeFragmentUserNickName = (TextView) layout.findViewById(R.id.tv_me_fragment_user_nick_name);
        mMeFragmentUserNickName.setText(BaseActivity.getUserEntityMap().get(
                BaseApplication.getInstance().getUserName()).getUserNickName());

        mMeFragmentUserName = (TextView) layout.findViewById(R.id.tv_me_fragment_user_name);
        mMeFragmentUserName.setText(BaseApplication.getInstance().getUserName());

        mMeFragmentPosts = (TextView) layout.findViewById(R.id.tv_me_my_posts);
        mMeFragmentPosts.setOnClickListener(clickListener);

        mMeFragmentFavorites = (TextView) layout.findViewById(R.id.tv_me_favorites);
        mMeFragmentFavorites.setOnClickListener(clickListener);

        mMeFragmentWallet = (TextView) layout.findViewById(R.id.tv_me_wallet);
        mMeFragmentWallet.setOnClickListener(clickListener);

        mMeFragmentSettings = (TextView) layout.findViewById(R.id.tv_me_settings);
        mMeFragmentSettings.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ll_person_profile:
                    onPersonProfileClick();
                    break;

                case R.id.tv_me_my_posts:

                    onMyPostsClick();
                    break;
                case R.id.tv_me_favorites:

                    onFavoritesClick();
                    break;
                case R.id.tv_me_wallet:

                    onWalletClick();
                    break;
                case R.id.tv_me_settings:

                    onSettingClick();
                    break;
            }
        }
    };

    /**
     * 启动个人资料
     */
    private void onPersonProfileClick() {
        Intent intent = new Intent(getActivity(), MyProfileActivity.class);
        getActivity().startActivity(intent);
    }

    private void onMyPostsClick() {

        Intent intent = new Intent(getActivity(), MyPostsActivity.class);
        getActivity().startActivity(intent);

    }

    private void onFavoritesClick() {
        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        startActivity(intent);
    }

    private void onWalletClick() {
        Intent intent = new Intent(getActivity(), AudioSelectedActivity.class);
        getActivity().startActivity(intent);
    }

    private void onSettingClick() {

        Intent intent = new Intent(getActivity(), SettingActivity.class);

        Bundle bundle = new Bundle();
        getActivity().startActivity(intent);
    }


}
