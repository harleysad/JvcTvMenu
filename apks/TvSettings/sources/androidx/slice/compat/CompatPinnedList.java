package androidx.slice.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.ArraySet;
import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;
import androidx.slice.SliceSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class CompatPinnedList {
    private static final long BOOT_THRESHOLD = 2000;
    private static final String LAST_BOOT = "last_boot";
    private static final String PIN_PREFIX = "pinned_";
    private static final String SPEC_NAME_PREFIX = "spec_names_";
    private static final String SPEC_REV_PREFIX = "spec_revs_";
    private final Context mContext;
    private final String mPrefsName;

    public CompatPinnedList(Context context, String prefsName) {
        this.mContext = context;
        this.mPrefsName = prefsName;
    }

    private SharedPreferences getPrefs() {
        SharedPreferences prefs = this.mContext.getSharedPreferences(this.mPrefsName, 0);
        long lastBootTime = prefs.getLong(LAST_BOOT, 0);
        long currentBootTime = getBootTime();
        if (Math.abs(lastBootTime - currentBootTime) > BOOT_THRESHOLD) {
            prefs.edit().clear().putLong(LAST_BOOT, currentBootTime).commit();
        }
        return prefs;
    }

    public List<Uri> getPinnedSlices() {
        List<Uri> pinned = new ArrayList<>();
        for (String key : getPrefs().getAll().keySet()) {
            if (key.startsWith(PIN_PREFIX)) {
                Uri uri = Uri.parse(key.substring(PIN_PREFIX.length()));
                if (!getPins(uri).isEmpty()) {
                    pinned.add(uri);
                }
            }
        }
        return pinned;
    }

    private Set<String> getPins(Uri uri) {
        SharedPreferences prefs = getPrefs();
        return prefs.getStringSet(PIN_PREFIX + uri.toString(), new ArraySet());
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:13:0x0063=Splitter:B:13:0x0063, B:20:0x007b=Splitter:B:20:0x007b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized android.support.v4.util.ArraySet<androidx.slice.SliceSpec> getSpecs(android.net.Uri r11) {
        /*
            r10 = this;
            monitor-enter(r10)
            android.support.v4.util.ArraySet r0 = new android.support.v4.util.ArraySet     // Catch:{ all -> 0x0082 }
            r0.<init>()     // Catch:{ all -> 0x0082 }
            android.content.SharedPreferences r1 = r10.getPrefs()     // Catch:{ all -> 0x0082 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0082 }
            r2.<init>()     // Catch:{ all -> 0x0082 }
            java.lang.String r3 = "spec_names_"
            r2.append(r3)     // Catch:{ all -> 0x0082 }
            java.lang.String r3 = r11.toString()     // Catch:{ all -> 0x0082 }
            r2.append(r3)     // Catch:{ all -> 0x0082 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0082 }
            r3 = 0
            java.lang.String r2 = r1.getString(r2, r3)     // Catch:{ all -> 0x0082 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0082 }
            r4.<init>()     // Catch:{ all -> 0x0082 }
            java.lang.String r5 = "spec_revs_"
            r4.append(r5)     // Catch:{ all -> 0x0082 }
            java.lang.String r5 = r11.toString()     // Catch:{ all -> 0x0082 }
            r4.append(r5)     // Catch:{ all -> 0x0082 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0082 }
            java.lang.String r3 = r1.getString(r4, r3)     // Catch:{ all -> 0x0082 }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0082 }
            if (r4 != 0) goto L_0x007b
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0082 }
            if (r4 == 0) goto L_0x004a
            goto L_0x007b
        L_0x004a:
            java.lang.String r4 = ","
            r5 = -1
            java.lang.String[] r4 = r2.split(r4, r5)     // Catch:{ all -> 0x0082 }
            java.lang.String r6 = ","
            java.lang.String[] r5 = r3.split(r6, r5)     // Catch:{ all -> 0x0082 }
            int r6 = r4.length     // Catch:{ all -> 0x0082 }
            int r7 = r5.length     // Catch:{ all -> 0x0082 }
            if (r6 == r7) goto L_0x0062
            android.support.v4.util.ArraySet r6 = new android.support.v4.util.ArraySet     // Catch:{ all -> 0x0082 }
            r6.<init>()     // Catch:{ all -> 0x0082 }
            monitor-exit(r10)
            return r6
        L_0x0062:
            r6 = 0
        L_0x0063:
            int r7 = r4.length     // Catch:{ all -> 0x0082 }
            if (r6 >= r7) goto L_0x0079
            androidx.slice.SliceSpec r7 = new androidx.slice.SliceSpec     // Catch:{ all -> 0x0082 }
            r8 = r4[r6]     // Catch:{ all -> 0x0082 }
            r9 = r5[r6]     // Catch:{ all -> 0x0082 }
            int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ all -> 0x0082 }
            r7.<init>(r8, r9)     // Catch:{ all -> 0x0082 }
            r0.add(r7)     // Catch:{ all -> 0x0082 }
            int r6 = r6 + 1
            goto L_0x0063
        L_0x0079:
            monitor-exit(r10)
            return r0
        L_0x007b:
            android.support.v4.util.ArraySet r4 = new android.support.v4.util.ArraySet     // Catch:{ all -> 0x0082 }
            r4.<init>()     // Catch:{ all -> 0x0082 }
            monitor-exit(r10)
            return r4
        L_0x0082:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.CompatPinnedList.getSpecs(android.net.Uri):android.support.v4.util.ArraySet");
    }

    private void setPins(Uri uri, Set<String> pins) {
        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putStringSet(PIN_PREFIX + uri.toString(), pins).commit();
    }

    private void setSpecs(Uri uri, ArraySet<SliceSpec> specs) {
        String[] specNames = new String[specs.size()];
        String[] specRevs = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            specNames[i] = specs.valueAt(i).getType();
            specRevs[i] = String.valueOf(specs.valueAt(i).getRevision());
        }
        SharedPreferences.Editor edit = getPrefs().edit();
        SharedPreferences.Editor putString = edit.putString(SPEC_NAME_PREFIX + uri.toString(), TextUtils.join(",", specNames));
        putString.putString(SPEC_REV_PREFIX + uri.toString(), TextUtils.join(",", specRevs)).commit();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public long getBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public synchronized boolean addPin(Uri uri, String pkg, Set<SliceSpec> specs) {
        boolean wasNotPinned;
        Set<String> pins = getPins(uri);
        wasNotPinned = pins.isEmpty();
        pins.add(pkg);
        setPins(uri, pins);
        if (wasNotPinned) {
            setSpecs(uri, new ArraySet(specs));
        } else {
            setSpecs(uri, mergeSpecs(getSpecs(uri), specs));
        }
        return wasNotPinned;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0022, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0024, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean removePin(android.net.Uri r4, java.lang.String r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.util.Set r0 = r3.getPins(r4)     // Catch:{ all -> 0x0025 }
            boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x0025 }
            r2 = 0
            if (r1 != 0) goto L_0x0023
            boolean r1 = r0.contains(r5)     // Catch:{ all -> 0x0025 }
            if (r1 != 0) goto L_0x0013
            goto L_0x0023
        L_0x0013:
            r0.remove(r5)     // Catch:{ all -> 0x0025 }
            r3.setPins(r4, r0)     // Catch:{ all -> 0x0025 }
            int r1 = r0.size()     // Catch:{ all -> 0x0025 }
            if (r1 != 0) goto L_0x0021
            r2 = 1
        L_0x0021:
            monitor-exit(r3)
            return r2
        L_0x0023:
            monitor-exit(r3)
            return r2
        L_0x0025:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.CompatPinnedList.removePin(android.net.Uri, java.lang.String):boolean");
    }

    private static ArraySet<SliceSpec> mergeSpecs(ArraySet<SliceSpec> specs, Set<SliceSpec> supportedSpecs) {
        int i;
        int i2 = 0;
        while (i2 < specs.size()) {
            SliceSpec s = specs.valueAt(i2);
            SliceSpec other = findSpec(supportedSpecs, s.getType());
            if (other == null) {
                i = i2 - 1;
                specs.removeAt(i2);
            } else if (other.getRevision() < s.getRevision()) {
                i = i2 - 1;
                specs.removeAt(i2);
                specs.add(other);
            } else {
                i = i2;
            }
            i2 = i + 1;
        }
        return specs;
    }

    private static SliceSpec findSpec(Set<SliceSpec> specs, String type) {
        for (SliceSpec spec : specs) {
            if (ObjectsCompat.equals(spec.getType(), type)) {
                return spec;
            }
        }
        return null;
    }
}
