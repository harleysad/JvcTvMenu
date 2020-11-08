package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvInputInfo;
import android.media.tv.TvView;
import android.net.Uri;

public interface ISource {
    public static final String GTAG = "ISource^";
    public static final int TYPE_ATV = 10000;
    public static final int TYPE_COMPONENT = 1004;
    public static final int TYPE_COMPOSITE = 1001;
    public static final int TYPE_DISPLAY_PORT = 1008;
    public static final int TYPE_DTV = 20000;
    public static final int TYPE_DVI = 1006;
    public static final int TYPE_HDMI = 1007;
    public static final int TYPE_OTHER = 1000;
    public static final int TYPE_SCART = 1003;
    public static final int TYPE_SVIDEO = 1002;
    public static final int TYPE_TV = 0;
    public static final int TYPE_VGA = 1005;

    int block(boolean z);

    boolean getConflict(ISource iSource);

    String getCustomSourceName(Context context);

    int getHardwareId();

    String getId();

    String getSourceName(Context context);

    int getState();

    TvInputInfo getTvInputInfo();

    TvInputInfo getTvInputInfo(long j);

    int getType();

    boolean isBlock();

    boolean isBlockEx();

    boolean isCurrentBlock();

    boolean isHidden(Context context);

    int tune(TvView tvView);

    int tune(TvView tvView, String str, Uri uri);

    void updateState(int i);
}
