package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvInputInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;

public final class AtvInput extends AbstractInput {
    private static final String DEFAULT_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW1";
    private static final String TAG = "AtvInput";
    private final boolean mHidden = (!MarketRegionInfo.isFunctionSupport(39));

    public AtvInput() {
        super((TvInputInfo) null, 10000);
    }

    public String getId() {
        return TAG;
    }

    public String getCustomSourceName(Context context) {
        return getSourceName(context);
    }

    public String getSourceName(Context context) {
        if (context != null) {
            return context.getResources().getString(R.string.nav_source_atv);
        }
        return "ATV";
    }

    public TvInputInfo getTvInputInfo() {
        return null;
    }

    public boolean isHidden(Context context) {
        return this.mHidden || InputUtil.mTvInputManager.getTvInputInfo(DEFAULT_ID) == null;
    }

    public String toString(Context context) {
        return "ATV Id:" + getHardwareId() + ",TvInputInfo=" + getTvInputInfo() + ", State=" + getState() + ", isHidden=" + isHidden(context) + ", CustomSourceName=" + getCustomSourceName(context) + ", SourceName=" + getSourceName(context) + ", isBlock=" + isBlock();
    }
}
