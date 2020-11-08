package com.android.tv;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.util.Log;
import com.android.tv.util.SetupUtils;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.wwtv.tvcenter.commonview.BaseActivity;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class SetupPassthroughActivity extends BaseActivity {
    private static final String ATV_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW1";
    private static final String DTV_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW0";
    private static final int REQUEST_START_SETUP_ACTIVITY = 200;
    private static final String TAG = "SetupPassthroughAct";
    /* access modifiers changed from: private */
    public Intent mActivityAfterCompletion;
    private TvInputInfo mTvInputInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        TvInputManagerHelper inputManager = ((DestroyApp) getApplicationContext()).getTvInputManagerHelper();
        String inputId = intent.getStringExtra("android.media.tv.extra.INPUT_ID");
        this.mTvInputInfo = inputManager.getTvInputInfo(inputId);
        MtkLog.d(TAG, "TvInputId " + inputId + " / TvInputInfo " + this.mTvInputInfo);
        StringBuilder sb = new StringBuilder();
        sb.append("intent:");
        sb.append(intent);
        MtkLog.d(TAG, sb.toString());
        if (this.mTvInputInfo == null) {
            Log.w(TAG, "There is no input with the ID " + inputId + ".");
            finish();
            return;
        }
        Intent setupIntent = (Intent) intent.getExtras().getParcelable("com.android.tv.extra.SETUP_INTENT");
        MtkLog.d(TAG, "Setup activity launch intent: " + setupIntent);
        if (setupIntent == null) {
            MtkLog.w(TAG, "The input (" + this.mTvInputInfo.getId() + ") doesn't have setup.");
            finish();
            return;
        }
        SetupUtils.grantEpgPermission(this, this.mTvInputInfo.getServiceInfo().packageName);
        this.mActivityAfterCompletion = (Intent) intent.getParcelableExtra("com.android.tv.intent.extra.ACTIVITY_AFTER_COMPLETION");
        MtkLog.d(TAG, "Activity after completion " + this.mActivityAfterCompletion);
        Bundle extras = intent.getExtras();
        extras.remove("com.android.tv.extra.SETUP_INTENT");
        setupIntent.putExtras(extras);
        int type = -1;
        try {
            if ("com.mediatek.tvinput/.tuner.TunerInputService/HW0".equals(inputId)) {
                type = 0;
            } else if (inputId.startsWith("com.mediatek.tvinput/.tuner.TunerInputService")) {
                type = 1;
            }
            setupIntent.putExtra("setup_source_scan_type", type);
            startActivityForResult(setupIntent, 200);
        } catch (ActivityNotFoundException e) {
            MtkLog.e(TAG, "Can't find activity: " + setupIntent.getComponent());
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        MtkLog.v(TAG, "onActivityResult, requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (!(requestCode == 200 && resultCode == -1)) {
            setResult(resultCode, data);
            finish();
            return;
        }
        DestroyApp.setActivityActiveStatus(true);
        SetupUtils.createForTvSingletons(this).onTvInputSetupFinished(this.mTvInputInfo.getId(), new Runnable() {
            public void run() {
                if (SetupPassthroughActivity.this.mActivityAfterCompletion != null) {
                    try {
                        SetupPassthroughActivity.this.startActivity(SetupPassthroughActivity.this.mActivityAfterCompletion);
                    } catch (ActivityNotFoundException e) {
                        MtkLog.w(SetupPassthroughActivity.TAG, "Activity launch failed", e);
                    }
                }
                SetupPassthroughActivity.this.setResult(resultCode, data);
                SetupPassthroughActivity.this.finish();
            }
        });
    }
}
