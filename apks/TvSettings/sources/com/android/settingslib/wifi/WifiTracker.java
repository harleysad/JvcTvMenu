package com.android.settingslib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;
import com.android.settingslib.R;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.WifiTracker;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiTracker implements LifecycleObserver, OnStart, OnStop, OnDestroy {
    private static final long DEFAULT_MAX_CACHED_SCORE_AGE_MILLIS = 1200000;
    private static final long MAX_SCAN_RESULT_AGE_MILLIS = 25000;
    private static final String TAG = "WifiTracker";
    private static final int WIFI_RESCAN_INTERVAL_MS = 10000;
    public static boolean sVerboseLogging;
    private final AtomicBoolean mConnected;
    /* access modifiers changed from: private */
    public final ConnectivityManager mConnectivityManager;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IntentFilter mFilter;
    @GuardedBy("mLock")
    private final List<AccessPoint> mInternalAccessPoints;
    private WifiInfo mLastInfo;
    private NetworkInfo mLastNetworkInfo;
    private final WifiListenerExecutor mListener;
    private final Object mLock;
    private long mMaxSpeedLabelScoreCacheAge;
    private WifiTrackerNetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private boolean mNetworkScoringUiEnabled;
    @VisibleForTesting
    final BroadcastReceiver mReceiver;
    /* access modifiers changed from: private */
    public boolean mRegistered;
    @GuardedBy("mLock")
    private final Set<NetworkKey> mRequestedScores;
    private final HashMap<String, ScanResult> mScanResultCache;
    @VisibleForTesting
    Scanner mScanner;
    private WifiNetworkScoreCache mScoreCache;
    /* access modifiers changed from: private */
    public boolean mStaleScanResults;
    /* access modifiers changed from: private */
    public final WifiManager mWifiManager;
    @VisibleForTesting
    Handler mWorkHandler;
    private HandlerThread mWorkThread;

    public interface WifiListener {
        void onAccessPointsChanged();

        void onConnectedChanged();

        void onWifiStateChanged(int i);
    }

    private static final boolean DBG() {
        return Log.isLoggable(TAG, 3);
    }

    /* access modifiers changed from: private */
    public static boolean isVerboseLoggingEnabled() {
        return sVerboseLogging || Log.isLoggable(TAG, 2);
    }

    private static IntentFilter newIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        filter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        filter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.RSSI_CHANGED");
        return filter;
    }

    @Deprecated
    public WifiTracker(Context context, WifiListener wifiListener, boolean includeSaved, boolean includeScans) {
        this(context, wifiListener, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class), newIntentFilter());
    }

    public WifiTracker(Context context, WifiListener wifiListener, @NonNull Lifecycle lifecycle, boolean includeSaved, boolean includeScans) {
        this(context, wifiListener, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class), newIntentFilter());
        lifecycle.addObserver(this);
    }

    @VisibleForTesting
    WifiTracker(Context context, WifiListener wifiListener, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, IntentFilter filter) {
        boolean z = false;
        this.mConnected = new AtomicBoolean(false);
        this.mLock = new Object();
        this.mInternalAccessPoints = new ArrayList();
        this.mRequestedScores = new ArraySet();
        this.mStaleScanResults = true;
        this.mScanResultCache = new HashMap<>();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                    WifiTracker.this.updateWifiState(intent.getIntExtra("wifi_state", 4));
                } else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                    boolean unused = WifiTracker.this.mStaleScanResults = false;
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action) || "android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(action)) {
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                    WifiTracker.this.updateNetworkInfo((NetworkInfo) intent.getParcelableExtra("networkInfo"));
                    WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
                } else if ("android.net.wifi.RSSI_CHANGED".equals(action)) {
                    WifiTracker.this.updateNetworkInfo(WifiTracker.this.mConnectivityManager.getNetworkInfo(WifiTracker.this.mWifiManager.getCurrentNetwork()));
                }
            }
        };
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mListener = new WifiListenerExecutor(wifiListener);
        this.mConnectivityManager = connectivityManager;
        sVerboseLogging = this.mWifiManager.getVerboseLoggingLevel() > 0 ? true : z;
        this.mFilter = filter;
        this.mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
        this.mNetworkScoreManager = networkScoreManager;
        HandlerThread workThread = new HandlerThread("WifiTracker{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        workThread.start();
        setWorkThread(workThread);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setWorkThread(HandlerThread workThread) {
        this.mWorkThread = workThread;
        this.mWorkHandler = new Handler(workThread.getLooper());
        this.mScoreCache = new WifiNetworkScoreCache(this.mContext, new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) {
            public void networkCacheUpdated(List<ScoredNetwork> networks) {
                if (WifiTracker.this.mRegistered) {
                    if (Log.isLoggable(WifiTracker.TAG, 2)) {
                        Log.v(WifiTracker.TAG, "Score cache was updated with networks: " + networks);
                    }
                    WifiTracker.this.updateNetworkScores();
                }
            }
        });
    }

    public void onDestroy() {
        this.mWorkThread.quit();
    }

    private void pauseScanning() {
        if (this.mScanner != null) {
            this.mScanner.pause();
            this.mScanner = null;
        }
        this.mStaleScanResults = true;
    }

    public void resumeScanning() {
        if (this.mScanner == null) {
            this.mScanner = new Scanner();
        }
        if (this.mWifiManager.isWifiEnabled()) {
            this.mScanner.resume();
        }
    }

    public void onStart() {
        forceUpdate();
        registerScoreCache();
        boolean z = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "network_scoring_ui_enabled", 0) == 1) {
            z = true;
        }
        this.mNetworkScoringUiEnabled = z;
        this.mMaxSpeedLabelScoreCacheAge = Settings.Global.getLong(this.mContext.getContentResolver(), "speed_label_cache_eviction_age_millis", DEFAULT_MAX_CACHED_SCORE_AGE_MILLIS);
        resumeScanning();
        if (!this.mRegistered) {
            this.mContext.registerReceiver(this.mReceiver, this.mFilter, (String) null, this.mWorkHandler);
            this.mNetworkCallback = new WifiTrackerNetworkCallback();
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkHandler);
            this.mRegistered = true;
        }
    }

    private void forceUpdate() {
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        this.mLastNetworkInfo = this.mConnectivityManager.getNetworkInfo(this.mWifiManager.getCurrentNetwork());
        fetchScansAndConfigsAndUpdateAccessPoints();
    }

    private void registerScoreCache() {
        this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mScoreCache, 2);
    }

    private void requestScoresForNetworkKeys(Collection<NetworkKey> keys) {
        if (!keys.isEmpty()) {
            if (DBG()) {
                Log.d(TAG, "Requesting scores for Network Keys: " + keys);
            }
            this.mNetworkScoreManager.requestScores((NetworkKey[]) keys.toArray(new NetworkKey[keys.size()]));
            synchronized (this.mLock) {
                this.mRequestedScores.addAll(keys);
            }
        }
    }

    public void onStop() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mRegistered = false;
        }
        unregisterScoreCache();
        pauseScanning();
        this.mWorkHandler.removeCallbacksAndMessages((Object) null);
    }

    private void unregisterScoreCache() {
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mScoreCache);
        synchronized (this.mLock) {
            this.mRequestedScores.clear();
        }
    }

    public List<AccessPoint> getAccessPoints() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mInternalAccessPoints);
        }
        return arrayList;
    }

    public WifiManager getManager() {
        return this.mWifiManager;
    }

    public boolean isWifiEnabled() {
        return this.mWifiManager.isWifiEnabled();
    }

    public int getNumSavedNetworks() {
        return WifiSavedConfigUtils.getAllConfigs(this.mContext, this.mWifiManager).size();
    }

    public boolean isConnected() {
        return this.mConnected.get();
    }

    public void dump(PrintWriter pw) {
        pw.println("  - wifi tracker ------");
        for (AccessPoint accessPoint : getAccessPoints()) {
            pw.println("  " + accessPoint);
        }
    }

    private ArrayMap<String, List<ScanResult>> updateScanResultCache(List<ScanResult> newResults) {
        List<ScanResult> resultList;
        for (ScanResult newResult : newResults) {
            if (newResult.SSID != null && !newResult.SSID.isEmpty()) {
                this.mScanResultCache.put(newResult.BSSID, newResult);
            }
        }
        if (!this.mStaleScanResults) {
            evictOldScans();
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = new ArrayMap<>();
        for (ScanResult result : this.mScanResultCache.values()) {
            if (!(result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]"))) {
                String apKey = AccessPoint.getKey(result);
                if (scanResultsByApKey.containsKey(apKey)) {
                    resultList = scanResultsByApKey.get(apKey);
                } else {
                    resultList = new ArrayList<>();
                    scanResultsByApKey.put(apKey, resultList);
                }
                resultList.add(result);
            }
        }
        return scanResultsByApKey;
    }

    private void evictOldScans() {
        long nowMs = SystemClock.elapsedRealtime();
        Iterator<ScanResult> iter = this.mScanResultCache.values().iterator();
        while (iter.hasNext()) {
            if (nowMs - (iter.next().timestamp / 1000) > MAX_SCAN_RESULT_AGE_MILLIS) {
                iter.remove();
            }
        }
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId, List<WifiConfiguration> configs) {
        if (configs == null) {
            return null;
        }
        for (WifiConfiguration config : configs) {
            if (this.mLastInfo != null && networkId == config.networkId) {
                if (!config.selfAdded || config.numAssociation != 0) {
                    return config;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void fetchScansAndConfigsAndUpdateAccessPoints() {
        List<ScanResult> newScanResults = this.mWifiManager.getScanResults();
        if (isVerboseLoggingEnabled()) {
            Log.i(TAG, "Fetched scan results: " + newScanResults);
        }
        updateAccessPoints(newScanResults, this.mWifiManager.getConfiguredNetworks());
    }

    private void updateAccessPoints(List<ScanResult> newScanResults, List<WifiConfiguration> configs) {
        List<WifiConfiguration> list = configs;
        Map<String, WifiConfiguration> configsByKey = new ArrayMap<>(configs.size());
        if (list != null) {
            for (WifiConfiguration config : configs) {
                configsByKey.put(AccessPoint.getKey(config), config);
            }
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = updateScanResultCache(newScanResults);
        WifiConfiguration connectionConfig = null;
        if (this.mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId(), list);
        }
        WifiConfiguration connectionConfig2 = connectionConfig;
        synchronized (this.mLock) {
            List<AccessPoint> cachedAccessPoints = new ArrayList<>(this.mInternalAccessPoints);
            ArrayList<AccessPoint> accessPoints = new ArrayList<>();
            List<NetworkKey> scoresToRequest = new ArrayList<>();
            for (Map.Entry<String, List<ScanResult>> entry : scanResultsByApKey.entrySet()) {
                for (ScanResult result : entry.getValue()) {
                    NetworkKey key = NetworkKey.createFromScanResult(result);
                    if (key != null && !this.mRequestedScores.contains(key)) {
                        scoresToRequest.add(key);
                    }
                }
                AccessPoint accessPoint = getCachedOrCreate(entry.getValue(), cachedAccessPoints);
                if (!(this.mLastInfo == null || this.mLastNetworkInfo == null)) {
                    accessPoint.update(connectionConfig2, this.mLastInfo, this.mLastNetworkInfo);
                }
                accessPoint.update(configsByKey.get(entry.getKey()));
                accessPoints.add(accessPoint);
            }
            if (accessPoints.isEmpty() && connectionConfig2 != null) {
                AccessPoint activeAp = new AccessPoint(this.mContext, connectionConfig2);
                activeAp.update(connectionConfig2, this.mLastInfo, this.mLastNetworkInfo);
                accessPoints.add(activeAp);
                scoresToRequest.add(NetworkKey.createFromWifiInfo(this.mLastInfo));
            }
            requestScoresForNetworkKeys(scoresToRequest);
            Iterator<AccessPoint> it = accessPoints.iterator();
            while (it.hasNext()) {
                it.next().update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge);
            }
            Collections.sort(accessPoints);
            if (DBG()) {
                Log.d(TAG, "------ Dumping SSIDs that were not seen on this scan ------");
                for (AccessPoint prevAccessPoint : this.mInternalAccessPoints) {
                    if (prevAccessPoint.getSsid() != null) {
                        String prevSsid = prevAccessPoint.getSsidStr();
                        boolean found = false;
                        Iterator<AccessPoint> it2 = accessPoints.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            AccessPoint newAccessPoint = it2.next();
                            if (newAccessPoint.getSsidStr() != null && newAccessPoint.getSsidStr().equals(prevSsid)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Log.d(TAG, "Did not find " + prevSsid + " in this scan");
                        }
                    }
                }
                Log.d(TAG, "---- Done dumping SSIDs that were not seen on this scan ----");
            }
            this.mInternalAccessPoints.clear();
            this.mInternalAccessPoints.addAll(accessPoints);
        }
        conditionallyNotifyListeners();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public AccessPoint getCachedOrCreate(List<ScanResult> scanResults, List<AccessPoint> cache) {
        int N = cache.size();
        for (int i = 0; i < N; i++) {
            if (cache.get(i).getKey().equals(AccessPoint.getKey(scanResults.get(0)))) {
                AccessPoint ret = cache.remove(i);
                ret.setScanResults(scanResults);
                return ret;
            }
        }
        return new AccessPoint(this.mContext, (Collection<ScanResult>) scanResults);
    }

    /* access modifiers changed from: private */
    public void updateNetworkInfo(NetworkInfo networkInfo) {
        if (!this.mWifiManager.isWifiEnabled()) {
            clearAccessPointsAndConditionallyUpdate();
            return;
        }
        if (networkInfo != null) {
            this.mLastNetworkInfo = networkInfo;
            if (DBG()) {
                Log.d(TAG, "mLastNetworkInfo set: " + this.mLastNetworkInfo);
            }
            if (networkInfo.isConnected() != this.mConnected.getAndSet(networkInfo.isConnected())) {
                this.mListener.onConnectedChanged();
            }
        }
        WifiConfiguration connectionConfig = null;
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        if (DBG()) {
            Log.d(TAG, "mLastInfo set as: " + this.mLastInfo);
        }
        if (this.mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId(), this.mWifiManager.getConfiguredNetworks());
        }
        boolean updated = false;
        boolean reorder = false;
        synchronized (this.mLock) {
            for (int i = this.mInternalAccessPoints.size() - 1; i >= 0; i--) {
                AccessPoint ap = this.mInternalAccessPoints.get(i);
                boolean previouslyConnected = ap.isActive();
                if (ap.update(connectionConfig, this.mLastInfo, this.mLastNetworkInfo)) {
                    updated = true;
                    if (previouslyConnected != ap.isActive()) {
                        reorder = true;
                    }
                }
                if (ap.update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    reorder = true;
                    updated = true;
                }
            }
            if (reorder) {
                Collections.sort(this.mInternalAccessPoints);
            }
            if (updated) {
                conditionallyNotifyListeners();
            }
        }
    }

    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (this.mLock) {
            if (!this.mInternalAccessPoints.isEmpty()) {
                this.mInternalAccessPoints.clear();
                conditionallyNotifyListeners();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateNetworkScores() {
        synchronized (this.mLock) {
            boolean updated = false;
            for (int i = 0; i < this.mInternalAccessPoints.size(); i++) {
                if (this.mInternalAccessPoints.get(i).update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    updated = true;
                }
            }
            if (updated) {
                Collections.sort(this.mInternalAccessPoints);
                conditionallyNotifyListeners();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateWifiState(int state) {
        if (state != 3) {
            clearAccessPointsAndConditionallyUpdate();
            this.mLastInfo = null;
            this.mLastNetworkInfo = null;
            if (this.mScanner != null) {
                this.mScanner.pause();
            }
            this.mStaleScanResults = true;
        } else if (this.mScanner != null) {
            this.mScanner.resume();
        }
        this.mListener.onWifiStateChanged(state);
    }

    private final class WifiTrackerNetworkCallback extends ConnectivityManager.NetworkCallback {
        private WifiTrackerNetworkCallback() {
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities nc) {
            if (network.equals(WifiTracker.this.mWifiManager.getCurrentNetwork())) {
                WifiTracker.this.updateNetworkInfo((NetworkInfo) null);
            }
        }
    }

    @VisibleForTesting
    class Scanner extends Handler {
        static final int MSG_SCAN = 0;
        private int mRetry = 0;

        Scanner() {
        }

        /* access modifiers changed from: package-private */
        public void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        /* access modifiers changed from: package-private */
        public void pause() {
            this.mRetry = 0;
            removeMessages(0);
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public boolean isScanning() {
            return hasMessages(0);
        }

        public void handleMessage(Message message) {
            if (message.what == 0) {
                if (WifiTracker.this.mWifiManager.startScan()) {
                    this.mRetry = 0;
                } else {
                    int i = this.mRetry + 1;
                    this.mRetry = i;
                    if (i >= 3) {
                        this.mRetry = 0;
                        if (WifiTracker.this.mContext != null) {
                            Toast.makeText(WifiTracker.this.mContext, R.string.wifi_fail_to_scan, 1).show();
                            return;
                        }
                        return;
                    }
                }
                sendEmptyMessageDelayed(0, 10000);
            }
        }
    }

    private static class Multimap<K, V> {
        private final HashMap<K, List<V>> store = new HashMap<>();

        private Multimap() {
        }

        /* access modifiers changed from: package-private */
        public List<V> getAll(K key) {
            List<V> values = this.store.get(key);
            return values != null ? values : Collections.emptyList();
        }

        /* access modifiers changed from: package-private */
        public void put(K key, V val) {
            List<V> curVals = this.store.get(key);
            if (curVals == null) {
                curVals = new ArrayList<>(3);
                this.store.put(key, curVals);
            }
            curVals.add(val);
        }
    }

    @VisibleForTesting
    class WifiListenerExecutor implements WifiListener {
        private final WifiListener mDelegatee;

        public WifiListenerExecutor(WifiListener listener) {
            this.mDelegatee = listener;
        }

        public void onWifiStateChanged(int state) {
            runAndLog(new Runnable(state) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiTracker.WifiListenerExecutor.this.mDelegatee.onWifiStateChanged(this.f$1);
                }
            }, String.format("Invoking onWifiStateChanged callback with state %d", new Object[]{Integer.valueOf(state)}));
        }

        public void onConnectedChanged() {
            WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() {
                public final void run() {
                    WifiTracker.WifiListener.this.onConnectedChanged();
                }
            }, "Invoking onConnectedChanged callback");
        }

        public void onAccessPointsChanged() {
            WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() {
                public final void run() {
                    WifiTracker.WifiListener.this.onAccessPointsChanged();
                }
            }, "Invoking onAccessPointsChanged callback");
        }

        private void runAndLog(Runnable r, String verboseLog) {
            ThreadUtils.postOnMainThread(new Runnable(verboseLog, r) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WifiTracker.WifiListenerExecutor.lambda$runAndLog$1(WifiTracker.WifiListenerExecutor.this, this.f$1, this.f$2);
                }
            });
        }

        public static /* synthetic */ void lambda$runAndLog$1(WifiListenerExecutor wifiListenerExecutor, String verboseLog, Runnable r) {
            if (WifiTracker.this.mRegistered) {
                if (WifiTracker.isVerboseLoggingEnabled()) {
                    Log.i(WifiTracker.TAG, verboseLog);
                }
                r.run();
            }
        }
    }

    private void conditionallyNotifyListeners() {
        if (!this.mStaleScanResults) {
            this.mListener.onAccessPointsChanged();
        }
    }
}
