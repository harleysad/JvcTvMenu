package com.mediatek.wwtv.setting.base.scan.model;

public interface RespondedKeyEvent {
    int getValue();

    void onKeyEnter();

    void onKeyLeft();

    void onKeyRight();

    void setValue(int i);

    void showValue(int i);
}
