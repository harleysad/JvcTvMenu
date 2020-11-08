package com.mediatek.wwtv.setting.util;

import android.accounts.Account;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class AccountImageChangeObserver {
    private static final boolean DEBUG = false;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final String TAG = "AccountImageChangeObserver";
    private static AccountImageChangeObserver sObserver;
    private static final Object sObserverInstanceLock = new Object();
    private HashMap<String, ContactChangeContentObserver> mObserverMap = new HashMap<>();

    private class ContactChangeContentObserver extends ContentObserver {
        private Context mContext;
        private String mCurrentImageUri;
        private Object mLock = new Object();
        private LinkedHashSet<Uri> mUrisToNotify;
        private Account mWatchedAccount;

        public ContactChangeContentObserver(Context context, Account watchedAccount) {
            super((Handler) null);
            this.mWatchedAccount = watchedAccount;
            this.mUrisToNotify = new LinkedHashSet<>();
            this.mContext = context;
            this.mCurrentImageUri = AccountImageHelper.getAccountPictureUri(this.mContext, this.mWatchedAccount);
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void addUriToNotifyList(Uri uri) {
            synchronized (this.mLock) {
                this.mUrisToNotify.add(uri);
            }
        }

        public void onChange(boolean selfChange) {
            String newUri = AccountImageHelper.getAccountPictureUri(this.mContext, this.mWatchedAccount);
            if (!TextUtils.equals(this.mCurrentImageUri, newUri)) {
                synchronized (this.mLock) {
                    Iterator it = this.mUrisToNotify.iterator();
                    while (it.hasNext()) {
                        this.mContext.getContentResolver().notifyChange((Uri) it.next(), (ContentObserver) null);
                    }
                    this.mCurrentImageUri = newUri;
                }
            }
        }
    }

    public static final AccountImageChangeObserver getInstance() {
        if (sObserver == null) {
            sObserver = new AccountImageChangeObserver();
        }
        return sObserver;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0090, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void registerChangeUriIfPresent(com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions r13) {
        /*
            r12 = this;
            monitor-enter(r12)
            android.net.Uri r0 = r13.getResourceUri()     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x008f
            boolean r1 = com.mediatek.wwtv.setting.util.UriUtils.isAccountImageUri(r0)     // Catch:{ all -> 0x0091 }
            if (r1 == 0) goto L_0x008f
            android.net.Uri r1 = com.mediatek.wwtv.setting.util.UriUtils.getAccountImageChangeNotifyUri(r0)     // Catch:{ all -> 0x0091 }
            android.net.Uri$Builder r2 = r0.buildUpon()     // Catch:{ all -> 0x0091 }
            android.net.Uri$Builder r2 = r2.clearQuery()     // Catch:{ all -> 0x0091 }
            android.net.Uri r2 = r2.build()     // Catch:{ all -> 0x0091 }
            r0 = r2
            if (r1 != 0) goto L_0x0022
            monitor-exit(r12)
            return
        L_0x0022:
            java.lang.String r2 = com.mediatek.wwtv.setting.util.UriUtils.getAccountName(r0)     // Catch:{ all -> 0x0091 }
            android.content.Context r3 = r13.getContext()     // Catch:{ all -> 0x0091 }
            if (r2 == 0) goto L_0x008f
            if (r3 == 0) goto L_0x008f
            r4 = 0
            android.accounts.AccountManager r5 = android.accounts.AccountManager.get(r3)     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = "com.google"
            android.accounts.Account[] r5 = r5.getAccountsByType(r6)     // Catch:{ all -> 0x0091 }
            int r6 = r5.length     // Catch:{ all -> 0x0091 }
            r7 = 0
            r8 = r7
        L_0x003c:
            if (r8 >= r6) goto L_0x004d
            r9 = r5[r8]     // Catch:{ all -> 0x0091 }
            java.lang.String r10 = r9.name     // Catch:{ all -> 0x0091 }
            boolean r10 = r10.equals(r2)     // Catch:{ all -> 0x0091 }
            if (r10 == 0) goto L_0x004a
            r4 = r9
            goto L_0x004d
        L_0x004a:
            int r8 = r8 + 1
            goto L_0x003c
        L_0x004d:
            if (r4 == 0) goto L_0x008f
            java.util.HashMap<java.lang.String, com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver> r5 = r12.mObserverMap     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = r4.name     // Catch:{ all -> 0x0091 }
            boolean r5 = r5.containsKey(r6)     // Catch:{ all -> 0x0091 }
            if (r5 == 0) goto L_0x0069
            java.util.HashMap<java.lang.String, com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver> r5 = r12.mObserverMap     // Catch:{ all -> 0x0091 }
            java.lang.String r6 = r4.name     // Catch:{ all -> 0x0091 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x0091 }
            com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver r5 = (com.mediatek.wwtv.setting.util.AccountImageChangeObserver.ContactChangeContentObserver) r5     // Catch:{ all -> 0x0091 }
            if (r5 == 0) goto L_0x008f
            r5.addUriToNotifyList(r1)     // Catch:{ all -> 0x0091 }
            goto L_0x008f
        L_0x0069:
            long r5 = r12.getContactIdForAccount(r3, r4)     // Catch:{ all -> 0x0091 }
            r8 = -1
            int r8 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x008f
            android.net.Uri r8 = android.provider.ContactsContract.Contacts.CONTENT_URI     // Catch:{ all -> 0x0091 }
            android.net.Uri r8 = android.content.ContentUris.withAppendedId(r8, r5)     // Catch:{ all -> 0x0091 }
            com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver r9 = new com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver     // Catch:{ all -> 0x0091 }
            r9.<init>(r3, r4)     // Catch:{ all -> 0x0091 }
            java.util.HashMap<java.lang.String, com.mediatek.wwtv.setting.util.AccountImageChangeObserver$ContactChangeContentObserver> r10 = r12.mObserverMap     // Catch:{ all -> 0x0091 }
            java.lang.String r11 = r4.name     // Catch:{ all -> 0x0091 }
            r10.put(r11, r9)     // Catch:{ all -> 0x0091 }
            r9.addUriToNotifyList(r1)     // Catch:{ all -> 0x0091 }
            android.content.ContentResolver r10 = r3.getContentResolver()     // Catch:{ all -> 0x0091 }
            r10.registerContentObserver(r8, r7, r9)     // Catch:{ all -> 0x0091 }
        L_0x008f:
            monitor-exit(r12)
            return
        L_0x0091:
            r13 = move-exception
            monitor-exit(r12)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.AccountImageChangeObserver.registerChangeUriIfPresent(com.mediatek.wwtv.setting.widget.view.BitmapWorkerOptions):void");
    }

    private long getContactIdForAccount(Context context, Account account) {
        Cursor c = null;
        long contactId = -1;
        String lookupKey = null;
        try {
            c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{"_id", "contact_id", "lookup"}, "data1 LIKE ?", new String[]{account.name}, (String) null);
            if (c.moveToNext()) {
                contactId = c.getLong(1);
                lookupKey = c.getString(2);
            }
            if (contactId == -1 || TextUtils.isEmpty(lookupKey)) {
                return -1;
            }
            return contactId;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
