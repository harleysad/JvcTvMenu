package com.android.tv.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.UiThread;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.util.Utils;
import com.mediatek.wwtv.tvcenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@UiThread
public class MenuLayoutManager {
    static final boolean DEBUG = false;
    private static final int INVALID_POSITION = -1;
    static final String TAG = "MenuLayoutManager";
    /* access modifiers changed from: private */
    public static final long TITLE_SHOW_DURATION_BEFORE_HIDDEN_MS = TimeUnit.SECONDS.toMillis(2);
    /* access modifiers changed from: private */
    public AnimatorSet mAnimatorSet;
    private final long mCurrentContentsFadeInDuration;
    private final TimeInterpolator mFastOutLinearIn = new FastOutLinearInInterpolator();
    private final TimeInterpolator mFastOutSlowIn = new FastOutSlowInInterpolator();
    /* access modifiers changed from: private */
    public final TimeInterpolator mLinearOutSlowIn = new LinearOutSlowInInterpolator();
    private final int mMenuMarginBottomMin;
    /* access modifiers changed from: private */
    public final List<MenuRowView> mMenuRowViews = new ArrayList();
    /* access modifiers changed from: private */
    public final List<MenuRow> mMenuRows = new ArrayList();
    /* access modifiers changed from: private */
    public final MenuView mMenuView;
    private final long mOldContentsFadeOutDuration;
    private int mPendingSelectedPosition = -1;
    private final List<ViewPropertyValueHolder> mPropertyValuesAfterAnimation = new ArrayList();
    /* access modifiers changed from: private */
    public final List<Integer> mRemovingRowViews = new ArrayList();
    private final int mRowAlignFromBottom;
    private final long mRowAnimationDuration;
    private final int mRowContentsPaddingBottomMax;
    private final int mRowContentsPaddingTop;
    private final int mRowScrollUpAnimationOffset;
    private final int mRowTitleHeight;
    private final int mRowTitleTextDescenderHeight;
    private int mSelectedPosition = -1;
    /* access modifiers changed from: private */
    public TextView mTempTitleViewForCurrent;
    /* access modifiers changed from: private */
    public TextView mTempTitleViewForOld;
    /* access modifiers changed from: private */
    public ObjectAnimator mTitleFadeOutAnimator;

    public MenuLayoutManager(Context context, MenuView menuView) {
        this.mMenuView = menuView;
        Resources res = context.getResources();
        this.mRowAlignFromBottom = res.getDimensionPixelOffset(R.dimen.menu_row_align_from_bottom);
        this.mRowContentsPaddingTop = res.getDimensionPixelOffset(R.dimen.menu_row_contents_padding_top);
        this.mRowContentsPaddingBottomMax = res.getDimensionPixelOffset(R.dimen.menu_row_contents_padding_bottom_max);
        this.mRowTitleTextDescenderHeight = res.getDimensionPixelOffset(R.dimen.menu_row_title_text_descender_height);
        this.mMenuMarginBottomMin = res.getDimensionPixelOffset(R.dimen.menu_margin_bottom_min);
        this.mRowTitleHeight = res.getDimensionPixelSize(R.dimen.menu_row_title_height);
        this.mRowScrollUpAnimationOffset = res.getDimensionPixelOffset(R.dimen.menu_row_scroll_up_anim_offset);
        this.mRowAnimationDuration = (long) res.getInteger(R.integer.menu_row_selection_anim_duration);
        this.mOldContentsFadeOutDuration = (long) res.getInteger(R.integer.menu_previous_contents_fade_out_duration);
        this.mCurrentContentsFadeInDuration = (long) res.getInteger(R.integer.menu_current_contents_fade_in_duration);
    }

    public void setMenuRowsAndViews(List<MenuRow> menuRows, List<MenuRowView> menuRowViews) {
        this.mMenuRows.clear();
        this.mMenuRows.addAll(menuRows);
        this.mMenuRowViews.clear();
        this.mMenuRowViews.addAll(menuRowViews);
    }

    public void layout(int left, int top, int right, int bottom) {
        if (this.mAnimatorSet == null) {
            int count = this.mMenuRowViews.size();
            if (this.mMenuRowViews.get(this.mSelectedPosition).getVisibility() == 8) {
                int firstVisiblePosition = findNextVisiblePosition(-1);
                if (firstVisiblePosition != -1) {
                    this.mSelectedPosition = firstVisiblePosition;
                } else {
                    return;
                }
            }
            List<Rect> layouts = getViewLayouts(left, top, right, bottom);
            for (int i = 0; i < count; i++) {
                Rect rect = layouts.get(i);
                if (rect != null) {
                    this.mMenuRowViews.get(i).layout(rect.left, rect.top, rect.right, rect.bottom);
                }
            }
            for (MenuRowView view : this.mMenuRowViews) {
                if (view.getVisibility() == 0 && view.getContentsView().getVisibility() == 4) {
                    view.onDeselected();
                }
            }
            if (this.mPendingSelectedPosition != -1) {
                setSelectedPositionSmooth(this.mPendingSelectedPosition);
            }
        }
    }

