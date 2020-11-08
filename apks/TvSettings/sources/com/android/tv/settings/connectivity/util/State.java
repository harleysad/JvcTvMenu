package com.android.tv.settings.connectivity.util;

import android.support.v4.app.Fragment;

public interface State {

    public interface FragmentChangeListener {
        void onFragmentChange(Fragment fragment, boolean z);
    }

    public interface StateCompleteListener {
        void onComplete(int i);
    }

    Fragment getFragment();

    void processBackward();

    void processForward();
}
