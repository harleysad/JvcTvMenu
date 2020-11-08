package com.mediatek.twoworlds.tv.model;

public class MtkTvExternalUIStatusBase {
    public static final int EXTERNAL_UI_ID_MENU = 0;
    public static final int EXTERNAL_UI_ID_SUBTITLE_OPT_LST = 1;
    public static final int EXTERNAL_UI_ID_UNKNOW = -1;
    private static final String TAG = "MtkTvExternalUIStatusBase";
    protected boolean isShow;
    protected int uiId;

    public MtkTvExternalUIStatusBase() {
        this.uiId = -1;
        this.isShow = false;
    }

    public MtkTvExternalUIStatusBase(int uiId2, boolean isShow2) {
        this.uiId = uiId2;
        this.isShow = isShow2;
    }

    public void setUIId(int uiId2) {
        this.uiId = uiId2;
    }

    public void setShow(boolean isShow2) {
        this.isShow = isShow2;
    }

    public int getUIId() {
        return this.uiId;
    }

    public boolean getShow() {
        return this.isShow;
    }
}
