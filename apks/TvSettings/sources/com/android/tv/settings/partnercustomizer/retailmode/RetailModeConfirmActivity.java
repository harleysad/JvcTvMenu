package com.android.tv.settings.partnercustomizer.retailmode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import java.util.List;

public class RetailModeConfirmActivity extends Activity {
    private static final String TAG = "RetailModeConfirmActivity";
    /* access modifiers changed from: private */
    public static String retailMode;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            GuidedStepFragment.addAsRoot(this, ConfirmFragment.newInstance(), 16908290);
        }
        Intent intent = getIntent();
        if (intent != null) {
            retailMode = getString(intent.getBooleanExtra(PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, false) ? R.string.pic_advance_video_entries_on : R.string.pic_advance_video_entries_off);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public static class ConfirmFragment extends GuidedStepFragment {
        public static ConfirmFragment newInstance() {
            Bundle args = new Bundle();
            ConfirmFragment fragment = new ConfirmFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.s_retail_mode), getString(R.string.s_retail_mode_confirm_description), (String) null, getContext().getDrawable(R.drawable.ic_settings_backup_restore_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).title((CharSequence) getString(R.string.s_retail_mode_confirm_continue))).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                add(getFragmentManager(), DoulbeConfirmFragment.newInstance());
            } else if (action.getId() == -5) {
                getActivity().finish();
            } else {
                Log.wtf(RetailModeConfirmActivity.TAG, "Unknown action clicked");
            }
        }
    }

    public static class DoulbeConfirmFragment extends GuidedStepFragment {
        public static DoulbeConfirmFragment newInstance() {
            Bundle args = new Bundle();
            DoulbeConfirmFragment fragment = new DoulbeConfirmFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String string = getString(R.string.s_retail_mode);
            return new GuidanceStylist.Guidance(string, getString(R.string.s_retail_mode_confirm2_description) + RetailModeConfirmActivity.retailMode, (String) null, getContext().getDrawable(R.drawable.ic_settings_backup_restore_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).title((CharSequence) getString(R.string.pic_advance_video_entries_off))).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).title((CharSequence) getString(R.string.pic_advance_video_entries_on))).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                Log.d(RetailModeConfirmActivity.TAG, "confirm OK !!!!");
                PreferenceConfigUtils.putSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 1);
                getActivity().finish();
            } else if (action.getId() == -5) {
                Log.d(RetailModeConfirmActivity.TAG, "confirm NO !!!!");
                PreferenceConfigUtils.putSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_DEVICE_RETAILMODE_ENABLE, 0);
                getActivity().finish();
            } else {
                Log.wtf(RetailModeConfirmActivity.TAG, "Unknown action clicked");
            }
        }
    }
}
