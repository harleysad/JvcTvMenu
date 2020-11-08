package com.mediatek.wwtv.tvcenter.epg.eu;

import android.os.Bundle;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class EPGEuHelper {
    private static final String TAG = "EPGEuHelper";
    private final Bundle mBundle = new Bundle();
    private MtkTvChannelInfoBase mCurChannel;

    public MtkTvChannelInfoBase getCurChannel() {
        return this.mCurChannel;
    }

    public void setCurChannel(MtkTvChannelInfoBase curChannel) {
        this.mCurChannel = curChannel;
    }

    public void selectCHWithEnterEPG() {
        CommonIntegration.getInstance().setTurnCHAfterExitEPG(false);
        this.mCurChannel = CommonIntegration.getInstance().getCurChInfo();
        if (CommonIntegration.getInstance().isBarkChannel(this.mCurChannel)) {
            setBarkerChannel(true);
            int channelId = CommonIntegration.getInstance().getCurrentChannelId();
            MtkLog.d(TAG, "selectCHWithEnterEPG>>>channelId=" + channelId);
            CommonIntegration.getInstance().selectChannelById(channelId);
        }
    }

    public void selectCHAfterExitEPG() {
        int currentChannelId = CommonIntegration.getInstance().getCurrentChannelId();
        if (CommonIntegration.getInstance().isBarkChannel(this.mCurChannel)) {
            CommonIntegration.getInstance().setTurnCHAfterExitEPG(true);
            setBarkerChannel(false);
            CommonIntegration.getInstance().selectChannelById(this.mCurChannel.getChannelId());
        }
    }

    private void setBarkerChannel(boolean isBarkerChannel) {
        if (isBarkerChannel) {
            this.mBundle.putByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL, (byte) 1);
        } else {
            this.mBundle.putByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL, (byte) 0);
        }
        TvSurfaceView tvView = TurnkeyUiMainActivity.getInstance().getTvView();
        if (tvView != null) {
            tvView.sendAppPrivateCommand(MtkTvTISMsgBase.MTK_TIS_MSG_CHANNEL, this.mBundle);
        }
    }

    public void turnChannel(EPGChannelInfo changeChannel) {
        this.mCurChannel = changeChannel.getTVChannel();
        boolean isBarkerChannel = CommonIntegration.getInstance().isBarkChannel(this.mCurChannel);
        MtkLog.d(TAG, "isBarkerChannel=" + isBarkerChannel);
        setBarkerChannel(isBarkerChannel);
        CommonIntegration.getInstance().selectChannelById((int) changeChannel.mId);
    }
}
