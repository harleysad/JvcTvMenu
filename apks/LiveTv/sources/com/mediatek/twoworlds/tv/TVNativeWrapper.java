package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvCecBase;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvMultiViewBase;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyHeader;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecActiveSourceBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecDevDiscoveryInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecRecordSouceInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecTimeInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvDvbScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcManualScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsSatelliteSettingBase;
import com.mediatek.twoworlds.tv.model.MtkTvEASParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvExternalUIStatusBase;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;
import com.mediatek.twoworlds.tv.model.MtkTvGingaAppInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvParserIniInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvPipPopFucusInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.model.MtkTvRectangle;
import com.mediatek.twoworlds.tv.model.MtkTvRegionCapability;
import com.mediatek.twoworlds.tv.model.MtkTvScreenModeOverscan;
import com.mediatek.twoworlds.tv.model.MtkTvSrcVideoResolution;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTimeRawDataBase;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeFirmwareInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvVideoInfoBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import java.util.List;

public class TVNativeWrapper {
    private static final String TAG = "TVNativeWrapper(Emu)";
    static boolean mIsregisterCallback = false;

    protected static boolean is_emulator() {
        if (SystemProperties.get("vendor.mtk.inside").length() == 0 || "0".equals(SystemProperties.get("vendor.mtk.inside"))) {
            return true;
        }
        return false;
    }

    protected static int registerDefaultCallback_native() {
        if (mIsregisterCallback || is_emulator()) {
            Log.d(TAG, "registerDefaultCallback_native called");
            return 0;
        }
        mIsregisterCallback = true;
        return TVNative.registerDefaultCallback_native();
    }

    protected static int unregisterDefaultCallback_native() {
        if (!mIsregisterCallback || is_emulator()) {
            Log.d(TAG, "unregisterDefaultCallback_native called");
            return 0;
        }
        mIsregisterCallback = false;
        return TVNative.unregisterDefaultCallback_native();
    }

    protected static int HighLevel_native(int func_id, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
        if (!is_emulator()) {
            return TVNative.HighLevel_native(func_id, arg1, arg2, arg3, arg4, arg5, arg6);
        }
        Log.d(TAG, "HighLevel_native called");
        return 0;
    }

    protected static int launchHbbtv_native(String type, String service, String url) {
        if (!is_emulator()) {
            return TVNative.launchHbbtv_native(type, service, url);
        }
        Log.d(TAG, "launchHbbtv_native called");
        return 0;
    }

    protected static void setSignalMonitor_native(boolean isSignalMonitorOn) {
        if (is_emulator()) {
            Log.d(TAG, "setSignalMonitor_native called");
        } else {
            TVNative.setSignalMonitor_native(isSignalMonitorOn);
        }
    }

    protected static int getWakeUpReason_native() {
        if (!is_emulator()) {
            return TVNative.getWakeUpReason_native();
        }
        Log.d(TAG, "getWakeUpReason called");
        return 5;
    }

    protected static int getWakeUpIrKey_native() {
        if (!is_emulator()) {
            return TVNative.getWakeUpIrKey_native();
        }
        Log.d(TAG, "getWakeUpIrKey_native called");
        return 0;
    }

    protected static boolean checkPCLWakeupReasonToBGM_native() {
        if (!is_emulator()) {
            return TVNative.checkPCLWakeupReasonToBGM_native();
        }
        Log.d(TAG, "checkPCLWakeupReasonToBGM called");
        return false;
    }

    protected static int getPclWakeupSetup_native() {
        if (!is_emulator()) {
            return TVNative.getPclWakeupSetup_native();
        }
        Log.d(TAG, "getPclWakeupSetup_native called");
        return 0;
    }

    protected static int setPclWakeupSetup_native(int setup) {
        if (!is_emulator()) {
            return TVNative.setPclWakeupSetup_native(setup);
        }
        Log.d(TAG, "setPclWakeupSetup_native called");
        return 0;
    }

    protected static void saveTimeStamp_native(String s) {
        if (is_emulator()) {
            Log.d(TAG, "saveTimeStamp_native called");
        } else {
            TVNative.saveTimeStamp_native(s);
        }
    }

    protected static void setLinuxKeyReceive_native(int i) {
        if (is_emulator()) {
            Log.d(TAG, "setLinuxKeyReceive_native called");
        } else {
            TVNative.setLinuxKeyReceive_native(i);
        }
    }

    protected static boolean isBGMapplicable_native(boolean isHintStandByToBGM) {
        if (!is_emulator()) {
            return TVNative.isBGMapplicable_native(isHintStandByToBGM);
        }
        Log.d(TAG, "isBGMapplicable_native called");
        return false;
    }

    protected static int setLinuxBGMStart_native() {
        if (!is_emulator()) {
            return TVNative.setLinuxBGMStart_native();
        }
        Log.d(TAG, "setLinuxBGMStart_native called");
        return 0;
    }

    protected static int setScreenOutputDispRect_native(String path, MtkTvRectangle rect) {
        if (!is_emulator()) {
            return TVNative.setScreenOutputDispRect_native(path, rect);
        }
        Log.d(TAG, "setScreenOutputDispRect_native called");
        return 0;
    }

    protected static int getScreenOutputDispRect_native(String path, MtkTvRectangle rect) {
        if (!is_emulator()) {
            return TVNative.getScreenOutputDispRect_native(path, rect);
        }
        Log.d(TAG, "getScreenOutputDispRect_native called");
        return 0;
    }

    protected static int setScreenSourceRect_native(String path, MtkTvRectangle rect) {
        if (!is_emulator()) {
            return TVNative.setScreenSourceRect_native(path, rect);
        }
        Log.d(TAG, "setScreenSourceRect_native called");
        return 0;
    }

    protected static int getScreenSourceRect_native(String path, MtkTvRectangle rect) {
        if (!is_emulator()) {
            return TVNative.getScreenSourceRect_native(path, rect);
        }
        Log.d(TAG, "getScreenSourceRect_native called");
        return 0;
    }

    protected static int getVideoSrcRegionCapability_native(String path, MtkTvRegionCapability Capability) {
        if (!is_emulator()) {
            return TVNative.getVideoSrcRegionCapability_native(path, Capability);
        }
        Log.d(TAG, "getVideoSrcRegionCapability_native");
        return 0;
    }

    protected static int getDispRegionCapability_native(String path, MtkTvRegionCapability Capability) {
        if (!is_emulator()) {
            return TVNative.getDispRegionCapability_native(path, Capability);
        }
        Log.d(TAG, "getDispRegionCapability_native");
        return 0;
    }

    protected static int getSrcVideoResolution_native(String path, MtkTvSrcVideoResolution resolution) {
        if (!is_emulator()) {
            return TVNative.getSrcVideoResolution_native(path, resolution);
        }
        Log.d(TAG, "getSrcVideoResolution_native");
        return 0;
    }

    protected static void resetPub_native() {
        if (is_emulator()) {
            Log.d(TAG, "resetPub_native called");
        } else {
            TVNative.resetPub_native();
        }
    }

    protected static void resetPri_native() {
        if (is_emulator()) {
            Log.d(TAG, "resetPri_native called");
        } else {
            TVNative.resetPri_native();
        }
    }

    protected static void resetFac_native() {
        if (is_emulator()) {
            Log.d(TAG, "resetFac_native called");
        } else {
            TVNative.resetFac_native();
        }
    }

    protected static String getSysVersion_native(int eType, String sVersion) {
        if (!is_emulator()) {
            return TVNative.getSysVersion_native(eType, sVersion);
        }
        Log.d(TAG, "getSysVersion_native called");
        return "";
    }

    protected static String getSerialNumber_native() {
        if (!is_emulator()) {
            return TVNative.getSerialNumber_native();
        }
        Log.d(TAG, "getSerialNumber_native called");
        return "";
    }

    protected static String getMacAddress_native() {
        if (!is_emulator()) {
            return TVNative.getMacAddress_native();
        }
        Log.d(TAG, "getMacAddress_native called");
        return "";
    }

    protected static boolean setOSDOpacity_native(int opacity) {
        if (!is_emulator()) {
            return TVNative.setOSDOpacity_native(opacity);
        }
        Log.d(TAG, "setOSDOpacity_native called");
        return false;
    }

    protected static void SendKeyClick_native(int dfb_keycode) {
        if (is_emulator()) {
            Log.d(TAG, "SendKeyClick_native called");
        } else {
            TVNative.SendKeyClick_native(dfb_keycode);
        }
    }

    protected static void SendKey_native(int up, int dfb_keycode) {
        if (is_emulator()) {
            Log.d(TAG, "SendKey_native called");
        } else {
            TVNative.SendKey_native(up, dfb_keycode);
        }
    }

    protected static void SendMouseMove_native(int android_screen_x, int android_screen_y, int android_abs_x, int android_abs_y) {
        if (is_emulator()) {
            Log.d(TAG, "SendMouseMove_native called");
        } else {
            TVNative.SendMouseMove_native(android_screen_x, android_screen_y, android_abs_x, android_abs_y);
        }
    }

    protected static void SendMouseButton_native(int button, int up) {
        if (is_emulator()) {
            Log.d(TAG, "SendMouseButton_native called");
        } else {
            TVNative.SendMouseButton_native(button, up);
        }
    }

    protected static int getMinMaxConfigValue_native(int inputGroup, String cfgId) {
        if (!is_emulator()) {
            return TVNative.getMinMaxConfigValue_native(inputGroup, cfgId);
        }
        Log.d(TAG, "getMinMaxConfigValue_native called");
        return 0;
    }

    protected static int getConfigValue_native(int inputGroup, String cfgId) {
        if (!is_emulator()) {
            return TVNative.getConfigValue_native(inputGroup, cfgId);
        }
        Log.d(TAG, "getConfigValue_native called");
        return 0;
    }

    protected static int setConfigValue_native(int inputGroup, String cfgId, int value, int isUpdate) {
        if (!is_emulator()) {
            return TVNative.setConfigValue_native(inputGroup, cfgId, value, isUpdate);
        }
        Log.d(TAG, "setConfigValue_native called");
        return 0;
    }

    protected static int isConfigVisible_native(int inputGroup, String cfgId) {
        if (!is_emulator()) {
            return TVNative.isConfigVisible_native(inputGroup, cfgId);
        }
        Log.d(TAG, "isConfigVisible_native called");
        return 0;
    }

    protected static int isConfigEnabled_native(int inputGroup, String cfgId) {
        if (!is_emulator()) {
            return TVNative.isConfigEnabled_native(inputGroup, cfgId);
        }
        Log.d(TAG, "isConfigEnabled_native called");
        return 0;
    }

    protected static int isConfigItemsEnabled_native(int inputGroup, String cfgId, MtkTvConfigBase cfg) {
        if (!is_emulator()) {
            return TVNative.isConfigItemsEnabled_native(inputGroup, cfgId, cfg);
        }
        Log.d(TAG, "isConfigItemsEnabled_native called");
        return 0;
    }

    protected static int resetConfigValues_native(int type) {
        if (!is_emulator()) {
            return TVNative.resetConfigValues_native(type);
        }
        Log.d(TAG, "resetConfigValues_native called");
        return 0;
    }

    protected static int setPipConfig_native(int status) {
        if (!is_emulator()) {
            return TVNative.setPipConfig_native(status);
        }
        Log.d(TAG, "setPipConfig_native called");
        return 0;
    }

    protected static String getConfigString_native(String cfgId) {
        if (!is_emulator()) {
            return TVNative.getConfigString_native(cfgId);
        }
        Log.d(TAG, "getConfigString_native called");
        return "";
    }

    protected static int setConfigString_native(String cfgId, String value) {
        if (!is_emulator()) {
            return TVNative.setConfigString_native(cfgId, value);
        }
        Log.d(TAG, "setConfigString_native called");
        return 0;
    }

    protected static int addConfigListener_native(String cfgId) {
        if (!is_emulator()) {
            return TVNative.addConfigListener_native(cfgId);
        }
        Log.d(TAG, "addConfigListener_native called");
        return 0;
    }

    protected static int removeConfigListener_native(String cfgId) {
        if (!is_emulator()) {
            return TVNative.removeConfigListener_native(cfgId);
        }
        Log.d(TAG, "removeConfigListener_native called");
        return 0;
    }

    protected static int getInputSourceTotalNumber_native() {
        if (!is_emulator()) {
            return TVNative.getInputSourceTotalNumber_native();
        }
        Log.d(TAG, "getInputSourceTotalNumber_native called");
        return 0;
    }

    protected static int getInputSourceRecbyidx_native(int idx, MtkTvInputSourceBase.InputSourceRecord rec) {
        if (!is_emulator()) {
            return TVNative.getInputSourceRecbyidx_native(idx, rec);
        }
        Log.d(TAG, "getInputSourceRecbyidx_native called");
        return 0;
    }

