package com.android.tv.settings.suggestions;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.tv.settings.R;

public class SuggestionItemView extends LinearLayout {
    private View mContainer;
    /* access modifiers changed from: private */
    public View mDissmissButton;
    /* access modifiers changed from: private */
    public View mIcon;
    /* access modifiers changed from: private */
    public View mItemContainer;

    public SuggestionItemView(Context context) {
        super(context);
    }

    public SuggestionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDissmissButton = findViewById(R.id.dismiss_button);
        this.mContainer = findViewById(R.id.main_container);
        this.mIcon = findViewById(16908294);
        this.mItemContainer = findViewById(R.id.item_container);
        int translateX = getResources().getDimensionPixelSize(R.dimen.suggestion_item_change_focus_translate_x);
        if (getResources().getConfiguration().getLayoutDirection() == 1) {
            translateX = -translateX;
        }
        final ObjectAnimator containerSlideOut = ObjectAnimator.ofFloat(this.mContainer, View.TRANSLATION_X, new float[]{0.0f, (float) translateX});
        final ObjectAnimator containerSlideIn = ObjectAnimator.ofFloat(this.mContainer, View.TRANSLATION_X, new float[]{(float) translateX, 0.0f});
        this.mDissmissButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    containerSlideOut.start();
                    SuggestionItemView.this.mDissmissButton.setAlpha(1.0f);
                    SuggestionItemView.this.mIcon.setAlpha(0.3f);
                    SuggestionItemView.this.mItemContainer.setAlpha(0.3f);
                    return;
                }
                containerSlideIn.start();
                SuggestionItemView.this.mDissmissButton.setAlpha(0.4f);
                SuggestionItemView.this.mIcon.setAlpha(1.0f);
                SuggestionItemView.this.mItemContainer.setAlpha(1.0f);
            }
        });
    }

    public View focusSearch(View focused, int direction) {
        boolean z = true;
        if (getResources().getConfiguration().getLayoutDirection() != 1) {
            z = false;
        }
        boolean isRTL = z;
        if (focused.getId() == R.id.dismiss_button && ((isRTL && direction == 66) || (!isRTL && direction == 17))) {
            return this.mContainer;
        }
        if (focused.getId() != R.id.main_container || ((!isRTL || direction != 17) && (isRTL || direction != 66))) {
            return super.focusSearch(focused, direction);
        }
        return this.mDissmissButton;
    }
}
