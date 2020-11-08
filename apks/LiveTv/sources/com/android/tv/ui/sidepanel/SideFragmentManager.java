package com.android.tv.ui.sidepanel;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.tvcenter.R;

public class SideFragmentManager implements AccessibilityManager.AccessibilityStateChangeListener {
    private static final String FIRST_BACKSTACK_RECORD_NAME = "0";
    private final Activity mActivity;
    private int mFragmentCount;
    private final FragmentManager mFragmentManager = this.mActivity.getFragmentManager();
    private final Animator mHideAnimator;
    /* access modifiers changed from: private */
    public final View mPanel;
    private final Runnable mPostHideRunnable;
    /* access modifiers changed from: private */
    public final Runnable mPreShowRunnable;
    /* access modifiers changed from: private */
    public final Animator mShowAnimator;
    private final long mShowDurationMillis;
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnGlobalLayoutListener mShowOnGlobalLayoutListener;

    public SideFragmentManager(Activity activity, Runnable preShowRunnable, Runnable postHideRunnable) {
        this.mActivity = activity;
        this.mPreShowRunnable = preShowRunnable;
        this.mPostHideRunnable = postHideRunnable;
        this.mPanel = this.mActivity.findViewById(R.id.side_panel);
        this.mShowAnimator = AnimatorInflater.loadAnimator(this.mActivity, R.animator.side_panel_enter);
        this.mShowAnimator.setTarget(this.mPanel);
        this.mHideAnimator = AnimatorInflater.loadAnimator(this.mActivity, R.animator.side_panel_exit);
        this.mHideAnimator.setTarget(this.mPanel);
        this.mHideAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                SideFragmentManager.this.hideAllInternal();
            }
        });
        this.mShowDurationMillis = (long) this.mActivity.getResources().getInteger(R.integer.side_panel_show_duration);
    }

    public int getCount() {
        return this.mFragmentCount;
    }

    public void setCount(int count) {
        this.mFragmentCount = count;
    }

    public boolean isActive() {
        return this.mFragmentCount != 0 && !isHiding();
    }

    public boolean isHiding() {
        return this.mHideAnimator.isStarted();
    }

    public void setVisibility(int visibility) {
        this.mPanel.setVisibility(visibility);
    }

    public void show(SideFragment sideFragment) {
        show(sideFragment, true);
    }

    public void show(SideFragment sideFragment, boolean showEnterAnimation) {
        if (isHiding()) {
            this.mHideAnimator.end();
        }
        boolean isFirst = this.mFragmentCount == 0;
        Log.d("SideFragmentManager", "show() isFirst :" + isFirst);
        FragmentTransaction ft = this.mFragmentManager.beginTransaction();
        if (!isFirst) {
            ft.setCustomAnimations(showEnterAnimation ? R.animator.side_panel_fragment_enter : 0, R.animator.side_panel_fragment_exit, R.animator.side_panel_fragment_pop_enter, R.animator.side_panel_fragment_pop_exit);
        }
        Log.d("SideFragmentManager", "show() active" + this.mActivity);
        ft.replace(R.id.side_fragment_container, sideFragment);
        ft.addToBackStack(Integer.toString(this.mFragmentCount));
        Log.d("SideFragmentManager", "show() active" + this.mActivity);
        ft.commit();
        this.mFragmentCount = this.mFragmentCount + 1;
        if (isFirst) {
            this.mPanel.setVisibility(0);
            this.mShowOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    SideFragmentManager.this.mPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ViewTreeObserver.OnGlobalLayoutListener unused = SideFragmentManager.this.mShowOnGlobalLayoutListener = null;
                    if (SideFragmentManager.this.mPreShowRunnable != null) {
                        SideFragmentManager.this.mPreShowRunnable.run();
                    }
                    SideFragmentManager.this.mShowAnimator.start();
                }
            };
            this.mPanel.getViewTreeObserver().addOnGlobalLayoutListener(this.mShowOnGlobalLayoutListener);
        }
        scheduleHideAll();
        Log.d("SideFragmentManager", "show() mFragmentCount :" + this.mFragmentCount);
    }

    public void popSideFragment() {
        this.mFragmentCount--;
        Log.d("SideFragmentManager", "popSideFragment() mFragmentCount :" + this.mFragmentCount);
        if (isActive()) {
            if (this.mFragmentCount == 0) {
                hideAll(true);
            } else {
                this.mFragmentManager.popBackStack();
            }
        }
    }

    public void popStackNumber() {
        this.mFragmentCount--;
        Log.d("SideFragmentManager", "popStackNumber() mFragmentCount :" + this.mFragmentCount);
        if (this.mFragmentCount == 0) {
            Log.d("SideFragmentManager", "popStackNumber() true ");
            hideAll(true);
            ((LiveTvSetting) this.mActivity).show();
        }
    }

    public void hideAll(boolean withAnimation) {
        if (this.mShowAnimator.isStarted()) {
            this.mShowAnimator.end();
        }
        if (this.mShowOnGlobalLayoutListener != null) {
            this.mPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this.mShowOnGlobalLayoutListener);
            this.mShowOnGlobalLayoutListener = null;
            if (this.mPreShowRunnable != null) {
                this.mPreShowRunnable.run();
            }
        }
        if (withAnimation) {
            if (!isHiding()) {
                this.mHideAnimator.start();
            }
        } else if (isHiding()) {
            this.mHideAnimator.end();
        } else {
            hideAllInternal();
        }
    }

    /* access modifiers changed from: private */
    public void hideAllInternal() {
        Log.d("SideFragmentManager", "hideAllInternal() mFragmentCount :" + this.mFragmentCount);
        if (this.mFragmentCount != 0) {
            this.mPanel.setVisibility(8);
            this.mFragmentManager.popBackStack(FIRST_BACKSTACK_RECORD_NAME, 1);
            this.mFragmentCount = 0;
            if (this.mPostHideRunnable != null) {
                this.mPostHideRunnable.run();
            }
            Log.d("SideFragmentManager", "hideAllInternal() mFragmentCount :" + this.mFragmentCount);
        }
    }

    public void showSidePanel(boolean withAnimation) {
        if (this.mFragmentCount != 0) {
            this.mPanel.setVisibility(0);
            if (withAnimation) {
                this.mShowAnimator.start();
            }
            scheduleHideAll();
        }
    }

    public void hideSidePanel(boolean withAnimation) {
        if (withAnimation) {
            Animator hideAnimator = AnimatorInflater.loadAnimator(this.mActivity, R.animator.side_panel_exit);
            hideAnimator.setTarget(this.mPanel);
            hideAnimator.start();
            hideAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    SideFragmentManager.this.mPanel.setVisibility(8);
                }
            });
            return;
        }
        this.mPanel.setVisibility(8);
    }

    public boolean isSidePanelVisible() {
        return this.mPanel.getVisibility() == 0;
    }

    public void scheduleHideAll() {
    }

    public boolean isHideKeyForCurrentPanel(int keyCode) {
        SideFragment current;
        if (!isActive() || (current = (SideFragment) this.mFragmentManager.findFragmentById(R.id.side_fragment_container)) == null || !current.isHideKeyForThisPanel(keyCode)) {
            return false;
        }
        return true;
    }

    public void onAccessibilityStateChanged(boolean enabled) {
    }
}
