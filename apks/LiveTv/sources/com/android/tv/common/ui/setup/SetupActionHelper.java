package com.android.tv.common.ui.setup;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SetupActionHelper {
    private static final String TAG = "SetupActionHelper";

    public static boolean onActionClick(Fragment fragment, String category, int actionId) {
        return onActionClick(fragment, category, actionId, (Bundle) null);
    }

    public static boolean onActionClick(Fragment fragment, String category, int actionId, Bundle params) {
        if (fragment.getActivity() instanceof OnActionClickListener) {
            return ((OnActionClickListener) fragment.getActivity()).onActionClick(category, actionId, params);
        }
        Log.e(TAG, "Activity can't handle the action: {category=" + category + ", actionId=" + actionId + ", params=" + params + "}");
        return false;
    }

    public static View.OnClickListener createOnClickListenerForAction(Fragment fragment, String category, int actionId, Bundle params) {
        return new OnActionClickListenerForAction(fragment, category, actionId, params);
    }

    private static class OnActionClickListenerForAction implements View.OnClickListener {
        private final int mActionId;
        private final String mCategory;
        private final Fragment mFragment;
        private final Bundle mParams;

        OnActionClickListenerForAction(Fragment fragment, String category, int actionId, Bundle params) {
            this.mFragment = fragment;
            this.mCategory = category;
            this.mActionId = actionId;
            this.mParams = params;
        }

        public void onClick(View v) {
            SetupActionHelper.onActionClick(this.mFragment, this.mCategory, this.mActionId, this.mParams);
        }
    }

    private SetupActionHelper() {
    }
}
