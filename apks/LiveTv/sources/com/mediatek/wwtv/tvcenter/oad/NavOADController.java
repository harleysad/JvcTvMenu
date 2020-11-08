package com.mediatek.wwtv.tvcenter.oad;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvOAD;

public class NavOADController {
    private NavOADActivity activity;
    private NavOADCallback callBack;

    public NavOADController(NavOADActivity activity2) {
        this.activity = activity2;
        registerCallback(activity2);
    }

    public NavOADActivity getActivity() {
        return this.activity;
    }

    public void setActivity(NavOADActivity activity2) {
        this.activity = activity2;
    }

    public void registerCallback(NavOADActivity activity2) {
        this.callBack = NavOADCallback.getInstance(activity2);
    }

    public void unRegisterCallback() {
        this.callBack.removeListener();
    }

    public int manualDetect() {
        return MtkTvOAD.getInstance().startManualDetect();
    }

    public int cancelManualDetect() {
        return MtkTvOAD.getInstance().stopManualDetect();
    }

    public int acceptDownload() {
        return MtkTvOAD.getInstance().startDownload();
    }

    public String getScheduleInfo() {
        MtkTvOAD.getInstance().getScheduleInfo();
        MtkTvOAD.getInstance();
        return MtkTvOAD.mscheduleInfo;
    }

    public int acceptScheduleOAD() {
        return MtkTvOAD.getInstance().acceptSchedule();
    }

    public int acceptJumpChannel() {
        return MtkTvOAD.getInstance().startJumpChannel();
    }

    public int cancelDownload() {
        return MtkTvOAD.getInstance().stopDownload();
    }

    public int acceptFlash() {
        return MtkTvOAD.getInstance().startFlash();
    }

    public int acceptRestart() {
        return MtkTvOAD.getInstance().acceptRestart();
    }

    public int remindMeLater() {
        return MtkTvOAD.getInstance().remindMeLater();
    }

    public int setOADAutoDownload(boolean auto) {
        return MtkTvOAD.getInstance().setAutoDownload(auto);
    }

    public static void setOADAutoDownload(Context context, boolean enableAutoDownload) {
        MtkTvConfig.getInstance().setConfigValue("g_oad__oad_sel_options_auto_download", enableAutoDownload);
    }

    public static boolean getOADAutoDownload(Context context) {
        return MtkTvConfig.getInstance().getConfigValue("g_oad__oad_sel_options_auto_download") != 0;
    }

    public int clearOadVersion() {
        return MtkTvOAD.getInstance().clearOadVersion();
    }
}
