package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;

public class NetUpdateGider implements Runnable {
    private TurnkeyCommDialog dissMissDiag;
    private LinearLayout layout;
    private TurnkeyCommDialog showDialog;
    private String type = "";

    public NetUpdateGider(Context context) {
    }

    public NetUpdateGider(Context context, TurnkeyCommDialog t) {
        this.dissMissDiag = t;
        this.showDialog = new TurnkeyCommDialog(context, 4);
    }

    public NetUpdateGider(Context context, TurnkeyCommDialog t, String type2, LinearLayout layout2) {
        this.dissMissDiag = t;
        this.type = type2;
        this.layout = layout2;
    }

    public void run() {
        this.dissMissDiag.dismiss();
        if (this.type.equals("numRepeatDialog")) {
            this.layout.setVisibility(0);
        } else {
            this.showDialog.show();
        }
    }

    public TurnkeyCommDialog getShowDialog() {
        return this.showDialog;
    }

    public void setShowDialog(TurnkeyCommDialog showDialog2) {
        this.showDialog = showDialog2;
    }

    public TurnkeyCommDialog getDissMissDiag() {
        return this.dissMissDiag;
    }

    public void setDissMissDiag(TurnkeyCommDialog dissMissDiag2) {
        this.dissMissDiag = dissMissDiag2;
    }
}
