package android.support.v4.os;

import android.support.annotation.Nullable;

public class OperationCanceledException extends RuntimeException {
    public OperationCanceledException() {
        this((String) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OperationCanceledException(@Nullable String message) {
        super(message != null ? message : "The operation has been canceled.");
    }
}
