package com.android.tv.twopanelsettings.slices;

import androidx.slice.core.SliceActionImpl;

public interface HasSliceAction {
    SliceActionImpl getFollowupSliceAction();

    SliceActionImpl getSliceAction();

    void setFollowupSliceAction(SliceActionImpl sliceActionImpl);

    void setSliceAction(SliceActionImpl sliceActionImpl);
}
