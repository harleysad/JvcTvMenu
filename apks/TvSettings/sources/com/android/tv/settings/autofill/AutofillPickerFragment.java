package com.android.tv.settings.autofill;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.Keep;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.Html;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.wrapper.PackageManagerWrapper;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;

@Keep
public class AutofillPickerFragment extends SettingsPreferenceFragment {
    private static final String AUTOFILL_SERVICE_RADIO_GROUP = "autofill_service_group";
    private static final int FINISH_ACTIVITY_DELAY = 300;
    @VisibleForTesting
    static final String KEY_FOR_NONE = "_none_";
    private DialogInterface.OnClickListener mCancelListener;
    private final Handler mHandler = new Handler();
    private PackageManagerWrapper mPm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null && activity.getIntent().getStringExtra(AutofillPickerActivity.EXTRA_PACKAGE_NAME) != null) {
            this.mCancelListener = new DialogInterface.OnClickListener(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AutofillPickerFragment.lambda$onCreate$0(this.f$0, dialogInterface, i);
                }
            };
        }
    }

    static /* synthetic */ void lambda$onCreate$0(Activity activity, DialogInterface d, int w) {
        activity.setResult(0);
        activity.finish();
    }

    public static AutofillPickerFragment newInstance() {
        return new AutofillPickerFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPm = new PackageManagerWrapper(context.getPackageManager());
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.autofill_picker, (String) null);
        PreferenceScreen screen = getPreferenceScreen();
        bind(screen, savedInstanceState == null);
        setPreferenceScreen(screen);
    }

    /* JADX WARNING: type inference failed for: r4v2, types: [android.support.v7.preference.Preference] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    @android.support.annotation.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bind(android.support.v7.preference.PreferenceScreen r10, boolean r11) {
        /*
            r9 = this;
            android.content.Context r0 = r9.getContext()
            com.android.settingslib.wrapper.PackageManagerWrapper r1 = r9.mPm
            int r2 = android.os.UserHandle.myUserId()
            java.util.List r1 = com.android.tv.settings.autofill.AutofillHelper.getAutofillCandidates(r0, r1, r2)
            com.android.settingslib.applications.DefaultAppInfo r2 = com.android.tv.settings.autofill.AutofillHelper.getCurrentAutofill(r0, r1)
            r3 = 0
            java.util.Iterator r4 = r1.iterator()
        L_0x0017:
            boolean r5 = r4.hasNext()
            r6 = 1
            if (r5 == 0) goto L_0x0050
            java.lang.Object r5 = r4.next()
            com.android.settingslib.applications.DefaultAppInfo r5 = (com.android.settingslib.applications.DefaultAppInfo) r5
            com.android.tv.settings.RadioPreference r7 = new com.android.tv.settings.RadioPreference
            r7.<init>(r0)
            java.lang.String r8 = r5.getKey()
            r7.setKey(r8)
            r8 = 0
            r7.setPersistent(r8)
            java.lang.CharSequence r8 = r5.loadLabel()
            r7.setTitle((java.lang.CharSequence) r8)
            java.lang.String r8 = "autofill_service_group"
            r7.setRadioGroup(r8)
            r8 = 2131493038(0x7f0c00ae, float:1.8609545E38)
            r7.setLayoutResource(r8)
            if (r2 != r5) goto L_0x004c
            r7.setChecked(r6)
            r3 = r7
        L_0x004c:
            r10.addPreference(r7)
            goto L_0x0017
        L_0x0050:
            if (r3 != 0) goto L_0x005e
            java.lang.String r4 = "_none_"
            android.support.v7.preference.Preference r4 = r10.findPreference(r4)
            r3 = r4
            com.android.tv.settings.RadioPreference r3 = (com.android.tv.settings.RadioPreference) r3
            r3.setChecked(r6)
        L_0x005e:
            if (r3 == 0) goto L_0x0065
            if (r11 == 0) goto L_0x0065
            r9.scrollToPreference((android.support.v7.preference.Preference) r3)
        L_0x0065:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.autofill.AutofillPickerFragment.bind(android.support.v7.preference.PreferenceScreen, boolean):void");
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RadioPreference) {
            Context context = getContext();
            DefaultAppInfo current = AutofillHelper.getCurrentAutofill(context, AutofillHelper.getAutofillCandidates(context, this.mPm, UserHandle.myUserId()));
            String currentKey = current != null ? current.getKey() : KEY_FOR_NONE;
            RadioPreference newPref = (RadioPreference) preference;
            String newKey = newPref.getKey();
            if (currentKey.equals(newKey) || KEY_FOR_NONE.equals(newKey)) {
                newPref.setChecked(true);
                newPref.clearOtherRadioPreferences(getPreferenceScreen());
                setAutofillService(newKey);
            } else {
                RadioPreference currentPref = (RadioPreference) findPreference(currentKey);
                currentPref.setChecked(true);
                currentPref.clearOtherRadioPreferences(getPreferenceScreen());
                displayAlert(Html.fromHtml(getContext().getString(R.string.autofill_confirmation_message, new Object[]{newPref.getTitle()})), new DialogInterface.OnClickListener(newKey) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AutofillPickerFragment.lambda$onPreferenceTreeClick$1(AutofillPickerFragment.this, this.f$1, dialogInterface, i);
                    }
                });
            }
        }
        return true;
    }

    public static /* synthetic */ void lambda$onPreferenceTreeClick$1(AutofillPickerFragment autofillPickerFragment, String newKey, DialogInterface dialog, int which) {
        RadioPreference pref = (RadioPreference) autofillPickerFragment.findPreference(newKey);
        if (pref != null) {
            pref.setChecked(true);
            pref.clearOtherRadioPreferences(autofillPickerFragment.getPreferenceScreen());
            autofillPickerFragment.setAutofillService(newKey);
        }
    }

    private void setAutofillService(String key) {
        AutofillHelper.setCurrentAutofill(getContext(), key);
        Activity activity = getActivity();
        if (activity != null) {
            String packageName = activity.getIntent().getStringExtra(AutofillPickerActivity.EXTRA_PACKAGE_NAME);
            int result = 0;
            if (packageName != null) {
                ComponentName componentName = ComponentName.unflattenFromString(key);
                if (componentName != null && componentName.getPackageName().equals(packageName)) {
                    result = -1;
                }
                finishActivity(true, result);
            } else if (!getFragmentManager().popBackStackImmediate()) {
                finishActivity(false, 0);
            }
        }
    }

    private void finishActivity(boolean sendResult, int result) {
        this.mHandler.postDelayed(new Runnable(sendResult, result) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AutofillPickerFragment.lambda$finishActivity$2(AutofillPickerFragment.this, this.f$1, this.f$2);
            }
        }, 300);
    }

    public static /* synthetic */ void lambda$finishActivity$2(AutofillPickerFragment autofillPickerFragment, boolean sendResult, int result) {
        if (sendResult) {
            autofillPickerFragment.getActivity().setResult(result);
        }
        autofillPickerFragment.getActivity().finish();
    }

    private void displayAlert(CharSequence message, DialogInterface.OnClickListener positiveOnClickListener) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage(message).setCancelable(true).setPositiveButton(17039370, positiveOnClickListener).setNegativeButton(17039360, this.mCancelListener).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(dialog) {
            private final /* synthetic */ AlertDialog f$0;

            {
                this.f$0 = r1;
            }

            public final void onShow(DialogInterface dialogInterface) {
                this.f$0.getButton(-2).requestFocus();
            }
        });
        dialog.show();
    }

    public int getMetricsCategory() {
        return 792;
    }
}
