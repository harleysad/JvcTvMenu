package com.mediatek.wwtv.tvcenter.epg.eu;

import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import java.util.ArrayList;
import java.util.List;

public interface EPGEuIView {
    void dismissLoading();

    void showLoading();

    void updateChannelList(ArrayList<EPGChannelInfo> arrayList, int i, int i2);

    void updateEventDetails(EPGProgramInfo ePGProgramInfo);

    void updateLockStatus(boolean z);

    void updateProgramList(List<EPGProgramInfo> list);
}
