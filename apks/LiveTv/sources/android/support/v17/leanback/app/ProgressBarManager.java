package android.support.v17.leanback.app;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public final class ProgressBarManager {
    private static final long DEFAULT_PROGRESS_BAR_DELAY = 1000;
    boolean mEnableProgressBar = true;
    private Handler mHandler = new Handler();
    private long mInitialDelay = 1000;
    boolean mIsShowing;
    View mProgressBarView;
    boolean mUserProvidedProgressBar;
    ViewGroup rootView;
    private Runnable runnable = new Runnable() {
        public void run() {
            if (!ProgressBarManager.this.mEnableProgressBar) {
                return;
            }
            if ((!ProgressBarManager.this.mUserProvidedProgressBar && ProgressBarManager.this.rootView == null) || !ProgressBarManager.this.mIsShowing) {
                return;
            }
            if (ProgressBarManager.this.mProgressBarView == null) {
                ProgressBarManager.this.mProgressBarView = new ProgressBar(ProgressBarManager.this.rootView.getContext(), (AttributeSet) null, 16842874);
                FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(-2, -2);
                progressBarParams.gravity = 17;
                ProgressBarManager.this.rootView.addView(ProgressBarManager.this.mProgressBarView, progressBarParams);
            } else if (ProgressBarManager.this.mUserProvidedProgressBar) {
                ProgressBarManager.this.mProgressBarView.setVisibility(0);
            }
        }
    };

    public void setRootView(ViewGroup rootView2) {
        this.rootView = rootView2;
    }

    public void show() {
        if (this.mEnableProgressBar) {
            this.mIsShowing = true;
            this.mHandler.postDelayed(this.runnable, this.mInitialDelay);
        }
    }

    public void hide() {
        this.mIsShowing = false;
        if (this.mUserProvidedProgressBar) {
            this.mProgressBarView.setVisibility(4);
        } else if (this.mProgressBarView != null) {
            this.rootView.removeView(this.mProgressBarView);
            this.mProgressBarView = null;
        }
        this.mHandler.removeCallbacks(this.runnable);
    }

    public void setProgressBarView(View progressBarView) {
        if (progressBarView.getParent() != null) {
            this.mProgressBarView = progressBarView;
            this.mProgressBarView.setVisibility(4);
            this.mUserProvidedProgressBar = true;
            return;
        }
        throw new IllegalArgumentException("Must have a parent");
    }

    public long getInitialDelay() {
        return this.mInitialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.mInitialDelay = initialDelay;
    }

    public void disableProgressBar() {
        this.mEnableProgressBar = false;
    }

    public void enableProgressBar() {
        this.mEnableProgressBar = true;
    }
}
