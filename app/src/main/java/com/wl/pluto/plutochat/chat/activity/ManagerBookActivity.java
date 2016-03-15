package com.wl.pluto.plutochat.chat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.aidl.BookEntity;
import com.wl.pluto.plutochat.chat.aidl.IBookManager;
import com.wl.pluto.plutochat.chat.aidl.IOnNewBookArrivedListener;
import com.wl.pluto.plutochat.chat.service.ManagerBookService;

import java.lang.ref.WeakReference;
import java.util.List;

public class ManagerBookActivity extends AppCompatActivity {

    private static final String TAG = "--ManagerBookActivity-->";

    private Button mGetBookButton;
    private Button mAddBookButton;
    private Button mRegisterButton;

    private IBookManager mBookManager;

    private NewBookHandler mBookHandler;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBookManager = IBookManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_book);
        initLayout();
        bindManagerBookService();


        mBookHandler = new NewBookHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);

        if (mBookManager != null) {
            try {
                mBookManager.unRegisterListener(bookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindManagerBookService() {
        Intent intent = new Intent(this, ManagerBookService.class);
        bindService(intent, serviceConnection, 1);


    }

    private void initLayout() {
        mGetBookButton = (Button) findViewById(R.id.btn_manager_get_book);
        mGetBookButton.setOnClickListener(clickListener);

        mAddBookButton = (Button) findViewById(R.id.btn_manager_add_book);
        mAddBookButton.setOnClickListener(clickListener);

        mRegisterButton = (Button) findViewById(R.id.btn_manager_register);
        mRegisterButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_manager_add_book:
                    onAddBookClick();
                    break;
                case R.id.btn_manager_get_book:
                    onGetBookClick();
                    break;
                case R.id.btn_manager_register:
                    onRegisterNewBookListener();
                    break;
            }
        }
    };

    private void onRegisterNewBookListener() {

        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.registerListener(bookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void onAddBookClick() {

        Log.i(TAG, "onAddBookClick");
        BookEntity book = new BookEntity(1001, "android progress");
        try {
            mBookManager.addBook(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void onGetBookClick() {

        Log.i(TAG, "onGetBookClick");

        try {
            List<BookEntity> bookEntities = mBookManager.getBookList();
            for (BookEntity entity : bookEntities) {
                Log.i(TAG, entity.toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IOnNewBookArrivedListener bookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(BookEntity book) throws RemoteException {

            Log.i(TAG, "onNewBookArrived");

            mBookHandler.obtainMessage(0, book).sendToTarget();
        }
    };

    /**
     *
     */
    private static class NewBookHandler extends Handler {

        private final WeakReference<ManagerBookActivity> bookActivityWeakReference;

        public NewBookHandler(ManagerBookActivity activity) {

            bookActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:

                    ManagerBookActivity bookActivity = bookActivityWeakReference.get();
                    bookActivity.printNewBook((BookEntity) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void printNewBook(BookEntity bookEntity) {
        Log.i(TAG, "receive a new book ");
        Log.i(TAG, "NewBook = " + bookEntity.toString());
    }
}
