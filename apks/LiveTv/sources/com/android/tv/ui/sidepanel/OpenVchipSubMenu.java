package com.android.tv.ui.sidepanel;

public abstract class OpenVchipSubMenu extends SubMenuItem {
    private int mDimId = 0;
    private int mLevelId = 0;
    private int mRegionId = 0;
    private final SideFragmentManager mSideFragmentManager;
    private int mTypeId = 0;

    public OpenVchipSubMenu(String title, SideFragmentManager fragmentManager) {
        super(title, fragmentManager);
        this.mSideFragmentManager = fragmentManager;
    }

    public int getmTypeId() {
        return this.mTypeId;
    }

    public int getmRegionId() {
        return this.mRegionId;
    }

    public int getmDimId() {
        return this.mDimId;
    }

    public int getmLevelId() {
        return this.mLevelId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public OpenVchipSubMenu(String title, String description, SideFragmentManager fragmentManager, int index, int id) {
        super(title, description, fragmentManager);
        this.mSideFragmentManager = fragmentManager;
    }
}
