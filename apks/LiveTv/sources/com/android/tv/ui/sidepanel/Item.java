package com.android.tv.ui.sidepanel;

import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;

@UiThread
public abstract class Item {
    private boolean mClickable = true;
    private boolean mEnabled = true;
    private View mItemView;

    /* access modifiers changed from: protected */
    public abstract int getResourceId();

    /* access modifiers changed from: protected */
    public abstract void onSelected();

    public void setEnabled(boolean enabled) {
        if (this.mEnabled != enabled) {
            this.mEnabled = enabled;
            if (this.mItemView != null) {
                setEnabledInternal(this.mItemView, enabled);
            }
        }
    }

    public void setClickable(boolean clickable) {
        this.mClickable = clickable;
        if (this.mItemView != null) {
            this.mItemView.setClickable(clickable);
        }
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public final void notifyUpdated() {
        if (this.mItemView != null) {
            onUpdate();
        }
    }

    /* access modifiers changed from: protected */
    public void onBind(View view) {
        this.mItemView = view;
    }

    /* access modifiers changed from: protected */
    public void onUnbind() {
        this.mItemView = null;
    }

    /* access modifiers changed from: protected */
    public void onUpdate() {
        setEnabledInternal(this.mItemView, this.mEnabled);
        this.mItemView.setClickable(this.mClickable);
    }

    /* access modifiers changed from: protected */
    public void onFocused() {
    }

    /* access modifiers changed from: protected */
    public boolean isBound() {
        return this.mItemView != null;
    }

    private void setEnabledInternal(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setEnabledInternal(parent.getChildAt(i), enabled);
            }
        }
    }
}
