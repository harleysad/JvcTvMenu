package com.android.tv.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Keep
public class AccountSyncFragment extends SettingsPreferenceFragment implements AuthenticatorHelper.OnAccountsUpdateListener {
    private static final String ARG_ACCOUNT = "account";
    private static final String KEY_REMOVE_ACCOUNT = "remove_account";
    private static final String KEY_SYNC_ADAPTERS = "sync_adapters";
    private static final String KEY_SYNC_NOW = "sync_now";
    private static final String TAG = "AccountSyncFragment";
    private Account mAccount;
    private AuthenticatorHelper mAuthenticatorHelper;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private ArrayList<SyncAdapterType> mInvisibleAdapters = Lists.newArrayList();
    private Object mStatusChangeListenerHandle;
    private PreferenceGroup mSyncCategory;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        public void onStatusChanged(int which) {
            AccountSyncFragment.this.mHandler.post(new Runnable() {
                public void run() {
                    if (AccountSyncFragment.this.isResumed()) {
                        AccountSyncFragment.this.onSyncStateUpdated();
                    }
                }
            });
        }
    };
    private UserHandle mUserHandle;

    public static AccountSyncFragment newInstance(Account account) {
        Bundle b = new Bundle(1);
        prepareArgs(b, account);
        AccountSyncFragment f = new AccountSyncFragment();
        f.setArguments(b);
        return f;
    }

    public static void prepareArgs(Bundle b, Account account) {
        b.putParcelable(ARG_ACCOUNT, account);
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mUserHandle = new UserHandle(UserHandle.myUserId());
        this.mAccount = (Account) getArguments().getParcelable(ARG_ACCOUNT);
        this.mAuthenticatorHelper = new AuthenticatorHelper(getActivity(), this.mUserHandle, this);
        super.onCreate(savedInstanceState);
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Got account: " + this.mAccount);
        }
    }

    public void onStart() {
        super.onStart();
        this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
        onSyncStateUpdated();
        this.mAuthenticatorHelper.listenToAccountUpdates();
        this.mAuthenticatorHelper.updateAuthDescriptions(getActivity());
    }

    public void onResume() {
        super.onResume();
        this.mHandler.post(new Runnable() {
            public final void run() {
                AccountSyncFragment.this.onAccountsUpdate(AccountSyncFragment.this.mUserHandle);
            }
        });
    }

    public void onStop() {
        super.onStop();
        ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
        this.mAuthenticatorHelper.stopListeningToAccountUpdates();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preference, (String) null);
        getPreferenceScreen().setTitle((CharSequence) this.mAccount.name);
        findPreference(KEY_REMOVE_ACCOUNT).setIntent(new Intent(getActivity(), RemoveAccountDialog.class).putExtra(AccountSyncActivity.EXTRA_ACCOUNT, this.mAccount.name));
        this.mSyncCategory = (PreferenceGroup) findPreference(KEY_SYNC_ADAPTERS);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof SyncStateSwitchPreference) {
            SyncStateSwitchPreference syncPref = (SyncStateSwitchPreference) preference;
            String authority = syncPref.getAuthority();
            Account account = syncPref.getAccount();
            int userId = this.mUserHandle.getIdentifier();
            if (syncPref.isOneTimeSyncMode()) {
                requestOrCancelSync(account, authority, true);
            } else {
                boolean syncOn = syncPref.isChecked();
                if (syncOn != ContentResolver.getSyncAutomaticallyAsUser(account, authority, userId)) {
                    ContentResolver.setSyncAutomaticallyAsUser(account, authority, syncOn, userId);
                    if (!ContentResolver.getMasterSyncAutomaticallyAsUser(userId) || !syncOn) {
                        requestOrCancelSync(account, authority, syncOn);
                    }
                }
            }
            return true;
        } else if (!TextUtils.equals(preference.getKey(), KEY_SYNC_NOW)) {
            return super.onPreferenceTreeClick(preference);
        } else {
            if (!ContentResolver.getCurrentSyncsAsUser(this.mUserHandle.getIdentifier()).isEmpty()) {
                cancelSyncForEnabledProviders();
            } else {
                startSyncForEnabledProviders();
            }
            return true;
        }
    }

    private void startSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(true);
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private void cancelSyncForEnabledProviders() {
        requestOrCancelSyncForEnabledProviders(false);
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    private void requestOrCancelSyncForEnabledProviders(boolean startSync) {
        int count = this.mSyncCategory.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference pref = this.mSyncCategory.getPreference(i);
            if (pref instanceof SyncStateSwitchPreference) {
                SyncStateSwitchPreference syncPref = (SyncStateSwitchPreference) pref;
                if (syncPref.isChecked()) {
                    requestOrCancelSync(syncPref.getAccount(), syncPref.getAuthority(), startSync);
                }
            }
        }
        if (this.mAccount != null) {
            Iterator<SyncAdapterType> it = this.mInvisibleAdapters.iterator();
            while (it.hasNext()) {
                requestOrCancelSync(this.mAccount, it.next().authority, startSync);
            }
        }
    }

    private void requestOrCancelSync(Account account, String authority, boolean flag) {
        if (flag) {
            Bundle extras = new Bundle();
            extras.putBoolean("force", true);
            ContentResolver.requestSyncAsUser(account, authority, this.mUserHandle.getIdentifier(), extras);
            return;
        }
        ContentResolver.cancelSyncAsUser(account, authority, this.mUserHandle.getIdentifier());
    }

    private boolean isSyncing(List<SyncInfo> currentSyncs, Account account, String authority) {
        for (SyncInfo syncInfo : currentSyncs) {
            if (syncInfo.account.equals(account) && syncInfo.authority.equals(authority)) {
                return true;
            }
        }
        return false;
    }

    private boolean accountExists(Account account) {
        if (account == null) {
            return false;
        }
        for (Account other : AccountManager.get(getActivity()).getAccountsByTypeAsUser(account.type, this.mUserHandle)) {
            if (other.equals(account)) {
                return true;
            }
        }
        return false;
    }

    public void onAccountsUpdate(UserHandle userHandle) {
        if (isResumed()) {
            if (accountExists(this.mAccount)) {
                updateAccountSwitches();
                onSyncStateUpdated();
            } else if (!getFragmentManager().popBackStackImmediate()) {
                getActivity().finish();
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0105 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0111 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSyncStateUpdated() {
        /*
            r24 = this;
            r0 = r24
            android.os.UserHandle r1 = r0.mUserHandle
            int r1 = r1.getIdentifier()
            java.util.List r2 = android.content.ContentResolver.getCurrentSyncsAsUser(r1)
            r24.updateAccountSwitches()
            r3 = 0
            android.support.v7.preference.PreferenceGroup r4 = r0.mSyncCategory
            int r4 = r4.getPreferenceCount()
        L_0x0016:
            if (r3 >= r4) goto L_0x013b
            android.support.v7.preference.PreferenceGroup r5 = r0.mSyncCategory
            android.support.v7.preference.Preference r5 = r5.getPreference(r3)
            boolean r6 = r5 instanceof com.android.tv.settings.accounts.SyncStateSwitchPreference
            if (r6 != 0) goto L_0x0029
            r19 = r2
            r20 = r4
            goto L_0x0131
        L_0x0029:
            r6 = r5
            com.android.tv.settings.accounts.SyncStateSwitchPreference r6 = (com.android.tv.settings.accounts.SyncStateSwitchPreference) r6
            java.lang.String r7 = r6.getAuthority()
            android.accounts.Account r8 = r6.getAccount()
            android.content.SyncStatusInfo r9 = android.content.ContentResolver.getSyncStatusAsUser(r8, r7, r1)
            boolean r10 = android.content.ContentResolver.getSyncAutomaticallyAsUser(r8, r7, r1)
            r11 = 0
            if (r9 == 0) goto L_0x0045
            boolean r13 = r9.pending
            if (r13 == 0) goto L_0x0045
            r13 = 1
            goto L_0x0046
        L_0x0045:
            r13 = r11
        L_0x0046:
            if (r9 == 0) goto L_0x004e
            boolean r14 = r9.initialize
            if (r14 == 0) goto L_0x004e
            r14 = 1
            goto L_0x004f
        L_0x004e:
            r14 = r11
        L_0x004f:
            boolean r15 = r0.isSyncing(r2, r8, r7)
            r16 = 0
            if (r9 == 0) goto L_0x0068
            r18 = r13
            long r12 = r9.lastFailureTime
            int r12 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r12 == 0) goto L_0x006a
            int r12 = r9.getLastFailureMesgAsInt(r11)
            r13 = 1
            if (r12 == r13) goto L_0x006a
            r12 = 1
            goto L_0x006b
        L_0x0068:
            r18 = r13
        L_0x006a:
            r12 = r11
        L_0x006b:
            if (r10 != 0) goto L_0x006e
            r12 = 0
        L_0x006e:
            java.lang.String r13 = "AccountSyncFragment"
            r11 = 2
            boolean r11 = android.util.Log.isLoggable(r13, r11)
            if (r11 == 0) goto L_0x00aa
            java.lang.String r11 = "AccountSyncFragment"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r19 = r2
            java.lang.String r2 = "Update sync status: "
            r13.append(r2)
            r13.append(r8)
            java.lang.String r2 = " "
            r13.append(r2)
            r13.append(r7)
            java.lang.String r2 = " active = "
            r13.append(r2)
            r13.append(r15)
            java.lang.String r2 = " pend ="
            r13.append(r2)
            r2 = r18
            r13.append(r2)
            java.lang.String r13 = r13.toString()
            android.util.Log.v(r11, r13)
            goto L_0x00ae
        L_0x00aa:
            r19 = r2
            r2 = r18
        L_0x00ae:
            if (r9 != 0) goto L_0x00b7
            r20 = r4
            r21 = r5
            r4 = r16
            goto L_0x00bd
        L_0x00b7:
            r20 = r4
            r21 = r5
            long r4 = r9.lastSuccessTime
        L_0x00bd:
            if (r10 != 0) goto L_0x00c9
            r11 = 2131690640(0x7f0f0490, float:1.901033E38)
            r6.setSummary((int) r11)
        L_0x00c5:
            r22 = r4
            r0 = 0
            goto L_0x00ff
        L_0x00c9:
            if (r15 == 0) goto L_0x00d2
            r11 = 2131690642(0x7f0f0492, float:1.9010333E38)
            r6.setSummary((int) r11)
            goto L_0x00c5
        L_0x00d2:
            int r11 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r11 == 0) goto L_0x00f7
            android.app.Activity r11 = r24.getActivity()
            r13 = 17
            java.lang.String r11 = android.text.format.DateUtils.formatDateTime(r11, r4, r13)
            android.content.res.Resources r13 = r24.getResources()
            r22 = r4
            r0 = 1
            java.lang.Object[] r4 = new java.lang.Object[r0]
            r0 = 0
            r4[r0] = r11
            r5 = 2131690228(0x7f0f02f4, float:1.9009494E38)
            java.lang.String r4 = r13.getString(r5, r4)
            r6.setSummary((java.lang.CharSequence) r4)
            goto L_0x00ff
        L_0x00f7:
            r22 = r4
            r0 = 0
            java.lang.String r4 = ""
            r6.setSummary((java.lang.CharSequence) r4)
        L_0x00ff:
            int r4 = android.content.ContentResolver.getIsSyncableAsUser(r8, r7, r1)
            if (r15 == 0) goto L_0x010b
            if (r4 < 0) goto L_0x010b
            if (r14 != 0) goto L_0x010b
            r5 = 1
            goto L_0x010c
        L_0x010b:
            r5 = r0
        L_0x010c:
            r6.setActive(r5)
            if (r2 == 0) goto L_0x0117
            if (r4 < 0) goto L_0x0117
            if (r14 != 0) goto L_0x0117
            r5 = 1
            goto L_0x0118
        L_0x0117:
            r5 = r0
        L_0x0118:
            r6.setPending(r5)
            r6.setFailed(r12)
            boolean r5 = android.content.ContentResolver.getMasterSyncAutomaticallyAsUser(r1)
            r13 = 1
            r5 = r5 ^ r13
            r6.setOneTimeSyncMode(r5)
            if (r5 != 0) goto L_0x012e
            if (r10 == 0) goto L_0x012c
            goto L_0x012e
        L_0x012c:
            r13 = r0
        L_0x012e:
            r6.setChecked(r13)
        L_0x0131:
            int r3 = r3 + 1
            r2 = r19
            r4 = r20
            r0 = r24
            goto L_0x0016
        L_0x013b:
            r19 = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.accounts.AccountSyncFragment.onSyncStateUpdated():void");
    }

    private void updateAccountSwitches() {
        this.mInvisibleAdapters.clear();
        SyncAdapterType[] syncAdapters = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
        ArrayList<String> authorities = new ArrayList<>(syncAdapters.length);
        for (SyncAdapterType sa : syncAdapters) {
            if (sa.accountType.equals(this.mAccount.type)) {
                if (sa.isUserVisible()) {
                    if (Log.isLoggable(TAG, 2)) {
                        Log.v(TAG, "updateAccountSwitches: added authority " + sa.authority + " to accountType " + sa.accountType);
                    }
                    authorities.add(sa.authority);
                } else {
                    this.mInvisibleAdapters.add(sa);
                }
            }
        }
        this.mSyncCategory.removeAll();
        List<Preference> switches = new ArrayList<>(authorities.size());
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "looking for sync adapters that match account " + this.mAccount);
        }
        Iterator<String> it = authorities.iterator();
        while (it.hasNext()) {
            String authority = it.next();
            int syncState = ContentResolver.getIsSyncableAsUser(this.mAccount, authority, this.mUserHandle.getIdentifier());
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "  found authority " + authority + " " + syncState);
            }
            if (syncState > 0) {
                switches.add(createSyncStateSwitch(this.mAccount, authority));
            }
        }
        Collections.sort(switches);
        for (Preference pref : switches) {
            this.mSyncCategory.addPreference(pref);
        }
    }

    private Preference createSyncStateSwitch(Account account, String authority) {
        SyncStateSwitchPreference preference = new SyncStateSwitchPreference(getPreferenceManager().getContext(), account, authority);
        preference.setPersistent(false);
        PackageManager packageManager = getActivity().getPackageManager();
        ProviderInfo providerInfo = packageManager.resolveContentProviderAsUser(authority, 0, this.mUserHandle.getIdentifier());
        if (providerInfo == null) {
            return null;
        }
        CharSequence providerLabel = providerInfo.loadLabel(packageManager);
        if (TextUtils.isEmpty(providerLabel)) {
            Log.e(TAG, "Provider needs a label for authority '" + authority + "'");
            return null;
        }
        preference.setTitle((CharSequence) getString(R.string.sync_item_title, new Object[]{providerLabel}));
        preference.setKey(authority);
        return preference;
    }

    public int getMetricsCategory() {
        return 9;
    }
}
