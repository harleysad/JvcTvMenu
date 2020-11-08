package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvPipPopFucusInfoBase {
    private static final String TAG = "MtkTvPipPopFucusInfoBase";
    private int h;
    private int w;
    private int x;
    private int y;

    public MtkTvPipPopFucusInfoBase() {
        Log.d(TAG, "MtkTvPipPopFucusInfoBase Constructor\n");
    }

    public MtkTvPipPopFucusInfoBase(int x2, int y2, int w2, int h2) {
        this.x = x2;
        this.y = y2;
        this.w = w2;
        this.h = h2;
    }

    public int getPopX() {
        Log.d(TAG, "getPopX" + this.x);
        return this.x;
    }

    public int getPopY() {
        Log.d(TAG, "getPopY" + this.y);
        return this.y;
    }

    public int getPopW() {
        Log.d(TAG, "getPopW" + this.w);
        return this.w;
    }

    public int getPopH() {
        Log.d(TAG, "getPopH" + this.h);
        return this.h;
    }

    public void setPopX(int x2) {
        Log.d(TAG, "setPopX" + x2);
        this.x = x2;
    }

    public void setPopY(int y2) {
        Log.d(TAG, "setPopY" + y2);
        this.y = y2;
    }

    public void setPopW(int w2) {
        Log.d(TAG, "setPopWw" + w2);
        this.w = w2;
    }

    public void setPopH(int h2) {
        Log.d(TAG, "setPopH" + h2);
        this.h = h2;
    }
}
