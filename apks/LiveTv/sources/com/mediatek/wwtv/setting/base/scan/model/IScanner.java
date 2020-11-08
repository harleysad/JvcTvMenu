package com.mediatek.wwtv.setting.base.scan.model;

public interface IScanner {
    void cancelScan();

    void fullATVScan();

    void fullDTVScan();

    void fullScan();

    boolean isScanning();

    void rangeATVFreqScan(int i, int i2);

    void scanDown(int i);

    void scanUp(int i);

    void singleRFScan();

    void updateScan();
}
