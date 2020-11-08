package com.android.settingslib.development;

import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

public class SystemPropPoker {
    private static final String TAG = "SystemPropPoker";
    private static final SystemPropPoker sInstance = new SystemPropPoker();
    private boolean mBlockPokes = false;

    private SystemPropPoker() {
    }

    @NonNull
    public static SystemPropPoker getInstance() {
        return sInstance;
    }

    public void blockPokes() {
        this.mBlockPokes = true;
    }

    public void unblockPokes() {
        this.mBlockPokes = false;
    }

    public void poke() {
        if (!this.mBlockPokes) {
            createPokerTask().execute(new Void[0]);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public PokerTask createPokerTask() {
        return new PokerTask();
    }

    public static class PokerTask extends AsyncTask<Void, Void, Void> {
        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public String[] listServices() {
            return ServiceManager.listServices();
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public IBinder checkService(String service) {
            return ServiceManager.checkService(service);
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            String[] services = listServices();
            if (services == null) {
                Log.e(SystemPropPoker.TAG, "There are no services, how odd");
                return null;
            }
            for (String service : services) {
                IBinder obj = checkService(service);
                if (obj != null) {
                    Parcel data = Parcel.obtain();
                    try {
                        obj.transact(1599295570, data, (Parcel) null, 0);
                    } catch (RemoteException e) {
                    } catch (Exception e2) {
                        Log.i(SystemPropPoker.TAG, "Someone wrote a bad service '" + service + "' that doesn't like to be poked", e2);
                    }
                    data.recycle();
                }
            }
            return null;
        }
    }
}
