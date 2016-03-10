package com.wl.pluto.plutochat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.service.MessengerService;

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "--MessengerActivity-->";

    /**
     * 与远程Service进行通信的Messenger(信使)
     */
    private Messenger mClientMessenger;

    /**
     *
     */
    private Messenger mGetReplyMessenger = new Messenger(new MessageHandler());

    private Button mStartMessengerServiceButton;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //利用服务端的service, 创建一个可以和服务的Service通信的messenger.
            mClientMessenger = new Messenger(service);

            //构造一个message对象, 这里的０就要和MessengerService中handlerMessage里面的case语句相对应。
            Message message = Message.obtain(null, 0);

            Bundle data = new Bundle();

            data.putString("msg", "this is client");

            message.setData(data);

            //将要接受从Service反馈回来处理消息的Messenger传递给远程Service, 远程的Service就是靠这个对象来给客户端发送回执消息的
            message.replyTo = mGetReplyMessenger;
            try {
                mClientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 处理从远程服务器端反馈回来的消息
     */
    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                //这里就需要和远程服务端构造消息的时候所需要的数据进行统一，里面构造message的时候用的是１，这里就需要１
                case 1:

                    //收到消息之后，那就处理了
                    String replyMessageFromService = msg.getData().getString("reply");
                    Log.i(TAG, "replyMessageFromService = " + replyMessageFromService);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        initLayout();
    }

    private void initLayout() {

        mStartMessengerServiceButton = (Button) findViewById(R.id.btn_messenger_start_service);
        mStartMessengerServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMessengerService();
            }
        });
    }

    private void startMessengerService() {
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, serviceConnection, 1);
    }
}
