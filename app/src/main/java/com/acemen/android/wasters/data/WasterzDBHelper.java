package com.acemen.android.wasters.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Audrik ! on 28/08/2016.
 * @see <a href="https://guides.codepath.com/android/Creating-Content-Providers">https://guides.codepath.com/android/Creating-Content-Providers</a>
 */
public class WasterzDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wasterz.db";

    public WasterzDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first created.
     * @param db The database being created, which all SQL statements will be executed on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        addWasteTable(db);
    }

    /**
     * Called whenever DATABASE_VERSION is incremented. This is used whenever schema changes need
     * to be made or new tables are added.
     * @param db The database being updated.
     * @param oldVersion The previous version of the database. Used to determine whether or not
     *                   certain updates should be run.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next line
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + WasterzContract.WasteEntry.TABLE_NAME);
        onCreate(db);
    }

    private void addWasteTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + WasterzContract.WasteEntry.TABLE_NAME + "(" +
                        WasterzContract.WasteEntry._ID + "INTEGER PRIMARY KEY, " +
                        WasterzContract.WasteEntry.COLUMN_FIlENAME + " TEXT UNQIUE NOT NULL, " +
                        WasterzContract.WasteEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                        WasterzContract.WasteEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        WasterzContract.WasteEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                        WasterzContract.WasteEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                        WasterzContract.WasteEntry.COLUMN_CREATION_DATE + " TEXT NOT NULL " +
                        ");"

        );
    }
}
