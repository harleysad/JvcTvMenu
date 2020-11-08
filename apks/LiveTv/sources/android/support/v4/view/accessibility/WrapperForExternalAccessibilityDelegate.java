package android.support.v4.view.accessibility;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class WrapperForExternalAccessibilityDelegate extends AccessibilityDelegateCompat {
    View.AccessibilityDelegate mOriginalDelegate;

    public WrapperForExternalAccessibilityDelegate(@NonNull View.AccessibilityDelegate delegate) {
        this.mOriginalDelegate = delegate;
    }

    public void sendAccessibilityEvent(View host, int eventType) {
        this.mOriginalDelegate.sendAccessibilityEvent(host, eventType);
    }

    public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
        this.mOriginalDelegate.sendAccessibilityEventUnchecked(host, event);
    }

    public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        return this.mOriginalDelegate.dispatchPopulateAccessibilityEvent(host, event);
    }

    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        this.mOriginalDelegate.onPopulateAccessibilityEvent(host, event);
    }

    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        this.mOriginalDelegate.onInitializeAccessibilityEvent(host, event);
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        this.mOriginalDelegate.onInitializeAccessibilityNodeInfo(host, info.unwrap());
    }

    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
        return this.mOriginalDelegate.onRequestSendAccessibilityEvent(host, child, event);
    }

    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View host) {
        Object provider;
        if (Build.VERSION.SDK_INT < 16 || (provider = this.mOriginalDelegate.getAccessibilityNodeProvider(host)) == null) {
            return null;
        }
        return new AccessibilityNodeProviderCompat(provider);
    }

    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (Build.VERSION.SDK_INT >= 16) {
            return this.mOriginalDelegate.performAccessibilityAction(host, action, args);
        }
        return false;
    }
}
