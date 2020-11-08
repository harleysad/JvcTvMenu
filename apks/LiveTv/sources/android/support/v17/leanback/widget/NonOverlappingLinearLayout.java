package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class NonOverlappingLinearLayout extends LinearLayout {
    boolean mDeferFocusableViewAvailableInLayout;
    boolean mFocusableViewAvailableFixEnabled;
    final ArrayList<ArrayList<View>> mSortedAvailableViews;

    public NonOverlappingLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public NonOverlappingLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NonOverlappingLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFocusableViewAvailableFixEnabled = false;
        this.mSortedAvailableViews = new ArrayList<>();
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setFocusableViewAvailableFixEnabled(boolean enabled) {
        this.mFocusableViewAvailableFixEnabled = enabled;
    }

    /*  JADX ERROR: StackOverflow in pass: MarkFinallyVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    protected void onLayout(boolean r5, int r6, int r7, int r8, int r9) {
        /*
            r4 = this;
            r0 = 0
            boolean r1 = r4.mFocusableViewAvailableFixEnabled     // Catch:{ all -> 0x009f }
            r2 = 1
            if (r1 == 0) goto L_0x0014
            int r1 = r4.getOrientation()     // Catch:{ all -> 0x009f }
            if (r1 != 0) goto L_0x0014
            int r1 = r4.getLayoutDirection()     // Catch:{ all -> 0x009f }
            if (r1 != r2) goto L_0x0014
            r1 = r2
            goto L_0x0015
        L_0x0014:
            r1 = r0
        L_0x0015:
            r4.mDeferFocusableViewAvailableInLayout = r1     // Catch:{ all -> 0x009f }
            boolean r1 = r4.mDeferFocusableViewAvailableInLayout     // Catch:{ all -> 0x009f }
            if (r1 == 0) goto L_0x004b
        L_0x001b:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            int r1 = r1.size()     // Catch:{ all -> 0x009f }
            int r3 = r4.getChildCount()     // Catch:{ all -> 0x009f }
            if (r1 <= r3) goto L_0x0034
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r3 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            int r3 = r3.size()     // Catch:{ all -> 0x009f }
            int r3 = r3 - r2
            r1.remove(r3)     // Catch:{ all -> 0x009f }
            goto L_0x001b
        L_0x0034:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            int r1 = r1.size()     // Catch:{ all -> 0x009f }
            int r2 = r4.getChildCount()     // Catch:{ all -> 0x009f }
            if (r1 >= r2) goto L_0x004b
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x009f }
            r2.<init>()     // Catch:{ all -> 0x009f }
            r1.add(r2)     // Catch:{ all -> 0x009f }
            goto L_0x0034
        L_0x004b:
            super.onLayout(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x009f }
            boolean r1 = r4.mDeferFocusableViewAvailableInLayout     // Catch:{ all -> 0x009f }
            if (r1 == 0) goto L_0x0081
            r1 = r0
        L_0x0053:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r2 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            int r2 = r2.size()     // Catch:{ all -> 0x009f }
            if (r1 >= r2) goto L_0x0081
            r2 = r0
        L_0x005c:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r3 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x009f }
            java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch:{ all -> 0x009f }
            int r3 = r3.size()     // Catch:{ all -> 0x009f }
            if (r2 >= r3) goto L_0x007e
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r3 = r4.mSortedAvailableViews     // Catch:{ all -> 0x009f }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x009f }
            java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch:{ all -> 0x009f }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x009f }
            android.view.View r3 = (android.view.View) r3     // Catch:{ all -> 0x009f }
            super.focusableViewAvailable(r3)     // Catch:{ all -> 0x009f }
            int r2 = r2 + 1
            goto L_0x005c
        L_0x007e:
            int r1 = r1 + 1
            goto L_0x0053
        L_0x0081:
            boolean r1 = r4.mDeferFocusableViewAvailableInLayout
            if (r1 == 0) goto L_0x009e
            r4.mDeferFocusableViewAvailableInLayout = r0
        L_0x0088:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x009e
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews
            java.lang.Object r1 = r1.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            r1.clear()
            int r0 = r0 + 1
            goto L_0x0088
        L_0x009e:
            return
        L_0x009f:
            r1 = move-exception
            boolean r2 = r4.mDeferFocusableViewAvailableInLayout
            if (r2 == 0) goto L_0x00bd
            r4.mDeferFocusableViewAvailableInLayout = r0
        L_0x00a7:
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r2 = r4.mSortedAvailableViews
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x00bd
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r2 = r4.mSortedAvailableViews
            java.lang.Object r2 = r2.get(r0)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            r2.clear()
            int r0 = r0 + 1
            goto L_0x00a7
        L_0x00bd:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.NonOverlappingLinearLayout.onLayout(boolean, int, int, int, int):void");
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void focusableViewAvailable(android.view.View r5) {
        /*
            r4 = this;
            boolean r0 = r4.mDeferFocusableViewAvailableInLayout
            if (r0 == 0) goto L_0x002d
            r0 = r5
            r1 = -1
            r2 = r0
            r0 = r1
        L_0x0008:
            if (r2 == r4) goto L_0x001f
            if (r2 == 0) goto L_0x001f
            android.view.ViewParent r3 = r2.getParent()
            if (r3 != r4) goto L_0x0017
            int r0 = r4.indexOfChild(r2)
            goto L_0x001f
        L_0x0017:
            android.view.ViewParent r3 = r2.getParent()
            r2 = r3
            android.view.View r2 = (android.view.View) r2
            goto L_0x0008
        L_0x001f:
            if (r0 == r1) goto L_0x002c
            java.util.ArrayList<java.util.ArrayList<android.view.View>> r1 = r4.mSortedAvailableViews
            java.lang.Object r1 = r1.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            r1.add(r5)
        L_0x002c:
            goto L_0x0030
        L_0x002d:
            super.focusableViewAvailable(r5)
        L_0x0030:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v17.leanback.widget.NonOverlappingLinearLayout.focusableViewAvailable(android.view.View):void");
    }
}
