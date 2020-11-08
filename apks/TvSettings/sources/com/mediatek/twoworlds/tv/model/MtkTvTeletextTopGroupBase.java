package com.mediatek.twoworlds.tv.model;

public class MtkTvTeletextTopGroupBase {
    private boolean groupHasName = false;
    private String groupName = "";
    private MtkTvTeletextPageBase groupPageAddr = new MtkTvTeletextPageBase();

    public MtkTvTeletextPageBase getGroupPageAddr() {
        return this.groupPageAddr;
    }

    public void setGroupPageAddr(MtkTvTeletextPageBase groupPageAddr2) {
        this.groupPageAddr = groupPageAddr2;
    }

    public boolean isGroupHasName() {
        return this.groupHasName;
    }

    public void setGroupHasName(boolean groupHasName2) {
        this.groupHasName = groupHasName2;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName2) {
        this.groupName = groupName2;
    }
}
