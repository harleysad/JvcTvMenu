package com.android.tv.settings.device.apps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v7.preference.Preference;
import com.android.settingslib.applications.ApplicationsState;
import java.util.List;

public abstract class AppActionPreference extends Preference {
    protected ApplicationsState.AppEntry mEntry;

    public abstract void refresh();

    public AppActionPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context);
        this.mEntry = entry;
    }

    public void setEntry(@NonNull ApplicationsState.AppEntry entry) {
        this.mEntry = entry;
        refresh();
    }

    public static abstract class ConfirmationFragment extends GuidedStepFragment {
        private static final int ID_CANCEL = 1;
        private static final int ID_OK = 0;

        public abstract void onOk();

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().title((CharSequence) getString(17039370))).id(0)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().title((CharSequence) getString(17039360))).id(1)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (((int) action.getId()) == 0) {
                onOk();
            }
            getFragmentManager().popBackStack();
        }
    }
}
