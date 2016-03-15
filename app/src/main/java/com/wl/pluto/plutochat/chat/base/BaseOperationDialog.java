package com.wl.pluto.plutochat.chat.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.eventbus.ChatConversationDeleteEvent;

import de.greenrobot.event.EventBus;

public class BaseOperationDialog extends Dialog {

    public static final String TAG = "--BaseOperationDialog-->";

    /**
     * FrameworkChatFragment 链表长按的dialog数据
     */
    public static final int DIALOG_CHAT_DATA = 1;

    /**
     * FrameworkContactsFragment 链表长按的dialog数据
     */
    public static final int DIALOG_CONTACTS_DATA = 2;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 对话框链表的数据源
     */
    private String[] mDialogAdapterDataSource;

    /**
     * 链表适配器
     */
    private ArrayAdapter mDialogListViewAdapter;

    private String mToDeleteUserName;


    public static BaseOperationDialog getInstance(Context context, int dialogResId, String deleteUserName) {

        return new BaseOperationDialog(context, dialogResId, deleteUserName);
    }

    private BaseOperationDialog(Context context, int dialogResId, String deleteUserName) {

        super(context, R.style.operation_dialog_style);
        this.context = context;
        this.mToDeleteUserName = deleteUserName;
        initDataSource(dialogResId);

    }

    private void initDataSource(int dialogDataSourceType) {

        switch (dialogDataSourceType) {

            case DIALOG_CHAT_DATA:
                mDialogAdapterDataSource = context.getResources().getStringArray(R.array.dialog_chat_data);
                break;
            case DIALOG_CONTACTS_DATA:
                mDialogAdapterDataSource = context.getResources().getStringArray(R.array.dialog_contacts_data);
                break;
        }

        mDialogListViewAdapter = new ArrayAdapter(this.context,
                R.layout.layout_fragment_dialog_list_item, mDialogAdapterDataSource);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_operation_dialog);

        Log.i(TAG, "onCreate");
        initLayout();
    }

    private void initLayout() {

        /*
      对话框的链表
     */
        ListView mDialogListView = (ListView) findViewById(R.id.lv_operation_dialog_list);


        mDialogListView.setAdapter(mDialogListViewAdapter);

        mDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mDialogAdapterDataSource.length) {

                    //这是联系人链表的数据源
                    case 1: {
                        onContactsSetRemarksAndTagsClick();
                        break;
                    }
                    //这是Chat链表的数据源
                    case 3: {
                        switch (position) {
                            case 0:
                                onChatMarkAsUnreadClick();
                                break;
                            case 1:
                                onChatStickOnTopClick();
                                break;
                            case 2:
                                onChatDeleteConversationClick(mToDeleteUserName);
                                break;
                        }
                        break;
                    }
                }
            }
        });
    }

    /**
     * 设置备注及标签
     */
    private void onContactsSetRemarksAndTagsClick() {

        Log.i(TAG, "onContactsSetRemarksAndTagsClick");

    }

    /**
     * 标为未读
     */
    private void onChatMarkAsUnreadClick() {

        Log.i(TAG, "onChatMarkAsUnreadClick");

        dismiss();
    }

    /**
     * 置顶聊天
     */
    private void onChatStickOnTopClick() {

        Log.i(TAG, "onChatStickOnTopClick");

        dismiss();
    }

    /**
     * 删除该聊天
     */
    private void onChatDeleteConversationClick(String toDeleteUserName) {

        Log.i(TAG, "onChatDeleteConversationClick");
        if (EMChatManager.getInstance().deleteConversation(toDeleteUserName)) {

            Log.i(TAG, "Delete success ");
            EventBus.getDefault().post(new ChatConversationDeleteEvent());
        }
        dismiss();
    }


}
