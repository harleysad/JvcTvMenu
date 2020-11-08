package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvBookingBase {
    public static final String TAG = "MtkTvBookingBase";
    private int mBookingId = 0;
    private int mChannelId = 0;
    private int mDevIndex = 0;
    private String mEventTitle = "";
    private int mGenre = 0;
    private int mInfoData = 0;
    private int mRecordDelay = 0;
    private long mRecordDuration = 0;
    private long mRecordStartTime = 0;
    private int mRmdMode = 0;
    private int mRptMode = 0;
    private int mRsltMode = 0;
    private int mSrcType = 0;
    private int mTunerType = 0;

    public MtkTvBookingBase() {
        Log.d(TAG, "Enter MtkTvBookingBase struct Here.");
    }

    public String getEventTitle() {
        return this.mEventTitle;
    }

    public void setEventTitle(String title) {
        this.mEventTitle = title;
    }

    public int getChannelId() {
        return this.mChannelId;
    }

    public void setChannelId(int channelId) {
        this.mChannelId = channelId;
    }

    public int getDeviceIndex() {
        return this.mDevIndex;
    }

    public void setDeviceIndex(int deviceIndex) {
        this.mDevIndex = deviceIndex;
    }

    public int getGenre() {
        return this.mGenre;
    }

    public void setGenre(int genre) {
        this.mGenre = genre;
    }

    public long getRecordStartTime() {
        return this.mRecordStartTime;
    }

    public void setRecordStartTime(long startTime) {
        this.mRecordStartTime = startTime;
    }

    public long getRecordDuration() {
        return this.mRecordDuration;
    }

    public void setRecordDuration(long duration) {
        this.mRecordDuration = duration;
    }

    public int getRecordDelay() {
        return this.mRecordDelay;
    }

    public void setRecordDelay(int delay) {
        this.mRecordDelay = delay;
    }

    public int getRecordMode() {
        return this.mRmdMode;
    }

    public void setRecordMode(int mode) {
        this.mRmdMode = mode;
    }

    public int getRepeatMode() {
        return this.mRptMode;
    }

    public void setRepeatMode(int mode) {
        this.mRptMode = mode;
    }

    public int getSourceType() {
        return this.mSrcType;
    }

    public void setSourceType(int type) {
        this.mSrcType = type;
    }

    public int getTunerType() {
        return this.mTunerType;
    }

    public void setTunerType(int type) {
        this.mTunerType = type;
    }

    public int getRsltMode() {
        return this.mRsltMode;
    }

    public void setRsltMode(int rsltMode) {
        this.mRsltMode = rsltMode;
    }

    public int getInfoData() {
        return this.mInfoData;
    }

    public void setInfoData(int infoData) {
        this.mInfoData = infoData;
    }

    public int getBookingId() {
        return this.mBookingId;
    }

    public void setBookingId(int id) {
        this.mBookingId = id;
    }

    public String toString() {
        return "MtkTvBookingBase [EventTitle=" + this.mEventTitle + ", mBookingId=" + this.mBookingId + ", mChannelId=" + this.mChannelId + ", DevIndex=" + this.mDevIndex + ", Genre=" + this.mGenre + ", StartTime=" + this.mRecordStartTime + ", duration=" + this.mRecordDuration + ", Delay=" + this.mRecordDelay + ", RmdMode=" + this.mRmdMode + ", RsltMode=" + this.mRsltMode + ", InfoData=" + this.mInfoData + ", mRptMode=" + this.mRptMode + ", mSrcType=" + this.mSrcType + ", mTunerType=" + this.mTunerType + "]";
    }
}
