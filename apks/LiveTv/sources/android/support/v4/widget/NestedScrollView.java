package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.NestedScrollingChild3;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent3;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import java.util.List;

public class NestedScrollView extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3, ScrollingView {
    private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
    static final int ANIMATED_SCROLL_GAP = 250;
    private static final int INVALID_POINTER = -1;
    static final float MAX_SCROLL_FACTOR = 0.5f;
    private static final int[] SCROLLVIEW_STYLEABLE = {16843130};
    private static final String TAG = "NestedScrollView";
    private int mActivePointerId;
    private final NestedScrollingChildHelper mChildHelper;
    private View mChildToScrollTo;
    private EdgeEffect mEdgeGlowBottom;
    private EdgeEffect mEdgeGlowTop;
    private boolean mFillViewport;
    private boolean mIsBeingDragged;
    private boolean mIsLaidOut;
    private boolean mIsLayoutDirty;
    private int mLastMotionY;
    private long mLastScroll;
    private int mLastScrollerY;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mNestedYOffset;
    private OnScrollChangeListener mOnScrollChangeListener;
    private final NestedScrollingParentHelper mParentHelper;
    private SavedState mSavedState;
    private final int[] mScrollConsumed;
    private final int[] mScrollOffset;
    private OverScroller mScroller;
    private boolean mSmoothScrollingEnabled;
    private final Rect mTempRect;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;

