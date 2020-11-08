package androidx.slice.compat;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.util.ArraySet;
import android.support.v4.util.Preconditions;
import android.util.Log;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.core.SliceHints;
import androidx.versionedparcelable.ParcelUtils;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class SliceProviderCompat {
    private static final String ALL_FILES = "slice_data_all_slice_files";
    public static final String ARG_SUPPORTS_VERSIONED_PARCELABLE = "supports_versioned_parcelable";
    private static final String DATA_PREFIX = "slice_data_";
    public static final String EXTRA_BIND_URI = "slice_uri";
    public static final String EXTRA_INTENT = "slice_intent";
    public static final String EXTRA_PID = "pid";
    public static final String EXTRA_PKG = "pkg";
    public static final String EXTRA_PROVIDER_PKG = "provider_pkg";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_SLICE = "slice";
    public static final String EXTRA_SLICE_DESCENDANTS = "slice_descendants";
    public static final String EXTRA_SUPPORTED_SPECS = "specs";
    public static final String EXTRA_SUPPORTED_SPECS_REVS = "revs";
    public static final String EXTRA_UID = "uid";
    public static final String METHOD_CHECK_PERMISSION = "check_perms";
    public static final String METHOD_GET_DESCENDANTS = "get_descendants";
    public static final String METHOD_GET_PINNED_SPECS = "get_specs";
    public static final String METHOD_GRANT_PERMISSION = "grant_perms";
    public static final String METHOD_MAP_INTENT = "map_slice";
    public static final String METHOD_MAP_ONLY_INTENT = "map_only";
    public static final String METHOD_PIN = "pin_slice";
    public static final String METHOD_REVOKE_PERMISSION = "revoke_perms";
    public static final String METHOD_SLICE = "bind_slice";
    public static final String METHOD_UNPIN = "unpin_slice";
    public static final String PERMS_PREFIX = "slice_perms_";
    private static final long SLICE_BIND_ANR = 2000;
    private static final String TAG = "SliceProviderCompat";
    private final Runnable mAnr = new Runnable() {
        public void run() {
            Process.sendSignal(Process.myPid(), 3);
            Log.wtf(SliceProviderCompat.TAG, "Timed out while handling slice callback " + SliceProviderCompat.this.mCallback);
        }
    };
    /* access modifiers changed from: private */
    public String mCallback;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private CompatPermissionManager mPermissionManager;
    private CompatPinnedList mPinnedList;
    private final SliceProvider mProvider;

    public SliceProviderCompat(SliceProvider provider, CompatPermissionManager permissionManager, Context context) {
        this.mProvider = provider;
        this.mContext = context;
        String prefsFile = DATA_PREFIX + getClass().getName();
        SharedPreferences allFiles = this.mContext.getSharedPreferences(ALL_FILES, 0);
        Set<String> files = allFiles.getStringSet(ALL_FILES, Collections.emptySet());
        if (!files.contains(prefsFile)) {
            Set<String> files2 = new ArraySet<>(files);
            files2.add(prefsFile);
            allFiles.edit().putStringSet(ALL_FILES, files2).commit();
        }
        this.mPinnedList = new CompatPinnedList(this.mContext, prefsFile);
        this.mPermissionManager = permissionManager;
    }

    private Context getContext() {
        return this.mContext;
    }

    public String getCallingPackage() {
        return this.mProvider.getCallingPackage();
    }

    public Bundle call(String method, String arg, Bundle extras) {
        Parcelable parcelable = null;
        if (method.equals(METHOD_SLICE)) {
            Slice s = handleBindSlice((Uri) extras.getParcelable(EXTRA_BIND_URI), getSpecs(extras), getCallingPackage());
            Bundle b = new Bundle();
            if (ARG_SUPPORTS_VERSIONED_PARCELABLE.equals(arg)) {
                if (s != null) {
                    parcelable = ParcelUtils.toParcelable(s);
                }
                b.putParcelable(EXTRA_SLICE, parcelable);
            } else {
                if (s != null) {
                    parcelable = s.toBundle();
                }
                b.putParcelable(EXTRA_SLICE, parcelable);
            }
            return b;
        } else if (method.equals(METHOD_MAP_INTENT)) {
            Uri uri = this.mProvider.onMapIntentToUri((Intent) extras.getParcelable(EXTRA_INTENT));
            Bundle b2 = new Bundle();
            if (uri != null) {
                Slice s2 = handleBindSlice(uri, getSpecs(extras), getCallingPackage());
                if (ARG_SUPPORTS_VERSIONED_PARCELABLE.equals(arg)) {
                    if (s2 != null) {
                        parcelable = ParcelUtils.toParcelable(s2);
                    }
                    b2.putParcelable(EXTRA_SLICE, parcelable);
                } else {
                    if (s2 != null) {
                        parcelable = s2.toBundle();
                    }
                    b2.putParcelable(EXTRA_SLICE, parcelable);
                }
            } else {
                b2.putParcelable(EXTRA_SLICE, (Parcelable) null);
            }
            return b2;
        } else if (method.equals(METHOD_MAP_ONLY_INTENT)) {
            Uri uri2 = this.mProvider.onMapIntentToUri((Intent) extras.getParcelable(EXTRA_INTENT));
            Bundle b3 = new Bundle();
            b3.putParcelable(EXTRA_SLICE, uri2);
            return b3;
        } else if (method.equals(METHOD_PIN)) {
            Uri uri3 = (Uri) extras.getParcelable(EXTRA_BIND_URI);
            Set<SliceSpec> specs = getSpecs(extras);
            if (this.mPinnedList.addPin(uri3, extras.getString(EXTRA_PKG), specs)) {
                handleSlicePinned(uri3);
            }
            return null;
        } else if (method.equals(METHOD_UNPIN)) {
            Uri uri4 = (Uri) extras.getParcelable(EXTRA_BIND_URI);
            if (this.mPinnedList.removePin(uri4, extras.getString(EXTRA_PKG))) {
                handleSliceUnpinned(uri4);
            }
            return null;
        } else if (method.equals(METHOD_GET_PINNED_SPECS)) {
            Bundle b4 = new Bundle();
            addSpecs(b4, this.mPinnedList.getSpecs((Uri) extras.getParcelable(EXTRA_BIND_URI)));
            return b4;
        } else if (method.equals(METHOD_GET_DESCENDANTS)) {
            Bundle b5 = new Bundle();
            b5.putParcelableArrayList(EXTRA_SLICE_DESCENDANTS, new ArrayList(handleGetDescendants((Uri) extras.getParcelable(EXTRA_BIND_URI))));
            return b5;
        } else if (method.equals(METHOD_CHECK_PERMISSION)) {
            String string = extras.getString(EXTRA_PKG);
            int pid = extras.getInt(EXTRA_PID);
            int uid = extras.getInt(EXTRA_UID);
            Bundle b6 = new Bundle();
            b6.putInt(EXTRA_RESULT, this.mPermissionManager.checkSlicePermission((Uri) extras.getParcelable(EXTRA_BIND_URI), pid, uid));
            return b6;
        } else {
            if (method.equals(METHOD_GRANT_PERMISSION)) {
                Uri uri5 = (Uri) extras.getParcelable(EXTRA_BIND_URI);
                String toPkg = extras.getString(EXTRA_PKG);
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.grantSlicePermission(uri5, toPkg);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            } else if (method.equals(METHOD_REVOKE_PERMISSION)) {
                Uri uri6 = (Uri) extras.getParcelable(EXTRA_BIND_URI);
                String toPkg2 = extras.getString(EXTRA_PKG);
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.revokeSlicePermission(uri6, toPkg2);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            }
            return null;
        }
    }

    private Collection<Uri> handleGetDescendants(Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return this.mProvider.onGetSliceDescendants(uri);
    }

    private void handleSlicePinned(Uri sliceUri) {
        this.mCallback = "onSlicePinned";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            this.mProvider.onSlicePinned(sliceUri);
            this.mProvider.handleSlicePinned(sliceUri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private void handleSliceUnpinned(Uri sliceUri) {
        this.mCallback = "onSliceUnpinned";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            this.mProvider.onSliceUnpinned(sliceUri);
            this.mProvider.handleSliceUnpinned(sliceUri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private Slice handleBindSlice(Uri sliceUri, Set<SliceSpec> specs, String callingPkg) {
        String pkg = callingPkg != null ? callingPkg : getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        if (this.mPermissionManager.checkSlicePermission(sliceUri, Binder.getCallingPid(), Binder.getCallingUid()) == 0) {
            return onBindSliceStrict(sliceUri, specs);
        }
        SliceProvider sliceProvider = this.mProvider;
        return SliceProvider.createPermissionSlice(getContext(), sliceUri, pkg);
    }

    private Slice onBindSliceStrict(Uri sliceUri, Set<SliceSpec> specs) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
        this.mCallback = "onBindSlice";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build());
            SliceProvider.setSpecs(specs);
            Slice onBindSlice = this.mProvider.onBindSlice(sliceUri);
            SliceProvider.setSpecs((Set<SliceSpec>) null);
            this.mHandler.removeCallbacks(this.mAnr);
            StrictMode.setThreadPolicy(oldPolicy);
            return onBindSlice;
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(oldPolicy);
            throw th;
        }
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder holder = acquireClient(context.getContentResolver(), uri);
        if (holder.mProvider != null) {
            try {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_BIND_URI, uri);
                addSpecs(extras, supportedSpecs);
                Bundle res = holder.mProvider.call(METHOD_SLICE, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
                if (res == null) {
                    return null;
                }
                res.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcel = res.getParcelable(EXTRA_SLICE);
                if (parcel == null) {
                    return null;
                }
                if (parcel instanceof Bundle) {
                    return new Slice((Bundle) parcel);
                }
                return (Slice) ParcelUtils.fromParcelable(parcel);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to bind slice", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static void addSpecs(Bundle extras, Set<SliceSpec> supportedSpecs) {
        ArrayList<String> types = new ArrayList<>();
        ArrayList<Integer> revs = new ArrayList<>();
        for (SliceSpec spec : supportedSpecs) {
            types.add(spec.getType());
            revs.add(Integer.valueOf(spec.getRevision()));
        }
        extras.putStringArrayList(EXTRA_SUPPORTED_SPECS, types);
        extras.putIntegerArrayList(EXTRA_SUPPORTED_SPECS_REVS, revs);
    }

    public static Set<SliceSpec> getSpecs(Bundle extras) {
        ArraySet<SliceSpec> specs = new ArraySet<>();
        ArrayList<String> types = extras.getStringArrayList(EXTRA_SUPPORTED_SPECS);
        ArrayList<Integer> revs = extras.getIntegerArrayList(EXTRA_SUPPORTED_SPECS_REVS);
        if (!(types == null || revs == null)) {
            for (int i = 0; i < types.size(); i++) {
                specs.add(new SliceSpec(types.get(i), revs.get(i).intValue()));
            }
        }
        return specs;
    }

    public static Slice bindSlice(Context context, Intent intent, Set<SliceSpec> supportedSpecs) {
        Preconditions.checkNotNull(intent, "intent");
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, String.format("Slice intent must be explicit %s", new Object[]{intent}));
        ContentResolver resolver = context.getContentResolver();
        Uri intentData = intent.getData();
        if (intentData != null && "vnd.android.slice".equals(resolver.getType(intentData))) {
            return bindSlice(context, intentData, supportedSpecs);
        }
        Intent queryIntent = new Intent(intent);
        if (!queryIntent.hasCategory("android.app.slice.category.SLICE")) {
            queryIntent.addCategory("android.app.slice.category.SLICE");
        }
        List<ResolveInfo> providers = context.getPackageManager().queryIntentContentProviders(queryIntent, 0);
        if (providers == null || providers.isEmpty()) {
            ResolveInfo resolve = context.getPackageManager().resolveActivity(intent, 128);
            if (resolve == null || resolve.activityInfo == null || resolve.activityInfo.metaData == null || !resolve.activityInfo.metaData.containsKey(SliceHints.SLICE_METADATA_KEY)) {
                return null;
            }
            return bindSlice(context, Uri.parse(resolve.activityInfo.metaData.getString(SliceHints.SLICE_METADATA_KEY)), supportedSpecs);
        }
        Uri uri = new Uri.Builder().scheme(BaseDialogFragment.TAG_CONTENT).authority(providers.get(0).providerInfo.authority).build();
        ProviderHolder holder = acquireClient(resolver, uri);
        if (holder.mProvider != null) {
            try {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_INTENT, intent);
                addSpecs(extras, supportedSpecs);
                Bundle res = holder.mProvider.call(METHOD_MAP_INTENT, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
                if (res == null) {
                    return null;
                }
                res.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcel = res.getParcelable(EXTRA_SLICE);
                if (parcel == null) {
                    return null;
                }
                if (parcel instanceof Bundle) {
                    return new Slice((Bundle) parcel);
                }
                return (Slice) ParcelUtils.fromParcelable(parcel);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to bind slice", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static void pinSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder holder = acquireClient(context.getContentResolver(), uri);
        if (holder.mProvider != null) {
            try {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_BIND_URI, uri);
                extras.putString(EXTRA_PKG, context.getPackageName());
                addSpecs(extras, supportedSpecs);
                holder.mProvider.call(METHOD_PIN, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to pin slice", e);
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static void unpinSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        ProviderHolder holder = acquireClient(context.getContentResolver(), uri);
        if (holder.mProvider != null) {
            try {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_BIND_URI, uri);
                extras.putString(EXTRA_PKG, context.getPackageName());
                addSpecs(extras, supportedSpecs);
                holder.mProvider.call(METHOD_UNPIN, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to unpin slice", e);
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static Set<SliceSpec> getPinnedSpecs(Context context, Uri uri) {
        ProviderHolder holder = acquireClient(context.getContentResolver(), uri);
        if (holder.mProvider != null) {
            try {
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_BIND_URI, uri);
                Bundle res = holder.mProvider.call(METHOD_GET_PINNED_SPECS, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
                if (res != null) {
                    return getSpecs(res);
                }
                return null;
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to get pinned specs", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00d1, code lost:
        r8 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00d2, code lost:
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d6, code lost:
        r9 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d7, code lost:
        r12 = r9;
        r9 = r8;
        r8 = r12;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri mapIntentToUri(android.content.Context r13, android.content.Intent r14) {
        /*
            java.lang.String r0 = "intent"
            android.support.v4.util.Preconditions.checkNotNull(r14, r0)
            android.content.ComponentName r0 = r14.getComponent()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x001c
            java.lang.String r0 = r14.getPackage()
            if (r0 != 0) goto L_0x001c
            android.net.Uri r0 = r14.getData()
            if (r0 == 0) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            r0 = r2
            goto L_0x001d
        L_0x001c:
            r0 = r1
        L_0x001d:
            java.lang.String r3 = "Slice intent must be explicit %s"
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r14
            java.lang.String r1 = java.lang.String.format(r3, r1)
            android.support.v4.util.Preconditions.checkArgument(r0, r1)
            android.content.ContentResolver r0 = r13.getContentResolver()
            android.net.Uri r1 = r14.getData()
            if (r1 == 0) goto L_0x0041
            java.lang.String r3 = "vnd.android.slice"
            java.lang.String r4 = r0.getType(r1)
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0041
            return r1
        L_0x0041:
            android.content.Intent r3 = new android.content.Intent
            r3.<init>(r14)
            java.lang.String r4 = "android.app.slice.category.SLICE"
            boolean r4 = r3.hasCategory(r4)
            if (r4 != 0) goto L_0x0053
            java.lang.String r4 = "android.app.slice.category.SLICE"
            r3.addCategory(r4)
        L_0x0053:
            android.content.pm.PackageManager r4 = r13.getPackageManager()
            java.util.List r4 = r4.queryIntentContentProviders(r3, r2)
            r5 = 0
            if (r4 == 0) goto L_0x00f4
            boolean r6 = r4.isEmpty()
            if (r6 == 0) goto L_0x0067
            goto L_0x00f4
        L_0x0067:
            java.lang.Object r2 = r4.get(r2)
            android.content.pm.ResolveInfo r2 = (android.content.pm.ResolveInfo) r2
            android.content.pm.ProviderInfo r2 = r2.providerInfo
            java.lang.String r2 = r2.authority
            android.net.Uri$Builder r6 = new android.net.Uri$Builder
            r6.<init>()
            java.lang.String r7 = "content"
            android.net.Uri$Builder r6 = r6.scheme(r7)
            android.net.Uri$Builder r6 = r6.authority(r2)
            android.net.Uri r6 = r6.build()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r7 = acquireClient(r0, r6)     // Catch:{ RemoteException -> 0x00eb }
            android.content.ContentProviderClient r8 = r7.mProvider     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            if (r8 == 0) goto L_0x00ba
            android.os.Bundle r8 = new android.os.Bundle     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            r8.<init>()     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            java.lang.String r9 = "slice_intent"
            r8.putParcelable(r9, r14)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            android.content.ContentProviderClient r9 = r7.mProvider     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            java.lang.String r10 = "map_only"
            java.lang.String r11 = "supports_versioned_parcelable"
            android.os.Bundle r9 = r9.call(r10, r11, r8)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            if (r9 == 0) goto L_0x00b4
            java.lang.String r10 = "slice"
            android.os.Parcelable r10 = r9.getParcelable(r10)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            android.net.Uri r10 = (android.net.Uri) r10     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            if (r7 == 0) goto L_0x00b3
            r7.close()     // Catch:{ RemoteException -> 0x00eb }
        L_0x00b3:
            return r10
        L_0x00b4:
            if (r7 == 0) goto L_0x00b9
            r7.close()     // Catch:{ RemoteException -> 0x00eb }
        L_0x00b9:
            goto L_0x00f3
        L_0x00ba:
            java.lang.IllegalArgumentException r8 = new java.lang.IllegalArgumentException     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            r9.<init>()     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            java.lang.String r10 = "Unknown URI "
            r9.append(r10)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            r9.append(r6)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            java.lang.String r9 = r9.toString()     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            r8.<init>(r9)     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
            throw r8     // Catch:{ Throwable -> 0x00d4, all -> 0x00d1 }
        L_0x00d1:
            r8 = move-exception
            r9 = r5
            goto L_0x00da
        L_0x00d4:
            r8 = move-exception
            throw r8     // Catch:{ all -> 0x00d6 }
        L_0x00d6:
            r9 = move-exception
            r12 = r9
            r9 = r8
            r8 = r12
        L_0x00da:
            if (r7 == 0) goto L_0x00ea
            if (r9 == 0) goto L_0x00e7
            r7.close()     // Catch:{ Throwable -> 0x00e2 }
            goto L_0x00ea
        L_0x00e2:
            r10 = move-exception
            r9.addSuppressed(r10)     // Catch:{ RemoteException -> 0x00eb }
            goto L_0x00ea
        L_0x00e7:
            r7.close()     // Catch:{ RemoteException -> 0x00eb }
        L_0x00ea:
            throw r8     // Catch:{ RemoteException -> 0x00eb }
        L_0x00eb:
            r7 = move-exception
            java.lang.String r8 = "SliceProviderCompat"
            java.lang.String r9 = "Unable to map slice"
            android.util.Log.e(r8, r9, r7)
        L_0x00f3:
            return r5
        L_0x00f4:
            android.content.pm.PackageManager r2 = r13.getPackageManager()
            r6 = 128(0x80, float:1.794E-43)
            android.content.pm.ResolveInfo r2 = r2.resolveActivity(r14, r6)
            if (r2 == 0) goto L_0x0125
            android.content.pm.ActivityInfo r6 = r2.activityInfo
            if (r6 == 0) goto L_0x0125
            android.content.pm.ActivityInfo r6 = r2.activityInfo
            android.os.Bundle r6 = r6.metaData
            if (r6 == 0) goto L_0x0125
            android.content.pm.ActivityInfo r6 = r2.activityInfo
            android.os.Bundle r6 = r6.metaData
            java.lang.String r7 = "android.metadata.SLICE_URI"
            boolean r6 = r6.containsKey(r7)
            if (r6 == 0) goto L_0x0125
            android.content.pm.ActivityInfo r5 = r2.activityInfo
            android.os.Bundle r5 = r5.metaData
            java.lang.String r6 = "android.metadata.SLICE_URI"
            java.lang.String r5 = r5.getString(r6)
            android.net.Uri r5 = android.net.Uri.parse(r5)
            return r5
        L_0x0125:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.mapIntentToUri(android.content.Context, android.content.Intent):android.net.Uri");
    }

    @NonNull
    public static Collection<Uri> getSliceDescendants(Context context, @NonNull Uri uri) {
        ProviderHolder holder;
        try {
            holder = acquireClient(context.getContentResolver(), uri);
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_BIND_URI, uri);
            Bundle res = holder.mProvider.call(METHOD_GET_DESCENDANTS, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            if (res != null) {
                ArrayList parcelableArrayList = res.getParcelableArrayList(EXTRA_SLICE_DESCENDANTS);
                if (holder != null) {
                    holder.close();
                }
                return parcelableArrayList;
            }
            if (holder != null) {
                holder.close();
            }
            return Collections.emptyList();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get slice descendants", e);
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    public static int checkSlicePermission(Context context, String packageName, Uri uri, int pid, int uid) {
        ProviderHolder holder;
        try {
            holder = acquireClient(context.getContentResolver(), uri);
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_BIND_URI, uri);
            extras.putString(EXTRA_PKG, packageName);
            extras.putInt(EXTRA_PID, pid);
            extras.putInt(EXTRA_UID, uid);
            Bundle res = holder.mProvider.call(METHOD_CHECK_PERMISSION, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            if (res != null) {
                int i = res.getInt(EXTRA_RESULT);
                if (holder != null) {
                    holder.close();
                }
                return i;
            } else if (holder == null) {
                return -1;
            } else {
                holder.close();
                return -1;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to check slice permission", e);
            return -1;
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    public static void grantSlicePermission(Context context, String packageName, String toPackage, Uri uri) {
        ProviderHolder holder;
        try {
            holder = acquireClient(context.getContentResolver(), uri);
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_BIND_URI, uri);
            extras.putString(EXTRA_PROVIDER_PKG, packageName);
            extras.putString(EXTRA_PKG, toPackage);
            holder.mProvider.call(METHOD_GRANT_PERMISSION, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            if (holder != null) {
                holder.close();
                return;
            }
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get slice descendants", e);
            return;
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    public static void revokeSlicePermission(Context context, String packageName, String toPackage, Uri uri) {
        ProviderHolder holder;
        try {
            holder = acquireClient(context.getContentResolver(), uri);
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_BIND_URI, uri);
            extras.putString(EXTRA_PROVIDER_PKG, packageName);
            extras.putString(EXTRA_PKG, toPackage);
            holder.mProvider.call(METHOD_REVOKE_PERMISSION, ARG_SUPPORTS_VERSIONED_PARCELABLE, extras);
            if (holder != null) {
                holder.close();
                return;
            }
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get slice descendants", e);
            return;
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    public static List<Uri> getPinnedSlices(Context context) {
        ArrayList<Uri> pinnedSlices = new ArrayList<>();
        for (String pref : context.getSharedPreferences(ALL_FILES, 0).getStringSet(ALL_FILES, Collections.emptySet())) {
            pinnedSlices.addAll(new CompatPinnedList(context, pref).getPinnedSlices());
        }
        return pinnedSlices;
    }

    private static ProviderHolder acquireClient(ContentResolver resolver, Uri uri) {
        ContentProviderClient provider = resolver.acquireContentProviderClient(uri);
        if (provider != null) {
            return new ProviderHolder(provider);
        }
        throw new IllegalArgumentException("No provider found for " + uri);
    }

    private static class ProviderHolder implements AutoCloseable {
        /* access modifiers changed from: private */
        public final ContentProviderClient mProvider;

        ProviderHolder(ContentProviderClient provider) {
            this.mProvider = provider;
        }

        public void close() {
            if (this.mProvider != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    this.mProvider.close();
                } else {
                    this.mProvider.release();
                }
            }
        }
    }
}
