package com.mediatek.wwtv.setting.base.scan.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.fragments.RegionListFragment;
import com.mediatek.wwtv.setting.fragments.SatAustrateFragment;
import com.mediatek.wwtv.setting.fragments.SatDiveoListFragment;
import com.mediatek.wwtv.setting.fragments.SatDiveoRegionDetailFragment;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegionalisationAusActivity extends BaseCustomActivity implements SatAustrateFragment.OnSatAustrateItemClick, SatDiveoListFragment.OnSatDiveoItemClick {
    private static final String TAG = "RegionalisationAusActivity";
    int currentLevelIndex = 0;
    Map<String, TIFChannelInfo> defaultRegion = new LinkedHashMap();
    FragmentManager fragmentManager;
    ArrayList<TIFChannelInfo> mList;
    Map<String, List<TIFChannelInfo>> maps;
    RegionListFragment regionListFragment;
    SatAustrateFragment satAustrateFragment;
    SatDiveoListFragment satDiveoListFragment;
    SatDiveoRegionDetailFragment satDiveoRegionDetailFragment;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sat_region_layout);
        if (ScanContent.isSatOpHDAustria()) {
            this.mList = (ArrayList) getIntent().getBundleExtra("regions").getSerializable("regions");
        } else if (ScanContent.isSatOpDiveo()) {
            this.maps = (Map) getIntent().getBundleExtra("regions").getSerializable("regions");
            if (this.maps == null) {
                finish();
            } else {
                initDefaultRegions(this.maps);
            }
        }
        if (savedInstanceState == null) {
            this.fragmentManager = getFragmentManager();
            FragmentTransaction tx = this.fragmentManager.beginTransaction();
            if (ScanContent.isSatOpDiveo()) {
                this.satDiveoListFragment = new SatDiveoListFragment();
                tx.add(R.id.container_layout, this.satDiveoListFragment, "SatAustrateFragment");
            } else if (ScanContent.isSatOpHDAustria()) {
                this.satAustrateFragment = new SatAustrateFragment();
                tx.add(R.id.container_layout, this.satAustrateFragment, "SatAustrateFragment");
            }
            tx.commit();
        }
    }

    private void initDefaultRegions(Map<String, List<TIFChannelInfo>> maps2) {
        this.defaultRegion.clear();
        for (Map.Entry<String, List<TIFChannelInfo>> next : maps2.entrySet()) {
            this.defaultRegion.put(next.getKey(), (TIFChannelInfo) next.getValue().get(0));
        }
    }

    public void satItemClick(int position) {
        switch (position) {
            case 0:
                finish();
                return;
            case 1:
                if (this.regionListFragment == null) {
                    this.regionListFragment = new RegionListFragment();
                }
                FragmentTransaction ft = this.fragmentManager.beginTransaction();
                ft.hide(this.satAustrateFragment);
                Bundle bundle = new Bundle();
                bundle.putSerializable("regions", this.mList);
                this.regionListFragment.setArguments(bundle);
                ft.add(R.id.container_layout, this.regionListFragment, "RegionListFragment");
                ft.addToBackStack((String) null);
                ft.commit();
                return;
            default:
                return;
        }
    }

    public Map<String, List<TIFChannelInfo>> getMaps() {
        return this.maps;
    }

    private List<String> filterServiceName(int position) {
        List<String> result = new ArrayList<>();
        for (TIFChannelInfo tifChannelInfo : this.maps.get(this.maps.keySet().toArray()[position])) {
            result.add(tifChannelInfo.mMtkTvChannelInfo.getServiceName());
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ScanContent.isSatOpDiveo() && keyCode == 22 && this.satDiveoListFragment.getUserVisibleHint()) {
            swapChannels(this.maps);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void swapChannels(Map<String, List<TIFChannelInfo>> maps2) {
        for (Map.Entry<String, List<TIFChannelInfo>> next : maps2.entrySet()) {
            int channelId = ((TIFChannelInfo) next.getValue().get(0)).mMtkTvChannelInfo.getChannelId();
            int defaultChannelId = this.defaultRegion.get(next.getKey()).mMtkTvChannelInfo.getChannelId();
            if (channelId != defaultChannelId) {
                EditChannel.getInstance(this).channelSort(channelId, defaultChannelId);
            }
        }
    }

    public void satDiveoItemClick(int level, int position) {
        if (level == 1) {
            this.currentLevelIndex = position;
            this.satDiveoRegionDetailFragment = new SatDiveoRegionDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("regions", (Serializable) filterServiceName(position));
            bundle.putString("title", (String) this.maps.keySet().toArray()[position]);
            this.satDiveoRegionDetailFragment.setArguments(bundle);
            FragmentTransaction ft = this.fragmentManager.beginTransaction();
            ft.addToBackStack((String) null);
            ft.replace(R.id.container_layout, this.satDiveoRegionDetailFragment, "SatDiveoRegionDetailFragment");
            ft.commit();
        } else if (level == 2) {
            this.fragmentManager.popBackStack();
            if (position != 0) {
                Collections.swap(this.maps.get(this.maps.keySet().toArray()[this.currentLevelIndex]), 0, position);
                this.satDiveoListFragment.refreshList();
            }
        }
    }
}
