package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvOpenVCHIPSettingInfoBase {
    public static final int LEVEL_BLOCK_LEN = 255;
    private static final String TAG = "MtkTvOpenVCHIPSettingInfo";
    private int dimIndex;
    private byte[] lvlBlockData = new byte[255];
    private int regionIndex;
    private int unratedBlock;

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

    public int getUnratedBlock() {
        return this.unratedBlock;
    }

    public void setUnratedBlock(int unratedBlock2) {
        this.unratedBlock = unratedBlock2;
    }

    public byte[] getLvlBlockData() {
        return this.lvlBlockData;
    }

    public void setLvlBlockData(byte[] lvlBlockData2) {
        if (lvlBlockData2 == null) {
            Log.d(TAG, "lvlBlockData is null \n");
        } else if (lvlBlockData2.length > 255) {
            Log.d(TAG, "setLvlBlockData fail, length more than 255!\n");
        } else {
            System.arraycopy(lvlBlockData2, 0, this.lvlBlockData, 0, lvlBlockData2.length);
        }
    }
}
