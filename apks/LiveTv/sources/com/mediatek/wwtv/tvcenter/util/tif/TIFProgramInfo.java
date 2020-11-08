package com.mediatek.wwtv.tvcenter.util.tif;

public class TIFProgramInfo {
    public String mAudioLanguage;
    public String mBroadcastGenre;
    public String mCanonicalGenre;
    public long mChannelId;
    public String mData;
    public String mDescription;
    public long mEndTimeUtcSec;
    public int mEpisodeNumber;
    public String mEpisodeTitle;
    public int mEventId;
    public String mLongDescription;
    public String mPosterArtUri;
    public String mRating;
    public int mSeasonNumber;
    public long mStartTimeUtcSec;
    public String mThumbnailUri;
    public String mTitle;
    public int mVideoHeight;
    public int mVideoWidth;

    public String toString() {
        return "[TIFProgramInfo] mChannelId:" + this.mChannelId + ",  mEventId:" + this.mEventId + ",  mTitle:" + this.mTitle + ",  mSeasonNumber:" + this.mSeasonNumber + ",  mEpisodeNumber:" + this.mEpisodeNumber + ",  mEpisodeTitle:" + this.mEpisodeTitle + ",  mStartTimeUtcSec:" + this.mStartTimeUtcSec + ",  mEndTimeUtcSec:" + this.mEndTimeUtcSec + ",  mBroadcastGenre:" + this.mBroadcastGenre + ",  mCanonicalGenre:" + this.mCanonicalGenre + ",  mDescription:" + this.mDescription + ",  mLongDescription:" + this.mLongDescription + ",  mVideoWidth:" + this.mVideoWidth + ",  mVideoHeight:" + this.mVideoHeight + ",  mAudioLanguage:" + this.mAudioLanguage + ",  mRating:" + this.mRating + ",  mPosterArtUri:" + this.mPosterArtUri + ",  mThumbnailUri:" + this.mThumbnailUri + ",  mData:" + this.mData;
    }

    public void copyFrom(TIFProgramInfo other) {
        if (this != other) {
            this.mChannelId = other.mChannelId;
            this.mTitle = other.mTitle;
            this.mEpisodeTitle = other.mEpisodeTitle;
            this.mSeasonNumber = other.mSeasonNumber;
            this.mEpisodeNumber = other.mEpisodeNumber;
            this.mStartTimeUtcSec = other.mStartTimeUtcSec;
            this.mEndTimeUtcSec = other.mEndTimeUtcSec;
            this.mDescription = other.mDescription;
            this.mVideoWidth = other.mVideoWidth;
            this.mVideoHeight = other.mVideoHeight;
            this.mPosterArtUri = other.mPosterArtUri;
            this.mThumbnailUri = other.mThumbnailUri;
            this.mRating = other.mRating;
        }
    }

    public long getmChannelId() {
        return this.mChannelId;
    }

    public void setmChannelId(long mChannelId2) {
        this.mChannelId = mChannelId2;
    }

    public int getmEventId() {
        return this.mEventId;
    }

    public void setmEventId(int mEventId2) {
        this.mEventId = mEventId2;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle2) {
        this.mTitle = mTitle2;
    }

    public int getmSeasonNumber() {
        return this.mSeasonNumber;
    }

    public void setmSeasonNumber(int mSeasonNumber2) {
        this.mSeasonNumber = mSeasonNumber2;
    }

    public int getmEpisodeNumber() {
        return this.mEpisodeNumber;
    }

    public void setmEpisodeNumber(int mEpisodeNumber2) {
        this.mEpisodeNumber = mEpisodeNumber2;
    }

    public String getmEpisodeTitle() {
        return this.mEpisodeTitle;
    }

    public void setmEpisodeTitle(String mEpisodeTitle2) {
        this.mEpisodeTitle = mEpisodeTitle2;
    }

    public long getmStartTimeUtcSec() {
        return this.mStartTimeUtcSec;
    }

    public void setmStartTimeUtcSec(long mStartTimeUtcSec2) {
        this.mStartTimeUtcSec = mStartTimeUtcSec2;
    }

    public long getmEndTimeUtcSec() {
        return this.mEndTimeUtcSec;
    }

    public void setmEndTimeUtcSec(long mEndTimeUtcSec2) {
        this.mEndTimeUtcSec = mEndTimeUtcSec2;
    }

    public String getmBroadcastGenre() {
        return this.mBroadcastGenre;
    }

    public void setmBroadcastGenre(String mBroadcastGenre2) {
        this.mBroadcastGenre = mBroadcastGenre2;
    }

    public String getmCanonicalGenre() {
        return this.mCanonicalGenre;
    }

    public void setmCanonicalGenre(String mCanonicalGenre2) {
        this.mCanonicalGenre = mCanonicalGenre2;
    }

    public String getmDescription() {
        return this.mDescription;
    }

    public void setmDescription(String mDescription2) {
        this.mDescription = mDescription2;
    }

    public String getmLongDescription() {
        return this.mLongDescription;
    }

