package com.android.tv.settings.name.setup;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import com.android.tv.settings.R;
import com.android.tv.settings.name.DeviceNameSetFragment;

public class DeviceNameFlowStartActivity extends Activity {
    private static final String EXTRA_MOVING_FORWARD = "movingForward";
    private boolean mResultOk = false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Animator animator;
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, DeviceNameSetFragment.newInstance(), 16908290);
            if (getIntent().getExtras().getBoolean(EXTRA_MOVING_FORWARD, true)) {
                animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_open_in);
            } else {
                animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_close_in);
            }
            animator.setTarget(getWindow().getDecorView());
            animator.start();
        }
    }

    public void finish() {
        Animator animator;
        if (this.mResultOk) {
            animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_open_out);
        } else {
            animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_close_out);
        }
        animator.setTarget(getWindow().getDecorView());
        animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                DeviceNameFlowStartActivity.this.doFinish();
            }

            public void onAnimationCancel(Animator animation) {
            }
        });
        animator.start();
    }

    /* access modifiers changed from: private */
    public void doFinish() {
        super.finish();
    }

    public void setResultOk(boolean resultOk) {
        this.mResultOk = resultOk;
    }
}
