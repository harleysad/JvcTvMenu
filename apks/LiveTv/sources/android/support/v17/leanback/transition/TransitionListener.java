package android.support.v17.leanback.transition;

import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TransitionListener {
    protected Object mImpl;

    public void onTransitionStart(Object transition) {
    }

    public void onTransitionEnd(Object transition) {
    }

    public void onTransitionCancel(Object transition) {
    }

    public void onTransitionPause(Object transition) {
    }

    public void onTransitionResume(Object transition) {
    }
}
