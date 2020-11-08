package com.mediatek.twoworlds.tv.model;

public class MtkTvRectangle {
    private float h;
    private float w;
    private float x;
    private float y;

    public MtkTvRectangle() {
    }

    public MtkTvRectangle(float x2, float y2, float w2, float h2) {
        this.x = x2;
        this.y = y2;
        this.w = w2;
        this.h = h2;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public float getW() {
        return this.w;
    }

    public void setW(float w2) {
        this.w = w2;
    }

    public float getH() {
        return this.h;
    }

    public void setH(float h2) {
        this.h = h2;
    }
}
