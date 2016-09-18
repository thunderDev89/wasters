package com.acemen.android.wasters.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Audrik ! on 28/08/2016.
 * @see <a href="https://guides.codepath.com/android/Creating-Content-Providers">https://guides.codepath.com/android/Creating-Content-Providers</a>
 */
public class WasterzProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WasterzDBHelper mOpenHelper;

    // Use an int for each URI we will run, this represents the different queries
    private static final int WASTE = 100;
    private static final int WASTE_ID = 101;

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = WasterzContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, WasterzContract.PATH_WASTE, WASTE); // matches list of wastes
        matcher.addURI(content, WasterzContract.PATH_WASTE + "#", WASTE_ID); // matches waste by id

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WasterzDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case WASTE:
                retCursor = db.query(
                        WasterzContract.WasteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WASTE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        WasterzContract.WasteEntry.TABLE_NAME,
                        projection,
                        WasterzContract.WasteEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WASTE:
                return WasterzContract.WasteEntry.CONTENT_TYPE;
            case WASTE_ID:
                return WasterzContract.WasteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case WASTE:
                _id = db.insert(WasterzContract.WasteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = WasterzContract.WasteEntry.buildWasteUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case WASTE:
                rows = db.delete(WasterzContract.WasteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows
        if (rows != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case WASTE:
                rows = db.update(WasterzContract.WasteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }
}
