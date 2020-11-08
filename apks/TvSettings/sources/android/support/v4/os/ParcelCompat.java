package android.support.v4.os;

import android.os.Parcel;
import android.support.annotation.NonNull;

public final class ParcelCompat {
    public static boolean readBoolean(@NonNull Parcel in) {
        return in.readInt() != 0;
    }

    public static void writeBoolean(@NonNull Parcel out, boolean value) {
        out.writeInt(value);
    }

    private ParcelCompat() {
    }
}
