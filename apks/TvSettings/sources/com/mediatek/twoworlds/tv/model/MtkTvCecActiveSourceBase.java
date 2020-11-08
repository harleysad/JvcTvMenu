package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvCecActiveSourceBase {
    private static final String TAG = "MtkTvCecActiveSource";
    protected boolean activeRoutingPath;
    protected int logAddr;
    protected int phyAddr;

    public MtkTvCecActiveSourceBase() {
        Log.d(TAG, "MtkTvCecActiveSource Constructor\n");
    }

    public String toString() {
        return "logaddr= 0x" + Integer.toHexString(this.logAddr) + "\nphyaddr= 0x" + Integer.toHexString(this.phyAddr) + "\nisActiveRoutingPath= " + this.activeRoutingPath;
    }

    public int getLogAddr() {
        return this.logAddr;
    }

    public void setLogAddr(int logAddr2) {
        this.logAddr = logAddr2;
    }

    public int getPhyAddr() {
        return this.phyAddr;
    }

    public void setPhyAddr(int phyAddr2) {
        this.phyAddr = phyAddr2;
    }

    public boolean getIsActiveRoutingPath() {
        return this.activeRoutingPath;
    }

    public void setIsActiveRoutingPath(boolean activeRoutingPath2) {
        this.activeRoutingPath = activeRoutingPath2;
    }
}
