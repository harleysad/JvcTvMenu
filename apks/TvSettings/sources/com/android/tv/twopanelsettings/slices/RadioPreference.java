package com.android.tv.twopanelsettings.slices;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.tv.twopanelsettings.R;

public class RadioPreference extends CheckBoxPreference {
    private String mRadioGroup;

    public RadioPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public RadioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioPreference, 0, 0);
        this.mRadioGroup = a.getString(R.styleable.RadioPreference_radioGroup);
        a.recycle();
        setWidgetLayoutResource(R.layout.radio_preference_widget);
    }

    public String getRadioGroup() {
        return this.mRadioGroup;
    }

    public void setRadioGroup(String radioGroup) {
        this.mRadioGroup = radioGroup;
    }

    public void clearOtherRadioPreferences(PreferenceGroup preferenceGroup) {
        int count = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceGroup.getPreference(i);
            if (p instanceof RadioPreference) {
                RadioPreference radioPreference = (RadioPreference) p;
                if (TextUtils.equals(getRadioGroup(), radioPreference.getRadioGroup()) && !TextUtils.equals(getKey(), radioPreference.getKey())) {
                    radioPreference.setChecked(false);
                }
            }
        }
    }
}
