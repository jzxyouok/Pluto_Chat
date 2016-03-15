package com.wl.pluto.plutochat.chat.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.wl.pluto.plutochat.R;

public class MobileContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    /**
     * define an array that contains column names to move from the cursor to listview
     */
    private static final String[] FROM_COLUMS = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

    /**
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents.
     */
    private static final int[] TO_IDS = {R.id.tv_contacts_text};

    /**
     * 定义查询映射
     */
    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

    /**
     * the column index for the _ID column
     */
    private static final int CONTACT_ID_INDEX = 0;

    /**
     * The column index for the LOOKUP_KEY column
     */
    private static final int LOOKUP_KEY_INDEX = 1;

    private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + "LIKE ?";

    private String mSearchString;
    private String[] mSelectionArgs = {mSearchString};

    /**
     * listview
     */
    private ListView mContactsListView;

    private long mContactsId;

    private String mContactsKey;

    private Uri mContactsUri;

    private SimpleCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_contacts);

        initLayout();
        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);
    }

    private void initLayout() {
        mContactsListView = (ListView) findViewById(R.id.lv_contacts_list);
        mContactsListView.setOnItemClickListener(this);

        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.layout_contacts_list_item,
                null, FROM_COLUMS, TO_IDS, 0);
        mContactsListView.setAdapter(mCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
