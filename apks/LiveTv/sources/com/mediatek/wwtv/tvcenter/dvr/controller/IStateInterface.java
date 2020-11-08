package com.mediatek.wwtv.tvcenter.dvr.controller;

public interface IStateInterface {
    void hiddenNotCoExistWindow(int i);

    boolean onKeyDown(int i);

    void onPause();

    void onRelease();

    void onResume();

    void onStop();
}
