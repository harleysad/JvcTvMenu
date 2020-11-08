package com.android.settingslib.inputmethod;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settingslib.R;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import java.text.Collator;

public class InputMethodPreference extends RestrictedSwitchPreference implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String EMPTY_TEXT = "";
    private static final int NO_WIDGET = 0;
    private static final String TAG = InputMethodPreference.class.getSimpleName();
    private AlertDialog mDialog;
    private final boolean mHasPriorityInSorting;
    private final InputMethodInfo mImi;
    private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private final boolean mIsAllowedByOrganization;
    private final OnSavePreferenceListener mOnSaveListener;

    public interface OnSavePreferenceListener {
        void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference);
    }

    public InputMethodPreference(Context context, InputMethodInfo imi, boolean isImeEnabler, boolean isAllowedByOrganization, OnSavePreferenceListener onSaveListener) {
        this(context, imi, imi.loadLabel(context.getPackageManager()), isAllowedByOrganization, onSaveListener);
        if (!isImeEnabler) {
            setWidgetLayoutResource(0);
        }
    }

    @VisibleForTesting
    InputMethodPreference(Context context, InputMethodInfo imi, CharSequence title, boolean isAllowedByOrganization, OnSavePreferenceListener onSaveListener) {
        super(context);
        this.mDialog = null;
        boolean z = false;
        setPersistent(false);
        this.mImi = imi;
        this.mIsAllowedByOrganization = isAllowedByOrganization;
        this.mOnSaveListener = onSaveListener;
        setSwitchTextOn((CharSequence) EMPTY_TEXT);
        setSwitchTextOff((CharSequence) EMPTY_TEXT);
        setKey(imi.getId());
        setTitle(title);
        String settingsActivity = imi.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            setIntent((Intent) null);
        } else {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName(imi.getPackageName(), settingsActivity);
            setIntent(intent);
        }
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(context);
        if (InputMethodUtils.isSystemIme(imi) && this.mInputMethodSettingValues.isValidSystemNonAuxAsciiCapableIme(imi, context)) {
            z = true;
        }
        this.mHasPriorityInSorting = z;
        setOnPreferenceClickListener(this);
        setOnPreferenceChangeListener(this);
    }

    public InputMethodInfo getInputMethodInfo() {
        return this.mImi;
    }

    private boolean isImeEnabler() {
        return getWidgetLayoutResource() != 0;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!isImeEnabler()) {
            return false;
        }
        if (isChecked()) {
            setCheckedInternal(false);
            return false;
        }
        if (!InputMethodUtils.isSystemIme(this.mImi)) {
            showSecurityWarnDialog();
        } else if (this.mImi.getServiceInfo().directBootAware || isTv()) {
            setCheckedInternal(true);
        } else if (!isTv()) {
            showDirectBootWarnDialog();
        }
        return false;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (isImeEnabler()) {
            return true;
        }
        Context context = getContext();
        try {
            Intent intent = getIntent();
            if (intent != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "IME's Settings Activity Not Found", e);
            Toast.makeText(context, context.getString(R.string.failed_to_open_app_settings_toast, new Object[]{this.mImi.loadLabel(context.getPackageManager())}), 1).show();
        }
        return true;
    }

    public void updatePreferenceViews() {
        if (this.mInputMethodSettingValues.isAlwaysCheckedIme(this.mImi, getContext()) && isImeEnabler()) {
            setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin) null);
            setEnabled(false);
        } else if (!this.mIsAllowedByOrganization) {
            setDisabledByAdmin(RestrictedLockUtils.checkIfInputMethodDisallowed(getContext(), this.mImi.getPackageName(), UserHandle.myUserId()));
        } else {
            setEnabled(true);
        }
        setChecked(this.mInputMethodSettingValues.isEnabledImi(this.mImi));
        if (!isDisabledByAdmin()) {
            setSummary((CharSequence) getSummaryString());
        }
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService("input_method");
    }

    private String getSummaryString() {
        return InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(getInputMethodManager().getEnabledInputMethodSubtypeList(this.mImi, true), getContext(), this.mImi);
    }

    /* access modifiers changed from: private */
    public void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
        this.mOnSaveListener.onSaveInputMethodPreference(this);
        notifyChanged();
    }

    private void showSecurityWarnDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(17039380);
        CharSequence label = this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager());
        builder.setMessage(context.getString(R.string.ime_security_warning, new Object[]{label}));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.lambda$showSecurityWarnDialog$0(InputMethodPreference.this, dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(false);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.show();
    }

    public static /* synthetic */ void lambda$showSecurityWarnDialog$0(InputMethodPreference inputMethodPreference, DialogInterface dialog, int which) {
        if (inputMethodPreference.mImi.getServiceInfo().directBootAware || inputMethodPreference.isTv()) {
            inputMethodPreference.setCheckedInternal(true);
        } else {
            inputMethodPreference.showDirectBootWarnDialog();
        }
    }

    private boolean isTv() {
        return (getContext().getResources().getConfiguration().uiMode & 15) == 4;
    }

    private void showDirectBootWarnDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage(context.getText(R.string.direct_boot_unaware_dialog_message));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(true);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(false);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.show();
    }

    public int compareTo(InputMethodPreference rhs, Collator collator) {
        int i = 0;
        if (this == rhs) {
            return 0;
        }
        if (this.mHasPriorityInSorting == rhs.mHasPriorityInSorting) {
            CharSequence title = getTitle();
            CharSequence rhsTitle = rhs.getTitle();
            boolean emptyTitle = TextUtils.isEmpty(title);
            boolean rhsEmptyTitle = TextUtils.isEmpty(rhsTitle);
            if (!emptyTitle && !rhsEmptyTitle) {
                return collator.compare(title.toString(), rhsTitle.toString());
            }
            int i2 = emptyTitle ? -1 : 0;
            if (rhsEmptyTitle) {
                i = -1;
            }
            return i2 - i;
        } else if (this.mHasPriorityInSorting) {
            return -1;
        } else {
            return 1;
        }
    }
}
