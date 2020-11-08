package com.android.tv.settings.device.storage;

import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

public class ConfirmClearCacheFragment extends GuidedStepFragment {
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.device_storage_clear_cache_title), getString(R.string.device_storage_clear_cache_message), (String) null, getContext().getDrawable(R.drawable.ic_settings_backup_restore_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            super.onGuidedActionClicked(action);
            PackageManager pm = getContext().getPackageManager();
            for (PackageInfo info : pm.getInstalledPackages(0)) {
                pm.deleteApplicationCacheFiles(info.packageName, (IPackageDataObserver) null);
            }
            getFragmentManager().popBackStack();
            return;
        }
        getFragmentManager().popBackStack();
    }
}
