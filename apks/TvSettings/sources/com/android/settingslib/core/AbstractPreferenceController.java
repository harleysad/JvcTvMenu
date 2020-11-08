package com.android.settingslib.core;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;

public abstract class AbstractPreferenceController {
    protected final Context mContext;

    public abstract String getPreferenceKey();

    public abstract boolean isAvailable();

    public AbstractPreferenceController(Context context) {
        this.mContext = context;
    }

    public void displayPreference(PreferenceScreen screen) {
        String prefKey = getPreferenceKey();
        if (isAvailable()) {
            setVisible(screen, prefKey, true);
            if (this instanceof Preference.OnPreferenceChangeListener) {
                screen.findPreference(prefKey).setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) this);
                return;
            }
            return;
        }
        setVisible(screen, prefKey, false);
    }

    public void updateState(Preference preference) {
        refreshSummary(preference);
    }

    /* access modifiers changed from: protected */
    public void refreshSummary(Preference preference) {
        CharSequence summary;
        if (preference != null && (summary = getSummary()) != null) {
            preference.setSummary(summary);
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    /* access modifiers changed from: protected */
    public final void setVisible(PreferenceGroup group, String key, boolean isVisible) {
        Preference pref = group.findPreference(key);
        if (pref != null) {
            pref.setVisible(isVisible);
        }
    }

    public CharSequence getSummary() {
        return null;
    }
}
