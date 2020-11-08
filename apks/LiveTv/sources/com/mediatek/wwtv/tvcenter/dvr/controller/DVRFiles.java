package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.net.Uri;
import com.mediatek.wwtv.tvcenter.dvr.manager.Util;

public class DVRFiles {
    private String channelName = "";
    private String channelNum = "";
    private String date = "";
    private long duration = 0;
    private String durationStr = "";
    private String fileName = "";
    private String indexAndName = "";
    public boolean isPlaying = false;
    public boolean isRecording = false;
    private String mDetailInfo = "";
    private long mId = 0;
    private Uri progarmUri;
    private String programName = "";
    private String time = "";
    private String week = "";

    public void setmId(long mId2) {
        this.mId = mId2;
    }

    public long getmId() {
        return this.mId;
    }

    public Uri getProgarmUri() {
        return this.progarmUri;
    }

    public void setProgarmUri(Uri progarmUri2) {
        this.progarmUri = progarmUri2;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration2) {
        this.duration = duration2;
    }

    public String getDurationStr() {
        if (this.durationStr.isEmpty()) {
            return Util.secondToString((int) this.duration);
        }
        return this.durationStr;
    }

    public void setDurationStr(String duration2) {
        this.durationStr = duration2;
    }

    public String getChannelNum() {
        return this.channelNum;
    }

    public void setChannelNum(String channelNum2) {
        this.channelNum = channelNum2;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(String channelName2) {
        this.channelName = channelName2;
    }

    public String getProgramName() {
        return this.programName;
    }

    public void setProgramName(String programName2) {
        this.programName = programName2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public String getWeek() {
        return this.week;
    }

    public void setWeek(String week2) {
        this.week = week2;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time2) {
        this.time = time2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName2) {
        this.fileName = fileName2;
    }

    public String getIndexAndName() {
        return this.indexAndName;
    }

    public void setIndexAndName(String indexAndName2) {
        this.indexAndName = indexAndName2;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    public void setRecording(boolean isRecording2) {
        this.isRecording = isRecording2;
    }

    public void dumpValues() {
        StringBuilder sb = new StringBuilder();
        sb.append("channelNum:" + this.channelNum);
        sb.append(";channelName:" + this.channelName);
        sb.append(";programName:" + this.programName);
        sb.append(";date:" + this.date);
        sb.append(";week:" + this.week);
        sb.append(";time:" + this.time);
        sb.append(";fileName:" + this.fileName);
        sb.append(";duration:" + this.duration);
        sb.append(";durationStr:" + this.durationStr);
        sb.append(":mDetailInfo :" + this.mDetailInfo);
    }

    public String getmDetailInfo() {
        return this.mDetailInfo;
    }

    public void setmDetailInfo(String mDetailInfo2) {
        this.mDetailInfo = mDetailInfo2;
    }
}
