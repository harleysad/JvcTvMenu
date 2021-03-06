package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Application;
import android.app.usage.StorageStatsManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.VisibleForTesting;
import android.text.format.Formatter;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.device.apps.specialaccess.DirectoryAccessDetails;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ApplicationsState {
    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppEntry object1, AppEntry object2) {
            int compareResult;
            int compareResult2 = this.sCollator.compare(object1.label, object2.label);
            if (compareResult2 != 0) {
                return compareResult2;
            }
            if (object1.info == null || object2.info == null || (compareResult = this.sCollator.compare(object1.info.packageName, object2.info.packageName)) == 0) {
                return object1.info.uid - object2.info.uid;
            }
            return compareResult;
        }
    };
    static final boolean DEBUG = false;
    static final boolean DEBUG_LOCKING = false;
    public static final int DEFAULT_SESSION_FLAGS = 15;
    public static final Comparator<AppEntry> EXTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() {
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.externalSize < object2.externalSize) {
                return 1;
            }
            if (object1.externalSize > object2.externalSize) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    public static final AppFilter FILTER_ALL_ENABLED = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return entry.info.enabled && !AppUtils.isInstant(entry.info);
        }
    };
    public static final AppFilter FILTER_AUDIO = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            boolean isMusicApp;
            synchronized (entry) {
                boolean z = true;
                if (entry.info.category != 1) {
                    z = false;
                }
                isMusicApp = z;
            }
            return isMusicApp;
        }
    };
    public static final AppFilter FILTER_DISABLED = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return !entry.info.enabled && !AppUtils.isInstant(entry.info);
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            if (AppUtils.isInstant(entry.info)) {
                return false;
            }
            if (ApplicationsState.hasFlag(entry.info.flags, 128) || !ApplicationsState.hasFlag(entry.info.flags, 1) || entry.hasLauncherEntry) {
                return true;
            }
            if (!ApplicationsState.hasFlag(entry.info.flags, 1) || !entry.isHomeApp) {
                return false;
            }
            return true;
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return AppUtils.isInstant(entry.info) || ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(entry);
        }
    };
    public static final AppFilter FILTER_EVERYTHING = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return true;
        }
    };
    public static final AppFilter FILTER_GAMES = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry info) {
            boolean isGame;
            synchronized (info.info) {
                if (!ApplicationsState.hasFlag(info.info.flags, 33554432)) {
                    if (info.info.category != 0) {
                        isGame = false;
                    }
                }
                isGame = true;
            }
            return isGame;
        }
    };
    public static final AppFilter FILTER_INSTANT = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return AppUtils.isInstant(entry.info);
        }
    };
    public static final AppFilter FILTER_MOVIES = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            boolean isMovieApp;
            synchronized (entry) {
                isMovieApp = entry.info.category == 2;
            }
            return isMovieApp;
        }
    };
    public static final AppFilter FILTER_NOT_HIDE = new AppFilter() {
        private String[] mHidePackageNames;

        public void init(Context context) {
            this.mHidePackageNames = context.getResources().getStringArray(17236014);
        }

        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            if (!ArrayUtils.contains(this.mHidePackageNames, entry.info.packageName)) {
                return true;
            }
            if (entry.info.enabled && entry.info.enabledSetting != 4) {
                return true;
            }
            return false;
        }
    };
    public static final AppFilter FILTER_OTHER_APPS = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            boolean isCategorized;
            synchronized (entry) {
                if (!ApplicationsState.FILTER_AUDIO.filterApp(entry) && !ApplicationsState.FILTER_GAMES.filterApp(entry) && !ApplicationsState.FILTER_MOVIES.filterApp(entry)) {
                    if (!ApplicationsState.FILTER_PHOTOS.filterApp(entry)) {
                        isCategorized = false;
                    }
                }
                isCategorized = true;
            }
            if (!isCategorized) {
                return true;
            }
            return false;
        }
    };
    public static final AppFilter FILTER_PERSONAL = new AppFilter() {
        private int mCurrentUser;

        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        public boolean filterApp(AppEntry entry) {
            return UserHandle.getUserId(entry.info.uid) == this.mCurrentUser;
        }
    };
    public static final AppFilter FILTER_PHOTOS = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            boolean isPhotosApp;
            synchronized (entry) {
                isPhotosApp = entry.info.category == 3;
            }
            return isPhotosApp;
        }
    };
    public static final AppFilter FILTER_THIRD_PARTY = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            if (!ApplicationsState.hasFlag(entry.info.flags, 128) && ApplicationsState.hasFlag(entry.info.flags, 1)) {
                return false;
            }
            return true;
        }
    };
    public static final AppFilter FILTER_WITHOUT_DISABLED_UNTIL_USED = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return entry.info.enabledSetting != 4;
        }
    };
    public static final AppFilter FILTER_WITH_DOMAIN_URLS = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry entry) {
            return !AppUtils.isInstant(entry.info) && ApplicationsState.hasFlag(entry.info.privateFlags, 16);
        }
    };
    public static final AppFilter FILTER_WORK = new AppFilter() {
        private int mCurrentUser;

        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        public boolean filterApp(AppEntry entry) {
            return UserHandle.getUserId(entry.info.uid) != this.mCurrentUser;
        }
    };
    public static final int FLAG_SESSION_REQUEST_HOME_APP = 1;
    public static final int FLAG_SESSION_REQUEST_ICONS = 2;
    public static final int FLAG_SESSION_REQUEST_LAUNCHER = 8;
    public static final int FLAG_SESSION_REQUEST_LEANBACK_LAUNCHER = 16;
    public static final int FLAG_SESSION_REQUEST_SIZES = 4;
    public static final Comparator<AppEntry> INTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() {
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.internalSize < object2.internalSize) {
                return 1;
            }
            if (object1.internalSize > object2.internalSize) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    static final Pattern REMOVE_DIACRITICALS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    public static final Comparator<AppEntry> SIZE_COMPARATOR = new Comparator<AppEntry>() {
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.size < object2.size) {
                return 1;
            }
            if (object1.size > object2.size) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    public static final int SIZE_INVALID = -2;
    public static final int SIZE_UNKNOWN = -1;
    static final String TAG = "ApplicationsState";
    static ApplicationsState sInstance;
    static final Object sLock = new Object();
    final ArrayList<WeakReference<Session>> mActiveSessions = new ArrayList<>();
    final int mAdminRetrieveFlags;
    final ArrayList<AppEntry> mAppEntries = new ArrayList<>();
    List<ApplicationInfo> mApplications = new ArrayList();
    final BackgroundHandler mBackgroundHandler;
    final Context mContext;
    String mCurComputingSizePkg;
    int mCurComputingSizeUserId;
    UUID mCurComputingSizeUuid;
    long mCurId = 1;
    final IconDrawableFactory mDrawableFactory;
    final SparseArray<HashMap<String, AppEntry>> mEntriesMap = new SparseArray<>();
    boolean mHaveDisabledApps;
    boolean mHaveInstantApps;
    final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    final IPackageManager mIpm;
    final MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());
    PackageIntentReceiver mPackageIntentReceiver;
    final PackageManager mPm;
    final ArrayList<Session> mRebuildingSessions = new ArrayList<>();
    boolean mResumed;
    final int mRetrieveFlags;
    final ArrayList<Session> mSessions = new ArrayList<>();
    boolean mSessionsChanged;
    final StorageStatsManager mStats;
    final HandlerThread mThread;
    final UserManager mUm;

    public interface Callbacks {
        void onAllSizesComputed();

        void onLauncherInfoChanged();

        void onLoadEntriesCompleted();

        void onPackageIconChanged();

        void onPackageListChanged();

        void onPackageSizeChanged(String str);

        void onRebuildComplete(ArrayList<AppEntry> arrayList);

        void onRunningStateChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SessionFlags {
    }

    public static class SizeInfo {
        public long cacheSize;
        public long codeSize;
        public long dataSize;
        public long externalCacheSize;
        public long externalCodeSize;
        public long externalDataSize;
    }

    public static ApplicationsState getInstance(Application app) {
        ApplicationsState applicationsState;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ApplicationsState(app);
            }
            applicationsState = sInstance;
        }
        return applicationsState;
    }

    private ApplicationsState(Application app) {
        this.mContext = app;
        this.mPm = this.mContext.getPackageManager();
        this.mDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        this.mIpm = AppGlobals.getPackageManager();
        this.mUm = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mStats = (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class);
        for (int userId : this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId())) {
            this.mEntriesMap.put(userId, new HashMap());
        }
        this.mThread = new HandlerThread("ApplicationsState.Loader", 10);
        this.mThread.start();
        this.mBackgroundHandler = new BackgroundHandler(this.mThread.getLooper());
        this.mAdminRetrieveFlags = 4227584;
        this.mRetrieveFlags = 33280;
        synchronized (this.mEntriesMap) {
            try {
                this.mEntriesMap.wait(1);
            } catch (InterruptedException e) {
            }
        }
    }

    public Looper getBackgroundLooper() {
        return this.mThread.getLooper();
    }

    public Session newSession(Callbacks callbacks) {
        return newSession(callbacks, (Lifecycle) null);
    }

    public Session newSession(Callbacks callbacks, Lifecycle lifecycle) {
        Session s = new Session(callbacks, lifecycle);
        synchronized (this.mEntriesMap) {
            this.mSessions.add(s);
        }
        return s;
    }

    /* access modifiers changed from: package-private */
    public void doResumeIfNeededLocked() {
        if (!this.mResumed) {
            this.mResumed = true;
            if (this.mPackageIntentReceiver == null) {
                this.mPackageIntentReceiver = new PackageIntentReceiver();
                this.mPackageIntentReceiver.registerReceiver();
            }
            this.mApplications = new ArrayList();
            for (UserInfo user : this.mUm.getProfiles(UserHandle.myUserId())) {
                try {
                    if (this.mEntriesMap.indexOfKey(user.id) < 0) {
                        this.mEntriesMap.put(user.id, new HashMap());
                    }
                    this.mApplications.addAll(this.mIpm.getInstalledApplications(user.isAdmin() ? this.mAdminRetrieveFlags : this.mRetrieveFlags, user.id).getList());
                } catch (RemoteException e) {
                }
            }
            int i = 0;
            if (this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources())) {
                clearEntries();
            } else {
                for (int i2 = 0; i2 < this.mAppEntries.size(); i2++) {
                    this.mAppEntries.get(i2).sizeStale = true;
                }
            }
            this.mHaveDisabledApps = false;
            this.mHaveInstantApps = false;
            while (true) {
                int i3 = i;
                if (i3 >= this.mApplications.size()) {
                    break;
                }
                ApplicationInfo info = this.mApplications.get(i3);
                if (!info.enabled) {
                    if (info.enabledSetting != 3) {
                        this.mApplications.remove(i3);
                        i3--;
                        i = i3 + 1;
                    } else {
                        this.mHaveDisabledApps = true;
                    }
                }
                if (!this.mHaveInstantApps && AppUtils.isInstant(info)) {
                    this.mHaveInstantApps = true;
                }
                AppEntry entry = (AppEntry) this.mEntriesMap.get(UserHandle.getUserId(info.uid)).get(info.packageName);
                if (entry != null) {
                    entry.info = info;
                }
                i = i3 + 1;
            }
            if (this.mAppEntries.size() > this.mApplications.size()) {
                clearEntries();
            }
            this.mCurComputingSizePkg = null;
            if (!this.mBackgroundHandler.hasMessages(2)) {
                this.mBackgroundHandler.sendEmptyMessage(2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void clearEntries() {
        for (int i = 0; i < this.mEntriesMap.size(); i++) {
            this.mEntriesMap.valueAt(i).clear();
        }
        this.mAppEntries.clear();
    }

    public boolean haveDisabledApps() {
        return this.mHaveDisabledApps;
    }

    public boolean haveInstantApps() {
        return this.mHaveInstantApps;
    }

    /* access modifiers changed from: package-private */
    public void doPauseIfNeededLocked() {
        if (this.mResumed) {
            int i = 0;
            while (i < this.mSessions.size()) {
                if (!this.mSessions.get(i).mResumed) {
                    i++;
                } else {
                    return;
                }
            }
            doPauseLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void doPauseLocked() {
        this.mResumed = false;
        if (this.mPackageIntentReceiver != null) {
            this.mPackageIntentReceiver.unregisterReceiver();
            this.mPackageIntentReceiver = null;
        }
    }

    public AppEntry getEntry(String packageName, int userId) {
        AppEntry entry;
        synchronized (this.mEntriesMap) {
            entry = (AppEntry) this.mEntriesMap.get(userId).get(packageName);
            if (entry == null) {
                ApplicationInfo info = getAppInfoLocked(packageName, userId);
                if (info == null) {
                    try {
                        info = this.mIpm.getApplicationInfo(packageName, 0, userId);
                    } catch (RemoteException e) {
                        Log.w(TAG, "getEntry couldn't reach PackageManager", e);
                        return null;
                    }
                }
                if (info != null) {
                    entry = getEntryLocked(info);
                }
            }
        }
        return entry;
    }

    private ApplicationInfo getAppInfoLocked(String pkg, int userId) {
        for (int i = 0; i < this.mApplications.size(); i++) {
            ApplicationInfo info = this.mApplications.get(i);
            if (pkg.equals(info.packageName) && userId == UserHandle.getUserId(info.uid)) {
                return info;
            }
        }
        return null;
    }

    public void ensureIcon(AppEntry entry) {
        if (entry.icon == null) {
            synchronized (entry) {
                entry.ensureIconLocked(this.mContext, this.mDrawableFactory);
            }
        }
    }

    public void requestSize(String packageName, int userId) {
        synchronized (this.mEntriesMap) {
            AppEntry entry = (AppEntry) this.mEntriesMap.get(userId).get(packageName);
            if (entry != null && hasFlag(entry.info.flags, 8388608)) {
                this.mBackgroundHandler.post(new Runnable(entry, packageName, userId) {
                    private final /* synthetic */ ApplicationsState.AppEntry f$1;
                    private final /* synthetic */ String f$2;
                    private final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ApplicationsState.lambda$requestSize$0(ApplicationsState.this, this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0046, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0047, code lost:
        android.util.Log.w(TAG, "Failed to query stats: " + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r6.mBackgroundHandler.mStatsObserver.onGetStatsCompleted((android.content.pm.PackageStats) null, false);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x0046 A[ExcHandler: NameNotFoundException | IOException (r0v0 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:0:0x0000] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$requestSize$0(com.android.settingslib.applications.ApplicationsState r6, com.android.settingslib.applications.ApplicationsState.AppEntry r7, java.lang.String r8, int r9) {
        /*
            android.app.usage.StorageStatsManager r0 = r6.mStats     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.content.pm.ApplicationInfo r1 = r7.info     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            java.util.UUID r1 = r1.storageUuid     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.os.UserHandle r2 = android.os.UserHandle.of(r9)     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.app.usage.StorageStats r0 = r0.queryStatsForPackage(r1, r8, r2)     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.app.usage.StorageStatsManager r1 = r6.mStats     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.content.pm.ApplicationInfo r2 = r7.info     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            java.util.UUID r2 = r2.storageUuid     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            java.lang.String r2 = r2.toString()     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.content.pm.ApplicationInfo r3 = r7.info     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            int r3 = r3.uid     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            long r1 = r1.getCacheQuotaBytes(r2, r3)     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            android.content.pm.PackageStats r3 = new android.content.pm.PackageStats     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            r3.<init>(r8, r9)     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            long r4 = r0.getCodeBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            r3.codeSize = r4     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            long r4 = r0.getDataBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            r3.dataSize = r4     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            long r4 = r0.getCacheBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            long r4 = java.lang.Math.min(r4, r1)     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            r3.cacheSize = r4     // Catch:{ NameNotFoundException | IOException -> 0x0046 }
            com.android.settingslib.applications.ApplicationsState$BackgroundHandler r4 = r6.mBackgroundHandler     // Catch:{ RemoteException -> 0x0044, NameNotFoundException | IOException -> 0x0046 }
            android.content.pm.IPackageStatsObserver$Stub r4 = r4.mStatsObserver     // Catch:{ RemoteException -> 0x0044, NameNotFoundException | IOException -> 0x0046 }
            r5 = 1
            r4.onGetStatsCompleted(r3, r5)     // Catch:{ RemoteException -> 0x0044, NameNotFoundException | IOException -> 0x0046 }
            goto L_0x0045
        L_0x0044:
            r4 = move-exception
        L_0x0045:
            goto L_0x0068
        L_0x0046:
            r0 = move-exception
            java.lang.String r1 = "ApplicationsState"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed to query stats: "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            android.util.Log.w(r1, r2)
            com.android.settingslib.applications.ApplicationsState$BackgroundHandler r1 = r6.mBackgroundHandler     // Catch:{ RemoteException -> 0x0067 }
            android.content.pm.IPackageStatsObserver$Stub r1 = r1.mStatsObserver     // Catch:{ RemoteException -> 0x0067 }
            r2 = 0
            r3 = 0
            r1.onGetStatsCompleted(r2, r3)     // Catch:{ RemoteException -> 0x0067 }
            goto L_0x0068
        L_0x0067:
            r1 = move-exception
        L_0x0068:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.lambda$requestSize$0(com.android.settingslib.applications.ApplicationsState, com.android.settingslib.applications.ApplicationsState$AppEntry, java.lang.String, int):void");
    }

    /* access modifiers changed from: package-private */
    public long sumCacheSizes() {
        long sum = 0;
        synchronized (this.mEntriesMap) {
            for (int i = this.mAppEntries.size() - 1; i >= 0; i--) {
                sum += this.mAppEntries.get(i).cacheSize;
            }
        }
        return sum;
    }

    /* access modifiers changed from: package-private */
    public int indexOfApplicationInfoLocked(String pkgName, int userId) {
        for (int i = this.mApplications.size() - 1; i >= 0; i--) {
            ApplicationInfo appInfo = this.mApplications.get(i);
            if (appInfo.packageName.equals(pkgName) && UserHandle.getUserId(appInfo.uid) == userId) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addPackage(java.lang.String r6, int r7) {
        /*
            r5 = this;
            android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r0 = r5.mEntriesMap     // Catch:{ RemoteException -> 0x0063 }
            monitor-enter(r0)     // Catch:{ RemoteException -> 0x0063 }
            boolean r1 = r5.mResumed     // Catch:{ all -> 0x0060 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            return
        L_0x0009:
            int r1 = r5.indexOfApplicationInfoLocked(r6, r7)     // Catch:{ all -> 0x0060 }
            if (r1 < 0) goto L_0x0011
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            return
        L_0x0011:
            android.content.pm.IPackageManager r1 = r5.mIpm     // Catch:{ all -> 0x0060 }
            android.os.UserManager r2 = r5.mUm     // Catch:{ all -> 0x0060 }
            boolean r2 = r2.isUserAdmin(r7)     // Catch:{ all -> 0x0060 }
            if (r2 == 0) goto L_0x001e
            int r2 = r5.mAdminRetrieveFlags     // Catch:{ all -> 0x0060 }
            goto L_0x0020
        L_0x001e:
            int r2 = r5.mRetrieveFlags     // Catch:{ all -> 0x0060 }
        L_0x0020:
            android.content.pm.ApplicationInfo r1 = r1.getApplicationInfo(r6, r2, r7)     // Catch:{ all -> 0x0060 }
            if (r1 != 0) goto L_0x0028
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            return
        L_0x0028:
            boolean r2 = r1.enabled     // Catch:{ all -> 0x0060 }
            r3 = 1
            if (r2 != 0) goto L_0x0036
            int r2 = r1.enabledSetting     // Catch:{ all -> 0x0060 }
            r4 = 3
            if (r2 == r4) goto L_0x0034
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            return
        L_0x0034:
            r5.mHaveDisabledApps = r3     // Catch:{ all -> 0x0060 }
        L_0x0036:
            boolean r2 = com.android.settingslib.applications.AppUtils.isInstant(r1)     // Catch:{ all -> 0x0060 }
            if (r2 == 0) goto L_0x003e
            r5.mHaveInstantApps = r3     // Catch:{ all -> 0x0060 }
        L_0x003e:
            java.util.List<android.content.pm.ApplicationInfo> r2 = r5.mApplications     // Catch:{ all -> 0x0060 }
            r2.add(r1)     // Catch:{ all -> 0x0060 }
            com.android.settingslib.applications.ApplicationsState$BackgroundHandler r2 = r5.mBackgroundHandler     // Catch:{ all -> 0x0060 }
            r3 = 2
            boolean r2 = r2.hasMessages(r3)     // Catch:{ all -> 0x0060 }
            if (r2 != 0) goto L_0x0051
            com.android.settingslib.applications.ApplicationsState$BackgroundHandler r2 = r5.mBackgroundHandler     // Catch:{ all -> 0x0060 }
            r2.sendEmptyMessage(r3)     // Catch:{ all -> 0x0060 }
        L_0x0051:
            com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r5.mMainHandler     // Catch:{ all -> 0x0060 }
            boolean r2 = r2.hasMessages(r3)     // Catch:{ all -> 0x0060 }
            if (r2 != 0) goto L_0x005e
            com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r5.mMainHandler     // Catch:{ all -> 0x0060 }
            r2.sendEmptyMessage(r3)     // Catch:{ all -> 0x0060 }
        L_0x005e:
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            goto L_0x0064
        L_0x0060:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0060 }
            throw r1     // Catch:{ RemoteException -> 0x0063 }
        L_0x0063:
            r0 = move-exception
        L_0x0064:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.addPackage(java.lang.String, int):void");
    }

    public void removePackage(String pkgName, int userId) {
        synchronized (this.mEntriesMap) {
            int idx = indexOfApplicationInfoLocked(pkgName, userId);
            if (idx >= 0) {
                AppEntry entry = (AppEntry) this.mEntriesMap.get(userId).get(pkgName);
                if (entry != null) {
                    this.mEntriesMap.get(userId).remove(pkgName);
                    this.mAppEntries.remove(entry);
                }
                ApplicationInfo info = this.mApplications.get(idx);
                this.mApplications.remove(idx);
                if (!info.enabled) {
                    this.mHaveDisabledApps = false;
                    Iterator<ApplicationInfo> it = this.mApplications.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (!it.next().enabled) {
                            this.mHaveDisabledApps = true;
                            break;
                        }
                    }
                }
                if (AppUtils.isInstant(info)) {
                    this.mHaveInstantApps = false;
                    Iterator<ApplicationInfo> it2 = this.mApplications.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        } else if (AppUtils.isInstant(it2.next())) {
                            this.mHaveInstantApps = true;
                            break;
                        }
                    }
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    public void invalidatePackage(String pkgName, int userId) {
        removePackage(pkgName, userId);
        addPackage(pkgName, userId);
    }

    /* access modifiers changed from: private */
    public void addUser(int userId) {
        if (ArrayUtils.contains(this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId()), userId)) {
            synchronized (this.mEntriesMap) {
                this.mEntriesMap.put(userId, new HashMap());
                if (this.mResumed) {
                    doPauseLocked();
                    doResumeIfNeededLocked();
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeUser(int userId) {
        synchronized (this.mEntriesMap) {
            HashMap<String, AppEntry> userMap = this.mEntriesMap.get(userId);
            if (userMap != null) {
                for (AppEntry appEntry : userMap.values()) {
                    this.mAppEntries.remove(appEntry);
                    this.mApplications.remove(appEntry.info);
                }
                this.mEntriesMap.remove(userId);
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public AppEntry getEntryLocked(ApplicationInfo info) {
        int userId = UserHandle.getUserId(info.uid);
        AppEntry entry = (AppEntry) this.mEntriesMap.get(userId).get(info.packageName);
        if (entry == null) {
            Context context = this.mContext;
            long j = this.mCurId;
            this.mCurId = 1 + j;
            AppEntry entry2 = new AppEntry(context, info, j);
            this.mEntriesMap.get(userId).put(info.packageName, entry2);
            this.mAppEntries.add(entry2);
            return entry2;
        } else if (entry.info == info) {
            return entry;
        } else {
            entry.info = info;
            return entry;
        }
    }

    /* access modifiers changed from: private */
    public long getTotalInternalSize(PackageStats ps) {
        if (ps != null) {
            return ps.codeSize + ps.dataSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    public long getTotalExternalSize(PackageStats ps) {
        if (ps != null) {
            return ps.externalCodeSize + ps.externalDataSize + ps.externalCacheSize + ps.externalMediaSize + ps.externalObbSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    public String getSizeStr(long size) {
        if (size >= 0) {
            return Formatter.formatFileSize(this.mContext, size);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void rebuildActiveSessions() {
        synchronized (this.mEntriesMap) {
            if (this.mSessionsChanged) {
                this.mActiveSessions.clear();
                for (int i = 0; i < this.mSessions.size(); i++) {
                    Session s = this.mSessions.get(i);
                    if (s.mResumed) {
                        this.mActiveSessions.add(new WeakReference(s));
                    }
                }
            }
        }
    }

    public static String normalize(String str) {
        return REMOVE_DIACRITICALS_PATTERN.matcher(Normalizer.normalize(str, Normalizer.Form.NFD)).replaceAll("").toLowerCase();
    }

    public class Session implements LifecycleObserver {
        final Callbacks mCallbacks;
        /* access modifiers changed from: private */
        public int mFlags = 15;
        private final boolean mHasLifecycle;
        ArrayList<AppEntry> mLastAppList;
        boolean mRebuildAsync;
        Comparator<AppEntry> mRebuildComparator;
        AppFilter mRebuildFilter;
        boolean mRebuildForeground;
        boolean mRebuildRequested;
        ArrayList<AppEntry> mRebuildResult;
        final Object mRebuildSync = new Object();
        boolean mResumed;

        Session(Callbacks callbacks, Lifecycle lifecycle) {
            this.mCallbacks = callbacks;
            if (lifecycle != null) {
                lifecycle.addObserver(this);
                this.mHasLifecycle = true;
                return;
            }
            this.mHasLifecycle = false;
        }

        public int getSessionFlags() {
            return this.mFlags;
        }

        public void setSessionFlags(int flags) {
            this.mFlags = flags;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            synchronized (ApplicationsState.this.mEntriesMap) {
                if (!this.mResumed) {
                    this.mResumed = true;
                    ApplicationsState.this.mSessionsChanged = true;
                    ApplicationsState.this.doResumeIfNeededLocked();
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            synchronized (ApplicationsState.this.mEntriesMap) {
                if (this.mResumed) {
                    this.mResumed = false;
                    ApplicationsState.this.mSessionsChanged = true;
                    ApplicationsState.this.mBackgroundHandler.removeMessages(1, this);
                    ApplicationsState.this.doPauseIfNeededLocked();
                }
            }
        }

        public ArrayList<AppEntry> getAllApps() {
            ArrayList<AppEntry> arrayList;
            synchronized (ApplicationsState.this.mEntriesMap) {
                arrayList = new ArrayList<>(ApplicationsState.this.mAppEntries);
            }
            return arrayList;
        }

        public ArrayList<AppEntry> rebuild(AppFilter filter, Comparator<AppEntry> comparator) {
            return rebuild(filter, comparator, true);
        }

        public ArrayList<AppEntry> rebuild(AppFilter filter, Comparator<AppEntry> comparator, boolean foreground) {
            synchronized (this.mRebuildSync) {
                synchronized (ApplicationsState.this.mRebuildingSessions) {
                    ApplicationsState.this.mRebuildingSessions.add(this);
                    this.mRebuildRequested = true;
                    this.mRebuildAsync = true;
                    this.mRebuildFilter = filter;
                    this.mRebuildComparator = comparator;
                    this.mRebuildForeground = foreground;
                    this.mRebuildResult = null;
                    if (!ApplicationsState.this.mBackgroundHandler.hasMessages(1)) {
                        ApplicationsState.this.mBackgroundHandler.sendMessage(ApplicationsState.this.mBackgroundHandler.obtainMessage(1));
                    }
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0020, code lost:
            if (r1 == null) goto L_0x0029;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0022, code lost:
            r1.init(r8.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0029, code lost:
            r4 = r8.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002d, code lost:
            monitor-enter(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
            r0 = new java.util.ArrayList<>(r8.this$0.mAppEntries);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0037, code lost:
            monitor-exit(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0038, code lost:
            r5 = new java.util.ArrayList<>();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0043, code lost:
            if (r3 >= r0.size()) goto L_0x0070;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0045, code lost:
            r4 = r0.get(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x004b, code lost:
            if (r4 == null) goto L_0x006d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004d, code lost:
            if (r1 == null) goto L_0x0055;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0053, code lost:
            if (r1.filterApp(r4) == false) goto L_0x006d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0055, code lost:
            r6 = r8.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0059, code lost:
            monitor-enter(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x005a, code lost:
            if (r2 == null) goto L_0x0066;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
            r4.ensureLabel(r8.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0064, code lost:
            r7 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0066, code lost:
            r5.add(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0069, code lost:
            monitor-exit(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x006c, code lost:
            throw r7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x006d, code lost:
            r3 = r3 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0070, code lost:
            if (r2 == null) goto L_0x007f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x0072, code lost:
            r3 = r8.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0076, code lost:
            monitor-enter(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
            java.util.Collections.sort(r5, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x007a, code lost:
            monitor-exit(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x007f, code lost:
            r3 = r8.mRebuildSync;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0081, code lost:
            monitor-enter(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x0084, code lost:
            if (r8.mRebuildRequested != false) goto L_0x00ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0086, code lost:
            r8.mLastAppList = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x008a, code lost:
            if (r8.mRebuildAsync != false) goto L_0x0094;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x008c, code lost:
            r8.mRebuildResult = r5;
            r8.mRebuildSync.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x009d, code lost:
            if (r8.this$0.mMainHandler.hasMessages(1, r8) != false) goto L_0x00ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x009f, code lost:
            r8.this$0.mMainHandler.sendMessage(r8.this$0.mMainHandler.obtainMessage(1, r8));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x00ae, code lost:
            monitor-exit(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00af, code lost:
            android.os.Process.setThreadPriority(10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00b4, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleRebuildList() {
            /*
                r8 = this;
                java.lang.Object r0 = r8.mRebuildSync
                monitor-enter(r0)
                boolean r1 = r8.mRebuildRequested     // Catch:{ all -> 0x00bb }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                return
            L_0x0009:
                com.android.settingslib.applications.ApplicationsState$AppFilter r1 = r8.mRebuildFilter     // Catch:{ all -> 0x00bb }
                java.util.Comparator<com.android.settingslib.applications.ApplicationsState$AppEntry> r2 = r8.mRebuildComparator     // Catch:{ all -> 0x00bb }
                r3 = 0
                r8.mRebuildRequested = r3     // Catch:{ all -> 0x00bb }
                r4 = 0
                r8.mRebuildFilter = r4     // Catch:{ all -> 0x00bb }
                r8.mRebuildComparator = r4     // Catch:{ all -> 0x00bb }
                boolean r4 = r8.mRebuildForeground     // Catch:{ all -> 0x00bb }
                if (r4 == 0) goto L_0x001f
                r4 = -2
                android.os.Process.setThreadPriority(r4)     // Catch:{ all -> 0x00bb }
                r8.mRebuildForeground = r3     // Catch:{ all -> 0x00bb }
            L_0x001f:
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                if (r1 == 0) goto L_0x0029
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                android.content.Context r0 = r0.mContext
                r1.init(r0)
            L_0x0029:
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r4 = r0.mEntriesMap
                monitor-enter(r4)
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x00b8 }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00b8 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r5 = r5.mAppEntries     // Catch:{ all -> 0x00b8 }
                r0.<init>(r5)     // Catch:{ all -> 0x00b8 }
                monitor-exit(r4)     // Catch:{ all -> 0x00b8 }
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r5 = r4
            L_0x003f:
                int r4 = r0.size()
                if (r3 >= r4) goto L_0x0070
                java.lang.Object r4 = r0.get(r3)
                com.android.settingslib.applications.ApplicationsState$AppEntry r4 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r4
                if (r4 == 0) goto L_0x006d
                if (r1 == 0) goto L_0x0055
                boolean r6 = r1.filterApp(r4)
                if (r6 == 0) goto L_0x006d
            L_0x0055:
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r6 = r6.mEntriesMap
                monitor-enter(r6)
                if (r2 == 0) goto L_0x0066
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0064 }
                android.content.Context r7 = r7.mContext     // Catch:{ all -> 0x0064 }
                r4.ensureLabel(r7)     // Catch:{ all -> 0x0064 }
                goto L_0x0066
            L_0x0064:
                r7 = move-exception
                goto L_0x006b
            L_0x0066:
                r5.add(r4)     // Catch:{ all -> 0x0064 }
                monitor-exit(r6)     // Catch:{ all -> 0x0064 }
                goto L_0x006d
            L_0x006b:
                monitor-exit(r6)     // Catch:{ all -> 0x0064 }
                throw r7
            L_0x006d:
                int r3 = r3 + 1
                goto L_0x003f
            L_0x0070:
                if (r2 == 0) goto L_0x007f
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r3 = r3.mEntriesMap
                monitor-enter(r3)
                java.util.Collections.sort(r5, r2)     // Catch:{ all -> 0x007c }
                monitor-exit(r3)     // Catch:{ all -> 0x007c }
                goto L_0x007f
            L_0x007c:
                r4 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x007c }
                throw r4
            L_0x007f:
                java.lang.Object r3 = r8.mRebuildSync
                monitor-enter(r3)
                boolean r4 = r8.mRebuildRequested     // Catch:{ all -> 0x00b5 }
                if (r4 != 0) goto L_0x00ae
                r8.mLastAppList = r5     // Catch:{ all -> 0x00b5 }
                boolean r4 = r8.mRebuildAsync     // Catch:{ all -> 0x00b5 }
                if (r4 != 0) goto L_0x0094
                r8.mRebuildResult = r5     // Catch:{ all -> 0x00b5 }
                java.lang.Object r4 = r8.mRebuildSync     // Catch:{ all -> 0x00b5 }
                r4.notifyAll()     // Catch:{ all -> 0x00b5 }
                goto L_0x00ae
            L_0x0094:
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r4 = r4.mMainHandler     // Catch:{ all -> 0x00b5 }
                r6 = 1
                boolean r4 = r4.hasMessages(r6, r8)     // Catch:{ all -> 0x00b5 }
                if (r4 != 0) goto L_0x00ae
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r4 = r4.mMainHandler     // Catch:{ all -> 0x00b5 }
                android.os.Message r4 = r4.obtainMessage(r6, r8)     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x00b5 }
                r6.sendMessage(r4)     // Catch:{ all -> 0x00b5 }
            L_0x00ae:
                monitor-exit(r3)     // Catch:{ all -> 0x00b5 }
                r3 = 10
                android.os.Process.setThreadPriority(r3)
                return
            L_0x00b5:
                r4 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00b5 }
                throw r4
            L_0x00b8:
                r0 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x00b8 }
                throw r0
            L_0x00bb:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.Session.handleRebuildList():void");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            if (!this.mHasLifecycle) {
                onPause();
            }
            synchronized (ApplicationsState.this.mEntriesMap) {
                ApplicationsState.this.mSessions.remove(this);
            }
        }
    }

    class MainHandler extends Handler {
        static final int MSG_ALL_SIZES_COMPUTED = 5;
        static final int MSG_LAUNCHER_INFO_CHANGED = 7;
        static final int MSG_LOAD_ENTRIES_COMPLETE = 8;
        static final int MSG_PACKAGE_ICON_CHANGED = 3;
        static final int MSG_PACKAGE_LIST_CHANGED = 2;
        static final int MSG_PACKAGE_SIZE_CHANGED = 4;
        static final int MSG_REBUILD_COMPLETE = 1;
        static final int MSG_RUNNING_STATE_CHANGED = 6;

        public MainHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            ApplicationsState.this.rebuildActiveSessions();
            switch (msg.what) {
                case 1:
                    Session s = (Session) msg.obj;
                    Iterator<WeakReference<Session>> it = ApplicationsState.this.mActiveSessions.iterator();
                    while (it.hasNext()) {
                        Session session = (Session) it.next().get();
                        if (session != null && session == s) {
                            s.mCallbacks.onRebuildComplete(s.mLastAppList);
                        }
                    }
                    return;
                case 2:
                    Iterator<WeakReference<Session>> it2 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it2.hasNext()) {
                        Session session2 = (Session) it2.next().get();
                        if (session2 != null) {
                            session2.mCallbacks.onPackageListChanged();
                        }
                    }
                    return;
                case 3:
                    Iterator<WeakReference<Session>> it3 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it3.hasNext()) {
                        Session session3 = (Session) it3.next().get();
                        if (session3 != null) {
                            session3.mCallbacks.onPackageIconChanged();
                        }
                    }
                    return;
                case 4:
                    Iterator<WeakReference<Session>> it4 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it4.hasNext()) {
                        Session session4 = (Session) it4.next().get();
                        if (session4 != null) {
                            session4.mCallbacks.onPackageSizeChanged((String) msg.obj);
                        }
                    }
                    return;
                case 5:
                    Iterator<WeakReference<Session>> it5 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it5.hasNext()) {
                        Session session5 = (Session) it5.next().get();
                        if (session5 != null) {
                            session5.mCallbacks.onAllSizesComputed();
                        }
                    }
                    return;
                case 6:
                    Iterator<WeakReference<Session>> it6 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it6.hasNext()) {
                        Session session6 = (Session) it6.next().get();
                        if (session6 != null) {
                            session6.mCallbacks.onRunningStateChanged(msg.arg1 != 0);
                        }
                    }
                    return;
                case 7:
                    Iterator<WeakReference<Session>> it7 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it7.hasNext()) {
                        Session session7 = (Session) it7.next().get();
                        if (session7 != null) {
                            session7.mCallbacks.onLauncherInfoChanged();
                        }
                    }
                    return;
                case 8:
                    Iterator<WeakReference<Session>> it8 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it8.hasNext()) {
                        Session session8 = (Session) it8.next().get();
                        if (session8 != null) {
                            session8.mCallbacks.onLoadEntriesCompleted();
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private class BackgroundHandler extends Handler {
        static final int MSG_LOAD_ENTRIES = 2;
        static final int MSG_LOAD_HOME_APP = 3;
        static final int MSG_LOAD_ICONS = 6;
        static final int MSG_LOAD_LAUNCHER = 4;
        static final int MSG_LOAD_LEANBACK_LAUNCHER = 5;
        static final int MSG_LOAD_SIZES = 7;
        static final int MSG_REBUILD_LIST = 1;
        boolean mRunning;
        final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
                java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
                	at java.base/jdk.internal.util.Preconditions.outOfBounds(Unknown Source)
                	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Unknown Source)
                	at java.base/jdk.internal.util.Preconditions.checkIndex(Unknown Source)
                	at java.base/java.util.Objects.checkIndex(Unknown Source)
                	at java.base/java.util.ArrayList.get(Unknown Source)
                	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
                */
            public void onGetStatsCompleted(android.content.pm.PackageStats r18, boolean r19) {
                /*
                    r17 = this;
                    r1 = r17
                    r2 = r18
                    if (r19 != 0) goto L_0x0007
                    return
                L_0x0007:
                    r4 = 0
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                    android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r5 = r0.mEntriesMap
                    monitor-enter(r5)
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012a }
                    android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r0 = r0.mEntriesMap     // Catch:{ all -> 0x012a }
                    int r6 = r2.userHandle     // Catch:{ all -> 0x012a }
                    java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x012a }
                    java.util.HashMap r0 = (java.util.HashMap) r0     // Catch:{ all -> 0x012a }
                    r6 = r0
                    if (r6 != 0) goto L_0x0022
                    monitor-exit(r5)     // Catch:{ all -> 0x012f }
                    return
                L_0x0022:
                    java.lang.String r0 = r2.packageName     // Catch:{ all -> 0x012a }
                    java.lang.Object r0 = r6.get(r0)     // Catch:{ all -> 0x012a }
                    com.android.settingslib.applications.ApplicationsState$AppEntry r0 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r0     // Catch:{ all -> 0x012a }
                    r7 = r0
                    if (r7 == 0) goto L_0x00f9
                    monitor-enter(r7)     // Catch:{ all -> 0x012a }
                    r0 = 0
                    r7.sizeStale = r0     // Catch:{ all -> 0x00f2 }
                    r8 = 0
                    r7.sizeLoadStart = r8     // Catch:{ all -> 0x00f2 }
                    long r8 = r2.externalCodeSize     // Catch:{ all -> 0x00f2 }
                    long r10 = r2.externalObbSize     // Catch:{ all -> 0x00f2 }
                    long r8 = r8 + r10
                    long r10 = r2.externalDataSize     // Catch:{ all -> 0x00f2 }
                    long r12 = r2.externalMediaSize     // Catch:{ all -> 0x00f2 }
                    long r10 = r10 + r12
                    long r12 = r8 + r10
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00f2 }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00f2 }
                    long r14 = r0.getTotalInternalSize(r2)     // Catch:{ all -> 0x00f2 }
                    long r12 = r12 + r14
                    long r14 = r7.size     // Catch:{ all -> 0x00f2 }
                    int r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
                    if (r0 != 0) goto L_0x0082
                    long r14 = r7.cacheSize     // Catch:{ all -> 0x00f2 }
                    r16 = r4
                    long r3 = r2.cacheSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
                    if (r0 != 0) goto L_0x0084
                    long r3 = r7.codeSize     // Catch:{ all -> 0x00ee }
                    long r14 = r2.codeSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
                    if (r0 != 0) goto L_0x0084
                    long r3 = r7.dataSize     // Catch:{ all -> 0x00ee }
                    long r14 = r2.dataSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
                    if (r0 != 0) goto L_0x0084
                    long r3 = r7.externalCodeSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                    if (r0 != 0) goto L_0x0084
                    long r3 = r7.externalDataSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
                    if (r0 != 0) goto L_0x0084
                    long r3 = r7.externalCacheSize     // Catch:{ all -> 0x00ee }
                    long r14 = r2.externalCacheSize     // Catch:{ all -> 0x00ee }
                    int r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
                    if (r0 == 0) goto L_0x007f
                    goto L_0x0084
                L_0x007f:
                    r4 = r16
                    goto L_0x00d4
                L_0x0082:
                    r16 = r4
                L_0x0084:
                    r7.size = r12     // Catch:{ all -> 0x00ee }
                    long r3 = r2.cacheSize     // Catch:{ all -> 0x00ee }
                    r7.cacheSize = r3     // Catch:{ all -> 0x00ee }
                    long r3 = r2.codeSize     // Catch:{ all -> 0x00ee }
                    r7.codeSize = r3     // Catch:{ all -> 0x00ee }
                    long r3 = r2.dataSize     // Catch:{ all -> 0x00ee }
                    r7.dataSize = r3     // Catch:{ all -> 0x00ee }
                    r7.externalCodeSize = r8     // Catch:{ all -> 0x00ee }
                    r7.externalDataSize = r10     // Catch:{ all -> 0x00ee }
                    long r3 = r2.externalCacheSize     // Catch:{ all -> 0x00ee }
                    r7.externalCacheSize = r3     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00ee }
                    long r3 = r7.size     // Catch:{ all -> 0x00ee }
                    java.lang.String r0 = r0.getSizeStr(r3)     // Catch:{ all -> 0x00ee }
                    r7.sizeStr = r0     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00ee }
                    long r3 = r0.getTotalInternalSize(r2)     // Catch:{ all -> 0x00ee }
                    r7.internalSize = r3     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00ee }
                    long r3 = r7.internalSize     // Catch:{ all -> 0x00ee }
                    java.lang.String r0 = r0.getSizeStr(r3)     // Catch:{ all -> 0x00ee }
                    r7.internalSizeStr = r0     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00ee }
                    long r3 = r0.getTotalExternalSize(r2)     // Catch:{ all -> 0x00ee }
                    r7.externalSize = r3     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00ee }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00ee }
                    long r3 = r7.externalSize     // Catch:{ all -> 0x00ee }
                    java.lang.String r0 = r0.getSizeStr(r3)     // Catch:{ all -> 0x00ee }
                    r7.externalSizeStr = r0     // Catch:{ all -> 0x00ee }
                    r0 = 1
                    r4 = r0
                L_0x00d4:
                    monitor-exit(r7)     // Catch:{ all -> 0x00f7 }
                    if (r4 == 0) goto L_0x00fb
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler     // Catch:{ all -> 0x012f }
                    r3 = 4
                    java.lang.String r8 = r2.packageName     // Catch:{ all -> 0x012f }
                    android.os.Message r0 = r0.obtainMessage(r3, r8)     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r3 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState$MainHandler r3 = r3.mMainHandler     // Catch:{ all -> 0x012f }
                    r3.sendMessage(r0)     // Catch:{ all -> 0x012f }
                    goto L_0x00fb
                L_0x00ee:
                    r0 = move-exception
                    r4 = r16
                    goto L_0x00f5
                L_0x00f2:
                    r0 = move-exception
                    r16 = r4
                L_0x00f5:
                    monitor-exit(r7)     // Catch:{ all -> 0x00f7 }
                    throw r0     // Catch:{ all -> 0x012f }
                L_0x00f7:
                    r0 = move-exception
                    goto L_0x00f5
                L_0x00f9:
                    r16 = r4
                L_0x00fb:
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    java.lang.String r0 = r0.mCurComputingSizePkg     // Catch:{ all -> 0x012f }
                    if (r0 == 0) goto L_0x0128
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    java.lang.String r0 = r0.mCurComputingSizePkg     // Catch:{ all -> 0x012f }
                    java.lang.String r3 = r2.packageName     // Catch:{ all -> 0x012f }
                    boolean r0 = r0.equals(r3)     // Catch:{ all -> 0x012f }
                    if (r0 == 0) goto L_0x0128
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    int r0 = r0.mCurComputingSizeUserId     // Catch:{ all -> 0x012f }
                    int r3 = r2.userHandle     // Catch:{ all -> 0x012f }
                    if (r0 != r3) goto L_0x0128
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x012f }
                    r3 = 0
                    r0.mCurComputingSizePkg = r3     // Catch:{ all -> 0x012f }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x012f }
                    r3 = 7
                    r0.sendEmptyMessage(r3)     // Catch:{ all -> 0x012f }
                L_0x0128:
                    monitor-exit(r5)     // Catch:{ all -> 0x012f }
                    return
                L_0x012a:
                    r0 = move-exception
                    r16 = r4
                L_0x012d:
                    monitor-exit(r5)     // Catch:{ all -> 0x012f }
                    throw r0
                L_0x012f:
                    r0 = move-exception
                    goto L_0x012d
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.BackgroundHandler.AnonymousClass1.onGetStatsCompleted(android.content.pm.PackageStats, boolean):void");
            }
        };

        BackgroundHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:101:0x01b9, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r4, 16) == false) goto L_0x02a6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:102:0x01bb, code lost:
            r5 = new android.content.Intent("android.intent.action.MAIN", (android.net.Uri) null);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x01c5, code lost:
            if (r2.what != 4) goto L_0x01ca;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:104:0x01c7, code lost:
            r6 = "android.intent.category.LAUNCHER";
         */
        /* JADX WARNING: Code restructure failed: missing block: B:105:0x01ca, code lost:
            r6 = android.support.v4.content.IntentCompat.CATEGORY_LEANBACK_LAUNCHER;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:106:0x01cc, code lost:
            r5.addCategory(r6);
            r6 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:108:0x01d8, code lost:
            if (r6 >= r1.this$0.mEntriesMap.size()) goto L_0x028f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:109:0x01da, code lost:
            r7 = r1.this$0.mEntriesMap.keyAt(r6);
            r9 = r1.this$0.mPm.queryIntentActivitiesAsUser(r5, 786944, r7);
            r10 = r1.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
            r4 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:110:0x01f1, code lost:
            monitor-enter(r10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:112:?, code lost:
            r11 = r1.this$0.mEntriesMap.valueAt(r6);
            r18 = r9.size();
            r16 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:113:0x0204, code lost:
            r15 = r18;
            r0 = r16;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:114:0x020a, code lost:
            if (r0 >= r15) goto L_0x0274;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:115:0x020c, code lost:
            r13 = r9.get(r0);
            r12 = r13.activityInfo.packageName;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:116:0x0220, code lost:
            r8 = r11.get(r12);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x0224, code lost:
            if (r8 == null) goto L_0x0240;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:?, code lost:
            r8.hasLauncherEntry = r14;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x022a, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:?, code lost:
            r8.launcherEntryEnabled = r13.activityInfo.enabled | r8.launcherEntryEnabled;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:123:0x0233, code lost:
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x0236, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:125:0x0237, code lost:
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x023a, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:127:0x023b, code lost:
            r23 = r3;
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:128:0x0240, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
            if (r4 >= r3.size()) goto L_0x003c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:130:?, code lost:
            r14 = new java.lang.StringBuilder();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:131:0x0249, code lost:
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:133:?, code lost:
            r14.append("Cannot find pkg: ");
            r14.append(r12);
            r14.append(" on user ");
            r14.append(r7);
            android.util.Log.w(com.android.settingslib.applications.ApplicationsState.TAG, r14.toString());
         */
        /* JADX WARNING: Code restructure failed: missing block: B:134:0x0262, code lost:
            r16 = r0 + 1;
            r18 = r15;
            r3 = r23;
            r5 = r24;
            r14 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x0270, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:136:0x0271, code lost:
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:137:0x0274, code lost:
            r23 = r3;
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:138:0x0278, code lost:
            monitor-exit(r10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:139:0x0279, code lost:
            r6 = r6 + 1;
            r3 = r23;
            r5 = r24;
            r0 = 0;
            r14 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0030, code lost:
            r3.get(r4).handleRebuildList();
            r4 = r4 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:140:0x0286, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:141:0x0287, code lost:
            r23 = r3;
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:142:0x028b, code lost:
            monitor-exit(r10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:143:0x028c, code lost:
            throw r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:144:0x028d, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:145:0x028f, code lost:
            r23 = r3;
            r24 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:146:0x029c, code lost:
            if (r1.this$0.mMainHandler.hasMessages(7) != false) goto L_0x02a8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:147:0x029e, code lost:
            r1.this$0.mMainHandler.sendEmptyMessage(7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:148:0x02a6, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
            r4 = getCombinedSessionFlags(r1.this$0.mSessions);
            r6 = 8388608;
            r14 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:150:0x02ab, code lost:
            if (r2.what != 4) goto L_0x02b3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:151:0x02ad, code lost:
            sendEmptyMessage(5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:152:0x02b3, code lost:
            sendEmptyMessage(6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:153:0x02b9, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:154:0x02c0, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r4, 1) == false) goto L_0x0314;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:155:0x02c2, code lost:
            r3 = new java.util.ArrayList<>();
            r1.this$0.mPm.getHomeActivities(r3);
            r5 = r1.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:156:0x02d3, code lost:
            monitor-enter(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:158:?, code lost:
            r0 = r1.this$0.mEntriesMap.size();
            r20 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:159:0x02de, code lost:
            r6 = r20;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0050, code lost:
            switch(r2.what) {
                case 1: goto L_0x03e3;
                case 2: goto L_0x031a;
                case 3: goto L_0x02b9;
                case 4: goto L_0x01a4;
                case 5: goto L_0x01a4;
                case 6: goto L_0x0124;
                case 7: goto L_0x0057;
                default: goto L_0x0053;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:160:0x02e0, code lost:
            if (r6 >= r0) goto L_0x030f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:161:0x02e2, code lost:
            r7 = r1.this$0.mEntriesMap.valueAt(r6);
            r8 = r3.iterator();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:163:0x02f4, code lost:
            if (r8.hasNext() == false) goto L_0x030c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:164:0x02f6, code lost:
            r11 = r7.get(r8.next().activityInfo.packageName);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:165:0x0306, code lost:
            if (r11 == null) goto L_0x030b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:166:0x0308, code lost:
            r11.isHomeApp = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:168:0x030c, code lost:
            r20 = r6 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:169:0x030f, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0053, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:174:0x0314, code lost:
            sendEmptyMessage(4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:175:0x031a, code lost:
            r23 = r3;
            r3 = r1.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:176:0x0321, code lost:
            monitor-enter(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:177:0x0322, code lost:
            r5 = 0;
            r0 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:180:0x032c, code lost:
            if (r0 >= r1.this$0.mApplications.size()) goto L_0x03c3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:182:0x032f, code lost:
            if (r5 >= 6) goto L_0x03c3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:184:0x0333, code lost:
            if (r1.mRunning != false) goto L_0x034d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:185:0x0335, code lost:
            r1.mRunning = true;
            r1.this$0.mMainHandler.sendMessage(r1.this$0.mMainHandler.obtainMessage(6, 1));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:187:0x034e, code lost:
            r11 = r1.this$0.mApplications.get(r0);
            r12 = android.os.UserHandle.getUserId(r11.uid);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:188:0x036e, code lost:
            if (r1.this$0.mEntriesMap.get(r12).get(r11.packageName) != null) goto L_0x0377;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:189:0x0370, code lost:
            r5 = r5 + 1;
            com.android.settingslib.applications.ApplicationsState.access$100(r1.this$0, r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x005b, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r4, 4) == false) goto L_0x0120;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:190:0x0377, code lost:
            if (r12 == 0) goto L_0x03bc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:192:0x0382, code lost:
            if (r1.this$0.mEntriesMap.indexOfKey(0) < 0) goto L_0x03ba;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:193:0x0384, code lost:
            r13 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r1.this$0.mEntriesMap.get(0).get(r11.packageName);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:194:0x0396, code lost:
            if (r13 == null) goto L_0x03bc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:196:0x03a0, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r13.info.flags, r6) != false) goto L_0x03bc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:197:0x03a2, code lost:
            r1.this$0.mEntriesMap.get(0).remove(r11.packageName);
            r1.this$0.mAppEntries.remove(r13);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x005d, code lost:
            r5 = r1.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:200:0x03bd, code lost:
            r0 = r0 + 1;
            r6 = 8388608;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:201:0x03c3, code lost:
            monitor-exit(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:203:0x03c5, code lost:
            if (r5 < 6) goto L_0x03cb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:204:0x03c7, code lost:
            sendEmptyMessage(2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:206:0x03d3, code lost:
            if (r1.this$0.mMainHandler.hasMessages(8) != false) goto L_0x03dc;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:207:0x03d5, code lost:
            r1.this$0.mMainHandler.sendEmptyMessage(8);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:208:0x03dc, code lost:
            sendEmptyMessage(3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0061, code lost:
            monitor-enter(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:213:0x03e3, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0066, code lost:
            if (r1.this$0.mCurComputingSizePkg == null) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0068, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:250:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:251:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:252:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:253:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:254:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:255:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:256:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:257:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0069, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x006a, code lost:
            r7 = android.os.SystemClock.uptimeMillis();
            r9 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0077, code lost:
            if (r9 >= r1.this$0.mAppEntries.size()) goto L_0x00f4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0079, code lost:
            r10 = r1.this$0.mAppEntries.get(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x008b, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r10.info.flags, 8388608) == false) goto L_0x00f0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0093, code lost:
            if (r10.size == -1) goto L_0x0099;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0097, code lost:
            if (r10.sizeStale == false) goto L_0x00f0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x009f, code lost:
            if (r10.sizeLoadStart == 0) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a9, code lost:
            if (r10.sizeLoadStart >= (r7 - 20000)) goto L_0x00ee;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ad, code lost:
            if (r1.mRunning != false) goto L_0x00c4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00af, code lost:
            r1.mRunning = true;
            r1.this$0.mMainHandler.sendMessage(r1.this$0.mMainHandler.obtainMessage(6, 1));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c4, code lost:
            r10.sizeLoadStart = r7;
            r1.this$0.mCurComputingSizeUuid = r10.info.storageUuid;
            r1.this$0.mCurComputingSizePkg = r10.info.packageName;
            r1.this$0.mCurComputingSizeUserId = android.os.UserHandle.getUserId(r10.info.uid);
            r1.this$0.mBackgroundHandler.post(new com.android.settingslib.applications.$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc(r1));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ee, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ef, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f0, code lost:
            r9 = r9 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00fd, code lost:
            if (r1.this$0.mMainHandler.hasMessages(5) != false) goto L_0x011b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ff, code lost:
            r1.this$0.mMainHandler.sendEmptyMessage(5);
            r1.mRunning = false;
            r1.this$0.mMainHandler.sendMessage(r1.this$0.mMainHandler.obtainMessage(6, 0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x011b, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0120, code lost:
            r23 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x0128, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r4, 2) == false) goto L_0x019f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x012a, code lost:
            r5 = 0;
            r6 = r1.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x012f, code lost:
            monitor-enter(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x0131, code lost:
            r7 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x013a, code lost:
            if (r7 >= r1.this$0.mAppEntries.size()) goto L_0x0182;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x013c, code lost:
            if (r5 >= 2) goto L_0x0182;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x013e, code lost:
            r11 = r1.this$0.mAppEntries.get(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x014b, code lost:
            if (r11.icon == null) goto L_0x0151;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x014f, code lost:
            if (r11.mounted != false) goto L_0x017c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x0151, code lost:
            monitor-enter(r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x015e, code lost:
            if (r11.ensureIconLocked(r1.this$0.mContext, r1.this$0.mDrawableFactory) == false) goto L_0x017b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:73:0x0162, code lost:
            if (r1.mRunning != false) goto L_0x0179;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:0x0164, code lost:
            r1.mRunning = true;
            r1.this$0.mMainHandler.sendMessage(r1.this$0.mMainHandler.obtainMessage(6, 1));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:75:0x0179, code lost:
            r5 = r5 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:76:0x017b, code lost:
            monitor-exit(r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:77:0x017c, code lost:
            r0 = r7 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:82:0x0182, code lost:
            monitor-exit(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:83:0x0183, code lost:
            if (r5 <= 0) goto L_0x0196;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x018d, code lost:
            if (r1.this$0.mMainHandler.hasMessages(3) != false) goto L_0x0196;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x018f, code lost:
            r1.this$0.mMainHandler.sendEmptyMessage(3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:0x0196, code lost:
            if (r5 < 2) goto L_0x019f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x0198, code lost:
            sendEmptyMessage(6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
            r0 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x019f, code lost:
            sendEmptyMessage(7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x01a6, code lost:
            if (r2.what != 4) goto L_0x01ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x01ac, code lost:
            if (com.android.settingslib.applications.ApplicationsState.access$200(r4, 8) != false) goto L_0x01bb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:99:0x01b1, code lost:
            if (r2.what != 5) goto L_0x02a6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0027, code lost:
            if (r3 == null) goto L_0x003c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r27) {
            /*
                r26 = this;
                r1 = r26
                r2 = r27
                r3 = 0
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r4 = r0.mRebuildingSessions
                monitor-enter(r4)
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03ea }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r0 = r0.mRebuildingSessions     // Catch:{ all -> 0x03ea }
                int r0 = r0.size()     // Catch:{ all -> 0x03ea }
                if (r0 <= 0) goto L_0x0025
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x03ea }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03ea }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r5 = r5.mRebuildingSessions     // Catch:{ all -> 0x03ea }
                r0.<init>(r5)     // Catch:{ all -> 0x03ea }
                r3 = r0
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03ea }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r0 = r0.mRebuildingSessions     // Catch:{ all -> 0x03ea }
                r0.clear()     // Catch:{ all -> 0x03ea }
            L_0x0025:
                monitor-exit(r4)     // Catch:{ all -> 0x03e6 }
                r0 = 0
                if (r3 == 0) goto L_0x003c
                r4 = r0
            L_0x002a:
                int r5 = r3.size()
                if (r4 >= r5) goto L_0x003c
                java.lang.Object r5 = r3.get(r4)
                com.android.settingslib.applications.ApplicationsState$Session r5 = (com.android.settingslib.applications.ApplicationsState.Session) r5
                r5.handleRebuildList()
                int r4 = r4 + 1
                goto L_0x002a
            L_0x003c:
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r4 = r4.mSessions
                int r4 = r1.getCombinedSessionFlags(r4)
                int r5 = r2.what
                r6 = 8388608(0x800000, float:1.17549435E-38)
                r7 = 8
                r8 = 7
                r9 = 3
                r10 = 2
                r12 = 4
                r13 = 6
                r14 = 1
                switch(r5) {
                    case 1: goto L_0x03e3;
                    case 2: goto L_0x031a;
                    case 3: goto L_0x02b9;
                    case 4: goto L_0x01a4;
                    case 5: goto L_0x01a4;
                    case 6: goto L_0x0124;
                    case 7: goto L_0x0057;
                    default: goto L_0x0053;
                }
            L_0x0053:
                r23 = r3
                goto L_0x03e5
            L_0x0057:
                boolean r5 = com.android.settingslib.applications.ApplicationsState.hasFlag(r4, r12)
                if (r5 == 0) goto L_0x0120
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r5 = r5.mEntriesMap
                monitor-enter(r5)
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                java.lang.String r7 = r7.mCurComputingSizePkg     // Catch:{ all -> 0x011d }
                if (r7 == 0) goto L_0x006a
                monitor-exit(r5)     // Catch:{ all -> 0x011d }
                return
            L_0x006a:
                long r7 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x011d }
                r9 = r0
            L_0x006f:
                com.android.settingslib.applications.ApplicationsState r10 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r10 = r10.mAppEntries     // Catch:{ all -> 0x011d }
                int r10 = r10.size()     // Catch:{ all -> 0x011d }
                if (r9 >= r10) goto L_0x00f4
                com.android.settingslib.applications.ApplicationsState r10 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r10 = r10.mAppEntries     // Catch:{ all -> 0x011d }
                java.lang.Object r10 = r10.get(r9)     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$AppEntry r10 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r10     // Catch:{ all -> 0x011d }
                android.content.pm.ApplicationInfo r12 = r10.info     // Catch:{ all -> 0x011d }
                int r12 = r12.flags     // Catch:{ all -> 0x011d }
                boolean r12 = com.android.settingslib.applications.ApplicationsState.hasFlag(r12, r6)     // Catch:{ all -> 0x011d }
                if (r12 == 0) goto L_0x00f0
                long r11 = r10.size     // Catch:{ all -> 0x011d }
                r16 = -1
                int r11 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
                if (r11 == 0) goto L_0x0099
                boolean r11 = r10.sizeStale     // Catch:{ all -> 0x011d }
                if (r11 == 0) goto L_0x00f0
            L_0x0099:
                long r11 = r10.sizeLoadStart     // Catch:{ all -> 0x011d }
                r15 = 0
                int r0 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
                if (r0 == 0) goto L_0x00ab
                long r11 = r10.sizeLoadStart     // Catch:{ all -> 0x011d }
                r15 = 20000(0x4e20, double:9.8813E-320)
                long r15 = r7 - r15
                int r0 = (r11 > r15 ? 1 : (r11 == r15 ? 0 : -1))
                if (r0 >= 0) goto L_0x00ee
            L_0x00ab:
                boolean r0 = r1.mRunning     // Catch:{ all -> 0x011d }
                if (r0 != 0) goto L_0x00c4
                r1.mRunning = r14     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler     // Catch:{ all -> 0x011d }
                java.lang.Integer r6 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x011d }
                android.os.Message r0 = r0.obtainMessage(r13, r6)     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x011d }
                r6.sendMessage(r0)     // Catch:{ all -> 0x011d }
            L_0x00c4:
                r10.sizeLoadStart = r7     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                android.content.pm.ApplicationInfo r6 = r10.info     // Catch:{ all -> 0x011d }
                java.util.UUID r6 = r6.storageUuid     // Catch:{ all -> 0x011d }
                r0.mCurComputingSizeUuid = r6     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                android.content.pm.ApplicationInfo r6 = r10.info     // Catch:{ all -> 0x011d }
                java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x011d }
                r0.mCurComputingSizePkg = r6     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                android.content.pm.ApplicationInfo r6 = r10.info     // Catch:{ all -> 0x011d }
                int r6 = r6.uid     // Catch:{ all -> 0x011d }
                int r6 = android.os.UserHandle.getUserId(r6)     // Catch:{ all -> 0x011d }
                r0.mCurComputingSizeUserId = r6     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = r0.mBackgroundHandler     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.-$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc r6 = new com.android.settingslib.applications.-$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc     // Catch:{ all -> 0x011d }
                r6.<init>()     // Catch:{ all -> 0x011d }
                r0.post(r6)     // Catch:{ all -> 0x011d }
            L_0x00ee:
                monitor-exit(r5)     // Catch:{ all -> 0x011d }
                return
            L_0x00f0:
                int r9 = r9 + 1
                goto L_0x006f
            L_0x00f4:
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x011d }
                r9 = 5
                boolean r6 = r6.hasMessages(r9)     // Catch:{ all -> 0x011d }
                if (r6 != 0) goto L_0x011b
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x011d }
                r6.sendEmptyMessage(r9)     // Catch:{ all -> 0x011d }
                r1.mRunning = r0     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x011d }
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x011d }
                android.os.Message r0 = r6.obtainMessage(r13, r0)     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011d }
                com.android.settingslib.applications.ApplicationsState$MainHandler r6 = r6.mMainHandler     // Catch:{ all -> 0x011d }
                r6.sendMessage(r0)     // Catch:{ all -> 0x011d }
            L_0x011b:
                monitor-exit(r5)     // Catch:{ all -> 0x011d }
                goto L_0x0120
            L_0x011d:
                r0 = move-exception
                monitor-exit(r5)     // Catch:{ all -> 0x011d }
                throw r0
            L_0x0120:
                r23 = r3
                goto L_0x03e5
            L_0x0124:
                boolean r5 = com.android.settingslib.applications.ApplicationsState.hasFlag(r4, r10)
                if (r5 == 0) goto L_0x019f
                r5 = 0
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r6 = r6.mEntriesMap
                monitor-enter(r6)
            L_0x0131:
                r7 = r0
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x019c }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r0 = r0.mAppEntries     // Catch:{ all -> 0x019c }
                int r0 = r0.size()     // Catch:{ all -> 0x019c }
                if (r7 >= r0) goto L_0x0182
                if (r5 >= r10) goto L_0x0182
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x019c }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r0 = r0.mAppEntries     // Catch:{ all -> 0x019c }
                java.lang.Object r0 = r0.get(r7)     // Catch:{ all -> 0x019c }
                com.android.settingslib.applications.ApplicationsState$AppEntry r0 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r0     // Catch:{ all -> 0x019c }
                r11 = r0
                android.graphics.drawable.Drawable r0 = r11.icon     // Catch:{ all -> 0x019c }
                if (r0 == 0) goto L_0x0151
                boolean r0 = r11.mounted     // Catch:{ all -> 0x019c }
                if (r0 != 0) goto L_0x017c
            L_0x0151:
                monitor-enter(r11)     // Catch:{ all -> 0x019c }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x017f }
                android.content.Context r0 = r0.mContext     // Catch:{ all -> 0x017f }
                com.android.settingslib.applications.ApplicationsState r12 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x017f }
                android.util.IconDrawableFactory r12 = r12.mDrawableFactory     // Catch:{ all -> 0x017f }
                boolean r0 = r11.ensureIconLocked(r0, r12)     // Catch:{ all -> 0x017f }
                if (r0 == 0) goto L_0x017b
                boolean r0 = r1.mRunning     // Catch:{ all -> 0x017f }
                if (r0 != 0) goto L_0x0179
                r1.mRunning = r14     // Catch:{ all -> 0x017f }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x017f }
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler     // Catch:{ all -> 0x017f }
                java.lang.Integer r12 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x017f }
                android.os.Message r0 = r0.obtainMessage(r13, r12)     // Catch:{ all -> 0x017f }
                com.android.settingslib.applications.ApplicationsState r12 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x017f }
                com.android.settingslib.applications.ApplicationsState$MainHandler r12 = r12.mMainHandler     // Catch:{ all -> 0x017f }
                r12.sendMessage(r0)     // Catch:{ all -> 0x017f }
            L_0x0179:
                int r5 = r5 + 1
            L_0x017b:
                monitor-exit(r11)     // Catch:{ all -> 0x017f }
            L_0x017c:
                int r0 = r7 + 1
                goto L_0x0131
            L_0x017f:
                r0 = move-exception
                monitor-exit(r11)     // Catch:{ all -> 0x017f }
                throw r0     // Catch:{ all -> 0x019c }
            L_0x0182:
                monitor-exit(r6)     // Catch:{ all -> 0x019c }
                if (r5 <= 0) goto L_0x0196
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                boolean r0 = r0.hasMessages(r9)
                if (r0 != 0) goto L_0x0196
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                r0.sendEmptyMessage(r9)
            L_0x0196:
                if (r5 < r10) goto L_0x019f
                r1.sendEmptyMessage(r13)
                goto L_0x0120
            L_0x019c:
                r0 = move-exception
                monitor-exit(r6)     // Catch:{ all -> 0x019c }
                throw r0
            L_0x019f:
                r1.sendEmptyMessage(r8)
                goto L_0x0120
            L_0x01a4:
                int r5 = r2.what
                if (r5 != r12) goto L_0x01ae
                boolean r5 = com.android.settingslib.applications.ApplicationsState.hasFlag(r4, r7)
                if (r5 != 0) goto L_0x01bb
            L_0x01ae:
                int r5 = r2.what
                r6 = 5
                if (r5 != r6) goto L_0x02a6
                r5 = 16
                boolean r5 = com.android.settingslib.applications.ApplicationsState.hasFlag(r4, r5)
                if (r5 == 0) goto L_0x02a6
            L_0x01bb:
                android.content.Intent r5 = new android.content.Intent
                java.lang.String r6 = "android.intent.action.MAIN"
                r7 = 0
                r5.<init>(r6, r7)
                int r6 = r2.what
                if (r6 != r12) goto L_0x01ca
                java.lang.String r6 = "android.intent.category.LAUNCHER"
                goto L_0x01cc
            L_0x01ca:
                java.lang.String r6 = "android.intent.category.LEANBACK_LAUNCHER"
            L_0x01cc:
                r5.addCategory(r6)
                r6 = r0
            L_0x01d0:
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r7 = r7.mEntriesMap
                int r7 = r7.size()
                if (r6 >= r7) goto L_0x028f
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r7 = r7.mEntriesMap
                int r7 = r7.keyAt(r6)
                com.android.settingslib.applications.ApplicationsState r9 = com.android.settingslib.applications.ApplicationsState.this
                android.content.pm.PackageManager r9 = r9.mPm
                r10 = 786944(0xc0200, float:1.102743E-39)
                java.util.List r9 = r9.queryIntentActivitiesAsUser(r5, r10, r7)
                com.android.settingslib.applications.ApplicationsState r10 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r10 = r10.mEntriesMap
                monitor-enter(r10)
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0286 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r11 = r11.mEntriesMap     // Catch:{ all -> 0x0286 }
                java.lang.Object r11 = r11.valueAt(r6)     // Catch:{ all -> 0x0286 }
                java.util.HashMap r11 = (java.util.HashMap) r11     // Catch:{ all -> 0x0286 }
                int r16 = r9.size()     // Catch:{ all -> 0x0286 }
                r18 = r16
                r16 = r0
            L_0x0204:
                r19 = r16
                r15 = r18
                r0 = r19
                if (r0 >= r15) goto L_0x0274
                java.lang.Object r16 = r9.get(r0)     // Catch:{ all -> 0x0286 }
                android.content.pm.ResolveInfo r16 = (android.content.pm.ResolveInfo) r16     // Catch:{ all -> 0x0286 }
                r21 = r16
                r13 = r21
                android.content.pm.ActivityInfo r12 = r13.activityInfo     // Catch:{ all -> 0x0286 }
                java.lang.String r12 = r12.packageName     // Catch:{ all -> 0x0286 }
                java.lang.Object r16 = r11.get(r12)     // Catch:{ all -> 0x0286 }
                com.android.settingslib.applications.ApplicationsState$AppEntry r16 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r16     // Catch:{ all -> 0x0286 }
                r22 = r16
                r8 = r22
                if (r8 == 0) goto L_0x0240
                r8.hasLauncherEntry = r14     // Catch:{ all -> 0x023a }
                boolean r14 = r8.launcherEntryEnabled     // Catch:{ all -> 0x023a }
                r23 = r3
                android.content.pm.ActivityInfo r3 = r13.activityInfo     // Catch:{ all -> 0x0236 }
                boolean r3 = r3.enabled     // Catch:{ all -> 0x0236 }
                r3 = r3 | r14
                r8.launcherEntryEnabled = r3     // Catch:{ all -> 0x0236 }
                r24 = r5
                goto L_0x0262
            L_0x0236:
                r0 = move-exception
                r24 = r5
                goto L_0x028b
            L_0x023a:
                r0 = move-exception
                r23 = r3
                r24 = r5
                goto L_0x028b
            L_0x0240:
                r23 = r3
                java.lang.String r3 = "ApplicationsState"
                java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0270 }
                r14.<init>()     // Catch:{ all -> 0x0270 }
                r24 = r5
                java.lang.String r5 = "Cannot find pkg: "
                r14.append(r5)     // Catch:{ all -> 0x028d }
                r14.append(r12)     // Catch:{ all -> 0x028d }
                java.lang.String r5 = " on user "
                r14.append(r5)     // Catch:{ all -> 0x028d }
                r14.append(r7)     // Catch:{ all -> 0x028d }
                java.lang.String r5 = r14.toString()     // Catch:{ all -> 0x028d }
                android.util.Log.w(r3, r5)     // Catch:{ all -> 0x028d }
            L_0x0262:
                int r16 = r0 + 1
                r18 = r15
                r3 = r23
                r5 = r24
                r0 = 0
                r8 = 7
                r12 = 4
                r13 = 6
                r14 = 1
                goto L_0x0204
            L_0x0270:
                r0 = move-exception
                r24 = r5
                goto L_0x028b
            L_0x0274:
                r23 = r3
                r24 = r5
                monitor-exit(r10)     // Catch:{ all -> 0x028d }
                int r6 = r6 + 1
                r3 = r23
                r5 = r24
                r0 = 0
                r8 = 7
                r12 = 4
                r13 = 6
                r14 = 1
                goto L_0x01d0
            L_0x0286:
                r0 = move-exception
                r23 = r3
                r24 = r5
            L_0x028b:
                monitor-exit(r10)     // Catch:{ all -> 0x028d }
                throw r0
            L_0x028d:
                r0 = move-exception
                goto L_0x028b
            L_0x028f:
                r23 = r3
                r24 = r5
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                r3 = 7
                boolean r0 = r0.hasMessages(r3)
                if (r0 != 0) goto L_0x02a8
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                r0.sendEmptyMessage(r3)
                goto L_0x02a8
            L_0x02a6:
                r23 = r3
            L_0x02a8:
                int r0 = r2.what
                r3 = 4
                if (r0 != r3) goto L_0x02b3
                r0 = 5
                r1.sendEmptyMessage(r0)
                goto L_0x03e5
            L_0x02b3:
                r0 = 6
                r1.sendEmptyMessage(r0)
                goto L_0x03e5
            L_0x02b9:
                r23 = r3
                r0 = 1
                boolean r3 = com.android.settingslib.applications.ApplicationsState.hasFlag(r4, r0)
                if (r3 == 0) goto L_0x0314
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r3 = r0
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                android.content.pm.PackageManager r0 = r0.mPm
                r0.getHomeActivities(r3)
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r5 = r0.mEntriesMap
                monitor-enter(r5)
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0311 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r0 = r0.mEntriesMap     // Catch:{ all -> 0x0311 }
                int r0 = r0.size()     // Catch:{ all -> 0x0311 }
                r20 = 0
            L_0x02de:
                r6 = r20
                if (r6 >= r0) goto L_0x030f
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0311 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r7 = r7.mEntriesMap     // Catch:{ all -> 0x0311 }
                java.lang.Object r7 = r7.valueAt(r6)     // Catch:{ all -> 0x0311 }
                java.util.HashMap r7 = (java.util.HashMap) r7     // Catch:{ all -> 0x0311 }
                java.util.Iterator r8 = r3.iterator()     // Catch:{ all -> 0x0311 }
            L_0x02f0:
                boolean r9 = r8.hasNext()     // Catch:{ all -> 0x0311 }
                if (r9 == 0) goto L_0x030c
                java.lang.Object r9 = r8.next()     // Catch:{ all -> 0x0311 }
                android.content.pm.ResolveInfo r9 = (android.content.pm.ResolveInfo) r9     // Catch:{ all -> 0x0311 }
                android.content.pm.ActivityInfo r10 = r9.activityInfo     // Catch:{ all -> 0x0311 }
                java.lang.String r10 = r10.packageName     // Catch:{ all -> 0x0311 }
                java.lang.Object r11 = r7.get(r10)     // Catch:{ all -> 0x0311 }
                com.android.settingslib.applications.ApplicationsState$AppEntry r11 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r11     // Catch:{ all -> 0x0311 }
                if (r11 == 0) goto L_0x030b
                r12 = 1
                r11.isHomeApp = r12     // Catch:{ all -> 0x0311 }
            L_0x030b:
                goto L_0x02f0
            L_0x030c:
                int r20 = r6 + 1
                goto L_0x02de
            L_0x030f:
                monitor-exit(r5)     // Catch:{ all -> 0x0311 }
                goto L_0x0314
            L_0x0311:
                r0 = move-exception
                monitor-exit(r5)     // Catch:{ all -> 0x0311 }
                throw r0
            L_0x0314:
                r0 = 4
                r1.sendEmptyMessage(r0)
                goto L_0x03e5
            L_0x031a:
                r23 = r3
                r0 = 0
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r3 = r3.mEntriesMap
                monitor-enter(r3)
                r5 = r0
                r0 = 0
            L_0x0324:
                com.android.settingslib.applications.ApplicationsState r8 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                java.util.List<android.content.pm.ApplicationInfo> r8 = r8.mApplications     // Catch:{ all -> 0x03e0 }
                int r8 = r8.size()     // Catch:{ all -> 0x03e0 }
                if (r0 >= r8) goto L_0x03c3
                r8 = 6
                if (r5 >= r8) goto L_0x03c3
                boolean r8 = r1.mRunning     // Catch:{ all -> 0x03e0 }
                if (r8 != 0) goto L_0x034d
                r8 = 1
                r1.mRunning = r8     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r11 = r11.mMainHandler     // Catch:{ all -> 0x03e0 }
                java.lang.Integer r12 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x03e0 }
                r13 = 6
                android.os.Message r11 = r11.obtainMessage(r13, r12)     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState r12 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r12 = r12.mMainHandler     // Catch:{ all -> 0x03e0 }
                r12.sendMessage(r11)     // Catch:{ all -> 0x03e0 }
                goto L_0x034e
            L_0x034d:
                r8 = 1
            L_0x034e:
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                java.util.List<android.content.pm.ApplicationInfo> r11 = r11.mApplications     // Catch:{ all -> 0x03e0 }
                java.lang.Object r11 = r11.get(r0)     // Catch:{ all -> 0x03e0 }
                android.content.pm.ApplicationInfo r11 = (android.content.pm.ApplicationInfo) r11     // Catch:{ all -> 0x03e0 }
                int r12 = r11.uid     // Catch:{ all -> 0x03e0 }
                int r12 = android.os.UserHandle.getUserId(r12)     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState r13 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r13 = r13.mEntriesMap     // Catch:{ all -> 0x03e0 }
                java.lang.Object r13 = r13.get(r12)     // Catch:{ all -> 0x03e0 }
                java.util.HashMap r13 = (java.util.HashMap) r13     // Catch:{ all -> 0x03e0 }
                java.lang.String r14 = r11.packageName     // Catch:{ all -> 0x03e0 }
                java.lang.Object r13 = r13.get(r14)     // Catch:{ all -> 0x03e0 }
                if (r13 != 0) goto L_0x0377
                int r5 = r5 + 1
                com.android.settingslib.applications.ApplicationsState r13 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState.AppEntry unused = r13.getEntryLocked(r11)     // Catch:{ all -> 0x03e0 }
            L_0x0377:
                if (r12 == 0) goto L_0x03bc
                com.android.settingslib.applications.ApplicationsState r13 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r13 = r13.mEntriesMap     // Catch:{ all -> 0x03e0 }
                r14 = 0
                int r13 = r13.indexOfKey(r14)     // Catch:{ all -> 0x03e0 }
                if (r13 < 0) goto L_0x03ba
                com.android.settingslib.applications.ApplicationsState r13 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r13 = r13.mEntriesMap     // Catch:{ all -> 0x03e0 }
                java.lang.Object r13 = r13.get(r14)     // Catch:{ all -> 0x03e0 }
                java.util.HashMap r13 = (java.util.HashMap) r13     // Catch:{ all -> 0x03e0 }
                java.lang.String r14 = r11.packageName     // Catch:{ all -> 0x03e0 }
                java.lang.Object r13 = r13.get(r14)     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState$AppEntry r13 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r13     // Catch:{ all -> 0x03e0 }
                if (r13 == 0) goto L_0x03bc
                android.content.pm.ApplicationInfo r14 = r13.info     // Catch:{ all -> 0x03e0 }
                int r14 = r14.flags     // Catch:{ all -> 0x03e0 }
                boolean r14 = com.android.settingslib.applications.ApplicationsState.hasFlag(r14, r6)     // Catch:{ all -> 0x03e0 }
                if (r14 != 0) goto L_0x03bc
                com.android.settingslib.applications.ApplicationsState r14 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r14 = r14.mEntriesMap     // Catch:{ all -> 0x03e0 }
                r15 = 0
                java.lang.Object r14 = r14.get(r15)     // Catch:{ all -> 0x03e0 }
                java.util.HashMap r14 = (java.util.HashMap) r14     // Catch:{ all -> 0x03e0 }
                java.lang.String r6 = r11.packageName     // Catch:{ all -> 0x03e0 }
                r14.remove(r6)     // Catch:{ all -> 0x03e0 }
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x03e0 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r6 = r6.mAppEntries     // Catch:{ all -> 0x03e0 }
                r6.remove(r13)     // Catch:{ all -> 0x03e0 }
                goto L_0x03bd
            L_0x03ba:
                r15 = r14
                goto L_0x03bd
            L_0x03bc:
                r15 = 0
            L_0x03bd:
                int r0 = r0 + 1
                r6 = 8388608(0x800000, float:1.17549435E-38)
                goto L_0x0324
            L_0x03c3:
                monitor-exit(r3)     // Catch:{ all -> 0x03e0 }
                r0 = 6
                if (r5 < r0) goto L_0x03cb
                r1.sendEmptyMessage(r10)
                goto L_0x03df
            L_0x03cb:
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                boolean r0 = r0.hasMessages(r7)
                if (r0 != 0) goto L_0x03dc
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler
                r0.sendEmptyMessage(r7)
            L_0x03dc:
                r1.sendEmptyMessage(r9)
            L_0x03df:
                goto L_0x03e5
            L_0x03e0:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x03e0 }
                throw r0
            L_0x03e3:
                r23 = r3
            L_0x03e5:
                return
            L_0x03e6:
                r0 = move-exception
                r23 = r3
                goto L_0x03eb
            L_0x03ea:
                r0 = move-exception
            L_0x03eb:
                monitor-exit(r4)     // Catch:{ all -> 0x03ea }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.BackgroundHandler.handleMessage(android.os.Message):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0040, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0041, code lost:
            android.util.Log.w(com.android.settingslib.applications.ApplicationsState.TAG, "Failed to query stats: " + r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
            r4.mStatsObserver.onGetStatsCompleted((android.content.pm.PackageStats) null, false);
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:5:0x0040 A[ExcHandler: NameNotFoundException | IOException (r0v0 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:0:0x0000] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static /* synthetic */ void lambda$handleMessage$0(com.android.settingslib.applications.ApplicationsState.BackgroundHandler r4) {
            /*
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                android.app.usage.StorageStatsManager r0 = r0.mStats     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                java.util.UUID r1 = r1.mCurComputingSizeUuid     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                java.lang.String r2 = r2.mCurComputingSizePkg     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                int r3 = r3.mCurComputingSizeUserId     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                android.os.UserHandle r3 = android.os.UserHandle.of(r3)     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                android.app.usage.StorageStats r0 = r0.queryStatsForPackage(r1, r2, r3)     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                android.content.pm.PackageStats r1 = new android.content.pm.PackageStats     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                java.lang.String r2 = r2.mCurComputingSizePkg     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                int r3 = r3.mCurComputingSizeUserId     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                r1.<init>(r2, r3)     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                long r2 = r0.getCodeBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                r1.codeSize = r2     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                long r2 = r0.getDataBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                r1.dataSize = r2     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                long r2 = r0.getCacheBytes()     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                r1.cacheSize = r2     // Catch:{ NameNotFoundException | IOException -> 0x0040 }
                android.content.pm.IPackageStatsObserver$Stub r2 = r4.mStatsObserver     // Catch:{ RemoteException -> 0x003e, NameNotFoundException | IOException -> 0x0040 }
                r3 = 1
                r2.onGetStatsCompleted(r1, r3)     // Catch:{ RemoteException -> 0x003e, NameNotFoundException | IOException -> 0x0040 }
                goto L_0x003f
            L_0x003e:
                r2 = move-exception
            L_0x003f:
                goto L_0x0060
            L_0x0040:
                r0 = move-exception
                java.lang.String r1 = "ApplicationsState"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Failed to query stats: "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                android.util.Log.w(r1, r2)
                android.content.pm.IPackageStatsObserver$Stub r1 = r4.mStatsObserver     // Catch:{ RemoteException -> 0x005f }
                r2 = 0
                r3 = 0
                r1.onGetStatsCompleted(r2, r3)     // Catch:{ RemoteException -> 0x005f }
                goto L_0x0060
            L_0x005f:
                r1 = move-exception
            L_0x0060:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.BackgroundHandler.lambda$handleMessage$0(com.android.settingslib.applications.ApplicationsState$BackgroundHandler):void");
        }

        private int getCombinedSessionFlags(List<Session> sessions) {
            int flags;
            synchronized (ApplicationsState.this.mEntriesMap) {
                flags = 0;
                for (Session session : sessions) {
                    flags |= session.mFlags;
                }
            }
            return flags;
        }
    }

    private class PackageIntentReceiver extends BroadcastReceiver {
        private PackageIntentReceiver() {
        }

        /* access modifiers changed from: package-private */
        public void registerReceiver() {
            IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            filter.addAction("android.intent.action.PACKAGE_REMOVED");
            filter.addAction("android.intent.action.PACKAGE_CHANGED");
            filter.addDataScheme(DirectoryAccessDetails.ARG_PACKAGE_NAME);
            ApplicationsState.this.mContext.registerReceiver(this, filter);
            IntentFilter sdFilter = new IntentFilter();
            sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            ApplicationsState.this.mContext.registerReceiver(this, sdFilter);
            IntentFilter userFilter = new IntentFilter();
            userFilter.addAction("android.intent.action.USER_ADDED");
            userFilter.addAction("android.intent.action.USER_REMOVED");
            ApplicationsState.this.mContext.registerReceiver(this, userFilter);
        }

        /* access modifiers changed from: package-private */
        public void unregisterReceiver() {
            ApplicationsState.this.mContext.unregisterReceiver(this);
        }

        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            int i = 0;
            if ("android.intent.action.PACKAGE_ADDED".equals(actionStr)) {
                String pkgName = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.addPackage(pkgName, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(actionStr)) {
                String pkgName2 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.removePackage(pkgName2, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_CHANGED".equals(actionStr)) {
                String pkgName3 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.invalidatePackage(pkgName3, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(actionStr) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(actionStr)) {
                String[] pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (pkgList != null && pkgList.length != 0 && "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(actionStr)) {
                    for (String pkgName4 : pkgList) {
                        for (int i2 = 0; i2 < ApplicationsState.this.mEntriesMap.size(); i2++) {
                            ApplicationsState.this.invalidatePackage(pkgName4, ApplicationsState.this.mEntriesMap.keyAt(i2));
                        }
                    }
                }
            } else if ("android.intent.action.USER_ADDED".equals(actionStr)) {
                ApplicationsState.this.addUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            } else if ("android.intent.action.USER_REMOVED".equals(actionStr)) {
                ApplicationsState.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            }
        }
    }

    public static class AppEntry extends SizeInfo {
        public final File apkFile;
        public long externalSize;
        public String externalSizeStr;
        public Object extraInfo;
        public boolean hasLauncherEntry;
        public Drawable icon;
        public final long id;
        public ApplicationInfo info;
        public long internalSize;
        public String internalSizeStr;
        public boolean isHomeApp;
        public String label;
        public boolean launcherEntryEnabled;
        public boolean mounted;
        public String normalizedLabel;
        public long size = -1;
        public long sizeLoadStart;
        public boolean sizeStale = true;
        public String sizeStr;

        public String getNormalizedLabel() {
            if (this.normalizedLabel != null) {
                return this.normalizedLabel;
            }
            this.normalizedLabel = ApplicationsState.normalize(this.label);
            return this.normalizedLabel;
        }

        @VisibleForTesting(otherwise = 2)
        public AppEntry(Context context, ApplicationInfo info2, long id2) {
            this.apkFile = new File(info2.sourceDir);
            this.id = id2;
            this.info = info2;
            ensureLabel(context);
        }

        public void ensureLabel(Context context) {
            if (this.label != null && this.mounted) {
                return;
            }
            if (!this.apkFile.exists()) {
                this.mounted = false;
                this.label = this.info.packageName;
                return;
            }
            this.mounted = true;
            CharSequence label2 = this.info.loadLabel(context.getPackageManager());
            this.label = label2 != null ? label2.toString() : this.info.packageName;
        }

        /* access modifiers changed from: package-private */
        public boolean ensureIconLocked(Context context, IconDrawableFactory drawableFactory) {
            if (this.icon == null) {
                if (this.apkFile.exists()) {
                    this.icon = drawableFactory.getBadgedIcon(this.info);
                    return true;
                }
                this.mounted = false;
                this.icon = context.getDrawable(17303560);
            } else if (!this.mounted && this.apkFile.exists()) {
                this.mounted = true;
                this.icon = drawableFactory.getBadgedIcon(this.info);
                return true;
            }
            return false;
        }

        public String getVersion(Context context) {
            try {
                return context.getPackageManager().getPackageInfo(this.info.packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return "";
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean hasFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    public interface AppFilter {
        boolean filterApp(AppEntry appEntry);

        void init();

        void init(Context context) {
            init();
        }
    }

    public static class VolumeFilter implements AppFilter {
        private final String mVolumeUuid;

        public VolumeFilter(String volumeUuid) {
            this.mVolumeUuid = volumeUuid;
        }

        public void init() {
        }

        public boolean filterApp(AppEntry info) {
            return Objects.equals(info.info.volumeUuid, this.mVolumeUuid);
        }
    }

    public static class CompoundFilter implements AppFilter {
        private final AppFilter mFirstFilter;
        private final AppFilter mSecondFilter;

        public CompoundFilter(AppFilter first, AppFilter second) {
            this.mFirstFilter = first;
            this.mSecondFilter = second;
        }

        public void init(Context context) {
            this.mFirstFilter.init(context);
            this.mSecondFilter.init(context);
        }

        public void init() {
            this.mFirstFilter.init();
            this.mSecondFilter.init();
        }

        public boolean filterApp(AppEntry info) {
            return this.mFirstFilter.filterApp(info) && this.mSecondFilter.filterApp(info);
        }
    }
}
