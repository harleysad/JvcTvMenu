package android.support.v17.leanback.widget;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import java.util.List;

final class DetailsOverviewSharedElementHelper extends SharedElementCallback {
    static final boolean DEBUG = false;
    static final String TAG = "DetailsTransitionHelper";
    Activity mActivityToRunTransition;
    int mRightPanelHeight;
    int mRightPanelWidth;
    private Matrix mSavedMatrix;
    private ImageView.ScaleType mSavedScaleType;
    String mSharedElementName;
    boolean mStartedPostpone;
    DetailsOverviewRowPresenter.ViewHolder mViewHolder;

    DetailsOverviewSharedElementHelper() {
    }

    static class TransitionTimeOutRunnable implements Runnable {
        WeakReference<DetailsOverviewSharedElementHelper> mHelperRef;

        TransitionTimeOutRunnable(DetailsOverviewSharedElementHelper helper) {
            this.mHelperRef = new WeakReference<>(helper);
        }

        public void run() {
            DetailsOverviewSharedElementHelper helper = (DetailsOverviewSharedElementHelper) this.mHelperRef.get();
            if (helper != null) {
                helper.startPostponedEnterTransition();
            }
        }
    }

    private boolean hasImageViewScaleChange(View snapshotView) {
        return snapshotView instanceof ImageView;
    }

    private void saveImageViewScale() {
        if (this.mSavedScaleType == null) {
            ImageView imageView = this.mViewHolder.mImageView;
            this.mSavedScaleType = imageView.getScaleType();
            this.mSavedMatrix = this.mSavedScaleType == ImageView.ScaleType.MATRIX ? imageView.getMatrix() : null;
        }
    }

