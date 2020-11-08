package com.mediatek.wwtv.setting.base.scan.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class BaseCustomActivity extends BaseActivity {
    private static final String TAG = "BaseCustomActivity";
    long ENTER_DELAYMILLS = 500;
    long enterPressTime = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        lp.width = (int) (((double) outMetrics.widthPixels) * 0.89d);
        lp.height = (int) (((double) outMetrics.heightPixels) * 0.94d);
        getWindow().setAttributes(lp);
        ((DestroyApp) getApplication()).add(this);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && (event.getKeyCode() == 66 || event.getKeyCode() == 23)) {
            long nowMills = SystemClock.uptimeMillis();
            if (nowMills - this.enterPressTime <= this.ENTER_DELAYMILLS) {
                return true;
            }
            this.enterPressTime = nowMills;
        }
        return super.dispatchKeyEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        ((DestroyApp) getApplication()).remove(this);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        MtkLog.d(TAG, "custom-activity now onRestart");
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(TAG, "custom-activity now onStop");
        super.onStop();
    }
}
