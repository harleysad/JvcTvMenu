package com.mediatek.twoworlds.tv.model;

public class MtkTvISDBScanParaBase {
    private int configFlag;
    private int endIndex;
    private int startIndex;

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex2) {
        this.startIndex = startIndex2;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex(int endIndex2) {
        this.endIndex = endIndex2;
    }

    public int getConfigFlag() {
        return this.configFlag;
    }

    public void setConfigFlag(int configFlag2) {
        this.configFlag = configFlag2;
    }
}
