package com.android.tv.ui;

import android.content.Context;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import com.mediatek.wwtv.tvcenter.R;

public class GuidedActionsStylistWithDivider extends GuidedActionsStylist {
    public static final int ACTION_DIVIDER = -100;
    private static final int VIEW_TYPE_DIVIDER = 1;

    public int getItemViewType(GuidedAction action) {
        if (action.getId() == -100) {
            return 1;
        }
        return super.getItemViewType(action);
    }

    public int onProvideItemLayoutId(int viewType) {
        if (viewType == 1) {
            return R.layout.guided_action_divider;
        }
        return super.onProvideItemLayoutId(viewType);
    }

    public static GuidedAction createDividerAction(Context context) {
        return ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-100)).title((CharSequence) null)).description((CharSequence) null)).focusable(false)).infoOnly(true)).build();
    }
}
