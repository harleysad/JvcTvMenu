package com.mediatek.twoworlds.tv.model;

public class TvProviderChannelEventBase {
    public static final int UPDATTE_EVENT_TYPE_CLEAN_DB = 4;
    public static final int UPDATTE_EVENT_TYPE_DELETE_REC = 3;
    public static final int UPDATTE_EVENT_TYPE_INSERT_REC = 1;
    public static final int UPDATTE_EVENT_TYPE_LOAD_DB = 5;
    public static final int UPDATTE_EVENT_TYPE_UPDATE_REC = 2;
    protected int mCount;
    protected int[] mEventType;
    protected int mSvlId;
    protected int[] mSvlRecId;

    public int getSvlId() {
        return this.mSvlId;
    }

    public void setSvlId(int mSvlId2) {
        this.mSvlId = mSvlId2;
    }

    public int getCount() {
        return this.mCount;
    }

    public void setCount(int mCount2) {
        this.mCount = mCount2;
    }

    public int[] getEventType() {
        return this.mEventType;
    }

    public void setEventType(int[] mEventType2) {
        this.mEventType = mEventType2;
    }

    public int[] getSvlRecId() {
        return this.mSvlRecId;
    }

    public void setSvlRecId(int[] mSvlRecId2) {
        this.mSvlRecId = mSvlRecId2;
    }
}
