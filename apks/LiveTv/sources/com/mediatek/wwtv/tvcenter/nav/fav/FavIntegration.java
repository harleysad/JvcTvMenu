package com.mediatek.wwtv.tvcenter.nav.fav;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import java.util.ArrayList;
import java.util.List;

public class FavIntegration {
    public static Context mContext;
    public static FavIntegration mFavIntegration;
    private final int MAX_FAV_SIZE = 7;

    public static FavIntegration getInstance(Context context) {
        if (mFavIntegration == null) {
            mFavIntegration = new FavIntegration();
        }
        mContext = context;
        return mFavIntegration;
    }

    public boolean enterKeyPress(int select, FavoriteListListener listener) {
        if (getSelectChannel(select).getChannelID() == -1) {
            clearChannel(getCurrentChannel());
            addCurrentChannel(select, listener);
            return false;
        } else if (!getCurrentChannel().equals(getSelectChannel(select))) {
            changeChannel(getSelectChannel(select));
            return true;
        } else {
            removeCurrentChannel(listener);
            return false;
        }
    }

    public void favKeyPress(int select, FavoriteListListener listener) {
        TVChannel channel = getCurrentChannel();
        if (getSelectChannel(select).getChannelID() == -1) {
            clearChannel(channel);
            addCurrentChannel(select, listener);
        } else if (!channel.equals(getSelectChannel(select))) {
            clearChannel(channel);
            addCurrentChannel(select, listener);
        } else {
            removeCurrentChannel(listener);
        }
    }

    private void addCurrentChannel(int select, FavoriteListListener listener) {
        MtkTvChannelList.getInstance().addFavoritelistChannelByIndex(select);
        MtkTvChannelList.getInstance().storeFavoritelistChannel();
        listener.updateFavoriteList();
    }

    private void clearChannel(TVChannel currentChannel) {
        MtkTvChannelList.getInstance().removeFavoritelistChannel(getRawFavList().indexOf(currentChannel));
        MtkTvChannelList.getInstance().storeFavoritelistChannel();
    }

    private void removeCurrentChannel(FavoriteListListener listener) {
        NavIntegration.getInstance(mContext).removeFavItemFromList(getCurrentChannel(), listener);
        MtkTvChannelList.getInstance().storeFavoritelistChannel();
    }

    private void changeChannel(TVChannel selectChannel) {
        NavIntegration.getInstance(mContext).selectChannel(selectChannel);
    }

    private TVChannel getCurrentChannel() {
        return NavIntegration.getInstance(mContext).iGetCurrentChannel();
    }

    private TVChannel getSelectChannel(int select) {
        return getRawFavList().get(select);
    }

    private List<TVChannel> getRawFavList() {
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
