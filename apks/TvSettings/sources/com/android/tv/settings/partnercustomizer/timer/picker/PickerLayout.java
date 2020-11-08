package com.android.tv.settings.partnercustomizer.timer.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class PickerLayout extends LinearLayout {
    private Picker mChildFocusListener;

    public PickerLayout(Context context) {
        super(context);
    }

    public PickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PickerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        this.mChildFocusListener.childFocusChanged();
    }

    public void setChildFocusListener(Picker childFocusListener) {
        this.mChildFocusListener = childFocusListener;
    }
}
