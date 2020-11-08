package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.wwtv.setting.base.scan.model.DVBSScanner;
import com.mediatek.wwtv.setting.preferences.ProgressPreference;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.math.BigDecimal;
import java.util.List;

public class MenuSystemInfo {
    private static final String TAG = "MenuSystemInfo";
    Context mContext;
    PreferenceScreen mPreferenceScreen;
    /* access modifiers changed from: private */
    public Handler mRefreshHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            MenuSystemInfo.this.setValue(msg.arg1, msg.arg2);
            MenuSystemInfo.this.sendMessageDelayedThread(1, 1000);
        }
    };
    private TVContent mTVContent;
    String[] mduType;
    private boolean mvLNBInfoShow = false;
    private Preference nSvcTypeInfo2;
    String[] scanMode;
    private int signalquality;
    private Preference vAgcInfo;
    private Preference vChannelId;
    private Preference vCnInfo;
    private Preference vFreqInfo;
    private Preference vLNBInfo;
    private Preference vModulationInfo;
    private Preference vNetworkIDInfo;
    private Preference vOnidInfo;
    private Preference vPreInfo;
    private Preference vProgInfo;
    private Preference vServiceIdInfo;
    private ProgressPreference vSignalProgress;
    private ProgressPreference vSignalQualityProgress;
    private Preference vSvcTypeInfo;
    private Preference vSvcTypeInfo2;
    private Preference vSymbolrateInfo;
    private Preference vUecInfo;
    private Preference vtsInfo;

    /* access modifiers changed from: private */
    public void sendMessageDelayedThread(final int what, final long delayMillis) {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                int level = EditChannel.getInstance(MenuSystemInfo.this.mContext).getSignalLevel();
                int quality = EditChannel.getInstance(MenuSystemInfo.this.mContext).getSignalQuality();
                Message msg = Message.obtain();
                msg.what = what;
                msg.arg1 = level;
                msg.arg2 = quality;
                MenuSystemInfo.this.mRefreshHandler.sendMessageDelayed(msg, delayMillis);
            }
        });
    }

    public MenuSystemInfo(Context context) {
        this.mContext = context;
    }

    public PreferenceScreen getPreferenceScreen(PreferenceScreen preferenceScreen) {
        init(preferenceScreen, this.mContext);
        return preferenceScreen;
    }

    public void init(PreferenceScreen preferenceScreen, Context context) {
        this.mPreferenceScreen = preferenceScreen;
        this.mPreferenceScreen.removeAll();
        this.vSignalProgress = new ProgressPreference(context);
        this.vSignalQualityProgress = new ProgressPreference(context);
        this.vChannelId = new Preference(context);
        this.vPreInfo = new Preference(context);
        this.vFreqInfo = new Preference(context);
        this.vCnInfo = new Preference(context);
        this.vProgInfo = new Preference(context);
        this.vUecInfo = new Preference(context);
        this.vServiceIdInfo = new Preference(context);
        this.vSymbolrateInfo = new Preference(context);
        this.vtsInfo = new Preference(context);
        this.vModulationInfo = new Preference(context);
        this.vOnidInfo = new Preference(context);
        this.vAgcInfo = new Preference(context);
        this.vNetworkIDInfo = new Preference(context);
        this.vLNBInfo = new Preference(context);
        this.vSvcTypeInfo2 = new Preference(context);
        this.vSvcTypeInfo = new Preference(context);
        this.signalquality = EditChannel.getInstance(this.mContext).getSignalQuality();
        this.mTVContent = TVContent.getInstance(this.mContext);
        this.scanMode = this.mContext.getResources().getStringArray(R.array.menu_tv_scan_mode_array);
        this.mduType = this.mContext.getResources().getStringArray(R.array.menu_tv_mdu_type_array);
        this.vSignalProgress.setMinValue(0);
        this.vSignalProgress.setMaxValue(100);
        this.vSignalProgress.setKey("vSignalProgress");
        this.vSignalProgress.setTitle((int) R.string.menu_tv_single_signal_level);
        this.vSignalProgress.setClickable(false);
        this.mPreferenceScreen.addPreference(this.vSignalProgress);
        this.vSignalQualityProgress.setKey("vSignalQualityProgress");
        this.vSignalQualityProgress.setMinValue(0);
        this.vSignalQualityProgress.setMaxValue(100);
        this.vSignalQualityProgress.setTitle((int) R.string.menu_tv_single_signal_quality);
        this.vSignalQualityProgress.setClickable(false);
        this.mPreferenceScreen.addPreference(this.vSignalQualityProgress);
        this.vChannelId.setKey("vChannelId");
        this.vChannelId.setTitle((int) R.string.menu_system_info_channel_title);
        this.mPreferenceScreen.addPreference(this.vChannelId);
        this.vPreInfo.setKey("vPreInfo");
        this.vPreInfo.setTitle((int) R.string.menu_system_info_pre);
        this.mPreferenceScreen.addPreference(this.vPreInfo);
        this.vFreqInfo.setKey("vFreqInfo");
        this.vFreqInfo.setTitle((int) R.string.menu_setup_biss_key_freqency);
        this.mPreferenceScreen.addPreference(this.vFreqInfo);
        this.vCnInfo.setKey("vCnInfo");
        this.vCnInfo.setTitle((int) R.string.menu_system_info_cn);
        this.mPreferenceScreen.addPreference(this.vCnInfo);
        this.vProgInfo.setKey("vProgInfo");
        this.vProgInfo.setTitle((int) R.string.menu_system_info_prog);
        this.mPreferenceScreen.addPreference(this.vProgInfo);
        this.vUecInfo.setKey("vUecInfo");
        this.vUecInfo.setTitle((int) R.string.menu_system_info_uec);
        this.mPreferenceScreen.addPreference(this.vUecInfo);
        this.vServiceIdInfo.setKey("vServiceIdInfo");
        this.vServiceIdInfo.setTitle((int) R.string.menu_system_info_service_id);
        this.mPreferenceScreen.addPreference(this.vServiceIdInfo);
        this.vSymbolrateInfo.setKey("vSymbolrateInfo");
        this.vSymbolrateInfo.setTitle((int) R.string.menu_tv_rf_sym_rate);
        this.mPreferenceScreen.addPreference(this.vSymbolrateInfo);
        this.vtsInfo.setKey("vtsInfo");
        this.vtsInfo.setTitle((int) R.string.menu_system_info_symbol_tsid);
        this.mPreferenceScreen.addPreference(this.vtsInfo);
        this.vModulationInfo.setKey("vModulationInfo");
        this.vModulationInfo.setTitle((int) R.string.menu_tv_sigle_modulation);
        this.mPreferenceScreen.addPreference(this.vModulationInfo);
        this.vOnidInfo.setKey("vOnidInfo");
        this.vOnidInfo.setTitle((int) R.string.menu_system_info_symbol_onid);
        this.mPreferenceScreen.addPreference(this.vOnidInfo);
        this.vAgcInfo.setKey("vAgcInfo");
        this.vAgcInfo.setTitle((int) R.string.menu_system_info_symbol_agc);
        this.mPreferenceScreen.addPreference(this.vAgcInfo);
        this.vNetworkIDInfo.setKey("vNetworkIDInfo");
        this.vNetworkIDInfo.setTitle((int) R.string.menu_c_net);
        this.mPreferenceScreen.addPreference(this.vNetworkIDInfo);
        this.vLNBInfo.setKey("vLNBInfo");
        this.vLNBInfo.setTitle((int) R.string.menu_system_info_symbol_lnb);
        this.mPreferenceScreen.addPreference(this.vLNBInfo);
        this.mvLNBInfoShow = true;
        this.vSvcTypeInfo2.setKey("vSvcTypeInfo2");
        this.vSvcTypeInfo2.setTitle((int) R.string.menu_system_info_symbol_svctype);
        this.mPreferenceScreen.addPreference(this.vSvcTypeInfo2);
        this.vSvcTypeInfo.setKey("vSvcTypeInfo");
        this.vSvcTypeInfo.setTitle((int) R.string.menu_system_info_symbol_svctype);
        this.mPreferenceScreen.addPreference(this.vSvcTypeInfo);
        if (this.mTVContent.getCurrentTunerMode() == 1) {
            this.mPreferenceScreen.removePreference(this.vChannelId);
            this.vPreInfo.setTitle((int) R.string.menu_system_info_pre);
            this.vSymbolrateInfo.setTitle((int) R.string.menu_tv_rf_sym_rate);
            this.vModulationInfo.setTitle((int) R.string.menu_tv_sigle_modulation);
        } else {
            this.vPreInfo.setTitle((int) R.string.menu_system_info_symbol_pre_viterbi);
            this.vSymbolrateInfo.setTitle((int) R.string.menu_system_info_symbol_post_viterbi);
            this.vModulationInfo.setTitle((int) R.string.menu_system_info_symbol_5s);
        }
        setValue(EditChannel.getInstance(this.mContext).getSignalLevel(), this.signalquality);
        this.mRefreshHandler.removeMessages(1);
        sendMessageDelayedThread(1, 1000);
    }

    public void setValue(int level, int signalquality2) {
        int modu;
        int i = signalquality2;
        int signalLevel = level;
        MtkLog.d(TAG, "setValue signalquality=" + i + ",level=" + signalLevel);
        if (this.vSignalProgress != null) {
            this.vSignalProgress.setValue(signalLevel);
        }
        if (this.vSignalQualityProgress != null) {
            this.vSignalQualityProgress.setValue(i);
        }
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
            this.mPreferenceScreen.removePreference(this.vSignalQualityProgress);
            this.vPreInfo.setSummary((CharSequence) String.format("%3.2e", new Object[]{Double.valueOf(((double) bervalue) / 100000.0d)}));
            this.vCnInfo.setSummary((CharSequence) String.format("%02d", new Object[]{Long.valueOf(app.GetConnAttrDBMSNR(path))}));
            this.vUecInfo.setSummary((CharSequence) String.format("%04d", new Object[]{Long.valueOf(app.GetConnAttrUEC(path))}));
            this.vSymbolrateInfo.setSummary((CharSequence) String.format("%07d", new Object[]{Long.valueOf(app.GetSymRate(path))}));
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
            this.vModulationInfo.setSummary((CharSequence) this.scanMode[modu]);
            int i2 = modu;
            this.vAgcInfo.setSummary((CharSequence) String.format("%03d", new Object[]{Long.valueOf(app.GetConnAttrAGC(path))}));
        } else {
            this.vPreInfo.setSummary((CharSequence) MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            this.vCnInfo.setSummary((CharSequence) String.format("%02d", new Object[]{Long.valueOf(app.GetConnAttrDBMSNR(path))}));
            this.vUecInfo.setSummary((CharSequence) String.format("%04d", new Object[]{Long.valueOf(app.GetConnAttrUEC(path))}));
            String berString = String.format("%3.2e", new Object[]{Double.valueOf(((double) bervalue) / 100000.0d)});
            this.vSymbolrateInfo.setSummary((CharSequence) berString);
            this.vModulationInfo.setSummary((CharSequence) berString);
            this.vAgcInfo.setSummary((CharSequence) String.format("%03d", new Object[]{Long.valueOf(app.GetConnAttrAGC(path))}));
        }
        MtkTvChannelInfoBase info = CommonIntegration.getInstance().getCurChInfo();
        boolean isDVBS = TVContent.getInstance(this.mContext).getCurrentTunerMode() >= 2;
        if (info == null || !(info instanceof MtkTvDvbChannelInfo)) {
            if (info != null) {
                if (info instanceof MtkTvAnalogChannelInfo) {
                    MtkLog.d("SystemInfo", "this is analog channel");
                }
                BigDecimal bigDecimal = new BigDecimal((double) (((float) info.getFrequency()) / 1000000.0f));
                Preference preference = this.vFreqInfo;
                preference.setSummary((CharSequence) ("" + bigDecimal.setScale(1, 4).floatValue()) + "MHz");
                this.vProgInfo.setSummary((CharSequence) info.getChannelNumber() + "");
            } else {
                this.vFreqInfo.setSummary((CharSequence) MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                this.vProgInfo.setSummary((CharSequence) MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            }
            this.vServiceIdInfo.setSummary((CharSequence) "0x0000");
            this.vtsInfo.setSummary((CharSequence) "0x0000");
            this.vOnidInfo.setSummary((CharSequence) "0x0000");
            this.vNetworkIDInfo.setSummary((CharSequence) "0x0000");
            this.mPreferenceScreen.removePreference(this.vLNBInfo);
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo);
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo2);
            this.mvLNBInfoShow = false;
            return;
        }
        MtkTvDvbChannelInfo channel = (MtkTvDvbChannelInfo) info;
        if (this.vChannelId != null) {
            String rfChannelName = TVContent.getInstance(this.mContext).getRFChannel(0);
            this.vChannelId.setSummary((CharSequence) "" + rfChannelName);
        }
        MtkLog.d("SystemInfo", "freq==" + channel.getFrequency());
        if (!isDVBS) {
            BigDecimal bigDecimal2 = new BigDecimal((double) (((float) channel.getFrequency()) / 1000000.0f));
            Preference preference2 = this.vFreqInfo;
            preference2.setSummary((CharSequence) ("" + bigDecimal2.setScale(1, 4).floatValue()) + "MHz");
        } else {
            this.vFreqInfo.setSummary((CharSequence) ((float) channel.getFrequency()) + "MHz");
        }
        this.vProgInfo.setSummary((CharSequence) info.getChannelNumber() + "");
        this.vServiceIdInfo.setSummary((CharSequence) parseHex(channel.getSvlRecId()));
        this.vtsInfo.setSummary((CharSequence) parseHex(channel.getTsId()));
        this.vOnidInfo.setSummary((CharSequence) parseHex(channel.getOnId()));
        this.vNetworkIDInfo.setSummary((CharSequence) parseHex(channel.getNwId()));
        if (DVBSScanner.isMDUScanMode()) {
            MtkLog.d("SystemInfo", "this is turkey logo ");
            int type = checkLNBInfo(channel);
            if (type == -1) {
                this.vLNBInfo.setSummary((CharSequence) MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
            } else {
                this.vLNBInfo.setSummary((CharSequence) this.mduType[type]);
            }
        } else {
            MtkLog.d("SystemInfo", "not turkey logo");
            this.mPreferenceScreen.removePreference(this.vLNBInfo);
            this.mvLNBInfoShow = false;
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
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo);
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo2);
            return false;
        } else if (this.mvLNBInfoShow) {
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo2);
            this.vSvcTypeInfo.setSummary((CharSequence) "0x1fHEVC TV");
            return true;
        } else {
            this.mPreferenceScreen.removePreference(this.vSvcTypeInfo);
            this.vSvcTypeInfo2.setSummary((CharSequence) "0x1fHEVC TV");
            return true;
        }
    }
}
