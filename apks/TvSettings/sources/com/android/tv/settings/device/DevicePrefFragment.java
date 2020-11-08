package com.android.tv.settings.device;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.wrapper.PackageManagerWrapper;
import com.android.tv.settings.MainFragment;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.autofill.AutofillHelper;
import com.android.tv.settings.device.sound.SoundFragment;
import com.android.tv.settings.inputmethod.InputMethodHelper;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.android.tv.settings.system.SecurityFragment;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.List;

@Keep
public class DevicePrefFragment extends SettingsPreferenceFragment {
    @VisibleForTesting
    static final String KEY_CAST_SETTINGS = "cast";
    @VisibleForTesting
    static final String KEY_DEVELOPER = "developer";
    private static final String KEY_GOOGLE_SETTINGS = "google_settings";
    private static final String KEY_HOME_SETTINGS = "home";
    private static final String KEY_INPUTS = "inputs";
    @VisibleForTesting
    static final String KEY_KEYBOARD = "keyboard";
    private static final String KEY_SOUNDS = "sound_effects";
    private static final String KEY_USAGE = "usageAndDiag";
    private static final String TAG = "DeviceFragment";
    private boolean mInputSettingNeeded;
    private PackageManagerWrapper mPm;
    private Preference mSoundsPref;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Preference powerPref;
        if (isRestricted()) {
            setPreferencesFromResource(R.xml.device_restricted, (String) null);
        } else {
            setPreferencesFromResource(R.xml.device, (String) null);
        }
        this.mSoundsPref = findPreference("sound_effects");
        Preference inputPref = findPreference(KEY_INPUTS);
        if (inputPref != null) {
            inputPref.setVisible(this.mInputSettingNeeded);
        }
        boolean isPowerShow = PartnerSettingsConfig.isMiscItemDisplay(PreferenceConfigUtils.KEY_POWER_EFFECTS);
        Log.d(TAG, "onCreatePreferences,isPowerShow==" + isPowerShow);
        if (!isPowerShow && (powerPref = findPreference(PreferenceConfigUtils.KEY_POWER_EFFECTS)) != null) {
            getPreferenceScreen().removePreference(powerPref);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        TvInputManager manager = (TvInputManager) getContext().getSystemService("tv_input");
        if (manager != null) {
            for (TvInputInfo input : manager.getTvInputList()) {
                if (input.isPassthroughInput()) {
                    this.mInputSettingNeeded = true;
                }
            }
        }
        super.onCreate(savedInstanceState);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPm = new PackageManagerWrapper(context.getPackageManager());
    }

    public void onResume() {
        super.onResume();
        updateDeveloperOptions();
        updateSounds();
        updateGoogleSettings();
        updateCastSettings();
        updateKeyboardAutofillSettings();
        hideIfIntentUnhandled(findPreference(KEY_HOME_SETTINGS));
        hideIfIntentUnhandled(findPreference(KEY_CAST_SETTINGS));
        hideIfIntentUnhandled(findPreference(KEY_USAGE));
    }

    public int getMetricsCategory() {
        return 1300;
    }

    private void hideIfIntentUnhandled(Preference preference) {
        if (preference != null && preference.isVisible()) {
            preference.setVisible(MainFragment.systemIntentIsHandled(getContext(), preference.getIntent()) != null);
        }
    }

    private boolean isRestricted() {
        return SecurityFragment.isRestrictedProfileInEffect(getContext());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateDeveloperOptions() {
        Preference developerPref = findPreference(KEY_DEVELOPER);
        if (developerPref != null) {
            developerPref.setVisible(DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext()));
        }
    }

    private void updateSounds() {
        if (this.mSoundsPref != null) {
            this.mSoundsPref.setIcon(SoundFragment.getSoundEffectsEnabled(getContext().getContentResolver()) ? R.drawable.ic_volume_up : R.drawable.ic_volume_off);
            if (MainFragment.systemIntentIsHandled(getContext(), new Intent(MainFragment.ACTION_SOUND)) != null) {
                this.mSoundsPref.setVisible(false);
            }
        }
    }

    private void updateGoogleSettings() {
        Preference googleSettingsPref = findPreference(KEY_GOOGLE_SETTINGS);
        if (googleSettingsPref != null) {
            ResolveInfo info = MainFragment.systemIntentIsHandled(getContext(), googleSettingsPref.getIntent());
            googleSettingsPref.setVisible(info != null);
            if (info != null && info.activityInfo != null) {
                googleSettingsPref.setIcon(info.activityInfo.loadIcon(getContext().getPackageManager()));
                googleSettingsPref.setTitle(info.activityInfo.loadLabel(getContext().getPackageManager()));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateCastSettings() {
        ResolveInfo info;
        Preference castPref = findPreference(KEY_CAST_SETTINGS);
        if (castPref != null && (info = MainFragment.systemIntentIsHandled(getContext(), castPref.getIntent())) != null) {
            try {
                castPref.setIcon(getContext().createPackageContext(info.resolvePackageName != null ? info.resolvePackageName : info.activityInfo.packageName, 0).getDrawable(info.getIconResource()));
            } catch (PackageManager.NameNotFoundException | Resources.NotFoundException | SecurityException e) {
                Log.e(TAG, "Cast settings icon not found", e);
            }
            castPref.setTitle(info.activityInfo.loadLabel(getContext().getPackageManager()));
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateKeyboardAutofillSettings() {
        InputMethodInfo info;
        Preference keyboardPref = findPreference(KEY_KEYBOARD);
        List<DefaultAppInfo> candidates = AutofillHelper.getAutofillCandidates(getContext(), this.mPm, UserHandle.myUserId());
        if (candidates.isEmpty()) {
            keyboardPref.setTitle((int) R.string.system_keyboard);
        } else {
            keyboardPref.setTitle((int) R.string.system_keyboard_autofill);
        }
        CharSequence summary = "";
        String defaultImId = InputMethodHelper.getDefaultInputMethodId(getContext());
        if (!TextUtils.isEmpty(defaultImId) && (info = InputMethodHelper.findInputMethod(defaultImId, InputMethodHelper.getEnabledSystemInputMethodList(getContext()))) != null) {
            summary = info.loadLabel(getContext().getPackageManager());
        }
        DefaultAppInfo appInfo = AutofillHelper.getCurrentAutofill(getContext(), candidates);
        if (appInfo != null) {
            CharSequence autofillInfo = appInfo.loadLabel();
            if (summary.length() > 0) {
                getContext().getString(R.string.string_concat, new Object[]{summary, autofillInfo});
            } else {
                summary = autofillInfo;
            }
        }
        keyboardPref.setSummary(summary);
    }
}
