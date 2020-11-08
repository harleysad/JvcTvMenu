package com.mediatek.twoworlds.tv.model;

public class MtkTvEventSeriesBase {
    private int episodeNum = 0;
    private int expireDate = 0;
    private boolean expireDateValidFlag = false;
    private int lastEpisodeNum = 0;
    private short programPattern = 0;
    private short repeatLabel = 0;
    private int seriesId = 0;
    private String seriesName = "";

    public int getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(int seriesId2) {
        this.seriesId = seriesId2;
    }

    public short getRepeatLabel() {
        return this.repeatLabel;
    }

    public void setRepeatLabel(short repeatLabel2) {
        this.repeatLabel = repeatLabel2;
    }

    public short getProgramPatternl() {
        return this.programPattern;
    }

    public void setProgramPattern(short programPattern2) {
        this.programPattern = programPattern2;
    }

    public boolean isExpireDateValidFlag() {
        return this.expireDateValidFlag;
    }

    public void setExpireDateValidFlag(boolean expireDateValidFlag2) {
        this.expireDateValidFlag = expireDateValidFlag2;
    }

    public int getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(int expireDate2) {
        this.expireDate = expireDate2;
    }

    public int getEpisodeNum() {
        return this.episodeNum;
    }

    public void setEpisodeNum(int episodeNum2) {
        this.episodeNum = episodeNum2;
    }

    public int getLastEpisodeNum() {
        return this.lastEpisodeNum;
    }

    public void setLastEpisodeNum(int lastEpisodeNum2) {
        this.lastEpisodeNum = lastEpisodeNum2;
    }

    public String getSeriesName() {
        return this.seriesName;
    }

    public void setSeriesName(String seriesName2) {
        this.seriesName = seriesName2;
    }

    public String toString() {
        return "MtkTvEventSeries: seriesId=" + this.seriesId + ", repeatLabel=" + this.repeatLabel + ", programPattern=" + this.programPattern + ", expireDateValidFlag=" + this.expireDateValidFlag + ", expireDate=" + this.expireDate + ", episodeNum=" + this.episodeNum + ", lastEpisodeNum=" + this.lastEpisodeNum + ", seriesName=" + this.seriesName;
    }
}
