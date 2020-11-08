package com.android.tv.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import com.android.tv.settings.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ScrollAdapterView extends AdapterView<Adapter> {
    private static final boolean DBG = false;
    private static final boolean DEBUG_FOCUS = false;
    private static final boolean DEFAULT_NAVIGATE_IN_ANIMATION_ALLOWED = true;
    private static final boolean DEFAULT_NAVIGATE_OUT_ALLOWED = true;
    private static final boolean DEFAULT_NAVIGATE_OUT_OF_OFF_AXIS_ALLOWED = true;
    public static final int GRID_SETTING_AUTO = 0;
    public static final int GRID_SETTING_SINGLE = 1;
    public static final int HORIZONTAL = 0;
    private static final int MAX_RECYCLED_EXPANDED_VIEWS = 3;
    private static final int MAX_RECYCLED_VIEWS = 10;
    private static final int NO_SCROLL = 0;
    private static final int SCROLL_AND_CENTER_FOCUS = 3;
    private static final int SEARCH_ID_RANGE = 30;
    private static final String TAG = "ScrollAdapterView";
    public static final int VERTICAL = 1;
    private ScrollAdapter mAdapter;
    private ScrollAdapterCustomAlign mAdapterCustomAlign;
    private ScrollAdapterCustomSize mAdapterCustomSize;
    /* access modifiers changed from: private */
    public boolean mAnimateLayoutChange = true;
    private final ScrollInfo mCurScroll = new ScrollInfo();
    private final DataSetObserver mDataObserver = new DataSetObserver() {
        public void onChanged() {
            ScrollAdapterView.this.fireDataSetChanged();
        }

        public void onInvalidated() {
            ScrollAdapterView.this.fireDataSetChanged();
        }
    };
    private boolean mDataSetChangedFlag;
    private ScrollAdapterBase mExpandAdapter;
    private final ExpandableChildStates mExpandableChildStates = new ExpandableChildStates();
    private final ExpandedChildStates mExpandedChildStates = new ExpandedChildStates();
    /* access modifiers changed from: private */
    public Animator mExpandedItemInAnim = null;
    /* access modifiers changed from: private */
    public Animator mExpandedItemOutAnim = null;
    /* access modifiers changed from: private */
    public final ArrayList<ExpandedView> mExpandedViews = new ArrayList<>(4);
    private int mGridSetting = 1;
    private int mItemSelected = -1;
    private ScrollAdapterTransform mItemTransform;
    private int mItemsOnOffAxis;
    private int mLeftIndex;
    private AdapterViewState mLoadingState;
    private boolean mMadeInitialSelection = false;
    private int mMeasuredSpec = -1;
    private boolean mNavigateInAnimationAllowed = true;
    private boolean mNavigateOutAllowed = true;
    private boolean mNavigateOutOfOffAxisAllowed = true;
    private final ArrayList<OnItemChangeListener> mOnItemChangeListeners = new ArrayList<>();
    private final ArrayList<OnScrollListener> mOnScrollListeners = new ArrayList<>();
    private int mOrientation = 0;
    private float mPendingScrollPosition = 0.0f;
    private int mPendingSelection = -1;
    private boolean mPlaySoundEffects = true;
    private final RecycledViews mRecycleExpandedViews = new RecycledViews(3);
    private final RecycledViews mRecycleViews = new RecycledViews(10);
    private int mRightIndex;
    private int mScrapHeight;
    private int mScrapWidth;
    private final ScrollController mScroll = new ScrollController(getContext());
    private final ScrollInfo mScrollBeforeReset = new ScrollInfo();
    final Runnable mScrollTask = new Runnable() {
        public void run() {
            try {
                ScrollAdapterView.this.scrollTaskRunInternal();
            } catch (RuntimeException ex) {
                ScrollAdapterView.this.reset();
                ex.printStackTrace();
            }
        }
    };
    private boolean mScrollTaskRunning;
    private int mScrollerState;
    private int mSelectedIndex;
    private int mSelectedSize;
    private int mSpace;
    private int mSpaceHigh;
    private int mSpaceLow;
    final Rect mTempRect = new Rect();

    public interface OnItemChangeListener {
        void onItemSelected(View view, int i, int i2);
    }

    public interface OnScrollListener {
        void onScrolled(View view, int i, float f, float f2);
    }

    private static class RecycledViews {
        ScrollAdapterBase mAdapter;
        final int mMaxRecycledViews;
        List<View>[] mViews;

        RecycledViews(int max) {
            this.mMaxRecycledViews = max;
        }

        /* access modifiers changed from: package-private */
        public void updateAdapter(ScrollAdapterBase adapter) {
            if (adapter != null) {
                int typeCount = adapter.getViewTypeCount();
                if (this.mViews == null || typeCount != this.mViews.length) {
                    this.mViews = new List[typeCount];
                    for (int i = 0; i < typeCount; i++) {
                        this.mViews[i] = new ArrayList();
                    }
                }
            }
            this.mAdapter = adapter;
        }

        /* access modifiers changed from: package-private */
        public void recycleView(View child, int type) {
            if (this.mAdapter != null) {
                this.mAdapter.viewRemoved(child);
            }
            if (this.mViews != null && type >= 0 && type < this.mViews.length && this.mViews[type].size() < this.mMaxRecycledViews) {
                this.mViews[type].add(child);
            }
        }

        /* access modifiers changed from: package-private */
        public View getView(int type) {
            if (this.mViews == null || type < 0 || type >= this.mViews.length) {
                return null;
            }
            List<View> array = this.mViews[type];
            if (array.size() > 0) {
                return array.remove(array.size() - 1);
            }
            return null;
        }
    }

    final class ExpandableChildStates extends ViewsStateBundle {
        ExpandableChildStates() {
            super(0, 0);
        }

        /* access modifiers changed from: protected */
        public void saveVisibleViewsUnchecked() {
            int last = ScrollAdapterView.this.lastExpandableIndex();
            for (int i = ScrollAdapterView.this.firstExpandableIndex(); i < last; i++) {
                saveViewUnchecked(ScrollAdapterView.this.getChildAt(i), ScrollAdapterView.this.getAdapterIndex(i));
            }
        }
    }

    final class ExpandedChildStates extends ViewsStateBundle {
        ExpandedChildStates() {
            super(2, 100);
        }

        /* access modifiers changed from: protected */
        public void saveVisibleViewsUnchecked() {
            int size = ScrollAdapterView.this.mExpandedViews.size();
            for (int i = 0; i < size; i++) {
                ExpandedView v = (ExpandedView) ScrollAdapterView.this.mExpandedViews.get(i);
                saveViewUnchecked(v.expandedView, v.index);
            }
        }
    }

    private static class ChildViewHolder {
        int mExtraSpaceLow;
        final int mItemViewType;
        float mLocation;
        float mLocationInParent;
        int mMaxSize;
        int mScrollCenter;

        ChildViewHolder(int t) {
            this.mItemViewType = t;
        }
    }

    private static class ScrollInfo {
        long id;
        int index;
        float mainPos;
        float secondPos;
        int viewLocation;

        ScrollInfo() {
            clear();
        }

        /* access modifiers changed from: package-private */
        public boolean isValid() {
            return this.index >= 0;
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            this.index = -1;
            this.id = Long.MIN_VALUE;
        }

        /* access modifiers changed from: package-private */
        public void copyFrom(ScrollInfo other) {
            this.index = other.index;
            this.id = other.id;
            this.mainPos = other.mainPos;
            this.secondPos = other.secondPos;
            this.viewLocation = other.viewLocation;
        }
    }

    final class ExpandedView {
        private static final int ANIM_DURATION = 450;
        final View expandedView;
        Animator grow_anim;
        final int index;
        float progress = 0.0f;
        Animator shrink_anim;
        final int viewType;

        ExpandedView(View v, int i, int t) {
            this.expandedView = v;
            this.index = i;
            this.viewType = t;
        }

        /* access modifiers changed from: package-private */
        public Animator createFadeInAnimator() {
            if (ScrollAdapterView.this.mExpandedItemInAnim != null) {
                return ScrollAdapterView.this.mExpandedItemInAnim.clone();
            }
            this.expandedView.setAlpha(0.0f);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat((Object) null, "alpha", new float[]{1.0f});
            anim1.setStartDelay(225);
            anim1.setDuration(900);
            return anim1;
        }

        /* access modifiers changed from: package-private */
        public Animator createFadeOutAnimator() {
            if (ScrollAdapterView.this.mExpandedItemOutAnim != null) {
                return ScrollAdapterView.this.mExpandedItemOutAnim.clone();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat((Object) null, "alpha", new float[]{0.0f});
            anim1.setDuration(450);
            return anim1;
        }

        /* access modifiers changed from: package-private */
        public void setProgress(float p) {
            boolean shrinking = false;
            boolean growing = p > this.progress;
            if (p < this.progress) {
                shrinking = true;
            }
            this.progress = p;
            if (growing) {
                if (this.shrink_anim != null) {
                    this.shrink_anim.cancel();
                    this.shrink_anim = null;
                }
                if (this.grow_anim == null) {
                    this.grow_anim = createFadeInAnimator();
                    this.grow_anim.setTarget(this.expandedView);
                    this.grow_anim.start();
                }
                if (!ScrollAdapterView.this.mAnimateLayoutChange) {
                    this.grow_anim.end();
                }
            } else if (shrinking) {
                if (this.grow_anim != null) {
                    this.grow_anim.cancel();
                    this.grow_anim = null;
                }
                if (this.shrink_anim == null) {
                    this.shrink_anim = createFadeOutAnimator();
                    this.shrink_anim.setTarget(this.expandedView);
                    this.shrink_anim.start();
                }
                if (!ScrollAdapterView.this.mAnimateLayoutChange) {
                    this.shrink_anim.end();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void close() {
            if (this.shrink_anim != null) {
                this.shrink_anim.cancel();
                this.shrink_anim = null;
            }
            if (this.grow_anim != null) {
                this.grow_anim.cancel();
                this.grow_anim = null;
            }
        }
    }

    public ScrollAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
        setSoundEffectsEnabled(true);
        setWillNotDraw(true);
        initFromAttributes(context, attrs);
        reset();
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollAdapterView);
        setOrientation(a.getInt(10, 0));
        this.mScroll.setScrollItemAlign(a.getInt(14, 0));
        setGridSetting(a.getInt(2, 1));
        if (a.hasValue(6)) {
            setLowItemTransform(AnimatorInflater.loadAnimator(getContext(), a.getResourceId(6, -1)));
        }
        if (a.hasValue(3)) {
            setHighItemTransform(AnimatorInflater.loadAnimator(getContext(), a.getResourceId(3, -1)));
        }
        if (a.hasValue(0)) {
            this.mExpandedItemInAnim = AnimatorInflater.loadAnimator(getContext(), a.getResourceId(0, -1));
        }
        if (a.hasValue(1)) {
            this.mExpandedItemOutAnim = AnimatorInflater.loadAnimator(getContext(), a.getResourceId(1, -1));
        }
        setSpace(a.getDimensionPixelSize(17, 0));
        setSelectedTakesMoreSpace(a.getBoolean(16, false));
        setSelectedSize(a.getDimensionPixelSize(15, 0));
        setScrollCenterStrategy(a.getInt(13, 0));
        setScrollCenterOffset(a.getDimensionPixelSize(11, 0));
        setScrollCenterOffsetPercent(a.getInt(12, 0));
        setNavigateOutAllowed(a.getBoolean(8, true));
        setNavigateOutOfOffAxisAllowed(a.getBoolean(9, true));
        setNavigateInAnimationAllowed(a.getBoolean(7, true));
        this.mScroll.lerper().setDivisor(a.getFloat(5, 2.0f));
        a.recycle();
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
        this.mScroll.setOrientation(orientation);
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    /* access modifiers changed from: private */
    public void reset() {
        this.mScrollBeforeReset.copyFrom(this.mCurScroll);
        this.mLeftIndex = -1;
        this.mRightIndex = 0;
        this.mDataSetChangedFlag = false;
        int c = this.mExpandedViews.size();
        for (int i = 0; i < c; i++) {
            ExpandedView v = this.mExpandedViews.get(i);
            v.close();
            removeViewInLayout(v.expandedView);
            this.mRecycleExpandedViews.recycleView(v.expandedView, v.viewType);
        }
        this.mExpandedViews.clear();
        for (int i2 = getChildCount() - 1; i2 >= 0; i2--) {
            View child = getChildAt(i2);
            removeViewInLayout(child);
            recycleExpandableView(child);
        }
        this.mRecycleViews.updateAdapter(this.mAdapter);
        this.mRecycleExpandedViews.updateAdapter(this.mExpandAdapter);
        this.mSelectedIndex = -1;
        this.mCurScroll.clear();
        this.mMadeInitialSelection = false;
    }

    private int findViewIndexContainingScrollCenter(int scrollCenter, int scrollCenterOffAxis, boolean findNext) {
        int viewSizeOffAxis;
        int lastExpandable = lastExpandableIndex();
        int i = firstExpandableIndex();
        while (i < lastExpandable) {
            View view = getChildAt(i);
            int centerOffAxis = getCenterInOffAxis(view);
            if (this.mOrientation == 0) {
                viewSizeOffAxis = view.getHeight();
            } else {
                viewSizeOffAxis = view.getWidth();
            }
            int centerMain = getScrollCenter(view);
            if (!hasScrollPosition(centerMain, getSize(view), scrollCenter) || (this.mItemsOnOffAxis != 1 && !hasScrollPositionSecondAxis(scrollCenterOffAxis, viewSizeOffAxis, centerOffAxis))) {
                i++;
            } else if (!findNext) {
                return i;
            } else {
                if (!this.mScroll.isMainAxisMovingForward() || centerMain >= scrollCenter) {
                    if (!this.mScroll.isMainAxisMovingForward() && centerMain > scrollCenter && i - this.mItemsOnOffAxis >= firstExpandableIndex()) {
                        i -= this.mItemsOnOffAxis;
                    }
                } else if (this.mItemsOnOffAxis + i < lastExpandableIndex()) {
                    i += this.mItemsOnOffAxis;
                }
                if (this.mItemsOnOffAxis == 1) {
                    return i;
                }
                if (!this.mScroll.isSecondAxisMovingForward() || centerOffAxis >= scrollCenterOffAxis) {
                    if (this.mScroll.isSecondAxisMovingForward() || centerOffAxis >= scrollCenterOffAxis || i - 1 < firstExpandableIndex()) {
                        return i;
                    }
                    return i - 1;
                } else if (i + 1 < lastExpandableIndex()) {
                    return i + 1;
                } else {
                    return i;
                }
            }
        }
        return -1;
    }

    private int findViewIndexContainingScrollCenter() {
        return findViewIndexContainingScrollCenter(this.mScroll.mainAxis().getScrollCenter(), this.mScroll.secondAxis().getScrollCenter(), false);
    }

    public int getFirstVisiblePosition() {
        int first = firstExpandableIndex();
        if (lastExpandableIndex() == first) {
            return -1;
        }
        return getAdapterIndex(first);
    }

    public int getLastVisiblePosition() {
        int last = lastExpandableIndex();
        if (firstExpandableIndex() == last) {
            return -1;
        }
        return getAdapterIndex(last - 1);
    }

    public void setSelection(int position) {
        setSelectionInternal(position, 0.0f, true);
    }

    public void setSelection(int position, float offset) {
        setSelectionInternal(position, offset, true);
    }

    public int getCurrentAnimationDuration() {
        return this.mScroll.getCurrentAnimationDuration();
    }

    public void setSelectionSmooth(int index) {
        setSelectionSmooth(index, 0);
    }

    public void setSelectionSmooth(int index, int duration) {
        int adapterIndex;
        int direction;
        int currentExpandableIndex = indexOfChild(getSelectedView());
        if (currentExpandableIndex >= 0 && index != (adapterIndex = getAdapterIndex(currentExpandableIndex))) {
            boolean isGrowing = index > adapterIndex;
            View nextTop = null;
            if (!isGrowing) {
                while (true) {
                    if (index < getAdapterIndex(firstExpandableIndex())) {
                        if (!fillOneLeftChildView(false)) {
                            break;
                        }
                    } else {
                        nextTop = getChildAt(expandableIndexFromAdapterIndex(index));
                        break;
                    }
                }
            } else {
                while (true) {
                    if (index >= getAdapterIndex(lastExpandableIndex())) {
                        if (!fillOneRightChildView(false)) {
                            break;
                        }
                    } else {
                        nextTop = getChildAt(expandableIndexFromAdapterIndex(index));
                        break;
                    }
                }
            }
            if (nextTop != null) {
                if (isGrowing) {
                    direction = this.mOrientation == 0 ? 66 : 130;
                } else {
                    direction = this.mOrientation == 0 ? 17 : 33;
                }
                scrollAndFocusTo(nextTop, direction, false, duration, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void fireDataSetChanged() {
        this.mDataSetChangedFlag = true;
        scheduleScrollTask();
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataObserver);
        }
        this.mAdapter = (ScrollAdapter) adapter;
        this.mExpandAdapter = this.mAdapter.getExpandAdapter();
        this.mAdapter.registerDataSetObserver(this.mDataObserver);
        this.mAdapterCustomSize = adapter instanceof ScrollAdapterCustomSize ? (ScrollAdapterCustomSize) adapter : null;
        this.mAdapterCustomAlign = adapter instanceof ScrollAdapterCustomAlign ? (ScrollAdapterCustomAlign) adapter : null;
        this.mMeasuredSpec = -1;
        this.mLoadingState = null;
        this.mPendingSelection = -1;
        this.mExpandableChildStates.clear();
        this.mExpandedChildStates.clear();
        this.mCurScroll.clear();
        this.mScrollBeforeReset.clear();
        fireDataSetChanged();
    }

    public View getSelectedView() {
        if (this.mSelectedIndex >= 0) {
            return getChildAt(expandableIndexFromAdapterIndex(this.mSelectedIndex));
        }
        return null;
    }

    public View getSelectedExpandedView() {
        ExpandedView ev = findExpandedView(this.mExpandedViews, getSelectedItemPosition());
        if (ev == null) {
            return null;
        }
        return ev.expandedView;
    }

    public View getViewContainingScrollCenter() {
        return getChildAt(findViewIndexContainingScrollCenter());
    }

    public int getIndexContainingScrollCenter() {
        return getAdapterIndex(findViewIndexContainingScrollCenter());
    }

    public int getSelectedItemPosition() {
        return this.mSelectedIndex;
    }

    public Object getSelectedItem() {
        int index = getSelectedItemPosition();
        if (index < 0) {
            return null;
        }
        return getAdapter().getItem(index);
    }

    public long getSelectedItemId() {
        int index;
        if (this.mAdapter == null || (index = getSelectedItemPosition()) < 0) {
            return Long.MIN_VALUE;
        }
        return this.mAdapter.getItemId(index);
    }

    public View getItemView(int position) {
        int index = expandableIndexFromAdapterIndex(position);
        if (index < firstExpandableIndex() || index >= lastExpandableIndex()) {
            return null;
        }
        return getChildAt(index);
    }

    private void adjustSystemScrollPos() {
        scrollTo(this.mScroll.horizontal.getSystemScrollPos(), this.mScroll.vertical.getSystemScrollPos());
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mScroll.horizontal.setSize(w);
        this.mScroll.vertical.setSize(h);
        scheduleScrollTask();
    }

    private void applyTransformations() {
        int i;
        if (this.mItemTransform != null) {
            int lastExpandable = lastExpandableIndex();
            for (int i2 = firstExpandableIndex(); i2 < lastExpandable; i2++) {
                View child = getChildAt(i2);
                ScrollAdapterTransform scrollAdapterTransform = this.mItemTransform;
                int scrollCenter = getScrollCenter(child) - this.mScroll.mainAxis().getScrollCenter();
                if (this.mItemsOnOffAxis == 1) {
                    i = 0;
                } else {
                    i = getCenterInOffAxis(child) - this.mScroll.secondAxis().getScrollCenter();
                }
                scrollAdapterTransform.transform(child, scrollCenter, i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateViewsLocations(true);
    }

    private void scheduleScrollTask() {
        if (!this.mScrollTaskRunning) {
            this.mScrollTaskRunning = true;
            postOnAnimation(this.mScrollTask);
        }
    }

    /* access modifiers changed from: private */
    public void scrollTaskRunInternal() {
        this.mScrollTaskRunning = false;
        if (this.mDataSetChangedFlag) {
            reset();
        }
        if (this.mAdapter == null || this.mAdapter.getCount() == 0) {
            invalidate();
            if (this.mAdapter != null) {
                fireItemChange();
            }
        } else if (this.mMeasuredSpec == -1) {
            requestLayout();
            scheduleScrollTask();
        } else {
            restoreLoadingState();
            this.mScroll.computeAndSetScrollPosition();
            boolean noChildBeforeFill = getChildCount() == 0;
            if (!noChildBeforeFill) {
                updateViewsLocations(false);
                adjustSystemScrollPos();
            }
            pruneInvisibleViewsInLayout();
            fillVisibleViewsInLayout();
            if (noChildBeforeFill && getChildCount() > 0) {
                updateViewsLocations(false);
                adjustSystemScrollPos();
            }
            fireScrollChange();
            applyTransformations();
            if (!this.mScroll.isFinished()) {
                scheduleScrollTask();
                return;
            }
            invalidate();
            fireItemChange();
        }
    }

    public void requestChildFocus(View child, View focused) {
        boolean receiveFocus = getFocusedChild() == null && child != null;
        super.requestChildFocus(child, focused);
        if (receiveFocus && this.mScroll.isFinished()) {
            scheduleScrollTask();
        }
    }

    private void recycleExpandableView(View child) {
        ChildViewHolder holder = (ChildViewHolder) child.getTag(R.id.ScrollAdapterViewChild);
        if (holder != null) {
            this.mRecycleViews.recycleView(child, holder.mItemViewType);
        }
    }

    private void pruneInvisibleViewsInLayout() {
        View selectedView = getSelectedView();
        if (this.mScroll.isFinished() || this.mScroll.isMainAxisMovingForward()) {
            while (true) {
                int firstIndex = firstExpandableIndex();
                View child = getChildAt(firstIndex);
                if (child == selectedView || getChildAt(this.mItemsOnOffAxis + firstIndex) == null) {
                    break;
                }
                if (this.mOrientation != 0) {
                    if (child.getBottom() - getScrollY() > 0) {
                        break;
                    }
                } else if (child.getRight() - getScrollX() > 0) {
                    break;
                }
                boolean foundFocus = false;
                int i = 0;
                while (true) {
                    if (i >= this.mItemsOnOffAxis) {
                        break;
                    } else if (childHasFocus(firstIndex + i)) {
                        foundFocus = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (foundFocus) {
                    break;
                }
                View view = child;
                for (int i2 = 0; i2 < this.mItemsOnOffAxis; i2++) {
                    View child2 = getChildAt(firstExpandableIndex());
                    this.mExpandableChildStates.saveInvisibleView(child2, this.mLeftIndex + 1);
                    removeViewInLayout(child2);
                    recycleExpandableView(child2);
                    this.mLeftIndex++;
                }
            }
        }
        if (this.mScroll.isFinished() || !this.mScroll.isMainAxisMovingForward()) {
            while (true) {
                int count = this.mRightIndex % this.mItemsOnOffAxis;
                if (count == 0) {
                    count = this.mItemsOnOffAxis;
                }
                if (count <= (this.mRightIndex - this.mLeftIndex) - 1) {
                    int lastIndex = lastExpandableIndex();
                    View child3 = getChildAt(lastIndex - 1);
                    if (child3 != selectedView) {
                        if (this.mOrientation == 0) {
                            if (child3.getLeft() - getScrollX() < getWidth()) {
                                return;
                            }
                        } else if (child3.getTop() - getScrollY() < getHeight()) {
                            return;
                        }
                        boolean foundFocus2 = false;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= count) {
                                break;
                            } else if (childHasFocus((lastIndex - 1) - i3)) {
                                foundFocus2 = true;
                                break;
                            } else {
                                i3++;
                            }
                        }
                        if (!foundFocus2) {
                            View view2 = child3;
                            for (int i4 = 0; i4 < count; i4++) {
                                View child4 = getChildAt(lastExpandableIndex() - 1);
                                this.mExpandableChildStates.saveInvisibleView(child4, this.mRightIndex - 1);
                                removeViewInLayout(child4);
                                recycleExpandableView(child4);
                                this.mRightIndex--;
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    private boolean childHasFocus(int expandableViewIndex) {
        if (getChildAt(expandableViewIndex).hasFocus()) {
            return true;
        }
        ExpandedView v = findExpandedView(this.mExpandedViews, getAdapterIndex(expandableViewIndex));
        if (v == null || !v.expandedView.hasFocus()) {
            return false;
        }
        return true;
    }

    public void setGridSetting(int gridSetting) {
        this.mGridSetting = gridSetting;
        requestLayout();
    }

    public int getGridSetting() {
        return this.mGridSetting;
    }

    private void fillVisibleViewsInLayout() {
        do {
        } while (fillOneRightChildView(true));
        do {
        } while (fillOneLeftChildView(true));
        if (this.mRightIndex < 0 || this.mLeftIndex != -1) {
            this.mScroll.mainAxis().invalidateScrollMin();
        } else {
            View child = getChildAt(firstExpandableIndex());
            int scrollCenter = getScrollCenter(child);
            this.mScroll.mainAxis().updateScrollMin(scrollCenter, getScrollLow(scrollCenter, child));
        }
        if (this.mRightIndex == this.mAdapter.getCount()) {
            View child2 = getChildAt(lastExpandableIndex() - 1);
            int scrollCenter2 = getScrollCenter(child2);
            this.mScroll.mainAxis().updateScrollMax(scrollCenter2, getScrollHigh(scrollCenter2, child2));
            return;
        }
        this.mScroll.mainAxis().invalidateScrollMax();
    }

    private boolean fillOneLeftChildView(boolean stopOnInvisible) {
        int top;
        int left;
        boolean itemInvisible;
        if (this.mLeftIndex < 0 || lastExpandableIndex() - firstExpandableIndex() <= 0) {
            return false;
        }
        int childIndex = firstExpandableIndex();
        int last = Math.min(lastExpandableIndex(), this.mItemsOnOffAxis + childIndex);
        int top2 = Integer.MAX_VALUE;
        int left2 = Integer.MAX_VALUE;
        for (int i = childIndex; i < last; i++) {
            View v = getChildAt(i);
            if (this.mOrientation == 0) {
                if (v.getLeft() < left2) {
                    left2 = v.getLeft();
                }
            } else if (v.getTop() < top2) {
                top2 = v.getTop();
            }
        }
        if (this.mOrientation == 0) {
            left = left2 - this.mSpace;
            itemInvisible = left - getScrollX() <= 0;
            top = getPaddingTop();
        } else {
            top = top2 - this.mSpace;
            itemInvisible = top - getScrollY() <= 0;
            left = getPaddingLeft();
        }
        if (!itemInvisible || !stopOnInvisible) {
            return fillOneAxis(left, top, false, true);
        }
        return false;
    }

    private View addAndMeasureExpandableView(int adapterIndex, int insertIndex) {
        int type = this.mAdapter.getItemViewType(adapterIndex);
        View child = this.mAdapter.getView(adapterIndex, this.mRecycleViews.getView(type), this);
        if (child == null) {
            return null;
        }
        child.setTag(R.id.ScrollAdapterViewChild, new ChildViewHolder(type));
        addViewInLayout(child, insertIndex, child.getLayoutParams(), true);
        measureChild(child);
        return child;
    }

    private void measureScrapChild(View child, int widthMeasureSpec, int heightMeasureSpec) {
        int lpHeight;
        int lpWidth;
        int childHeightSpec;
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        if (this.mOrientation == 1) {
            lpWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, p.width);
            int lpHeight2 = p.height;
            if (lpHeight2 > 0) {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight2, 1073741824);
            } else {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            lpHeight = childHeightSpec;
        } else {
            lpHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, p.height);
            int lpWidth2 = p.width;
            if (lpWidth2 > 0) {
                lpWidth = View.MeasureSpec.makeMeasureSpec(lpWidth2, 1073741824);
            } else {
                lpWidth = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
        }
        child.measure(lpWidth, lpHeight);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        if (this.mAdapter == null) {
            Log.e(TAG, "onMeasure: Adapter not available ");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        this.mScroll.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mScroll.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int clientWidthSize = (widthSize - getPaddingLeft()) - getPaddingRight();
        int clientHeightSize = (heightSize - getPaddingTop()) - getPaddingBottom();
        if (this.mMeasuredSpec == -1) {
            View scrapView = this.mAdapter.getScrapView(this);
            measureScrapChild(scrapView, 0, 0);
            this.mScrapWidth = scrapView.getMeasuredWidth();
            this.mScrapHeight = scrapView.getMeasuredHeight();
        }
        if (this.mGridSetting > 0) {
            i = this.mGridSetting;
        } else {
            if (this.mOrientation == 0) {
                if (heightMode != 0) {
                    i = clientHeightSize / this.mScrapHeight;
                }
            } else if (widthMode != 0) {
                i = clientWidthSize / this.mScrapWidth;
            }
            i = 1;
        }
        this.mItemsOnOffAxis = i;
        if (this.mItemsOnOffAxis == 0) {
            this.mItemsOnOffAxis = 1;
        }
        if (!(this.mLoadingState == null || this.mItemsOnOffAxis == this.mLoadingState.itemsOnOffAxis)) {
            this.mLoadingState = null;
        }
        if (widthMode == 0 || (widthMode == Integer.MIN_VALUE && this.mOrientation == 1)) {
            int size = (this.mOrientation == 1 ? (this.mScrapWidth * this.mItemsOnOffAxis) + (this.mSpace * (this.mItemsOnOffAxis - 1)) : this.mScrapWidth) + getPaddingLeft() + getPaddingRight();
            widthSize = widthMode == Integer.MIN_VALUE ? Math.min(size, widthSize) : size;
        }
        if (heightMode == 0 || (heightMode == Integer.MIN_VALUE && this.mOrientation == 0)) {
            int size2 = (this.mOrientation == 0 ? (this.mScrapHeight * this.mItemsOnOffAxis) + (this.mSpace * (this.mItemsOnOffAxis - 1)) : this.mScrapHeight) + getPaddingTop() + getPaddingBottom();
            heightSize = heightMode == Integer.MIN_VALUE ? Math.min(size2, heightSize) : size2;
        }
        this.mMeasuredSpec = this.mOrientation == 0 ? heightMeasureSpec : widthMeasureSpec;
        setMeasuredDimension(widthSize, heightSize);
        int scrollMin = this.mScroll.secondAxis().getPaddingLow();
        int scrollMax = (this.mOrientation == 0 ? heightSize : widthSize) - this.mScroll.secondAxis().getPaddingHigh();
        this.mScroll.secondAxis().updateScrollMin(scrollMin, scrollMin);
        this.mScroll.secondAxis().updateScrollMax(scrollMax, scrollMax);
        int size3 = this.mExpandedViews.size();
        for (int j = 0; j < size3; j++) {
            measureChild(this.mExpandedViews.get(j).expandedView);
        }
        for (int i2 = firstExpandableIndex(); i2 < lastExpandableIndex(); i2++) {
            View v = getChildAt(i2);
            if (v.isLayoutRequested()) {
                measureChild(v);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int childCount, int i) {
        int focusIndex;
        if (this.mSelectedIndex < 0) {
            focusIndex = -1;
        } else {
            focusIndex = expandableIndexFromAdapterIndex(this.mSelectedIndex);
        }
        if (focusIndex < 0 || i < focusIndex) {
            return i;
        }
        if (i < childCount - 1) {
            return ((focusIndex + childCount) - 1) - i;
        }
        return focusIndex;
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean fillOneAxis(int r18, int r19, boolean r20, boolean r21) {
        /*
            r17 = this;
            r0 = r17
            int r1 = r17.lastExpandableIndex()
            if (r20 == 0) goto L_0x0018
            int r2 = r0.mItemsOnOffAxis
            com.android.tv.settings.widget.ScrollAdapter r3 = r0.mAdapter
            int r3 = r3.getCount()
            int r4 = r0.mRightIndex
            int r3 = r3 - r4
            int r2 = java.lang.Math.min(r2, r3)
            goto L_0x001a
        L_0x0018:
            int r2 = r0.mItemsOnOffAxis
        L_0x001a:
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = r4
            r4 = r3
            r3 = r5
        L_0x0020:
            if (r3 >= r2) goto L_0x005a
            if (r20 == 0) goto L_0x002d
            int r7 = r0.mRightIndex
            int r7 = r7 + r3
            r8 = -1
            android.view.View r7 = r0.addAndMeasureExpandableView(r7, r8)
            goto L_0x0038
        L_0x002d:
            int r7 = r0.mLeftIndex
            int r7 = r7 - r3
            int r8 = r17.firstExpandableIndex()
            android.view.View r7 = r0.addAndMeasureExpandableView(r7, r8)
        L_0x0038:
            if (r7 != 0) goto L_0x003b
            return r5
        L_0x003b:
            int r8 = r0.mOrientation
            if (r8 != 0) goto L_0x0044
            int r8 = r7.getMeasuredWidth()
            goto L_0x0048
        L_0x0044:
            int r8 = r7.getMeasuredHeight()
        L_0x0048:
            int r4 = java.lang.Math.max(r4, r8)
            int r8 = r0.mLeftIndex
            int r8 = r8 - r3
            int r8 = r0.getSelectedItemSize(r8, r7)
            int r6 = java.lang.Math.max(r6, r8)
            int r3 = r3 + 1
            goto L_0x0020
        L_0x005a:
            if (r20 != 0) goto L_0x006c
            int r1 = r17.firstExpandableIndex()
            int r3 = r0.mOrientation
            if (r3 != 0) goto L_0x0067
            int r3 = r18 - r4
            goto L_0x006e
        L_0x0067:
            int r7 = r19 - r4
            r3 = r18
            goto L_0x0070
        L_0x006c:
            r3 = r18
        L_0x006e:
            r7 = r19
        L_0x0070:
            r8 = r7
            r7 = r3
            r3 = r5
        L_0x0073:
            r9 = 1
            if (r3 >= r2) goto L_0x0181
            int r10 = r1 + r3
            android.view.View r10 = r0.getChildAt(r10)
            r11 = 2131361804(0x7f0a000c, float:1.834337E38)
            java.lang.Object r11 = r10.getTag(r11)
            com.android.tv.settings.widget.ScrollAdapterView$ChildViewHolder r11 = (com.android.tv.settings.widget.ScrollAdapterView.ChildViewHolder) r11
            r11.mMaxSize = r4
            int r12 = r0.mOrientation
            if (r12 != 0) goto L_0x00db
            com.android.tv.settings.widget.ScrollController r12 = r0.mScroll
            int r12 = r12.getScrollItemAlign()
            switch(r12) {
                case 0: goto L_0x00b5;
                case 1: goto L_0x00a7;
                case 2: goto L_0x0095;
                default: goto L_0x0094;
            }
        L_0x0094:
            goto L_0x00d2
        L_0x0095:
            int r12 = r7 + r4
            int r13 = r10.getMeasuredWidth()
            int r12 = r12 - r13
            int r13 = r7 + r4
            int r14 = r10.getMeasuredHeight()
            int r14 = r14 + r8
            r10.layout(r12, r8, r13, r14)
            goto L_0x00d2
        L_0x00a7:
            int r12 = r10.getMeasuredWidth()
            int r12 = r12 + r7
            int r13 = r10.getMeasuredHeight()
            int r13 = r13 + r8
            r10.layout(r7, r8, r12, r13)
            goto L_0x00d2
        L_0x00b5:
            int r12 = r4 / 2
            int r12 = r12 + r7
            int r13 = r10.getMeasuredWidth()
            int r13 = r13 / 2
            int r12 = r12 - r13
            int r13 = r4 / 2
            int r13 = r13 + r7
            int r14 = r10.getMeasuredWidth()
            int r14 = r14 / 2
            int r13 = r13 + r14
            int r14 = r10.getMeasuredHeight()
            int r14 = r14 + r8
            r10.layout(r12, r8, r13, r14)
        L_0x00d2:
            int r12 = r10.getMeasuredHeight()
            int r8 = r8 + r12
            int r12 = r0.mSpace
            int r8 = r8 + r12
            goto L_0x012a
        L_0x00db:
            com.android.tv.settings.widget.ScrollController r12 = r0.mScroll
            int r12 = r12.getScrollItemAlign()
            switch(r12) {
                case 0: goto L_0x0105;
                case 1: goto L_0x00f7;
                case 2: goto L_0x00e5;
                default: goto L_0x00e4;
            }
        L_0x00e4:
            goto L_0x0122
        L_0x00e5:
            int r12 = r8 + r4
            int r13 = r10.getMeasuredHeight()
            int r12 = r12 - r13
            int r13 = r17.getMeasuredWidth()
            int r13 = r13 + r7
            int r14 = r8 + r4
            r10.layout(r7, r12, r13, r14)
            goto L_0x0122
        L_0x00f7:
            int r12 = r10.getMeasuredWidth()
            int r12 = r12 + r7
            int r13 = r10.getMeasuredHeight()
            int r13 = r13 + r8
            r10.layout(r7, r8, r12, r13)
            goto L_0x0122
        L_0x0105:
            int r12 = r4 / 2
            int r12 = r12 + r8
            int r13 = r10.getMeasuredHeight()
            int r13 = r13 / 2
            int r12 = r12 - r13
            int r13 = r10.getMeasuredWidth()
            int r13 = r13 + r7
            int r14 = r4 / 2
            int r14 = r14 + r8
            int r15 = r10.getMeasuredHeight()
            int r15 = r15 / 2
            int r14 = r14 + r15
            r10.layout(r7, r12, r13, r14)
        L_0x0122:
            int r12 = r10.getMeasuredWidth()
            int r7 = r7 + r12
            int r12 = r0.mSpace
            int r7 = r7 + r12
        L_0x012a:
            if (r20 == 0) goto L_0x0139
            com.android.tv.settings.widget.ScrollAdapterView$ExpandableChildStates r12 = r0.mExpandableChildStates
            int r13 = r0.mRightIndex
            r12.loadView(r10, r13)
            int r12 = r0.mRightIndex
            int r12 = r12 + r9
            r0.mRightIndex = r12
            goto L_0x0145
        L_0x0139:
            com.android.tv.settings.widget.ScrollAdapterView$ExpandableChildStates r12 = r0.mExpandableChildStates
            int r13 = r0.mLeftIndex
            r12.loadView(r10, r13)
            int r12 = r0.mLeftIndex
            int r12 = r12 - r9
            r0.mLeftIndex = r12
        L_0x0145:
            int r12 = r1 + r3
            int r12 = r0.computeScrollCenter(r12)
            r11.mScrollCenter = r12
            if (r21 == 0) goto L_0x017d
            if (r20 == 0) goto L_0x017d
            com.android.tv.settings.widget.ScrollAdapter r13 = r0.mAdapter
            int r14 = r0.mRightIndex
            int r14 = r14 - r9
            boolean r13 = r13.isEnabled(r14)
            if (r13 == 0) goto L_0x017d
            boolean r13 = r0.mMadeInitialSelection
            if (r13 != 0) goto L_0x017d
            int r13 = r0.getScrollCenter(r10)
            int r14 = r0.getCenterInOffAxis(r10)
            int r5 = r0.mOrientation
            if (r5 != 0) goto L_0x0172
            com.android.tv.settings.widget.ScrollController r5 = r0.mScroll
            r5.setScrollCenter(r13, r14)
            goto L_0x0177
        L_0x0172:
            com.android.tv.settings.widget.ScrollController r5 = r0.mScroll
            r5.setScrollCenter(r14, r13)
        L_0x0177:
            r0.mMadeInitialSelection = r9
            r5 = 0
            r0.transferFocusTo(r10, r5)
        L_0x017d:
            int r3 = r3 + 1
            goto L_0x0073
        L_0x0181:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.widget.ScrollAdapterView.fillOneAxis(int, int, boolean, boolean):boolean");
    }

    private boolean fillOneRightChildView(boolean stopOnInvisible) {
        boolean itemInvisible;
        if (this.mRightIndex >= this.mAdapter.getCount()) {
            return false;
        }
        int left = getPaddingLeft();
        int top = getPaddingTop();
        boolean checkedChild = false;
        if (lastExpandableIndex() - firstExpandableIndex() > 0) {
            int childIndex = lastExpandableIndex() - 1;
            int i = childIndex - (getAdapterIndex(childIndex) % this.mItemsOnOffAxis);
            while (true) {
                if (i >= lastExpandableIndex()) {
                    break;
                }
                View v = getChildAt(i);
                ExpandedView expandedView = findExpandedView(this.mExpandedViews, getAdapterIndex(i));
                if (expandedView == null) {
                    if (this.mOrientation == 0) {
                        if (!checkedChild) {
                            checkedChild = true;
                            left = v.getRight();
                        } else if (v.getRight() > left) {
                            left = v.getRight();
                        }
                    } else if (!checkedChild) {
                        checkedChild = true;
                        top = v.getBottom();
                    } else if (v.getBottom() > top) {
                        top = v.getBottom();
                    }
                    i++;
                } else if (this.mOrientation == 0) {
                    left = expandedView.expandedView.getRight();
                } else {
                    top = expandedView.expandedView.getBottom();
                }
            }
            if (this.mOrientation == 0) {
                left += this.mSpace;
                itemInvisible = left - getScrollX() >= getWidth();
                top = getPaddingTop();
            } else {
                top += this.mSpace;
                itemInvisible = top - getScrollY() >= getHeight();
                left = getPaddingLeft();
            }
            if (itemInvisible && stopOnInvisible) {
                return false;
            }
        }
        return fillOneAxis(left, top, true, true);
    }

    private int heuristicGetPersistentIndex() {
        int c = this.mAdapter.getCount();
        if (this.mScrollBeforeReset.id != Long.MIN_VALUE) {
            if (this.mScrollBeforeReset.index < c && this.mAdapter.getItemId(this.mScrollBeforeReset.index) == this.mScrollBeforeReset.id) {
                return this.mScrollBeforeReset.index;
            }
            for (int i = 1; i <= 30; i++) {
                int index = this.mScrollBeforeReset.index + i;
                if (index < c && this.mAdapter.getItemId(index) == this.mScrollBeforeReset.id) {
                    return index;
                }
                int index2 = this.mScrollBeforeReset.index - i;
                if (index2 >= 0 && index2 < c && this.mAdapter.getItemId(index2) == this.mScrollBeforeReset.id) {
                    return index2;
                }
            }
        }
        return this.mScrollBeforeReset.index >= c ? c - 1 : this.mScrollBeforeReset.index;
    }

    private void restoreLoadingState() {
        int selection;
        int top;
        int left;
        int finalLocation;
        int viewLoc = Integer.MIN_VALUE;
        float scrollPosition = 0.0f;
        if (this.mPendingSelection >= 0) {
            selection = this.mPendingSelection;
            scrollPosition = this.mPendingScrollPosition;
        } else if (this.mScrollBeforeReset.isValid()) {
            selection = heuristicGetPersistentIndex();
            viewLoc = this.mScrollBeforeReset.viewLocation;
        } else if (this.mLoadingState != null) {
            selection = this.mLoadingState.index;
        } else {
            return;
        }
        this.mPendingSelection = -1;
        this.mScrollBeforeReset.clear();
        this.mLoadingState = null;
        if (selection < 0 || selection >= this.mAdapter.getCount()) {
            Log.w(TAG, "invalid selection " + selection);
            return;
        }
        int startIndex = selection - (selection % this.mItemsOnOffAxis);
        if (this.mOrientation == 0) {
            left = viewLoc != Integer.MIN_VALUE ? viewLoc : this.mScroll.horizontal.getPaddingLow() + (this.mScrapWidth * (selection / this.mItemsOnOffAxis));
            top = this.mScroll.vertical.getPaddingLow();
        } else {
            left = this.mScroll.horizontal.getPaddingLow();
            top = viewLoc != Integer.MIN_VALUE ? viewLoc : this.mScroll.vertical.getPaddingLow() + (this.mScrapHeight * (selection / this.mItemsOnOffAxis));
        }
        this.mRightIndex = startIndex;
        this.mLeftIndex = this.mRightIndex - 1;
        fillOneAxis(left, top, true, false);
        this.mMadeInitialSelection = true;
        fillVisibleViewsInLayout();
        View child = getExpandableView(selection);
        if (child == null) {
            Log.w(TAG, "unable to restore selection view");
            return;
        }
        this.mExpandableChildStates.loadView(child, selection);
        if (viewLoc == Integer.MIN_VALUE || this.mScrollerState != 3) {
            setSelectionInternal(selection, scrollPosition, false);
            return;
        }
        if (this.mOrientation == 0) {
            finalLocation = this.mScroll.getFinalX();
        } else {
            finalLocation = this.mScroll.getFinalY();
        }
        this.mSelectedIndex = getAdapterIndex(indexOfChild(child));
        int scrollCenter = getScrollCenter(child);
        if (this.mScroll.mainAxis().getScrollCenter() <= finalLocation) {
            while (scrollCenter < finalLocation) {
                int nextAdapterIndex = this.mSelectedIndex + this.mItemsOnOffAxis;
                View nextView = getExpandableView(nextAdapterIndex);
                if (nextView == null) {
                    if (!fillOneRightChildView(false)) {
                        break;
                    }
                    nextView = getExpandableView(nextAdapterIndex);
                }
                int nextScrollCenter = getScrollCenter(nextView);
                if (nextScrollCenter > finalLocation) {
                    break;
                }
                this.mSelectedIndex = nextAdapterIndex;
                scrollCenter = nextScrollCenter;
            }
        } else {
            while (scrollCenter > finalLocation) {
                int nextAdapterIndex2 = this.mSelectedIndex - this.mItemsOnOffAxis;
                View nextView2 = getExpandableView(nextAdapterIndex2);
                if (nextView2 == null) {
                    if (!fillOneLeftChildView(false)) {
                        break;
                    }
                    nextView2 = getExpandableView(nextAdapterIndex2);
                }
                int nextScrollCenter2 = getScrollCenter(nextView2);
                if (nextScrollCenter2 < finalLocation) {
                    break;
                }
                this.mSelectedIndex = nextAdapterIndex2;
                scrollCenter = nextScrollCenter2;
            }
        }
        if (this.mOrientation == 0) {
            this.mScroll.setFinalX(scrollCenter);
        } else {
            this.mScroll.setFinalY(scrollCenter);
        }
    }

    private void measureChild(View child) {
        int childWidthSpec;
        int childHeightSpec;
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        if (this.mOrientation == 1) {
            int childWidthSpec2 = ViewGroup.getChildMeasureSpec(this.mMeasuredSpec, 0, p.width);
            int lpHeight = p.height;
            if (lpHeight > 0) {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
            } else {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            child.measure(childWidthSpec2, childHeightSpec);
            return;
        }
        int childHeightSpec2 = ViewGroup.getChildMeasureSpec(this.mMeasuredSpec, 0, p.height);
        int lpWidth = p.width;
        if (lpWidth > 0) {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, 1073741824);
        } else {
            childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec2);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || event.dispatch(this, (KeyEvent.DispatcherState) null, (Object) null);
    }

    /* access modifiers changed from: protected */
    public boolean internalKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 19:
                if (handleArrowKey(33, 0, false, false)) {
                    return true;
                }
                break;
            case 20:
                if (handleArrowKey(130, 0, false, false)) {
                    return true;
                }
                break;
            case 21:
                if (handleArrowKey(17, 0, false, false)) {
                    return true;
                }
                break;
            case 22:
                if (handleArrowKey(66, 0, false, false)) {
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return internalKeyDown(keyCode, event);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000f, code lost:
        r0 = findViewIndexContainingScrollCenter();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyUp(int r10, android.view.KeyEvent r11) {
        /*
            r9 = this;
            r0 = 23
            if (r10 == r0) goto L_0x0009
            r0 = 66
            if (r10 == r0) goto L_0x0009
            goto L_0x002f
        L_0x0009:
            android.widget.AdapterView$OnItemClickListener r0 = r9.getOnItemClickListener()
            if (r0 == 0) goto L_0x002f
            int r0 = r9.findViewIndexContainingScrollCenter()
            android.view.View r7 = r9.getChildAt(r0)
            if (r7 == 0) goto L_0x002f
            int r8 = r9.getAdapterIndex(r0)
            android.widget.AdapterView$OnItemClickListener r1 = r9.getOnItemClickListener()
            com.android.tv.settings.widget.ScrollAdapter r2 = r9.mAdapter
            long r5 = r2.getItemId(r8)
            r2 = r9
            r3 = r7
            r4 = r8
            r1.onItemClick(r2, r3, r4, r5)
            r1 = 1
            return r1
        L_0x002f:
            boolean r0 = super.onKeyUp(r10, r11)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.widget.ScrollAdapterView.onKeyUp(int, android.view.KeyEvent):boolean");
    }

    public boolean arrowScroll(int direction, int repeats) {
        return handleArrowKey(direction, repeats, true, false);
    }

    public boolean arrowScroll(int direction) {
        return arrowScroll(direction, 0);
    }

    public boolean isInScrolling() {
        return !this.mScroll.isFinished();
    }

    public boolean isInScrollingOrDragging() {
        return this.mScrollerState != 0;
    }

    public void setPlaySoundEffects(boolean playSoundEffects) {
        this.mPlaySoundEffects = playSoundEffects;
    }

    private static boolean isDirectionGrowing(int direction) {
        return direction == 66 || direction == 130;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isDescendant(android.view.View r3, android.view.View r4) {
        /*
        L_0x0000:
            r0 = 0
            if (r4 == 0) goto L_0x0014
            android.view.ViewParent r1 = r4.getParent()
            if (r1 != r3) goto L_0x000b
            r0 = 1
            return r0
        L_0x000b:
            boolean r2 = r1 instanceof android.view.View
            if (r2 != 0) goto L_0x0010
            return r0
        L_0x0010:
            r4 = r1
            android.view.View r4 = (android.view.View) r4
            goto L_0x0000
        L_0x0014:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.widget.ScrollAdapterView.isDescendant(android.view.View, android.view.View):boolean");
    }

    private boolean requestNextFocus(int direction, View focused, View newFocus) {
        focused.getFocusedRect(this.mTempRect);
        offsetDescendantRectToMyCoords(focused, this.mTempRect);
        offsetRectIntoDescendantCoords(newFocus, this.mTempRect);
        return newFocus.requestFocus(direction, this.mTempRect);
    }

    /* access modifiers changed from: protected */
    public boolean handleArrowKey(int direction, int repeats, boolean forceFindNextExpandable, boolean page) {
        int nextFocusAdapterIndex;
        View view;
        View nextFocused;
        View v;
        int i = direction;
        View currentTop = getFocusedChild();
        View currentExpandable = getExpandableChild(currentTop);
        View focused = findFocus();
        if (currentTop != currentExpandable || focused == null || forceFindNextExpandable || (v = focused.focusSearch(i)) == null || v == focused || !isDescendant(currentTop, v)) {
            boolean isGrowing = isDirectionGrowing(direction);
            boolean isOnOffAxis = false;
            if (i == 66 || i == 17) {
                isOnOffAxis = this.mOrientation == 1;
            } else if (i == 130 || i == 33) {
                isOnOffAxis = this.mOrientation == 0;
            }
            boolean isOnOffAxis2 = isOnOffAxis;
            if (currentTop != currentExpandable && !forceFindNextExpandable) {
                if (currentTop instanceof ViewGroup) {
                    nextFocused = FocusFinder.getInstance().findNextFocus((ViewGroup) currentTop, findFocus(), i);
                } else {
                    nextFocused = null;
                }
                if (getTopItem(nextFocused) == currentTop) {
                    return false;
                }
            }
            int currentExpandableIndex = expandableIndexFromAdapterIndex(this.mSelectedIndex);
            if (currentExpandableIndex < 0) {
                return false;
            }
            View nextTop = null;
            if (!isOnOffAxis2) {
                int adapterIndex = getAdapterIndex(currentExpandableIndex);
                int focusAdapterIndex = adapterIndex;
                int totalCount = repeats + 1;
                while (true) {
                    if (totalCount <= 0) {
                        break;
                    }
                    if (isGrowing) {
                        nextFocusAdapterIndex = this.mItemsOnOffAxis + focusAdapterIndex;
                    } else {
                        nextFocusAdapterIndex = focusAdapterIndex - this.mItemsOnOffAxis;
                    }
                    if ((!isGrowing || nextFocusAdapterIndex < this.mAdapter.getCount()) && (isGrowing || nextFocusAdapterIndex >= 0)) {
                        focusAdapterIndex = nextFocusAdapterIndex;
                        if (this.mAdapter.isEnabled(focusAdapterIndex)) {
                            totalCount--;
                        }
                    }
                }
                if (focusAdapterIndex == adapterIndex || !this.mAdapter.isEnabled(focusAdapterIndex)) {
                    if (hasFocus() && this.mNavigateOutAllowed && (view = getChildAt(expandableIndexFromAdapterIndex(focusAdapterIndex))) != null && !view.hasFocus()) {
                        view.requestFocus();
                    }
                    return !this.mNavigateOutAllowed;
                }
                if (isGrowing) {
                    while (true) {
                        if (focusAdapterIndex > getAdapterIndex(lastExpandableIndex() - 1)) {
                            if (!fillOneRightChildView(false)) {
                                break;
                            }
                        } else {
                            nextTop = getChildAt(expandableIndexFromAdapterIndex(focusAdapterIndex));
                            break;
                        }
                    }
                    if (nextTop == null) {
                        nextTop = getChildAt(lastExpandableIndex() - 1);
                    }
                } else {
                    while (true) {
                        if (focusAdapterIndex < getAdapterIndex(firstExpandableIndex())) {
                            if (!fillOneLeftChildView(false)) {
                                break;
                            }
                        } else {
                            nextTop = getChildAt(expandableIndexFromAdapterIndex(focusAdapterIndex));
                            break;
                        }
                    }
                    if (nextTop == null) {
                        nextTop = getChildAt(firstExpandableIndex());
                    }
                }
                if (nextTop == null) {
                    return true;
                }
            } else if (isGrowing && currentExpandableIndex + 1 < lastExpandableIndex() && getAdapterIndex(currentExpandableIndex) % this.mItemsOnOffAxis != this.mItemsOnOffAxis - 1) {
                nextTop = getChildAt(currentExpandableIndex + 1);
            } else if (isGrowing || currentExpandableIndex - 1 < firstExpandableIndex() || getAdapterIndex(currentExpandableIndex) % this.mItemsOnOffAxis == 0) {
                return !this.mNavigateOutOfOffAxisAllowed;
            } else {
                nextTop = getChildAt(currentExpandableIndex - 1);
            }
            scrollAndFocusTo(nextTop, i, false, 0, page);
            if (this.mPlaySoundEffects) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }
            return true;
        }
        requestNextFocus(i, focused, v);
        return true;
    }

    private void fireItemChange() {
        int childIndex = findViewIndexContainingScrollCenter();
        View topItem = getChildAt(childIndex);
        if (isFocused() && getDescendantFocusability() == 262144 && topItem != null) {
            topItem.requestFocus();
        }
        if (this.mOnItemChangeListeners != null && !this.mOnItemChangeListeners.isEmpty()) {
            if (topItem != null) {
                int adapterIndex = getAdapterIndex(childIndex);
                int scrollCenter = getScrollCenter(topItem);
                Iterator<OnItemChangeListener> it = this.mOnItemChangeListeners.iterator();
                while (it.hasNext()) {
                    it.next().onItemSelected(topItem, adapterIndex, scrollCenter - this.mScroll.mainAxis().getSystemScrollPos(scrollCenter));
                }
                this.mItemSelected = adapterIndex;
            } else if (this.mItemSelected != -1) {
                Iterator<OnItemChangeListener> it2 = this.mOnItemChangeListeners.iterator();
                while (it2.hasNext()) {
                    it2.next().onItemSelected((View) null, -1, 0);
                }
                this.mItemSelected = -1;
            }
        }
        sendAccessibilityEvent(4);
    }

    private void updateScrollInfo(ScrollInfo info) {
        int scrollCenter = this.mScroll.mainAxis().getScrollCenter();
        int scrollCenterOff = this.mScroll.secondAxis().getScrollCenter();
        int index = findViewIndexContainingScrollCenter(scrollCenter, scrollCenterOff, false);
        if (index < 0) {
            info.index = -1;
            return;
        }
        View view = getChildAt(index);
        int center = getScrollCenter(view);
        if (scrollCenter > center) {
            if (this.mItemsOnOffAxis + index < lastExpandableIndex()) {
                info.mainPos = ((float) (scrollCenter - center)) / ((float) (getScrollCenter(getChildAt(this.mItemsOnOffAxis + index)) - center));
            } else {
                info.mainPos = ((float) (scrollCenter - center)) / ((float) getSize(view));
            }
        } else if (scrollCenter == center) {
            info.mainPos = 0.0f;
        } else if (index - this.mItemsOnOffAxis >= firstExpandableIndex()) {
            index -= this.mItemsOnOffAxis;
            view = getChildAt(index);
            int previousCenter = getScrollCenter(view);
            info.mainPos = ((float) (scrollCenter - previousCenter)) / ((float) (center - previousCenter));
        } else {
            info.mainPos = ((float) (scrollCenter - center)) / ((float) getSize(view));
        }
        int centerOffAxis = getCenterInOffAxis(view);
        if (scrollCenterOff > centerOffAxis) {
            if (index + 1 < lastExpandableIndex()) {
                info.secondPos = ((float) (scrollCenterOff - centerOffAxis)) / ((float) (getCenterInOffAxis(getChildAt(index + 1)) - centerOffAxis));
            } else {
                info.secondPos = ((float) (scrollCenterOff - centerOffAxis)) / ((float) getSizeInOffAxis(view));
            }
        } else if (scrollCenterOff == centerOffAxis) {
            info.secondPos = 0.0f;
        } else if (index - 1 >= firstExpandableIndex()) {
            index--;
            view = getChildAt(index);
            int previousCenter2 = getCenterInOffAxis(view);
            info.secondPos = ((float) (scrollCenterOff - previousCenter2)) / ((float) (centerOffAxis - previousCenter2));
        } else {
            info.secondPos = ((float) (scrollCenterOff - centerOffAxis)) / ((float) getSizeInOffAxis(view));
        }
        info.index = getAdapterIndex(index);
        info.viewLocation = this.mOrientation == 0 ? view.getLeft() : view.getTop();
        if (this.mAdapter.hasStableIds()) {
            info.id = this.mAdapter.getItemId(info.index);
        }
    }

    private void fireScrollChange() {
        int savedIndex = this.mCurScroll.index;
        float savedMainPos = this.mCurScroll.mainPos;
        float savedSecondPos = this.mCurScroll.secondPos;
        updateScrollInfo(this.mCurScroll);
        if (this.mOnScrollListeners != null && !this.mOnScrollListeners.isEmpty()) {
            if (!(savedIndex == this.mCurScroll.index && savedMainPos == this.mCurScroll.mainPos && savedSecondPos == this.mCurScroll.secondPos) && this.mCurScroll.index >= 0) {
                Iterator<OnScrollListener> it = this.mOnScrollListeners.iterator();
                while (it.hasNext()) {
                    it.next().onScrolled(getChildAt(expandableIndexFromAdapterIndex(this.mCurScroll.index)), this.mCurScroll.index, this.mCurScroll.mainPos, this.mCurScroll.secondPos);
                }
            }
        }
    }

    private void fireItemSelected() {
        AdapterView.OnItemSelectedListener listener = getOnItemSelectedListener();
        if (listener != null) {
            listener.onItemSelected(this, getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
        }
        sendAccessibilityEvent(4);
    }

    private void setSelectionInternal(int adapterIndex, float scrollPosition, boolean fireEvent) {
        int nextCenter;
        if (adapterIndex < 0 || this.mAdapter == null || adapterIndex >= this.mAdapter.getCount() || !this.mAdapter.isEnabled(adapterIndex)) {
            Log.w(TAG, "invalid selection index = " + adapterIndex);
            return;
        }
        int viewIndex = expandableIndexFromAdapterIndex(adapterIndex);
        if (this.mDataSetChangedFlag || viewIndex < firstExpandableIndex() || viewIndex >= lastExpandableIndex()) {
            this.mPendingSelection = adapterIndex;
            this.mPendingScrollPosition = scrollPosition;
            fireDataSetChanged();
            return;
        }
        View view = getChildAt(viewIndex);
        int scrollCenter = getScrollCenter(view);
        int scrollCenterOffAxis = getCenterInOffAxis(view);
        if (scrollPosition <= 0.0f || this.mItemsOnOffAxis + viewIndex >= lastExpandableIndex()) {
            nextCenter = (int) (((float) getSize(view)) * scrollPosition);
        } else {
            nextCenter = (int) (((float) (getScrollCenter(getChildAt(this.mItemsOnOffAxis + viewIndex)) - scrollCenter)) * scrollPosition);
        }
        if (this.mOrientation == 0) {
            this.mScroll.setScrollCenter(scrollCenter + nextCenter, scrollCenterOffAxis);
        } else {
            this.mScroll.setScrollCenter(scrollCenterOffAxis, scrollCenter + nextCenter);
        }
        transferFocusTo(view, 0);
        adjustSystemScrollPos();
        applyTransformations();
        if (fireEvent) {
            updateViewsLocations(false);
            fireScrollChange();
            if (scrollPosition == 0.0f) {
                fireItemChange();
            }
        }
    }

    private void transferFocusTo(View topItem, int direction) {
        if (topItem != getSelectedView()) {
            this.mSelectedIndex = getAdapterIndex(indexOfChild(topItem));
            View focused = findFocus();
            if (focused != null) {
                if (direction != 0) {
                    requestNextFocus(direction, focused, topItem);
                } else {
                    topItem.requestFocus();
                }
            }
            fireItemSelected();
        }
    }

    public void scrollAndFocusTo(View topItem, int direction, boolean easeFling, int duration, boolean page) {
        if (topItem == null) {
            this.mScrollerState = 0;
            return;
        }
        int delta = getScrollCenter(topItem) - this.mScroll.mainAxis().getScrollCenter();
        int deltaOffAxis = this.mItemsOnOffAxis == 1 ? 0 : getCenterInOffAxis(topItem) - this.mScroll.secondAxis().getScrollCenter();
        if (delta == 0 && deltaOffAxis == 0) {
            this.mScrollerState = 0;
        } else {
            this.mScrollerState = 3;
            this.mScroll.startScrollByMain(delta, deltaOffAxis, easeFling, duration, page);
        }
        transferFocusTo(topItem, direction);
        scheduleScrollTask();
    }

    public int getScrollCenterStrategy() {
        return this.mScroll.mainAxis().getScrollCenterStrategy();
    }

    public void setScrollCenterStrategy(int scrollCenterStrategy) {
        this.mScroll.mainAxis().setScrollCenterStrategy(scrollCenterStrategy);
    }

    public int getScrollCenterOffset() {
        return this.mScroll.mainAxis().getScrollCenterOffset();
    }

    public void setScrollCenterOffset(int scrollCenterOffset) {
        this.mScroll.mainAxis().setScrollCenterOffset(scrollCenterOffset);
    }

    public void setScrollCenterOffsetPercent(int scrollCenterOffsetPercent) {
        this.mScroll.mainAxis().setScrollCenterOffsetPercent(scrollCenterOffsetPercent);
    }

    public void setItemTransform(ScrollAdapterTransform transform) {
        this.mItemTransform = transform;
    }

    public ScrollAdapterTransform getItemTransform() {
        return this.mItemTransform;
    }

    private void ensureSimpleItemTransform() {
        if (!(this.mItemTransform instanceof SimpleScrollAdapterTransform)) {
            this.mItemTransform = new SimpleScrollAdapterTransform(getContext());
        }
    }

    public void setLowItemTransform(Animator anim) {
        ensureSimpleItemTransform();
        ((SimpleScrollAdapterTransform) this.mItemTransform).setLowItemTransform(anim);
    }

    public void setHighItemTransform(Animator anim) {
        ensureSimpleItemTransform();
        ((SimpleScrollAdapterTransform) this.mItemTransform).setHighItemTransform(anim);
    }

    /* access modifiers changed from: protected */
    public float getRightFadingEdgeStrength() {
        if (this.mOrientation != 0 || this.mAdapter == null || getChildCount() == 0) {
            return 0.0f;
        }
        if (this.mRightIndex != this.mAdapter.getCount() || getScrollX() + getWidth() < getChildAt(lastExpandableIndex() - 1).getRight()) {
            return 1.0f;
        }
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        if (this.mOrientation != 0 || this.mAdapter == null || getChildCount() == 0) {
            return 0.0f;
        }
        if (this.mRightIndex != this.mAdapter.getCount() || getScrollY() + getHeight() < getChildAt(lastExpandableIndex() - 1).getBottom()) {
            return 1.0f;
        }
        return 0.0f;
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View getTopItem(android.view.View r4) {
        /*
            r3 = this;
            r0 = r3
            r1 = r4
        L_0x0002:
            if (r1 == 0) goto L_0x001b
            android.view.ViewParent r2 = r1.getParent()
            if (r2 == r0) goto L_0x001b
            android.view.ViewParent r2 = r1.getParent()
            boolean r2 = r2 instanceof android.view.View
            if (r2 != 0) goto L_0x0013
            goto L_0x001b
        L_0x0013:
            android.view.ViewParent r2 = r1.getParent()
            r1 = r2
            android.view.View r1 = (android.view.View) r1
            goto L_0x0002
        L_0x001b:
            if (r1 != 0) goto L_0x001e
            return r4
        L_0x001e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.widget.ScrollAdapterView.getTopItem(android.view.View):android.view.View");
    }

    private int getCenter(View v) {
        if (this.mOrientation == 0) {
            return (v.getLeft() + v.getRight()) / 2;
        }
        return (v.getTop() + v.getBottom()) / 2;
    }

    private int getCenterInOffAxis(View v) {
        if (this.mOrientation == 1) {
            return (v.getLeft() + v.getRight()) / 2;
        }
        return (v.getTop() + v.getBottom()) / 2;
    }

    private int getSize(View v) {
        return ((ChildViewHolder) v.getTag(R.id.ScrollAdapterViewChild)).mMaxSize;
    }

    private int getSizeInOffAxis(View v) {
        return this.mOrientation == 0 ? v.getHeight() : v.getWidth();
    }

    public View getExpandableView(int adapterIndex) {
        return getChildAt(expandableIndexFromAdapterIndex(adapterIndex));
    }

    public int firstExpandableIndex() {
        return this.mExpandedViews.size();
    }

    public int lastExpandableIndex() {
        return getChildCount();
    }

    /* access modifiers changed from: private */
    public int getAdapterIndex(int expandableViewIndex) {
        return (expandableViewIndex - firstExpandableIndex()) + this.mLeftIndex + 1;
    }

    private int expandableIndexFromAdapterIndex(int index) {
        return ((firstExpandableIndex() + index) - this.mLeftIndex) - 1;
    }

    /* access modifiers changed from: package-private */
    public View getExpandableChild(View view) {
        if (view != null) {
            int size = this.mExpandedViews.size();
            for (int i = 0; i < size; i++) {
                ExpandedView v = this.mExpandedViews.get(i);
                if (v.expandedView == view) {
                    return getChildAt(expandableIndexFromAdapterIndex(v.index));
                }
            }
        }
        return view;
    }

    private static ExpandedView findExpandedView(ArrayList<ExpandedView> expandedView, int index) {
        int expandedCount = expandedView.size();
        for (int i = 0; i < expandedCount; i++) {
            ExpandedView v = expandedView.get(i);
            if (v.index == index) {
                return v;
            }
        }
        return null;
    }

    private ExpandedView getOrCreateExpandedView(int index) {
        if (this.mExpandAdapter == null || index < 0) {
            return null;
        }
        ExpandedView ret = findExpandedView(this.mExpandedViews, index);
        if (ret != null) {
            return ret;
        }
        int type = this.mExpandAdapter.getItemViewType(index);
        View v = this.mExpandAdapter.getView(index, this.mRecycleExpandedViews.getView(type), this);
        if (v == null) {
            return null;
        }
        addViewInLayout(v, 0, v.getLayoutParams(), true);
        this.mExpandedChildStates.loadView(v, index);
        measureChild(v);
        ExpandedView view = new ExpandedView(v, index, type);
        int size = this.mExpandedViews.size();
        for (int i = 0; i < size; i++) {
            if (view.index < this.mExpandedViews.get(i).index) {
                this.mExpandedViews.add(i, view);
                return view;
            }
        }
        this.mExpandedViews.add(view);
        return view;
    }

    public void setAnimateLayoutChange(boolean animateLayoutChange) {
        this.mAnimateLayoutChange = animateLayoutChange;
    }

    public boolean getAnimateLayoutChange() {
        return this.mAnimateLayoutChange;
    }

    /* JADX WARNING: Removed duplicated region for block: B:181:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x044f  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x045e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateViewsLocations(boolean r59) {
        /*
            r58 = this;
            r0 = r58
            int r1 = r58.lastExpandableIndex()
            com.android.tv.settings.widget.ScrollAdapterBase r2 = r0.mExpandAdapter
            if (r2 != 0) goto L_0x0014
            boolean r2 = r58.selectedItemCanScale()
            if (r2 != 0) goto L_0x0014
            com.android.tv.settings.widget.ScrollAdapterCustomAlign r2 = r0.mAdapterCustomAlign
            if (r2 == 0) goto L_0x0016
        L_0x0014:
            if (r1 != 0) goto L_0x001f
        L_0x0016:
            if (r59 == 0) goto L_0x001e
            int r3 = r58.getChildCount()
            if (r3 != 0) goto L_0x001f
        L_0x001e:
            return
        L_0x001f:
            com.android.tv.settings.widget.ScrollController r3 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r3 = r3.mainAxis()
            int r3 = r3.getScrollCenter()
            com.android.tv.settings.widget.ScrollController r4 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r4 = r4.secondAxis()
            int r4 = r4.getScrollCenter()
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r5 = r0.mExpandedViews
            int r5 = r5.size()
            r6 = -1
            r7 = -1
            r8 = -1
            int r9 = r58.firstExpandableIndex()
            r10 = 0
            r11 = r10
            r10 = r6
            r6 = r9
        L_0x0044:
            if (r6 >= r1) goto L_0x008c
            android.view.View r13 = r0.getChildAt(r6)
            int r14 = r0.getScrollCenter(r13)
            int r15 = r0.getCenterInOffAxis(r13)
            int r12 = r0.mOrientation
            if (r12 != 0) goto L_0x005b
            int r12 = r13.getHeight()
            goto L_0x005f
        L_0x005b:
            int r12 = r13.getWidth()
        L_0x005f:
            if (r14 > r3) goto L_0x0085
            int r2 = r0.mItemsOnOffAxis
            r17 = r3
            r3 = 1
            if (r2 == r3) goto L_0x006e
            boolean r2 = r0.hasScrollPositionSecondAxis(r4, r12, r15)
            if (r2 == 0) goto L_0x0087
        L_0x006e:
            r2 = r6
            r3 = r14
            com.android.tv.settings.widget.ScrollAdapterCustomAlign r8 = r0.mAdapterCustomAlign
            if (r8 == 0) goto L_0x0082
            com.android.tv.settings.widget.ScrollAdapterCustomAlign r8 = r0.mAdapterCustomAlign
            int r10 = r0.getAdapterIndex(r6)
            int r8 = r8.getItemAlignmentExtraOffset(r10, r13)
            r10 = r3
            r11 = r8
            r8 = r2
            goto L_0x0087
        L_0x0082:
            r8 = r2
            r10 = r3
            goto L_0x0087
        L_0x0085:
            r17 = r3
        L_0x0087:
            int r6 = r6 + 1
            r3 = r17
            goto L_0x0044
        L_0x008c:
            r17 = r3
            r2 = -1
            if (r8 != r2) goto L_0x0092
            return
        L_0x0092:
            int r3 = r0.mItemsOnOffAxis
            int r3 = r3 + r8
            r6 = 0
            if (r3 >= r1) goto L_0x00af
            android.view.View r12 = r0.getChildAt(r3)
            int r7 = r0.getScrollCenter(r12)
            com.android.tv.settings.widget.ScrollAdapterCustomAlign r13 = r0.mAdapterCustomAlign
            if (r13 == 0) goto L_0x00ae
            com.android.tv.settings.widget.ScrollAdapterCustomAlign r13 = r0.mAdapterCustomAlign
            int r14 = r0.getAdapterIndex(r3)
            int r6 = r13.getItemAlignmentExtraOffset(r14, r12)
        L_0x00ae:
            goto L_0x00b0
        L_0x00af:
            r3 = -1
        L_0x00b0:
            int r12 = r0.mItemsOnOffAxis
            int r12 = r8 - r12
            if (r12 >= r9) goto L_0x00b7
            r12 = -1
        L_0x00b7:
            int r13 = r0.getAdapterIndex(r8)
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r14 = r0.getOrCreateExpandedView(r13)
            r15 = 0
            if (r3 == r2) goto L_0x00c9
            int r2 = r0.mItemsOnOffAxis
            int r2 = r2 + r13
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r15 = r0.getOrCreateExpandedView(r2)
        L_0x00c9:
            r2 = 0
            r19 = r1
            r1 = -1
            if (r12 == r1) goto L_0x00d7
            int r1 = r0.mItemsOnOffAxis
            int r1 = r13 - r1
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r2 = r0.getOrCreateExpandedView(r1)
        L_0x00d7:
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r1 = r0.mExpandedViews
            int r1 = r1.size()
            int r1 = r1 - r5
            int r8 = r8 + r1
            r20 = r4
            r4 = -1
            if (r3 == r4) goto L_0x00e5
            int r3 = r3 + r1
        L_0x00e5:
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r4 = r0.mExpandedViews
            int r4 = r4.size()
            int r5 = r58.lastExpandableIndex()
            r18 = 0
            r19 = 0
            r21 = 1065353216(0x3f800000, float:1.0)
            r22 = r1
            int r1 = r5 + -1
            r23 = r4
            if (r8 >= r1) goto L_0x0155
            com.android.tv.settings.widget.ScrollController r1 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r1 = r1.mainAxis()
            int r1 = r1.getScrollCenter()
            int r1 = r7 - r1
            float r1 = (float) r1
            int r4 = r7 - r10
            float r4 = (float) r4
            float r1 = r1 / r4
            if (r14 == 0) goto L_0x012c
            int r4 = r0.mOrientation
            if (r4 != 0) goto L_0x011b
            android.view.View r4 = r14.expandedView
            int r4 = r4.getMeasuredWidth()
            goto L_0x0121
        L_0x011b:
            android.view.View r4 = r14.expandedView
            int r4 = r4.getMeasuredHeight()
        L_0x0121:
            r24 = r7
            float r7 = (float) r4
            float r7 = r7 * r1
            int r4 = (int) r7
            r14.setProgress(r1)
            r18 = r4
            goto L_0x012e
        L_0x012c:
            r24 = r7
        L_0x012e:
            if (r15 == 0) goto L_0x0173
            int r4 = r0.mOrientation
            if (r4 != 0) goto L_0x013b
            android.view.View r4 = r15.expandedView
            int r4 = r4.getMeasuredWidth()
            goto L_0x0141
        L_0x013b:
            android.view.View r4 = r15.expandedView
            int r4 = r4.getMeasuredHeight()
        L_0x0141:
            r7 = 1065353216(0x3f800000, float:1.0)
            float r19 = r7 - r1
            float r7 = (float) r4
            float r7 = r7 * r19
            int r4 = (int) r7
            r26 = r4
            r7 = 1065353216(0x3f800000, float:1.0)
            float r4 = r7 - r1
            r15.setProgress(r4)
            r19 = r26
            goto L_0x0173
        L_0x0155:
            r24 = r7
            if (r14 == 0) goto L_0x0171
            int r1 = r0.mOrientation
            if (r1 != 0) goto L_0x0164
            android.view.View r1 = r14.expandedView
            int r1 = r1.getMeasuredWidth()
            goto L_0x016a
        L_0x0164:
            android.view.View r1 = r14.expandedView
            int r1 = r1.getMeasuredHeight()
        L_0x016a:
            r18 = r1
            r1 = 1065353216(0x3f800000, float:1.0)
            r14.setProgress(r1)
        L_0x0171:
            r1 = r21
        L_0x0173:
            int r4 = r18 + r19
            r7 = 0
            r21 = 0
            r25 = 2147483647(0x7fffffff, float:NaN)
            r26 = 0
            r27 = 0
            int r28 = r58.firstExpandableIndex()
            int r28 = r5 - r28
            r29 = r4
            int r4 = r0.mItemsOnOffAxis
            int r28 = r28 + r4
            r4 = 1
            int r28 = r28 + -1
            int r4 = r0.mItemsOnOffAxis
            int r4 = r28 / r4
            r30 = r7
            boolean r7 = r0.mAnimateLayoutChange
            r31 = r9
            if (r7 == 0) goto L_0x01a8
            com.android.tv.settings.widget.ScrollController r7 = r0.mScroll
            boolean r7 = r7.isFinished()
            if (r7 == 0) goto L_0x01a8
            com.android.tv.settings.widget.ScrollAdapterBase r7 = r0.mExpandAdapter
            if (r7 == 0) goto L_0x01a8
            r7 = 1
            goto L_0x01a9
        L_0x01a8:
            r7 = 0
        L_0x01a9:
            r34 = r21
            r32 = r29
            r33 = r30
            r21 = 0
        L_0x01b1:
            r35 = r21
            r9 = r35
            if (r9 >= r4) goto L_0x048d
            int r21 = r58.firstExpandableIndex()
            r36 = r4
            int r4 = r0.mItemsOnOffAxis
            int r35 = r9 * r4
            int r4 = r21 + r35
            r37 = r10
            int r10 = r0.mItemsOnOffAxis
            int r10 = r10 + r4
            r16 = 1
            int r10 = r10 + -1
            if (r10 < r5) goto L_0x01d0
            int r10 = r5 + -1
        L_0x01d0:
            r21 = 0
            r38 = r5
            r39 = r12
            r12 = r21
            r5 = r25
            r21 = r4
        L_0x01dc:
            r40 = r21
            r41 = r13
            r13 = r40
            if (r13 > r10) goto L_0x0264
            r42 = r2
            android.view.View r2 = r0.getChildAt(r13)
            r43 = r15
            r15 = 2131361804(0x7f0a000c, float:1.834337E38)
            java.lang.Object r15 = r2.getTag(r15)
            com.android.tv.settings.widget.ScrollAdapterView$ChildViewHolder r15 = (com.android.tv.settings.widget.ScrollAdapterView.ChildViewHolder) r15
            if (r7 == 0) goto L_0x0221
            r44 = r14
            int r14 = r0.mOrientation
            if (r14 != 0) goto L_0x020f
            int r14 = r2.getLeft()
            float r14 = (float) r14
            r15.mLocation = r14
            float r14 = r15.mLocation
            float r21 = r2.getTranslationX()
            float r14 = r14 + r21
            r15.mLocationInParent = r14
            goto L_0x0223
        L_0x020f:
            int r14 = r2.getTop()
            float r14 = (float) r14
            r15.mLocation = r14
            float r14 = r15.mLocation
            float r21 = r2.getTranslationY()
            float r14 = r14 + r21
            r15.mLocationInParent = r14
            goto L_0x0223
        L_0x0221:
            r44 = r14
        L_0x0223:
            int r14 = r0.mOrientation
            if (r14 != 0) goto L_0x022c
            int r14 = r2.getMeasuredWidth()
            goto L_0x0230
        L_0x022c:
            int r14 = r2.getMeasuredHeight()
        L_0x0230:
            int r12 = java.lang.Math.max(r12, r14)
            if (r9 != 0) goto L_0x0258
            int r14 = r0.mOrientation
            if (r14 != 0) goto L_0x023f
            int r14 = r2.getLeft()
            goto L_0x0243
        L_0x023f:
            int r14 = r2.getTop()
        L_0x0243:
            r45 = r2
            com.android.tv.settings.widget.ScrollController r2 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r2 = r2.mainAxis()
            boolean r2 = r2.getSelectedTakesMoreSpace()
            if (r2 == 0) goto L_0x0254
            int r2 = r15.mExtraSpaceLow
            int r14 = r14 - r2
        L_0x0254:
            if (r14 >= r5) goto L_0x0258
            r2 = r14
            r5 = r2
        L_0x0258:
            int r21 = r13 + 1
            r13 = r41
            r2 = r42
            r15 = r43
            r14 = r44
            goto L_0x01dc
        L_0x0264:
            r42 = r2
            r44 = r14
            r43 = r15
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            int r13 = r5 + r12
            r14 = r2
            r2 = r4
        L_0x0270:
            if (r2 > r10) goto L_0x032b
            android.view.View r15 = r0.getChildAt(r2)
            r21 = r5
            r46 = r5
            int r5 = r0.mOrientation
            if (r5 != 0) goto L_0x0283
            int r5 = r15.getMeasuredWidth()
            goto L_0x0287
        L_0x0283:
            int r5 = r15.getMeasuredHeight()
        L_0x0287:
            r47 = r6
            com.android.tv.settings.widget.ScrollController r6 = r0.mScroll
            int r6 = r6.getScrollItemAlign()
            if (r6 == 0) goto L_0x029e
            r48 = r11
            r11 = 2
            if (r6 == r11) goto L_0x0299
        L_0x0296:
            r6 = r21
            goto L_0x02a8
        L_0x0299:
            int r6 = r12 - r5
            int r21 = r21 + r6
            goto L_0x0296
        L_0x029e:
            r48 = r11
            int r6 = r12 / 2
            int r11 = r5 / 2
            int r6 = r6 - r11
            int r21 = r21 + r6
            goto L_0x0296
        L_0x02a8:
            int r11 = r0.mOrientation
            if (r11 != 0) goto L_0x02df
            boolean r11 = r15.isLayoutRequested()
            if (r11 == 0) goto L_0x02d1
            r0.measureChild(r15)
            int r11 = r15.getTop()
            int r21 = r15.getMeasuredWidth()
            r49 = r5
            int r5 = r6 + r21
            int r21 = r15.getTop()
            int r25 = r15.getMeasuredHeight()
            r50 = r9
            int r9 = r21 + r25
            r15.layout(r6, r11, r5, r9)
            goto L_0x030b
        L_0x02d1:
            r49 = r5
            r50 = r9
            int r5 = r15.getLeft()
            int r5 = r6 - r5
            r15.offsetLeftAndRight(r5)
            goto L_0x030b
        L_0x02df:
            r49 = r5
            r50 = r9
            boolean r5 = r15.isLayoutRequested()
            if (r5 == 0) goto L_0x0302
            r0.measureChild(r15)
            int r5 = r15.getLeft()
            int r9 = r15.getLeft()
            int r11 = r15.getMeasuredWidth()
            int r9 = r9 + r11
            int r11 = r15.getMeasuredHeight()
            int r11 = r11 + r6
            r15.layout(r5, r6, r9, r11)
            goto L_0x030b
        L_0x0302:
            int r5 = r15.getTop()
            int r5 = r6 - r5
            r15.offsetTopAndBottom(r5)
        L_0x030b:
            boolean r5 = r58.selectedItemCanScale()
            if (r5 == 0) goto L_0x031f
            int r5 = r0.getAdapterIndex(r2)
            int r5 = r0.getSelectedItemSize(r5, r15)
            int r5 = java.lang.Math.max(r14, r5)
            r14 = r5
        L_0x031f:
            int r2 = r2 + 1
            r5 = r46
            r6 = r47
            r11 = r48
            r9 = r50
            goto L_0x0270
        L_0x032b:
            r46 = r5
            r47 = r6
            r50 = r9
            r48 = r11
            r2 = r4
        L_0x0334:
            if (r2 > r10) goto L_0x0352
            android.view.View r5 = r0.getChildAt(r2)
            r6 = 2131361804(0x7f0a000c, float:1.834337E38)
            java.lang.Object r9 = r5.getTag(r6)
            r6 = r9
            com.android.tv.settings.widget.ScrollAdapterView$ChildViewHolder r6 = (com.android.tv.settings.widget.ScrollAdapterView.ChildViewHolder) r6
            r6.mMaxSize = r12
            r9 = 0
            r6.mExtraSpaceLow = r9
            int r9 = r0.computeScrollCenter(r2)
            r6.mScrollCenter = r9
            int r2 = r2 + 1
            goto L_0x0334
        L_0x0352:
            if (r4 > r8) goto L_0x0359
            if (r8 > r10) goto L_0x0359
            r2 = r16
            goto L_0x035a
        L_0x0359:
            r2 = 0
        L_0x035a:
            if (r4 > r3) goto L_0x0361
            if (r3 > r10) goto L_0x0361
            r5 = r16
            goto L_0x0362
        L_0x0361:
            r5 = 0
        L_0x0362:
            r6 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r14 == r6) goto L_0x03e4
            r6 = 0
            if (r2 == 0) goto L_0x036f
            int r9 = r14 - r12
            float r9 = (float) r9
            float r9 = r9 * r1
            int r6 = (int) r9
            goto L_0x037a
        L_0x036f:
            if (r5 == 0) goto L_0x037a
            int r9 = r14 - r12
            float r9 = (float) r9
            r11 = 1065353216(0x3f800000, float:1.0)
            float r15 = r11 - r1
            float r9 = r9 * r15
            int r6 = (int) r9
        L_0x037a:
            if (r6 <= 0) goto L_0x03e4
            com.android.tv.settings.widget.ScrollController r9 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r9 = r9.mainAxis()
            boolean r9 = r9.getSelectedTakesMoreSpace()
            if (r9 == 0) goto L_0x039f
            int r13 = r13 + r6
            r9 = r32
            int r32 = r9 + r6
            com.android.tv.settings.widget.ScrollController r9 = r0.mScroll
            int r9 = r9.getScrollItemAlign()
            if (r9 == 0) goto L_0x039c
            r11 = 2
            if (r9 == r11) goto L_0x039a
            r9 = 0
            goto L_0x03a4
        L_0x039a:
            r9 = r6
            goto L_0x03a4
        L_0x039c:
            int r9 = r6 / 2
            goto L_0x03a4
        L_0x039f:
            r9 = r32
            int r11 = r6 / 2
            r9 = r11
        L_0x03a4:
            r11 = r33
            int r33 = r11 + r9
            int r11 = r6 - r9
            r15 = r34
            int r34 = r15 + r11
            r11 = r4
        L_0x03af:
            if (r11 > r10) goto L_0x03e1
            android.view.View r15 = r0.getChildAt(r11)
            r51 = r3
            com.android.tv.settings.widget.ScrollController r3 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r3 = r3.mainAxis()
            boolean r3 = r3.getSelectedTakesMoreSpace()
            if (r3 == 0) goto L_0x03dc
            int r3 = r0.mOrientation
            if (r3 != 0) goto L_0x03cb
            r15.offsetLeftAndRight(r9)
            goto L_0x03ce
        L_0x03cb:
            r15.offsetTopAndBottom(r9)
        L_0x03ce:
            r3 = 2131361804(0x7f0a000c, float:1.834337E38)
            java.lang.Object r21 = r15.getTag(r3)
            r3 = r21
            com.android.tv.settings.widget.ScrollAdapterView$ChildViewHolder r3 = (com.android.tv.settings.widget.ScrollAdapterView.ChildViewHolder) r3
            r3.mExtraSpaceLow = r9
        L_0x03dc:
            int r11 = r11 + 1
            r3 = r51
            goto L_0x03af
        L_0x03e1:
            r51 = r3
            goto L_0x03f2
        L_0x03e4:
            r51 = r3
            r9 = r32
            r11 = r33
            r15 = r34
            r32 = r9
            r33 = r11
            r34 = r15
        L_0x03f2:
            if (r7 == 0) goto L_0x044b
            r3 = r4
        L_0x03f5:
            if (r3 > r10) goto L_0x044b
            android.view.View r6 = r0.getChildAt(r3)
            r9 = 2131361804(0x7f0a000c, float:1.834337E38)
            java.lang.Object r11 = r6.getTag(r9)
            com.android.tv.settings.widget.ScrollAdapterView$ChildViewHolder r11 = (com.android.tv.settings.widget.ScrollAdapterView.ChildViewHolder) r11
            int r15 = r0.mOrientation
            if (r15 != 0) goto L_0x040e
            int r15 = r6.getLeft()
        L_0x040c:
            float r15 = (float) r15
            goto L_0x0413
        L_0x040e:
            int r15 = r6.getTop()
            goto L_0x040c
        L_0x0413:
            float r9 = r11.mLocation
            int r9 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r9 == 0) goto L_0x0444
            int r9 = r0.mOrientation
            r52 = r4
            r4 = 0
            if (r9 != 0) goto L_0x0432
            float r9 = r11.mLocationInParent
            float r9 = r9 - r15
            r6.setTranslationX(r9)
            android.view.ViewPropertyAnimator r9 = r6.animate()
            android.view.ViewPropertyAnimator r4 = r9.translationX(r4)
            r4.start()
            goto L_0x0446
        L_0x0432:
            float r9 = r11.mLocationInParent
            float r9 = r9 - r15
            r6.setTranslationY(r9)
            android.view.ViewPropertyAnimator r9 = r6.animate()
            android.view.ViewPropertyAnimator r4 = r9.translationY(r4)
            r4.start()
            goto L_0x0446
        L_0x0444:
            r52 = r4
        L_0x0446:
            int r3 = r3 + 1
            r4 = r52
            goto L_0x03f5
        L_0x044b:
            r52 = r4
            if (r2 == 0) goto L_0x045e
            r3 = r13
            r4 = 1065353216(0x3f800000, float:1.0)
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0459
            r4 = r18
            goto L_0x045a
        L_0x0459:
            r4 = 0
        L_0x045a:
            int r13 = r13 + r4
            r26 = r3
            goto L_0x046f
        L_0x045e:
            if (r5 == 0) goto L_0x046f
            r3 = r13
            r4 = 1065353216(0x3f800000, float:1.0)
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x046a
            r4 = r19
            goto L_0x046c
        L_0x046a:
            int r4 = r18 + r19
        L_0x046c:
            int r13 = r13 + r4
            r27 = r3
        L_0x046f:
            int r3 = r0.mSpace
            int r25 = r13 + r3
            int r21 = r50 + 1
            r4 = r36
            r10 = r37
            r5 = r38
            r12 = r39
            r13 = r41
            r2 = r42
            r15 = r43
            r14 = r44
            r6 = r47
            r11 = r48
            r3 = r51
            goto L_0x01b1
        L_0x048d:
            r42 = r2
            r51 = r3
            r36 = r4
            r38 = r5
            r47 = r6
            r37 = r10
            r48 = r11
            r39 = r12
            r41 = r13
            r44 = r14
            r43 = r15
            r9 = r32
            r11 = r33
            r15 = r34
            com.android.tv.settings.widget.ScrollController r2 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r2 = r2.mainAxis()
            r10 = r48
            float r3 = (float) r10
            float r3 = r3 * r1
            float r4 = (float) r6
            r5 = 1065353216(0x3f800000, float:1.0)
            float r12 = r5 - r1
            float r4 = r4 * r12
            float r3 = r3 + r4
            int r3 = (int) r3
            r2.setAlignExtraOffset(r3)
            com.android.tv.settings.widget.ScrollController r2 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r2 = r2.mainAxis()
            r2.setExpandedSize(r9)
            com.android.tv.settings.widget.ScrollController r2 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r2 = r2.mainAxis()
            r2.setExtraSpaceLow(r11)
            com.android.tv.settings.widget.ScrollController r2 = r0.mScroll
            com.android.tv.settings.widget.ScrollController$Axis r2 = r2.mainAxis()
            r2.setExtraSpaceHigh(r15)
            r3 = r23
            r2 = 0
        L_0x04dc:
            if (r2 >= r3) goto L_0x054f
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r4 = r0.mExpandedViews
            java.lang.Object r4 = r4.get(r2)
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r4 = (com.android.tv.settings.widget.ScrollAdapterView.ExpandedView) r4
            r5 = r44
            if (r4 == r5) goto L_0x0539
            r12 = r43
            if (r4 == r12) goto L_0x0532
            r13 = r42
            if (r4 == r13) goto L_0x052d
            android.view.View r14 = r4.expandedView
            boolean r14 = r14.hasFocus()
            if (r14 == 0) goto L_0x0507
            int r14 = r4.index
            int r14 = r0.expandableIndexFromAdapterIndex(r14)
            android.view.View r14 = r0.getChildAt(r14)
            r14.requestFocus()
        L_0x0507:
            r4.close()
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedChildStates r14 = r0.mExpandedChildStates
            r53 = r6
            android.view.View r6 = r4.expandedView
            r54 = r7
            int r7 = r4.index
            r14.saveInvisibleView(r6, r7)
            android.view.View r6 = r4.expandedView
            r0.removeViewInLayout(r6)
            com.android.tv.settings.widget.ScrollAdapterView$RecycledViews r6 = r0.mRecycleExpandedViews
            android.view.View r7 = r4.expandedView
            int r14 = r4.viewType
            r6.recycleView(r7, r14)
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r6 = r0.mExpandedViews
            r6.remove(r2)
            int r3 = r3 + -1
            goto L_0x0543
        L_0x052d:
            r53 = r6
            r54 = r7
            goto L_0x0541
        L_0x0532:
            r53 = r6
            r54 = r7
            r13 = r42
            goto L_0x0541
        L_0x0539:
            r53 = r6
            r54 = r7
            r13 = r42
            r12 = r43
        L_0x0541:
            int r2 = r2 + 1
        L_0x0543:
            r44 = r5
            r43 = r12
            r42 = r13
            r6 = r53
            r7 = r54
            goto L_0x04dc
        L_0x054f:
            r53 = r6
            r54 = r7
            r13 = r42
            r12 = r43
            r5 = r44
            r2 = 0
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r4 = r0.mExpandedViews
            int r4 = r4.size()
        L_0x0560:
            if (r2 >= r4) goto L_0x05e2
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r6 = r0.mExpandedViews
            java.lang.Object r6 = r6.get(r2)
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r6 = (com.android.tv.settings.widget.ScrollAdapterView.ExpandedView) r6
            if (r6 != r5) goto L_0x056f
            r7 = r26
            goto L_0x0571
        L_0x056f:
            r7 = r27
        L_0x0571:
            if (r6 == r13) goto L_0x0588
            if (r6 != r12) goto L_0x057f
            r14 = 1065353216(0x3f800000, float:1.0)
            int r16 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x057c
            goto L_0x057f
        L_0x057c:
            r55 = r3
            goto L_0x058a
        L_0x057f:
            android.view.View r14 = r6.expandedView
            r55 = r3
            r3 = 0
            r14.setVisibility(r3)
            goto L_0x058a
        L_0x0588:
            r55 = r3
        L_0x058a:
            int r3 = r0.mOrientation
            if (r3 != 0) goto L_0x05b4
            android.view.View r3 = r6.expandedView
            boolean r3 = r3.isLayoutRequested()
            if (r3 == 0) goto L_0x059b
            android.view.View r3 = r6.expandedView
            r0.measureChild(r3)
        L_0x059b:
            android.view.View r3 = r6.expandedView
            android.view.View r14 = r6.expandedView
            int r14 = r14.getMeasuredWidth()
            int r14 = r14 + r7
            r56 = r4
            android.view.View r4 = r6.expandedView
            int r4 = r4.getMeasuredHeight()
            r57 = r5
            r5 = 0
            r3.layout(r7, r5, r14, r4)
            r14 = r5
            goto L_0x05d8
        L_0x05b4:
            r56 = r4
            r57 = r5
            android.view.View r3 = r6.expandedView
            boolean r3 = r3.isLayoutRequested()
            if (r3 == 0) goto L_0x05c5
            android.view.View r3 = r6.expandedView
            r0.measureChild(r3)
        L_0x05c5:
            android.view.View r3 = r6.expandedView
            android.view.View r4 = r6.expandedView
            int r4 = r4.getMeasuredWidth()
            android.view.View r5 = r6.expandedView
            int r5 = r5.getMeasuredHeight()
            int r5 = r5 + r7
            r14 = 0
            r3.layout(r14, r7, r4, r5)
        L_0x05d8:
            int r2 = r2 + 1
            r3 = r55
            r4 = r56
            r5 = r57
            goto L_0x0560
        L_0x05e2:
            r55 = r3
            r57 = r5
            r2 = 0
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r3 = r0.mExpandedViews
            int r3 = r3.size()
        L_0x05ed:
            if (r2 >= r3) goto L_0x0610
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r4 = r0.mExpandedViews
            java.lang.Object r4 = r4.get(r2)
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r4 = (com.android.tv.settings.widget.ScrollAdapterView.ExpandedView) r4
            if (r4 == r13) goto L_0x0605
            if (r4 != r12) goto L_0x0602
            r5 = 1065353216(0x3f800000, float:1.0)
            int r6 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r6 != 0) goto L_0x060d
            goto L_0x0607
        L_0x0602:
            r5 = 1065353216(0x3f800000, float:1.0)
            goto L_0x060d
        L_0x0605:
            r5 = 1065353216(0x3f800000, float:1.0)
        L_0x0607:
            android.view.View r6 = r4.expandedView
            r7 = 4
            r6.setVisibility(r7)
        L_0x060d:
            int r2 = r2 + 1
            goto L_0x05ed
        L_0x0610:
            com.android.tv.settings.widget.ScrollAdapterBase r2 = r0.mExpandAdapter
            if (r2 == 0) goto L_0x0651
            boolean r2 = r58.hasFocus()
            if (r2 == 0) goto L_0x0651
            android.view.View r2 = r58.getFocusedChild()
            int r3 = r0.indexOfChild(r2)
            int r4 = r58.firstExpandableIndex()
            if (r3 < r4) goto L_0x0651
            r4 = 0
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r5 = r0.mExpandedViews
            int r5 = r5.size()
        L_0x062f:
            if (r4 >= r5) goto L_0x0651
            java.util.ArrayList<com.android.tv.settings.widget.ScrollAdapterView$ExpandedView> r6 = r0.mExpandedViews
            java.lang.Object r6 = r6.get(r4)
            com.android.tv.settings.widget.ScrollAdapterView$ExpandedView r6 = (com.android.tv.settings.widget.ScrollAdapterView.ExpandedView) r6
            int r7 = r6.index
            int r7 = r0.expandableIndexFromAdapterIndex(r7)
            if (r7 != r3) goto L_0x064e
            android.view.View r7 = r6.expandedView
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x064e
            android.view.View r7 = r6.expandedView
            r7.requestFocus()
        L_0x064e:
            int r4 = r4 + 1
            goto L_0x062f
        L_0x0651:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.widget.ScrollAdapterView.updateViewsLocations(boolean):void");
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View view = getSelectedExpandedView();
        if (view != null) {
            return view.requestFocus(direction, previouslyFocusedRect);
        }
        View view2 = getSelectedView();
        if (view2 != null) {
            return view2.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    private int getScrollCenter(View view) {
        return ((ChildViewHolder) view.getTag(R.id.ScrollAdapterViewChild)).mScrollCenter;
    }

    public int getScrollItemAlign() {
        return this.mScroll.getScrollItemAlign();
    }

    private boolean hasScrollPosition(int scrollCenter, int maxSize, int scrollPosInMain) {
        switch (this.mScroll.getScrollItemAlign()) {
            case 0:
                if ((scrollCenter - (maxSize / 2)) - this.mSpaceLow >= scrollPosInMain || scrollPosInMain >= (maxSize / 2) + scrollCenter + this.mSpaceHigh) {
                    return false;
                }
                return true;
            case 1:
                if (scrollCenter - this.mSpaceLow > scrollPosInMain || scrollPosInMain >= scrollCenter + maxSize + this.mSpaceHigh) {
                    return false;
                }
                return true;
            case 2:
                if ((scrollCenter - maxSize) - this.mSpaceLow >= scrollPosInMain || scrollPosInMain > this.mSpaceHigh + scrollCenter) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    private boolean hasScrollPositionSecondAxis(int scrollCenterOffAxis, int viewSizeOffAxis, int centerOffAxis) {
        return (centerOffAxis - (viewSizeOffAxis / 2)) - this.mSpaceLow <= scrollCenterOffAxis && scrollCenterOffAxis <= ((viewSizeOffAxis / 2) + centerOffAxis) + this.mSpaceHigh;
    }

    private int computeScrollCenter(int expandViewIndex) {
        int lastIndex = lastExpandableIndex();
        int firstIndex = firstExpandableIndex();
        View firstView = getChildAt(firstIndex);
        int center = 0;
        switch (this.mScroll.getScrollItemAlign()) {
            case 0:
                center = getCenter(firstView);
                break;
            case 1:
                center = this.mOrientation == 0 ? firstView.getLeft() : firstView.getTop();
                break;
            case 2:
                center = this.mOrientation == 0 ? firstView.getRight() : firstView.getBottom();
                break;
        }
        if (this.mScroll.mainAxis().getSelectedTakesMoreSpace()) {
            center -= ((ChildViewHolder) firstView.getTag(R.id.ScrollAdapterViewChild)).mExtraSpaceLow;
        }
        int nextCenter = -1;
        int center2 = center;
        int idx = firstIndex;
        while (idx < lastIndex) {
            View view = getChildAt(idx);
            if (idx <= expandViewIndex && expandViewIndex < this.mItemsOnOffAxis + idx) {
                return center2;
            }
            if (idx < lastIndex - this.mItemsOnOffAxis) {
                View nextView = getChildAt(this.mItemsOnOffAxis + idx);
                switch (this.mScroll.getScrollItemAlign()) {
                    case 0:
                        nextCenter = center2 + ((getSize(view) + getSize(nextView)) / 2);
                        break;
                    case 1:
                        nextCenter = center2 + getSize(view);
                        break;
                    case 2:
                        nextCenter = center2 + getSize(nextView);
                        break;
                }
                nextCenter += this.mSpace;
            } else {
                nextCenter = Integer.MAX_VALUE;
            }
            center2 = nextCenter;
            idx += this.mItemsOnOffAxis;
        }
        assertFailure("Scroll out of range?");
        return 0;
    }

    private int getScrollLow(int scrollCenter, View view) {
        ChildViewHolder holder = (ChildViewHolder) view.getTag(R.id.ScrollAdapterViewChild);
        switch (this.mScroll.getScrollItemAlign()) {
            case 0:
                return scrollCenter - (holder.mMaxSize / 2);
            case 1:
                return scrollCenter;
            case 2:
                return scrollCenter - holder.mMaxSize;
            default:
                return 0;
        }
    }

    private int getScrollHigh(int scrollCenter, View view) {
        ChildViewHolder holder = (ChildViewHolder) view.getTag(R.id.ScrollAdapterViewChild);
        switch (this.mScroll.getScrollItemAlign()) {
            case 0:
                return (holder.mMaxSize / 2) + scrollCenter;
            case 1:
                return holder.mMaxSize + scrollCenter;
            case 2:
                return scrollCenter;
            default:
                return 0;
        }
    }

    static final class AdapterViewState {
        Bundle expandableChildStates = Bundle.EMPTY;
        Bundle expandedChildStates = Bundle.EMPTY;
        int index;
        int itemsOnOffAxis;

        AdapterViewState() {
        }
    }

    static final class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        final AdapterViewState theState = new AdapterViewState();

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.theState.itemsOnOffAxis);
            out.writeInt(this.theState.index);
            out.writeBundle(this.theState.expandedChildStates);
            out.writeBundle(this.theState.expandableChildStates);
        }

        SavedState(Parcel in) {
            super(in);
            this.theState.itemsOnOffAxis = in.readInt();
            this.theState.index = in.readInt();
            ClassLoader loader = ScrollAdapterView.class.getClassLoader();
            this.theState.expandedChildStates = in.readBundle(loader);
            this.theState.expandableChildStates = in.readBundle(loader);
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        int index = findViewIndexContainingScrollCenter();
        if (index < 0) {
            return superState;
        }
        this.mExpandedChildStates.saveVisibleViews();
        this.mExpandableChildStates.saveVisibleViews();
        ss.theState.itemsOnOffAxis = this.mItemsOnOffAxis;
        ss.theState.index = getAdapterIndex(index);
        ss.theState.expandedChildStates = this.mExpandedChildStates.getChildStates();
        ss.theState.expandableChildStates = this.mExpandableChildStates.getChildStates();
        return ss;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mLoadingState = ss.theState;
        fireDataSetChanged();
    }

    public int getSaveExpandableViewsPolicy() {
        return this.mExpandableChildStates.getSavePolicy();
    }

    public void setSaveExpandableViewsPolicy(int saveExpandablePolicy) {
        this.mExpandableChildStates.setSavePolicy(saveExpandablePolicy);
    }

    public int getSaveExpandableViewsLimit() {
        return this.mExpandableChildStates.getLimitNumber();
    }

    public void setSaveExpandableViewsLimit(int saveExpandableChildNumber) {
        this.mExpandableChildStates.setLimitNumber(saveExpandableChildNumber);
    }

    public int getSaveExpandedViewsPolicy() {
        return this.mExpandedChildStates.getSavePolicy();
    }

    public void setSaveExpandedViewsPolicy(int saveExpandedChildPolicy) {
        this.mExpandedChildStates.setSavePolicy(saveExpandedChildPolicy);
    }

    public int getSaveExpandedViewsLimit() {
        return this.mExpandedChildStates.getLimitNumber();
    }

    public void setSaveExpandedViewsLimit(int mSaveExpandedNumber) {
        this.mExpandedChildStates.setLimitNumber(mSaveExpandedNumber);
    }

    public ArrayList<OnItemChangeListener> getOnItemChangeListeners() {
        return this.mOnItemChangeListeners;
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.mOnItemChangeListeners.clear();
        addOnItemChangeListener(onItemChangeListener);
    }

    public void addOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        if (!this.mOnItemChangeListeners.contains(onItemChangeListener)) {
            this.mOnItemChangeListeners.add(onItemChangeListener);
        }
    }

    public ArrayList<OnScrollListener> getOnScrollListeners() {
        return this.mOnScrollListeners;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListeners.clear();
        addOnScrollListener(onScrollListener);
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        if (!this.mOnScrollListeners.contains(onScrollListener)) {
            this.mOnScrollListeners.add(onScrollListener);
        }
    }

    public void setExpandedItemInAnim(Animator animator) {
        this.mExpandedItemInAnim = animator;
    }

    public Animator getExpandedItemInAnim() {
        return this.mExpandedItemInAnim;
    }

    public void setExpandedItemOutAnim(Animator animator) {
        this.mExpandedItemOutAnim = animator;
    }

    public Animator getExpandedItemOutAnim() {
        return this.mExpandedItemOutAnim;
    }

    public boolean isNavigateOutOfOffAxisAllowed() {
        return this.mNavigateOutOfOffAxisAllowed;
    }

    public boolean isNavigateOutAllowed() {
        return this.mNavigateOutAllowed;
    }

    public void setNavigateOutOfOffAxisAllowed(boolean navigateOut) {
        this.mNavigateOutOfOffAxisAllowed = navigateOut;
    }

    public void setNavigateOutAllowed(boolean navigateOut) {
        this.mNavigateOutAllowed = navigateOut;
    }

    public boolean isNavigateInAnimationAllowed() {
        return this.mNavigateInAnimationAllowed;
    }

    public void setNavigateInAnimationAllowed(boolean navigateInAnimation) {
        this.mNavigateInAnimationAllowed = navigateInAnimation;
    }

    public void setSpace(int space) {
        this.mSpace = space;
        this.mSpaceLow = this.mSpace / 2;
        this.mSpaceHigh = this.mSpace - this.mSpaceLow;
    }

    public int getSpace() {
        return this.mSpace;
    }

    public void setSelectedSize(int selectedScale) {
        this.mSelectedSize = selectedScale;
    }

    public int getSelectedSize() {
        return this.mSelectedSize;
    }

    public void setSelectedTakesMoreSpace(boolean selectedTakesMoreSpace) {
        this.mScroll.mainAxis().setSelectedTakesMoreSpace(selectedTakesMoreSpace);
    }

    public boolean getSelectedTakesMoreSpace() {
        return this.mScroll.mainAxis().getSelectedTakesMoreSpace();
    }

    private boolean selectedItemCanScale() {
        return (this.mSelectedSize == 0 && this.mAdapterCustomSize == null) ? false : true;
    }

    private int getSelectedItemSize(int adapterIndex, View view) {
        if (this.mSelectedSize != 0) {
            return this.mSelectedSize;
        }
        if (this.mAdapterCustomSize != null) {
            return this.mAdapterCustomSize.getSelectItemSize(adapterIndex, view);
        }
        return 0;
    }

    private static void assertFailure(String msg) {
        throw new RuntimeException(msg);
    }
}
