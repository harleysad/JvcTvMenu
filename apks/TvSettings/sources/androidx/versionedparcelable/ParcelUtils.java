package androidx.versionedparcelable;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.io.InputStream;
import java.io.OutputStream;

public class ParcelUtils {
    private static final String INNER_BUNDLE_KEY = "a";

    private ParcelUtils() {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Parcelable toParcelable(VersionedParcelable obj) {
        return new ParcelImpl(obj);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static <T extends VersionedParcelable> T fromParcelable(Parcelable p) {
        if (p instanceof ParcelImpl) {
            return ((ParcelImpl) p).getVersionedParcel();
        }
        throw new IllegalArgumentException("Invalid parcel");
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void toOutputStream(VersionedParcelable obj, OutputStream output) {
        VersionedParcelStream stream = new VersionedParcelStream((InputStream) null, output);
        stream.writeVersionedParcelable(obj);
        stream.closeField();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static <T extends VersionedParcelable> T fromInputStream(InputStream input) {
        return new VersionedParcelStream(input, (OutputStream) null).readVersionedParcelable();
    }

    public static void putVersionedParcelable(@NonNull Bundle b, @NonNull String key, @NonNull VersionedParcelable obj) {
        Bundle innerBundle = new Bundle();
        innerBundle.putParcelable(INNER_BUNDLE_KEY, toParcelable(obj));
        b.putParcelable(key, innerBundle);
    }

    @Nullable
    public static <T extends VersionedParcelable> T getVersionedParcelable(Bundle bundle, String key) {
        try {
            Bundle innerBundle = (Bundle) bundle.getParcelable(key);
            innerBundle.setClassLoader(ParcelUtils.class.getClassLoader());
            return fromParcelable(innerBundle.getParcelable(INNER_BUNDLE_KEY));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
