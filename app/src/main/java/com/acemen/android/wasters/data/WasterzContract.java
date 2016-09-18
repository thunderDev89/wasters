package com.acemen.android.wasters.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Audrik ! on 28/08/2016.
 * @see <a href="https://guides.codepath.com/android/Creating-Content-Providers">https://guides.codepath.com/android/Creating-Content-Providers</a>
 */
public class WasterzContract {
    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.acemen.android.wasterz";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * List of tables for this database
     */
    public static final String PATH_WASTE = "waste";

    public static final class WasteEntry implements BaseColumns {
        // Content Uri represents the base location of the class
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WASTE).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WASTE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WASTE;

        // Table name
        public static final String TABLE_NAME = "waste";

        //Columns
        public static final String COLUMN_FIlENAME = "filename";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        public static final String COLUMN_CREATION_DATE = "creation_date";

        // Define a function to build a URI to find a specific waste by it's identifier
        public static Uri buildWasteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
