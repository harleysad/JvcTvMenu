package com.android.tv.common.ui.setup;

import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;

public abstract class SetupGuidedStepFragment extends GuidedStepFragment {
    public static final String KEY_THREE_PANE = "key_three_pane";
    private boolean mAccessibilityMode;
    private View mContentFragment;
    /* access modifiers changed from: private */
    public boolean mFromContentFragment;

    /* access modifiers changed from: protected */
    public abstract String getActionCategory();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle arguments = getArguments();
        view.findViewById(R.id.action_fragment_root).setPadding(0, 0, 0, 0);
        this.mContentFragment = view.findViewById(R.id.content_fragment);
        LinearLayout.LayoutParams guidanceLayoutParams = (LinearLayout.LayoutParams) this.mContentFragment.getLayoutParams();
        guidanceLayoutParams.weight = 0.0f;
        if (arguments == null || !arguments.getBoolean(KEY_THREE_PANE, false)) {
            guidanceLayoutParams.width = getResources().getDimensionPixelOffset(R.dimen.setup_guidedstep_guidance_section_width_2pane);
        } else {
            guidanceLayoutParams.width = getResources().getDimensionPixelOffset(R.dimen.setup_guidedstep_guidance_section_width_3pane);
            int doneButtonWidth = getResources().getDimensionPixelOffset(R.dimen.setup_done_button_container_width);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.findViewById(R.id.guidedactions_list).getLayoutParams();
            if (getResources().getConfiguration().getLayoutDirection() == 0) {
                marginLayoutParams.rightMargin = doneButtonWidth;
            } else {
                marginLayoutParams.leftMargin = doneButtonWidth;
            }
        }
        VerticalGridView gridView = getGuidedActionsStylist().getActionsGridView();
        gridView.setWindowAlignmentOffset(getResources().getDimensionPixelOffset(R.dimen.setup_guidedactions_selector_margin_top));
        gridView.setWindowAlignmentOffsetPercent(0.0f);
        gridView.setItemAlignmentOffsetPercent(0.0f);
        ((ViewGroup) view.findViewById(R.id.guidedactions_list)).setTransitionGroup(false);
        ViewGroup group = (ViewGroup) view.findViewById(R.id.content_frame);
        group.setClipChildren(false);
        group.setClipToPadding(false);
        return view;
    }

    public GuidedActionsStylist onCreateActionsStylist() {
        return new SetupGuidedStepFragmentGuidedActionsStylist();
    }

    public void onResume() {
        super.onResume();
        AccessibilityManager am = (AccessibilityManager) getActivity().getSystemService(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY);
        this.mAccessibilityMode = am != null && am.isEnabled() && am.isTouchExplorationEnabled();
        this.mContentFragment.setFocusable(this.mAccessibilityMode);
        if (this.mAccessibilityMode) {
            this.mContentFragment.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                public boolean performAccessibilityAction(View host, int action, Bundle args) {
                    if (action == 64 && !SetupGuidedStepFragment.this.getActions().isEmpty()) {
                        SetupGuidedStepFragment.this.getGuidedActionsStylist().getActionsGridView().scrollToPosition(0);
                        boolean unused = SetupGuidedStepFragment.this.mFromContentFragment = true;
                    }
                    return super.performAccessibilityAction(host, action, args);
                }
            });
            this.mContentFragment.requestFocus();
        }
    }

    public GuidanceStylist onCreateGuidanceStylist() {
        return new GuidanceStylist() {
            public View onCreateView(LayoutInflater inflater, ViewGroup container, GuidanceStylist.Guidance guidance) {
                View view = super.onCreateView(inflater, container, guidance);
                if (guidance.getIconDrawable() == null) {
                    getIconView().setVisibility(8);
                }
                return view;
            }
        };
    }

    /* access modifiers changed from: protected */
    public View getDoneButton() {
        return getActivity().findViewById(R.id.button_done);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.isFocusable()) {
            SetupActionHelper.onActionClick(this, getActionCategory(), (int) action.getId());
        }
    }

    /* access modifiers changed from: protected */
    public void onProvideFragmentTransitions() {
    }

    public boolean isFocusOutEndAllowed() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void setAccessibilityDelegate(GuidedActionsStylist.ViewHolder vh, GuidedAction action) {
        if (this.mAccessibilityMode && findActionPositionById(action.getId()) != 0) {
            vh.itemView.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                public boolean performAccessibilityAction(View host, int action, Bundle args) {
                    View view;
                    if ((action != 1 && action != 64) || !SetupGuidedStepFragment.this.mFromContentFragment || (view = SetupGuidedStepFragment.this.getActionItemView(0)) == null) {
                        return super.performAccessibilityAction(host, action, args);
                    }
                    view.sendAccessibilityEvent(8);
                    boolean unused = SetupGuidedStepFragment.this.mFromContentFragment = false;
                    return true;
                }
            });
        }
    }

    private class SetupGuidedStepFragmentGuidedActionsStylist extends GuidedActionsStylist {
        private SetupGuidedStepFragmentGuidedActionsStylist() {
        }

        public void onBindViewHolder(GuidedActionsStylist.ViewHolder vh, GuidedAction action) {
            super.onBindViewHolder(vh, action);
            SetupGuidedStepFragment.this.setAccessibilityDelegate(vh, action);
        }
    }
}
