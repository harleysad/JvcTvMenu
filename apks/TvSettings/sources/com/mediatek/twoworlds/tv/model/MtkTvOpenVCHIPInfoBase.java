package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvOpenVCHIPInfoBase {
    private static final String TAG = "MtkTvOpenVCHIPInfoBase";
    private int dimNum;
    private String dimText;
    private boolean isDimGrad;
    private int levelNum;
    private String levelText;
    private String lvlAbbrText;
    private int regionId;
    private int regionNum;
    private String regionText;
    private int regionVersion;

    public int getRegionNum() {
        return this.regionNum;
    }

    public void setRegionNum(int regionNum2) {
        this.regionNum = regionNum2;
    }

    public int getDimNum() {
        return this.dimNum;
    }

    public void setDimNum(int dimNum2) {
        this.dimNum = dimNum2;
    }

    public int getLevelNum() {
        return this.levelNum;
    }

    public void setLevelNum(int levelNum2) {
        this.levelNum = levelNum2;
    }

    public int getRegionVersion() {
        return this.regionVersion;
    }

    public void setRegionVersion(int regionVersion2) {
        this.regionVersion = regionVersion2;
    }

    public int getRegionId() {
        return this.regionId;
    }

    public void setRegionId(int regionId2) {
        this.regionId = regionId2;
    }

    public boolean isDimGrad() {
        return this.isDimGrad;
    }

    public void setDimGrad(boolean isDimGrad2) {
        this.isDimGrad = isDimGrad2;
    }

    public String getRegionText() {
        return this.regionText;
    }

    public void setRegionText(String regionText2) {
        Log.d(TAG, "setRegionText:" + regionText2);
        this.regionText = regionText2;
    }

    public String getDimText() {
        return this.dimText;
    }

    public void setDimText(String dimText2) {
        Log.d(TAG, "setDimText:" + dimText2);
        this.dimText = dimText2;
    }

    public String getLevelText() {
        return this.levelText;
    }

    public void setLevelText(String levelText2) {
        Log.d(TAG, "setLevelText:" + levelText2);
        this.levelText = levelText2;
    }

    public String getLvlAbbrText() {
        Log.d(TAG, "getLvlAbbrText:" + this.lvlAbbrText);
        return this.lvlAbbrText;
    }

    public void setLvlAbbrText(String lvlAbbrText2) {
        Log.d(TAG, "setLvlAbbrText:" + lvlAbbrText2);
        this.lvlAbbrText = lvlAbbrText2;
    }
}
