package com.android.tv.settings.util;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.util.TypedValue;
import com.android.tv.settings.R;

public class TransitionUtils {
    public static ObjectAnimator createActivityFadeInAnimator(Resources res, boolean useFloats) {
        TypedValue startAlpha = new TypedValue();
        res.getValue(R.dimen.alpha_activity_in_bkg_start, startAlpha, true);
        TypedValue endAlpha = new TypedValue();
        res.getValue(R.dimen.alpha_activity_in_bkg_end, endAlpha, true);
        ObjectAnimator animator = new ObjectAnimator();
        animator.setPropertyName("alpha");
        if (useFloats) {
            animator.setFloatValues(new float[]{startAlpha.getFloat(), endAlpha.getFloat()});
        } else {
            animator.setIntValues(new int[]{Float.valueOf(startAlpha.getFloat() * 255.0f).intValue(), Float.valueOf(endAlpha.getFloat() * 255.0f).intValue()});
        }
        animator.setDuration((long) res.getInteger(R.integer.alpha_activity_in_bkg_duration));
        animator.setStartDelay((long) res.getInteger(R.integer.alpha_activity_in_bkg_delay));
        return animator;
    }

    public static ObjectAnimator createActivityFadeOutAnimator(Resources res, boolean useFloats) {
        TypedValue startAlpha = new TypedValue();
        res.getValue(R.dimen.alpha_activity_out_bkg_start, startAlpha, true);
        TypedValue endAlpha = new TypedValue();
        res.getValue(R.dimen.alpha_activity_out_bkg_end, endAlpha, true);
        ObjectAnimator animator = new ObjectAnimator();
        animator.setPropertyName("alpha");
        if (useFloats) {
            animator.setFloatValues(new float[]{startAlpha.getFloat(), endAlpha.getFloat()});
        } else {
            animator.setIntValues(new int[]{Float.valueOf(startAlpha.getFloat() * 255.0f).intValue(), Float.valueOf(endAlpha.getFloat() * 255.0f).intValue()});
        }
        animator.setDuration((long) res.getInteger(R.integer.alpha_activity_out_bkg_duration));
        animator.setStartDelay((long) res.getInteger(R.integer.alpha_activity_out_bkg_delay));
        return animator;
    }
}
