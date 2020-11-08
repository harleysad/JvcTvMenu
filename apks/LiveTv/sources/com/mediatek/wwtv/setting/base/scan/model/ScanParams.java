package com.mediatek.wwtv.setting.base.scan.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import java.util.HashMap;

public class ScanParams {
    public Dvbc_scan_mode dvbcScanMode = Dvbc_scan_mode.FULL;
    public Dvbc_scan_type dvbcScanType = Dvbc_scan_type.DIGITAL;
    public int endfreq = -1;
    public int freq = -1;
    public int networkID = -1;
    public HashMap<Integer, MtkTvScanDvbsBase.MtkTvSbDvbsNetworkScanInfo> networkInfo = new HashMap<>();
    public int singleRFChannel = 0;
    public int startfreq = -1;
    private String tag = "ScanParams";

    public enum Dvbc_scan_mode {
        ADVANCE,
        QUICK,
        FULL
    }

    public enum Dvbc_scan_type {
        DIGITAL,
        ALL
    }

    public String toString() {
        Log.d(this.tag, String.format("singleRFChannel: %d", new Object[]{Integer.valueOf(this.singleRFChannel)}));
        Log.d(this.tag, String.format("mFreq: %d", new Object[]{Integer.valueOf(this.freq)}));
        Log.d(this.tag, String.format("mNetWorkId: %d", new Object[]{Integer.valueOf(this.networkID)}));
        Log.d(this.tag, String.format("mMode: %s", new Object[]{this.dvbcScanMode.name()}));
        return super.toString();
    }
}
