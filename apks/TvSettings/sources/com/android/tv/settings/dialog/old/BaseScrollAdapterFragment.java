package com.android.tv.settings.dialog.old;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import com.android.tv.settings.R;
import com.android.tv.settings.widget.ScrollAdapter;
import com.android.tv.settings.widget.ScrollAdapterView;

public class BaseScrollAdapterFragment implements ScrollAdapterView.OnScrollListener {
    private static final String STATE_SELECTION = "BaseScrollAdapterFragment.selection";
    private ScrollAdapter mAdapter;
    private int mAnimationDuration;
    /* access modifiers changed from: private */
    public volatile boolean mFadedOut = true;
    private final LiteFragment mFragment;
    private ScrollAdapterView mScrollAdapterView;
    private View mSelectedView = null;
    private View mSelectorView;

    public BaseScrollAdapterFragment(LiteFragment fragment) {
        this.mFragment = fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_list, container, false);
        this.mScrollAdapterView = null;
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.mScrollAdapterView != null) {
            outState.putInt(STATE_SELECTION, getSelectedItemPosition());
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        ensureList();
        if (savedInstanceState != null) {
            setSelection(savedInstanceState.getInt(STATE_SELECTION, 0));
        }
    }

    public boolean hasCreatedView() {
        return (this.mFragment == null || this.mFragment.getView() == null) ? false : true;
    }

    public ScrollAdapterView getScrollAdapterView() {
        ensureList();
        return this.mScrollAdapterView;
    }

    public ScrollAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(ScrollAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mScrollAdapterView != null) {
            this.mScrollAdapterView.setAdapter(this.mAdapter);
        }
    }

    public void setSelection(int position) {
        this.mScrollAdapterView.setSelection(position);
    }

    public void setSelectionSmooth(int position) {
        this.mScrollAdapterView.setSelectionSmooth(position);
    }

    public int getSelectedItemPosition() {
        return this.mScrollAdapterView.getSelectedItemPosition();
    }

    public void ensureList() {
        if (this.mScrollAdapterView == null) {
            View root = this.mFragment.getView();
            if (root != null) {
                if (root instanceof ScrollAdapterView) {
                    this.mScrollAdapterView = (ScrollAdapterView) root;
                    this.mSelectorView = null;
                } else {
                    this.mScrollAdapterView = (ScrollAdapterView) root.findViewById(R.id.list);
                    if (this.mScrollAdapterView != null) {
                        this.mSelectorView = root.findViewById(R.id.selector);
                    } else {
                        throw new IllegalStateException("No scroll adapter view exists.");
                    }
                }
                if (this.mScrollAdapterView != null) {
                    this.mScrollAdapterView.requestFocusFromTouch();
                    if (this.mAdapter != null) {
                        this.mScrollAdapterView.setAdapter(this.mAdapter);
                    }
                    if (this.mSelectorView != null) {
                        this.mAnimationDuration = this.mFragment.getActivity().getResources().getInteger(R.integer.dialog_animation_duration);
                        this.mScrollAdapterView.addOnScrollListener(this);
                        return;
                    }
                    return;
                }
                return;
            }
            throw new IllegalStateException("Content view not created yet.");
        }
    }

    private class Listener implements Animator.AnimatorListener {
        private boolean mCanceled;
        private final boolean mFadingOut;

        public Listener(boolean fadingOut) {
            this.mFadingOut = fadingOut;
        }

        public void onAnimationStart(Animator animation) {
            if (!this.mFadingOut) {
                boolean unused = BaseScrollAdapterFragment.this.mFadedOut = false;
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mCanceled && this.mFadingOut) {
                boolean unused = BaseScrollAdapterFragment.this.mFadedOut = true;
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    public synchronized void onScrolled(View view, int position, float mainPosition, float secondPosition) {
        if (((double) mainPosition) == 0.0d) {
            if (view != null) {
                int selectorHeight = this.mSelectorView.getHeight();
                if (selectorHeight == 0) {
                    ViewGroup.LayoutParams lp = this.mSelectorView.getLayoutParams();
                    int dimensionPixelSize = this.mFragment.getActivity().getResources().getDimensionPixelSize(R.dimen.action_fragment_selector_min_height);
                    selectorHeight = dimensionPixelSize;
                    lp.height = dimensionPixelSize;
                    this.mSelectorView.setLayoutParams(lp);
                }
                float scaleY = ((float) view.getHeight()) / ((float) selectorHeight);
                ViewPropertyAnimator animation = this.mSelectorView.animate().alpha(1.0f).setListener(new Listener(false)).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f));
                if (this.mFadedOut) {
                    this.mSelectorView.setScaleY(scaleY);
                } else {
                    animation.scaleY(scaleY);
                }
                animation.start();
                this.mSelectedView = view;
            } else {
                ViewGroup.LayoutParams lp2 = this.mSelectorView.getLayoutParams();
                lp2.height = 0;
                this.mSelectorView.setLayoutParams(lp2);
            }
        } else if (this.mSelectedView != null) {
            this.mSelectorView.animate().alpha(0.0f).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new Listener(true)).start();
            this.mSelectedView = null;
        }
    }
}
