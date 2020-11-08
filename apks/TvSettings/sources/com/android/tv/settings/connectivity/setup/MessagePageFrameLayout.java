package com.android.tv.settings.connectivity.setup;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessagePageFrameLayout extends FrameLayout {
    private float mTitleKeylinePercent;

    public MessagePageFrameLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public MessagePageFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessagePageFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTitleKeylinePercent = getKeyLinePercent(context);
    }

    private static float getKeyLinePercent(Context context) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
        float percent = ta.getFloat(45, 40.0f);
        ta.recycle();
        return percent;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int guidanceTextContainerTop;
        super.onLayout(changed, l, t, r, b);
        TextView mStatusView = (TextView) getRootView().findViewById(com.android.tv.settings.R.id.status_text);
        View mContentView = getRootView().findViewById(com.android.tv.settings.R.id.message_content);
        int mTitleKeylinePixels = (int) ((((float) getMeasuredHeight()) * this.mTitleKeylinePercent) / 100.0f);
        if (mStatusView != null) {
            int guidanceTextContainerTop2 = mTitleKeylinePixels - ((LinearLayout.LayoutParams) mStatusView.getLayoutParams()).topMargin;
            if (mStatusView.getLineCount() > 1) {
                guidanceTextContainerTop = guidanceTextContainerTop2 - mStatusView.getLayout().getLineBaseline(1);
            } else {
                guidanceTextContainerTop = guidanceTextContainerTop2 - mStatusView.getLayout().getLineBaseline(0);
            }
            mContentView.offsetTopAndBottom(guidanceTextContainerTop);
        }
    }
}
