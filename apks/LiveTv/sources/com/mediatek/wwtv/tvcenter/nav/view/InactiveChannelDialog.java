package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.CustListView;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.List;

public class InactiveChannelDialog extends Dialog {
    static final int NORMALPAGE_SIZE = 10;
    static String TAG = "InactiveChannelDialog";
    CommonIntegration mCommonInter;
    private Context mContext;
    List<TIFChannelInfo> mInactiveChannelList;
    SetConfigListViewAdapter tRDAdapter;
    CustListView trdItemsListView;
    LayoutInflater trdLayoutInflater;
    private CustListView.UpDateListView update = new CustListView.UpDateListView() {
        public void updata() {
            InactiveChannelDialog.this.tRDAdapter.setmGroup(InactiveChannelDialog.this.trdItemsListView.getCurrentList());
            InactiveChannelDialog.this.trdItemsListView.setAdapter(InactiveChannelDialog.this.tRDAdapter);
        }
    };

    public InactiveChannelDialog(Context context) {
        super(context, 2131755419);
        this.mContext = context;
        this.mCommonInter = CommonIntegration.getInstanceWithContext(this.mContext);
    }

    public void showInactiveChannels() {
        initInactivechannelUI();
        show();
    }

    public void setInactiveChnnaelList(List<TIFChannelInfo> inactiveChannelList) {
        this.mInactiveChannelList = inactiveChannelList;
    }

