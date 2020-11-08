package com.mediatek.twoworlds.tv.model;

public class MtkTvScreenModeOverscan {
    private int bottom;
    private int left;
    private int right;
    private int timingIdx;
    private int top;

    public int getTop() {
        return this.top;
    }

    public void setTop(int mtop) {
        this.top = mtop;
    }

    public int getBottom() {
        return this.bottom;
    }

    public void setBottom(int mbottom) {
        this.bottom = mbottom;
    }

    public int getLeft() {
        return this.left;
    }

    public void setLeft(int mleft) {
        this.left = mleft;
    }

    public int getRight() {
        return this.right;
    }

    public void setRight(int mright) {
        this.right = mright;
    }

    public int getTimingIdx() {
        return this.timingIdx;
    }

    public void setTimingIdx(int mtimingIdx) {
        this.timingIdx = mtimingIdx;
    }
}
