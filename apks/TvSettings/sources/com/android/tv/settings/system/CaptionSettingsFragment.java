package com.android.tv.settings.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import com.android.internal.widget.SubtitleView;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.R;
import java.util.Locale;

public class CaptionSettingsFragment extends BaseSettingsFragment {
    public static final String ACTION_REFRESH_CAPTIONS_PREVIEW = "CaptionSettingsFragment.refresh";
    private final CaptioningManager.CaptioningChangeListener mCaptionChangeListener = new CaptioningManager.CaptioningChangeListener() {
        public void onEnabledChanged(boolean enabled) {
            CaptionSettingsFragment.this.refreshPreviewText();
        }

        public void onUserStyleChanged(@NonNull CaptioningManager.CaptionStyle userStyle) {
            CaptionSettingsFragment.this.loadCaptionSettings();
            CaptionSettingsFragment.this.refreshPreviewText();
        }

        public void onLocaleChanged(Locale locale) {
            CaptionSettingsFragment.this.loadCaptionSettings();
            CaptionSettingsFragment.this.refreshPreviewText();
        }

        public void onFontScaleChanged(float fontScale) {
            CaptionSettingsFragment.this.loadCaptionSettings();
            CaptionSettingsFragment.this.refreshPreviewText();
        }
    };
    private CaptioningManager mCaptioningManager;
    private int mDefaultFontSize;
    private float mFontScale;
    private Locale mLocale;
    private SubtitleView mPreviewText;
    private View mPreviewWindow;
    private final BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            CaptionSettingsFragment.this.refreshPreviewText();
        }
    };
    private int mStyleId;

    public static CaptionSettingsFragment newInstance() {
        return new CaptionSettingsFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        if (v != null) {
            inflater.inflate(R.layout.captioning_preview, v, true);
            return v;
        }
        throw new IllegalStateException("Unexpectedly null view from super");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCaptioningManager = (CaptioningManager) getActivity().getSystemService("captioning");
        this.mDefaultFontSize = getResources().getInteger(R.integer.captioning_preview_default_font_size);
        loadCaptionSettings();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPreviewText = view.findViewById(R.id.preview_text);
        this.mPreviewWindow = view.findViewById(R.id.preview_window);
    }

    public void onPreferenceStartInitialScreen() {
        startPreferenceFragment(CaptionFragment.newInstance());
    }

    public void onStart() {
        super.onStart();
        this.mCaptioningManager.addCaptioningChangeListener(this.mCaptionChangeListener);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mRefreshReceiver, new IntentFilter(ACTION_REFRESH_CAPTIONS_PREVIEW));
        refreshPreviewText();
    }

    public void onStop() {
        super.onStop();
        this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptionChangeListener);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mRefreshReceiver);
    }

    /* access modifiers changed from: private */
    public void loadCaptionSettings() {
        this.mFontScale = this.mCaptioningManager.getFontScale();
        this.mStyleId = this.mCaptioningManager.getRawUserStyle();
        this.mLocale = this.mCaptioningManager.getLocale();
    }

    /* access modifiers changed from: private */
    public void refreshPreviewText() {
        if (this.mPreviewText == null) {
            return;
        }
        if (this.mCaptioningManager.isEnabled()) {
            this.mPreviewText.setVisibility(0);
            Activity activity = getActivity();
            this.mPreviewText.setStyle(this.mStyleId);
            this.mPreviewText.setTextSize(this.mFontScale * ((float) this.mDefaultFontSize));
            if (this.mLocale != null) {
                this.mPreviewText.setText(AccessibilityUtils.getTextForLocale(activity, this.mLocale, R.string.captioning_preview_text));
            } else {
                this.mPreviewText.setText(getResources().getString(R.string.captioning_preview_text));
            }
            CaptioningManager.CaptionStyle style = this.mCaptioningManager.getUserStyle();
            if (style.hasWindowColor()) {
                this.mPreviewWindow.setBackgroundColor(style.windowColor);
            } else {
                this.mPreviewWindow.setBackgroundColor(CaptioningManager.CaptionStyle.DEFAULT.windowColor);
            }
            this.mPreviewText.invalidate();
            return;
        }
        this.mPreviewText.setVisibility(4);
    }
}
