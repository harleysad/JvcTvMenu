package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanCeBase;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DVBCCNScanner implements IScanner {
    private static DVBCCNScanner instance;
    public static int mHighFreq = 858000000;
    public static int mLowerFreq = 48000000;
    public static boolean mQuichScanSwitch = false;
    public static int selectedRFChannelFreq = 0;
    private final String TAG = "DVBCCNScanner";
    private ScanParams mParams;
    private MtkTvScanCeBase mtkTvScanCeBase = new MtkTvScanCeBase();

    public static DVBCCNScanner getScanInstance(ScanParams params) {
        if (instance == null) {
            instance = new DVBCCNScanner(params);
        }
        return instance;
    }

    public DVBCCNScanner(ScanParams params) {
        this.mParams = params;
        if (this.mParams != null && this.mParams.freq > 0) {
            this.mParams.freq *= 1000000;
        }
    }

    public ScanParams getScanParams() {
        return this.mParams;
    }

    public void setScanParams(ScanParams parms) {
        this.mParams = parms;
    }

    public void fullScan() {
        if (this.mParams == null) {
            this.mParams = new ScanParams();
            this.mParams.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        }
        int ret = -2;
        switch (this.mParams.dvbcScanMode) {
            case ADVANCE:
                break;
            case QUICK:
                this.mtkTvScanCeBase.setNitMode(1);
                this.mtkTvScanCeBase.setNetworkID(0);
                this.mtkTvScanCeBase.setStartFreq(this.mParams.freq);
                this.mtkTvScanCeBase.setEndFreq(this.mParams.freq);
                this.mtkTvScanCeBase.setCfgFlag(0);
                ret = this.mtkTvScanCeBase.startQuickScan(this.mParams.freq);
                break;
            default:
                this.mtkTvScanCeBase.setNitMode(0);
                this.mtkTvScanCeBase.setNetworkID(0);
                this.mtkTvScanCeBase.setStartFreq(mLowerFreq);
                this.mtkTvScanCeBase.setEndFreq(mHighFreq);
                this.mtkTvScanCeBase.setCfgFlag(8192);
                ret = this.mtkTvScanCeBase.startAutoScan();
                break;
        }
        MtkLog.d("DVBCCNScanner", String.format("Freq:%d,NetworkID:%d,Type:%s", new Object[]{Integer.valueOf(this.mParams.freq), Integer.valueOf(this.mParams.networkID), this.mParams.dvbcScanMode.name()}) + ">>>" + ret);
    }

    public void fullDTVScan() {
        if (this.mParams == null) {
            this.mParams = new ScanParams();
            this.mParams.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        }
        this.mtkTvScanCeBase.setNitMode(0);
        this.mtkTvScanCeBase.setNetworkID(this.mParams.networkID);
        this.mtkTvScanCeBase.setStartFreq(mLowerFreq);
        this.mtkTvScanCeBase.setEndFreq(mHighFreq);
        this.mtkTvScanCeBase.setCfgFlag(8192);
        int ret = this.mtkTvScanCeBase.startAutoScan();
        MtkLog.d("DVBCCNScanner", "DVBC CN fullDTVScan>" + ret);
    }

    public void fullATVScan() {
    }

    public void updateScan() {
    }

    public void singleRFScan() {
        if (this.mParams == null) {
            this.mParams = new ScanParams();
            this.mParams.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        }
        MtkLog.d("DVBCCNScanner", String.format("singleRFScan Freq:%d,NetworkID:%d,Type:%s", new Object[]{Integer.valueOf(this.mParams.freq), Integer.valueOf(this.mParams.networkID), this.mParams.dvbcScanMode.name()}));
        this.mtkTvScanCeBase.setNitMode(0);
        this.mtkTvScanCeBase.setNetworkID(this.mParams.networkID);
        this.mtkTvScanCeBase.setStartFreq(this.mParams.freq);
        this.mtkTvScanCeBase.setEndFreq(this.mParams.freq);
        this.mtkTvScanCeBase.startRfScan(this.mParams.freq);
    }

    public void cancelScan() {
        MtkLog.d("DVBCCNScanner", "cancelScan");
        this.mtkTvScanCeBase.cancelScan();
    }

    public void rangeATVFreqScan(int startFreq, int endFreq) {
    }

    public void scanUp(int frequency) {
    }

    public void scanDown(int frequency) {
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }
}
