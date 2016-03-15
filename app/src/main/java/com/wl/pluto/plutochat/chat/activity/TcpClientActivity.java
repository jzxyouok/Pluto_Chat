package com.wl.pluto.plutochat.chat.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.service.TCPServerService;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class TcpClientActivity extends AppCompatActivity {

    private static final String TAG = "--TcpClientActivity-->";
    private EditText mMessageEditText;
    private Button mSendMessageButton;
    private TextView mMessageTextView;

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;

    private MessageHandler mHandler;

    private Socket mClientSocket;
    private PrintWriter mPrintWriter;

    private static class MessageHandler extends Handler {

        private final WeakReference<TcpClientActivity> activityWeakReference;

        public MessageHandler(TcpClientActivity activity) {

            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            TcpClientActivity activity = activityWeakReference.get();

            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    activity.onPrintMessage();
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    break;
            }
        }
    }

    private void onPrintMessage() {

        Log.i(TAG, "new message");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);
        initActivity();
        initLayout();

        startTcpServerService();
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectTCPServer();
            }
        }).start();
    }

    private void initActivity() {
        mHandler = new MessageHandler(this);
    }

    private void initLayout() {
        mMessageEditText = (EditText) findViewById(R.id.et_tcp_message_edit);
        mSendMessageButton = (Button) findViewById(R.id.btn_tcp_send);
        mSendMessageButton.setOnClickListener(clickListener);
        mMessageTextView = (TextView) findViewById(R.id.tv_tcp_message_text);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_tcp_send:
                    onSendMessageClick();
                    break;
            }
        }
    };

    private void onSendMessageClick() {

        String sendMessage = mMessageEditText.getText().toString();
        if (!TextUtils.isEmpty(sendMessage) && mPrintWriter != null) {
            mPrintWriter.println(sendMessage);
        }

        mMessageTextView.setText("\n" + sendMessage);
        mMessageEditText.setText("");

    }

    private void startTcpServerService() {
        Intent intent = new Intent(this, TCPServerService.class);
        startService(intent);
    }

    private void connectTCPServer() {

        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 54321);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedOutputStream(mClientSocket.getOutputStream()));
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
            }
        }

        try {
            //接受服务器端的消息
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));

            //
            while (!isFinishing()) {

                String msg = bufferedReader.readLine();
                Log.i(TAG, "from server msg = " + msg);

            }

            if (bufferedReader != null) {
                bufferedReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (mPrintWriter != null) {
                mPrintWriter.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
