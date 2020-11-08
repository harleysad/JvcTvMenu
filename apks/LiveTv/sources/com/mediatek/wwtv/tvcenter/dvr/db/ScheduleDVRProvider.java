package com.mediatek.wwtv.tvcenter.dvr.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class ScheduleDVRProvider extends ContentProvider {
    public static final String AUTHORITY = "dvr.alarm";
    public static final String CONTACTS_TABLE = "scheduledvr";
    public static final Uri CONTENT_URI = Uri.parse("content://dvr.alarm/scheduledvr");
    public static final int ITEM = 1;
    public static final int ITEM_ID = 2;
    private static final String TAG = "ScheduleDVRProvider";
    private static final UriMatcher uriMatcher = new UriMatcher(-1);
    private DBHelper mDbHelper;
    private SQLiteDatabase mDvrDB;

    static {
        uriMatcher.addURI(AUTHORITY, "scheduledvr", 1);
        uriMatcher.addURI(AUTHORITY, "scheduledvr/#", 2);
    }

    public boolean onCreate() {
        this.mDbHelper = DBHelper.getInstance(getContext());
        this.mDvrDB = this.mDbHelper.getWritableDatabase();
        this.mDbHelper.updateAlarm();
        return this.mDvrDB != null;
    }

    public int delete(Uri uri, String where, String[] selectionArgs) {
        int count;
        String str;
        MtkLog.e(TAG, "delete. " + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case 1:
                count = this.mDvrDB.delete("scheduledvr", where, selectionArgs);
                break;
            case 2:
                long contactID = ContentUris.parseId(uri);
                SQLiteDatabase sQLiteDatabase = this.mDvrDB;
                StringBuilder sb = new StringBuilder();
                sb.append("taskId=");
                sb.append(contactID);
                if (!TextUtils.isEmpty(where)) {
                    str = " and (" + where + ")";
                } else {
                    str = "";
                }
                sb.append(str);
                count = sQLiteDatabase.delete("scheduledvr", sb.toString(), selectionArgs);
                MtkLog.e(TAG, "delete. " + ContentUris.parseId(uri) + "___id:" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count2 = count;
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        this.mDbHelper.updateAlarm();
        return count2;
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case 1:
                return "";
            case 2:
                return "";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (uriMatcher.match(uri) == 1) {
            switch (uriMatcher.match(uri)) {
                case 1:
                    if (initialValues != null) {
                        values = new ContentValues(initialValues);
                    } else {
                        values = new ContentValues();
                    }
                    Long now1 = Long.valueOf(System.currentTimeMillis());
                    if (!values.containsKey("inputSrc")) {
                        values.put("inputSrc", "DTV");
                    }
                    if (!values.containsKey("channelNum")) {
                        values.put("channelNum", now1);
                    }
                    if (!values.containsKey("startTime")) {
                        values.put("startTime", "");
                    }
                    if (!values.containsKey("endTime")) {
                        values.put("endTime", "");
                    }
                    if (!values.containsKey("scheduleType")) {
                        values.put("scheduleType", -1);
                    }
                    if (!values.containsKey("repeatType")) {
                        values.put("repeatType", "");
                    }
                    if (!values.containsKey("dayofweek")) {
                        values.put("dayofweek", 0);
                    }
                    if (!values.containsKey("isEnable")) {
                        values.put("isEnable", 1);
                    }
                    if (!values.containsKey("channelID")) {
                        values.put("channelID", -1);
                    }
                    MtkLog.e(TAG, values.toString());
                    long rowId1 = this.mDvrDB.insert("scheduledvr", (String) null, values);
                    if (rowId1 > 0) {
                        Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId1);
                        getContext().getContentResolver().notifyChange(noteUri, (ContentObserver) null);
                        this.mDbHelper.updateAlarm();
                        MtkLog.e(TAG, "insert, Success.row: " + noteUri.toString());
                        return noteUri;
                    }
                    MtkLog.e(TAG, "insert, fail");
                    throw new SQLException("Failed to insert row into " + uri);
                case 2:
                    MtkLog.e(TAG, "case CONTACT_ID");
                    return null;
                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String orderBy;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case 1:
                qb.setTables("scheduledvr");
                break;
            case 2:
                qb.setTables("scheduledvr");
                qb.appendWhere("taskId=" + uri.getPathSegments().get(1));
                break;
        }
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = "taskId";
        } else {
            orderBy = sortOrder;
        }
        try {
            Cursor c = qb.query(this.mDvrDB, projection, selection, selectionArgs, (String) null, (String) null, orderBy);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    public int update(Uri uri, ContentValues values, String where, String[] selectionArgs) {
        int count;
        String str;
        String str2;
        MtkLog.e(TAG, "update, " + values.toString() + "," + uri.toString() + "," + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case 1:
                MtkLog.e(TAG, "update, 1");
                SQLiteDatabase sQLiteDatabase = this.mDvrDB;
                StringBuilder sb = new StringBuilder();
                sb.append("taskId=");
                sb.append(values.getAsString("taskId"));
                if (!TextUtils.isEmpty(where)) {
                    str = " AND (" + where + ")";
                } else {
                    str = "";
                }
                sb.append(str);
                count = sQLiteDatabase.update("scheduledvr", values, sb.toString(), selectionArgs);
                break;
            case 2:
                String contactID1 = uri.getPathSegments().get(1);
                MtkLog.e("ScheduleDVRProviderupdate", contactID1 + "");
                SQLiteDatabase sQLiteDatabase2 = this.mDvrDB;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("taskId=");
                sb2.append(contactID1);
                if (!TextUtils.isEmpty(where)) {
                    str2 = " AND (" + where + ")";
                } else {
                    str2 = "";
                }
                sb2.append(str2);
                count = sQLiteDatabase2.update("scheduledvr", values, sb2.toString(), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count2 = count;
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        this.mDbHelper.updateAlarm();
        return count2;
    }
}
