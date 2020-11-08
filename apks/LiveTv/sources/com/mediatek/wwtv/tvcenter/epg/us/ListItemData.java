package com.mediatek.wwtv.tvcenter.epg.us;

public class ListItemData {
    private int eventId;
    private boolean isBlocked;
    private boolean isCC;
    private String itemDay;
    private int itemId;
    private String itemProgramDetail;
    private String itemProgramName;
    private String itemProgramType;
    private String itemTime;
    private long millsDurationTime;
    private long millsStartTime;
    private String programStartTime;
    private String programTime;
    private boolean valid = true;

    public void setMillsStartTime(long startTime) {
        this.millsStartTime = startTime;
    }

    public long getMillsStartTime() {
        return this.millsStartTime;
    }

    public void setMillsDurationTime(long durationTime) {
        this.millsDurationTime = durationTime;
    }

    public long getMillsDurationTime() {
        return this.millsDurationTime;
    }

    public void setEventId(int eventid) {
        this.eventId = eventid;
    }

    public int getEventId() {
        return this.eventId;
    }

    public String getProgramStartTime() {
        return this.programStartTime;
    }

    public void setProgramStartTime(String programStartTime2) {
        this.programStartTime = programStartTime2;
    }

    public String getProgramTime() {
        return this.programTime;
    }

    public void setProgramTime(String programTime2) {
        this.programTime = programTime2;
    }

    public String getItemProgramDetail() {
        if (this.itemProgramDetail == null || this.itemProgramDetail.replace(" ", "").equals("")) {
            return "(No Program Detail).";
        }
        return this.itemProgramDetail;
    }

    public void setItemProgramDetail(String itemProgramDetail2) {
        this.itemProgramDetail = itemProgramDetail2;
    }

    public String getItemProgramType() {
        return this.itemProgramType;
    }

    public void setItemProgramType(String itemProgramType2) {
        this.itemProgramType = itemProgramType2;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean value) {
        this.valid = value;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemId2) {
        this.itemId = itemId2;
    }

    public String getItemDay() {
        return this.itemDay;
    }

    public void setItemDay(String itemDay2) {
        this.itemDay = itemDay2;
    }

    public String getItemTime() {
        return this.itemTime;
    }

    public void setItemTime(String itemTime2) {
        this.itemTime = itemTime2;
    }

    public String getItemProgramName() {
        return this.itemProgramName;
    }

    public void setItemProgramName(String itemProgramName2) {
        this.itemProgramName = itemProgramName2;
    }

    public boolean isBlocked() {
        return this.isBlocked;
    }

    public void setBlocked(boolean isBlocked2) {
        this.isBlocked = isBlocked2;
    }

    public boolean isCC() {
        return this.isCC;
    }

    public void setCC(boolean isCC2) {
        this.isCC = isCC2;
    }
}
