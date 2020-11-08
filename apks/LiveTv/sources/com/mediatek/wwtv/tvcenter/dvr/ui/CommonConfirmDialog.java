package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.manager.Util;

public class CommonConfirmDialog extends CommonDialog {
    private float hScale = 0.35f;
    public StateBase mState;
    private TextView mTV1;
    private TextView mTV2;
    private Button negativeBtn;
    private Button positiveBtn;
    private float wScale = 0.3f;

    public CommonConfirmDialog(Context context, StateBase state) {
        super(context, R.layout.pvr_tshift_confirmdialog);
        getWindow().setLayout((int) (((float) Util.getTVWidth()) * this.wScale), (int) (((float) Util.getTVHeight()) * this.hScale));
        this.mState = state;
        setCancelable(true);
        initView2();
    }

    private void initView2() {
        this.mTV1 = (TextView) findViewById(R.id.diskop_title_line1);
        this.mTV1.setText("");
        this.mTV2 = (TextView) findViewById(R.id.diskop_title_line2);
        this.mTV2.setText("");
        this.positiveBtn = (Button) findViewById(R.id.confirm_btn_yes);
        this.negativeBtn = (Button) findViewById(R.id.confirm_btn_no);
    }

    public void setPositiveButton(View.OnClickListener listener) {
        this.positiveBtn.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        this.negativeBtn.setOnClickListener(listener);
    }

    public void setTitle(String line1, String line2) {
        this.mTV1.setText(line1);
        this.mTV2.setText(line2);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 23) {
            onClick(getCurrentFocus());
        }
        return super.onKeyDown(keyCode, event);
    }
}
