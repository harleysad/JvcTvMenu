package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvGingaAppInfoBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvGingaBase {
    public static final String TAG = "TV_MtkTvGingaBase";

    public int startApplication(String sAppID) {
        Log.d(TAG, "startApplication\n");
        return TVNativeWrapper.startApplication_native(sAppID);
    }

    public int stopApplication(String sAppID) {
        Log.d(TAG, "stopApplication\n");
        return TVNativeWrapper.stopApplication_native(sAppID);
    }

    public int getApplicationInfo() {
        Log.d(TAG, "getApplicationInfo\n");
        return TVNativeWrapper.getApplicationInfo_native();
    }

    public int startGinga() {
        Log.d(TAG, "startGinga\n");
        return TVNativeWrapper.startGinga_native();
    }

    public int stopGinga() {
        Log.d(TAG, "stopGinga\n");
        return TVNativeWrapper.stopGinga_native();
    }

    public int warningStartCC(boolean b_start_cc) {
        Log.d(TAG, "warningStartCC\n");
        return TVNativeWrapper.warningStartCC_native(b_start_cc);
    }

    public int warningStartGingaApp(boolean b_start_ginga_app) {
        Log.d(TAG, "warningStartGingaApp\n");
        return TVNativeWrapper.warningStartGingaApp_native(b_start_ginga_app);
    }

    public List<MtkTvGingaAppInfoBase> getApplicationInfoList() {
        List<MtkTvGingaAppInfoBase> gingaAppList = new ArrayList<>();
        Log.d(TAG, "getApplicationInfoList begin");
        int ret = TVNativeWrapper.getApplicationInfoList_native(gingaAppList);
        Log.d(TAG, "getApplicationInfoList: count=" + gingaAppList.size());
        for (int i = 0; i < gingaAppList.size(); i++) {
            Log.d(TAG, " getApplicationInfoList: AppId=" + gingaAppList.get(i).getAppId() + ",AppName=" + gingaAppList.get(i).getAppName() + ",IsRuning=" + gingaAppList.get(i).isRunning());
        }
        Log.d(TAG, "- getApplicationInfoList. ret=" + ret);
        return gingaAppList;
    }

    public boolean isGingaWindowResize() {
        Log.d(TAG, "isGingaWindowResize\n");
        return TVNativeWrapper.getGingaScreenModeisEnable_native();
    }
}
