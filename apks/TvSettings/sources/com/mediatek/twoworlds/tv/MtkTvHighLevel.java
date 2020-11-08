package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvHighLevel extends MtkTvHighLevelBase {
    private static MtkTvHighLevel mtkTvHighLevel = null;

    public int launch3rdParty(int third_party_id, String contentId) {
        Log.d(MtkTvHighLevelBase.TAG, "launch_3rd_party id=" + third_party_id);
        if (third_party_id <= 0 || third_party_id >= 4) {
            return -1;
        }
        TVNativeWrapper.setConfigString_native("MISC_PREFIX__Content_ID", contentId);
        return launch3rdParty(third_party_id);
    }
}
