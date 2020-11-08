package com.mediatek.twoworlds.tv;

import android.os.RemoteException;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;

public class MtkTvTVCallbackHandler {
    private static final String TAG = "MtkTvTVCallbackHandler";

    public MtkTvTVCallbackHandler() {
        synchronized (MtkTvTVCallbackHandler.class) {
            try {
                TVCallback.getTVCallback().registerCallback(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void removeListener() {
        try {
            TVCallback.getTVCallback().unregisterCallback(this);
            TVCallback.getTVCallback().removeListenerConfig(this, (String) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int notifySvctxNotificationCode(int code) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySvctxNotificationCode=" + code);
        return 0;
    }

    public int notifySvctxNotificationCodebyPath(int path, int code) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySvctxNotificationCodebyPath(path = " + path + ",code = " + code + ")");
        return 0;
    }

    public int notifyOtherMessage(int a1, int a2, int a3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyOtherMessage a1=" + a1 + " a2=" + a2 + " a3=" + a3);
        return 0;
    }

    public int notifyConfigMessage(int notifyId, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyConfigMessage notifyId=" + notifyId + " data=" + data);
        return 0;
    }

    public int notifyChannelListUpdateMsg(int condition, int reason, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyChannelListChanged: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        return 0;
    }

    public int notifyListModeUpdateMsg(int oldMode, int newMode) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyListModeUpdateMsg: oldMode = " + oldMode + ", newMode = " + newMode + "\n");
        return 0;
    }

    public int notifyOclScanInfo(int msgId, int channelNum) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyOclScanInfo: msgId = " + msgId + ", channelNum = " + channelNum + "\n");
        return 0;
    }

    public int notifyBisskeyUpdateMsg(int condition, int reason, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyBisskeyUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        return 0;
    }

    public int notifySatlListUpdateMsg(int condition, int reason, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySatlListUpdateMsg: condition = " + condition + ", reason = " + reason + ", data = " + data + "\n");
        return 0;
    }

    public int notifyTvproviderUpdateMsg(int svlid, int count, int[] eventType, int[] svlRecId) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTvproviderUpdateMsg: svlid = " + svlid + ", count = " + count + "\n");
        return 0;
    }

    public int notifyConcernColumnUpdateMsg(int svlid, int count, int[] eventType, int[] svlRecId) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyConcernColumnUpdateMsg: svlid = " + svlid + ", count = " + count + "\n");
        return 0;
    }

    public int notifySvlIdUpdateMsg(int condition, int reason, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySvlIdUpdateMsg: svlid = " + condition + "\n");
        return 0;
    }

    public int notifyTslIdUpdateMsg(int condition, int reason, int data) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTslIdUpdateMsg: condition = " + condition + ",reason = " + reason + ",data = " + data + "\n");
        return 0;
    }

