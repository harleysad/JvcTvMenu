package com.android.tv.settings.system.development;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.usb.IUsbManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;
import com.android.tv.settings.R;
import java.util.List;

@Keep
public class AdbKeysDialog extends GuidedStepFragment {
    private static final String TAG = "AdbKeysDialog";

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.clear_adb_keys), getString(R.string.adb_keys_warning_message), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getContext();
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-4)).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            try {
                IUsbManager.Stub.asInterface(ServiceManager.getService("usb")).clearUsbDebuggingKeys();
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to clear adb keys", e);
            }
            getFragmentManager().popBackStack();
            return;
        }
        getFragmentManager().popBackStack();
    }
}