    public interface OnScrollChangeListener {
        void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4);
    }

    public NestedScrollView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public NestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempRect = new Rect();
        this.mIsLayoutDirty = true;
        this.mIsLaidOut = false;
        this.mChildToScrollTo = null;
        this.mIsBeingDragged = false;
        this.mSmoothScrollingEnabled = true;
        this.mActivePointerId = -1;
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        initScrollView();
        TypedArray a = context.obtainStyledAttributes(attrs, SCROLLVIEW_STYLEABLE, defStyleAttr, 0);
        setFillViewport(a.getBoolean(0, false));
        a.recycle();
        this.mParentHelper = new NestedScrollingParentHelper(this);
        this.mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
    }

    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    public boolean startNestedScroll(int axes, int type) {
        return this.mChildHelper.startNestedScroll(axes, type);
    }

    public void stopNestedScroll(int type) {
        this.mChildHelper.stopNestedScroll(type);
    }

    public boolean hasNestedScrollingParent(int type) {
        return this.mChildHelper.hasNestedScrollingParent(type);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        return this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        return this.mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        this.mChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return this.mChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, 0);
    }

    public void stopNestedScroll() {
        stopNestedScroll(0);
    }

    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(0);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, 0);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        onNestedScrollInternal(dyUnconsumed, type, consumed);
    }

    private void onNestedScrollInternal(int dyUnconsumed, int type, @Nullable int[] consumed) {
        int oldScrollY = getScrollY();
        scrollBy(0, dyUnconsumed);
        int myConsumed = getScrollY() - oldScrollY;
        if (consumed != null) {
            consumed[1] = consumed[1] + myConsumed;
        }
        this.mChildHelper.dispatchNestedScroll(0, myConsumed, 0, dyUnconsumed - myConsumed, (int[]) null, type, consumed);
    }

    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & 2) != 0;
    }

    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        this.mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(2, type);
    }

    public void onStopNestedScroll(@NonNull View target, int type) {
        this.mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
    }

    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScrollInternal(dyUnconsumed, type, (int[]) null);
    }

    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        dispatchNestedPreScroll(dx, dy, consumed, (int[]) null, type);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, 0);
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, 0);
    }

    public void onStopNestedScroll(View target) {
        onStopNestedScroll(target, 0);
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScrollInternal(dyUnconsumed, 0, (int[]) null);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, 0);
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (consumed) {
            return false;
        }
        dispatchNestedFling(0.0f, velocityY, true);
        fling((int) velocityY);
        return true;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    public int getNestedScrollAxes() {
        return this.mParentHelper.getNestedScrollAxes();
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    /* access modifiers changed from: protected */
    public float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int length = getVerticalFadingEdgeLength();
        int scrollY = getScrollY();
        if (scrollY < length) {
            return ((float) scrollY) / ((float) length);
        }
        return 1.0f;
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        View child = getChildAt(0);
        int length = getVerticalFadingEdgeLength();
        int span = ((child.getBottom() + ((FrameLayout.LayoutParams) child.getLayoutParams()).bottomMargin) - getScrollY()) - (getHeight() - getPaddingBottom());
        if (span < length) {
            return ((float) span) / ((float) length);
        }
        return 1.0f;
    }

    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * ((float) getHeight()));
    }

    private void initScrollView() {
        this.mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void addView(View child) {
        if (getChildCount() <= 0) {
            super.addView(child);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, int index) {
        if (getChildCount() <= 0) {
            super.addView(child, index);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, params);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, index, params);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void setOnScrollChangeListener(@Nullable OnScrollChangeListener l) {
        this.mOnScrollChangeListener = l;
    }

    private boolean canScroll() {
        if (getChildCount() <= 0) {
            return false;
        }
        View child = getChildAt(0);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if (child.getHeight() + lp.topMargin + lp.bottomMargin > (getHeight() - getPaddingTop()) - getPaddingBottom()) {
            return true;
        }
        return false;
    }

    public boolean isFillViewport() {
        return this.mFillViewport;
    }

    public void setFillViewport(boolean fillViewport) {
        if (fillViewport != this.mFillViewport) {
            this.mFillViewport = fillViewport;
            requestLayout();
        }
    }

    public boolean isSmoothScrollingEnabled() {
        return this.mSmoothScrollingEnabled;
    }

    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        this.mSmoothScrollingEnabled = smoothScrollingEnabled;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mOnScrollChangeListener != null) {
            this.mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mFillViewport && View.MeasureSpec.getMode(heightMeasureSpec) != 0 && getChildCount() > 0) {
            View child = getChildAt(0);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            int childSize = child.getMeasuredHeight();
            int parentSpace = (((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - lp.topMargin) - lp.bottomMargin;
            if (childSize < parentSpace) {
                child.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width), View.MeasureSpec.makeMeasureSpec(parentSpace, 1073741824));
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        this.mTempRect.setEmpty();
        int i = 130;
        if (!canScroll()) {
            if (!isFocused() || event.getKeyCode() == 4) {
                return false;
            }
            View currentFocused = findFocus();
            if (currentFocused == this) {
                currentFocused = null;
            }
            View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, 130);
            if (nextFocused == null || nextFocused == this || !nextFocused.requestFocus(130)) {
                return false;
            }
            return true;
        } else if (event.getAction() != 0) {
            return false;
        } else {
            int keyCode = event.getKeyCode();
            if (keyCode != 62) {
                switch (keyCode) {
                    case 19:
                        if (!event.isAltPressed()) {
                            return arrowScroll(33);
                        }
                        return fullScroll(33);
                    case 20:
                        if (!event.isAltPressed()) {
                            return arrowScroll(130);
                        }
                        return fullScroll(130);
                    default:
                        return false;
                }
            } else {
                if (event.isShiftPressed()) {
                    i = 33;
                }
                pageScroll(i);
                return false;
            }
        }
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() <= 0) {
            return false;
        }
        int scrollY = getScrollY();
        View child = getChildAt(0);
        if (y < child.getTop() - scrollY || y >= child.getBottom() - scrollY || x < child.getLeft() || x >= child.getRight()) {
            return false;
        }
        return true;
    }

    private void initOrResetVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MotionEvent motionEvent = ev;
        int action = ev.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        int i = action & 255;
        if (i != 6) {
            switch (i) {
                case 0:
                    int y = (int) ev.getY();
                    if (inChild((int) ev.getX(), y)) {
                        this.mLastMotionY = y;
                        this.mActivePointerId = motionEvent.getPointerId(0);
                        initOrResetVelocityTracker();
                        this.mVelocityTracker.addMovement(motionEvent);
                        this.mScroller.computeScrollOffset();
                        this.mIsBeingDragged = true ^ this.mScroller.isFinished();
                        startNestedScroll(2, 0);
                        break;
                    } else {
                        this.mIsBeingDragged = false;
                        recycleVelocityTracker();
                        break;
                    }
                case 1:
                case 3:
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    recycleVelocityTracker();
                    if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                    stopNestedScroll(0);
                    break;
                case 2:
                    int activePointerId = this.mActivePointerId;
                    if (activePointerId != -1) {
                        int pointerIndex = motionEvent.findPointerIndex(activePointerId);
                        if (pointerIndex != -1) {
                            int y2 = (int) motionEvent.getY(pointerIndex);
                            if (Math.abs(y2 - this.mLastMotionY) > this.mTouchSlop && (2 & getNestedScrollAxes()) == 0) {
                                this.mIsBeingDragged = true;
                                this.mLastMotionY = y2;
                                initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(motionEvent);
                                this.mNestedYOffset = 0;
                                ViewParent parent = getParent();
                                if (parent != null) {
                                    parent.requestDisallowInterceptTouchEvent(true);
                                    break;
                                }
                            }
                        } else {
                            Log.e(TAG, "Invalid pointerId=" + activePointerId + " in onInterceptTouchEvent");
                            break;
                        }
                    }
                    break;
            }
        } else {
            onSecondaryPointerUp(ev);
        }
        return this.mIsBeingDragged;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r28) {
        /*
            r27 = this;
            r10 = r27
            r11 = r28
            r27.initVelocityTrackerIfNotExists()
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r28)
            int r13 = r28.getActionMasked()
            r14 = 0
            if (r13 != 0) goto L_0x0014
            r10.mNestedYOffset = r14
        L_0x0014:
            int r0 = r10.mNestedYOffset
            float r0 = (float) r0
            r15 = 0
            r12.offsetLocation(r15, r0)
            r0 = -1
            r9 = 1
            switch(r13) {
                case 0: goto L_0x0243;
                case 1: goto L_0x01f1;
                case 2: goto L_0x0070;
                case 3: goto L_0x0046;
                case 4: goto L_0x0020;
                case 5: goto L_0x0034;
                case 6: goto L_0x0023;
                default: goto L_0x0020;
            }
        L_0x0020:
            r15 = r9
            goto L_0x027c
        L_0x0023:
            r27.onSecondaryPointerUp(r28)
            int r0 = r10.mActivePointerId
            int r0 = r11.findPointerIndex(r0)
            float r0 = r11.getY(r0)
            int r0 = (int) r0
            r10.mLastMotionY = r0
            goto L_0x0020
        L_0x0034:
            int r0 = r28.getActionIndex()
            float r1 = r11.getY(r0)
            int r1 = (int) r1
            r10.mLastMotionY = r1
            int r1 = r11.getPointerId(r0)
            r10.mActivePointerId = r1
            goto L_0x0020
        L_0x0046:
            boolean r1 = r10.mIsBeingDragged
            if (r1 == 0) goto L_0x006a
            int r1 = r27.getChildCount()
            if (r1 <= 0) goto L_0x006a
            android.widget.OverScroller r2 = r10.mScroller
            int r3 = r27.getScrollX()
            int r4 = r27.getScrollY()
            r5 = 0
            r6 = 0
            r7 = 0
            int r8 = r27.getScrollRange()
            boolean r1 = r2.springBack(r3, r4, r5, r6, r7, r8)
            if (r1 == 0) goto L_0x006a
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(r27)
        L_0x006a:
            r10.mActivePointerId = r0
            r27.endDrag()
            goto L_0x0020
        L_0x0070:
            int r1 = r10.mActivePointerId
            int r8 = r11.findPointerIndex(r1)
            if (r8 != r0) goto L_0x0096
            java.lang.String r0 = "NestedScrollView"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid pointerId="
            r1.append(r2)
            int r2 = r10.mActivePointerId
            r1.append(r2)
            java.lang.String r2 = " in onTouchEvent"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.e(r0, r1)
            goto L_0x0020
        L_0x0096:
            float r0 = r11.getY(r8)
            int r7 = (int) r0
            int r0 = r10.mLastMotionY
            int r6 = r0 - r7
            r1 = 0
            int[] r3 = r10.mScrollConsumed
            int[] r4 = r10.mScrollOffset
            r5 = 0
            r0 = r10
            r2 = r6
            boolean r0 = r0.dispatchNestedPreScroll(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x00c3
            int[] r0 = r10.mScrollConsumed
            r0 = r0[r9]
            int r6 = r6 - r0
            int[] r0 = r10.mScrollOffset
            r0 = r0[r9]
            float r0 = (float) r0
            r12.offsetLocation(r15, r0)
            int r0 = r10.mNestedYOffset
            int[] r1 = r10.mScrollOffset
            r1 = r1[r9]
            int r0 = r0 + r1
            r10.mNestedYOffset = r0
        L_0x00c3:
            boolean r0 = r10.mIsBeingDragged
            if (r0 != 0) goto L_0x00e3
            int r0 = java.lang.Math.abs(r6)
            int r1 = r10.mTouchSlop
            if (r0 <= r1) goto L_0x00e3
            android.view.ViewParent r0 = r27.getParent()
            if (r0 == 0) goto L_0x00d8
            r0.requestDisallowInterceptTouchEvent(r9)
        L_0x00d8:
            r10.mIsBeingDragged = r9
            if (r6 <= 0) goto L_0x00e0
            int r1 = r10.mTouchSlop
            int r6 = r6 - r1
            goto L_0x00e3
        L_0x00e0:
            int r1 = r10.mTouchSlop
            int r6 = r6 + r1
        L_0x00e3:
            r16 = r6
            boolean r0 = r10.mIsBeingDragged
            if (r0 == 0) goto L_0x0020
            int[] r0 = r10.mScrollOffset
            r0 = r0[r9]
            int r0 = r7 - r0
            r10.mLastMotionY = r0
            int r17 = r27.getScrollY()
            int r6 = r27.getScrollRange()
            int r5 = r27.getOverScrollMode()
            if (r5 == 0) goto L_0x0106
            if (r5 != r9) goto L_0x0104
            if (r6 <= 0) goto L_0x0104
            goto L_0x0106
        L_0x0104:
            r0 = r14
            goto L_0x0107
        L_0x0106:
            r0 = r9
        L_0x0107:
            r18 = r0
            r1 = 0
            r3 = 0
            int r4 = r27.getScrollY()
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 1
            r0 = r10
            r2 = r16
            r23 = r5
            r5 = r19
            r24 = r6
            r19 = r7
            r7 = r20
            r25 = r8
            r8 = r21
            r15 = r9
            r9 = r22
            boolean r0 = r0.overScrollByCompat(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            if (r0 == 0) goto L_0x013c
            boolean r0 = r10.hasNestedScrollingParent(r14)
            if (r0 != 0) goto L_0x013c
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.clear()
        L_0x013c:
            int r0 = r27.getScrollY()
            int r8 = r0 - r17
            int r9 = r16 - r8
            int[] r0 = r10.mScrollConsumed
            r0[r15] = r14
            r1 = 0
            r3 = 0
            int[] r5 = r10.mScrollOffset
            r6 = 0
            int[] r7 = r10.mScrollConsumed
            r0 = r10
            r2 = r8
            r4 = r9
            r0.dispatchNestedScroll(r1, r2, r3, r4, r5, r6, r7)
            int r0 = r10.mLastMotionY
            int[] r1 = r10.mScrollOffset
            r1 = r1[r15]
            int r0 = r0 - r1
            r10.mLastMotionY = r0
            int[] r0 = r10.mScrollOffset
            r0 = r0[r15]
            float r0 = (float) r0
            r1 = 0
            r12.offsetLocation(r1, r0)
            int r0 = r10.mNestedYOffset
            int[] r1 = r10.mScrollOffset
            r1 = r1[r15]
            int r0 = r0 + r1
            r10.mNestedYOffset = r0
            if (r18 == 0) goto L_0x01ed
            int[] r0 = r10.mScrollConsumed
            r0 = r0[r15]
            int r0 = r16 - r0
            r27.ensureGlows()
            int r1 = r17 + r0
            if (r1 >= 0) goto L_0x01a7
            android.widget.EdgeEffect r2 = r10.mEdgeGlowTop
            float r3 = (float) r0
            int r4 = r27.getHeight()
            float r4 = (float) r4
            float r3 = r3 / r4
            r4 = r25
            float r5 = r11.getX(r4)
            int r6 = r27.getWidth()
            float r6 = (float) r6
            float r5 = r5 / r6
            android.support.v4.widget.EdgeEffectCompat.onPull(r2, r3, r5)
            android.widget.EdgeEffect r2 = r10.mEdgeGlowBottom
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x01a4
            android.widget.EdgeEffect r2 = r10.mEdgeGlowBottom
            r2.onRelease()
        L_0x01a4:
            r2 = r24
            goto L_0x01d3
        L_0x01a7:
            r4 = r25
            r2 = r24
            if (r1 <= r2) goto L_0x01d3
            android.widget.EdgeEffect r3 = r10.mEdgeGlowBottom
            float r5 = (float) r0
            int r6 = r27.getHeight()
            float r6 = (float) r6
            float r5 = r5 / r6
            r6 = 1065353216(0x3f800000, float:1.0)
            float r7 = r11.getX(r4)
            int r14 = r27.getWidth()
            float r14 = (float) r14
            float r7 = r7 / r14
            float r6 = r6 - r7
            android.support.v4.widget.EdgeEffectCompat.onPull(r3, r5, r6)
            android.widget.EdgeEffect r3 = r10.mEdgeGlowTop
            boolean r3 = r3.isFinished()
            if (r3 != 0) goto L_0x01d3
            android.widget.EdgeEffect r3 = r10.mEdgeGlowTop
            r3.onRelease()
        L_0x01d3:
            android.widget.EdgeEffect r3 = r10.mEdgeGlowTop
            if (r3 == 0) goto L_0x01ea
            android.widget.EdgeEffect r3 = r10.mEdgeGlowTop
            boolean r3 = r3.isFinished()
            if (r3 == 0) goto L_0x01e7
            android.widget.EdgeEffect r3 = r10.mEdgeGlowBottom
            boolean r3 = r3.isFinished()
            if (r3 != 0) goto L_0x01ea
        L_0x01e7:
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(r27)
        L_0x01ea:
            r16 = r0
            goto L_0x01ef
        L_0x01ed:
            r4 = r25
        L_0x01ef:
            goto L_0x027c
        L_0x01f1:
            r15 = r9
            android.view.VelocityTracker r1 = r10.mVelocityTracker
            r2 = 1000(0x3e8, float:1.401E-42)
            int r3 = r10.mMaximumVelocity
            float r3 = (float) r3
            r1.computeCurrentVelocity(r2, r3)
            int r2 = r10.mActivePointerId
            float r2 = r1.getYVelocity(r2)
            int r2 = (int) r2
            int r3 = java.lang.Math.abs(r2)
            int r4 = r10.mMinimumVelocity
            if (r3 <= r4) goto L_0x021e
            int r3 = -r2
            float r3 = (float) r3
            r4 = 0
            boolean r3 = r10.dispatchNestedPreFling(r4, r3)
            if (r3 != 0) goto L_0x023d
            int r3 = -r2
            float r3 = (float) r3
            r10.dispatchNestedFling(r4, r3, r15)
            int r3 = -r2
            r10.fling(r3)
            goto L_0x023d
        L_0x021e:
            android.widget.OverScroller r3 = r10.mScroller
            int r17 = r27.getScrollX()
            int r18 = r27.getScrollY()
            r19 = 0
            r20 = 0
            r21 = 0
            int r22 = r27.getScrollRange()
            r16 = r3
            boolean r3 = r16.springBack(r17, r18, r19, r20, r21, r22)
            if (r3 == 0) goto L_0x023d
            android.support.v4.view.ViewCompat.postInvalidateOnAnimation(r27)
        L_0x023d:
            r10.mActivePointerId = r0
            r27.endDrag()
            goto L_0x027c
        L_0x0243:
            r15 = r9
            int r0 = r27.getChildCount()
            if (r0 != 0) goto L_0x024b
            return r14
        L_0x024b:
            android.widget.OverScroller r0 = r10.mScroller
            boolean r0 = r0.isFinished()
            r0 = r0 ^ r15
            r10.mIsBeingDragged = r0
            if (r0 == 0) goto L_0x025f
            android.view.ViewParent r0 = r27.getParent()
            if (r0 == 0) goto L_0x025f
            r0.requestDisallowInterceptTouchEvent(r15)
        L_0x025f:
            android.widget.OverScroller r0 = r10.mScroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x026a
            r27.abortAnimatedScroll()
        L_0x026a:
            float r0 = r28.getY()
            int r0 = (int) r0
            r10.mLastMotionY = r0
            int r0 = r11.getPointerId(r14)
            r10.mActivePointerId = r0
            r0 = 2
            r10.startNestedScroll(r0, r14)
        L_0x027c:
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            if (r0 == 0) goto L_0x0285
            android.view.VelocityTracker r0 = r10.mVelocityTracker
            r0.addMovement(r12)
        L_0x0285:
            r12.recycle()
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.NestedScrollView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = ev.getActionIndex();
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionY = (int) ev.getY(newPointerIndex);
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & 2) == 0 || event.getAction() != 8 || this.mIsBeingDragged) {
            return false;
        }
        float vscroll = event.getAxisValue(9);
        if (vscroll == 0.0f) {
            return false;
        }
        int range = getScrollRange();
        int oldScrollY = getScrollY();
        int newScrollY = oldScrollY - ((int) (getVerticalScrollFactorCompat() * vscroll));
        if (newScrollY < 0) {
            newScrollY = 0;
        } else if (newScrollY > range) {
            newScrollY = range;
        }
        if (newScrollY == oldScrollY) {
            return false;
        }
        super.scrollTo(getScrollX(), newScrollY);
        return true;
    }

    private float getVerticalScrollFactorCompat() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            Context context = getContext();
            if (context.getTheme().resolveAttribute(16842829, outValue, true)) {
                this.mVerticalScrollFactor = outValue.getDimension(context.getResources().getDisplayMetrics());
            } else {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
        }
        return this.mVerticalScrollFactor;
    }

    /* access modifiers changed from: protected */
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.scrollTo(scrollX, scrollY);
    }

    /* access modifiers changed from: package-private */
    public boolean overScrollByCompat(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int maxOverScrollX2;
        int maxOverScrollY2;
        int overScrollMode = getOverScrollMode();
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || (overScrollMode == 1 && canScrollHorizontal);
        boolean overScrollVertical = overScrollMode == 0 || (overScrollMode == 1 && canScrollVertical);
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX2 = 0;
        } else {
            maxOverScrollX2 = maxOverScrollX;
        }
        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY2 = 0;
        } else {
            maxOverScrollY2 = maxOverScrollY;
        }
        int left = -maxOverScrollX2;
        int right = maxOverScrollX2 + scrollRangeX;
        int i = overScrollMode;
        int overScrollMode2 = -maxOverScrollY2;
        boolean z = canScrollHorizontal;
        int bottom = maxOverScrollY2 + scrollRangeY;
        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }
        boolean clampedX2 = clampedX;
        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < overScrollMode2) {
            newScrollY = overScrollMode2;
            clampedY = true;
        }
        int newScrollY2 = newScrollY;
        boolean clampedY2 = clampedY;
        if (clampedY2) {
            int i2 = overScrollMode2;
            if (!hasNestedScrollingParent(1)) {
                this.mScroller.springBack(newScrollX, newScrollY2, 0, 0, 0, getScrollRange());
            }
        } else {
            int top = overScrollMode2;
        }
        onOverScrolled(newScrollX, newScrollY2, clampedX2, clampedY2);
        return clampedX2 || clampedY2;
    }

    /* access modifiers changed from: package-private */
    public int getScrollRange() {
        if (getChildCount() <= 0) {
            return 0;
        }
        View child = getChildAt(0);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        return Math.max(0, ((child.getHeight() + lp.topMargin) + lp.bottomMargin) - ((getHeight() - getPaddingTop()) - getPaddingBottom()));
    }

    private View findFocusableViewInBounds(boolean topFocus, int top, int bottom) {
        List<View> focusables = getFocusables(2);
        int count = focusables.size();
        boolean foundFullyContainedFocusable = false;
        View focusCandidate = null;
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();
            if (top < viewBottom && viewTop < bottom) {
                boolean viewIsCloserToBoundary = true;
                boolean viewIsFullyContained = top < viewTop && viewBottom < bottom;
                if (focusCandidate == null) {
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    if ((!topFocus || viewTop >= focusCandidate.getTop()) && (topFocus || viewBottom <= focusCandidate.getBottom())) {
                        viewIsCloserToBoundary = false;
                    }
                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            focusCandidate = view;
                        }
                    } else if (viewIsFullyContained) {
                        focusCandidate = view;
                        foundFullyContainedFocusable = true;
                    } else if (viewIsCloserToBoundary) {
                        focusCandidate = view;
                    }
                }
            }
        }
        return focusCandidate;
    }

    public boolean pageScroll(int direction) {
        boolean down = direction == 130;
        int height = getHeight();
        if (down) {
            this.mTempRect.top = getScrollY() + height;
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                int bottom = view.getBottom() + ((FrameLayout.LayoutParams) view.getLayoutParams()).bottomMargin + getPaddingBottom();
                if (this.mTempRect.top + height > bottom) {
                    this.mTempRect.top = bottom - height;
                }
            }
        } else {
            this.mTempRect.top = getScrollY() - height;
            if (this.mTempRect.top < 0) {
                this.mTempRect.top = 0;
            }
        }
        this.mTempRect.bottom = this.mTempRect.top + height;
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    public boolean fullScroll(int direction) {
        int count;
        boolean down = direction == 130;
        int height = getHeight();
        this.mTempRect.top = 0;
        this.mTempRect.bottom = height;
        if (down && (count = getChildCount()) > 0) {
            View view = getChildAt(count - 1);
            this.mTempRect.bottom = view.getBottom() + ((FrameLayout.LayoutParams) view.getLayoutParams()).bottomMargin + getPaddingBottom();
            this.mTempRect.top = this.mTempRect.bottom - height;
        }
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    private boolean scrollAndFocus(int direction, int top, int bottom) {
        boolean handled = true;
        int height = getHeight();
        int containerTop = getScrollY();
        int containerBottom = containerTop + height;
        boolean up = direction == 33;
        View newFocused = findFocusableViewInBounds(up, top, bottom);
        if (newFocused == null) {
            newFocused = this;
        }
        if (top < containerTop || bottom > containerBottom) {
            doScrollY(up ? top - containerTop : bottom - containerBottom);
        } else {
            handled = false;
        }
        if (newFocused != findFocus()) {
            newFocused.requestFocus(direction);
        }
        return handled;
    }

    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this) {
            currentFocused = null;
        }
        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
        int maxJump = getMaxScrollAmount();
        if (nextFocused == null || !isWithinDeltaOfScreen(nextFocused, maxJump, getHeight())) {
            int scrollDelta = maxJump;
            if (direction == 33 && getScrollY() < scrollDelta) {
                scrollDelta = getScrollY();
            } else if (direction == 130 && getChildCount() > 0) {
                View child = getChildAt(0);
                scrollDelta = Math.min((child.getBottom() + ((FrameLayout.LayoutParams) child.getLayoutParams()).bottomMargin) - ((getScrollY() + getHeight()) - getPaddingBottom()), maxJump);
            }
            if (scrollDelta == 0) {
                return false;
            }
            doScrollY(direction == 130 ? scrollDelta : -scrollDelta);
        } else {
            nextFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(nextFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
            nextFocused.requestFocus(direction);
        }
        if (currentFocused == null || !currentFocused.isFocused() || !isOffScreen(currentFocused)) {
            return true;
        }
        int descendantFocusability = getDescendantFocusability();
        setDescendantFocusability(131072);
        requestFocus();
        setDescendantFocusability(descendantFocusability);
        return true;
    }

    private boolean isOffScreen(View descendant) {
        return !isWithinDeltaOfScreen(descendant, 0, getHeight());
    }

    private boolean isWithinDeltaOfScreen(View descendant, int delta, int height) {
        descendant.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        return this.mTempRect.bottom + delta >= getScrollY() && this.mTempRect.top - delta <= getScrollY() + height;
    }

    private void doScrollY(int delta) {
        if (delta == 0) {
            return;
        }
        if (this.mSmoothScrollingEnabled) {
            smoothScrollBy(0, delta);
        } else {
            scrollBy(0, delta);
        }
    }

    public final void smoothScrollBy(int dx, int dy) {
        if (getChildCount() != 0) {
            if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
                View child = getChildAt(0);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                int childSize = child.getHeight() + lp.topMargin + lp.bottomMargin;
                int parentSpace = (getHeight() - getPaddingTop()) - getPaddingBottom();
                int scrollY = getScrollY();
                this.mScroller.startScroll(getScrollX(), scrollY, 0, Math.max(0, Math.min(scrollY + dy, Math.max(0, childSize - parentSpace))) - scrollY);
                runAnimatedScroll(false);
            } else {
                if (!this.mScroller.isFinished()) {
                    abortAnimatedScroll();
                }
                scrollBy(dx, dy);
            }
            this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
        }
    }

    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeVerticalScrollRange() {
        int count = getChildCount();
        int parentSpace = (getHeight() - getPaddingBottom()) - getPaddingTop();
        if (count == 0) {
            return parentSpace;
        }
        View child = getChildAt(0);
        int scrollRange = child.getBottom() + ((FrameLayout.LayoutParams) child.getLayoutParams()).bottomMargin;
        int scrollY = getScrollY();
        int overscrollBottom = Math.max(0, scrollRange - parentSpace);
        if (scrollY < 0) {
            return scrollRange - scrollY;
        }
        if (scrollY > overscrollBottom) {
            return scrollRange + (scrollY - overscrollBottom);
        }
        return scrollRange;
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    /* access modifiers changed from: protected */
    public void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed, lp.width), View.MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, 0));
    }

    public void computeScroll() {
        if (!this.mScroller.isFinished()) {
            this.mScroller.computeScrollOffset();
            int y = this.mScroller.getCurrY();
            int unconsumed = y - this.mLastScrollerY;
            this.mLastScrollerY = y;
            boolean canOverscroll = true;
            this.mScrollConsumed[1] = 0;
            dispatchNestedPreScroll(0, unconsumed, this.mScrollConsumed, (int[]) null, 1);
            int unconsumed2 = unconsumed - this.mScrollConsumed[1];
            int range = getScrollRange();
            if (unconsumed2 != 0) {
                int oldScrollY = getScrollY();
                overScrollByCompat(0, unconsumed2, getScrollX(), oldScrollY, 0, range, 0, 0, false);
                int scrolledByMe = getScrollY() - oldScrollY;
                int unconsumed3 = unconsumed2 - scrolledByMe;
                this.mScrollConsumed[1] = 0;
                dispatchNestedScroll(0, scrolledByMe, 0, unconsumed3, this.mScrollOffset, 1, this.mScrollConsumed);
                unconsumed2 = unconsumed3 - this.mScrollConsumed[1];
            }
            if (unconsumed2 != 0) {
                int mode = getOverScrollMode();
                if (mode != 0 && (mode != 1 || range <= 0)) {
                    canOverscroll = false;
                }
                if (canOverscroll) {
                    ensureGlows();
                    if (unconsumed2 < 0) {
                        if (this.mEdgeGlowTop.isFinished()) {
                            this.mEdgeGlowTop.onAbsorb((int) this.mScroller.getCurrVelocity());
                        }
                    } else if (this.mEdgeGlowBottom.isFinished()) {
                        this.mEdgeGlowBottom.onAbsorb((int) this.mScroller.getCurrVelocity());
                    }
                }
                abortAnimatedScroll();
            }
            if (!this.mScroller.isFinished()) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    private void runAnimatedScroll(boolean participateInNestedScrolling) {
        if (participateInNestedScrolling) {
            startNestedScroll(2, 1);
        } else {
            stopNestedScroll(1);
        }
        this.mLastScrollerY = getScrollY();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void abortAnimatedScroll() {
        this.mScroller.abortAnimation();
        stopNestedScroll(1);
    }

    private void scrollToChild(View child) {
        child.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(child, this.mTempRect);
        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
        if (scrollDelta != 0) {
            scrollBy(0, scrollDelta);
        }
    }

    private boolean scrollToChildRect(Rect rect, boolean immediate) {
        int delta = computeScrollDeltaToGetChildRectOnScreen(rect);
        boolean scroll = delta != 0;
        if (scroll) {
            if (immediate) {
                scrollBy(0, delta);
            } else {
                smoothScrollBy(0, delta);
            }
        }
        return scroll;
    }

    /* access modifiers changed from: protected */
    public int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        int scrollYDelta;
        int scrollYDelta2;
        if (getChildCount() == 0) {
            return 0;
        }
        int height = getHeight();
        int screenTop = getScrollY();
        int screenBottom = screenTop + height;
        int actualScreenBottom = screenBottom;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (rect.top > 0) {
            screenTop += fadingEdge;
        }
        View child = getChildAt(0);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if (rect.bottom < child.getHeight() + lp.topMargin + lp.bottomMargin) {
            screenBottom -= fadingEdge;
        }
        if (rect.bottom > screenBottom && rect.top > screenTop) {
            if (rect.height() > height) {
                scrollYDelta2 = 0 + (rect.top - screenTop);
            } else {
                scrollYDelta2 = 0 + (rect.bottom - screenBottom);
            }
            return Math.min(scrollYDelta2, (child.getBottom() + lp.bottomMargin) - actualScreenBottom);
        } else if (rect.top >= screenTop || rect.bottom >= screenBottom) {
            return 0;
        } else {
            if (rect.height() > height) {
                scrollYDelta = 0 - (screenBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (screenTop - rect.top);
            }
            return Math.max(scrollYDelta, -getScrollY());
        }
    }

    public void requestChildFocus(View child, View focused) {
        if (!this.mIsLayoutDirty) {
            scrollToChild(focused);
        } else {
            this.mChildToScrollTo = focused;
        }
        super.requestChildFocus(child, focused);
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View nextFocus;
        if (direction == 2) {
            direction = 130;
        } else if (direction == 1) {
            direction = 33;
        }
        if (previouslyFocusedRect == null) {
            nextFocus = FocusFinder.getInstance().findNextFocus(this, (View) null, direction);
        } else {
            nextFocus = FocusFinder.getInstance().findNextFocusFromRect(this, previouslyFocusedRect, direction);
        }
        if (nextFocus != null && !isOffScreen(nextFocus)) {
            return nextFocus.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
        return scrollToChildRect(rectangle, immediate);
    }

    public void requestLayout() {
        this.mIsLayoutDirty = true;
        super.requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mIsLayoutDirty = false;
        if (this.mChildToScrollTo != null && isViewDescendantOf(this.mChildToScrollTo, this)) {
            scrollToChild(this.mChildToScrollTo);
        }
        this.mChildToScrollTo = null;
        if (!this.mIsLaidOut) {
            if (this.mSavedState != null) {
                scrollTo(getScrollX(), this.mSavedState.scrollPosition);
                this.mSavedState = null;
            }
            int childSize = 0;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                childSize = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            }
            int parentSpace = ((b - t) - getPaddingTop()) - getPaddingBottom();
            int currentScrollY = getScrollY();
            int newScrollY = clamp(currentScrollY, parentSpace, childSize);
            if (newScrollY != currentScrollY) {
                scrollTo(getScrollX(), newScrollY);
            }
        }
        scrollTo(getScrollX(), getScrollY());
        this.mIsLaidOut = true;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsLaidOut = false;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View currentFocused = findFocus();
        if (currentFocused != null && this != currentFocused && isWithinDeltaOfScreen(currentFocused, 0, oldh)) {
            currentFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
        }
    }

    private static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        if (!(theParent instanceof ViewGroup) || !isViewDescendantOf((View) theParent, parent)) {
            return false;
        }
        return true;
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            this.mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            runAnimatedScroll(true);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll(0);
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
    }

    public void scrollTo(int x, int y) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            int x2 = clamp(x, (getWidth() - getPaddingLeft()) - getPaddingRight(), child.getWidth() + lp.leftMargin + lp.rightMargin);
            int y2 = clamp(y, (getHeight() - getPaddingTop()) - getPaddingBottom(), child.getHeight() + lp.topMargin + lp.bottomMargin);
            if (x2 != getScrollX() || y2 != getScrollY()) {
                super.scrollTo(x2, y2);
            }
        }
    }

    private void ensureGlows() {
        if (getOverScrollMode() == 2) {
            this.mEdgeGlowTop = null;
            this.mEdgeGlowBottom = null;
        } else if (this.mEdgeGlowTop == null) {
            Context context = getContext();
            this.mEdgeGlowTop = new EdgeEffect(context);
            this.mEdgeGlowBottom = new EdgeEffect(context);
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mEdgeGlowTop != null) {
            int scrollY = getScrollY();
            if (!this.mEdgeGlowTop.isFinished()) {
                int restoreCount = canvas.save();
                int width = getWidth();
                int height = getHeight();
                int xTranslation = 0;
                int yTranslation = Math.min(0, scrollY);
                if (Build.VERSION.SDK_INT < 21 || getClipToPadding()) {
                    width -= getPaddingLeft() + getPaddingRight();
                    xTranslation = 0 + getPaddingLeft();
                }
                if (Build.VERSION.SDK_INT >= 21 && getClipToPadding()) {
                    height -= getPaddingTop() + getPaddingBottom();
                    yTranslation += getPaddingTop();
                }
                canvas.translate((float) xTranslation, (float) yTranslation);
                this.mEdgeGlowTop.setSize(width, height);
                if (this.mEdgeGlowTop.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                int restoreCount2 = canvas.save();
                int width2 = getWidth();
                int height2 = getHeight();
                int xTranslation2 = 0;
                int yTranslation2 = Math.max(getScrollRange(), scrollY) + height2;
                if (Build.VERSION.SDK_INT < 21 || getClipToPadding()) {
                    width2 -= getPaddingLeft() + getPaddingRight();
                    xTranslation2 = 0 + getPaddingLeft();
                }
                if (Build.VERSION.SDK_INT >= 21 && getClipToPadding()) {
                    height2 -= getPaddingTop() + getPaddingBottom();
                    yTranslation2 -= getPaddingBottom();
                }
                canvas.translate((float) (xTranslation2 - width2), (float) yTranslation2);
                canvas.rotate(180.0f, (float) width2, 0.0f);
                this.mEdgeGlowBottom.setSize(width2, height2);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount2);
            }
        }
    }

    private static int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            return 0;
        }
        if (my + n > child) {
            return child - my;
        }
        return n;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mSavedState = ss;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.scrollPosition = getScrollY();
        return ss;
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public int scrollPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source) {
            super(source);
            this.scrollPosition = source.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.scrollPosition);
        }

        public String toString() {
            return "HorizontalScrollView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " scrollPosition=" + this.scrollPosition + "}";
        }
    }

    static class AccessibilityDelegate extends AccessibilityDelegateCompat {
        AccessibilityDelegate() {
        }

        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }
            NestedScrollView nsvHost = (NestedScrollView) host;
            if (!nsvHost.isEnabled()) {
                return false;
            }
            if (action == 4096) {
                int targetScrollY = Math.min(nsvHost.getScrollY() + ((nsvHost.getHeight() - nsvHost.getPaddingBottom()) - nsvHost.getPaddingTop()), nsvHost.getScrollRange());
                if (targetScrollY == nsvHost.getScrollY()) {
                    return false;
                }
                nsvHost.smoothScrollTo(0, targetScrollY);
                return true;
            } else if (action != 8192) {
                return false;
            } else {
                int targetScrollY2 = Math.max(nsvHost.getScrollY() - ((nsvHost.getHeight() - nsvHost.getPaddingBottom()) - nsvHost.getPaddingTop()), 0);
                if (targetScrollY2 == nsvHost.getScrollY()) {
                    return false;
                }
                nsvHost.smoothScrollTo(0, targetScrollY2);
                return true;
            }
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            int scrollRange;
            super.onInitializeAccessibilityNodeInfo(host, info);
            NestedScrollView nsvHost = (NestedScrollView) host;
            info.setClassName(ScrollView.class.getName());
            if (nsvHost.isEnabled() && (scrollRange = nsvHost.getScrollRange()) > 0) {
                info.setScrollable(true);
                if (nsvHost.getScrollY() > 0) {
                    info.addAction(8192);
                }
                if (nsvHost.getScrollY() < scrollRange) {
                    info.addAction(4096);
                }
            }
        }

        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            NestedScrollView nsvHost = (NestedScrollView) host;
            event.setClassName(ScrollView.class.getName());
            event.setScrollable(nsvHost.getScrollRange() > 0);
            event.setScrollX(nsvHost.getScrollX());
            event.setScrollY(nsvHost.getScrollY());
            AccessibilityRecordCompat.setMaxScrollX(event, nsvHost.getScrollX());
            AccessibilityRecordCompat.setMaxScrollY(event, nsvHost.getScrollRange());
        }
    }
}