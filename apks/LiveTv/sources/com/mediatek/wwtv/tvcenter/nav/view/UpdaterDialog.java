package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.mediatek.dm.DMNativeDaemonConnector;
import com.mediatek.twoworlds.tv.MtkTvUpgrade;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeFirmwareInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeItemInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.ProgressBarPlus;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpdaterDialog extends NavBasicDialog implements ComponentStatusListener.ICStatusListener {
    private static final String TAG = "Updater";
    private static int mHeight = DMNativeDaemonConnector.ResponseCode.CommandSyntaxError;
    private static final String mOadFirmwareName = "upgrade.pkg";
    private static final String mOadFirmwarePath1 = "/mnt/vendor/3rd/upgrade";
    private static final String mOadFirmwarePath2 = "/data/vendor/3rd_rw/upgrade";
    private static int mWidth = 1000;
    private MtkTvUpgradeDeliveryTypeBase eDeliveryType;
    private boolean isPower;
    private boolean isTVstart;
    /* access modifiers changed from: private */
    public InternalList1Adapter list1;
    /* access modifiers changed from: private */
    public int mCurrentStatus;
    private ImageView mImageExit;
    private ImageView mImageNext;
    private LinearLayout mLinearTab1;
    private LinearLayout mLinearTab2;
    /* access modifiers changed from: private */
    public ListView mListViewInfo;
    /* access modifiers changed from: private */
    public ListView mListViewYesNo;
    private ProgressBarPlus mProgressBar;
    private TextView mTxtExit;
    private TextView mTxtNext;
    private TextView mTxtStatusInfo;
    private TextView mTxtTips;
    private MtkTvUpgrade mUpgrade;

    public UpdaterDialog(Context context) {
        this(context, R.layout.nav_updater);
    }

    public UpdaterDialog(Context context, int theme) {
        super(context, theme);
        this.mCurrentStatus = -1;
        this.isPower = false;
        this.mUpgrade = MtkTvUpgrade.getInstance();
        this.eDeliveryType = MtkTvUpgradeDeliveryTypeBase.USB;
        this.mTxtStatusInfo = null;
        this.mListViewInfo = null;
        this.mListViewYesNo = null;
        this.mProgressBar = null;
        this.mTxtTips = null;
        this.mLinearTab1 = null;
        this.mLinearTab2 = null;
        this.mImageNext = null;
        this.mTxtNext = null;
        this.mImageExit = null;
        this.mTxtExit = null;
        this.isTVstart = true;
        this.componentID = NavBasic.NAV_COMP_ID_UPDATER;
        ComponentStatusListener.getInstance().addListener(3, this);
    }

    public void show() {
        if (this.eDeliveryType == MtkTvUpgradeDeliveryTypeBase.INTERNET) {
            try {
                InfoBarDialog.getInstance(this.mContext).show(0, this.mContext.getString(R.string.nav_upgrader_network_tip), 2000);
                this.mUpgrade.triggerUpgrade(this.eDeliveryType);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.show();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.mCurrentStatus = -1;
        initView();
        setWindowPosition();
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d(TAG, "KeyHandler: keyCode=" + keyCode);
        if (keyCode != 4) {
            switch (keyCode) {
            }
        } else if (this.mCurrentStatus == 10) {
            startupgrade(false);
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mCurrentStatus == 7 || this.mCurrentStatus == 8 || this.mCurrentStatus == 9) {
            if (event.getKeyCode() == 26) {
                Intent intent = new Intent("android.intent.action.REBOOT");
                intent.putExtra("nowait", 1);
                intent.putExtra("interval", 1);
                intent.putExtra("window", 0);
                this.mContext.sendBroadcast(intent);
                return true;
            } else if (event.getKeyCode() == 4) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean initView() {
        setContentView(R.layout.nav_updater);
        this.mTxtStatusInfo = (TextView) findViewById(R.id.nav_upgraderStatusInfo);
        this.mListViewInfo = (ListView) findViewById(R.id.nav_upgraderListDetailInfo);
        this.mListViewYesNo = (ListView) findViewById(R.id.nav_upgraderlistYesNo);
        this.mProgressBar = (ProgressBarPlus) findViewById(R.id.nav_upgraderProgressBar);
        this.mProgressBar.setMax(100);
        this.mTxtTips = (TextView) findViewById(R.id.nav_upgraderTips);
        this.mLinearTab1 = (LinearLayout) findViewById(R.id.nav_upgraderTable1);
        this.mLinearTab2 = (LinearLayout) findViewById(R.id.nav_upgraderTable2);
        this.mImageNext = (ImageView) findViewById(R.id.nav_upgraderNext_image);
        this.mTxtNext = (TextView) findViewById(R.id.nav_upgraderNext);
        this.mImageExit = (ImageView) findViewById(R.id.nav_upgraderExit_image);
        this.mTxtExit = (TextView) findViewById(R.id.nav_upgraderExit);
        YesNoListOnKey keyListener = new YesNoListOnKey();
        this.list1 = new InternalList1Adapter(this.mContext);
        this.mListViewInfo.setAdapter(this.list1);
        this.mListViewInfo.setOnKeyListener(keyListener);
        this.mListViewYesNo.setAdapter(new InternalList2Adapter(this.mContext));
        this.mListViewYesNo.setOnKeyListener(keyListener);
        return true;
    }

    public boolean setDeliveryType(MtkTvUpgradeDeliveryTypeBase deliveryType) {
        this.eDeliveryType = deliveryType;
        return true;
    }

    /* access modifiers changed from: private */
    public void startupgrade(boolean isUpgrade) {
        try {
            this.mUpgrade.startUpgrade(this.eDeliveryType, isUpgrade);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setWindowPosition() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (mWidth * ScreenConstant.SCREEN_WIDTH) / 1280;
        lp.height = (mHeight * ScreenConstant.SCREEN_HEIGHT) / 720;
        lp.x = 0;
        lp.y = 0;
        MtkLog.d(TAG, "ScreenConstant.SCREEN_WIDTH=" + ScreenConstant.SCREEN_WIDTH + ",ScreenConstant.SCREEN_HEIGHT=" + ScreenConstant.SCREEN_HEIGHT + ",lp.width=" + lp.width + "," + lp.x + ", lp.height=" + lp.height + "," + lp.y);
        window.setAttributes(lp);
    }

    /* access modifiers changed from: private */
    public void switchFocus(boolean isDetailInfoFocus) {
        if (isDetailInfoFocus) {
            this.mListViewYesNo.setFocusable(false);
            this.mListViewInfo.setFocusable(true);
            this.mListViewInfo.setSelection(0);
            this.mListViewInfo.requestFocus();
            return;
        }
        this.mListViewInfo.setFocusable(false);
        this.mListViewYesNo.setFocusable(true);
        this.mListViewYesNo.setSelection(0);
        this.mListViewYesNo.requestFocus();
    }

    public void modifyViewStatus(TvCallbackData data) {
        if (!this.mIsComponetShow) {
            show();
        }
        if (data.param1 == 0) {
            this.mCurrentStatus = data.param2;
            switch (data.param2) {
                case 0:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_validate));
                    break;
                case 1:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_validate));
                    break;
                case 2:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_validate));
                    break;
                case 3:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_validate));
                    break;
                case 4:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_confirm));
                    MtkTvUpgradeFirmwareInfoBase info = new MtkTvUpgradeFirmwareInfoBase();
                    this.mUpgrade.getFirmwareInfo(info);
                    ArrayList<MtkTvUpgradeItemInfoBase> infoList = info.getUpgradeItemList();
                    MtkLog.d(TAG, "infoList:" + infoList);
                    if (infoList != null) {
                        this.list1.resetContent();
                        Iterator<MtkTvUpgradeItemInfoBase> it = infoList.iterator();
                        while (it.hasNext()) {
                            MtkTvUpgradeItemInfoBase item = it.next();
                            this.list1.addItem(item.getName(), item.getVersion());
                            MtkLog.d(TAG, "item.getName():" + item.getName());
                        }
                    }
                    this.mListViewInfo.setAdapter(this.list1);
                    break;
                case 5:
                    MtkTvUpgradeFirmwareInfoBase info2 = new MtkTvUpgradeFirmwareInfoBase();
                    this.mUpgrade.getFirmwareInfo(info2);
                    ArrayList<MtkTvUpgradeItemInfoBase> infoList2 = info2.getUpgradeItemList();
                    MtkLog.d(TAG, "infoList:" + infoList2);
                    if (infoList2 != null) {
                        this.list1.resetContent();
                        Iterator<MtkTvUpgradeItemInfoBase> it2 = infoList2.iterator();
                        while (it2.hasNext()) {
                            MtkTvUpgradeItemInfoBase item2 = it2.next();
                            this.list1.addItem(item2.getName(), item2.getVersion());
                            MtkLog.d(TAG, "item.getName():" + item2.getName());
                        }
                    }
                    this.mListViewInfo.setAdapter(this.list1);
                    break;
                case 6:
                    this.mListViewInfo.setAdapter(this.list1);
                    break;
                case 7:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_completed));
                    this.mTxtExit.setText(this.mContext.getString(R.string.nav_upgrader_power));
                    this.mImageExit.setImageResource(R.drawable.tk_cm_key_power);
                    this.isPower = true;
                    break;
                case 8:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_failed));
                    this.mTxtExit.setText(this.mContext.getString(R.string.nav_upgrader_power));
                    this.mImageExit.setImageResource(R.drawable.tk_cm_key_power);
                    this.isPower = true;
                    break;
                case 9:
                    this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_failed));
                    this.mTxtExit.setText(this.mContext.getString(R.string.nav_upgrader_power));
                    this.mImageExit.setImageResource(R.drawable.tk_cm_key_power);
                    this.isPower = true;
                    break;
                case 10:
                    switch (data.param3) {
                        case 0:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_failed));
                            break;
                        case 1:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_usb_problem));
                            break;
                        case 2:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_download_fail));
                            break;
                        case 3:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_check_fail));
                            break;
                        case 4:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_no_update));
                            break;
                        case 5:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_decry_fail));
                            break;
                        case 6:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_firmware_invalid));
                            break;
                        case 7:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_not_avail_firmware));
                            break;
                        case 8:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_internal_err));
                            break;
                        case 9:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_usb_not_ready));
                            break;
                        case 10:
                            this.mTxtStatusInfo.setText(this.mContext.getString(R.string.nav_upgrader_usb_space_not_enough));
                            break;
                    }
            }
            switch (data.param2) {
                case 0:
                case 1:
                case 2:
                case 3:
                    this.mTxtStatusInfo.setVisibility(0);
                    this.mListViewInfo.setVisibility(4);
                    this.mListViewYesNo.setVisibility(8);
                    this.mProgressBar.setVisibility(8);
                    this.mTxtTips.setVisibility(8);
                    this.mLinearTab1.setVisibility(4);
                    this.mLinearTab2.setVisibility(4);
                    return;
                case 4:
                    this.mTxtStatusInfo.setVisibility(0);
                    this.mListViewInfo.setVisibility(0);
                    this.mListViewYesNo.setVisibility(0);
                    this.mProgressBar.setVisibility(8);
                    this.mTxtTips.setVisibility(8);
                    this.mLinearTab1.setVisibility(0);
                    this.mLinearTab2.setVisibility(0);
                    this.mImageNext.setVisibility(0);
                    this.mTxtNext.setVisibility(0);
                    switchFocus(false);
                    return;
                case 5:
                    this.mTxtStatusInfo.setVisibility(0);
                    this.mListViewInfo.setVisibility(0);
                    this.mListViewYesNo.setVisibility(8);
                    this.mProgressBar.setVisibility(0);
                    this.mTxtTips.setVisibility(0);
                    this.mLinearTab1.setVisibility(4);
                    this.mLinearTab2.setVisibility(4);
                    return;
                case 6:
                    this.mTxtStatusInfo.setVisibility(0);
                    this.mListViewInfo.setVisibility(0);
                    this.mListViewYesNo.setVisibility(8);
                    this.mProgressBar.setVisibility(0);
                    this.mTxtTips.setVisibility(0);
                    this.mLinearTab1.setVisibility(4);
                    this.mLinearTab2.setVisibility(4);
                    return;
                case 7:
                case 8:
                case 9:
                case 10:
                    this.mTxtStatusInfo.setVisibility(0);
                    this.mListViewInfo.setVisibility(4);
                    this.mListViewYesNo.setVisibility(8);
                    this.mProgressBar.setVisibility(8);
                    this.mTxtTips.setVisibility(8);
                    this.mLinearTab1.setVisibility(4);
                    this.mLinearTab2.setVisibility(0);
                    this.mImageNext.setVisibility(4);
                    this.mTxtNext.setVisibility(4);
                    this.mImageExit.setVisibility(0);
                    this.mTxtExit.setVisibility(0);
                    return;
                default:
                    return;
            }
        } else if (data.param1 == 1) {
            if (this.list1.getUpdatingIndex() != data.param2) {
                this.list1.setUpdatingItem(data.param2);
                this.mListViewInfo.setAdapter(this.list1);
                this.mListViewInfo.setFocusable(false);
            }
            this.mProgressBar.setProgress(data.param3);
            this.mTxtStatusInfo.setVisibility(0);
            this.mListViewInfo.setVisibility(0);
            this.mListViewYesNo.setVisibility(8);
            this.mProgressBar.setVisibility(0);
            this.mTxtTips.setVisibility(0);
            this.mLinearTab1.setVisibility(4);
            this.mLinearTab2.setVisibility(4);
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        if (statusID == 3 && this.isTVstart) {
            if (MarketRegionInfo.isFunctionSupport(10)) {
                MtkLog.d(TAG, "OAD check valid firmware~");
                if (new File("/mnt/vendor/3rd/upgrade/upgrade.pkg").exists() || new File("/data/vendor/3rd_rw/upgrade/upgrade.pkg").exists()) {
                    MtkLog.d(TAG, "OAD triggle upgrade~");
                    setDeliveryType(MtkTvUpgradeDeliveryTypeBase.OAD);
                    this.mUpgrade.triggerUpgrade(this.eDeliveryType);
                }
            }
            this.isTVstart = false;
        }
    }

    private class InternalList1Adapter extends BaseAdapter {
        private List<String> listDesc;
        private List<String> listName;
        private LayoutInflater mInflater;
        private int updatingIndex = -1;

        public InternalList1Adapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            this.updatingIndex = -1;
            this.listName = new ArrayList();
            this.listDesc = new ArrayList();
        }

        public boolean addItem(String name, String desc) {
            this.listName.add(name);
            this.listDesc.add(desc);
            return true;
        }

        public int getUpdatingIndex() {
            return this.updatingIndex;
        }

        public boolean setUpdatingItem(int index) {
            this.updatingIndex = index;
            return true;
        }

        public boolean resetContent() {
            this.listName.clear();
            this.listDesc.clear();
            this.updatingIndex = -1;
            return true;
        }

        public int getCount() {
            if (this.listName != null) {
                return this.listName.size();
            }
            return 0;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public String getItem(int position) {
            return this.listName.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder hodler;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_updater_detailinfo, (ViewGroup) null);
                hodler = new ViewHolder();
                hodler.mImageView = (ImageView) convertView.findViewById(R.id.nav_upgrader_detailinfo_image);
                hodler.mTextView = (TextView) convertView.findViewById(R.id.nav_upgrader_detailinfo_name);
                hodler.mTextViewDesc = (TextView) convertView.findViewById(R.id.nav_upgrader_detailinfo_desc);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHolder) convertView.getTag();
            }
            if (position == this.updatingIndex) {
                hodler.mImageView.setImageResource(R.drawable.nav_upgrader_downloading);
            } else {
                hodler.mImageView.setImageResource(0);
            }
            hodler.mTextView.setText(this.listName.get(position));
            hodler.mTextViewDesc.setText(this.listDesc.get(position));
            return convertView;
        }

        private class ViewHolder {
            ImageView mImageView;
            TextView mTextView;
            TextView mTextViewDesc;

            private ViewHolder() {
            }
        }
    }

    private class InternalList2Adapter extends BaseAdapter {
        private LayoutInflater mInflater;
        String[] mYesNo = {UpdaterDialog.this.mContext.getString(R.string.nav_upgrader_yes), UpdaterDialog.this.mContext.getString(R.string.nav_upgrader_no)};

        public InternalList2Adapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return 2;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public String getItem(int position) {
            if (this.mYesNo.length > position) {
                return this.mYesNo[position];
            }
            return "";
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.nav_updater_yesno, (ViewGroup) null);
                text = (TextView) convertView.findViewById(R.id.nav_upgrader_yesnotext);
                convertView.setTag(text);
            } else {
                text = (TextView) convertView.getTag();
            }
            text.setText(getItem(position));
            return convertView;
        }
    }

    private class YesNoListOnKey implements View.OnKeyListener {
        private YesNoListOnKey() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            MtkLog.d(UpdaterDialog.TAG, "onKeyDown: keyCode=" + keyCode);
            MtkLog.d(UpdaterDialog.TAG, "mListViewYesNo.getSelectedItemPosition()=" + UpdaterDialog.this.mListViewYesNo.getSelectedItemPosition());
            if (event.getAction() != 0) {
                return false;
            }
            if (keyCode != 165) {
                switch (keyCode) {
                    case 19:
                        if (v.equals(UpdaterDialog.this.mListViewInfo) && UpdaterDialog.this.mListViewInfo.getSelectedItemPosition() == 0) {
                            UpdaterDialog.this.mListViewInfo.setSelection(UpdaterDialog.this.list1.getCount() - 1);
                            return true;
                        }
                    case 20:
                        if (v.equals(UpdaterDialog.this.mListViewInfo) && UpdaterDialog.this.mListViewInfo.getSelectedItemPosition() + 1 == UpdaterDialog.this.list1.getCount()) {
                            UpdaterDialog.this.mListViewInfo.setSelection(0);
                            return true;
                        }
                    case 21:
                        if (v.equals(UpdaterDialog.this.mListViewInfo) && UpdaterDialog.this.mCurrentStatus == 4) {
                            UpdaterDialog.this.switchFocus(false);
                            break;
                        }
                    case 22:
                        if (v.equals(UpdaterDialog.this.mListViewYesNo)) {
                            TvCallbackData data = new TvCallbackData();
                            data.param1 = 0;
                            data.param2 = 5;
                            UpdaterDialog.this.modifyViewStatus(data);
                            UpdaterDialog.this.startupgrade(true);
                            break;
                        }
                        break;
                    case 23:
                        if (!v.equals(UpdaterDialog.this.mListViewYesNo) || ((ListView) v).getSelectedItemId() != 0) {
                            if (((ListView) v).getSelectedItemId() == 1) {
                                UpdaterDialog.this.dismiss();
                                break;
                            }
                        } else {
                            TvCallbackData data2 = new TvCallbackData();
                            data2.param1 = 0;
                            data2.param2 = 5;
                            UpdaterDialog.this.modifyViewStatus(data2);
                            UpdaterDialog.this.startupgrade(true);
                            break;
                        }
                        break;
                }
            } else if (v.equals(UpdaterDialog.this.mListViewYesNo)) {
                UpdaterDialog.this.switchFocus(true);
            } else {
                UpdaterDialog.this.switchFocus(false);
            }
            return false;
        }
    }
}
