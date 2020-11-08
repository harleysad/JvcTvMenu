package com.android.tv.settings.device.storage;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;
import com.android.tv.settings.R;
import java.util.List;

public class ResetActivity extends Activity {
    private static final String SHUTDOWN_INTENT_EXTRA = "shutdown";
    private static final String TAG = "ResetActivity";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, ResetFragment.newInstance(), 16908290);
        }
    }

    public static class ResetFragment extends GuidedStepFragment {
        public static ResetFragment newInstance() {
            Bundle args = new Bundle();
            ResetFragment fragment = new ResetFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.device_reset), getString(R.string.factory_reset_description), (String) null, getContext().getDrawable(R.drawable.ic_settings_backup_restore_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).title((CharSequence) getString(R.string.device_reset))).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                add(getFragmentManager(), ResetConfirmFragment.newInstance());
            } else if (action.getId() == -5) {
                getActivity().finish();
            } else {
                Log.wtf(ResetActivity.TAG, "Unknown action clicked");
            }
        }
    }

    public static class ResetConfirmFragment extends GuidedStepFragment {
        public static ResetConfirmFragment newInstance() {
            Bundle args = new Bundle();
            ResetConfirmFragment fragment = new ResetConfirmFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.device_reset), getString(R.string.confirm_factory_reset_description), (String) null, getContext().getDrawable(R.drawable.ic_settings_backup_restore_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).title((CharSequence) getString(R.string.confirm_factory_reset_device))).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                if (ActivityManager.isUserAMonkey()) {
                    Log.v(ResetActivity.TAG, "Monkey tried to erase the device. Bad monkey, bad!");
                    getActivity().finish();
                    return;
                }
                Intent resetIntent = new Intent("android.intent.action.FACTORY_RESET");
                resetIntent.setPackage("android");
                resetIntent.setFlags(268435456);
                resetIntent.putExtra("android.intent.extra.REASON", "ResetConfirmFragment");
                if (getActivity().getIntent().getBooleanExtra(ResetActivity.SHUTDOWN_INTENT_EXTRA, false)) {
                    resetIntent.putExtra(ResetActivity.SHUTDOWN_INTENT_EXTRA, true);
                }
                getContext().sendBroadcast(resetIntent);
            } else if (action.getId() == -5) {
                getActivity().finish();
            } else {
                Log.wtf(ResetActivity.TAG, "Unknown action clicked");
            }
        }
    }
}
