package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import androidx.slice.core.SliceActionImpl;

public class SliceSwitchPreference extends SwitchPreference implements HasSliceAction {
    protected SliceActionImpl mAction;

    public SliceSwitchPreference(Context context, SliceActionImpl action) {
        super(context);
        this.mAction = action;
        update();
    }

    public SliceSwitchPreference(Context context, AttributeSet attrs, SliceActionImpl action) {
        super(context, attrs);
        this.mAction = action;
        update();
    }

    public SliceSwitchPreference(Context context) {
        super(context);
    }

    public SliceSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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
