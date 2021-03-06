package androidx.slice;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.CoreComponentFactory;
import android.support.v4.os.BuildCompat;
import androidx.slice.Slice;
import androidx.slice.compat.CompatPermissionManager;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.compat.SliceProviderWrapperContainer;
import androidx.slice.core.R;
import androidx.slice.core.SliceHints;
import com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SliceProvider extends ContentProvider implements CoreComponentFactory.CompatWrapped {
    private static final boolean DEBUG = false;
    private static final String TAG = "SliceProvider";
    private static Clock sClock;
    private static Set<SliceSpec> sSpecs;
    private final String[] mAutoGrantPermissions;
    private SliceProviderCompat mCompat;
    private List<Uri> mPinnedSliceUris;

    public abstract Slice onBindSlice(Uri uri);

    public abstract boolean onCreateSliceProvider();

    public SliceProvider(@NonNull String... autoGrantPermissions) {
        this.mAutoGrantPermissions = autoGrantPermissions;
    }

    public SliceProvider() {
        this.mAutoGrantPermissions = new String[0];
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public Object getWrapper() {
        if (BuildCompat.isAtLeastP()) {
            return new SliceProviderWrapperContainer.SliceProviderWrapper(this, this.mAutoGrantPermissions);
        }
        return null;
    }

    public final boolean onCreate() {
        this.mPinnedSliceUris = new ArrayList(SliceManager.getInstance(getContext()).getPinnedSlices());
        if (!BuildCompat.isAtLeastP()) {
            this.mCompat = new SliceProviderCompat(this, onCreatePermissionManager(this.mAutoGrantPermissions), getContext());
        }
        return onCreateSliceProvider();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public CompatPermissionManager onCreatePermissionManager(String[] autoGrantPermissions) {
        Context context = getContext();
        return new CompatPermissionManager(context, SliceProviderCompat.PERMS_PREFIX + getClass().getName(), Process.myUid(), autoGrantPermissions);
    }

    public final String getType(Uri uri) {
        return "vnd.android.slice";
    }

    public Bundle call(String method, String arg, Bundle extras) {
        if (this.mCompat != null) {
            return this.mCompat.call(method, arg, extras);
        }
        return null;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Slice createPermissionSlice(Context context, Uri sliceUri, String callingPackage) {
        Slice.Builder parent = new Slice.Builder(sliceUri);
        parent.addSubSlice(new Slice.Builder(sliceUri.buildUpon().appendPath("permission").build()).addText(getPermissionString(context, callingPackage), (String) null, new String[0]).addSubSlice(new Slice.Builder(parent).addHints("title", "shortcut").addAction(createPermissionIntent(context, sliceUri, callingPackage), new Slice.Builder(parent).build(), (String) null).build()).build());
        return parent.addHints(SliceHints.HINT_PERMISSION_REQUEST).build();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static PendingIntent createPermissionIntent(Context context, Uri sliceUri, String callingPackage) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), "androidx.slice.compat.SlicePermissionActivity"));
        intent.putExtra(SliceProviderCompat.EXTRA_BIND_URI, sliceUri);
        intent.putExtra(SliceProviderCompat.EXTRA_PKG, callingPackage);
        intent.putExtra(SliceProviderCompat.EXTRA_PROVIDER_PKG, context.getPackageName());
        intent.setData(sliceUri.buildUpon().appendQueryParameter(DirectoryAccessDetails.ARG_PACKAGE_NAME, callingPackage).build());
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static CharSequence getPermissionString(Context context, String callingPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            return context.getString(R.string.abc_slices_permission_request, new Object[]{pm.getApplicationInfo(callingPackage, 0).loadLabel(pm), context.getApplicationInfo().loadLabel(pm)});
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unknown calling app", e);
        }
    }

    public void onSlicePinned(Uri sliceUri) {
    }

    public void onSliceUnpinned(Uri sliceUri) {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public void handleSlicePinned(Uri sliceUri) {
        if (!this.mPinnedSliceUris.contains(sliceUri)) {
            this.mPinnedSliceUris.add(sliceUri);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public void handleSliceUnpinned(Uri sliceUri) {
        if (this.mPinnedSliceUris.contains(sliceUri)) {
            this.mPinnedSliceUris.remove(sliceUri);
        }
    }

    @NonNull
    public Uri onMapIntentToUri(Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }

    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        return Collections.emptyList();
    }

    @NonNull
    public List<Uri> getPinnedSlices() {
        return this.mPinnedSliceUris;
    }

    @Nullable
    public final Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @RequiresApi(28)
    public final Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable Bundle queryArgs, @Nullable CancellationSignal cancellationSignal) {
        return null;
    }

    @Nullable
    @RequiresApi(16)
    public final Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        return null;
    }

    @Nullable
    public final Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    public final int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return 0;
    }

    public final int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public final int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @RequiresApi(19)
    public final Uri canonicalize(@NonNull Uri url) {
        return null;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static void setSpecs(Set<SliceSpec> specs) {
        sSpecs = specs;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Set<SliceSpec> getCurrentSpecs() {
        return sSpecs;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static void setClock(Clock clock) {
        sClock = clock;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY})
    public static Clock getClock() {
        return sClock;
    }
}