    public void setmLongDescription(String mLongDescription2) {
        this.mLongDescription = mLongDescription2;
    }

    public int getmVideoWidth() {
        return this.mVideoWidth;
    }

    public void setmVideoWidth(int mVideoWidth2) {
        this.mVideoWidth = mVideoWidth2;
    }

    public int getmVideoHeight() {
        return this.mVideoHeight;
    }

    public void setmVideoHeight(int mVideoHeight2) {
        this.mVideoHeight = mVideoHeight2;
    }

    public String getmAudioLanguage() {
        return this.mAudioLanguage;
    }

    public void setmAudioLanguage(String mAudioLanguage2) {
        this.mAudioLanguage = mAudioLanguage2;
    }

    public String getmRating() {
        return this.mRating;
    }

    public void setmRating(String mRating2) {
        this.mRating = mRating2;
    }

    public String getmPosterArtUri() {
        return this.mPosterArtUri;
    }

    public void setmPosterArtUri(String mPosterArtUri2) {
        this.mPosterArtUri = mPosterArtUri2;
    }

    public String getmThumbnailUri() {
        return this.mThumbnailUri;
    }

    public void setmThumbnailUri(String mThumbnailUri2) {
        this.mThumbnailUri = mThumbnailUri2;
    }

    public String getmData() {
        return this.mData;
    }

    public void setmData(String mData2) {
        this.mData = mData2;
    }

    public static final class Builder {
        private final TIFProgramInfo mProgramInfo = new TIFProgramInfo();

        public Builder() {
            this.mProgramInfo.mChannelId = -1;
            this.mProgramInfo.mTitle = "title";
            this.mProgramInfo.mSeasonNumber = -1;
            this.mProgramInfo.mEpisodeNumber = -1;
            this.mProgramInfo.mStartTimeUtcSec = -1;
            this.mProgramInfo.mEndTimeUtcSec = -1;
            this.mProgramInfo.mDescription = "description";
        }

        public Builder setmChannelId(long mChannelId) {
            this.mProgramInfo.mChannelId = mChannelId;
            return this;
        }

        public Builder setmEventId(int mEventId) {
            this.mProgramInfo.mEventId = mEventId;
            return this;
        }

        public Builder setmTitle(String mTitle) {
            this.mProgramInfo.mTitle = mTitle;
            return this;
        }

        public Builder setmSeasonNumber(int mSeasonNumber) {
            this.mProgramInfo.mSeasonNumber = mSeasonNumber;
            return this;
        }

        public Builder setmEpisodeNumber(int mEpisodeNumber) {
            this.mProgramInfo.mEpisodeNumber = mEpisodeNumber;
            return this;
        }

        public Builder setmEpisodeTitle(String mEpisodeTitle) {
            this.mProgramInfo.mEpisodeTitle = mEpisodeTitle;
            return this;
        }

        public Builder setmStartTimeUtcSec(long mStartTimeUtcSec) {
            this.mProgramInfo.mStartTimeUtcSec = mStartTimeUtcSec;
            return this;
        }

        public Builder setmEndTimeUtcSec(long mEndTimeUtcSec) {
            this.mProgramInfo.mEndTimeUtcSec = mEndTimeUtcSec;
            return this;
        }

        public Builder setmBroadcastGenre(String mBroadcastGenre) {
            this.mProgramInfo.mBroadcastGenre = mBroadcastGenre;
            return this;
        }

        public Builder setmCanonicalGenre(String mCanonicalGenre) {
            this.mProgramInfo.mCanonicalGenre = mCanonicalGenre;
            return this;
        }

        public Builder setmDescription(String mDescription) {
            this.mProgramInfo.mDescription = mDescription;
            return this;
        }

        public Builder setmLongDescription(String mLongDescription) {
            this.mProgramInfo.mLongDescription = mLongDescription;
            return this;
        }

        public Builder setmVideoWidth(int mVideoWidth) {
            this.mProgramInfo.mVideoWidth = mVideoWidth;
            return this;
        }

        public Builder setmVideoHeight(int mVideoHeight) {
            this.mProgramInfo.mVideoHeight = mVideoHeight;
            return this;
        }

        public Builder setmAudioLanguage(String mAudioLanguage) {
            this.mProgramInfo.mAudioLanguage = mAudioLanguage;
            return this;
        }

        public Builder setmRating(String mRating) {
            this.mProgramInfo.mRating = mRating;
            return this;
        }

        public Builder setmPosterArtUri(String mPosterArtUri) {
            this.mProgramInfo.mPosterArtUri = mPosterArtUri;
            return this;
        }

        public Builder setmThumbnailUri(String mThumbnailUri) {
            this.mProgramInfo.mThumbnailUri = mThumbnailUri;
            return this;
        }

        public Builder setmData(String mData) {
            this.mProgramInfo.mData = mData;
            return this;
        }

        public TIFProgramInfo build() {
            TIFProgramInfo program = new TIFProgramInfo();
            program.copyFrom(this.mProgramInfo);
            return program;
        }
    }
}
