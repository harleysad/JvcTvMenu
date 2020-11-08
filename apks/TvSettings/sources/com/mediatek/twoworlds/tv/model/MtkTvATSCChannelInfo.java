package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import java.util.Arrays;

public class MtkTvATSCChannelInfo extends MtkTvChannelInfoBase {
    private static final String TAG = "MtkTvATSCChannelInfo";
    private int majorNumber;
    private int minorNumber;
    private int mod;
    private int nwId;
    private int onId;
    private int progId;
    private int scanIdx;
    private int tsId;

    public MtkTvATSCChannelInfo() {
    }

    public MtkTvATSCChannelInfo(String dbName) {
        this.svlId = MtkTvChCommonBase.getSvlIdByName(dbName);
    }

    public MtkTvATSCChannelInfo(int svlId, int svlRecId) {
        super(svlId, svlRecId);
        Log.d(TAG, "MtkTvATSCChannelInfo Constructor(svlid = " + svlId + ", svlRecId = " + svlRecId + ")\n");
    }

    public void _setMajorNum(int major) {
        this.majorNumber = major;
    }

    public void setMajorNum(int major) {
        _setMajorNum(major);
        setChannelNumberEdited(true);
    }

    public int getMajorNum() {
        return this.majorNumber;
    }

    public void _setMinorNum(int minor) {
        this.minorNumber = minor;
    }

    public void setMinorNum(int minor) {
        _setMinorNum(minor);
        setChannelNumberEdited(true);
    }

    public int getMinorNum() {
        return this.minorNumber;
    }

    public void setProgId(int progId2) {
        this.progId = progId2;
    }

    public int getProgId() {
        return this.progId;
    }

    public void setScanIdx(int scanIdx2) {
        this.scanIdx = scanIdx2;
    }

    public int getScanIdx() {
        return this.scanIdx;
    }

    public void setMod(int modulation) {
        this.mod = modulation;
    }

    public int getMod() {
        return this.mod;
    }

    public void _setChannelNumberByChannelID(int ChannelID) {
        this.majorNumber = MtkTvChCommonBase.SB_ATSC_GET_MAJOR_CHANNEL_NUM(ChannelID);
        this.minorNumber = MtkTvChCommonBase.SB_ATSC_GET_MINOR_CHANNEL_NUM(ChannelID);
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

    public String toString() {
        return "MtkTvATSCChannelInfo   [svlId=" + this.svlId + " , svlRecId=" + this.svlRecId + " , channelId=" + this.channelId + " , brdcstType=" + BrdcstTypeToString(this.brdcstType) + " , nwMask=" + this.nwMask + " , optionMask=" + this.optionMask + " , serviceType=" + this.serviceType + " , channelNumber=" + this.channelNumber + " , serviceName=" + this.serviceName + " , privateData=" + Arrays.toString(this.privateData) + " , customData=" + Arrays.toString(this.customData) + " , frequency=" + this.frequency + " , brdcstMedium=" + this.brdcstMedium + " , majorNumber=" + this.majorNumber + " , minorNumber=" + this.minorNumber + " , scanIdx=" + this.scanIdx + " , progId=" + this.progId + " , mod=" + this.mod + " , nwId=" + this.nwId + " , onId=" + this.onId + " , tsId=" + this.tsId + "]\n";
    }
}
