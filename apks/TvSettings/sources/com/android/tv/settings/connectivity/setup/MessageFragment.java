package com.android.tv.settings.connectivity.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.util.AccessibilityHelper;

public class MessageFragment extends Fragment {
    private static final String EXTRA_SHOW_PROGRESS_INDICATOR = "show_progress_indicator";
    private static final String EXTRA_TITLE = "title";

    public static MessageFragment newInstance(String title, boolean showProgressIndicator) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        addArguments(args, title, showProgressIndicator);
        fragment.setArguments(args);
        return fragment;
    }

    public static void addArguments(Bundle args, String title, boolean showProgressIndicator) {
        args.putString(EXTRA_TITLE, title);
        args.putBoolean(EXTRA_SHOW_PROGRESS_INDICATOR, showProgressIndicator);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View view = inflater.inflate(R.layout.setup_message, container, false);
        View progressView = view.findViewById(R.id.progress);
        TextView titleView = (TextView) view.findViewById(R.id.status_text);
        Bundle args = getArguments();
        String title = args.getString(EXTRA_TITLE);
        boolean showProgressIndicator = args.getBoolean(EXTRA_SHOW_PROGRESS_INDICATOR);
        if (title != null) {
            titleView.setText(title);
            titleView.setVisibility(0);
            if (AccessibilityHelper.forceFocusableViews(getActivity())) {
                titleView.setFocusable(true);
                titleView.setFocusableInTouchMode(true);
            }
        } else {
            titleView.setVisibility(8);
        }
        if (showProgressIndicator) {
            progressView.setVisibility(0);
        } else {
            progressView.setVisibility(8);
        }
        return view;
    }

    public void onResume() {
        super.onResume();
        if (AccessibilityHelper.forceFocusableViews(getActivity())) {
            ((TextView) getView().findViewById(R.id.status_text)).requestFocus();
        }
    }
}
