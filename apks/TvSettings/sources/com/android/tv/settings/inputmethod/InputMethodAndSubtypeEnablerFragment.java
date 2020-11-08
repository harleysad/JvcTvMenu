package com.android.tv.settings.inputmethod;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeEnablerManager;
import com.android.tv.settings.SettingsPreferenceFragment;

@Keep
public class InputMethodAndSubtypeEnablerFragment extends SettingsPreferenceFragment {
    private InputMethodAndSubtypeEnablerManager mManager;

    public static InputMethodAndSubtypeEnablerFragment newInstance() {
        return new InputMethodAndSubtypeEnablerFragment();
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        String targetImi = getStringExtraFromIntentOrArguments("input_method_id");
        String title = getStringExtraFromIntentOrArguments("android.intent.extra.TITLE");
        this.mManager = new InputMethodAndSubtypeEnablerManager(this);
        this.mManager.init(this, targetImi, root);
        root.setTitle((CharSequence) title);
        setPreferenceScreen(root);
    }

    private String getStringExtraFromIntentOrArguments(String name) {
        String fromIntent = getActivity().getIntent().getStringExtra(name);
        if (fromIntent != null) {
            return fromIntent;
        }
        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        return arguments.getString(name);
    }

    public void onActivityCreated(Bundle icicle) {
        super.onActivityCreated(icicle);
        String title = getStringExtraFromIntentOrArguments("android.intent.extra.TITLE");
        if (!TextUtils.isEmpty(title)) {
            getActivity().setTitle(title);
        }
    }

    public void onResume() {
        super.onResume();
        this.mManager.refresh(getContext(), this);
    }

    public void onPause() {
        super.onPause();
        this.mManager.save(getContext(), this);
    }

    public int getMetricsCategory() {
        return 60;
    }
}
