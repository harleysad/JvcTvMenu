package com.mediatek.twoworlds.tv.model;

import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import java.util.Arrays;

public class MtkTvDvbChannelInfo extends MtkTvChannelInfoBase {
    private static final String TAG = "MtkTvDvbChannelInfo";
    private int bandWidth;
    protected int barkerMask;
    protected int categoryMask;
    protected int hbbtvStatus;
    protected int mfType;
    private int mod;
    private int nwId;
    private String nwName = "";
    private int onId;
    private int progId;
    private int satRecId;
    protected int sdtServiceType;
    private String shortName = "";
    protected String svcProName = "";
    private int symRate;
    private int tsId;

    public MtkTvDvbChannelInfo() {
    }

    public MtkTvDvbChannelInfo(String dbName) {
        this.svlId = MtkTvChCommonBase.getSvlIdByName(dbName);
    }

    public MtkTvDvbChannelInfo(int svlId, int svlRecId) {
        super(svlId, svlRecId);
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName2) {
        this.shortName = shortName2;
    }

    public int getBandWidth() {
        return this.bandWidth;
    }

    public void setBandWidth(int bandWidth2) {
        this.bandWidth = bandWidth2;
    }

    public int getNwId() {
        return this.nwId;
    }

    public void setNwId(int nwId2) {
        this.nwId = nwId2;
    }

    public int getOnId() {
        return this.onId;
    }

    public void setOnId(int onId2) {
        this.onId = onId2;
    }

    public int getTsId() {
        return this.tsId;
    }

    public void setTsId(int tsId2) {
        this.tsId = tsId2;
    }

    public int getProgId() {
        return this.progId;
    }

    public void setProgId(int progId2) {
        this.progId = progId2;
    }

    public int getSymRate() {
        return this.symRate;
    }

    public void setSymRate(int symRate2) {
        this.symRate = symRate2;
    }

    public int getMod() {
        return this.mod;
    }

    public void setMod(int mod2) {
        this.mod = mod2;
    }

    public int getSatRecId() {
        return this.satRecId;
    }

    public void setSatRecId(int satRecId2) {
        this.satRecId = satRecId2;
    }

    public String getNwName() {
        return this.nwName;
    }

    public void setNwName(String nwName2) {
        this.nwName = nwName2;
    }

    public int getBarkerMask() {
        return this.barkerMask;
    }

    public void setBarkerMask(int barkerMask2) {
        this.barkerMask = barkerMask2;
    }

    public int getSdtServiceType() {
        return this.sdtServiceType;
    }

    public void setSdtServiceType(int sdtServiceType2) {
        this.sdtServiceType = sdtServiceType2;
    }

    public int getHbbtvStatus() {
        return this.hbbtvStatus;
    }

    public void setHbbtvStatus(int mhbbtvStatus) {
        this.hbbtvStatus = mhbbtvStatus;
    }

    public String getSvcProName() {
        return this.svcProName;
    }

    public void setSvcProName(String svcProName2) {
        this.svcProName = svcProName2;
    }

    public int getMfType() {
        return this.mfType;
    }

    public void setMfType(int mfType2) {
        this.mfType = mfType2;
    }

    public int getCategoryMask() {
        return this.categoryMask;
    }

    public void setCategoryMask(int categoryMask2) {
        this.categoryMask = categoryMask2;
    }

    public String toString() {
        return "MtkTvDVBChannelInfo    [svlId=" + this.svlId + " , svlRecId=" + this.svlRecId + " , channelId=" + this.channelId + " , brdcstType=" + BrdcstTypeToString(this.brdcstType) + " , nwMask=" + this.nwMask + " , optionMask=" + this.optionMask + " , serviceType=" + this.serviceType + " , channelNumber=" + this.channelNumber + " , serviceName=" + this.serviceName + " , privateData=" + Arrays.toString(this.privateData) + " , customData=" + Arrays.toString(this.customData) + " , frequency=" + this.frequency + " , brdcstMedium=" + this.brdcstMedium + " , shortName=" + this.shortName + " , bandWidth=" + this.bandWidth + " , nwId=" + this.nwId + " , tsId=" + this.tsId + " , onId=" + this.onId + " , progId=" + this.progId + " , symRate=" + this.symRate + " , mod=" + this.mod + " , satRecId=" + this.satRecId + " , nwName=" + this.nwName + " , barkerMask=" + this.barkerMask + " , sdtServiceType=" + this.sdtServiceType + " , mfType=" + this.mfType + " , categoryMask=" + this.categoryMask + "]\n";
    }
}
