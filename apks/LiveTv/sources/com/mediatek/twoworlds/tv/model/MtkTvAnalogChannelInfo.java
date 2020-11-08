package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import java.util.Arrays;

public class MtkTvAnalogChannelInfo extends MtkTvChannelInfoBase {
    private static final String TAG = "MtkTvAnalogChannelInfo";
    private int audioSys;
    private int centralFrequency;
    private int colorSys;
    private boolean noAutoFineTune;
    private int nwId;
    private int onId;
    private int scanIdx;
    private int sch_blcok_from_second;
    private int sch_block_enable;
    private int sch_block_to_second;
    private int sch_block_type;
    private int tsId;
    private int tvSys;

    public enum ANALOG_SCHEDULE_BLOCK_TYPE {
        SCHEDULE_BLOCK_NONE,
        SCHEDULE_BLOCK_DAILY,
        SCHEDULE_BLOCK_ONCE
    }

    public MtkTvAnalogChannelInfo() {
        this.svlId = MtkTvChCommonBase.getSvlIdByName(MtkTvChCommonBase.DB_ANA);
    }

    public MtkTvAnalogChannelInfo(String dbName) {
        this.svlId = MtkTvChCommonBase.getSvlIdByName(dbName);
    }

    public MtkTvAnalogChannelInfo(int svlid, int svlRecId) {
        super(svlid, svlRecId);
        Log.d(TAG, "MtkTvAnalogChannelInfo Constructor(svlid = " + svlid + ", svlRecId = " + svlRecId + ")\n");
    }

    public void setSchBlkEnable(int block_Enable) {
        Log.d(TAG, "Analog setSchBlkEnable = " + block_Enable + ")\n");
        this.sch_block_enable = block_Enable;
    }

    public int getSchBlkEnable() {
        Log.d(TAG, "Analog getSchBlkEnable = " + this.sch_block_enable + ")\n");
        return this.sch_block_enable;
    }

    public int getSchBlkType() {
        Log.d(TAG, "Analog getSchBlkType = " + this.sch_block_type + ")\n");
        return this.sch_block_type;
    }

    public void setSchBlkType(int block_type) {
        Log.d(TAG, "Analog setSchBlkType = " + block_type + ")\n");
        this.sch_block_type = block_type;
    }

    public int getSchBlkFromTime() {
        return this.sch_blcok_from_second;
    }

    public void setSchBlkFromTime(int block_from_utc_time) {
        this.sch_blcok_from_second = block_from_utc_time;
        Log.d(TAG, "Analog setSchBlkFromTime = " + block_from_utc_time + ")\n");
    }

    public int getSchBlkToTime() {
        return this.sch_block_to_second;
    }

    public void setSchBlkToTime(int block_to_utc_time) {
        this.sch_block_to_second = block_to_utc_time;
        Log.d(TAG, "Analog setSchBlkToTime = " + block_to_utc_time + ")\n");
    }

    public int getTvSys() {
        return this.tvSys;
    }

    public void setTvSys(int tvSys2) {
        this.tvSys = tvSys2;
    }

    public int getAudioSys() {
        return this.audioSys;
    }

    public void setAudioSys(int audioSys2) {
        this.audioSys = audioSys2;
    }

    public int getColorSys() {
        return this.colorSys;
    }

    public void setColorSys(int colorSys2) {
        this.colorSys = colorSys2;
    }

    public boolean isNoAutoFineTune() {
        return this.noAutoFineTune;
    }

    public void setNoAutoFineTune(boolean noAutoFineTune2) {
        this.noAutoFineTune = noAutoFineTune2;
    }

    public int getScanIdx() {
        return this.scanIdx;
    }

    public void setScanIdx(int scanIdx2) {
        this.scanIdx = scanIdx2;
    }

    public int getCentralFreq() {
        return this.centralFrequency;
    }

    public void setCentralFreq(int frequency) {
        this.centralFrequency = frequency;
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
        return "MtkTvAnalogChannelInfo [svlId=" + this.svlId + " , svlRecId=" + this.svlRecId + " , channelId=" + this.channelId + " , brdcstType=" + BrdcstTypeToString(this.brdcstType) + " , nwMask=" + this.nwMask + " , optionMask=" + this.optionMask + " , serviceType=" + this.serviceType + " , channelNumber=" + this.channelNumber + " , serviceName=" + this.serviceName + " , privateData=" + Arrays.toString(this.privateData) + " , customData=" + Arrays.toString(this.customData) + " , frequency=" + this.frequency + " , brdcstMedium=" + this.brdcstMedium + " , tvSys=" + this.tvSys + " , audioSys=" + this.audioSys + " , colorSys=" + this.colorSys + " , frequency=" + getFrequency() + " , noAutoFineTune=" + this.noAutoFineTune + " , brdcstMedium=" + getBrdcstMedium() + " , centralFreq=" + getCentralFreq() + "]\n";
    }
}
