package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.adapter.RegionListAdapter;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.util.ArrayList;
import java.util.Collections;

public class RegionListFragment extends Fragment {
    TextView f_child_title;
    RegionListAdapter listAdapter;
    ArrayList<TIFChannelInfo> mList;
    ListView region_list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.region_list_layout, container, false);
        this.f_child_title = (TextView) view.findViewById(R.id.f_child_title);
        this.region_list = (ListView) view.findViewById(R.id.region_list);
        this.f_child_title.setText(getResources().getString(R.string.select_region_title));
        this.mList = new ArrayList<>();
        Bundle regionBundle = getArguments();
        if (regionBundle != null) {
            this.mList = (ArrayList) regionBundle.getSerializable("regions");
        }
        this.listAdapter = new RegionListAdapter(getActivity(), this.mList);
        this.region_list.setAdapter(this.listAdapter);
        this.region_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                RegionListFragment.this.changChannelPostion(arg2);
            }
        });
        return view;
    }

    public void changChannelPostion(int position) {
        if (position != 0) {
            Collections.swap(this.mList, 0, position);
            EditChannel.getInstance(getActivity()).channelSort(this.mList.get(position).mMtkTvChannelInfo.getChannelId(), this.mList.get(0).mMtkTvChannelInfo.getChannelId());
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
