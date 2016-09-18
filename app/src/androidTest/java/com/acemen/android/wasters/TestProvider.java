package com.acemen.android.wasters;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.acemen.android.wasters.data.WasterzContract;
import com.acemen.android.wasters.data.WasterzDBHelper;

/**
 * Created by Audrik ! on 28/08/2016.
 */
public class TestProvider extends AndroidTestCase {
    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                WasterzContract.WasteEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WasterzContract.WasteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from waste table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void testBasicWasteQuery() {
        WasterzDBHelper dbHelper = new WasterzDBHelper(mContext);

        ContentValues values = TestUtilities.createFakeWaste();
        long rowId = TestUtilities.insertFakeWasteValues(mContext);

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                WasterzContract.WasteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicWasteQuery", cursor, values);
    }
}
