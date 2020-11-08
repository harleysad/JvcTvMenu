package com.android.tv.twopanelsettings.slices;

import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.widget.ListContent;
import com.android.tv.twopanelsettings.R;
import com.android.tv.twopanelsettings.TwoPanelSettingsFragment;
import com.android.tv.twopanelsettings.slices.PreferenceSliceLiveData;
import com.android.tv.twopanelsettings.slices.SlicePreferencesUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Keep
public class SliceFragment extends SettingsPreferenceFragment implements Observer<Slice>, TwoPanelSettingsFragment.SliceFragmentCallback {
    private static final String KEY_LAST_PREFERENCE = "key_last_preference";
    private static final String KEY_PREFERENCE_FOLLOWUP_INTENT = "key_preference_followup_intent";
    private static final String KEY_PREFERENCE_FOLLOWUP_RESULT_CODE = "key_preference_followup_result_code";
    private static final String KEY_SCREEN_ICON = "key_screen_icon";
    private static final String KEY_SCREEN_SUBTITLE = "key_screen_subtitle";
    private static final String KEY_SCREEN_TITLE = "key_screen_title";
    private static final String KEY_URI_STRING = "key_uri_string";
    private static final int SLICE_REQUEST_CODE = 10000;
    private static final String TAG = "SliceFragment";
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, Uri uri) {
            SliceFragment.this.handleUri(uri);
            super.onChange(selfChange, uri);
        }
    };
    private ContextThemeWrapper mContextThemeWrapper;
    private Intent mFollowupPendingIntentExtras;
    private int mFollowupPendingIntentResultCode;
    private String mLastFocusedPreferenceKey;
    private ListContent mListContent;
    private PendingIntent mPreferenceFollowupIntent;
    private Icon mScreenIcon;
    private CharSequence mScreenSubtitle;
    private CharSequence mScreenTitle;
    private Slice mSlice;
    private String mUriString = null;

    public void onCreate(Bundle savedInstanceState) {
        this.mUriString = getArguments().getString("TAG_TARGET_URI");
        ContextSingleton.getInstance().grantFullAccess(getContext(), Uri.parse(this.mUriString));
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        setTitle(this.mScreenTitle);
        setSubtitle(this.mScreenSubtitle);
        setIcon(this.mScreenIcon);
        getPreferenceScreen().removeAll();
        getSliceLiveData().observeForever(this);
        if (TextUtils.isEmpty(this.mScreenTitle)) {
            this.mScreenTitle = getArguments().getCharSequence(SlicesConstants.TAG_SCREEN_TITLE, "");
        }
        showProgressBar();
        super.onResume();
        getContext().getContentResolver().registerContentObserver(SlicePreferencesUtil.getStatusPath(this.mUriString), false, this.mContentObserver);
        fireFollowupPendingIntent();
    }

    private PreferenceSliceLiveData.SliceLiveDataImpl getSliceLiveData() {
        return ContextSingleton.getInstance().getSliceLiveData(getActivity(), Uri.parse(this.mUriString));
    }

    private void fireFollowupPendingIntent() {
        if (this.mPreferenceFollowupIntent != null) {
            try {
                this.mPreferenceFollowupIntent.send(getContext(), this.mFollowupPendingIntentResultCode, this.mFollowupPendingIntentExtras);
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "Followup PendingIntent for slice cannot be sent", e);
            }
            this.mPreferenceFollowupIntent = null;
        }
    }

    public void onPause() {
        super.onPause();
        hideProgressBar();
        getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
        getSliceLiveData().removeObserver(this);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
        TypedValue themeTypedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        this.mContextThemeWrapper = new ContextThemeWrapper(getActivity(), themeTypedValue.resourceId);
    }

    public int getMetricsCategory() {
        return 0;
    }

    private void update() {
        List<SliceItem> items;
        this.mListContent = new ListContent(getContext(), this.mSlice, (AttributeSet) null, 0, 0);
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        if (preferenceScreen != null && (items = this.mListContent.getRowItems()) != null && items.size() != 0) {
            SliceItem screenTitleItem = SlicePreferencesUtil.getScreenTitleItem(items);
            if (screenTitleItem == null) {
                setTitle(this.mScreenTitle);
            } else {
                SlicePreferencesUtil.Data data = SlicePreferencesUtil.extract(screenTitleItem);
                CharSequence title = SlicePreferencesUtil.getText(data.mTitleItem);
                if (!TextUtils.isEmpty(title)) {
                    setTitle(title);
                    this.mScreenTitle = title;
                } else {
                    setTitle(this.mScreenTitle);
                }
                setSubtitle(SlicePreferencesUtil.getText(data.mSubtitleItem));
                setIcon(SlicePreferencesUtil.getIcon(data.mStartItem));
            }
            List<Preference> newPrefs = new ArrayList<>();
            for (SliceItem item : items) {
                Preference preference = SlicePreferencesUtil.getPreference(item, this.mContextThemeWrapper, getClass().getCanonicalName());
                if (preference != null) {
                    newPrefs.add(preference);
                }
            }
            updatePreferenceScreen(preferenceScreen, newPrefs);
            if (this.mLastFocusedPreferenceKey != null) {
                scrollToPreference(this.mLastFocusedPreferenceKey);
            }
        }
    }

    private void back() {
        if (getCallbackFragment() instanceof TwoPanelSettingsFragment) {
            TwoPanelSettingsFragment parentFragment = (TwoPanelSettingsFragment) getCallbackFragment();
            if (parentFragment.isFragmentInTheMainPanel(this)) {
                parentFragment.navigateBack();
            }
        }
    }

    private void forward() {
        if (getCallbackFragment() instanceof TwoPanelSettingsFragment) {
            TwoPanelSettingsFragment parentFragment = (TwoPanelSettingsFragment) getCallbackFragment();
            if (parentFragment.isFragmentInTheMainPanel(this)) {
                parentFragment.navigateToPreviewFragment();
            }
        }
    }

    private void updatePreferenceScreen(PreferenceScreen screen, List<Preference> newPrefs) {
        int index = 0;
        while (index < screen.getPreferenceCount()) {
            boolean needToRemoveCurrentPref = true;
            Preference oldPref = screen.getPreference(index);
            if (oldPref.getKey() != null) {
                Iterator<Preference> it = newPrefs.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Preference newPref = it.next();
                    if (newPref.getKey() != null && newPref.getKey().equals(oldPref.getKey())) {
                        needToRemoveCurrentPref = false;
                        break;
                    }
                }
            }
            if (needToRemoveCurrentPref) {
                screen.removePreference(oldPref);
            } else {
                index++;
            }
        }
        for (int i = 0; i < newPrefs.size(); i++) {
            Preference newPref2 = newPrefs.get(i);
            boolean neededToAddNewPref = true;
            if (newPref2.getKey() != null) {
                int j = 0;
                while (true) {
                    if (j >= screen.getPreferenceCount()) {
                        break;
                    }
                    Preference oldPref2 = screen.getPreference(j);
                    if (oldPref2.getKey() == null || !oldPref2.getKey().equals(newPref2.getKey())) {
                        j++;
                    } else {
                        oldPref2.setIcon(newPref2.getIcon());
                        oldPref2.setTitle(newPref2.getTitle());
                        oldPref2.setSummary(newPref2.getSummary());
                        oldPref2.setEnabled(newPref2.isEnabled());
                        if ((oldPref2 instanceof TwoStatePreference) && (newPref2 instanceof TwoStatePreference)) {
                            ((TwoStatePreference) oldPref2).setChecked(((TwoStatePreference) newPref2).isChecked());
                        }
                        if ((oldPref2 instanceof HasSliceAction) && (newPref2 instanceof HasSliceAction)) {
                            ((HasSliceAction) oldPref2).setSliceAction(((HasSliceAction) newPref2).getSliceAction());
                        }
                        oldPref2.setOrder(i);
                        neededToAddNewPref = false;
                    }
                }
            }
            if (neededToAddNewPref) {
                newPref2.setOrder(i);
                screen.addPreference(newPref2);
            }
        }
    }

    public void onPreferenceFocused(Preference preference) {
        setLastFocused(preference);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        Preference preference2 = preference;
        if (preference2 instanceof SliceRadioPreference) {
            SliceRadioPreference radioPref = (SliceRadioPreference) preference2;
            if (!radioPref.isChecked()) {
                radioPref.setChecked(true);
                return true;
            }
            try {
                startIntentSenderForResult(radioPref.getSliceAction().getAction().getIntentSender(), 10000, new Intent().putExtra(SlicesConstants.EXTRA_PREFERENCE_KEY, preference.getKey()), 0, 0, 0, (Bundle) null);
                for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                    Preference pref = getPreferenceScreen().getPreference(i);
                    if ((pref instanceof SliceRadioPreference) && pref != preference2) {
                        ((SliceRadioPreference) pref).setChecked(false);
                    }
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "PendingIntent for slice cannot be sent", e);
            }
        } else if ((preference2 instanceof TwoStatePreference) && (preference2 instanceof HasSliceAction)) {
            try {
                SliceActionImpl action = ((HasSliceAction) preference2).getSliceAction();
                if (action.isToggle()) {
                    action.getActionItem().fireAction(getContext(), new Intent().putExtra("android.app.slice.extra.TOGGLE_STATE", ((TwoStatePreference) preference2).isChecked()).putExtra(SlicesConstants.EXTRA_PREFERENCE_KEY, preference.getKey()));
                } else {
                    action.getActionItem().fireAction((Context) null, (Intent) null);
                }
            } catch (PendingIntent.CanceledException e2) {
                ((TwoStatePreference) preference2).setChecked(!((TwoStatePreference) preference2).isChecked());
                Log.e(TAG, "PendingIntent for slice cannot be sent", e2);
            }
            return true;
        } else if (preference2 instanceof SlicePreference) {
            SlicePreference actionPref = (SlicePreference) preference2;
            if (actionPref.getSliceAction() != null) {
                try {
                    startIntentSenderForResult(actionPref.getSliceAction().getAction().getIntentSender(), 10000, new Intent().putExtra(SlicesConstants.EXTRA_PREFERENCE_KEY, preference.getKey()), 0, 0, 0, (Bundle) null);
                    if (actionPref.getFollowupSliceAction() != null) {
                        try {
                            this.mPreferenceFollowupIntent = actionPref.getFollowupSliceAction().getAction();
                        } catch (IntentSender.SendIntentException e3) {
                            e = e3;
                        }
                    }
                } catch (IntentSender.SendIntentException e4) {
                    e = e4;
                    Log.e(TAG, "PendingIntent for slice cannot be sent", e);
                    return true;
                }
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 0) {
            this.mFollowupPendingIntentExtras = data;
            this.mFollowupPendingIntentResultCode = resultCode;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PREFERENCE_FOLLOWUP_INTENT, this.mPreferenceFollowupIntent);
        outState.putInt(KEY_PREFERENCE_FOLLOWUP_RESULT_CODE, this.mFollowupPendingIntentResultCode);
        outState.putCharSequence(KEY_SCREEN_TITLE, this.mScreenTitle);
        outState.putCharSequence(KEY_SCREEN_SUBTITLE, this.mScreenSubtitle);
        outState.putParcelable(KEY_SCREEN_ICON, this.mScreenIcon);
        outState.putString(KEY_LAST_PREFERENCE, this.mLastFocusedPreferenceKey);
        outState.putString(KEY_URI_STRING, this.mUriString);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            this.mPreferenceFollowupIntent = (PendingIntent) savedInstanceState.getParcelable(KEY_PREFERENCE_FOLLOWUP_INTENT);
            this.mFollowupPendingIntentResultCode = savedInstanceState.getInt(KEY_PREFERENCE_FOLLOWUP_RESULT_CODE);
            this.mScreenTitle = savedInstanceState.getCharSequence(KEY_SCREEN_TITLE);
            this.mScreenSubtitle = savedInstanceState.getCharSequence(KEY_SCREEN_SUBTITLE);
            this.mScreenIcon = (Icon) savedInstanceState.getParcelable(KEY_SCREEN_ICON);
            this.mLastFocusedPreferenceKey = savedInstanceState.getString(KEY_LAST_PREFERENCE);
            this.mUriString = savedInstanceState.getString(KEY_URI_STRING);
        }
    }

    public void onChanged(@NonNull Slice slice) {
        if (getSliceLiveData().mUpdatePending.compareAndSet(true, false)) {
            this.mSlice = slice;
            if (slice != null && slice.getHints() != null) {
                if (slice.getHints().contains("partial")) {
                    showProgressBar();
                } else {
                    hideProgressBar();
                }
                update();
            }
        }
    }

    private void showProgressBar() {
        View progressBar = getView().findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.bringToFront();
            progressBar.setVisibility(0);
        }
    }

    private void hideProgressBar() {
        View progressBar = getView().findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(8);
        }
    }

    private void setSubtitle(CharSequence subtitle) {
        TextView decorSubtitle;
        View view = getView();
        if (view == null) {
            decorSubtitle = null;
        } else {
            decorSubtitle = (TextView) view.findViewById(R.id.decor_subtitle);
        }
        if (decorSubtitle != null) {
            if (TextUtils.isEmpty(subtitle)) {
                decorSubtitle.setVisibility(4);
            } else {
                decorSubtitle.setVisibility(0);
                decorSubtitle.setText(subtitle);
            }
        }
        this.mScreenSubtitle = subtitle;
    }

    private void setIcon(Icon icon) {
        View view = getView();
        ImageView decorIcon = view == null ? null : (ImageView) view.findViewById(R.id.decor_icon);
        if (!(decorIcon == null || icon == null)) {
            decorIcon.setImageDrawable(icon.loadDrawable(this.mContextThemeWrapper));
        }
        this.mScreenIcon = icon;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        LayoutInflater themedInflater = LayoutInflater.from(view.getContext());
        View newTitleContainer = themedInflater.inflate(R.layout.slice_title_container, container, false);
        view.removeView(view.findViewById(R.id.decor_title_container));
        view.addView(newTitleContainer, 0);
        View newContainer = themedInflater.inflate(R.layout.slice_progress_bar, container, false);
        ((ViewGroup) newContainer).addView(view);
        return newContainer;
    }

    public void setLastFocused(Preference preference) {
        this.mLastFocusedPreferenceKey = preference.getKey();
    }

    /* access modifiers changed from: private */
    public void handleUri(Uri uri) {
        String uriString = uri.getQueryParameter("uri");
        if (uriString != null && uriString.equals(this.mUriString)) {
            String direction = uri.getQueryParameter(SlicesConstants.PARAMETER_DIRECTION);
            if (direction != null) {
                if (direction.equals(SlicesConstants.FORWARD)) {
                    forward();
                } else if (direction.equals(SlicesConstants.BACKWARD)) {
                    back();
                }
            }
            String errorMessage = uri.getQueryParameter(SlicesConstants.PARAMETER_ERROR);
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, 0).show();
            }
        }
    }
}
