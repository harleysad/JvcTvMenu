package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvRectangle;
import com.mediatek.twoworlds.tv.model.MtkTvRegionCapability;
import com.mediatek.twoworlds.tv.model.MtkTvSrcVideoResolution;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import java.util.List;

public class MtkTvUtilBase {
    private static final String PERSIST_KEY = "sys.mtk.keyDisable";
    public static final int PROP_VALUE_MAX = 91;
    public static final String TAG = "MtkTvUtil";
    private MtkTvRegionCapability disRegionCapability = new MtkTvRegionCapability();
    private MtkTvRectangle dispRctangle = new MtkTvRectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private MtkTvRectangle sourceRectangle = new MtkTvRectangle(0.0f, 0.0f, 0.0f, 0.0f);
    private MtkTvSrcVideoResolution srcVideoResolution = new MtkTvSrcVideoResolution();
    private MtkTvRegionCapability videoSrcRegionCapability = new MtkTvRegionCapability();

    public MtkTvUtilBase() {
        Log.d(TAG, "MtkTvUtil object created");
    }

    public static int getWakeUpReason() {
        int i = TVNativeWrapper.getWakeUpReason_native();
        Log.d(TAG, "getWakeUpReason = " + i);
        return i;
    }

    public static int getWakeUpIrKey() {
        int i = TVNativeWrapper.getWakeUpIrKey_native();
        Log.d(TAG, "getWakeUpIrKey = " + i);
        return i;
    }

    public static boolean checkPCLWakeupReasonToBGM() {
        boolean b = TVNativeWrapper.checkPCLWakeupReasonToBGM_native();
        Log.d(TAG, "checkPCLWakeupReasonToBGM = " + b);
        return b;
    }

    public static int getPclWakeupSetup() {
        int setup = TVNativeWrapper.getPclWakeupSetup_native();
        Log.d(TAG, "getPclWakeupSetup, setup=" + setup);
        return setup;
    }

    public static int setPclWakeupSetup(int setup) {
        int ret = TVNativeWrapper.setPclWakeupSetup_native(setup);
        Log.d(TAG, "setPclWakeupSetup, setup=" + setup + ", ret=" + ret);
        return setup;
    }

    public static void saveTimeStamp(String s) {
        TVNativeWrapper.saveTimeStamp_native(s);
    }

    public static int setKeyRouteSwitch(boolean isKeyToAndroid) {
        return setKeyRouteSwitchFromTVRemoteService(isKeyToAndroid);
    }

    public static int setKeyRouteSwitchFromTVRemoteService(boolean isKeyToAndroid) {
        Log.d(TAG, "setKeyRouteSwitchFromTVRemoteService " + isKeyToAndroid);
        if (isKeyToAndroid) {
            setSystemProperties(PERSIST_KEY, "0");
            TVNativeWrapper.setLinuxKeyReceive_native(0);
        } else {
            setSystemProperties(PERSIST_KEY, "1");
            TVNativeWrapper.setLinuxKeyReceive_native(1);
        }
        return 0;
    }

    public static boolean isBGMapplicable(boolean isHintStandByToBGM) {
        boolean b = TVNativeWrapper.isBGMapplicable_native(isHintStandByToBGM);
        Log.d(TAG, "isBGMapplicable = " + b);
        return b;
    }

    public static int setLinuxBGMStart() {
        int i = TVNativeWrapper.setLinuxBGMStart_native();
        Log.d(TAG, "setLinuxBGMStart = " + i);
        return i;
    }

    public static int setWakeUpReason(int mReason) {
        int i = TVNativeWrapper.HighLevel_native(300, mReason, 0, 0, 0, 0, 0);
        Log.d(TAG, "setWakeUpReason = " + i);
        return i;
    }

    public static int getBroadcastMwPowerState() {
        int i = TVNativeWrapper.HighLevel_native(MessageType.MENU_TV_RF_SCAN_REFRESH, 0, 0, 0, 0, 0, 0);
        Log.d(TAG, "getBroadcastMwPowerState = " + i);
        return i;
    }

    public int setScreenOutputDispRect(String path, MtkTvRectangle rect) {
        return TVNativeWrapper.setScreenOutputDispRect_native(path, rect);
    }

    public MtkTvRectangle getScreenOutputDispRect(String path) {
        TVNativeWrapper.getScreenOutputDispRect_native(path, this.dispRctangle);
        return this.dispRctangle;
    }

    public int setScreenSourceRect(String path, MtkTvRectangle rect) {
        return TVNativeWrapper.setScreenSourceRect_native(path, rect);
    }

