package com.mediatek.twoworlds.tv.model;

import java.util.Arrays;

public class MtkTvEventGroupBase {
    public static int MAX_EVENT_COMMON_INFO = 32;
    private MtkTvEventComom[] eventCommom = new MtkTvEventComom[MAX_EVENT_COMMON_INFO];
    private short eventCommonCnt = 0;
    private short eventType = 0;

    public MtkTvEventGroupBase() {
        for (int i = 0; i < MAX_EVENT_COMMON_INFO; i++) {
            this.eventCommom[i] = new MtkTvEventComom();
        }
    }

    public short geteventType() {
        return this.eventType;
    }

    public void seteventType(short eventType2) {
        this.eventType = eventType2;
    }

    public short geteventCommonCnt() {
        return this.eventCommonCnt;
    }

    public void seteventCommonCnt(short eventCommonCnt2) {
        this.eventCommonCnt = eventCommonCnt2;
    }

    public MtkTvEventComom[] geteventCommom() {
        return this.eventCommom;
    }

    public void seteventCommom(MtkTvEventComom[] eventCommom2) {
        this.eventCommom = eventCommom2;
    }

    public String toString() {
        return "MtkTvEventGroup: eventType=" + this.eventType + ", eventCommonCnt=" + this.eventCommonCnt + ", eventCommom=" + Arrays.toString(this.eventCommom);
    }
}
