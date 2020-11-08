package com.android.tv.settings.dialog;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.tv.settings.R;

public class ProgressDialogFragment extends Fragment {
    private TextView mExtraTextView;
    private ImageView mIconView;
    private TextView mOperationalRemindTextView;
    private ProgressBar mProgressBar;
    private TextView mSummaryView;
    private TextView mTitleView;
    private int mWidth = -1;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.progress_fragment, container, false);
        this.mIconView = (ImageView) view.findViewById(16908294);
        this.mTitleView = (TextView) view.findViewById(16908310);
        this.mExtraTextView = (TextView) view.findViewById(R.id.extra);
        this.mSummaryView = (TextView) view.findViewById(16908304);
        this.mProgressBar = (ProgressBar) view.findViewById(16908301);
        this.mOperationalRemindTextView = (TextView) view.findViewById(R.id.operational_remind);
        if (this.mWidth != -1) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = this.mWidth;
            view.setLayoutParams(params);
        }
        return view;
    }

    public void setIcon(@DrawableRes int resId) {
        this.mIconView.setImageResource(resId);
        this.mIconView.setVisibility(0);
    }

    public void setIcon(@Nullable Drawable icon) {
        this.mIconView.setImageDrawable(icon);
        this.mIconView.setVisibility(icon == null ? 8 : 0);
    }

    public void setTitle(@StringRes int resId) {
        this.mTitleView.setText(resId);
    }

    public void setTitle(CharSequence title) {
        this.mTitleView.setText(title);
    }

    public void setExtraText(@StringRes int resId) {
        this.mExtraTextView.setText(resId);
    }

    public void setExtraText(CharSequence text) {
        this.mExtraTextView.setText(text);
        this.mExtraTextView.setVisibility(TextUtils.isEmpty(text) ? 8 : 0);
    }

    public void setOperationalRemindText(@StringRes int resId) {
        this.mOperationalRemindTextView.setText(resId);
    }

    public void setOperationalRemindText(CharSequence text) {
        this.mOperationalRemindTextView.setText(text);
    }

    public void setSummary(@StringRes int resId) {
        this.mSummaryView.setText(resId);
    }

    public void setSummary(CharSequence summary) {
        this.mSummaryView.setText(summary);
    }

    public void setIndeterminte(boolean indeterminte) {
        this.mProgressBar.setIndeterminate(indeterminte);
    }

    public void setProgress(int progress) {
        this.mProgressBar.setProgress(progress);
    }

    public void setProgressMax(int max) {
        this.mProgressBar.setMax(max);
    }

    public void setContentWidth(int width) {
        this.mWidth = width;
        View root = getView();
        if (root != null) {
            ViewGroup.LayoutParams params = root.getLayoutParams();
            params.width = width;
            root.setLayoutParams(params);
        }
    }
}
