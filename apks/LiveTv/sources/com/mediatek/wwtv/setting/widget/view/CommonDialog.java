package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;

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

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("Base", "KeyEvent=" + event);
        if (!(DataSeparaterUtil.getInstance() == null || DataSeparaterUtil.getInstance().getValueAutoSleep() == 1)) {
            TurnkeyUiMainActivity.getInstance().getHandlers().sendEmptyMessage(2);
        }
        return super.dispatchKeyEvent(event);
    }
}
