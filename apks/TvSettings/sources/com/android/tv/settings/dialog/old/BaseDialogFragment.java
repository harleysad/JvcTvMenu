package com.android.tv.settings.dialog.old;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.old.ActionAdapter;
import com.android.tv.settings.widget.FrameLayoutWithShadows;

public class BaseDialogFragment {
    private static final int ANIMATE_DELAY = 550;
    private static final int ANIMATE_IN_DURATION = 250;
    private static final int SLIDE_IN_DISTANCE = 120;
    public static final String TAG_ACTION = "action";
    public static final String TAG_CONTENT = "content";
    public int mActionAreaId = R.id.action_fragment;
    /* access modifiers changed from: private */
    public ColorDrawable mBgDrawable = new ColorDrawable();
    public int mContentAreaId = R.id.content_fragment;
    public boolean mFirstOnStart = true;
    /* access modifiers changed from: private */
    public final LiteFragment mFragment;
    private boolean mIntroAnimationInProgress = false;
    /* access modifiers changed from: private */
    public FrameLayoutWithShadows mShadowLayer;

    public BaseDialogFragment(LiteFragment fragment) {
        this.mFragment = fragment;
    }

    public void onActionClicked(Activity activity, Action action) {
        if (activity instanceof ActionAdapter.Listener) {
            ((ActionAdapter.Listener) activity).onActionClicked(action);
            return;
        }
        Intent intent = action.getIntent();
        if (intent != null) {
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public void disableEntryAnimation() {
        this.mFirstOnStart = false;
    }

    public void setLayoutProperties(int contentAreaId, int actionAreaId) {
        this.mContentAreaId = contentAreaId;
        this.mActionAreaId = actionAreaId;
    }

    public void performEntryTransition(Activity activity, ViewGroup contentView, ImageView icon, TextView title, TextView description, TextView breadcrumb) {
        ViewGroup viewGroup = contentView;
        ViewGroup twoPane = (ViewGroup) viewGroup.getChildAt(0);
        twoPane.setVisibility(4);
        this.mIntroAnimationInProgress = true;
        Activity activity2 = activity;
        activity2.overridePendingTransition(R.anim.hard_cut_in, R.anim.fade_out);
        this.mBgDrawable.setColor(this.mFragment.getActivity().getColor(R.color.dialog_activity_background));
        this.mBgDrawable.setAlpha(0);
        twoPane.setBackground(this.mBgDrawable);
        this.mShadowLayer = (FrameLayoutWithShadows) twoPane.findViewById(R.id.shadow_layout);
        final ViewGroup viewGroup2 = twoPane;
        final Activity activity3 = activity2;
        final ViewGroup viewGroup3 = viewGroup;
        final TextView textView = title;
        final TextView textView2 = breadcrumb;
        final TextView textView3 = description;
        final ImageView imageView = icon;
        twoPane.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            final Runnable mEntryAnimationRunnable = new Runnable() {
                public void run() {
                    int endDist;
                    if (BaseDialogFragment.this.mFragment.isAdded()) {
                        boolean isRtl = false;
                        viewGroup2.setVisibility(0);
                        ObjectAnimator oa = ObjectAnimator.ofInt(BaseDialogFragment.this.mBgDrawable, "alpha", new int[]{255});
                        oa.setDuration(250);
                        oa.setStartDelay(120);
                        oa.setInterpolator(new DecelerateInterpolator(1.0f));
                        oa.start();
                        View actionFragmentView = activity3.findViewById(BaseDialogFragment.this.mActionAreaId);
                        if (ViewCompat.getLayoutDirection(viewGroup3) == 1) {
                            isRtl = true;
                        }
                        int startDist = isRtl ? 120 : -120;
                        if (isRtl) {
                            endDist = -actionFragmentView.getMeasuredWidth();
                        } else {
                            endDist = actionFragmentView.getMeasuredWidth();
                        }
                        BaseDialogFragment.this.prepareAndAnimateView(textView, 0.0f, (float) startDist, 120, 250, new DecelerateInterpolator(1.0f), false);
                        BaseDialogFragment.this.prepareAndAnimateView(textView2, 0.0f, (float) startDist, 120, 250, new DecelerateInterpolator(1.0f), false);
                        BaseDialogFragment.this.prepareAndAnimateView(textView3, 0.0f, (float) startDist, 120, 250, new DecelerateInterpolator(1.0f), false);
                        BaseDialogFragment.this.prepareAndAnimateView(actionFragmentView, 0.0f, (float) endDist, 120, 250, new DecelerateInterpolator(1.0f), false);
                        if (imageView != null) {
                            BaseDialogFragment.this.prepareAndAnimateView(imageView, 0.0f, (float) startDist, 120, 250, new DecelerateInterpolator(1.0f), true);
                            if (BaseDialogFragment.this.mShadowLayer != null) {
                                BaseDialogFragment.this.mShadowLayer.setShadowsAlpha(0.0f);
                            }
                        }
                    }
                }
            };

            public void onGlobalLayout() {
                viewGroup2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                viewGroup2.postOnAnimationDelayed(this.mEntryAnimationRunnable, 550);
            }
        });
    }

    public void prepareAndAnimateView(final View v, float initAlpha, float initTransX, int delay, int duration, Interpolator interpolator, final boolean isIcon) {
        if (v != null && v.getWindowToken() != null) {
            v.setLayerType(2, (Paint) null);
            v.buildLayer();
            v.setAlpha(initAlpha);
            v.setTranslationX(initTransX);
            v.animate().alpha(1.0f).translationX(0.0f).setDuration((long) duration).setStartDelay((long) delay);
            if (interpolator != null) {
                v.animate().setInterpolator(interpolator);
            }
            v.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    v.setLayerType(0, (Paint) null);
                    if (isIcon) {
                        if (BaseDialogFragment.this.mShadowLayer != null) {
                            BaseDialogFragment.this.mShadowLayer.setShadowsAlpha(1.0f);
                        }
                        BaseDialogFragment.this.onIntroAnimationFinished();
                    }
                }
            });
            v.animate().start();
        }
    }

    public void onIntroAnimationFinished() {
        this.mIntroAnimationInProgress = false;
    }

    public boolean isIntroAnimationInProgress() {
        return this.mIntroAnimationInProgress;
    }

    public ColorDrawable getBackgroundDrawable() {
        return this.mBgDrawable;
    }

    public void setBackgroundDrawable(ColorDrawable drawable) {
        this.mBgDrawable = drawable;
    }
}
