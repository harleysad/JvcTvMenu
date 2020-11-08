package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvExternalUIStatusBase;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;

public class MtkTvBroadcastBase {
    public static final int BRDCST_CM_CTRL_GET_AGC = 5;
    public static final int BRDCST_CM_CTRL_GET_BER = 0;
    public static final int BRDCST_CM_CTRL_GET_DBM_SNR = 1;
    public static final int BRDCST_CM_CTRL_GET_MODULATION = 4;
    public static final int BRDCST_CM_CTRL_GET_SYM_RATE = 3;
    public static final int BRDCST_CM_CTRL_GET_TS_LOCK_STATUS = 6;
    public static final int BRDCST_CM_CTRL_GET_UEC = 2;
    public static final int BRDCST_RET_FAIL = -1;
    public static final int BRDCST_RET_INVALID_ARG = -2;
    public static final int BRDCST_RET_OK = 0;
    protected static final boolean FORWARD_KEY_TO_LINUX_WORLD = false;
    protected static final String TAG = "MtkTvBroadcast ";
    protected MtkTvKeyEventBase mKeyEvent = new MtkTvKeyEventBase();

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

    public int channelSelect(MtkTvChannelInfoBase channelInfo, boolean isNeedCheckBarkerChannel) {
        Log.d(TAG, "Enter channelSelect: \n");
        if (channelInfo != null) {
            Log.d(TAG, "svlId = " + channelInfo.getSvlId() + ", svlRecId = " + channelInfo.getSvlRecId() + ", channellId = " + channelInfo.getChannelId() + ", isNeedCheckBarkerChannel = " + isNeedCheckBarkerChannel + "\n");
            return TVNativeWrapper.channelSelect_native(channelInfo, isNeedCheckBarkerChannel);
        }
        Log.e(TAG, "The 'channelInfo' is null!\n");
        return -2;
    }

    public int channelSelect(MtkTvChannelInfoBase channelInfo, boolean isNeedCheckBarkerChannel, int focus_id) {
        Log.d(TAG, "Enter channelSelect: \n");
        if (channelInfo != null) {
            Log.d(TAG, "svlId = " + channelInfo.getSvlId() + ", svlRecId = " + channelInfo.getSvlRecId() + ", channellId = " + channelInfo.getChannelId() + ", isNeedCheckBarkerChannel = " + isNeedCheckBarkerChannel + ", focus_id = " + focus_id + "\n");
            return TVNativeWrapper.channelSelect_native(channelInfo, isNeedCheckBarkerChannel, focus_id);
        }
        Log.e(TAG, "The 'channelInfo' is null!\n");
        return -2;
    }

    public int channelSelect(int channelId, boolean isNeedCheckBarkerChannel) {
        Log.d(TAG, "Enter channelSelect: \n");
        if (channelId != 0) {
            Log.d(TAG, "channelId = " + channelId + ", isNeedCheckBarkerChannel = " + isNeedCheckBarkerChannel + "\n");
            return TVNativeWrapper.channelSelectByChannelId_native(channelId, isNeedCheckBarkerChannel);
        }
        Log.e(TAG, "The 'channelId' is invalid!\n");
        return -2;
    }

    public int channelSelectSilently(MtkTvChannelInfoBase channelInfo, int windowId) {
        Log.d(TAG, "Enter channelSelect: \n");
        if (channelInfo != null) {
            Log.d(TAG, "svlId = " + channelInfo.getSvlId() + ", svlRecId = " + channelInfo.getSvlRecId() + ", channellId = " + channelInfo.getChannelId() + "\n");
            return TVNativeWrapper.channelSelectSilently_native(channelInfo, windowId);
        }
        Log.e(TAG, "The 'channelInfo' is null!\n");
        return -2;
    }

    public int channelSelectByChannelNumber(int majorNo) {
        Log.d(TAG, "Enter channelSelectByChannelNumber: majorNo = " + majorNo + "\n");
        return TVNativeWrapper.channelSelectByChannelNumber_native(majorNo, 0, false);
    }

    public int channelSelectByChannelNumber(int majorNo, int minorNo) {
        Log.d(TAG, "Enter channelSelectByChannelNumber: majorNo = " + majorNo + ", minorNo = " + minorNo + "\n");
        return TVNativeWrapper.channelSelectByChannelNumber_native(majorNo, minorNo, true);
    }

    public int syncStop(String path, boolean force) {
        Log.d(TAG, "Enter syncStop\n");
        return TVNativeWrapper.syncStop_native(path, force);
    }

    public boolean isSignalLoss() {
        Log.d(TAG, "Enter isSignalLoss\n");
        return TVNativeWrapper.isSignalLoss_native();
    }

    public int getSignalLevel() {
        Log.d(TAG, "Enter getSignalLevel\n");
        return TVNativeWrapper.getSignalLevel_native();
    }

    public int getSignalQuality() {
        Log.d(TAG, "Enter getSignalQuality\n");
        return TVNativeWrapper.getSignalQuality_native();
    }

    public boolean isOrigScrambleAudio(String path) {
        Log.d(TAG, "Enter isOrigScrambleAudio\n");
        return TVNativeWrapper.isOrigScrambleStrm_native(path, 0);
    }

    public boolean isOrigScrambleVideo(String path) {
        Log.d(TAG, "Enter isOrigScrambleVideo\n");
        return TVNativeWrapper.isOrigScrambleStrm_native(path, 1);
    }

    public int getConnectAttr(String path, int e_ctrl) {
        Log.d(TAG, "Enter getConnectAttr\n");
        return TVNativeWrapper.getConnectAttr_native(path, e_ctrl);
    }

    public int changeFreq(String path, MtkTvFreqChgParamBase freqInfo) {
        Log.d(TAG, "Enter changeFreq\n");
        if (freqInfo == null) {
            return -1;
        }
        Log.d(TAG, "ConType = " + freqInfo.getConType() + ", freqType = " + freqInfo.getfreqType() + ", freq = " + freqInfo.getfreq() + ", tunerMod = " + freqInfo.gettunerMod() + ", symRate = " + freqInfo.getsymRate() + ", SatPol = " + freqInfo.getSatPol() + ", SatLstId = " + freqInfo.getSatLstId() + ", SatLstRecId = " + freqInfo.getSatLstRecId() + "\n");
        if (isFregChgInfoValid(freqInfo.getConType(), freqInfo.getfreqType(), freqInfo.getSatPol(), freqInfo.gettunerMod())) {
            return TVNativeWrapper.changeFreq_native(path, freqInfo);
        }
        return -1;
    }

    public int transferExternalUIStatus(MtkTvExternalUIStatusBase uiStatus) {
        Log.d(TAG, "Enter transferExternalUIStatus\n");
        if (uiStatus == null) {
            return -1;
        }
        Log.d(TAG, "uiId = " + uiStatus.getUIId() + ", isShow = " + uiStatus.getShow() + "\n");
        return TVNativeWrapper.transferExternalUIStatus_native(uiStatus);
    }
}
