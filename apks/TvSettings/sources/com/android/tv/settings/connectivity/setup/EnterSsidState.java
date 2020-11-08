package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.WifiConfigHelper;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class EnterSsidState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public EnterSsidState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new EnterSsidFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new EnterSsidFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class EnterSsidFragment extends WifiConnectivityGuidedStepFragment {
        private GuidedAction mAction;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_ssid), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                public int onProvideItemLayoutId() {
                    return R.layout.setup_text_input_item;
                }
            };
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            CharSequence prevSsid = this.mUserChoiceInfo.getPageSummary(4);
            this.mAction = ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).editable(true)).title(prevSsid == null ? "" : prevSsid)).build();
            actions.add(this.mAction);
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            openInEditMode(this.mAction);
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            this.mUserChoiceInfo.put(4, action.getTitle().toString());
            CharSequence ssid = action.getTitle();
            if (!TextUtils.equals(this.mUserChoiceInfo.getWifiConfiguration().getPrintableSsid(), ssid)) {
                this.mUserChoiceInfo.removePageSummary(2);
            }
            WifiConfigHelper.setConfigSsid(this.mUserChoiceInfo.getWifiConfiguration(), ssid.toString());
            this.mStateMachine.getListener().onComplete(2);
            return action.getId();
        }
    }
}
