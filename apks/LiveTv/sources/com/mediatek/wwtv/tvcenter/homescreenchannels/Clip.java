package com.mediatek.wwtv.tvcenter.homescreenchannels;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.URI;
import java.net.URISyntaxException;

public class Clip implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Clip createFromParcel(Parcel in) {
            return new Clip(in);
        }

        public Clip[] newArray(int size) {
            return new Clip[size];
        }
    };
    private int mAspectRatio;
    private final String mBgImageUrl;
    private final String mCardImageUrl;
    private final String mCategory;
    private long mClipId;
    private final String mContentId;
    private final String mDescription;
    private final String mPreviewVideoUrl;
    private long mProgramId;
    private final String mTitle;
    private final String mVideoUrl;
    private int mViewCount;

    Clip(String title, String description, String bgImageUrl, String cardImageUrl, String videoUrl, String previewVideoUrl, String category, long clipId, long programId, String contentId, int aspectRatio) {
        this.mProgramId = 0;
        this.mClipId = clipId;
        this.mProgramId = programId;
        this.mContentId = contentId;
        this.mTitle = title;
        this.mDescription = description;
        this.mBgImageUrl = bgImageUrl;
        this.mCardImageUrl = cardImageUrl;
        this.mVideoUrl = videoUrl;
        this.mPreviewVideoUrl = previewVideoUrl;
        this.mCategory = category;
        this.mAspectRatio = aspectRatio;
    }

    private Clip(Parcel in) {
        this.mProgramId = 0;
        this.mClipId = in.readLong();
        this.mContentId = in.readString();
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mBgImageUrl = in.readString();
        this.mCardImageUrl = in.readString();
        this.mVideoUrl = in.readString();
        this.mPreviewVideoUrl = in.readString();
        this.mCategory = in.readString();
        this.mProgramId = in.readLong();
        this.mViewCount = in.readInt();
    }

    /* access modifiers changed from: package-private */
    public long getProgramId() {
        return this.mProgramId;
    }

    /* access modifiers changed from: package-private */
    public void setProgramId(long programId) {
        this.mProgramId = programId;
    }

    public long getClipId() {
        return this.mClipId;
    }

    /* access modifiers changed from: package-private */
    public void setClipId(long clipId) {
        this.mClipId = clipId;
    }

    public String getContentId() {
        return this.mContentId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    /* access modifiers changed from: package-private */
    public String getVideoUrl() {
        return this.mVideoUrl;
    }

    /* access modifiers changed from: package-private */
    public String getPreviewVideoUrl() {
        return this.mPreviewVideoUrl;
    }

    /* access modifiers changed from: package-private */
    public String getBackgroundImageUrl() {
        return this.mBgImageUrl;
    }

    public String getCardImageUrl() {
        return this.mCardImageUrl;
    }

    /* access modifiers changed from: package-private */
    public URI getBackgroundImageURI() {
        try {
            return new URI(this.mBgImageUrl);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public int incrementViewCount() {
        int i = this.mViewCount + 1;
        this.mViewCount = i;
        return i;
    }

    /* access modifiers changed from: package-private */
    public void setViewCount(int viewCount) {
        this.mViewCount = viewCount;
    }

    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mClipId);
        dest.writeString(this.mContentId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeString(this.mBgImageUrl);
        dest.writeString(this.mCardImageUrl);
        dest.writeString(this.mVideoUrl);
        dest.writeString(this.mPreviewVideoUrl);
        dest.writeString(this.mCategory);
        dest.writeLong(this.mProgramId);
        dest.writeInt(this.mViewCount);
    }

    public String toString() {
        URI uri = getBackgroundImageURI();
        StringBuilder sb = new StringBuilder();
        sb.append("Clip{clipId=");
        sb.append(this.mClipId);
        sb.append(", contentId='");
        sb.append(this.mContentId);
        sb.append('\'');
        sb.append(", title='");
        sb.append(this.mTitle);
        sb.append('\'');
        sb.append(", videoUrl='");
        sb.append(this.mVideoUrl);
        sb.append('\'');
        sb.append(", backgroundImageUrl='");
        sb.append(this.mBgImageUrl);
        sb.append('\'');
        sb.append(", backgroundImageURI='");
        sb.append(uri == null ? "null" : uri.toString());
        sb.append('\'');
        sb.append(", cardImageUrl='");
        sb.append(this.mCardImageUrl);
        sb.append('\'');
        sb.append(", aspectRatio='");
        sb.append(this.mAspectRatio);
        sb.append('\'');
        sb.append(", programId='");
        sb.append(this.mProgramId);
        sb.append('\'');
        sb.append(", viewCount='");
        sb.append(this.mViewCount);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }
}
