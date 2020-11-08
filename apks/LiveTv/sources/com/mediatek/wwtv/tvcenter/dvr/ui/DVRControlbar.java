package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.RelativeLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;

@SuppressLint({"ViewConstructor"})
public class DVRControlbar extends BaseInfoBar {
    private static float hScale = 0.23f;
    private static float wScale = 0.5f;
    StateBase state;

    public DVRControlbar(Activity context) {
        super(context, R.layout.pvr_timeshfit_nf);
    }

    public DVRControlbar(Activity context, int layoutID, Long duration, StateBase state2) {
        super(context, layoutID, duration, (int) (((float) state2.getManager().getTVWidth()) * wScale), (int) (((float) state2.getManager().getTVHeight()) * hScale));
        this.state = state2;
    }

    public void doSomething() {
        super.doSomething();
    }

    public void show() {
        super.show();
    }

    public void setLocation() {
        try {
            showAtLocation((RelativeLayout) TurnkeyUiMainActivity.getInstance().findViewById(R.id.linear_glview), 81, 0, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
