package com.android.tv.settings.connectivity.setup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.tv.settings.R;

public class GuidanceRelativeLayout extends RelativeLayout {
    private float mTitleKeylinePercent;

    public GuidanceRelativeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public GuidanceRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuidanceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
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
        TextView titleView = (TextView) getRootView().findViewById(R.id.guidance_title);
        TextView descriptionView = (TextView) getRootView().findViewById(R.id.guidance_description);
        int mTitleKeylinePixels = (int) ((((float) getMeasuredHeight()) * this.mTitleKeylinePercent) / 100.0f);
        if (titleView != null && titleView.getParent() == this) {
            int guidanceTextContainerTop2 = mTitleKeylinePixels - ((RelativeLayout.LayoutParams) titleView.getLayoutParams()).topMargin;
            if (titleView.getLineCount() > 1) {
                guidanceTextContainerTop = guidanceTextContainerTop2 - titleView.getLayout().getLineBaseline(1);
            } else {
                guidanceTextContainerTop = guidanceTextContainerTop2 - titleView.getLayout().getLineBaseline(0);
            }
            int offset = guidanceTextContainerTop;
            titleView.offsetTopAndBottom(offset);
            if (descriptionView != null && descriptionView.getParent() == this) {
                descriptionView.offsetTopAndBottom(offset);
            }
        }
    }
}
