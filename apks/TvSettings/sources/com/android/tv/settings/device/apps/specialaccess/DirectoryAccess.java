package com.android.tv.settings.device.apps.specialaccess;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import java.util.Set;

@Keep
public class DirectoryAccess extends ManageAppOp {
    private static final boolean DEBUG = false;
    private static final ApplicationsState.AppFilter FILTER_APP_HAS_DIRECTORY_ACCESS = new ApplicationsState.AppFilter() {
        private Set<String> mPackages;

        public void init() {
            throw new UnsupportedOperationException("Need to call constructor that takes context");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:23:0x007b, code lost:
            if (r2 != null) goto L_0x007d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
            if (r0 != null) goto L_0x007f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0083, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0084, code lost:
            r0.addSuppressed(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0088, code lost:
            r2.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x004c, code lost:
            r3 = move-exception;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void init(android.content.Context r7) {
            /*
                r6 = this;
                r0 = 0
                r6.mPackages = r0
                android.net.Uri$Builder r1 = new android.net.Uri$Builder
                r1.<init>()
                java.lang.String r2 = "content"
                android.net.Uri$Builder r1 = r1.scheme(r2)
                java.lang.String r2 = "com.android.documentsui.scopedAccess"
                android.net.Uri$Builder r1 = r1.authority(r2)
                java.lang.String r2 = "packages"
                android.net.Uri$Builder r1 = r1.appendPath(r2)
                java.lang.String r2 = "*"
                android.net.Uri$Builder r1 = r1.appendPath(r2)
                android.net.Uri r1 = r1.build()
                android.content.ContentResolver r2 = r7.getContentResolver()
                java.lang.String[] r3 = android.os.storage.StorageVolume.ScopedAccessProviderContract.TABLE_PACKAGES_COLUMNS
                android.database.Cursor r2 = r2.query(r1, r3, r0, r0)
                if (r2 != 0) goto L_0x0050
                java.lang.String r3 = "DirectoryAccess"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x004e }
                r4.<init>()     // Catch:{ Throwable -> 0x004e }
                java.lang.String r5 = "Didn't get cursor for "
                r4.append(r5)     // Catch:{ Throwable -> 0x004e }
                r4.append(r1)     // Catch:{ Throwable -> 0x004e }
                java.lang.String r4 = r4.toString()     // Catch:{ Throwable -> 0x004e }
                android.util.Log.w(r3, r4)     // Catch:{ Throwable -> 0x004e }
                if (r2 == 0) goto L_0x004b
                r2.close()
            L_0x004b:
                return
            L_0x004c:
                r3 = move-exception
                goto L_0x007b
            L_0x004e:
                r0 = move-exception
                goto L_0x007a
            L_0x0050:
                int r3 = r2.getCount()     // Catch:{ Throwable -> 0x004e }
                if (r3 != 0) goto L_0x005c
                if (r2 == 0) goto L_0x005b
                r2.close()
            L_0x005b:
                return
            L_0x005c:
                android.util.ArraySet r4 = new android.util.ArraySet     // Catch:{ Throwable -> 0x004e }
                r4.<init>(r3)     // Catch:{ Throwable -> 0x004e }
                r6.mPackages = r4     // Catch:{ Throwable -> 0x004e }
            L_0x0063:
                boolean r4 = r2.moveToNext()     // Catch:{ Throwable -> 0x004e }
                if (r4 == 0) goto L_0x0074
                java.util.Set<java.lang.String> r4 = r6.mPackages     // Catch:{ Throwable -> 0x004e }
                r5 = 0
                java.lang.String r5 = r2.getString(r5)     // Catch:{ Throwable -> 0x004e }
                r4.add(r5)     // Catch:{ Throwable -> 0x004e }
                goto L_0x0063
            L_0x0074:
                if (r2 == 0) goto L_0x0079
                r2.close()
            L_0x0079:
                return
            L_0x007a:
                throw r0     // Catch:{ all -> 0x004c }
            L_0x007b:
                if (r2 == 0) goto L_0x008b
                if (r0 == 0) goto L_0x0088
                r2.close()     // Catch:{ Throwable -> 0x0083 }
                goto L_0x008b
            L_0x0083:
                r4 = move-exception
                r0.addSuppressed(r4)
                goto L_0x008b
            L_0x0088:
                r2.close()
            L_0x008b:
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.device.apps.specialaccess.DirectoryAccess.AnonymousClass1.init(android.content.Context):void");
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            return this.mPackages != null && this.mPackages.contains(info.info.packageName);
        }
    };
    private static final String TAG = "DirectoryAccess";

    public ApplicationsState.AppFilter getAppFilter() {
        return FILTER_APP_HAS_DIRECTORY_ACCESS;
    }

    public int getAppOpsOpCode() {
        return -1;
    }

    public String getPermission() {
        return "android.permission.MANAGE_SCOPED_ACCESS_DIRECTORY_PERMISSIONS";
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.directory_access, (String) null);
    }

    public int getMetricsCategory() {
        return 1283;
    }

    public Preference bindPreference(Preference preference, ApplicationsState.AppEntry entry) {
        preference.setTitle((CharSequence) entry.label);
        preference.setKey(entry.info.packageName);
        preference.setIcon(entry.icon);
        preference.setFragment(DirectoryAccessDetails.class.getCanonicalName());
        preference.getExtras().putString(DirectoryAccessDetails.ARG_PACKAGE_NAME, entry.info.packageName);
        return preference;
    }

    public Preference createAppPreference() {
        return new Preference(getPreferenceManager().getContext());
    }

    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }
}
