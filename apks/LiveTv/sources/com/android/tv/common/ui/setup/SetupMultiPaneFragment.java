package com.android.tv.common.ui.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.wwtv.tvcenter.R;

public abstract class SetupMultiPaneFragment extends SetupFragment {
    public static final int ACTION_DONE = Integer.MAX_VALUE;
    public static final int ACTION_SKIP = 2147483646;
    public static final String CONTENT_FRAGMENT_TAG = "content_fragment";
    private static final boolean DEBUG = false;
    public static final int MAX_SUBCLASSES_ID = 2147483645;
    private static final String TAG = "SetupMultiPaneFragment";

    /* access modifiers changed from: protected */
    public abstract String getActionCategory();

    /* access modifiers changed from: protected */
    public abstract SetupGuidedStepFragment onCreateContentFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().replace(R.id.guided_step_fragment_container, onCreateContentFragment(), CONTENT_FRAGMENT_TAG).commit();
        }
        if (needsDoneButton()) {
            setOnClickAction(view.findViewById(R.id.button_done), getActionCategory(), Integer.MAX_VALUE);
        }
        if (needsSkipButton()) {
            view.findViewById(R.id.button_skip).setVisibility(0);
            setOnClickAction(view.findViewById(R.id.button_skip), getActionCategory(), ACTION_SKIP);
        }
        if (!needsDoneButton() && !needsSkipButton()) {
            View doneButtonContainer = view.findViewById(R.id.done_button_container);
            if (getResources().getConfiguration().getLayoutDirection() == 0) {
                ((ViewGroup.MarginLayoutParams) doneButtonContainer.getLayoutParams()).rightMargin = -getResources().getDimensionPixelOffset(R.dimen.setup_done_button_container_width);
            } else {
                ((ViewGroup.MarginLayoutParams) doneButtonContainer.getLayoutParams()).leftMargin = -getResources().getDimensionPixelOffset(R.dimen.setup_done_button_container_width);
            }
            view.findViewById(R.id.button_done).setFocusable(false);
        }
        return view;
    }

    /* access modifiers changed from: protected */
    public int getLayoutResourceId() {
        return R.layout.fragment_setup_multi_pane;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public SetupGuidedStepFragment getContentFragment() {
        return (SetupGuidedStepFragment) getChildFragmentManager().findFragmentByTag(CONTENT_FRAGMENT_TAG);
    }

    /* access modifiers changed from: protected */
    public boolean needsDoneButton() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean needsSkipButton() {
        return false;
    }

    /* access modifiers changed from: protected */
    public int[] getParentIdsForDelay() {
        return new int[]{R.id.content_fragment, R.id.guidedactions_list};
    }

    public int[] getSharedElementIds() {
        return new int[]{R.id.action_fragment_background, R.id.done_button_container};
    }
}
