package com.mediatek.wwtv.setting;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import com.android.tv.onboarding.SetupSourceActivity;
import com.android.tv.ui.sidepanel.ClosedCaptionFragment;
import com.android.tv.ui.sidepanel.MultiAudioFragment;
import com.android.tv.ui.sidepanel.RecordTShiftFragment;
import com.mediatek.tv.ini.IniDocument;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.fragments.BaseContentFragment;
import com.mediatek.wwtv.setting.fragments.MainFragment;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.SettingsUtil;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.io.File;

public class LiveTvSetting extends TvSettingsActivity {
    private static final String TAG = "LiveTvSetting";
    public static boolean isRunning = false;
    private static boolean isStartTV;
    private static final EventHelper mEventHelper = new EventHelper();
    /* access modifiers changed from: private */
    public static boolean mFactoryMode = false;
    /* access modifiers changed from: private */
    public static MainFragment mFragment = null;
    private static LiveTvSetting mLiveTvSetting = null;
    /* access modifiers changed from: private */
    public final MtkTvAppTVBase appTV = new MtkTvAppTVBase();
    private CloseBroadcast broadcast = null;
    int facTimes = 0;
    /* access modifiers changed from: private */
    public boolean isBeganToExit = false;
    /* access modifiers changed from: private */
    public TVContent mTV;
    private String mainActivity;
    private String packageName;
    private String serialKeys;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(TAG, "onCreate.");
        setContentView(R.layout.menu_main_layout);
        super.onCreate(savedInstanceState);
        getScreenWH();
        receiveIntent(getIntent());
        mLiveTvSetting = this;
        this.mTV = TVContent.getInstance(this);
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                MtkLog.d(LiveTvSetting.TAG, "now onResume updatedSysStatus to RESUME");
                LiveTvSetting.this.appTV.updatedSysStatus(MtkTvAppTVBase.SYS_MENU_RESUME);
                if (TurnkeyUiMainActivity.getInstance() != null) {
                    Intent intent = new Intent("com.mediatek.tv.callcc");
                    intent.putExtra("ccvisible", false);
                    LiveTvSetting.this.sendBroadcast(intent);
                }
            }
        });
        this.broadcast = new CloseBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish_live_tv_settings");
        registerReceiver(this.broadcast, filter);
        mFactoryMode = false;
        getFacCusInfo();
    }

    public static LiveTvSetting getInstance() {
        return mLiveTvSetting;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        MtkLog.d(TAG, "onStart.");
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        receiveIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MtkLog.d(TAG, "onResume.");
        super.onResume();
        isRunning = true;
        MtkLog.d(TAG, "isFromLiveTv: " + isBootFromLiveTV());
        if (isBootFromLiveTV()) {
            this.facTimes = 0;
            mFactoryMode = false;
            setResult(NavBasic.NAV_RESULT_CODE_MENU);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MtkLog.d(TAG, "onPause.");
        super.onPause();
        if (isBootFromLiveTV()) {
            boolean z = this.isBeganToExit;
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        MtkLog.d(TAG, "onStop.");
        isRunning = false;
        if (ScheduleListDialog.getDialog() != null) {
            ScheduleListDialog.getDialog().dismiss();
        }
        super.onStop();
    }

    public void onDestroy() {
        MtkLog.d(TAG, "onDestroy.");
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                boolean unused = LiveTvSetting.this.isBeganToExit = false;
                LiveTvSetting.this.appTV.updatedSysStatus(MtkTvAppTVBase.SYS_MENU_PAUSE);
                LiveTvSetting.this.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
                if (TurnkeyUiMainActivity.getInstance() != null) {
                    TurnkeyUiMainActivity.getInstance().resetLayout();
                    Intent intent = new Intent("com.mediatek.tv.callcc");
                    intent.putExtra("ccvisible", true);
                    LiveTvSetting.this.sendBroadcast(intent);
                }
            }
        });
        if (this.broadcast != null) {
            unregisterReceiver(this.broadcast);
        }
        super.onDestroy();
        SundryImplement.setInstanceNavSundryImplementNull();
        PreferenceData.setInstance();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "onKeyDown, keyCode: " + keyCode);
        if (handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        MtkLog.d(TAG, "createSettingsFragment.");
        SettingsFragment fragment = SettingsFragment.newInstance();
        SettingsFragment.mActionId = this.mActionId;
        this.mActionId = null;
        return fragment;
    }

    public static class SettingsFragment extends BaseSettingsFragment {
        public static String mActionId;

        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            if (LiveTvSetting.mFactoryMode) {
                MainFragment unused = LiveTvSetting.mFragment = MainFragment.newInstance(true);
                boolean unused2 = LiveTvSetting.mFactoryMode = false;
            } else {
                MainFragment unused3 = LiveTvSetting.mFragment = MainFragment.newInstance(false);
            }
            if (LiveTvSetting.isForMultiAudioBootFromTVMenuOption()) {
                startPreferenceFragment(new MultiAudioFragment());
            } else if (LiveTvSetting.isFor3rdCaptionsTVMenuOptionOrSaRegion()) {
                if (CommonIntegration.getInstance().is3rdTVSource()) {
                    startPreferenceFragment(new ClosedCaptionFragment());
                    return;
                }
                CommonIntegration.getInstance();
                if (CommonIntegration.isSARegion()) {
                    startPreferenceFragment(LiveTvSetting.mFragment);
                }
            } else if (LiveTvSetting.isForTShiftBootFromTVMenuOption()) {
                startPreferenceFragment(new RecordTShiftFragment());
            } else if (!startPreferenceScanFragment()) {
                startPreferenceFragment(LiveTvSetting.mFragment);
            }
        }

        private boolean startPreferenceScanFragment() {
            MtkLog.d(LiveTvSetting.TAG, "startPreferenceScanFragment mActionId " + mActionId);
            if (mActionId == null) {
                return false;
            }
            if (!MenuConfigManager.CHANNEL_CHANNEL_SOURCES.equals(mActionId)) {
                BaseContentFragment base = BaseContentFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID, mActionId);
                base.setArguments(bundle);
                startPreferenceFragment(base);
                return true;
            }
            getActivity().startActivity(new Intent(getActivity(), SetupSourceActivity.class));
            return true;
        }
    }

    private void receiveIntent(Intent intent) {
        MtkLog.d(TAG, "receiveIntent: action:" + intent);
        mEventHelper.updateIntent(intent);
    }

    private boolean handleKeyEvent(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "handleKeyEvent: keyCode=" + keyCode);
        if (keyCode == 4) {
            MtkLog.d(TAG, "handleKeyEvent: getSideFragmentManager().getCount() >" + getSideFragmentManager().getCount());
            if (getSideFragmentManager().getCount() > 0) {
                getSideFragmentManager().popStackNumber();
            }
            if (mFragment != null && mFragment.getActive()) {
                this.isBeganToExit = true;
            }
        }
        if (!isBootFromLiveTV()) {
            return false;
        }
        if (handlerFacKey(keyCode)) {
            this.facTimes++;
            if (this.facTimes == getSerialKeys().split(",").length) {
                mFactoryMode = true;
                updateFragment(getPackageNames(), getMainActivity());
                this.facTimes = -1;
            }
            return true;
        }
        this.facTimes = -1;
        return false;
    }

    public static boolean isBootFromLiveTV() {
        return mEventHelper.isEvent(8);
    }

    public static boolean isForChannelBootFromTVSettingPlus() {
        return mEventHelper.isEvent(32);
    }

    public static boolean isForMultiAudioBootFromTVMenuOption() {
        return mEventHelper.isEvent(256);
    }

    public static boolean isFor3rdCaptionsTVMenuOptionOrSaRegion() {
        return mEventHelper.isEvent(32768);
    }

    public static boolean isForTShiftBootFromTVMenuOption() {
        return mEventHelper.isEvent(262144);
    }

    private void getFacCusInfo() {
        try {
            IniDocument idc = new IniDocument(getFile());
            this.serialKeys = idc.get("SerialKeys").toString().trim();
            this.packageName = idc.get("PackageName").toString().trim();
            this.mainActivity = idc.get("MainActivity").toString().trim();
            this.serialKeys = keyTool(this.serialKeys);
            this.packageName = keyTool(this.packageName);
            this.mainActivity = keyTool(this.mainActivity);
            MtkLog.d(TAG, "idc.get(SerialKeys) = " + this.serialKeys);
            MtkLog.d(TAG, "idc.get(PackageName) = " + this.packageName);
            MtkLog.d(TAG, "idc.get(MainActivity) = " + this.mainActivity);
        } catch (Exception e) {
            this.serialKeys = "7,7,7,7";
        }
    }

    private String getSerialKeys() {
        return this.serialKeys;
    }

    private String getPackageNames() {
        return this.packageName;
    }

    private String getMainActivity() {
        return this.mainActivity;
    }

    private boolean handlerFacKey(int keyCode) {
        if (this.facTimes < 0) {
            MtkLog.d(TAG, "handlerFacKey false");
            return false;
        } else if (Integer.parseInt(getSerialKeys().split(",")[this.facTimes].trim()) == keyCode) {
            return true;
        } else {
            return false;
        }
    }

    private String keyTool(String key) {
        if (key != null && !key.isEmpty()) {
            if (key.startsWith("[")) {
                key = key.replace("[", "");
            }
            if (key.endsWith("]")) {
                key = key.replace("]", "");
            }
        }
        MtkLog.d(TAG, " = " + key);
        return key;
    }

    private String getFile() {
        for (String path : new String[]{"/vendor/etc/" + "ManualEnterFactory.ini", "/vendor/tvconfig/apollo/" + "ManualEnterFactory.ini"}) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    public static void tryToStartTV(Context context) {
        if (isStartTV) {
            Intent mIntent = new Intent(TurnkeyUiMainActivity.LIVE_SETTING_SELECT_SOURCE);
            mIntent.putExtra(TurnkeyUiMainActivity.LIVE_SETTING_SELECT_SOURCE_NAME, "");
            context.sendBroadcast(mIntent);
            MtkLog.e(TAG, "start tv");
            isStartTV = false;
            return;
        }
        MtkLog.e(TAG, "not start tv");
    }

    private void getScreenWH() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        SettingsUtil.SCREEN_WIDTH = dm.widthPixels;
        SettingsUtil.SCREEN_HEIGHT = dm.heightPixels;
    }

    class CloseBroadcast extends BroadcastReceiver {
        CloseBroadcast() {
        }

        public void onReceive(Context arg0, Intent arg1) {
            MtkLog.d(LiveTvSetting.TAG, "onReceive android.intent.action.MASTER_CLEAR");
            if ("finish_live_tv_settings".equals(arg1.getAction())) {
                LiveTvSetting.this.finish();
            }
        }
    }
}
