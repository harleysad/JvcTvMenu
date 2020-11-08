package com.mediatek.wwtv.tvcenter.epg;

public class EventInfo {
    public static int MAX_COMPONENT_INFO = 8;
    public static int MAX_EVENT_LINKAGE_INFO = 4;
    private int[] caSystemId = new int[4];
    private boolean caption;
    private int channelId;
    private long duration;
    private int[] eventCategory = new int[8];
    private int eventCategoryNum;
    private String eventDetail;
    private int eventId;
    private String eventTitle;
    private boolean freeCaMode;
    private int guidanceMode;
    private String guidanceText;
    private long startTime;
    private int svlId;

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

    public String getEventDetail() {
        return this.eventDetail;
    }

    public void setEventDetail(String eventDetail2) {
        this.eventDetail = eventDetail2;
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

    public int describeContents() {
        return 0;
    }
}
