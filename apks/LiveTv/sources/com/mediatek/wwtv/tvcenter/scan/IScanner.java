package com.mediatek.wwtv.tvcenter.scan;

public interface IScanner {
    void cancelScan();

    void fullATVScan();

    void fullDTVScan();

    void fullScan();

    boolean isScanning();

    void scanDown(int i);

    void scanUp(int i);

    void singleRFScan();

    void updateScan();
}
