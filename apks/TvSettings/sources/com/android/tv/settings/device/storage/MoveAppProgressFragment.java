package com.android.tv.settings.device.storage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.ProgressDialogFragment;
import com.mediatek.twoworlds.tv.MtkTvConfigBase;

public class MoveAppProgressFragment extends ProgressDialogFragment {
    private static final String ARG_APP_TITLE = "appTitle";

    public static MoveAppProgressFragment newInstance(CharSequence appTitle) {
        MoveAppProgressFragment fragment = new MoveAppProgressFragment();
        Bundle b = new Bundle(1);
        b.putCharSequence(ARG_APP_TITLE, appTitle);
        fragment.setArguments(b);
        return fragment;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CharSequence appTitle = getArguments().getCharSequence(ARG_APP_TITLE);
        setTitle((CharSequence) getActivity().getString(R.string.storage_wizard_move_app_progress_title, new Object[]{appTitle}));
        setSummary((CharSequence) getActivity().getString(R.string.storage_wizard_move_app_progress_description, new Object[]{appTitle}));
    }

    public static CharSequence moveStatusToMessage(Context context, int returnCode) {
        if (returnCode == -8) {
            return context.getString(R.string.move_error_device_admin);
        }
        switch (returnCode) {
            case MtkTvConfigBase.CFGR_REC_NOT_FOUND /*-5*/:
                return context.getString(R.string.invalid_location);
            case -4:
                return context.getString(R.string.app_forward_locked);
            case -3:
                return context.getString(R.string.system_package);
            case -2:
                return context.getString(R.string.does_not_exist);
            case -1:
                return context.getString(R.string.insufficient_storage);
            default:
                return context.getString(R.string.insufficient_storage);
        }
    }
}
