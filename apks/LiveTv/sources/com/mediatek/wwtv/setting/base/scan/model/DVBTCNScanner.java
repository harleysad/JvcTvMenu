package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDtmbBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Objects;

public class DVBTCNScanner implements IScanner {
    public static final int RF_CHANNEL_CURRENT = 0;
    public static final int RF_CHANNEL_NEXT = 2;
    public static final int RF_CHANNEL_PREVIOUS = 1;
    private static DVBTCNScanner instance;
    private static MtkTvScanDtmbBase mtkTvScanDtmbBase;
    public static int selectedRFChannelFreq = 0;
    private String TAG = "DVBTCNScanner";
    private ScannerManager mScannerManager;

    public static DVBTCNScanner getScanInstance(ScannerManager scannerManager) {
        if (instance == null) {
            instance = new DVBTCNScanner((ScanParams) null, scannerManager);
        }
        return instance;
    }

    public static DVBTCNScanner getScanInstance() {
        if (instance == null) {
            instance = new DVBTCNScanner((ScanParams) null);
        }
        return instance;
    }

    public DVBTCNScanner(ScanParams params, ScannerManager scannerManager) {
        this.mScannerManager = scannerManager;
        dvbtScanInit();
    }

    public DVBTCNScanner(ScanParams params) {
        dvbtScanInit();
    }

    private void dvbtScanInit() {
        mtkTvScanDtmbBase = new MtkTvScanDtmbBase();
    }

    private void dvbtCNFullScan() {
        int ret = mtkTvScanDtmbBase.startAutoScan();
        if (ret == -1 && this.mScannerManager != null) {
            this.mScannerManager.onScanOK();
            MtkLog.d("dvbtCNFullScan()>> onScanOK");
        }
        MtkLog.d("dvbtCNFullScan()>>" + ret);
    }

    private void dvbtCNRfScan() {
        int ret = mtkTvScanDtmbBase.startRfScan();
        if (ret == -1 && this.mScannerManager != null) {
            this.mScannerManager.onScanOK();
            MtkLog.d("dvbtCNRfScan()>> onScanOK");
        }
        MtkLog.d("dvbtCNRfScan()>>" + ret);
    }

    private void dvbtCNCancelScant() {
        int ret = mtkTvScanDtmbBase.cancelScan();
        MtkLog.d("dvbtCNCancelScant()>>>" + ret);
    }

    public String getCNRFChannel(int type) {
        MtkTvScanDtmbBase mtkTvScanDtmbBase2 = mtkTvScanDtmbBase;
        Objects.requireNonNull(mtkTvScanDtmbBase2);
        MtkTvScanDtmbBase.RfInfo rfInfo = new MtkTvScanDtmbBase.RfInfo();
        selectedRFChannelFreq = 0;
        switch (type) {
            case 0:
                rfInfo = mtkTvScanDtmbBase.gotoDestinationRf(0);
                break;
            case 1:
                rfInfo = mtkTvScanDtmbBase.gotoDestinationRf(1);
                break;
            case 2:
                rfInfo = mtkTvScanDtmbBase.gotoDestinationRf(2);
                break;
        }
        MtkLog.d("getdvbtCNRFChannel()>>>" + type + ">>>>" + rfInfo);
        if (rfInfo == null) {
            return "";
        }
        selectedRFChannelFreq = rfInfo.rfFreq;
        return rfInfo.rfChannelName;
    }

    public void cancelScan() {
        dvbtCNCancelScant();
    }

    public void fullScan() {
        dvbtCNFullScan();
    }

    public void fullDTVScan() {
    }

    public void fullATVScan() {
    }

    public void updateScan() {
    }

    public void singleRFScan() {
        dvbtCNRfScan();
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    public void rangeATVFreqScan(int startFreq, int endFreq) {
    }

    public void scanUp(int frequency) {
    }

    public void scanDown(int frequency) {
    }
}
