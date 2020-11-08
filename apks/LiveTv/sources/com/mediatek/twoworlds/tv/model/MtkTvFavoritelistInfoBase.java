package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvFavoritelistInfoBase {
    private static final String TAG = "MtkTvFavoritelistInfo";
    protected int brdcstType;
    protected int caSystemId;
    protected int channelId;
    protected String channelName;
    protected String channelNumber;
    protected int inptSrcId;
    protected int nwMask;
    protected int servType;
    protected String shortName;
    protected int svlId;
    protected int svlRecId;

    public MtkTvFavoritelistInfoBase() {
        Log.d(TAG, "MtkTvFavoritelistInfo Constructor\n");
    }

    public MtkTvFavoritelistInfoBase(int svlId2, int svlRecId2) {
        Log.d(TAG, "MtkTvFavoritelistInfo Constructor(svlId = " + svlId2 + ", svlRecId = " + svlRecId2 + ")\n");
        this.svlId = svlId2;
        this.svlRecId = svlRecId2;
    }

    public String getChannelNumber() {
        return this.channelNumber;
    }

    public void setChannelNumber(String channelNumber2) {
        this.channelNumber = channelNumber2;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(String channelName2) {
        this.channelName = channelName2;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int channelId2) {
        this.channelId = channelId2;
    }

    public int getSvlId() {
        return this.svlId;
    }

    /* access modifiers changed from: protected */
    public void setSvlId(int svlId2) {
        this.svlId = svlId2;
    }

    public int getSvlRecId() {
        return this.svlRecId;
    }

    /* access modifiers changed from: protected */
    public void setSvlRecId(int svlRecId2) {
        this.svlRecId = svlRecId2;
    }

    public int getInptSrcId() {
        return this.inptSrcId;
    }

    /* access modifiers changed from: protected */
    public void setInptSrcId(int inptSrcId2) {
        this.inptSrcId = inptSrcId2;
    }

    public int getNwMask() {
        return this.nwMask;
    }

    public void setNwMask(int nwMask2) {
        this.nwMask = nwMask2;
    }

    public int getBrdcstType() {
        return this.brdcstType;
    }

    public void setBrdcstType(int brdcstType2) {
        this.brdcstType = brdcstType2;
    }

    public int getServType() {
        return this.servType;
    }

    public void setServType(int servType2) {
        this.servType = servType2;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName2) {
        this.shortName = shortName2;
    }

    public int getCaSystemId() {
        return this.caSystemId;
    }

    public void setCaSystemId(int caSystemId2) {
        this.caSystemId = caSystemId2;
    }
}
