package com.mediatek.wwtv.tvcenter.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class TvBlockView extends View {
    public static final int BLOCK_3RD_CHANNEL_INPUT_BLOCK = 2;
    public static final int BLOCK_BY_EVENT = 3;
    public static final int BLOCK_COMMON = 1;
    public static final int BLOCK_EMPTY = 0;
    public static final int BLOCK_PVR_LAST_MEMORY_BLOCK = 4;
    private static final String TAG = "TvBlockView";
    private int mBlockStatus;

    public TvBlockView(Context context) {
        super(context);
        this.mBlockStatus = 0;
    }

    public TvBlockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvBlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBlockStatus = 0;
    }

    public void setVisibility(int visibility) {
        setVisibility(visibility, 1);
    }

    public void setVisibility(int visibility, int bitMask) {
        if (visibility == 8) {
            this.mBlockStatus &= ~bitMask;
            if (this.mBlockStatus == 0) {
                super.setVisibility(visibility);
            }
        } else {
            this.mBlockStatus |= bitMask;
            super.setVisibility(visibility);
        }
        MtkLog.d(TAG, "mBlockStatus=" + this.mBlockStatus);
    }

    public boolean isBlock() {
        return this.mBlockStatus != 0;
    }
}
