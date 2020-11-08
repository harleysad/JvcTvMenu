package com.android.tv.menu.customization;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class CustomAction implements Comparable<CustomAction> {
    private static final int POSITION_THRESHOLD = 100;
    private final Drawable mIconDrawable;
    private final Intent mIntent;
    private final int mPositionPriority;
    private final String mTitle;

    public CustomAction(int positionPriority, String title, Drawable iconDrawable, Intent intent) {
        this.mPositionPriority = positionPriority;
        this.mTitle = title;
        this.mIconDrawable = iconDrawable;
        this.mIntent = intent;
    }

    public boolean isFront() {
        return this.mPositionPriority < 100;
    }

    public int compareTo(@NonNull CustomAction another) {
        return this.mPositionPriority - another.mPositionPriority;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Drawable getIconDrawable() {
        return this.mIconDrawable;
    }

    public Intent getIntent() {
        return this.mIntent;
    }
}
