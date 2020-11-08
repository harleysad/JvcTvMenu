package com.mediatek.twoworlds.tv.model;

public abstract class MtkTvCIMMIBase {
    private static final String TAG = "MtkTvCIMMI";
    protected int mmi_id;

    public MtkTvCIMMIBase(int mmi_id2) {
        this.mmi_id = mmi_id2;
    }

    public MtkTvCIMMIBase() {
    }

    public int getMMIId() {
        return this.mmi_id;
    }

    public void setMMIId(int mmi_id2) {
        this.mmi_id = mmi_id2;
    }
}
