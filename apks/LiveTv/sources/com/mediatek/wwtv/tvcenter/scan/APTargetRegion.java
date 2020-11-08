package com.mediatek.wwtv.tvcenter.scan;

import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import java.util.HashMap;

public class APTargetRegion {
    private static final int MAX_REGION_NAME_LEN = 35;
    private HashMap<Integer, APTargetRegion> children = new HashMap<>();
    public int internalIdx;
    public int level;
    public String name;
    public int primary;
    public int secondary;
    public int tertiary;

    public APTargetRegion(MtkTvScanDvbtBase.TargetRegion targetRegion) {
        this.internalIdx = targetRegion.internalIdx;
        this.level = targetRegion.level;
        this.primary = targetRegion.primary;
        this.secondary = targetRegion.secondary;
        this.tertiary = targetRegion.tertiary;
        this.name = targetRegion.name;
    }

    public APTargetRegion(int internalIdx2, int level2, int primary2, int secondary2, int tertiary2, String name2) {
        this.internalIdx = internalIdx2;
        this.level = level2;
        this.primary = primary2;
        this.secondary = secondary2;
        this.tertiary = tertiary2;
        this.name = name2;
    }

    public APTargetRegion() {
    }

    public HashMap<Integer, APTargetRegion> getChildren() {
        return this.children;
    }

    public void setChildren(HashMap<Integer, APTargetRegion> children2) {
        this.children = children2;
    }

    public boolean equals(Object o) {
        if (!(o instanceof APTargetRegion) || ((APTargetRegion) o).name != this.name) {
            return false;
        }
        return true;
    }
}
