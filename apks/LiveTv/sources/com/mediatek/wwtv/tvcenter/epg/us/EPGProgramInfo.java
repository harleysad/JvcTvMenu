package com.mediatek.wwtv.tvcenter.epg.us;

import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Date;

public class EPGProgramInfo {
    private static final int NULL_VALUE = -1;
    private static final String TAG = "EPGProgramInfo";
    private String describe;
    private boolean hasSubTitle;
    private boolean isDrawLeftIcon;
    private boolean isDrawRightIcon;
    private boolean mCancel;
    private Date mEndTime;
    private String mEndTimeStr;
    private float mLeftMargin;
    private float mScale;
    private Date mStartTime;
    private String mStartTimeStr;
    private String mTitle;
    private int mainType;
    private int subType;

    public EPGProgramInfo() {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
    }

    public EPGProgramInfo(Date mStartTime2, Date mEndTime2, String mTitle2) {
        this(mStartTime2, mEndTime2, mTitle2, "", -1, -1, false);
    }

    public EPGProgramInfo(Date mStartTime2, Date mEndTime2, String mTitle2, String describe2, int mainType2, int subType2, boolean mCancel2) {
        this.mCancel = false;
        this.hasSubTitle = false;
        this.isDrawRightIcon = false;
        this.isDrawLeftIcon = false;
        this.mStartTime = mStartTime2;
        this.mEndTime = mEndTime2;
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

    public Date getmStartTime() {
        return this.mStartTime;
    }

    public float getLeftMargin() {
        return this.mLeftMargin;
    }

    public void setLeftMargin(float mLeftMargin2) {
        this.mLeftMargin = mLeftMargin2;
    }

    public void setmStartTime(Date mStartTime2) {
        this.mStartTime = mStartTime2;
    }

    public Date getmEndTime() {
        return this.mEndTime;
    }

    public void setmEndTime(Date mEndTime2) {
        this.mEndTime = mEndTime2;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
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
