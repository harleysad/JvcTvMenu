package com.mediatek.twoworlds.tv.model;

public class MtkTvTeletextTopBlockBase {
    private boolean blockHasName = false;
    private String blockName = "";
    private MtkTvTeletextPageBase blockPageAddr = new MtkTvTeletextPageBase();

    public MtkTvTeletextPageBase getBlockPageAddr() {
        return this.blockPageAddr;
    }

    public void setBlockPageAddr(MtkTvTeletextPageBase blockPageAddr2) {
        this.blockPageAddr = blockPageAddr2;
    }

    public boolean isBlockHasName() {
        return this.blockHasName;
    }

    public void setBlockHasName(boolean blockHasName2) {
        this.blockHasName = blockHasName2;
    }

    public String getBlockName() {
        return this.blockName;
    }

    public void setBlockName(String blockName2) {
        this.blockName = blockName2;
    }
}
