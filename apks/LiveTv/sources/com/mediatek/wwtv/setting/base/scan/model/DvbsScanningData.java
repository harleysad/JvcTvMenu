package com.mediatek.wwtv.setting.base.scan.model;

import java.util.HashMap;

/* compiled from: ScannerManager */
class DvbsScanningData {
    public HashMap<Integer, Integer> channelNum = new HashMap<>();
    public int currentSatIndex = 0;
    public String currentSatName = "";
    public boolean isUpdateScan;
    public int orbPos;
    public ScanParams params;
    public int[] satList;
    public int scannedChannel = 0;
    public String tkgsUserMessage;
    public int totalSatSize = 0;

    DvbsScanningData() {
    }

    public int getSatSize() {
        if (this.satList == null) {
            return 0;
        }
        return this.satList.length;
    }
}
