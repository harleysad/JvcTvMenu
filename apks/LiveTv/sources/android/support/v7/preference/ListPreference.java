package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ListPreference extends DialogPreference {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mSummary;
    private String mValue;
    private boolean mValueSet;

    public ListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListPreference, defStyleAttr, defStyleRes);
        this.mEntries = TypedArrayUtils.getTextArray(a, R.styleable.ListPreference_entries, R.styleable.ListPreference_android_entries);
        this.mEntryValues = TypedArrayUtils.getTextArray(a, R.styleable.ListPreference_entryValues, R.styleable.ListPreference_android_entryValues);
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, defStyleRes);
        this.mSummary = TypedArrayUtils.getString(a2, R.styleable.Preference_summary, R.styleable.Preference_android_summary);
        a2.recycle();
    }

    public ListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle, 16842897));
    }

    public ListPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setEntries(CharSequence[] entries) {
        this.mEntries = entries;
    }

    public void setEntries(@ArrayRes int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public void setEntryValues(CharSequence[] entryValues) {
        this.mEntryValues = entryValues;
    }

    public void setEntryValues(@ArrayRes int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        if (summary == null && this.mSummary != null) {
            this.mSummary = null;
        } else if (summary != null && !summary.equals(this.mSummary)) {
            this.mSummary = summary.toString();
        }
    }

    public CharSequence getSummary() {
        CharSequence entry = getEntry();
        if (this.mSummary == null) {
            return super.getSummary();
        }
        String str = this.mSummary;
        Object[] objArr = new Object[1];
        objArr[0] = entry == null ? "" : entry;
        return String.format(str, objArr);
    }

    public void setValue(String value) {
        boolean changed = !TextUtils.equals(this.mValue, value);
        if (changed || !this.mValueSet) {
            this.mValue = value;
            this.mValueSet = true;
            persistString(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    public String getValue() {
        return this.mValue;
    }

    public CharSequence getEntry() {
        int index = getValueIndex();
        if (index < 0 || this.mEntries == null) {
            return null;
        }
        return this.mEntries[index];
    }

    public int findIndexOfValue(String value) {
        if (value == null || this.mEntryValues == null) {
            return -1;
        }
        for (int i = this.mEntryValues.length - 1; i >= 0; i--) {
            if (this.mEntryValues[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public void setValueIndex(int index) {
        if (this.mEntryValues != null) {
            setValue(this.mEntryValues[index].toString());
        }
    }

    private int getValueIndex() {
        return findIndexOfValue(this.mValue);
    }

    /* access modifiers changed from: protected */
    public Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /* access modifiers changed from: protected */
    public void onSetInitialValue(Object defaultValue) {
        setValue(getPersistedString((String) defaultValue));
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.mValue = getValue();
        return myState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.mValue);
    }

    private static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        String mValue;

        SavedState(Parcel source) {
            super(source);
            this.mValue = source.readString();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.mValue);
        }
    }
}
