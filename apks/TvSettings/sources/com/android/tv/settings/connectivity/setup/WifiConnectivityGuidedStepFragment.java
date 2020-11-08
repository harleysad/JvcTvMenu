package com.android.tv.settings.connectivity.setup;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.GuidedActionsAlignUtil;

public class WifiConnectivityGuidedStepFragment extends GuidedStepSupportFragment {
    /* access modifiers changed from: protected */
    public void onProvideFragmentTransitions() {
        setEnterTransition((Object) null);
        setExitTransition((Object) null);
    }

    public GuidanceStylist onCreateGuidanceStylist() {
        return new GuidanceStylist() {
            public int onProvideLayoutId() {
                return R.layout.wifi_content;
            }
        };
    }

    public GuidedActionsStylist onCreateActionsStylist() {
        return new GuidedActionsStylist() {
            public GuidedActionsStylist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new GuidedActionsAlignUtil.SetupViewHolder(LayoutInflater.from(parent.getContext()).inflate(onProvideItemLayoutId(viewType), parent, false));
            }
        };
    }

    public Resources getResourcesSafely() {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        return activity.getResources();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View actionFragmentRoot = view.findViewById(R.id.action_fragment_root);
        if (actionFragmentRoot != null) {
            actionFragmentRoot.setPadding(0, 0, 0, 0);
        }
        Resources resources = getResourcesSafely();
        if (resources == null) {
            return view;
        }
        getGuidedActionsStylist().getActionsGridView().setItemSpacing(resources.getDimensionPixelSize(R.dimen.setup_list_item_margin));
        GuidedActionsAlignUtil.align(getGuidedActionsStylist());
        return view;
    }
}
