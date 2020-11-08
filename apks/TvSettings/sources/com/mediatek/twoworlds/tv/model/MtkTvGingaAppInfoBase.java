package com.mediatek.twoworlds.tv.model;

public class MtkTvGingaAppInfoBase {
    private String AppId = "";
    private String AppName = "";
    private boolean bIsRunning = false;

    public String getAppName() {
        return this.AppName;
    }

    public String getAppId() {
        return this.AppId;
    }

    public boolean isRunning() {
        return this.bIsRunning;
    }

    public void setAppName(String appName) {
        this.AppName = appName;
    }

    public void setAppId(String appId) {
        this.AppId = appId;
    }

    public void setIsRunning(boolean isRunningState) {
        this.bIsRunning = isRunningState;
    }

    public String toString() {
        return "MtkTvGingaAppInfoBase: AppName=" + this.AppName + ", AppId=" + this.AppId + ", isRunning=" + this.bIsRunning;
    }
}
