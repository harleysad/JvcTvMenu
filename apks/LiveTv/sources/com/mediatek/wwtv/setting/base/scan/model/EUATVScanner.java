package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanPalSecamBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class EUATVScanner implements IScanner {
    public static final int FREQ_HIGH_VALUE = 865;
    public static final int FREQ_LOW_VALUE = 44;
    private ScannerManager mScannerManager;

    public EUATVScanner(ScannerManager scannerManager) {
        this.mScannerManager = scannerManager;
    }

    public void fullScan() {
    }

    public void fullDTVScan() {
    }

    public void fullATVScan() {
        MtkLog.d("fullATVScan()");
        checkStartScanResult(MtkTvScan.getInstance().getScanPalSecamInstance().startAutoScan());
    }

    public void updateScan() {
        checkStartScanResult(MtkTvScan.getInstance().getScanPalSecamInstance().startUpdateScan());
    }

    private void checkStartScanResult(MtkTvScanPalSecamBase.ScanPalSecamRet rect) {
        if (rect == MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR) {
            this.mScannerManager.onScanError();
        }
    }

    public void singleRFScan() {
    }

    public void cancelScan() {
        MtkLog.d("cancelScan()hhhhhhhhhhhh");
        MtkTvScan.getInstance().getScanPalSecamInstance().cancelScan();
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    public void scanUp(int frequency) {
        int frequency2 = (int) (((double) frequency) * 1000000.0d);
        MtkLog.d("scanUp().frequency:" + frequency2);
        MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase.ScanPalSecamFreqRange();
        MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
        int i = range.upper_freq;
        checkStartScanResult(MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(frequency2, Integer.MAX_VALUE));
    }

    public void scanDown(int frequency) {
        int frequency2 = (int) (((double) frequency) * 1000000.0d);
        MtkLog.d("scanDown().frequency:" + frequency2);
        MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase.ScanPalSecamFreqRange();
        MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
        checkStartScanResult(MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(frequency2, range.lower_freq));
    }

    public void rangeATVFreqScan(int startFreq, int endFreq) {
        int startFreq2 = (int) (((double) startFreq) * 1000000.0d);
        int endFreq2 = (int) (((double) endFreq) * 1000000.0d);
        MtkLog.d("rangeATVFreqScan().frequency:" + startFreq2 + ">>" + endFreq2);
        MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase.ScanPalSecamFreqRange();
        MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
        int i = range.lower_freq;
        checkStartScanResult(MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(startFreq2, endFreq2));
    }
}
