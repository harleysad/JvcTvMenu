package com.android.tv.dialog;

import android.app.Activity;
import android.app.DialogFragment;

public abstract class SafeDismissDialogFragment extends DialogFragment {
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

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
        this.mAttached = false;
    }

    public void dismiss() {
        if (!this.mAttached) {
            this.mDismissPending = true;
        } else {
            super.dismiss();
        }
    }
}
