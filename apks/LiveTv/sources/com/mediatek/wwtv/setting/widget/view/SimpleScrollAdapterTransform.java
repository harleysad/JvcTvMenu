package com.mediatek.wwtv.setting.widget.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import java.util.ArrayList;

public class SimpleScrollAdapterTransform implements ScrollAdapterTransform {
    private DisplayMetrics mDisplayMetrics;
    private Animator mHighItemTransform;
    private Animator mLowItemTransform;

    public SimpleScrollAdapterTransform(Context context) {
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    public void transform(View child, int distanceFromCenter, int distanceFromCenter2ndAxis) {
        if (this.mLowItemTransform != null || this.mHighItemTransform != null) {
            int absDistance = Math.abs(distanceFromCenter) + Math.abs(distanceFromCenter2ndAxis);
            if (distanceFromCenter < 0) {
                applyTransformationRecursive(absDistance, this.mLowItemTransform, child);
            } else {
                applyTransformationRecursive(absDistance, this.mHighItemTransform, child);
            }
        }
    }

    private void applyTransformationRecursive(int distanceFromCenter, Animator animator, View child) {
        if (animator instanceof AnimatorSet) {
            ArrayList<Animator> children = ((AnimatorSet) animator).getChildAnimations();
            for (int i = children.size() - 1; i >= 0; i--) {
                applyTransformationRecursive(distanceFromCenter, children.get(i), child);
            }
        } else if (animator instanceof ValueAnimator) {
            ValueAnimator valueAnim = (ValueAnimator) animator;
            valueAnim.setTarget(child);
            long duration = valueAnim.getDuration();
            if (((long) distanceFromCenter) < duration) {
                valueAnim.setCurrentPlayTime((long) distanceFromCenter);
            } else {
                valueAnim.setCurrentPlayTime(duration);
            }
        }
    }

    private void initializeTransformationRecursive(Animator animator, long defaultDuration) {
        long duration = animator.getDuration();
        if (duration == 0) {
            duration = defaultDuration;
        }
        if (animator instanceof AnimatorSet) {
            ArrayList<Animator> children = ((AnimatorSet) animator).getChildAnimations();
            int i = children.size() - 1;
            while (true) {
                int i2 = i;
                if (i2 >= 0) {
                    initializeTransformationRecursive(children.get(i2), duration);
                    i = i2 - 1;
                } else {
                    return;
                }
            }
        } else if (animator instanceof ValueAnimator) {
            ((ValueAnimator) animator).setDuration((long) TypedValue.applyDimension(1, (float) duration, this.mDisplayMetrics));
        }
    }

    public Animator getHighItemTransform() {
        return this.mHighItemTransform;
    }

    public void setHighItemTransform(Animator highItemTransform) {
        this.mHighItemTransform = highItemTransform;
        initializeTransformationRecursive(this.mHighItemTransform, 0);
    }

    public Animator getLowItemTransform() {
        return this.mLowItemTransform;
    }

    public void setLowItemTransform(Animator lowItemTransform) {
        this.mLowItemTransform = lowItemTransform;
        initializeTransformationRecursive(this.mLowItemTransform, 0);
    }
}
