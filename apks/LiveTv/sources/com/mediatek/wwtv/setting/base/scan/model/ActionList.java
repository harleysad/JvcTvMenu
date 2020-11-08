package com.mediatek.wwtv.setting.base.scan.model;

import java.util.ArrayList;

public class ActionList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 1;
    public int totalScanActionSize = 1;

    public void clear() {
        super.clear();
        this.totalScanActionSize = 1;
    }
}
