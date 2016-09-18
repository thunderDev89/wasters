package com.acemen.android.wasters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.acemen.android.wasters.data.WasterzContract;
import com.acemen.android.wasters.data.WasterzDBHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Audrik ! on 28/08/2016.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createFakeWaste() {
        ContentValues wasteValues = new ContentValues();
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_FIlENAME, "null");
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_CREATION_DATE, "2016-08-23 22:49:10.000000");
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_TYPE, "any-waste");
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_COORD_LONG, 2.22951550);
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_COORD_LAT, 48.87327720);
        wasteValues.put(WasterzContract.WasteEntry.COLUMN_ADDRESS, "Pantin");

        return wasteValues;
    }

    static long insertFakeWasteValues(Context context) {
        WasterzDBHelper dbHelper = new WasterzDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues fakeValues = TestUtilities.createFakeWaste();

        long rowId = db.insert(WasterzContract.WasteEntry.TABLE_NAME, null, fakeValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", rowId != -1);
        return rowId;
    }
}