    private int findNextVisiblePosition(int start) {
        int count = this.mMenuRowViews.size();
        for (int i = start + 1; i < count; i++) {
            if (this.mMenuRowViews.get(i).getVisibility() != 8) {
                return i;
            }
        }
        return -1;
    }

    private void dumpChildren(String prefix) {
        int position = 0;
        for (MenuRowView view : this.mMenuRowViews) {
            View title = view.getChildAt(0);
            View contents = view.getChildAt(1);
            Log.d(TAG, prefix + " position=" + position + " rowView={visiblility=" + view.getVisibility() + ", alpha=" + view.getAlpha() + ", translationY=" + view.getTranslationY() + ", left=" + view.getLeft() + ", top=" + view.getTop() + ", right=" + view.getRight() + ", bottom=" + view.getBottom() + "}, title={visiblility=" + title.getVisibility() + ", alpha=" + title.getAlpha() + ", translationY=" + title.getTranslationY() + ", left=" + title.getLeft() + ", top=" + title.getTop() + ", right=" + title.getRight() + ", bottom=" + title.getBottom() + "}, contents={visiblility=" + contents.getVisibility() + ", alpha=" + contents.getAlpha() + ", translationY=" + contents.getTranslationY() + ", left=" + contents.getLeft() + ", top=" + contents.getTop() + ", right=" + contents.getRight() + ", bottom=" + contents.getBottom() + "}");
            position++;
        }
    }

    private boolean isVisibleInLayout(int position, MenuRowView view, List<Integer> rowsToAdd, List<Integer> rowsToRemove) {
        return (view.getVisibility() != 8 && !rowsToRemove.contains(Integer.valueOf(position))) || rowsToAdd.contains(Integer.valueOf(position));
    }

    private List<Rect> getViewLayouts(int left, int top, int right, int bottom) {
        return getViewLayouts(left, top, right, bottom, Collections.emptyList(), Collections.emptyList());
    }

    private List<Rect> getViewLayouts(int left, int top, int right, int bottom, List<Integer> rowsToAdd, List<Integer> rowsToRemove) {
        int childBottom;
        int childBottom2;
        List<Integer> list = rowsToAdd;
        List<Integer> list2 = rowsToRemove;
        int relateiveRight = right - left;
        int relativeBottom = bottom - top;
        List<Rect> layouts = new ArrayList<>();
        int count = this.mMenuRowViews.size();
        MenuRowView selectedView = this.mMenuRowViews.get(this.mSelectedPosition);
        int rowTitleHeight = selectedView.getTitleView().getMeasuredHeight();
        int rowContentsHeight = selectedView.getPreferredContentsHeight();
        int childTop = (((relativeBottom - this.mRowAlignFromBottom) - (rowContentsHeight / 2)) - this.mRowContentsPaddingTop) - rowTitleHeight;
        int childBottom3 = relativeBottom;
        int position = this.mSelectedPosition + 1;
        while (true) {
            if (position >= count) {
                childBottom = childBottom3;
                break;
            }
            MenuRowView nextView = this.mMenuRowViews.get(position);
            if (isVisibleInLayout(position, nextView, list, list2)) {
                MenuRowView menuRowView = nextView;
                int nextTitleTopMax = ((relativeBottom - this.mMenuMarginBottomMin) - rowTitleHeight) + this.mRowTitleTextDescenderHeight;
                childBottom = Math.min(nextTitleTopMax, (((relativeBottom - this.mRowAlignFromBottom) + (rowContentsHeight / 2)) + this.mRowContentsPaddingBottomMax) - rowTitleHeight);
                int i = nextTitleTopMax;
                layouts.add(new Rect(0, childBottom, relateiveRight, relativeBottom));
                break;
            }
            layouts.add((Object) null);
            position++;
        }
        layouts.add(0, new Rect(0, childTop, relateiveRight, childBottom2));
        for (int i2 = this.mSelectedPosition - 1; i2 >= 0; i2--) {
            MenuRowView view = this.mMenuRowViews.get(i2);
            if (isVisibleInLayout(i2, view, list, list2)) {
                MenuRowView menuRowView2 = view;
                childTop -= this.mRowTitleHeight;
                int childBottom4 = childTop + rowTitleHeight;
                layouts.add(0, new Rect(0, childTop, relateiveRight, childBottom4));
                childBottom2 = childBottom4;
            } else {
                layouts.add(0, (Object) null);
                childBottom2 = childBottom2;
            }
        }
        int i3 = childBottom2;
        int childTop2 = relativeBottom;
        while (true) {
            position++;
            if (position >= count) {
                return layouts;
            }
            if (isVisibleInLayout(position, this.mMenuRowViews.get(position), list, list2)) {
                int childBottom5 = childTop2 + rowTitleHeight;
                layouts.add(new Rect(0, childTop2, relateiveRight, childBottom5));
                childTop2 += this.mRowTitleHeight;
                int i4 = childBottom5;
            } else {
                layouts.add((Object) null);
            }
        }
    }

