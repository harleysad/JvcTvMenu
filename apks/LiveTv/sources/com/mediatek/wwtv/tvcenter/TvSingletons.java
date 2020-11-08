package com.mediatek.wwtv.tvcenter;

import com.android.tv.parental.ContentRatingsManager;
import com.android.tv.parental.ParentalControlSettings;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.util.concurrent.Executor;

public interface TvSingletons {
    TIFChannelManager getChannelDataManager();

    CommonIntegration getCommonIntegration();

    ContentRatingsManager getContentRatingsManager();

    Executor getDbExecutor();

    InputSourceManager getInputSourceManager();

    ParentalControlSettings getParentalControlSettings();

    TIFProgramManager getProgramDataManager();

    TvInputManagerHelper getTvInputManagerHelper();

    static TvSingletons getSingletons() {
        return DestroyApp.getSingletons();
    }
}
