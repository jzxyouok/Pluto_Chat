package com.wl.pluto.plutochat.base;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.wl.pluto.plutochat.activity.MainActivity;
import com.wl.pluto.plutochat.constant.AppConstant;
import com.wl.pluto.plutochat.manager.ThreadPoolManager;
import com.wl.pluto.plutochat.utils.SDCardUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pluto on 16-1-5.
 */
public class BaseCrashHandler implements UncaughtExceptionHandler {


    /**
     * 标示
     */
    private static final String TAG = "--BaseCrashHandler-->";

    /**
     * 系统默认的UncaughtExceptionHandler
     */
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 要使用单例模式
     */
    private static BaseCrashHandler instance;

    /**
     * 上下文
     */
    private Context mContext;

    /**
     *
     */
    private String mHost;

    /**
     *
     */
    private String mUsername;

    /**
     *
     */
    private String mPassword;

    /**
     *
     */
    private String mEmainAddress;

    /**
     * 信息ｍａｐ
     */
    private HashMap<String, String> infoMap = new HashMap<>();

    // 用于格式化日期作为日志文件名的名称
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    private BaseCrashHandler(Context context) {
        this.mContext = context;
    }

    /**
     * 单例模式
     */
    public static BaseCrashHandler getInstance(Context context) {
        if (instance == null) {
            instance = new BaseCrashHandler(context);
        }

        return instance;
    }

    public void init() {

        //获取系统默认的UncaughtException类
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        //设置该BaseCrashHandler为本ａｐｐ默认的处理类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当系统崩溃时，就会进入这里来执行善后操作
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {

            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处�?收集错误信息 发�?错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信�?

        try {

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序出现异常,我们尽快修复。",
                            Toast.LENGTH_SHORT).show();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "error : ", e);
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);

                    Looper.loop();
                }
            }.start();
        } catch (Exception e) {

            // TODO: handle exception
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfoToFile(ex);
        return true;
    }

    /**
     * 收集设备信息
     *
     * @param context
     */
    private void collectDeviceInfo(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";

                infoMap.put(AppConstant.APP_VERSION_NAME, versionName);
                infoMap.put(AppConstant.APP_VERSION_CODE, versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {

                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param throwable
     */
    private String saveCrashInfoToFile(Throwable throwable) {

        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : infoMap.entrySet()) {

            String key = entry.getKey();

            String value = entry.getValue();

            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();

        PrintWriter printWriter = new PrintWriter(writer);

        throwable.printStackTrace(printWriter);

        Throwable cause = throwable.getCause();

        while (cause != null) {

            cause.printStackTrace(printWriter);

            cause = cause.getCause();
        }

        printWriter.close();

        String result = writer.toString();

        sb.append(result);

        try {

            String time = formatter.format(new Date());

            String fileName = "crash-" + time + "-" + ".log";

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                String path = SDCardUtils.getCrashPath();

                File dir = new File(path);

                if (!dir.exists()) {

                    dir.mkdirs();
                }

                send(sb.toString());

                FileOutputStream fos = new FileOutputStream(path + fileName);

                fos.write(sb.toString().getBytes());

                fos.close();
            }
            return fileName;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    private void send(final String cuowu) {
        new Thread(new Runnable() {

            @Override
            public void run() {
//                Email email = new Email();
//                email.setAddressTo(AddrTo);
//                email.setHost(host);
//                email.setPwd(userPass);
//                email.setUserName(userName);
//                try {
//                    email.send("【"getAppName()"】 手机厂商:"
//                            android.os.Build.MANUFACTURER"-手机型号:"
//                            android.os.Build.MODEL",错误报告.", new String(
//                                    cuowu.getBytes(), "UTF-8"));
//                    System.out.println("发送成功！");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();
    }

    private String getAppName() {
        ApplicationInfo android = mContext.getApplicationInfo();
        return mContext.getString(android.labelRes);
    }

    private void showDialog() {

        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setCancelable(false)
                        .setMessage("很抱歉,程序出现异常,异常已发送,我们尽快修复。")
                        .setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                  System.exit(0);
                                Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
                                PendingIntent restartIntent = PendingIntent.getActivity(
                                        mContext.getApplicationContext(), 0, intent,
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                // 退出程序
                                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent); // 1秒钟后重启应用
//                                  mContext.finishActivity();

                            }
                        }).create().show();
                Looper.loop();
            }
        });
    }
}
