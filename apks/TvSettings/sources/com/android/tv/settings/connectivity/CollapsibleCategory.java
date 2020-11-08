package com.android.tv.settings.connectivity;

import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.util.AttributeSet;

public class CollapsibleCategory extends PreferenceCategory {
    private static final int COLLAPSED_ITEM_COUNT = 3;
    private boolean mCollapsed = true;

    public CollapsibleCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CollapsibleCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CollapsibleCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsibleCategory(Context context) {
        super(context);
    }

    public int getPreferenceCount() {
        if (!this.mCollapsed || !shouldShowCollapsePref()) {
            return super.getPreferenceCount();
        }
        return 3;
    }

    public int getRealPreferenceCount() {
        return super.getPreferenceCount();
    }

    public boolean shouldShowCollapsePref() {
        return super.getPreferenceCount() >= 4;
    }

    public boolean isCollapsed() {
        return this.mCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.mCollapsed = collapsed;
        notifyHierarchyChanged();
    }
}
