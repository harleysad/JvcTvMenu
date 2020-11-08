package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class OpenFVPArea extends FVPIntentBasic {
    public String getRequest() {
        return "FVP_MNR_ARE";
    }

    public String[] getMode() {
        return new String[]{"UIB", "RCU"};
    }

    public Intent getLaunchedIntent(Context context, Intent intent) {
        String request = getRequest();
        Log.d(request, "intent:" + intent);
        return null;
    }
}
