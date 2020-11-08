package com.mediatek.wwtv.tvcenter.commonview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;

public class BaseActivity extends Activity {
    public static final int AUTO_SLEEP_DELAY = 3;
    public static final int AUTO_SLEEP_GO = 1;
    public static final int AUTO_SLEEP_REFRESH = 2;
    AlertDialog.Builder builder = null;
    Dialog dialog = null;
    Handler handlers = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                BaseActivity.this.builder = new AlertDialog.Builder(BaseActivity.this.getApplicationContext());
                BaseActivity.this.builder.setTitle(R.string.menu_string_power_tips);
                BaseActivity.this.builder.setMessage(R.string.menu_string_power_content);
                BaseActivity.this.builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BaseActivity.this.setMessage();
                    }
                });
                BaseActivity.this.dialog = BaseActivity.this.builder.create();
                BaseActivity.this.dialog.getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
                if (!BaseActivity.this.dialog.isShowing()) {
                    BaseActivity.this.dialog.show();
                    BaseActivity.this.setTimeToPower();
                }
            }
            if (msg.what == 2) {
                BaseActivity.this.setMessage();
            }
            if (msg.what == 3) {
                if (BaseActivity.this.dialog.isShowing()) {
                    BaseActivity.this.dialog.dismiss();
                }
                InstrumentationHandler.getInstance().sendKeyDownUpSync(26);
            }
            super.handleMessage(msg);
        }
    };

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        setMessage();
        setAutoValue();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        DestroyApp.setActivityActiveStatus(true);
        DestroyApp.setRunningActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.handlers.removeMessages(1);
        DestroyApp.setActivityActiveStatus(false);
        DestroyApp.setRunningActivity((Activity) null);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("Base", "KeyEvent=" + event);
        setMessage();
        return super.dispatchKeyEvent(event);
    }

    public void setMessage() {
        if (DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().getValueAutoSleep() != 1) {
            Log.d("Base", "new Message");
        }
    }

    public void setTimeToPower() {
        int leftmin = Integer.valueOf(getResources().getString(R.string.menu_string_power_left_minite)).intValue();
        if (DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().getValueAutoSleep() != 1) {
            this.handlers.sendEmptyMessageDelayed(3, (long) (leftmin * 60 * 1000));
        }
    }

    public void setAutoValue() {
        if ((DataSeparaterUtil.getInstance() != null ? DataSeparaterUtil.getInstance().getValueAutoSleep() : -1) != 1) {
            PreferenceUtil.getInstance(this).mConfigManager.getDefaultPowerSetting(this);
        } else {
            PreferenceUtil.getInstance(this).mConfigManager.setAutoSleepValue(1);
        }
    }

    public Handler getHandlers() {
        return this.handlers;
    }
}
