package com.mediatek.wwtv.tvcenter.scan;

import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.HashMap;

public class RegionUtils {
    private HashMap<Integer, APTargetRegion> children = new HashMap<>();
    private IRegionChangeInterface onRegionChangeListener;

    public void addChild(MtkTvScanDvbtBase.TargetRegion region) {
        addChild(new APTargetRegion(region));
    }

    public void addChild(APTargetRegion region) {
        switch (region.level) {
            case 1:
                getChildren().put(Integer.valueOf(region.primary), region);
                return;
            case 2:
                if (getChildren().get(Integer.valueOf(region.primary)) != null) {
                    getChildren().get(Integer.valueOf(region.primary)).getChildren().put(Integer.valueOf(region.secondary), region);
                    return;
                }
                return;
            case 3:
                if (getChildren().get(Integer.valueOf(region.primary)) != null && getChildren().get(Integer.valueOf(region.primary)).getChildren().get(Integer.valueOf(region.secondary)) != null) {
                    getChildren().get(Integer.valueOf(region.primary)).getChildren().get(Integer.valueOf(region.secondary)).getChildren().put(Integer.valueOf(region.tertiary), region);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void dumpRM() {
    }

    public void dumpMaps(HashMap<Integer, APTargetRegion> regions) {
        MtkLog.d("dumpMaps(...,...)");
        ArrayList<APTargetRegion> list1 = new ArrayList<>();
        list1.addAll(regions.values());
        String regionInfoStr = "";
        for (int i = 0; i < list1.size(); i++) {
            switch (list1.get(i).level) {
                case 1:
                    regionInfoStr = "Level1," + list1.get(i).name;
                    break;
                case 2:
                    regionInfoStr = "---Level2," + list1.get(i).name;
                    break;
                case 3:
                    regionInfoStr = "------Level3," + list1.get(i).name;
                    break;
            }
            MtkLog.d(regionInfoStr);
            if (list1.get(i).getChildren() != null && list1.get(i).getChildren().size() > 0) {
                dumpMaps(list1.get(i).getChildren());
            }
        }
    }

    public HashMap<Integer, APTargetRegion> getChildren() {
        return this.children;
    }

    public IRegionChangeInterface getOnRegionChangeListener() {
        return this.onRegionChangeListener;
    }

    public void setOnRegionChangeListener(IRegionChangeInterface onRegionChangeListener2) {
        this.onRegionChangeListener = onRegionChangeListener2;
    }
}
