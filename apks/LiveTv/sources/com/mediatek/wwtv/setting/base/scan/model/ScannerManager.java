package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvHighLevel;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ScannerManager {
    public static final int ADVANCE_SCAN = 8;
    public static final int ATV_RANGE_FREQ_SCAN = 12;
    public static final int ATV_SCAN = 2;
    public static final int ATV_SCAN_DOWN = 7;
    public static final int ATV_SCAN_UP = 6;
    public static final int DTV_SCAN = 1;
    public static final int DTV_UPDATE = 9;
    public static final int DVBS_MULTI_SAT = 11;
    public static final int FULL_SCAN = 0;
    public static final int M7_Channel_Scan = 18;
    public static final int M7_LNB_Scan = 17;
    public static final int SA_ATV_UPDATE = 15;
    public static final int SA_DTV_UPDATE = 16;
    public static final int SIGNAL_RF_SCAN = 3;
    public static final int UPDATE_ATV_SCAN = 5;
    public static final int UPDATE_SCAN = 4;
    private final int LEVEL_1;
    private final int LEVEL_2;
    private final int LEVEL_3;
    private final String TAG;
    private ActionList<Action> actionList;
    boolean atvFirst;
    /* access modifiers changed from: private */
    public ScanCallback callback;
    int mAnalogCHs;
    private Context mContext;
    public DvbsScanningData mDVBSData;
    DVBSScanner mDVBSScanner;
    int mDigitalCHs;
    private IScanner mEuScanner;
    private RegionUtils mRegionMgr;
    private boolean mSAScanDTVFirst;
    /* access modifiers changed from: private */
    public MtkTvScan mScan;
    /* access modifiers changed from: private */
    public ScannerListener mScanListener;
    /* access modifiers changed from: private */
    public ScanParams mScanParams;
    /* access modifiers changed from: private */
    public TVContent mTV;
    Region region;
    private boolean rollback;
    /* access modifiers changed from: private */
    public int scanType;
    private MtkTvScanDvbtBase.TargetRegion targetRegion;

    public enum Action {
        DTV,
        ATV,
        DTV_UPDATE,
        ATV_UPDATE,
        SA_ATV_UPDATE,
        SA_DTV_UPDATE
    }

    enum Region {
        CN,
        US,
        SA,
        EU
    }

    public ScannerManager() {
        this.TAG = "ScannerManager";
        this.rollback = false;
        this.scanType = -1;
        this.LEVEL_1 = 1;
        this.LEVEL_2 = 2;
        this.LEVEL_3 = 3;
        this.actionList = new ActionList<>();
        this.mDVBSData = new DvbsScanningData();
        this.mSAScanDTVFirst = true;
        this.atvFirst = ScanContent.isATVScanFirst();
        this.mAnalogCHs = 0;
        this.mDigitalCHs = 0;
        this.mScan = MtkTvScan.getInstance();
        initRegion();
        setRegionMgr(new RegionUtils());
        this.callback = ScanCallback.getInstance(this);
    }

    public ScannerManager(Context context, TVContent tvcontent) {
        this.TAG = "ScannerManager";
        this.rollback = false;
        this.scanType = -1;
        this.LEVEL_1 = 1;
        this.LEVEL_2 = 2;
        this.LEVEL_3 = 3;
        this.actionList = new ActionList<>();
        this.mDVBSData = new DvbsScanningData();
        this.mSAScanDTVFirst = true;
        this.atvFirst = ScanContent.isATVScanFirst();
        this.mAnalogCHs = 0;
        this.mDigitalCHs = 0;
        this.mContext = context;
        this.mScan = MtkTvScan.getInstance();
        initRegion();
        this.mTV = tvcontent;
        setRegionMgr(new RegionUtils());
        this.callback = ScanCallback.getInstance(this);
    }

    public void startScan(final int type, final ScannerListener listener, final ScanParams params) {
        new Thread(new Runnable() {
            public void run() {
                Log.d("ScannerManager", "ScannerManager.startScan() " + type);
                if (ScannerManager.this.isScanning()) {
                    MtkLog.d("ScannerManager", "is Scanning,return!!");
                }
                ScannerManager.this.clearScanStates();
                ScannerManager.this.clearCallbackParams();
                ScannerListener unused = ScannerManager.this.mScanListener = listener;
                ScanCallback unused2 = ScannerManager.this.callback = ScanCallback.getInstance(ScannerManager.this);
                int unused3 = ScannerManager.this.scanType = type;
                ScannerManager.this.getActionList().clear();
                ScanParams unused4 = ScannerManager.this.mScanParams = null;
                switch (type) {
                    case 0:
                        ScannerManager.this.startFullScan(params);
                        return;
                    case 1:
                        if (ScannerManager.this.mTV.isUKCountry()) {
                            ScannerManager.this.setRollbackCleanChannel();
                        }
                        ScannerManager.this.startDTVScan(params);
                        return;
                    case 2:
                        if (ScannerManager.this.isCN()) {
                            ScannerManager.this.setRollbackCleanChannel();
                            if (ScannerManager.this.mTV == null || ScannerManager.this.mTV.mHandler == null) {
                                ScannerManager.this.startATVAutoScan();
                                return;
                            }
                            MtkLog.d("ScannerManager", "start ATV scan delay MessageType.MESSAGE_START_SCAN");
                            ScannerManager.this.mTV.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_START_SCAN, 800);
                            return;
                        }
                        MtkLog.d("ScannerManager", "other startATVAutoScan");
                        ScannerManager.this.startATVAutoScan();
                        return;
                    case 3:
                        ScannerManager.this.startSingleRFScan(params);
                        return;
                    case 4:
                        ScannerManager.this.startUpdateScan(params);
                        return;
                    case 5:
                        ScannerManager.this.startATVUpdateScan();
                        return;
                    case 6:
                        ScannerManager.this.startATVScanUpOrDown(true, params);
                        return;
                    case 7:
                        ScannerManager.this.startATVScanUpOrDown(false, params);
                        return;
                    case 9:
                        ScannerManager.this.startEuDTVUpdateScan(params);
                        return;
                    case 11:
                        ScannerManager.this.startDvbsMultiScan(params);
                        return;
                    case 12:
                        ScannerManager.this.startATVRangeFreqScan(params);
                        return;
                    case 15:
                        ScannerManager.this.mScan.startScan(MtkTvScanBase.ScanType.SCAN_TYPE_NTSC, MtkTvScanBase.ScanMode.SCAN_MODE_ADD_ON, true);
                        return;
                    case 16:
                        ScannerManager.this.mScan.startScan(MtkTvScanBase.ScanType.SCAN_TYPE_ISDB, MtkTvScanBase.ScanMode.SCAN_MODE_ADD_ON, true);
                        return;
                    case 17:
                        ScannerManager.this.startDvbsLNBScan();
                        return;
                    case 18:
                        ScannerManager.this.startDvbsM7ChannelScan(params);
                        return;
                    default:
                        return;
                }
            }
        }).start();
    }

    public void setRollbackCleanChannel() {
        MtkLog.d("ScannerManager", "set rollback true and clean channel");
        setRollback(true);
        TVContent.createChanneListSnapshot();
        clearChannelNum();
        TVContent.getInstance(this.mContext).clearCurrentSvlChannelDB();
    }

    /* access modifiers changed from: private */
    public void startDvbsLNBScan() {
        MtkLog.d("ScannerManager", "startDvbsLNBScan()");
        this.mDVBSScanner = new DVBSScanner(this, (ScanParams) null, this.callback);
        this.mDVBSScanner.dvbsM7LNBScan();
    }

    /* access modifiers changed from: private */
    public void startDvbsM7ChannelScan(ScanParams params) {
        MtkLog.d("ScannerManager", "startDvbsM7ChannelScan()");
        this.mDVBSData.satList = new int[1];
        SatelliteInfo satelliteInfo = ((DVBSSettingsInfo) params).getSatelliteInfo();
        this.mDVBSData.satList[0] = satelliteInfo.getSatlRecId();
        this.mDVBSData.currentSatIndex = 0;
        MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
        base.dvbsGetM7MainSatIdx(ScanContent.getDVBSCurrentOP());
        MtkTvScanDvbsBase.DvbsM7MainSatelliteIdx idx = base.getM7MainSat_idx;
        MtkLog.d("ScannerManager", "DvbsM7MainSatelliteIdx =" + idx);
        this.mDVBSData.currentSatName = getM7SatelliteName(idx);
        this.mDVBSData.orbPos = satelliteInfo.getOrbPos();
        this.mDVBSData.totalSatSize = 1;
        this.mDVBSData.isUpdateScan = ((DVBSSettingsInfo) params).isUpdateScan;
        this.mDVBSData.params = params;
        this.mDVBSScanner = new DVBSScanner(this, (ScanParams) null, this.callback);
        this.mDVBSScanner.dvbsM7ChannelScan();
    }

    private String getM7SatelliteName(MtkTvScanDvbsBase.DvbsM7MainSatelliteIdx idx) {
        int satIndex = -1;
        try {
            satIndex = Integer.parseInt(idx.toString().substring(idx.toString().lastIndexOf("_") + 1));
        } catch (Exception e) {
        }
        if (satIndex == -1) {
            return "";
        }
        List<SatelliteInfo> list = ScanContent.getDVBSsatellites(this.mContext);
        if (list.size() > satIndex) {
            return list.get(satIndex).getSatName();
        }
        return "";
    }

    public void cancleDvbsM7ChannelScan() {
        MtkLog.d("ScannerManager", "cancleDvbsM7ChannelScan()");
        if (this.mDVBSScanner != null) {
            this.mDVBSScanner.canceldvbsM7ChannelScan();
        }
    }

    public void cancleDvbsM7LNBSearch() {
        MtkLog.d("ScannerManager", "cancleDvbsM7LNBSearch()");
        if (this.mDVBSScanner != null) {
            this.mDVBSScanner.cancelDvbsLnbScan();
        }
    }

    /* access modifiers changed from: private */
    public void startDTVScan(ScanParams params) {
        MtkTvScanBase.ScanType type;
        MtkLog.d("ScannerManager", "mScan.startDTVScan()");
        switch (this.region) {
            case US:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
            case CN:
                startCNDTVScan(params);
                return;
            case SA:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_SA;
                break;
            default:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
        }
        MtkLog.d("ScannerManager", "region?" + this.region.name());
        if (isEU()) {
            startEUDTVScan(params);
        } else {
            this.mScan.startScan(type, MtkTvScanBase.ScanMode.SCAN_MODE_FULL, true);
        }
    }

    public void setListener(ScannerListener listener) {
        this.mScanListener = listener;
    }

    public ScannerListener getListener() {
        return this.mScanListener;
    }

    private void startATVScan() {
        MtkLog.d("ScannerManager", "startATVScan()");
        if (isEU() || isCN()) {
            getActionList().remove(Action.ATV);
            this.mEuScanner = new EUATVScanner(this);
            this.mEuScanner.fullATVScan();
        }
    }

    private void stopTVBeforeScanATVOnPIPMode() {
        if (CommonIntegration.getInstance().isPipOrPopState() && CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            MtkTvBroadcast.getInstance().syncStop("sub", false);
        }
    }

    private void startTVBeforeScanATVOnPIPMode() {
        if (CommonIntegration.getInstance().isPipOrPopState() && CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            new MtkTvHighLevel().startTV();
        }
    }

    /* access modifiers changed from: private */
    public void startATVUpdateScan() {
        if (isEU() || isCN()) {
            getActionList().remove(Action.ATV_UPDATE);
            this.mEuScanner = new EUATVScanner(this);
            this.mEuScanner.updateScan();
        }
    }

    /* access modifiers changed from: private */
    public void startATVScanUpOrDown(boolean up, ScanParams params) {
        MtkLog.d("ScannerManager", "startATVScanUpOrDown()");
        if (isEU()) {
            this.mEuScanner = new EUATVScanner(this);
            if (params == null || params.freq == -1) {
                startATVScan();
                return;
            }
            stopTVBeforeScanATVOnPIPMode();
            clearChannelNum();
            if (up) {
                this.mEuScanner.scanUp(params.freq);
            } else {
                this.mEuScanner.scanDown(params.freq);
            }
        } else {
            MtkLog.d("ScannerManager", "startATVScanUpOrDown(),NotEU");
        }
    }

    /* access modifiers changed from: private */
    public void startATVRangeFreqScan(ScanParams params) {
        MtkLog.d("ScannerManager", "startATVRangeFreqScan()");
        if (isCN()) {
            this.mEuScanner = new EUATVScanner(this);
            if (params == null || params.startfreq == -1 || params.endfreq == -1) {
                startATVScan();
                return;
            }
            stopTVBeforeScanATVOnPIPMode();
            clearChannelNum();
            this.mEuScanner.rangeATVFreqScan(params.startfreq, params.endfreq);
            return;
        }
        MtkLog.d("ScannerManager", "startATVRangeFreqScan(),NotCN");
    }

    public boolean isScanning() {
        boolean isScanning = MtkTvScan.getInstance().isScanning();
        MtkLog.d("ScannerManager", "isScanning?" + isScanning);
        return isScanning;
    }

    private void startScan(ScannerListener listener, ScanParams params) {
        startScan(0, listener, params);
    }

    /* access modifiers changed from: private */
    public void startSingleRFScan(ScanParams params) {
        MtkLog.d("ScannerManager", "startSingleRFScan()-Only EU/CN??");
        if (isEU()) {
            startEUSingleRFScan(params);
        } else if (isCN()) {
            startCNSingleRFScan(params);
        }
    }

    /* access modifiers changed from: private */
    public void startUpdateScan(ScanParams params) {
        MtkTvScanBase.ScanType type;
        MtkLog.d("ScannerManager", "startUpdateScan()");
        switch (this.region) {
            case US:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
            case CN:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_ATSC;
                break;
            case SA:
                if (getTuneMode() != 1) {
                    if (!this.mSAScanDTVFirst) {
                        type = MtkTvScanBase.ScanType.SCAN_TYPE_NTSC;
                        break;
                    } else {
                        type = MtkTvScanBase.ScanType.SCAN_TYPE_ISDB;
                        break;
                    }
                } else {
                    type = MtkTvScanBase.ScanType.SCAN_TYPE_NTSC;
                    break;
                }
            default:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
        }
        MtkLog.d("ScannerManager", "region?" + this.region.name());
        if (isEU()) {
            startEUUpdateScan(params);
        } else if (isSA()) {
            int tunerMode = getTuneMode();
            getActionList().clear();
            this.mScanParams = null;
            if (tunerMode == 0) {
                if (this.mSAScanDTVFirst) {
                    getActionList().add(Action.SA_ATV_UPDATE);
                } else {
                    getActionList().add(Action.SA_DTV_UPDATE);
                }
                getActionList().totalScanActionSize = 2;
            }
            this.mScan.startScan(type, MtkTvScanBase.ScanMode.SCAN_MODE_ADD_ON, true);
        } else {
            this.mScan.startScan(type, MtkTvScanBase.ScanMode.SCAN_MODE_ADD_ON, true);
        }
    }

    /* access modifiers changed from: private */
    public void startFullScan(ScanParams params) {
        MtkTvScanBase.ScanType type;
        MtkLog.d("ScannerManager", "startFullScan()");
        clearCallbackParams();
        setRollback(true);
        TVContent.createChanneListSnapshot();
        TVContent.backUpDVBSOP();
        clearChannelNum();
        switch (this.region) {
            case US:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
            case CN:
                startCNFullScan(params);
                return;
            case SA:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_SA;
                break;
            default:
                type = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                break;
        }
        MtkLog.d("ScannerManager", "region?" + this.region.name());
        if (isEU()) {
            startEUFullScan(params);
        } else {
            this.mScan.startScan(type, MtkTvScanBase.ScanMode.SCAN_MODE_FULL, true);
        }
    }

    private void startCNFullScan(ScanParams params) {
        MtkLog.d("ScannerManager", "startCNFullScan()");
        int tuneMode = getTuneMode();
        getActionList().clear();
        this.mScanParams = null;
        TVContent.clearChannelDB();
        getActionList().add(Action.DTV);
        this.mScanParams = params;
        getActionList().totalScanActionSize = 2;
        startATVScan();
    }

    /* access modifiers changed from: private */
    public void startDvbsMultiScan(ScanParams params) {
        getDVBSData().currentSatIndex++;
        SatelliteInfo info = ScanContent.getDVBSsatellitesBySatID(((DVBSSettingsInfo) params).context, this.mDVBSData.satList[getDVBSData().currentSatIndex]);
        ((DVBSSettingsInfo) params).getSatelliteInfo().setSatlRecId(getDVBSData().satList[getDVBSData().currentSatIndex]);
        getDVBSData().currentSatName = info.getSatName();
        getDVBSData().orbPos = info.getOrbPos();
        MtkLog.d("ScannerManager", String.format("%s,SatName:%s,SatID:%d", new Object[]{"startDvbsMultiScan()", info.getSatName(), Integer.valueOf(info.getSatlRecId())}));
        setRollback(true);
        if (getListener() != null) {
            getListener().onDVBSInfoUpdated(-1, "ChangeSatelliteFrequence");
        }
        this.mEuScanner = new DVBSScanner(this, params, this.callback);
    }

    private void startEUFullScan(ScanParams params) {
        MtkLog.d("ScannerManager", "startEUFullScan()");
        int tuneMode = getTuneMode();
        getActionList().clear();
        this.mScanParams = null;
        boolean atvFirst2 = ScanContent.isATVScanFirst();
        if (tuneMode >= 2) {
            int scanMode = ((DVBSSettingsInfo) params).scanMode;
            this.mDVBSData = new DvbsScanningData();
            if (scanMode == 0 || scanMode == 1) {
                MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).menuSelectedOP?" + ((DVBSSettingsInfo) params).menuSelectedOP + ">>>" + ((DVBSSettingsInfo) params).mIsDvbsNeedCleanChannelDB);
                if (((DVBSSettingsInfo) params).mIsDvbsNeedCleanChannelDB) {
                    TVContent.clearChannelDB();
                } else {
                    setRollback(false);
                }
                if (((DVBSSettingsInfo) params).menuSelectedOP != -1) {
                    MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_brdcster", ((DVBSSettingsInfo) params).menuSelectedOP);
                    MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).getDVBSCurrentOP:" + ScanContent.getDVBSCurrentOP());
                }
                List<SatelliteInfo> list = ScanContent.getDVBSEnablesatellites(((DVBSSettingsInfo) params).context);
                if (list.size() > 0) {
                    if (DVBSScanner.isMDUScanMode()) {
                        if (((DVBSSettingsInfo) params).mIsOnlyScanOneSatellite) {
                            this.mDVBSData.satList = new int[1];
                            SatelliteInfo satelliteInfo = ((DVBSSettingsInfo) params).getSatelliteInfo();
                            this.mDVBSData.satList[0] = satelliteInfo.getSatlRecId();
                            this.mDVBSData.currentSatIndex = 0;
                            this.mDVBSData.currentSatName = satelliteInfo.getSatName();
                            this.mDVBSData.orbPos = list.get(0).getOrbPos();
                            this.mDVBSData.totalSatSize = 1;
                            MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).isMDUScanMode add scan:" + this.mDVBSData.satList[0]);
                        } else {
                            MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).isMDUScanMode:re scan or update scan");
                            this.mDVBSData.satList = new int[list.size()];
                            this.mDVBSData.currentSatIndex = 0;
                            this.mDVBSData.currentSatName = list.get(0).getSatName();
                            this.mDVBSData.orbPos = list.get(0).getOrbPos();
                            MtkLog.d("ScannerManager", "mDVBSData.currentSatName-1:" + this.mDVBSData.currentSatName);
                            this.mDVBSData.totalSatSize = list.size();
                            for (int i = 0; i < list.size(); i++) {
                                this.mDVBSData.satList[i] = list.get(i).getSatlRecId();
                                MtkLog.d("ScannerManager", "isMDUScanModemDVBSData.satID:" + i + "," + list.get(i).getSatlRecId());
                            }
                            MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).isMDUScanModerescan:" + this.mDVBSData.satList[0]);
                        }
                    } else if (((DVBSSettingsInfo) params).mIsOnlyScanOneSatellite) {
                        this.mDVBSData.satList = new int[1];
                        SatelliteInfo satelliteInfo2 = ((DVBSSettingsInfo) params).getSatelliteInfo();
                        this.mDVBSData.satList[0] = satelliteInfo2.getSatlRecId();
                        this.mDVBSData.currentSatIndex = 0;
                        this.mDVBSData.currentSatName = satelliteInfo2.getSatName();
                        this.mDVBSData.orbPos = satelliteInfo2.getOrbPos();
                        MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).mIsOnlyScanOneSatellite:>>" + this.mDVBSData.satList[0]);
                        this.mDVBSData.totalSatSize = 1;
                    } else if (DVBSScanner.isM7ScanMode()) {
                        this.mDVBSData.satList = new int[1];
                        SatelliteInfo satelliteInfo3 = ((DVBSSettingsInfo) params).getSatelliteInfo();
                        this.mDVBSData.satList[0] = satelliteInfo3.getSatlRecId();
                        this.mDVBSData.currentSatIndex = 0;
                        this.mDVBSData.orbPos = satelliteInfo3.getOrbPos();
                        this.mDVBSData.totalSatSize = 1;
                        MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).isM7ScanMode:" + this.mDVBSData.satList[0]);
                    } else {
                        MtkLog.d("ScannerManager", "((DVBSSettingsInfo)params).commonScanMode:");
                        this.mDVBSData.satList = new int[list.size()];
                        this.mDVBSData.currentSatIndex = 0;
                        this.mDVBSData.currentSatName = list.get(0).getSatName();
                        MtkLog.d("ScannerManager", "mDVBSData.currentSatName-1:" + this.mDVBSData.currentSatName);
                        this.mDVBSData.totalSatSize = list.size();
                        this.mDVBSData.orbPos = list.get(0).getOrbPos();
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            this.mDVBSData.satList[i2] = list.get(i2).getSatlRecId();
                            MtkLog.d("ScannerManager", "mDVBSData.satID:" + i2 + "," + list.get(i2).getSatlRecId());
                        }
                    }
                    ((DVBSSettingsInfo) params).getSatelliteInfo().setSatlRecId(this.mDVBSData.satList[0]);
                    this.mEuScanner = new DVBSScanner(this, params, this.callback);
                }
            } else if (scanMode == 2) {
                setRollback(false);
                this.mDVBSData.satList = new int[1];
                this.mDVBSData.satList[0] = ((DVBSSettingsInfo) params).getSatelliteInfo().getSatlRecId();
                this.mDVBSData.currentSatIndex = 0;
                this.mDVBSData.totalSatSize = 1;
                SatelliteInfo info = ScanContent.getDVBSsatellitesBySatID(((DVBSSettingsInfo) params).context, this.mDVBSData.satList[0]);
                this.mDVBSData.currentSatName = info.getSatName();
                this.mDVBSData.orbPos = info.getOrbPos();
                MtkLog.d("ScannerManager", "mDVBSData.currentSatName-2:" + this.mDVBSData.currentSatName + ">>>" + this.mDVBSData.satList[0]);
                this.mEuScanner = new DVBSScanner(this, params, this.callback);
            }
            this.mDVBSData.isUpdateScan = ((DVBSSettingsInfo) params).isUpdateScan;
            this.mDVBSData.params = params;
            if (getListener() != null) {
                getListener().onDVBSInfoUpdated(-1, "ChangeSatelliteFrequence");
                return;
            }
            return;
        }
        TVContent.clearChannelDB();
        if (atvFirst2) {
            getActionList().add(Action.DTV);
            this.mScanParams = params;
        } else {
            getActionList().add(Action.ATV);
        }
        getActionList().totalScanActionSize = 2;
        if (!atvFirst2) {
            startEUDTVScan(params);
        } else if (this.mTV == null || this.mTV.mHandler == null) {
            MtkLog.d("ScannerManager", "start ATV scan not delay!!!");
            startATVAutoScan();
        } else {
            MtkLog.d("ScannerManager", "start ATV scan delay MessageType.MESSAGE_START_SCAN");
            this.mTV.mHandler.sendEmptyMessageDelayed(MessageType.MESSAGE_START_SCAN, 800);
        }
    }

    public void startDvbsScanAfterTsLock() {
        if (this.mEuScanner != null) {
            MtkLog.d("ScannerManager", "startDvbsScanAfterTsLock()");
            this.mEuScanner.fullScan();
        }
    }

    public void startATVAutoScan() {
        MtkLog.d("ScannerManager", "startATVAutoScan()");
        if (isEU()) {
            getActionList().remove(Action.ATV);
            this.mEuScanner = new EUATVScanner(this);
            this.mEuScanner.fullATVScan();
        } else if (isCN()) {
            getActionList().remove(Action.ATV);
            this.mEuScanner = new EUATVScanner(this);
            this.mEuScanner.fullATVScan();
        }
    }

    private void startEUDTVScan(ScanParams params) {
        if (TVContent.getInstance(this.mContext).isHKRegion()) {
            startCNDTVScan(params);
            return;
        }
        switch (getTuneMode()) {
            case 0:
                this.mEuScanner = new DVBTScanner(params);
                this.mEuScanner.fullScan();
                return;
            case 1:
                this.mEuScanner = new DVBCScanner(params);
                this.mEuScanner.fullScan();
                return;
            default:
                return;
        }
    }

    private void startCNDTVScan(ScanParams params) {
        switch (getTuneMode()) {
            case 0:
                this.mEuScanner = DVBTCNScanner.getScanInstance(this);
                this.mEuScanner.fullScan();
                return;
            case 1:
                this.mEuScanner = DVBCCNScanner.getScanInstance((ScanParams) null);
                if (params == null) {
                    params = new ScanParams();
                    params.dvbcScanMode = ScanParams.Dvbc_scan_mode.FULL;
                }
                ((DVBCCNScanner) this.mEuScanner).setScanParams(params);
                this.mEuScanner.fullScan();
                return;
            default:
                return;
        }
    }

    private void startEUUpdateScan(ScanParams params) {
        this.atvFirst = ScanContent.isATVScanFirst();
        int tuneMode = getTuneMode();
        getActionList().clear();
        this.mScanParams = null;
        if (!MarketRegionInfo.isFunctionSupport(16)) {
            if (this.atvFirst) {
                getActionList().add(Action.DTV_UPDATE);
            } else {
                getActionList().add(Action.ATV_UPDATE);
            }
            getActionList().totalScanActionSize = 2;
            if (this.atvFirst) {
                startATVUpdateScan();
                MtkLog.d("ScannerManager", "start ATV UpdateScan");
                this.atvFirst = false;
            } else {
                startEuDTVUpdateScan(params);
                MtkLog.d("ScannerManager", "start DTV UpdateScan");
                this.atvFirst = true;
            }
            MtkLog.d("ScannerManager", "atvFirst:" + this.atvFirst);
        } else if (TVContent.getInstance(this.mContext).isCurrentSourceATV()) {
            startATVUpdateScan();
        } else {
            startEuDTVUpdateScan(params);
        }
    }

    /* access modifiers changed from: private */
    public void startEuDTVUpdateScan(ScanParams params) {
        switch (getTuneMode()) {
            case 0:
                this.mEuScanner = new DVBTScanner(params);
                this.mEuScanner.updateScan();
                return;
            case 1:
                this.mEuScanner = new DVBCScanner(params);
                this.mEuScanner.updateScan();
                return;
            default:
                return;
        }
    }

    private void startCNSingleRFScan(ScanParams params) {
        switch (getTuneMode()) {
            case 0:
                this.mEuScanner = DVBTCNScanner.getScanInstance();
                if (this.callback != null) {
                    this.callback.mLastMsg = null;
                }
                this.mEuScanner.singleRFScan();
                return;
            case 1:
                this.mEuScanner = DVBCCNScanner.getScanInstance((ScanParams) null);
                ((DVBCCNScanner) this.mEuScanner).setScanParams(params);
                if (this.callback != null) {
                    this.callback.mLastMsg = null;
                }
                this.mEuScanner.singleRFScan();
                return;
            default:
                return;
        }
    }

    private void startEUSingleRFScan(ScanParams params) {
        switch (getTuneMode()) {
            case 0:
                this.mEuScanner = new DVBTScanner(params);
                if (this.callback != null) {
                    this.callback.mLastMsg = null;
                }
                this.mEuScanner.singleRFScan();
                return;
            case 1:
                this.mEuScanner = new DVBCScanner(params);
                this.mEuScanner.singleRFScan();
                return;
            default:
                return;
        }
    }

    public void onMdu_detect_nfy() {
        MtkLog.d("ScannerManager", "onMdu_detect_nfy()");
        if (getTuneMode() >= 2) {
            MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
            base.dvbsGetNfyMduDetect();
            int mduType = Arrays.asList(MtkTvScanDvbsBase.TunerMduType.values()).indexOf(base.nfyMduDetect_mduType);
            MtkLog.d("ScannerManager", "onMdu_detect_nfy():" + mduType);
            int mduType2 = Math.max(mduType, 0);
            ((DVBSSettingsInfo) this.mDVBSData.params).mduType = mduType2;
            SatelliteInfo info = ScanContent.getDVBSsatellitesBySatID(((DVBSSettingsInfo) this.mDVBSData.params).context, ((DVBSSettingsInfo) this.mDVBSData.params).getSatelliteInfo().getSatlRecId());
            info.setMduType(mduType2);
            ScanContent.saveDVBSSatelliteToSatl(info);
        }
    }

    public void startSecondMduScan() {
        this.mEuScanner = new DVBSScanner(this, this.mDVBSData.params, this.callback);
        this.mEuScanner.fullScan();
    }

    public int getTuneMode() {
        int tuneMode = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
        MtkLog.d("ScannerManager", "tuneMode?" + tuneMode);
        return tuneMode;
    }

    public void cancelScan() {
        if (isEU() || isCN()) {
            if (getScanType() == 2) {
                if (this.mScan != null) {
                    MtkLog.d("ScannerManager", "cancelScan  ATV_SCAN");
                    this.mScan.getScanPalSecamInstance().cancelScan();
                }
            } else if (this.mTV.isM7ScanMode()) {
                cancleDvbsM7LNBSearch();
                cancleDvbsM7ChannelScan();
            } else if (this.mEuScanner != null) {
                MtkLog.d("ScannerManager", "cancelScan 222 ATV_SCAN");
                this.mEuScanner.cancelScan();
            }
        } else if (this.mScan != null) {
            MtkLog.d("ScannerManager", "cancelScan  3333ATV_SCAN");
            this.mScan.cancelScan();
        }
    }

    public boolean isEU() {
        return this.region == Region.EU;
    }

    public boolean isSA() {
        return this.region == Region.SA;
    }

    public boolean isCN() {
        return this.region == Region.CN;
    }

    /* access modifiers changed from: private */
    public void clearScanStates() {
        setRollback(false);
        this.scanType = -1;
    }

    private void initRegion() {
        if (MarketRegionInfo.getCurrentMarketRegion() == 1) {
            this.region = Region.US;
        } else if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
            this.region = Region.EU;
        } else if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            this.region = Region.SA;
        } else if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
            this.region = Region.CN;
        } else {
            this.region = Region.CN;
        }
    }

    private int getScanType() {
        return this.scanType;
    }

    public void resumeTV() {
        startTVBeforeScanATVOnPIPMode();
    }

    public boolean isDVBTSingleRFScan() {
        return this.scanType == 3 && getTuneMode() == 0;
    }

    public boolean isSingleRFScan() {
        return this.scanType == 3;
    }

    public boolean needStartTVAfterATVScanUpDown() {
        return this.scanType == 6 || (this.scanType == 7 && !hasChannels());
    }

    public boolean isATVScan() {
        return this.scanType == 2;
    }

    public ActionList<Action> getActionList() {
        return this.actionList;
    }

    public void setActionList(ActionList<Action> actionList2) {
        this.actionList = actionList2;
    }

    public void clearActionList() {
        if (this.actionList != null) {
            this.actionList.clear();
        }
    }

    public ScanParams getstoredParams() {
        return this.mScanParams;
    }

    public boolean hasOPToDo() {
        return this.callback.isHasOpTODO() || getOPType() != -1;
    }

    public void setOPType(int type) {
        this.callback.setOpType(type);
    }

    public int getOPType() {
        return this.callback.getOpType();
    }

    /* access modifiers changed from: private */
    public void clearCallbackParams() {
        this.callback.setHasOpTODO(false);
        this.callback.setOpType(-1);
    }

    public void uiOpEnd() {
        clearCallbackParams();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        this.callback.removeListener();
    }

    public void onScanError() {
        if (this.mScanListener != null) {
            this.mScanListener.onCompleted(0);
        }
    }

    public void onScanOK() {
        if (this.mScanListener != null) {
            this.mScanListener.onCompleted(2);
        }
    }

    public void reMapRegions(List<MtkTvScanDvbtBase.TargetRegion> regions) {
        MtkLog.d("ScannerManager", "reMapRegions()");
        List<MtkTvScanDvbtBase.TargetRegion> targetRegionList = new ArrayList<>();
        targetRegionList.addAll(regions);
        for (int i = targetRegionList.size() - 1; i >= 0; i--) {
            this.targetRegion = targetRegionList.get(i);
            if (this.targetRegion.level == 1) {
                getRegionMgr().addChild(this.targetRegion);
                targetRegionList.remove(i);
            }
        }
        for (int i2 = targetRegionList.size() - 1; i2 >= 0; i2--) {
            this.targetRegion = targetRegionList.get(i2);
            if (this.targetRegion.level == 2) {
                getRegionMgr().addChild(this.targetRegion);
                targetRegionList.remove(i2);
            }
        }
        for (int i3 = targetRegionList.size() - 1; i3 >= 0; i3--) {
            this.targetRegion = targetRegionList.get(i3);
            if (this.targetRegion.level == 3) {
                getRegionMgr().addChild(this.targetRegion);
            }
        }
        getRegionMgr().dumpRM();
    }

    public HashMap<Integer, APTargetRegion> getRegionsOfGBR() {
        return getRegionMgr().getChildren();
    }

    public RegionUtils getRegionMgr() {
        return this.mRegionMgr;
    }

    public void setRegionMgr(RegionUtils mRegionMgr2) {
        this.mRegionMgr = mRegionMgr2;
    }

    public void showDVBS_TKGS_UserMsgDialog(final String umsg) {
        MtkLog.d("ScannerManager", "showDVBS_TKGS_UserMsgDialog()-umsg==" + umsg);
        if (TurnkeyUiMainActivity.getInstance() != null) {
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    TurnkeyUiMainActivity.getInstance().showDVBS_TKGS_UserMsgDialog(umsg);
                }
            });
        }
    }

    public boolean isRollback() {
        return this.rollback;
    }

    public void setRollback(boolean rollback2) {
        MtkLog.d("ScannerManager", "setRollback>>>" + rollback2);
        this.rollback = rollback2;
    }

    public void rollbackChannelsWhenScanNothingOnUIThread() {
        MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothingOnUIThread()");
        if (TurnkeyUiMainActivity.getInstance() != null) {
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    ScannerManager.this.rollbackChannelsWhenScanNothing();
                }
            });
        }
    }

    public void saveBGMDataInWizard(int svl) {
        if (isRollback() && hasChannels()) {
            DVBSScanner.forceSaveBGMData(this.mDVBSData, svl);
        }
    }

    public void rollbackChannelsWhenScanNothing() {
        MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing()");
        int svl = CommonIntegration.getInstance().getSvl();
        int tunerMode = getTuneMode();
        if (isRollback()) {
            MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing(),isRollback!!");
            if (!hasChannels()) {
                MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing(),hasnoChannels!!");
                TVContent.restoreChanneListSnapshot();
                TVContent.restoreDVBSOP();
            } else if (tunerMode >= 2) {
                DVBSScanner.forceSaveBGMData(this.mDVBSData, svl);
            }
        } else if (hasChannels() && tunerMode >= 2) {
            DVBSScanner.forceSaveBGMData(this.mDVBSData, svl);
        }
        TVContent.releaseChanneListSnapshot();
        TVContent.freeBackupDVBSOP();
        if (!isRollback() || hasChannels()) {
            MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing after scan, free DVBS info");
            TVContent.freeBachUpDVBSsatellites();
            if (tunerMode >= 2 && this.mDVBSData != null && this.mDVBSData.params != null && (this.mDVBSData.params instanceof DVBSSettingsInfo)) {
                MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing free TP info");
                if (((DVBSSettingsInfo) this.mDVBSData.params).mRescanSatLocalInfoList != null) {
                    ((DVBSSettingsInfo) this.mDVBSData.params).mRescanSatLocalInfoList.clear();
                }
                if (((DVBSSettingsInfo) this.mDVBSData.params).mRescanSatLocalTPInfoList != null) {
                    ((DVBSSettingsInfo) this.mDVBSData.params).mRescanSatLocalTPInfoList.clear();
                    return;
                }
                return;
            }
            return;
        }
        MtkLog.d("ScannerManager", "rollbackChannelsWhenScanNothing after scan, has no free DVBS info");
    }

    public boolean hasChannels() {
        MtkLog.d("ScannerManager", "hasChannels(),mAnalogCHs:" + this.mAnalogCHs);
        MtkLog.d("ScannerManager", "hasChannels(),mDigitalCHs:" + this.mDigitalCHs);
        if (this.mAnalogCHs == 0 && this.mDigitalCHs == 0) {
            return false;
        }
        return true;
    }

    private void clearChannelNum() {
        this.mAnalogCHs = 0;
        this.mDigitalCHs = 0;
    }

    public boolean isBATCountry() {
        if (MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("FRA")) {
            return true;
        }
        return false;
    }

    public String getFirstSatName() {
        MtkLog.d("ScannerManager", "getFirstSatName()");
        if (getDVBSData() != null) {
            return getDVBSData().currentSatName;
        }
        MtkLog.d("ScannerManager", "getFirstSatName():getDVBSData()==null");
        return "";
    }

    public String getScanningSatName() {
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        dvbsScan.dvbsGetNfySatName();
        MtkLog.d("ScannerManager", "original orbPos=" + this.mDVBSData.orbPos + ",now orbPos=" + dvbsScan.nfySatName_orbPos);
        if (this.mDVBSData.orbPos == dvbsScan.nfySatName_orbPos) {
            MtkLog.d("ScannerManager", "orbPos is same,return");
            return null;
        }
        if (dvbsScan.nfySatName_satName == null || !dvbsScan.nfySatName_satName.equalsIgnoreCase("null")) {
            int nfySatName_orbPos = dvbsScan.nfySatName_orbPos;
            if (nfySatName_orbPos > 0) {
                this.mDVBSData.currentSatName = convertSatNameByPos(nfySatName_orbPos, MtkTvRatingConvert2Goo.STR_CA_TV_E);
            } else {
                this.mDVBSData.currentSatName = convertSatNameByPos(-nfySatName_orbPos, "W");
            }
        } else {
            this.mDVBSData.currentSatName = dvbsScan.nfySatName_satName;
        }
        return this.mDVBSData.currentSatName;
    }

    private String convertSatNameByPos(int pos, String suf) {
        StringBuilder sb = new StringBuilder();
        sb.append(pos / 10);
        if (pos % 10 != 0) {
            sb.append(".");
            sb.append(pos % 10);
        }
        sb.append("°");
        sb.append(suf);
        return sb.toString();
    }

    public void setChannelsNum(int channels, int channelType) {
        switch (channelType) {
            case 0:
            case 1:
                this.mAnalogCHs = channels;
                return;
            case 2:
                this.mDigitalCHs = channels;
                return;
            default:
                return;
        }
    }

    public boolean showPTSaveChannelDialog() {
        return isEU() && ScanContent.isCountryPT() && isRollback() && hasChannels();
    }

    public void showDVBT_THD_ConfirmDialog() {
        MtkLog.d("ScannerManager", "showDVBT_THD_ConfirmDialog()-ScannerManager");
        if (TurnkeyUiMainActivity.getInstance() != null) {
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    TurnkeyUiMainActivity.getInstance().showDVBT_THD_ConfirmDialog();
                }
            });
        }
    }

    public int getDVBSCurrentIndex() {
        return this.mDVBSData.currentSatIndex;
    }

    public int getDVBSTotalSatSize() {
        return this.mDVBSData.getSatSize();
    }

    public ScanParams getDVBSDataParams() {
        return this.mDVBSData.params;
    }

    public void setTkgsNfyInfoMask(int mask) {
        this.callback.setTkgsNfyInfoMask(mask);
    }

    public int getTkgsNfyInfoMask() {
        return this.callback.getTkgsNfyInfoMask();
    }

    public String getDVBSTkgsUserMessage() {
        return this.mDVBSData.tkgsUserMessage;
    }

    public void setDVBSTkgsUserMessage(String msg) {
        this.mDVBSData.tkgsUserMessage = msg;
    }

    public int getDVBSScannedChannel() {
        int sumSize = 0;
        for (Integer key : this.mDVBSData.channelNum.keySet()) {
            sumSize += this.mDVBSData.channelNum.get(key).intValue();
        }
        MtkLog.d("ScannerManager", "getDVBSScannedChannel(),size:" + sumSize);
        return sumSize;
    }

    public void setDVBSScannedChannel(int index, int num) {
        this.mDVBSData.channelNum.put(Integer.valueOf(index), Integer.valueOf(num));
    }

    public DvbsScanningData getDVBSData() {
        return this.mDVBSData;
    }

    public void clearDVBSData() {
        if (getTuneMode() >= 2) {
            this.mDVBSData = new DvbsScanningData();
        }
    }

    public boolean isScanTaskFinish() {
        if (getTuneMode() >= 2) {
            MtkLog.d("ScannerManager", "isScanTaskFinish getDVBSCurrentIndex>>>" + getDVBSCurrentIndex() + ">>>" + getDVBSTotalSatSize() + ">>>" + getActionList().size());
            return getDVBSCurrentIndex() + 1 >= getDVBSTotalSatSize() && getActionList().size() <= 0;
        } else if (getActionList().size() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentSalId() {
        if (getDVBSData() == null) {
            return 0;
        }
        return getDVBSData().satList[getDVBSCurrentIndex()];
    }
}
