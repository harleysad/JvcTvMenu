package com.mediatek.twoworlds.tv.model;

import java.util.ArrayList;
import java.util.Iterator;

public class MtkTvUpgradeFirmwareInfoBase {
    private String mBuildDate;
    private String mBuildName;
    private String mHwVer;
    private String mIcVer;
    private String mManufacturer;
    private String mModel;
    private String mSerialNum;
    ArrayList<MtkTvUpgradeItemInfoBase> mUpgradeItemList;

    public MtkTvUpgradeFirmwareInfoBase() {
        this.mManufacturer = null;
        this.mModel = null;
        this.mIcVer = null;
        this.mHwVer = null;
        this.mBuildName = null;
        this.mBuildDate = null;
        this.mSerialNum = null;
        this.mUpgradeItemList = new ArrayList<>();
    }

    public MtkTvUpgradeFirmwareInfoBase(String manufacturer, String model, String icVer, String hwVer, String buildName, String buildDate, String serialNum, ArrayList<MtkTvUpgradeItemInfoBase> upgradeItemList) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mIcVer = icVer;
        this.mHwVer = hwVer;
        this.mBuildName = buildName;
        this.mBuildDate = buildDate;
        this.mSerialNum = serialNum;
        this.mUpgradeItemList = upgradeItemList;
    }

    public String getManufacturerName() {
        return this.mManufacturer;
    }

    public void setManufacturerName(String manufacturer) {
        this.mManufacturer = manufacturer;
    }

    public String getModelName() {
        return this.mModel;
    }

    public void setModelName(String model) {
        this.mModel = model;
    }

    public String getIcVersion() {
        return this.mIcVer;
    }

    public void setIcVersion(String icVer) {
        this.mIcVer = icVer;
    }

    public String getHwVersion() {
        return this.mHwVer;
    }

    public void setHwVersion(String hwVer) {
        this.mHwVer = hwVer;
    }

    public String getBuildName() {
        return this.mBuildName;
    }

    public void setBuildName(String buildName) {
        this.mBuildName = buildName;
    }

    public String getBuildDate() {
        return this.mBuildDate;
    }

    public void setBuildDate(String buildDate) {
        this.mBuildDate = buildDate;
    }

    public String getSerialNumber() {
        return this.mSerialNum;
    }

    public void setSerialNumber(String serialNum) {
        this.mSerialNum = serialNum;
    }

    public ArrayList<MtkTvUpgradeItemInfoBase> getUpgradeItemList() {
        return this.mUpgradeItemList;
    }

    public void setUpgradeItemList(ArrayList<MtkTvUpgradeItemInfoBase> upgradeItemList) {
        this.mUpgradeItemList = upgradeItemList;
    }

    public String toSting() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nUpgradeFirmwareInfo\n\t Manufactuer: ");
        sb.append(this.mManufacturer == null ? "" : this.mManufacturer);
        sb.append("\n\t Model: ");
        sb.append(this.mModel == null ? "" : this.mModel);
        sb.append("\n\t IC Version: ");
        sb.append(this.mIcVer == null ? "" : this.mIcVer);
        sb.append("\n\t Hardware Version: ");
        sb.append(this.mHwVer == null ? "" : this.mHwVer);
        sb.append("\n\t Build Name: ");
        sb.append(this.mBuildName == null ? "" : this.mBuildName);
        sb.append("\n\t Build Date: ");
        sb.append(this.mBuildDate == null ? "" : this.mBuildDate);
        sb.append("\n\t Serial Number: ");
        sb.append(this.mSerialNum == null ? "" : this.mSerialNum);
        String info = sb.toString();
        Iterator<MtkTvUpgradeItemInfoBase> it = this.mUpgradeItemList.iterator();
        while (it.hasNext()) {
            info = info + it.next().toString();
        }
        return info;
    }
}
