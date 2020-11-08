package com.android.tv.settings.accessories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.R;

public class BluetoothDevicePickerActivity extends Activity {
    private static final boolean DEBUG = false;
    public static final String TAG = "BtDevicePickerActivity";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Log.d(TAG, "Bluetooth sharing not supported on this device, ignoring request. Intent = " + intent);
        Toast.makeText(this, getString(R.string.error_action_not_supported), 0).show();
        finish();
    }
}
