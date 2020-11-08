package com.android.tv.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.tv.menu.ItemListRowView;
import com.mediatek.wwtv.tvcenter.R;

public class ActionCardView extends RelativeLayout implements ItemListRowView.CardView<MenuAction> {
    private static final float OPACITY_DISABLED = 0.3f;
    private static final float OPACITY_ENABLED = 1.0f;
    private static final String TAG = MenuView.TAG;
    private ImageView mIconView;
    private TextView mLabelView;
    private TextView mStateView;

    public ActionCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ActionCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = (ImageView) findViewById(R.id.action_card_icon);
        this.mLabelView = (TextView) findViewById(R.id.action_card_label);
        this.mStateView = (TextView) findViewById(R.id.action_card_state);
    }

    public void onBind(MenuAction action, boolean selected) {
        String str = TAG;
        Log.d(str, "onBind: action=" + action.getActionName(getContext()));
        this.mIconView.setImageDrawable(action.getDrawable(getContext()));
        this.mLabelView.setText(action.getActionName(getContext()));
        this.mStateView.setText(action.getActionDescription());
        if (action.isEnabled()) {
            setEnabled(true);
            setFocusable(true);
            this.mIconView.setAlpha(1.0f);
            this.mLabelView.setAlpha(1.0f);
            this.mStateView.setAlpha(1.0f);
            return;
        }
        setEnabled(false);
        setFocusable(false);
        this.mIconView.setAlpha(OPACITY_DISABLED);
        this.mLabelView.setAlpha(OPACITY_DISABLED);
        this.mStateView.setAlpha(OPACITY_DISABLED);
    }

    public void onSelected() {
        String str = TAG;
        Log.d(str, "onSelected: action=" + this.mLabelView.getText());
        this.mLabelView.setSelected(true);
    }

    public void onDeselected() {
        String str = TAG;
        Log.d(str, "onDeselected: action=" + this.mLabelView.getText());
        this.mLabelView.setSelected(false);
    }

    public void onRecycled() {
    }
}
