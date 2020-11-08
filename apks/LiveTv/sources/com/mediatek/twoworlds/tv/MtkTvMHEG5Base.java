package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvMHEG5PfgBase;

public class MtkTvMHEG5Base {
    public static final String TAG = "MtkTvMHEG5Base";
    public static boolean bInternalScrn;
    public static MtkTvMHEG5PfgBase pfg = new MtkTvMHEG5PfgBase();

    public MtkTvMHEG5Base() {
        Log.d(TAG, "MtkTvMHEG5Base object created");
    }

    public void setPfgResult(boolean bRight) {
        Log.d(TAG, "setPfgResult entered, bRight:" + bRight);
        TVNativeWrapper.setPfgResult_native(bRight);
        Log.d(TAG, "setPfgResult exit");
    }

    public void setHbbtvStatus(boolean bSuccess) {
        Log.d(TAG, "setHbbtvStatus entered, bSuccess:" + bSuccess);
        TVNativeWrapper.setHbbtvStatus_native(bSuccess);
        Log.d(TAG, "setHbbtvStatus exit");
    }

    public void setMheg5Enable() {
        Log.d(TAG, "setMheg5Enable entered.");
        TVNativeWrapper.setMheg5Enable_native();
        Log.d(TAG, "setMheg5Enable exit");
    }

    public void setMheg5Disable() {
        Log.d(TAG, "setMheg5Disable entered.");
        TVNativeWrapper.setMheg5Disable_native();
        Log.d(TAG, "setMheg5Disable exit");
    }

    public MtkTvMHEG5PfgBase getPfgInfo() {
        Log.d(TAG, "getPfgString entered");
        TVNativeWrapper.getPfgInfo_native(this);
        Log.i(TAG, "str:" + pfg.pfgString + " show:" + pfg.show);
        Log.d(TAG, "getPfgString exit");
        return pfg;
    }

    public boolean getInternalScrnMode() {
        Log.d(TAG, "getInternalScrnMode entered");
        TVNativeWrapper.getInternalScrnMode_native(this);
        Log.i(TAG, "bInternalScrn:" + bInternalScrn);
        Log.d(TAG, "getInternalScrnMode exit");
        return bInternalScrn;
    }

    public void setPfgString(String str) {
        Log.i(TAG, "setPfgString enterdstr:" + str);
        pfg.pfgString = str;
        Log.i(TAG, "setPfgString exit");
    }

    public void setPfgShow(boolean show) {
        Log.i(TAG, "setPfgShow enterdshow:" + show);
        pfg.show = show;
        Log.i(TAG, "setPfgShow exit");
    }

    public void setInternalScrnMode(boolean bInternal) {
        Log.i(TAG, "setInternalScrnMode enterdbInternal:" + bInternal);
        bInternalScrn = bInternal;
        Log.i(TAG, "setInternalScrnMode exit");
    }

    public void setMheg5Status(int updateType, int argv1, int argv2, int argv3) {
        Log.d(TAG, "setMheg5Status entered.");
        TVNativeWrapper.setMheg5Status_native(updateType, argv1, argv2, argv3);
        Log.d(TAG, "setMheg5Status exit");
    }
}
