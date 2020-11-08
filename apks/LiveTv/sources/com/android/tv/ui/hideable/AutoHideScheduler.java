package com.android.tv.ui.hideable;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.view.accessibility.AccessibilityManager;
import com.mediatek.wwtv.tvcenter.util.WeakHandler;

@UiThread
public final class AutoHideScheduler implements AccessibilityManager.AccessibilityStateChangeListener {
    private static final int MSG_HIDE = 1;
    private final HideHandler mHandler;
    private final Runnable mRunnable;

    public AutoHideScheduler(Context context, Runnable runnable) {
        this(runnable, (AccessibilityManager) context.getSystemService(AccessibilityManager.class), Looper.getMainLooper());
    }

    @VisibleForTesting
    AutoHideScheduler(Runnable runnable, AccessibilityManager accessibilityManager, Looper looper) {
        this.mRunnable = runnable;
        this.mHandler = new HideHandler(looper, this.mRunnable);
        this.mHandler.setAllowAutoHide(!accessibilityManager.isEnabled());
    }

    public void cancel() {
        this.mHandler.removeMessages(1);
    }

    public void schedule(long delayMs) {
        cancel();
        if (this.mHandler.mAllowAutoHide) {
            this.mHandler.sendEmptyMessageDelayed(1, delayMs);
        }
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        this.mHandler.onAccessibilityStateChanged(enabled);
    }

    public boolean isScheduled() {
        return this.mHandler.hasMessages(1);
    }

    private static class HideHandler extends WeakHandler<Runnable> implements AccessibilityManager.AccessibilityStateChangeListener {
        /* access modifiers changed from: private */
        public boolean mAllowAutoHide;

        public HideHandler(Looper looper, Runnable hideRunner) {
            super(looper, hideRunner);
        }

        /* access modifiers changed from: protected */
        public void handleMessage(Message msg, @NonNull Runnable runnable) {
            if (msg.what == 1 && this.mAllowAutoHide) {
                runnable.run();
            }
        }

        public void setAllowAutoHide(boolean mAllowAutoHide2) {
            this.mAllowAutoHide = mAllowAutoHide2;
        }

        public void onAccessibilityStateChanged(boolean enabled) {
            this.mAllowAutoHide = !enabled;
        }
    }
}