    private static void updateImageViewAfterScaleTypeChange(ImageView imageView) {
        imageView.measure(View.MeasureSpec.makeMeasureSpec(imageView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(imageView.getMeasuredHeight(), 1073741824));
        imageView.layout(imageView.getLeft(), imageView.getTop(), imageView.getRight(), imageView.getBottom());
    }

    private void changeImageViewScale(View snapshotView) {
        ImageView snapshotImageView = (ImageView) snapshotView;
        ImageView imageView = this.mViewHolder.mImageView;
        imageView.setScaleType(snapshotImageView.getScaleType());
        if (snapshotImageView.getScaleType() == ImageView.ScaleType.MATRIX) {
            imageView.setImageMatrix(snapshotImageView.getImageMatrix());
        }
        updateImageViewAfterScaleTypeChange(imageView);
    }

    private void restoreImageViewScale() {
        if (this.mSavedScaleType != null) {
            ImageView imageView = this.mViewHolder.mImageView;
            imageView.setScaleType(this.mSavedScaleType);
            if (this.mSavedScaleType == ImageView.ScaleType.MATRIX) {
                imageView.setImageMatrix(this.mSavedMatrix);
            }
            this.mSavedScaleType = null;
            updateImageViewAfterScaleTypeChange(imageView);
        }
    }

    public void onSharedElementStart(List<String> list, List<View> sharedElements, List<View> sharedElementSnapshots) {
        if (sharedElements.size() >= 1) {
            View overviewView = sharedElements.get(0);
            if (this.mViewHolder != null && this.mViewHolder.mOverviewFrame == overviewView) {
                View snapshot = sharedElementSnapshots.get(0);
                if (hasImageViewScaleChange(snapshot)) {
                    saveImageViewScale();
                    changeImageViewScale(snapshot);
                }
                View imageView = this.mViewHolder.mImageView;
                int width = overviewView.getWidth();
                int height = overviewView.getHeight();
                imageView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                imageView.layout(0, 0, width, height);
                View rightPanel = this.mViewHolder.mRightPanel;
                if (this.mRightPanelWidth == 0 || this.mRightPanelHeight == 0) {
                    rightPanel.offsetLeftAndRight(width - rightPanel.getLeft());
                } else {
                    rightPanel.measure(View.MeasureSpec.makeMeasureSpec(this.mRightPanelWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mRightPanelHeight, 1073741824));
                    rightPanel.layout(width, rightPanel.getTop(), this.mRightPanelWidth + width, rightPanel.getTop() + this.mRightPanelHeight);
                }
                this.mViewHolder.mActionsRow.setVisibility(4);
                this.mViewHolder.mDetailsDescriptionFrame.setVisibility(4);
            }
        }
    }

    public void onSharedElementEnd(List<String> list, List<View> sharedElements, List<View> list2) {
        if (sharedElements.size() >= 1) {
            View overviewView = sharedElements.get(0);
            if (this.mViewHolder != null && this.mViewHolder.mOverviewFrame == overviewView) {
                restoreImageViewScale();
                this.mViewHolder.mActionsRow.setDescendantFocusability(131072);
                this.mViewHolder.mActionsRow.setVisibility(0);
                this.mViewHolder.mActionsRow.setDescendantFocusability(262144);
                this.mViewHolder.mActionsRow.requestFocus();
                this.mViewHolder.mDetailsDescriptionFrame.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setSharedElementEnterTransition(Activity activity, String sharedElementName, long timeoutMs) {
        if ((activity == null && !TextUtils.isEmpty(sharedElementName)) || (activity != null && TextUtils.isEmpty(sharedElementName))) {
            throw new IllegalArgumentException();
        } else if (activity != this.mActivityToRunTransition || !TextUtils.equals(sharedElementName, this.mSharedElementName)) {
            if (this.mActivityToRunTransition != null) {
                ActivityCompat.setEnterSharedElementCallback(this.mActivityToRunTransition, (SharedElementCallback) null);
            }
            this.mActivityToRunTransition = activity;
            this.mSharedElementName = sharedElementName;
            ActivityCompat.setEnterSharedElementCallback(this.mActivityToRunTransition, this);
            ActivityCompat.postponeEnterTransition(this.mActivityToRunTransition);
            if (timeoutMs > 0) {
                new Handler().postDelayed(new TransitionTimeOutRunnable(this), timeoutMs);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onBindToDrawable(DetailsOverviewRowPresenter.ViewHolder vh) {
        if (this.mViewHolder != null) {
            ViewCompat.setTransitionName(this.mViewHolder.mOverviewFrame, (String) null);
        }
        this.mViewHolder = vh;
        this.mViewHolder.mRightPanel.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.removeOnLayoutChangeListener(this);
                DetailsOverviewSharedElementHelper.this.mRightPanelWidth = DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.getWidth();
                DetailsOverviewSharedElementHelper.this.mRightPanelHeight = DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.getHeight();
            }
        });
        this.mViewHolder.mRightPanel.postOnAnimation(new Runnable() {
            public void run() {
                ViewCompat.setTransitionName(DetailsOverviewSharedElementHelper.this.mViewHolder.mOverviewFrame, DetailsOverviewSharedElementHelper.this.mSharedElementName);
                Object transition = TransitionHelper.getSharedElementEnterTransition(DetailsOverviewSharedElementHelper.this.mActivityToRunTransition.getWindow());
                if (transition != null) {
                    TransitionHelper.addTransitionListener(transition, new TransitionListener() {
                        public void onTransitionEnd(Object transition) {
                            if (DetailsOverviewSharedElementHelper.this.mViewHolder.mActionsRow.isFocused()) {
                                DetailsOverviewSharedElementHelper.this.mViewHolder.mActionsRow.requestFocus();
                            }
                            TransitionHelper.removeTransitionListener(transition, this);
                        }
                    });
                }
                DetailsOverviewSharedElementHelper.this.startPostponedEnterTransition();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void startPostponedEnterTransition() {
        if (!this.mStartedPostpone) {
            ActivityCompat.startPostponedEnterTransition(this.mActivityToRunTransition);
            this.mStartedPostpone = true;
        }
    }
}
