package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class ChannelScan extends FVPIntentBasic {
    public String getRequest() {
        return "FVP_CHN_SCN";
    }

    public String[] getMode() {
        return new String[]{"SCN"};
    }

    public Intent getLaunchedIntent(Context context, Intent intent) {
        String request = getRequest();
        Log.d(request, "intent:" + intent);
        return null;
    }
}
