package com.android.tv.settings.system;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.Keep;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.LockPatternUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.dialog.PinDialogFragment;
import com.android.tv.settings.users.AppRestrictionsFragment;
import com.android.tv.settings.users.RestrictedProfileModel;
import com.android.tv.settings.users.RestrictedProfilePinDialogFragment;
import com.android.tv.settings.users.UserSwitchListenerService;
import com.android.tv.twopanelsettings.TwoPanelSettingsFragment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
public class SecurityFragment extends SettingsPreferenceFragment implements RestrictedProfilePinDialogFragment.Callback {
    private static final String ACTION_RESTRICTED_PROFILE_CREATED = "SecurityFragment.RESTRICTED_PROFILE_CREATED";
    private static final String EXTRA_RESTRICTED_PROFILE_INFO = "SecurityFragment.RESTRICTED_PROFILE_INFO";
    private static final String KEY_RESTRICTED_PROFILE_APPS = "restricted_profile_apps";
    private static final String KEY_RESTRICTED_PROFILE_CREATE = "restricted_profile_create";
    private static final String KEY_RESTRICTED_PROFILE_DELETE = "restricted_profile_delete";
    private static final String KEY_RESTRICTED_PROFILE_ENTER = "restricted_profile_enter";
    private static final String KEY_RESTRICTED_PROFILE_EXIT = "restricted_profile_exit";
    private static final String KEY_RESTRICTED_PROFILE_GROUP = "restricted_profile_group";
    private static final String KEY_RESTRICTED_PROFILE_PIN = "restricted_profile_pin";
    private static final String KEY_UNKNOWN_SOURCES = "unknown_sources";
    private static final String KEY_VERIFY_APPS = "verify_apps";
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final int PIN_MODE_CHOOSE_LOCKSCREEN = 1;
    private static final int PIN_MODE_RESTRICTED_PROFILE_CHANGE_PASSWORD = 3;
    private static final int PIN_MODE_RESTRICTED_PROFILE_DELETE = 4;
    private static final int PIN_MODE_RESTRICTED_PROFILE_SWITCH_OUT = 2;
    private static final String SAVESTATE_CREATING_RESTRICTED_PROFILE = "SecurityFragment.CREATING_RESTRICTED_PROFILE";
    private static final String TAG = "SecurityFragment";
    /* access modifiers changed from: private */
    @SuppressLint({"StaticFieldLeak"})
    public static CreateRestrictedProfileTask sCreateRestrictedProfileTask;
    private boolean mCreatingRestrictedProfile;
    private final Handler mHandler = new Handler();
    private ILockSettings mLockSettingsService;
    private RestrictedProfileModel mRestrictedProfile;
    private Preference mRestrictedProfileAppsPref;
    private Preference mRestrictedProfileCreatePref;
    private Preference mRestrictedProfileDeletePref;
    private Preference mRestrictedProfileEnterPref;
    private Preference mRestrictedProfileExitPref;
    private PreferenceGroup mRestrictedProfileGroup;
    private Preference mRestrictedProfilePinPref;
    private final BroadcastReceiver mRestrictedProfileReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            UserInfo result = intent.getParcelableExtra(SecurityFragment.EXTRA_RESTRICTED_PROFILE_INFO);
            if (SecurityFragment.this.isResumed()) {
                SecurityFragment.this.onRestrictedUserCreated(result);
            }
        }
    };
    private Preference mUnknownSourcesPref;
    private TwoStatePreference mVerifyAppsPref;

    @Retention(RetentionPolicy.SOURCE)
    private @interface PinMode {
    }

    public static SecurityFragment newInstance() {
        return new SecurityFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mRestrictedProfile = new RestrictedProfileModel(getContext());
        super.onCreate(savedInstanceState);
        this.mCreatingRestrictedProfile = savedInstanceState != null && savedInstanceState.getBoolean(SAVESTATE_CREATING_RESTRICTED_PROFILE);
    }

    public void onResume() {
        UserInfo userInfo;
        super.onResume();
        refresh();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.mRestrictedProfileReceiver, new IntentFilter(ACTION_RESTRICTED_PROFILE_CREATED));
        if (this.mCreatingRestrictedProfile && (userInfo = this.mRestrictedProfile.getUser()) != null) {
            onRestrictedUserCreated(userInfo);
        }
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.mRestrictedProfileReceiver);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVESTATE_CREATING_RESTRICTED_PROFILE, this.mCreatingRestrictedProfile);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.security, (String) null);
        this.mUnknownSourcesPref = findPreference(KEY_UNKNOWN_SOURCES);
        this.mVerifyAppsPref = (TwoStatePreference) findPreference(KEY_VERIFY_APPS);
        this.mRestrictedProfileGroup = (PreferenceGroup) findPreference(KEY_RESTRICTED_PROFILE_GROUP);
        this.mRestrictedProfileEnterPref = findPreference(KEY_RESTRICTED_PROFILE_ENTER);
        this.mRestrictedProfileExitPref = findPreference(KEY_RESTRICTED_PROFILE_EXIT);
        this.mRestrictedProfileAppsPref = findPreference(KEY_RESTRICTED_PROFILE_APPS);
        this.mRestrictedProfilePinPref = findPreference(KEY_RESTRICTED_PROFILE_PIN);
        this.mRestrictedProfileCreatePref = findPreference(KEY_RESTRICTED_PROFILE_CREATE);
        this.mRestrictedProfileDeletePref = findPreference(KEY_RESTRICTED_PROFILE_DELETE);
    }

    private void refresh() {
        boolean z = false;
        if (this.mRestrictedProfile.isCurrentUser()) {
            this.mUnknownSourcesPref.setVisible(false);
            this.mVerifyAppsPref.setVisible(false);
            this.mRestrictedProfileGroup.setVisible(true);
            this.mRestrictedProfileEnterPref.setVisible(false);
            this.mRestrictedProfileExitPref.setVisible(true);
            this.mRestrictedProfileAppsPref.setVisible(false);
            this.mRestrictedProfilePinPref.setVisible(false);
            this.mRestrictedProfileCreatePref.setVisible(false);
            this.mRestrictedProfileDeletePref.setVisible(false);
        } else if (this.mRestrictedProfile.getUser() != null) {
            this.mUnknownSourcesPref.setVisible(true);
            this.mVerifyAppsPref.setVisible(shouldShowVerifierSetting());
            this.mRestrictedProfileGroup.setVisible(true);
            this.mRestrictedProfileEnterPref.setVisible(true);
            this.mRestrictedProfileExitPref.setVisible(false);
            this.mRestrictedProfileAppsPref.setVisible(true);
            this.mRestrictedProfilePinPref.setVisible(true);
            this.mRestrictedProfileCreatePref.setVisible(false);
            this.mRestrictedProfileDeletePref.setVisible(true);
            AppRestrictionsFragment.prepareArgs(this.mRestrictedProfileAppsPref.getExtras(), this.mRestrictedProfile.getUser().id, false);
        } else if (UserManager.supportsMultipleUsers()) {
            this.mUnknownSourcesPref.setVisible(true);
            this.mVerifyAppsPref.setVisible(shouldShowVerifierSetting());
            this.mRestrictedProfileGroup.setVisible(true);
            this.mRestrictedProfileEnterPref.setVisible(false);
            this.mRestrictedProfileExitPref.setVisible(false);
            this.mRestrictedProfileAppsPref.setVisible(false);
            this.mRestrictedProfilePinPref.setVisible(false);
            this.mRestrictedProfileCreatePref.setVisible(true);
            this.mRestrictedProfileDeletePref.setVisible(false);
        } else {
            this.mUnknownSourcesPref.setVisible(true);
            this.mVerifyAppsPref.setVisible(shouldShowVerifierSetting());
            this.mRestrictedProfileGroup.setVisible(false);
            this.mRestrictedProfileEnterPref.setVisible(false);
            this.mRestrictedProfileExitPref.setVisible(false);
            this.mRestrictedProfileAppsPref.setVisible(false);
            this.mRestrictedProfilePinPref.setVisible(false);
            this.mRestrictedProfileCreatePref.setVisible(false);
            this.mRestrictedProfileDeletePref.setVisible(false);
        }
        Preference preference = this.mRestrictedProfileCreatePref;
        if (sCreateRestrictedProfileTask == null) {
            z = true;
        }
        preference.setEnabled(z);
        this.mUnknownSourcesPref.setEnabled(true ^ isUnknownSourcesBlocked());
        this.mVerifyAppsPref.setChecked(isVerifyAppsEnabled());
        this.mVerifyAppsPref.setEnabled(isVerifierInstalled());
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (TextUtils.isEmpty(key)) {
            return super.onPreferenceTreeClick(preference);
        }
        char c = 65535;
        switch (key.hashCode()) {
            case -1948819848:
                if (key.equals(KEY_VERIFY_APPS)) {
                    c = 0;
                    break;
                }
                break;
            case -803612490:
                if (key.equals(KEY_RESTRICTED_PROFILE_CREATE)) {
                    c = 4;
                    break;
                }
                break;
            case -786776731:
                if (key.equals(KEY_RESTRICTED_PROFILE_DELETE)) {
                    c = 5;
                    break;
                }
                break;
            case -769484968:
                if (key.equals(KEY_RESTRICTED_PROFILE_EXIT)) {
                    c = 2;
                    break;
                }
                break;
            case -579001317:
                if (key.equals(KEY_RESTRICTED_PROFILE_PIN)) {
                    c = 3;
                    break;
                }
                break;
            case 1915482078:
                if (key.equals(KEY_RESTRICTED_PROFILE_ENTER)) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setVerifyAppsEnabled(this.mVerifyAppsPref.isChecked());
                return true;
            case 1:
                if (this.mRestrictedProfile.enterUser()) {
                    getActivity().finish();
                }
                return true;
            case 2:
                launchPinDialog(2);
                return true;
            case 3:
                launchPinDialog(3);
                return true;
            case 4:
                if (hasLockscreenSecurity(new LockPatternUtils(getActivity()))) {
                    addRestrictedUser();
                } else {
                    launchPinDialog(1);
                }
                return true;
            case 5:
                launchPinDialog(4);
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    private boolean isUnknownSourcesBlocked() {
        return ((UserManager) getContext().getSystemService("user")).hasUserRestriction("no_install_unknown_sources");
    }

    private boolean isVerifyAppsEnabled() {
        if (Settings.Global.getInt(getContext().getContentResolver(), "package_verifier_enable", 1) <= 0 || !isVerifierInstalled()) {
            return false;
        }
        return true;
    }

    private void setVerifyAppsEnabled(boolean enable) {
        Settings.Global.putInt(getContext().getContentResolver(), "package_verifier_enable", enable);
    }

    private boolean isVerifierInstalled() {
        PackageManager pm = getContext().getPackageManager();
        Intent verification = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
        verification.setType(PACKAGE_MIME_TYPE);
        verification.addFlags(1);
        if (pm.queryBroadcastReceivers(verification, 0).size() > 0) {
            return true;
        }
        return false;
    }

    private boolean shouldShowVerifierSetting() {
        return Settings.Global.getInt(getContext().getContentResolver(), "verifier_setting_visible", 1) > 0;
    }

    private void launchPinDialog(int pinMode) {
        int pinDialogMode;
        switch (pinMode) {
            case 1:
                pinDialogMode = 3;
                break;
            case 2:
                pinDialogMode = 2;
                break;
            case 3:
                pinDialogMode = 3;
                break;
            case 4:
                pinDialogMode = 5;
                break;
            default:
                throw new IllegalArgumentException("Unknown pin mode: " + pinMode);
        }
        RestrictedProfilePinDialogFragment restrictedProfilePinDialogFragment = RestrictedProfilePinDialogFragment.newInstance(pinDialogMode);
        restrictedProfilePinDialogFragment.setTargetFragment(this, pinMode);
        restrictedProfilePinDialogFragment.show(getFragmentManager(), PinDialogFragment.DIALOG_TAG);
    }

    public void saveLockPassword(String pin, String originalPin, int quality) {
        new LockPatternUtils(getActivity()).saveLockPassword(pin, originalPin, quality, UserHandle.myUserId());
    }

    public void clearLockPassword(String oldPin) {
        new LockPatternUtils(getActivity()).clearLock(oldPin != null ? oldPin.getBytes() : null, getContext().getUserId());
    }

    public boolean checkPassword(String password, int userId) {
        byte[] passwordBytes;
        if (password != null) {
            try {
                passwordBytes = password.getBytes();
            } catch (RemoteException e) {
                return false;
            }
        } else {
            passwordBytes = null;
        }
        if (getLockSettings().checkCredential(passwordBytes, 2, userId, (ICheckCredentialProgressCallback) null).getResponseCode() == 0) {
            return true;
        }
        return false;
    }

    public boolean hasLockscreenSecurity() {
        return hasLockscreenSecurity(new LockPatternUtils(getActivity()));
    }

    private ILockSettings getLockSettings() {
        if (this.mLockSettingsService == null) {
            this.mLockSettingsService = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        }
        return this.mLockSettingsService;
    }

    private static boolean hasLockscreenSecurity(LockPatternUtils lpu) {
        return lpu.isLockPasswordEnabled(UserHandle.myUserId()) || lpu.isLockPatternEnabled(UserHandle.myUserId());
    }

    public void pinFragmentDone(int requestCode, boolean success) {
        switch (requestCode) {
            case 1:
                if (success) {
                    addRestrictedUser();
                    return;
                }
                return;
            case 2:
                if (success) {
                    this.mRestrictedProfile.exitUser();
                    getActivity().finish();
                    return;
                }
                return;
            case 4:
                if (success) {
                    this.mHandler.post(new Runnable() {
                        public final void run() {
                            SecurityFragment.lambda$pinFragmentDone$0(SecurityFragment.this);
                        }
                    });
                    return;
                }
                return;
            default:
                return;
        }
    }

    public static /* synthetic */ void lambda$pinFragmentDone$0(SecurityFragment securityFragment) {
        securityFragment.mRestrictedProfile.removeUser();
        UserSwitchListenerService.updateLaunchPoint(securityFragment.getActivity(), false);
        securityFragment.refresh();
    }

    private void addRestrictedUser() {
        if (sCreateRestrictedProfileTask == null) {
            sCreateRestrictedProfileTask = new CreateRestrictedProfileTask(getContext());
            sCreateRestrictedProfileTask.execute(new Void[0]);
            this.mCreatingRestrictedProfile = true;
        }
        refresh();
    }

    public static boolean isRestrictedProfileInEffect(Context context) {
        return new RestrictedProfileModel(context).isCurrentUser();
    }

    /* access modifiers changed from: private */
    public void onRestrictedUserCreated(UserInfo result) {
        int userId = result.id;
        if (result.isRestricted() && result.restrictedProfileParentId == UserHandle.myUserId()) {
            AppRestrictionsFragment restrictionsFragment = AppRestrictionsFragment.newInstance(userId, true);
            Fragment settingsFragment = getCallbackFragment();
            if (settingsFragment instanceof LeanbackSettingsFragment) {
                ((LeanbackSettingsFragment) settingsFragment).startPreferenceFragment(restrictionsFragment);
            } else if (settingsFragment instanceof TwoPanelSettingsFragment) {
                ((TwoPanelSettingsFragment) settingsFragment).startPreferenceFragment(restrictionsFragment);
            } else {
                throw new IllegalStateException("Didn't find fragment of expected type: " + settingsFragment);
            }
        }
        this.mCreatingRestrictedProfile = false;
        refresh();
    }

    private static class CreateRestrictedProfileTask extends AsyncTask<Void, Void, UserInfo> {
        private final Context mContext;
        private final UserManager mUserManager = ((UserManager) this.mContext.getSystemService("user"));

        CreateRestrictedProfileTask(Context context) {
            this.mContext = context.getApplicationContext();
        }

        /* access modifiers changed from: protected */
        public UserInfo doInBackground(Void... params) {
            UserInfo restrictedUserInfo = this.mUserManager.createProfileForUser(this.mContext.getString(R.string.user_new_profile_name), 8, UserHandle.myUserId());
            if (restrictedUserInfo == null) {
                UserInfo existingUserInfo = new RestrictedProfileModel(this.mContext).getUser();
                if (existingUserInfo == null) {
                    Log.wtf(SecurityFragment.TAG, "Got back a null user handle!");
                }
                return existingUserInfo;
            }
            int userId = restrictedUserInfo.id;
            UserHandle user = new UserHandle(userId);
            this.mUserManager.setUserRestriction("no_modify_accounts", true, user);
            this.mUserManager.setUserIcon(userId, createBitmapFromDrawable(R.drawable.ic_avatar_default));
            AccountManager.get(this.mContext).addSharedAccountsFromParentUser(UserHandle.of(UserHandle.myUserId()), user);
            return restrictedUserInfo;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(UserInfo result) {
            CreateRestrictedProfileTask unused = SecurityFragment.sCreateRestrictedProfileTask = null;
            if (result != null) {
                UserSwitchListenerService.updateLaunchPoint(this.mContext, true);
                LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(new Intent(SecurityFragment.ACTION_RESTRICTED_PROFILE_CREATED).putExtra(SecurityFragment.EXTRA_RESTRICTED_PROFILE_INFO, result));
            }
        }

        private Bitmap createBitmapFromDrawable(@DrawableRes int resId) {
            Drawable icon = this.mContext.getDrawable(resId);
            if (icon != null) {
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                icon.draw(new Canvas(bitmap));
                return bitmap;
            }
            throw new IllegalArgumentException("Drawable is missing!");
        }
    }

    public int getMetricsCategory() {
        return 87;
    }
}
