package com.wl.pluto.plutochat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private static final String TAG = "--MessengerService-->";

    private final Messenger messenger = new Messenger(new MessengerHandler());

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        if (messenger != null) {
            return messenger.getBinder();
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 0:
                    //这里就对应的是客户端（MessengerActivity）中传递过来的数据
                    String result = msg.getData().getString("msg");
                    Log.i(TAG, "remote message = " + result);

                    //收到远程的消息之后，如果需要恢复客户端的话，再构造一个消息发送回去就ｏｋ了
                    Messenger client = msg.replyTo;
                    Message replyMessage = Message.obtain(null, 1);
                    Bundle data = new Bundle();
                    data.putString("reply", "i have received you message, i will reply you later !");
                    replyMessage.setData(data);

                    try {

                        //这里就是向远程的客户端发送回执消息
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