    public int notifyShowOSDMessage(int stringId, int msgType) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyShowOSDMessage stringId=" + stringId + " msgType=" + msgType);
        return 0;
    }

    public int notifyHideOSDMessage() throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyHideOSDMessage");
        return 0;
    }

    public int notifyNativeAppStatus(int nativeAppId, boolean show) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyNativeAppStatus nativeAppId=" + nativeAppId + " show=" + show);
        return 0;
    }

    public int notifyAmpVolCtrlMessage(int volume, boolean isMute) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyAmpVolCtrlMessage volume=" + volume + " isMute=" + isMute);
        return 0;
    }

    public int notifyCecNotificationCode(int code) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyCecNotificationCode volume=" + code);
        return 0;
    }

    public int notifyCecFrameInfo(int initLA, int destLA, int opcode, int[] operand, int operandSize) throws RemoteException {
        return 0;
    }

    public int notifySysAudMod(int sysAudMod) throws RemoteException {
        return 0;
    }

    public int notifyCecActiveSource(int destLA, int destPA, boolean activeRoutingPath) throws RemoteException {
        return 0;
    }

    public int notifyMhlScratchpadData(int destLA, int adopterId, int dataLen, int[] scratchpadData) throws RemoteException {
        return 0;
    }

    public int notifyDeviceDiscovery() throws RemoteException {
        TvDebugLog.d(TAG, "Device Discovery Finish");
        return 0;
    }

    public int notifyUiMsDisplay(int uiType, boolean show) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyUiMsDisplay uiType=" + uiType + " show=" + show);
        return 0;
    }

    public int notifySpdInfoFrame(int upStatus, int upStatusChg, int svStatus, int svStatusChg, int[] vndrName, int[] productDes, int srcInfo) throws RemoteException {
        return 0;
    }

    public int notifyEventNotification(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyEventNotification type=" + updateType + "argv1=" + argv1 + "argv2=" + argv2 + "argv3=" + argv3);
        return 0;
    }

    public int notifyRecordPBNotification(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyRecordPBNotification type=" + updateType + "argv1=" + argv1 + "argv2=" + argv2 + "argv3=" + argv3);
        return 0;
    }

    public int notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyScanNotification msg_id=" + msg_id + "scanProgress=" + scanProgress + "channelNum=" + channelNum + "argv4=" + argv4);
        return 0;
    }

    public int notifySimulated3dAutoTurnOff() throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySimulated3dAutoTurnOff:");
        TvDebugLog.d(TAG, "====>Simulated 3D mode has been turned off because 1 hour has elapsed. Press the 3D key to re-start Simulated 3D mode.");
        return 0;
    }

    public int notifySleepTimerChange(int type, int remainingTime) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySimulated3dAutoTurnOff:");
        TvDebugLog.d(TAG, "notifySleepTimerChange type=" + type + ", remaining time =" + remainingTime);
        return 0;
    }

    public int notifyPipPopMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "notifyPipPopMessage updateType=" + updateType);
        return 0;
    }

    public int notifyAVModeMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "notifyAVModeMessage updateType=" + updateType);
        return 0;
    }

    public int notifyGingaMessage(int updateType, String sAppID, String sAppName) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyGingaMessage updateType=" + updateType + " app_id=" + sAppID + " sAppName=" + sAppName);
        return 0;
    }

    public int notifyGingaVolumeChanged(int updateType, int level) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyGingaVolumeChanged updateType=" + updateType + " level=" + level);
        return 0;
    }

    public int notifyMHEG5Message(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyMHEG5Message=" + updateType);
        return 0;
    }

    public int notifyMHEG5LanuchHbbtv(String sAppURL, int argv1) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyMHEG5LanuchHbbtv.");
        return 0;
    }

    public int notifyMHEG5MimeTypeSupport(String sMimeType, boolean[] pbSupported, int argv1) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyMHEG5MimeTypeSupport.");
        return 0;
    }

    public int notifyEWSPAMessage(int msgType) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyEWSPAMessage=" + msgType);
        return 0;
    }

    public int notifyMHPMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyMHPMessage=" + updateType);
        return 0;
    }

    public int notifyCIMessage(int slot_id, int messageType, int arg3, int arg4, MtkTvCIMMIMenuBase MMIMenu, MtkTvCIMMIEnqBase MMIEnq) throws RemoteException {
        return 0;
    }

    public int notifyOADMessage(int messageType, String scheduleInfo, int progress, boolean autoDld, int argv5) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyOADMessage messageType=" + messageType + ", scheduleInfo =" + scheduleInfo + ", progress =" + progress + ", autoDld =" + autoDld);
        return 0;
    }

    public int notifyHBBTVMessage(int callbackType, int[] callbackData, int callbackDataLen) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyHBBTVMessage=" + callbackType);
        return 0;
    }

    public int notifyWarningMessage(int updateType, int channelID, String eventInfo, int duration, int args5) throws RemoteException {
        TvDebugLog.d(TAG, "notifyWarningMessage msg_id=" + updateType);
        return 0;
    }

    public int notifyEASMessage(int updateType, int channel_change, int argv1, int argv2) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyEASMessage:");
        TvDebugLog.d(TAG, "notifyEASMessage updateType=" + updateType + ", channel_change =" + channel_change);
        return 0;
    }

    public int notifyInputSourceMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        return 0;
    }

    public int notifyTeletextMessage(int msg_id, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTeletextMessage messageID=" + msg_id);
        return 0;
    }

    public int notifyFeatureMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyFeatureMessage type=" + updateType + "argv1=" + argv1);
        return 0;
    }

    public int notifyBroadcastMessage(int msgType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyBroadcastMessage type=" + msgType + "argv1=" + argv1);
        return 0;
    }

    public int notifyUpgradeMessage(int msgType, int argv1, int argv2, int argv3) throws RemoteException {
        return 0;
    }

    public int notifyBannerMessage(int msgType, int msgName, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyBannerMessage msgType=" + msgType + ", msgName=" + msgName);
        return 0;
    }

    public int notifyCCMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        return 0;
    }

    public int notifyPWDDialogMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "notifyPWDDialogMessage updateType=" + updateType);
        return 0;
    }

    public int notifyScreenSaverMessage(int msg_id, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "notifyScreenSaverMessage msg_id=" + msg_id);
        return 0;
    }

    public int notifyATSCEventMessage(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
        TvDebugLog.d(TAG, "notifyATSCEventMessage updateType=" + updateType + ",arg1=" + argv1 + ",arg2=" + argv2 + ",arg3=" + argv3);
        return 0;
    }

    public int notifyOpenVCHIPMessage(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyOpenVCHIPMessage:");
        TvDebugLog.d(TAG, "notifyOpenVCHIPMessage updateType=" + updateType);
        return 0;
    }

    public int notifyNoUsedkeyMessage(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
        return 0;
    }

    public int notifyRecordNotification(int updateType, int argv1, int argv2) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyRecordNotification type=" + updateType + "argv1=" + argv1 + "argv2=" + argv2);
        return 0;
    }

    public int notifyTimeshiftNotification(int updateType, long argv1) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftNotification type=" + updateType + "argv1=" + argv1);
        return 0;
    }

    public int notifyTimeshiftRecordStatus(int status, long argv1) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftRecordStatus status=" + status + "argv1=" + argv1);
        return 0;
    }

    public int notifyTimeshiftNoDiskFile(long argv1) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftNoDiskFile argv1=" + argv1);
        return 0;
    }

    public int notifyTimeshiftSpeedUpdate(int speed) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftSpeedUpdate speed=" + speed);
        return 0;
    }

    public int notifyTimeshiftPlaybackStatusUpdate(int status) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftPlaybackStatusUpdate status=" + status);
        return 0;
    }

    public int notifyTimeshiftStorageRemoved() throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyTimeshiftStorageRemoved");
        return 0;
    }

    public int notifyCDTLogoMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyCDTLogoMessage type=" + updateType + "argv1=" + argv1);
        return 0;
    }

    public int notifyGpioStatus(int gpioId, int status) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyGpioStatus gpioId=" + gpioId + ", status =" + status);
        return 0;
    }

    public int notifyUARTSerialPortCallback(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data) {
        TvDebugLog.d(TAG, "(Default Handler) notifyUARTSerialPortCallback uartSerialID=" + uartSerialID + ", ioNotifyCond=" + ioNotifyCond + ", eventCode=" + eventCode + ",data=" + data);
        return 0;
    }

    public int notifySubtitleMsg(int msg_id, int argv1, int argv2, int argv3) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifySubtitleMsg msg_id=" + msg_id + "argv1=" + argv1 + "argv2=" + argv2 + "argv3=" + argv3);
        return 0;
    }

    public int notifyInputSignalChanged(int inputId, boolean hasSignal) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyInputSignalChanged inputId=" + inputId + "hasSignal=" + hasSignal);
        return 0;
    }

    public int notifyVdpMuteAllFinished(int vdpId) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyVdpMuteAllFinished vdpId=" + vdpId);
        return 0;
    }

    public final int addListenerConfig(String cfgId) {
        try {
            TVCallback.getTVCallback().addListenerConfig(this, cfgId);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public final int removeListenerConfig(String cfgId) {
        TvDebugLog.d(TAG, "removeListenerConfig: cfgId = " + cfgId);
        try {
            TVCallback.getTVCallback().removeListenerConfig(this, cfgId);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int notifyConfigValuechanged(String cfgId) throws Exception {
        TvDebugLog.d(TAG, "(Default Handler) notifyConfigValuechanged cfgId=" + cfgId);
        return 0;
    }

    public int notifyHtmlAgentMessage(int callbackType, int[] callbackData, int callbackDataLen) throws RemoteException {
        TvDebugLog.d(TAG, "(Default Handler) notifyHtmlAgentMessage=" + callbackType);
        return 0;
    }
}
