package com.mediatek.twoworlds.tv.model;

public class MtkTvChannelQuery {
    public static final int QUERY_BY_FREQ = 2;
    public static final int QUERY_BY_RFIDX = 1;
    public static final int QUERY_BY_UNKN = 0;
    private static final String TAG = "MtkTvChannelQuery";
    private int frequency = 0;
    private int queryType = 0;
    private int rfIdx = 0;

    public int getQueryType() {
        return this.queryType;
    }

    public void setQueryType(int queryType2) {
        this.queryType = queryType2;
    }

    public int getRfIdx() {
        return this.rfIdx;
    }

    public void setRfIdx(int rfIdx2) {
        this.rfIdx = rfIdx2;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency2) {
        this.frequency = frequency2;
    }

    public String toString() {
        switch (this.queryType) {
            case 1:
                return TAG + "[RfIdx=" + this.rfIdx + "]";
            case 2:
                return TAG + "[Frequency=" + this.frequency + "]";
            default:
                return TAG + "[UNKNOWN=" + this.queryType + "]";
        }
    }
}
