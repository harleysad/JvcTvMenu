package com.android.tv.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.widget.AnimatedImageView;
import com.android.tv.settings.R;

public class SyncStateSwitchPreference extends SwitchPreference {
    private Account mAccount;
    private String mAuthority;
    private boolean mFailed;
    private boolean mIsActive;
    private boolean mIsPending;
    private boolean mOneTimeSyncMode;

    public SyncStateSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs, 0, R.style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        this.mAccount = null;
        this.mAuthority = null;
    }

    public SyncStateSwitchPreference(Context context, Account account, String authority) {
        super(context, (AttributeSet) null, 0, R.style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        this.mAccount = account;
        this.mAuthority = authority;
    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        AnimatedImageView syncActiveView = (AnimatedImageView) view.findViewById(R.id.sync_active);
        View syncFailedView = view.findViewById(R.id.sync_failed);
        boolean activeVisible = this.mIsActive || this.mIsPending;
        syncActiveView.setVisibility(activeVisible ? 0 : 8);
        syncActiveView.setAnimating(this.mIsActive);
        syncFailedView.setVisibility(this.mFailed && !activeVisible ? 0 : 8);
        View switchView = view.findViewById(R.id.switch_widget_container);
        if (this.mOneTimeSyncMode) {
            switchView.setVisibility(8);
            ((TextView) view.findViewById(16908304)).setText(getContext().getString(R.string.sync_one_time_sync, new Object[]{getSummary()}));
            return;
        }
        switchView.setVisibility(0);
    }

    public void setActive(boolean isActive) {
        this.mIsActive = isActive;
        notifyChanged();
    }

    public void setPending(boolean isPending) {
        this.mIsPending = isPending;
        notifyChanged();
    }

    public void setFailed(boolean failed) {
        this.mFailed = failed;
        notifyChanged();
    }

    public void setOneTimeSyncMode(boolean oneTimeSyncMode) {
        this.mOneTimeSyncMode = oneTimeSyncMode;
        notifyChanged();
    }

    public boolean isOneTimeSyncMode() {
        return this.mOneTimeSyncMode;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        if (this.mOneTimeSyncMode) {
            return;
        }
        if (ActivityManager.isUserAMonkey()) {
            Log.d("SyncState", "ignoring monkey's attempt to flip sync state");
        } else {
            super.onClick();
        }
    }

    public Account getAccount() {
        return this.mAccount;
    }

    public String getAuthority() {
        return this.mAuthority;
    }
}
