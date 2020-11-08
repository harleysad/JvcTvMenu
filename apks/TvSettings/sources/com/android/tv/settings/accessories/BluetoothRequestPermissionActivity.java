package com.android.tv.settings.accessories;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class BluetoothRequestPermissionActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(-1);
        finish();
    }
}
