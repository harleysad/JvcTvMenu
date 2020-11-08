package com.android.tv.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;

public class AddAccountWithTypeActivity extends Activity {
    private static final String CHOOSE_ACCOUNT_TYPE_ACTION = "com.google.android.gms.common.account.CHOOSE_ACCOUNT_TYPE";
    public static final String EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY = "allowableAccountTypes";
    private static final int REQUEST_ADD_ACCOUNT = 1;
    private static final int REQUEST_CHOOSE_ACCOUNT_TYPE = 0;
    private static final String TAG = "AddAccountWithType";
    private final AccountManagerCallback<Bundle> mCallback = new AccountManagerCallback<Bundle>() {
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Intent addAccountIntent = (Intent) future.getResult().getParcelable("intent");
                if (addAccountIntent == null) {
                    Log.e(AddAccountWithTypeActivity.TAG, "Failed to retrieve add account intent from authenticator");
                    AddAccountWithTypeActivity.this.setResultAndFinish(0);
                    return;
                }
                AddAccountWithTypeActivity.this.startActivityForResult(addAccountIntent, 1);
            } catch (AuthenticatorException | OperationCanceledException | IOException e) {
                Log.e(AddAccountWithTypeActivity.TAG, "Failed to get add account intent: ", e);
                AddAccountWithTypeActivity.this.setResultAndFinish(0);
            }
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String accountType = getIntent().getStringExtra("accountType");
        if (accountType != null) {
            startAddAccount(accountType);
            return;
        }
        String[] allowedTypes = getIntent().getStringArrayExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY);
        if (allowedTypes == null || allowedTypes.length == 0) {
            allowedTypes = getIntent().getStringArrayExtra("account_types");
        }
        startAccountTypePicker(allowedTypes);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == -1) {
            startAddAccount(data.getExtras().getString("accountType"));
        } else {
            setResultAndFinish(resultCode);
        }
    }

    private void startAccountTypePicker(String[] allowedTypes) {
        Intent i = new Intent(CHOOSE_ACCOUNT_TYPE_ACTION);
        i.putExtra(EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY, allowedTypes);
        startActivityForResult(i, 0);
    }

    private void startAddAccount(String accountType) {
        AccountManager.get(this).addAccount(accountType, (String) null, (String[]) null, (Bundle) null, (Activity) null, this.mCallback, (Handler) null);
    }

    /* access modifiers changed from: private */
    public void setResultAndFinish(int resultCode) {
        setResult(resultCode);
        finish();
    }
}
