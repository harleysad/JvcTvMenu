package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class NetFlixEsnInfoFrag extends Fragment {
    private Context context;
    private TextView esnName;
    private TextView esnNumber;
    private LayoutInflater mInflater;
    private ViewGroup mRootView;
    private TVContent mTVContent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.netflix_esn_info, (ViewGroup) null);
        init();
        return this.mRootView;
    }

    public void init() {
        this.mTVContent = TVContent.getInstance(this.context);
        this.esnName = (TextView) this.mRootView.findViewById(R.id.netflix_esn_info_name);
        this.esnNumber = (TextView) this.mRootView.findViewById(R.id.netflix_esn_info_number);
        this.esnName.setText("ESN:");
        String nummber = (("QWERTYUIOP=DFGHJ567889" + "\nQWERTYUIOP=DFGHJ567889=ERE44=DFGHJK-TYUITESD45567F4") + "\nQWERTYUIOP=DFGHJ567889=ERE44=DFGHJK-TYUITESD45567F4") + "\nQWERTYUIOP=DFGHJ567889=ERE44=DFGHJK-TYUITESD45567F4";
        this.esnNumber.setText(SaveValue.getInstance(this.context).readStrValue("ESNStr"));
    }
}
