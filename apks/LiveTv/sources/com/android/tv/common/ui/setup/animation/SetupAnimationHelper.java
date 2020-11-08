package com.android.tv.common.ui.setup.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.ImageView;
import com.mediatek.wwtv.tvcenter.R;
import java.util.Iterator;

public final class SetupAnimationHelper {
    private static final float ANIMATION_TIME_SCALE = 1.0f;
    public static final long DELAY_BETWEEN_SIBLINGS_MS = applyAnimationTimeScale(33);
    /* access modifiers changed from: private */
    public static long sFragmentTransitionDuration;
    /* access modifiers changed from: private */
    public static int sFragmentTransitionLongDistance;
    private static int sFragmentTransitionShortDistance;
    private static boolean sInitialized;

    private SetupAnimationHelper() {
    }

    public static void initialize(Context context) {
        if (!sInitialized) {
            sFragmentTransitionDuration = (long) context.getResources().getInteger(R.integer.setup_fragment_transition_duration);
            sFragmentTransitionLongDistance = context.getResources().getDimensionPixelOffset(R.dimen.setup_fragment_transition_long_distance);
            sFragmentTransitionShortDistance = context.getResources().getDimensionPixelOffset(R.dimen.setup_fragment_transition_short_distance);
            sInitialized = true;
        }
    }

    /* access modifiers changed from: private */
    public static void checkInitialized() {
        if (!sInitialized) {
            throw new IllegalStateException("SetupAnimationHelper not initialized");
        }
    }

    public static class TransitionBuilder {
        private final int mDistance = SetupAnimationHelper.sFragmentTransitionLongDistance;
        private long mDuration = SetupAnimationHelper.sFragmentTransitionDuration;
        private int[] mExcludeIds;
        private int[] mParentIdForDelay;
        private int mSlideEdge = GravityCompat.START;

        public TransitionBuilder() {
            SetupAnimationHelper.checkInitialized();
        }

        public TransitionBuilder setSlideEdge(int slideEdge) {
            this.mSlideEdge = slideEdge;
            return this;
        }

        public TransitionBuilder setDuration(long duration) {
            this.mDuration = duration;
            return this;
        }

        public TransitionBuilder setParentIdsForDelay(int[] parentIdForDelay) {
            this.mParentIdForDelay = parentIdForDelay;
            return this;
        }

        public TransitionBuilder setExcludeIds(int[] excludeIds) {
            this.mExcludeIds = excludeIds;
            return this;
        }

        public Transition build() {
            FadeAndShortSlide transition = new FadeAndShortSlide(this.mSlideEdge, this.mParentIdForDelay);
            transition.setDistance(this.mDistance);
            transition.setDuration(this.mDuration);
            if (this.mExcludeIds != null) {
                for (int id : this.mExcludeIds) {
                    transition.excludeTarget(id, true);
                }
            }
            return transition;
        }
    }

    public static void setLongDistance(FadeAndShortSlide transition) {
        checkInitialized();
        transition.setDistance(sFragmentTransitionLongDistance);
    }

    public static void setShortDistance(FadeAndShortSlide transition) {
        checkInitialized();
        transition.setDistance(sFragmentTransitionShortDistance);
    }

    public static Animator applyAnimationTimeScale(Animator animator) {
        if (animator instanceof AnimatorSet) {
            Iterator<Animator> it = ((AnimatorSet) animator).getChildAnimations().iterator();
            while (it.hasNext()) {
                applyAnimationTimeScale(it.next());
            }
        }
        if (animator.getDuration() > 0) {
            animator.setDuration((long) (((float) animator.getDuration()) * 1.0f));
        }
        animator.setStartDelay((long) (((float) animator.getStartDelay()) * 1.0f));
        return animator;
    }

    public static Transition applyAnimationTimeScale(Transition transition) {
        if (transition instanceof TransitionSet) {
            TransitionSet set = (TransitionSet) transition;
            int count = set.getTransitionCount();
            for (int i = 0; i < count; i++) {
                applyAnimationTimeScale(set.getTransitionAt(i));
            }
        }
        if (transition.getDuration() > 0) {
            transition.setDuration((long) (((float) transition.getDuration()) * 1.0f));
        }
        transition.setStartDelay((long) (((float) transition.getStartDelay()) * 1.0f));
        return transition;
    }

    public static long applyAnimationTimeScale(long time) {
        return (long) (((float) time) * 1.0f);
    }

    public static ObjectAnimator createFrameAnimator(ImageView imageView, int[] frames) {
        return createFrameAnimatorWithDelay(imageView, frames, 0);
    }

    public static ObjectAnimator createFrameAnimatorWithDelay(ImageView imageView, int[] frames, long startDelay) {
        ObjectAnimator animator = ObjectAnimator.ofInt(imageView, "imageResource", frames);
        animator.setDuration((long) ((frames.length * 1000) / 60));
        animator.setInterpolator((TimeInterpolator) null);
        animator.setStartDelay(startDelay);
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return startValue;
            }
        });
        return animator;
    }

    public static Animator createFadeOutAnimator(final View view, long duration, boolean makeVisibleAfterAnimation) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}).setDuration(duration);
        if (makeVisibleAfterAnimation) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    view.setAlpha(1.0f);
                }
            });
        }
        return animator;
    }
}
