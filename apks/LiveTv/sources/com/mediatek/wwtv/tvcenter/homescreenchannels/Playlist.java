package com.mediatek.wwtv.tvcenter.homescreenchannels;

import java.util.List;

public final class Playlist {
    private long mChannelId;
    private boolean mChannelPublished;
    private List<Clip> mClips;
    private final String mDescription = "playlist description";
    private final int mLogoResId;
    private final String mName;
    private final String mPlaylistId;
    private final String mTitle = "Playlist Title";
    private final String mVideoUri = "empty";

    Playlist(String name, List<Clip> clip, String playlistId, int logo) {
        this.mName = name;
        this.mLogoResId = logo;
        this.mClips = clip;
        this.mPlaylistId = playlistId;
    }

    public String getName() {
        return this.mName;
    }

    /* access modifiers changed from: package-private */
    public List<Clip> getClips() {
        return this.mClips;
    }

    public String getDescription() {
        return this.mDescription;
    }

    /* access modifiers changed from: package-private */
    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public boolean isChannelPublished() {
        return this.mChannelPublished;
    }

    public void setChannelPublished(boolean channelPublished) {
        this.mChannelPublished = channelPublished;
    }

    /* access modifiers changed from: package-private */
    public void setChannelPublishedId(long id) {
        this.mChannelPublished = true;
        this.mChannelId = id;
    }

    /* access modifiers changed from: package-private */
    public long getChannelId() {
        return this.mChannelId;
    }

    /* access modifiers changed from: package-private */
    public int getLogoId() {
        return this.mLogoResId;
    }

    public String toString() {
        return "Playlist { mName = '" + this.mName + "' mDescription = '" + this.mDescription + "' mVideoUri = '" + this.mVideoUri + "' mLogoResId = '" + this.mLogoResId + "' mTitle = '" + this.mTitle + "' mList = '" + this.mClips + "' mId = '" + this.mPlaylistId + "' mChannelPublished" + this.mChannelPublished + "'";
    }
}
