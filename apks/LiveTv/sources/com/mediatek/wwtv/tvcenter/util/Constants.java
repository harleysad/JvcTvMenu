package com.mediatek.wwtv.tvcenter.util;

import android.util.Log;
import java.util.HashSet;

public final class Constants {
    public static final String ACTION_PREPARE_SHUTDOWN = "android.intent.action.ACTION_PREPARE_SHUTDOWN";
    public static final HashSet<Integer> BLACKLIST_KEYCODE_TO_TIS = new HashSet<>();
    public static final int INPUT_TYPE_ATV = 13;
    public static final int INPUT_TYPE_COMPONENT = 15;
    public static final int INPUT_TYPE_COMPOSITE = 14;
    public static final int INPUT_TYPE_DTV = 12;
    public static final int INPUT_TYPE_HDMI = 17;
    public static final int INPUT_TYPE_OTHER = 10;
    public static final int INPUT_TYPE_TV = 11;
    public static final int INPUT_TYPE_VGA = 16;
    public static final String MTK_3RD_APP_FLAG = "vendor.mtk.3rd.flag";
    public static final String MTK_ACTION_EXIT_TVSETTINGSPLUS = "mtk.intent.action.exit.android.setting";
    public static final int TUNERMODE_DVBC = 1;
    public static final int TUNERMODE_DVBS = 2;
    public static final int TUNERMODE_DVBT = 0;
    public static int slot_id = 0;

    static {
        BLACKLIST_KEYCODE_TO_TIS.add(84);
        BLACKLIST_KEYCODE_TO_TIS.add(130);
        BLACKLIST_KEYCODE_TO_TIS.add(Integer.valueOf(KeyMap.KEYCODE_MTKIR_PIPPOP));
        BLACKLIST_KEYCODE_TO_TIS.add(10471);
        BLACKLIST_KEYCODE_TO_TIS.add(10066);
        BLACKLIST_KEYCODE_TO_TIS.add(Integer.valueOf(KeyMap.KEYCODE_MTKIR_SLEEP));
        BLACKLIST_KEYCODE_TO_TIS.add(255);
        BLACKLIST_KEYCODE_TO_TIS.add(251);
        BLACKLIST_KEYCODE_TO_TIS.add(222);
        BLACKLIST_KEYCODE_TO_TIS.add(10061);
        BLACKLIST_KEYCODE_TO_TIS.add(82);
        BLACKLIST_KEYCODE_TO_TIS.add(Integer.valueOf(KeyMap.KEYCODE_MTKIR_INFO));
        if (DataSeparaterUtil.getInstance().isCHUPDOWNACTIONSupport()) {
            Log.d("Constants", "support ch+/-, not disaptch to cec");
            BLACKLIST_KEYCODE_TO_TIS.add(Integer.valueOf(KeyMap.KEYCODE_MTKIR_CHUP));
            BLACKLIST_KEYCODE_TO_TIS.add(Integer.valueOf(KeyMap.KEYCODE_MTKIR_CHDN));
        }
    }
}
