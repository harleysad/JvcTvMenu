package com.mediatek.wwtv.setting.util;

import java.io.Serializable;

public class TransItem implements Serializable {
    private static final long serialVersionUID = -115591532090430814L;
    public int mEndValue;
    public int mInitValue;
    public String mItemID;
    public int mStartValue;
    public String mTitle;

    public TransItem(String id, String title, int initVlaue, int startValue, int endValue) {
        this.mItemID = id;
        this.mTitle = title;
        this.mStartValue = startValue;
        this.mInitValue = initVlaue;
        this.mEndValue = endValue;
    }

    public String getmItemId() {
        return this.mItemID;
    }

    public void setmItemId(String mItemID2) {
        this.mItemID = mItemID2;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String mTitle2) {
        this.mTitle = mTitle2;
    }
}
