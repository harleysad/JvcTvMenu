package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.ProgressDialogFragment;

public class FormattingProgressFragment extends ProgressDialogFragment {
    public static FormattingProgressFragment newInstance() {
        return new FormattingProgressFragment();
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle((CharSequence) getActivity().getString(R.string.storage_wizard_format_progress_title));
        setSummary((CharSequence) getActivity().getString(R.string.storage_wizard_format_progress_description));
    }
}
