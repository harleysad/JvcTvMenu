package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;
import android.util.AttributeSet;
import androidx.slice.core.SliceActionImpl;

public class SliceCheckboxPreference extends CheckBoxPreference implements HasSliceAction {
    private SliceActionImpl mAction;

    public SliceCheckboxPreference(Context context, SliceActionImpl action) {
        super(context);
        this.mAction = action;
        update();
    }

    public SliceCheckboxPreference(Context context, AttributeSet attrs, SliceActionImpl action) {
        super(context, attrs);
        this.mAction = action;
        update();
    }

    public SliceActionImpl getSliceAction() {
        return this.mAction;
    }

    public void setSliceAction(SliceActionImpl sliceAction) {
        this.mAction = sliceAction;
    }

    public SliceActionImpl getFollowupSliceAction() {
        return null;
    }

    public void setFollowupSliceAction(SliceActionImpl sliceAction) {
    }

    private void update() {
        setChecked(this.mAction.isChecked());
    }
}
