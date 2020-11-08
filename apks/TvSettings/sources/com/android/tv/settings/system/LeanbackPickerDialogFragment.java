package com.android.tv.settings.system;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v17.leanback.widget.picker.DatePicker;
import android.support.v17.leanback.widget.picker.TimePicker;
import android.support.v17.preference.LeanbackPreferenceDialogFragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.Calendar;

public class LeanbackPickerDialogFragment extends LeanbackPreferenceDialogFragment {
    private static final String EXTRA_PICKER_TYPE = "LeanbackPickerDialogFragment.PickerType";
    private static final String SAVE_STATE_TITLE = "LeanbackPickerDialogFragment.title";
    private static final String SET_TIMER_POWER_OFF_TIMER = "set_timer_power_off_timer";
    private static final String SET_TIMER_POWER_OFF_TIMER_HOUR = "set_timer_power_off_timer_hour";
    private static final String SET_TIMER_POWER_OFF_TIMER_MIN = "set_timer_power_off_timer_min";
    private static final String SET_TIMER_POWER_ON_TIMER = "set_timer_power_on_timer";
    private static final String SET_TIMER_POWER_ON_TIMER_HOUR = "set_timer_power_on_timer_hour";
    private static final String SET_TIMER_POWER_ON_TIMER_MIN = "set_timer_power_on_timer_min";
    private static final String TYPE_DATE = "date";
    private static final String TYPE_TIME = "time";
    private Calendar mCalendar;
    private CharSequence mDialogTitle;
    private ContentResolver mResolver;
    private SaveValue mSaveValue;

