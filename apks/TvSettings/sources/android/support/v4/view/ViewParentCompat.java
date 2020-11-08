package android.support.v4.view;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;

public final class ViewParentCompat {
    private static final String TAG = "ViewParentCompat";
    private static int[] sTempNestedScrollConsumed;

    private ViewParentCompat() {
    }

    @Deprecated
    public static boolean requestSendAccessibilityEvent(ViewParent parent, View child, AccessibilityEvent event) {
        return parent.requestSendAccessibilityEvent(child, event);
    }

    public static boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes) {
        return onStartNestedScroll(parent, child, target, nestedScrollAxes, 0);
    }

    public static void onNestedScrollAccepted(ViewParent parent, View child, View target, int nestedScrollAxes) {
        onNestedScrollAccepted(parent, child, target, nestedScrollAxes, 0);
    }

    public static void onStopNestedScroll(ViewParent parent, View target) {
        onStopNestedScroll(parent, target, 0);
    }

    public static void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(parent, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, 0, getTempNestedScrollConsumed());
    }

    public static void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(parent, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, getTempNestedScrollConsumed());
    }

    public static void onNestedPreScroll(ViewParent parent, View target, int dx, int dy, int[] consumed) {
        onNestedPreScroll(parent, target, dx, dy, consumed, 0);
    }

    public static boolean onStartNestedScroll(ViewParent parent, View child, View target, int nestedScrollAxes, int type) {
        if (parent instanceof NestedScrollingParent2) {
            return ((NestedScrollingParent2) parent).onStartNestedScroll(child, target, nestedScrollAxes, type);
        }
        if (type != 0) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                return parent.onStartNestedScroll(child, target, nestedScrollAxes);
            } catch (AbstractMethodError e) {
                Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onStartNestedScroll", e);
                return false;
            }
        } else if (parent instanceof NestedScrollingParent) {
            return ((NestedScrollingParent) parent).onStartNestedScroll(child, target, nestedScrollAxes);
        } else {
            return false;
        }
    }

    public static void onNestedScrollAccepted(ViewParent parent, View child, View target, int nestedScrollAxes, int type) {
        if (parent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) parent).onNestedScrollAccepted(child, target, nestedScrollAxes, type);
        } else if (type != 0) {
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    parent.onNestedScrollAccepted(child, target, nestedScrollAxes);
                } catch (AbstractMethodError e) {
                    Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onNestedScrollAccepted", e);
                }
            } else if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onNestedScrollAccepted(child, target, nestedScrollAxes);
            }
        }
    }

    public static void onStopNestedScroll(ViewParent parent, View target, int type) {
        if (parent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) parent).onStopNestedScroll(target, type);
        } else if (type != 0) {
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    parent.onStopNestedScroll(target);
                } catch (AbstractMethodError e) {
                    Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onStopNestedScroll", e);
                }
            } else if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onStopNestedScroll(target);
            }
        }
    }

    public static void onNestedScroll(ViewParent parent, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        ViewParent viewParent = parent;
        if (viewParent instanceof NestedScrollingParent3) {
            ((NestedScrollingParent3) viewParent).onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
            return;
        }
        consumed[0] = consumed[0] + dxUnconsumed;
        consumed[1] = consumed[1] + dyUnconsumed;
        if (viewParent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) viewParent).onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        } else if (type != 0) {
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    parent.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                } catch (AbstractMethodError e) {
                    AbstractMethodError abstractMethodError = e;
                    Log.e(TAG, "ViewParent " + viewParent + " does not implement interface " + "method onNestedScroll", e);
                }
            } else if (viewParent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) viewParent).onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }
        }
    }

    public static void onNestedPreScroll(ViewParent parent, View target, int dx, int dy, int[] consumed, int type) {
        if (parent instanceof NestedScrollingParent2) {
            ((NestedScrollingParent2) parent).onNestedPreScroll(target, dx, dy, consumed, type);
        } else if (type != 0) {
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                try {
                    parent.onNestedPreScroll(target, dx, dy, consumed);
                } catch (AbstractMethodError e) {
                    Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onNestedPreScroll", e);
                }
            } else if (parent instanceof NestedScrollingParent) {
                ((NestedScrollingParent) parent).onNestedPreScroll(target, dx, dy, consumed);
            }
        }
    }

    public static boolean onNestedFling(ViewParent parent, View target, float velocityX, float velocityY, boolean consumed) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                return parent.onNestedFling(target, velocityX, velocityY, consumed);
            } catch (AbstractMethodError e) {
                Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onNestedFling", e);
                return false;
            }
        } else if (parent instanceof NestedScrollingParent) {
            return ((NestedScrollingParent) parent).onNestedFling(target, velocityX, velocityY, consumed);
        } else {
            return false;
        }
    }

    public static boolean onNestedPreFling(ViewParent parent, View target, float velocityX, float velocityY) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                return parent.onNestedPreFling(target, velocityX, velocityY);
            } catch (AbstractMethodError e) {
                Log.e(TAG, "ViewParent " + parent + " does not implement interface " + "method onNestedPreFling", e);
                return false;
            }
        } else if (parent instanceof NestedScrollingParent) {
            return ((NestedScrollingParent) parent).onNestedPreFling(target, velocityX, velocityY);
        } else {
            return false;
        }
    }

    public static void notifySubtreeAccessibilityStateChanged(ViewParent parent, View child, View source, int changeType) {
        if (Build.VERSION.SDK_INT >= 19) {
            parent.notifySubtreeAccessibilityStateChanged(child, source, changeType);
        }
    }

    private static int[] getTempNestedScrollConsumed() {
        if (sTempNestedScrollConsumed == null) {
            sTempNestedScrollConsumed = new int[2];
        } else {
            sTempNestedScrollConsumed[0] = 0;
            sTempNestedScrollConsumed[1] = 0;
        }
        return sTempNestedScrollConsumed;
    }
}