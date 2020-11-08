package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.OpenVchipSubMenu;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;

public class OpenVchipRegionFragment extends SideFragment {
    private static final int MESSAGE_DISMISS_DIALOG = 1;
    private static final String TRACKER_LABEL = "Program restrictions";
    /* access modifiers changed from: private */
    public LiveTVDialog factroyCofirm = null;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1 && OpenVchipRegionFragment.this.pdialog != null) {
                OpenVchipRegionFragment.this.pdialog.dismiss();
                OpenVchipRegionFragment.this.getData();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mRegionNum = 0;
    /* access modifiers changed from: private */
    public SaveValue mSaveValue;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            OpenVchipRegionFragment.this.notifyDataSetChanged();
        }
    };
    /* access modifiers changed from: private */
    public TVContent mTV;
    /* access modifiers changed from: private */
    public ProgressDialog pdialog = null;

    public static String getDescription(Activity tvActivity) {
        return RatingsFragment.getDescription(tvActivity);
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.parental_open_vchip_region);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        this.mContext = getActivity();
        this.mTV = TVContent.getInstance(this.mContext);
        this.mSaveValue = SaveValue.getInstance(this.mContext);
        this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(0);
        this.mRegionNum = this.mTV.getOpenVchip().getRegionNum();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < this.mRegionNum; i++) {
            this.mTV.getOpenVCHIPPara().setOpenVCHIPParaType(1);
            this.mTV.getOpenVCHIPPara().setRegionIndex(i);
            final int regionIndex = i;
            items.add(new OpenVchipSubMenu(this.mTV.getOpenVchip().getRegionText(), OpenVchipDimFragment.getDescription(getMainActivity()), getMainActivity().getSideFragmentManager(), 0, i) {
                /* access modifiers changed from: protected */
                public SideFragment getFragment() {
                    SideFragment fragment = new OpenVchipDimFragment();
                    fragment.setListener(OpenVchipRegionFragment.this.mSideFragmentListener);
                    return fragment;
                }

                /* access modifiers changed from: protected */
                public void onSelected() {
                    OpenVchipRegionFragment.this.mSaveValue.saveValue("OpenVchipRegionIndex", regionIndex);
                    super.onSelected();
                }
            });
        }
        items.add(new ResetAllItem());
        return items;
    }

    private class ResetAllItem extends ActionItem {
        private TextView mTextView;

        public ResetAllItem() {
            super((String) null);
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            this.mTextView = (TextView) view.findViewById(R.id.title);
            setEnabled(OpenVchipRegionFragment.this.mRegionNum != 0);
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            this.mTextView.setText(OpenVchipRegionFragment.this.getString(R.string.parental_rrt5_reset));
        }

        /* access modifiers changed from: protected */
        public void onUnbind() {
            super.onUnbind();
            this.mTextView = null;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            LiveTVDialog unused = OpenVchipRegionFragment.this.factroyCofirm = new LiveTVDialog(OpenVchipRegionFragment.this.mContext, 3);
            OpenVchipRegionFragment.this.factroyCofirm.setMessage(OpenVchipRegionFragment.this.mContext.getString(R.string.parental_rrt5_reset));
            OpenVchipRegionFragment.this.factroyCofirm.setButtonYesName(OpenVchipRegionFragment.this.mContext.getString(R.string.menu_ok));
            OpenVchipRegionFragment.this.factroyCofirm.setButtonNoName(OpenVchipRegionFragment.this.mContext.getString(R.string.menu_cancel));
            OpenVchipRegionFragment.this.factroyCofirm.show();
            OpenVchipRegionFragment.this.factroyCofirm.setPositon(-20, 70);
            OpenVchipRegionFragment.this.factroyCofirm.getButtonNo().requestFocus();
            View.OnKeyListener listener = new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 0 && (keyCode == 66 || keyCode == 23 || keyCode == 183)) {
                        if (v.getId() == OpenVchipRegionFragment.this.factroyCofirm.getButtonYes().getId()) {
                            OpenVchipRegionFragment.this.factroyCofirm.dismiss();
                            ProgressDialog unused = OpenVchipRegionFragment.this.pdialog = ProgressDialog.show(OpenVchipRegionFragment.this.mContext, "Reset RRT5", "Reseting please wait...", false, false);
                            OpenVchipRegionFragment.this.mTV.resetRRT5();
                            OpenVchipRegionFragment.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                            return true;
                        } else if (v.getId() == OpenVchipRegionFragment.this.factroyCofirm.getButtonNo().getId()) {
                            OpenVchipRegionFragment.this.factroyCofirm.dismiss();
                            return true;
                        }
                    }
                    return false;
                }
            };
            OpenVchipRegionFragment.this.factroyCofirm.getButtonNo().setOnKeyListener(listener);
            OpenVchipRegionFragment.this.factroyCofirm.getButtonYes().setOnKeyListener(listener);
        }

        /* access modifiers changed from: protected */
        public void onFocused() {
            super.onFocused();
        }
    }
}