    protected static String getCurrentInputSourceName_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentInputSourceName_native();
        }
        Log.d(TAG, "getCurrentInputSourceName_native called");
        return null;
    }

    protected static String getCurrentInputSourceName_native(String path) {
        if (!is_emulator()) {
            return TVNative.getCurrentInputSourceName_native(path);
        }
        Log.d(TAG, "getCurrentInputSourceName_native called");
        return null;
    }

    protected static String getInputSourceNamebySourceid_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.getInputSourceNamebySourceid_native(source_id);
        }
        Log.d(TAG, "getInputSourceNamebySourceid_native called");
        return null;
    }

    protected static int changeInputSourcebySourceid_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.changeInputSourcebySourceid_native(source_id);
        }
        Log.d(TAG, "changeInputSourcebySourceid_native called");
        return 0;
    }

    protected static int changeInputSourcebySourceid_native(int source_id, String path) {
        if (!is_emulator()) {
            return TVNative.changeInputSourcebySourceid_native(source_id, path);
        }
        Log.d(TAG, "changeInputSourcebySourceid_native called");
        return 0;
    }

    protected static boolean isBlockEx_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.isBlockEx_native(source_id);
        }
        Log.d(TAG, "isBlockEx_native called");
        return false;
    }

    protected static boolean isBlock_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.isBlock_native(source_id);
        }
        Log.d(TAG, "isBlock_native called");
        return false;
    }

    protected static int block_native(int source_id, boolean block) {
        if (!is_emulator()) {
            return TVNative.block_native(source_id, block);
        }
        Log.d(TAG, "block_native called");
        return 0;
    }

    protected static boolean isAutoDetectPlugStatus_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.isAutoDetectPlugStatus_native(source_id);
        }
        Log.d(TAG, "isAutoDetectPlugStatus_native called");
        return false;
    }

    protected static int setAutoDetectPlugStatus_native(int source_id, boolean needDetect) {
        if (!is_emulator()) {
            return TVNative.setAutoDetectPlugStatus_native(source_id, needDetect);
        }
        Log.d(TAG, "setAutoDetectPlugStatus_native called");
        return 0;
    }

    protected static int getInputLabelIdx_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.getInputLabelIdx_native(source_id);
        }
        Log.d(TAG, "getInputLabelIdx_native called");
        return 0;
    }

    protected static int setInputLabelIdx_native(int source_id, int labelidx) {
        if (!is_emulator()) {
            return TVNative.setInputLabelIdx_native(source_id, labelidx);
        }
        Log.d(TAG, "setInputLabelIdx_native called");
        return 0;
    }

    protected static String getInputLabelUserDefName_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.getInputLabelUserDefName_native(source_id);
        }
        Log.d(TAG, "getInputLabelUserDefName_native called");
        return null;
    }

    protected static int setInputLabelUserDefName_native(int source_id, String userdef) {
        if (!is_emulator()) {
            return TVNative.setInputLabelUserDefName_native(source_id, userdef);
        }
        Log.d(TAG, "setInputLabelUserDefName_native called");
        return 0;
    }

    protected static boolean getExternalDeviceHasSignal_native(MtkTvInputSourceBase.InputDeviceType inputType, int internalIdx) {
        if (!is_emulator()) {
            return TVNative.getExternalDeviceHasSignal_native(inputType, internalIdx);
        }
        Log.d(TAG, "getExternalDeviceHasSignal_native called");
        return false;
    }

    public static int inputSyncStop_native(String path, boolean force) {
        if (!is_emulator()) {
            return TVNative.inputSyncStop_native(path, force);
        }
        Log.d(TAG, "inputSyncStop_native called...\n");
        return 0;
    }

    protected static boolean queryConflict_native(int sourceId1, int sourceId2) {
        if (!is_emulator()) {
            return TVNative.queryConflict_native(sourceId1, sourceId2);
        }
        Log.d(TAG, "queryConflict_native called");
        return false;
    }

    protected static int getMhlPortNum_native() {
        if (!is_emulator()) {
            return TVNative.getMhlPortNum_native();
        }
        Log.d(TAG, "getMhlPortNum_native called");
        return 0;
    }

    protected static int setScartAutoJump_native(boolean enable) {
        if (!is_emulator()) {
            return TVNative.setScartAutoJump_native(enable);
        }
        Log.d(TAG, "setScartAutoJump_native called");
        return 0;
    }

    protected static int getPOPTunerFocus_native() {
        if (!is_emulator()) {
            return TVNative.getPOPTunerFocus_native();
        }
        Log.d(TAG, "getPOPTunerFocus_native called");
        return 0;
    }

    protected static int setAudioFocusbySourceid_native(int source_id) {
        if (!is_emulator()) {
            return TVNative.setAudioFocusbySourceid_native(source_id);
        }
        Log.d(TAG, "setAudioFocusbySourceid_native called");
        return 0;
    }

    protected static int setAudioFocus_native(String path) {
        if (!is_emulator()) {
            return TVNative.setAudioFocus_native(path);
        }
        Log.d(TAG, "setAudioFocus_native called");
        return 0;
    }

    protected static int getAudioFocus_native() {
        if (!is_emulator()) {
            return TVNative.getAudioFocus_native();
        }
        Log.d(TAG, "getAudioFocus_native called");
        return 0;
    }

    protected static boolean isTvRunning_native(String path) {
        if (!is_emulator()) {
            return TVNative.isTvRunning_native(path);
        }
        Log.d(TAG, "setAudioFocus_native called");
        return false;
    }

    protected static int onlyChgFocus_native(String path) {
        if (!is_emulator()) {
            return TVNative.onlyChgFocus_native(path);
        }
        Log.d(TAG, "onlyChgFocus_native called");
        return 0;
    }

    protected static int setNewTvMode_native(int tv_mode) {
        if (!is_emulator()) {
            return TVNative.setNewTvMode_native(tv_mode);
        }
        Log.d(TAG, "setNewTvMode_native called");
        return 0;
    }

    protected static boolean isMultiviewInputsourceAvailable_native(int ui1_input_src, String path) {
        if (!is_emulator()) {
            return TVNative.isMultiviewInputsourceAvailable_native(ui1_input_src, path);
        }
        Log.d(TAG, "isMultiviewInputsourceAvailable_native called");
        return false;
    }

    protected static int startMainVideo_native(int ui1_main_input_src, MtkTvMultiViewBase.Region_Info_T region) {
        if (!is_emulator()) {
            return TVNative.startMainVideo_native(ui1_main_input_src, region);
        }
        Log.d(TAG, "startMainVideo_native called");
        return 0;
    }

    protected static int startSubVideo_native(int ui1_sub_input_src, MtkTvMultiViewBase.Region_Info_T sub_region) {
        if (!is_emulator()) {
            return TVNative.startSubVideo_native(ui1_sub_input_src, sub_region);
        }
        Log.d(TAG, "startSubVideo_native called");
        return 0;
    }

    protected static int stopMainVideo_native() {
        if (!is_emulator()) {
            return TVNative.stopMainVideo_native();
        }
        Log.d(TAG, "stopMainVideo_native called");
        return 0;
    }

    protected static int stopSubVideo_native() {
        if (!is_emulator()) {
            return TVNative.stopSubVideo_native();
        }
        Log.d(TAG, "stopSubVideo_native called");
        return 0;
    }

    protected static int setChgSource_native(boolean b_chg_source) {
        if (!is_emulator()) {
            return TVNative.setChgSource_native(b_chg_source);
        }
        Log.d(TAG, "setChgSource_native called");
        return 0;
    }

    protected static int incrVolume_native() {
        if (!is_emulator()) {
            return TVNative.incrVolume_native();
        }
        Log.d(TAG, "incrVolume_native called");
        return 0;
    }

    protected static int incrVolume_native(int mType) {
        if (!is_emulator()) {
            return TVNative.incrVolume_native(mType);
        }
        Log.d(TAG, "incrVolume_native called");
        return 0;
    }

    protected static int decrVolume_native() {
        if (!is_emulator()) {
            return TVNative.decrVolume_native();
        }
        Log.d(TAG, "decrVolume_native called");
        return 0;
    }

    protected static int decrVolume_native(int mType) {
        if (!is_emulator()) {
            return TVNative.decrVolume_native(mType);
        }
        Log.d(TAG, "decrVolume_native called");
        return 0;
    }

    protected static int setVolume_native(int value) {
        if (!is_emulator()) {
            return TVNative.setVolume_native(value);
        }
        Log.d(TAG, "setVolume_native called");
        return 0;
    }

    protected static int setVolume_native(int mType, int value) {
        if (!is_emulator()) {
            return TVNative.setVolume_native(mType, value);
        }
        Log.d(TAG, "setVolume_native called");
        return 0;
    }

    protected static int getVolume_native() {
        if (!is_emulator()) {
            return TVNative.getVolume_native();
        }
        Log.d(TAG, "getVolume_native called");
        return 0;
    }

    protected static int getVolume_native(int mType) {
        if (!is_emulator()) {
            return TVNative.getVolume_native(mType);
        }
        Log.d(TAG, "getVolume_native called");
        return 0;
    }

    protected static int setMute_native(boolean isMute) {
        if (!is_emulator()) {
            return TVNative.setMute_native(isMute);
        }
        Log.d(TAG, "setMute_native called");
        return 0;
    }

    protected static int setMute_native(int mType, boolean isMute) {
        if (!is_emulator()) {
            return TVNative.setMute_native(mType, isMute);
        }
        Log.d(TAG, "setMute_native called");
        return 0;
    }

    protected static boolean getMute_native() {
        if (!is_emulator()) {
            return TVNative.getMute_native();
        }
        Log.d(TAG, "setMute_native called");
        return false;
    }

    protected static boolean getMute_native(int mType) {
        if (!is_emulator()) {
            return TVNative.getMute_native(mType);
        }
        Log.d(TAG, "setMute_native called");
        return false;
    }

    protected static int channelSelect_native(MtkTvChannelInfoBase channelInfo, boolean checkBarkerChannel) {
        if (!is_emulator()) {
            return TVNative.channelSelect_native(channelInfo, checkBarkerChannel);
        }
        Log.d(TAG, "channelSelect_native called...\n");
        return 0;
    }

    protected static int channelSelect_native(MtkTvChannelInfoBase channelInfo, boolean checkBarkerChannel, int focus_id) {
        if (!is_emulator()) {
            return TVNative.channelSelect_native(channelInfo, checkBarkerChannel, focus_id);
        }
        Log.d(TAG, "channelSelect_native called...\n");
        return 0;
    }

    protected static int channelSelectByKeys_native(int keyCode) {
        if (!is_emulator()) {
            return TVNative.channelSelectByKeys_native(keyCode);
        }
        Log.d(TAG, "channelSelectByKeys_native called...\n");
        return 0;
    }

    protected static int channelSelectByChannelId_native(int channelid, boolean checkBarkerChannel) {
        if (!is_emulator()) {
            return TVNative.channelSelectByChannelId_native(channelid, checkBarkerChannel);
        }
        Log.d(TAG, "channelSelectByChannelId_native called...\n");
        return 0;
    }

    protected static int channelSelectSilently_native(MtkTvChannelInfoBase channelInfo, int windowId) {
        if (!is_emulator()) {
            return TVNative.channelSelectSilently_native(channelInfo, windowId);
        }
        Log.d(TAG, "channelSelectSilently_native called...\n");
        return 0;
    }

    protected static int channelSelectByChannelNumber_native(int majorNo, int minorNo, boolean isMinorNoValid) {
        if (!is_emulator()) {
            return TVNative.channelSelectByChannelNumber_native(majorNo, minorNo, isMinorNoValid);
        }
        Log.d(TAG, "channelSelectByChannelNumber_native called...\n");
        return 0;
    }

    public static int syncStop_native(String path, boolean force) {
        if (!is_emulator()) {
            return TVNative.syncStop_native(path, force);
        }
        Log.d(TAG, "syncStop_native called...\n");
        return 0;
    }

    public static boolean isSignalLoss_native() {
        if (!is_emulator()) {
            return TVNative.isSignalLoss_native();
        }
        Log.d(TAG, "isSignalLoss_native called...\n");
        return false;
    }

    public static int getSignalLevel_native() {
        if (!is_emulator()) {
            return TVNative.getSignalLevel_native();
        }
        Log.d(TAG, "getSignalLevel_native called...\n");
        return 0;
    }

    public static int getSignalQuality_native() {
        if (!is_emulator()) {
            return TVNative.getSignalQuality_native();
        }
        Log.d(TAG, "getSignalQuality_native called...\n");
        return 0;
    }

    public static boolean isOrigScrambleStrm_native(String path, int strmType) {
        if (!is_emulator()) {
            return TVNative.isOrigScrambleStrm_native(path, strmType);
        }
        Log.d(TAG, "isOrigScrambleStrm_native called...\n");
        return false;
    }

    protected static int switchInputToTvSource_native() {
        if (!is_emulator()) {
            return TVNative.switchInputToTvSource_native();
        }
        Log.d(TAG, "switchInputToTvSource_native called...\n");
        return 0;
    }

    protected static boolean isCamInstalled_native() {
        if (!is_emulator()) {
            return TVNative.isCamInstalled_native();
        }
        Log.d(TAG, "isCamInstalled_native called...\n");
        return false;
    }

    protected static boolean isSupport4DigitsChannelNo_native() {
        if (!is_emulator()) {
            return TVNative.isSupport4DigitsChannelNo_native();
        }
        Log.d(TAG, "isSupport4DigitsChannelNo_native called...\n");
        return false;
    }

    protected static boolean isDigitKeysHandled_native() {
        if (!is_emulator()) {
            return TVNative.isDigitKeysHandled_native();
        }
        Log.d(TAG, "isDigitKeysHandled_native called...\n");
        return true;
    }

    protected static void resetDigitKeysHandledFlag_native() {
        if (is_emulator()) {
            Log.d(TAG, "resetDigitKeysHandledFlag_native called...\n");
        } else {
            TVNative.resetDigitKeysHandledFlag_native();
        }
    }

    public static int getConnectAttr_native(String path, int e_ctrl) {
        if (!is_emulator()) {
            return TVNative.getConnectAttr_native(path, e_ctrl);
        }
        Log.d(TAG, "getConnectAttr_native called...\n");
        return -1;
    }

    public static int changeFreq_native(String path, MtkTvFreqChgParamBase freqInfo) {
        if (!is_emulator()) {
            return TVNative.changeFreq_native(path, freqInfo);
        }
        Log.d(TAG, "changeFreq_native called...\n");
        return 0;
    }

    public static int transferExternalUIStatus_native(MtkTvExternalUIStatusBase uiStatus) {
        if (!is_emulator()) {
            return TVNative.transferExternalUIStatus_native(uiStatus);
        }
        Log.d(TAG, "transferExternalUIStatus_native called...\n");
        return 0;
    }

    protected static int getCurrentChannel_native(MtkTvChannelInfoBase crntChannelInfo) {
        if (!is_emulator()) {
            return TVNative.getCurrentChannel_native(crntChannelInfo);
        }
        Log.d(TAG, "getCurrentChannel_native called...\n");
        return 0;
    }

    protected static MtkTvChannelInfoBase getCurrentChannelFromNav_native(int winId) {
        if (!is_emulator()) {
            return TVNative.getCurrentChannelFromNav_native(winId);
        }
        Log.d(TAG, "getCurrentChannelFromNav_native called...\n");
        return null;
    }

    protected static int getCurrentChannelId_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentChannelId_native();
        }
        Log.d(TAG, "getCurrentChannelId_native called...\n");
        return 0;
    }

    protected static int getChannelType_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.getChannelType_native(channelId);
        }
        Log.d(TAG, "getChannelType_native called...\n");
        return 0;
    }

    protected static int cleanChannelList_native(int svlID, boolean clearLOL) {
        if (!is_emulator()) {
            return TVNative.cleanChannelList_native(svlID, clearLOL);
        }
        Log.d(TAG, "cleanChannelList_native called...\n");
        return 0;
    }

    protected static int createSnapshot_native(int svlID) {
        if (!is_emulator()) {
            return TVNative.createSnapshot_native(svlID);
        }
        Log.d(TAG, "createSnapshot_native called...\n");
        return 0;
    }

    protected static int restoreSnapshot_native(int snapshotID) {
        if (!is_emulator()) {
            return TVNative.restoreSnapshot_native(snapshotID);
        }
        Log.d(TAG, "restoreSnapshot_native called...\n");
        return 0;
    }

    protected static int freeSnapshot_native(int snapshotID) {
        if (!is_emulator()) {
            return TVNative.freeSnapshot_native(snapshotID);
        }
        Log.d(TAG, "freeSnapshot_native called...\n");
        return 0;
    }

    protected static int setCurrentChannel_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.setCurrentChannel_native(channelId);
        }
        Log.d(TAG, "setCurrentChannel_native called...\n");
        return 0;
    }

    protected static int set2ndCurrentChannelBySvlId_native(int svlId, int channelId) {
        if (!is_emulator()) {
            return TVNative.set2ndCurrentChannelBySvlId_native(svlId, channelId);
        }
        Log.d(TAG, "set2ndCurrentChannelBySvlId_native called...\n");
        return 0;
    }

    protected static int setCurrentChannelBySvlId_native(int svlId, int channelId) {
        if (!is_emulator()) {
            return TVNative.setCurrentChannelBySvlId_native(svlId, channelId);
        }
        Log.d(TAG, "setCurrentChannelBySvlId_native called...\n");
        return 0;
    }

    protected static int getChannelList_native(int svlId, List<MtkTvChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getChannelList_native(svlId, channelList);
        }
        Log.d(TAG, "getChannelList_native called...\n");
        return 0;
    }

    protected static int getChannelInfoByChannelId_native(int channelId, MtkTvChannelInfoBase channelInfo) {
        if (!is_emulator()) {
            return TVNative.getChannelInfoByChannelId_native(channelId, channelInfo);
        }
        Log.d(TAG, "getChannelInfoByChannelId_native called...\n");
        return 0;
    }

    protected static int setChannelList_native(int channelOperator, int svlId, List<MtkTvChannelInfoBase> channels) {
        if (!is_emulator()) {
            return TVNative.setChannelList_native(channelOperator, svlId, channels);
        }
        Log.d(TAG, "setChannelList_native called...\n");
        return 0;
    }

    protected static int swapChannelInfo_native(int channelIdA, int channelIdB, int svlId) {
        if (!is_emulator()) {
            return TVNative.swapChannelInfo_native(channelIdA, channelIdB, svlId);
        }
        Log.d(TAG, "swapChannelInfo_native called...\n");
        return 0;
    }

    protected static int swapDigitalFavorites_native(int favIndex, int channelIdA, int channelIdB, int svlId) {
        if (!is_emulator()) {
            return TVNative.swapDigitalFavorites_native(favIndex, channelIdA, channelIdB, svlId);
        }
        Log.d(TAG, "swapDigitalFavorites_native called...\n");
        return 0;
    }

    protected static int getChannelRFInfo_native(int svlId, int channelId, int rfInfoType) {
        if (!is_emulator()) {
            return TVNative.getChannelRFInfo_native(svlId, channelId, rfInfoType);
        }
        Log.d(TAG, "queryCurrentChannelRFInfo_native called...\n");
        return 0;
    }

    protected static int getDigitalFavoritesList_native(int favIndex, int favType, int broadcastMedium, List<MtkTvChannelInfoBase> favoritesList) {
        if (!is_emulator()) {
            return TVNative.getDigitalFavoritesList_native(favIndex, favType, broadcastMedium, favoritesList);
        }
        Log.d(TAG, "getDigitalFavoritesList_native called...\n");
        return 0;
    }

    protected static int setDigitalFavoritesList_native(int favIndex, int svlId, int favoritesOperator, List<MtkTvChannelInfoBase> channelsToSet, int order) {
        if (!is_emulator()) {
            return TVNative.setDigitalFavoritesList_native(favIndex, svlId, favoritesOperator, channelsToSet, order);
        }
        Log.d(TAG, "setDigitalFavoritesList_native called...\n");
        return 0;
    }

    protected static int getChannelListByFilter_native(int svlId, int filter, int channelId, int prevCount, int nextCount, List<MtkTvChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getChannelListByFilter_native(svlId, filter, channelId, prevCount, nextCount, channelList);
        }
        Log.d(TAG, "getChannelListByFilter_native called...\n");
        return 0;
    }

    protected static int getChannelCountByFilter_native(int svlId, int filter) {
        if (!is_emulator()) {
            return TVNative.getChannelCountByFilter_native(svlId, filter);
        }
        Log.d(TAG, "getChannelCountByFilter_native called...\n");
        return 0;
    }

    protected static int getChannelListByQueryInfo_native(int svlId, MtkTvChannelQuery info, List<MtkTvChannelInfoBase> channelList, int count) {
        if (!is_emulator()) {
            return TVNative.getChannelListByQueryInfo_native(svlId, info, channelList, count);
        }
        Log.d(TAG, "getChannelListByQueryInfo_native called...\n");
        return 0;
    }

    protected static int getChannelCountByQueryInfo_native(int svlId, MtkTvChannelQuery info) {
        if (!is_emulator()) {
            return TVNative.getChannelCountByQueryInfo_native(svlId, info);
        }
        Log.d(TAG, "getChannelCountByQueryInfo_native called...\n");
        return 0;
    }

    protected static int getChannelCountByMask_native(int svlId, int mask, int value, int satRecId) {
        if (!is_emulator()) {
            return TVNative.getChannelCountByMask_native(svlId, mask, value, satRecId);
        }
        Log.d(TAG, "getChannelCountByMask_native called...\n");
        return 0;
    }

    protected static int getChannelListByMask_native(int svlId, int mask, int value, int satRecId, int dir, int channel, int num, List<MtkTvChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getChannelListByMask_native(svlId, mask, value, satRecId, dir, channel, num, channelList);
        }
        Log.d(TAG, "getChannelListByMask_native called...\n");
        return 0;
    }

    protected static int getTvproviderBySvlId_native(int svlId, List<TvProviderChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getTvproviderBySvlId_native(svlId, channelList);
        }
        Log.d(TAG, "getTvproviderBySvlId_native called...\n");
        return 0;
    }

    protected static int getAllTvprovider_native(List<TvProviderChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getAllTvprovider_native(channelList);
        }
        Log.d(TAG, "getAllTvprovider_native called...\n");
        return 0;
    }

    protected static int getTvproviderBySvlRecId_native(int svlId, int svlRecId, TvProviderChannelInfoBase record) {
        if (!is_emulator()) {
            return TVNative.getTvproviderBySvlRecId_native(svlId, svlRecId, record);
        }
        Log.d(TAG, "getTvproviderBySvlRecId_native called...\n");
        return 0;
    }

    protected static MtkTvChannelInfoBase getChannelInfoBySvlRecId_native(int svlId, int svlRecId) {
        if (!is_emulator()) {
            return TVNative.getChannelInfoBySvlRecId_native(svlId, svlRecId);
        }
        Log.d(TAG, "getChannelInfoBySvlRecId_native called...\n");
        return null;
    }

    protected static int deleteChannelByBrdcstType_native(int svlId, int brdcstType, boolean clearLOL) {
        if (!is_emulator()) {
            return TVNative.deleteChannelByBrdcstType_native(svlId, brdcstType, clearLOL);
        }
        Log.d(TAG, "deleteChannelByBrdcstType called...\n");
        return 0;
    }

    protected static boolean isChannelNumberExsit_native(int svlId, int ch_num) {
        if (!is_emulator()) {
            return TVNative.isChannelNumberExsit_native(svlId, ch_num);
        }
        Log.d(TAG, "isChannelNumberExsit_native called...\n");
        return true;
    }

    protected static int getTvproviderOcl_native(List<TvProviderChannelInfoBase> channelList) {
        if (!is_emulator()) {
            return TVNative.getTvproviderOcl_native(channelList);
        }
        Log.d(TAG, "getTvproviderOcl_native called...\n");
        return 0;
    }

    protected static int oneChannelListArrange_native(int svlIdT, int svlIdC) {
        if (!is_emulator()) {
            return TVNative.oneChannelListArrange_native(svlIdT, svlIdC);
        }
        Log.d(TAG, "getTvproviderOcl_native called... svlIdT=\n" + svlIdT + ", svlIdC=" + svlIdC);
        return 0;
    }

    protected static int getChannelListMode_native() {
        if (!is_emulator()) {
            return TVNative.getChannelListMode_native();
        }
        Log.d(TAG, "getChannelListMode_native called...\n");
        return 0;
    }

    protected static int getChannelPumpVer_native(int svlId) {
        if (!is_emulator()) {
            return TVNative.getChannelPumpVer_native(svlId);
        }
        Log.d(TAG, "getChannelPumpVer_native called. svlId = " + svlId);
        return 0;
    }

    protected static int setChannelPumpVer_native(int svlId, int pumpVer) {
        if (!is_emulator()) {
            return TVNative.setChannelPumpVer_native(svlId, pumpVer);
        }
        Log.d(TAG, "setChannelPumpVer_native called. svlId = " + svlId + ", pumpVer = " + pumpVer);
        return 0;
    }

    protected static int oneChannelListAddToMax_native(int svlId, int svlRecId) {
        if (!is_emulator()) {
            return TVNative.oneChannelListAddToMax_native(svlId, svlRecId);
        }
        Log.d(TAG, "oneChannelListAddToMax_native called. svlId = " + svlId + ", svlRecId = " + svlRecId);
        return 0;
    }

    protected static int getFavoritelistByFilter_native(List<MtkTvFavoritelistInfoBase> favoriteList) {
        if (!is_emulator()) {
            return TVNative.getFavoritelistByFilter_native(favoriteList);
        }
        Log.d(TAG, "getFavoritelistByFilter_native called...\n");
        return 0;
    }

    protected static int addFavoritelistChannel_native() {
        if (!is_emulator()) {
            return TVNative.addFavoritelistChannel_native();
        }
        Log.d(TAG, "addFavoritelistChannel_native called...\n");
        return 0;
    }

    protected static int addFavoritelistChannelByIndex_native(int index) {
        if (!is_emulator()) {
            return TVNative.addFavoritelistChannelByIndex_native(index);
        }
        Log.d(TAG, "addFavoritelistChannelByIndex_native called...\n");
        return 0;
    }

    protected static int removeFavoritelistChannelByIndexWithoutShowFavIcon_native(int index) {
        if (!is_emulator()) {
            return TVNative.removeFavoritelistChannelByIndexWithoutShowFavIcon_native(index);
        }
        Log.d(TAG, "removeFavoritelistChannelByIndexWithoutShowFavIcon_native called...\n");
        return 0;
    }

    protected static int removeFavoritelistChannel_native(int index) {
        if (!is_emulator()) {
            return TVNative.removeFavoritelistChannel_native(index);
        }
        Log.d(TAG, "removeFavoritelistChannel_native called...\n");
        return 0;
    }

    protected static int storeFavoritelistChannel_native() {
        if (!is_emulator()) {
            return TVNative.storeFavoritelistChannel_native();
        }
        Log.d(TAG, "storeFavoritelistChannel_native called...\n");
        return 0;
    }

    protected static int swapFavoritelistByIndex_native(int index1, int index2) {
        if (!is_emulator()) {
            return TVNative.swapFavoritelistByIndex_native(index1, index2);
        }
        Log.d(TAG, "swapFavoritelistByIndex_native called...\n");
        return 0;
    }

    protected static int satllistLockDatabase_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.satllistLockDatabase_native(satlId);
        }
        Log.d(TAG, "satllistLockDatabase_native called...\n");
        return 0;
    }

    protected static int satllistUnLockDatabase_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.satllistUnLockDatabase_native(satlId);
        }
        Log.d(TAG, "satllistUnLockDatabase_native called...\n");
        return 0;
    }

    protected static int satllistReadLockDatabase_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.satllistReadLockDatabase_native(satlId);
        }
        Log.d(TAG, "satllistReadLockDatabase_native called...\n");
        return 0;
    }

    protected static int satllistReadUnLockDatabase_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.satllistReadUnLockDatabase_native(satlId);
        }
        Log.d(TAG, "satllistReadUnLockDatabase_native called...\n");
        return 0;
    }

    protected static int satllistCleanDatabase_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.satllistCleanDatabase_native(satlId);
        }
        Log.d(TAG, "satllistCleanDatabase_native called...\n");
        return 0;
    }

    protected static int getSatlRecord_native(int satlId, int satlRecId, List<MtkTvDvbsConfigInfoBase> satlList) {
        if (!is_emulator()) {
            return TVNative.getSatlRecord_native(satlId, satlRecId, satlList);
        }
        Log.d(TAG, "getSatlRecordx_native called...\n");
        return 0;
    }

    protected static int getSatlRecordByRecIdx_native(int satlId, int idx, List<MtkTvDvbsConfigInfoBase> satlList) {
        if (!is_emulator()) {
            return TVNative.getSatlRecordByRecIdx_native(satlId, idx, satlList);
        }
        Log.d(TAG, "getSatlRecordByRecIdx_native called...\n");
        return 0;
    }

    protected static int getSatlNumRecs_native(int satlId) {
        if (!is_emulator()) {
            return TVNative.getSatlNumRecs_native(satlId);
        }
        Log.d(TAG, "getSatlNumRecs_native called...\n");
        return 0;
    }

    protected static int updateSatlRecord_native(int satlId, MtkTvDvbsConfigInfoBase satlList, boolean mustExist) {
        if (!is_emulator()) {
            return TVNative.updateSatlRecord_native(satlId, satlList, mustExist);
        }
        Log.d(TAG, "updateSatlRecord_native called...\n");
        return 0;
    }

    protected static int getRecordsNumber_native(int bslId) {
        if (!is_emulator()) {
            return TVNative.getRecordsNumber_native(bslId);
        }
        Log.d(TAG, "getRecordsNumber_native called...\n");
        return 0;
    }

    protected static int setBisskeyInfo_native(int bslId, MtkTvBisskeyInfoBase bslInfo, int operator) {
        if (!is_emulator()) {
            return TVNative.setBisskeyInfo_native(bslId, bslInfo, operator);
        }
        Log.d(TAG, "setBisskeyInfo_native called...\n");
        return 0;
    }

    protected static MtkTvBisskeyInfoBase getRecordByBslRecId_native(int bslId, int bslRecId) {
        if (!is_emulator()) {
            return TVNative.getRecordByBslRecId_native(bslId, bslRecId);
        }
        Log.d(TAG, "getRecordByBslRecId_native called...\n");
        return null;
    }

    protected static int getRecordNumByHeader_native(int bslId, MtkTvBisskeyHeader header) {
        if (!is_emulator()) {
            return TVNative.getRecordNumByHeader_native(bslId, header);
        }
        Log.d(TAG, "getRecordNumByHeader_native called...\n");
        return 0;
    }

    protected static MtkTvBisskeyInfoBase getRecordByHeader_native(int bslId, MtkTvBisskeyHeader header, int index) {
        if (!is_emulator()) {
            return TVNative.getRecordByHeader_native(bslId, header, index);
        }
        Log.d(TAG, "getRecordByHeader_native called...\n");
        return null;
    }

    protected static MtkTvBisskeyInfoBase getRecordByIndex_native(int bslId, int index) {
        if (!is_emulator()) {
            return TVNative.getRecordByIndex_native(bslId, index);
        }
        Log.d(TAG, "getRecordByIndex_native called...\n");
        return null;
    }

    protected static int bisskeyClean_native(int bslId) {
        if (!is_emulator()) {
            return TVNative.bisskeyClean_native(bslId);
        }
        Log.d(TAG, "bisskeyClean_native called...\n");
        return 0;
    }

    protected static int bisskeyIsSameRecord_native(MtkTvBisskeyInfoBase bslInfo1, MtkTvBisskeyInfoBase bslInfo2) {
        if (!is_emulator()) {
            return TVNative.bisskeyIsSameRecord_native(bslInfo1, bslInfo2);
        }
        Log.d(TAG, "bisskeyIsSameRecord_native called...\n");
        return 0;
    }

    protected static MtkTvBisskeyInfoBase bisskeyGetDefaultRecord_native() {
        if (!is_emulator()) {
            return TVNative.bisskeyGetDefaultRecord_native();
        }
        Log.d(TAG, "bisskeyGetDefaultRecord_native called...\n");
        return null;
    }

    protected static int bisskeySetKeyForCurrentChannel_native() {
        if (!is_emulator()) {
            return TVNative.bisskeySetKeyForCurrentChannel_native();
        }
        Log.d(TAG, "bisskeySetKeyForCurrentChannel_native called...\n");
        return 0;
    }

    protected static int getCecDevInfo_native(int logAddr, MtkTvCecBase.CecDevInfo cecDevInfo) {
        if (!is_emulator()) {
            return TVNative.getCecDevInfo_native(logAddr, cecDevInfo);
        }
        Log.d(TAG, "getCecDevInfo_native called...\n");
        return 0;
    }

    protected static int getCecDevListInfo_native(List<MtkTvCecBase.CecDevInfo> cecDevList) {
        if (!is_emulator()) {
            return TVNative.getCecDevListInfo_native(cecDevList);
        }
        Log.d(TAG, "getCecDevListInfo_native called...\n");
        return 0;
    }

    protected static int getActiveSourceInfo_native(MtkTvCecActiveSourceBase cecActSrcInfo) {
        if (!is_emulator()) {
            return TVNative.getActiveSourceInfo_native(cecActSrcInfo);
        }
        Log.d(TAG, "getActiveSourceInfo_native called...\n");
        return 0;
    }

    protected static int setStandby_native(int logAddr) {
        if (!is_emulator()) {
            return TVNative.setStandby_native(logAddr);
        }
        Log.d(TAG, "setStandby_native called...\n");
        return 0;
    }

    protected static int setUserCtrlPressed_native(MtkTvCecBase.CecUserCtrlInfo userCtrlInfo) {
        if (!is_emulator()) {
            return TVNative.setUserCtrlPressed_native(userCtrlInfo);
        }
        Log.d(TAG, "setUserCtrlPressed_native called...\n");
        return 0;
    }

    protected static int setUserCtrlReleased_native(MtkTvCecBase.CecUserCtrlInfo userCtrlInfo) {
        if (!is_emulator()) {
            return TVNative.setUserCtrlReleased_native(userCtrlInfo);
        }
        Log.d(TAG, "setUserCtrlReleased_native called...\n");
        return 0;
    }

    protected static int setSystemAudioModeRequest_native(int sysAudioMode) {
        if (!is_emulator()) {
            return TVNative.setSystemAudioModeRequest_native(sysAudioMode);
        }
        Log.d(TAG, "setSystemAudioModeRequest_native called...\n");
        return 0;
    }

    protected static int setTimer_native(int logAddr, MtkTvCecTimeInfoBase timerInfo) {
        if (!is_emulator()) {
            return TVNative.setTimer_native(logAddr, timerInfo);
        }
        Log.d(TAG, "setTimer_native called...\n");
        return 0;
    }

    protected static int setRecordOn_native(int logAddr, MtkTvCecRecordSouceInfoBase recordSourceInfo) {
        if (!is_emulator()) {
            return TVNative.setRecordOn_native(logAddr, recordSourceInfo);
        }
        Log.d(TAG, "setRecordOn_native called...\n");
        return 0;
    }

    protected static int setRecordOff_native(int logAddr) {
        if (!is_emulator()) {
            return TVNative.setRecordOff_native(logAddr);
        }
        Log.d(TAG, "setRecordOff_native called...\n");
        return 0;
    }

    protected static int setVendorCmdWithId_native(MtkTvCecBase.CecVndrCmdWithIdInfo vndrCmdWithIdInfo) {
        if (!is_emulator()) {
            return TVNative.setVendorCmdWithId_native(vndrCmdWithIdInfo);
        }
        Log.d(TAG, "setVendorCmdWithId_native called...\n");
        return 0;
    }

    protected static int isDeviceExist_native(int logAddr) {
        if (!is_emulator()) {
            return TVNative.isDeviceExist_native(logAddr);
        }
        Log.d(TAG, "isDeviceExist_native called...\n");
        return 0;
    }

    protected static int notifyCecCompInfo_native(String notifyType, int notifyData, String notifyString) {
        if (!is_emulator()) {
            return TVNative.notifyCecCompInfo_native(notifyType, notifyData, notifyString);
        }
        Log.d(TAG, "notifyCecCompInfo_native called...\n");
        return 0;
    }

    protected static int discoveryDevice_native(MtkTvCecDevDiscoveryInfoBase cecDiscoveryInfo) {
        if (!is_emulator()) {
            return TVNative.discoveryDevice_native(cecDiscoveryInfo);
        }
        Log.d(TAG, "discoveryDevice_native called...\n");
        return 0;
    }

    protected static int powerOnDeviceByLogicAddr_native(int logAddr) {
        if (!is_emulator()) {
            return TVNative.powerOnDeviceByLogicAddr_native(logAddr);
        }
        Log.d(TAG, "powerOnDeviceByLogicAddr_native called...\n");
        return 0;
    }

    protected static int setStandbyToAll_native(boolean sync) {
        if (!is_emulator()) {
            return TVNative.setStandbyToAll_native(sync);
        }
        Log.d(TAG, "setStandbyToAll_native called...\n");
        return 0;
    }

    public static int getEventListByChannelId_native(int iChannelId, long startTime, long duration, int mMaxEventNumPerChannel, List<MtkTvEventInfoBase> eventList) {
        if (!is_emulator()) {
            return TVNative.getEventListByChannelId_native(iChannelId, startTime, duration, mMaxEventNumPerChannel, eventList);
        }
        Log.d(TAG, "getEventListByChannelId_native called...\n");
        return 0;
    }

    protected static void setEventCallback_native(boolean isCallbackOn) {
        if (is_emulator()) {
            Log.d(TAG, "setEventCallback_native called...\n");
        } else {
            TVNative.setEventCallback_native(isCallbackOn);
        }
    }

    protected static void getEventInfoByEventId_native(int iEventId, int iChannelId, MtkTvEventInfoBase eventInfo) {
        if (is_emulator()) {
            Log.d(TAG, "getEventInfoByEventId_native called...\n");
        } else {
            TVNative.getEventInfoByEventId_native(iEventId, iChannelId, eventInfo);
        }
    }

    protected static void setCurrentActiveWindowsInfo_native(List<MtkTvChannelInfoBase> channels, long startTime) {
        if (is_emulator()) {
            Log.d(TAG, "setCurrentActiveWindowsInfo_native called...\n");
        } else {
            TVNative.setCurrentActiveWindowsInfo_native(channels, startTime);
        }
    }

    protected static void setCurrentActiveWindowsInfoEx_native(List<MtkTvChannelInfoBase> channels, long startTime, long durationTime) {
        if (is_emulator()) {
            Log.d(TAG, "setCurrentActiveWindowsInfoEx_native called...\n");
        } else {
            TVNative.setCurrentActiveWindowsInfoEx_native(channels, startTime, durationTime);
        }
    }

    protected static int clearActiveWindow_native() {
        if (!is_emulator()) {
            return TVNative.clearActiveWindow_native();
        }
        Log.d(TAG, "clearActiveWindow_native called...\n");
        return 0;
    }

    protected static boolean checkEventBlock_native(int iChannelId, int iEventId) {
        if (!is_emulator()) {
            return TVNative.checkEventBlock_native(iChannelId, iEventId);
        }
        Log.d(TAG, "checkEventBlock called...\n");
        return false;
    }

    protected static int getEventRatingMapById_native(int iChannelId, int iEventId, MtkTvRatingConvert2Goo ratingMapped) {
        if (!is_emulator()) {
            return TVNative.getEventRatingMapById_native(iChannelId, iEventId, ratingMapped);
        }
        Log.d(TAG, "getEventRatingMapById_native called...\n");
        return 0;
    }

    protected static int[] getCurrentActiveWinChannelList_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentActiveWinChannelList_native();
        }
        Log.d(TAG, "getCurrentActiveWinChannelList called...\n");
        return null;
    }

    protected static long getCurrentActiveWinStartTime_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentActiveWinStartTime_native();
        }
        Log.d(TAG, "getCurrentActiveWinStartTime called...\n");
        return 0;
    }

    protected static long getCurrentActiveWinEndTime_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentActiveWinEndTime_native();
        }
        Log.d(TAG, "getCurrentActiveWinEndTime called...\n");
        return 0;
    }

    protected static int getPFEventInfoByChannel_native(int channelId, boolean isPEvent, MtkTvEventInfoBase event) {
        if (!is_emulator()) {
            return TVNative.getPFEventInfoByChannel_native(channelId, isPEvent, event);
        }
        Log.d(TAG, "getPFEventInfoByChannel called...\n");
        return 0;
    }

    protected static int getBookingCount_native() {
        if (!is_emulator()) {
            return TVNative.getBookingCount_native();
        }
        Log.d(TAG, "getBookingCount_native called...\n");
        return 0;
    }

    protected static int getBookingID(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingID_native(index);
        }
        Log.d(TAG, "getBookingID called...\n");
        return -1;
    }

    protected static int getBookingChannelId(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingChannelId_native(index);
        }
        Log.d(TAG, "getBookingChannelId called...\n");
        return -1;
    }

    protected static int setBookingChannelId(int index, int channelID) {
        if (!is_emulator()) {
            return TVNative.setBookingChannelId_native(index, channelID);
        }
        Log.d(TAG, "setBookingChannelId called...\n");
        return -1;
    }

    protected static int getBookingDeviceIndex(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingDeviceIndex_native(index);
        }
        Log.d(TAG, "getBookingDeviceIndex called...\n");
        return -1;
    }

    protected static int setBookingDeviceIndex(int index, int deviceID) {
        if (!is_emulator()) {
            return TVNative.setBookingDeviceIndex_native(index, deviceID);
        }
        Log.d(TAG, "setBookingDeviceIndex called...\n");
        return -1;
    }

    protected static String getBookingEventTitle(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingEventTitle_native(index);
        }
        Log.d(TAG, "getBookingEventTitle called...\n");
        return "";
    }

    protected static int setBookingEventTitle(int index, String title) {
        if (!is_emulator()) {
            return TVNative.setBookingEventTitle_native(index, title);
        }
        Log.d(TAG, "setBookingEventTitle called...\n");
        return 0;
    }

    protected static int getBookingGenre(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingGenre_native(index);
        }
        Log.d(TAG, "getBookingGenre called...\n");
        return -1;
    }

    protected static int setBookingGenre(int index, int genre) {
        if (!is_emulator()) {
            return TVNative.setBookingGenre_native(index, genre);
        }
        Log.d(TAG, "setBookingGenre called...\n");
        return -1;
    }

    protected static int getBookingInfoData(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingInfoData_native(index);
        }
        Log.d(TAG, "getBookingInfoData called...\n");
        return -1;
    }

    protected static int setBookingInfoData(int index, int infodata) {
        if (!is_emulator()) {
            return TVNative.setBookingInfoData_native(index, infodata);
        }
        Log.d(TAG, "setBookingInfoData called...\n");
        return -1;
    }

    protected static int getBookingRecordDelay(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingRecordDelay_native(index);
        }
        Log.d(TAG, "getBookingRecordDelay called...\n");
        return -1;
    }

    protected static int setBookingRecordDelay(int index, int recordDelay) {
        if (!is_emulator()) {
            return TVNative.setBookingRecordDelay_native(index, recordDelay);
        }
        Log.d(TAG, "setBookingRecordDelay called...\n");
        return -1;
    }

    protected static long getBookingRecordDuration(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingRecordDuration_native(index);
        }
        Log.d(TAG, "getBookingRecordDuration called...\n");
        return -1;
    }

    protected static int setBookingRecordDuration(int index, long duration) {
        if (!is_emulator()) {
            return TVNative.setBookingRecordDuration_native(index, duration);
        }
        Log.d(TAG, "setBookingRecordDuration called...\n");
        return -1;
    }

    protected static int getBookingRecordMode(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingRecordMode_native(index);
        }
        Log.d(TAG, "getBookingRecordMode called...\n");
        return -1;
    }

    protected static int setBookingRecordMode(int index, int mode) {
        if (!is_emulator()) {
            return TVNative.setBookingRecordMode_native(index, mode);
        }
        Log.d(TAG, "setBookingRecordMode called...\n");
        return -1;
    }

    protected static int getBookingRepeatMode(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingRepeatMode_native(index);
        }
        Log.d(TAG, "getBookingRepeatMode called...\n");
        return -1;
    }

    protected static int setBookingRepeatMode(int index, int mode) {
        if (!is_emulator()) {
            return TVNative.setBookingRepeatMode_native(index, mode);
        }
        Log.d(TAG, "setBookingRepeatMode called...\n");
        return -1;
    }

    protected static int getBookingSourceType(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingSourceType_native(index);
        }
        Log.d(TAG, "getBookingSourceType called...\n");
        return -1;
    }

    protected static int setBookingSourceType(int index, int mode) {
        if (!is_emulator()) {
            return TVNative.setBookingSourceType_native(index, mode);
        }
        Log.d(TAG, "setBookingSourceType called...\n");
        return -1;
    }

    protected static int getBookingTunerType(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingTunerType_native(index);
        }
        Log.d(TAG, "getBookingTunerType called...\n");
        return -1;
    }

    protected static int setBookingTunerType(int index, int type) {
        if (!is_emulator()) {
            return TVNative.setBookingTunerType_native(index, type);
        }
        Log.d(TAG, "setBookingTunerType called...\n");
        return -1;
    }

    protected static long getBookingStartTime(int index) {
        if (!is_emulator()) {
            return TVNative.getBookingStartTime_native(index);
        }
        Log.d(TAG, "getBookingDeviceIndex called...\n");
        return -1;
    }

    protected static int setBookingStartTime(int index, long starttime) {
        if (!is_emulator()) {
            return TVNative.setBookingStartTime_native(index, starttime);
        }
        Log.d(TAG, "setBookingStartTime called...\n");
        return -1;
    }

    protected static boolean addBooking_native(int channelId, int eventId) {
        if (!is_emulator()) {
            return TVNative.addBooking_native(channelId, eventId);
        }
        Log.d(TAG, "addBooking_native called...\n");
        return true;
    }

    protected static int delBooking_native(int index) {
        if (!is_emulator()) {
            return TVNative.delBooking_native(index);
        }
        Log.d(TAG, "delBooking_native called...\n");
        return -1;
    }

    protected static boolean addBooking_native(int channelId, long startTime, int duration, String firstEventTitle) {
        if (!is_emulator()) {
            return TVNative.addBooking_native(channelId, startTime, duration, firstEventTitle);
        }
        Log.d(TAG, "addBooking_native called...\n");
        return true;
    }

    protected static void updateBooking_native() {
        if (is_emulator()) {
            Log.d(TAG, "updateBooking_native called...\n");
        } else {
            TVNative.updateBooking_native();
        }
    }

    protected static String getRecordingFile_native() {
        if (!is_emulator()) {
            return TVNative.getRecordingFile_native();
        }
        Log.d(TAG, "getRecordingFile_native called...\n");
        return "";
    }

    protected static String getRecordingFileByHandle_native(int handle) {
        if (!is_emulator()) {
            return TVNative.getRecordingFileByHandle_native(handle);
        }
        Log.d(TAG, "getRecordingFile_native called...\n");
        return "";
    }

    protected static boolean setRecordRegisterFile_native(String fileName) {
        if (!is_emulator()) {
            return TVNative.setRecordRegisterFile_native(fileName);
        }
        Log.d(TAG, "setRecordRegisterFile_native called...\n");
        return true;
    }

    protected static boolean checkRecordRegisterFile_native(String fileName) {
        if (!is_emulator()) {
            return TVNative.checkRecordRegisterFile_native(fileName);
        }
        Log.d(TAG, "checkRecordRegisterFile_native called...\n");
        return true;
    }

    protected static boolean SetRecordDisk(String mountPoint) {
        if (!is_emulator()) {
            return TVNative.SetRecordDisk_native(mountPoint);
        }
        Log.d(TAG, "SetRecordDisk called...\n");
        return true;
    }

    protected static String getRecordDisk() {
        if (!is_emulator()) {
            return TVNative.GetRecordDisk_native();
        }
        Log.d(TAG, "getPvrBrowserItemTime_native called...\n");
        return "";
    }

    protected static boolean SetRecordDiskByHandle(int handle, String mountPoint) {
        if (!is_emulator()) {
            return TVNative.SetRecordDiskByHandle_native(handle, mountPoint);
        }
        Log.d(TAG, "SetRecordDisk called...\n");
        return true;
    }

    protected static boolean recordStartSpeedTest(String mountPoint, int duration) {
        if (!is_emulator()) {
            return TVNative.recordStartSpeedTest_native(mountPoint, duration);
        }
        Log.d(TAG, "recordStartSpeedTest called...\n");
        return true;
    }

    protected static long getStorageFreeSize_native() {
        if (!is_emulator()) {
            return TVNative.getStorageFreeSize_native();
        }
        Log.d(TAG, "getStorageFreeSize called...\n");
        return 0;
    }

    protected static int getRecordingPosition() {
        if (!is_emulator()) {
            return TVNative.getRecordingPosition();
        }
        Log.d(TAG, "getRecordingPosition called...\n");
        return 0;
    }

    protected static int getRecordingPositionByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.getRecordingPositionByHandle(handle);
        }
        Log.d(TAG, "getRecordingPosition called...\n");
        return 0;
    }

    protected static long getStorageSize_native() {
        if (!is_emulator()) {
            return TVNative.getStorageSize_native();
        }
        Log.d(TAG, "getStorageSize called...\n");
        return 0;
    }

    protected static int recordOpenPVR(int type) {
        if (!is_emulator()) {
            return TVNative.recordOpenPVR(type);
        }
        Log.d(TAG, "recordStartPVR called...\n");
        return 0;
    }

    protected static int recordStartPVR(int type) {
        if (!is_emulator()) {
            return TVNative.recordStartPVR(type);
        }
        Log.d(TAG, "recordStartPVR called...\n");
        return 0;
    }

    protected static int recordStartPVRByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.recordStartPVRByHandle(handle);
        }
        Log.d(TAG, "recordStartPVR called...\n");
        return 0;
    }

    protected static int recordStopPVR() {
        if (!is_emulator()) {
            return TVNative.recordStopPVR();
        }
        Log.d(TAG, "recordStopPVR called...\n");
        return 0;
    }

    protected static int recordGetPVRStatus() {
        if (!is_emulator()) {
            return TVNative.recordGetPVRStatus();
        }
        Log.d(TAG, "recordGetPVRStatus called...\n");
        return 0;
    }

    protected static int recordGetPVRStatusByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.recordGetPVRStatusByHandle(handle);
        }
        Log.d(TAG, "recordGetPVRStatus called...\n");
        return 0;
    }

    protected static int recordGetPVRSrcType() {
        if (!is_emulator()) {
            return TVNative.recordGetPVRSrcType();
        }
        Log.d(TAG, "recordGetPVRSrcType called...\n");
        return 0;
    }

    protected static int recordGetPVRSrcTypeByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.recordGetPVRSrcTypeByHandle(handle);
        }
        Log.d(TAG, "recordGetPVRSrcType called...\n");
        return 0;
    }

    protected static int recordClosePVR(int handle) {
        if (!is_emulator()) {
            return TVNative.recordClosePVR(handle);
        }
        Log.d(TAG, "recordClosePVR called...\n");
        return 0;
    }

    protected static int recordStopPVRByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.recordStopPVRByHandle(handle);
        }
        Log.d(TAG, "recordStopPVR called...\n");
        return 0;
    }

    protected static int recordgetErrorID() {
        if (!is_emulator()) {
            return TVNative.recordgetErrorID();
        }
        Log.d(TAG, "recordgetErrorID called...\n");
        return 0;
    }

    protected static long recordGetRecordingFilesize() {
        if (!is_emulator()) {
            return TVNative.recordGetRecordingFilesize();
        }
        Log.d(TAG, "recordGetRecordingFilesize called...\n");
        return 0;
    }

    protected static int recordOpenPVREx(int type, boolean bind) {
        if (!is_emulator()) {
            return TVNative.recordOpenPVREx(type, bind);
        }
        Log.d(TAG, "recordStartPVREx called...\n");
        return 0;
    }

    protected static int recordPVRSelectSvc(int handle, int channelId, int svlId) {
        if (!is_emulator()) {
            return TVNative.recordPVRSelectSvc(handle, channelId, svlId);
        }
        Log.d(TAG, "recordPVRSelectSvc called...\n");
        return 0;
    }

    protected static MtkTvTimeshiftBase.TimeshiftRecordStatus getRecordStatus_native() {
        if (!is_emulator()) {
            return TVNative.getRecordStatus_native();
        }
        Log.d(TAG, "getRecordStatus_native called...\n");
        return MtkTvTimeshiftBase.TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;
    }

    protected static int stopTimeshift_native(MtkTvTimeshiftBase.TimeshiftStopFlag flag) {
        if (!is_emulator()) {
            return TVNative.stopTimeshift_native(flag);
        }
        Log.d(TAG, "getRecordStatus_native called...\n");
        return 0;
    }

    protected static int setAutoRecord_native(boolean flag) {
        if (!is_emulator()) {
            return TVNative.setAutoRecord_native(flag);
        }
        Log.d(TAG, "setAutoRecord_native called...\n");
        return 0;
    }

    protected static int setPlaybackSpeed_native(MtkTvTimeshiftBase.TimeshiftPlaybackSpeed speed) {
        if (!is_emulator()) {
            return TVNative.setPlaybackSpeed_native(speed);
        }
        Log.d(TAG, "setPlaybackSpeed_native called...\n");
        return 0;
    }

    protected static int setPlaybackPause_native() {
        if (!is_emulator()) {
            return TVNative.setPlaybackPause_native();
        }
        Log.d(TAG, "setPlaybackPause_native called...\n");
        return 0;
    }

    protected static int setPlaybackResume_native() {
        if (!is_emulator()) {
            return TVNative.setPlaybackResume_native();
        }
        Log.d(TAG, "setPlaybackResume_native called...\n");
        return 0;
    }

    protected static int seekTo_native(long timeMs) {
        if (!is_emulator()) {
            return TVNative.seekTo_native(timeMs);
        }
        Log.d(TAG, "seekTo_native called...\n");
        return 0;
    }

    protected static long getStartPosition_native() {
        if (!is_emulator()) {
            return TVNative.getStartPosition_native();
        }
        Log.d(TAG, "getStartPosition_native called...\n");
        return 0;
    }

    protected static long getCurrentPosition_native() {
        if (!is_emulator()) {
            return TVNative.getCurrentPosition_native();
        }
        Log.d(TAG, "getCurrentPosition_native called...\n");
        return 0;
    }

    protected static int getTimeshiftRegisterDeviceSchedule_native() {
        if (!is_emulator()) {
            return TVNative.getTimeshiftRegisterDeviceSchedule_native();
        }
        Log.d(TAG, "getRegisterDeviceSchedule_native called...\n");
        return 0;
    }

    protected static long getTimeshiftDuration_native() {
        if (!is_emulator()) {
            return TVNative.getTimeshiftDuration_native();
        }
        Log.d(TAG, "getTimeshiftDuration_native called...\n");
        return 0;
    }

    protected static long getTimeshiftPosition_native() {
        if (!is_emulator()) {
            return TVNative.getTimeshiftPosition_native();
        }
        Log.d(TAG, "getTimeshiftPosition_native called...\n");
        return 0;
    }

    protected static int registerTimeshiftDevice_native(String path, long size) {
        if (!is_emulator()) {
            return TVNative.registerTimeshiftDevice_native(path, size);
        }
        Log.d(TAG, "registerTimeshiftDevice called...\n");
        return 0;
    }

    protected static int startTimeshift_native() {
        if (!is_emulator()) {
            return TVNative.startTimeshift_native();
        }
        Log.d(TAG, "startTimeshift_native called...\n");
        return 0;
    }

    protected static int stopTimeshift_native() {
        if (!is_emulator()) {
            return TVNative.stopTimeshift_native();
        }
        Log.d(TAG, "stopTimeshift_native called...\n");
        return 0;
    }

    protected static int getTimeshiftDeviceStatus_native() {
        if (!is_emulator()) {
            return TVNative.getTimeshiftDeviceStatus_native();
        }
        Log.d(TAG, "getTimeshiftDeviceStatus_native called...\n");
        return 0;
    }

    protected static int getTimeshiftErrorID_native() {
        if (!is_emulator()) {
            return TVNative.getTimeshiftErrorID_native();
        }
        Log.d(TAG, "getTimeshiftErrorID_native called...\n");
        return 0;
    }

    protected static boolean playTimeshift_native() {
        if (!is_emulator()) {
            return TVNative.playTimeshift_native();
        }
        Log.d(TAG, "playTimeshift_native called...\n");
        return true;
    }

    protected static boolean pauseTimeshift_native() {
        if (!is_emulator()) {
            return TVNative.pauseTimeshift_native();
        }
        Log.d(TAG, "pauseTimeshift_native called...\n");
        return true;
    }

    protected static int seekTimeshift_native(long time) {
        if (!is_emulator()) {
            return TVNative.seekTimeshift_native(time);
        }
        Log.d(TAG, "seekTimeshift_native called...\n");
        return 0;
    }

    protected static int trickPlayTimeshift_native(double speed) {
        if (!is_emulator()) {
            return TVNative.trickPlayTimeshift_native(speed);
        }
        Log.d(TAG, "trickPlayTimeshift_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemChannelId_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemChannelId_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemChannelId_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemChannelId_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemChannelId_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemChannelId_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemChannelName_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemChannelName_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemChannelName_native called...\n");
        return "";
    }

    protected static long getPvrBrowserItemDuration_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemDuration_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemDuration_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemProgramName_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemProgramName_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemProgramName_native called...\n");
        return "";
    }

    protected static long getPvrBrowserItemStartTime_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemStartTime_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemStartTime_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemStartTime_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemStartTime_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemStartTime_native called...\n");
        return 0;
    }

    protected static int getPvrBrowserItemCount_native() {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemCount_native();
        }
        Log.d(TAG, "getPvrBrowserCount_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemPath_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemPath_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemPath_native called...\n");
        return "";
    }

    protected static int getPvrBrowserItemMajorChannelNum_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemMajorChannelNum_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemMajorChannelNum_native called...\n");
        return 0;
    }

    protected static int getPvrBrowserItemMinorChannelNum_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemMinorChannelNum_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemMinorChannelNum_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemChannelName_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemChannelName_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemChannelName_native called...\n");
        return "";
    }

    protected static long getPvrBrowserItemDuration_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemDuration_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemDuration_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemProgramName_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemProgramName_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemProgramName_native called...\n");
        return "";
    }

    protected static String getPvrBrowserItemDate_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemDate_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemDate_native called...\n");
        return "";
    }

    protected static String getPvrBrowserItemWeek_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemWeek_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemWeek_native called...\n");
        return "";
    }

    protected static String getPvrBrowserItemTime_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemTime_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemTime_native called...\n");
        return "";
    }

    protected static int getPvrBrowserRecordingFileCount_native() {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserRecordingFileCount_native();
        }
        Log.d(TAG, "getPvrBrowserRecordingFileCount_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserRecordingFileName_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserRecordingFileName_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemTime_native called...\n");
        return "";
    }

    protected static int deletePvrBrowserFileByIndex_native(int index) {
        if (!is_emulator()) {
            return TVNative.deletePvrBrowserFileByIndex_native(index);
        }
        Log.d(TAG, "deletePvrBrowserFileByIndex_native called...\n");
        return 0;
    }

    protected static int getPvrBrowserItemMajorChannelNum_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemMajorChannelNum_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemMajorChannelNum_native called...\n");
        return 0;
    }

    protected static int getPvrBrowserItemMinorChannelNum_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemMinorChannelNum_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemMinorChannelNum_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemFirstRatingRange_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemFirstRatingRange_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemFirstRatingRange_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemFirstRatingRange_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemFirstRatingRange_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemFirstRatingRange_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemRentention_native(int index) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemRentention_native(index);
        }
        Log.d(TAG, "getPvrBrowserItemRentention_native called...\n");
        return 0;
    }

    protected static long getPvrBrowserItemRentention_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemRentention_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemRentention_native called...\n");
        return 0;
    }

    protected static String getPvrBrowserItemDate_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemDate_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemDate_native called...\n");
        return "";
    }

    protected static String getPvrBrowserItemWeek_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemWeek_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemWeek_native called...\n");
        return "";
    }

    protected static String getPvrBrowserItemTime_native(String path) {
        if (!is_emulator()) {
            return TVNative.getPvrBrowserItemTime_native(path);
        }
        Log.d(TAG, "getPvrBrowserItemTime_native called...\n");
        return "";
    }

    protected static long getUtcTime_native() {
        if (!is_emulator()) {
            return TVNative.getUtcTime_native();
        }
        Log.d(TAG, "getUtcTime_native called...\n");
        return 0;
    }

    protected static long getBroadcastTime_native(MtkTvTimeFormatBase time) {
        if (!is_emulator()) {
            return TVNative.getBroadcastTime_native(time);
        }
        Log.d(TAG, "getBroadcastTime_native called...\n");
        return 0;
    }

    protected static boolean isReceivedTOT_native() {
        if (!is_emulator()) {
            return TVNative.isReceivedTOT_native();
        }
        Log.d(TAG, "isReceivedTOT_native called...\n");
        return false;
    }

    protected static int convertUTCSecToDTG_native(long seconds, MtkTvTimeFormatBase time) {
        if (!is_emulator()) {
            return TVNative.convertUTCSecToDTG_native(seconds, time);
        }
        Log.d(TAG, "convertUTCSecToDTG_native called...\n");
        return 0;
    }

    protected static void setUtcTime_native(long when) {
        if (is_emulator()) {
            Log.d(TAG, "setUtcTime_native called...\n");
        } else {
            TVNative.setUtcTime_native(when);
        }
    }

    protected static boolean isTimeEditable_native() {
        if (!is_emulator()) {
            return TVNative.isTimeEditable_native();
        }
        Log.d(TAG, "isTimeEditable_native called...\n");
        return true;
    }

    protected static int convertTime_native(int type, MtkTvTimeFormatBase from, MtkTvTimeFormatBase to) {
        if (!is_emulator()) {
            return TVNative.convertTime_native(type, from, to);
        }
        Log.d(TAG, "convertTime_native called...\n");
        return 0;
    }

    protected static MtkTvTimeRawDataBase getBrdcstRawData_native() {
        if (!is_emulator()) {
            return TVNative.getBrdcstRawData_native();
        }
        Log.d(TAG, "getBrdcstRawData_native called...\n");
        return null;
    }

    protected static long getTimeZone_native() {
        if (!is_emulator()) {
            return TVNative.getTimeZone_native();
        }
        Log.d(TAG, "getTimeZone_native called...\n");
        return 0;
    }

    protected static void setTimeZone_native(long timeZone) {
        if (is_emulator()) {
            Log.d(TAG, "setTimeZone_native called...\n");
        } else {
            TVNative.setTimeZone_native(timeZone);
        }
    }

    protected static boolean isTimeZoneEditable_native() {
        if (!is_emulator()) {
            return TVNative.isTimeZoneEditable_native();
        }
        Log.d(TAG, "isTimeZoneEditable_native called...\n");
        return true;
    }

    protected static long getTimeOffset_native() {
        if (!is_emulator()) {
            return TVNative.getTimeOffset_native();
        }
        Log.d(TAG, "getTimeOffset_native called...\n");
        return 0;
    }

    protected static int slideForOnePartChannelId_native(int svlId, int chNumBegin, int chNumEnd) {
        if (!is_emulator()) {
            return TVNative.slideForOnePartChannelId_native(svlId, chNumBegin, chNumEnd);
        }
        Log.d(TAG, "getChannelListByMask_native called...\n");
        return 0;
    }

    protected static void setTimeOffset_native(long timeOffset) {
        if (is_emulator()) {
            Log.d(TAG, "setTimeOffset_native called...\n");
        } else {
            TVNative.setTimeOffset_native(timeOffset);
        }
    }

    protected static boolean isTimeOffsetEditable_native() {
        if (!is_emulator()) {
            return TVNative.isTimeOffsetEditable_native();
        }
        Log.d(TAG, "isTimeOffsetEditable_native called...\n");
        return true;
    }

    protected static int getSleepTimer_native(boolean direction) {
        if (!is_emulator()) {
            return TVNative.getSleepTimer_native(direction);
        }
        Log.d(TAG, "getSleepTimer_native called...\n");
        return 0;
    }

    protected static void setSleepTimer_native(int timer) {
        if (is_emulator()) {
            Log.d(TAG, "setSleepTimer_native called...\n");
        } else {
            TVNative.setSleepTimer_native(timer);
        }
    }

    protected static void convertMillisToLocalTime_native(long millis, MtkTvTimeFormatBase time) {
        if (is_emulator()) {
            Log.d(TAG, "convertMillisToLocalTime_native called...\n");
        } else {
            TVNative.convertMillisToLocalTime_native(millis, time);
        }
    }

    protected static long convertLocalTimeToMillis_native(MtkTvTimeFormatBase time) {
        if (!is_emulator()) {
            return TVNative.convertLocalTimeToMillis_native(time);
        }
        Log.d(TAG, "convertLocalTimeToMillis_native called...\n");
        return 0;
    }

    protected static long convertDTGToSeconds_native(MtkTvTimeFormatBase time) {
        if (!is_emulator()) {
            return TVNative.convertDTGToSeconds_native(time);
        }
        Log.d(TAG, "convertDTGToSeconds_native called...\n");
        return 0;
    }

    protected static int getSleepTimerRemainingTime_native() {
        if (!is_emulator()) {
            return TVNative.getSleepTimerRemainingTime_native();
        }
        Log.d(TAG, "getSleepTimerRemainingTime_native called...\n");
        return 0;
    }

    protected static int getTimeSyncSource_native() {
        if (!is_emulator()) {
            return TVNative.getTimeSyncSource_native();
        }
        Log.d(TAG, "getTimeSyncSource_native called...\n");
        return 0;
    }

    protected static void setTimeSyncSource_native(int syncSource) {
        if (is_emulator()) {
            Log.d(TAG, "setTimeSyncSource_native called...\n");
        } else {
            TVNative.setTimeSyncSource_native(syncSource);
        }
    }

    protected static void setLocalTimeZone_native(String timezone) {
        if (is_emulator()) {
            Log.d(TAG, "setLocalTimeZone_native called...\n");
        } else {
            TVNative.setLocalTimeZone_native(timezone);
        }
    }

    protected static void softKeyboardInit_native() {
        if (is_emulator()) {
            Log.d(TAG, "softKeyboardInit_native called...\n");
        } else {
            TVNative.softKeyboardInit_native();
        }
    }

    protected static void notifySoftKeyboardStatusChange_native(int status) {
        if (is_emulator()) {
            Log.d(TAG, "notifySoftKeyboardStatusChange_native called...\n");
        } else {
            TVNative.notifySoftKeyboardStatusChange_native(status);
        }
    }

    protected static void notifyTextChange_native(String text) {
        if (is_emulator()) {
            Log.d(TAG, "notifyTextChange_native called...\n");
        } else {
            TVNative.notifyTextChange_native(text);
        }
    }

    protected static boolean isScaning_native() {
        if (!is_emulator()) {
            return TVNative.isScaning_native();
        }
        Log.d(TAG, "isScaning_native called...\n");
        return false;
    }

    protected static int startScan_native(int type, int mode, boolean isDefaultSetting) {
        if (!is_emulator()) {
            return TVNative.startScan_native(type, mode, isDefaultSetting);
        }
        Log.d(TAG, "startScan_native called...\n");
        return 0;
    }

    protected static int cancelScan_native() {
        if (!is_emulator()) {
            return TVNative.cancelScan_native();
        }
        Log.d(TAG, "cancelScan_native called...\n");
        return 0;
    }

    protected static int setDvbScanParas(MtkTvDvbScanParaBase mDvbScanPara) {
        if (!is_emulator()) {
            return TVNative.setDvbScanParas(mDvbScanPara);
        }
        Log.d(TAG, "setDvbScanParas called...\n");
        return 0;
    }

    protected static int setDvbcManualScanParas(MtkTvDvbcManualScanParaBase mDvbcScanPara) {
        if (!is_emulator()) {
            return TVNative.setDvbcManualScanParas(mDvbcScanPara);
        }
        Log.d(TAG, "setDvbcManualScanParas called...\n");
        return 0;
    }

    protected static int setDvbcScanParas(MtkTvDvbcScanParaBase mDvbcScanPara) {
        if (!is_emulator()) {
            return TVNative.setDvbcScanParas(mDvbcScanPara);
        }
        Log.d(TAG, "setDvbcScanParas called...\n");
        return 0;
    }

    protected static int setATSCScanParas(MtkTvATSCScanParaBase mATSCScanPara) {
        if (!is_emulator()) {
            return TVNative.setATSCScanParas(mATSCScanPara);
        }
        Log.d(TAG, "setATSCScanParas called...\n");
        return 0;
    }

    protected static int setNTSCScanParas(MtkTvNTSCScanParaBase mNTSCScanPara) {
        if (!is_emulator()) {
            return TVNative.setNTSCScanParas(mNTSCScanPara);
        }
        Log.d(TAG, "setNTSCScanParas called...\n");
        return 0;
    }

    protected static int setISDBScanParas(MtkTvISDBScanParaBase mISDBScanPara) {
        if (!is_emulator()) {
            return TVNative.setISDBScanParas(mISDBScanPara);
        }
        Log.d(TAG, "setISDBScanParas called...\n");
        return 0;
    }

    protected static int getMarketRegion() {
        if (!is_emulator()) {
            return TVNative.getMarketRegion();
        }
        Log.d(TAG, "getMarketRegion called...\n");
        return 0;
    }

    protected static int setSatelliteSetting(MtkTvDvbsSatelliteSettingBase mPara) {
        if (!is_emulator()) {
            return TVNative.setSatelliteSetting(mPara);
        }
        Log.d(TAG, "setSatelliteSetting called...\n");
        return 0;
    }

    protected static int getUIMode() {
        if (!is_emulator()) {
            return TVNative.getUIMode();
        }
        Log.d(TAG, "getUIMode called...\n");
        return 0;
    }

    protected static int ScanPalSecamExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanPalSecamExchangeData_native(data);
        }
        Log.d(TAG, "ScanPalSecamExchangeData called...\n");
        return 0;
    }

    protected static int ScanDvbcExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanDvbcExchangeData_native(data);
        }
        Log.d(TAG, "ScanDvbcExchangeData called...\n");
        return 0;
    }

    protected static String ScanDvbcGetStrData_native(int type) {
        if (!is_emulator()) {
            return TVNative.ScanDvbcGetStrData_native(type);
        }
        Log.d(TAG, "ScanDvbcGetStrData called...\n");
        return null;
    }

    protected static int ScanATSCExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanATSCExchangeData_native(data);
        }
        Log.d(TAG, "ScanATSCExchangeData called...\n");
        return 0;
    }

    protected static int ScanISDBExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanISDBExchangeData_native(data);
        }
        Log.d(TAG, "ScanISDBExchangeData called...\n");
        return 0;
    }

    protected static int ScanCeExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanCeExchangeData_native(data);
        }
        Log.d(TAG, "ScanCeExchangeData called... \n");
        return 0;
    }

    protected static int ScanDtmbExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanDtmbExchangeData_native(data);
        }
        Log.d(TAG, "ScanDtmbExchangeData called... \n");
        return 0;
    }

    protected static int ScanDvbtExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanDvbtExchangeData_native(data);
        }
        Log.d(TAG, "ScanDvbtExchangeData called...\n");
        return 0;
    }

    protected static int ScanDvbsExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.ScanDvbsExchangeData_native(data);
        }
        Log.d(TAG, "ScanDvbsExchangeData called...\n");
        return 0;
    }

    protected static String ScanDvbsgetUserMessage_native(String stridx) {
        if (!is_emulator()) {
            return TVNative.ScanDvbsgetUserMessage_native(stridx);
        }
        Log.d(TAG, "ScanDvbsgetUserMessage_native called");
        return null;
    }

    protected static int startManualDetect_native() {
        if (!is_emulator()) {
            return TVNative.startManualDetect_native();
        }
        Log.d(TAG, "startManualDetect_native called...");
        return 0;
    }

    protected static int getScheduleInfo_native(MtkTvOADBase oadbase) {
        if (!is_emulator()) {
            return TVNative.getScheduleInfo_native(oadbase);
        }
        Log.d(TAG, "getScheduleInfo_native called...");
        return 0;
    }

    protected static int getDataInfo_native(MtkTvOADBase oadbase, int dataType) {
        if (!is_emulator()) {
            return TVNative.getDataInfo_native(oadbase, dataType);
        }
        Log.d(TAG, "getDataInfo_native called...");
        return 0;
    }

    protected static int stopManualDetect_native() {
        if (!is_emulator()) {
            return TVNative.stopManualDetect_native();
        }
        Log.d(TAG, "stopManualDetect_native called...");
        return 0;
    }

    protected static int startDownload_native() {
        if (!is_emulator()) {
            return TVNative.startDownload_native();
        }
        Log.d(TAG, "startDownload_native called...");
        return 0;
    }

    protected static int stopDownload_native() {
        if (!is_emulator()) {
            return TVNative.stopDownload_native();
        }
        Log.d(TAG, "stopDownload_native called...");
        return 0;
    }

    protected static int setAutoDownload_native(boolean bAutoDownload) {
        if (!is_emulator()) {
            return TVNative.setAutoDownload_native(bAutoDownload);
        }
        Log.d(TAG, "setAutoDownload_native called...");
        return 0;
    }

    protected static int startFlash_native() {
        if (!is_emulator()) {
            return TVNative.startFlash_native();
        }
        Log.d(TAG, "startFlash_native called...");
        return 0;
    }

    protected static int acceptRestart_native() {
        if (!is_emulator()) {
            return TVNative.acceptRestart_native();
        }
        Log.d(TAG, "acceptRestart_native called...");
        return 0;
    }

    protected static int remindMeLater_native() {
        if (!is_emulator()) {
            return TVNative.remindMeLater_native();
        }
        Log.d(TAG, "remindMeLater_native called...");
        return 0;
    }

    protected static int acceptSchedule_native() {
        if (!is_emulator()) {
            return TVNative.acceptSchedule_native();
        }
        Log.d(TAG, "acceptSchedule_native called...");
        return 0;
    }

    protected static int startJumpChannel_native() {
        if (!is_emulator()) {
            return TVNative.startJumpChannel_native();
        }
        Log.d(TAG, "startJumpChannel_native called...");
        return 0;
    }

    protected static int getPowerOnStatus_native() {
        if (!is_emulator()) {
            return TVNative.getPowerOnStatus_native();
        }
        Log.d(TAG, "getPowerOnStatus_native called...");
        return 0;
    }

    protected static int setPkgPathname_native(String pathname) {
        if (!is_emulator()) {
            return TVNative.setPkgPathname_native(pathname);
        }
        Log.d(TAG, "setPkgPathname_native called...");
        return 0;
    }

    protected static int clearOadVersion_native() {
        if (!is_emulator()) {
            return TVNative.clearOadVersion_native();
        }
        Log.d(TAG, "clearOadVersion_native called...");
        return 0;
    }

    protected static void getPfgInfo_native(MtkTvMHEG5Base mheg5) {
        if (is_emulator()) {
            Log.d(TAG, "getPfgString called...\n");
        } else {
            TVNative.getPfgInfo_native(mheg5);
        }
    }

    protected static void setPfgResult_native(boolean bRight) {
        if (is_emulator()) {
            Log.d(TAG, "setPfgResult called...\n");
        } else {
            TVNative.setPfgResult_native(bRight);
        }
    }

    protected static void setHbbtvStatus_native(boolean bSuccess) {
        if (is_emulator()) {
            Log.d(TAG, "setHbbtvStatus_native called...\n");
        } else {
            TVNative.setHbbtvStatus_native(bSuccess);
        }
    }

    protected static void setMheg5Enable_native() {
        if (is_emulator()) {
            Log.d(TAG, "setMheg5Enable_native called...\n");
        } else {
            TVNative.setMheg5Enable_native();
        }
    }

    protected static void setMheg5Disable_native() {
        if (is_emulator()) {
            Log.d(TAG, "setMheg5Disable_native called...\n");
        } else {
            TVNative.setMheg5Disable_native();
        }
    }

    protected static void getInternalScrnMode_native(MtkTvMHEG5Base mheg5) {
        if (is_emulator()) {
            Log.d(TAG, "getInternalScrnMode called...\n");
        } else {
            TVNative.getInternalScrnMode_native(mheg5);
        }
    }

    protected static void setMheg5Status_native(int updateType, int argv1, int argv2, int argv3) {
        if (is_emulator()) {
            Log.d(TAG, "setMheg5Status called...\n");
        } else {
            TVNative.setMheg5Status_native(updateType, argv1, argv2, argv3);
        }
    }

    protected static void createMonitorInst_native(byte monitorType) {
        if (is_emulator()) {
            Log.d(TAG, "createMonitorInst_native called...\n");
        } else {
            TVNative.createMonitorInst_native(monitorType);
        }
    }

    protected static void deleteMonitorInst_native(byte monitorType) {
        if (is_emulator()) {
            Log.d(TAG, "deleteMonitorInst_native called...\n");
        } else {
            TVNative.deleteMonitorInst_native(monitorType);
        }
    }

    protected static void getInfo_native(byte monitorType, int getType, int[] data) {
        if (is_emulator()) {
            Log.d(TAG, "getInfo_native called...\n");
        } else {
            TVNative.getInfo_native(monitorType, getType, data);
        }
    }

    protected static void setInfo_native(byte monitorType, int setType, int[] data) {
        if (is_emulator()) {
            Log.d(TAG, "setInfo_native called...\n");
        } else {
            TVNative.setInfo_native(monitorType, setType, data);
        }
    }

    protected static int enter_native() {
        if (!is_emulator()) {
            return TVNative.enter_native();
        }
        Log.d(TAG, "enter called...\n");
        return 0;
    }

    protected static boolean isDisplayBanner_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayBanner_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayBanner called...\n");
        return true;
    }

    protected static boolean isDisplayFrmCH_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayFrmCH_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayFrmCH called...\n");
        return true;
    }

    protected static boolean isDisplayFrmInfo_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayFrmInfo_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayFrmInfo called...\n");
        return true;
    }

    protected static boolean isDisplayFrmDetail_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayFrmDetail_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayFrmDetail called...\n");
        return true;
    }

    protected static boolean isDisplayIptsLockIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayIptsLockIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayIptsLockIcon called...\n");
        return true;
    }

    protected static boolean isDisplayTVLockIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayTVLockIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayTVLockIcon called...\n");
        return true;
    }

    protected static boolean isDisplayFavIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayFavIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayFavIcon called...\n");
        return true;
    }

    protected static boolean isDisplayCaptionIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayCaptionIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayCaptionIcon called...\n");
        return true;
    }

    protected static boolean isDisplayTtxIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayTtxIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayTtxIcon called...\n");
        return true;
    }

    protected static boolean isDisplayProgDetailUpIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayProgDetailUpIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayProgDetailUpIcon called...\n");
        return true;
    }

    protected static boolean isDisplayProgDetailDownIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayProgDetailDownIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayProgDetailDownIcon called...\n");
        return true;
    }

    protected static boolean isDisplayLogoIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayLogoIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayLogoIcon called...\n");
        return true;
    }

    protected static boolean isDisplayGingaIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayGingaIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayGingaIcon called...\n");
        return true;
    }

    protected static boolean isDisplayADEyeIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayADEyeIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayADEyeIcon called...\n");
        return true;
    }

    protected static boolean isDisplayADEarIcon_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayADEarIcon_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayADEarIcon called...\n");
        return true;
    }

    protected static boolean isDisplayADAtmos_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.isDisplayADAtmos_native(msgType, msgName);
        }
        Log.d(TAG, "isDisplayADAtmos called...\n");
        return true;
    }

    protected static String getChannelNumber_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getChannelNumber_native(msgType, msgName);
        }
        Log.d(TAG, "getChannelNumber called...\n");
        return null;
    }

    protected static String getChannelName_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getChannelName_native(msgType, msgName);
        }
        Log.d(TAG, "getChannelName called...\n");
        return null;
    }

    protected static String getTVSrc_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getTVSrc_native(msgType, msgName);
        }
        Log.d(TAG, "getTVSrc called...\n");
        return null;
    }

    protected static String getTimer_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getTimer_native(msgType, msgName);
        }
        Log.d(TAG, "getTimer called...\n");
        return null;
    }

    protected static String getMsg_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getMsg_native(msgType, msgName);
        }
        Log.d(TAG, "getMsg called...\n");
        return null;
    }

    protected static String getProgTitle_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgTitle_native(msgType, msgName);
        }
        Log.d(TAG, "getProgTitle called...\n");
        return null;
    }

    protected static String getProgCategory_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgCategory_native(msgType, msgName);
        }
        Log.d(TAG, "getProgCategory called...\n");
        return null;
    }

    protected static String getProgCategoryIdx_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgCategoryIdx_native(msgType, msgName);
        }
        Log.d(TAG, "getProgCategoryIdx called...\n");
        return null;
    }

    protected static String getNextProgDetail_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getNextProgDetail_native(msgType, msgName);
        }
        Log.d(TAG, "getNextProgDetail called...\n");
        return null;
    }

    protected static String getNextProgCategoryIdx_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getNextProgCategoryIdx_native(msgType, msgName);
        }
        Log.d(TAG, "getNextProgCategoryIdx called...\n");
        return null;
    }

    protected static String getNextRating_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getNextRating_native(msgType, msgName);
        }
        Log.d(TAG, "getNextRating called...\n");
        return null;
    }

    protected static String getProgTime_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgTime_native(msgType, msgName);
        }
        Log.d(TAG, "getProgTime called...\n");
        return null;
    }

    protected static String getNextProgTitle_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getNextProgTitle_native(msgType, msgName);
        }
        Log.d(TAG, "getNextProgTitle called...\n");
        return null;
    }

    protected static String getNextProgTime_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getNextProgTime_native(msgType, msgName);
        }
        Log.d(TAG, "getNextProgTime called...\n");
        return null;
    }

    protected static String getProgDetail_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgDetail_native(msgType, msgName);
        }
        Log.d(TAG, "getProgDetail called...\n");
        return null;
    }

    protected static String getProgDetailPageIdx_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getProgDetailPageIdx_native(msgType, msgName);
        }
        Log.d(TAG, "getProgDetailPageIdx called...\n");
        return null;
    }

    protected static String getRating_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getRating_native(msgType, msgName);
        }
        Log.d(TAG, "getRating called...\n");
        return null;
    }

    protected static String getCaption_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getCaption_native(msgType, msgName);
        }
        Log.d(TAG, "getCaption called...\n");
        return null;
    }

    protected static String getIptsCC_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getIptsCC_native(msgType, msgName);
        }
        Log.d(TAG, "getIptsCC called...\n");
        return null;
    }

    protected static String getIptsRating_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getIptsRating_native(msgType, msgName);
        }
        Log.d(TAG, "getIptsRating called...\n");
        return null;
    }

    protected static String getAudioInfo_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getAudioInfo_native(msgType, msgName);
        }
        Log.d(TAG, "getAudioInfo called...\n");
        return null;
    }

    protected static String getVideoInfo_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getVideoInfo_native(msgType, msgName);
        }
        Log.d(TAG, "getVideoInfo called...\n");
        return null;
    }

    protected static String getIptsName_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getIptsName_native(msgType, msgName);
        }
        Log.d(TAG, "getIptsName called...\n");
        return null;
    }

    protected static String getIptsRslt_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getIptsRslt_native(msgType, msgName);
        }
        Log.d(TAG, "getIptsRslt called...\n");
        return null;
    }

    protected static String getIptChannelNumber_native(int msgType, int msgName) {
        if (!is_emulator()) {
            return TVNative.getIptChannelNumber_native(msgType, msgName);
        }
        Log.d(TAG, "getIptChannelNumber called...\n");
        return null;
    }

    protected static int setFreeze_native(String pass, boolean b_freeze) {
        if (!is_emulator()) {
            return TVNative.setFreeze_native(pass, b_freeze);
        }
        Log.d(TAG, "setFreeze_native called ...\n");
        return 0;
    }

    protected static boolean isFreeze_native(String pass) {
        if (!is_emulator()) {
            return TVNative.isFreeze_native(pass);
        }
        Log.d(TAG, "isFreeze_native called ...\n");
        return false;
    }

    protected static int setNextAudioLang_native() {
        if (!is_emulator()) {
            return TVNative.setNextAudioLang_native();
        }
        Log.d(TAG, "setNextAudioLang_native...\n");
        return 0;
    }

    protected static void getAudioLang_native(MtkTvAVModeBase audio) {
        if (is_emulator()) {
            Log.d(TAG, "getAudioLang_native...\n");
        } else {
            TVNative.getAudioLang_native(audio);
        }
    }

    protected static void getAudioAvailableRecord_native(List<TvProviderAudioTrackBase> audioList) {
        if (is_emulator()) {
            Log.d(TAG, "getAudioAvailableRecord_native...\n");
        } else {
            TVNative.getAudioAvailableRecord_native(audioList);
        }
    }

    protected static int setVideoInfoValue_native(int setType, int setVal) {
        if (!is_emulator()) {
            return TVNative.setVideoInfoValue_native(setType, setVal);
        }
        Log.d(TAG, "setVideoBackLight_native...\n");
        return 0;
    }

    protected static int getVideoInfoValue_native(int setType) {
        if (!is_emulator()) {
            return TVNative.getVideoInfoValue_native(setType);
        }
        Log.d(TAG, "getVideoBackLight_native...\n");
        return 0;
    }

    protected static int setVideoInfoData_native(int setType, MtkTvVideoInfoBase base) {
        if (!is_emulator()) {
            return TVNative.setVideoInfoData_native(setType, base);
        }
        Log.d(TAG, "setVideoBackLight_native...\n");
        return 0;
    }

    protected static int getVideoInfoData_native(int setType, MtkTvVideoInfoBase videoInfoList) {
        if (!is_emulator()) {
            return TVNative.getVideoInfoData_native(setType, videoInfoList);
        }
        Log.d(TAG, "getVideoBackLight_native...\n");
        return 0;
    }

    protected static void getInputSourceAudioAvailableRecord_native(List<TvProviderAudioTrackBase> audioList) {
        if (is_emulator()) {
            Log.d(TAG, "getInputSourceAudioAvailableRecord_native...\n");
        } else {
            TVNative.getInputSourceAudioAvailableRecord_native(audioList);
        }
    }

    protected static int getCurrentAudio_native(TvProviderAudioTrackBase audio) {
        if (!is_emulator()) {
            return TVNative.getCurrentAudio_native(audio);
        }
        Log.d(TAG, "getCurrentAudio_native...\n");
        return 0;
    }

    protected static int getAudioDecType_native() {
        if (!is_emulator()) {
            return TVNative.getAudioDecType_native();
        }
        Log.d(TAG, "getAudioDecType_native...\n");
        return 0;
    }

    protected static int getInputSourceCurrentAudio_native(TvProviderAudioTrackBase audio) {
        if (!is_emulator()) {
            return TVNative.getInputSourceCurrentAudio_native(audio);
        }
        Log.d(TAG, "getHDMICurrentAudio_native...\n");
        return 0;
    }

    protected static int selectAudioById_native(int audioId) {
        if (!is_emulator()) {
            return TVNative.selectAudioById_native(audioId);
        }
        Log.d(TAG, "selectAudioById_native...\n");
        return 0;
    }

    protected static int unselectAudio_native() {
        if (!is_emulator()) {
            return TVNative.unselectAudio_native();
        }
        Log.d(TAG, "unselectAudio_native...\n");
        return 0;
    }

    protected static int selectMainSubAudioById_native(int mainAudioId, int subAudioId) {
        if (!is_emulator()) {
            return TVNative.selectMainSubAudioById_native(mainAudioId, subAudioId);
        }
        Log.d(TAG, "selectMainSubAudioById_native...\n");
        return 0;
    }

    protected static int getSlotNum_navtive() {
        if (!is_emulator()) {
            return TVNative.getSlotNum_navtive();
        }
        Log.d(TAG, "getSlotNum_navtive called...");
        return 0;
    }

    protected static int getMenuListID_navtive() {
        if (!is_emulator()) {
            return TVNative.getMenuListID_navtive();
        }
        Log.d(TAG, "getMenuListID_navtive called...");
        return 0;
    }

    protected static int getEnqID_navtive() {
        if (!is_emulator()) {
            return TVNative.getEnqID_navtive();
        }
        Log.d(TAG, "getEnqID_navtive called...");
        return 0;
    }

    protected static String getCamName_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getCamName_navtive(slotId);
        }
        Log.d(TAG, "getCamName_navtive called...");
        return "";
    }

    protected static String getCamID_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getCamID_navtive(slotId);
        }
        Log.d(TAG, "getCamID_navtive called...");
        return "";
    }

    protected static boolean getSlotActive_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getSlotActive_navtive(slotId);
        }
        Log.d(TAG, "getSlotActive_navtive called...");
        return false;
    }

    protected static int setMenuAnswer_navtive(int slotId, int mmi_id, int answer_item) {
        if (!is_emulator()) {
            return TVNative.setMenuAnswer_navtive(slotId, mmi_id, answer_item);
        }
        Log.d(TAG, "setMenuAnswer_navtive called...");
        return 0;
    }

    protected static int setEnqAnswer_navtive(int slotId, int mmi_id, int answer, String answer_data) {
        if (!is_emulator()) {
            return TVNative.setEnqAnswer_navtive(slotId, mmi_id, answer, answer_data);
        }
        Log.d(TAG, "setEnqAnswer_navtive called...");
        return 0;
    }

    protected static void setMMIClose_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "setMMIClose_navtive called...");
        } else {
            TVNative.setMMIClose_navtive(slotId);
        }
    }

    protected static void setMMICloseDone_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "setMMICloseDone_navtive called...");
        } else {
            TVNative.setMMICloseDone_navtive(slotId);
        }
    }

    protected static void enterMMI_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "enterMMI_navtive called...");
        } else {
            TVNative.enterMMI_navtive(slotId);
        }
    }

    protected static int startCamScan_navtive(int slotId, boolean b_flag) {
        if (!is_emulator()) {
            return TVNative.startCamScan_navtive(slotId, b_flag);
        }
        Log.d(TAG, "startCamScan_navtive called...");
        return 0;
    }

    protected static int cancelCamScan_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "cancelCamScan_navtive called...");
            return 0;
        }
        Log.d(TAG, "cancelCamScan_navtive called...");
        return TVNative.cancelCamScan_navtive(slotId);
    }

    public static int updateCIKey_navtive() {
        if (!is_emulator()) {
            return TVNative.updateCIKey_navtive();
        }
        Log.d(TAG, "updateCIKey_navtive called ...\n");
        return 0;
    }

    public static int writeCIKey_navtive(byte[] key_buffer, int key_buffer_len) {
        if (is_emulator()) {
            Log.d(TAG, "writeCIKey_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "writeCIKey_navtive ...\n");
        return TVNative.writeCIKey_navtive(key_buffer, key_buffer_len);
    }

    public static int activateCIKey_navtive() {
        if (is_emulator()) {
            Log.d(TAG, "activateCIKey_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "activateCIKey_navtive ...\n");
        return TVNative.activateCIKey_navtive();
    }

    public static int updateCIKeyEx_navtive(int cert_type) {
        if (is_emulator()) {
            Log.d(TAG, "updateCIKeyEx_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "updateCIKeyEx_navtive ...\n");
        return TVNative.updateCIKeyEx_navtive(cert_type);
    }

    public static int eraseCIKey_navtive() {
        if (!is_emulator()) {
            return TVNative.eraseCIKey_navtive();
        }
        Log.d(TAG, "eraseCIKey_navtive called ...\n");
        return 0;
    }

    public static String getCIKeyinfo_navtive() {
        if (!is_emulator()) {
            return TVNative.getCIKeyinfo_navtive();
        }
        Log.d(TAG, "getCIKeyinfo_navtive called ...\n");
        return null;
    }

    public static boolean getCIKeyStatus_navtive() {
        if (is_emulator()) {
            Log.d(TAG, "getCIKeyStatus_navtive called ...\n");
            return false;
        }
        Log.d(TAG, "getCIKeyStatus_navtive called ...\n");
        return TVNative.getCIKeyStatus_navtive();
    }

    public static boolean getCIKeyStatusEx_navtive(int cert_type) {
        if (is_emulator()) {
            Log.d(TAG, "getCIKeyStatusEx_navtive called ...\n");
            return false;
        }
        Log.d(TAG, "getCIKeyStatusEx_navtive called ...\n");
        return TVNative.getCIKeyStatusEx_navtive(cert_type);
    }

    public static int updateCIKeyWithPath_navtive(String path) {
        if (is_emulator()) {
            Log.d(TAG, "updateCIKeyWithPath_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "updateCIKeyWithPath_navtive called ...\n");
        return TVNative.updateCIKeyWithPath_navtive(path);
    }

    public static int updateCIKeyWithPathEx_navtive(String path, int cert_type) {
        if (is_emulator()) {
            Log.d(TAG, "updateCIKeyWithPathEx_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "updateCIKeyWithPathEx_navtive called ...\n");
        return TVNative.updateCIKeyWithPathEx_navtive(path, cert_type);
    }

    public static int getCIHostID_navtive(int[] host_id) {
        if (is_emulator()) {
            Log.d(TAG, "getCIHostID_navtive called ...\n");
            return 0;
        }
        Log.d(TAG, "getCIHostID_navtive called ...\n");
        return TVNative.getCIHostID_navtive(host_id);
    }

    protected static int setCamPinCode_navtive(int slotId, String answer_data) {
        if (!is_emulator()) {
            return TVNative.setCamPinCode_navtive(slotId, answer_data);
        }
        Log.d(TAG, "setCamPinCode_navtive called...");
        return 0;
    }

    protected static String getCamPinCode_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getCamPinCode_navtive called...");
            return null;
        }
        Log.d(TAG, "getCamPinCode_navtive called...");
        return TVNative.getCamPinCode_navtive(slotId);
    }

    protected static int recordgetErrorIDByHandle(int handle) {
        if (!is_emulator()) {
            return TVNative.recordgetErrorIDByhandle(handle);
        }
        Log.d(TAG, "recordgetErrorID called...\n");
        return 0;
    }

    protected static int getCamPinCaps_navtive() {
        if (!is_emulator()) {
            return TVNative.getCamPinCaps_navtive();
        }
        Log.d(TAG, "getEnqID_navtive called...");
        return 0;
    }

    protected static int getCamRatingValue_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getCamRatingValue_navtive(slotId);
        }
        Log.d(TAG, "getCamRatingValue_navtive called...");
        return 0;
    }

    protected static int getCaSystemIDMatch_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getCaSystemIDMatch_navtive(slotId);
        }
        Log.d(TAG, "getCaSystemIDMatch_navtive called...");
        return 0;
    }

    protected static int getProfileValid_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getProfileValid_navtive called...");
            return 0;
        }
        Log.d(TAG, "getProfileValid_navtive called...");
        return TVNative.getProfileValid_navtive(slotId);
    }

    protected static int getProfileSupport_navtive(int slotId, int tuner_mode) {
        if (is_emulator()) {
            Log.d(TAG, "getProfileSupport_navtive called...");
            return 0;
        }
        Log.d(TAG, "getProfileSupport_navtive called...");
        return TVNative.getProfileSupport_navtive(slotId, tuner_mode);
    }

    protected static int getProfileResourceIsOpen_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getProfileResourceIsOpen_navtive called...");
            return 0;
        }
        Log.d(TAG, "getProfileResourceIsOpen_navtive called...");
        return TVNative.getProfileResourceIsOpen_navtive(slotId);
    }

    protected static String getProfileName_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getProfileName_navtive called...");
            return null;
        }
        Log.d(TAG, "getProfileName_navtive called...");
        return TVNative.getProfileName_navtive(slotId);
    }

    protected static String getProfileISO639LangCode_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getProfileISO639LangCode_navtive called...");
            return "";
        }
        Log.d(TAG, "getProfileISO639LangCode_navtive called...");
        return TVNative.getProfileISO639LangCode_navtive(slotId);
    }

    protected static int getSasItvState_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getSasItvState_navtive called...");
            return 0;
        }
        Log.d(TAG, "getSasItvState_navtive called...");
        return TVNative.getSasItvState_navtive(slotId);
    }

    protected static int setSasForceItvExit_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "setSasForceItvExit_navtive called...");
            return 0;
        }
        Log.d(TAG, "setSasForceItvExit_navtive called...");
        return TVNative.setSasForceItvExit_navtive(slotId);
    }

    protected static int getSasCbctState_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getSasCbctState_navtive called...");
            return 0;
        }
        Log.d(TAG, "getSasCbctState_navtive called...");
        return TVNative.getSasCbctState_navtive(slotId);
    }

    protected static int getHostTuneStatus_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getHostTuneStatus_navtive called...");
            return 0;
        }
        Log.d(TAG, "getHostTuneStatus_navtive called...");
        return TVNative.getHostTuneStatus_navtive(slotId);
    }

    protected static int getHostTuneBrdcstStatus_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getHostTuneBrdcstStatus_navtive called...");
            return 0;
        }
        Log.d(TAG, "getHostTuneBrdcstStatus_navtive called...");
        return TVNative.getHostTuneBrdcstStatus_navtive(slotId);
    }

    protected static int setScanComplete_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "setScanComplete_navtive called...");
            return 0;
        }
        Log.d(TAG, "setScanComplete_navtive called...");
        return TVNative.setScanComplete_navtive(slotId);
    }

    protected static int getHostQuietTuneStatus_navtive(int slotId) {
        if (is_emulator()) {
            Log.d(TAG, "getHostQuietTuneStatus_navtive called...");
            return 0;
        }
        Log.d(TAG, "getHostQuietTuneStatus_navtive called...");
        return TVNative.getHostQuietTuneStatus_navtive(slotId);
    }

    protected static int setHDSConfirm_navtive(int slotId, int confirm_value) {
        if (is_emulator()) {
            Log.d(TAG, "setHDSConfirm_navtive called...");
            return 0;
        }
        Log.d(TAG, "setHDSConfirm_navtive called...");
        return TVNative.setHDSConfirm_navtive(slotId, confirm_value);
    }

    protected static String getVirtualChannelInfo_navtive(int slotId) {
        if (!is_emulator()) {
            return TVNative.getVirtualChannelInfo_navtive(slotId);
        }
        Log.d(TAG, "getVirtualChannelInfo_navtive called...");
        return null;
    }

    protected static int ISDBCCEnable_native(boolean flag) {
        if (!is_emulator()) {
            return TVNative.ISDBCCEnable_native(flag);
        }
        Log.d(TAG, "ISDBCCEnable_native called ...\n");
        return 0;
    }

    protected static int ISDBCCNextStream_native() {
        if (!is_emulator()) {
            return TVNative.ISDBCCNextStream_native();
        }
        Log.d(TAG, "ISDBCCNextStream_native called ...\n");
        return 0;
    }

    protected static int ISDBCCGetCCString_native() {
        if (!is_emulator()) {
            return TVNative.ISDBCCGetCCString_native();
        }
        Log.d(TAG, "ISDBCCGetCCString_native called ...\n");
        return 0;
    }

    protected static boolean checkPWD_native(String spwd) {
        if (!is_emulator()) {
            return TVNative.checkPWD_native(spwd);
        }
        Log.d(TAG, "checkPWD_native called ...\n");
        return false;
    }

    protected static int PWDShow_native() {
        if (!is_emulator()) {
            return TVNative.PWDShow_native();
        }
        Log.d(TAG, "PWDShow_native is called ... \n");
        return 0;
    }

    protected static int getScrnSvrMsgID_native() {
        if (!is_emulator()) {
            return TVNative.getScrnSvrMsgID_native();
        }
        Log.d(TAG, "getScrnSvrMsgID_native is called ... \n");
        return 0;
    }

    protected static int getUSTvRatingSettingInfo_native(MtkTvUSTvRatingSettingInfoBase ratingInfo) {
        if (!is_emulator()) {
            return TVNative.getUSTvRatingSettingInfo_native(ratingInfo);
        }
        Log.d(TAG, "getUSTvRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int setUSTvRatingSettingInfo_native(MtkTvUSTvRatingSettingInfoBase ratingInfo) {
        if (!is_emulator()) {
            return TVNative.setUSTvRatingSettingInfo_native(ratingInfo);
        }
        Log.d(TAG, "setUSTvRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int getUSMovieRatingSettingInfo_native() {
        if (!is_emulator()) {
            return TVNative.getUSMovieRatingSettingInfo_native();
        }
        Log.d(TAG, "getUSMovieRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int setUSMovieRatingSettingInfo_native(int movieRatingSettingInfo) {
        if (!is_emulator()) {
            return TVNative.setUSMovieRatingSettingInfo_native(movieRatingSettingInfo);
        }
        Log.d(TAG, "setUSMovieRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int getCANEngRatingSettingInfo_native() {
        if (!is_emulator()) {
            return TVNative.getCANEngRatingSettingInfo_native();
        }
        Log.d(TAG, "getCANEngRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int setCANEngRatingSettingInfo_native(int engRatingSettingInfo) {
        if (!is_emulator()) {
            return TVNative.setCANEngRatingSettingInfo_native(engRatingSettingInfo);
        }
        Log.d(TAG, "setCANEngRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int getCANFreRatingSettingInfo_native() {
        if (!is_emulator()) {
            return TVNative.getCANFreRatingSettingInfo_native();
        }
        Log.d(TAG, "getCANFreRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static int setCANFreRatingSettingInfo_native(int freRatingSettingInfo) {
        if (!is_emulator()) {
            return TVNative.setCANFreRatingSettingInfo_native(freRatingSettingInfo);
        }
        Log.d(TAG, "setCANFreRatingSettingInfo_native called...\n");
        return 0;
    }

    protected static boolean getBlockUnrated_native() {
        if (!is_emulator()) {
            return TVNative.getBlockUnrated_native();
        }
        Log.d(TAG, "getBlockUnrated_native called...\n");
        return false;
    }

    protected static int setBlockUnrated_native(boolean isBlockUnrated) {
        if (!is_emulator()) {
            return TVNative.setBlockUnrated_native(isBlockUnrated);
        }
        Log.d(TAG, "setBlockUnrated_native called...\n");
        return 0;
    }

    protected static boolean getRatingEnable_native() {
        if (!is_emulator()) {
            return TVNative.getRatingEnable_native();
        }
        Log.d(TAG, "getRatingEnable_native called...\n");
        return false;
    }

    protected static int setRatingEnable_native(boolean isRatingEnable) {
        if (!is_emulator()) {
            return TVNative.setRatingEnable_native(isRatingEnable);
        }
        Log.d(TAG, "setRatingEnable_native called...\n");
        return 0;
    }

    protected static boolean isOpenVCHIPInfoAvailable_native() {
        if (!is_emulator()) {
            return TVNative.isOpenVCHIPInfoAvailable_native();
        }
        Log.d(TAG, "isOpenVCHIPInfoAvailable_native called...\n");
        return false;
    }

    protected static int getOpenVCHIPInfo_native(MtkTvOpenVCHIPParaBase opVCHIPPara, MtkTvOpenVCHIPInfoBase openVCHIPInfo) {
        if (!is_emulator()) {
            return TVNative.getOpenVCHIPInfo_native(opVCHIPPara, openVCHIPInfo);
        }
        Log.d(TAG, "getOpenVCHIPInfo_native called...\n");
        return 0;
    }

    protected static int getOpenVCHIPSettingInfo_native(MtkTvOpenVCHIPSettingInfoBase opSettingInfo) {
        if (!is_emulator()) {
            return TVNative.getOpenVCHIPSettingInfo_native(opSettingInfo);
        }
        Log.d(TAG, "getOpenVCHIPSettingInfo_native called...\n");
        return 0;
    }

    protected static int setOpenVCHIPSettingInfo_native(MtkTvOpenVCHIPSettingInfoBase opSettingInfo) {
        if (!is_emulator()) {
            return TVNative.setOpenVCHIPSettingInfo_native(opSettingInfo);
        }
        Log.d(TAG, "setOpenVCHIPSettingInfo_native called...\n");
        return 0;
    }

    protected static int setAtscStorage_native(boolean b_store) {
        if (!is_emulator()) {
            return TVNative.setAtscStorage_native(b_store);
        }
        Log.d(TAG, "setAtscStorage_native called...\n");
        return 0;
    }

    protected static int getISDBContentRatingSetting_native() {
        if (!is_emulator()) {
            return TVNative.getISDBContentRatingSetting_native();
        }
        Log.d(TAG, "getISDBContentRatingSetting_native called...\n");
        return 0;
    }

    protected static int setISDBContentRatingSetting_native(int contentRatingSetting) {
        if (!is_emulator()) {
            return TVNative.setISDBContentRatingSetting_native(contentRatingSetting);
        }
        Log.d(TAG, "setISDBContentRatingSetting_native called...\n");
        return 0;
    }

    protected static int getISDBAgeRatingSetting_native() {
        if (!is_emulator()) {
            return TVNative.getISDBAgeRatingSetting_native();
        }
        Log.d(TAG, "getISDBAgeRatingSetting_native called...\n");
        return 0;
    }

    protected static int setISDBAgeRatingSetting_native(int ageRatingSetting) {
        if (!is_emulator()) {
            return TVNative.setISDBAgeRatingSetting_native(ageRatingSetting);
        }
        Log.d(TAG, "setISDBAgeRatingSetting_native called...\n");
        return 0;
    }

    protected static int getDVBAgeRatingSetting_native() {
        if (!is_emulator()) {
            return TVNative.getDVBAgeRatingSetting_native();
        }
        Log.d(TAG, "getDVBAgeRatingSetting_native called...\n");
        return 0;
    }

    protected static int setDVBAgeRatingSetting_native(int ageRatingSetting) {
        if (!is_emulator()) {
            return TVNative.setDVBAgeRatingSetting_native(ageRatingSetting);
        }
        Log.d(TAG, "setDVBAgeRatingSetting_native called...\n");
        return 0;
    }

    public static int getCrntRatingInfo_native(MtkTvRatingConvert2Goo ratingMapped) {
        if (!is_emulator()) {
            return TVNative.getCrntRatingInfo_native(ratingMapped);
        }
        Log.d(TAG, "getCrntRatingInfo_native called...\n");
        return 0;
    }

    protected static int[] loadEvents_native(int channelId, long startTime, int count) {
        if (!is_emulator()) {
            return TVNative.loadEvents_native(channelId, startTime, count);
        }
        Log.d(TAG, "loadEvents_native called ...\n");
        return null;
    }

    protected static void getEvent_native(int requestId, MtkTvEventInfoBase eventInfo) {
        if (is_emulator()) {
            Log.d(TAG, "getEvent_native called ...\n");
        } else {
            TVNative.getEvent_native(requestId, eventInfo);
        }
    }

    protected static int freeEvent_native(int requestId) {
        if (!is_emulator()) {
            return TVNative.freeEvent_native(requestId);
        }
        Log.d(TAG, "freeEvent_native called ...\n");
        return 0;
    }

    protected static boolean checkAtscEventBlock_native(int requestId, int channelId) {
        if (!is_emulator()) {
            return TVNative.checkAtscEventBlock_native(requestId, channelId);
        }
        Log.d(TAG, "checkAtscEventBlock_native called ...\n");
        return false;
    }

    protected static int loadEventByEIT_native(int channelId, int eitIndex) {
        if (!is_emulator()) {
            return TVNative.loadEventByEIT_native(channelId, eitIndex);
        }
        Log.d(TAG, "loadEventByEIT_native called ...\n");
        return 0;
    }

    protected static int getEventNumberInEIT_native(int requestId) {
        if (!is_emulator()) {
            return TVNative.getEventNumberInEIT_native(requestId);
        }
        Log.d(TAG, "getEventNumberInEIT_native called ...\n");
        return 0;
    }

    protected static void getEventByIndex_native(int requestId, int indexInEIT, MtkTvEventInfoBase eventInfo) {
        if (is_emulator()) {
            Log.d(TAG, "getEventByIndex_native called ...\n");
        } else {
            TVNative.getEventByIndex_native(requestId, indexInEIT, eventInfo);
        }
    }

    protected static String getEventDetailByIndex_native(int requestId, int eventId) {
        if (!is_emulator()) {
            return TVNative.getEventDetailByIndex_native(requestId, eventId);
        }
        Log.d(TAG, "getEventDetailByIndex_native called ...\n");
        return null;
    }

    protected static int getEventRatingMapByIndex_native(int requestId, int indexInEIT, MtkTvRatingConvert2Goo ratingMapped) {
        if (!is_emulator()) {
            return TVNative.getEventRatingMapByIndex_native(requestId, indexInEIT, ratingMapped);
        }
        Log.d(TAG, "getEventRatingMapByIndex_native called ...\n");
        return 0;
    }

    protected static int[] checkEITStauts_native(int requestId, int group_number) {
        if (!is_emulator()) {
            return TVNative.checkEITStauts_native(requestId, group_number);
        }
        Log.d(TAG, "checkEITStauts_native called ...\n");
        return null;
    }

    protected static int getCurrentTeletextPage_native(MtkTvTeletextPageBase teletextPageAddr) {
        if (!is_emulator()) {
            return TVNative.getCurrentTeletextPage_native(teletextPageAddr);
        }
        Log.d(TAG, "getCurrentTeletextPage_native called ...\n");
        return 0;
    }

    protected static int setTeletextPage_native(MtkTvTeletextPageBase teletextPageAddr) {
        if (!is_emulator()) {
            return TVNative.setTeletextPage_native(teletextPageAddr);
        }
        Log.d(TAG, "setTeletextPage_native called ...\n");
        return 0;
    }

    protected static boolean teletextHasTopInfo_native() {
        if (!is_emulator()) {
            return TVNative.teletextHasTopInfo_native();
        }
        Log.d(TAG, "teletextHasTopInfo_native called ...\n");
        return false;
    }

    protected static int getTeletextTopBlockList_native(List<MtkTvTeletextTopBlockBase> blockList) {
        if (!is_emulator()) {
            return TVNative.getTeletextTopBlockList_native(blockList);
        }
        Log.d(TAG, "getTeletextTopBlockList_native called ...\n");
        return 0;
    }

    protected static int getTeletextTopGroupList_native(MtkTvTeletextTopBlockBase indexBlock, List<MtkTvTeletextTopGroupBase> groupList) {
        if (!is_emulator()) {
            return TVNative.getTeletextTopGroupList_native(indexBlock, groupList);
        }
        Log.d(TAG, "getTeletextTopGroupList_native called ...\n");
        return 0;
    }

    protected static int getTeletextTopPageList_native(MtkTvTeletextTopGroupBase indexGroup, List<MtkTvTeletextTopPageBase> normalPageList) {
        if (!is_emulator()) {
            return TVNative.getTeletextTopPageList_native(indexGroup, normalPageList);
        }
        Log.d(TAG, "getTeletextTopPageList_native called ...\n");
        return 0;
    }

    protected static int[] getAllScreenMode_native() {
        if (!is_emulator()) {
            return TVNative.getAllScreenMode_native();
        }
        Log.d(TAG, "getAllScreenMode_native called ...\n");
        return null;
    }

    protected static int setScreenMode_native(int screenMode) {
        if (!is_emulator()) {
            return TVNative.setScreenMode_native(screenMode);
        }
        Log.d(TAG, "setScreenMode_native called ...\n");
        return 0;
    }

    protected static int getScreenMode_native() {
        if (!is_emulator()) {
            return TVNative.getScreenMode_native();
        }
        Log.d(TAG, "getScreenMode_native called ...\n");
        return 0;
    }

    protected static boolean isZoomEnable_native() {
        if (!is_emulator()) {
            return TVNative.isZoomEnable_native();
        }
        Log.d(TAG, "isZoomEnable_native called ...\n");
        return false;
    }

    protected static int setCrntOverscan_native(int top, int bottom, int left, int right) {
        if (!is_emulator()) {
            return TVNative.setCrntOverscan_native(top, bottom, left, right);
        }
        Log.d(TAG, "setCrntOverscan_native called ...\n");
        return 0;
    }

    protected static int getCrntOverscan_native(MtkTvScreenModeOverscan overscan) {
        if (!is_emulator()) {
            return TVNative.getCrntOverscan_native(overscan);
        }
        Log.d(TAG, "getCrntOverscan_native called ...\n");
        return 0;
    }

    protected static int updateOverscanIni_native() {
        if (!is_emulator()) {
            return TVNative.updateOverscanIni_native();
        }
        Log.d(TAG, "updateOverscanIni_native called ...\n");
        return 0;
    }

    protected static int nextStream_native() {
        if (!is_emulator()) {
            return TVNative.nextStream_native();
        }
        Log.d(TAG, "nextStream_native called ...\n");
        return 0;
    }

    protected static int playStream_native(int trackId) {
        if (!is_emulator()) {
            return TVNative.playStream_native(trackId);
        }
        Log.d(TAG, "playStream_native called ...\n");
        return 0;
    }

    protected static int SubtitleGetTracks_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.SubtitleGetTracks_native(data);
        }
        Log.d(TAG, "SubtitleGetTracks_native called ...\n");
        return 0;
    }

    protected static int[] getAllSoundEffect_native() {
        if (!is_emulator()) {
            return TVNative.getAllSoundEffect_native();
        }
        Log.d(TAG, "getAllSoundEffect_native called ...\n");
        return null;
    }

    protected static String isdbCDTGetChannelLogo_native(int curOriginalNetworkID, int curServiceID, int curFrequency) {
        if (!is_emulator()) {
            return TVNative.isdbCDTGetChannelLogo_native(curOriginalNetworkID, curServiceID, curFrequency);
        }
        Log.d(TAG, "isdbCDTGetChannelLogo_native called ...\n");
        return null;
    }

    protected static int setSoundEffect_native(int soundEffect) {
        if (!is_emulator()) {
            return TVNative.setSoundEffect_native(soundEffect);
        }
        Log.d(TAG, "setSoundEffect_native called ...\n");
        return 0;
    }

    protected static int getSoundEffect_native() {
        if (!is_emulator()) {
            return TVNative.getSoundEffect_native();
        }
        Log.d(TAG, "getSoundEffect_native called ...\n");
        return 0;
    }

    protected static int[] getAllPictureMode_native() {
        if (!is_emulator()) {
            return TVNative.getAllPictureMode_native();
        }
        Log.d(TAG, "getAllPictureMode_native called ...\n");
        return null;
    }

    protected static int setPictureMode_native(int pictureMode) {
        if (!is_emulator()) {
            return TVNative.setPictureMode_native(pictureMode);
        }
        Log.d(TAG, "setPictureMode_native called ...\n");
        return 0;
    }

    protected static int getPictureMode_native() {
        if (!is_emulator()) {
            return TVNative.getPictureMode_native();
        }
        Log.d(TAG, "getPictureMode_native called ...\n");
        return 0;
    }

    protected static int setEASAndroidLaunchStatus_native(boolean b_flag) {
        if (!is_emulator()) {
            return TVNative.setEASAndroidLaunchStatus_native(b_flag);
        }
        Log.d(TAG, "setEASAndroidLaunchStatus_native called ...\n");
        return 0;
    }

    protected static boolean getEASAndroidLaunchStatus_native() {
        if (!is_emulator()) {
            return TVNative.getEASAndroidLaunchStatus_native();
        }
        Log.d(TAG, "getEASAndroidLaunchStatus_native called ...\n");
        return false;
    }

    protected static int analogCCEnable_native(boolean b_flag) {
        if (!is_emulator()) {
            return TVNative.analogCCEnable_native(b_flag);
        }
        Log.d(TAG, "analogCCEnable_native called ...\n");
        return 0;
    }

    protected static int analogCCNextStream_native() {
        if (!is_emulator()) {
            return TVNative.analogCCNextStream_native();
        }
        Log.d(TAG, "analogCCNextStream_native called ...\n");
        return 0;
    }

    protected static int analogCCGetCcIndex_native() {
        if (!is_emulator()) {
            return TVNative.analogCCGetCcIndex_native();
        }
        Log.d(TAG, "analogCCGetCcIndex_native called ...\n");
        return 0;
    }

    protected static int analogCCSetCcVisible_native(boolean b_visible) {
        if (!is_emulator()) {
            return TVNative.analogCCSetCcVisible_native(b_visible);
        }
        Log.d(TAG, "analogCCSetCcVisible_native called ...\n");
        return 0;
    }

    protected static int atscCCEnable_native(boolean b_flag) {
        if (!is_emulator()) {
            return TVNative.atscCCEnable_native(b_flag);
        }
        Log.d(TAG, "atscCCEnable_native called ...\n");
        return 0;
    }

    protected static int atscCCNextStream_native() {
        if (!is_emulator()) {
            return TVNative.atscCCNextStream_native();
        }
        Log.d(TAG, "atscCCNextStream_native called ...\n");
        return 0;
    }

    protected static int atscCCGetCCIndex_native() {
        if (!is_emulator()) {
            return TVNative.atscCCGetCCIndex_native();
        }
        Log.d(TAG, "atscCCGetCCIndex_native called ...\n");
        return 0;
    }

    protected static int atscCCSetCcVisible_native(boolean b_visible) {
        if (!is_emulator()) {
            return TVNative.atscCCSetCcVisible_native(b_visible);
        }
        Log.d(TAG, "atscCCSetCcVisible_native called ...\n");
        return 0;
    }

    protected static int atscCCDemoSet_native(int setType, int setValue) {
        if (!is_emulator()) {
            return TVNative.atscCCDemoSet_native(setType, setValue);
        }
        Log.d(TAG, "atscCCDemoSet_native called ...\n");
        return 0;
    }

    protected static int hbbtvExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.hbbtvExchangeData_native(data);
        }
        Log.d(TAG, "hbbtvExchangeData_native called...\n");
        return 0;
    }

    protected static int hbbtvSetAudioDescription_native(int e_enable) {
        if (!is_emulator()) {
            return TVNative.hbbtvSetAudioDescription_native(e_enable);
        }
        Log.d(TAG, "hbbtvExchangeData_native called...\n");
        return 0;
    }

    protected static int hbbtvSetDefaultAudioLang_native(String lang) {
        if (!is_emulator()) {
            return TVNative.hbbtvSetDefaultAudioLang_native(lang);
        }
        Log.d(TAG, "hbbtvSetDefaultAudioLang_native called...\n");
        return 0;
    }

    protected static int hbbtvSetDefaultSubtitleLang_native(String lang, int enable) {
        if (!is_emulator()) {
            return TVNative.hbbtvSetDefaultSubtitleLang_native(lang, enable);
        }
        Log.d(TAG, "hbbtvSetDefaultSubtitleLang_native called...\n");
        return 0;
    }

    protected static int hbbtvStmGetAudioCount_native() {
        if (!is_emulator()) {
            return TVNative.hbbtvStmGetAudioCount_native();
        }
        Log.d(TAG, "hbbtvStmGetAudioCount_native called...\n");
        return 0;
    }

    protected static String hbbtvStmGetAudioLang_native(int idx) {
        if (!is_emulator()) {
            return TVNative.hbbtvStmGetAudioLang_native(idx);
        }
        Log.d(TAG, "hbbtvStmGetAudioLang_native called...\n");
        return "";
    }

    protected static int hbbtvStmGetAudio_native() {
        if (!is_emulator()) {
            return TVNative.hbbtvStmGetAudio_native();
        }
        Log.d(TAG, "hbbtvStmGetAudio_native called...\n");
        return 0;
    }

    protected static int hbbtvStmSetAudioIndex_native(int idx) {
        if (!is_emulator()) {
            return TVNative.hbbtvStmSetAudioIndex_native(idx);
        }
        Log.d(TAG, "hbbtvStmSetAudioIndex_native called...\n");
        return 0;
    }

    protected static int startApplication_native(String sAppID) {
        if (!is_emulator()) {
            return TVNative.startApplication_native(sAppID);
        }
        Log.d(TAG, "startApplication_native called ...\n");
        return 0;
    }

    protected static int stopApplication_native(String sAppID) {
        if (!is_emulator()) {
            return TVNative.stopApplication_native(sAppID);
        }
        Log.d(TAG, "stopApplication_native called ...\n");
        return 0;
    }

    protected static int getApplicationInfo_native() {
        if (!is_emulator()) {
            return TVNative.getApplicationInfo_native();
        }
        Log.d(TAG, "getApplicationInfo_native called ...\n");
        return 0;
    }

    protected static boolean getGingaScreenModeisEnable_native() {
        if (!is_emulator()) {
            return TVNative.getGingaScreenModeisEnable_native();
        }
        Log.d(TAG, "getGingaScreenModeisEnable_native called ...\n");
        return false;
    }

    protected static int startGinga_native() {
        if (!is_emulator()) {
            return TVNative.startGinga_native();
        }
        Log.d(TAG, "startGinga_native called ...\n");
        return 0;
    }

    protected static int stopGinga_native() {
        if (!is_emulator()) {
            return TVNative.stopGinga_native();
        }
        Log.d(TAG, "stopGinga_native called ...\n");
        return 0;
    }

    protected static int warningStartCC_native(boolean b_start_cc) {
        if (!is_emulator()) {
            return TVNative.warningStartCC_native(b_start_cc);
        }
        Log.d(TAG, "warningStartCC_native called ...\n");
        return 0;
    }

    protected static int warningStartGingaApp_native(boolean b_start_ginga_app) {
        if (!is_emulator()) {
            return TVNative.warningStartGingaApp_native(b_start_ginga_app);
        }
        Log.d(TAG, "warningStartGingaApp_native called ...\n");
        return 0;
    }

    protected static int getApplicationInfoList_native(List<MtkTvGingaAppInfoBase> gingaAppInfoList) {
        if (!is_emulator()) {
            return TVNative.getApplicationInfoList_native(gingaAppInfoList);
        }
        Log.d(TAG, "getApplicationInfoList_native called ...\n");
        return 0;
    }

    protected static int startUpgrade_native(MtkTvUpgradeDeliveryTypeBase type, boolean flag) {
        if (!is_emulator()) {
            return TVNative.startUpgrade_native(type, flag);
        }
        Log.d(TAG, "startUpgrade_native called ...");
        return 0;
    }

    protected static int triggerUpgrade_native(MtkTvUpgradeDeliveryTypeBase type) {
        if (!is_emulator()) {
            return TVNative.triggerUpgrade_native(type);
        }
        Log.d(TAG, "triggerUpgrade_native called ...");
        return 0;
    }

    protected static int getFirmwareInfo_native(MtkTvUpgradeFirmwareInfoBase firmwareInfo) {
        if (!is_emulator()) {
            return TVNative.getFirmwareInfo_native(firmwareInfo);
        }
        Log.d(TAG, "triggerUpgrade_native called ...");
        return 0;
    }

    protected static int startDownloadFirmware_native(MtkTvUpgradeDeliveryTypeBase type, String url, String fwPath) {
        if (!is_emulator()) {
            return TVNative.startDownloadFirmware_native(type, url, fwPath);
        }
        Log.d(TAG, "startDownloadFirmware_native called ...");
        return 0;
    }

    protected static int cancelDownloadFirmware_native(MtkTvUpgradeDeliveryTypeBase type) {
        if (!is_emulator()) {
            return TVNative.cancelDownloadFirmware_native(type);
        }
        Log.d(TAG, "cancelDownloadFirmware_native called ...");
        return 0;
    }

    protected static int startRebootUpgrade_native(MtkTvUpgradeDeliveryTypeBase type) {
        if (!is_emulator()) {
            return TVNative.startRebootUpgrade_native(type);
        }
        Log.d(TAG, "startRebootUpgrade_native called ...");
        return 0;
    }

    protected static int queryUpgradeResult_native(MtkTvUpgradeDeliveryTypeBase type) {
        if (!is_emulator()) {
            return TVNative.queryUpgradeResult_native(type);
        }
        Log.d(TAG, "queryUpgradeResult_native called ...");
        return 0;
    }

    protected static int popEnterNextTvMode_native() {
        if (!is_emulator()) {
            return TVNative.popEnterNextTvMode_native();
        }
        Log.d(TAG, "popEnterNextTvMode_native Emulator called");
        return 0;
    }

    protected static int popSwapWindow_native() {
        if (!is_emulator()) {
            return TVNative.popSwapWindow_native();
        }
        Log.d(TAG, "popSwapWindow_native Emulator called");
        return 0;
    }

    protected static int popSwitchAudioFocus_native() {
        if (!is_emulator()) {
            return TVNative.popSwitchAudioFocus_native();
        }
        Log.d(TAG, "popSwitchAudioFocus_native Emulator called");
        return 0;
    }

    protected static int popNextPipWindowSize_native() {
        if (!is_emulator()) {
            return TVNative.popNextPipWindowSize_native();
        }
        Log.d(TAG, "popNextPipWindowSize_native Emulator called");
        return 0;
    }

    protected static int popNextPipWindowPosition_native() {
        if (!is_emulator()) {
            return TVNative.popNextPipWindowPosition_native();
        }
        Log.d(TAG, "popNextPipWindowPosition_native Emulator called");
        return 0;
    }

    protected static boolean popGetPipFocusId_native() {
        if (is_emulator()) {
            Log.d(TAG, "popGetPipFocusId_native Emulator called");
            return true;
        }
        Log.d(TAG, "popGetPipFocusId_native  called");
        return TVNative.popGetPipFocusId_native();
    }

    protected static int popGetFocusInfo_native(MtkTvPipPopFucusInfoBase PopFocusInfo) {
        if (is_emulator()) {
            Log.d(TAG, "popGetFocusInfo_native Emulator called");
            return 0;
        }
        Log.d(TAG, "popGetFocusInfo_native  called");
        return TVNative.popGetFocusInfo_native(PopFocusInfo);
    }

    protected static int AppTVsaveTimestamp_native(String str) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVsaveTimestamp_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVsaveTimestamp_native  called");
        return TVNative.AppTVsaveTimestamp_native(str);
    }

    protected static int AppTVunlockService_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVunlockService_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVunlockService_native  called");
        return TVNative.AppTVunlockService_native(path);
    }

    protected static int AppTVunblockSvc_native(String path, boolean forceUnblock) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVunblockSvc_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVunblockSvc_native  called");
        return TVNative.AppTVunblockSvc_native(path, forceUnblock);
    }

    protected static int AppTVsetVideoMute_native(String path, boolean videoMute) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVsetVideoMute_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVsetVideoMute_native  called");
        return TVNative.AppTVsetVideoMute_native(path, videoMute);
    }

    protected static boolean AppTVgetVideoMute_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVgetVideoMute_native Emulator called \n");
            return false;
        }
        Log.d(TAG, "[app_tv]AppTVgetVideoMute_native  called String=" + path + "\n");
        return TVNative.AppTVgetVideoMute_native(path);
    }

    protected static boolean isCaptureLogo_native() {
        if (is_emulator()) {
            Log.d(TAG, "isCaptureLogo Emulator called");
            return false;
        }
        Log.d(TAG, "isCaptureLogo  called");
        return TVNative.isCaptureLogo_native();
    }

    protected static int tunerFacQuery_navtive(boolean isAnalog, List<String> displayName, List<String> displayValue) {
        if (is_emulator()) {
            Log.d(TAG, "tunerFacQuery Emulator called");
            return 0;
        }
        Log.d(TAG, "tunerFacQuery  called");
        return TVNative.tunerFacQuery_navtive(isAnalog, displayName, displayValue);
    }

    protected static boolean getAdbStatus_native() {
        if (is_emulator()) {
            Log.d(TAG, "getAdbStatus Emulator called");
            return false;
        }
        Log.d(TAG, "getAdbStatus  called");
        return TVNative.getAdbStatus_native();
    }

    protected static int setAdbStatus_native(boolean isAdbEnabled) {
        if (is_emulator()) {
            Log.d(TAG, "setAdbStatus Emulator called");
            return 0;
        }
        Log.d(TAG, "setAdbStatus  called");
        return TVNative.setAdbStatus_native(isAdbEnabled);
    }

    protected static int AppTVgetMatchedChannel_native(int svlID, boolean hasMinor, int major, int minor) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVgetMatchedChannel_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVgetMatchedChannel_native  called");
        return TVNative.AppTVgetMatchedChannel_native(svlID, hasMinor, major, minor);
    }

    protected static int AppTVsetFinetuneFreq_native(String path, int iFreq, boolean leave) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVsetFinetuneFreq_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVsetFinetuneFreq_native  called");
        return TVNative.AppTVsetFinetuneFreq_native(path, iFreq, leave);
    }

    protected static int AppTVchangeFreq_native(String path, MtkTvFreqChgParamBase freqInfo) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVchangeFreq_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVchangeFreq_native  called");
        return TVNative.AppTVchangeFreq_native(path, freqInfo);
    }

    protected static int AppTVsetAutoClockPhasePostion_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVsetAutoClockPhasePostion_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVsetAutoClockPhasePostion_native  called");
        return TVNative.AppTVsetAutoClockPhasePostion_native(path);
    }

    protected static boolean AppTVAutoClockPhasePostionCondSuccess_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVAutoClockPhasePostionCondSuccess_native Emulator called");
            return true;
        }
        Log.d(TAG, "AutoClockPhasePostionCondSuccess_native  called");
        return TVNative.AppTVAutoClockPhasePostionCondSuccess_native(path);
    }

    protected static int AppTVsetAutoColor_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVsetAutoColor_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVsetAutoColor_native  called");
        return TVNative.AppTVsetAutoColor_native(path);
    }

    protected static boolean AppTVAutoColorCondSuccess_native(String path) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVAutoColorCondSuccess_native Emulator called");
            return true;
        }
        Log.d(TAG, "AppTVAutoColorCondSuccess_native  called");
        return TVNative.AppTVAutoColorCondSuccess_native(path);
    }

    protected static int AppTVupdatedSysStatus_native(int sysStatus) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVupdatedSysStatus_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVupdatedSysStatus_native  called");
        return TVNative.AppTVupdatedSysStatus_native(sysStatus);
    }

    protected static int AppTVupdatedSysStatus_native(String path, int sysStatus) {
        if (is_emulator()) {
            Log.d(TAG, "AppTVupdatedSysStatus_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTVupdatedSysStatus_native  called");
        return TVNative.AppTVupdatedSysStatus_native(path, sysStatus);
    }

    protected static int AppTV_swapTmpUnlockInfo_native(int modeId) {
        if (is_emulator()) {
            Log.d(TAG, "AppTV_swapTmpUnlockInfo_native Emulator called");
            return 0;
        }
        Log.d(TAG, "AppTV_swapTmpUnlockInfo_native    called");
        return TVNative.AppTV_swapTmpUnlockInfo_native(modeId);
    }

    protected static int getEASCurrentStatus_native(MtkTvEASParaBase mEASPara) {
        if (!is_emulator()) {
            return TVNative.getEASCurrentStatus_native(mEASPara);
        }
        Log.d(TAG, "getEASCurrentStatus called...\n");
        return 0;
    }

    protected static int GetConnAttrBER_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetConnAttrBER_native(path);
        }
        Log.d(TAG, "GetConnAttrBER_native called...\n");
        return 0;
    }

    protected static int GetSignalBER_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetSignalBER_native(path);
        }
        Log.d(TAG, "GetSignalBER_native called...\n");
        return 0;
    }

    protected static long GetConnAttrDBMSNR_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetConnAttrDBMSNR_native(path);
        }
        Log.d(TAG, "GetConnAttrDBMSNR_native called...\n");
        return 0;
    }

    protected static long GetConnAttrUEC_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetConnAttrUEC_native(path);
        }
        Log.d(TAG, "GetConnAttrUEC_native called...\n");
        return 0;
    }

    protected static long GetSymRate_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetSymRate_native(path);
        }
        Log.d(TAG, "GetSymRate_native called...\n");
        return 0;
    }

    protected static int GetModulation_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetModulation_native(path);
        }
        Log.d(TAG, "GetModulation_native called...\n");
        return 0;
    }

    protected static long GetConnAtrrAGC_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetConnAtrrAGC_native(path);
        }
        Log.d(TAG, "GetConnAtrrAGC_native called...\n");
        return 0;
    }

    protected static int GetVideoSrcTag3DType_native(String path) {
        if (!is_emulator()) {
            return TVNative.GetVideoSrcTag3DType_native(path);
        }
        Log.d(TAG, "GetVideoSrcTag3DType_native called...\n");
        return 0;
    }

    protected static boolean VideoSrcIsProgressive_native(String path) {
        if (!is_emulator()) {
            return TVNative.VideoSrcIsProgressive_native(path);
        }
        Log.d(TAG, "VideoSrcIsProgressive_native called...\n");
        return true;
    }

    protected static boolean setWifiWolCtl_native(boolean isOn) {
        if (!is_emulator()) {
            return TVNative.setWifiWolCtl_native(isOn);
        }
        Log.d(TAG, "setWifiWolCtl_native called...\n");
        return true;
    }

    protected static boolean getWifiWolCtl_native() {
        if (!is_emulator()) {
            return TVNative.getWifiWolCtl_native();
        }
        Log.d(TAG, "getWifiWolCtl_native called...\n");
        return true;
    }

    protected static boolean setEthernetWolCtl_native(boolean isOn) {
        if (!is_emulator()) {
            return TVNative.setEthernetWolCtl_native(isOn);
        }
        Log.d(TAG, "setEthernetWolCtl called...\n");
        return true;
    }

    protected static boolean getEthernetWolCtl_native() {
        if (!is_emulator()) {
            return TVNative.getEthernetWolCtl_native();
        }
        Log.d(TAG, "getWifiWolCtl_native called...\n");
        return true;
    }

    public static int sbAtscGetMajorChannelNum_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.sbAtscGetMajorChannelNum_native(channelId);
        }
        Log.d(TAG, "sbAtscGetMajorChannelNum_native called...\n");
        return 0;
    }

    public static int sbAtscGetMinorChannelNum_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.sbAtscGetMinorChannelNum_native(channelId);
        }
        Log.d(TAG, "sbAtscGetMinorChannelNum_native called...\n");
        return 0;
    }

    public static int sbIsdbGetMajorNumber_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.sbIsdbGetMajorNumber_native(channelId);
        }
        Log.d(TAG, "sbIsdbGetMajorNumber_native called...\n");
        return 0;
    }

    public static int sbIsdbGetMinorNumber_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.sbIsdbGetMinorNumber_native(channelId);
        }
        Log.d(TAG, "sbIsdbGetMinorNumber_native called...\n");
        return 0;
    }

    public static int sbIsdbGetChannelIndex_native(int channelId) {
        if (!is_emulator()) {
            return TVNative.sbIsdbGetChannelIndex_native(channelId);
        }
        Log.d(TAG, "sbIsdbGetChannelIndex_native called...\n");
        return 0;
    }

    public static String GetCurrentChannelServiceText_native() {
        if (!is_emulator()) {
            return TVNative.GetCurrentChannelServiceText_native();
        }
        Log.d(TAG, "GetCurrentChannelServiceText_native called...\n");
        return new String();
    }

    public static int queryGpioStatus_native(int gpioId, int mode) {
        if (!is_emulator()) {
            return TVNative.queryGpioStatus_native(gpioId, mode);
        }
        Log.d(TAG, "queryGpioStatus_native called...\n");
        return 0;
    }

    public static int applyGpioStatus_native(int gpioId, int value) {
        if (!is_emulator()) {
            return TVNative.applyGpioStatus_native(gpioId, value);
        }
        Log.d(TAG, "applyGpioStatus_native called...\n");
        return 0;
    }

    public static int openUARTSerial_native(int uartSerialID, int[] uartSerialSetting, int[] handle) {
        if (!is_emulator()) {
            return TVNative.openUARTSerial_native(uartSerialID, uartSerialSetting, handle);
        }
        Log.d(TAG, "openUARTSerial_native called...\n");
        return 0;
    }

    public static int closeUARTSerial_native(int handle) {
        if (!is_emulator()) {
            return TVNative.closeUARTSerial_native(handle);
        }
        Log.d(TAG, "closeUARTSerial_native called...\n");
        return 0;
    }

    public static int getUARTSerialSetting_native(int handle, int[] data) {
        if (!is_emulator()) {
            return TVNative.getUARTSerialSetting_native(handle, data);
        }
        Log.d(TAG, "getUARTSerialSetting_native called...\n");
        return 0;
    }

    public static int setUARTSerialSetting_native(int handle, int[] data) {
        if (!is_emulator()) {
            return TVNative.setUARTSerialSetting_native(handle, data);
        }
        Log.d(TAG, "setUARTSerialSetting_native called...\n");
        return 0;
    }

    public static int getUARTSerialOperationMode_native(int handle, int[] operationMode) {
        if (!is_emulator()) {
            return TVNative.getUARTSerialOperationMode_native(handle, operationMode);
        }
        Log.d(TAG, "getUARTSerialOperationMode_native called...\n");
        return 0;
    }

    public static int setUARTSerialOperationMode_native(int handle, int operationMode) {
        if (!is_emulator()) {
            return TVNative.setUARTSerialOperationMode_native(handle, operationMode);
        }
        Log.d(TAG, "setUARTSerialOperationMode_native called...\n");
        return 0;
    }

    public static int outputUARTSerial_native(int handle, byte[] uartSerialData) {
        if (!is_emulator()) {
            return TVNative.outputUARTSerial_native(handle, uartSerialData);
        }
        Log.d(TAG, "outputUARTSerial_native called...\n");
        return 0;
    }

    public static int factoryWriteKey_native(String keyType, String srcPath) {
        if (!is_emulator()) {
            return TVNative.factoryWriteKey_native(keyType, srcPath);
        }
        Log.d(TAG, "factoryWriteKey_native called...\n");
        return 0;
    }

    public static int factoryWriteKeyFinish_native() {
        if (!is_emulator()) {
            return TVNative.factoryWriteKeyFinish_native();
        }
        Log.d(TAG, "factoryWriteKeyFinish_native called...\n");
        return 0;
    }

    public static int factoryCheckKey_native(String keyType) {
        if (!is_emulator()) {
            return TVNative.factoryCheckKey_native(keyType);
        }
        Log.d(TAG, "factoryCheckKey_native called...\n");
        return 0;
    }

    public static boolean menuIsTvBlock_native() {
        if (!is_emulator()) {
            return TVNative.menuIsTvBlock_native();
        }
        Log.d(TAG, "menuIsTvBlock_native called...\n");
        return true;
    }

    protected static int createSatlSnapshot_native(int satlID) {
        if (!is_emulator()) {
            return TVNative.createSatlSnapshot_native(satlID);
        }
        Log.d(TAG, "createSatlSnapshot_native called...\n");
        return 0;
    }

    protected static int restoreSatlSnapshot_native(int satlsnapshotID) {
        if (!is_emulator()) {
            return TVNative.restoreSatlSnapshot_native(satlsnapshotID);
        }
        Log.d(TAG, "restoreSatlSnapshot_native called...\n");
        return 0;
    }

    protected static int freeSatlSnapshot_native(int satlsnapshotID) {
        if (!is_emulator()) {
            return TVNative.freeSatlSnapshot_native(satlsnapshotID);
        }
        Log.d(TAG, "freeSatlSnapshot_native called...\n");
        return 0;
    }

    public static String GetDrmRegistrationCode_native() {
        if (!is_emulator()) {
            return TVNative.GetDrmRegistrationCode_native();
        }
        Log.d(TAG, "GetDrmRegistrationCode_native called...\n");
        return new String();
    }

    public static String SetDrmDeactivation_native() {
        if (!is_emulator()) {
            return TVNative.SetDrmDeactivation_native();
        }
        Log.d(TAG, "SetDrmDeactivation_native called...\n");
        return new String();
    }

    public static long GetDrmUiHelpInfo_native() {
        if (!is_emulator()) {
            return TVNative.GetDrmUiHelpInfo_native();
        }
        Log.d(TAG, "GetDrmUiHelpInfo_native called...\n");
        return 0;
    }

    public static boolean GetDivXPlusSupport_native() {
        if (!is_emulator()) {
            return TVNative.GetDivXPlusSupport_native();
        }
        Log.d(TAG, "GetDivXPlusSupport_native called...\n");
        return false;
    }

    public static boolean GetDivXHDSupport_native() {
        if (!is_emulator()) {
            return TVNative.GetDivXHDSupport_native();
        }
        Log.d(TAG, "GetDivXHDSupport_native called...\n");
        return false;
    }

    public static void rpcCloseClient_native() {
        if (is_emulator()) {
            Log.d(TAG, "rpcCloseClient_native called...\n");
            return;
        }
        Log.d(TAG, "TVNative.rpcCloseClient_nativeinvoked");
        TVNative.rpcCloseClient_native();
    }

    public static int sifWriteMultipleSubAddr(int port, int clock, byte deviceAddr, byte[] address, byte[] data) {
        if (!is_emulator()) {
            return TVNative.sifWriteMultipleSubAddr_native(port, clock, deviceAddr, address, data);
        }
        Log.d(TAG, "sifWriteMultipleSubAddr called...\n");
        return 0;
    }

    public static int sifReadMultipleSubAddr(int port, int clock, byte deviceAddr, byte[] address, byte[] data) {
        if (!is_emulator()) {
            return TVNative.sifReadMultipleSubAddr_native(port, clock, deviceAddr, address, data);
        }
        Log.d(TAG, "sifReadMultipleSubAddr called...\n");
        return 0;
    }

    public static int setSystemProperties_native(String key, String val) {
        if (!is_emulator()) {
            return TVNative.setSystemProperties_native(key, val);
        }
        Log.d(TAG, "setSystemProperties_native called...\n");
        return 0;
    }

    public static String getFileBasePath_native() {
        if (!is_emulator()) {
            return TVNative.getFileBasePath_native();
        }
        Log.d(TAG, "getFileBasePath_native called...\n");
        return new String();
    }

    protected static MtkTvParserIniInfoBase getIntConfigData_native(String filePath, String keyName) {
        if (!is_emulator()) {
            return TVNative.getIntConfigData_native(filePath, keyName);
        }
        Log.d(TAG, "getIntConfigData_native called...\n");
        return null;
    }

    protected static MtkTvParserIniInfoBase getStringConfigData_native(String filePath, String keyName) {
        if (!is_emulator()) {
            return TVNative.getStringConfigData_native(filePath, keyName);
        }
        Log.d(TAG, "getStringConfigData_native called...\n");
        return null;
    }

    protected static int htmlAgentExchangeData_native(int[] data) {
        if (!is_emulator()) {
            return TVNative.htmlAgentExchangeData_native(data);
        }
        Log.d(TAG, "htmlAgentExchangeData_native called...\n");
        return 0;
    }
}