    public void setSelectedPosition(int position) {
        if (this.mSelectedPosition == position || !Utils.isIndexValid(this.mMenuRowViews, position)) {
            return;
        }
        if (!this.mMenuRows.get(position).isVisible()) {
            Log.e(TAG, "Selecting invisible row: " + position);
            return;
        }
        if (Utils.isIndexValid(this.mMenuRowViews, this.mSelectedPosition)) {
            this.mMenuRowViews.get(this.mSelectedPosition).onDeselected();
        }
        this.mSelectedPosition = position;
        this.mPendingSelectedPosition = -1;
        if (Utils.isIndexValid(this.mMenuRowViews, this.mSelectedPosition)) {
            this.mMenuRowViews.get(this.mSelectedPosition).onSelected(false);
        }
        if (this.mMenuView.getVisibility() == 0) {
            this.mMenuView.requestFocus();
            this.mMenuView.requestLayout();
        }
    }

    public void setSelectedPositionSmooth(int position) {
        boolean oldIndexValid;
        boolean newIndexValid;
        List<Rect> layouts;
        ArrayList arrayList;
        TextView oldTitleView;
        Rect oldLayoutRect;
        Rect oldLayoutRect2;
        int nextPosition;
        int nextPosition2;
        int nextPosition3;
        int i = position;
        Log.d(TAG, "setSelectedPositionSmooth(position=" + i + ") {previousPosition=" + this.mSelectedPosition + "}");
        if (this.mMenuView.getVisibility() != 0) {
            setSelectedPosition(position);
        } else if (this.mSelectedPosition == i || !(oldIndexValid = Utils.isIndexValid(this.mMenuRowViews, this.mSelectedPosition)) || !(newIndexValid = Utils.isIndexValid(this.mMenuRowViews, i))) {
        } else {
            if (!this.mMenuRows.get(i).isVisible()) {
                Log.e(TAG, "Moving to the invisible row: " + i);
                return;
            }
            if (this.mAnimatorSet != null) {
                this.mAnimatorSet.end();
            }
            if (this.mTitleFadeOutAnimator != null) {
                this.mTitleFadeOutAnimator.cancel();
            }
            MenuRowView currentView = this.mMenuRowViews.get(i);
            TextView currentTitleView = currentView.getTitleView();
            View currentContentsView = currentView.getContentsView();
            currentTitleView.setVisibility(0);
            currentContentsView.setVisibility(0);
            if (!(currentContentsView instanceof RecyclerView) || !((RecyclerView) currentContentsView).hasPendingAdapterUpdates()) {
                int oldPosition = this.mSelectedPosition;
                this.mSelectedPosition = i;
                this.mPendingSelectedPosition = -1;
                this.mMenuView.requestFocus();
                if (this.mTempTitleViewForOld == null) {
                    this.mTempTitleViewForOld = (TextView) this.mMenuView.findViewById(R.id.temp_title_for_old);
                    this.mTempTitleViewForCurrent = (TextView) this.mMenuView.findViewById(R.id.temp_title_for_current);
                }
                this.mPropertyValuesAfterAnimation.clear();
                ArrayList arrayList2 = new ArrayList();
                boolean scrollDown = i > oldPosition;
                List<Rect> layouts2 = getViewLayouts(this.mMenuView.getLeft(), this.mMenuView.getTop(), this.mMenuView.getRight(), this.mMenuView.getBottom());
                MenuRowView oldView = this.mMenuRowViews.get(oldPosition);
                View oldContentsView = oldView.getContentsView();
                TimeInterpolator timeInterpolator = this.mLinearOutSlowIn;
                MenuRowView oldView2 = oldView;
                MenuRow oldRow = this.mMenuRows.get(oldPosition);
                boolean z = oldIndexValid;
                ArrayList arrayList3 = arrayList2;
                arrayList3.add(createAlphaAnimator(oldContentsView, 1.0f, 0.0f, 1.0f, timeInterpolator).setDuration(this.mOldContentsFadeOutDuration));
                TextView oldTitleView2 = oldView2.getTitleView();
                setTempTitleView(this.mTempTitleViewForOld, oldTitleView2);
                List<Rect> layouts3 = layouts2;
                Rect oldLayoutRect3 = layouts3.get(oldPosition);
                if (scrollDown) {
                    MenuRow oldRow2 = oldRow;
                    if (!oldRow2.hideTitleWhenSelected() || oldTitleView2.getVisibility() == 0) {
                        boolean z2 = newIndexValid;
                        arrayList3.add(createScaleXAnimator(this.mTempTitleViewForOld, oldView2.getTitleViewScaleSelected(), 1.0f));
                        arrayList3.add(createScaleYAnimator(this.mTempTitleViewForOld, oldView2.getTitleViewScaleSelected(), 1.0f));
                        arrayList3.add(createAlphaAnimator(this.mTempTitleViewForOld, oldTitleView2.getAlpha(), oldView2.getTitleViewAlphaDeselected(), this.mLinearOutSlowIn));
                        arrayList3.add(createTranslationYAnimator(this.mTempTitleViewForOld, 0.0f, (float) (oldLayoutRect3.top - this.mTempTitleViewForOld.getTop())));
                    } else {
                        this.mTempTitleViewForOld.setScaleX(1.0f);
                        this.mTempTitleViewForOld.setScaleY(1.0f);
                        MenuRow menuRow = oldRow2;
                        boolean z3 = newIndexValid;
                        arrayList3.add(createAlphaAnimator(this.mTempTitleViewForOld, 0.0f, oldView2.getTitleViewAlphaDeselected(), this.mFastOutLinearIn));
                        int offset = oldLayoutRect3.top - this.mTempTitleViewForOld.getTop();
                        arrayList3.add(createTranslationYAnimator(this.mTempTitleViewForOld, (float) (this.mRowScrollUpAnimationOffset + offset), (float) offset));
                    }
                    oldTitleView2.setAlpha(oldView2.getTitleViewAlphaDeselected());
                    oldTitleView2.setVisibility(4);
                    layouts = layouts3;
                    arrayList = arrayList3;
                    oldLayoutRect = oldLayoutRect3;
                    oldTitleView = oldTitleView2;
                } else {
                    MenuRow menuRow2 = oldRow;
                    Rect currentLayoutRect = new Rect(layouts3.get(i));
                    int distance = Math.max(this.mRowScrollUpAnimationOffset, currentLayoutRect.top - currentView.getTop());
                    Rect oldLayoutRect4 = oldLayoutRect3;
                    int distanceToTopOfSecondTitle = (oldLayoutRect3.top - this.mRowScrollUpAnimationOffset) - oldView2.getTop();
                    arrayList3.add(createTranslationYAnimator(oldTitleView2, 0.0f, (float) Math.min(distance, distanceToTopOfSecondTitle)));
                    int i2 = distance;
                    int i3 = distanceToTopOfSecondTitle;
                    layouts = layouts3;
                    Rect rect = currentLayoutRect;
                    arrayList = arrayList3;
                    arrayList.add(createAlphaAnimator(oldTitleView2, 1.0f, 0.0f, 1.0f, this.mLinearOutSlowIn).setDuration(this.mOldContentsFadeOutDuration));
                    oldTitleView = oldTitleView2;
                    arrayList.add(createScaleXAnimator(oldTitleView, oldView2.getTitleViewScaleSelected(), 1.0f));
                    arrayList.add(createScaleYAnimator(oldTitleView, oldView2.getTitleViewScaleSelected(), 1.0f));
                    this.mTempTitleViewForOld.setScaleX(1.0f);
                    this.mTempTitleViewForOld.setScaleY(1.0f);
                    arrayList.add(createAlphaAnimator(this.mTempTitleViewForOld, 0.0f, oldView2.getTitleViewAlphaDeselected(), this.mFastOutLinearIn));
                    oldLayoutRect = oldLayoutRect4;
                    int offset2 = oldLayoutRect.top - this.mTempTitleViewForOld.getTop();
                    arrayList.add(createTranslationYAnimator(this.mTempTitleViewForOld, (float) (offset2 - this.mRowScrollUpAnimationOffset), (float) offset2));
                }
                List<Rect> layouts4 = layouts;
                Rect currentLayoutRect2 = new Rect(layouts4.get(i));
                currentContentsView.setAlpha(0.0f);
                if (scrollDown) {
                    setTempTitleView(this.mTempTitleViewForCurrent, currentTitleView);
                    int distanceOldTitle = oldView2.getTop() - oldLayoutRect.top;
                    int distance2 = Math.max(this.mRowScrollUpAnimationOffset, distanceOldTitle);
                    int i4 = distanceOldTitle;
                    int distanceTopOfSecondTitle = (currentView.getTop() - this.mRowScrollUpAnimationOffset) - currentLayoutRect2.top;
                    int i5 = distance2;
                    arrayList.add(createTranslationYAnimator(currentTitleView, (float) Math.min(distance2, distanceTopOfSecondTitle), 0.0f));
                    currentView.setTop(currentLayoutRect2.top);
                    int i6 = distanceTopOfSecondTitle;
                    ObjectAnimator animator = createAlphaAnimator(currentTitleView, 0.0f, 1.0f, this.mFastOutLinearIn).setDuration(this.mCurrentContentsFadeInDuration);
                    animator.setStartDelay(this.mOldContentsFadeOutDuration);
                    currentTitleView.setAlpha(0.0f);
                    arrayList.add(animator);
                    arrayList.add(createScaleXAnimator(currentTitleView, 1.0f, currentView.getTitleViewScaleSelected()));
                    arrayList.add(createScaleYAnimator(currentTitleView, 1.0f, currentView.getTitleViewScaleSelected()));
                    ObjectAnimator objectAnimator = animator;
                    arrayList.add(createTranslationYAnimator(this.mTempTitleViewForCurrent, 0.0f, (float) (-this.mRowScrollUpAnimationOffset)));
                    oldLayoutRect2 = oldLayoutRect;
                    arrayList.add(createAlphaAnimator(this.mTempTitleViewForCurrent, currentView.getTitleViewAlphaDeselected(), 0.0f, this.mLinearOutSlowIn));
                    arrayList.add(createTranslationYAnimator(currentContentsView, (float) this.mRowScrollUpAnimationOffset, 0.0f));
                    ObjectAnimator animator2 = createAlphaAnimator(currentContentsView, 0.0f, 1.0f, this.mFastOutLinearIn).setDuration(this.mCurrentContentsFadeInDuration);
                    animator2.setStartDelay(this.mOldContentsFadeOutDuration);
                    arrayList.add(animator2);
                } else {
                    oldLayoutRect2 = oldLayoutRect;
                    currentView.setBottom(currentLayoutRect2.bottom);
                    int currentViewOffset = currentLayoutRect2.top - currentView.getTop();
                    arrayList.add(createTranslationYAnimator(currentTitleView, 0.0f, (float) currentViewOffset));
                    arrayList.add(createAlphaAnimator(currentTitleView, currentView.getTitleViewAlphaDeselected(), 1.0f, this.mFastOutSlowIn));
                    arrayList.add(createScaleXAnimator(currentTitleView, 1.0f, currentView.getTitleViewScaleSelected()));
                    arrayList.add(createScaleYAnimator(currentTitleView, 1.0f, currentView.getTitleViewScaleSelected()));
                    arrayList.add(createTranslationYAnimator(currentContentsView, (float) (currentViewOffset - this.mRowScrollUpAnimationOffset), (float) currentViewOffset));
                    ObjectAnimator animator3 = createAlphaAnimator(currentContentsView, 0.0f, 1.0f, this.mFastOutLinearIn).setDuration(this.mCurrentContentsFadeInDuration);
                    animator3.setStartDelay(this.mOldContentsFadeOutDuration);
                    arrayList.add(animator3);
                }
                if (scrollDown) {
                    int nextPosition4 = findNextVisiblePosition(position);
                    if (nextPosition4 != -1) {
                        MenuRowView nextView = this.mMenuRowViews.get(nextPosition4);
                        Rect nextLayoutRect = layouts4.get(nextPosition4);
                        nextPosition3 = nextPosition4;
                        arrayList.add(createTranslationYAnimator(nextView, (float) ((nextLayoutRect.top + this.mRowScrollUpAnimationOffset) - nextView.getTop()), (float) (nextLayoutRect.top - nextView.getTop())));
                        Rect rect2 = nextLayoutRect;
                        arrayList.add(createAlphaAnimator(nextView, 0.0f, 1.0f, this.mFastOutLinearIn));
                    } else {
                        nextPosition3 = nextPosition4;
                    }
                    Rect rect3 = currentLayoutRect2;
                    TextView textView = oldTitleView;
                    Rect rect4 = oldLayoutRect2;
                    nextPosition = nextPosition3;
                } else {
                    int nextPosition5 = findNextVisiblePosition(oldPosition);
                    if (nextPosition5 != -1) {
                        MenuRowView nextView2 = this.mMenuRowViews.get(nextPosition5);
                        arrayList.add(createTranslationYAnimator(nextView2, 0.0f, (float) this.mRowScrollUpAnimationOffset));
                        MenuRowView menuRowView = nextView2;
                        MenuRowView menuRowView2 = nextView2;
                        float titleViewAlphaDeselected = nextView2.getTitleViewAlphaDeselected();
                        nextPosition2 = nextPosition5;
                        Rect rect5 = oldLayoutRect2;
                        Rect rect6 = currentLayoutRect2;
                        TextView textView2 = oldTitleView;
                        arrayList.add(createAlphaAnimator(menuRowView, titleViewAlphaDeselected, 0.0f, 1.0f, this.mLinearOutSlowIn));
                    } else {
                        nextPosition2 = nextPosition5;
                        Rect rect7 = currentLayoutRect2;
                        TextView textView3 = oldTitleView;
                        Rect rect8 = oldLayoutRect2;
                    }
                    nextPosition = nextPosition2;
                }
                int count = this.mMenuRowViews.size();
                int i7 = 0;
                while (true) {
                    int i8 = i7;
                    if (i8 < count) {
                        MenuRowView view = this.mMenuRowViews.get(i8);
                        if (view.getVisibility() == 0 && i8 != oldPosition && i8 != i && i8 != nextPosition) {
                            Rect rect9 = layouts4.get(i8);
                            Rect rect10 = rect9;
                            arrayList.add(createTranslationYAnimator(view, 0.0f, (float) (rect9.top - view.getTop())));
                        }
                        i7 = i8 + 1;
                    } else {
                        ArrayList arrayList4 = new ArrayList();
                        arrayList4.addAll(this.mPropertyValuesAfterAnimation);
                        this.mAnimatorSet = new AnimatorSet();
                        this.mAnimatorSet.playTogether(arrayList);
                        ArrayList arrayList5 = arrayList;
                        AnonymousClass1 r8 = r0;
                        List<Rect> list = layouts4;
                        AnimatorSet animatorSet = this.mAnimatorSet;
                        final ArrayList arrayList6 = arrayList4;
                        ArrayList arrayList7 = arrayList4;
                        final MenuRowView menuRowView3 = oldView2;
                        int i9 = count;
                        final MenuRowView menuRowView4 = currentView;
                        int i10 = nextPosition;
                        final int nextPosition6 = i;
                        AnonymousClass1 r0 = new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = MenuLayoutManager.this.mAnimatorSet = null;
                                for (ViewPropertyValueHolder holder : arrayList6) {
                                    holder.property.set(holder.view, Float.valueOf(holder.value));
                                }
                                menuRowView3.onDeselected();
                                menuRowView4.onSelected(true);
                                menuRowView3.setImportantForAccessibility(1);
                                menuRowView4.setImportantForAccessibility(1);
                                MenuLayoutManager.this.mTempTitleViewForOld.setVisibility(8);
                                MenuLayoutManager.this.mTempTitleViewForCurrent.setVisibility(8);
                                MenuLayoutManager.this.layout(MenuLayoutManager.this.mMenuView.getLeft(), MenuLayoutManager.this.mMenuView.getTop(), MenuLayoutManager.this.mMenuView.getRight(), MenuLayoutManager.this.mMenuView.getBottom());
                                if (((MenuRow) MenuLayoutManager.this.mMenuRows.get(nextPosition6)).hideTitleWhenSelected()) {
                                    View titleView = ((MenuRowView) MenuLayoutManager.this.mMenuRowViews.get(nextPosition6)).getTitleView();
                                    ObjectAnimator unused2 = MenuLayoutManager.this.mTitleFadeOutAnimator = MenuLayoutManager.this.createAlphaAnimator(titleView, titleView.getAlpha(), 0.0f, MenuLayoutManager.this.mLinearOutSlowIn);
                                    MenuLayoutManager.this.mTitleFadeOutAnimator.setStartDelay(MenuLayoutManager.TITLE_SHOW_DURATION_BEFORE_HIDDEN_MS);
                                    MenuLayoutManager.this.mTitleFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
                                        private boolean mCanceled;

                                        public void onAnimationCancel(Animator animator) {
                                            this.mCanceled = true;
                                        }

                                        public void onAnimationEnd(Animator animator) {
                                            ObjectAnimator unused = MenuLayoutManager.this.mTitleFadeOutAnimator = null;
                                            if (!this.mCanceled) {
                                                ((MenuRowView) MenuLayoutManager.this.mMenuRowViews.get(nextPosition6)).onSelected(false);
                                            }
                                        }
                                    });
                                    MenuLayoutManager.this.mTitleFadeOutAnimator.start();
                                }
                            }
                        };
                        animatorSet.addListener(r8);
                        this.mAnimatorSet.start();
                        return;
                    }
                }
            } else {
                currentContentsView.requestLayout();
                this.mPendingSelectedPosition = i;
            }
        }
    }

    private void setTempTitleView(TextView dest, TextView src) {
        dest.setVisibility(0);
        dest.setText(src.getText());
        dest.setTranslationY(0.0f);
        if (src.getVisibility() == 0) {
            dest.setAlpha(src.getAlpha());
            dest.setScaleX(src.getScaleX());
            dest.setScaleY(src.getScaleY());
        } else {
            dest.setAlpha(0.0f);
            dest.setScaleX(1.0f);
            dest.setScaleY(1.0f);
        }
        View parent = (View) src.getParent();
        dest.setLeft(src.getLeft() + parent.getLeft());
        dest.setRight(src.getRight() + parent.getLeft());
        dest.setTop(src.getTop() + parent.getTop());
        dest.setBottom(src.getBottom() + parent.getTop());
    }

    public void onMenuRowUpdated() {
        int i = 0;
        if (this.mMenuView.getVisibility() != 0) {
            int count = this.mMenuRowViews.size();
            for (int i2 = 0; i2 < count; i2++) {
                this.mMenuRowViews.get(i2).setVisibility(this.mMenuRows.get(i2).isVisible() ? 0 : 8);
            }
            return;
        }
        List<Integer> addedRowViews = new ArrayList<>();
        List<Integer> removedRowViews = new ArrayList<>();
        Map<Integer, Integer> offsetsToMove = new HashMap<>();
        int added = 0;
        for (int i3 = this.mSelectedPosition - 1; i3 >= 0; i3--) {
            MenuRow row = this.mMenuRows.get(i3);
            MenuRowView view = this.mMenuRowViews.get(i3);
            if (row.isVisible() && (view.getVisibility() == 8 || this.mRemovingRowViews.contains(Integer.valueOf(i3)))) {
                addedRowViews.add(Integer.valueOf(i3));
                added++;
            } else if (!row.isVisible() && view.getVisibility() == 0) {
                removedRowViews.add(Integer.valueOf(i3));
                added--;
            } else if (added != 0) {
                offsetsToMove.put(Integer.valueOf(i3), Integer.valueOf(-added));
            }
        }
        int count2 = this.mMenuRowViews.size();
        int i4 = this.mSelectedPosition + 1;
        int added2 = 0;
        while (true) {
            int added3 = i4;
            if (added3 >= count2) {
                break;
            }
            MenuRow row2 = this.mMenuRows.get(added3);
            MenuRowView view2 = this.mMenuRowViews.get(added3);
            if (row2.isVisible() && (view2.getVisibility() == 8 || this.mRemovingRowViews.contains(Integer.valueOf(added3)))) {
                addedRowViews.add(Integer.valueOf(added3));
                added2++;
            } else if (!row2.isVisible() && view2.getVisibility() == 0) {
                removedRowViews.add(Integer.valueOf(added3));
                added2--;
            } else if (added2 != 0) {
                offsetsToMove.put(Integer.valueOf(added3), Integer.valueOf(added2));
            }
            i4 = added3 + 1;
        }
        if (addedRowViews.size() != 0 || removedRowViews.size() != 0) {
            if (this.mAnimatorSet != null) {
                this.mAnimatorSet.end();
            }
            if (this.mTitleFadeOutAnimator != null) {
                this.mTitleFadeOutAnimator.end();
            }
            this.mPropertyValuesAfterAnimation.clear();
            List<Animator> animators = new ArrayList<>();
            List<Rect> layouts = getViewLayouts(this.mMenuView.getLeft(), this.mMenuView.getTop(), this.mMenuView.getRight(), this.mMenuView.getBottom(), addedRowViews, removedRowViews);
            Iterator<Integer> it = addedRowViews.iterator();
            while (it.hasNext()) {
                int position = it.next().intValue();
                MenuRowView view3 = this.mMenuRowViews.get(position);
                view3.setVisibility(i);
                Rect rect = layouts.get(position);
                view3.layout(rect.left, rect.top, rect.right, rect.bottom);
                View titleView = view3.getTitleView();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleView.getLayoutParams();
                int i5 = position;
                titleView.layout(view3.getPaddingLeft() + params.leftMargin, view3.getPaddingTop() + params.topMargin, ((rect.right - rect.left) - view3.getPaddingRight()) - params.rightMargin, ((rect.bottom - rect.top) - view3.getPaddingBottom()) - params.bottomMargin);
                animators.add(createAlphaAnimator(view3, 0.0f, 1.0f, this.mFastOutLinearIn));
                it = it;
                layouts = layouts;
                i = 0;
            }
            for (Integer intValue : removedRowViews) {
                animators.add(createAlphaAnimator(this.mMenuRowViews.get(intValue.intValue()), 1.0f, 0.0f, 1.0f, this.mLinearOutSlowIn));
            }
            for (Map.Entry<Integer, Integer> entry : offsetsToMove.entrySet()) {
                animators.add(createTranslationYAnimator(this.mMenuRowViews.get(entry.getKey().intValue()), 0.0f, (float) (entry.getValue().intValue() * this.mRowTitleHeight)));
            }
            final List<ViewPropertyValueHolder> propertyValuesAfterAnimation = new ArrayList<>();
            propertyValuesAfterAnimation.addAll(this.mPropertyValuesAfterAnimation);
            this.mRemovingRowViews.clear();
            this.mRemovingRowViews.addAll(removedRowViews);
            this.mAnimatorSet = new AnimatorSet();
            this.mAnimatorSet.playTogether(animators);
            this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = MenuLayoutManager.this.mAnimatorSet = null;
                    for (ViewPropertyValueHolder holder : propertyValuesAfterAnimation) {
                        holder.property.set(holder.view, Float.valueOf(holder.value));
                    }
                    for (Integer intValue : MenuLayoutManager.this.mRemovingRowViews) {
                        ((MenuRowView) MenuLayoutManager.this.mMenuRowViews.get(intValue.intValue())).setVisibility(8);
                    }
                    MenuLayoutManager.this.layout(MenuLayoutManager.this.mMenuView.getLeft(), MenuLayoutManager.this.mMenuView.getTop(), MenuLayoutManager.this.mMenuView.getRight(), MenuLayoutManager.this.mMenuView.getBottom());
                }
            });
            this.mAnimatorSet.start();
        }
    }

    private ObjectAnimator createTranslationYAnimator(View view, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{from, to});
        animator.setDuration(this.mRowAnimationDuration);
        animator.setInterpolator(this.mFastOutSlowIn);
        this.mPropertyValuesAfterAnimation.add(new ViewPropertyValueHolder(View.TRANSLATION_Y, view, 0.0f));
        return animator;
    }

    /* access modifiers changed from: private */
    public ObjectAnimator createAlphaAnimator(View view, float from, float to, TimeInterpolator interpolator) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{from, to});
        animator.setDuration(this.mRowAnimationDuration);
        animator.setInterpolator(interpolator);
        return animator;
    }

    private ObjectAnimator createAlphaAnimator(View view, float from, float to, float end, TimeInterpolator interpolator) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{from, to});
        animator.setDuration(this.mRowAnimationDuration);
        animator.setInterpolator(interpolator);
        this.mPropertyValuesAfterAnimation.add(new ViewPropertyValueHolder(View.ALPHA, view, end));
        return animator;
    }

    private ObjectAnimator createScaleXAnimator(View view, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{from, to});
        animator.setDuration(this.mRowAnimationDuration);
        animator.setInterpolator(this.mFastOutSlowIn);
        return animator;
    }

    private ObjectAnimator createScaleYAnimator(View view, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{from, to});
        animator.setDuration(this.mRowAnimationDuration);
        animator.setInterpolator(this.mFastOutSlowIn);
        return animator;
    }

    public int getSelectedPosition() {
        return this.mSelectedPosition;
    }

    private static final class ViewPropertyValueHolder {
        public final Property<View, Float> property;
        public final float value;
        public final View view;

        public ViewPropertyValueHolder(Property<View, Float> property2, View view2, float value2) {
            this.property = property2;
            this.view = view2;
            this.value = value2;
        }
    }

    public void onMenuShow() {
    }

    public void onMenuHide() {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.end();
            this.mAnimatorSet = null;
        }
        if (this.mTitleFadeOutAnimator != null) {
            this.mTitleFadeOutAnimator.end();
            this.mTitleFadeOutAnimator = null;
        }
    }
}
