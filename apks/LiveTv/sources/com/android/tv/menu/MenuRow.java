package com.android.tv.menu;

import android.content.Context;

public abstract class MenuRow {
    protected final Context mContext;
    private final int mHeight;
    private final Menu mMenu;
    private MenuRowView mMenuRowView;
    private final String mTitle;

    public abstract String getId();

    public abstract int getLayoutResId();

    public abstract void update();

    public MenuRow(Context context, Menu menu, int titleResId, int heightResId) {
        this(context, menu, context.getString(titleResId), heightResId);
    }

    public MenuRow(Context context, Menu menu, String title, int heightResId) {
        this.mContext = context;
        this.mTitle = title;
        this.mMenu = menu;
        this.mHeight = context.getResources().getDimensionPixelSize(heightResId);
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    public Menu getMenu() {
        return this.mMenu;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setMenuRowView(MenuRowView menuRowView) {
        this.mMenuRowView = menuRowView;
    }

    /* access modifiers changed from: protected */
    public MenuRowView getMenuRowView() {
        return this.mMenuRowView;
    }

    public boolean isVisible() {
        return true;
    }

    public void release() {
    }

    public void onRecentChannelsChanged() {
    }

    public void onStreamInfoChanged() {
    }

    public boolean hideTitleWhenSelected() {
        return false;
    }
}
