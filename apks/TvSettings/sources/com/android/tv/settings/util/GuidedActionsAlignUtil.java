package com.android.tv.settings.util;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;

public class GuidedActionsAlignUtil {

    private static class SetupViewHolder extends GuidedActionsStylist.ViewHolder implements FacetProvider {
        SetupViewHolder(View v) {
            super(v);
        }

        public Object getFacet(Class facet) {
            if (!facet.equals(ItemAlignmentFacet.class)) {
                return null;
            }
            ItemAlignmentFacet.ItemAlignmentDef alignedDef = new ItemAlignmentFacet.ItemAlignmentDef();
            alignedDef.setItemAlignmentViewId(R.id.guidedactions_item_title);
            alignedDef.setAlignedToTextViewBaseline(false);
            alignedDef.setItemAlignmentOffset(0);
            alignedDef.setItemAlignmentOffsetWithPadding(true);
            alignedDef.setItemAlignmentOffsetPercent(50.0f);
            ItemAlignmentFacet f = new ItemAlignmentFacet();
            f.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[]{alignedDef});
            return f;
        }
    }

    public static GuidedActionsStylist createGuidedActionsStylist() {
        return new GuidedActionsStylist() {
            public GuidedActionsStylist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new SetupViewHolder(LayoutInflater.from(parent.getContext()).inflate(onProvideItemLayoutId(viewType), parent, false));
            }
        };
    }

    public static GuidedActionsStylist createNoBackgroundGuidedActionsStylist() {
        return new GuidedActionsStylist() {
            public GuidedActionsStylist.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(onProvideItemLayoutId(viewType), parent, false);
                v.setBackground((Drawable) null);
                return new SetupViewHolder(v);
            }
        };
    }

    public static View createView(View view, GuidedStepFragment guidedStepFragment) {
        View actionFragmentRoot = view.findViewById(R.id.action_fragment_root);
        if (actionFragmentRoot != null) {
            actionFragmentRoot.setPadding(0, 0, 0, 0);
        }
        VerticalGridView gridView = guidedStepFragment.getGuidedActionsStylist().getActionsGridView();
        gridView.setItemSpacing(guidedStepFragment.getResources().getDimensionPixelSize(R.dimen.setup_list_item_margin));
        gridView.setWindowAlignment(2);
        gridView.setWindowAlignmentPreferKeyLineOverHighEdge(true);
        return view;
    }

    public static GuidanceStylist createGuidanceStylist() {
        return new GuidanceStylist() {
            public int onProvideLayoutId() {
                return R.layout.device_name_content;
            }
        };
    }
}
