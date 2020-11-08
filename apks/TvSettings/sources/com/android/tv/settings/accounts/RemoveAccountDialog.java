package com.android.tv.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.R;
import java.io.IOException;
import java.util.List;

public class RemoveAccountDialog extends Activity implements AccountManagerCallback<Bundle> {
    private static final String TAG = "RemoveAccountDialog";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, RemoveAccountFragment.newInstance(getIntent().getStringExtra(AccountSyncActivity.EXTRA_ACCOUNT)), 16908290);
        }
    }

    public void run(AccountManagerFuture<Bundle> future) {
        if (isResumed()) {
            try {
                if (!future.getResult().getBoolean("booleanResult")) {
                    Toast.makeText(this, R.string.account_remove_failed, 1).show();
                }
            } catch (AuthenticatorException | OperationCanceledException | IOException e) {
                Log.e(TAG, "Could not remove", e);
            }
            finish();
        } else if (!isFinishing()) {
            finish();
        }
    }

    public static class RemoveAccountFragment extends GuidedStepFragment {
        private static final String ARG_ACCOUNT_NAME = "accountName";
        private static final int ID_CANCEL = 0;
        private static final int ID_OK = 1;
        private String mAccountName;
        private boolean mIsRemoving;

        public static RemoveAccountFragment newInstance(String accountName) {
            RemoveAccountFragment f = new RemoveAccountFragment();
            Bundle b = new Bundle(1);
            b.putString(ARG_ACCOUNT_NAME, accountName);
            f.setArguments(b);
            return f;
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAccountName = getArguments().getString(ARG_ACCOUNT_NAME);
            super.onCreate(savedInstanceState);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.account_remove), (String) null, this.mAccountName, getActivity().getDrawable(R.drawable.ic_delete_132dp));
        }

        public void onGuidedActionClicked(GuidedAction action) {
            RemoveAccountDialog activity = (RemoveAccountDialog) getActivity();
            if (action.getId() != 1) {
                activity.finish();
            } else if (ActivityManager.isUserAMonkey()) {
                activity.finish();
            } else if (!this.mIsRemoving) {
                this.mIsRemoving = true;
                AccountManager manager = AccountManager.get(activity.getApplicationContext());
                Account account = null;
                Account[] accounts = manager.getAccounts();
                int length = accounts.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Account accountLoop = accounts[i];
                    if (accountLoop.name.equals(this.mAccountName)) {
                        account = accountLoop;
                        break;
                    }
                    i++;
                }
                manager.removeAccount(account, activity, activity, new Handler());
            }
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().id(0)).title((CharSequence) getString(17039360))).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().id(1)).title((CharSequence) getString(17039370))).build());
        }
    }
}
