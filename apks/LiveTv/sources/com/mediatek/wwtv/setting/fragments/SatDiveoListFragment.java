package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.wwtv.setting.base.scan.ui.RegionalisationAusActivity;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SatDiveoListFragment extends Fragment {
    ArrayAdapter<String> adapter;
    List<String> datas = new ArrayList();
    TextView f_child_title;
    ListView region_list;

    public interface OnSatDiveoItemClick {
        void satDiveoItemClick(int i, int i2);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.region_list_layout, container, false);
        this.f_child_title = (TextView) view.findViewById(R.id.f_child_title);
        this.region_list = (ListView) view.findViewById(R.id.region_list);
        this.f_child_title.setText(getResources().getString(R.string.select_region_title));
        this.datas = buildDatas();
        this.adapter = new ArrayAdapter<>(getActivity(), 17367043, 16908308, this.datas);
        this.region_list.setAdapter(this.adapter);
        this.region_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((RegionalisationAusActivity) SatDiveoListFragment.this.getActivity()).satDiveoItemClick(1, position);
            }
        });
        return view;
    }

    public void refreshList() {
        this.datas = buildDatas();
        this.adapter.notifyDataSetChanged();
    }

    private List<String> buildDatas() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<TIFChannelInfo>> next : ((RegionalisationAusActivity) getActivity()).getMaps().entrySet()) {
            result.add(next.getKey() + "    [" + ((MtkTvDvbChannelInfo) ((TIFChannelInfo) next.getValue().get(0)).mMtkTvChannelInfo).getServiceName() + "]");
        }
        return result;
    }
}
