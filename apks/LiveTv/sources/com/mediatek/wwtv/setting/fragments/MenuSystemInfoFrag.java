package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.wwtv.setting.base.scan.model.DVBSScanner;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.math.BigDecimal;
import java.util.List;

public class MenuSystemInfoFrag extends Fragment {
    public static final String REFRESH_RECEIVER = "com.mediatek.wwtv.setting.fragments.MenuSystemInfoFrag.RefreshReceiver";
    public static final String REFRESH_RECEIVER_BOOLEAN = "refresh_boolean";
    private Context context;
    private boolean isPositionView = false;
    private Action mAction;
    private View.OnKeyListener mBackListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == 0 && keyCode == 4) {
                return true;
            }
            return false;
        }
    };
    private LayoutInflater mInflater;
    /* access modifiers changed from: private */
    public ViewGroup mRootView;
    private TVContent mTVContent;
    String[] mduType;
    private TextView nLNBInfo;
    private TextView nSvcTypeInfo2;
    private final BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (MenuSystemInfoFrag.this.mRootView == null) {
                return;
            }
            if (intent.getBooleanExtra(MenuSystemInfoFrag.REFRESH_RECEIVER_BOOLEAN, false)) {
                MenuSystemInfoFrag.this.setValue(MenuSystemInfoFrag.this.signalquality);
            } else {
                MenuSystemInfoFrag.this.setValue(0);
            }
        }
    };
    private TableRow rowForSvcType;
    String[] scanMode;
    /* access modifiers changed from: private */
    public int signalquality;
    private TextView vAgcInfo;
    private TextView vChannelId;
    private TextView vCnInfo;
    private TextView vFreqInfo;
    private TextView vLNBInfo;
    private TextView vModulationInfo;
    private TextView vNetworkIDInfo;
    private TextView vOnidInfo;
    private TextView vPreInfo;
    private TextView vProgInfo;
    private TextView vServiceIdInfo;
    private TextView vSignalLevel;
    private ProgressBar vSignalProgress;
    private TextView vSignalQuality;
    private ProgressBar vSignalQualityProgress;
    private TextView vSvcTypeInfo;
    private TextView vSvcTypeInfo2;
    private TextView vSymbolrateInfo;
    private TextView vUecInfo;
    private TextView vtsInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        this.signalquality = EditChannel.getInstance(this.context).getSignalQuality();
        registerRefreshReceiver();
    }

    public void setAction(Action action) {
        this.mAction = action;
        if (action.mDataType == Action.DataType.POSITIONVIEW) {
            this.isPositionView = true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.menu_system_info, (ViewGroup) null);
        init();
        this.mRootView.setFocusable(true);
        this.mRootView.setFocusableInTouchMode(true);
        this.mRootView.setOnKeyListener(this.mBackListener);
        return this.mRootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!this.mRootView.hasFocus()) {
            this.mRootView.requestFocus();
        }
    }

    public void init() {
        this.mTVContent = TVContent.getInstance(this.context);
        this.scanMode = this.context.getResources().getStringArray(R.array.menu_tv_scan_mode_array);
        this.mduType = this.context.getResources().getStringArray(R.array.menu_tv_mdu_type_array);
        this.vChannelId = (TextView) this.mRootView.findViewById(R.id.common_system_info_channel_value);
        this.vSignalProgress = (ProgressBar) this.mRootView.findViewById(R.id.common_ps_signal_level);
        this.vSignalQualityProgress = (ProgressBar) this.mRootView.findViewById(R.id.common_ps_signal_qua);
        this.vSignalLevel = (TextView) this.mRootView.findViewById(R.id.common_tv_signal_level);
        this.vSignalQuality = (TextView) this.mRootView.findViewById(R.id.common_tv_signal_qua);
        this.vPreInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_pre_value);
        this.vFreqInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_freq_value);
        this.vCnInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_cn_value);
        this.vProgInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_prog_value);
        this.vUecInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_uec_value);
        this.vServiceIdInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_service_id_value);
        this.vSymbolrateInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_sysbolrate_value);
        this.vtsInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_tsid_value);
        this.vModulationInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_modulation_value);
        this.vOnidInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_onid_value);
        this.vAgcInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_agc_value);
        this.vNetworkIDInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_networkid_value);
        this.nLNBInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_lnb);
        this.vLNBInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_lnb_value);
        this.rowForSvcType = (TableRow) this.mRootView.findViewById(R.id.tablerow_for_svctype);
        this.nSvcTypeInfo2 = (TextView) this.mRootView.findViewById(R.id.common_system_info_svctype_2);
        this.vSvcTypeInfo2 = (TextView) this.mRootView.findViewById(R.id.common_system_info_svctype_value_2);
        this.vSvcTypeInfo = (TextView) this.mRootView.findViewById(R.id.common_system_info_svctype_value);
        if (this.mTVContent.getCurrentTunerMode() == 1) {
            this.vChannelId.setVisibility(4);
            ((TextView) this.mRootView.findViewById(R.id.common_signal_qua)).setVisibility(4);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_channel)).setVisibility(4);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_pre)).setText(R.string.menu_system_info_pre);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_cn)).setText(R.string.menu_system_info_cn);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_uec)).setText(R.string.menu_system_info_uec);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_sysbolrate)).setText(R.string.menu_tv_rf_sym_rate);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_modulation)).setText(R.string.menu_tv_sigle_modulation);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_agc)).setText(R.string.menu_system_info_symbol_agc);
        } else {
            this.vChannelId.setVisibility(0);
            ((TextView) this.mRootView.findViewById(R.id.common_signal_qua)).setVisibility(0);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_channel)).setVisibility(0);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_pre)).setText(R.string.menu_system_info_symbol_pre_viterbi);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_cn)).setText(R.string.menu_system_info_cn);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_uec)).setText(R.string.menu_system_info_uec);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_sysbolrate)).setText(R.string.menu_system_info_symbol_post_viterbi);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_modulation)).setText(R.string.menu_system_info_symbol_5s);
            ((TextView) this.mRootView.findViewById(R.id.common_system_info_agc)).setText(R.string.menu_system_info_symbol_agc);
        }
        setValue(this.signalquality);
    }

    public void setValue(int signalquality2) {
        boolean z;
        int modu;
        int i = signalquality2;
        int signalLevel = EditChannel.getInstance(this.context).getSignalLevel();
        this.vSignalProgress.setProgress(signalLevel);
        this.vSignalLevel.setText(signalLevel + "%");
        this.vSignalQualityProgress.setProgress(i);
        this.vSignalQuality.setText(i + "%");
        MtkTvAppTVBase app = new MtkTvAppTVBase();
        String path = CommonIntegration.getInstance().getCurrentFocus();
        MtkLog.d("SystemInfo", "path:" + path);
        int bervalue = app.GetConnAttrBER(path);
        if (bervalue < 0) {
            bervalue = -bervalue;
        }
        MtkLog.d("SystemInfo", "app.GetConnAttrDBMSNR(path):" + app.GetConnAttrDBMSNR(path));
        MtkLog.d("SystemInfo", "app.GetConnAttrUEC(path):" + app.GetConnAttrUEC(path));
        if (this.mTVContent.getCurrentTunerMode() == 1) {
            this.vSignalQualityProgress.setVisibility(4);
            this.vSignalQuality.setVisibility(4);
            this.vPreInfo.setText(String.format("%3.2e", new Object[]{Double.valueOf(((double) bervalue) / 100000.0d)}));
            this.vCnInfo.setText(String.format("%02d", new Object[]{Long.valueOf(app.GetConnAttrDBMSNR(path))}));
            this.vUecInfo.setText(String.format("%04d", new Object[]{Long.valueOf(app.GetConnAttrUEC(path))}));
            this.vSymbolrateInfo.setText(String.format("%07d", new Object[]{Long.valueOf(app.GetSymRate(path))}));
            MtkLog.d("SystemInfo", "app.GetSymRate(path):" + app.GetSymRate(path));
            MtkLog.d("SystemInfo", "app.GetModulation(path):" + app.GetModulation(path));
            int modu2 = app.GetModulation(path);
            if (modu2 != 4 && modu2 != 5 && modu2 != 6 && modu2 != 10 && modu2 != 14) {
                modu = 0;
            } else if (modu2 <= 6) {
                modu = modu2 - 3;
            } else if (modu2 == 10) {
                modu = 4;
            } else {
                modu = 5;
            }
            this.vModulationInfo.setText(this.scanMode[modu]);
            int i2 = modu;
            this.vAgcInfo.setText(String.format("%03d", new Object[]{Long.valueOf(app.GetConnAttrAGC(path))}));
            z = false;
        } else {
            this.vSignalQualityProgress.setVisibility(0);
            this.vSignalQuality.setVisibility(0);
            this.vPreInfo.setText(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            this.vCnInfo.setText(String.format("%02d", new Object[]{Long.valueOf(app.GetConnAttrDBMSNR(path))}));
            this.vUecInfo.setText(String.format("%04d", new Object[]{Long.valueOf(app.GetConnAttrUEC(path))}));
            String berString = String.format("%3.2e", new Object[]{Double.valueOf(((double) bervalue) / 100000.0d)});
            this.vSymbolrateInfo.setText(berString);
            this.vModulationInfo.setText(berString);
            z = false;
            this.vAgcInfo.setText(String.format("%03d", new Object[]{Long.valueOf(app.GetConnAttrAGC(path))}));
        }
        MtkTvChannelInfoBase info = CommonIntegration.getInstance().getCurChInfo();
        if (TVContent.getInstance(this.context).getCurrentTunerMode() >= 2) {
            z = true;
        }
        boolean isDVBS = z;
        if (info == null || !(info instanceof MtkTvDvbChannelInfo)) {
            if (info != null) {
                if (info instanceof MtkTvAnalogChannelInfo) {
                    MtkLog.d("SystemInfo", "this is analog channel");
                }
                BigDecimal bigDecimal = new BigDecimal((double) (((float) info.getFrequency()) / 1000000.0f));
                TextView textView = this.vFreqInfo;
                textView.setText(("" + bigDecimal.setScale(1, 4).floatValue()) + "MHz");
                this.vProgInfo.setText(info.getChannelNumber() + "");
            } else {
                this.vFreqInfo.setText(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                this.vProgInfo.setText(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            }
            this.vServiceIdInfo.setText("0x0000");
            this.vtsInfo.setText("0x0000");
            this.vOnidInfo.setText("0x0000");
            this.vNetworkIDInfo.setText("0x0000");
            this.nLNBInfo.setVisibility(4);
            this.vLNBInfo.setVisibility(4);
            return;
        }
        MtkTvDvbChannelInfo channel = (MtkTvDvbChannelInfo) info;
        if (this.vChannelId != null) {
            if ("".equals(channel.getShortName()) || channel.getShortName() == null) {
                this.vChannelId.setText("SO");
            } else {
                this.vChannelId.setText("" + channel.getShortName());
            }
        }
        MtkLog.d("SystemInfo", "freq==" + channel.getFrequency());
        if (!isDVBS) {
            BigDecimal bigDecimal2 = new BigDecimal((double) (((float) channel.getFrequency()) / 1000000.0f));
            TextView textView2 = this.vFreqInfo;
            textView2.setText(("" + bigDecimal2.setScale(1, 4).floatValue()) + "MHz");
        } else {
            this.vFreqInfo.setText(((float) channel.getFrequency()) + "MHz");
        }
        this.vProgInfo.setText(info.getChannelNumber() + "");
        this.vServiceIdInfo.setText(parseHex(channel.getSvlRecId()));
        this.vtsInfo.setText(parseHex(channel.getTsId()));
        this.vOnidInfo.setText(parseHex(channel.getOnId()));
        this.vNetworkIDInfo.setText(parseHex(channel.getNwId()));
        if (DVBSScanner.isMDUScanMode()) {
            MtkLog.d("SystemInfo", "this is turkey logo ");
            int type = checkLNBInfo(channel);
            if (type == -1) {
                this.vLNBInfo.setText(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            } else {
                this.vLNBInfo.setText(this.mduType[type]);
            }
        } else {
            MtkLog.d("SystemInfo", "not turkey logo");
            this.nLNBInfo.setVisibility(4);
            this.vLNBInfo.setVisibility(4);
        }
        checkSvcType(channel);
    }

    private String parseHex(int parsevalue) {
        String hexValue = Integer.toHexString(parsevalue);
        switch (hexValue.length()) {
            case 0:
                return "0x0000";
            case 1:
                return "0x000" + hexValue;
            case 2:
                return "0x00" + hexValue;
            case 3:
                return "0x0" + hexValue;
            case 4:
                return "0x" + hexValue;
            default:
                return "0x0000";
        }
    }

    private int checkLNBInfo(MtkTvDvbChannelInfo mCurrentChannel) {
        MtkTvDvbsConfigBase mSatl = new MtkTvDvbsConfigBase();
        int svlID = CommonIntegration.getInstance().getSvl();
        int satRecId = mCurrentChannel.getSatRecId();
        MtkLog.d("SystemInfo", "svlID is ==" + svlID + ",satRecId==" + satRecId);
        List<MtkTvDvbsConfigInfoBase> list = mSatl.getSatlRecord(svlID, satRecId);
        if (list == null || list.size() <= 0) {
            return -1;
        }
        int type = list.get(0).getMduType();
        MtkLog.d("SystemInfo", "type is " + type);
        return type;
    }

    private boolean checkSvcType(MtkTvDvbChannelInfo mCurrentChannel) {
        if (mCurrentChannel.getSdtServiceType() != 31) {
            return false;
        }
        if (this.nLNBInfo.isShown()) {
            this.rowForSvcType.setVisibility(0);
            this.vSvcTypeInfo.setText("0x1fHEVC TV");
            return true;
        }
        this.nSvcTypeInfo2.setVisibility(0);
        this.vSvcTypeInfo2.setVisibility(0);
        this.vSvcTypeInfo2.setText("0x1fHEVC TV");
        return true;
    }

    private void registerRefreshReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REFRESH_RECEIVER);
        this.context.registerReceiver(this.refreshReceiver, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        this.context.unregisterReceiver(this.refreshReceiver);
    }
}
