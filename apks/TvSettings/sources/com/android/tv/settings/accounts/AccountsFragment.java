package com.android.tv.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.system.SecurityFragment;
import java.util.ArrayList;
import java.util.Set;

@Keep
public class AccountsFragment extends SettingsPreferenceFragment {
    private static final String KEY_ADD_ACCOUNT = "add_account";
    private static final String TAG = "AccountsFragment";
    private AuthenticatorHelper mAuthenticatorHelper;

    public void onCreate(Bundle savedInstanceState) {
        this.mAuthenticatorHelper = new AuthenticatorHelper(getContext(), new UserHandle(UserHandle.myUserId()), new AuthenticatorHelper.OnAccountsUpdateListener() {
            public final void onAccountsUpdate(UserHandle userHandle) {
                AccountsFragment.this.updateAccounts();
            }
        });
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.accounts, (String) null);
    }

    public void onStart() {
        super.onStart();
        this.mAuthenticatorHelper.listenToAccountUpdates();
    }

    public void onStop() {
        super.onStop();
        this.mAuthenticatorHelper.stopListeningToAccountUpdates();
    }

    public void onResume() {
        super.onResume();
        updateAccounts();
    }

    /* access modifiers changed from: private */
    public void updateAccounts() {
        Context themedContext;
        String str;
        PreferenceScreen prefScreen = getPreferenceScreen();
        Set<String> touchedAccounts = new ArraySet<>(prefScreen.getPreferenceCount());
        AccountManager am = AccountManager.get(getContext());
        AuthenticatorDescription[] authTypes = am.getAuthenticatorTypes();
        ArrayList<String> allowableAccountTypes = new ArrayList<>(authTypes.length);
        Context themedContext2 = getPreferenceManager().getContext();
        int length = authTypes.length;
        int i = 0;
        while (i < length) {
            AuthenticatorDescription authDesc = authTypes[i];
            Context targetContext = getTargetContext(getContext(), authDesc);
            if (targetContext != null) {
                String authTitle = getAuthTitle(targetContext, authDesc);
                Account[] accounts = am.getAccountsByType(authDesc.type);
                if (!(accounts == null || accounts.length == 0)) {
                    Drawable authImage = getAuthImage(targetContext, authDesc);
                    int length2 = accounts.length;
                    int i2 = 0;
                    while (i2 < length2) {
                        AccountManager am2 = am;
                        Account account = accounts[i2];
                        AuthenticatorDescription[] authTypes2 = authTypes;
                        StringBuilder sb = new StringBuilder();
                        ArrayList<String> allowableAccountTypes2 = allowableAccountTypes;
                        sb.append("account_pref:");
                        sb.append(account.type);
                        sb.append(":");
                        sb.append(account.name);
                        String key = sb.toString();
                        Preference preference = findPreference(key);
                        if (preference == null) {
                            Preference preference2 = preference;
                            preference = new Preference(themedContext2);
                        } else {
                            Preference preference3 = preference;
                        }
                        if (authTitle != null) {
                            themedContext = themedContext2;
                            str = authTitle;
                        } else {
                            themedContext = themedContext2;
                            str = account.name;
                        }
                        preference.setTitle((CharSequence) str);
                        preference.setIcon(authImage);
                        preference.setSummary((CharSequence) authTitle != null ? account.name : null);
                        preference.setFragment(AccountSyncFragment.class.getName());
                        AccountSyncFragment.prepareArgs(preference.getExtras(), account);
                        touchedAccounts.add(key);
                        preference.setKey(key);
                        prefScreen.addPreference(preference);
                        i2++;
                        am = am2;
                        authTypes = authTypes2;
                        allowableAccountTypes = allowableAccountTypes2;
                        themedContext2 = themedContext;
                    }
                }
            }
            i++;
            am = am;
            authTypes = authTypes;
            allowableAccountTypes = allowableAccountTypes;
            themedContext2 = themedContext2;
        }
        AuthenticatorDescription[] authenticatorDescriptionArr = authTypes;
        ArrayList<String> arrayList = allowableAccountTypes;
        Context context = themedContext2;
        int i3 = 0;
        while (i3 < prefScreen.getPreferenceCount()) {
            Preference preference4 = prefScreen.getPreference(i3);
            String key2 = preference4.getKey();
            if (touchedAccounts.contains(key2) || TextUtils.equals(KEY_ADD_ACCOUNT, key2)) {
                i3++;
            } else {
                prefScreen.removePreference(preference4);
            }
        }
        Preference addAccountPref = findPreference(KEY_ADD_ACCOUNT);
        if (addAccountPref != null) {
            addAccountPref.setOrder(Integer.MAX_VALUE);
            if (isRestricted()) {
                addAccountPref.setVisible(false);
            } else {
                setUpAddAccountPrefIntent(addAccountPref, getContext());
            }
        }
    }

    private boolean isRestricted() {
        return SecurityFragment.isRestrictedProfileInEffect(getContext());
    }

    public int getMetricsCategory() {
        return 11;
    }

    public static void setUpAddAccountPrefIntent(Preference preference, Context context) {
        AuthenticatorDescription[] authTypes = AccountManager.get(context).getAuthenticatorTypes();
        ArrayList<String> allowableAccountTypes = new ArrayList<>(authTypes.length);
        for (AuthenticatorDescription authDesc : authTypes) {
            Context targetContext = getTargetContext(context, authDesc);
            if (!(targetContext == null || (getAuthTitle(targetContext, authDesc) == null && authDesc.iconId == 0))) {
                allowableAccountTypes.add(authDesc.type);
            }
        }
        Intent i = new Intent().setComponent(new ComponentName("com.android.tv.settings", "com.android.tv.settings.accounts.AddAccountWithTypeActivity"));
        i.putExtra(AddAccountWithTypeActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, (String[]) allowableAccountTypes.toArray(new String[allowableAccountTypes.size()]));
        preference.setVisible(!allowableAccountTypes.isEmpty());
        preference.setIntent(i);
    }

    private static Context getTargetContext(Context context, AuthenticatorDescription authDesc) {
        try {
            return context.createPackageContext(authDesc.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Authenticator description with bad package name", e);
            return null;
        } catch (SecurityException e2) {
            Log.e(TAG, "Security exception loading package resources", e2);
            return null;
        }
    }

    private static String getAuthTitle(Context targetContext, AuthenticatorDescription authDesc) {
        try {
            String authTitle = targetContext.getString(authDesc.labelId);
            if (TextUtils.isEmpty(authTitle)) {
                return null;
            }
            return authTitle;
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Authenticator description with bad label id", e);
            return null;
        }
    }

    private static Drawable getAuthImage(Context targetContext, AuthenticatorDescription authDesc) {
        try {
            return targetContext.getDrawable(authDesc.iconId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Authenticator has bad resources", e);
            return null;
        }
    }
}
