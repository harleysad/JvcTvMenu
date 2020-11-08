package com.mediatek.twoworlds.tv.model;

public class MtkTvEventLinkageBase {
    private int onId;
    private int svcId;
    private int targetEventId;
    private int tsId;

    public MtkTvEventLinkageBase() {
        this.onId = 0;
        this.tsId = 0;
        this.svcId = 0;
        this.targetEventId = 0;
    }

    public MtkTvEventLinkageBase(int onId2, int tsId2, int svcId2, int targetEventId2) {
        this.onId = onId2;
        this.tsId = tsId2;
        this.svcId = svcId2;
        this.targetEventId = targetEventId2;
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

    public int getSvcId() {
        return this.svcId;
    }

    public void setSvcId(int svcId2) {
        this.svcId = svcId2;
    }

    public int getTargetEventId() {
        return this.targetEventId;
    }

    public void setTargetEventId(int targetEventId2) {
        this.targetEventId = targetEventId2;
    }

    public String toString() {
        return "MtkTvEventLinkAge: Onid=" + this.onId + ", tsId=" + this.tsId + ", svcId=" + this.svcId + ", targetEventId=" + this.targetEventId;
    }
}
