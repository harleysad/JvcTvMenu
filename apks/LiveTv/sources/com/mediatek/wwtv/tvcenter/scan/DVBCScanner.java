package com.mediatek.wwtv.tvcenter.scan;

import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Objects;

public class DVBCScanner implements IScanner {
    public static int selectedRFChannelFreq = 0;
    private final String TAG = "DVBCScanner";
    private MtkTvScanDvbcBase.MtkTvScanDvbcParameter mDvbcScanPara;
    private ScanParams mParams;
    private MtkTvScanBase mScan = MtkTvScan.getInstance();
    private MtkTvScanDvbcBase mtkTvScanDvbcBase = this.mScan.getScanDvbcInstance();

    public DVBCScanner(ScanParams params) {
        MtkTvScanDvbcBase mtkTvScanDvbcBase2 = this.mtkTvScanDvbcBase;
        Objects.requireNonNull(mtkTvScanDvbcBase2);
        this.mDvbcScanPara = new MtkTvScanDvbcBase.MtkTvScanDvbcParameter();
        this.mParams = params;
        if (this.mParams != null && this.mParams.freq > 0) {
            this.mParams.freq *= 1000;
        }
    }

    public void fullScan() {
        if (this.mParams == null) {
            this.mParams = new ScanParams();
            this.mParams.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        }
        switch (this.mParams.dvbcScanMode) {
            case ADVANCE:
                this.mDvbcScanPara.setStartFreq(this.mParams.freq);
                this.mDvbcScanPara.setEndFreq(this.mParams.freq);
                this.mDvbcScanPara.setNetWorkID(this.mParams.networkID);
                this.mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_EX_QUICK);
                this.mDvbcScanPara.setCfgFlag(0);
                this.mtkTvScanDvbcBase.setDvbcScanParas(this.mDvbcScanPara);
                this.mtkTvScanDvbcBase.startAutoScan();
                break;
            case QUICK:
                this.mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_QUICK);
                this.mDvbcScanPara.setNetWorkID(this.mParams.networkID);
                this.mDvbcScanPara.setStartFreq(this.mParams.freq);
                this.mDvbcScanPara.setEndFreq(-1);
                this.mDvbcScanPara.setCfgFlag(0);
                this.mtkTvScanDvbcBase.setDvbcScanParas(this.mDvbcScanPara);
                this.mtkTvScanDvbcBase.startAutoScan();
                break;
            default:
                this.mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF);
                this.mDvbcScanPara.setNetWorkID(-1);
                this.mDvbcScanPara.setStartFreq(this.mParams.freq);
                this.mDvbcScanPara.setEndFreq(-1);
                this.mDvbcScanPara.setCfgFlag(8192);
                this.mtkTvScanDvbcBase.setDvbcScanParas(this.mDvbcScanPara);
                this.mtkTvScanDvbcBase.startAutoScan();
                break;
        }
        MtkLog.d("DVBCScanner", String.format("Freq:%d,NetworkID:%d,Type:%s", new Object[]{Integer.valueOf(this.mParams.freq), Integer.valueOf(this.mParams.networkID), this.mParams.dvbcScanMode.name()}) + ">>>" + this.mDvbcScanPara.getCfgFlag());
    }

    public void fullDTVScan() {
    }

    public void fullATVScan() {
    }

    public void updateScan() {
        this.mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF);
        this.mDvbcScanPara.setNetWorkID(this.mParams.networkID);
        this.mDvbcScanPara.setStartFreq(this.mParams.freq);
        this.mDvbcScanPara.setEndFreq(this.mParams.freq);
        this.mtkTvScanDvbcBase.setDvbcScanParas(this.mDvbcScanPara);
        this.mtkTvScanDvbcBase.startUpdateScan();
    }

    public void singleRFScan() {
        if (this.mParams == null) {
            this.mParams = new ScanParams();
            this.mParams.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
        }
        MtkLog.d("DVBCScanner", String.format("singleRFScan Freq:%d,NetworkID:%d,Type:%s", new Object[]{Integer.valueOf(this.mParams.freq), Integer.valueOf(this.mParams.networkID), this.mParams.dvbcScanMode.name()}));
        this.mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF);
        this.mDvbcScanPara.setNetWorkID(this.mParams.networkID);
        this.mDvbcScanPara.setStartFreq(this.mParams.freq);
        this.mDvbcScanPara.setEndFreq(this.mParams.freq);
        this.mtkTvScanDvbcBase.setDvbcScanParas(this.mDvbcScanPara);
        this.mtkTvScanDvbcBase.startRfScan();
    }

    public void cancelScan() {
        MtkLog.d("DVBCScanner", "cancelScan");
        this.mtkTvScanDvbcBase.cancelScan();
    }

    public void scanUp(int frequency) {
    }

    public void scanDown(int frequency) {
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }
}
