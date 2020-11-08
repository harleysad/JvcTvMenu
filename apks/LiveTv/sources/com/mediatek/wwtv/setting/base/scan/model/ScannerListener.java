package com.mediatek.wwtv.setting.base.scan.model;

public interface ScannerListener {
    public static final int CALL_BACK_TYPE_ATV = 1;
    public static final int CALL_BACK_TYPE_AUTO = 0;
    public static final int CALL_BACK_TYPE_DTV = 2;
    public static final int CALL_BACK_TYPE_DTV_DVBS_BOUQUET_INFO_NFY_FCT = 7;
    public static final int CALL_BACK_TYPE_DTV_DVBS_MDU_DETECT_NFY_FCT = 9;
    public static final int CALL_BACK_TYPE_DTV_DVBS_NEW_SVC_NFY_FCT = 6;
    public static final int CALL_BACK_TYPE_DTV_DVBS_SAT_NAME_NFY_FCT = 4;
    public static final int CALL_BACK_TYPE_DTV_DVBS_SVC_UPDATE_NFY_FCT = 5;
    public static final int CALL_BACK_TYPE_DTV_DVBT_UI_OP = 3;
    public static final int CALL_BACK_TYPE_NUM = 9;
    public static final int COMPLETE_CANCEL = 1;
    public static final int COMPLETE_ERROR = 0;
    public static final int COMPLETE_OK = 2;

    void onCompleted(int i);

    void onDVBSInfoUpdated(int i, String str);

    void onFrequence(int i);

    @Deprecated
    void onProgress(int i, int i2);

    void onProgress(int i, int i2, int i3);
}
