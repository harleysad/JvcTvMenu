package com.mediatek.wwtv.tvcenter.dvr.controller;

import android.content.Context;
import android.content.res.Resources;
import com.mediatek.wwtv.tvcenter.dvr.manager.Controller;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;

public class StateBase implements IStateInterface {
    private boolean isRunning = false;
    protected Context mContext;
    protected DvrManager mManager;
    protected StatusType type = StatusType.UNKNOWN;

    public StateBase(Context context, DvrManager manager) {
        this.mManager = manager;
        this.mContext = context;
        initView();
    }

    public void initView() {
    }

    public void hideView() {
    }

    public void showView() {
    }

    public StatusType getType() {
        return this.type;
    }

    public void setType(StatusType type2) {
        this.type = type2;
    }

    public Context getmContext() {
        return this.mContext;
    }

    public void setmContext(Context mContext2) {
        this.mContext = mContext2;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning2) {
        this.isRunning = isRunning2;
    }

    public Controller getController() {
        if (this.mManager == null) {
            return null;
        }
        return this.mManager.getController();
    }

    public Resources getResource() {
        return this.mContext.getResources();
    }

    public DvrManager getManager() {
        return this.mManager;
    }

    public boolean onKeyDown(int keycode) {
        return false;
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onRelease() {
    }

    public void hiddenNotCoExistWindow(int compID) {
    }
}
