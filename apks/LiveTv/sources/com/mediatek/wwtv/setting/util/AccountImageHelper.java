package com.mediatek.wwtv.setting.util;

import android.accounts.Account;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.mediatek.wwtv.tvcenter.R;

public final class AccountImageHelper {
    static final String[] CONTACT_PROJECTION_DATA = {"_id", "contact_id", "raw_contact_id", "lookup", "photo_uri", "photo_file_id"};
    static final String CONTACT_SELECTION = "data1 LIKE ?";

    private AccountImageHelper() {
    }

    /* JADX INFO: finally extract failed */
    public static String getAccountPictureUri(Context context, Account account) {
        Cursor c = null;
        long contactId = -1;
        String lookupKey = null;
        String photoUri = null;
        int photoFileId = 0;
        long rawContactId = 0;
        try {
            Cursor c2 = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, CONTACT_PROJECTION_DATA, CONTACT_SELECTION, new String[]{account.name}, (String) null);
            if (c2.moveToNext()) {
                contactId = c2.getLong(1);
                rawContactId = c2.getLong(2);
                lookupKey = c2.getString(3);
                photoUri = c2.getString(4);
                photoFileId = c2.getInt(5);
            }
            if (c2 != null) {
                c2.close();
            }
            if (contactId == -1 || TextUtils.isEmpty(lookupKey) || TextUtils.isEmpty(photoUri)) {
                Context context2 = context;
                return getDefaultPictureUri(context);
            }
            if (photoFileId == 0) {
                syncContactHiResPhoto(context, rawContactId);
            } else {
                Context context3 = context;
            }
            return photoUri;
        } catch (Throwable th) {
            Context context4 = context;
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private static void syncContactHiResPhoto(Context context, long rawContactId) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId);
        Intent intent = new Intent();
        intent.setClassName("com.google.android.syncadapters.contacts", "com.google.android.syncadapters.contacts.SyncHighResPhotoIntentService");
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(uri, "vnd.android.cursor.item/raw_contact");
        try {
            context.startService(intent);
        } catch (Exception e) {
        }
    }

    public static String getDefaultPictureUri(Context context) {
        Intent.ShortcutIconResource iconResource = new Intent.ShortcutIconResource();
        iconResource.packageName = context.getPackageName();
        iconResource.resourceName = context.getResources().getResourceName(R.drawable.default_contact_picture);
        return UriUtils.getShortcutIconResourceUri(iconResource).toString();
    }
}
