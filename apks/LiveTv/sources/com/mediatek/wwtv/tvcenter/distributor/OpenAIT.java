package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class OpenAIT extends FVPIntentBasic {
    public String getRequest() {
        return "FVP_AIT_URL";
    }

    public String[] getMode() {
        return new String[]{"PLY", "CNT", "RST", "SRV", "IPS"};
    }

    public Intent getLaunchedIntent(Context context, Intent intent) {
        String request = getRequest();
        Log.d(request, "intent:" + intent);
        return null;
    }
}
