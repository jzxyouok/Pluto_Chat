package com.wl.pluto.plutochat.chat.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.base.BaseActivity;
import com.wl.pluto.plutochat.chat.base.BaseApplication;
import com.wl.pluto.plutochat.chat.common_interface.IUpload;
import com.wl.pluto.plutochat.chat.constant.URLConstant;
import com.wl.pluto.plutochat.chat.constant.UserConstant;
import com.wl.pluto.plutochat.chat.entity.UserEntity;
import com.wl.pluto.plutochat.chat.greedao.ContactsUserDao;
import com.wl.pluto.plutochat.chat.manager.FileManager;
import com.wl.pluto.plutochat.chat.model.UploadImpl;
import com.wl.pluto.plutochat.chat.utils.PermissionUtil;
import com.wl.pluto.plutochat.chat.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;

public class MyProfileActivity extends BaseActivity {

    private static final String TAG = "--MyProfileActivity-->";

    /**
     * 头像布局
     */
    private RelativeLayout mProfileImageLayout;

    /**
     * 头像
     */
    private ImageView mHeadImageView;

    /**
     * 昵称
     */
    private RelativeLayout mProfileNickNameLayout;
    private TextView mProfileNickName;


    /**
     * Chat ID
     */
    private RelativeLayout mProfileChatIDLayout;
    private TextView mProfileUserName;

    /**
     * QR code
     */
    private RelativeLayout mProfileQRCodeLayout;
    private TextView mProfileQRCode;


    /**
     * 性别
     */
    private RelativeLayout mProfileGenderLayout;
    private TextView mProfileGender;


    /**
     * 地区
     */
    private RelativeLayout mProfileRegionLayout;
    private TextView mProfileRegion;


    /**
     * 签名
     */
    private RelativeLayout mProfileSignatureLayout;
    private TextView mProfileSignature;

    /**
     * 请求码nick name
     */
    private static final int REQUEST_EDIT_NICK_NAME_CODE = 1001;

    /**
     * 获取系统的相册的请求码
     */
    private static final int REQUEST_EDIT_HEAD_IMAGE_CODE = 1002;

    /**
     *
     */
    public static final String EDIT_NICK_NAME_KEY = "EDIT_NICK_NAME_KEY";

    /**
     * 裁剪照片的请求码
     */
    private static final int REQUEST_EDIT_ZOOM_CODE = 1003;

    /**
     * 进度条对话框
     */
    private ProgressDialog progressDialog;

    /**
     * 联系人数据库
     */
    private ContactsUserDao userDao;

    /**
     * 当前用户的数据
     */
    private UserEntity mProfileInfo;

