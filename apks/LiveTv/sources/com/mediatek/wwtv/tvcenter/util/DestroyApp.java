package com.mediatek.wwtv.tvcenter.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.tv.parental.ContentRatingsManager;
import com.android.tv.parental.ParentalControlSettings;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyService;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DestroyApp extends Application implements TvSingletons {
    private static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            return new Thread(r, "tv-app-db");
        }
    });
    private static final String TAG = "DestroyApp";
    public static Context appContext;
    private static boolean isAppAlive = false;
    private static Activity mRunningActivity = null;
    private static List<Activity> mainActivities = new ArrayList();
    private final String AUTO_TEST_CHANGE_SOURCE_KEY = "auto_test_change_source";
    private final Uri BASE_URI = Settings.Global.CONTENT_URI;
    private ContentObserver contentObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            MtkLog.d(DestroyApp.TAG, "come to change source , slefChange:" + selfChange);
            if ("1".equals(SystemProperties.get(PwdDialog.AUTO_TEST_PROPERTY))) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        DestroyApp.this.autoTestChangeSource();
                    }
                }, 500);
            }
        }
    };
    private CommonIntegration mCommonIntegration;
    private InputSourceManager mInputSourceManager;
    private ParentalControlSettings mParentalControlSettings;
    private TIFChannelManager mTIFChannelManager;
    private TIFProgramManager mTIFProgramManager;
    private TvInputManagerHelper mTvInputManagerHelper;

    public static Activity getTopActivity() {
        return mRunningActivity;
    }

    public static void setRunningActivity(Activity activity) {
        mRunningActivity = activity;
    }

    public static String getRunningActivity() {
        if (mRunningActivity == null) {
            return "";
        }
        return mRunningActivity.getClass().getSimpleName();
    }

    public static boolean isCurTaskTKUI() {
        return isAppAlive;
    }

    public static void setActivityActiveStatus(boolean isActive) {
        isAppAlive = isActive;
    }

    public static boolean isCurActivityTkuiMainActivity() {
        return getRunningActivity().equals("TurnkeyUiMainActivity");
    }

    public static boolean isCurOADActivity() {
        return getRunningActivity().equals("NavOADActivity");
    }

    public static boolean isCurEPGActivity() {
        String name = getRunningActivity();
        return name.equals("EPGUsActivity") || name.equals("EPGEuActivity") || name.equals("EPGCnActivity") || name.equals("EPGSaActivity");
    }

    public void add(Activity act) {
        mainActivities.add(act);
    }

    public void remove(Activity activity) {
        if (mainActivities.size() > 0) {
            mainActivities.remove(activity);
        }
    }

    private void initDataFromSharedPreference() {
        SaveValue.getInstance(this).saveBooleanValue(MenuConfigManager.TIMESHIFT_START, false);
        SaveValue.saveWorldBooleanValue(this, MenuConfigManager.TIMESHIFT_START, false, false);
        SaveValue.setLocalMemoryValue("mPreConfigsFlags", 0);
        SaveValue.setLocalMemoryValue("showUpgradeMsg", true);
    }

    public void onCreate() {
        super.onCreate();
        appContext = this;
        try {
            getTvInputManagerHelper();
            KeyDispatch.getInstance();
            getCommonIntegration();
            getChannelDataManager();
            getProgramDataManager();
            getInputSourceManager();
        } catch (Exception ex) {
            MtkLog.d(TAG, "Exception: " + ex);
        }
        initDataFromSharedPreference();
        startForegroundService(new Intent(this, TurnkeyService.class));
        getContentResolver().registerContentObserver(buildUri("auto_test_change_source"), true, this.contentObserver);
    }

    /* access modifiers changed from: private */
    public void autoTestChangeSource() {
        String action = Settings.Global.getString(getContentResolver(), "auto_test_change_source");
        Settings.Global.putString(getContentResolver(), "auto_test_change_source", "");
        if (TextUtils.isEmpty(action)) {
            MtkLog.e(TAG, "somethings error.");
            return;
        }
        String sourcename = action.substring(action.lastIndexOf(".") + 1);
        MtkLog.d(TAG, "selectSourceReceiver,sourcename" + sourcename);
        if (this.mInputSourceManager != null && !sourcename.equalsIgnoreCase(this.mInputSourceManager.autoChangeTestGetCurrentSourceName(this.mCommonIntegration.getCurrentFocus()))) {
            MtkLog.d(TAG, "selectSourceReceiver,autoChangeTestSourceChange =" + sourcename);
            this.mInputSourceManager.autoChangeTestSourceChange(sourcename, this.mCommonIntegration.getCurrentFocus());
        }
    }

    private Uri buildUri(String uId) {
        return this.BASE_URI.buildUpon().appendPath(uId).build();
    }

    public void onTerminate() {
        super.onTerminate();
        KeyDispatch.remove();
        TvCallbackHandler.getInstance().removeAll();
        CommonIntegration.remove();
        InputSourceManager.remove();
        this.mTvInputManagerHelper.stop();
        this.mTvInputManagerHelper = null;
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            MtkLog.w(TAG, "Unable to find package '" + getPackageName() + "'.", e);
            return "";
        }
    }

    public TvInputManagerHelper getTvInputManagerHelper() {
        if (this.mTvInputManagerHelper == null) {
            this.mTvInputManagerHelper = new TvInputManagerHelper(appContext);
            this.mTvInputManagerHelper.start();
        }
        return this.mTvInputManagerHelper;
    }

    public TIFChannelManager getChannelDataManager() {
        if (this.mTIFChannelManager == null) {
            this.mTIFChannelManager = TIFChannelManager.getInstance(appContext);
            this.mTIFChannelManager.start();
        }
        return this.mTIFChannelManager;
    }

    public TIFProgramManager getProgramDataManager() {
        if (this.mTIFProgramManager == null) {
            this.mTIFProgramManager = TIFProgramManager.getInstance(appContext);
        }
        return this.mTIFProgramManager;
    }

    public InputSourceManager getInputSourceManager() {
        if (this.mInputSourceManager == null) {
            this.mInputSourceManager = InputSourceManager.getInstance(appContext);
        }
        return this.mInputSourceManager;
    }

    public CommonIntegration getCommonIntegration() {
        if (this.mCommonIntegration == null) {
            this.mCommonIntegration = CommonIntegration.getInstance();
        }
        return this.mCommonIntegration;
    }

    public ParentalControlSettings getParentalControlSettings() {
        if (this.mParentalControlSettings == null) {
            this.mParentalControlSettings = new ParentalControlSettings(appContext);
        }
        return this.mParentalControlSettings;
    }

    public ContentRatingsManager getContentRatingsManager() {
        return getTvInputManagerHelper().getContentRatingsManager();
    }

    public Executor getDbExecutor() {
        return DB_EXECUTOR;
    }

    public static TvSingletons getSingletons() {
        return (TvSingletons) appContext;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        MtkLog.w(TAG, "onConfigurationChanged--->newConfig language=" + newConfig.locale.getLanguage());
        super.onConfigurationChanged(newConfig);
        getTvInputManagerHelper().getContentRatingsManager().update();
    }
}
