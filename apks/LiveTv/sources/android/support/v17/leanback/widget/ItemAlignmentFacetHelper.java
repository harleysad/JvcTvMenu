package android.support.v17.leanback.widget;

import android.graphics.Rect;
import android.support.v17.leanback.widget.GridLayoutManager;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.view.View;
import android.view.ViewGroup;

class ItemAlignmentFacetHelper {
    private static Rect sRect = new Rect();

    static int getAlignmentPosition(View itemView, ItemAlignmentFacet.ItemAlignmentDef facet, int orientation) {
        int alignPos;
        int i;
        int i2;
        int i3;
        GridLayoutManager.LayoutParams p = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
        View view = itemView;
        if (facet.mViewId != 0 && (view = itemView.findViewById(facet.mViewId)) == null) {
            view = itemView;
        }
        int alignPos2 = facet.mOffset;
        if (orientation != 0) {
            if (facet.mOffsetWithPadding != 0) {
                if (facet.mOffsetPercent == 0.0f) {
                    alignPos2 += view.getPaddingTop();
                } else if (facet.mOffsetPercent == 100.0f) {
                    alignPos2 -= view.getPaddingBottom();
                }
            }
            if (facet.mOffsetPercent != -1.0f) {
                alignPos2 += (int) ((((float) (view == itemView ? p.getOpticalHeight(view) : view.getHeight())) * facet.mOffsetPercent) / 100.0f);
            }
            if (itemView != view) {
                sRect.top = alignPos2;
                ((ViewGroup) itemView).offsetDescendantRectToMyCoords(view, sRect);
                alignPos = sRect.top - p.getOpticalTopInset();
            } else {
                alignPos = alignPos2;
            }
            if (facet.isAlignedToTextViewBaseLine() != 0) {
                return alignPos + view.getBaseline();
            }
            return alignPos;
        } else if (itemView.getLayoutDirection() == 1) {
            if (view == itemView) {
                i2 = p.getOpticalWidth(view);
            } else {
                i2 = view.getWidth();
            }
            int alignPos3 = i2 - alignPos2;
            if (facet.mOffsetWithPadding != 0) {
                if (facet.mOffsetPercent == 0.0f) {
                    alignPos3 -= view.getPaddingRight();
                } else if (facet.mOffsetPercent == 100.0f) {
                    alignPos3 += view.getPaddingLeft();
                }
            }
            if (facet.mOffsetPercent != -1.0f) {
                if (view == itemView) {
                    i3 = p.getOpticalWidth(view);
                } else {
                    i3 = view.getWidth();
                }
                alignPos3 -= (int) ((((float) i3) * facet.mOffsetPercent) / 100.0f);
            }
            if (itemView == view) {
                return alignPos3;
            }
            sRect.right = alignPos3;
            ((ViewGroup) itemView).offsetDescendantRectToMyCoords(view, sRect);
            return sRect.right + p.getOpticalRightInset();
        } else {
            if (facet.mOffsetWithPadding != 0) {
                if (facet.mOffsetPercent == 0.0f) {
                    alignPos2 += view.getPaddingLeft();
                } else if (facet.mOffsetPercent == 100.0f) {
                    alignPos2 -= view.getPaddingRight();
                }
            }
            if (facet.mOffsetPercent != -1.0f) {
                if (view == itemView) {
                    i = p.getOpticalWidth(view);
                } else {
                    i = view.getWidth();
                }
                alignPos2 += (int) ((((float) i) * facet.mOffsetPercent) / 100.0f);
            }
            int alignPos4 = alignPos2;
            if (itemView == view) {
                return alignPos4;
            }
            sRect.left = alignPos4;
            ((ViewGroup) itemView).offsetDescendantRectToMyCoords(view, sRect);
            return sRect.left - p.getOpticalLeftInset();
        }
    }

    private ItemAlignmentFacetHelper() {
    }
}
