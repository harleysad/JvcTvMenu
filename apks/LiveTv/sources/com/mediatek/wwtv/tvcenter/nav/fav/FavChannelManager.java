package com.mediatek.wwtv.tvcenter.nav.fav;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class FavChannelManager {
    private static final String FAVOURITE_TYPE = "favouriteType";
    private static final String SPNAME = "CHMODE";
    private static final String TAG = "FavChannelManager";
    private static CommonIntegration commonIntegration = null;
    private static FavChannelManager favChannelManager = null;
    private int CURRENT_FAVOURITE_TYPE = 0;
    private final int WRITE_CHANNEL_FLASH = 1;
    private final int[] favMask = {MtkTvChCommonBase.SB_VNET_FAVORITE1, MtkTvChCommonBase.SB_VNET_FAVORITE2, MtkTvChCommonBase.SB_VNET_FAVORITE3, MtkTvChCommonBase.SB_VNET_FAVORITE4};
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            removeMessages(1);
            if (msg.what == 1) {
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
            }
        }
    };
    private SaveValue mSaveValue;
    private int saveFlashDealyTime = 10000;

    private FavChannelManager(Context context) {
        commonIntegration = CommonIntegration.getInstance();
        this.mSaveValue = SaveValue.getInstance(context);
        this.CURRENT_FAVOURITE_TYPE = this.mSaveValue.readValue(FAVOURITE_TYPE, 0);
        this.saveFlashDealyTime = context.getResources().getInteger(R.integer.nav_favorite_list_saveflash_delay_time);
    }

    public static FavChannelManager getInstance(Context context) {
        if (favChannelManager == null) {
            favChannelManager = new FavChannelManager(context);
        }
        return favChannelManager;
    }

    public void setFavoriteType(int currentFavoriteType) {
        this.CURRENT_FAVOURITE_TYPE = currentFavoriteType;
    }

    public void favAddOrErase(FavoriteListListener favoriteListListener) {
        MtkTvChannelInfoBase currentChannel = commonIntegration.getCurChInfo();
        if (currentChannel != null) {
            int nwMask = currentChannel.getNwMask();
            MtkLog.d(TAG, "nwMask before = " + nwMask);
            if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) == 0) {
                nwMask |= this.favMask[this.CURRENT_FAVOURITE_TYPE];
            } else if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) > 0) {
                nwMask &= ~this.favMask[this.CURRENT_FAVOURITE_TYPE];
            }
            MtkLog.d(TAG, "nwMask after = " + nwMask);
            currentChannel.setNwMask(nwMask);
            List<MtkTvChannelInfoBase> chList = new ArrayList<>();
            chList.add(currentChannel);
            CommonIntegration.getInstance().setChannelList(1, chList);
            favoriteListListener.updateFavoriteList();
            this.handler.sendEmptyMessageDelayed(1, (long) this.saveFlashDealyTime);
            return;
        }
        MtkLog.d(TAG, "currentChannel is null");
    }

    public void favAddOrErase() {
        MtkTvChannelInfoBase currentChannel = commonIntegration.getCurChInfo();
        if (currentChannel != null) {
            int nwMask = currentChannel.getNwMask();
            MtkLog.d(TAG, "nwMask before = " + nwMask);
            if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) == 0) {
                nwMask |= this.favMask[this.CURRENT_FAVOURITE_TYPE];
            } else if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) > 0) {
                nwMask &= ~this.favMask[this.CURRENT_FAVOURITE_TYPE];
            }
            MtkLog.d(TAG, "nwMask after = " + nwMask);
            currentChannel.setNwMask(nwMask);
            List<MtkTvChannelInfoBase> chList = new ArrayList<>();
            chList.add(currentChannel);
            CommonIntegration.getInstance().setChannelList(1, chList);
            this.handler.sendEmptyMessageDelayed(1, (long) this.saveFlashDealyTime);
            return;
        }
        MtkLog.d(TAG, "currentChannel is null");
    }

    public boolean isFavChannel() {
        MtkTvChannelInfoBase currentChannel = commonIntegration.getCurChInfo();
        if (currentChannel != null) {
            int nwMask = currentChannel.getNwMask();
            MtkLog.d(TAG, "nwMask before = " + nwMask);
            if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) == 0) {
                int nwMask2 = nwMask | this.favMask[this.CURRENT_FAVOURITE_TYPE];
                return false;
            } else if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) > 0) {
                int i = (~this.favMask[this.CURRENT_FAVOURITE_TYPE]) & nwMask;
                return true;
            }
        }
        return false;
    }

    public void deleteFavorite(MtkTvChannelInfoBase selectChannel, FavoriteListListener favoriteListListener) {
        if (selectChannel != null) {
            int nwMask = selectChannel.getNwMask();
            MtkLog.d(TAG, "nwMask before = " + nwMask);
            if ((this.favMask[this.CURRENT_FAVOURITE_TYPE] & nwMask) > 0) {
                nwMask &= ~this.favMask[this.CURRENT_FAVOURITE_TYPE];
            }
            MtkLog.d(TAG, "nwMask after = " + nwMask);
            selectChannel.setNwMask(nwMask);
            List<MtkTvChannelInfoBase> chList = new ArrayList<>();
            chList.add(selectChannel);
            CommonIntegration.getInstance().setChannelList(1, chList);
            favoriteListListener.updateFavoriteList();
            this.handler.sendEmptyMessageDelayed(1, (long) this.saveFlashDealyTime);
            return;
        }
        MtkLog.d(TAG, "selectChannel is null");
    }

    public boolean changeChannelToNextFav() {
        MtkLog.d(TAG, "changeChannelToNextFav");
        if (!commonIntegration.isCurrentSourceTv()) {
            MtkLog.d(TAG, "changeChannelToNextFav,not tv sourece!");
            return false;
        }
        int totalCount = commonIntegration.getChannelActiveNumByAPI();
        int preNum = commonIntegration.getFavouriteChannelCount(this.favMask[this.CURRENT_FAVOURITE_TYPE]);
        if (totalCount <= 0 || preNum <= 0) {
            MtkLog.d(TAG, "totalCount = " + totalCount + " preNum = " + preNum);
            return false;
        }
        MtkTvChannelInfoBase currentChannel = commonIntegration.getCurChInfo();
        List<MtkTvChannelInfoBase> mfavChannelList = commonIntegration.getFavoriteListByFilter(this.favMask[this.CURRENT_FAVOURITE_TYPE], 0, 0, preNum);
        int index = mfavChannelList.indexOf(currentChannel);
        if (index < 0) {
            return commonIntegration.selectChannelByInfo(mfavChannelList.get(0));
        }
        if (preNum == 1) {
            return false;
        }
        if (index == preNum - 1) {
            return commonIntegration.selectChannelByInfo(mfavChannelList.get(0));
        }
        if (index < preNum - 1) {
            return commonIntegration.selectChannelByInfo(mfavChannelList.get(index + 1));
        }
        return false;
    }
}
