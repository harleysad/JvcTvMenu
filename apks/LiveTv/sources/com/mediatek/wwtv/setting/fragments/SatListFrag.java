package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.wwtv.setting.SatActivity;
import com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteTPInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class SatListFrag extends Fragment {
    String TAG = "SatListFrag";
    boolean isFargResumed;
    List<SatListAdapter.SatItem> list = null;
    private Action mAction;
    SatListAdapter mAdapter;
    private int mAddSatId = -1;
    private MenuConfigManager mConfigManager;
    private Context mContext;
    private int mDVBSCurrentOP = -1;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    MenuDataHelper mHelper;
    private ScrollAdapterView mListView;
    private boolean mNeedCheckM7Scan;
    boolean mNeedDisableItem = false;
    private List<SatelliteInfo> mRescanSatLocalInfoList;
    private List<SatelliteTPInfo> mRescanSatLocalTPInfoList;
    private ViewGroup mRootView;
    List<SatelliteInfo> mSatellites;
    private TVContent mTvContent;
    int remeberPos;
    int selectPosForOP;

    public void setAction(Action action) {
        this.mAction = action;
    }

    public void setSelectPos(int pos) {
        this.selectPosForOP = pos;
    }

    public void remeberPos(int pos) {
        this.remeberPos = pos;
    }

    public void setSatellites(List<SatelliteInfo> satellites) {
        this.mSatellites = satellites;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(this.TAG, "onCreate........");
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.menu_sat_list, (ViewGroup) null);
        this.mListView = (ScrollAdapterView) this.mRootView.findViewById(R.id.list);
        String str = this.TAG;
        MtkLog.d(str, "onCreateView.......isFargResumed:" + this.isFargResumed);
        bindData();
        if (this.mNeedCheckM7Scan && this.mTvContent.isM7ScanMode()) {
            ((SatActivity) this.mContext).showM7LNBScanConfirmDialog();
            this.mNeedCheckM7Scan = false;
        }
        return this.mRootView;
    }

    private void bindData() {
        this.mTvContent = TVContent.getInstance(getActivity());
        this.mConfigManager = MenuConfigManager.getInstance(getActivity());
        MenuDataHelper.setMySelfNull();
        this.mHelper = MenuDataHelper.getInstance(getActivity());
        String str = this.TAG;
        MtkLog.d(str, "itemID:" + this.mAction.mItemID);
        if (this.mAction.mItemID.equals("Satellite Add")) {
            this.mSatellites = ScanContent.getDVBSsatellites(this.mContext);
            this.mDVBSCurrentOP = -1;
            if (this.isFargResumed) {
                this.list = this.mHelper.buildDVBSInfoItem(this.mAction, ScanContent.getDVBSsatellitesBySatID(this.mContext, this.mAddSatId));
            } else {
                this.list = this.mHelper.buildDVBSSATDetailInfo(this.mAction, this.mSatellites, 2);
                if (this.list.size() > 0) {
                    this.list = this.list.subList(0, 1);
                    this.mAddSatId = this.list.get(0).satID;
                }
            }
        } else if (this.mAction.mItemID.equals(MenuConfigManager.DVBS_SAT_UPDATE_SCAN) || this.mAction.mItemID.equals(MenuConfigManager.DVBS_SAT_MANUAL_TURNING)) {
            this.mSatellites = ScanContent.getDVBSsatellites(this.mContext);
            this.mDVBSCurrentOP = -1;
            this.list = this.mHelper.buildDVBSSATDetailInfo(this.mAction, this.mSatellites, 1);
        } else {
            if (!this.isFargResumed) {
                MtkLog.d(this.TAG, "backUpDVBSsatellites........");
                TVContent.backUpDVBSsatellites();
                this.mRescanSatLocalInfoList = ScanContent.getDVBSsatellites(this.mContext);
                this.mRescanSatLocalTPInfoList = ScanContent.getDVBSTransponderList(this.mRescanSatLocalInfoList);
                if (ScanContent.isPreferedSat()) {
                    if (this.mAction.mItemID.equals(MenuConfigManager.DVBS_SAT_OP)) {
                        int dVBSCurrentOP = ScanContent.getDVBSCurrentOP();
                        this.mDVBSCurrentOP = ScanContent.setSelectedSatelliteOPFromMenu(this.mContext, this.selectPosForOP);
                    }
                    this.mNeedDisableItem = true;
                } else {
                    this.mDVBSCurrentOP = -1;
                    ScanContent.setSelectedSatelliteOPFromMenu(this.mContext, 0);
                }
            }
            this.mSatellites = ScanContent.getDVBSsatellites(this.mContext);
            this.list = this.mHelper.buildDVBSSATDetailInfo(this.mAction, this.mSatellites, 0);
        }
        ScanContent.setDVBSCurroperator(this.mDVBSCurrentOP);
        this.mAdapter = new SatListAdapter(this.mContext, this.list);
        this.mAdapter.setNeedDisableWhenisOff(this.mNeedDisableItem);
        this.mAdapter.setListener((SatActivity) getActivity());
        this.mAdapter.setScrollAdapterView(this.mListView);
        this.mListView.setAdapter(this.mAdapter);
    }

    private void whenBack() {
        TVContent.restoreDVBSsatellites();
        TVContent.freeBachUpDVBSsatellites();
        ScanContent.restoreSatTpInfo(this.mRescanSatLocalInfoList, this.mRescanSatLocalTPInfoList);
        this.mRescanSatLocalInfoList = null;
        this.mRescanSatLocalTPInfoList = null;
    }

    public void onResume() {
        super.onResume();
        SatActivity.isSatListShow = true;
        if (!this.isFargResumed) {
            this.isFargResumed = true;
        }
        MtkLog.d(this.TAG, "onResume........");
    }

    public void onStop() {
        SatActivity.isSatListShow = false;
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mAction.mItemID.equals(MenuConfigManager.DVBS_SAT_OP) || this.mAction.mItemID.equals(MenuConfigManager.DVBS_SAT_RE_SCAN)) {
            whenBack();
        }
    }

    public void setNeedCheckM7Scan(boolean check) {
        this.mNeedCheckM7Scan = check;
    }
}
