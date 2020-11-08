package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import androidx.slice.core.SliceActionImpl;

public class SliceRadioPreference extends RadioPreference implements HasSliceAction {
    private SliceActionImpl mSliceAction;

    public SliceRadioPreference(Context context, SliceActionImpl action) {
        super(context);
        this.mSliceAction = action;
        update();
    }

    public SliceActionImpl getSliceAction() {
        return this.mSliceAction;
    }

    public void setSliceAction(SliceActionImpl sliceAction) {
        this.mSliceAction = sliceAction;
    }

    public SliceActionImpl getFollowupSliceAction() {
        return null;
    }

    public void setFollowupSliceAction(SliceActionImpl sliceAction) {
    }

    private void update() {
        setChecked(this.mSliceAction.isChecked());
    }
}
