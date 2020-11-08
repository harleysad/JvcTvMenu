package com.android.tv.settings.inputmethod;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.PreferenceScreen;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Keep
public final class AvailableVirtualKeyboardFragment extends SettingsPreferenceFragment implements InputMethodPreference.OnSavePreferenceListener {
    private DevicePolicyManager mDpm;
    private InputMethodManager mImm;
    private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();
    private InputMethodSettingValuesWrapper mInputMethodSettingValues;

    public void onCreatePreferences(Bundle bundle, String s) {
        Activity activity = getActivity();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(activity);
        screen.setTitle((CharSequence) activity.getString(R.string.available_virtual_keyboard_category));
        setPreferenceScreen(screen);
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(activity);
        this.mImm = (InputMethodManager) activity.getSystemService(InputMethodManager.class);
        this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
    }

    public void onResume() {
        super.onResume();
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        updateInputMethodPreferenceViews();
    }

    public void onSaveInputMethodPreference(InputMethodPreference pref) {
        InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContext().getContentResolver(), this.mImm.getInputMethodList(), getResources().getConfiguration().keyboard == 2);
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        Iterator<InputMethodPreference> it = this.mInputMethodPreferenceList.iterator();
        while (it.hasNext()) {
            it.next().updatePreferenceViews();
        }
    }

    private static Drawable loadDrawable(PackageManager packageManager, String packageName, int resId, ApplicationInfo applicationInfo) {
        if (resId == 0) {
            return null;
        }
        try {
            return packageManager.getDrawable(packageName, resId, applicationInfo);
        } catch (Exception e) {
            return null;
        }
    }

    private static Drawable getInputMethodIcon(PackageManager packageManager, InputMethodInfo imi) {
        ServiceInfo si = imi.getServiceInfo();
        ApplicationInfo ai = si != null ? si.applicationInfo : null;
        String packageName = imi.getPackageName();
        if (si == null || ai == null || packageName == null) {
            return new ColorDrawable(0);
        }
        Drawable drawable = loadDrawable(packageManager, packageName, si.logo, ai);
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = loadDrawable(packageManager, packageName, si.icon, ai);
        if (drawable2 != null) {
            return drawable2;
        }
        Drawable drawable3 = loadDrawable(packageManager, packageName, ai.logo, ai);
        if (drawable3 != null) {
            return drawable3;
        }
        Drawable drawable4 = loadDrawable(packageManager, packageName, ai.icon, ai);
        if (drawable4 != null) {
            return drawable4;
        }
        return new ColorDrawable(0);
    }

    private void updateInputMethodPreferenceViews() {
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        this.mInputMethodPreferenceList.clear();
        List<String> permittedList = this.mDpm.getPermittedInputMethodsForCurrentUser();
        Context context = getPreferenceManager().getContext();
        PackageManager packageManager = getActivity().getPackageManager();
        List<InputMethodInfo> imis = this.mInputMethodSettingValues.getInputMethodList();
        int i = 0;
        int numImis = imis == null ? 0 : imis.size();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 >= numImis) {
                break;
            }
            InputMethodInfo imi = imis.get(i3);
            InputMethodPreference pref = new InputMethodPreference(context, imi, true, permittedList == null || permittedList.contains(imi.getPackageName()), (InputMethodPreference.OnSavePreferenceListener) this);
            pref.setIcon(getInputMethodIcon(packageManager, imi));
            this.mInputMethodPreferenceList.add(pref);
            i2 = i3 + 1;
        }
        this.mInputMethodPreferenceList.sort(new Comparator(Collator.getInstance()) {
            private final /* synthetic */ Collator f$0;

            {
                this.f$0 = r1;
            }

            public final int compare(Object obj, Object obj2) {
                return ((InputMethodPreference) obj).compareTo((InputMethodPreference) obj2, this.f$0);
            }
        });
        getPreferenceScreen().removeAll();
        while (true) {
            int i4 = i;
            if (i4 < numImis) {
                InputMethodPreference pref2 = this.mInputMethodPreferenceList.get(i4);
                pref2.setOrder(i4);
                getPreferenceScreen().addPreference(pref2);
                InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(pref2);
                pref2.updatePreferenceViews();
                i = i4 + 1;
            } else {
                return;
            }
        }
    }

    public int getMetricsCategory() {
        return 347;
    }
}
