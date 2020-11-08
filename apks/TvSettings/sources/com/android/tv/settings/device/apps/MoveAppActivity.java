package com.android.tv.settings.device.apps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.device.storage.MoveAppProgressFragment;
import com.android.tv.settings.device.storage.MoveAppStepFragment;

public class MoveAppActivity extends Activity implements MoveAppStepFragment.Callback {
    private static final String ARG_PACKAGE_DESC = "packageDesc";
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String SAVE_STATE_MOVE_ID = "MoveAppActivity.moveId";
    private static final String TAG = "MoveAppActivity";
    /* access modifiers changed from: private */
    public int mAppMoveId = -1;
    private final PackageManager.MoveCallback mMoveCallback = new PackageManager.MoveCallback() {
        public void onStatusChanged(int moveId, int status, long estMillis) {
            if (moveId == MoveAppActivity.this.mAppMoveId && PackageManager.isMoveStatusFinished(status)) {
                MoveAppActivity.this.finish();
                if (status != -100) {
                    Log.d(MoveAppActivity.TAG, "Move failure status: " + status);
                    Toast.makeText(MoveAppActivity.this, MoveAppProgressFragment.moveStatusToMessage(MoveAppActivity.this, status), 1).show();
                }
            }
        }
    };
    private PackageManager mPackageManager;

    public static Intent getLaunchIntent(Context context, String packageName, String packageDesc) {
        Intent i = new Intent(context, MoveAppActivity.class);
        i.putExtra(ARG_PACKAGE_NAME, packageName);
        i.putExtra(ARG_PACKAGE_DESC, packageDesc);
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPackageManager = getPackageManager();
        this.mPackageManager.registerMoveCallback(this.mMoveCallback, new Handler());
        if (savedInstanceState != null) {
            this.mAppMoveId = savedInstanceState.getInt(SAVE_STATE_MOVE_ID);
            return;
        }
        getFragmentManager().beginTransaction().add(16908290, MoveAppStepFragment.newInstance(getIntent().getStringExtra(ARG_PACKAGE_NAME), getIntent().getStringExtra(ARG_PACKAGE_DESC))).commit();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_MOVE_ID, this.mAppMoveId);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mPackageManager.unregisterMoveCallback(this.mMoveCallback);
    }

    public void onRequestMovePackageToVolume(String packageName, VolumeInfo destination) {
        this.mAppMoveId = this.mPackageManager.movePackage(packageName, destination);
        try {
            getFragmentManager().beginTransaction().replace(16908290, MoveAppProgressFragment.newInstance(this.mPackageManager.getApplicationLabel(this.mPackageManager.getApplicationInfo(packageName, 0)))).commit();
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
