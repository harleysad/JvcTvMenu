package com.android.tv.settings.device.display.daydream;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import com.android.settingslib.dream.DreamBackend;

public class DaydreamVoiceAction extends Activity {
    private static final String SLEEP_ACTION = "com.google.android.pano.action.SLEEP";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        setContentView(view);
        if (getIntent().getAction().equals(SLEEP_ACTION)) {
            new DreamBackend(this).startDreaming();
        }
        finish();
    }
}
