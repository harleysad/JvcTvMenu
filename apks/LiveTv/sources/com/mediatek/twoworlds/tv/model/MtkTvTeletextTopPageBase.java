package com.mediatek.twoworlds.tv.model;

public class MtkTvTeletextTopPageBase {
    private MtkTvTeletextPageBase normalPageAddr = new MtkTvTeletextPageBase();
    private boolean normalPageHasName = false;
    private String normalPageName = "";

    public MtkTvTeletextPageBase getNormalPageAddr() {
        return this.normalPageAddr;
    }

    public void setNormalPageAddr(MtkTvTeletextPageBase normalPageAddr2) {
        this.normalPageAddr = normalPageAddr2;
    }

    public boolean isNormalPageHasName() {
        return this.normalPageHasName;
    }

    public void setNormalPageHasName(boolean normalPageHasName2) {
        this.normalPageHasName = normalPageHasName2;
    }

    public String getNormalPageName() {
        return this.normalPageName;
    }

    public void setNormalPageName(String normalPageName2) {
        this.normalPageName = normalPageName2;
    }
}
