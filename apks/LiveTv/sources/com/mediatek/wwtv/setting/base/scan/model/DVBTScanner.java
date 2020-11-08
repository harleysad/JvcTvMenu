package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Objects;

public class DVBTScanner implements IScanner {
    public static final int RF_CHANNEL_CURRENT = 0;
    public static final int RF_CHANNEL_NEXT = 2;
    public static final int RF_CHANNEL_PREVIOUS = 1;
    public static int selectedRFChannelFreq = 0;
    private String TAG = "DVBTScanner";
    private MtkTvScanDvbtBase mtkTvScanDvbtBase;

    public DVBTScanner(ScanParams params) {
        dvbtScanInit();
    }

    private void dvbtScanInit() {
        this.mtkTvScanDvbtBase = MtkTvScan.getInstance().getScanDvbtInstance();
    }

    private void dvbtFullScan() {
        MtkLog.d("dvbtFullScan()");
        MtkTvScanDvbtBase.ScanDvbtRet startAutoScan = this.mtkTvScanDvbtBase.startAutoScan();
        MtkTvScanDvbtBase.ScanDvbtRet scanDvbtRet = MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK;
    }

    private void dvbtUpdateScan() {
        MtkLog.d("dvbtUpdateScan()");
        MtkTvScanDvbtBase.ScanDvbtRet startUpdateScan = this.mtkTvScanDvbtBase.startUpdateScan();
        MtkTvScanDvbtBase.ScanDvbtRet scanDvbtRet = MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK;
    }

    public boolean isUKCountry() {
        if (!MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("GBR")) {
            return false;
        }
        MtkLog.d(this.TAG, "isUKCountry");
        return true;
    }

    private void dvbtRfScan() {
        MtkLog.d("dvbtRfScan()");
        if (MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK == this.mtkTvScanDvbtBase.startRfScan()) {
            MtkLog.d("dvbtRfScan(),SCAN_DVBT_RET_OK");
        } else {
            MtkLog.d("dvbtRfScan(),SCAN_DVBT_RET_FAIL");
        }
    }

    private void dvbtCancelScant() {
        MtkLog.d("dvbtCancelScant()");
        MtkTvScanDvbtBase.ScanDvbtRet cancelScan = this.mtkTvScanDvbtBase.cancelScan();
        MtkTvScanDvbtBase.ScanDvbtRet scanDvbtRet = MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK;
    }

    public static String getRFChannel(int type) {
        MtkTvScanDvbtBase mtkTvScanDvbtBase2 = new MtkTvScanDvbtBase();
        Objects.requireNonNull(mtkTvScanDvbtBase2);
        MtkTvScanDvbtBase.RfInfo rfInfo = new MtkTvScanDvbtBase.RfInfo();
        selectedRFChannelFreq = 0;
        switch (type) {
            case 0:
                rfInfo = MtkTvScan.getInstance().getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.CURRENT);
                break;
            case 1:
                rfInfo = MtkTvScan.getInstance().getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.PREVIOUS);
                break;
            case 2:
                rfInfo = MtkTvScan.getInstance().getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.NEXT);
                break;
        }
        selectedRFChannelFreq = rfInfo.rfFrequence;
        return rfInfo.rfChannelName;
    }

    public void cancelScan() {
        dvbtCancelScant();
    }

    public void fullScan() {
        dvbtFullScan();
    }

    public void fullDTVScan() {
    }

    public void fullATVScan() {
    }

    public void updateScan() {
        dvbtUpdateScan();
    }

    public void singleRFScan() {
        dvbtRfScan();
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    public void scanUp(int frequency) {
    }

    public void scanDown(int frequency) {
    }

    public void rangeATVFreqScan(int startFreq, int endFreq) {
    }
}
