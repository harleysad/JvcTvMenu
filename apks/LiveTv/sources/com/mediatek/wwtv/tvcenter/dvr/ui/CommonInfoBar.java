package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Activity;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class CommonInfoBar extends BaseInfoBar {
    private TextView mInfo;
    private String mToastString = "";

    public CommonInfoBar(Activity context) {
        super(context, R.layout.pvr_timeshfit_nf);
    }

    public CommonInfoBar(Activity context, String info) {
        super(context, R.layout.pvr_timeshfit_nf);
        this.mToastString = info;
    }

    public CommonInfoBar(Activity context, int layoutID, Long duration, String strInfo) {
        super(context, layoutID);
        this.mDefaultDuration = duration;
        this.mToastString = strInfo;
    }

    public void setInfo(String info) {
        this.mToastString = info;
    }

    public void initView() {
        super.initView();
        this.mInfo = (TextView) getContentView().findViewById(R.id.info);
        this.mInfo.setText(this.mToastString);
    }

    public void doSomething() {
        super.doSomething();
    }

    public void show() {
        initView();
        super.show();
    }
}
