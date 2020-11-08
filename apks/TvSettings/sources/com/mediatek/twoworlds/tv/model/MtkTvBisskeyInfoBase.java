package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import java.util.Arrays;

public class MtkTvBisskeyInfoBase {
    public static final int CUSTOM_DATA_LEN = 8;
    public static final int KEY_LEN = 8;
    public static final int PRIVATE_DATA_LEN = 20;
    private static final String TAG = "MtkTvBisskeyInfo";
    protected int bslId;
    protected int bslRecId;
    protected int frequency;
    protected int polarization;
    protected int programId;
    protected byte[] serviceCwKey = new byte[8];
    protected int symRate;

    private void bisskeyInfoSetDefault() {
    }

    public MtkTvBisskeyInfoBase() {
        bisskeyInfoSetDefault();
    }

    public MtkTvBisskeyInfoBase(int bslId2, int bslRecId2) {
        bisskeyInfoSetDefault();
        this.bslId = bslId2;
        this.bslRecId = bslRecId2;
    }

    public int getBslId() {
        return this.bslId;
    }

    public void setBslId(int bslId2) {
        this.bslId = bslId2;
    }

    public int getBslRecId() {
        return this.bslRecId;
    }

    /* access modifiers changed from: protected */
    public void setBslRecId(int bslRecId2) {
        this.bslRecId = bslRecId2;
    }

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

    public byte[] getServiceCwKey() {
        return this.serviceCwKey;
    }

    public void setServiceCwKey(byte[] _serviceCwKey) {
        if (_serviceCwKey == null) {
            Log.d(TAG, "setServiceCwKey fail!, argu is null.\n");
        } else if (_serviceCwKey.length > 8) {
            Log.d(TAG, "setServiceCwKey fail because data length more than 8! \n");
        } else {
            System.arraycopy(_serviceCwKey, 0, this.serviceCwKey, 0, 8);
        }
    }

    public String toString() {
        return "MtkTvBisskeyInfo [bslId=" + this.bslId + " , bslRecId=" + this.bslRecId + " , frequency=" + this.frequency + " , symRate=" + this.symRate + " , polarization=" + this.polarization + " , programId=" + this.programId + ", serviceCwKey=" + Arrays.toString(this.serviceCwKey) + "]\n";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MtkTvBisskeyInfoBase other = (MtkTvBisskeyInfoBase) obj;
        if (this.bslId == other.bslId && this.bslRecId == other.bslRecId && this.frequency == other.frequency && this.symRate == other.symRate && this.polarization == other.polarization && this.programId == other.programId) {
            return true;
        }
        return false;
    }
}
