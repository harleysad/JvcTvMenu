package com.android.tv.settings.name;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class SuggestionContentProvider extends ContentProvider {
    private static final String EXTRA_IS_COMPLETE = "candidate_is_complete";
    private static final String GET_SUGGESTION_STATE_METHOD = "getSuggestionState";

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("query operation not supported currently.");
    }

    public String getType(Uri uri) {
        throw new UnsupportedOperationException("getType operation not supported currently.");
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert operation not supported currently.");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete operation not supported currently.");
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("update operation not supported currently.");
    }

    public Bundle call(String method, String arg, Bundle extras) {
        Bundle bundle = new Bundle();
        if (method.equals(GET_SUGGESTION_STATE_METHOD)) {
            bundle.putBoolean(EXTRA_IS_COMPLETE, DeviceNameSuggestionStatus.getInstance(getContext()).isFinished());
        }
        return bundle;
    }
}
