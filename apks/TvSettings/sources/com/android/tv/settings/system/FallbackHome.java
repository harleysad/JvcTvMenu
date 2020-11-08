package com.android.tv.settings.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.tv.settings.dialog.PinDialogFragment;
import com.android.tv.settings.users.RestrictedProfilePinDialogFragment;
import java.util.Objects;

public class FallbackHome extends Activity implements RestrictedProfilePinDialogFragment.Callback {
    private static final String TAG = "FallbackHome";
    private static boolean isNetflixObserverStarted = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            FallbackHome.this.maybeFinish();
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            FallbackHome.this.maybeFinish();
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        maybeStartPinDialog();
        maybeFinish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: private */
    public void maybeFinish() {
        if (((UserManager) getSystemService(UserManager.class)).isUserUnlocked()) {
            if (!isNetflixObserverStarted) {
                startNetflixObserver();
            }
            if (Objects.equals(getPackageName(), getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0).activityInfo.packageName)) {
                Log.d(TAG, "User unlocked but no home; let's hope someone enables one soon?");
                this.mHandler.sendEmptyMessageDelayed(0, 500);
                return;
            }
            Log.d(TAG, "User unlocked and real home found; let's go!");
            finish();
        }
    }

    private void maybeStartPinDialog() {
        if (!isUserUnlocked() && hasLockscreenSecurity(getUserId()) && LockPatternUtils.isFileEncryptionEnabled()) {
            RestrictedProfilePinDialogFragment.newInstance(2).show(getFragmentManager(), PinDialogFragment.DIALOG_TAG);
        }
    }

    private boolean isUserUnlocked() {
        return ((UserManager) getSystemService(UserManager.class)).isUserUnlocked();
    }

    public void saveLockPassword(String pin, String originalPin, int quality) {
        Log.wtf(TAG, "Not supported", new Throwable());
    }

    public void clearLockPassword(String oldPin) {
        Log.wtf(TAG, "Not supported", new Throwable());
    }

    public boolean checkPassword(String password, int userId) {
        byte[] passwordBytes;
        if (password != null) {
            try {
                passwordBytes = password.getBytes();
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to check password for unlocking the user", e);
                return false;
            }
        } else {
            passwordBytes = null;
        }
        if (new LockPatternUtils(this).getLockSettings().checkCredential(passwordBytes, 2, userId, (ICheckCredentialProgressCallback) null).getResponseCode() == 0) {
            return true;
        }
        return false;
    }

    public boolean hasLockscreenSecurity() {
        return hasLockscreenSecurity(0);
    }

    private boolean hasLockscreenSecurity(int userId) {
        LockPatternUtils lpu = new LockPatternUtils(this);
        return lpu.isLockPasswordEnabled(userId) || lpu.isLockPatternEnabled(userId);
    }

    public void pinFragmentDone(int requestCode, boolean success) {
        maybeStartPinDialog();
        maybeFinish();
    }

    private void startNetflixObserver() {
        Log.i(TAG, "start Netflix Observer");
        Intent netflixService = new Intent();
        netflixService.putExtra("isFallBackHome", "true");
        netflixService.setComponent(new ComponentName("com.mstar.netflixobserver", "com.mstar.netflixobserver.NetflixObserver"));
        startForegroundService(netflixService);
        isNetflixObserverStarted = true;
    }
}
