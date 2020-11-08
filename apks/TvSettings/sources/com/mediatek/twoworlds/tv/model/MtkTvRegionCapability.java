package com.mediatek.twoworlds.tv.model;

public class MtkTvRegionCapability {
    private int height_max = 0;
    private int height_min = 0;
    private boolean isEnable = false;
    private int width_max = 0;
    private int width_min = 0;
    private int x_max = 0;
    private int x_min = 0;
    private int y_max = 0;
    private int y_min = 0;

    public boolean getEnable() {
        return this.isEnable;
    }

    public int getXMin() {
        return this.x_min;
    }

    public int getXMax() {
        return this.x_max;
    }

    public int getYMin() {
        return this.y_min;
    }

    public int getYMax() {
        return this.y_max;
    }

    public int getWidthMin() {
        return this.width_min;
    }

    public int getWidthMax() {
        return this.width_max;
    }

    public int getHeightMin() {
        return this.height_min;
    }

    public int getHeightMax() {
        return this.height_max;
    }

    public void setEnable(boolean enable) {
        this.isEnable = enable;
    }

    public void setXMin(int x_min2) {
        this.x_min = x_min2;
    }

    public void setXMax(int x_max2) {
        this.x_max = x_max2;
    }

    public void setYMin(int y_min2) {
        this.y_min = y_min2;
    }

    public void setYMax(int width_min2) {
        this.y_max = width_min2;
    }

    public void setWidthMin(int width_min2) {
        this.width_min = width_min2;
    }

    public void setWidthMax(int width_max2) {
        this.width_max = width_max2;
    }

    public void setHeightMin(int height_min2) {
        this.height_min = height_min2;
    }

    public void setHeightMax(int height_max2) {
        this.height_max = height_max2;
    }
}
