package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.mediatek.wwtv.tvcenter.R;

public class CommonDialog extends Dialog implements View.OnClickListener {
    protected Context mContext;

    protected CommonDialog(Context context, int layoutID) {
        super(context, R.style.MTK_Dialog_bg);
        this.mContext = context;
        setContentView(layoutID);
        initView();
    }

    public void initView() {
    }

    public void onClick(View v) {
    }

    public void dismiss() {
        super.dismiss();
    }
}
