package com.mediatek.twoworlds.tv.model;

public class MtkTvEventComom {
    private int channelId = 0;
    private int eventId = 0;

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int channelId2) {
        this.channelId = channelId2;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId2) {
        this.eventId = eventId2;
    }

    public String toString() {
        return "MtkTvEventComom: channelId=" + this.channelId + ", eventId=" + this.eventId;
    }
}
