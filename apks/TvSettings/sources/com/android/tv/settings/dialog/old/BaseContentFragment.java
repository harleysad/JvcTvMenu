package com.android.tv.settings.dialog.old;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.widget.FrameLayoutWithShadows;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;

public class BaseContentFragment {
    private static final String EXTRA_BREADCRUMB = "breadcrumb";
    private static final String EXTRA_DESCRIPTION = "description";
    private static final String EXTRA_ICON_BACKGROUND = "iconBackground";
    private static final String EXTRA_ICON_BITMAP = "iconBitmap";
    private static final String EXTRA_ICON_RESOURCE_ID = "iconResourceId";
    private static final String EXTRA_ICON_URI = "iconUri";
    private static final String EXTRA_TITLE = "title";
    private AccessibilityManager mAccessManager;
    private Activity mActivity;
    private String mBreadcrumb;
    private String mDescription;
    private final LiteFragment mFragment;
    private int mIconBackgroundColor;
    private Bitmap mIconBitmap;
    private int mIconResourceId;
    private String mTitle;

    public static Bundle buildArgs(String title, String breadcrumb, String description, int iconResourceId, int backgroundColor) {
        return buildArgs(title, breadcrumb, description, iconResourceId, (Uri) null, (Bitmap) null, backgroundColor);
    }

    public static Bundle buildArgs(String title, String breadcrumb, String description, Uri iconUri, int backgroundColor) {
        return buildArgs(title, breadcrumb, description, 0, iconUri, (Bitmap) null, backgroundColor);
    }

    public static Bundle buildArgs(String title, String breadcrumb, String description, Bitmap iconBitmap) {
        return buildArgs(title, breadcrumb, description, 0, (Uri) null, iconBitmap, 0);
    }

