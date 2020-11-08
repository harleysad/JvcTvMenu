package com.android.settingslib.bluetooth;

import android.content.Context;
import android.util.Log;
import java.lang.ref.WeakReference;

public class LocalBluetoothManager {
    private static final String TAG = "LocalBluetoothManager";
    private static LocalBluetoothManager sInstance;
    private final CachedBluetoothDeviceManager mCachedDeviceManager;
    private final Context mContext;
    private final BluetoothEventManager mEventManager;
    private WeakReference<Context> mForegroundActivity;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final LocalBluetoothProfileManager mProfileManager;

    public interface BluetoothManagerCallback {
        void onBluetoothManagerInitialized(Context context, LocalBluetoothManager localBluetoothManager);
    }

    public static synchronized LocalBluetoothManager getInstance(Context context, BluetoothManagerCallback onInitCallback) {
        synchronized (LocalBluetoothManager.class) {
            if (sInstance == null) {
                LocalBluetoothAdapter adapter = LocalBluetoothAdapter.getInstance();
                if (adapter == null) {
                    return null;
                }
                Context appContext = context.getApplicationContext();
                sInstance = new LocalBluetoothManager(adapter, appContext);
                if (onInitCallback != null) {
                    onInitCallback.onBluetoothManagerInitialized(appContext, sInstance);
                }
            }
            LocalBluetoothManager localBluetoothManager = sInstance;
            return localBluetoothManager;
        }
    }

    private LocalBluetoothManager(LocalBluetoothAdapter adapter, Context context) {
        this.mContext = context;
        this.mLocalAdapter = adapter;
        this.mCachedDeviceManager = new CachedBluetoothDeviceManager(context, this);
        this.mEventManager = new BluetoothEventManager(this.mLocalAdapter, this.mCachedDeviceManager, context);
        this.mProfileManager = new LocalBluetoothProfileManager(context, this.mLocalAdapter, this.mCachedDeviceManager, this.mEventManager);
        this.mEventManager.readPairedDevices();
    }

    public LocalBluetoothAdapter getBluetoothAdapter() {
        return this.mLocalAdapter;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Context getForegroundActivity() {
        if (this.mForegroundActivity == null) {
            return null;
        }
        return (Context) this.mForegroundActivity.get();
    }

    public boolean isForegroundActivity() {
        return (this.mForegroundActivity == null || this.mForegroundActivity.get() == null) ? false : true;
    }

    public synchronized void setForegroundActivity(Context context) {
        if (context != null) {
            try {
                Log.d(TAG, "setting foreground activity to non-null context");
                this.mForegroundActivity = new WeakReference<>(context);
            } catch (Throwable th) {
                throw th;
            }
        } else if (this.mForegroundActivity != null) {
            Log.d(TAG, "setting foreground activity to null");
            this.mForegroundActivity = null;
        }
    }

    public CachedBluetoothDeviceManager getCachedDeviceManager() {
        return this.mCachedDeviceManager;
    }

    public BluetoothEventManager getEventManager() {
        return this.mEventManager;
    }

    public LocalBluetoothProfileManager getProfileManager() {
        return this.mProfileManager;
    }
}
