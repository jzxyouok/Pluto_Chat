package com.wl.pluto.plutochat.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.activity.MainActivity;
import com.wl.pluto.plutochat.activity.MainFrameworkActivity;

import java.util.HashSet;
import java.util.Locale;

/**
 * Created by jeck on 15-11-20.
 */
public class BaseNotification {

    private static final String TAG = "--BaseNotification-->";

    /**
     * 响铃
     */
    private Ringtone mRingtone;

    /**
     * 消息类型
     */
    protected final static String[] msg_en = {"sent a message",
            "sent a picture", "sent a voice", "sent location message",
            "sent a video", "sent a file", "%1 contacts sent %2 messages"};

    protected final static String[] msg_ch = {"发来一条消息", "发来一张图片", "发来一段语音",
            "发来位置信息", "发来一个视频", "发来一个文件", "%1个联系人发来%2条消息"};

    protected static int notificationId = 1001;

    protected static int foregroundNotifyID = 1002;

    protected NotificationManager notificationManager;

    protected HashSet<String> fromUsers = new HashSet<>();

    protected int notificationNum = 0;

    protected Context context;

    protected String packageName;

    protected String[] messages;

    protected long lastNotificationTime;

    protected AudioManager audioManager;

    protected Vibrator vibrator;

    protected NotificationInfoProvider notificationInfoProvider = null;

    public interface NotificationInfoProvider {

        /**
         * 设置发送notification时，状态栏提示的新消息的内容(如，xxx发来一条图片消息)
         *
         * @param message
         * @return
         */
        String getDisplayedText(EMMessage message);

        /**
         * 设置notification持续显示的消息提示(如有２个联系人发来了５条消息)
         *
         * @param message
         * @param fromUserNum
         * @param messageNum
         * @return
         */
        String getLatestText(EMMessage message, int fromUserNum, int messageNum);

        /**
         * 设置notification的标题
         *
         * @return
         */
        String getNotificationTitle(EMMessage message);

        /**
         * 设置小图标
         *
         * @param message
         * @return
         */
        int getSmallIcon(EMMessage message);

        /**
         * 设置notification点击时跳转的intent;
         *
         * @param message
         * @return
         */
        Intent getLaunchIntent(EMMessage message);
    }

    public BaseNotification() {

    }

    /**
     * 你可以重载次函数
     *
     * @param context
     * @return
     */
    public BaseNotification init(Context context) {
        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = context.getApplicationInfo().packageName;

        //选择语言
        if (Locale.getDefault().getLanguage().equals("zh")) {

            messages = msg_ch;
        } else {
            messages = msg_en;
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        return this;
    }

    public void reset() {

        resetNotificationCount();
        cancelNotification();
    }

    private void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }

    private void cancelNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

    /**
     * 处理新收到的消息，然后发送通知
     */
    public synchronized void onNewMessage(EMMessage message) {

        Log.i(TAG, "onNewMessage");

        //判断是否为静音状态？
        if (EMChatManager.getInstance().isSlientMessage(message)) {
            return;
        }

        //判断App是否在后台运行
        if (EasyUtils.isAppRunningForeground(context)) {

            //前台运行
            sendNotification(message, true);
        } else {
            //在后台运行，　就是按下home键，app暂时看不到了
            sendNotification(message, false);

        }
    }

    /**
     * 发送通知消息, 是否在后台
     *
     * @param message
     * @param isForeground
     */
    public void sendNotification(EMMessage message, boolean isForeground) {

        Log.i(TAG, "sendNotification");
        sendNotification(message, isForeground, true);
    }

    /**
     * 发送通知消息，消息数目是否自增长
     *
     * @param message
     * @param isForeground
     * @param numIncrease
     */
    public void sendNotification(EMMessage message, boolean isForeground, boolean numIncrease) {

        //获取发送者
        String username = message.getFrom();

        try {

            String ticker = username + " ";
            switch (message.getType()) {

                case TXT:
                    ticker += messages[0];
                    break;
                case IMAGE:
                    ticker += messages[1];
                    break;
                case VOICE:
                    ticker += messages[2];
                    break;
                case LOCATION:
                    ticker += messages[3];
                    break;
                case VIDEO:
                    ticker += messages[4];
                    break;
                case FILE:
                    ticker += messages[5];
                    break;

            }

            PackageManager manager = context.getPackageManager();
            String appName = (String) manager.getApplicationLabel(context.getApplicationInfo());

            String contentTitle = appName;

            //这个地方为什么要用接口呢？它背后隐含的逻辑是什么？
            if (notificationInfoProvider != null) {

                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customNotifyTitle = notificationInfoProvider.getNotificationTitle(message);

                if (customNotifyText != null) {
                    ticker = customNotifyText;
                }

                if (customNotifyTitle != null) {
                    contentTitle = customNotifyTitle;
                }
            }

            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setTicker(ticker);
            builder.setWhen(System.currentTimeMillis());
            builder.setAutoCancel(true);

            //Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            Intent intent = new Intent(context, MainFrameworkActivity.class);

            if (notificationInfoProvider != null) {

                //设置自定义的跳转intent
                intent = notificationInfoProvider.getLaunchIntent(message);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            if (numIncrease) {
                if (!isForeground) {
                    notificationNum++;
                    fromUsers.add(message.getFrom());
                }
            }

            int fromUserNum = fromUsers.size();

            String contentText = messages[6].replaceFirst("%1", Integer.toString(fromUserNum))
                    .replaceFirst("%2", Integer.toString(notificationNum));

            if (notificationInfoProvider != null) {
                String customSummeryBody = notificationInfoProvider.getLatestText(message, fromUserNum, notificationNum);

                if (customSummeryBody != null) {
                    contentText = customSummeryBody;
                }

                int smallIcon = notificationInfoProvider.getSmallIcon(message);

                if (smallIcon != 0) {
                    builder.setSmallIcon(smallIcon);
                }
            }

            //这个地方应该显示PlutoChat
            builder.setContentTitle(contentTitle);

            //xx发了xx条消息
            builder.setContentText(contentText);

            //设置点击的启动界面。这个地方就需要涉及到Activity的启动模式了
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notificationId, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置该接口
     *
     * @param provider
     */
    public void setNotificationInfoProvider(NotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    /**
     * 手机震动和声音
     *
     * @param message
     */
    public void vibrateAndPlayTone(EMMessage message) {

        if (message != null) {
            if (EMChatManager.getInstance().isSlientMessage(message)) {
                return;
            }
        }

        BaseLogicModel model = BaseHelper.getInstance().getBaseLogicModel();
        if (!model.isSettingMsgNotification()) {
            return;
        }


        if (System.currentTimeMillis() - lastNotificationTime < 1000) {
            return;
        }

        try {

            lastNotificationTime = System.currentTimeMillis();

            //判断是否处于静音状态
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return;
            }

            if (model.isSettingMsgVibrate()) {
                long[] patten = new long[]{0, 180, 80, 120};
                vibrator.vibrate(patten, -1);
            }

            if (model.isSettingMsgSound()) {
                if (mRingtone == null) {
                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    mRingtone = RingtoneManager.getRingtone(context, notificationUri);

                    if (mRingtone == null) {
                        EMLog.e(TAG, "cant find ringtone at " + notificationUri.getPath());
                        return;
                    }
                }

                if (!mRingtone.isPlaying()) {

                    String vendor = Build.MANUFACTURER;
                    mRingtone.play();

                    if (vendor != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    Thread.sleep(3000);

                                    if (mRingtone.isPlaying()) {
                                        mRingtone.stop();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
