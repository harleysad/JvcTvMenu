package com.android.tv.settings.device.privacy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.tv.settings.device.storage.ResetActivity;

public class PrivacyActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ResetActivity.class));
        finish();
    }
}
