package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class DVBSSettingsInfo extends ScanParams {
    public static final int CHANNELS_ALL = 2;
    public static final int CHANNELS_ENCRYPTED = 0;
    public static final int CHANNELS_FREE = 1;
    public static final int CHANNELS_STORE_TYPE_ALL = 2;
    public static final int CHANNELS_STORE_TYPE_DIGITAL = 0;
    public static final int CHANNELS_STORE_TYPE_RADIO = 1;
    public static final int FULL_SCAN_MODE = 1;
    public static final int NETWORK_SCAN_MODE = 0;
    public static final int TP_SCAN_MODE = 2;
    public static int svlID = 3;
    public int BATId = -1;
    public String antenaType = "";
    public String bandFreq = "";
    public boolean checkBATInfo = false;
    public Context context;
    public int i4BatID = -1;
    public boolean isUpdateScan;
    public boolean mIsDvbsNeedCleanChannelDB = true;
    public boolean mIsOnlyScanOneSatellite;
    public List<SatelliteInfo> mRescanSatLocalInfoList;
    public List<SatelliteTPInfo> mRescanSatLocalTPInfoList;
    public int mduType = -1;
    public int menuSelectedOP = -1;
    private int operator = -1;
    private SatelliteInfo satelliteInfo = new SatelliteInfo();
    public int scanChannels = -1;
    public int scanMode = -1;
    public int scanStoreType = -1;
    public MtkTvScanDvbsBase.SbDvbsScanType scanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_AUTO_MODE;
    public int tkgsType = -1;
    public String tuner = "";
    public int type = -1;

    public void clearDVBSSatelliteType() {
        MtkLog.d("clearDVBSSatelliteType()");
        this.antenaType = "";
        this.tuner = "";
        this.bandFreq = "";
    }

    public void clearDVBSOperator() {
        MtkLog.d("clearDVBSOperator()");
        this.antenaType = "";
        this.tuner = "";
        this.bandFreq = "";
    }

    public void clearDVBSConfigInfo() {
        MtkLog.d("clearDVBSConfigInfo()");
        this.antenaType = "";
        this.tuner = "";
        this.bandFreq = "";
    }

    public void clearScanConfigInfo() {
        MtkLog.d("clearScanConfigInfo()");
        this.scanMode = 0;
        this.scanChannels = 0;
    }

    public void dumpInfo() {
        MtkLog.d(String.format("type=%3d,operator=%3d,antenaType=%s,tuner=%s,bandFreq=%s,scanMode=%3d,scanChannels=%3d,", new Object[]{Integer.valueOf(this.type), Integer.valueOf(this.operator), this.antenaType, this.tuner, this.bandFreq, Integer.valueOf(this.scanMode), Integer.valueOf(this.scanChannels)}));
        MtkLog.d(this.satelliteInfo.toString());
    }

    public SatelliteInfo getSatelliteInfo() {
        return this.satelliteInfo;
    }

    public void setSatelliteInfo(SatelliteInfo satelliteInfo2) {
        this.satelliteInfo = satelliteInfo2;
    }
}
