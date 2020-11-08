package com.mediatek.twoworlds.tv.model;

import java.util.Arrays;

public class MtkTvEventInfoBase {
    public static int MAX_AUDIO_COMPONENT_INFO = 8;
    public static int MAX_COMPONENT_INFO = 8;
    public static int MAX_EVENT_GROUP_INFO = 1;
    public static int MAX_EVENT_LINKAGE_INFO = 4;
    public static int MAX_EVENT_SERIES_INFO = 1;
    private int[] caSystemId = new int[4];
    private boolean caption;
    private int channelId;
    private long duration;
    private MtkTvEventAudioComponentBase[] eventAudioComponent = new MtkTvEventAudioComponentBase[MAX_AUDIO_COMPONENT_INFO];
    private int[] eventCategory = new int[8];
    private int eventCategoryNum;
    private MtkTvEventComponentDescriptorBase[] eventComponent = new MtkTvEventComponentDescriptorBase[MAX_COMPONENT_INFO];
    private String eventDetail;
    private String eventDetail_extended;
    private MtkTvEventGroupBase[] eventGroup = new MtkTvEventGroupBase[MAX_EVENT_GROUP_INFO];
    private int eventId;
    private MtkTvEventLinkageBase[] eventLinkage = new MtkTvEventLinkageBase[MAX_EVENT_LINKAGE_INFO];
    private String eventRating;
    private int eventRatingType;
    private MtkTvEventSeriesBase[] eventSeries = new MtkTvEventSeriesBase[MAX_EVENT_SERIES_INFO];
    private String eventTitle;
    private boolean freeCaMode;
    private int guidanceMode;
    private String guidanceText;
    private long startTime;
    private int svlId;

    public MtkTvEventInfoBase() {
        for (int i = 0; i < MAX_EVENT_LINKAGE_INFO; i++) {
            this.eventLinkage[i] = new MtkTvEventLinkageBase();
        }
        for (int i2 = 0; i2 < MAX_EVENT_GROUP_INFO; i2++) {
            this.eventGroup[i2] = new MtkTvEventGroupBase();
        }
        for (int i3 = 0; i3 < MAX_EVENT_SERIES_INFO; i3++) {
            this.eventSeries[i3] = new MtkTvEventSeriesBase();
        }
        for (int i4 = 0; i4 < MAX_COMPONENT_INFO; i4++) {
            this.eventComponent[i4] = new MtkTvEventComponentDescriptorBase();
        }
        for (int i5 = 0; i5 < MAX_AUDIO_COMPONENT_INFO; i5++) {
            this.eventAudioComponent[i5] = new MtkTvEventAudioComponentBase();
        }
        this.svlId = 0;
        this.channelId = 0;
        this.eventId = 0;
        this.startTime = 0;
        this.duration = 0;
        this.caption = false;
        this.freeCaMode = false;
        this.eventTitle = "";
        this.eventDetail = "";
        this.eventDetail_extended = "";
        this.guidanceMode = 0;
        this.guidanceText = "";
        this.eventRating = "";
        this.eventRatingType = 0;
        for (int i6 = 0; i6 < this.caSystemId.length; i6++) {
            this.caSystemId[i6] = 0;
        }
        this.eventCategoryNum = 0;
        for (int i7 = 0; i7 < this.eventCategory.length; i7++) {
            this.eventCategory[i7] = 0;
        }
    }

    public int getSvlId() {
        return this.svlId;
    }

    public void setSvlId(int svlId2) {
        this.svlId = svlId2;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int channelId2) {
        this.channelId = channelId2;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId2) {
        this.eventId = eventId2;
    }

    public int getEventRatingType() {
        return this.eventRatingType;
    }

    public void setEventRatingType(int eventRatingType2) {
        this.eventRatingType = eventRatingType2;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime2) {
        this.startTime = startTime2;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration2) {
        this.duration = duration2;
    }

    public boolean isCaption() {
        return this.caption;
    }

    public void setCaption(boolean caption2) {
        this.caption = caption2;
    }

    public boolean isFreeCaMode() {
        return this.freeCaMode;
    }

