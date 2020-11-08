package com.android.settingslib.deviceinfo;

import android.app.usage.ExternalStorageStats;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseLongArray;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class StorageMeasurement {
    private static final String TAG = "StorageMeasurement";
    private final Context mContext;
    /* access modifiers changed from: private */
    public WeakReference<MeasurementReceiver> mReceiver;
    private final VolumeInfo mSharedVolume;
    private final StorageStatsManager mStats = ((StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class));
    private final UserManager mUser = ((UserManager) this.mContext.getSystemService(UserManager.class));
    private final VolumeInfo mVolume;

    public interface MeasurementReceiver {
        void onDetailsChanged(MeasurementDetails measurementDetails);
    }

    public static class MeasurementDetails {
        public SparseLongArray appsSize = new SparseLongArray();
        public long availSize;
        public long cacheSize;
        public SparseArray<HashMap<String, Long>> mediaSize = new SparseArray<>();
        public SparseLongArray miscSize = new SparseLongArray();
        public long totalSize;
        public SparseLongArray usersSize = new SparseLongArray();

        public String toString() {
            return "MeasurementDetails: [totalSize: " + this.totalSize + " availSize: " + this.availSize + " cacheSize: " + this.cacheSize + " mediaSize: " + this.mediaSize + " miscSize: " + this.miscSize + "usersSize: " + this.usersSize + "]";
        }
    }

    public StorageMeasurement(Context context, VolumeInfo volume, VolumeInfo sharedVolume) {
        this.mContext = context.getApplicationContext();
        this.mVolume = volume;
        this.mSharedVolume = sharedVolume;
    }

    public void setReceiver(MeasurementReceiver receiver) {
        if (this.mReceiver == null || this.mReceiver.get() == null) {
            this.mReceiver = new WeakReference<>(receiver);
        }
    }

    public void forceMeasure() {
        measure();
    }

    public void measure() {
        new MeasureTask().execute(new Void[0]);
    }

    public void onDestroy() {
        this.mReceiver = null;
    }

    private class MeasureTask extends AsyncTask<Void, Void, MeasurementDetails> {
        private MeasureTask() {
        }

        /* access modifiers changed from: protected */
        public MeasurementDetails doInBackground(Void... params) {
            return StorageMeasurement.this.measureExactStorage();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(MeasurementDetails result) {
            MeasurementReceiver receiver = StorageMeasurement.this.mReceiver != null ? (MeasurementReceiver) StorageMeasurement.this.mReceiver.get() : null;
            if (receiver != null) {
                receiver.onDetailsChanged(result);
            }
        }
    }

    /* access modifiers changed from: private */
    public MeasurementDetails measureExactStorage() {
        List<UserInfo> users = this.mUser.getUsers();
        long start = SystemClock.elapsedRealtime();
        MeasurementDetails details = new MeasurementDetails();
        if (this.mVolume == null) {
            return details;
        }
        if (this.mVolume.getType() == 0) {
            details.totalSize = this.mVolume.getPath().getTotalSpace();
            details.availSize = this.mVolume.getPath().getUsableSpace();
            return details;
        }
        try {
            details.totalSize = this.mStats.getTotalBytes(this.mVolume.fsUuid);
            details.availSize = this.mStats.getFreeBytes(this.mVolume.fsUuid);
            long finishTotal = SystemClock.elapsedRealtime();
            Log.d(TAG, "Measured total storage in " + (finishTotal - start) + "ms");
            if (this.mSharedVolume != null && this.mSharedVolume.isMountedReadable()) {
                for (UserInfo user : users) {
                    HashMap hashMap = new HashMap();
                    details.mediaSize.put(user.id, hashMap);
                    try {
                        ExternalStorageStats stats = this.mStats.queryExternalStatsForUser(this.mSharedVolume.fsUuid, UserHandle.of(user.id));
                        addValue(details.usersSize, user.id, stats.getTotalBytes());
                        hashMap.put(Environment.DIRECTORY_MUSIC, Long.valueOf(stats.getAudioBytes()));
                        hashMap.put(Environment.DIRECTORY_MOVIES, Long.valueOf(stats.getVideoBytes()));
                        hashMap.put(Environment.DIRECTORY_PICTURES, Long.valueOf(stats.getImageBytes()));
                        addValue(details.miscSize, user.id, ((stats.getTotalBytes() - stats.getAudioBytes()) - stats.getVideoBytes()) - stats.getImageBytes());
                    } catch (IOException e) {
                        Log.w(TAG, e);
                    }
                }
            }
            long finishShared = SystemClock.elapsedRealtime();
            Log.d(TAG, "Measured shared storage in " + (finishShared - finishTotal) + "ms");
            if (this.mVolume.getType() == 1 && this.mVolume.isMountedReadable()) {
                for (UserInfo user2 : users) {
                    try {
                        StorageStats stats2 = this.mStats.queryStatsForUser(this.mVolume.fsUuid, UserHandle.of(user2.id));
                        if (user2.id == UserHandle.myUserId()) {
                            addValue(details.usersSize, user2.id, stats2.getCodeBytes());
                        }
                        addValue(details.usersSize, user2.id, stats2.getDataBytes());
                        addValue(details.appsSize, user2.id, stats2.getCodeBytes() + stats2.getDataBytes());
                        details.cacheSize += stats2.getCacheBytes();
                    } catch (IOException e2) {
                        Log.w(TAG, e2);
                    }
                }
            }
            long finishPrivate = SystemClock.elapsedRealtime();
            Log.d(TAG, "Measured private storage in " + (finishPrivate - finishShared) + "ms");
            return details;
        } catch (IOException e3) {
            Log.w(TAG, e3);
            return details;
        }
    }

    private static void addValue(SparseLongArray array, int key, long value) {
        array.put(key, array.get(key) + value);
    }
}
