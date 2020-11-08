package com.android.tv.twopanelsettings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TwoPanelSettingsRootView extends FrameLayout {
    private View.OnKeyListener mOnBackKeyListener;

    public TwoPanelSettingsRootView(Context context) {
        super(context);
    }

    public TwoPanelSettingsRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoPanelSettingsRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBackKeyListener(View.OnKeyListener backKeyListener) {
        this.mOnBackKeyListener = backKeyListener;
    }

    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        boolean handled = false;
        if ((event.getKeyCode() == 4 || event.getKeyCode() == 19 || event.getKeyCode() == 20 || event.getKeyCode() == 21 || event.getKeyCode() == 22) && this.mOnBackKeyListener != null) {
            handled = this.mOnBackKeyListener.onKey(this, event.getKeyCode(), event);
        }
        return handled || super.dispatchKeyEvent(event);
    }
}
