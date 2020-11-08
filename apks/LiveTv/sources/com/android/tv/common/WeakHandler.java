package com.android.tv.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;

public abstract class WeakHandler<T> extends Handler {
    private final WeakReference<T> mRef;

    /* access modifiers changed from: protected */
    public abstract void handleMessage(Message message, @NonNull T t);

    public WeakHandler(@NonNull Looper looper, T ref) {
        super(looper);
        this.mRef = new WeakReference<>(ref);
    }

    public WeakHandler(T ref) {
        this.mRef = new WeakReference<>(ref);
    }

    public final void handleMessage(Message msg) {
        T referent = this.mRef.get();
        if (referent != null) {
            handleMessage(msg, referent);
        }
    }
}
