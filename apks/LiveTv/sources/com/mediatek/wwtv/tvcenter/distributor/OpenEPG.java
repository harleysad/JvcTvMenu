package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class OpenEPG extends FVPIntentBasic {
    public String getRequest() {
        return "FVP_EPG_REQ";
    }

    public String[] getMode() {
        return new String[]{"LCN", "LST", "FST"};
    }

    public Intent getLaunchedIntent(Context context, Intent intent) {
        String request = getRequest();
        Log.d(request, "intent:" + intent);
        return null;
    }
}
