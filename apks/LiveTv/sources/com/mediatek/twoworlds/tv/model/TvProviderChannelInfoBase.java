package com.mediatek.twoworlds.tv.model;

import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;

public class TvProviderChannelInfoBase {
    private static final String TAG = "TvProviderChannelInfo";
    protected int mBrowsableMask;
    protected int mChannelId;
    protected String mChannelNumber;
    protected String mDisplayName;
    protected int mDisplayNumber;
    protected int mGoogleType;
    protected int mHashcode;
    protected int mLockedMask;
    protected int mMajorNumber;
    protected int mMinorNumber;
    protected int mNetworkMask;
    protected int mOption2Mask;
    protected int mOptionMask;
    protected int mOriginalNetworkId;
    protected int mProgramNumber;
    protected int mServiceType;
    protected int mSvlId;
    protected int mSvlRecId;
    protected int mTransportStreamId;
    protected int mType;

    public int getBrowsableMask() {
        return this.mBrowsableMask;
    }

    public void setBrowsableMask(int mBrowsableMask2) {
        this.mBrowsableMask = mBrowsableMask2;
    }

    public int getLockedMask() {
        return this.mLockedMask;
    }

    public void setLockedMask(int mLockedMask2) {
        this.mLockedMask = mLockedMask2;
    }

    public int getSvlId() {
        return this.mSvlId;
    }

    /* access modifiers changed from: protected */
    public void setSvlId(int mSvlId2) {
        this.mSvlId = mSvlId2;
    }

    public int getSvlRecId() {
        return this.mSvlRecId;
    }

    /* access modifiers changed from: protected */
    public void setSvlRecId(int mSvlRecId2) {
        this.mSvlRecId = mSvlRecId2;
    }

    public int getHashcode() {
        return this.mHashcode;
    }

    public void setHashcode(int mHashcode2) {
        this.mHashcode = mHashcode2;
    }

    public int getbroadcastType() {
        return this.mType;
    }

    public void setbroadcastType(int mType2) {
        this.mType = mType2;
    }

    public int getGoogBrdcstType() {
        return this.mGoogleType;
    }

    public void setGoogBrdcstType(int mGoogleType2) {
        this.mGoogleType = mGoogleType2;
    }

    public int getServiceType() {
        return this.mServiceType;
    }

    public void setServiceType(int mServiceType2) {
        this.mServiceType = mServiceType2;
    }

    public int getOriginalNetworkId() {
        return this.mOriginalNetworkId;
    }

    public void setOriginalNetworkId(int mOriginalNetworkId2) {
        this.mOriginalNetworkId = mOriginalNetworkId2;
    }

    public int getTransportStreamId() {
        return this.mTransportStreamId;
    }

    public void setTransportStreamId(int mTransportStreamId2) {
        this.mTransportStreamId = mTransportStreamId2;
    }

    public int getProgramNumber() {
        return this.mProgramNumber;
    }

    public void setProgramNumber(int mProgramNumber2) {
        this.mProgramNumber = mProgramNumber2;
    }

    public int getDisplayNumber() {
        return this.mDisplayNumber;
    }

    public void setDisplayNumber(int mDisplayNumber2) {
        this.mDisplayNumber = mDisplayNumber2;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public void setDisplayName(String mDisplayName2) {
        this.mDisplayName = mDisplayName2;
    }

    public int getMajorNumber() {
        return this.mMajorNumber;
    }

    public void setMajorNumber(int mMajorNumber2) {
        this.mMajorNumber = mMajorNumber2;
    }

    public int getMinorNumber() {
        return this.mMinorNumber;
    }

    public void setMinorNumber(int mMinorNumber2) {
        this.mMinorNumber = mMinorNumber2;
    }

    public String getChannelNumber() {
        if (this.mType == 0 || 3 == this.mType) {
            if ((this.mChannelId & 128) == 0) {
                this.mChannelNumber = Integer.toString(this.mMajorNumber) + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + Integer.toString(this.mMinorNumber);
            } else {
                this.mChannelNumber = Integer.toString(this.mMajorNumber);
            }
        } else if (5 != this.mType) {
            this.mChannelNumber = Integer.toString(this.mDisplayNumber);
        } else if ((this.mChannelId & 128) == 0) {
            this.mChannelNumber = Integer.toString(this.mMajorNumber) + "." + Integer.toString(this.mMinorNumber);
        } else {
            this.mChannelNumber = Integer.toString(this.mMajorNumber);
        }
        return this.mChannelNumber;
    }

    public int getChannelId() {
        return this.mChannelId;
    }

    public void setChannelId(int mChannelId2) {
        this.mChannelId = mChannelId2;
    }

    public int getNetworkMask() {
        return this.mNetworkMask;
    }

    public void setNetworkMask(int mNetworkMask2) {
        this.mNetworkMask = mNetworkMask2;
    }

    public int getOptionMask() {
        return this.mOptionMask;
    }

    public void setOptionMask(int mOptionMask2) {
        this.mOptionMask = mOptionMask2;
    }

    public int getOption2Mask() {
        return this.mOption2Mask;
    }

    public void setOption2Mask(int mOption2Mask2) {
        this.mOption2Mask = mOption2Mask2;
    }

    public String toString() {
        return "MtkTvChannelInfo info mSvlId=" + this.mSvlId + " , mSvlRecId=" + this.mSvlRecId + " , mHashcode=" + this.mHashcode + " , mType=" + this.mType + " , mOriginalNetworkId=" + this.mOriginalNetworkId + " , mTransportStreamId=" + this.mTransportStreamId + " , mProgramNumber=" + this.mProgramNumber + " , mDisplayNumber=" + this.mDisplayNumber + " , mDisplayName=" + this.mDisplayName + " , mChannelNumber=" + this.mChannelNumber + " , mChannelId=" + this.mChannelId + " , mGoogleType=" + this.mGoogleType + " , mBrowsableMask=" + this.mBrowsableMask + " , mLockedMask=" + this.mLockedMask + "]\n";
    }
}
