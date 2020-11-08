package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.IntegrationZoom;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.ZoomTipView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class TVLogicManager {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_PIP = 1;
    public static final int MODE_POP = 2;
    private static TVLogicManager instance;
    private final MtkTvBroadcast mChannelBroadcast = MtkTvBroadcast.getInstance();
    private final DvrManager mTopManager;
    private final MtkTvChannelList mtkChannelManager = MtkTvChannelList.getInstance();

    public TVLogicManager(Context context, DvrManager topManager) {
        this.mTopManager = topManager;
    }

    public void changeDTVSource() {
        MtkLog.d("changeDTVSource", "------- change input source to dtv --------- ");
    }

    public static TVLogicManager getInstance(Context context, DvrManager topManager) {
        if (instance == null) {
            instance = new TVLogicManager(context, topManager);
        }
        return instance;
    }

    public static TVLogicManager getInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    public int getCurrentMode() {
        return 0;
    }

    public boolean isScanning() {
        return false;
    }

    public boolean isPipPopMode() {
        return CommonIntegration.getInstance().isPipOrPopState();
    }

    public boolean isDTV() {
        return false;
    }

    public boolean isCurrentChannelDTV() {
        return false;
    }

    public boolean dtvNotReadyForRecord() {
        return !isDTV() || !hasVideo() || !dtvHasSignal() || !hasDTVSignal() || dtvIsScrambled() || dtvIsScrambled();
    }

    public List<MtkTvChannelInfoBase> getChannels() {
        return CommonIntegration.getInstance().getChannelList(CommonIntegration.getInstance().getCurrentChannelId(), 7, 7, MtkTvChCommonBase.SB_VNET_ALL);
    }

    public List<MtkTvChannelInfoBase> getChannelList() {
        return CommonIntegration.getInstance().getChList(0, 0, CommonIntegration.getInstance().getChannelAllNumByAPI());
    }

    public String getChannelNumber(MtkTvChannelInfoBase channel) {
        MtkTvChannelInfoBase mCurrentChannel = channel;
        if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
            MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
            return tmpAtsc.getMajorNum() + "." + tmpAtsc.getMinorNum();
        } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
            MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
            return tmpIsdb.getMajorNum() + "." + tmpIsdb.getMinorNum();
        } else if (!(mCurrentChannel instanceof MtkTvAnalogChannelInfo) || MarketRegionInfo.getCurrentMarketRegion() != 2) {
            return "" + mCurrentChannel.getChannelNumber();
        } else {
            return "" + mCurrentChannel.getChannelNumber();
        }
    }

    public MtkTvChannelInfoBase getCurrentChannel() {
        return CommonIntegration.getInstance().getCurChInfo();
    }

    public String getChannelName() {
        return getCurrentChannel().getServiceName();
    }

    public int getChannelNumInt() {
        return getCurrentChannel().getChannelNumber();
    }

    public String getChannelNumStr() {
        return "CH" + getChannelNumInt();
    }

    private boolean dtvIsScrambled() {
        return isAudioScrambled() || isVideoScrambled();
    }

    private boolean dtvHasSignal() {
        return false;
    }

    private String getScrambleState() {
        return null;
    }

    private boolean isAudioScrambled() {
        String scrambleState = getScrambleState();
        return false;
    }

    private boolean isVideoScrambled() {
        String scrambleState = getScrambleState();
        return false;
    }

    public boolean hasDTVSignal() {
        return false;
    }

    private boolean hasVideo() {
        return true;
    }

    public boolean prepareScheduleTask(MtkTvBookingBase item) {
        if (item == null) {
            return false;
        }
        try {
            changeDTVSource((String) null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void changeDTVSource(String src) {
        CommonIntegration.getInstance().iSetSourcetoTv();
    }

    public void resumeTV() {
    }

    public void selectChannel(int keyCode) {
        if (keyCode == 167) {
            CommonIntegration.getInstance().channelDown();
        } else {
            CommonIntegration.getInstance().channelUp();
        }
    }

    public void selectChannelByNum(int channelNum) {
        this.mChannelBroadcast.channelSelectByChannelNumber(channelNum);
    }

    public void removeZoomTips() {
        ((ZoomTipView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_ZOOM_PAN)).setVisibility(8);
    }

    public void removeTwinkView() {
        ((TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)).setVisibility(8);
    }

    public void reSetZoomValues(Context mContext) {
        IntegrationZoom.getInstance(mContext).setZoomMode(1);
        removeZoomTips();
    }

    public void removeChannelList(Context mContext) {
        ChannelListDialog mChannelDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChannelDialog != null && mChannelDialog.isShowing()) {
            mChannelDialog.dismiss();
        }
    }
}
