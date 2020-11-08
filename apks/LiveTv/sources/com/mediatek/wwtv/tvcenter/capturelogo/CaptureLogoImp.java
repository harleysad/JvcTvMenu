package com.mediatek.wwtv.tvcenter.capturelogo;

import android.content.Context;
import android.graphics.Rect;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.wwtv.tvcenter.capturelogo.TVStorage;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class CaptureLogoImp {
    public static int MMP_IMAGE_LOGO = TVStorage.CAP_LOGO_MM_IMAGE;
    public static int MMP_VIDEO_LOGO = TVStorage.CAP_LOGO_MM_VIDEO;
    private static final String TAG = "CaptureLogoImp";
    public static int TV_LOGO = TVStorage.CAP_LOGO_TV;
    public static CaptureLogoImp capImp;
    private static Rect mArea = new Rect();
    public static Context mContext;
    private static int saveId = 0;
    private static TVStorage tvLogo;

    private CaptureLogoImp(Context context) {
        mContext = context;
        tvLogo = TVStorage.getInstance(context);
    }

    public static synchronized CaptureLogoImp getInstance(Context context) {
        CaptureLogoImp captureLogoImp;
        synchronized (CaptureLogoImp.class) {
            if (capImp == null) {
                capImp = new CaptureLogoImp(context);
            }
            captureLogoImp = capImp;
        }
        return captureLogoImp;
    }

    public void setSavePosition(int logoId) {
        saveId = logoId;
        MtkLog.d(TAG, "----- Save position: " + saveId);
    }

    public void setSpecialArea(Rect r) {
        MtkLog.d(TAG, "----------Special Area Rect------------------");
        if (r != null) {
            MtkLog.d(TAG, "Left: " + r.left + "  Top: " + r.top + "  Width: " + r.width() + "  Height: " + r.height());
        } else {
            MtkLog.d(TAG, "select full screen");
        }
        mArea = r;
    }

    public void setLogoCaptureListener(TVStorage.LogoCaptureListener listener, int sourceType) {
        tvLogo.captureLogo(sourceType, mArea, saveId, listener);
    }

    public void setLogoCaptureListener(TVStorage.LogoCaptureListener listener, int sourceType, int skBitmap, int bufferWidth, int bufferHeight, int bufferPitch, int colorMode) {
        tvLogo.captureLogo(sourceType, mArea, saveId, listener, skBitmap, bufferWidth, bufferHeight, bufferPitch, colorMode);
    }

    public void removeLogoCaptureListener(int sourceType) {
        tvLogo.cancleCaptureLogo(sourceType);
    }

    public void finishLogoCaputer(int sourceType) {
        tvLogo.finishCaptureLogo(sourceType);
    }

    public void freezeScreen(boolean freezed) {
        MtkTvAVMode.getInstance().setFreeze("main", freezed);
    }

    public boolean isFreeze() {
        return MtkTvAVMode.getInstance().isFreeze("main");
    }
}
