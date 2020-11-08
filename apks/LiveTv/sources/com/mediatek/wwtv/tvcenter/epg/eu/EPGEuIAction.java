package com.mediatek.wwtv.tvcenter.epg.eu;

import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;

public interface EPGEuIAction {
    void checkPWDShow();

    void clearActiveWindow();

    void getChannelList();

    void getProgramListByChId(EPGChannelInfo ePGChannelInfo, int i, int i2);

    int getTimeType12_24();

    boolean is3rdTVSource();

    boolean isCountryUK();

    boolean isCurrentSourceATV();

    void refreshDetailsInfo(EPGProgramInfo ePGProgramInfo, int i);

    void setActiveWindow(EPGChannelInfo ePGChannelInfo, int i, int i2);
}
