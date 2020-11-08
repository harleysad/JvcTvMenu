package com.android.tv.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public abstract class MenuRowView extends LinearLayout {
    private static final boolean DEBUG = false;
    private static final String TAG = "MenuRowView";
    private View mContentsView;
    private View mLastFocusView;
    private final View.OnFocusChangeListener mOnFocusChangeListener;
    private MenuRow mRow;
    private TextView mTitleView;
    private final float mTitleViewAlphaDeselected;
    private final float mTitleViewScaleSelected;

    /* access modifiers changed from: protected */
    public abstract int getContentsViewId();

    public float getTitleViewAlphaDeselected() {
        return this.mTitleViewAlphaDeselected;
    }

    public float getTitleViewScaleSelected() {
        return this.mTitleViewScaleSelected;
    }

    public MenuRowView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MenuRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MenuRowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOnFocusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                MenuRowView.this.onChildFocusChange(v, hasFocus);
            }
        };
        Resources res = context.getResources();
        TypedValue outValue = new TypedValue();
        res.getValue(R.dimen.menu_row_title_alpha_deselected, outValue, true);
        this.mTitleViewAlphaDeselected = outValue.getFloat();
        this.mTitleViewScaleSelected = ((float) res.getDimensionPixelSize(R.dimen.menu_row_title_text_size_selected)) / ((float) res.getDimensionPixelSize(R.dimen.menu_row_title_text_size_deselected));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = (TextView) findViewById(R.id.title);
        this.mContentsView = findViewById(getContentsViewId());
        if (this.mContentsView.isFocusable()) {
            this.mContentsView.setOnFocusChangeListener(this.mOnFocusChangeListener);
        }
        if (this.mContentsView instanceof ViewGroup) {
            setOnFocusChangeListenerToChildren((ViewGroup) this.mContentsView);
        }
        this.mContentsView.setVisibility(4);
    }

    private void setOnFocusChangeListenerToChildren(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child.isFocusable()) {
                child.setOnFocusChangeListener(this.mOnFocusChangeListener);
            }
            if (child instanceof ViewGroup) {
                setOnFocusChangeListenerToChildren((ViewGroup) child);
            }
        }
    }

    public final TextView getTitleView() {
        return this.mTitleView;
    }

    public final View getContentsView() {
        return this.mContentsView;
    }

    public void initialize(int reason) {
        this.mLastFocusView = null;
    }

    /* access modifiers changed from: protected */
    public Menu getMenu() {
        if (this.mRow == null) {
            return null;
        }
        return this.mRow.getMenu();
    }

    public void onBind(MenuRow row) {
        this.mRow = row;
        this.mTitleView.setText(row.getTitle());
    }

    public void updateLanguage() {
        if (this.mRow != null) {
            this.mTitleView.setText(this.mRow.getTitle());
        }
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return getInitialFocusView().requestFocus();
    }

    @NonNull
    private View getInitialFocusView() {
        if (this.mLastFocusView == null) {
            return this.mContentsView;
        }
        return this.mLastFocusView;
    }

    /* access modifiers changed from: protected */
    public void setInitialFocusView(@NonNull View v) {
        this.mLastFocusView = v;
    }

    /* access modifiers changed from: protected */
    public void onChildFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            this.mLastFocusView = v;
        }
        Menu menu = getMenu();
        if (menu != null) {
            menu.scheduleHide();
        }
    }

    public String getRowId() {
        if (this.mRow == null) {
            return null;
        }
        return this.mRow.getId();
    }

    public void onSelected(boolean showTitle) {
        if (!this.mRow.hideTitleWhenSelected() || showTitle) {
            this.mTitleView.setVisibility(0);
            this.mTitleView.setAlpha(1.0f);
            this.mTitleView.setScaleX(this.mTitleViewScaleSelected);
            this.mTitleView.setScaleY(this.mTitleViewScaleSelected);
        } else {
            this.mTitleView.setVisibility(4);
        }
        View lastFocusView = this.mLastFocusView;
        this.mContentsView.setVisibility(0);
        this.mLastFocusView = lastFocusView;
    }

    public void onDeselected() {
        this.mTitleView.setVisibility(0);
        this.mTitleView.setAlpha(this.mTitleViewAlphaDeselected);
        this.mTitleView.setScaleX(1.0f);
        this.mTitleView.setScaleY(1.0f);
        this.mContentsView.setVisibility(8);
    }

    public int getPreferredContentsHeight() {
        return this.mRow.getHeight();
    }
}
