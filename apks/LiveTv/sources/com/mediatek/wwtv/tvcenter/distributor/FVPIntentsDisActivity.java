package com.mediatek.wwtv.tvcenter.distributor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FVPIntentsDisActivity extends Activity {
    private FVPIntentFactory mFVPIntentFactory = new FVPIntentFactory();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentHnadler(getIntent());
        finish();
    }

    private void intentHnadler(Intent intent) {
        if (intent != null) {
            try {
                Log.e("Type", intent.getType() + "");
                Log.e("Action", intent.getAction() + "");
                Log.e("Package", intent.getPackage() + "");
                Log.e("senderPackage", intent.getStringExtra("senderPackage") + "");
                Log.e("requestingPackage", intent.getStringExtra("requestingPackage") + "");
                Log.e("Request", intent.getStringExtra("Request") + "");
                Log.e("Mode", intent.getStringExtra("Mode") + "");
                Log.e(FVPIntentBasic.FVP_DETAIL, intent.getStringExtra(FVPIntentBasic.FVP_DETAIL) + "");
                Log.e(FVPIntentBasic.FVP_CALLER, intent.getStringExtra(FVPIntentBasic.FVP_CALLER) + "");
                Intent in = this.mFVPIntentFactory.getIntent(this, intent);
                if (in != null) {
                    startActivity(in);
                }
            } catch (Exception e) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
