package android.support.v17.leanback.widget;

import android.os.Build;
import android.support.v17.leanback.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

final class StaticShadowHelper {
    private StaticShadowHelper() {
    }

    static boolean supportsShadow() {
        return Build.VERSION.SDK_INT >= 21;
    }

    static void prepareParent(ViewGroup parent) {
        if (Build.VERSION.SDK_INT >= 21) {
            parent.setLayoutMode(1);
        }
    }

    static Object addStaticShadow(ViewGroup shadowContainer) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        shadowContainer.setLayoutMode(1);
        LayoutInflater.from(shadowContainer.getContext()).inflate(R.layout.lb_shadow, shadowContainer, true);
        ShadowImpl impl = new ShadowImpl();
        impl.mNormalShadow = shadowContainer.findViewById(R.id.lb_shadow_normal);
        impl.mFocusShadow = shadowContainer.findViewById(R.id.lb_shadow_focused);
        return impl;
    }

    static void setShadowFocusLevel(Object impl, float level) {
        if (Build.VERSION.SDK_INT >= 21) {
            ShadowImpl shadowImpl = (ShadowImpl) impl;
            shadowImpl.mNormalShadow.setAlpha(1.0f - level);
            shadowImpl.mFocusShadow.setAlpha(level);
        }
    }

    static class ShadowImpl {
        View mFocusShadow;
        View mNormalShadow;

        ShadowImpl() {
        }
    }
}
