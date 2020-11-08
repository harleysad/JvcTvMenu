package com.mediatek.wwtv.tvcenter.nav.fav;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class NavIntegration {
    public static final int CHANNEL_DOWN = 1;
    public static final int CHANNEL_PRE = 2;
    public static final int CHANNEL_UP = 0;
    public static final int SUBTITTLE_LEN = 3;
    private static final int _MAX_FAV_SIZE = 7;
    public static NavIntegration mNavIntegration;

    public NavIntegration(Context context) {
    }

    public static NavIntegration getInstance(Context context) {
        if (mNavIntegration == null) {
            mNavIntegration = new NavIntegration(context);
        }
        return mNavIntegration;
    }

    public boolean isCurrentSourceTv() {
        String srcName = MtkTvInputSource.getInstance().getCurrentInputSourceName();
        if (srcName == null) {
            return false;
        }
        if (srcName.equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_TV) || srcName.equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_ATV) || srcName.equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_DTV)) {
            return true;
        }
        return false;
    }

    public int getChannelLength() {
        return CommonIntegration.getInstance().getChannelActiveNumByAPI();
    }

    public TVChannel iGetCurrentChannel() {
        TVChannel channel = new TVChannel();
        MtkTvChannelInfoBase currentChannel = CommonIntegration.getInstance().getChannelById(CommonIntegration.getInstance().getCurrentChannelId());
        if (currentChannel != null) {
            try {
                channel.setChannelNum(String.valueOf(currentChannel.getChannelNumber()));
                channel.setChannelID(currentChannel.getChannelId());
                channel.setFreq(currentChannel.getFrequency());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        if (getFavoriteList().contains(channel)) {
            channel.setFavorite(true);
        } else {
            channel.setFavorite(false);
        }
        return channel;
    }

    public void isSetChannelFavorite(Object iGetCurrentChannel) {
        if (!CommonIntegration.getInstance().isFavListFull()) {
            MtkTvChannelList.getInstance().addFavoritelistChannel();
        }
    }

    public List<TVChannel> getFavoriteList() {
        List<TVChannel> favChannelList = new ArrayList<>();
        List<MtkTvFavoritelistInfoBase> list = MtkTvChannelList.getInstance().getFavoritelistByFilter();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TVChannel channel = new TVChannel();
                if (list.get(i).getChannelId() != -1) {
                    try {
                        channel.setChannelName(list.get(i).getChannelName());
                        channel.setChannelNum(list.get(i).getChannelNumber());
                        channel.setChannelID(list.get(i).getChannelId());
                        MtkTvChannelInfoBase mtkTvChannelInfo = CommonIntegration.getInstance().getChannelById(list.get(i).getChannelId());
                        if (mtkTvChannelInfo != null) {
                            channel.setFreq(mtkTvChannelInfo.getFrequency());
                        }
                        channel.setFavorite(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                favChannelList.add(channel);
            }
        }
        return favChannelList;
    }

    public int getFavoriteChannelsCount() {
        int count = 0;
        for (MtkTvFavoritelistInfoBase simpleChannel : MtkTvChannelList.getInstance().getFavoritelistByFilter()) {
            if (simpleChannel.getChannelId() != -1) {
                count++;
            }
        }
        return count;
    }

    public boolean changeChannelToNextFav() {
        MtkLog.printStackTrace();
        if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
            return false;
        }
        int totalCount = getChannelLength();
        List<TVChannel> mfavChannelList = mNavIntegration.getFavoriteList();
        TVChannel currentChannel = iGetCurrentChannel();
        if (getFavoriteChannelsCount() <= 0 || ((getFavoriteChannelsCount() == 1 && getFavoriteList().indexOf(currentChannel) != -1) || totalCount <= 0)) {
            return false;
        }
        int index = getFavoriteList().indexOf(currentChannel);
        if (index == -1) {
            for (int i = 0; i < mfavChannelList.size(); i++) {
                if (compareChannum(mfavChannelList.get(i), currentChannel)) {
                    selectChannel(mfavChannelList.get(i));
                    return true;
                }
            }
            selectChannel(mfavChannelList.get(0));
            return true;
        }
        int index2 = index + 1;
        if (index2 >= mfavChannelList.size()) {
            index2 = 0;
        }
        int index3 = index2;
        int count = 0;
        while (mfavChannelList.get(index3).getChannelID() == -1) {
            index3 = (index3 + 1) % 7;
            count++;
            if (count >= 7) {
                return false;
            }
        }
        selectChannel(mfavChannelList.get(index3));
        return true;
    }

    private boolean compareChannum(TVChannel facChannel, TVChannel currentChannel) {
        String favChannelNum = facChannel.getChannelNum();
        String currentChannum = currentChannel.getChannelNum();
        if (favChannelNum == null || favChannelNum.equalsIgnoreCase("null") || favChannelNum.replaceAll("[*_-]", ".").replaceAll("[*_-]", ".").compareTo(currentChannum.replaceAll("[*_-]", ".").replaceAll("[*_-]", ".")) <= 0) {
            return false;
        }
        return true;
    }

    public void selectFavChanUp() {
        selectFavChanUp(true);
    }

    public void selectFavChanUp(boolean favListOnScreen) {
        List<TVChannel> favList = getFavoriteList();
        if (favList.size() != 0) {
            int index = favList.indexOf(iGetCurrentChannel());
            if (index >= 0 && index < favList.size() - 1) {
                selectChannel(favList.get(index + 1));
            } else if (index != favList.size() - 1) {
                int num = Integer.valueOf(iGetCurrentChannel().getChannelNum()).intValue();
                for (int i = 0; i < favList.size(); i++) {
                    if (num < Integer.valueOf(favList.get(i).getChannelNum()).intValue()) {
                        selectChannel(favList.get(i));
                        return;
                    }
                }
                selectChannel(favList.get(0));
            }
        }
    }

    public void selectFavChanDown() {
        List<TVChannel> favList = getFavoriteList();
        int index = favList.indexOf(iGetCurrentChannel()) - 1;
        if (index >= 0 && index <= favList.size() - 1) {
            selectChannel(favList.get(index));
        }
    }

    public void setChannel(int channelPre) {
        setSourcetoTv();
    }

    private void setSourcetoTv() {
        CommonIntegration.getInstance().iSetSourcetoTv();
    }

    public void selectChannel(TVChannel selectedChannel) {
        if (CommonIntegration.getInstance().isCurrentSourceTv()) {
            new MtkTvChannelInfo().setChannelId(selectedChannel.getChannelID());
            CommonIntegration.getInstance().selectChannelById(selectedChannel.getChannelID());
        }
    }

    public void setChannelFavorite(FavoriteListListener listener, TVChannel currentChannel) {
        MtkLog.printStackTrace();
        if (currentChannel != null && listener != null) {
            if (currentChannel.isFavorite()) {
                currentChannel.setFavorite(false);
                removeFavItemFromList(currentChannel, listener);
            } else if (!CommonIntegration.getInstance().isFavListFull()) {
                MtkTvChannelList.getInstance().addFavoritelistChannel();
                currentChannel.setFavorite(true);
            }
        }
    }

    public void addChannelFavorite(FavoriteListListener listener, TVChannel currentChannel, int index) {
        int originalIndex = getRawFavList().indexOf(currentChannel);
        if (originalIndex != -1 && originalIndex != index) {
            cleanFavChannel(originalIndex);
            MtkTvChannelList.getInstance().addFavoritelistChannelByIndex(index);
        }
    }

    public void addChannelFavorite(FavoriteListListener listener, TVChannel currentChannel) {
        List<TVChannel> channels = getRawFavList();
        if (getRawFavList().indexOf(currentChannel) == -1) {
            for (int i = 0; i < 7; i++) {
                if (channels.get(i).getChannelID() == -1) {
                    MtkTvChannelList.getInstance().addFavoritelistChannelByIndex(i);
                    return;
                }
            }
        }
    }

    public void removeFavItemFromList(TVChannel currentChannel, FavoriteListListener listener) {
        if (getFavoriteList().indexOf(currentChannel) != -1) {
            List<MtkTvFavoritelistInfoBase> list = MtkTvChannelList.getInstance().getFavoritelistByFilter();
            int i = 0;
            while (true) {
                if (i >= list.size()) {
                    break;
                } else if (list.get(i).getChannelId() == currentChannel.getChannelID()) {
                    MtkTvChannelList.getInstance().removeFavoritelistChannel(i);
                    break;
                } else {
                    i++;
                }
            }
        }
        listener.updateFavoriteList();
    }

    public void moveUP(TVChannel currentChannel, FavoriteListListener listener) {
        int index = getFavoriteList().indexOf(currentChannel);
        if (index > 0) {
            swapFavlist(index, index - 1);
            listener.updateFavoriteList();
        }
    }

    public void moveDown(TVChannel currentChannel, FavoriteListListener listener) {
        List<TVChannel> favChannels = getFavoriteList();
        int index = favChannels.indexOf(currentChannel);
        if (index < favChannels.size() - 1) {
            swapFavlist(index, index + 1);
            listener.updateFavoriteList();
        }
    }

    private void swapFavlist(int index, int index2) {
        int indexMin = Math.min(index, index2);
        int indexMax = Math.max(index, index2);
        List<TVChannel> favList = getFavoriteList();
        TVChannel channeMin = favList.get(indexMin);
        TVChannel channeMax = favList.get(indexMax);
        List<MtkTvFavoritelistInfoBase> list = MtkTvChannelList.getInstance().getFavoritelistByFilter();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getChannelId() == channeMin.getChannelID()) {
                index = i;
            }
            if (list.get(i).getChannelId() == channeMax.getChannelID()) {
                index2 = i;
            }
        }
        MtkTvChannelList.getInstance().swapFavoritelistByIndex(index, index2);
    }

    public List<TVChannel> getChannelList() {
        List<MtkTvChannelInfoBase> channelBaseList = CommonIntegration.getInstance().getChannelList(0, 0, CommonIntegration.getInstance().getChannelActiveNumByAPI(), MtkTvChCommonBase.SB_VNET_ALL);
        List<TVChannel> favChannelList = new ArrayList<>();
        for (MtkTvChannelInfoBase channel : channelBaseList) {
            TVChannel favChannel = new TVChannel();
            try {
                favChannel.setChannelNum(String.valueOf(channel.getChannelNumber()));
                favChannel.setChannelID(channel.getChannelId());
                favChannel.setFreq(channel.getFrequency());
                favChannelList.add(favChannel);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return favChannelList;
    }

    public boolean dispatchKeyToTimeshift() {
        if (MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode") != 0) {
            return true;
        }
        if (!CommonIntegration.getInstance().isCurrentSourceATV() && MtkTvConfig.getInstance().getConfigValue("g_record__rec_tshift_mode") != 0) {
            return true;
        }
        return false;
    }

    private boolean cleanFavChannel(int index) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public List<TVChannel> getRawFavList() {
        List<TVChannel> favChannelList = new ArrayList<>();
        List<MtkTvFavoritelistInfoBase> list = MtkTvChannelList.getInstance().getFavoritelistByFilter();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TVChannel channel = new TVChannel();
                if (list.get(i).getChannelId() == -1) {
                    channel.setChannelID(-1);
                } else {
                    try {
                        channel.setChannelName(list.get(i).getChannelName());
                        channel.setChannelNum(list.get(i).getChannelNumber());
                        channel.setChannelID(list.get(i).getChannelId());
                        channel.setFavorite(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                favChannelList.add(channel);
            }
        }
        while (favChannelList.size() < 7) {
            TVChannel channel2 = new TVChannel();
            channel2.setChannelID(-1);
            favChannelList.add(channel2);
        }
        return favChannelList;
    }
}