    public MtkTvRectangle getScreenSourceRect(String path) {
        TVNativeWrapper.getScreenSourceRect_native(path, this.sourceRectangle);
        return this.sourceRectangle;
    }

    public MtkTvRegionCapability getVideoSrcRegionCapability(String Path) {
        TVNativeWrapper.getVideoSrcRegionCapability_native(Path, this.videoSrcRegionCapability);
        return this.videoSrcRegionCapability;
    }

    public MtkTvSrcVideoResolution getSrcVideoResolution(String Path) {
        TVNativeWrapper.getSrcVideoResolution_native(Path, this.srcVideoResolution);
        Log.d(TAG, " getSrcVideoResolution Here path:" + Path + " return " + this.srcVideoResolution.toString());
        return this.srcVideoResolution;
    }

    public MtkTvRegionCapability getDispRegionCapability(String Path) {
        TVNativeWrapper.getDispRegionCapability_native(Path, this.disRegionCapability);
        return this.disRegionCapability;
    }

    public int nativeAppPause(int appid) {
        return appid;
    }

    public int nativeAppResume(int appid) {
        return appid;
    }

    public void resetPub() {
        Log.d(TAG, "resetPub entered");
        TVNativeWrapper.resetPub_native();
        Log.d(TAG, "resetPub exit");
    }

    public void resetPri() {
        Log.d(TAG, "resetPri entered");
        TVNativeWrapper.resetPri_native();
        Log.d(TAG, "resetPri exit");
    }

    public void resetFac() {
        Log.d(TAG, "resetFac entered");
        TVNativeWrapper.resetFac_native();
        Log.d(TAG, "resetFac exit");
    }

    public String getSysVersion(int eType, String sVersion) {
        Log.d(TAG, "getSysVersion entered");
        String s_str = TVNativeWrapper.getSysVersion_native(eType, sVersion);
        Log.d(TAG, "getSysVersion EXIT: version:" + s_str);
        return s_str;
    }

    public boolean isCaptureLogo() {
        Log.d(TAG, "isCaptureLogo entered");
        return TVNativeWrapper.isCaptureLogo_native();
    }

    public int tunerFacQuery(boolean isAnalog, List<String> displayName, List<String> displayValue) {
        Log.d(TAG, "tunerFacQuery");
        return TVNativeWrapper.tunerFacQuery_navtive(isAnalog, displayName, displayValue);
    }

    public static boolean getAdbStatus() {
        boolean b = TVNativeWrapper.getAdbStatus_native();
        Log.d(TAG, "getAdbStatus = " + b);
        return b;
    }

    public static int setAdbStatus(boolean isAdbEnabled) {
        int ret = TVNativeWrapper.setAdbStatus_native(isAdbEnabled);
        Log.d(TAG, "setAdbStatus, isAdbEnabled = " + isAdbEnabled + ", ret = " + ret);
        return ret;
    }

    public static int setOSDPlaneEnable(int mPlane, int mEnable) {
        return TVNativeWrapper.HighLevel_native(112, mPlane, mEnable, 0, 0, 0, 0);
    }

    public static void rpcCloseClient() {
        Log.d(TAG, "mtktvuitlbase.javarpcCloseClient invoked");
        TVNativeWrapper.rpcCloseClient_native();
    }

    public static int sifInit() {
        return TVNativeWrapper.HighLevel_native(115, 0, 0, 0, 0, 0, 0);
    }

    public static int sifStop() {
        return TVNativeWrapper.HighLevel_native(115, 1, 0, 0, 0, 0, 0);
    }

    public static int sifWriteMultipleSubAddr(int port, int clock, byte deviceAddr, byte[] address, byte[] data) {
        return TVNativeWrapper.sifWriteMultipleSubAddr(port, clock, deviceAddr, address, data);
    }

    public static int sifReadMultipleSubAddr(int port, int clock, byte deviceAddr, byte[] address, byte[] data) {
        return TVNativeWrapper.sifReadMultipleSubAddr(port, clock, deviceAddr, address, data);
    }

    public static int setLocalDimming(boolean enabled) {
        return TVNativeWrapper.HighLevel_native(116, enabled ? 1 : 0, 0, 0, 0, 0, 0);
    }

    public static boolean setOpacity(int opacity) {
        return TVNativeWrapper.setOSDOpacity_native(opacity);
    }

    public static int setSystemProperties(String key, String val) {
        if (val == null || val.length() <= 91) {
            return TVNativeWrapper.setSystemProperties_native(key, val);
        }
        return -1;
    }
}
