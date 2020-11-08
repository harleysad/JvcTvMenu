package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R;
import android.transition.Transition;
import android.view.View;

@RequiresApi(21)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
class TranslationAnimationCreator {
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: int[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.animation.Animator createAnimation(android.view.View r20, android.transition.TransitionValues r21, int r22, int r23, float r24, float r25, float r26, float r27, android.animation.TimeInterpolator r28, android.transition.Transition r29) {
        /*
            r7 = r20
            r8 = r21
            r9 = r26
            r10 = r27
            float r11 = r20.getTranslationX()
            float r12 = r20.getTranslationY()
            android.view.View r0 = r8.view
            int r1 = android.support.v17.leanback.R.id.transitionPosition
            java.lang.Object r0 = r0.getTag(r1)
            r13 = r0
            int[] r13 = (int[]) r13
            if (r13 == 0) goto L_0x002e
            r0 = 0
            r0 = r13[r0]
            int r0 = r0 - r22
            float r0 = (float) r0
            float r0 = r0 + r11
            r1 = 1
            r1 = r13[r1]
            int r1 = r1 - r23
            float r1 = (float) r1
            float r1 = r1 + r12
            r6 = r0
            r5 = r1
            goto L_0x0032
        L_0x002e:
            r6 = r24
            r5 = r25
        L_0x0032:
            float r0 = r6 - r11
            int r0 = java.lang.Math.round(r0)
            int r14 = r22 + r0
            float r0 = r5 - r12
            int r0 = java.lang.Math.round(r0)
            int r15 = r23 + r0
            r7.setTranslationX(r6)
            r7.setTranslationY(r5)
            int r0 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0052
            int r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0052
            r0 = 0
            return r0
        L_0x0052:
            android.graphics.Path r0 = new android.graphics.Path
            r0.<init>()
            r4 = r0
            r4.moveTo(r6, r5)
            r4.lineTo(r9, r10)
            android.util.Property r0 = android.view.View.TRANSLATION_X
            android.util.Property r1 = android.view.View.TRANSLATION_Y
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r7, r0, r1, r4)
            android.support.v17.leanback.transition.TranslationAnimationCreator$TransitionPositionListener r16 = new android.support.v17.leanback.transition.TranslationAnimationCreator$TransitionPositionListener
            android.view.View r2 = r8.view
            r0 = r16
            r1 = r7
            r7 = r3
            r3 = r14
            r17 = r4
            r4 = r15
            r18 = r5
            r5 = r11
            r19 = r6
            r6 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r1 = r29
            r1.addListener(r0)
            r7.addListener(r0)
            r7.addPauseListener(r0)
            r2 = r28
            r7.setInterpolator(r2)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.transition.TranslationAnimationCreator.createAnimation(android.view.View, android.transition.TransitionValues, int, int, float, float, float, float, android.animation.TimeInterpolator, android.transition.Transition):android.animation.Animator");
    }

    private static class TransitionPositionListener extends AnimatorListenerAdapter implements Transition.TransitionListener {
        private final View mMovingView;
        private float mPausedX;
        private float mPausedY;
        private final int mStartX;
        private final int mStartY;
        private final float mTerminalX;
        private final float mTerminalY;
        private int[] mTransitionPosition = ((int[]) this.mViewInHierarchy.getTag(R.id.transitionPosition));
        private final View mViewInHierarchy;

        TransitionPositionListener(View movingView, View viewInHierarchy, int startX, int startY, float terminalX, float terminalY) {
            this.mMovingView = movingView;
            this.mViewInHierarchy = viewInHierarchy;
            this.mStartX = startX - Math.round(this.mMovingView.getTranslationX());
            this.mStartY = startY - Math.round(this.mMovingView.getTranslationY());
            this.mTerminalX = terminalX;
            this.mTerminalY = terminalY;
            if (this.mTransitionPosition != null) {
                this.mViewInHierarchy.setTag(R.id.transitionPosition, (Object) null);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (this.mTransitionPosition == null) {
                this.mTransitionPosition = new int[2];
            }
            this.mTransitionPosition[0] = Math.round(((float) this.mStartX) + this.mMovingView.getTranslationX());
            this.mTransitionPosition[1] = Math.round(((float) this.mStartY) + this.mMovingView.getTranslationY());
            this.mViewInHierarchy.setTag(R.id.transitionPosition, this.mTransitionPosition);
        }

        public void onAnimationEnd(Animator animator) {
        }

        public void onAnimationPause(Animator animator) {
            this.mPausedX = this.mMovingView.getTranslationX();
            this.mPausedY = this.mMovingView.getTranslationY();
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }

        public void onAnimationResume(Animator animator) {
            this.mMovingView.setTranslationX(this.mPausedX);
            this.mMovingView.setTranslationY(this.mPausedY);
        }

        public void onTransitionStart(Transition transition) {
        }

        public void onTransitionEnd(Transition transition) {
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }

        public void onTransitionCancel(Transition transition) {
        }

        public void onTransitionPause(Transition transition) {
        }

        public void onTransitionResume(Transition transition) {
        }
    }

    private TranslationAnimationCreator() {
    }
}
