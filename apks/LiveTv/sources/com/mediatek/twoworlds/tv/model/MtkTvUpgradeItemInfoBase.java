package com.mediatek.twoworlds.tv.model;

public class MtkTvUpgradeItemInfoBase {
    private String mCheckSum;
    private String mDevPath;
    private String mFileName;
    private MtkTvUpgradeMethodBase mMethod;
    private String mName;
    private long mOffset;
    private long mSize;
    private String mTag;
    private String mVer;

    public MtkTvUpgradeItemInfoBase() {
        this.mTag = null;
        this.mDevPath = null;
        this.mMethod = null;
        this.mName = null;
        this.mFileName = null;
        this.mVer = null;
        this.mCheckSum = null;
        this.mSize = 0;
        this.mOffset = 0;
    }

    public MtkTvUpgradeItemInfoBase(String tag, String devPath, MtkTvUpgradeMethodBase method, String name, String fileName, String version, String checkSum, long size, long offset) {
        this.mTag = tag;
        this.mDevPath = devPath;
        this.mMethod = method;
        this.mName = name;
        this.mFileName = fileName;
        this.mVer = version;
        this.mCheckSum = checkSum;
        this.mSize = size;
        this.mOffset = offset;
    }

    public String getTag() {
        return this.mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public String getDevicePath() {
        return this.mDevPath;
    }

    public void setDevicePath(String devicePath) {
        this.mDevPath = devicePath;
    }

    public MtkTvUpgradeMethodBase getMethod() {
        return this.mMethod;
    }

    public void setMethod(MtkTvUpgradeMethodBase method) {
        this.mMethod = method;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getVersion() {
        return this.mVer;
    }

    public void setVersion(String version) {
        this.mVer = version;
    }

    public String getCheckSum() {
        return this.mCheckSum;
    }

    public void setCheckSum(String checkSum) {
        this.mCheckSum = checkSum;
    }

    public long getSize() {
        return this.mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public long getOffset() {
        return this.mOffset;
    }

    public void setOffset(long offset) {
        this.mOffset = offset;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nUpgradeItemInfo\n\tTag: ");
        sb.append(this.mTag == null ? "" : this.mTag);
        sb.append("\n\tDevPath: ");
        sb.append(this.mDevPath == null ? "" : this.mDevPath);
        sb.append("\n\tMethod: ");
        sb.append(this.mMethod);
        sb.append("\n\tName: ");
        sb.append(this.mName == null ? "" : this.mName);
        sb.append("\n\tFileName: ");
        sb.append(this.mFileName == null ? "" : this.mFileName);
        sb.append("\n\tVersion: ");
        sb.append(this.mVer == null ? "" : this.mVer);
        sb.append("\n\tCheckSum: ");
        sb.append(this.mCheckSum == null ? "" : this.mCheckSum);
        sb.append("\n\tSize: ");
        sb.append(this.mSize);
        sb.append("\n\tOffset: ");
        sb.append(this.mOffset);
        return sb.toString();
    }
}
