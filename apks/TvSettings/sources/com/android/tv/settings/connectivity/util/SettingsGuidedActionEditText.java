package com.android.tv.settings.connectivity.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v17.leanback.widget.ImeKeyMonitor;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsGuidedActionEditText extends EditText implements ImeKeyMonitor {
    private ImeKeyMonitor.ImeKeyListener mKeyListener;

    public SettingsGuidedActionEditText(Context context) {
        super(context);
    }

    public SettingsGuidedActionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsGuidedActionEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImeKeyListener(ImeKeyMonitor.ImeKeyListener listener) {
        this.mKeyListener = listener;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        boolean result = false;
        if (this.mKeyListener != null) {
            result = this.mKeyListener.onKeyPreIme(this, keyCode, event);
        }
        if (!result) {
            return super.onKeyPreIme(keyCode, event);
        }
        return result;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName((isFocused() ? EditText.class : TextView.class).getName());
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused) {
            setFocusable(false);
        }
    }
}
