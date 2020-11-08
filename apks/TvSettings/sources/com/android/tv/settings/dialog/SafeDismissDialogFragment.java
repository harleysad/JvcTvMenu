package com.android.tv.settings.dialog;

import android.app.Activity;
import android.app.DialogFragment;

public class SafeDismissDialogFragment extends DialogFragment {
    private boolean mAttached = false;
    private boolean mDismissPending = false;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAttached = true;
        if (this.mDismissPending) {
            this.mDismissPending = false;
            dismiss();
        }
    }

    public void onDetach() {
        this.mAttached = false;
        super.onDetach();
    }

    public void dismiss() {
        if (!this.mAttached) {
            this.mDismissPending = true;
        } else {
            super.dismiss();
        }
    }
}
