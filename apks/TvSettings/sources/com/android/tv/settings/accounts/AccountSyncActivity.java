package com.android.tv.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.tv.settings.TvSettingsActivity;
import com.android.tv.settings.overlay.FeatureFactory;

public class AccountSyncActivity extends TvSettingsActivity {
    private static final String ARG_ACCOUNT = "account";
    public static final String EXTRA_ACCOUNT = "account_name";

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        String accountName = getIntent().getStringExtra(EXTRA_ACCOUNT);
        Account account = null;
        if (!TextUtils.isEmpty(accountName)) {
            Account[] accounts = AccountManager.get(this).getAccounts();
            int length = accounts.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Account candidateAccount = accounts[i];
                if (candidateAccount.name.equals(accountName)) {
                    account = candidateAccount;
                    break;
                }
                i++;
            }
        }
        return FeatureFactory.getFactory(this).getSettingsFragmentProvider().newSettingsFragment(AccountSyncFragment.class.getName(), getArguments(account));
    }

    private Bundle getArguments(Account account) {
        Bundle b = new Bundle(1);
        b.putParcelable(ARG_ACCOUNT, account);
        return b;
    }
}
