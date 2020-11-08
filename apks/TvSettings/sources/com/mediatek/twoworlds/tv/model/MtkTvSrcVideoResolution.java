package com.mediatek.twoworlds.tv.model;

public class MtkTvSrcVideoResolution {
    public static final int TIMING_TYPE_GRAPHIC = 2;
    public static final int TIMING_TYPE_NOT_SUPPORT = 3;
    public static final int TIMING_TYPE_UNKNOWN = 0;
    public static final int TIMING_TYPE_VIDEO = 1;
    private int mFrameRate = 0;
    private int mHeight = 0;
    private boolean mIsProgressive = false;
    private int mTimingType = 0;
    private int mWidth = 0;

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setWidth(int mWidth2) {
        this.mWidth = mWidth2;
    }

    public void setHeight(int mHeight2) {
        this.mHeight = mHeight2;
    }

    public int getTimingType() {
        return this.mTimingType;
    }

    public void setTimingType(int mTimingType2) {
        this.mTimingType = mTimingType2;
    }

    public int getFrameRate() {
        return this.mFrameRate;
    }

    public void setFrameRate(int mFrameRate2) {
        this.mFrameRate = mFrameRate2;
    }

    public boolean getIsProgressive() {
        return this.mIsProgressive;
    }

    public void setIsProgressive(boolean mIsProgressive2) {
        this.mIsProgressive = mIsProgressive2;
    }

    public String toString() {
        return "MtkTvSrcVideoResolution  mWidth=" + this.mWidth + " , mHeight=" + this.mHeight + " , mTimingType=" + this.mTimingType + " , mIsProgressive=" + this.mIsProgressive + "\n";
    }
}