    private static Bundle buildArgs(String title, String breadcrumb, String description, int iconResourceId, Uri iconUri, Bitmap iconBitmap, int iconBackgroundColor) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_BREADCRUMB, breadcrumb);
        args.putString(EXTRA_DESCRIPTION, description);
        args.putInt(EXTRA_ICON_RESOURCE_ID, iconResourceId);
        args.putParcelable(EXTRA_ICON_URI, iconUri);
        args.putParcelable(EXTRA_ICON_BITMAP, iconBitmap);
        args.putInt(EXTRA_ICON_BACKGROUND, iconBackgroundColor);
        return args;
    }

    public BaseContentFragment(LiteFragment fragment) {
        this.mFragment = fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        Bundle state = savedInstanceState != null ? savedInstanceState : this.mFragment.getArguments();
        if (this.mTitle == null) {
            this.mTitle = state.getString(EXTRA_TITLE);
        }
        if (this.mBreadcrumb == null) {
            this.mBreadcrumb = state.getString(EXTRA_BREADCRUMB);
        }
        if (this.mDescription == null) {
            this.mDescription = state.getString(EXTRA_DESCRIPTION);
        }
        if (this.mIconResourceId == 0) {
            this.mIconResourceId = state.getInt(EXTRA_ICON_RESOURCE_ID, 0);
        }
        if (this.mIconBitmap == null) {
            this.mIconBitmap = (Bitmap) state.getParcelable(EXTRA_ICON_BITMAP);
        }
        if (this.mIconBackgroundColor == 0) {
            this.mIconBackgroundColor = state.getInt(EXTRA_ICON_BACKGROUND, 0);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_TITLE, this.mTitle);
        outState.putString(EXTRA_BREADCRUMB, this.mBreadcrumb);
        outState.putString(EXTRA_DESCRIPTION, this.mDescription);
        outState.putInt(EXTRA_ICON_RESOURCE_ID, this.mIconResourceId);
        outState.putParcelable(EXTRA_ICON_BITMAP, this.mIconBitmap);
        outState.putInt(EXTRA_ICON_BACKGROUND, this.mIconBackgroundColor);
    }

    public void onAttach(Activity activity) {
        this.mActivity = activity;
    }

    public void onDetach() {
        this.mActivity = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment, container, false);
        setText(view, R.id.title, this.mTitle);
        setText(view, R.id.breadcrumb, this.mBreadcrumb);
        setText(view, R.id.description, this.mDescription);
        int iconResourceId = getIconResourceId();
        ImageView iconImageView = (ImageView) view.findViewById(R.id.icon);
        int iconBackground = getIconBackgroundColor();
        if (iconBackground != 0) {
            iconImageView.setBackgroundColor(iconBackground);
        }
        if (iconResourceId != 0) {
            iconImageView.setImageResource(iconResourceId);
            addShadow(iconImageView, view);
            updateViewSize(iconImageView);
        } else {
            Bitmap iconBitmap = getIconBitmap();
            if (iconBitmap != null) {
                iconImageView.setImageBitmap(iconBitmap);
                addShadow(iconImageView, view);
                updateViewSize(iconImageView);
            } else {
                iconImageView.setVisibility(8);
            }
        }
        return view;
    }

    public ImageView getIcon() {
        if (this.mFragment.getView() == null) {
            return null;
        }
        return (ImageView) this.mFragment.getView().findViewById(R.id.icon);
    }

    public TextView getTitle() {
        if (this.mFragment.getView() == null) {
            return null;
        }
        return (TextView) this.mFragment.getView().findViewById(R.id.title);
    }

    public int getIconResourceId() {
        return this.mIconResourceId;
    }

    public Bitmap getIconBitmap() {
        return this.mIconBitmap;
    }

    public int getIconBackgroundColor() {
        return this.mIconBackgroundColor;
    }

    public RelativeLayout getRoot() {
        return (RelativeLayout) this.mFragment.getView();
    }

    public TextView getBreadCrumb() {
        if (this.mFragment.getView() == null) {
            return null;
        }
        return (TextView) this.mFragment.getView().findViewById(R.id.breadcrumb);
    }

    public TextView getDescription() {
        if (this.mFragment.getView() == null) {
            return null;
        }
        return (TextView) this.mFragment.getView().findViewById(R.id.description);
    }

    public void setTextToExtra(View parent, int textViewResourceId, String extraLabel) {
        setText(parent, textViewResourceId, this.mFragment.getArguments().getString(extraLabel, (String) null));
    }

    public void setTextToExtra(int textViewResourceId, String extraLabel) {
        if (this.mFragment.getView() != null) {
            setTextToExtra(this.mFragment.getView(), textViewResourceId, extraLabel);
        }
    }

    public void setText(View parent, int textViewResourceId, String text) {
        TextView textView = (TextView) parent.findViewById(textViewResourceId);
        if (textView != null && text != null) {
            textView.setText(text);
            if (this.mActivity != null) {
                if (this.mAccessManager == null) {
                    this.mAccessManager = (AccessibilityManager) this.mActivity.getSystemService(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY);
                }
                if (this.mAccessManager.isEnabled()) {
                    textView.setFocusable(true);
                    textView.setFocusableInTouchMode(true);
                }
            }
        }
    }

    public void setText(int textViewResourceId, String text) {
        if (this.mFragment.getView() != null) {
            setText(this.mFragment.getView(), textViewResourceId, text);
        }
    }

    public void setTitleText(String text) {
        this.mTitle = text;
        if (this.mFragment.getView() != null) {
            setText(this.mFragment.getView(), R.id.title, text);
        }
    }

    public void setBreadCrumbText(String text) {
        this.mBreadcrumb = text;
        if (this.mFragment.getView() != null) {
            setText(this.mFragment.getView(), R.id.breadcrumb, text);
        }
    }

    public void setDescriptionText(String text) {
        this.mDescription = text;
        if (this.mFragment.getView() != null) {
            setText(this.mFragment.getView(), R.id.description, text);
        }
    }

    public void setIcon(Drawable iconDrawable) {
        ImageView iconImageView;
        if (this.mFragment.getView() != null && (iconImageView = (ImageView) this.mFragment.getView().findViewById(R.id.icon)) != null && iconDrawable != null) {
            iconImageView.setImageDrawable(iconDrawable);
            iconImageView.setVisibility(0);
            updateViewSize(iconImageView);
        }
    }

    public void setIcon(int iconResourceId) {
        ImageView iconImageView;
        this.mIconResourceId = iconResourceId;
        if (this.mFragment.getView() != null && (iconImageView = (ImageView) this.mFragment.getView().findViewById(R.id.icon)) != null && iconResourceId != 0) {
            iconImageView.setImageResource(iconResourceId);
            iconImageView.setVisibility(0);
            updateViewSize(iconImageView);
        }
    }

    private void updateViewSize(ImageView iconView) {
        int intrinsicWidth = iconView.getDrawable().getIntrinsicWidth();
        ViewGroup.LayoutParams lp = iconView.getLayoutParams();
        if (intrinsicWidth > 0) {
            lp.height = (lp.width * iconView.getDrawable().getIntrinsicHeight()) / intrinsicWidth;
        } else {
            lp.height = lp.width;
        }
    }

    private void addShadow(ImageView icon, View view) {
        ((FrameLayoutWithShadows) view.findViewById(R.id.shadow_layout)).addShadowView(icon);
    }

    private void fadeIn(View v) {
        v.setAlpha(0.0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", new float[]{1.0f});
        alphaAnimator.setDuration((long) this.mActivity.getResources().getInteger(17694721));
        alphaAnimator.start();
    }
}
