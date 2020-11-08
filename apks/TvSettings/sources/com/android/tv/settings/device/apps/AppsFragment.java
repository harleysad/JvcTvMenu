package com.android.tv.settings.device.apps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.tv.settings.PreferenceControllerFragment;
import com.android.tv.settings.R;
import java.util.ArrayList;
import java.util.List;

@Keep
public class AppsFragment extends PreferenceControllerFragment {
    private static final String KEY_PERMISSIONS = "Permissions";

    public static void prepareArgs(Bundle b, String volumeUuid, String volumeName) {
        b.putString("volumeUuid", volumeUuid);
        b.putString("volumeName", volumeName);
    }

    public static AppsFragment newInstance(String volumeUuid, String volumeName) {
        Bundle b = new Bundle(2);
        prepareArgs(b, volumeUuid, volumeName);
        AppsFragment f = new AppsFragment();
        f.setArguments(b);
        return f;
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        findPreference(KEY_PERMISSIONS).setVisible(TextUtils.isEmpty(getArguments().getString("volumeUuid")));
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return R.xml.apps;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> onCreatePreferenceControllers(Context context) {
        Activity activity = getActivity();
        Application app = activity != null ? activity.getApplication() : null;
        List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new RecentAppsPreferenceController(getContext(), app));
        return controllers;
    }

    public int getMetricsCategory() {
        return 748;
    }
}
