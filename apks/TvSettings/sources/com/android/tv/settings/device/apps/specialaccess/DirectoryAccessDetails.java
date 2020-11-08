package com.android.tv.settings.device.apps.specialaccess;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Keep
public class DirectoryAccessDetails extends SettingsPreferenceFragment {
    public static final String ARG_PACKAGE_NAME = "package";
    private static final boolean DEBUG = false;
    private static final String TAG = "DirectoryAccessDetails";
    private static final boolean VERBOSE = false;
    private ApplicationsState.AppEntry mAppEntry;
    private boolean mCreated;
    private PackageInfo mPackageInfo;
    private String mPackageName;
    private PackageManager mPm;
    private ApplicationsState mState;
    private int mUserId;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mCreated) {
            Log.w(TAG, "onActivityCreated(): ignoring duplicate call");
            return;
        }
        this.mCreated = true;
        this.mPm = getActivity().getPackageManager();
        this.mState = ApplicationsState.getInstance(getActivity().getApplication());
        retrieveAppEntry();
        if (this.mPackageInfo == null) {
            Log.w(TAG, "onActivityCreated(): no package info");
            return;
        }
        getPreferenceScreen().addPreference(new PreferenceCategory(getPrefContext(), (AttributeSet) null));
        refreshUi();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.directory_access_details);
    }

    private Context getPrefContext() {
        return getPreferenceManager().getContext();
    }

    /* access modifiers changed from: protected */
    public Intent getIntent() {
        if (getActivity() == null) {
            return null;
        }
        return getActivity().getIntent();
    }

    /* access modifiers changed from: protected */
    public String retrieveAppEntry() {
        Bundle args = getArguments();
        this.mPackageName = args != null ? args.getString(ARG_PACKAGE_NAME) : null;
        Intent intent = args == null ? getIntent() : (Intent) args.getParcelable("intent");
        if (!(this.mPackageName != null || intent == null || intent.getData() == null)) {
            this.mPackageName = intent.getData().getSchemeSpecificPart();
        }
        if (intent == null || !intent.hasExtra("android.intent.extra.user_handle")) {
            this.mUserId = UserHandle.myUserId();
        } else {
            this.mUserId = ((UserHandle) intent.getParcelableExtra("android.intent.extra.user_handle")).getIdentifier();
        }
        this.mAppEntry = this.mState.getEntry(this.mPackageName, this.mUserId);
        if (this.mAppEntry != null) {
            try {
                this.mPackageInfo = this.mPm.getPackageInfoAsUser(this.mAppEntry.info.packageName, 134222336, this.mUserId);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Exception when retrieving package:" + this.mAppEntry.info.packageName, e);
            }
        } else {
            Log.w(TAG, "Missing AppEntry; maybe reinstalling?");
            this.mPackageInfo = null;
        }
        return this.mPackageName;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0288  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean refreshUi() {
        /*
            r23 = this;
            r15 = r23
            android.content.Context r14 = r23.getPrefContext()
            android.support.v7.preference.PreferenceScreen r13 = r23.getPreferenceScreen()
            r13.removeAll()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r12 = r0
            android.net.Uri$Builder r0 = new android.net.Uri$Builder
            r0.<init>()
            java.lang.String r1 = "content"
            android.net.Uri$Builder r0 = r0.scheme(r1)
            java.lang.String r1 = "com.android.documentsui.scopedAccess"
            android.net.Uri$Builder r0 = r0.authority(r1)
            java.lang.String r1 = "permissions"
            android.net.Uri$Builder r0 = r0.appendPath(r1)
            java.lang.String r1 = "*"
            android.net.Uri$Builder r0 = r0.appendPath(r1)
            android.net.Uri r17 = r0.build()
            android.content.ContentResolver r1 = r14.getContentResolver()
            java.lang.String[] r3 = android.os.storage.StorageVolume.ScopedAccessProviderContract.TABLE_PERMISSIONS_COLUMNS
            r0 = 1
            java.lang.String[] r5 = new java.lang.String[r0]
            java.lang.String r2 = r15.mPackageName
            r9 = 0
            r5[r9] = r2
            r4 = 0
            r6 = 0
            r2 = r17
            android.database.Cursor r10 = r1.query(r2, r3, r4, r5, r6)
            r11 = 0
            if (r10 != 0) goto L_0x007c
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.<init>()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = "Didn't get cursor for "
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = r15.mPackageName     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            android.util.Log.w(r1, r2)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            if (r10 == 0) goto L_0x006b
            r10.close()
        L_0x006b:
            return r0
        L_0x006c:
            r0 = move-exception
            r1 = r0
            r6 = r12
            r7 = r13
            r21 = r14
            goto L_0x0286
        L_0x0074:
            r0 = move-exception
            r11 = r0
            r6 = r12
            r7 = r13
            r21 = r14
            goto L_0x0283
        L_0x007c:
            int r1 = r10.getCount()     // Catch:{ Throwable -> 0x027d, all -> 0x0276 }
            r16 = r1
            if (r16 != 0) goto L_0x00a3
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.<init>()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = "No permissions for "
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = r15.mPackageName     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            android.util.Log.w(r1, r2)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            if (r10 == 0) goto L_0x00a2
            r10.close()
        L_0x00a2:
            return r0
        L_0x00a3:
            boolean r1 = r10.moveToNext()     // Catch:{ Throwable -> 0x027d, all -> 0x0276 }
            if (r1 == 0) goto L_0x015c
            java.lang.String r1 = r10.getString(r9)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r8 = r1
            java.lang.String r1 = r10.getString(r0)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r7 = r1
            r1 = 2
            java.lang.String r1 = r10.getString(r1)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r6 = r1
            r1 = 3
            int r1 = r10.getInt(r1)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            if (r1 != r0) goto L_0x00c2
            r1 = r0
            goto L_0x00c3
        L_0x00c2:
            r1 = r9
        L_0x00c3:
            r5 = r1
            java.lang.String r1 = r15.mPackageName     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            boolean r1 = r1.equals(r8)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            if (r1 != 0) goto L_0x00fd
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.<init>()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = "Ignoring "
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r7)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = "/"
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r6)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = " due to package mismatch: expected "
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = r15.mPackageName     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r3 = ", got "
            r2.append(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.append(r8)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            android.util.Log.w(r1, r2)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            goto L_0x00a3
        L_0x00fd:
            if (r7 != 0) goto L_0x0127
            if (r6 != 0) goto L_0x0109
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.String r2 = "Ignoring permission on primary storage root"
            android.util.Log.wtf(r1, r2)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            goto L_0x0158
        L_0x0109:
            r18 = 0
            r19 = 0
            r1 = r15
            r2 = r14
            r3 = r6
            r4 = r17
            r20 = r5
            r5 = r18
            r21 = r6
            r9 = r7
            r7 = r20
            r18 = r8
            r8 = r19
            android.support.v14.preference.SwitchPreference r1 = r1.newPreference(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r13.addPreference(r1)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            goto L_0x0158
        L_0x0127:
            r20 = r5
            r21 = r6
            r9 = r7
            r18 = r8
            java.lang.Object r1 = r12.get(r9)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails$ExternalVolume r1 = (com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails.ExternalVolume) r1     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            if (r1 != 0) goto L_0x013f
            com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails$ExternalVolume r2 = new com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails$ExternalVolume     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r2.<init>(r9)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r1 = r2
            r12.put(r9, r1)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
        L_0x013f:
            r2 = r21
            if (r2 != 0) goto L_0x0148
            r3 = r20
            r1.mGranted = r3     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            goto L_0x0158
        L_0x0148:
            r3 = r20
            java.util.List<android.util.Pair<java.lang.String, java.lang.Boolean>> r4 = r1.mChildren     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            android.util.Pair r5 = new android.util.Pair     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r3)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r5.<init>(r2, r6)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
            r4.add(r5)     // Catch:{ Throwable -> 0x0074, all -> 0x006c }
        L_0x0158:
            r9 = 0
            goto L_0x00a3
        L_0x015c:
            if (r10 == 0) goto L_0x0161
            r10.close()
        L_0x0161:
            boolean r1 = r12.isEmpty()
            if (r1 == 0) goto L_0x0168
            return r0
        L_0x0168:
            java.lang.Class<android.os.storage.StorageManager> r1 = android.os.storage.StorageManager.class
            java.lang.Object r1 = r14.getSystemService(r1)
            r11 = r1
            android.os.storage.StorageManager r11 = (android.os.storage.StorageManager) r11
            java.util.List r10 = r11.getVolumes()
            boolean r1 = r10.isEmpty()
            if (r1 == 0) goto L_0x0183
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.String r2 = "StorageManager returned no secondary volumes"
            android.util.Log.w(r1, r2)
            return r0
        L_0x0183:
            java.util.HashMap r1 = new java.util.HashMap
            int r2 = r10.size()
            r1.<init>(r2)
            r9 = r1
            java.util.Iterator r1 = r10.iterator()
        L_0x0191:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x01cd
            java.lang.Object r2 = r1.next()
            android.os.storage.VolumeInfo r2 = (android.os.storage.VolumeInfo) r2
            java.lang.String r3 = r2.getFsUuid()
            if (r3 != 0) goto L_0x01a4
            goto L_0x0191
        L_0x01a4:
            java.lang.String r4 = r11.getBestVolumeDescription(r2)
            if (r4 != 0) goto L_0x01c9
            java.lang.String r5 = "DirectoryAccessDetails"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "No description for "
            r6.append(r7)
            r6.append(r2)
            java.lang.String r7 = "; using uuid instead: "
            r6.append(r7)
            r6.append(r3)
            java.lang.String r6 = r6.toString()
            android.util.Log.w(r5, r6)
            r4 = r3
        L_0x01c9:
            r9.put(r3, r4)
            goto L_0x0191
        L_0x01cd:
            java.util.Collection r1 = r12.values()
            java.util.Iterator r7 = r1.iterator()
        L_0x01d5:
            boolean r1 = r7.hasNext()
            if (r1 == 0) goto L_0x026d
            java.lang.Object r1 = r7.next()
            r6 = r1
            com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails$ExternalVolume r6 = (com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails.ExternalVolume) r6
            java.lang.String r1 = r6.mUuid
            java.lang.Object r1 = r9.get(r1)
            r18 = r1
            java.lang.String r18 = (java.lang.String) r18
            if (r18 != 0) goto L_0x0207
            java.lang.String r1 = "DirectoryAccessDetails"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Ignoring entry for invalid UUID: "
            r2.append(r3)
            java.lang.String r3 = r6.mUuid
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Log.w(r1, r2)
            goto L_0x01d5
        L_0x0207:
            android.support.v7.preference.PreferenceCategory r1 = new android.support.v7.preference.PreferenceCategory
            r1.<init>(r14)
            r5 = r1
            r13.addPreference(r5)
            java.util.HashSet r8 = new java.util.HashSet
            java.util.List<android.util.Pair<java.lang.String, java.lang.Boolean>> r1 = r6.mChildren
            int r1 = r1.size()
            r8.<init>(r1)
            java.lang.String r4 = r6.mUuid
            r16 = 0
            boolean r3 = r6.mGranted
            r1 = r15
            r2 = r14
            r19 = r3
            r3 = r18
            r20 = r4
            r4 = r17
            r0 = r5
            r5 = r20
            r22 = r13
            r13 = r6
            r6 = r16
            r20 = r7
            r7 = r19
            android.support.v14.preference.SwitchPreference r1 = r1.newPreference(r2, r3, r4, r5, r6, r7, r8)
            r0.addPreference(r1)
            java.util.List<android.util.Pair<java.lang.String, java.lang.Boolean>> r1 = r13.mChildren
            com.android.tv.settings.device.apps.specialaccess.-$$Lambda$DirectoryAccessDetails$5q-FHkvtik46KXvkZRyoe_7nUb8 r2 = new com.android.tv.settings.device.apps.specialaccess.-$$Lambda$DirectoryAccessDetails$5q-FHkvtik46KXvkZRyoe_7nUb8
            r3 = r9
            r9 = r2
            r4 = r10
            r10 = r15
            r5 = r11
            r11 = r14
            r6 = r12
            r12 = r18
            r19 = r13
            r7 = r22
            r13 = r17
            r21 = r14
            r14 = r19
            r15 = r0
            r16 = r8
            r9.<init>(r11, r12, r13, r14, r15, r16)
            r1.forEach(r2)
            r15 = r23
            r9 = r3
            r10 = r4
            r11 = r5
            r12 = r6
            r13 = r7
            r7 = r20
            r14 = r21
            r0 = 1
            goto L_0x01d5
        L_0x026d:
            r3 = r9
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r13
            r21 = r14
            r0 = 1
            return r0
        L_0x0276:
            r0 = move-exception
            r6 = r12
            r7 = r13
            r21 = r14
            r1 = r0
            goto L_0x0286
        L_0x027d:
            r0 = move-exception
            r6 = r12
            r7 = r13
            r21 = r14
            r11 = r0
        L_0x0283:
            throw r11     // Catch:{ all -> 0x0284 }
        L_0x0284:
            r0 = move-exception
            r1 = r0
        L_0x0286:
            if (r10 == 0) goto L_0x0297
            if (r11 == 0) goto L_0x0294
            r10.close()     // Catch:{ Throwable -> 0x028e }
            goto L_0x0297
        L_0x028e:
            r0 = move-exception
            r2 = r0
            r11.addSuppressed(r2)
            goto L_0x0297
        L_0x0294:
            r10.close()
        L_0x0297:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails.refreshUi():boolean");
    }

    public static /* synthetic */ void lambda$refreshUi$0(DirectoryAccessDetails directoryAccessDetails, Context context, String volumeName, Uri providerUri, ExternalVolume volume, PreferenceCategory category, Set children, Pair pair) {
        Pair pair2 = pair;
        String dir = (String) pair2.first;
        SwitchPreference childPref = directoryAccessDetails.newPreference(context, context.getResources().getString(R.string.directory_on_volume, new Object[]{volumeName, dir}), providerUri, volume.mUuid, dir, ((Boolean) pair2.second).booleanValue(), (Set<SwitchPreference>) null);
        category.addPreference(childPref);
        children.add(childPref);
    }

    private SwitchPreference newPreference(Context context, String title, Uri providerUri, String uuid, String dir, boolean granted, Set<SwitchPreference> children) {
        Context context2 = context;
        SwitchPreference pref = new SwitchPreference(context2);
        pref.setKey(String.format("%s:%s", new Object[]{uuid, dir}));
        pref.setTitle((CharSequence) title);
        pref.setChecked(granted);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(context2, providerUri, uuid, dir, children) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ Uri f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ Set f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return DirectoryAccessDetails.lambda$newPreference$1(DirectoryAccessDetails.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, preference, obj);
            }
        });
        return pref;
    }

    public static /* synthetic */ boolean lambda$newPreference$1(DirectoryAccessDetails directoryAccessDetails, Context context, Uri providerUri, String uuid, String dir, Set children, Preference unused, Object value) {
        if (!Boolean.class.isInstance(value)) {
            Log.wtf(TAG, "Invalid value from switch: " + value);
            return true;
        }
        boolean newValue = ((Boolean) value).booleanValue();
        directoryAccessDetails.resetDoNotAskAgain(context, newValue, providerUri, uuid, dir);
        if (children != null) {
            boolean newChildValue = !newValue;
            Iterator it = children.iterator();
            while (it.hasNext()) {
                ((SwitchPreference) it.next()).setVisible(newChildValue);
            }
        }
        return true;
    }

    private void resetDoNotAskAgain(Context context, boolean newValue, Uri providerUri, String uuid, String directory) {
        ContentValues values = new ContentValues(1);
        values.put("granted", Boolean.valueOf(newValue));
        int update = context.getContentResolver().update(providerUri, values, (String) null, new String[]{this.mPackageName, uuid, directory});
    }

    public int getMetricsCategory() {
        return 1284;
    }

    private static class ExternalVolume {
        final List<Pair<String, Boolean>> mChildren = new ArrayList();
        boolean mGranted;
        final String mUuid;

        ExternalVolume(String uuid) {
            this.mUuid = uuid;
        }

        public String toString() {
            return "ExternalVolume: [uuid=" + this.mUuid + ", granted=" + this.mGranted + ", children=" + this.mChildren + "]";
        }
    }
}
