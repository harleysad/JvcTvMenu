package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import java.util.Arrays;

public class MtkTvISDBChannelInfo extends MtkTvChannelInfoBase {
    private static final String TAG = "MtkTvISDBChannelInfo";
    private int channelIdx;
    private int majorNumber;
    private int minorNumber;
    private int nwId;
    private int onId;
    private int progId;
    private int rfIdx;
    private int sch_blcok_from_second;
    private int sch_block_enable;
    private int sch_block_to_second;
    private int sch_block_type;
    private int tsID;
    private String tsName;

    public enum ISDB_SCHEDULE_BLOCK_TYPE {
        SCHEDULE_BLOCK_NONE,
        SCHEDULE_BLOCK_DAILY,
        SCHEDULE_BLOCK_ONCE
    }

    private void isdbChannelInfosetDefault() {
        this.rfIdx = 0;
        this.onId = 0;
        this.tsID = 0;
        this.majorNumber = 0;
        this.minorNumber = 0;
        this.channelIdx = 0;
        this.progId = 0;
    }

    public MtkTvISDBChannelInfo() {
        isdbChannelInfosetDefault();
    }

    public MtkTvISDBChannelInfo(String dbName) {
        isdbChannelInfosetDefault();
        this.svlId = MtkTvChCommonBase.getSvlIdByName(dbName);
    }

    public MtkTvISDBChannelInfo(int svlId, int svlRecId) {
        super(svlId, svlRecId);
        isdbChannelInfosetDefault();
        Log.d(TAG, "MtkTvISDBChannelInfo Constructor(svlid = " + svlId + ", svlRecId = " + svlRecId + ")\n");
    }

    public void setSchBlkEnable(int block_Enable) {
        Log.d(TAG, "Digital setSchBlkEnable = " + block_Enable + ")\n");
        this.sch_block_enable = block_Enable;
    }

    public int getSchBlkEnable() {
        Log.d(TAG, "Digital getSchBlkEnable = " + this.sch_block_enable + ")\n");
        return this.sch_block_enable;
    }

    public int getSchBlkType() {
        Log.d(TAG, "Digital getSchBlkType = " + this.sch_block_type + ")\n");
        return this.sch_block_type;
    }

    public void setSchBlkType(int block_type) {
        Log.d(TAG, "Digital setSchBlkType = " + block_type + ")\n");
        this.sch_block_type = block_type;
    }

    public int getSchBlkFromTime() {
        return this.sch_blcok_from_second;
    }

    public void setSchBlkFromTime(int block_from_utc_time) {
        this.sch_blcok_from_second = block_from_utc_time;
        Log.d(TAG, "Digital setSchBlkFromTime = " + block_from_utc_time + ")\n");
    }

    public int getSchBlkToTime() {
        return this.sch_block_to_second;
    }

    public void setSchBlkToTime(int block_to_utc_time) {
        this.sch_block_to_second = block_to_utc_time;
        Log.d(TAG, "Digital setSchBlkToTime = " + block_to_utc_time + ")\n");
    }

    public int getMajorNum() {
        return this.majorNumber;
    }

    public void _setMajorNum(int major) {
        this.majorNumber = major;
    }

    public void setMajorNum(int major) {
        _setMajorNum(major);
        setChannelNumberEdited(true);
    }

    public int getMinorNum() {
        return this.minorNumber;
    }

    public void _setMinorNum(int minor) {
        this.minorNumber = minor;
    }

    public void setMinorNum(int minor) {
        _setMinorNum(minor);
        setChannelNumberEdited(true);
    }

    private int getChIdx() {
        return this.channelIdx;
    }

    private void setChIdx(int idx) {
        this.channelIdx = idx;
    }

    public int getProgId() {
        return this.progId;
    }

    public void setProgId(int progId2) {
        this.progId = progId2;
    }

    public int getRfIdx() {
        return this.rfIdx;
    }

    public void setRfIdx(int rfIdx2) {
        this.rfIdx = rfIdx2;
    }

    public int getOnID() {
        return this.onId;
    }

    public void setOnId(int onId2) {
        this.onId = onId2;
    }

    public int getTsID() {
        return this.tsID;
    }

    public void setTsId(int tsID2) {
        this.tsID = tsID2;
    }

    public int getNwId() {
        return this.nwId;
    }

    public void setNwId(int nwId2) {
        this.nwId = nwId2;
    }

    public String getTsName() {
        return this.tsName;
    }

    public void setTsName(String tsName2) {
        this.tsName = tsName2;
    }

    public void _setChannelNumberByChannelID(int ChannelID) {
        this.majorNumber = MtkTvChCommonBase.SB_ISDB_GET_MAJOR_NUMBER(ChannelID);
        this.minorNumber = MtkTvChCommonBase.SB_ISDB_GET_MINOR_NUMBER(ChannelID);
        this.channelIdx = MtkTvChCommonBase.SB_ISDB_GET_CHANNEL_INDEX(ChannelID);
    }

    public String toString() {
        return "MtkTvISDBChannelInfo   [svlId=" + this.svlId + " , svlRecId=" + this.svlRecId + " , channelId=" + this.channelId + " , brdcstType=" + BrdcstTypeToString(this.brdcstType) + " , nwMask=" + this.nwMask + " , optionMask=" + this.optionMask + " , serviceType=" + this.serviceType + " , channelNumber=" + this.channelNumber + " , serviceName=" + this.serviceName + " , privateData=" + Arrays.toString(this.privateData) + " , customData=" + Arrays.toString(this.customData) + " , frequency=" + this.frequency + " , brdcstMedium=" + this.brdcstMedium + " , rfIdx=" + this.rfIdx + " , onId=" + this.onId + " , tsId=" + this.tsID + " , majorNumber=" + this.majorNumber + " , minorNumber=" + this.minorNumber + " , channelIdx=" + this.channelIdx + " , progId=" + this.progId + " , sch_block_enable=" + this.sch_block_enable + " , sch_block_type=" + this.sch_block_type + " , sch_block_from_second=" + this.sch_blcok_from_second + " , sch_block_to_second=" + this.sch_block_to_second + " , nwId=" + this.nwId + "]\n";
    }
}
