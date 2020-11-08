package com.android.tv.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebDialogFragment extends SafeDismissDialogFragment {
    private static final String TAG = "WebDialogFragment";
    private static final String TITLE = "TITLE";
    private static final String TRACKER_LABEL = "TRACKER_LABEL";
    private static final String URL = "URL";
    private String mTrackerLabel;
    private WebView mWebView;

    public static WebDialogFragment newInstance(String url, @Nullable String title, String trackerLabel) {
        WebDialogFragment f = new WebDialogFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(TITLE, title);
        args.putString(TRACKER_LABEL, trackerLabel);
        f.setArguments(args);
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        int style;
        super.onCreate(savedInstanceState);
        String title = getArguments().getString(TITLE);
        this.mTrackerLabel = getArguments().getString(TRACKER_LABEL);
        if (TextUtils.isEmpty(title)) {
            style = 1;
        } else {
            style = 0;
        }
        setStyle(style, 0);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getArguments().getString(TITLE));
        this.mWebView = new WebView(getActivity());
        this.mWebView.setWebViewClient(new WebViewClient());
        String url = getArguments().getString(URL);
        this.mWebView.loadUrl(url);
        Log.d(TAG, "Loading web content from " + url);
        return this.mWebView;
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.mWebView != null) {
            this.mWebView.destroy();
        }
    }

    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(-1, -1);
    }

    public String getTrackerLabel() {
        return this.mTrackerLabel;
    }
}
