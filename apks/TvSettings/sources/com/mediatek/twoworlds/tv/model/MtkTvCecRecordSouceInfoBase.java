package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvCecRecordSouceInfoBase {
    private static final String TAG = "MtkTvCecRecordSouceInfo";
    protected int digBrdcstSys;
    protected int digIdMethod;
    protected int[] serviceId = new int[6];

    public MtkTvCecRecordSouceInfoBase() {
        Log.d(TAG, "MtkTvCecRecordSouceInfo Constructor\n");
    }

    public int getDigIdMethod() {
        return this.digIdMethod;
    }

    /* access modifiers changed from: protected */
    public void setDigIdMethod(int digIdMethod2) {
        this.digIdMethod = digIdMethod2;
    }

    public int getDigBrdcstSys() {
        return this.digBrdcstSys;
    }

    /* access modifiers changed from: protected */
    public void setDigBrdcstSys(int digBrdcstSys2) {
        this.digBrdcstSys = digBrdcstSys2;
    }

    public int[] getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(int[] serviceId2) {
        this.serviceId = serviceId2;
    }
}
