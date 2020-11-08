package com.android.tv.ui.sidepanel;

public abstract class SubMenuItem extends ActionItem {
    private final SideFragmentManager mSideFragmentManager;

    /* access modifiers changed from: protected */
    public abstract SideFragment getFragment();

    public SubMenuItem(String title, SideFragmentManager fragmentManager) {
        this(title, (String) null, fragmentManager);
    }

    public SubMenuItem(String title, String description, SideFragmentManager fragmentManager) {
        super(title, description);
        this.mSideFragmentManager = fragmentManager;
    }

    /* access modifiers changed from: protected */
    public void onSelected() {
        launchFragment();
    }

    /* access modifiers changed from: protected */
    public void launchFragment() {
        this.mSideFragmentManager.show(getFragment());
    }
}
