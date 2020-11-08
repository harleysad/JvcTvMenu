package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;
import java.util.HashMap;
import java.util.Map;

public class MtkTvAppTVBase {
    private static final int JNI_APPTV_SYS_STATUS_AFTER_ASPECT_RATIO_CHG = 9;
    private static final int JNI_APPTV_SYS_STATUS_AFTER_ZOOM_MODE_CHG = 11;
    private static final int JNI_APPTV_SYS_STATUS_BEFORE_ASPECT_RATIO_CHG = 8;
    private static final int JNI_APPTV_SYS_STATUS_BEFORE_ZOOM_MODE_CHG = 10;
    private static final int JNI_APPTV_SYS_STATUS_CHANNEL_SCANNING = 6;
    private static final int JNI_APPTV_SYS_STATUS_MENU_PAUSE = 1;
    private static final int JNI_APPTV_SYS_STATUS_MENU_RESUME = 0;
    private static final int JNI_APPTV_SYS_STATUS_MMP_PAUSE = 3;
    private static final int JNI_APPTV_SYS_STATUS_MMP_RESUME = 2;
    private static final int JNI_APPTV_SYS_STATUS_SCAN_FINISHED = 7;
    private static final int JNI_APPTV_SYS_STATUS_WIZARD_PAUSE = 5;
    private static final int JNI_APPTV_SYS_STATUS_WIZARD_RESUME = 4;
    public static final String SYS_AFTER_ASPECT_RATIO_CHG = "After_ASP_Ratio_chg";
    public static final String SYS_AFTER_ZOOM_MODE_CHG = "SAfter_Zoom_Mode_Chg";
    public static final String SYS_BEFORE_ASPECT_RATIO_CHG = "Before_ASP_Ratio_chg";
    public static final String SYS_BEFORE_ZOOM_MODE_CHG = "Before_Zoom_Mode_Chg";
    public static final String SYS_CHANNEL_SCANNING = "Channel_Scaning";
    public static final String SYS_MENU_PAUSE = "Menu_Pause";
    public static final String SYS_MENU_RESUME = "Menu_Resume";
    public static final String SYS_MMP_PAUSE = "MMP_Pause";
    public static final String SYS_MMP_RESUME = "MMP_Resume";
    public static final String SYS_SCAN_FINISHED = "Scan_Finished";
    public static final String SYS_WIZARD_PAUSE = "Wizard_Pause";
    public static final String SYS_WIZARD_RESUME = "Wizard_Resume";
    private static Map<String, Integer> SysStatus_map = new HashMap();
    public static final String TAG = "MtkTvAppTVBase";

    public MtkTvAppTVBase() {
        setSysStatus_map();
    }

    private static void setSysStatus_map() {
        SysStatus_map.put(SYS_MENU_RESUME, 0);
        SysStatus_map.put(SYS_MENU_PAUSE, 1);
        SysStatus_map.put(SYS_MMP_RESUME, 2);
        SysStatus_map.put(SYS_MMP_PAUSE, 3);
        SysStatus_map.put(SYS_WIZARD_RESUME, 4);
        SysStatus_map.put(SYS_WIZARD_PAUSE, 5);
        SysStatus_map.put(SYS_CHANNEL_SCANNING, 6);
        SysStatus_map.put(SYS_SCAN_FINISHED, 7);
        SysStatus_map.put(SYS_BEFORE_ASPECT_RATIO_CHG, 8);
        SysStatus_map.put(SYS_AFTER_ASPECT_RATIO_CHG, 9);
        SysStatus_map.put(SYS_BEFORE_ZOOM_MODE_CHG, 10);
        SysStatus_map.put(SYS_AFTER_ZOOM_MODE_CHG, 11);
    }

