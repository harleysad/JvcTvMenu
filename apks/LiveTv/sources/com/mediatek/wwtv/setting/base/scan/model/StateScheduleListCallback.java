package com.mediatek.wwtv.setting.base.scan.model;

public interface StateScheduleListCallback<T> {
    boolean onItemClick(T t);

    boolean switchToScheduleItemInfo(T t);

    boolean switchToScheduleList(T t);
}
