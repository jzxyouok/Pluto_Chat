package com.wl.pluto.plutochat.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.wl.pluto.plutochat.chat.manager.ThreadPoolManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerService extends Service {

    private static final String TAG = "--TCPServerService-->";

    private boolean isServiceDestroyed = false;

    public TCPServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ThreadPoolManager.getInstance().addTask(new TcpServerRunnable());
        return super.onStartCommand(intent, flags, startId);
    }

    private class TcpServerRunnable implements Runnable {

        @Override
        public void run() {

            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(54321);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            while (!isServiceDestroyed) {

                try {
                    final Socket socket = serverSocket.accept();

                    Log.i(TAG, "accept a request");

                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {

                            //恢复客户端
                            try {
                                responseClient(socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void responseClient(Socket client) throws IOException {

        //用于接受客户端的消息
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        //用于向客户端发送消息
        PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(client.getOutputStream()));

        printWriter.println("欢迎来到聊天室");
        Log.i(TAG, "欢迎来到聊天室");

        while (!isServiceDestroyed) {

            //这是客户端向服务器端发送的消息
            String str = bufferedReader.readLine();
            System.out.println("Client : " + str);
            Log.i(TAG, "Client :" + str);
            if (str == null) {
                break;
            }

            //这是向客户端发送的消息
            printWriter.println("哈哈哈");
        }

        if (printWriter != null) {
            printWriter.close();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }
        client.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDestroyed = true;
    }
}
