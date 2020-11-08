package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.support.v17.leanback.widget.Grid;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.WindowAlignment;
import android.support.v4.util.CircularIntArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class GridLayoutManager extends RecyclerView.LayoutManager {
    static final boolean DEBUG = false;
    static final int DEFAULT_MAX_PENDING_MOVES = 10;
    static final int MIN_MS_SMOOTH_SCROLL_MAIN_SCREEN = 30;
    private static final int NEXT_ITEM = 1;
    private static final int NEXT_ROW = 3;
    static final int PF_FAST_RELAYOUT = 4;
    static final int PF_FAST_RELAYOUT_UPDATED_SELECTED_POSITION = 8;
    static final int PF_FOCUS_OUT_END = 4096;
    static final int PF_FOCUS_OUT_FRONT = 2048;
    static final int PF_FOCUS_OUT_MASKS = 6144;
    static final int PF_FOCUS_OUT_SIDE_END = 16384;
    static final int PF_FOCUS_OUT_SIDE_MASKS = 24576;
    static final int PF_FOCUS_OUT_SIDE_START = 8192;
    static final int PF_FOCUS_SEARCH_DISABLED = 32768;
    static final int PF_FORCE_FULL_LAYOUT = 256;
    static final int PF_IN_LAYOUT_SEARCH_FOCUS = 16;
    static final int PF_IN_SELECTION = 32;
    static final int PF_LAYOUT_EATEN_IN_SLIDING = 128;
    static final int PF_LAYOUT_ENABLED = 512;
    static final int PF_PRUNE_CHILD = 65536;
    static final int PF_REVERSE_FLOW_MASK = 786432;
    static final int PF_REVERSE_FLOW_PRIMARY = 262144;
    static final int PF_REVERSE_FLOW_SECONDARY = 524288;
    static final int PF_ROW_SECONDARY_SIZE_REFRESH = 1024;
    static final int PF_SCROLL_ENABLED = 131072;
    static final int PF_SLIDING = 64;
    static final int PF_STAGE_LAYOUT = 1;
    static final int PF_STAGE_MASK = 3;
    static final int PF_STAGE_SCROLL = 2;
    private static final int PREV_ITEM = 0;
    private static final int PREV_ROW = 2;
    private static final String TAG = "GridLayoutManager";
    static final boolean TRACE = false;
    private static final Rect sTempRect = new Rect();
    static int[] sTwoInts = new int[2];
    final BaseGridView mBaseGridView;
    OnChildLaidOutListener mChildLaidOutListener = null;
    private OnChildSelectedListener mChildSelectedListener = null;
    private ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = null;
    int mChildVisibility;
    final ViewsStateBundle mChildrenStates = new ViewsStateBundle();
    GridLinearSmoothScroller mCurrentSmoothScroller;
    int[] mDisappearingPositions;
    private int mExtraLayoutSpace;
    int mExtraLayoutSpaceInPreLayout;
    private FacetProviderAdapter mFacetProviderAdapter;
    private int mFixedRowSizeSecondary;
    int mFlag = 221696;
    int mFocusPosition = -1;
    private int mFocusPositionOffset = 0;
    private int mFocusScrollStrategy = 0;
    private int mGravity = 8388659;
    Grid mGrid;
    private Grid.Provider mGridProvider = new Grid.Provider() {
        public int getMinIndex() {
            return GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        public int getCount() {
            return GridLayoutManager.this.mState.getItemCount() + GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        public int createItem(int index, boolean append, Object[] item, boolean disappearingItem) {
            View v = GridLayoutManager.this.getViewForPosition(index - GridLayoutManager.this.mPositionDeltaInPreLayout);
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            lp.setItemAlignmentFacet((ItemAlignmentFacet) GridLayoutManager.this.getFacet(GridLayoutManager.this.mBaseGridView.getChildViewHolder(v), ItemAlignmentFacet.class));
            if (!lp.isItemRemoved()) {
                if (disappearingItem) {
                    if (append) {
                        GridLayoutManager.this.addDisappearingView(v);
                    } else {
                        GridLayoutManager.this.addDisappearingView(v, 0);
                    }
                } else if (append) {
                    GridLayoutManager.this.addView(v);
                } else {
                    GridLayoutManager.this.addView(v, 0);
                }
                if (GridLayoutManager.this.mChildVisibility != -1) {
                    v.setVisibility(GridLayoutManager.this.mChildVisibility);
                }
                if (GridLayoutManager.this.mPendingMoveSmoothScroller != null) {
                    GridLayoutManager.this.mPendingMoveSmoothScroller.consumePendingMovesBeforeLayout();
                }
                int subindex = GridLayoutManager.this.getSubPositionByView(v, v.findFocus());
                if ((GridLayoutManager.this.mFlag & 3) != 1) {
                    if (index == GridLayoutManager.this.mFocusPosition && subindex == GridLayoutManager.this.mSubFocusPosition && GridLayoutManager.this.mPendingMoveSmoothScroller == null) {
                        GridLayoutManager.this.dispatchChildSelected();
                    }
                } else if ((GridLayoutManager.this.mFlag & 4) == 0) {
                    if ((GridLayoutManager.this.mFlag & 16) == 0 && index == GridLayoutManager.this.mFocusPosition && subindex == GridLayoutManager.this.mSubFocusPosition) {
                        GridLayoutManager.this.dispatchChildSelected();
                    } else if ((GridLayoutManager.this.mFlag & 16) != 0 && index >= GridLayoutManager.this.mFocusPosition && v.hasFocusable()) {
                        GridLayoutManager.this.mFocusPosition = index;
                        GridLayoutManager.this.mSubFocusPosition = subindex;
                        GridLayoutManager.this.mFlag &= -17;
                        GridLayoutManager.this.dispatchChildSelected();
                    }
                }
                GridLayoutManager.this.measureChild(v);
            }
            item[0] = v;
            if (GridLayoutManager.this.mOrientation == 0) {
                return GridLayoutManager.this.getDecoratedMeasuredWidthWithMargin(v);
            }
            return GridLayoutManager.this.getDecoratedMeasuredHeightWithMargin(v);
        }

        public void addItem(Object item, int index, int length, int rowIndex, int edge) {
            int start;
            int end;
            long j;
            int i;
            int edge2 = edge;
            View v = (View) item;
            if (edge2 == Integer.MIN_VALUE || edge2 == Integer.MAX_VALUE) {
                if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                    i = GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMin();
                } else {
                    i = GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() - GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMax();
                }
                edge2 = i;
            }
            if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                start = edge2;
                end = edge2 + length;
            } else {
                end = edge2;
                start = edge2 - length;
            }
            int i2 = rowIndex;
            int startSecondary = (GridLayoutManager.this.getRowStartSecondary(i2) + GridLayoutManager.this.mWindowAlignment.secondAxis().getPaddingMin()) - GridLayoutManager.this.mScrollOffsetSecondary;
            int i3 = index;
            GridLayoutManager.this.mChildrenStates.loadView(v, i3);
            GridLayoutManager.this.layoutChild(i2, v, start, end, startSecondary);
            if (!GridLayoutManager.this.mState.isPreLayout()) {
                GridLayoutManager.this.updateScrollLimits();
            }
            if (!((GridLayoutManager.this.mFlag & 3) == 1 || GridLayoutManager.this.mPendingMoveSmoothScroller == null)) {
                GridLayoutManager.this.mPendingMoveSmoothScroller.consumePendingMovesAfterLayout();
            }
            if (GridLayoutManager.this.mChildLaidOutListener != null) {
                RecyclerView.ViewHolder vh = GridLayoutManager.this.mBaseGridView.getChildViewHolder(v);
                OnChildLaidOutListener onChildLaidOutListener = GridLayoutManager.this.mChildLaidOutListener;
                BaseGridView baseGridView = GridLayoutManager.this.mBaseGridView;
                if (vh == null) {
                    j = -1;
                } else {
                    j = vh.getItemId();
                }
                onChildLaidOutListener.onChildLaidOut(baseGridView, v, i3, j);
            }
        }

        public void removeItem(int index) {
            View v = GridLayoutManager.this.findViewByPosition(index - GridLayoutManager.this.mPositionDeltaInPreLayout);
            if ((GridLayoutManager.this.mFlag & 3) == 1) {
                GridLayoutManager.this.detachAndScrapView(v, GridLayoutManager.this.mRecycler);
            } else {
                GridLayoutManager.this.removeAndRecycleView(v, GridLayoutManager.this.mRecycler);
            }
        }

        public int getEdge(int index) {
            View v = GridLayoutManager.this.findViewByPosition(index - GridLayoutManager.this.mPositionDeltaInPreLayout);
            return (GridLayoutManager.this.mFlag & 262144) != 0 ? GridLayoutManager.this.getViewMax(v) : GridLayoutManager.this.getViewMin(v);
        }

        public int getSize(int index) {
            return GridLayoutManager.this.getViewPrimarySize(GridLayoutManager.this.findViewByPosition(index - GridLayoutManager.this.mPositionDeltaInPreLayout));
        }
    };
    private int mHorizontalSpacing;
    private final ItemAlignment mItemAlignment = new ItemAlignment();
    @VisibleForTesting
    OnLayoutCompleteListener mLayoutCompleteListener;
    int mMaxPendingMoves = 10;
    private int mMaxSizeSecondary;
    private int[] mMeasuredDimension = new int[2];
    int mNumRows;
    private int mNumRowsRequested = 1;
    int mOrientation = 0;
    private OrientationHelper mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
    PendingMoveSmoothScroller mPendingMoveSmoothScroller;
    int mPositionDeltaInPreLayout;
    final SparseIntArray mPositionToRowInPostLayout = new SparseIntArray();
    private int mPrimaryScrollExtra;
    RecyclerView.Recycler mRecycler;
    private final Runnable mRequestLayoutRunnable = new Runnable() {
        public void run() {
            GridLayoutManager.this.requestLayout();
        }
    };
    private int[] mRowSizeSecondary;
    private int mRowSizeSecondaryRequested;
    int mScrollOffsetSecondary;
    private int mSizePrimary;
    private int mSpacingPrimary;
    private int mSpacingSecondary;
    RecyclerView.State mState;
    int mSubFocusPosition = 0;
    private int mVerticalSpacing;
    final WindowAlignment mWindowAlignment = new WindowAlignment();

    static final class LayoutParams extends RecyclerView.LayoutParams {
        private int[] mAlignMultiple;
        private int mAlignX;
        private int mAlignY;
        private ItemAlignmentFacet mAlignmentFacet;
        int mBottomInset;
        int mLeftInset;
        int mRightInset;
        int mTopInset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super((RecyclerView.LayoutParams) source);
        }

        /* access modifiers changed from: package-private */
        public int getAlignX() {
            return this.mAlignX;
        }

        /* access modifiers changed from: package-private */
        public int getAlignY() {
            return this.mAlignY;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalLeft(View view) {
            return view.getLeft() + this.mLeftInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalTop(View view) {
            return view.getTop() + this.mTopInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalRight(View view) {
            return view.getRight() - this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalBottom(View view) {
            return view.getBottom() - this.mBottomInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalWidth(View view) {
            return (view.getWidth() - this.mLeftInset) - this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalHeight(View view) {
            return (view.getHeight() - this.mTopInset) - this.mBottomInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalLeftInset() {
            return this.mLeftInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalRightInset() {
            return this.mRightInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalTopInset() {
            return this.mTopInset;
        }

        /* access modifiers changed from: package-private */
        public int getOpticalBottomInset() {
            return this.mBottomInset;
        }

        /* access modifiers changed from: package-private */
        public void setAlignX(int alignX) {
            this.mAlignX = alignX;
        }

        /* access modifiers changed from: package-private */
        public void setAlignY(int alignY) {
            this.mAlignY = alignY;
        }

        /* access modifiers changed from: package-private */
        public void setItemAlignmentFacet(ItemAlignmentFacet facet) {
            this.mAlignmentFacet = facet;
        }

        /* access modifiers changed from: package-private */
        public ItemAlignmentFacet getItemAlignmentFacet() {
            return this.mAlignmentFacet;
        }

        /* access modifiers changed from: package-private */
        public void calculateItemAlignments(int orientation, View view) {
            ItemAlignmentFacet.ItemAlignmentDef[] defs = this.mAlignmentFacet.getAlignmentDefs();
            if (this.mAlignMultiple == null || this.mAlignMultiple.length != defs.length) {
                this.mAlignMultiple = new int[defs.length];
            }
            for (int i = 0; i < defs.length; i++) {
                this.mAlignMultiple[i] = ItemAlignmentFacetHelper.getAlignmentPosition(view, defs[i], orientation);
            }
            if (orientation == 0) {
                this.mAlignX = this.mAlignMultiple[0];
            } else {
                this.mAlignY = this.mAlignMultiple[0];
            }
        }

        /* access modifiers changed from: package-private */
        public int[] getAlignMultiple() {
            return this.mAlignMultiple;
        }

        /* access modifiers changed from: package-private */
        public void setOpticalInsets(int leftInset, int topInset, int rightInset, int bottomInset) {
            this.mLeftInset = leftInset;
            this.mTopInset = topInset;
            this.mRightInset = rightInset;
            this.mBottomInset = bottomInset;
        }
    }

    abstract class GridLinearSmoothScroller extends LinearSmoothScroller {
        boolean mSkipOnStopInternal;

        GridLinearSmoothScroller() {
            super(GridLayoutManager.this.mBaseGridView.getContext());
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            if (!this.mSkipOnStopInternal) {
                onStopInternal();
            }
            if (GridLayoutManager.this.mCurrentSmoothScroller == this) {
                GridLayoutManager.this.mCurrentSmoothScroller = null;
            }
            if (GridLayoutManager.this.mPendingMoveSmoothScroller == this) {
                GridLayoutManager.this.mPendingMoveSmoothScroller = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onStopInternal() {
            View targetView = findViewByPosition(getTargetPosition());
            if (targetView != null) {
                if (GridLayoutManager.this.mFocusPosition != getTargetPosition()) {
                    GridLayoutManager.this.mFocusPosition = getTargetPosition();
                }
                if (GridLayoutManager.this.hasFocus()) {
                    GridLayoutManager.this.mFlag |= 32;
                    targetView.requestFocus();
                    GridLayoutManager.this.mFlag &= -33;
                }
                GridLayoutManager.this.dispatchChildSelected();
                GridLayoutManager.this.dispatchChildSelectedAndPositioned();
            } else if (getTargetPosition() >= 0) {
                GridLayoutManager.this.scrollToSelection(getTargetPosition(), 0, false, 0);
            }
        }

        /* access modifiers changed from: protected */
        public int calculateTimeForScrolling(int dx) {
            int ms = super.calculateTimeForScrolling(dx);
            if (GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() <= 0) {
                return ms;
            }
            float minMs = (30.0f / ((float) GridLayoutManager.this.mWindowAlignment.mainAxis().getSize())) * ((float) dx);
            if (((float) ms) < minMs) {
                return (int) minMs;
            }
            return ms;
        }

        /* access modifiers changed from: protected */
        public void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
            int dy;
            int dx;
            if (GridLayoutManager.this.getScrollPosition(targetView, (View) null, GridLayoutManager.sTwoInts)) {
                if (GridLayoutManager.this.mOrientation == 0) {
                    dx = GridLayoutManager.sTwoInts[0];
                    dy = GridLayoutManager.sTwoInts[1];
                } else {
                    dx = GridLayoutManager.sTwoInts[1];
                    dy = GridLayoutManager.sTwoInts[0];
                }
                action.update(dx, dy, calculateTimeForDeceleration((int) Math.sqrt((double) ((dx * dx) + (dy * dy)))), this.mDecelerateInterpolator);
            }
        }
    }

    final class PendingMoveSmoothScroller extends GridLinearSmoothScroller {
        static final int TARGET_UNDEFINED = -2;
        private int mPendingMoves;
        private final boolean mStaggeredGrid;

        PendingMoveSmoothScroller(int initialPendingMoves, boolean staggeredGrid) {
            super();
            this.mPendingMoves = initialPendingMoves;
            this.mStaggeredGrid = staggeredGrid;
            setTargetPosition(-2);
        }

        /* access modifiers changed from: package-private */
        public void increasePendingMoves() {
            if (this.mPendingMoves < GridLayoutManager.this.mMaxPendingMoves) {
                this.mPendingMoves++;
            }
        }

        /* access modifiers changed from: package-private */
        public void decreasePendingMoves() {
            if (this.mPendingMoves > (-GridLayoutManager.this.mMaxPendingMoves)) {
                this.mPendingMoves--;
            }
        }

        /* access modifiers changed from: package-private */
        public void consumePendingMovesBeforeLayout() {
            View v;
            if (!this.mStaggeredGrid && this.mPendingMoves != 0) {
                View newSelected = null;
                int pos = this.mPendingMoves > 0 ? GridLayoutManager.this.mFocusPosition + GridLayoutManager.this.mNumRows : GridLayoutManager.this.mFocusPosition - GridLayoutManager.this.mNumRows;
                while (this.mPendingMoves != 0 && (v = findViewByPosition(pos)) != null) {
                    if (GridLayoutManager.this.canScrollTo(v)) {
                        newSelected = v;
                        GridLayoutManager.this.mFocusPosition = pos;
                        GridLayoutManager.this.mSubFocusPosition = 0;
                        if (this.mPendingMoves > 0) {
                            this.mPendingMoves--;
                        } else {
                            this.mPendingMoves++;
                        }
                    }
                    pos = this.mPendingMoves > 0 ? GridLayoutManager.this.mNumRows + pos : pos - GridLayoutManager.this.mNumRows;
                }
                if (newSelected != null && GridLayoutManager.this.hasFocus()) {
                    GridLayoutManager.this.mFlag |= 32;
                    newSelected.requestFocus();
                    GridLayoutManager.this.mFlag &= -33;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void consumePendingMovesAfterLayout() {
            if (this.mStaggeredGrid && this.mPendingMoves != 0) {
                this.mPendingMoves = GridLayoutManager.this.processSelectionMoves(true, this.mPendingMoves);
            }
            if (this.mPendingMoves == 0 || ((this.mPendingMoves > 0 && GridLayoutManager.this.hasCreatedLastItem()) || (this.mPendingMoves < 0 && GridLayoutManager.this.hasCreatedFirstItem()))) {
                setTargetPosition(GridLayoutManager.this.mFocusPosition);
                stop();
            }
        }

        /* access modifiers changed from: protected */
        public void updateActionForInterimTarget(RecyclerView.SmoothScroller.Action action) {
            if (this.mPendingMoves != 0) {
                super.updateActionForInterimTarget(action);
            }
        }

        public PointF computeScrollVectorForPosition(int targetPosition) {
            if (this.mPendingMoves == 0) {
                return null;
            }
            int direction = ((GridLayoutManager.this.mFlag & 262144) == 0 ? this.mPendingMoves >= 0 : this.mPendingMoves <= 0) ? 1 : -1;
            if (GridLayoutManager.this.mOrientation == 0) {
                return new PointF((float) direction, 0.0f);
            }
            return new PointF(0.0f, (float) direction);
        }

        /* access modifiers changed from: protected */
        public void onStopInternal() {
            super.onStopInternal();
            this.mPendingMoves = 0;
            View v = findViewByPosition(getTargetPosition());
            if (v != null) {
                GridLayoutManager.this.scrollToView(v, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public String getTag() {
        return "GridLayoutManager:" + this.mBaseGridView.getId();
    }

    public GridLayoutManager(BaseGridView baseGridView) {
        this.mBaseGridView = baseGridView;
        this.mChildVisibility = -1;
        setItemPrefetchEnabled(false);
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == 1) {
            this.mOrientation = orientation;
            this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, this.mOrientation);
            this.mWindowAlignment.setOrientation(orientation);
            this.mItemAlignment.setOrientation(orientation);
            this.mFlag |= 256;
        }
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        int flags;
        boolean z = false;
        if (this.mOrientation == 0) {
            flags = layoutDirection == 1 ? 262144 : 0;
        } else {
            flags = layoutDirection == 1 ? 524288 : 0;
        }
        if ((this.mFlag & PF_REVERSE_FLOW_MASK) != flags) {
            this.mFlag = (this.mFlag & -786433) | flags;
            this.mFlag |= 256;
            WindowAlignment.Axis axis = this.mWindowAlignment.horizontal;
            if (layoutDirection == 1) {
                z = true;
            }
            axis.setReversedFlow(z);
        }
    }

    public int getFocusScrollStrategy() {
        return this.mFocusScrollStrategy;
    }

    public void setFocusScrollStrategy(int focusScrollStrategy) {
        this.mFocusScrollStrategy = focusScrollStrategy;
    }

    public void setWindowAlignment(int windowAlignment) {
        this.mWindowAlignment.mainAxis().setWindowAlignment(windowAlignment);
    }

    public int getWindowAlignment() {
        return this.mWindowAlignment.mainAxis().getWindowAlignment();
    }

    public void setWindowAlignmentOffset(int alignmentOffset) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffset(alignmentOffset);
    }

    public int getWindowAlignmentOffset() {
        return this.mWindowAlignment.mainAxis().getWindowAlignmentOffset();
    }

    public void setWindowAlignmentOffsetPercent(float offsetPercent) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffsetPercent(offsetPercent);
    }

    public float getWindowAlignmentOffsetPercent() {
        return this.mWindowAlignment.mainAxis().getWindowAlignmentOffsetPercent();
    }

    public void setItemAlignmentOffset(int alignmentOffset) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffset(alignmentOffset);
        updateChildAlignments();
    }

    public int getItemAlignmentOffset() {
        return this.mItemAlignment.mainAxis().getItemAlignmentOffset();
    }

    public void setItemAlignmentOffsetWithPadding(boolean withPadding) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetWithPadding(withPadding);
        updateChildAlignments();
    }

    public boolean isItemAlignmentOffsetWithPadding() {
        return this.mItemAlignment.mainAxis().isItemAlignmentOffsetWithPadding();
    }

    public void setItemAlignmentOffsetPercent(float offsetPercent) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetPercent(offsetPercent);
        updateChildAlignments();
    }

    public float getItemAlignmentOffsetPercent() {
        return this.mItemAlignment.mainAxis().getItemAlignmentOffsetPercent();
    }

    public void setItemAlignmentViewId(int viewId) {
        this.mItemAlignment.mainAxis().setItemAlignmentViewId(viewId);
        updateChildAlignments();
    }

    public int getItemAlignmentViewId() {
        return this.mItemAlignment.mainAxis().getItemAlignmentViewId();
    }

    public void setFocusOutAllowed(boolean throughFront, boolean throughEnd) {
        int i = 0;
        int i2 = (this.mFlag & -6145) | (throughFront ? 2048 : 0);
        if (throughEnd) {
            i = 4096;
        }
        this.mFlag = i2 | i;
    }

    public void setFocusOutSideAllowed(boolean throughStart, boolean throughEnd) {
        int i = 0;
        int i2 = (this.mFlag & -24577) | (throughStart ? 8192 : 0);
        if (throughEnd) {
            i = 16384;
        }
        this.mFlag = i2 | i;
    }

    public void setNumRows(int numRows) {
        if (numRows >= 0) {
            this.mNumRowsRequested = numRows;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setRowHeight(int height) {
        if (height >= 0 || height == -2) {
            this.mRowSizeSecondaryRequested = height;
            return;
        }
        throw new IllegalArgumentException("Invalid row height: " + height);
    }

    public void setItemSpacing(int space) {
        this.mHorizontalSpacing = space;
        this.mVerticalSpacing = space;
        this.mSpacingSecondary = space;
        this.mSpacingPrimary = space;
    }

    public void setVerticalSpacing(int space) {
        if (this.mOrientation == 1) {
            this.mVerticalSpacing = space;
            this.mSpacingPrimary = space;
            return;
        }
        this.mVerticalSpacing = space;
        this.mSpacingSecondary = space;
    }

    public void setHorizontalSpacing(int space) {
        if (this.mOrientation == 0) {
            this.mHorizontalSpacing = space;
            this.mSpacingPrimary = space;
            return;
        }
        this.mHorizontalSpacing = space;
        this.mSpacingSecondary = space;
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    /* access modifiers changed from: protected */
    public boolean hasDoneFirstLayout() {
        return this.mGrid != null;
    }

    public void setOnChildSelectedListener(OnChildSelectedListener listener) {
        this.mChildSelectedListener = listener;
    }

    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener listener) {
        if (listener == null) {
            this.mChildViewHolderSelectedListeners = null;
            return;
        }
        if (this.mChildViewHolderSelectedListeners == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        } else {
            this.mChildViewHolderSelectedListeners.clear();
        }
        this.mChildViewHolderSelectedListeners.add(listener);
    }

    public void addOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener listener) {
        if (this.mChildViewHolderSelectedListeners == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        }
        this.mChildViewHolderSelectedListeners.add(listener);
    }

    public void removeOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener listener) {
        if (this.mChildViewHolderSelectedListeners != null) {
            this.mChildViewHolderSelectedListeners.remove(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasOnChildViewHolderSelectedListener() {
        return this.mChildViewHolderSelectedListeners != null && this.mChildViewHolderSelectedListeners.size() > 0;
    }

    /* access modifiers changed from: package-private */
    public void fireOnChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
        if (this.mChildViewHolderSelectedListeners != null) {
            for (int i = this.mChildViewHolderSelectedListeners.size() - 1; i >= 0; i--) {
                this.mChildViewHolderSelectedListeners.get(i).onChildViewHolderSelected(parent, child, position, subposition);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void fireOnChildViewHolderSelectedAndPositioned(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
        if (this.mChildViewHolderSelectedListeners != null) {
            for (int i = this.mChildViewHolderSelectedListeners.size() - 1; i >= 0; i--) {
                this.mChildViewHolderSelectedListeners.get(i).onChildViewHolderSelectedAndPositioned(parent, child, position, subposition);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setOnChildLaidOutListener(OnChildLaidOutListener listener) {
        this.mChildLaidOutListener = listener;
    }

    private int getAdapterPositionByView(View view) {
        LayoutParams params;
        if (view == null || (params = (LayoutParams) view.getLayoutParams()) == null || params.isItemRemoved()) {
            return -1;
        }
        return params.getViewAdapterPosition();
    }

    /* JADX WARNING: type inference failed for: r6v1, types: [android.view.ViewParent] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getSubPositionByView(android.view.View r9, android.view.View r10) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x003c
            if (r10 != 0) goto L_0x0006
            goto L_0x003c
        L_0x0006:
            android.view.ViewGroup$LayoutParams r1 = r9.getLayoutParams()
            android.support.v17.leanback.widget.GridLayoutManager$LayoutParams r1 = (android.support.v17.leanback.widget.GridLayoutManager.LayoutParams) r1
            android.support.v17.leanback.widget.ItemAlignmentFacet r2 = r1.getItemAlignmentFacet()
            if (r2 == 0) goto L_0x003b
            android.support.v17.leanback.widget.ItemAlignmentFacet$ItemAlignmentDef[] r3 = r2.getAlignmentDefs()
            int r4 = r3.length
            r5 = 1
            if (r4 <= r5) goto L_0x003b
        L_0x001a:
            if (r10 == r9) goto L_0x003b
            int r4 = r10.getId()
            r6 = -1
            if (r4 == r6) goto L_0x0033
            r6 = r5
        L_0x0024:
            int r7 = r3.length
            if (r6 >= r7) goto L_0x0033
            r7 = r3[r6]
            int r7 = r7.getItemAlignmentFocusViewId()
            if (r7 != r4) goto L_0x0030
            return r6
        L_0x0030:
            int r6 = r6 + 1
            goto L_0x0024
        L_0x0033:
            android.view.ViewParent r6 = r10.getParent()
            r10 = r6
            android.view.View r10 = (android.view.View) r10
            goto L_0x001a
        L_0x003b:
            return r0
        L_0x003c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.GridLayoutManager.getSubPositionByView(android.view.View, android.view.View):int");
    }

    private int getAdapterPositionByIndex(int index) {
        return getAdapterPositionByView(getChildAt(index));
    }

    /* access modifiers changed from: package-private */
    public void dispatchChildSelected() {
        long j;
        if (this.mChildSelectedListener != null || hasOnChildViewHolderSelectedListener()) {
            View view = this.mFocusPosition == -1 ? null : findViewByPosition(this.mFocusPosition);
            int i = 0;
            if (view != null) {
                RecyclerView.ViewHolder vh = this.mBaseGridView.getChildViewHolder(view);
                if (this.mChildSelectedListener != null) {
                    OnChildSelectedListener onChildSelectedListener = this.mChildSelectedListener;
                    BaseGridView baseGridView = this.mBaseGridView;
                    int i2 = this.mFocusPosition;
                    if (vh == null) {
                        j = -1;
                    } else {
                        j = vh.getItemId();
                    }
                    onChildSelectedListener.onChildSelected(baseGridView, view, i2, j);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, vh, this.mFocusPosition, this.mSubFocusPosition);
            } else {
                if (this.mChildSelectedListener != null) {
                    this.mChildSelectedListener.onChildSelected(this.mBaseGridView, (View) null, -1, -1);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, (RecyclerView.ViewHolder) null, -1, 0);
            }
            if ((this.mFlag & 3) != 1 && !this.mBaseGridView.isLayoutRequested()) {
                int childCount = getChildCount();
                while (true) {
                    int i3 = i;
                    if (i3 >= childCount) {
                        return;
                    }
                    if (getChildAt(i3).isLayoutRequested()) {
                        forceRequestLayout();
                        return;
                    }
                    i = i3 + 1;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchChildSelectedAndPositioned() {
        if (hasOnChildViewHolderSelectedListener()) {
            View view = this.mFocusPosition == -1 ? null : findViewByPosition(this.mFocusPosition);
            if (view != null) {
                fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, this.mBaseGridView.getChildViewHolder(view), this.mFocusPosition, this.mSubFocusPosition);
                return;
            }
            if (this.mChildSelectedListener != null) {
                this.mChildSelectedListener.onChildSelected(this.mBaseGridView, (View) null, -1, -1);
            }
            fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, (RecyclerView.ViewHolder) null, -1, 0);
        }
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0 || this.mNumRows > 1;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1 || this.mNumRows > 1;
    }

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attrs) {
        return new LayoutParams(context, attrs);
    }

    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        }
        if (lp instanceof RecyclerView.LayoutParams) {
            return new LayoutParams((RecyclerView.LayoutParams) lp);
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    /* access modifiers changed from: protected */
    public View getViewForPosition(int position) {
        return this.mRecycler.getViewForPosition(position);
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalLeft(View v) {
        return ((LayoutParams) v.getLayoutParams()).getOpticalLeft(v);
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalRight(View v) {
        return ((LayoutParams) v.getLayoutParams()).getOpticalRight(v);
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalTop(View v) {
        return ((LayoutParams) v.getLayoutParams()).getOpticalTop(v);
    }

    /* access modifiers changed from: package-private */
    public final int getOpticalBottom(View v) {
        return ((LayoutParams) v.getLayoutParams()).getOpticalBottom(v);
    }

    public int getDecoratedLeft(View child) {
        return super.getDecoratedLeft(child) + ((LayoutParams) child.getLayoutParams()).mLeftInset;
    }

    public int getDecoratedTop(View child) {
        return super.getDecoratedTop(child) + ((LayoutParams) child.getLayoutParams()).mTopInset;
    }

    public int getDecoratedRight(View child) {
        return super.getDecoratedRight(child) - ((LayoutParams) child.getLayoutParams()).mRightInset;
    }

    public int getDecoratedBottom(View child) {
        return super.getDecoratedBottom(child) - ((LayoutParams) child.getLayoutParams()).mBottomInset;
    }

    public void getDecoratedBoundsWithMargins(View view, Rect outBounds) {
        super.getDecoratedBoundsWithMargins(view, outBounds);
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        outBounds.left += params.mLeftInset;
        outBounds.top += params.mTopInset;
        outBounds.right -= params.mRightInset;
        outBounds.bottom -= params.mBottomInset;
    }

    /* access modifiers changed from: package-private */
    public int getViewMin(View v) {
        return this.mOrientationHelper.getDecoratedStart(v);
    }

    /* access modifiers changed from: package-private */
    public int getViewMax(View v) {
        return this.mOrientationHelper.getDecoratedEnd(v);
    }

    /* access modifiers changed from: package-private */
    public int getViewPrimarySize(View view) {
        getDecoratedBoundsWithMargins(view, sTempRect);
        return this.mOrientation == 0 ? sTempRect.width() : sTempRect.height();
    }

    private int getViewCenter(View view) {
        return this.mOrientation == 0 ? getViewCenterX(view) : getViewCenterY(view);
    }

    private int getAdjustedViewCenter(View view) {
        View child;
        if (!view.hasFocus() || (child = view.findFocus()) == null || child == view) {
            return getViewCenter(view);
        }
        return getAdjustedPrimaryAlignedScrollDistance(getViewCenter(view), view, child);
    }

    private int getViewCenterSecondary(View view) {
        return this.mOrientation == 0 ? getViewCenterY(view) : getViewCenterX(view);
    }

    private int getViewCenterX(View v) {
        LayoutParams p = (LayoutParams) v.getLayoutParams();
        return p.getOpticalLeft(v) + p.getAlignX();
    }

    private int getViewCenterY(View v) {
        LayoutParams p = (LayoutParams) v.getLayoutParams();
        return p.getOpticalTop(v) + p.getAlignY();
    }

    private void saveContext(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!(this.mRecycler == null && this.mState == null)) {
            Log.e(TAG, "Recycler information was not released, bug!");
        }
        this.mRecycler = recycler;
        this.mState = state;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }

    private void leaveContext() {
        this.mRecycler = null;
        this.mState = null;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007b, code lost:
        if (((r6.mFlag & 262144) != 0) != r6.mGrid.isReversedFlow()) goto L_0x007d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean layoutInit() {
        /*
            r6 = this;
            android.support.v7.widget.RecyclerView$State r0 = r6.mState
            int r0 = r0.getItemCount()
            r1 = -1
            r2 = 0
            if (r0 != 0) goto L_0x000f
            r6.mFocusPosition = r1
            r6.mSubFocusPosition = r2
            goto L_0x0024
        L_0x000f:
            int r3 = r6.mFocusPosition
            if (r3 < r0) goto L_0x001a
            int r1 = r0 + -1
            r6.mFocusPosition = r1
            r6.mSubFocusPosition = r2
            goto L_0x0024
        L_0x001a:
            int r3 = r6.mFocusPosition
            if (r3 != r1) goto L_0x0024
            if (r0 <= 0) goto L_0x0024
            r6.mFocusPosition = r2
            r6.mSubFocusPosition = r2
        L_0x0024:
            android.support.v7.widget.RecyclerView$State r1 = r6.mState
            boolean r1 = r1.didStructureChange()
            r3 = 1
            if (r1 != 0) goto L_0x0057
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            if (r1 == 0) goto L_0x0057
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r1 = r1.getFirstVisibleIndex()
            if (r1 < 0) goto L_0x0057
            int r1 = r6.mFlag
            r1 = r1 & 256(0x100, float:3.59E-43)
            if (r1 != 0) goto L_0x0057
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r1 = r1.getNumRows()
            int r4 = r6.mNumRows
            if (r1 != r4) goto L_0x0057
            r6.updateScrollController()
            r6.updateSecondaryScrollLimits()
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r2 = r6.mSpacingPrimary
            r1.setSpacing(r2)
            return r3
        L_0x0057:
            int r1 = r6.mFlag
            r1 = r1 & -257(0xfffffffffffffeff, float:NaN)
            r6.mFlag = r1
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            r4 = 262144(0x40000, float:3.67342E-40)
            if (r1 == 0) goto L_0x007d
            int r1 = r6.mNumRows
            android.support.v17.leanback.widget.Grid r5 = r6.mGrid
            int r5 = r5.getNumRows()
            if (r1 != r5) goto L_0x007d
            int r1 = r6.mFlag
            r1 = r1 & r4
            if (r1 == 0) goto L_0x0074
            r1 = r3
            goto L_0x0075
        L_0x0074:
            r1 = r2
        L_0x0075:
            android.support.v17.leanback.widget.Grid r5 = r6.mGrid
            boolean r5 = r5.isReversedFlow()
            if (r1 == r5) goto L_0x0098
        L_0x007d:
            int r1 = r6.mNumRows
            android.support.v17.leanback.widget.Grid r1 = android.support.v17.leanback.widget.Grid.createGrid(r1)
            r6.mGrid = r1
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            android.support.v17.leanback.widget.Grid$Provider r5 = r6.mGridProvider
            r1.setProvider(r5)
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r5 = r6.mFlag
            r4 = r4 & r5
            if (r4 == 0) goto L_0x0094
            goto L_0x0095
        L_0x0094:
            r3 = r2
        L_0x0095:
            r1.setReversedFlow(r3)
        L_0x0098:
            r6.initScrollController()
            r6.updateSecondaryScrollLimits()
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r3 = r6.mSpacingPrimary
            r1.setSpacing(r3)
            android.support.v7.widget.RecyclerView$Recycler r1 = r6.mRecycler
            r6.detachAndScrapAttachedViews(r1)
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            r1.resetVisibleIndex()
            android.support.v17.leanback.widget.WindowAlignment r1 = r6.mWindowAlignment
            android.support.v17.leanback.widget.WindowAlignment$Axis r1 = r1.mainAxis()
            r1.invalidateScrollMin()
            android.support.v17.leanback.widget.WindowAlignment r1 = r6.mWindowAlignment
            android.support.v17.leanback.widget.WindowAlignment$Axis r1 = r1.mainAxis()
            r1.invalidateScrollMax()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.GridLayoutManager.layoutInit():boolean");
    }

    private int getRowSizeSecondary(int rowIndex) {
        if (this.mFixedRowSizeSecondary != 0) {
            return this.mFixedRowSizeSecondary;
        }
        if (this.mRowSizeSecondary == null) {
            return 0;
        }
        return this.mRowSizeSecondary[rowIndex];
    }

    /* access modifiers changed from: package-private */
    public int getRowStartSecondary(int rowIndex) {
        int start = 0;
        if ((this.mFlag & 524288) != 0) {
            for (int i = this.mNumRows - 1; i > rowIndex; i--) {
                start += getRowSizeSecondary(i) + this.mSpacingSecondary;
            }
        } else {
            for (int i2 = 0; i2 < rowIndex; i2++) {
                start += getRowSizeSecondary(i2) + this.mSpacingSecondary;
            }
        }
        return start;
    }

    private int getSizeSecondary() {
        int rightmostIndex = (this.mFlag & 524288) != 0 ? 0 : this.mNumRows - 1;
        return getRowStartSecondary(rightmostIndex) + getRowSizeSecondary(rightmostIndex);
    }

    /* access modifiers changed from: package-private */
    public int getDecoratedMeasuredWidthWithMargin(View v) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        return getDecoratedMeasuredWidth(v) + lp.leftMargin + lp.rightMargin;
    }

    /* access modifiers changed from: package-private */
    public int getDecoratedMeasuredHeightWithMargin(View v) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        return getDecoratedMeasuredHeight(v) + lp.topMargin + lp.bottomMargin;
    }

    private void measureScrapChild(int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        View view = this.mRecycler.getViewForPosition(position);
        if (view != null) {
            LayoutParams p = (LayoutParams) view.getLayoutParams();
            calculateItemDecorationsForChild(view, sTempRect);
            view.measure(ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight() + p.leftMargin + p.rightMargin + sTempRect.left + sTempRect.right, p.width), ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom() + p.topMargin + p.bottomMargin + sTempRect.top + sTempRect.bottom, p.height));
            measuredDimension[0] = getDecoratedMeasuredWidthWithMargin(view);
            measuredDimension[1] = getDecoratedMeasuredHeightWithMargin(view);
            this.mRecycler.recycleView(view);
        }
    }

    private boolean processRowSizeSecondary(boolean measure) {
        int secondarySize;
        if (this.mFixedRowSizeSecondary != 0 || this.mRowSizeSecondary == null) {
            return false;
        }
        CircularIntArray[] rows = this.mGrid == null ? null : this.mGrid.getItemPositionsInRows();
        int scrapeChildSize = -1;
        boolean changed = false;
        for (int rowIndex = 0; rowIndex < this.mNumRows; rowIndex++) {
            CircularIntArray row = rows == null ? null : rows[rowIndex];
            int rowItemsPairCount = row == null ? 0 : row.size();
            int rowSize = -1;
            int rowItemPairIndex = 0;
            while (rowItemPairIndex < rowItemsPairCount) {
                int rowIndexStart = row.get(rowItemPairIndex);
                int rowIndexEnd = row.get(rowItemPairIndex + 1);
                int rowSize2 = rowSize;
                for (int i = rowIndexStart; i <= rowIndexEnd; i++) {
                    View view = findViewByPosition(i - this.mPositionDeltaInPreLayout);
                    if (view != null) {
                        if (measure) {
                            measureChild(view);
                        }
                        if (this.mOrientation == 0) {
                            secondarySize = getDecoratedMeasuredHeightWithMargin(view);
                        } else {
                            secondarySize = getDecoratedMeasuredWidthWithMargin(view);
                        }
                        if (secondarySize > rowSize2) {
                            rowSize2 = secondarySize;
                        }
                    }
                }
                rowItemPairIndex += 2;
                rowSize = rowSize2;
            }
            int itemCount = this.mState.getItemCount();
            if (!this.mBaseGridView.hasFixedSize() && measure && rowSize < 0 && itemCount > 0) {
                if (scrapeChildSize < 0) {
                    int position = this.mFocusPosition;
                    if (position < 0) {
                        position = 0;
                    } else if (position >= itemCount) {
                        position = itemCount - 1;
                    }
                    if (getChildCount() > 0) {
                        int firstPos = this.mBaseGridView.getChildViewHolder(getChildAt(0)).getLayoutPosition();
                        int lastPos = this.mBaseGridView.getChildViewHolder(getChildAt(getChildCount() - 1)).getLayoutPosition();
                        if (position >= firstPos && position <= lastPos) {
                            position = position - firstPos <= lastPos - position ? firstPos - 1 : lastPos + 1;
                            if (position < 0 && lastPos < itemCount - 1) {
                                position = lastPos + 1;
                            } else if (position >= itemCount && firstPos > 0) {
                                position = firstPos - 1;
                            }
                        }
                    }
                    if (position >= 0 && position < itemCount) {
                        measureScrapChild(position, View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0), this.mMeasuredDimension);
                        scrapeChildSize = this.mOrientation == 0 ? this.mMeasuredDimension[1] : this.mMeasuredDimension[0];
                    }
                }
                if (scrapeChildSize >= 0) {
                    rowSize = scrapeChildSize;
                }
            }
            if (rowSize < 0) {
                rowSize = 0;
            }
            if (this.mRowSizeSecondary[rowIndex] != rowSize) {
                this.mRowSizeSecondary[rowIndex] = rowSize;
                changed = true;
            }
        }
        return changed;
    }

    private void updateRowSecondarySizeRefresh() {
        int i = this.mFlag & -1025;
        int i2 = 0;
        if (processRowSizeSecondary(false)) {
            i2 = 1024;
        }
        this.mFlag = i | i2;
        if ((this.mFlag & 1024) != 0) {
            forceRequestLayout();
        }
    }

    private void forceRequestLayout() {
        ViewCompat.postOnAnimation(this.mBaseGridView, this.mRequestLayoutRunnable);
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int paddingSecondary;
        int modeSecondary;
        int sizeSecondary;
        int sizePrimary;
        int measuredSizeSecondary;
        int childrenSize;
        saveContext(recycler, state);
        if (this.mOrientation == 0) {
            sizePrimary = View.MeasureSpec.getSize(widthSpec);
            sizeSecondary = View.MeasureSpec.getSize(heightSpec);
            modeSecondary = View.MeasureSpec.getMode(heightSpec);
            paddingSecondary = getPaddingTop() + getPaddingBottom();
        } else {
            sizeSecondary = View.MeasureSpec.getSize(widthSpec);
            sizePrimary = View.MeasureSpec.getSize(heightSpec);
            modeSecondary = View.MeasureSpec.getMode(widthSpec);
            paddingSecondary = getPaddingLeft() + getPaddingRight();
        }
        this.mMaxSizeSecondary = sizeSecondary;
        if (this.mRowSizeSecondaryRequested == -2) {
            this.mNumRows = this.mNumRowsRequested == 0 ? 1 : this.mNumRowsRequested;
            this.mFixedRowSizeSecondary = 0;
            if (this.mRowSizeSecondary == null || this.mRowSizeSecondary.length != this.mNumRows) {
                this.mRowSizeSecondary = new int[this.mNumRows];
            }
            if (this.mState.isPreLayout()) {
                updatePositionDeltaInPreLayout();
            }
            processRowSizeSecondary(true);
            if (modeSecondary == Integer.MIN_VALUE) {
                measuredSizeSecondary = Math.min(getSizeSecondary() + paddingSecondary, this.mMaxSizeSecondary);
            } else if (modeSecondary == 0) {
                measuredSizeSecondary = getSizeSecondary() + paddingSecondary;
            } else if (modeSecondary == 1073741824) {
                measuredSizeSecondary = this.mMaxSizeSecondary;
            } else {
                throw new IllegalStateException("wrong spec");
            }
        } else {
            if (modeSecondary != Integer.MIN_VALUE) {
                if (modeSecondary == 0) {
                    this.mFixedRowSizeSecondary = this.mRowSizeSecondaryRequested == 0 ? sizeSecondary - paddingSecondary : this.mRowSizeSecondaryRequested;
                    this.mNumRows = this.mNumRowsRequested == 0 ? 1 : this.mNumRowsRequested;
                    measuredSizeSecondary = (this.mFixedRowSizeSecondary * this.mNumRows) + (this.mSpacingSecondary * (this.mNumRows - 1)) + paddingSecondary;
                } else if (modeSecondary != 1073741824) {
                    throw new IllegalStateException("wrong spec");
                }
            }
            if (this.mNumRowsRequested == 0 && this.mRowSizeSecondaryRequested == 0) {
                this.mNumRows = 1;
                this.mFixedRowSizeSecondary = sizeSecondary - paddingSecondary;
            } else if (this.mNumRowsRequested == 0) {
                this.mFixedRowSizeSecondary = this.mRowSizeSecondaryRequested;
                this.mNumRows = (this.mSpacingSecondary + sizeSecondary) / (this.mRowSizeSecondaryRequested + this.mSpacingSecondary);
            } else if (this.mRowSizeSecondaryRequested == 0) {
                this.mNumRows = this.mNumRowsRequested;
                this.mFixedRowSizeSecondary = ((sizeSecondary - paddingSecondary) - (this.mSpacingSecondary * (this.mNumRows - 1))) / this.mNumRows;
            } else {
                this.mNumRows = this.mNumRowsRequested;
                this.mFixedRowSizeSecondary = this.mRowSizeSecondaryRequested;
            }
            measuredSizeSecondary = sizeSecondary;
            if (modeSecondary == Integer.MIN_VALUE && (childrenSize = (this.mFixedRowSizeSecondary * this.mNumRows) + (this.mSpacingSecondary * (this.mNumRows - 1)) + paddingSecondary) < measuredSizeSecondary) {
                measuredSizeSecondary = childrenSize;
            }
        }
        if (this.mOrientation == 0) {
            setMeasuredDimension(sizePrimary, measuredSizeSecondary);
        } else {
            setMeasuredDimension(measuredSizeSecondary, sizePrimary);
        }
        leaveContext();
    }

    /* access modifiers changed from: package-private */
    public void measureChild(View child) {
        int secondarySpec;
        int heightSpec;
        int widthSpec;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        calculateItemDecorationsForChild(child, sTempRect);
        int widthUsed = lp.leftMargin + lp.rightMargin + sTempRect.left + sTempRect.right;
        int heightUsed = lp.topMargin + lp.bottomMargin + sTempRect.top + sTempRect.bottom;
        if (this.mRowSizeSecondaryRequested == -2) {
            secondarySpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        } else {
            secondarySpec = View.MeasureSpec.makeMeasureSpec(this.mFixedRowSizeSecondary, 1073741824);
        }
        if (this.mOrientation == 0) {
            widthSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), widthUsed, lp.width);
            heightSpec = ViewGroup.getChildMeasureSpec(secondarySpec, heightUsed, lp.height);
        } else {
            heightSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), heightUsed, lp.height);
            widthSpec = ViewGroup.getChildMeasureSpec(secondarySpec, widthUsed, lp.width);
        }
        child.measure(widthSpec, heightSpec);
    }

    /* access modifiers changed from: package-private */
    public <E> E getFacet(RecyclerView.ViewHolder vh, Class<? extends E> facetClass) {
        FacetProvider p;
        E facet = null;
        if (vh instanceof FacetProvider) {
            facet = ((FacetProvider) vh).getFacet(facetClass);
        }
        if (facet != null || this.mFacetProviderAdapter == null || (p = this.mFacetProviderAdapter.getFacetProvider(vh.getItemViewType())) == null) {
            return facet;
        }
        return p.getFacet(facetClass);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void layoutChild(int r18, android.view.View r19, int r20, int r21, int r22) {
        /*
            r17 = this;
            r6 = r17
            r7 = r19
            int r1 = r6.mOrientation
            if (r1 != 0) goto L_0x000d
            int r1 = r6.getDecoratedMeasuredHeightWithMargin(r7)
            goto L_0x0011
        L_0x000d:
            int r1 = r6.getDecoratedMeasuredWidthWithMargin(r7)
        L_0x0011:
            int r2 = r6.mFixedRowSizeSecondary
            if (r2 <= 0) goto L_0x001b
            int r2 = r6.mFixedRowSizeSecondary
            int r1 = java.lang.Math.min(r1, r2)
        L_0x001b:
            r8 = r1
            int r1 = r6.mGravity
            r9 = r1 & 112(0x70, float:1.57E-43)
            int r1 = r6.mFlag
            r2 = 786432(0xc0000, float:1.102026E-39)
            r1 = r1 & r2
            r2 = 1
            if (r1 == 0) goto L_0x0033
            int r1 = r6.mGravity
            r3 = 8388615(0x800007, float:1.1754953E-38)
            r1 = r1 & r3
            int r1 = android.view.Gravity.getAbsoluteGravity(r1, r2)
            goto L_0x0037
        L_0x0033:
            int r1 = r6.mGravity
            r1 = r1 & 7
        L_0x0037:
            r10 = r1
            int r1 = r6.mOrientation
            if (r1 != 0) goto L_0x0040
            r1 = 48
            if (r9 == r1) goto L_0x0078
        L_0x0040:
            int r1 = r6.mOrientation
            if (r1 != r2) goto L_0x0048
            r1 = 3
            if (r10 != r1) goto L_0x0048
            goto L_0x0078
        L_0x0048:
            int r1 = r6.mOrientation
            if (r1 != 0) goto L_0x0050
            r1 = 80
            if (r9 == r1) goto L_0x0057
        L_0x0050:
            int r1 = r6.mOrientation
            if (r1 != r2) goto L_0x0060
            r1 = 5
            if (r10 != r1) goto L_0x0060
        L_0x0057:
            int r1 = r17.getRowSizeSecondary(r18)
            int r1 = r1 - r8
            int r0 = r22 + r1
        L_0x005e:
            r11 = r0
            goto L_0x007a
        L_0x0060:
            int r1 = r6.mOrientation
            if (r1 != 0) goto L_0x0068
            r1 = 16
            if (r9 == r1) goto L_0x006e
        L_0x0068:
            int r1 = r6.mOrientation
            if (r1 != r2) goto L_0x0078
            if (r10 != r2) goto L_0x0078
        L_0x006e:
            int r1 = r17.getRowSizeSecondary(r18)
            int r1 = r1 - r8
            int r1 = r1 / 2
            int r0 = r22 + r1
            goto L_0x005e
        L_0x0078:
            r11 = r22
        L_0x007a:
            int r0 = r6.mOrientation
            if (r0 != 0) goto L_0x0089
            r0 = r20
            r1 = r11
            r2 = r21
            int r3 = r11 + r8
            r12 = r0
            r13 = r1
            r14 = r3
            goto L_0x0094
        L_0x0089:
            r0 = r20
            r1 = r11
            r2 = r21
            int r3 = r11 + r8
            r13 = r0
            r12 = r1
            r14 = r2
            r2 = r3
        L_0x0094:
            r15 = r2
            android.view.ViewGroup$LayoutParams r0 = r19.getLayoutParams()
            r5 = r0
            android.support.v17.leanback.widget.GridLayoutManager$LayoutParams r5 = (android.support.v17.leanback.widget.GridLayoutManager.LayoutParams) r5
            r0 = r6
            r1 = r7
            r2 = r12
            r3 = r13
            r4 = r15
            r16 = r8
            r8 = r5
            r5 = r14
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
            android.graphics.Rect r0 = sTempRect
            super.getDecoratedBoundsWithMargins(r7, r0)
            android.graphics.Rect r0 = sTempRect
            int r0 = r0.left
            int r0 = r12 - r0
            android.graphics.Rect r1 = sTempRect
            int r1 = r1.top
            int r1 = r13 - r1
            android.graphics.Rect r2 = sTempRect
            int r2 = r2.right
            int r2 = r2 - r15
            android.graphics.Rect r3 = sTempRect
            int r3 = r3.bottom
            int r3 = r3 - r14
            r8.setOpticalInsets(r0, r1, r2, r3)
            r6.updateChildAlignments(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.GridLayoutManager.layoutChild(int, android.view.View, int, int, int):void");
    }

    private void updateChildAlignments(View v) {
        LayoutParams p = (LayoutParams) v.getLayoutParams();
        if (p.getItemAlignmentFacet() == null) {
            p.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(v));
            p.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(v));
            return;
        }
        p.calculateItemAlignments(this.mOrientation, v);
        if (this.mOrientation == 0) {
            p.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(v));
        } else {
            p.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(v));
        }
    }

    private void updateChildAlignments() {
        int c = getChildCount();
        for (int i = 0; i < c; i++) {
            updateChildAlignments(getChildAt(i));
        }
    }

    /* access modifiers changed from: package-private */
    public void setExtraLayoutSpace(int extraLayoutSpace) {
        if (this.mExtraLayoutSpace != extraLayoutSpace) {
            if (this.mExtraLayoutSpace >= 0) {
                this.mExtraLayoutSpace = extraLayoutSpace;
                requestLayout();
                return;
            }
            throw new IllegalArgumentException("ExtraLayoutSpace must >= 0");
        }
    }

    /* access modifiers changed from: package-private */
    public int getExtraLayoutSpace() {
        return this.mExtraLayoutSpace;
    }

    private void removeInvisibleViewsAtEnd() {
        if ((this.mFlag & 65600) == 65536) {
            this.mGrid.removeInvisibleItemsAtEnd(this.mFocusPosition, (this.mFlag & 262144) != 0 ? -this.mExtraLayoutSpace : this.mSizePrimary + this.mExtraLayoutSpace);
        }
    }

    private void removeInvisibleViewsAtFront() {
        if ((this.mFlag & 65600) == 65536) {
            this.mGrid.removeInvisibleItemsAtFront(this.mFocusPosition, (this.mFlag & 262144) != 0 ? this.mSizePrimary + this.mExtraLayoutSpace : -this.mExtraLayoutSpace);
        }
    }

    private boolean appendOneColumnVisibleItems() {
        return this.mGrid.appendOneColumnVisibleItems();
    }

    /* access modifiers changed from: package-private */
    public void slideIn() {
        if ((this.mFlag & 64) != 0) {
            this.mFlag &= -65;
            if (this.mFocusPosition >= 0) {
                scrollToSelection(this.mFocusPosition, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
            } else {
                this.mFlag &= -129;
                requestLayout();
            }
            if ((this.mFlag & 128) != 0) {
                this.mFlag &= -129;
                if (this.mBaseGridView.getScrollState() != 0 || isSmoothScrolling()) {
                    this.mBaseGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            if (newState == 0) {
                                GridLayoutManager.this.mBaseGridView.removeOnScrollListener(this);
                                GridLayoutManager.this.requestLayout();
                            }
                        }
                    });
                } else {
                    requestLayout();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getSlideOutDistance() {
        int start;
        int start2;
        int top;
        if (this.mOrientation == 1) {
            int distance = -getHeight();
            if (getChildCount() <= 0 || (top = getChildAt(0).getTop()) >= 0) {
                return distance;
            }
            return distance + top;
        } else if ((this.mFlag & 262144) != 0) {
            int distance2 = getWidth();
            if (getChildCount() <= 0 || (start2 = getChildAt(0).getRight()) <= distance2) {
                return distance2;
            }
            return start2;
        } else {
            int distance3 = -getWidth();
            if (getChildCount() <= 0 || (start = getChildAt(0).getLeft()) >= 0) {
                return distance3;
            }
            return distance3 + start;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSlidingChildViews() {
        return (this.mFlag & 64) != 0;
    }

    /* access modifiers changed from: package-private */
    public void slideOut() {
        if ((this.mFlag & 64) == 0) {
            this.mFlag |= 64;
            if (getChildCount() != 0) {
                if (this.mOrientation == 1) {
                    this.mBaseGridView.smoothScrollBy(0, getSlideOutDistance(), new AccelerateDecelerateInterpolator());
                } else {
                    this.mBaseGridView.smoothScrollBy(getSlideOutDistance(), 0, new AccelerateDecelerateInterpolator());
                }
            }
        }
    }

    private boolean prependOneColumnVisibleItems() {
        return this.mGrid.prependOneColumnVisibleItems();
    }

    private void appendVisibleItems() {
        this.mGrid.appendVisibleItems((this.mFlag & 262144) != 0 ? (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout : this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout);
    }

    private void prependVisibleItems() {
        this.mGrid.prependVisibleItems((this.mFlag & 262144) != 0 ? this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout : (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout);
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00e1 A[LOOP:3: B:34:0x00e1->B:37:0x00ef, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fastRelayout() {
        /*
            r18 = this;
            r6 = r18
            r7 = 0
            int r8 = r18.getChildCount()
            android.support.v17.leanback.widget.Grid r0 = r6.mGrid
            int r0 = r0.getFirstVisibleIndex()
            r1 = 0
            int r2 = r6.mFlag
            r2 = r2 & -9
            r6.mFlag = r2
            r10 = r0
            r9 = r1
        L_0x0016:
            if (r9 >= r8) goto L_0x00a0
            android.view.View r0 = r6.getChildAt(r9)
            int r1 = r6.getAdapterPositionByView(r0)
            if (r10 == r1) goto L_0x0025
            r7 = 1
            goto L_0x00a2
        L_0x0025:
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            android.support.v17.leanback.widget.Grid$Location r11 = r1.getLocation(r10)
            if (r11 != 0) goto L_0x0030
            r7 = 1
            goto L_0x00a2
        L_0x0030:
            int r1 = r11.row
            int r1 = r6.getRowStartSecondary(r1)
            android.support.v17.leanback.widget.WindowAlignment r2 = r6.mWindowAlignment
            android.support.v17.leanback.widget.WindowAlignment$Axis r2 = r2.secondAxis()
            int r2 = r2.getPaddingMin()
            int r1 = r1 + r2
            int r2 = r6.mScrollOffsetSecondary
            int r12 = r1 - r2
            int r13 = r6.getViewMin(r0)
            int r14 = r6.getViewPrimarySize(r0)
            android.view.ViewGroup$LayoutParams r1 = r0.getLayoutParams()
            r15 = r1
            android.support.v17.leanback.widget.GridLayoutManager$LayoutParams r15 = (android.support.v17.leanback.widget.GridLayoutManager.LayoutParams) r15
            boolean r1 = r15.viewNeedsUpdate()
            if (r1 == 0) goto L_0x006c
            int r1 = r6.mFlag
            r1 = r1 | 8
            r6.mFlag = r1
            android.support.v7.widget.RecyclerView$Recycler r1 = r6.mRecycler
            r6.detachAndScrapView(r0, r1)
            android.view.View r0 = r6.getViewForPosition(r10)
            r6.addView(r0, r9)
        L_0x006c:
            r5 = r0
            r6.measureChild(r5)
            int r0 = r6.mOrientation
            if (r0 != 0) goto L_0x007d
            int r0 = r6.getDecoratedMeasuredWidthWithMargin(r5)
            int r1 = r13 + r0
        L_0x007a:
            r3 = r0
            r4 = r1
            goto L_0x0084
        L_0x007d:
            int r0 = r6.getDecoratedMeasuredHeightWithMargin(r5)
            int r1 = r13 + r0
            goto L_0x007a
        L_0x0084:
            int r1 = r11.row
            r0 = r6
            r2 = r5
            r16 = r7
            r7 = r3
            r3 = r13
            r17 = r5
            r5 = r12
            r0.layoutChild(r1, r2, r3, r4, r5)
            if (r14 == r7) goto L_0x0098
            r0 = 1
            r7 = r0
            goto L_0x00a2
        L_0x0098:
            int r9 = r9 + 1
            int r10 = r10 + 1
            r7 = r16
            goto L_0x0016
        L_0x00a0:
            r16 = r7
        L_0x00a2:
            if (r7 == 0) goto L_0x00f2
            android.support.v17.leanback.widget.Grid r0 = r6.mGrid
            int r0 = r0.getLastVisibleIndex()
            int r1 = r8 + -1
        L_0x00ac:
            if (r1 < r9) goto L_0x00ba
            android.view.View r2 = r6.getChildAt(r1)
            android.support.v7.widget.RecyclerView$Recycler r3 = r6.mRecycler
            r6.detachAndScrapView(r2, r3)
            int r1 = r1 + -1
            goto L_0x00ac
        L_0x00ba:
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            r1.invalidateItemsAfter(r10)
            int r1 = r6.mFlag
            r2 = 65536(0x10000, float:9.18355E-41)
            r1 = r1 & r2
            if (r1 == 0) goto L_0x00e1
            r18.appendVisibleItems()
            int r1 = r6.mFocusPosition
            if (r1 < 0) goto L_0x00f2
            int r1 = r6.mFocusPosition
            if (r1 > r0) goto L_0x00f2
        L_0x00d1:
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r1 = r1.getLastVisibleIndex()
            int r2 = r6.mFocusPosition
            if (r1 >= r2) goto L_0x00f2
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            r1.appendOneColumnVisibleItems()
            goto L_0x00d1
        L_0x00e1:
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            boolean r1 = r1.appendOneColumnVisibleItems()
            if (r1 == 0) goto L_0x00f2
            android.support.v17.leanback.widget.Grid r1 = r6.mGrid
            int r1 = r1.getLastVisibleIndex()
            if (r1 >= r0) goto L_0x00f2
            goto L_0x00e1
        L_0x00f2:
            r18.updateScrollLimits()
            r18.updateSecondaryScrollLimits()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.GridLayoutManager.fastRelayout():void");
    }

    public void removeAndRecycleAllViews(RecyclerView.Recycler recycler) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            removeAndRecycleViewAt(i, recycler);
        }
    }

    private void focusToViewInLayout(boolean hadFocus, boolean alignToView, int extraDelta, int extraDeltaSecondary) {
        View focusView = findViewByPosition(this.mFocusPosition);
        if (focusView != null && alignToView) {
            scrollToView(focusView, false, extraDelta, extraDeltaSecondary);
        }
        if (focusView != null && hadFocus && !focusView.hasFocus()) {
            focusView.requestFocus();
        } else if (!hadFocus && !this.mBaseGridView.hasFocus()) {
            if (focusView == null || !focusView.hasFocusable()) {
                int i = 0;
                int count = getChildCount();
                while (true) {
                    if (i < count) {
                        focusView = getChildAt(i);
                        if (focusView != null && focusView.hasFocusable()) {
                            this.mBaseGridView.focusableViewAvailable(focusView);
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
            } else {
                this.mBaseGridView.focusableViewAvailable(focusView);
            }
            if (alignToView && focusView != null && focusView.hasFocus()) {
                scrollToView(focusView, false, extraDelta, extraDeltaSecondary);
            }
        }
    }

    @VisibleForTesting
    public static class OnLayoutCompleteListener {
        public void onLayoutCompleted(RecyclerView.State state) {
        }
    }

    public void onLayoutCompleted(RecyclerView.State state) {
        if (this.mLayoutCompleteListener != null) {
            this.mLayoutCompleteListener.onLayoutCompleted(state);
        }
    }

    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public void updatePositionToRowMapInPostLayout() {
        Grid.Location loc;
        this.mPositionToRowInPostLayout.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int position = this.mBaseGridView.getChildViewHolder(getChildAt(i)).getOldPosition();
            if (position >= 0 && (loc = this.mGrid.getLocation(position)) != null) {
                this.mPositionToRowInPostLayout.put(position, loc.row);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void fillScrapViewsInPostLayout() {
        List<RecyclerView.ViewHolder> scrapList = this.mRecycler.getScrapList();
        int scrapSize = scrapList.size();
        if (scrapSize != 0) {
            if (this.mDisappearingPositions == null || scrapSize > this.mDisappearingPositions.length) {
                int length = this.mDisappearingPositions == null ? 16 : this.mDisappearingPositions.length;
                while (length < scrapSize) {
                    length <<= 1;
                }
                this.mDisappearingPositions = new int[length];
            }
            int totalItems = 0;
            for (int i = 0; i < scrapSize; i++) {
                int pos = scrapList.get(i).getAdapterPosition();
                if (pos >= 0) {
                    this.mDisappearingPositions[totalItems] = pos;
                    totalItems++;
                }
            }
            if (totalItems > 0) {
                Arrays.sort(this.mDisappearingPositions, 0, totalItems);
                this.mGrid.fillDisappearingItems(this.mDisappearingPositions, totalItems, this.mPositionToRowInPostLayout);
            }
            this.mPositionToRowInPostLayout.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePositionDeltaInPreLayout() {
        if (getChildCount() > 0) {
            this.mPositionDeltaInPreLayout = this.mGrid.getFirstVisibleIndex() - ((LayoutParams) getChildAt(0).getLayoutParams()).getViewLayoutPosition();
        } else {
            this.mPositionDeltaInPreLayout = 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:88:0x018a A[LOOP:1: B:88:0x018a->B:91:0x0194, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayoutChildren(android.support.v7.widget.RecyclerView.Recycler r17, android.support.v7.widget.RecyclerView.State r18) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.mNumRows
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = r18.getItemCount()
            if (r1 >= 0) goto L_0x000e
            return
        L_0x000e:
            int r2 = r0.mFlag
            r2 = r2 & 64
            if (r2 == 0) goto L_0x0021
            int r2 = r16.getChildCount()
            if (r2 <= 0) goto L_0x0021
            int r2 = r0.mFlag
            r2 = r2 | 128(0x80, float:1.794E-43)
            r0.mFlag = r2
            return
        L_0x0021:
            int r2 = r0.mFlag
            r2 = r2 & 512(0x200, float:7.175E-43)
            if (r2 != 0) goto L_0x002e
            r16.discardLayoutInfo()
            r16.removeAndRecycleAllViews(r17)
            return
        L_0x002e:
            int r2 = r0.mFlag
            r2 = r2 & -4
            r3 = 1
            r2 = r2 | r3
            r0.mFlag = r2
            r16.saveContext(r17, r18)
            boolean r2 = r18.isPreLayout()
            r4 = 0
            if (r2 == 0) goto L_0x00de
            r16.updatePositionDeltaInPreLayout()
            int r2 = r16.getChildCount()
            android.support.v17.leanback.widget.Grid r3 = r0.mGrid
            if (r3 == 0) goto L_0x00d4
            if (r2 <= 0) goto L_0x00d4
            r3 = 2147483647(0x7fffffff, float:NaN)
            r5 = -2147483648(0xffffffff80000000, float:-0.0)
            android.support.v17.leanback.widget.BaseGridView r6 = r0.mBaseGridView
            android.view.View r7 = r0.getChildAt(r4)
            android.support.v7.widget.RecyclerView$ViewHolder r6 = r6.getChildViewHolder(r7)
            int r6 = r6.getOldPosition()
            android.support.v17.leanback.widget.BaseGridView r7 = r0.mBaseGridView
            int r8 = r2 + -1
            android.view.View r8 = r0.getChildAt(r8)
            android.support.v7.widget.RecyclerView$ViewHolder r7 = r7.getChildViewHolder(r8)
            int r7 = r7.getOldPosition()
        L_0x0071:
            if (r4 >= r2) goto L_0x00c8
            android.view.View r8 = r0.getChildAt(r4)
            android.view.ViewGroup$LayoutParams r9 = r8.getLayoutParams()
            android.support.v17.leanback.widget.GridLayoutManager$LayoutParams r9 = (android.support.v17.leanback.widget.GridLayoutManager.LayoutParams) r9
            android.support.v17.leanback.widget.BaseGridView r10 = r0.mBaseGridView
            int r10 = r10.getChildAdapterPosition(r8)
            boolean r11 = r9.isItemChanged()
            if (r11 != 0) goto L_0x00b5
            boolean r11 = r9.isItemRemoved()
            if (r11 != 0) goto L_0x00b5
            boolean r11 = r8.isLayoutRequested()
            if (r11 != 0) goto L_0x00b5
            boolean r11 = r8.hasFocus()
            if (r11 != 0) goto L_0x00a3
            int r11 = r0.mFocusPosition
            int r12 = r9.getViewAdapterPosition()
            if (r11 == r12) goto L_0x00b5
        L_0x00a3:
            boolean r11 = r8.hasFocus()
            if (r11 == 0) goto L_0x00b1
            int r11 = r0.mFocusPosition
            int r12 = r9.getViewAdapterPosition()
            if (r11 != r12) goto L_0x00b5
        L_0x00b1:
            if (r10 < r6) goto L_0x00b5
            if (r10 <= r7) goto L_0x00c5
        L_0x00b5:
            int r11 = r0.getViewMin(r8)
            int r3 = java.lang.Math.min(r3, r11)
            int r11 = r0.getViewMax(r8)
            int r5 = java.lang.Math.max(r5, r11)
        L_0x00c5:
            int r4 = r4 + 1
            goto L_0x0071
        L_0x00c8:
            if (r5 <= r3) goto L_0x00ce
            int r4 = r5 - r3
            r0.mExtraLayoutSpaceInPreLayout = r4
        L_0x00ce:
            r16.appendVisibleItems()
            r16.prependVisibleItems()
        L_0x00d4:
            int r3 = r0.mFlag
            r3 = r3 & -4
            r0.mFlag = r3
            r16.leaveContext()
            return
        L_0x00de:
            boolean r2 = r18.willRunPredictiveAnimations()
            if (r2 == 0) goto L_0x00e7
            r16.updatePositionToRowMapInPostLayout()
        L_0x00e7:
            boolean r2 = r16.isSmoothScrolling()
            if (r2 != 0) goto L_0x00f2
            int r2 = r0.mFocusScrollStrategy
            if (r2 != 0) goto L_0x00f2
            goto L_0x00f3
        L_0x00f2:
            r3 = r4
        L_0x00f3:
            r2 = r3
            int r3 = r0.mFocusPosition
            r5 = -1
            if (r3 == r5) goto L_0x0108
            int r3 = r0.mFocusPositionOffset
            r6 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r3 == r6) goto L_0x0108
            int r3 = r0.mFocusPosition
            int r6 = r0.mFocusPositionOffset
            int r3 = r3 + r6
            r0.mFocusPosition = r3
            r0.mSubFocusPosition = r4
        L_0x0108:
            r0.mFocusPositionOffset = r4
            int r3 = r0.mFocusPosition
            android.view.View r3 = r0.findViewByPosition(r3)
            int r6 = r0.mFocusPosition
            int r7 = r0.mSubFocusPosition
            android.support.v17.leanback.widget.BaseGridView r8 = r0.mBaseGridView
            boolean r8 = r8.hasFocus()
            android.support.v17.leanback.widget.Grid r9 = r0.mGrid
            if (r9 == 0) goto L_0x0125
            android.support.v17.leanback.widget.Grid r9 = r0.mGrid
            int r9 = r9.getFirstVisibleIndex()
            goto L_0x0126
        L_0x0125:
            r9 = r5
        L_0x0126:
            android.support.v17.leanback.widget.Grid r10 = r0.mGrid
            if (r10 == 0) goto L_0x0131
            android.support.v17.leanback.widget.Grid r10 = r0.mGrid
            int r10 = r10.getLastVisibleIndex()
            goto L_0x0132
        L_0x0131:
            r10 = r5
        L_0x0132:
            int r11 = r0.mOrientation
            if (r11 != 0) goto L_0x013f
            int r11 = r18.getRemainingScrollHorizontal()
            int r12 = r18.getRemainingScrollVertical()
            goto L_0x0147
        L_0x013f:
            int r12 = r18.getRemainingScrollHorizontal()
            int r11 = r18.getRemainingScrollVertical()
        L_0x0147:
            boolean r13 = r16.layoutInit()
            r14 = 16
            if (r13 == 0) goto L_0x0160
            int r4 = r0.mFlag
            r4 = r4 | 4
            r0.mFlag = r4
            android.support.v17.leanback.widget.Grid r4 = r0.mGrid
            int r5 = r0.mFocusPosition
            r4.setStart(r5)
            r16.fastRelayout()
            goto L_0x0197
        L_0x0160:
            int r13 = r0.mFlag
            r13 = r13 & -5
            r0.mFlag = r13
            int r13 = r0.mFlag
            r13 = r13 & -17
            if (r8 == 0) goto L_0x016e
            r4 = r14
        L_0x016e:
            r4 = r4 | r13
            r0.mFlag = r4
            if (r2 == 0) goto L_0x0181
            if (r9 < 0) goto L_0x017d
            int r4 = r0.mFocusPosition
            if (r4 > r10) goto L_0x017d
            int r4 = r0.mFocusPosition
            if (r4 >= r9) goto L_0x0181
        L_0x017d:
            int r4 = r0.mFocusPosition
            r13 = r4
            goto L_0x0183
        L_0x0181:
            r4 = r9
            r13 = r10
        L_0x0183:
            android.support.v17.leanback.widget.Grid r15 = r0.mGrid
            r15.setStart(r4)
            if (r13 == r5) goto L_0x0197
        L_0x018a:
            boolean r5 = r16.appendOneColumnVisibleItems()
            if (r5 == 0) goto L_0x0197
            android.view.View r5 = r0.findViewByPosition(r13)
            if (r5 != 0) goto L_0x0197
            goto L_0x018a
        L_0x0197:
            r16.updateScrollLimits()
            android.support.v17.leanback.widget.Grid r4 = r0.mGrid
            int r4 = r4.getFirstVisibleIndex()
            android.support.v17.leanback.widget.Grid r5 = r0.mGrid
            int r5 = r5.getLastVisibleIndex()
            int r13 = -r11
            int r15 = -r12
            r0.focusToViewInLayout(r8, r2, r13, r15)
            r16.appendVisibleItems()
            r16.prependVisibleItems()
            android.support.v17.leanback.widget.Grid r13 = r0.mGrid
            int r13 = r13.getFirstVisibleIndex()
            if (r13 != r4) goto L_0x0197
            android.support.v17.leanback.widget.Grid r13 = r0.mGrid
            int r13 = r13.getLastVisibleIndex()
            if (r13 != r5) goto L_0x0197
            r16.removeInvisibleViewsAtFront()
            r16.removeInvisibleViewsAtEnd()
            boolean r13 = r18.willRunPredictiveAnimations()
            if (r13 == 0) goto L_0x01d0
            r16.fillScrapViewsInPostLayout()
        L_0x01d0:
            int r13 = r0.mFlag
            r13 = r13 & 1024(0x400, float:1.435E-42)
            if (r13 == 0) goto L_0x01dd
            int r13 = r0.mFlag
            r13 = r13 & -1025(0xfffffffffffffbff, float:NaN)
            r0.mFlag = r13
            goto L_0x01e0
        L_0x01dd:
            r16.updateRowSecondarySizeRefresh()
        L_0x01e0:
            int r13 = r0.mFlag
            r13 = r13 & 4
            if (r13 == 0) goto L_0x0200
            int r13 = r0.mFocusPosition
            if (r13 != r6) goto L_0x01fc
            int r13 = r0.mSubFocusPosition
            if (r13 != r7) goto L_0x01fc
            int r13 = r0.mFocusPosition
            android.view.View r13 = r0.findViewByPosition(r13)
            if (r13 != r3) goto L_0x01fc
            int r13 = r0.mFlag
            r13 = r13 & 8
            if (r13 == 0) goto L_0x0200
        L_0x01fc:
            r16.dispatchChildSelected()
            goto L_0x0209
        L_0x0200:
            int r13 = r0.mFlag
            r13 = r13 & 20
            if (r13 != r14) goto L_0x0209
            r16.dispatchChildSelected()
        L_0x0209:
            r16.dispatchChildSelectedAndPositioned()
            int r13 = r0.mFlag
            r13 = r13 & 64
            if (r13 == 0) goto L_0x0219
            int r13 = r16.getSlideOutDistance()
            r0.scrollDirectionPrimary(r13)
        L_0x0219:
            int r13 = r0.mFlag
            r13 = r13 & -4
            r0.mFlag = r13
            r16.leaveContext()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.GridLayoutManager.onLayoutChildren(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State):void");
    }

    private void offsetChildrenSecondary(int increment) {
        int childCount = getChildCount();
        int i = 0;
        if (this.mOrientation == 0) {
            while (true) {
                int i2 = i;
                if (i2 < childCount) {
                    getChildAt(i2).offsetTopAndBottom(increment);
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        } else {
            while (true) {
                int i3 = i;
                if (i3 < childCount) {
                    getChildAt(i3).offsetLeftAndRight(increment);
                    i = i3 + 1;
                } else {
                    return;
                }
            }
        }
    }

    private void offsetChildrenPrimary(int increment) {
        int childCount = getChildCount();
        int i = 0;
        if (this.mOrientation == 1) {
            while (true) {
                int i2 = i;
                if (i2 < childCount) {
                    getChildAt(i2).offsetTopAndBottom(increment);
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        } else {
            while (true) {
                int i3 = i;
                if (i3 < childCount) {
                    getChildAt(i3).offsetLeftAndRight(increment);
                    i = i3 + 1;
                } else {
                    return;
                }
            }
        }
    }

    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        saveContext(recycler, state);
        this.mFlag = (this.mFlag & -4) | 2;
        if (this.mOrientation == 0) {
            result = scrollDirectionPrimary(dx);
        } else {
            result = scrollDirectionSecondary(dx);
        }
        leaveContext();
        this.mFlag &= -4;
        return result;
    }

    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        this.mFlag = (this.mFlag & -4) | 2;
        saveContext(recycler, state);
        if (this.mOrientation == 1) {
            result = scrollDirectionPrimary(dy);
        } else {
            result = scrollDirectionSecondary(dy);
        }
        leaveContext();
        this.mFlag &= -4;
        return result;
    }

    private int scrollDirectionPrimary(int da) {
        int minScroll;
        int maxScroll;
        if ((this.mFlag & 64) == 0 && (this.mFlag & 3) != 1) {
            if (da > 0) {
                if (!this.mWindowAlignment.mainAxis().isMaxUnknown() && da > (maxScroll = this.mWindowAlignment.mainAxis().getMaxScroll())) {
                    da = maxScroll;
                }
            } else if (da < 0 && !this.mWindowAlignment.mainAxis().isMinUnknown() && da < (minScroll = this.mWindowAlignment.mainAxis().getMinScroll())) {
                da = minScroll;
            }
        }
        boolean minScroll2 = false;
        if (da == 0) {
            return 0;
        }
        offsetChildrenPrimary(-da);
        if ((this.mFlag & 3) == 1) {
            updateScrollLimits();
            return da;
        }
        int childCount = getChildCount();
        if ((this.mFlag & 262144) == 0 ? da >= 0 : da <= 0) {
            appendVisibleItems();
        } else {
            prependVisibleItems();
        }
        boolean updated = getChildCount() > childCount;
        int childCount2 = getChildCount();
        if ((262144 & this.mFlag) == 0 ? da >= 0 : da <= 0) {
            removeInvisibleViewsAtFront();
        } else {
            removeInvisibleViewsAtEnd();
        }
        if (getChildCount() < childCount2) {
            minScroll2 = true;
        }
        if (minScroll2 || updated) {
            updateRowSecondarySizeRefresh();
        }
        this.mBaseGridView.invalidate();
        updateScrollLimits();
        return da;
    }

    private int scrollDirectionSecondary(int dy) {
        if (dy == 0) {
            return 0;
        }
        offsetChildrenSecondary(-dy);
        this.mScrollOffsetSecondary += dy;
        updateSecondaryScrollLimits();
        this.mBaseGridView.invalidate();
        return dy;
    }

    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            saveContext((RecyclerView.Recycler) null, state);
            int da = this.mOrientation == 0 ? dx : dy;
            if (getChildCount() != 0) {
                if (da != 0) {
                    this.mGrid.collectAdjacentPrefetchPositions(da < 0 ? -this.mExtraLayoutSpace : this.mSizePrimary + this.mExtraLayoutSpace, da, layoutPrefetchRegistry);
                    leaveContext();
                }
            }
        } finally {
            leaveContext();
        }
    }

    public void collectInitialPrefetchPositions(int adapterItemCount, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int numToPrefetch = this.mBaseGridView.mInitialPrefetchItemCount;
        if (adapterItemCount != 0 && numToPrefetch != 0) {
            int initialPos = Math.max(0, Math.min(this.mFocusPosition - ((numToPrefetch - 1) / 2), adapterItemCount - numToPrefetch));
            int i = initialPos;
            while (i < adapterItemCount && i < initialPos + numToPrefetch) {
                layoutPrefetchRegistry.addPosition(i, 0);
                i++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateScrollLimits() {
        int lowMinPos;
        int lowVisiblePos;
        int highMaxPos;
        int highVisiblePos;
        int maxViewCenter;
        int maxEdge;
        int minEdge;
        int minViewCenter;
        if (this.mState.getItemCount() != 0) {
            if ((this.mFlag & 262144) == 0) {
                highVisiblePos = this.mGrid.getLastVisibleIndex();
                highMaxPos = this.mState.getItemCount() - 1;
                lowVisiblePos = this.mGrid.getFirstVisibleIndex();
                lowMinPos = 0;
            } else {
                highVisiblePos = this.mGrid.getFirstVisibleIndex();
                highMaxPos = 0;
                lowVisiblePos = this.mGrid.getLastVisibleIndex();
                lowMinPos = this.mState.getItemCount() - 1;
            }
            if (highVisiblePos >= 0 && lowVisiblePos >= 0) {
                boolean highAvailable = highVisiblePos == highMaxPos;
                boolean lowAvailable = lowVisiblePos == lowMinPos;
                if (highAvailable || !this.mWindowAlignment.mainAxis().isMaxUnknown() || lowAvailable || !this.mWindowAlignment.mainAxis().isMinUnknown()) {
                    if (highAvailable) {
                        maxEdge = this.mGrid.findRowMax(true, sTwoInts);
                        View maxChild = findViewByPosition(sTwoInts[1]);
                        maxViewCenter = getViewCenter(maxChild);
                        int[] multipleAligns = ((LayoutParams) maxChild.getLayoutParams()).getAlignMultiple();
                        if (multipleAligns != null && multipleAligns.length > 0) {
                            maxViewCenter += multipleAligns[multipleAligns.length - 1] - multipleAligns[0];
                        }
                    } else {
                        maxEdge = Integer.MAX_VALUE;
                        maxViewCenter = Integer.MAX_VALUE;
                    }
                    int maxViewCenter2 = maxViewCenter;
                    if (lowAvailable) {
                        minEdge = this.mGrid.findRowMin(false, sTwoInts);
                        minViewCenter = getViewCenter(findViewByPosition(sTwoInts[1]));
                    } else {
                        minEdge = Integer.MIN_VALUE;
                        minViewCenter = Integer.MIN_VALUE;
                    }
                    this.mWindowAlignment.mainAxis().updateMinMax(minEdge, maxEdge, minViewCenter, maxViewCenter2);
                }
            }
        }
    }

    private void updateSecondaryScrollLimits() {
        WindowAlignment.Axis secondAxis = this.mWindowAlignment.secondAxis();
        int minEdge = secondAxis.getPaddingMin() - this.mScrollOffsetSecondary;
        int maxEdge = getSizeSecondary() + minEdge;
        secondAxis.updateMinMax(minEdge, maxEdge, minEdge, maxEdge);
    }

    private void initScrollController() {
        this.mWindowAlignment.reset();
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
        this.mScrollOffsetSecondary = 0;
    }

    private void updateScrollController() {
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
    }

    public void scrollToPosition(int position) {
        setSelection(position, 0, false, 0);
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        setSelection(position, 0, true, 0);
    }

    public void setSelection(int position, int primaryScrollExtra) {
        setSelection(position, 0, false, primaryScrollExtra);
    }

    public void setSelectionSmooth(int position) {
        setSelection(position, 0, true, 0);
    }

    public void setSelectionWithSub(int position, int subposition, int primaryScrollExtra) {
        setSelection(position, subposition, false, primaryScrollExtra);
    }

    public void setSelectionSmoothWithSub(int position, int subposition) {
        setSelection(position, subposition, true, 0);
    }

    public int getSelection() {
        return this.mFocusPosition;
    }

    public int getSubSelection() {
        return this.mSubFocusPosition;
    }

    public void setSelection(int position, int subposition, boolean smooth, int primaryScrollExtra) {
        if ((this.mFocusPosition != position && position != -1) || subposition != this.mSubFocusPosition || primaryScrollExtra != this.mPrimaryScrollExtra) {
            scrollToSelection(position, subposition, smooth, primaryScrollExtra);
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToSelection(int position, int subposition, boolean smooth, int primaryScrollExtra) {
        this.mPrimaryScrollExtra = primaryScrollExtra;
        View view = findViewByPosition(position);
        boolean notSmoothScrolling = !isSmoothScrolling();
        if (notSmoothScrolling && !this.mBaseGridView.isLayoutRequested() && view != null && getAdapterPositionByView(view) == position) {
            this.mFlag |= 32;
            scrollToView(view, smooth);
            this.mFlag &= -33;
        } else if ((this.mFlag & 512) == 0 || (this.mFlag & 64) != 0) {
            this.mFocusPosition = position;
            this.mSubFocusPosition = subposition;
            this.mFocusPositionOffset = Integer.MIN_VALUE;
        } else if (!smooth || this.mBaseGridView.isLayoutRequested()) {
            if (!notSmoothScrolling) {
                skipSmoothScrollerOnStopInternal();
                this.mBaseGridView.stopScroll();
            }
            if (this.mBaseGridView.isLayoutRequested() || view == null || getAdapterPositionByView(view) != position) {
                this.mFocusPosition = position;
                this.mSubFocusPosition = subposition;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
                this.mFlag |= 256;
                requestLayout();
                return;
            }
            this.mFlag |= 32;
            scrollToView(view, smooth);
            this.mFlag &= -33;
        } else {
            this.mFocusPosition = position;
            this.mSubFocusPosition = subposition;
            this.mFocusPositionOffset = Integer.MIN_VALUE;
            if (!hasDoneFirstLayout()) {
                Log.w(getTag(), "setSelectionSmooth should not be called before first layout pass");
                return;
            }
            int position2 = startPositionSmoothScroller(position);
            if (position2 != this.mFocusPosition) {
                this.mFocusPosition = position2;
                this.mSubFocusPosition = 0;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int startPositionSmoothScroller(int position) {
        LinearSmoothScroller linearSmoothScroller = new GridLinearSmoothScroller() {
            public PointF computeScrollVectorForPosition(int targetPosition) {
                if (getChildCount() == 0) {
                    return null;
                }
                boolean isStart = false;
                int firstChildPos = GridLayoutManager.this.getPosition(GridLayoutManager.this.getChildAt(0));
                int i = 1;
                if ((GridLayoutManager.this.mFlag & 262144) == 0 ? targetPosition < firstChildPos : targetPosition > firstChildPos) {
                    isStart = true;
                }
                if (isStart) {
                    i = -1;
                }
                int direction = i;
                if (GridLayoutManager.this.mOrientation == 0) {
                    return new PointF((float) direction, 0.0f);
                }
                return new PointF(0.0f, (float) direction);
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
        return linearSmoothScroller.getTargetPosition();
    }

    /* access modifiers changed from: package-private */
    public void skipSmoothScrollerOnStopInternal() {
        if (this.mCurrentSmoothScroller != null) {
            this.mCurrentSmoothScroller.mSkipOnStopInternal = true;
        }
    }

    public void startSmoothScroll(RecyclerView.SmoothScroller smoothScroller) {
        skipSmoothScrollerOnStopInternal();
        super.startSmoothScroll(smoothScroller);
        if (!smoothScroller.isRunning() || !(smoothScroller instanceof GridLinearSmoothScroller)) {
            this.mCurrentSmoothScroller = null;
            this.mPendingMoveSmoothScroller = null;
            return;
        }
        this.mCurrentSmoothScroller = (GridLinearSmoothScroller) smoothScroller;
        if (this.mCurrentSmoothScroller instanceof PendingMoveSmoothScroller) {
            this.mPendingMoveSmoothScroller = (PendingMoveSmoothScroller) this.mCurrentSmoothScroller;
        } else {
            this.mPendingMoveSmoothScroller = null;
        }
    }

    private void processPendingMovement(boolean forward) {
        if (forward) {
            if (hasCreatedLastItem()) {
                return;
            }
        } else if (hasCreatedFirstItem()) {
            return;
        }
        if (this.mPendingMoveSmoothScroller == null) {
            this.mBaseGridView.stopScroll();
            boolean z = true;
            int i = forward ? 1 : -1;
            if (this.mNumRows <= 1) {
                z = false;
            }
            PendingMoveSmoothScroller linearSmoothScroller = new PendingMoveSmoothScroller(i, z);
            this.mFocusPositionOffset = 0;
            startSmoothScroll(linearSmoothScroller);
        } else if (forward) {
            this.mPendingMoveSmoothScroller.increasePendingMoves();
        } else {
            this.mPendingMoveSmoothScroller.decreasePendingMoves();
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        if (!(this.mFocusPosition == -1 || this.mGrid == null || this.mGrid.getFirstVisibleIndex() < 0 || this.mFocusPositionOffset == Integer.MIN_VALUE || positionStart > this.mFocusPosition + this.mFocusPositionOffset)) {
            this.mFocusPositionOffset += itemCount;
        }
        this.mChildrenStates.clear();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mFocusPositionOffset = 0;
        this.mChildrenStates.clear();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        int pos;
        if (!(this.mFocusPosition == -1 || this.mGrid == null || this.mGrid.getFirstVisibleIndex() < 0 || this.mFocusPositionOffset == Integer.MIN_VALUE || positionStart > (pos = this.mFocusPosition + this.mFocusPositionOffset))) {
            if (positionStart + itemCount > pos) {
                this.mFocusPositionOffset += positionStart - pos;
                this.mFocusPosition += this.mFocusPositionOffset;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
            } else {
                this.mFocusPositionOffset -= itemCount;
            }
        }
        this.mChildrenStates.clear();
    }

    public void onItemsMoved(RecyclerView recyclerView, int fromPosition, int toPosition, int itemCount) {
        if (!(this.mFocusPosition == -1 || this.mFocusPositionOffset == Integer.MIN_VALUE)) {
            int pos = this.mFocusPosition + this.mFocusPositionOffset;
            if (fromPosition <= pos && pos < fromPosition + itemCount) {
                this.mFocusPositionOffset += toPosition - fromPosition;
            } else if (fromPosition < pos && toPosition > pos - itemCount) {
                this.mFocusPositionOffset -= itemCount;
            } else if (fromPosition > pos && toPosition < pos) {
                this.mFocusPositionOffset += itemCount;
            }
        }
        this.mChildrenStates.clear();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        int end = positionStart + itemCount;
        for (int i = positionStart; i < end; i++) {
            this.mChildrenStates.remove(i);
        }
    }

    public boolean onRequestChildFocus(RecyclerView parent, View child, View focused) {
        if ((this.mFlag & 32768) == 0 && getAdapterPositionByView(child) != -1 && (this.mFlag & 35) == 0) {
            scrollToView(child, focused, true);
        }
        return true;
    }

    public boolean requestChildRectangleOnScreen(RecyclerView parent, View view, Rect rect, boolean immediate) {
        return false;
    }

    public void getViewSelectedOffsets(View view, int[] offsets) {
        if (this.mOrientation == 0) {
            offsets[0] = getPrimaryAlignedScrollDistance(view);
            offsets[1] = getSecondaryScrollDistance(view);
            return;
        }
        offsets[1] = getPrimaryAlignedScrollDistance(view);
        offsets[0] = getSecondaryScrollDistance(view);
    }

    private int getPrimaryAlignedScrollDistance(View view) {
        return this.mWindowAlignment.mainAxis().getScroll(getViewCenter(view));
    }

    private int getAdjustedPrimaryAlignedScrollDistance(int scrollPrimary, View view, View childView) {
        int subindex = getSubPositionByView(view, childView);
        if (subindex == 0) {
            return scrollPrimary;
        }
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        return scrollPrimary + (lp.getAlignMultiple()[subindex] - lp.getAlignMultiple()[0]);
    }

    private int getSecondaryScrollDistance(View view) {
        return this.mWindowAlignment.secondAxis().getScroll(getViewCenterSecondary(view));
    }

    /* access modifiers changed from: package-private */
    public void scrollToView(View view, boolean smooth) {
        scrollToView(view, view == null ? null : view.findFocus(), smooth);
    }

    /* access modifiers changed from: package-private */
    public void scrollToView(View view, boolean smooth, int extraDelta, int extraDeltaSecondary) {
        scrollToView(view, view == null ? null : view.findFocus(), smooth, extraDelta, extraDeltaSecondary);
    }

    private void scrollToView(View view, View childView, boolean smooth) {
        scrollToView(view, childView, smooth, 0, 0);
    }

    private void scrollToView(View view, View childView, boolean smooth, int extraDelta, int extraDeltaSecondary) {
        if ((this.mFlag & 64) == 0) {
            int newFocusPosition = getAdapterPositionByView(view);
            int newSubFocusPosition = getSubPositionByView(view, childView);
            if (!(newFocusPosition == this.mFocusPosition && newSubFocusPosition == this.mSubFocusPosition)) {
                this.mFocusPosition = newFocusPosition;
                this.mSubFocusPosition = newSubFocusPosition;
                this.mFocusPositionOffset = 0;
                if ((this.mFlag & 3) != 1) {
                    dispatchChildSelected();
                }
                if (this.mBaseGridView.isChildrenDrawingOrderEnabledInternal()) {
                    this.mBaseGridView.invalidate();
                }
            }
            if (view != null) {
                if (!view.hasFocus() && this.mBaseGridView.hasFocus()) {
                    view.requestFocus();
                }
                if ((this.mFlag & 131072) == 0 && smooth) {
                    return;
                }
                if (getScrollPosition(view, childView, sTwoInts) || extraDelta != 0 || extraDeltaSecondary != 0) {
                    scrollGrid(sTwoInts[0] + extraDelta, sTwoInts[1] + extraDeltaSecondary, smooth);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getScrollPosition(View view, View childView, int[] deltas) {
        switch (this.mFocusScrollStrategy) {
            case 1:
            case 2:
                return getNoneAlignedPosition(view, deltas);
            default:
                return getAlignedPosition(view, childView, deltas);
        }
    }

    private boolean getNoneAlignedPosition(View view, int[] deltas) {
        View secondaryAlignedView;
        int pos = getAdapterPositionByView(view);
        int viewMin = getViewMin(view);
        int viewMax = getViewMax(view);
        View firstView = null;
        View lastView = null;
        int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
        int clientSize = this.mWindowAlignment.mainAxis().getClientSize();
        int row = this.mGrid.getRowIndex(pos);
        if (viewMin < paddingMin) {
            firstView = view;
            if (this.mFocusScrollStrategy == 2) {
                while (true) {
                    if (!prependOneColumnVisibleItems()) {
                        break;
                    }
                    CircularIntArray positions = this.mGrid.getItemPositionsInRows(this.mGrid.getFirstVisibleIndex(), pos)[row];
                    firstView = findViewByPosition(positions.get(0));
                    if (viewMax - getViewMin(firstView) > clientSize) {
                        if (positions.size() > 2) {
                            firstView = findViewByPosition(positions.get(2));
                        }
                    }
                }
            }
        } else if (viewMax > clientSize + paddingMin) {
            if (this.mFocusScrollStrategy == 2) {
                View firstView2 = view;
                while (true) {
                    CircularIntArray positions2 = this.mGrid.getItemPositionsInRows(pos, this.mGrid.getLastVisibleIndex())[row];
                    lastView = findViewByPosition(positions2.get(positions2.size() - 1));
                    if (getViewMax(lastView) - viewMin <= clientSize) {
                        if (!appendOneColumnVisibleItems()) {
                            break;
                        }
                    } else {
                        lastView = null;
                        break;
                    }
                }
                firstView = lastView != null ? null : firstView2;
            } else {
                lastView = view;
            }
        }
        int scrollPrimary = 0;
        if (firstView != null) {
            scrollPrimary = getViewMin(firstView) - paddingMin;
        } else if (lastView != null) {
            scrollPrimary = getViewMax(lastView) - (paddingMin + clientSize);
        }
        if (firstView != null) {
            secondaryAlignedView = firstView;
        } else if (lastView != null) {
            secondaryAlignedView = lastView;
        } else {
            secondaryAlignedView = view;
        }
        int scrollSecondary = getSecondaryScrollDistance(secondaryAlignedView);
        if (scrollPrimary == 0 && scrollSecondary == 0) {
            return false;
        }
        deltas[0] = scrollPrimary;
        deltas[1] = scrollSecondary;
        return true;
    }

    private boolean getAlignedPosition(View view, View childView, int[] deltas) {
        int scrollPrimary = getPrimaryAlignedScrollDistance(view);
        if (childView != null) {
            scrollPrimary = getAdjustedPrimaryAlignedScrollDistance(scrollPrimary, view, childView);
        }
        int scrollSecondary = getSecondaryScrollDistance(view);
        int scrollPrimary2 = scrollPrimary + this.mPrimaryScrollExtra;
        if (scrollPrimary2 == 0 && scrollSecondary == 0) {
            deltas[0] = 0;
            deltas[1] = 0;
            return false;
        }
        deltas[0] = scrollPrimary2;
        deltas[1] = scrollSecondary;
        return true;
    }

    private void scrollGrid(int scrollPrimary, int scrollSecondary, boolean smooth) {
        int scrollY;
        int scrollX;
        if ((this.mFlag & 3) == 1) {
            scrollDirectionPrimary(scrollPrimary);
            scrollDirectionSecondary(scrollSecondary);
            return;
        }
        if (this.mOrientation == 0) {
            scrollX = scrollPrimary;
            scrollY = scrollSecondary;
        } else {
            scrollX = scrollSecondary;
            scrollY = scrollPrimary;
        }
        if (smooth) {
            this.mBaseGridView.smoothScrollBy(scrollX, scrollY);
            return;
        }
        this.mBaseGridView.scrollBy(scrollX, scrollY);
        dispatchChildSelectedAndPositioned();
    }

    public void setPruneChild(boolean pruneChild) {
        int i = 65536;
        if (((this.mFlag & 65536) != 0) != pruneChild) {
            int i2 = this.mFlag & -65537;
            if (!pruneChild) {
                i = 0;
            }
            this.mFlag = i2 | i;
            if (pruneChild) {
                requestLayout();
            }
        }
    }

    public boolean getPruneChild() {
        return (this.mFlag & 65536) != 0;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        int i = 0;
        if (((this.mFlag & 131072) != 0) != scrollEnabled) {
            int i2 = this.mFlag & -131073;
            if (scrollEnabled) {
                i = 131072;
            }
            this.mFlag = i2 | i;
            if ((this.mFlag & 131072) != 0 && this.mFocusScrollStrategy == 0 && this.mFocusPosition != -1) {
                scrollToSelection(this.mFocusPosition, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
            }
        }
    }

    public boolean isScrollEnabled() {
        return (this.mFlag & 131072) != 0;
    }

    private int findImmediateChildIndex(View view) {
        View view2;
        if (this.mBaseGridView == null || view == this.mBaseGridView || (view2 = findContainingItemView(view)) == null) {
            return -1;
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i) == view2) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            int i = this.mFocusPosition;
            while (true) {
                View view = findViewByPosition(i);
                if (view != null) {
                    if (view.getVisibility() != 0 || !view.hasFocusable()) {
                        i++;
                    } else {
                        view.requestFocus();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setFocusSearchDisabled(boolean disabled) {
        this.mFlag = (this.mFlag & -32769) | (disabled ? 32768 : 0);
    }

    /* access modifiers changed from: package-private */
    public boolean isFocusSearchDisabled() {
        return (this.mFlag & 32768) != 0;
    }

    public View onInterceptFocusSearch(View focused, int direction) {
        if ((this.mFlag & 32768) != 0) {
            return focused;
        }
        FocusFinder ff = FocusFinder.getInstance();
        View result = null;
        if (direction == 2 || direction == 1) {
            if (canScrollVertically()) {
                result = ff.findNextFocus(this.mBaseGridView, focused, direction == 2 ? 130 : 33);
            }
            if (canScrollHorizontally() != 0) {
                result = ff.findNextFocus(this.mBaseGridView, focused, (direction == 2) ^ (getLayoutDirection() == 1) ? 66 : 17);
            }
        } else {
            result = ff.findNextFocus(this.mBaseGridView, focused, direction);
        }
        if (result != null) {
            return result;
        }
        if (this.mBaseGridView.getDescendantFocusability() == 393216) {
            return this.mBaseGridView.getParent().focusSearch(focused, direction);
        }
        int movement = getMovement(direction);
        boolean isScroll = this.mBaseGridView.getScrollState() != 0;
        if (movement == 1) {
            if (isScroll || (this.mFlag & 4096) == 0) {
                result = focused;
            }
            if ((this.mFlag & 131072) != 0 && !hasCreatedLastItem()) {
                processPendingMovement(true);
                result = focused;
            }
        } else if (movement == 0) {
            if (isScroll || (this.mFlag & 2048) == 0) {
                result = focused;
            }
            if ((this.mFlag & 131072) != 0 && !hasCreatedFirstItem()) {
                processPendingMovement(false);
                result = focused;
            }
        } else if (movement == 3) {
            if (isScroll || (this.mFlag & 16384) == 0) {
                result = focused;
            }
        } else if (movement == 2 && (isScroll || (this.mFlag & 8192) == 0)) {
            result = focused;
        }
        if (result != null) {
            return result;
        }
        View result2 = this.mBaseGridView.getParent().focusSearch(focused, direction);
        if (result2 != null) {
            return result2;
        }
        return focused != null ? focused : this.mBaseGridView;
    }

    /* access modifiers changed from: package-private */
    public boolean hasPreviousViewInSameRow(int pos) {
        if (this.mGrid == null || pos == -1 || this.mGrid.getFirstVisibleIndex() < 0) {
            return false;
        }
        if (this.mGrid.getFirstVisibleIndex() > 0) {
            return true;
        }
        int focusedRow = this.mGrid.getLocation(pos).row;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            int position = getAdapterPositionByIndex(i);
            Grid.Location loc = this.mGrid.getLocation(position);
            if (loc != null && loc.row == focusedRow && position < pos) {
                return true;
            }
        }
        return false;
    }

    public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> views, int direction, int focusableMode) {
        View immediateFocusedChild;
        int loop_start;
        int i;
        int loop_end;
        int loop_start2;
        int focusedIndex;
        View focused;
        ArrayList<View> arrayList = views;
        int i2 = direction;
        int i3 = focusableMode;
        if ((this.mFlag & 32768) != 0) {
            return true;
        }
        if (!recyclerView.hasFocus()) {
            int focusableCount = views.size();
            if (this.mFocusScrollStrategy != 0) {
                int left = this.mWindowAlignment.mainAxis().getPaddingMin();
                int right = this.mWindowAlignment.mainAxis().getClientSize() + left;
                int count = getChildCount();
                for (int i4 = 0; i4 < count; i4++) {
                    View child = getChildAt(i4);
                    if (child.getVisibility() == 0 && getViewMin(child) >= left && getViewMax(child) <= right) {
                        child.addFocusables(arrayList, i2, i3);
                    }
                }
                if (views.size() == focusableCount) {
                    int count2 = getChildCount();
                    for (int i5 = 0; i5 < count2; i5++) {
                        View child2 = getChildAt(i5);
                        if (child2.getVisibility() == 0) {
                            child2.addFocusables(arrayList, i2, i3);
                        }
                    }
                }
            } else {
                View view = findViewByPosition(this.mFocusPosition);
                if (view != null) {
                    view.addFocusables(arrayList, i2, i3);
                }
            }
            if (views.size() != focusableCount) {
                return true;
            }
            if (recyclerView.isFocusable()) {
                arrayList.add(recyclerView);
                return true;
            }
            RecyclerView recyclerView2 = recyclerView;
            return true;
        } else if (this.mPendingMoveSmoothScroller != null) {
            return true;
        } else {
            int movement = getMovement(i2);
            View focused2 = recyclerView.findFocus();
            int focusedIndex2 = findImmediateChildIndex(focused2);
            int focusedPos = getAdapterPositionByIndex(focusedIndex2);
            if (focusedPos == -1) {
                immediateFocusedChild = null;
            } else {
                immediateFocusedChild = findViewByPosition(focusedPos);
            }
            if (immediateFocusedChild != null) {
                immediateFocusedChild.addFocusables(arrayList, i2, i3);
            }
            if (this.mGrid == null) {
                View view2 = focused2;
                int i6 = focusedIndex2;
                return true;
            } else if (getChildCount() == 0) {
                View view3 = focused2;
                int i7 = focusedIndex2;
                return true;
            } else if ((movement == 3 || movement == 2) && this.mGrid.getNumRows() <= 1) {
                return true;
            } else {
                int focusedRow = (this.mGrid == null || immediateFocusedChild == null) ? -1 : this.mGrid.getLocation(focusedPos).row;
                int focusableCount2 = views.size();
                int inc = (movement == 1 || movement == 3) ? 1 : -1;
                int i8 = 0;
                int loop_end2 = inc > 0 ? getChildCount() - 1 : 0;
                if (focusedIndex2 == -1) {
                    if (inc <= 0) {
                        i8 = getChildCount() - 1;
                    }
                    loop_start = i8;
                } else {
                    loop_start = focusedIndex2 + inc;
                }
                int i9 = loop_start;
                while (true) {
                    int i10 = i9;
                    if (inc <= 0) {
                        loop_end = loop_end2;
                        i = i10;
                        if (i < loop_end) {
                            break;
                        }
                    } else {
                        loop_end = loop_end2;
                        i = i10;
                        if (i > loop_end) {
                            break;
                        }
                    }
                    View child3 = getChildAt(i);
                    if (child3.getVisibility() == 0) {
                        if (!child3.hasFocusable()) {
                            focused = focused2;
                        } else if (immediateFocusedChild == null) {
                            child3.addFocusables(arrayList, i2, i3);
                            focused = focused2;
                            if (views.size() > focusableCount2) {
                                break;
                            }
                        } else {
                            focused = focused2;
                            int position = getAdapterPositionByIndex(i);
                            focusedIndex = focusedIndex2;
                            Grid.Location loc = this.mGrid.getLocation(position);
                            if (loc != null) {
                                loop_start2 = loop_start;
                                if (movement == 1) {
                                    if (loc.row == focusedRow && position > focusedPos) {
                                        child3.addFocusables(arrayList, i2, i3);
                                        if (views.size() > focusableCount2) {
                                            break;
                                        }
                                    }
                                } else if (movement == 0) {
                                    if (loc.row == focusedRow && position < focusedPos) {
                                        child3.addFocusables(arrayList, i2, i3);
                                        if (views.size() > focusableCount2) {
                                            break;
                                        }
                                    }
                                } else if (movement == 3) {
                                    if (loc.row == focusedRow) {
                                        continue;
                                    } else if (loc.row < focusedRow) {
                                        break;
                                    } else {
                                        child3.addFocusables(arrayList, i2, i3);
                                    }
                                } else if (movement == 2 && loc.row != focusedRow) {
                                    if (loc.row > focusedRow) {
                                        break;
                                    }
                                    child3.addFocusables(arrayList, i2, i3);
                                }
                            }
                            loop_start2 = loop_start;
                        }
                        focusedIndex = focusedIndex2;
                        loop_start2 = loop_start;
                    } else {
                        focused = focused2;
                        focusedIndex = focusedIndex2;
                        loop_start2 = loop_start;
                    }
                    i9 = i + inc;
                    loop_end2 = loop_end;
                    focused2 = focused;
                    focusedIndex2 = focusedIndex;
                    loop_start = loop_start2;
                }
                RecyclerView recyclerView3 = recyclerView;
                return true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasCreatedLastItem() {
        int count = getItemCount();
        return count == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(count + -1) != null;
    }

    /* access modifiers changed from: package-private */
    public boolean hasCreatedFirstItem() {
        return getItemCount() == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(0) != null;
    }

    /* access modifiers changed from: package-private */
    public boolean isItemFullyVisible(int pos) {
        RecyclerView.ViewHolder vh = this.mBaseGridView.findViewHolderForAdapterPosition(pos);
        if (vh != null && vh.itemView.getLeft() >= 0 && vh.itemView.getRight() <= this.mBaseGridView.getWidth() && vh.itemView.getTop() >= 0 && vh.itemView.getBottom() <= this.mBaseGridView.getHeight()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean canScrollTo(View view) {
        return view.getVisibility() == 0 && (!hasFocus() || view.hasFocusable());
    }

    /* access modifiers changed from: package-private */
    public boolean gridOnRequestFocusInDescendants(RecyclerView recyclerView, int direction, Rect previouslyFocusedRect) {
        switch (this.mFocusScrollStrategy) {
            case 1:
            case 2:
                return gridOnRequestFocusInDescendantsUnaligned(recyclerView, direction, previouslyFocusedRect);
            default:
                return gridOnRequestFocusInDescendantsAligned(recyclerView, direction, previouslyFocusedRect);
        }
    }

    private boolean gridOnRequestFocusInDescendantsAligned(RecyclerView recyclerView, int direction, Rect previouslyFocusedRect) {
        View view = findViewByPosition(this.mFocusPosition);
        if (view != null) {
            return view.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    private boolean gridOnRequestFocusInDescendantsUnaligned(RecyclerView recyclerView, int direction, Rect previouslyFocusedRect) {
        int end;
        int increment;
        int index;
        int count = getChildCount();
        if ((direction & 2) != 0) {
            index = 0;
            increment = 1;
            end = count;
        } else {
            index = count - 1;
            increment = -1;
            end = -1;
        }
        int left = this.mWindowAlignment.mainAxis().getPaddingMin();
        int right = this.mWindowAlignment.mainAxis().getClientSize() + left;
        for (int i = index; i != end; i += increment) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0 && getViewMin(child) >= left && getViewMax(child) <= right && child.requestFocus(direction, previouslyFocusedRect)) {
                return true;
            }
        }
        return false;
    }

    private int getMovement(int direction) {
        if (this.mOrientation == 0) {
            int movement = 0;
            if (direction == 17) {
                if ((this.mFlag & 262144) != 0) {
                    movement = 1;
                }
                return movement;
            } else if (direction == 33) {
                return 2;
            } else {
                if (direction == 66) {
                    if ((this.mFlag & 262144) == 0) {
                        movement = 1;
                    }
                    return movement;
                } else if (direction != 130) {
                    return 17;
                } else {
                    return 3;
                }
            }
        } else if (this.mOrientation != 1) {
            return 17;
        } else {
            int movement2 = 2;
            if (direction == 17) {
                if ((this.mFlag & 524288) != 0) {
                    movement2 = 3;
                }
                return movement2;
            } else if (direction == 33) {
                return 0;
            } else {
                if (direction == 66) {
                    if ((this.mFlag & 524288) == 0) {
                        movement2 = 3;
                    }
                    return movement2;
                } else if (direction != 130) {
                    return 17;
                } else {
                    return 1;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getChildDrawingOrder(RecyclerView recyclerView, int childCount, int i) {
        int focusIndex;
        View view = findViewByPosition(this.mFocusPosition);
        if (view == null || i < (focusIndex = recyclerView.indexOfChild(view))) {
            return i;
        }
        if (i < childCount - 1) {
            return ((focusIndex + childCount) - 1) - i;
        }
        return focusIndex;
    }

    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        if (oldAdapter != null) {
            discardLayoutInfo();
            this.mFocusPosition = -1;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.clear();
        }
        if (newAdapter instanceof FacetProviderAdapter) {
            this.mFacetProviderAdapter = (FacetProviderAdapter) newAdapter;
        } else {
            this.mFacetProviderAdapter = null;
        }
        super.onAdapterChanged(oldAdapter, newAdapter);
    }

    private void discardLayoutInfo() {
        this.mGrid = null;
        this.mRowSizeSecondary = null;
        this.mFlag &= -1025;
    }

    public void setLayoutEnabled(boolean layoutEnabled) {
        int i = 512;
        if (((this.mFlag & 512) != 0) != layoutEnabled) {
            int i2 = this.mFlag & -513;
            if (!layoutEnabled) {
                i = 0;
            }
            this.mFlag = i2 | i;
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void setChildrenVisibility(int visibility) {
        this.mChildVisibility = visibility;
        if (this.mChildVisibility != -1) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).setVisibility(this.mChildVisibility);
            }
        }
    }

    static final class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle childStates = Bundle.EMPTY;
        int index;

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.index);
            out.writeBundle(this.childStates);
        }

        public int describeContents() {
            return 0;
        }

        SavedState(Parcel in) {
            this.index = in.readInt();
            this.childStates = in.readBundle(GridLayoutManager.class.getClassLoader());
        }

        SavedState() {
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState();
        ss.index = getSelection();
        Bundle bundle = this.mChildrenStates.saveAsBundle();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            int position = getAdapterPositionByView(view);
            if (position != -1) {
                bundle = this.mChildrenStates.saveOnScreenView(bundle, view, position);
            }
        }
        ss.childStates = bundle;
        return ss;
    }

    /* access modifiers changed from: package-private */
    public void onChildRecycled(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position != -1) {
            this.mChildrenStates.saveOffscreenView(holder.itemView, position);
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState loadingState = (SavedState) state;
            this.mFocusPosition = loadingState.index;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.loadFromBundle(loadingState.childStates);
            this.mFlag |= 256;
            requestLayout();
        }
    }

    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation != 0 || this.mGrid == null) {
            return super.getRowCountForAccessibility(recycler, state);
        }
        return this.mGrid.getNumRows();
    }

    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation != 1 || this.mGrid == null) {
            return super.getColumnCountForAccessibility(recycler, state);
        }
        return this.mGrid.getNumRows();
    }

    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
        ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (this.mGrid != null && (lp instanceof LayoutParams)) {
            int position = ((LayoutParams) lp).getViewAdapterPosition();
            int rowIndex = position >= 0 ? this.mGrid.getRowIndex(position) : -1;
            if (rowIndex >= 0) {
                int guessSpanIndex = position / this.mGrid.getNumRows();
                if (this.mOrientation == 0) {
                    info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(rowIndex, 1, guessSpanIndex, 1, false, false));
                } else {
                    info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(guessSpanIndex, 1, rowIndex, 1, false, false));
                }
            }
        }
    }

    public boolean performAccessibilityAction(RecyclerView.Recycler recycler, RecyclerView.State state, int action, Bundle args) {
        if (!isScrollEnabled()) {
            return true;
        }
        saveContext(recycler, state);
        int translatedAction = action;
        boolean reverseFlowPrimary = (this.mFlag & 262144) != 0;
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.mOrientation == 0) {
                if (action == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT.getId()) {
                    translatedAction = reverseFlowPrimary ? 4096 : 8192;
                } else if (action == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT.getId()) {
                    translatedAction = reverseFlowPrimary ? 8192 : 4096;
                }
            } else if (action == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP.getId()) {
                translatedAction = 8192;
            } else if (action == AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN.getId()) {
                translatedAction = 4096;
            }
        }
        if (translatedAction == 4096) {
            processPendingMovement(true);
            processSelectionMoves(false, 1);
        } else if (translatedAction == 8192) {
            processPendingMovement(false);
            processSelectionMoves(false, -1);
        }
        leaveContext();
        return true;
    }

    /* access modifiers changed from: package-private */
    public int processSelectionMoves(boolean preventScroll, int moves) {
        if (this.mGrid == null) {
            return moves;
        }
        int focusPosition = this.mFocusPosition;
        int focusedRow = focusPosition != -1 ? this.mGrid.getRowIndex(focusPosition) : -1;
        View newSelected = null;
        int count = getChildCount();
        for (int i = 0; i < count && moves != 0; i++) {
            int index = moves > 0 ? i : (count - 1) - i;
            View child = getChildAt(index);
            if (canScrollTo(child)) {
                int position = getAdapterPositionByIndex(index);
                int rowIndex = this.mGrid.getRowIndex(position);
                if (focusedRow == -1) {
                    focusPosition = position;
                    newSelected = child;
                    focusedRow = rowIndex;
                } else if (rowIndex == focusedRow && ((moves > 0 && position > focusPosition) || (moves < 0 && position < focusPosition))) {
                    focusPosition = position;
                    newSelected = child;
                    if (moves > 0) {
                        moves--;
                    } else {
                        moves++;
                    }
                }
            }
        }
        if (newSelected != null) {
            if (preventScroll) {
                if (hasFocus()) {
                    this.mFlag |= 32;
                    newSelected.requestFocus();
                    this.mFlag &= -33;
                }
                this.mFocusPosition = focusPosition;
                this.mSubFocusPosition = 0;
            } else {
                scrollToView(newSelected, true);
            }
        }
        return moves;
    }

    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat info) {
        saveContext(recycler, state);
        int count = state.getItemCount();
        boolean reverseFlowPrimary = (this.mFlag & 262144) != 0;
        if (count > 1 && !isItemFullyVisible(0)) {
            if (Build.VERSION.SDK_INT < 23) {
                info.addAction(8192);
            } else if (this.mOrientation == 0) {
                info.addAction(reverseFlowPrimary ? AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT : AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT);
            } else {
                info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP);
            }
            info.setScrollable(true);
        }
        if (count > 1 && !isItemFullyVisible(count - 1)) {
            if (Build.VERSION.SDK_INT < 23) {
                info.addAction(4096);
            } else if (this.mOrientation == 0) {
                info.addAction(reverseFlowPrimary ? AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT : AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT);
            } else {
                info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN);
            }
            info.setScrollable(true);
        }
        info.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        leaveContext();
    }
}
