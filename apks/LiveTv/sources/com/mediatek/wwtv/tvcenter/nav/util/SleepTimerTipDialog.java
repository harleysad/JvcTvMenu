package com.mediatek.wwtv.tvcenter.nav.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class SleepTimerTipDialog extends Dialog {
    private Context context;
    String itemId;

    public SleepTimerTipDialog(Context context2) {
        super(context2, 2131755414);
    }

    public SleepTimerTipDialog(Context context2, String itemId2) {
        super(context2, 2131755414);
        this.itemId = itemId2;
        MtkLog.d("SleepTimerTipDialog", "hi sleep dialog init");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_sleep_tip_dialog);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        dismiss();
        MtkLog.d("SleepTimerTipDialog", "hi dialog dispatchKeyEvent");
        SleepTimerTask.doCancelTask(this.itemId);
        if (isShowing()) {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    public void setPositon(int xoff, int yoff) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = (ScreenConstant.SCREEN_WIDTH * xoff) / 1280;
        lp.y = (ScreenConstant.SCREEN_HEIGHT * yoff) / 720;
        MtkLog.d("SleepTimerTipDialog", "sleep position ==" + lp.x + "," + lp.y);
        window.setAttributes(lp);
    }
}