    private void initInactivechannelUI() {
        setTitle(R.string.menu_tab_inactive_channels);
        this.trdLayoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        View view = this.trdLayoutInflater.inflate(R.layout.menu_main_scan_trd_fav_network, (ViewGroup) null);
        ((TextView) view.findViewById(R.id.scan_fav_network_str)).setVisibility(8);
        this.trdItemsListView = (CustListView) view.findViewById(R.id.scan_fav_network_list);
        this.mInactiveChannelList = TIFChannelManager.getInstance(this.mContext).getAttentionMaskChannels(TIFFunctionUtil.CH_CONFIRM_REMOVE_MASK, TIFFunctionUtil.CH_CONFIRM_REMOVE_VAL, -1);
        MtkLog.d(TAG, "inactiveChannelList>>>" + this.mInactiveChannelList.size());
        List<String[]> channelInfo = new ArrayList<>();
        int currentChannelID = this.mCommonInter.getCurrentChannelId();
        int size = this.mInactiveChannelList.size();
        List<SetConfigListViewAdapter.DataItem> dataItems = new ArrayList<>();
        int currentIndex = 0;
        for (int i = 0; i < size; i++) {
            TIFChannelInfo tempInfo = this.mInactiveChannelList.get(i);
            if (tempInfo.mMtkTvChannelInfo.getChannelId() == currentChannelID) {
                currentIndex = i;
            }
            String[] tempStr = new String[5];
            tempStr[0] = tempInfo.mDisplayNumber;
            if (tempInfo.mMtkTvChannelInfo instanceof MtkTvAnalogChannelInfo) {
                tempStr[1] = "Analog";
            } else if (tempInfo.mMtkTvChannelInfo instanceof MtkTvDvbChannelInfo) {
                MtkTvChannelInfoBase mtkTvChannelInfoBase = tempInfo.mMtkTvChannelInfo;
                tempStr[1] = "Digital";
            }
            tempStr[2] = tempInfo.mDisplayName;
            tempStr[3] = String.valueOf(tempInfo.mMtkTvChannelInfo.getChannelId());
            tempStr[4] = String.valueOf((float) tempInfo.mMtkTvChannelInfo.getFrequency());
            channelInfo.add(tempStr);
            dataItems.add(new SetConfigListViewAdapter.DataItem(MenuConfigManager.TV_CHANNEL_INACTIVE_LIST, " ", 10004, 10004, 10004, channelInfo.get(i), 1, SetConfigListViewAdapter.DataItem.DataType.CHANNELEUEDIT));
        }
        int gotoPage = (currentIndex / 10) + 1;
        int gotoPosition = currentIndex % 10;
        try {
            this.tRDAdapter = new SetConfigListViewAdapter(this.mContext);
            this.trdItemsListView.initData(dataItems, 10, this.update);
            this.tRDAdapter.setmGroup(this.trdItemsListView.getListWithPage(gotoPage));
            this.trdItemsListView.setAdapter(this.tRDAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tRDAdapter.setSelectPos(gotoPosition);
        this.trdItemsListView.setSelection(gotoPosition);
        this.trdItemsListView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    if (keyCode == 23) {
                        InactiveChannelDialog.this.cleanInactiveChannelConfirm(false, InactiveChannelDialog.this.trdItemsListView.getSelectedItemPosition());
                        return true;
                    } else if (keyCode == 4) {
                        InactiveChannelDialog.this.dismiss();
                        return true;
                    } else if (keyCode == 183 || keyCode == 46) {
                        MtkLog.d(InactiveChannelDialog.TAG, "KeyMap.KEYCODE_MTKIR_REDshow remove all confirm dialog");
                        InactiveChannelDialog.this.cleanInactiveChannelConfirm(true, -1);
                        return true;
                    }
                }
                return false;
            }
        });
        this.trdItemsListView.requestFocus();
        getWindow().setContentView(view);
    }

    /* access modifiers changed from: private */
    public void cleanInactiveChannelConfirm(final boolean isRemoveAll, final int removeOnePos) {
        String msg;
        if (isRemoveAll) {
            msg = this.mContext.getString(R.string.menu_tv_remove_all_inactive_channels);
        } else {
            msg = this.mContext.getString(R.string.menu_tv_remove_inactive_channel);
        }
        final TurnkeyCommDialog factroyCofirm = new TurnkeyCommDialog(this.mContext, 3);
        factroyCofirm.setMessage(msg);
        factroyCofirm.setButtonYesName(this.mContext.getString(R.string.menu_ok));
        factroyCofirm.setButtonNoName(this.mContext.getString(R.string.menu_cancel));
        factroyCofirm.show();
        factroyCofirm.setPositon(-20, 70);
        factroyCofirm.getButtonNo().requestFocus();
        factroyCofirm.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                factroyCofirm.dismiss();
                InactiveChannelDialog.this.trdItemsListView.requestFocus();
                return true;
            }
        });
        View.OnKeyListener listener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                if (v.getId() == factroyCofirm.getButtonYes().getId()) {
                    factroyCofirm.dismiss();
                    if (isRemoveAll) {
                        if (InactiveChannelDialog.this.mInactiveChannelList != null && InactiveChannelDialog.this.deleteAllInactiveChannels(InactiveChannelDialog.this.mInactiveChannelList)) {
                            InactiveChannelDialog.this.mInactiveChannelList.clear();
                        }
                    } else if (InactiveChannelDialog.this.mInactiveChannelList != null && removeOnePos < InactiveChannelDialog.this.mInactiveChannelList.size() && InactiveChannelDialog.this.deleteInactiveChannel(InactiveChannelDialog.this.mInactiveChannelList.get(removeOnePos).mMtkTvChannelInfo.getChannelId())) {
                        InactiveChannelDialog.this.mInactiveChannelList.remove(removeOnePos);
                    }
                    InactiveChannelDialog.this.dismiss();
                    if (InactiveChannelDialog.this.mInactiveChannelList == null || InactiveChannelDialog.this.mInactiveChannelList.size() == 0) {
                        return true;
                    }
                    InactiveChannelDialog.this.trdItemsListView.requestFocus();
                    return true;
                } else if (v.getId() != factroyCofirm.getButtonNo().getId()) {
                    return true;
                } else {
                    factroyCofirm.dismiss();
                    InactiveChannelDialog.this.trdItemsListView.requestFocus();
                    return true;
                }
            }
        };
        factroyCofirm.getButtonNo().setOnKeyListener(listener);
        factroyCofirm.getButtonYes().setOnKeyListener(listener);
    }

    public boolean deleteInactiveChannel(int channelId) {
        MtkTvChannelInfoBase selChannel = TIFChannelManager.getInstance(this.mContext).getAPIChannelInfoById(channelId);
        String str = TAG;
        MtkLog.d(str, "deleteInactiveChannel selChannel>>>" + selChannel);
        if (selChannel == null) {
            return false;
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(selChannel);
        List<TIFChannelInfo> tifChannelInfoList = null;
        if (channelId == this.mCommonInter.getCurrentChannelId()) {
            tifChannelInfoList = TIFChannelManager.getInstance(this.mContext).getTIFPreOrNextChannelList(channelId, false, false, 1, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        }
        this.mCommonInter.setChannelList(2, list);
        if (tifChannelInfoList != null && tifChannelInfoList.size() > 1) {
            TIFChannelManager.getInstance(this.mContext).selectChannelByTIFInfo(tifChannelInfoList.get(0));
        }
        return true;
    }

    public boolean deleteAllInactiveChannels(List<TIFChannelInfo> inactiveChannelList) {
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        for (TIFChannelInfo tempInfo : inactiveChannelList) {
            list.add(TIFChannelManager.getInstance(this.mContext).getAPIChannelInfoById(tempInfo.mMtkTvChannelInfo.getChannelId()));
        }
        String str = TAG;
        MtkLog.d(str, "deleteAllInactiveChannels list.size()>>>" + list.size());
        if (list.size() <= 0) {
            return false;
        }
        this.mCommonInter.setChannelList(2, list);
        return true;
    }

    public boolean deleteAllInactiveChannels() {
        return deleteAllInactiveChannels(TIFChannelManager.getInstance(this.mContext).getAttentionMaskChannels(TIFFunctionUtil.CH_CONFIRM_REMOVE_MASK, TIFFunctionUtil.CH_CONFIRM_REMOVE_VAL, -1));
    }
}
