package com.mediatek.wwtv.tvcenter.distributor;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Iterator;

public final class FVPIntentFactory {
    ArrayList<FVPIntentBasic> mList = new ArrayList<>();

    public FVPIntentFactory() {
        this.mList.add(new AcceptToU());
        this.mList.add(new ChannelScan());
        this.mList.add(new FirstTimeInstall());
        this.mList.add(new OpenAIT());
        this.mList.add(new OpenEPG());
        this.mList.add(new OpenFVPArea());
        this.mList.add(new OpenLiveTV());
    }

    public Intent getIntent(Context context, Intent intent) {
        Iterator<FVPIntentBasic> it = this.mList.iterator();
        while (it.hasNext()) {
            FVPIntentBasic basic = it.next();
            if (basic.isMatched(intent)) {
                return basic.getLaunchedIntent(context, intent);
            }
        }
        return null;
    }
}
