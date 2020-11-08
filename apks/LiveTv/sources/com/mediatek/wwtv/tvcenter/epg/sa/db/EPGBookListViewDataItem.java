package com.mediatek.wwtv.tvcenter.epg.sa.db;

public class EPGBookListViewDataItem {
    public int mChannelId;
    public String mChannelNoName;
    public int mProgramId;
    public String mProgramName;
    public long mProgramStartTime;
    public boolean marked = true;

    public EPGBookListViewDataItem() {
    }

    public EPGBookListViewDataItem(int channelId, int progranId, String channelName, String programName, long startTime) {
        this.mChannelId = channelId;
        this.mProgramId = progranId;
        this.mChannelNoName = channelName;
        this.mProgramName = programName;
        this.mProgramStartTime = startTime;
    }
}
