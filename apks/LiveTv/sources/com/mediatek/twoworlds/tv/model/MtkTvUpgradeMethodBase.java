package com.mediatek.twoworlds.tv.model;

public enum MtkTvUpgradeMethodBase {
    PARTITION(0),
    FILE(1);
    
    private int mMethodValue;

    private MtkTvUpgradeMethodBase(int methodValue) {
        this.mMethodValue = methodValue;
    }

    public int getMethodValue() {
        return this.mMethodValue;
    }

    public String toString() {
        switch (this.mMethodValue) {
            case 0:
                return "Partition";
            case 1:
                return "File";
            default:
                return "Unknown";
        }
    }
}
