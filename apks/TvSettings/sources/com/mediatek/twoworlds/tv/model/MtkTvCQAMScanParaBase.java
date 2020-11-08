package com.mediatek.twoworlds.tv.model;

public class MtkTvCQAMScanParaBase {
    private int countOfWideScanChannel;
    private int endIndex;
    private int freqPlan;
    private int modMask;
    private int startIndex;
    private int[] wideScanChannel;

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

    public int getCountOfWideScanChannel() {
        return this.countOfWideScanChannel;
    }

    public void setCountOfWideScanChannel(int countOfWideScanChannel2) {
        this.countOfWideScanChannel = countOfWideScanChannel2;
    }

    public int[] getWideScanChannel() {
        return this.wideScanChannel;
    }

    public void setWideScanChannel(int[] wideScanChannel2) {
        this.wideScanChannel = wideScanChannel2;
    }
}
