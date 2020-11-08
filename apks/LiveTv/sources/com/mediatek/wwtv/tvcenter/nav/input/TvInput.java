package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvInputInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;

public final class TvInput extends AbstractInput {
    private static final String TAG = "TvInput";
    private final boolean mHidden = MarketRegionInfo.isFunctionSupport(39);

    public TvInput() {
        super((TvInputInfo) null, 0);
    }

    public String getId() {
        return TAG;
    }

    public String getCustomSourceName(Context context) {
        return getSourceName(context);
    }

    public String getSourceName(Context context) {
        if (context != null) {
            return context.getResources().getString(R.string.menu_tab_tv);
        }
        return "TV";
    }

    public TvInputInfo getTvInputInfo() {
        return null;
    }

    public boolean isHidden(Context context) {
        return this.mHidden || InputUtil.mTvInputManager.getTvInputInfo(DtvInput.DEFAULT_ID) == null;
    }

    public String toString(Context context) {
        return "TV Id:" + getHardwareId() + ",TvInputInfo=" + getTvInputInfo() + ", State=" + getState() + ", isHidden=" + isHidden(context) + ", CustomSourceName=" + getCustomSourceName(context) + ", SourceName=" + getSourceName(context) + ", isBlock=" + isBlock();
    }
}
