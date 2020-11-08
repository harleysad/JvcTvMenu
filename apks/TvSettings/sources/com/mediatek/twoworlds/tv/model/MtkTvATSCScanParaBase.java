package com.mediatek.twoworlds.tv.model;

public class MtkTvATSCScanParaBase {
    private int endIndex;
    private int freqPlan;
    private int modMask;
    private int startIndex;

    public enum ATSCFreqPlan {
        FREQ_PLAN_NONE,
        FREQ_PLAN_STD,
        FREQ_PLAN_IRC,
        FREQ_PLAN_HRC,
        FREQ_PLAN_AUTO
    }

    public enum ATSCModulation {
        MOD_NONE,
        MOD_VSB_8,
        MOD_QAM_64,
        MOD_QAM_256,
        MOD_ALL
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex2) {
        this.startIndex = startIndex2;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public void setEndIndex(int endIndex2) {
        this.endIndex = endIndex2;
    }

    public int getModMask() {
        return this.modMask;
    }

    public void setModMask(int modMask2) {
        this.modMask = modMask2;
    }

    public int getFreqPlan() {
        return this.freqPlan;
    }

    public void setFreqPlan(int freqPlan2) {
        this.freqPlan = freqPlan2;
    }
}
