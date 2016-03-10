package com.wl.pluto.plutochat.utils;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 6.0权限管理
 * <p>
 * Created by pluto on 1/26/16.
 */
public class PermissionUtil {

    public static final int PERMISSION_WRITE_EXT = 10;

    public static void reqPermission(Activity aty, int reqCode) {

        String permission = getPermission(reqCode);

        if (TextUtils.isEmpty(permission)) {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(aty, permission)) {

            Toast.makeText(aty, "please give me the permission", Toast.LENGTH_SHORT).show();
        } else {

            //进行权限请求
            ActivityCompat.requestPermissions(aty, new String[]{permission}, reqCode);
        }
    }

    private static String getPermission(int request) {
        switch (request) {
            case PERMISSION_WRITE_EXT:
                return Manifest.permission.WRITE_EXTERNAL_STORAGE;
            default:
                return null;
        }
    }
}
