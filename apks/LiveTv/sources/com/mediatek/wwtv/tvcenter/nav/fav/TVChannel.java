package com.mediatek.wwtv.tvcenter.nav.fav;

public class TVChannel {
    private int channelID = -1;
    private String channelName = "";
    private String channelNum = "";
    private boolean favorite = false;
    private int frequence = -1;

    public void setChannelNum(String str) {
        this.channelNum = str;
    }

    public void setChannelName(String str) {
        this.channelName = str;
    }

    public String getChannelNum() {
        return this.channelNum;
    }

    public CharSequence getChannelName() {
        return this.channelName;
    }

    public int getFreq() {
        return this.frequence;
    }

    public void setFreq(int freq) {
        this.frequence = freq;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.favorite = isFavorite;
    }

    public void flush() {
    }

    public boolean equals(Object o) {
        if (!(o instanceof TVChannel) || ((TVChannel) o).getChannelID() != getChannelID()) {
            return false;
        }
        return true;
    }

    public int getChannelID() {
        return this.channelID;
    }

    public void setChannelID(int channelID2) {
        this.channelID = channelID2;
    }

    public void dumpChannelInfo() {
    }
}
