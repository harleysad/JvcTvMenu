package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class FirstTimeInstall extends FVPIntentBasic {
    public String getRequest() {
        return "FVP_APP_INS";
    }

    public String[] getMode() {
        return new String[]{"AOK"};
    }

    public Intent getLaunchedIntent(Context context, Intent intent) {
        String request = getRequest();
        Log.d(request, "intent:" + intent);
        return null;
    }
}
