package com.android.tv.common.ui.setup.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import com.mediatek.wwtv.tvcenter.R;

class TranslationAnimationCreator {
    TranslationAnimationCreator() {
    }

    static Animator createAnimation(View view, TransitionValues values, int viewPosX, float startX, float endX, TimeInterpolator interpolator, Transition transition) {
        float startX2;
        View view2 = view;
        TransitionValues transitionValues = values;
        float f = endX;
        float terminalX = view.getTranslationX();
        Integer startPosition = (Integer) transitionValues.view.getTag(R.id.transitionPosition);
        if (startPosition != null) {
            startX2 = ((float) (startPosition.intValue() - viewPosX)) + terminalX;
        } else {
            startX2 = startX;
        }
        int startPosX = viewPosX + Math.round(startX2 - terminalX);
        view2.setTranslationX(startX2);
        if (startX2 == f) {
            return null;
        }
        Path path = new Path();
        path.moveTo(startX2, 0.0f);
        path.lineTo(f, 0.0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(view2, View.TRANSLATION_X, View.TRANSLATION_Y, path);
        TransitionPositionListener listener = new TransitionPositionListener(view2, transitionValues.view, startPosX, terminalX);
        transition.addListener(listener);
        anim.addListener(listener);
        anim.addPauseListener(listener);
        anim.setInterpolator(interpolator);
        return anim;
    }

    private static class TransitionPositionListener extends AnimatorListenerAdapter implements Transition.TransitionListener {
        private final View mMovingView;
        private float mPausedX;
        private final int mStartX;
        private final float mTerminalX;
        private Integer mTransitionPosition;
        private final View mViewInHierarchy;

        private TransitionPositionListener(View movingView, View viewInHierarchy, int startX, float terminalX) {
            this.mMovingView = movingView;
            this.mViewInHierarchy = viewInHierarchy;
            this.mStartX = startX - Math.round(this.mMovingView.getTranslationX());
            this.mTerminalX = terminalX;
            this.mTransitionPosition = (Integer) this.mViewInHierarchy.getTag(R.id.transitionPosition);
            if (this.mTransitionPosition != null) {
                this.mViewInHierarchy.setTag(R.id.transitionPosition, (Object) null);
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.mTransitionPosition = Integer.valueOf(Math.round(((float) this.mStartX) + this.mMovingView.getTranslationX()));
            this.mViewInHierarchy.setTag(R.id.transitionPosition, this.mTransitionPosition);
        }

        public void onAnimationEnd(Animator animator) {
        }

        public void onAnimationPause(Animator animator) {
            this.mPausedX = this.mMovingView.getTranslationX();
            this.mMovingView.setTranslationX(this.mTerminalX);
        }

        public void onAnimationResume(Animator animator) {
            this.mMovingView.setTranslationX(this.mPausedX);
        }

        public void onTransitionStart(Transition transition) {
        }

        public void onTransitionEnd(Transition transition) {
            this.mMovingView.setTranslationX(this.mTerminalX);
        }

        public void onTransitionCancel(Transition transition) {
        }

        public void onTransitionPause(Transition transition) {
        }

        public void onTransitionResume(Transition transition) {
        }
    }
}
