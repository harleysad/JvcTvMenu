package com.mediatek.wwtv.tvcenter.dvr.db;

public class RecordedProgramInfo {
    public String mAudioLanguage;
    public String mBroadcastGenre;
    public String mCanonicalGenre;
    public long mChannelId;
    public String mContentRating;
    public String mDetailInfo;
    public long mEndTimeUtcMills;
    public String mEpisodeDisplayNumber;
    public String mEpisodeTitle;
    public long mId;
    public String mInputId;
    public byte[] mInternalData;
    public int mInternalFlag1;
    public int mInternalFlag2;
    public int mInternalFlag3;
    public int mInternalFlag4;
    public String mLongDescription;
    public String mPosterArtUri;
    public long mRecordingDataBytes;
    public String mRecordingDataUri;
    public int mRecordingDurationMills;
    public long mRecordingExpireTimeUtcMills;
    public boolean mSearchable;
    public String mSeasonDisplayNumber;
    public String mSeasonTitle;
    public String mShortDescription;
    public long mStartTimeUtcMills;
    public String mThumbnallUri;
    public String mTitle;
    public int mVersionNumber;
    public int mVideoHeight;
    public int mVideoWidth;

    public String toString() {
        return "mInputId:" + this.mInputId + ",mChannelId:" + this.mChannelId + ",mTitle:" + this.mTitle + ",mSeasonDisplayNumber:" + this.mSeasonDisplayNumber + ",mSeasonTitle:" + this.mSeasonTitle + ",mEpisodeDisplayNumber:" + this.mEpisodeDisplayNumber + ",mEpisodeTitle:" + this.mEpisodeTitle + ",mStartTimeUtcMills:" + this.mStartTimeUtcMills + ",mEndTimeUtcMills:" + this.mEndTimeUtcMills + ",mRecordingDurationMills:" + this.mRecordingDurationMills + ",mRecordingExpireTimeUtcMills:" + this.mRecordingExpireTimeUtcMills + ",mVersionNumber:" + this.mVersionNumber + "mDetailInfo" + this.mDetailInfo;
    }
}
