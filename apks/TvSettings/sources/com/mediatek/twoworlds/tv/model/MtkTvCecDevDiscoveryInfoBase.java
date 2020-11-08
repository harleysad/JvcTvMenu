package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvCecDevDiscoveryInfoBase {
    private static final String TAG = "MtkTvCecDevDiscoveryInfoBase";
    private boolean batchPolling;
    private int itvl;
    private int startLa;
    private int stopLa;

    public MtkTvCecDevDiscoveryInfoBase() {
        Log.d(TAG, "MtkTvCecDevDiscoveryInfoBase Constructor\n");
    }

    public String toString() {
        return "startLa= 0x" + Integer.toHexString(this.startLa) + "\nstopLa= 0x" + Integer.toHexString(this.stopLa) + "\nitvl= 0x" + Integer.toHexString(this.itvl) + "\nbatchPolling= " + this.batchPolling;
    }

    public int getStartLa() {
        return this.startLa;
    }

    public void setStartLa(int startLa2) {
        this.startLa = startLa2;
    }

    public int getStopLa() {
        return this.stopLa;
    }

    public void setStopLa(int stopLa2) {
        this.stopLa = stopLa2;
    }

    public int getItvl() {
        return this.itvl;
    }

    public void setItvl(int itvl2) {
        this.itvl = itvl2;
    }

    public boolean getBatchPolling() {
        return this.batchPolling;
    }

    public void setBatchPolling(boolean batchPolling2) {
        this.batchPolling = batchPolling2;
    }
}
