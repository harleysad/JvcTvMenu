package com.android.tv.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.ArraySet;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetupUtils {
    private static final String PREF_KEY_IS_FIRST_TUNE = "is_first_tune";
    private static final String PREF_KEY_KNOWN_INPUTS = "known_inputs";
    private static final String PREF_KEY_RECOGNIZED_INPUTS = "recognized_inputs";
    private static final String PREF_KEY_SET_UP_INPUTS = "set_up_inputs";
    private static final String TAG = "SetupUtils";
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mIsFirstTune;
    private final Set<String> mKnownInputs;
    private final Set<String> mRecognizedInputs;
    private final Set<String> mSetUpInputs = new ArraySet();
    private final SharedPreferences mSharedPreferences;
    private final String mTunerInputId;

    @VisibleForTesting
    protected SetupUtils(Context context) {
        this.mContext = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mSetUpInputs.addAll(this.mSharedPreferences.getStringSet(PREF_KEY_SET_UP_INPUTS, Collections.emptySet()));
        this.mKnownInputs = new ArraySet();
        this.mKnownInputs.addAll(this.mSharedPreferences.getStringSet(PREF_KEY_KNOWN_INPUTS, Collections.emptySet()));
        this.mRecognizedInputs = new ArraySet();
        this.mRecognizedInputs.addAll(this.mSharedPreferences.getStringSet(PREF_KEY_RECOGNIZED_INPUTS, this.mKnownInputs));
        this.mIsFirstTune = this.mSharedPreferences.getBoolean(PREF_KEY_IS_FIRST_TUNE, true);
        this.mTunerInputId = TvContract.buildInputId(new ComponentName(context, TvInputService.class));
    }

    public static SetupUtils createForTvSingletons(Context context) {
        return new SetupUtils(context.getApplicationContext());
    }

    public void onTvInputSetupFinished(final String inputId, @Nullable final Runnable postRunnable) {
        onSetupDone(inputId);
        final TIFChannelManager manager = TvSingletons.getSingletons().getChannelDataManager();
        if (!manager.isDbLoadFinished()) {
            manager.addListener(new TIFChannelManager.Listener() {
                public void onLoadFinished() {
                    manager.removeListener(this);
                    SetupUtils.updateChannelsAfterSetup(SetupUtils.this.mContext, inputId, postRunnable);
                }

                public void onChannelListUpdated() {
                }

                public void onChannelBrowsableChanged() {
                }
            });
        } else {
            updateChannelsAfterSetup(this.mContext, inputId, postRunnable);
        }
    }

    /* access modifiers changed from: private */
    public static void updateChannelsAfterSetup(Context context, String inputId, Runnable postRunnable) {
        TIFChannelManager manager = TvSingletons.getSingletons().getChannelDataManager();
        manager.updateChannels(new Runnable(inputId, postRunnable) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ Runnable f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SetupUtils.lambda$updateChannelsAfterSetup$0(TIFChannelManager.this, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$updateChannelsAfterSetup$0(TIFChannelManager manager, String inputId, Runnable postRunnable) {
        TIFChannelInfo firstChannelForInput = null;
        boolean browsableChanged = false;
        for (TIFChannelInfo channel : manager.getChannelList()) {
            if (channel.mInputServiceName.equals(inputId)) {
                if (!channel.mIsBrowsable) {
                    manager.updateBrowsable(Long.valueOf(channel.mId), true, true);
                    browsableChanged = true;
                }
                if (firstChannelForInput == null) {
                    firstChannelForInput = channel;
                }
            }
        }
        if (browsableChanged) {
            manager.notifyChannelBrowsableChanged();
            manager.applyUpdatedValuesToDb();
        }
        if (postRunnable != null) {
            postRunnable.run();
        }
    }

    private static TvInputManagerHelper getTvInputManagerHelper(Context context) {
        if (context.getApplicationContext() instanceof DestroyApp) {
            return ((DestroyApp) context.getApplicationContext()).getTvInputManagerHelper();
        }
        return null;
    }

    @UiThread
    public void markNewChannelsBrowsable() {
        Set<String> newInputsWithChannels = new HashSet<>();
        TvInputManagerHelper tvInputManagerHelper = getTvInputManagerHelper(this.mContext);
        if (tvInputManagerHelper != null) {
            TIFChannelManager channelDataManager = TvSingletons.getSingletons().getChannelDataManager();
            for (TvInputInfo input : tvInputManagerHelper.getTvInputInfos(true, true)) {
                String inputId = input.getId();
                if (!isSetupDone(inputId) && channelDataManager.getChannelCountForInput(inputId) > 0) {
                    onSetupDone(inputId);
                    newInputsWithChannels.add(inputId);
                    MtkLog.d(TAG, "New input " + inputId + " has " + channelDataManager.getChannelCountForInput(inputId) + " channels");
                }
            }
            if (!newInputsWithChannels.isEmpty()) {
                for (TIFChannelInfo channel : channelDataManager.getChannelList()) {
                    if (newInputsWithChannels.contains(channel.mInputServiceName)) {
                        channelDataManager.updateBrowsable(Long.valueOf(channel.mId), true);
                    }
                }
                channelDataManager.applyUpdatedValuesToDb();
            }
        }
    }

    public boolean isFirstTune() {
        return this.mIsFirstTune;
    }

    public boolean isNewInput(String inputId) {
        return !this.mKnownInputs.contains(inputId);
    }

    public void markAsKnownInput(String inputId) {
        this.mKnownInputs.add(inputId);
        this.mRecognizedInputs.add(inputId);
        this.mSharedPreferences.edit().putStringSet(PREF_KEY_KNOWN_INPUTS, this.mKnownInputs).putStringSet(PREF_KEY_RECOGNIZED_INPUTS, this.mRecognizedInputs).apply();
    }

    public boolean isSetupDone(String inputId) {
        boolean done = this.mSetUpInputs.contains(inputId);
        MtkLog.d(TAG, "isSetupDone: (input=" + inputId + ", result= " + done + ")");
        return done;
    }

    public boolean hasNewInput(TvInputManagerHelper inputManager) {
        for (TvInputInfo input : inputManager.getTvInputInfos(true, true)) {
            if (isNewInput(input.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isRecognizedInput(String inputId) {
        return this.mRecognizedInputs.contains(inputId);
    }

    public void markAllInputsRecognized(TvInputManagerHelper inputManager) {
        for (TvInputInfo input : inputManager.getTvInputInfos(true, true)) {
            this.mRecognizedInputs.add(input.getId());
        }
        this.mSharedPreferences.edit().putStringSet(PREF_KEY_RECOGNIZED_INPUTS, this.mRecognizedInputs).apply();
    }

    public boolean hasUnrecognizedInput(TvInputManagerHelper inputManager) {
        for (TvInputInfo input : inputManager.getTvInputInfos(true, true)) {
            if (!isRecognizedInput(input.getId())) {
                return true;
            }
        }
        return false;
    }

    public static void grantEpgPermissionToSetUpPackages(Context context) {
        ComponentName componentName;
        Set<String> setUpPackages = new HashSet<>();
        for (String input : PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_KEY_SET_UP_INPUTS, Collections.emptySet())) {
            if (!TextUtils.isEmpty(input) && (componentName = ComponentName.unflattenFromString(input)) != null) {
                setUpPackages.add(componentName.getPackageName());
            }
        }
        for (String packageName : setUpPackages) {
            grantEpgPermission(context, packageName);
        }
    }

    public static void grantEpgPermission(Context context, String packageName) {
        MtkLog.d(TAG, "grantEpgPermission(context=" + context + ", packageName=" + packageName + ")");
        try {
            context.grantUriPermission(packageName, TvContract.Channels.CONTENT_URI, 130);
            context.grantUriPermission(packageName, TvContract.Programs.CONTENT_URI, 130);
        } catch (SecurityException e) {
            MtkLog.e(TAG, "Either TvProvider does not allow granting of Uri permissions or the app does not have permission.", e);
        }
    }

    public void onTuned() {
        if (this.mIsFirstTune) {
            this.mIsFirstTune = false;
            this.mSharedPreferences.edit().putBoolean(PREF_KEY_IS_FIRST_TUNE, false).apply();
        }
    }

    public void onInputListUpdated(TvInputManager manager) {
        Set<String> removedInputList = new HashSet<>(this.mRecognizedInputs);
        for (TvInputInfo input : manager.getTvInputList()) {
            removedInputList.remove(input.getId());
        }
        removedInputList.remove(this.mTunerInputId);
        if (!removedInputList.isEmpty()) {
            boolean inputPackageDeleted = false;
            for (String input2 : removedInputList) {
                try {
                    this.mContext.getPackageManager().getPackageInfo(ComponentName.unflattenFromString(input2).getPackageName(), 1);
                    MtkLog.i(TAG, "TV input (" + input2 + ") is removed but package is not deleted");
                } catch (PackageManager.NameNotFoundException e) {
                    MtkLog.i(TAG, "TV input (" + input2 + ") and its package are removed");
                    this.mRecognizedInputs.remove(input2);
                    this.mSetUpInputs.remove(input2);
                    this.mKnownInputs.remove(input2);
                    inputPackageDeleted = true;
                }
            }
            if (inputPackageDeleted) {
                this.mSharedPreferences.edit().putStringSet(PREF_KEY_SET_UP_INPUTS, this.mSetUpInputs).putStringSet(PREF_KEY_KNOWN_INPUTS, this.mKnownInputs).putStringSet(PREF_KEY_RECOGNIZED_INPUTS, this.mRecognizedInputs).apply();
            }
        }
    }

    private void onSetupDone(String inputId) {
        MtkLog.d(TAG, "onSetupDone: input=" + inputId);
        if (!this.mRecognizedInputs.contains(inputId)) {
            MtkLog.i(TAG, "An unrecognized input's setup has been done. inputId=" + inputId);
            this.mRecognizedInputs.add(inputId);
            this.mSharedPreferences.edit().putStringSet(PREF_KEY_RECOGNIZED_INPUTS, this.mRecognizedInputs).apply();
        }
        if (!this.mKnownInputs.contains(inputId)) {
            MtkLog.i(TAG, "An unknown input's setup has been done. inputId=" + inputId);
            this.mKnownInputs.add(inputId);
            this.mSharedPreferences.edit().putStringSet(PREF_KEY_KNOWN_INPUTS, this.mKnownInputs).apply();
        }
        if (!this.mSetUpInputs.contains(inputId)) {
            this.mSetUpInputs.add(inputId);
            this.mSharedPreferences.edit().putStringSet(PREF_KEY_SET_UP_INPUTS, this.mSetUpInputs).apply();
        }
    }
}
