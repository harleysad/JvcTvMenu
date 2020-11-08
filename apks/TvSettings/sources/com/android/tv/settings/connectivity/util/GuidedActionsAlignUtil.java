package com.android.tv.settings.connectivity.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.View;
import com.android.tv.settings.R;

public class GuidedActionsAlignUtil {

    public static class SetupViewHolder extends GuidedActionsStylist.ViewHolder implements FacetProvider {
        public SetupViewHolder(View v) {
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

    private static float getKeyLinePercent(Context context) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(android.support.v17.leanback.R.styleable.LeanbackGuidedStepTheme);
        float percent = ta.getFloat(45, 40.0f);
        ta.recycle();
        return percent;
    }

    public static void align(GuidedActionsStylist guidedActionsStylist) {
        VerticalGridView gridView = guidedActionsStylist.getActionsGridView();
        gridView.setWindowAlignment(2);
        gridView.setWindowAlignmentPreferKeyLineOverHighEdge(true);
    }
}
