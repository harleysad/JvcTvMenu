package com.mediatek.twoworlds.tv.model;

public class MtkTvBisskeyHeader {
    private static final String TAG = "MtkTvBisskeyHeader";
    protected int frequency = 0;
    protected int polarization = 0;
    protected int programId = 0;
    protected int symRate = 0;

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency2) {
        this.frequency = frequency2;
    }

    public int getSymRate() {
        return this.symRate;
    }

    public void setSymRate(int symRate2) {
        this.symRate = symRate2;
    }

    public int getPolarization() {
        return this.polarization;
    }

    public void setPolarization(int polarization2) {
        this.polarization = polarization2;
    }

    public int getProgramId() {
        return this.programId;
    }

    public void setProgramId(int programId2) {
        this.programId = programId2;
    }

    public String toString() {
        return "MtkTvBisskeyHeader: [frequency=" + this.frequency + ", symRate=" + this.symRate + ", polarization=" + this.polarization + ", programId=" + this.programId + "]";
    }
}
