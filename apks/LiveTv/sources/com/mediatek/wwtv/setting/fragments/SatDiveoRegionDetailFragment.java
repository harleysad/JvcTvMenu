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
import com.mediatek.wwtv.setting.base.scan.ui.RegionalisationAusActivity;
import com.mediatek.wwtv.tvcenter.R;
import java.util.Collections;
import java.util.List;

public class SatDiveoRegionDetailFragment extends Fragment {
    ArrayAdapter<String> adapter;
    private List<String> datas;
    TextView f_child_title;
    ListView region_list;
    private String title;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.datas = (List) getArguments().getSerializable("regions");
        this.title = getArguments().getString("title");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.region_list_layout, container, false);
        this.f_child_title = (TextView) view.findViewById(R.id.f_child_title);
        this.region_list = (ListView) view.findViewById(R.id.region_list);
        this.f_child_title.setText(getResources().getString(R.string.select_region_detail_title, new Object[]{this.title}));
        this.region_list.setAdapter(new ArrayAdapter<>(getActivity(), 17367043, 16908308, this.datas));
        this.region_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((RegionalisationAusActivity) SatDiveoRegionDetailFragment.this.getActivity()).satDiveoItemClick(2, position);
            }
        });
        return view;
    }

    public void swapAdapterDatas(int position) {
        Collections.swap(this.datas, 0, position);
        this.adapter.notifyDataSetChanged();
    }
}