    private boolean isFregChgInfoValid(int conType, int freqType, int SatPol, int tunerMod) {
        switch (conType) {
            case 0:
            case 1:
            case 2:
            case 3:
                switch (freqType) {
                    case 0:
                    case 1:
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                        break;
                    case 2:
                    case 5:
                        switch (SatPol) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                break;
                            default:
                                Log.d(TAG, "The Sat Pol is invalid\n");
                                return false;
                        }
                    default:
                        Log.d(TAG, "The Freq type is invalid\n");
                        return false;
                }
                switch (tunerMod) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                        return true;
                    default:
                        Log.d(TAG, "The tuner mod is invalid\n");
                        return false;
                }
            default:
                Log.d(TAG, "The contype is invalid\n");
                return false;
        }
    }

    public static int saveTimestamp(String str) {
        Log.d(TAG, "Enter saveTimestamp\n");
        return TVNativeWrapper.AppTVsaveTimestamp_native(str);
    }

    public int unlockService(String path) {
        Log.d(TAG, "Enter unlockService\n");
        return TVNativeWrapper.AppTVunlockService_native(path);
    }

    public int unblockSvc(String path, boolean forceUnblock) {
        Log.d(TAG, "Enter unblockSvc\n");
        return TVNativeWrapper.AppTVunblockSvc_native(path, forceUnblock);
    }

    public int setVideoMute(String path, boolean videoMute) {
        Log.d(TAG, "Enter setVideoMute\n");
        return TVNativeWrapper.AppTVsetVideoMute_native(path, videoMute);
    }

    public boolean getVideoMute(String path) {
        Log.d(TAG, "[app_tv]Enter getVideoMute\n");
        return TVNativeWrapper.AppTVgetVideoMute_native(path);
    }

    public int getMatchedChannel(int svlID, boolean hasMinor, int major, int minor) {
        Log.d(TAG, "Enter getMatchedChannel\n");
        return TVNativeWrapper.AppTVgetMatchedChannel_native(svlID, hasMinor, major, minor);
    }

    public int setFinetuneFreq(String path, int freqMhz, boolean leave) {
        Log.d(TAG, "Enter setFinetuneFreq\n");
        return TVNativeWrapper.AppTVsetFinetuneFreq_native(path, freqMhz, leave);
    }

    public int changeFreq(String path, MtkTvFreqChgParamBase freqInfo) {
        Log.d(TAG, "Enter changeFreq\n");
        if (freqInfo == null) {
            return -1;
        }
        Log.d(TAG, "ConType = " + freqInfo.getConType() + ", freqType = " + freqInfo.getfreqType() + ", freq = " + freqInfo.getfreq() + ", tunerMod = " + freqInfo.gettunerMod() + ", symRate = " + freqInfo.getsymRate() + ", SatPol = " + freqInfo.getSatPol() + ", SatLstId = " + freqInfo.getSatLstId() + ", SatLstRecId = " + freqInfo.getSatLstRecId() + "\n");
        if (isFregChgInfoValid(freqInfo.getConType(), freqInfo.getfreqType(), freqInfo.getSatPol(), freqInfo.gettunerMod())) {
            return TVNativeWrapper.AppTVchangeFreq_native(path, freqInfo);
        }
        return -1;
    }

    public int setAutoClockPhasePostion(String path) {
        Log.d(TAG, "Enter setAutoClockPhasePostion\n");
        return TVNativeWrapper.AppTVsetAutoClockPhasePostion_native(path);
    }

    public boolean AutoClockPhasePostionCondSuccess(String path) {
        Log.d(TAG, "Enter AutoClockPhasePostionCondSuccess\n");
        return TVNativeWrapper.AppTVAutoClockPhasePostionCondSuccess_native(path);
    }

    public int setAutoColor(String path) {
        Log.d(TAG, "Enter setAutoColor\n");
        return TVNativeWrapper.AppTVsetAutoColor_native(path);
    }

    public boolean AutoColorCondSuccess(String path) {
        Log.d(TAG, "Enter AutoColorCondSuccess\n");
        return TVNativeWrapper.AppTVAutoColorCondSuccess_native(path);
    }

    public int updatedSysStatus(String sysStatus) {
        Log.d(TAG, "Enter UpdatedSysStatus\n");
        int ww_status = SysStatus_map.get(sysStatus).intValue();
        Log.d(TAG, "ww_status:" + ww_status);
        return TVNativeWrapper.AppTVupdatedSysStatus_native(ww_status);
    }

    public int updatedSysStatus(String path, String sysStatus) {
        Log.d(TAG, "Enter updatedSysStatus + \n");
        int ww_status = SysStatus_map.get(sysStatus).intValue();
        Log.d(TAG, "ww_status:" + ww_status);
        return TVNativeWrapper.AppTVupdatedSysStatus_native(path, ww_status);
    }

    public int swapTmpUnlockInfo(int modeId) {
        Log.d(TAG, "Enter swapTmpUnlockInfo + mode_id:\n" + modeId);
        return TVNativeWrapper.AppTV_swapTmpUnlockInfo_native(modeId);
    }

    public int GetConnAttrBER(String path) {
        Log.d(TAG, "Enter GetConnAttrBER + \n");
        return TVNativeWrapper.GetConnAttrBER_native(path);
    }

    public int GetSignalBER(String path) {
        Log.d(TAG, "Enter GetSignalBER + \n");
        return TVNativeWrapper.GetSignalBER_native(path);
    }

    public long GetConnAttrDBMSNR(String path) {
        Log.d(TAG, "Enter GetConnAtttrDBMSNR + \n");
        return TVNativeWrapper.GetConnAttrDBMSNR_native(path);
    }

    public long GetConnAttrUEC(String path) {
        Log.d(TAG, "Enter GetConnAttrUEC + \n");
        return TVNativeWrapper.GetConnAttrUEC_native(path);
    }

    public long GetSymRate(String path) {
        Log.d(TAG, "Enter GetSymRate + \n");
        return TVNativeWrapper.GetSymRate_native(path);
    }

    public int GetModulation(String path) {
        Log.d(TAG, "Enter GetModulation + \n");
        return TVNativeWrapper.GetModulation_native(path);
    }

    public long GetConnAttrAGC(String path) {
        Log.d(TAG, "Enter GetConnAttrAGC + \n");
        return TVNativeWrapper.GetConnAtrrAGC_native(path);
    }

    public String GetCurrentChannelServiceText() {
        Log.d(TAG, "Enter GetCurrentChannelServiceText + \n");
        return TVNativeWrapper.GetCurrentChannelServiceText_native();
    }

    public int GetVideoSrcTag3DType(String path) {
        Log.d(TAG, "Enter GetVideoSrcTag3DType  \n");
        return TVNativeWrapper.GetVideoSrcTag3DType_native(path);
    }

    public boolean VideoSrcIsProgressive(String path) {
        Log.d(TAG, "Enter VideoSrcIsProgressive \n");
        return TVNativeWrapper.VideoSrcIsProgressive_native(path);
    }
}
