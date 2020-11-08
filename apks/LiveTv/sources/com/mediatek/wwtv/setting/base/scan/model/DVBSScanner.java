package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DVBSScanner implements IScanner {
    private final boolean DEV_TAG = MtkLog.logOnFlag;
    private final String TAG = "DVBSScanner";
    public MtkTvScanDvbsBase dvbsScan;
    public ScanCallback mCallback;
    private DVBSSettingsInfo mParams;
    private MtkTvScanDvbsBase mScan;
    public ScannerManager mScanManager;
    public MtkTvScanDvbsBase.MtkTvSbDvbsScanData scanData;

    public DVBSScanner(ScannerManager manager, ScanParams params, ScanCallback callback) {
        this.mScanManager = manager;
        this.mScan = new MtkTvScanDvbsBase();
        this.mCallback = callback;
        if (params != null) {
            this.mParams = (DVBSSettingsInfo) params;
        }
    }

    public void fullScan() {
        MtkLog.d("DVBSScanner", "fullScan()");
        if (this.DEV_TAG) {
            MtkLog.printStackTrace();
        }
        if (this.mParams == null) {
            this.mParams = new DVBSSettingsInfo();
        }
        this.dvbsScan = new MtkTvScanDvbsBase();
        this.scanData = initScanData(this.dvbsScan);
        switch (this.mParams.scanMode) {
            case 0:
                networkScanAndTpInfo();
                return;
            case 1:
                autoScan();
                return;
            case 2:
                manualTurningScan();
                return;
            default:
                autoScan();
                return;
        }
    }

    public boolean cancelDvbsLnbScan() {
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        if (MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK != new MtkTvScanDvbsBase().dvbsM7LNBSearchCancel()) {
            return false;
        }
        return true;
    }

    public void dvbsM7LNBScan() {
        MtkLog.d("DVBSScanner", "dvbsM7LNBScan");
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        if (MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK != new MtkTvScanDvbsBase().dvbsM7LNBSearch()) {
            MtkLog.d("DVBSScanner", "dvbsM7LNBScan Error");
        }
    }

    public void dvbsM7ChannelScan() {
        MtkLog.d("DVBSScanner", "dvbsM7ChannelScan");
        if (this.mParams == null) {
            this.mParams = new DVBSSettingsInfo();
        }
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        if (MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK != new MtkTvScanDvbsBase().dvbsM7ChannelSearch(8)) {
            MtkLog.d("DVBSScanner", "dvbsM7ChannelScan Error");
        }
    }

    public void canceldvbsM7ChannelScan() {
        MtkLog.d("DVBSScanner", "canceldvbsM7ChannelScan");
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        if (MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK != new MtkTvScanDvbsBase().dvbsM7ChannelSearchCancel()) {
            MtkLog.d("DVBSScanner", "canceldvbsM7ChannelScan Error");
        }
    }

    private MtkTvScanDvbsBase.MtkTvSbDvbsScanData initScanData(MtkTvScanDvbsBase scanBase) {
        MtkTvScanDvbsBase mtkTvScanDvbsBase = this.dvbsScan;
        Objects.requireNonNull(mtkTvScanDvbsBase);
        MtkTvScanDvbsBase.MtkTvSbDvbsScanData scanData2 = new MtkTvScanDvbsBase.MtkTvSbDvbsScanData();
        scanData2.i4SatlID = CommonIntegration.getInstance().getSvl();
        scanData2.i4SatlRecID = this.mParams.getSatelliteInfo().getSatlRecId();
        String satName = this.mParams.getSatelliteInfo().getSatName();
        scanData2.bIsBgm = Boolean.valueOf(this.mParams.isUpdateScan);
        scanData2.bIsMduDetect = false;
        scanData2.bIsGetInfoStep = false;
        if (ScanContent.isPreferedSat()) {
            scanData2.i4DvbsOperatorName = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
        } else {
            scanData2.i4DvbsOperatorName = 0;
        }
        scanData2.i4EngCfgFlag = calculatorI4EngCfgFlag(this.mParams, scanData2.i4EngCfgFlag);
        if (scanData2.i4DvbsOperatorName == 17) {
            MtkLog.d("FRANSAT LOGO,BATId:" + this.mParams.BATId);
            if (this.mParams.BATId == -1) {
                scanData2.bIsGetInfoStep = true;
            } else {
                scanData2.bIsGetInfoStep = false;
                scanData2.scanInfo.networkScanInfo.i4BatID = this.mParams.BATId;
                this.mParams.BATId = -1;
                MtkLog.d("FRANSAT LOGO,BATId-2:" + scanData2.scanInfo.networkScanInfo.i4BatID);
            }
        } else if (scanData2.i4DvbsOperatorName == 32) {
            MtkLog.d("DVBSScanner", "DVBS_OPERATOR_NAME_TRICOLOR,i4BatID:" + this.mParams.i4BatID + ",scanData.i4SatlRecID:" + scanData2.i4SatlRecID);
            MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
            Objects.requireNonNull(base);
            MtkTvScanDvbsBase.MtkTvSbDvbsBGMData bgmData = new MtkTvScanDvbsBase.MtkTvSbDvbsBGMData();
            base.dvbsGetSaveBgmData(bgmData);
            int batId = -1;
            MtkTvScanDvbsBase.MtkTvSbDvbsBGMData.OneBGMData[] oneBGMDataArr = bgmData.bgmData_List;
            int length = oneBGMDataArr.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    MtkTvScanDvbsBase.MtkTvSbDvbsBGMData.OneBGMData one = oneBGMDataArr[i];
                    if (one != null && one.i4SatRecID == scanData2.i4SatlRecID && one.networkScanInfo != null) {
                        MtkLog.d("DVBSScanner", "one i4Batid" + one.networkScanInfo.i4BatID);
                        batId = one.networkScanInfo.i4BatID;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (this.mParams.isUpdateScan) {
                scanData2.bIsGetInfoStep = false;
                scanData2.scanInfo.networkScanInfo.i4BatID = batId;
            } else if (this.mParams.i4BatID < 0) {
                scanData2.bIsGetInfoStep = true;
            } else {
                scanData2.bIsGetInfoStep = false;
                scanData2.scanInfo.networkScanInfo.i4BatID = this.mParams.i4BatID;
                this.mParams.i4BatID = -1;
            }
        } else if (scanData2.i4DvbsOperatorName == 27) {
            MtkLog.d("TKGS LOGO, callback tkgsType info:" + this.mParams.tkgsType);
            if (this.mParams.tkgsType == -1) {
                scanData2.bIsGetInfoStep = true;
            } else {
                scanData2.bIsGetInfoStep = false;
                this.mParams.tkgsType = -1;
                MtkLog.d("TKGS LOGO,tkgsType#:" + this.mParams.tkgsType);
            }
        } else if (scanData2.i4DvbsOperatorName == 24) {
            this.dvbsScan.dvbsGetBatId(24);
            scanData2.scanInfo.networkScanInfo.i4BatID = this.dvbsScan.getBat_id;
            MtkLog.d("NC+ LOGO,Bat_id:" + scanData2.scanInfo.networkScanInfo.i4BatID);
        } else if (scanData2.i4DvbsOperatorName == 16) {
            MtkLog.d("MDU LOGO,mduType:" + this.mParams.mduType);
            if (this.mParams.mduType == -1) {
                scanData2.bIsMduDetect = true;
            } else {
                scanData2.bIsMduDetect = false;
                this.mParams.mduType = -1;
            }
            this.dvbsScan.dvbsGetBatId(16);
            scanData2.scanInfo.networkScanInfo.i4BatID = this.dvbsScan.getBat_id;
        } else {
            this.dvbsScan.dvbsGetBatId(scanData2.i4DvbsOperatorName);
            scanData2.scanInfo.networkScanInfo.i4BatID = this.dvbsScan.getBat_id;
        }
        if (isM7ScanMode()) {
            this.dvbsScan.dvbsGetM7MainSatIdx(scanData2.i4DvbsOperatorName);
            MtkTvScanDvbsBase.DvbsM7MainSatelliteIdx mainSatID = this.dvbsScan.getM7MainSat_idx;
            List<SatelliteInfo> list = ScanContent.getDVBSsatellites(this.mParams.context);
            int mainIndex = Math.max(Math.min(Arrays.asList(MtkTvScanDvbsBase.DvbsM7MainSatelliteIdx.values()).indexOf(mainSatID) - 1, list.size() - 1), 0);
            List<SatelliteInfo> enableList = new ArrayList<>();
            for (int i2 = 0; i2 < list.size(); i2++) {
                if (list.get(i2).getEnable()) {
                    enableList.add(list.get(i2));
                }
            }
            if (list.get(mainIndex).getEnable()) {
                scanData2.i4SatlRecID = list.get(mainIndex).getSatlRecId();
            } else {
                scanData2.i4SatlRecID = enableList.get(0).getSatlRecId();
            }
            MtkLog.d("DVBSScanner", ">>>" + this.mScanManager.getDVBSData().satList[0] + ">>>" + scanData2.i4SatlRecID);
            this.mScanManager.getDVBSData().satList[0] = scanData2.i4SatlRecID;
            this.mScanManager.getDVBSData().currentSatName = ScanContent.getDVBSsatellitesBySatID(this.mParams.context, scanData2.i4SatlRecID).getSatName();
            if (enableList.size() > 1) {
                scanData2.scanInfo.networkScanInfo.i4AssocSatlRecNum = enableList.size() - 1;
                int index = 0;
                for (int i3 = 0; i3 < enableList.size(); i3++) {
                    if (enableList.get(i3).getSatlRecId() != scanData2.i4SatlRecID) {
                        scanData2.scanInfo.networkScanInfo.aAssocSatlRec[index] = enableList.get(i3).getSatlRecId();
                        index++;
                    }
                }
            } else {
                scanData2.scanInfo.networkScanInfo.i4AssocSatlRecNum = 0;
            }
            MtkLog.d("isM7ScanMode(),list.size:" + list.size() + ",mainIndex:" + mainIndex);
            StringBuilder sb = new StringBuilder();
            sb.append("isM7ScanMode(),enableList.size:");
            sb.append(enableList.size());
            MtkLog.d(sb.toString());
            MtkLog.d("isM7ScanMode()i4SatlRecID:," + scanData2.i4SatlRecID);
            MtkLog.d("isM7ScanMode(),AssocSatNum:" + scanData2.scanInfo.networkScanInfo.i4AssocSatlRecNum);
            for (int i4 = 0; i4 < 3; i4++) {
                MtkLog.d("isM7ScanMode(),Assoc:Index:" + i4 + "," + scanData2.scanInfo.networkScanInfo.aAssocSatlRec[i4]);
            }
        }
        this.mScanManager.getDVBSData().params.networkInfo.put(Integer.valueOf(scanData2.i4SatlRecID), scanData2.scanInfo.networkScanInfo);
        MtkLog.d("DVBSScanner", String.format("ScanMode:%d,Channels:%d,svlID:%d,op:%d,satID:%d,satName:%s", new Object[]{Integer.valueOf(this.mParams.scanMode), Integer.valueOf(this.mParams.scanChannels), Integer.valueOf(scanData2.i4SatlID), Integer.valueOf(scanData2.i4DvbsOperatorName), Integer.valueOf(scanData2.i4SatlRecID), satName}));
        return scanData2;
    }

    private static int calculatorI4EngCfgFlag(DVBSSettingsInfo mParams2, int sourceValue) {
        int sourceValue2;
        if (mParams2.scanChannels == 0) {
            sourceValue2 = sourceValue | 262144;
        } else if (mParams2.scanChannels == 1) {
            sourceValue2 = sourceValue | 8;
        } else {
            sourceValue2 = sourceValue & -262145 & -9;
        }
        if (mParams2.scanStoreType == 0) {
            return sourceValue2 | 16;
        }
        if (mParams2.scanStoreType == 1) {
            return sourceValue2 | 32;
        }
        return sourceValue2 & -17 & -33;
    }

    private void autoScan() {
        MtkLog.d("DVBSScanner", "autoScan()");
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        this.scanData.i4DvbsOperatorName = 0;
        this.scanData.eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_AUTO_MODE;
        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = this.dvbsScan.dvbsStartScan(this.scanData);
        checkResult(dvbsRet);
        MtkLog.d("DVBSScanner", "autoScan(),dvbsRet:" + dvbsRet.name());
    }

    private void networkScanAndTpInfo() {
        MtkLog.d("DVBSScanner", "networkScanAndTpInfo()");
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        this.dvbsScan = new MtkTvScanDvbsBase();
        this.scanData.eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN;
        this.scanData.i4EngCfgFlag = 0;
        this.scanData.bIsM7PortDetect = false;
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = ScanContent.getDVBSTransponder(this.scanData.i4SatlRecID);
        if (tpInfo != null) {
            this.scanData.scanInfo.networkScanInfo.tpInfo.i4Frequency = tpInfo.i4Frequency;
            this.scanData.scanInfo.networkScanInfo.tpInfo.ePol = tpInfo.ePol;
            this.scanData.scanInfo.networkScanInfo.tpInfo.i4Symbolrate = tpInfo.i4Symbolrate;
        }
        MtkLog.d("networkScanAndTpInfo(),i4Frequency:" + this.scanData.scanInfo.networkScanInfo.tpInfo.i4Frequency);
        MtkLog.d("networkScanAndTpInfo(),ePol:" + this.scanData.scanInfo.networkScanInfo.tpInfo.ePol.name());
        MtkLog.d("networkScanAndTpInfo(),i4Symbolrate:" + this.scanData.scanInfo.networkScanInfo.tpInfo.i4Symbolrate);
        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = this.dvbsScan.dvbsStartScan(this.scanData);
        checkResult(dvbsRet);
        MtkLog.d("DVBSScanner", "networkScanAndTpInfo(),dvbsRet:" + dvbsRet.name());
    }

    private void manualTurningScan() {
        MtkLog.d("DVBSScanner", "singleTPScan()");
        MtkTvScanDvbsBase.ScanDvbsRet scanDvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK;
        this.scanData.eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_SINGLE_TP_SCAN;
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = ScanContent.getDVBSTransponder(this.scanData.i4SatlRecID);
        if (tpInfo != null) {
            this.scanData.scanInfo.singleTpScanInfo.i4Frequency = tpInfo.i4Frequency;
            this.scanData.scanInfo.singleTpScanInfo.ePol = tpInfo.ePol;
            this.scanData.scanInfo.singleTpScanInfo.i4Symbolrate = tpInfo.i4Symbolrate;
            MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = this.dvbsScan.dvbsStartScan(this.scanData);
            checkResult(dvbsRet);
            MtkLog.d("DVBSScanner", "manualTurningScan(),dvbsRet:" + dvbsRet.name());
            return;
        }
        MtkLog.d("DVBSScanner", "singleTPScan(),tpInfo==null,scan canceled!!!!");
    }

    private void checkResult(MtkTvScanDvbsBase.ScanDvbsRet dvbsRet) {
        if (MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_OK != dvbsRet) {
            ScanCallback scanCallback = this.mCallback;
            Objects.requireNonNull(this.mCallback);
            scanCallback.dealScanMsg(8, 0, 0, 0);
        }
    }

    public static boolean isMDUScanMode() {
        if (!ScanContent.isPreferedSat() || MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster") != 16) {
            return false;
        }
        return true;
    }

    public static boolean isM7ScanMode() {
        if (!ScanContent.isPreferedSat()) {
            return false;
        }
        switch (MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster")) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
                return true;
            default:
                return false;
        }
    }

    public void fullDTVScan() {
    }

    public void fullATVScan() {
    }

    public void updateScan() {
    }

    public void singleRFScan() {
    }

    public void cancelScan() {
        MtkLog.d("DVBSScanner", "cancelScan()");
        this.mScan.dvbsCancelScan();
    }

    public void scanUp(int frequency) {
    }

    public void scanDown(int frequency) {
    }

    public boolean isScanning() {
        return false;
    }

    public static void forceSaveBGMData(DvbsScanningData data, int svl) {
        MtkLog.d("forceSaveBGMData()");
        if (data != null) {
            MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
            Objects.requireNonNull(base);
            MtkTvScanDvbsBase.MtkTvSbDvbsBGMData bgmData = new MtkTvScanDvbsBase.MtkTvSbDvbsBGMData();
            MtkLog.d("forceSaveBGMData(),svl:" + svl);
            if (svl == 3) {
                bgmData.i4SatlID = 3;
            } else {
                bgmData.i4SatlID = 4;
            }
            bgmData.i4DvbsOperatorName = ScanContent.getDVBSCurrentOP();
            bgmData.i4ScanTimes = data.totalSatSize;
            MtkLog.d("forceSaveBGMData(),op:" + bgmData.i4DvbsOperatorName + ",scanTimes:" + bgmData.i4ScanTimes);
            int i = 0;
            while (true) {
                if (i >= data.totalSatSize) {
                    break;
                } else if (i >= 4) {
                    MtkLog.d("forceSaveBGMData() out of MAX_SAT_NUM break");
                    break;
                } else {
                    MtkTvScanDvbsBase.MtkTvSbDvbsBGMData.OneBGMData[] oneBGMDataArr = bgmData.bgmData_List;
                    Objects.requireNonNull(bgmData);
                    oneBGMDataArr[i] = new MtkTvScanDvbsBase.MtkTvSbDvbsBGMData.OneBGMData();
                    if (((DVBSSettingsInfo) data.params).scanMode == 1) {
                        bgmData.bgmData_List[i].eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_AUTO_MODE;
                    } else if (((DVBSSettingsInfo) data.params).scanMode == 0) {
                        bgmData.bgmData_List[i].eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_NETWORK_SCAN;
                    } else {
                        bgmData.bgmData_List[i].eSbDvbsScanType = MtkTvScanDvbsBase.SbDvbsScanType.SB_DVBS_SCAN_TYPE_AUTO_MODE;
                    }
                    bgmData.bgmData_List[i].i4EngCfgFlag = calculatorI4EngCfgFlag((DVBSSettingsInfo) data.params, bgmData.bgmData_List[i].i4EngCfgFlag);
                    MtkLog.d("forceSaveBGMData(),scanMode:" + bgmData.bgmData_List[i].eSbDvbsScanType + ",scanChannels:" + bgmData.bgmData_List[i].i4EngCfgFlag + ">>>" + data.satList[i]);
                    bgmData.bgmData_List[i].i4SatRecID = data.satList[i];
                    bgmData.bgmData_List[i].networkScanInfo = data.params.networkInfo.get(Integer.valueOf(bgmData.bgmData_List[i].i4SatRecID));
                    if (bgmData.bgmData_List[i].networkScanInfo == null) {
                        MtkLog.d("forceSaveBGMData(),networkScanInfo == null");
                        MtkTvScanDvbsBase.MtkTvSbDvbsBGMData.OneBGMData oneBGMData = bgmData.bgmData_List[i];
                        Objects.requireNonNull(base);
                        oneBGMData.networkScanInfo = new MtkTvScanDvbsBase.MtkTvSbDvbsNetworkScanInfo();
                    } else {
                        MtkLog.d("forceSaveBGMData(),networkScanInfo != null>>" + bgmData.bgmData_List[i].networkScanInfo.i4BatID);
                    }
                    MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = ScanContent.getDVBSTransponder(bgmData.bgmData_List[i].i4SatRecID);
                    if (tpInfo != null) {
                        bgmData.bgmData_List[i].networkScanInfo.tpInfo = tpInfo;
                    }
                    MtkLog.d("forceSaveBGMData(),i4Frequency:" + tpInfo.i4Frequency + ",i4Symbolrate:" + tpInfo.i4Symbolrate + ",tpInfo.ePol" + tpInfo.ePol.name());
                    i++;
                }
            }
            MtkTvScanDvbsBase.ScanDvbsRet rect = base.dvbsSetSaveBgmData(bgmData);
            MtkLog.d("forceSaveBGMData(),rect:" + rect);
        }
    }

    public void rangeATVFreqScan(int startFreq, int endFreq) {
    }
}
