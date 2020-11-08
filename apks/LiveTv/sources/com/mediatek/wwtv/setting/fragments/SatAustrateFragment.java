package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.adapter.SatAustrateListAdapter;
import com.mediatek.wwtv.tvcenter.R;

public class SatAustrateFragment extends Fragment {
    TextView f_child_title;
    ListView region_list;

    public interface OnSatAustrateItemClick {
        void satItemClick(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.region_list_layout, container, false);
        this.f_child_title = (TextView) view.findViewById(R.id.f_child_title);
        this.region_list = (ListView) view.findViewById(R.id.region_list);
        this.f_child_title.setText(getResources().getString(R.string.select_channel_title));
        this.region_list.setAdapter(new SatAustrateListAdapter(getActivity(), getResources().getStringArray(R.array.sat_austrate_array)));
        this.region_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                ((OnSatAustrateItemClick) SatAustrateFragment.this.getActivity()).satItemClick(arg2);
            }
        });
        return view;
    }
}
