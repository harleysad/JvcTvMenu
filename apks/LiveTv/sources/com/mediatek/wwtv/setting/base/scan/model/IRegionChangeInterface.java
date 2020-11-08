package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.wwtv.setting.base.scan.adapter.ThirdItemAdapter;
import java.util.List;

public interface IRegionChangeInterface {
    void onRegionChange(List<ThirdItemAdapter.ThirdItem> list);
}
