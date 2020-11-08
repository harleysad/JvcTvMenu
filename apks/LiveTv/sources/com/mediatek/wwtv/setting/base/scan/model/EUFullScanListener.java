package com.mediatek.wwtv.setting.base.scan.model;

import java.util.ArrayList;
import java.util.List;

public class EUFullScanListener implements ScannerListener {
    private List<Action> actionList = new ArrayList();
    private ScannerListener mCommonListener;
    private ScannerManager mScannerManager;
    private boolean needScanATV = true;

    enum Action {
        DTV,
        ATV
    }

    public EUFullScanListener(ScannerManager scannerManager, ScannerListener mCommonListener2) {
        this.mScannerManager = scannerManager;
        this.mCommonListener = mCommonListener2;
    }

    public void onCompleted(int completeValue) {
        onCompleteScan(completeValue);
        if (completeValue == 2 && this.actionList.size() > 0) {
            this.actionList.remove(0);
        }
    }

    public void onFrequence(int freq) {
        this.mCommonListener.onFrequence(freq);
    }

    public void onProgress(int progress, int channels) {
        this.mCommonListener.onProgress(progress, channels);
    }

    public void onProgress(int progress, int channels, int type) {
        this.mCommonListener.onProgress(progress, channels, type);
    }

    private void onCompleteScan(int completeValue) {
        if (completeValue != 2) {
            this.mCommonListener.onCompleted(completeValue);
        } else if (this.needScanATV) {
            getScannerManager().startScan(2, this.mCommonListener, (ScanParams) null);
        } else {
            this.mCommonListener.onCompleted(2);
        }
    }

    public ScannerManager getScannerManager() {
        return this.mScannerManager;
    }

    public void onDVBSInfoUpdated(int argv4, String name) {
    }
}
