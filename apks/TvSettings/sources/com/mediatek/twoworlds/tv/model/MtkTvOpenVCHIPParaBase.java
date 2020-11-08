package com.mediatek.twoworlds.tv.model;

public class MtkTvOpenVCHIPParaBase {
    public static final int OPEN_VCHIP_KEY_GET_DIM_GRAD = 6;
    public static final int OPEN_VCHIP_KEY_GET_DIM_NUM = 4;
    public static final int OPEN_VCHIP_KEY_GET_DIM_TEXT = 5;
    public static final int OPEN_VCHIP_KEY_GET_LVL_ABBR = 8;
    public static final int OPEN_VCHIP_KEY_GET_LVL_NUM = 7;
    public static final int OPEN_VCHIP_KEY_GET_LVL_TEXT = 9;
    public static final int OPEN_VCHIP_KEY_GET_RGN_ID = 3;
    public static final int OPEN_VCHIP_KEY_GET_RGN_NUM = 0;
    public static final int OPEN_VCHIP_KEY_GET_RGN_TEXT = 1;
    public static final int OPEN_VCHIP_KEY_GET_RGN_VER = 2;
    private int dimIndex;
    private int levelIndex;
    private int openVCHIPParaType;
    private int regionIndex;

    public int getOpenVCHIPParaType() {
        return this.openVCHIPParaType;
    }

    public void setOpenVCHIPParaType(int openVCHIPParaType2) {
        this.openVCHIPParaType = openVCHIPParaType2;
    }

    public int getRegionIndex() {
        return this.regionIndex;
    }

    public void setRegionIndex(int regionIndex2) {
        this.regionIndex = regionIndex2;
    }

    public int getDimIndex() {
        return this.dimIndex;
    }

    public void setDimIndex(int dimIndex2) {
        this.dimIndex = dimIndex2;
    }

    public int getLevelIndex() {
        return this.levelIndex;
    }

    public void setLevelIndex(int levelIndex2) {
        this.levelIndex = levelIndex2;
    }
}
