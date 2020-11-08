package com.mediatek.wwtv.tvcenter.epg;

import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Date;

public class EPGProgramInfo {
    private static final int NULL_VALUE = -1;
    private static final String TAG = "EPGProgramInfo";
    private String appendDescription;
    private int[] categoryType;
    private int channelId;
    private String describe;
    private boolean hasSubTitle;
    private boolean isDrawBgHiL;
    private boolean isDrawLeftIcon;
    private boolean isDrawRightIcon;
    private boolean mCancel;
    private long mEndTime;
    private String mEndTimeStr;
    private float mLeftMargin;
    public String mLongDescription;
    private boolean mProgramBlock;
    private String mProgramType;
    private String mRatingType;
    private int mRatingValue;
    private float mScale;
    private long mStartTime;
    private String mStartTimeStr;
    private String mTitle;
    private int mainType;
    private int programId;
    private int subType;

    public EPGProgramInfo() {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
    }

    public EPGProgramInfo(int channelId2, int programId2, long mStartTime2, long mEndTime2, String mTitle2, String mRatingType2) {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
        this.channelId = channelId2;
        this.programId = programId2;
        this.mStartTime = mStartTime2;
        this.mEndTime = mEndTime2;
        this.mTitle = mTitle2;
        this.mRatingType = mRatingType2;
    }

    public EPGProgramInfo(Long mStartTime2, Long mEndTime2, String mTitle2) {
        this(mStartTime2, mEndTime2, mTitle2, "", -1, -1, false);
    }

    public EPGProgramInfo(Long mStartTime2, Long mEndTime2, String mTitle2, String describe2, int mainType2, int subType2, boolean mCancel2) {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
        this.mStartTime = mStartTime2.longValue();
        this.mEndTime = mEndTime2.longValue();
        this.mTitle = mTitle2;
        this.describe = describe2;
        this.mainType = mainType2;
        this.subType = subType2;
        this.mCancel = mCancel2;
    }

    public EPGProgramInfo(String mStartTimeStr2, String mEndTimeStr2, String mTitle2, String describe2, int mainType2, int subType2, boolean mCancel2, float mScale2) {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
        this.mStartTimeStr = mStartTimeStr2;
        this.mEndTimeStr = mEndTimeStr2;
        this.mTitle = mTitle2;
        this.describe = describe2;
        this.mainType = mainType2;
        this.subType = subType2;
        this.mCancel = mCancel2;
        this.mScale = mScale2;
    }

    public EPGProgramInfo(String mStartTimeStr2, String mEndTimeStr2, String mTitle2) {
        this(mStartTimeStr2, mEndTimeStr2, mTitle2, "", -1, -1, false, 0.0f);
    }

    public void setProgramBlock(boolean block) {
        this.mProgramBlock = block;
    }

    public boolean isProgramBlock() {
        return this.mProgramBlock;
    }

    public void setChannelId(int id) {
        this.channelId = id;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setProgramId(int id) {
        this.programId = id;
    }

    public int getProgramId() {
        return this.programId;
    }

    public boolean isDrawLeftIcon() {
        return this.isDrawLeftIcon;
    }

    public void setDrawLeftIcon(boolean isDrawLeftIcon2) {
        this.isDrawLeftIcon = isDrawLeftIcon2;
    }

    public boolean isDrawRightwardIcon() {
        return this.isDrawRightIcon;
    }

    public void setDrawRightIcon(boolean isDrawRightIcon2) {
        this.isDrawRightIcon = isDrawRightIcon2;
    }

    public void setBgHighLigth(boolean isHighligth) {
        this.isDrawBgHiL = isHighligth;
    }

    public boolean isBgHighLight() {
        return this.isDrawBgHiL;
    }

    public String getmStartTimeStr() {
        return this.mStartTimeStr;
    }

    public void setmStartTimeStr(String mStartTimeStr2) {
        this.mStartTimeStr = mStartTimeStr2;
    }

    public String getmEndTimeStr() {
        return this.mEndTimeStr;
    }

    public void setmEndTimeStr(String mEndTimeStr2) {
        this.mEndTimeStr = mEndTimeStr2;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle2) {
        this.mTitle = mTitle2;
    }

    public String getDescribe() {
        MtkLog.d(TAG, "EPG event detail: " + this.describe);
        return this.describe;
    }

    public void setDescribe(String describe2) {
        this.describe = describe2;
    }

    public int getMainType() {
        return this.mainType;
    }

    public void setMainType(int mainType2) {
        this.mainType = mainType2;
    }

    public int getSubType() {
        return this.subType;
    }

    public void setSubType(int subType2) {
        this.subType = subType2;
    }

    public boolean ismCancel() {
        return this.mCancel;
    }

    public void setmCancel(boolean mCancel2) {
        this.mCancel = mCancel2;
    }

    public boolean isHasSubTitle() {
        return this.hasSubTitle;
    }

    public void setHasSubTitle(boolean mHasSubTilte) {
        this.hasSubTitle = mHasSubTilte;
    }

    public float getmScale() {
        return this.mScale;
    }

    public void setmScale(float mScale2) {
        this.mScale = mScale2;
    }

    public Long getmStartTime() {
        return Long.valueOf(this.mStartTime);
    }

    public float getLeftMargin() {
        return this.mLeftMargin;
    }

    public void setLeftMargin(float mLeftMargin2) {
        this.mLeftMargin = mLeftMargin2;
    }

    public void setmStartTime(Long mStartTime2) {
        this.mStartTime = mStartTime2.longValue();
    }

    public Long getmEndTime() {
        return Long.valueOf(this.mEndTime);
    }

    public void setmEndTime(Long mEndTime2) {
        this.mEndTime = mEndTime2.longValue();
    }

    public void setCategoryType(int[] category) {
        this.categoryType = category;
    }

    public int[] getCategoryType() {
        return this.categoryType;
    }

    public void setRatingType(String ratingType) {
        this.mRatingType = ratingType;
    }

    public String getRatingType() {
        return this.mRatingType == null ? "" : this.mRatingType;
    }

    public int getRatingValue() {
        return this.mRatingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.mRatingValue = ratingValue;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getProgramType() {
        return this.mProgramType;
    }

    public void setProgramType(String programType) {
        this.mProgramType = programType;
    }

    public String getLongDescription() {
        return this.mLongDescription;
    }

    public void setLongDescription(String longDescription) {
        this.mLongDescription = longDescription;
    }

    public String getAppendDescription() {
        return this.appendDescription;
    }

    public void setAppendDescription(String appendDescription2) {
        this.appendDescription = appendDescription2;
    }

    public int countTimeZoom(int hours, int timeZoomSpan) {
        if (hours % timeZoomSpan == 0) {
            return (hours / timeZoomSpan) - 1;
        }
        return hours / timeZoomSpan;
    }

    public float dateToMinutes(Date time, int mTimeZoomSpan, boolean flag) {
        int timeZoom = countTimeZoom(time.getHours(), mTimeZoomSpan);
        if (flag) {
            return (((float) ((time.getHours() - (timeZoom * mTimeZoomSpan)) - 1)) * 60.0f) + ((float) time.getMinutes());
        }
        return (((float) ((((timeZoom + 1) * mTimeZoomSpan) + 1) - time.getHours())) * 60.0f) - ((float) time.getMinutes());
    }
}