    public void setFreeCaMode(boolean freeCaMode2) {
        this.freeCaMode = freeCaMode2;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle2) {
        this.eventTitle = eventTitle2;
    }

    public String getEventRating() {
        return this.eventRating;
    }

    public void setEventRating(String eventRating2) {
        this.eventRating = eventRating2;
    }

    public String getEventDetail() {
        return this.eventDetail;
    }

    public String getEventDetailExtend() {
        return this.eventDetail_extended;
    }

    public void setEventDetail(String eventDetail2) {
        this.eventDetail = eventDetail2;
    }

    public void setEventDetailExtened(String eventDetail_extended2) {
        this.eventDetail_extended = eventDetail_extended2;
    }

    public int getGuidanceMode() {
        return this.guidanceMode;
    }

    public void setGuidanceMode(int guidanceMode2) {
        this.guidanceMode = guidanceMode2;
    }

    public String getGuidanceText() {
        return this.guidanceText;
    }

    public void setGuidanceText(String guidanceText2) {
        this.guidanceText = guidanceText2;
    }

    public int[] getCaSystemId() {
        return this.caSystemId;
    }

    public void setCaSystemId(int[] caSystemId2) {
        this.caSystemId = caSystemId2;
    }

    public int getEventCategoryNum() {
        return this.eventCategoryNum;
    }

    public void setEventCategoryNum(int eventCategoryNum2) {
        this.eventCategoryNum = eventCategoryNum2;
    }

    public int[] getEventCategory() {
        return this.eventCategory;
    }

    public void setEventCategory(int[] eventCategory2) {
        this.eventCategory = eventCategory2;
    }

    public MtkTvEventLinkageBase[] getEventLinkage() {
        return this.eventLinkage;
    }

    public void setEventLinkage(MtkTvEventLinkageBase[] eventLinkage2) {
        this.eventLinkage = eventLinkage2;
    }

    public MtkTvEventGroupBase[] getEventGroup() {
        return this.eventGroup;
    }

    public void setEventGroup(MtkTvEventGroupBase[] eventGroup2) {
        this.eventGroup = eventGroup2;
    }

    public MtkTvEventSeriesBase[] getEventSeries() {
        return this.eventSeries;
    }

    public void setEventSeries(MtkTvEventSeriesBase[] eventSeries2) {
        this.eventSeries = eventSeries2;
    }

    public MtkTvEventComponentDescriptorBase[] getEventComponent() {
        return this.eventComponent;
    }

    public void setEventComponent(MtkTvEventComponentDescriptorBase[] eventComponent2) {
        this.eventComponent = eventComponent2;
    }

    public MtkTvEventAudioComponentBase[] getEventAudioComponent() {
        return this.eventAudioComponent;
    }

    public void setEventAudioComponent(MtkTvEventAudioComponentBase[] eventAudioComponent2) {
        this.eventAudioComponent = eventAudioComponent2;
    }

    public String toString() {
        return "MtkTvEventInfo [svlId=" + this.svlId + ", channelId=" + this.channelId + ", eventId=" + this.eventId + ", startTime=" + this.startTime + ", duration=" + this.duration + ", caption=" + this.caption + ", freeCaMode=" + this.freeCaMode + ", eventTitle=" + this.eventTitle + ", eventDetail=" + this.eventDetail + ", eventDetailextended=" + this.eventDetail_extended + ", guidanceMode=" + this.guidanceMode + ", guidanceText=" + this.guidanceText + ", eventRating=" + this.eventRating + ", eventRatingType=" + this.eventRatingType + ", caSystemId=" + Arrays.toString(this.caSystemId) + ", eventCategoryNum=" + this.eventCategoryNum + ", eventCategory=" + Arrays.toString(this.eventCategory) + ", eventLinkage=" + Arrays.toString(this.eventLinkage) + ", eventSeries=" + Arrays.toString(this.eventSeries) + ", eventGroup=" + Arrays.toString(this.eventGroup) + ", eventComponent=" + Arrays.toString(this.eventComponent) + ", eventAudioComponent=" + Arrays.toString(this.eventAudioComponent) + "]";
    }
}