    /**
     * 需要保存的头像
     */
    private Bitmap mSaveHeadBitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initUserDao();
        initLayout();
    }

    private void initUserDao() {
        userDao = new ContactsUserDao(this);
        mProfileInfo = mUserEntityMap.get(BaseApplication.getInstance().getUserName());
    }

    private void initLayout() {

        mProfileImageLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_photo);
        mProfileImageLayout.setOnClickListener(clickListener);

        mHeadImageView = (ImageView) findViewById(R.id.iv_my_profile_photo);
        //这里是加载的本地图片，其实也可以加载存储在oss上面的图片
        Glide.with(this)
                .load(new File(SDCardUtils.getUserHeadImageAbsolutePath()))
                .override(UserConstant.USER_HEAD_IMAGE_WIDTH, UserConstant.USER_HEAD_IMAGE_HEIGHT)
                .placeholder(R.mipmap.head_image_default1)
                .into(mHeadImageView);

        mProfileNickNameLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_nick_name);
        mProfileNickNameLayout.setOnClickListener(clickListener);
        mProfileNickName = (TextView) findViewById(R.id.tv_my_profile_nick_name);


        mProfileChatIDLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_chat_id);
        mProfileChatIDLayout.setOnClickListener(clickListener);
        mProfileUserName = (TextView) findViewById(R.id.tv_my_profile_chat_id);
        mProfileUserName.setText(BaseApplication.getInstance().getUserName());

        mProfileQRCodeLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_qr_code);
        mProfileQRCodeLayout.setOnClickListener(clickListener);


        mProfileGenderLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_gender);
        mProfileGenderLayout.setOnClickListener(clickListener);
        mProfileGender = (TextView) findViewById(R.id.tv_my_profile_gender);

        mProfileRegionLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_Region);
        mProfileRegionLayout.setOnClickListener(clickListener);
        mProfileRegion = (TextView) findViewById(R.id.tv_my_profile_region);

        mProfileSignatureLayout = (RelativeLayout) findViewById(R.id.rl_my_profile_signature);
        mProfileSignatureLayout.setOnClickListener(clickListener);
        mProfileSignature = (TextView) findViewById(R.id.tv_my_profile_signature);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mProfileInfo != null) {
            mProfileNickName.setText(mProfileInfo.getUserNickName());
            mProfileGender.setText(mProfileInfo.getUserGender());
            mProfileRegion.setText(mProfileInfo.getUserAddress());
            mProfileSignature.setText(mProfileInfo.getUserPersonalitySignature());
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_my_profile_photo:
                    onProfileImageClick();
                    break;
                case R.id.rl_my_profile_nick_name:
                    startEditNickNameActivity();
                    break;
                case R.id.rl_my_profile_chat_id:
                    break;
                case R.id.rl_my_profile_qr_code:
                    break;
                case R.id.rl_my_profile_gender:
                    break;
                case R.id.rl_my_profile_Region:
                    break;
                case R.id.rl_my_profile_signature:
                    break;
            }
        }
    };

    /**
     * 上传头像
     */
    private void onProfileImageClick() {

        Intent pictureChooseIntent = new Intent(Intent.ACTION_PICK, null);
        pictureChooseIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pictureChooseIntent, REQUEST_EDIT_HEAD_IMAGE_CODE);
    }

    private void startEditNickNameActivity() {
        Intent intent = new Intent(this, EditNickNameActivity.class);

        startActivityForResult(intent, REQUEST_EDIT_NICK_NAME_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            //请求头像
            case REQUEST_EDIT_HEAD_IMAGE_CODE:
                if (data == null || data.getData() == null) {
                    return;
                } else {

                    //对选取的头像进行裁剪，裁剪成我们需要的大小
                    startPhoneZoom(data.getData());
                }
                break;

            case REQUEST_EDIT_ZOOM_CODE:
                if (data != null) {

                    //设置头像为刚刚选取裁剪的头像
                    setHeadImage(data);
                }
                break;
            case REQUEST_EDIT_NICK_NAME_CODE:

                String nickName = data.getStringExtra(EDIT_NICK_NAME_KEY);
                updateNickName(nickName);
                break;
        }
    }

    /**
     * 裁剪照片
     *
     * @param uri
     */
    private void startPhoneZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", UserConstant.USER_HEAD_IMAGE_WIDTH);
        intent.putExtra("outputY", UserConstant.USER_HEAD_IMAGE_HEIGHT);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_EDIT_ZOOM_CODE);
    }

    /**
     * 设置头像，并保持
     *
     * @param intent
     */
    private void setHeadImage(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Bitmap bitmap = bundle.getParcelable("data");
            mSaveHeadBitmap = bitmap;

            //先保持在本地磁盘，然后上传到阿里云。最后显示出来
            new SaveImageTask(bitmap).execute();
        }
    }

    private void updateNickName(final String nickName) {

        progressDialog = ProgressDialog.show(this, getString(R.string.text_dialog_update_nick),
                getString(R.string.text_dialog_waiting));

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    /**
     * 异步任务
     */
    private class SaveImageTask extends AsyncTask<Void, Void, String> {

        private Bitmap bitmap;
        private IUpload upload;

        public SaveImageTask(Bitmap bitmap) {
            this.bitmap = bitmap;
            upload = new UploadImpl();
        }

        /**
         * 跟新用户头像的oss存储地址
         */
        private void updateContactHeadImageUrl() {
            ContentValues updateValues = new ContentValues();
            updateValues.put(ContactsUserDao.COLUMN_NAME_HEADIMAGE_URL, URLConstant.OSS_MY_HEAD_IMAGE_RUL);
            userDao.updateContactByName(BaseApplication.getInstance().getUserName(), updateValues);
        }

        @Override
        protected String doInBackground(Void... params) {

            updateContactHeadImageUrl();
            try {
                return FileManager.saveBitmapToSDCard(bitmap, UserConstant.MY_HEAD_IMAGE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                //申请权限
                PermissionUtil.reqPermission(MyProfileActivity.this, PermissionUtil.PERMISSION_WRITE_EXT);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                Log.i(TAG, result);
                upload.upload(result);

                //最后设置设置当前的头像
                Glide.with(MyProfileActivity.this).load(new File(result)).into(mHeadImageView);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.PERMISSION_WRITE_EXT:

                if (mSaveHeadBitmap != null) {
                    //先保持在本地磁盘，然后上传到阿里云。最后显示出来
                    new SaveImageTask(mSaveHeadBitmap).execute();
                } else {
                    Log.i(TAG, "mSaveHeadBitmap == null");
                }
                break;
        }
    }
}
