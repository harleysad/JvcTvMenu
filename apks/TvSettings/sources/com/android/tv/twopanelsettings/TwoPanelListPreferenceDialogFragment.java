package com.android.tv.twopanelsettings;

import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v17.preference.LeanbackListPreferenceDialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class TwoPanelListPreferenceDialogFragment extends LeanbackListPreferenceDialogFragment {
    private static final String SAVE_STATE_ENTRIES = "LeanbackListPreferenceDialogFragment.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "LeanbackListPreferenceDialogFragment.entryValues";
    private static final String SAVE_STATE_INITIAL_SELECTION = "LeanbackListPreferenceDialogFragment.initialSelection";
    private static final String SAVE_STATE_IS_MULTI = "LeanbackListPreferenceDialogFragment.isMulti";
    private CharSequence[] mEntriesCopy;
    private CharSequence[] mEntryValuesCopy;
    private String mInitialSelectionCopy;
    private boolean mMultiCopy;

    public static TwoPanelListPreferenceDialogFragment newInstanceSingle(String key) {
        Bundle args = new Bundle(1);
        args.putString("key", key);
        TwoPanelListPreferenceDialogFragment fragment = new TwoPanelListPreferenceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            DialogPreference preference = getPreference();
            if (preference instanceof ListPreference) {
                this.mMultiCopy = false;
                this.mEntriesCopy = ((ListPreference) preference).getEntries();
                this.mEntryValuesCopy = ((ListPreference) preference).getEntryValues();
                this.mInitialSelectionCopy = ((ListPreference) preference).getValue();
            } else if (preference instanceof MultiSelectListPreference) {
                this.mMultiCopy = true;
            } else {
                throw new IllegalArgumentException("Preference must be a ListPreference or MultiSelectListPreference");
            }
        } else {
            this.mMultiCopy = savedInstanceState.getBoolean(SAVE_STATE_IS_MULTI);
            this.mEntriesCopy = savedInstanceState.getCharSequenceArray(SAVE_STATE_ENTRIES);
            this.mEntryValuesCopy = savedInstanceState.getCharSequenceArray(SAVE_STATE_ENTRY_VALUES);
            if (!this.mMultiCopy) {
                this.mInitialSelectionCopy = savedInstanceState.getString(SAVE_STATE_INITIAL_SELECTION);
            }
        }
    }

    public RecyclerView.Adapter onCreateAdapter() {
        if (!this.mMultiCopy) {
            return new TwoPanelAdapterSingle(this.mEntriesCopy, this.mEntryValuesCopy, this.mInitialSelectionCopy);
        }
        return super.onCreateAdapter();
    }

    private class TwoPanelAdapterSingle extends RecyclerView.Adapter<LeanbackListPreferenceDialogFragment.ViewHolder> implements LeanbackListPreferenceDialogFragment.ViewHolder.OnItemClickListener {
        private final CharSequence[] mEntries;
        private final CharSequence[] mEntryValues;
        private CharSequence mSelectedValue;

        TwoPanelAdapterSingle(CharSequence[] entries, CharSequence[] entryValues, CharSequence selectedValue) {
            this.mEntries = entries;
            this.mEntryValues = entryValues;
            this.mSelectedValue = selectedValue;
        }

        public LeanbackListPreferenceDialogFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LeanbackListPreferenceDialogFragment.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.leanback_list_preference_item_single, parent, false), this);
        }

        public void onBindViewHolder(LeanbackListPreferenceDialogFragment.ViewHolder holder, int position) {
            holder.getWidgetView().setChecked(this.mEntryValues[position].equals(this.mSelectedValue));
            holder.getTitleView().setText(this.mEntries[position]);
        }

        public int getItemCount() {
            return this.mEntries.length;
        }

        public void onItemClick(LeanbackListPreferenceDialogFragment.ViewHolder viewHolder) {
            int index = viewHolder.getAdapterPosition();
            if (index != -1) {
                CharSequence entry = this.mEntryValues[index];
                ListPreference preference = (ListPreference) TwoPanelListPreferenceDialogFragment.this.getPreference();
                if (index >= 0) {
                    String value = this.mEntryValues[index].toString();
                    if (preference.callChangeListener(value)) {
                        preference.setValue(value);
                        this.mSelectedValue = entry;
                    }
                }
                if (TwoPanelListPreferenceDialogFragment.this.getParentFragment() instanceof TwoPanelSettingsFragment) {
                    ((TwoPanelSettingsFragment) TwoPanelListPreferenceDialogFragment.this.getParentFragment()).navigateBack();
                }
                notifyDataSetChanged();
            }
        }
    }
}
