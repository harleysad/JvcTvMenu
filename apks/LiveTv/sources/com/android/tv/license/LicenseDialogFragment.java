package com.android.tv.license;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.dialog.SafeDismissDialogFragment;
import com.mediatek.wwtv.tvcenter.R;

public class LicenseDialogFragment extends SafeDismissDialogFragment {
    public static final String DIALOG_TAG = LicenseDialogFragment.class.getSimpleName();
    private static final String LICENSE = "LICENSE";
    private License mLicense;
    private String mTrackerLabel;

    public static LicenseDialogFragment newInstance(License license) {
        LicenseDialogFragment f = new LicenseDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(LICENSE, license);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        int style;
        super.onCreate(savedInstanceState);
        this.mLicense = (License) getArguments().getParcelable(LICENSE);
        String title = this.mLicense.getLibraryName();
        this.mTrackerLabel = getArguments().getString(title + "_license");
        if (TextUtils.isEmpty(title)) {
            style = 1;
        } else {
            style = 0;
        }
        setStyle(style, 0);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        String licenseText = Licenses.getLicenseText(getContext(), this.mLicense);
        textView.setText(licenseText != null ? licenseText : "");
        textView.setMovementMethod(new ScrollingMovementMethod());
        int verticalOverscan = getResources().getDimensionPixelSize(R.dimen.vertical_overscan_safe_margin);
        int horizontalOverscan = getResources().getDimensionPixelSize(R.dimen.horizontal_overscan_safe_margin);
        textView.setPadding(horizontalOverscan, verticalOverscan, horizontalOverscan, verticalOverscan);
        return textView;
    }

    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(-1, -1);
        getDialog().setTitle(this.mLicense.getLibraryName());
    }

    public String getTrackerLabel() {
        return this.mTrackerLabel;
    }
}
