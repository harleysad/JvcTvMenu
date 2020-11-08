package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mediatek.wwtv.setting.TKGSSettingActivity;
import com.mediatek.wwtv.setting.base.scan.adapter.TkgsLocatorListAdapter;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class TkgsLocatorListFrag extends Fragment {
    String TAG = "TkgsLocatorListFrag";
    boolean isFargResumed;
    boolean isHiddLocs = false;
    private Action mAction;
    TkgsLocatorListAdapter mAdapter;
    private MenuConfigManager mConfigManager;
    private Context mContext;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    MenuDataHelper mHelper;
    /* access modifiers changed from: private */
    public ScrollAdapterView mListView;
    private ViewGroup mRootView;
    private TVContent mTvContent;
    int remeberPos;

    public void setAction(Action action) {
        this.mAction = action;
    }

    public void remeberPos(int pos) {
        this.remeberPos = pos;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(this.TAG, "onCreate........");
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.menu_sat_list, (ViewGroup) null);
        this.mListView = (ScrollAdapterView) this.mRootView.findViewById(R.id.list);
        bindData();
        MtkLog.d(this.TAG, "onCreateView........");
        return this.mRootView;
    }

    private void bindData() {
        this.mTvContent = TVContent.getInstance(getActivity());
        this.mConfigManager = MenuConfigManager.getInstance(getActivity());
        this.mHelper = MenuDataHelper.getInstance(getActivity());
        List<TkgsLocatorListAdapter.TkgsLocatorItem> list = new ArrayList<>();
        String str = this.TAG;
        MtkLog.d(str, "itemID:" + this.mAction.mItemID);
        if (this.isFargResumed && this.mAdapter != null) {
            int normalPos = this.mAdapter.getSelectItemNum();
            String str2 = this.TAG;
            MtkLog.d(str2, "setsotth normal:" + normalPos);
            if (normalPos < 0) {
                normalPos = 0;
            }
            remeberPos(normalPos);
        }
        this.isHiddLocs = false;
        if (this.mAction.mItemID.equals(MenuConfigManager.TKGS_HIDD_LOCS)) {
            list = this.mHelper.getHiddenTKGSLocatorList();
            if (list.size() > 0) {
                this.isHiddLocs = true;
                TkgsLocatorListAdapter.TkgsLocatorItem delAllKey = new TkgsLocatorListAdapter.TkgsLocatorItem(true, MenuConfigManager.TKGS_LOC_ITEM_HIDD_CLEANALL, "Clear All Locators", Action.DataType.DIALOGPOP);
                if (!list.contains(delAllKey)) {
                    list.add(delAllKey);
                }
            } else {
                TkgsLocatorListAdapter.TkgsLocatorItem emptyKey = new TkgsLocatorListAdapter.TkgsLocatorItem(true, "Hidden TKGS Locations Empty", "No Hidden TKGS Locations", Action.DataType.DIALOGPOP);
                emptyKey.setEnabled(false);
                if (!list.contains(emptyKey)) {
                    list.add(emptyKey);
                }
            }
        } else if (this.mAction.mItemID.equals(MenuConfigManager.TKGS_LOC_LIST)) {
            list = this.mHelper.convertToTKGSLocatorList();
            TkgsLocatorListAdapter.TkgsLocatorItem addKey = new TkgsLocatorListAdapter.TkgsLocatorItem(true, MenuConfigManager.TKGS_LOC_ITEM_ADD, "Click to Add Locator", Action.DataType.HAVESUBCHILD);
            if (!list.contains(addKey)) {
                list.add(addKey);
            }
        }
        this.mAdapter = new TkgsLocatorListAdapter(this.mContext, list);
        this.mAdapter.setListener((TKGSSettingActivity) getActivity());
        this.mListView.setAdapter(this.mAdapter);
        this.mAdapter.setScrollAdapterView(this.mListView);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                int toPos;
                if (TkgsLocatorListFrag.this.isFargResumed) {
                    MtkLog.d(TkgsLocatorListFrag.this.TAG, "mHandler isFargResumed");
                    if (TkgsLocatorListFrag.this.isHiddLocs) {
                        toPos = TkgsLocatorListFrag.this.mListView.getChildCount() - 1;
                    } else {
                        toPos = TkgsLocatorListFrag.this.remeberPos;
                    }
                } else if (TkgsLocatorListFrag.this.isHiddLocs) {
                    toPos = TkgsLocatorListFrag.this.mListView.getChildCount() - 1;
                } else {
                    toPos = 0;
                }
                if (TkgsLocatorListFrag.this.mListView.getChildCount() > toPos) {
                    boolean requestFocus = TkgsLocatorListFrag.this.mListView.getChildAt(toPos).requestFocus();
                    TkgsLocatorListFrag.this.mListView.setSelectionSmooth(toPos);
                    MtkLog.d(TkgsLocatorListFrag.this.TAG, "setsotth:" + toPos);
                }
            }
        }, 500);
    }

    public void onResume() {
        super.onResume();
        if (!this.isFargResumed) {
            this.isFargResumed = true;
        }
        MtkLog.d(this.TAG, "onResume........");
    }
}