    public static LeanbackPickerDialogFragment newDatePickerInstance(String key) {
        Bundle args = new Bundle(1);
        args.putString("key", key);
        args.putString(EXTRA_PICKER_TYPE, TYPE_DATE);
        LeanbackPickerDialogFragment fragment = new LeanbackPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static LeanbackPickerDialogFragment newTimePickerInstance(String key) {
        Bundle args = new Bundle(1);
        args.putString("key", key);
        args.putString(EXTRA_PICKER_TYPE, TYPE_TIME);
        LeanbackPickerDialogFragment fragment = new LeanbackPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            this.mDialogTitle = getPreference().getDialogTitle();
        } else {
            this.mDialogTitle = savedInstanceState.getCharSequence(SAVE_STATE_TITLE);
        }
        this.mCalendar = Calendar.getInstance();
        this.mResolver = getContext().getContentResolver();
        this.mSaveValue = SaveValue.getInstance(getContext());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SAVE_STATE_TITLE, this.mDialogTitle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pickerType = getArguments().getString(EXTRA_PICKER_TYPE);
        TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
        int theme = tv.resourceId;
        if (theme == 0) {
            theme = R.style.PreferenceThemeOverlayLeanback;
        }
        LayoutInflater styledInflater = inflater.cloneInContext(new ContextThemeWrapper(getActivity(), theme));
        View view = styledInflater.inflate(R.layout.picker_dialog_fragment, container, false);
        ViewGroup pickerContainer = (ViewGroup) view.findViewById(R.id.picker_container);
        if (pickerType.equals(TYPE_DATE)) {
            styledInflater.inflate(R.layout.date_picker_widget, pickerContainer, true);
            DatePicker datePicker = (DatePicker) pickerContainer.findViewById(R.id.date_picker);
            datePicker.setActivated(true);
            datePicker.setOnClickListener(new View.OnClickListener(datePicker) {
                private final /* synthetic */ DatePicker f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    LeanbackPickerDialogFragment.lambda$onCreateView$0(LeanbackPickerDialogFragment.this, this.f$1, view);
                }
            });
        } else {
            styledInflater.inflate(R.layout.time_picker_widget, pickerContainer, true);
            TimePicker timePicker = (TimePicker) pickerContainer.findViewById(R.id.time_picker);
            timePicker.setActivated(true);
            String pickerKey = getArguments().getString("key");
            if (pickerKey.equals(SET_TIMER_POWER_ON_TIMER)) {
                int h = this.mSaveValue.readValue(SET_TIMER_POWER_ON_TIMER_HOUR, -1);
                int m = this.mSaveValue.readValue(SET_TIMER_POWER_ON_TIMER_MIN, -1);
                if (h != -1) {
                    timePicker.setHour(h);
                    timePicker.setMinute(m);
                }
            } else if (pickerKey.equals(SET_TIMER_POWER_OFF_TIMER)) {
                int h2 = this.mSaveValue.readValue(SET_TIMER_POWER_OFF_TIMER_HOUR, -1);
                int m2 = this.mSaveValue.readValue(SET_TIMER_POWER_OFF_TIMER_MIN, -1);
                if (h2 != -1) {
                    timePicker.setHour(h2);
                    timePicker.setMinute(m2);
                }
            }
            timePicker.setOnClickListener(new View.OnClickListener(timePicker) {
                private final /* synthetic */ TimePicker f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    LeanbackPickerDialogFragment.lambda$onCreateView$1(LeanbackPickerDialogFragment.this, this.f$1, view);
                }
            });
        }
        CharSequence title = this.mDialogTitle;
        if (!TextUtils.isEmpty(title)) {
            ((TextView) view.findViewById(R.id.decor_title)).setText(title);
        }
        return view;
    }

    public static /* synthetic */ void lambda$onCreateView$0(LeanbackPickerDialogFragment leanbackPickerDialogFragment, DatePicker datePicker, View v) {
        ((AlarmManager) leanbackPickerDialogFragment.getContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).setTime(datePicker.getDate());
        if (!leanbackPickerDialogFragment.getFragmentManager().popBackStackImmediate()) {
            leanbackPickerDialogFragment.getActivity().finish();
        }
    }

    public static /* synthetic */ void lambda$onCreateView$1(LeanbackPickerDialogFragment leanbackPickerDialogFragment, TimePicker timePicker, View v) {
        leanbackPickerDialogFragment.mCalendar.set(11, timePicker.getHour());
        leanbackPickerDialogFragment.mCalendar.set(12, timePicker.getMinute());
        leanbackPickerDialogFragment.mCalendar.set(13, 0);
        leanbackPickerDialogFragment.mCalendar.set(14, 0);
        if (!leanbackPickerDialogFragment.Onclick(timePicker)) {
            ((AlarmManager) leanbackPickerDialogFragment.getContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).setTime(leanbackPickerDialogFragment.mCalendar.getTimeInMillis());
        }
        if (!leanbackPickerDialogFragment.getFragmentManager().popBackStackImmediate()) {
            leanbackPickerDialogFragment.getActivity().finish();
        }
    }

    private boolean Onclick(TimePicker timePicker) {
        String pickerKey = getArguments().getString("key");
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        StringBuilder sb = new StringBuilder();
        if (h < 0 || h >= 10) {
            sb.append(h);
        } else {
            sb.append("0" + h);
        }
        sb.append(":");
        if (h < 0 || m >= 10) {
            sb.append(m);
        } else {
            sb.append("0" + m);
        }
        String mDate = new String(sb);
        if (pickerKey == null) {
            return false;
        }
        Log.d("updateSettingDB", "key  " + pickerKey + "mDate  " + sb);
        if (pickerKey.equals(SET_TIMER_POWER_ON_TIMER)) {
            this.mSaveValue.saveValue(SET_TIMER_POWER_ON_TIMER_HOUR, h);
            this.mSaveValue.saveValue(SET_TIMER_POWER_ON_TIMER_MIN, m);
        } else if (pickerKey.equals(SET_TIMER_POWER_OFF_TIMER)) {
            this.mSaveValue.saveValue(SET_TIMER_POWER_OFF_TIMER_HOUR, h);
            this.mSaveValue.saveValue(SET_TIMER_POWER_OFF_TIMER_MIN, m);
        }
        if (!updateSettingDB(pickerKey, mDate) || !updateConfig(pickerKey, mDate)) {
            return false;
        }
        return true;
    }

    private boolean updateSettingDB(String pickerKey, String mDate) {
        Log.d("updateSettingDB", " key  " + pickerKey + "   mDate  " + mDate);
        if (pickerKey.equals(SET_TIMER_POWER_ON_TIMER)) {
            PreferenceUtils.putSettingValueString(this.mResolver, "tv_timer_power_on_timer_values", mDate);
            return true;
        } else if (!pickerKey.equals(SET_TIMER_POWER_OFF_TIMER)) {
            return false;
        } else {
            PreferenceUtils.putSettingValueString(this.mResolver, "tv_timer_power_off_timer_values", mDate);
            return true;
        }
    }

    private boolean updateConfig(String pickerKey, String mDate) {
        Log.d("updateSettingDB", " key  " + pickerKey + "   mDate  " + mDate);
        TVSettingConfig config = TVSettingConfig.getInstance(getContext());
        if (pickerKey.equals(SET_TIMER_POWER_ON_TIMER)) {
            if (PreferenceUtils.getSettingIntValue(this.mResolver, "tv_timer_power_on_time_type_entry_values", 0) == 2) {
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 1, mDate);
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, mDate);
            } else {
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, mDate);
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, mDate);
            }
            return true;
        } else if (!pickerKey.equals(SET_TIMER_POWER_OFF_TIMER)) {
            return false;
        } else {
            if (PreferenceUtils.getSettingIntValue(this.mResolver, "tv_timer_power_off_time_type_entry_values", 0) == 2) {
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 1, mDate);
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, mDate);
            } else {
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, mDate);
                config.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, mDate);
            }
            return true;
        }
    }
}
