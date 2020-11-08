package com.mediatek.wwtv.setting.base.scan.model;

public interface StateDiskSettingsCallback {
    boolean cancelFormat();

    boolean cancelSpeedTest();

    boolean showFormatDisk();

    boolean showSetPVR();

    boolean showSetTSHIFT();

    boolean showSpeedTest();
}
