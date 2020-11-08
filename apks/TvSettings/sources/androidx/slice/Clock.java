package androidx.slice;

import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public interface Clock {
    long currentTimeMillis();
}
