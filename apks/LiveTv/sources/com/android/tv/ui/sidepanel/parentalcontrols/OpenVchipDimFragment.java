package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.OpenVchipSubMenu;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class OpenVchipDimFragment extends SideFragment {
    private static final String TRACKER_LABEL = "Program restrictions";
    private Context mContext;
    /* access modifiers changed from: private */
    public SaveValue mSaveValue;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            OpenVchipDimFragment.this.notifyDataSetChanged();
        }
    };
    private TVContent mTV;

    public static String getDescription(Activity tvActivity) {
        return "";
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.parental_open_vchip_dim);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mContext = getActivity().getApplicationContext();
        this.mTV = TVContent.getInstance(getActivity().getApplicationContext());
        this.mSaveValue = SaveValue.getInstance(this.mContext);
        this.mTV.getOpenVCHIPPara().setRegionIndex(this.mSaveValue.readValue("OpenVchipRegionIndex"));
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(4);
        int dimNum = this.mTV.getOpenVchip().getDimNum();
        Log.d("RRT", dimNum + "");
        List<Item> items = new ArrayList<>();
        for (int j = 0; j < dimNum; j++) {
            this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(5);
            this.mTV.getOpenVCHIPPara().setDimIndex(j);
            final int dimIndex = j;
            items.add(new OpenVchipSubMenu(this.mTV.getOpenVchip().getDimText(), OpenVchipLevelFragment.getDescription(getMainActivity()), getMainActivity().getSideFragmentManager(), 1, j) {
                /* access modifiers changed from: protected */
                public SideFragment getFragment() {
                    OpenVchipLevelFragment fragment = new OpenVchipLevelFragment();
                    fragment.setListener(OpenVchipDimFragment.this.mSideFragmentListener);
                    fragment.setTitle(getTitle());
                    return fragment;
                }

                /* access modifiers changed from: protected */
                public void onSelected() {
                    OpenVchipDimFragment.this.mSaveValue.saveValue("OpenVchipDimIndex", dimIndex);
                    super.onSelected();
                }
            });
            Log.d("RRT5", this.mTV.getOpenVchip().getDimText());
        }
        return items;
    }
}
