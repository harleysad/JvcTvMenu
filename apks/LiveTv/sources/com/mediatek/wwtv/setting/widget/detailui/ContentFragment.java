package com.mediatek.wwtv.setting.widget.detailui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentFragment extends Fragment implements LiteFragment {
    private final BaseContentFragment mBase = new BaseContentFragment(this);

    public static ContentFragment newInstance(String title) {
        return newInstance(title, (String) null, (String) null, 0, 0);
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description) {
        return newInstance(title, breadcrumb, description, 0, 0);
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description, int iconResourceId) {
        return newInstance(title, breadcrumb, description, iconResourceId, 0);
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description, int iconResourceId, int iconBackgroundColor) {
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(BaseContentFragment.buildArgs(title, breadcrumb, description, iconResourceId, iconBackgroundColor));
        return fragment;
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description, Uri iconResourceUri) {
        return newInstance(title, breadcrumb, description, iconResourceUri, 0);
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description, Uri iconResourceUri, int iconBackgroundColor) {
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(BaseContentFragment.buildArgs(title, breadcrumb, description, iconResourceUri, iconBackgroundColor));
        return fragment;
    }

    public static ContentFragment newInstance(String title, String breadcrumb, String description, Bitmap iconbitmap) {
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(BaseContentFragment.buildArgs(title, breadcrumb, description, iconbitmap));
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mBase.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        this.mBase.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public void onAttach(Activity activity) {
        this.mBase.onAttach(activity);
        super.onAttach(activity);
    }

    public void onDetach() {
        this.mBase.onDetach();
        super.onDetach();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.mBase.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroyView() {
        this.mBase.onDestroyView();
        super.onDestroyView();
    }

    public ImageView getIcon() {
        return this.mBase.getIcon();
    }

    public TextView getTitle() {
        return this.mBase.getTitle();
    }

    public int getIconResourceId() {
        return this.mBase.getIconResourceId();
    }

    public Uri getIconResourceUri() {
        return this.mBase.getIconResourceUri();
    }

    public Bitmap getIconBitmap() {
        return this.mBase.getIconBitmap();
    }

    public RelativeLayout getRoot() {
        return this.mBase.getRoot();
    }

    public TextView getBreadCrumb() {
        return this.mBase.getBreadCrumb();
    }

    public TextView getDescription() {
        return this.mBase.getDescription();
    }

    public void setTextToExtra(int textViewResourceId, String extraLabel) {
        this.mBase.setTextToExtra(textViewResourceId, extraLabel);
    }

    public void setText(int textViewResourceId, String text) {
        this.mBase.setText(textViewResourceId, text);
    }

    public void setTitleText(String text) {
        this.mBase.setTitleText(text);
    }

    public void setBreadCrumbText(String text) {
        this.mBase.setBreadCrumbText(text);
    }

    public void setDescriptionText(String text) {
        this.mBase.setDescriptionText(text);
    }

    public void setIcon(int iconResourceId) {
        this.mBase.setIcon(iconResourceId);
    }

    public void setIcon(Uri iconUri) {
        this.mBase.setIcon(iconUri);
    }

    public void setIcon(Drawable iconDrawable) {
        this.mBase.setIcon(iconDrawable);
    }

    /* access modifiers changed from: protected */
    public void setTextToExtra(View parent, int textViewResourceId, String extraLabel) {
        this.mBase.setTextToExtra(parent, textViewResourceId, extraLabel);
    }

    /* access modifiers changed from: protected */
    public void setText(View parent, int textViewResourceId, String text) {
        this.mBase.setText(parent, textViewResourceId, text);
    }
}
