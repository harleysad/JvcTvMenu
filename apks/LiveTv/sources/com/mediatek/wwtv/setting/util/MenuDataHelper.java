package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvBisskeyBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScanCeBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.setting.base.scan.adapter.BissListAdapter;
import com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter;
import com.mediatek.wwtv.setting.base.scan.adapter.TkgsLocatorListAdapter;
import com.mediatek.wwtv.setting.base.scan.model.CableOperator;
import com.mediatek.wwtv.setting.base.scan.model.CountrysIndex;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.detailui.ActionFragment;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuDataHelper {
    public static final String SLEEP_TIMER_ACTION = "com.mediatek.ui.menu.util.sleep.timer";
    public static final String TAG = "MenuDataHelper";
    protected static MenuDataHelper mSelf;
    final int NORMALPAGE_SIZE = 10;
    public int TKGSVisibleLocSize = 0;
    private int chNum = 0;
    private List<MtkTvChannelInfoBase> ch_list = null;
    List<String[]> channelInfo;
    int gotoPage = 0;
    int gotoPosition = 0;
    Map<Integer, String> mChNumberMap = new HashMap();
    Map<String, Action> mChannelActionMap;
    Map<String, Preference> mChannelPreferenceMap;
    CommonIntegration mCommonIntegration;
    protected MenuConfigManager mConfigManager;
    Context mContext;
    TVContent mTV;
    List<TkgsLocatorListAdapter.TkgsLocatorItem> tkgsList = new ArrayList();
    List<MtkTvScanDvbsBase.TKGSOneSvcList> tkgsSvcList = null;
    int tkgsSvcListSelPos = 0;
    String tkgsUserMessage = null;

    private MenuDataHelper(Context context) {
        this.mContext = context;
        this.mConfigManager = MenuConfigManager.getInstance(context);
        this.mChannelActionMap = new HashMap();
        this.mChannelPreferenceMap = new HashMap();
        this.mTV = TVContent.getInstance(this.mContext);
        this.mCommonIntegration = CommonIntegration.getInstance();
    }

    public static MenuDataHelper getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new MenuDataHelper(context);
        }
        return mSelf;
    }

    public Map<String, Action> getChannelActionMap() {
        return this.mChannelActionMap;
    }

    public Map<String, Preference> getChannelPreferenceMap() {
        return this.mChannelPreferenceMap;
    }

    public int[] getEffectGroupInitValues(Action mEffectParentAction) {
        if (mEffectParentAction == null || mEffectParentAction.getmDataType() != Action.DataType.EFFECTOPTIONVIEW) {
            throw new IllegalArgumentException("type of mEffectParentAction is not EFFECTOPTIONVIEW or mEffectParentAction is null");
        }
        int[] mEffectGroupInitValues = new int[mEffectParentAction.getmEffectGroup().size()];
        int i = 0;
        for (Action childAction : mEffectParentAction.getmEffectGroup()) {
            mEffectGroupInitValues[i] = this.mConfigManager.getDefault(childAction.getmItemId());
            i++;
        }
        return mEffectGroupInitValues;
    }

    public void dealSwitchChildGroupEnable(Action mainAction) {
        List<Action> mChildGroup = mainAction.mEffectGroup;
        if ("g_video__vid_3d_mode".equals(mainAction.mItemID)) {
            ArrayList<Boolean> m3DConfigList = MenuConfigManager.getInstance(this.mContext).get3DConfig();
            mainAction.setEnabled(m3DConfigList.get(0).booleanValue());
            MtkLog.d("OptionView", "mAction:" + mainAction.mItemID + ",isEnable:" + mainAction.isEnabled());
            int i = 1;
            for (Action childItem : mChildGroup) {
                childItem.setEnabled(m3DConfigList.get(i).booleanValue());
                MtkLog.d("OptionView", "childItem:" + childItem + "childItem.isEnable:" + childItem.isEnabled());
                i++;
            }
            return;
        }
        Boolean[] isEnables = mainAction.getmSwitchHashMap().get(Integer.valueOf(mainAction.mInitValue));
        if (isEnables != null) {
            MtkLog.d("SwitchOptionView", "isEnables[0]==:" + isEnables[0]);
        }
        int i2 = 0;
        for (Action childItem2 : mChildGroup) {
            if (isEnables != null) {
                int i3 = i2 + 1;
                childItem2.setEnabled(isEnables[i2].booleanValue());
                MtkLog.d(TAG, "childItem.isEnbaled:" + childItem2.mItemID + "isEnable:" + childItem2.isEnabled());
                if (childItem2.isEnabled() && childItem2.mItemID.equals(MenuConfigManager.SETUP_POWER_ON_CH) && this.mCommonIntegration.getChannelAllNumByAPI() <= 0) {
                    childItem2.setEnabled(false);
                }
                if ("g_audio__aud_type".equals(mainAction.mItemID) && mainAction.mInitValue == 2) {
                    MtkLog.d("SwitchOptionView", "SwitchOptionView mItemID:" + childItem2.mItemID + "isEnable:" + childItem2.isEnabled() + "mInitValue:" + mainAction.mInitValue);
                    if (CommonIntegration.isEURegion()) {
                        if (TVContent.getInstance(this.mContext).iCurrentInputSourceHasSignal()) {
                            childItem2.setEnabled(true);
                        } else {
                            childItem2.setEnabled(false);
                        }
                    }
                }
                i2 = i3;
            }
        }
    }

    public void loadTuneDiagInfo(Action diagAction, boolean flag) {
        Action action = diagAction;
        if (!flag) {
            List<String> displayName = new ArrayList<>();
            List<String> displayValue = new ArrayList<>();
            if (!this.mCommonIntegration.isCurrentSourceTv() || this.mCommonIntegration.isCurrentSourceDTV()) {
                MtkLog.d(TAG, "loadTuneDiagInfo not show");
                MtkTvUtil.getInstance().tunerFacQuery(false, displayName, displayValue);
            } else {
                MtkLog.d(TAG, "loadTuneDiagInfo show atv ==" + this.mCommonIntegration.isCurrentSourceATV());
                MtkTvUtil.getInstance().tunerFacQuery(true, displayName, displayValue);
            }
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= displayName.size()) {
                    break;
                }
                Action fvEventForm = new Action(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO, displayName.get(i2), 10004, 10004, 0, new String[]{displayValue.get(i2)}, 1, Action.DataType.FACTORYOPTIONVIEW);
                fvEventForm.hasRealChild = false;
                action.mSubChildGroup.add(fvEventForm);
                i = i2 + 1;
            }
            if (displayName.size() == 0) {
                Action fvEventForm2 = new Action(MenuConfigManager.FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO, this.mContext.getString(R.string.menu_factory_TV_tunerdiag_noinfo), 10004, 10004, 0, this.mContext.getResources().getStringArray(R.array.menu_setup_null_array), 1, Action.DataType.FACTORYOPTIONVIEW);
                fvEventForm2.hasRealChild = false;
                action.mSubChildGroup.add(fvEventForm2);
            }
        }
    }

    public void changePreferenceEnable() {
        MtkLog.d(TAG, "start changeEnable");
        Preference channelskip = this.mChannelPreferenceMap.get("channelskip");
        Preference channelSort = this.mChannelPreferenceMap.get("channelSort");
        Preference channelEdit = this.mChannelPreferenceMap.get("channelEdit");
        Preference cleanList = this.mChannelPreferenceMap.get("cleanList");
        Preference saChannelEdit = this.mChannelPreferenceMap.get("saChannelEdit");
        Preference saChannelFine = this.mChannelPreferenceMap.get("saChannelFine");
        boolean isTKGS = true;
        if (CommonIntegration.isCNRegion()) {
            if (!this.mCommonIntegration.hasActiveChannel() || this.mCommonIntegration.is3rdTVSource()) {
                MtkLog.d(TAG, "changeEnable false");
                if (!(channelEdit == null || cleanList == null)) {
                    channelEdit.setEnabled(false);
                    cleanList.setEnabled(false);
                }
            } else {
                MtkLog.d(TAG, "changeEnable true");
                if (!(channelEdit == null || cleanList == null)) {
                    channelEdit.setEnabled(true);
                    cleanList.setEnabled(true);
                }
            }
        } else if (channelskip == null) {
            MtkLog.d(TAG, "end changeEnable null return");
            return;
        } else if (!this.mCommonIntegration.hasActiveChannel() || this.mCommonIntegration.is3rdTVSource()) {
            MtkLog.d(TAG, "changeEnable false");
            channelskip.setEnabled(false);
            if (!(!CommonIntegration.isSARegion() || saChannelEdit == null || saChannelFine == null)) {
                saChannelEdit.setEnabled(false);
                saChannelFine.setEnabled(false);
            }
            if (CommonIntegration.isEURegion() && saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nullfalse");
                channelskip.setEnabled(false);
                channelSort.setEnabled(false);
                channelEdit.setEnabled(false);
                saChannelFine.setEnabled(false);
                cleanList.setEnabled(false);
            }
        } else {
            MtkLog.d(TAG, "changeEnable true");
            channelskip.setEnabled(true);
            if (!(!CommonIntegration.isSARegion() || saChannelEdit == null || saChannelFine == null)) {
                saChannelEdit.setEnabled(true);
                if (TIFChannelManager.getInstance(this.mContext).hasATVChannels()) {
                    saChannelFine.setEnabled(true);
                } else {
                    saChannelFine.setEnabled(false);
                }
            }
            if (CommonIntegration.isEURegion() && saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nulltrue");
                channelskip.setEnabled(true);
                channelSort.setEnabled(true);
                channelEdit.setEnabled(true);
                StringBuilder sb = new StringBuilder();
                sb.append("changeEnable saChannelFine != nulltrue-source:");
                sb.append(!this.mTV.isCurrentSourceDTV());
                MtkLog.d(TAG, sb.toString());
                if (!this.mTV.isCurrentSourceDTV()) {
                    saChannelFine.setEnabled(true);
                } else {
                    saChannelFine.setEnabled(false);
                }
                MtkLog.d(TAG, "mTV.isTurkeyCountry()> " + this.mTV.isTurkeyCountry() + "dvbs_operator_name_tivibu > " + ScanContent.getDVBSCurrentOPStr(this.mContext) + "  >>   " + this.mContext.getString(R.string.dvbs_operator_name_tivibu));
                if (!this.mTV.isTurkeyCountry() || !ScanContent.getDVBSCurrentOPStr(this.mContext).equalsIgnoreCase(this.mContext.getString(R.string.dvbs_operator_name_tivibu))) {
                    channelskip.setEnabled(true);
                    channelSort.setEnabled(true);
                    cleanList.setEnabled(true);
                } else {
                    channelEdit.setEnabled(false);
                    channelskip.setEnabled(false);
                    channelSort.setEnabled(false);
                    cleanList.setEnabled(false);
                }
                if (!ScanContent.isPreferedSat() || !this.mTV.isTurkeyCountry() || !this.mTV.isTKGSOperator()) {
                    isTKGS = false;
                }
                if (isTKGS && this.mTV.getTKGSOperatorMode() == 0) {
                    channelEdit.setEnabled(false);
                    channelSort.setEnabled(false);
                }
            }
            setSkipSortEditItemHid(channelskip, channelSort, channelEdit);
        }
        MtkLog.d(TAG, "end changeEnable set success");
    }

    public void changeEnable() {
        MtkLog.d(TAG, "start changeEnable");
        Action channelskip = this.mChannelActionMap.get("channelskip");
        Action channelSort = this.mChannelActionMap.get("channelSort");
        Action channelEdit = this.mChannelActionMap.get("channelEdit");
        Action cleanList = this.mChannelActionMap.get("cleanList");
        Action saChannelEdit = this.mChannelActionMap.get("saChannelEdit");
        Action saChannelFine = this.mChannelActionMap.get("saChannelFine");
        if (CommonIntegration.isCNRegion()) {
            if (this.mCommonIntegration.hasActiveChannel()) {
                MtkLog.d(TAG, "changeEnable true");
                if (!(channelEdit == null || cleanList == null)) {
                    channelEdit.setEnabled(true);
                    cleanList.setEnabled(true);
                }
            } else {
                MtkLog.d(TAG, "changeEnable false");
                if (!(channelEdit == null || cleanList == null)) {
                    channelEdit.setEnabled(false);
                    cleanList.setEnabled(false);
                }
            }
        } else if (channelskip == null) {
            MtkLog.d(TAG, "end changeEnable null return");
            return;
        } else if (this.mCommonIntegration.hasActiveChannel()) {
            MtkLog.d(TAG, "changeEnable true");
            channelskip.setEnabled(true);
            if (!(!CommonIntegration.isSARegion() || saChannelEdit == null || saChannelFine == null)) {
                saChannelEdit.setEnabled(true);
                if (TIFChannelManager.getInstance(this.mContext).hasATVChannels()) {
                    saChannelFine.setEnabled(true);
                } else {
                    saChannelFine.setEnabled(false);
                }
            }
            if (CommonIntegration.isEURegion() && saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nulltrue");
                channelskip.setEnabled(true);
                channelSort.setEnabled(true);
                channelEdit.setEnabled(true);
                StringBuilder sb = new StringBuilder();
                sb.append("changeEnable saChannelFine != nulltrue-source:");
                sb.append(!this.mTV.isCurrentSourceDTV());
                MtkLog.d(TAG, sb.toString());
                if (!this.mTV.isCurrentSourceDTV()) {
                    saChannelFine.setEnabled(true);
                } else {
                    saChannelFine.setEnabled(false);
                }
                cleanList.setEnabled(true);
            }
            setSkipSortEditItemHid(channelskip, channelSort, channelEdit);
        } else {
            MtkLog.d(TAG, "changeEnable false");
            channelskip.setEnabled(false);
            if (!(!CommonIntegration.isSARegion() || saChannelEdit == null || saChannelFine == null)) {
                saChannelEdit.setEnabled(false);
                saChannelFine.setEnabled(false);
            }
            if (CommonIntegration.isEURegion() && saChannelFine != null) {
                MtkLog.d(TAG, "changeEnable saChannelFine != nullfalse");
                channelskip.setEnabled(false);
                channelSort.setEnabled(false);
                channelEdit.setEnabled(false);
                saChannelFine.setEnabled(false);
                cleanList.setEnabled(false);
            }
        }
        MtkLog.d(TAG, "end changeEnable set success");
    }

    public void setSkipSortEditItemHid(Preference channelskip, Preference channelSort, Preference channelEdit) {
        CableOperator co = ScanContent.getCurrentOperator();
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        if (this.mCommonIntegration.getTunerMode() != 1) {
            return;
        }
        if (CableOperator.Ziggo.ordinal() == co.ordinal() || (CableOperator.OTHER.ordinal() == co.ordinal() && 13 == countryID)) {
            if (channelskip != null) {
                channelskip.setEnabled(false);
            }
            if (channelSort != null) {
                channelSort.setEnabled(false);
            }
            if (channelEdit != null) {
                channelEdit.setEnabled(false);
            }
        }
    }

    public void setSkipSortEditItemHid(Preference channelitem) {
        CableOperator co = ScanContent.getCurrentOperator();
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        if (this.mCommonIntegration.getTunerMode() != 1) {
            return;
        }
        if ((CableOperator.Ziggo.ordinal() == co.ordinal() || (CableOperator.OTHER.ordinal() == co.ordinal() && 13 == countryID)) && channelitem != null) {
            channelitem.setEnabled(false);
        }
    }

    public void setSkipSortEditItemHid(Action channelskip, Action channelSort, Action channelEdit) {
        CableOperator co = ScanContent.getCurrentOperator();
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        if (this.mCommonIntegration.getTunerMode() != 1) {
            return;
        }
        if (CableOperator.Ziggo.ordinal() == co.ordinal() || (CableOperator.OTHER.ordinal() == co.ordinal() && 13 == countryID)) {
            if (channelskip != null) {
                channelskip.setEnabled(false);
            }
            if (channelSort != null) {
                channelSort.setEnabled(false);
            }
            if (channelEdit != null) {
                channelEdit.setEnabled(false);
            }
        }
    }

    public int getChNum() {
        return this.chNum;
    }

    public void setChNum(int chNum2) {
        this.chNum = chNum2;
    }

    public List<MtkTvChannelInfoBase> getCh_list() {
        return this.ch_list;
    }

    public void setCh_list(List<MtkTvChannelInfoBase> ch_list2) {
        this.ch_list = ch_list2;
    }

    public List<String[]> getChannelInfo() {
        return this.channelInfo;
    }

    public void setChannelInfo(List<String[]> channelInfo2) {
        this.channelInfo = channelInfo2;
    }

    public void setGotoPage(int page) {
        this.gotoPage = page;
    }

    public void setGotoPosition(int pos) {
        this.gotoPosition = pos;
    }

    public int getGotoPage() {
        return this.gotoPage;
    }

    public int getGotoPosition() {
        return this.gotoPosition;
    }

    public String getDisplayChNumber(int chId) {
        return this.mChNumberMap.get(Integer.valueOf(chId));
    }

    public List<MtkTvChannelInfoBase> getTVChannelList() {
        List<MtkTvChannelInfoBase> ch_list2 = new ArrayList<>();
        this.mChNumberMap.clear();
        if (!CommonIntegration.supportTIFFunction()) {
            return EditChannel.getInstance(this.mContext).getChannelList();
        }
        List<TIFChannelInfo> tif_list = TIFChannelManager.getInstance(this.mContext).getCurrentSVLChannelListBase();
        new ArrayList();
        for (int i = 0; i < tif_list.size(); i++) {
            MtkTvChannelInfoBase mCurrentChannel = TIFChannelManager.getInstance(this.mContext).getAPIChannelInfoByChannelId(tif_list.get(i).mInternalProviderFlag3);
            MtkLog.d(TAG, "mCurrentChannel=" + mCurrentChannel);
            if (mCurrentChannel != null && TIFFunctionUtil.checkChMask(mCurrentChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                mCurrentChannel.setBlock(tif_list.get(i).mLocked);
                ch_list2.add(mCurrentChannel);
                int keyChId = mCurrentChannel.getChannelId();
                MtkLog.d(TAG, "mCurrentChannel- keyChId:" + keyChId);
                if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
                    MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
                    this.mChNumberMap.put(Integer.valueOf(keyChId), tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum());
                } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
                    MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
                    int majorNum = tmpIsdb.getMajorNum();
                    this.mChNumberMap.put(Integer.valueOf(keyChId), majorNum + "." + tmpIsdb.getMinorNum());
                } else if (mCurrentChannel instanceof MtkTvAnalogChannelInfo) {
                    this.mChNumberMap.put(Integer.valueOf(keyChId), "" + mCurrentChannel.getChannelNumber());
                } else if (mCurrentChannel instanceof MtkTvDvbChannelInfo) {
                    this.mChNumberMap.put(Integer.valueOf(keyChId), "" + mCurrentChannel.getChannelNumber());
                }
            }
        }
        return ch_list2;
    }

    public void getTVData(String currActionId) {
        if (MarketRegionInfo.isFunctionSupport(13)) {
            List<TIFChannelInfo> tif_list = TIFChannelManager.getInstance(this.mContext).queryChanelListAll(TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
            MtkLog.d(TAG, "getTVData Channel tif_list Length: " + tif_list.size());
            List<MtkTvChannelInfoBase> ch_list2 = new ArrayList<>();
            new ArrayList();
            for (int i = 0; i < tif_list.size(); i++) {
                if (currActionId.equals(MenuConfigManager.TV_CHANNEL_SKIP) || currActionId.equals(MenuConfigManager.TV_CHANNEL_MOVE)) {
                    ch_list2.add(tif_list.get(i).mMtkTvChannelInfo);
                } else {
                    MtkLog.d(TAG, "getTVData() tif_list.get(i).mMtkTvChannelInfo.isSkip(): " + tif_list.get(i).mMtkTvChannelInfo.isSkip());
                    if (!tif_list.get(i).mMtkTvChannelInfo.isSkip()) {
                        ch_list2.add(tif_list.get(i).mMtkTvChannelInfo);
                    }
                }
            }
            getTvDataByList(ch_list2, currActionId);
            return;
        }
        getTvDataByList(EditChannel.getInstance(this.mContext).getChannelList(), currActionId);
    }

    public boolean isM7HasNumExceed4K() {
        if (!this.mTV.isM7ScanMode()) {
            return false;
        }
        MtkLog.d(TAG, "isM7");
        for (TIFChannelInfo tifChannelInfo : TIFChannelManager.getInstance(this.mContext).queryChanelListAll(TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
            if (Integer.parseInt(tifChannelInfo.getDisplayNumber()) > 4001) {
                MtkLog.d(TAG, "hasChannelNum > 4000");
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0229  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x024d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getTvDataByList(java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> r31, java.lang.String r32) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            r2 = r32
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r4 = 0
            if (r1 == 0) goto L_0x003b
            int r4 = r31.size()
            java.lang.String r5 = "MenuDataHelper"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Channel List Length: "
            r6.append(r7)
            int r7 = r31.size()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            if (r4 != 0) goto L_0x003b
            r0.setChannelInfo(r3)
            r0.setChNum(r4)
            r30.setCh_list(r31)
            r30.changeEnable()
            return
        L_0x003b:
            android.content.Context r5 = r0.mContext
            com.mediatek.wwtv.setting.scan.EditChannel r5 = com.mediatek.wwtv.setting.scan.EditChannel.getInstance(r5)
            int r5 = r5.getCurrentChannelId()
            java.lang.String r6 = "MenuDataHelper"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "currentChannelID: "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            r6 = 0
            r7 = 0
            r8 = 0
            com.mediatek.wwtv.setting.util.TVContent r9 = r0.mTV
            int r9 = r9.getCurrentTunerMode()
            r10 = 0
            r11 = 2
            if (r9 < r11) goto L_0x0069
            r10 = 1
        L_0x0069:
            android.content.Context r12 = r0.mContext
            android.content.res.Resources r12 = r12.getResources()
            r13 = 2131690352(0x7f0f0370, float:1.9009745E38)
            java.lang.String r12 = r12.getString(r13)
            android.content.Context r13 = r0.mContext
            android.content.res.Resources r13 = r13.getResources()
            r14 = 2131690144(0x7f0f02a0, float:1.9009323E38)
            java.lang.String r13 = r13.getString(r14)
            r15 = r8
            r8 = r7
            r7 = r6
            r6 = 0
        L_0x0087:
            r16 = 1
            if (r6 >= r4) goto L_0x0265
            r11 = 7
            java.lang.String[] r11 = new java.lang.String[r11]
            java.lang.String r17 = ""
            r18 = 6
            r11[r18] = r17
            java.lang.String r17 = ""
            r19 = 5
            r11[r19] = r17
            java.lang.Object r17 = r1.get(r6)
            r14 = r17
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r14 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r14
            r20 = r9
            java.lang.String r9 = "MenuDataHelper"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r21 = r4
            java.lang.String r4 = "mCurrentChannel: "
            r1.append(r4)
            int r4 = r14.getChannelId()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r1)
            int r1 = r14.getChannelId()
            if (r1 != r5) goto L_0x00c8
            r1 = r6
            r7 = r1
        L_0x00c8:
            boolean r1 = r14 instanceof com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo
            if (r1 == 0) goto L_0x00f9
            r1 = r14
            com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo r1 = (com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r9 = r1.getMajorNum()
            r4.append(r9)
            java.lang.String r9 = "-"
            r4.append(r9)
            int r9 = r1.getMinorNum()
            r4.append(r9)
            java.lang.String r4 = r4.toString()
            r9 = 0
            r11[r9] = r4
            r11[r16] = r12
            r24 = r5
            r22 = r7
        L_0x00f5:
            r25 = r13
            goto L_0x01c2
        L_0x00f9:
            boolean r1 = r14 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r1 == 0) goto L_0x012f
            r1 = r14
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r1 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r1
            int r4 = r1.getMajorNum()
            int r9 = r1.getMinorNum()
            r22 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            r23 = r4
            java.lang.String r4 = "."
            r7.append(r4)
            r7.append(r9)
            java.lang.String r4 = r7.toString()
            r7 = 0
            r11[r7] = r4
            r11[r16] = r12
            java.lang.String r4 = r1.getTsName()
            r11[r19] = r4
            r24 = r5
            goto L_0x00f5
        L_0x012f:
            r22 = r7
            boolean r1 = r14 instanceof com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo
            if (r1 == 0) goto L_0x01c4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = ""
            r1.append(r4)
            int r4 = r14.getChannelNumber()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r4 = 0
            r11[r4] = r1
            r11[r16] = r13
            int r15 = r15 + 1
            int r1 = r14.getChannelId()
            if (r1 != r5) goto L_0x0159
            int r8 = r15 + -1
        L_0x0159:
            r1 = r14
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r1 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r1
            int r1 = r1.getTvSys()
            r4 = r14
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r4 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r4
            int r4 = r4.getColorSys()
            r7 = r14
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r7 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r7
            int r7 = r7.getAudioSys()
            int r9 = r0.getSoundSystemIndex(r1, r7)
            r24 = r5
            android.content.Context r5 = r0.mContext
            android.content.res.Resources r5 = r5.getResources()
            r25 = r13
            r13 = 2130903254(0x7f0300d6, float:1.741332E38)
            java.lang.String[] r5 = r5.getStringArray(r13)
            r13 = r5[r9]
            r11[r18] = r13
            java.lang.String r13 = "MenuDataHelper"
            r26 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r27 = r9
            java.lang.String r9 = "fineindex: "
            r5.append(r9)
            r5.append(r8)
            java.lang.String r9 = "analogNum"
            r5.append(r9)
            r5.append(r15)
            java.lang.String r9 = ">>"
            r5.append(r9)
            r5.append(r1)
            java.lang.String r9 = ">>>"
            r5.append(r9)
            r5.append(r4)
            java.lang.String r9 = ">>>"
            r5.append(r9)
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r5)
        L_0x01c2:
            r9 = 0
            goto L_0x01ef
        L_0x01c4:
            r24 = r5
            r25 = r13
            boolean r1 = r14 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r1 == 0) goto L_0x01c2
            r1 = r14
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r1 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r1
            r11[r16] = r12
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = ""
            r4.append(r5)
            int r5 = r14.getChannelNumber()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r9 = 0
            r11[r9] = r4
            java.lang.String r4 = r1.getNwName()
            r11[r19] = r4
        L_0x01ef:
            java.lang.String r1 = r14.getServiceName()
            r4 = 2
            r11[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = ""
            r1.append(r4)
            int r4 = r14.getChannelId()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            r4 = 3
            r11[r4] = r1
            int r1 = r14.getFrequency()
            float r1 = (float) r1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = ""
            r5.append(r7)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            r7 = 4
            r11[r7] = r5
            if (r10 != 0) goto L_0x024d
            r5 = 1232348160(0x49742400, float:1000000.0)
            float r1 = r1 / r5
            java.math.BigDecimal r5 = new java.math.BigDecimal
            r28 = r10
            double r9 = (double) r1
            r5.<init>(r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = ""
            r9.append(r10)
            java.math.BigDecimal r4 = r5.setScale(r4, r7)
            r9.append(r4)
            java.lang.String r4 = r9.toString()
            r11[r7] = r4
            goto L_0x024f
        L_0x024d:
            r28 = r10
        L_0x024f:
            r3.add(r11)
            int r6 = r6 + 1
            r9 = r20
            r4 = r21
            r7 = r22
            r5 = r24
            r13 = r25
            r10 = r28
            r1 = r31
            r11 = 2
            goto L_0x0087
        L_0x0265:
            r21 = r4
            r24 = r5
            r20 = r9
            r28 = r10
            r25 = r13
            java.lang.String r1 = "MenuDataHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "mListViewSelectedItemData.getmItemID(): "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
            java.lang.String r1 = "MenuDataHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "index: "
            r4.append(r5)
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
            java.lang.String r1 = "Analog ChannelFine Tune"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x02b7
            java.lang.String r1 = "channel_decode"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x02ac
            goto L_0x02b7
        L_0x02ac:
            int r1 = r7 / 10
            int r1 = r1 + 1
            r0.gotoPage = r1
            int r1 = r7 % 10
            r0.gotoPosition = r1
            goto L_0x02e3
        L_0x02b7:
            int r1 = r8 / 10
            int r1 = r1 + 1
            r0.gotoPage = r1
            int r1 = r8 % 10
            r0.gotoPosition = r1
            java.lang.String r1 = "MenuDataHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "TV_CHANNELFINE_TUNE: "
            r4.append(r5)
            int r5 = r0.gotoPage
            r4.append(r5)
            java.lang.String r5 = "gotoPosition:"
            r4.append(r5)
            int r5 = r0.gotoPosition
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
        L_0x02e3:
            java.lang.String r1 = "MenuDataHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "gotoPage: "
            r4.append(r5)
            int r5 = r0.gotoPage
            r4.append(r5)
            java.lang.String r5 = "gotoPosition:"
            r4.append(r5)
            int r5 = r0.gotoPosition
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
            r0.setChannelInfo(r3)
            r4 = r21
            r0.setChNum(r4)
            r30.setCh_list(r31)
            r29 = 0
        L_0x0312:
            r1 = r29
            int r5 = r3.size()
            if (r1 >= r5) goto L_0x033c
            java.lang.String r5 = "forupdate"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r9 = "when bind:"
            r6.append(r9)
            java.lang.Object r9 = r3.get(r1)
            java.lang.String[] r9 = (java.lang.String[]) r9
            r10 = 2
            r9 = r9[r10]
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            int r29 = r1 + 1
            goto L_0x0312
        L_0x033c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.MenuDataHelper.getTvDataByList(java.util.List, java.lang.String):void");
    }

    public List<String[]> setChannelInfoList(String mActionID) {
        List<String[]> mChannelInfos = new ArrayList<>();
        for (int i = 0; i < getChNum(); i++) {
            MtkTvChannelInfoBase ch = null;
            if (getCh_list() != null) {
                ch = getCh_list().get(i);
            }
            if ((!mActionID.equals(MenuConfigManager.TV_CHANNELFINE_TUNE) && !mActionID.equals(MenuConfigManager.TV_CHANNEL_DECODE)) || ch == null || this.mTV.isAnalog(ch)) {
                mChannelInfos.add(getChannelInfo().get(i));
            }
        }
        return mChannelInfos;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:102:0x04d2, code lost:
        if ((r4 + 1) > r5.length) goto L_0x04d7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter.EditItem> getChannelEditDetail(java.lang.String[] r43) {
        /*
            r42 = this;
            r0 = r42
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            r6 = 2131691547(0x7f0f081b, float:1.9012169E38)
            r7 = 2131691523(0x7f0f0803, float:1.901212E38)
            r8 = 1176255488(0x461c3c00, float:9999.0)
            r9 = 0
            r10 = 2131691524(0x7f0f0804, float:1.9012122E38)
            r11 = 5
            r12 = 2
            r13 = 3
            r14 = 4
            r4 = 0
            if (r3 == 0) goto L_0x00e9
            r3 = r43[r13]
            int r3 = java.lang.Integer.parseInt(r3)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r13 = r0.mCommonIntegration
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r13 = r13.getChannelById(r3)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r26 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_sa_tsname"
            android.content.Context r5 = r0.mContext
            r15 = 2131691528(0x7f0f0808, float:1.901213E38)
            java.lang.String r21 = r5.getString(r15)
            r22 = r43[r11]
            r23 = 0
            r24 = 0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r26
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r5 = r26
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r11 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_sa_no"
            android.content.Context r15 = r0.mContext
            java.lang.String r21 = r15.getString(r10)
            r22 = r43[r4]
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r11
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r10 = r11
            r10.minValue = r9
            r10.maxValue = r8
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_sa_name"
            android.content.Context r9 = r0.mContext
            java.lang.String r21 = r9.getString(r7)
            r22 = r43[r12]
            r23 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r8
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r7 = r8
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_frequency_sa"
            android.content.Context r9 = r0.mContext
            java.lang.String r21 = r9.getString(r6)
            r22 = r43[r14]
            r24 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r8
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r6 = r8
            r8 = r43[r14]
            float r8 = java.lang.Float.parseFloat(r8)
            if (r13 == 0) goto L_0x00bd
            com.mediatek.wwtv.setting.util.TVContent r9 = r0.mTV
            boolean r9 = r9.isAnalog(r13)
            if (r9 == 0) goto L_0x00bd
            r9 = r13
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r9
            int r9 = r9.getCentralFreq()
            float r9 = (float) r9
            r11 = 1232348160(0x49742400, float:1000000.0)
            float r9 = r9 / r11
            r12 = 1067030938(0x3f99999a, float:1.2)
            float r9 = r9 - r12
            r6.minValue = r9
            r9 = r13
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r9
            int r9 = r9.getCentralFreq()
            float r9 = (float) r9
            float r9 = r9 / r11
            r11 = 1072064102(0x3fe66666, float:1.8)
            float r9 = r9 + r11
            r6.maxValue = r9
            goto L_0x00c7
        L_0x00bd:
            r9 = 1069547520(0x3fc00000, float:1.5)
            float r11 = r8 - r9
            r6.minValue = r11
            float r14 = r8 + r9
            r6.maxValue = r14
        L_0x00c7:
            r9 = 1
            r11 = r43[r9]
            if (r11 == 0) goto L_0x00d9
            r11 = r43[r9]
            java.lang.String r12 = "Analog"
            boolean r11 = r11.equals(r12)
            if (r11 == 0) goto L_0x00d9
            r6.isEnable = r9
            goto L_0x00db
        L_0x00d9:
            r6.isEnable = r4
        L_0x00db:
            r2.add(r5)
            r2.add(r10)
            r2.add(r7)
            r2.add(r6)
            goto L_0x0649
        L_0x00e9:
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEURegion()
            if (r3 != 0) goto L_0x02f3
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r3 == 0) goto L_0x00f7
            goto L_0x02f3
        L_0x00f7:
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r3 == 0) goto L_0x0649
            r3 = r43[r13]
            int r3 = java.lang.Integer.parseInt(r3)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r5 = r0.mCommonIntegration
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = r5.getChannelById(r3)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r11 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_no"
            android.content.Context r13 = r0.mContext
            java.lang.String r21 = r13.getString(r10)
            r22 = r43[r4]
            r23 = 1
            r24 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r11
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r10 = r11
            r10.minValue = r9
            r10.maxValue = r8
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_sa_name"
            android.content.Context r9 = r0.mContext
            java.lang.String r21 = r9.getString(r7)
            r22 = r43[r12]
            r24 = 0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r8
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r7 = r8
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_frequency"
            android.content.Context r9 = r0.mContext
            java.lang.String r21 = r9.getString(r6)
            r22 = r43[r14]
            r24 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r8
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r6 = r8
            r8 = r43[r14]
            float r8 = java.lang.Float.parseFloat(r8)
            if (r5 == 0) goto L_0x0183
            com.mediatek.wwtv.setting.util.TVContent r9 = r0.mTV
            boolean r9 = r9.isAnalog(r5)
            if (r9 == 0) goto L_0x0183
            r9 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r9
            int r9 = r9.getCentralFreq()
            float r9 = (float) r9
            r11 = 1232348160(0x49742400, float:1000000.0)
            float r9 = r9 / r11
            r12 = 1067030938(0x3f99999a, float:1.2)
            float r9 = r9 - r12
            r6.minValue = r9
            r9 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r9
            int r9 = r9.getCentralFreq()
            float r9 = (float) r9
            float r9 = r9 / r11
            r11 = 1072064102(0x3fe66666, float:1.8)
            float r9 = r9 + r11
            r6.maxValue = r9
            goto L_0x018d
        L_0x0183:
            r9 = 1069547520(0x3fc00000, float:1.5)
            float r11 = r8 - r9
            r6.minValue = r11
            float r14 = r8 + r9
            r6.maxValue = r14
        L_0x018d:
            android.content.Context r9 = r0.mContext
            android.content.res.Resources r9 = r9.getResources()
            r11 = 2130903240(0x7f0300c8, float:1.7413292E38)
            java.lang.String[] r9 = r9.getStringArray(r11)
            r11 = 0
            if (r5 == 0) goto L_0x01ae
            com.mediatek.wwtv.setting.util.TVContent r12 = r0.mTV
            boolean r12 = r12.isAnalog(r5)
            if (r12 == 0) goto L_0x01ae
            r12 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r12
            int r11 = r12.getColorSys()
            r12 = 1
            int r11 = r11 + r12
        L_0x01ae:
            if (r11 < 0) goto L_0x01b5
            int r12 = r11 + 1
            int r13 = r9.length
            if (r12 <= r13) goto L_0x01bd
        L_0x01b5:
            java.lang.String r12 = "MenuDataHelper"
            java.lang.String r13 = "Warning: colorNum changed"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r12, r13)
            r11 = 0
        L_0x01bd:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r19 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r13 = "CHANNELEDIT_color_system"
            android.content.Context r12 = r0.mContext
            r14 = 2131691533(0x7f0f080d, float:1.901214E38)
            java.lang.String r14 = r12.getString(r14)
            r17 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r18 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r12 = r19
            r15 = r11
            r16 = r9
            r12.<init>((java.lang.String) r13, (java.lang.String) r14, (int) r15, (java.lang.String[]) r16, (boolean) r17, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r18)
            android.content.Context r13 = r0.mContext
            android.content.res.Resources r13 = r13.getResources()
            r14 = 2130903254(0x7f0300d6, float:1.741332E38)
            java.lang.String[] r9 = r13.getStringArray(r14)
            r13 = 0
            boolean r14 = r5 instanceof com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo
            if (r14 == 0) goto L_0x01fa
            r14 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r14 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r14
            int r14 = r14.getTvSys()
            r15 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r15 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r15
            int r15 = r15.getAudioSys()
            int r13 = r0.getSoundSystemIndex(r14, r15)
        L_0x01fa:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r14 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r16 = "CHANNELEDIT_sound_system"
            android.content.Context r15 = r0.mContext
            r4 = 2131691581(0x7f0f083d, float:1.9012238E38)
            java.lang.String r17 = r15.getString(r4)
            r20 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r21 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r15 = r14
            r18 = r13
            r19 = r9
            r15.<init>((java.lang.String) r16, (java.lang.String) r17, (int) r18, (java.lang.String[]) r19, (boolean) r20, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r21)
            r4 = r14
            r14 = 0
            android.content.Context r15 = r0.mContext
            android.content.res.Resources r15 = r15.getResources()
            r27 = r3
            r3 = 2130903244(0x7f0300cc, float:1.74133E38)
            java.lang.String[] r3 = r15.getStringArray(r3)
            if (r5 == 0) goto L_0x0234
            boolean r9 = r5 instanceof com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo
            if (r9 == 0) goto L_0x0234
            r9 = r5
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r9
            boolean r9 = r9.isNoAutoFineTune()
            r15 = 1
            r9 = r9 ^ r15
            goto L_0x0235
        L_0x0234:
            r9 = 0
        L_0x0235:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r14 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r17 = "UNDEFINE_channel_edit_aft"
            android.content.Context r15 = r0.mContext
            r28 = r8
            r8 = 2131691520(0x7f0f0800, float:1.9012114E38)
            java.lang.String r18 = r15.getString(r8)
            r21 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r22 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r16 = r14
            r19 = r9
            r20 = r3
            r16.<init>((java.lang.String) r17, (java.lang.String) r18, (int) r19, (java.lang.String[]) r20, (boolean) r21, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r22)
            r8 = r14
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r21 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r15 = "channel_edit_finetune"
            android.content.Context r14 = r0.mContext
            r29 = r3
            r3 = 2131691545(0x7f0f0819, float:1.9012165E38)
            java.lang.String r16 = r14.getString(r3)
            r18 = 0
            r19 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r20 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
            r14 = r21
            r17 = r9
            r14.<init>((java.lang.String) r15, (java.lang.String) r16, (int) r17, (java.lang.String[]) r18, (boolean) r19, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r20)
            r3 = r21
            r14 = 0
            if (r5 == 0) goto L_0x0278
            boolean r15 = r5.isSkip()
            r14 = r15
        L_0x0278:
            android.content.Context r15 = r0.mContext
            android.content.res.Resources r15 = r15.getResources()
            r30 = r9
            r9 = 2130903253(0x7f0300d5, float:1.7413319E38)
            java.lang.String[] r9 = r15.getStringArray(r9)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r22 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r16 = "UNDEFINE_channel_edit_skip"
            android.content.Context r15 = r0.mContext
            r31 = r11
            r11 = 2131691579(0x7f0f083b, float:1.9012234E38)
            java.lang.String r17 = r15.getString(r11)
            r20 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r21 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r15 = r22
            r18 = r14
            r19 = r9
            r15.<init>((java.lang.String) r16, (java.lang.String) r17, (int) r18, (java.lang.String[]) r19, (boolean) r20, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r21)
            r11 = r22
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r22 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r16 = "channel_edit_store"
            android.content.Context r15 = r0.mContext
            r32 = r13
            r13 = 2131691585(0x7f0f0841, float:1.9012246E38)
            java.lang.String r17 = r15.getString(r13)
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r21 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
            r15 = r22
            r15.<init>((java.lang.String) r16, (java.lang.String) r17, (int) r18, (java.lang.String[]) r19, (boolean) r20, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r21)
            r13 = r22
            r2.add(r10)
            r2.add(r7)
            if (r5 == 0) goto L_0x02dc
            com.mediatek.wwtv.setting.util.TVContent r15 = r0.mTV
            boolean r15 = r15.isAnalog(r5)
            if (r15 != 0) goto L_0x02dc
            r15 = 0
            r6.isEnable = r15
            r12.isEnable = r15
            r4.isEnable = r15
            r8.isEnable = r15
            r3.isEnable = r15
            r2.add(r6)
            goto L_0x02eb
        L_0x02dc:
            r2.add(r6)
            r2.add(r12)
            r2.add(r4)
            r2.add(r8)
            r2.add(r3)
        L_0x02eb:
            r2.add(r11)
            r2.add(r13)
            goto L_0x0649
        L_0x02f3:
            r3 = 0
            java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> r4 = r0.ch_list
            if (r4 == 0) goto L_0x0343
            r4 = r3
            r3 = 0
        L_0x02fa:
            java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> r5 = r0.ch_list
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0342
            java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> r5 = r0.ch_list
            java.lang.Object r5 = r5.get(r3)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r5
            java.lang.String r15 = "MenuDataHelper"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r6 = "now data[3]:"
            r14.append(r6)
            r6 = r43[r13]
            r14.append(r6)
            java.lang.String r6 = ",currID:"
            r14.append(r6)
            int r6 = r5.getChannelId()
            r14.append(r6)
            java.lang.String r6 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r6)
            r6 = r43[r13]
            int r6 = java.lang.Integer.parseInt(r6)
            int r14 = r5.getChannelId()
            if (r6 != r14) goto L_0x033b
            r4 = r5
        L_0x033b:
            int r3 = r3 + 1
            r6 = 2131691547(0x7f0f081b, float:1.9012169E38)
            r14 = 4
            goto L_0x02fa
        L_0x0342:
            r3 = r4
        L_0x0343:
            java.lang.String r4 = "UNDEFINE_channel_nw_name"
            r5 = 1
            r6 = r43[r5]
            if (r6 == 0) goto L_0x0356
            r6 = r43[r5]
            java.lang.String r5 = "Analog"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0356
            java.lang.String r4 = "UNDEFINE_channel_nw_analog_name"
        L_0x0356:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r5 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            android.content.Context r6 = r0.mContext
            r13 = 2131691557(0x7f0f0825, float:1.901219E38)
            java.lang.String r21 = r6.getString(r13)
            r22 = r43[r11]
            r23 = 0
            r24 = 0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r5
            r20 = r4
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r6 = 1
            r13 = 1
            com.mediatek.wwtv.setting.util.MenuConfigManager r14 = r0.mConfigManager
            java.lang.String r15 = "g_misc__tkgs_operating_mode"
            int r14 = r14.getDefault(r15)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r15 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r15 = r15.isPreferSatMode()
            com.mediatek.wwtv.setting.util.TVContent r11 = r0.mTV
            boolean r11 = r11.isTKGSOperator()
            java.lang.String r7 = "MenuDataHelper"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "satOperOnly="
            r8.append(r9)
            r8.append(r15)
            java.lang.String r9 = "mTV.isTurkeyCountry() :"
            r8.append(r9)
            com.mediatek.wwtv.setting.util.TVContent r9 = r0.mTV
            boolean r9 = r9.isTurkeyCountry()
            r8.append(r9)
            java.lang.String r9 = "mTV.isTKGSOperator(): "
            r8.append(r9)
            com.mediatek.wwtv.setting.util.TVContent r9 = r0.mTV
            boolean r9 = r9.isTKGSOperator()
            r8.append(r9)
            java.lang.String r9 = "tkgsmode="
            r8.append(r9)
            r8.append(r14)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r8)
            if (r14 != 0) goto L_0x03c9
            if (r15 == 0) goto L_0x03c9
            if (r11 == 0) goto L_0x03c9
            r6 = 0
        L_0x03c9:
            if (r6 == 0) goto L_0x03e9
            com.mediatek.wwtv.setting.util.MenuConfigManager r7 = r0.mConfigManager
            java.lang.String r8 = "g_bs__bs_src"
            int r7 = r7.getDefault(r8)
            if (r7 >= r12) goto L_0x03ea
            com.mediatek.wwtv.setting.util.TVContent r7 = r0.mTV
            java.lang.String r8 = "g_fusion_common__lcn"
            int r7 = r7.getConfigValue(r8)
            if (r7 == 0) goto L_0x03ea
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r7 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r7 = r7.isCurrentSourceATVforEuPA()
            if (r7 != 0) goto L_0x03ea
        L_0x03e9:
            r13 = 0
        L_0x03ea:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r7 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_no"
            android.content.Context r8 = r0.mContext
            java.lang.String r21 = r8.getString(r10)
            r8 = 0
            r22 = r43[r8]
            r24 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r7
            r23 = r13
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r8 = 0
            r7.minValue = r8
            r8 = 1176255488(0x461c3c00, float:9999.0)
            r7.maxValue = r8
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_sa_name"
            android.content.Context r9 = r0.mContext
            r10 = 2131691523(0x7f0f0803, float:1.901212E38)
            java.lang.String r21 = r9.getString(r10)
            r22 = r43[r12]
            r24 = 0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r8
            r23 = r6
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r9 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r20 = "UNDEFINE_channel_edit_frequency"
            android.content.Context r10 = r0.mContext
            r12 = 2131691547(0x7f0f081b, float:1.9012169E38)
            java.lang.String r21 = r10.getString(r12)
            r10 = 4
            r22 = r43[r10]
            r23 = 1
            r24 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r25 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.INPUTBOX
            r19 = r9
            r19.<init>((java.lang.String) r20, (java.lang.String) r21, (java.lang.String) r22, (boolean) r23, (boolean) r24, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r25)
            r10 = 4
            r12 = r43[r10]
            float r10 = java.lang.Float.parseFloat(r12)
            if (r3 == 0) goto L_0x0476
            com.mediatek.wwtv.setting.util.TVContent r12 = r0.mTV
            boolean r12 = r12.isAnalog(r3)
            if (r12 == 0) goto L_0x0476
            r12 = r3
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r12
            int r12 = r12.getCentralFreq()
            float r12 = (float) r12
            r16 = 1232348160(0x49742400, float:1000000.0)
            float r12 = r12 / r16
            r18 = 1067030938(0x3f99999a, float:1.2)
            float r12 = r12 - r18
            r9.minValue = r12
            r12 = r3
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r12
            int r12 = r12.getCentralFreq()
            float r12 = (float) r12
            float r12 = r12 / r16
            r16 = 1072064102(0x3fe66666, float:1.8)
            float r12 = r12 + r16
            r9.maxValue = r12
            goto L_0x0480
        L_0x0476:
            r12 = 1069547520(0x3fc00000, float:1.5)
            float r1 = r10 - r12
            r9.minValue = r1
            float r1 = r10 + r12
            r9.maxValue = r1
        L_0x0480:
            r2.add(r5)
            r2.add(r7)
            r2.add(r8)
            if (r3 == 0) goto L_0x04a6
            com.mediatek.wwtv.setting.util.TVContent r1 = r0.mTV
            boolean r1 = r1.isAnalog(r3)
            if (r1 != 0) goto L_0x04a6
            r1 = 0
            r9.isEnable = r1
            r2.add(r9)
            r1 = r4
            r33 = r5
            r35 = r6
            r38 = r7
            r39 = r8
            r41 = r10
            goto L_0x0572
        L_0x04a6:
            java.lang.String r1 = "CHANNELEDIT_color_system"
            r4 = 0
            if (r3 == 0) goto L_0x04bc
            com.mediatek.wwtv.setting.util.TVContent r12 = r0.mTV
            boolean r12 = r12.isAnalog(r3)
            if (r12 == 0) goto L_0x04bc
            r12 = r3
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r12
            int r4 = r12.getColorSys()
            r12 = 1
            int r4 = r4 + r12
        L_0x04bc:
            android.content.Context r12 = r0.mContext
            android.content.res.Resources r12 = r12.getResources()
            r33 = r5
            r5 = 2130903241(0x7f0300c9, float:1.7413294E38)
            java.lang.String[] r5 = r12.getStringArray(r5)
            if (r4 < 0) goto L_0x04d5
            int r12 = r4 + 1
            r34 = r4
            int r4 = r5.length
            if (r12 <= r4) goto L_0x04e1
            goto L_0x04d7
        L_0x04d5:
            r34 = r4
        L_0x04d7:
            java.lang.String r4 = "MenuDataHelper"
            java.lang.String r12 = "Warning: colorNum changed"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r4, r12)
            r4 = 0
            r34 = r4
        L_0x04e1:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r4 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            android.content.Context r12 = r0.mContext
            r35 = r6
            r6 = 2131691533(0x7f0f080d, float:1.901214E38)
            java.lang.String r18 = r12.getString(r6)
            r21 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r22 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r16 = r4
            r17 = r1
            r19 = r34
            r20 = r5
            r16.<init>((java.lang.String) r17, (java.lang.String) r18, (int) r19, (java.lang.String[]) r20, (boolean) r21, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r22)
            java.lang.String r1 = "CHANNELEDIT_sound_system"
            r6 = 0
            r12 = 1
            r23 = 8
            r36 = r5
            android.content.Context r5 = r0.mContext
            android.content.res.Resources r5 = r5.getResources()
            r37 = r6
            r6 = 2130903254(0x7f0300d6, float:1.741332E38)
            java.lang.String[] r5 = r5.getStringArray(r6)
            java.lang.String r6 = "MenuDataHelper"
            r38 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r39 = r8
            java.lang.String r8 = "Sound System Num: "
            r7.append(r8)
            int r8 = r5.length
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.i(r6, r7)
            r6 = r12
            r7 = 0
            boolean r8 = r3 instanceof com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo
            if (r8 == 0) goto L_0x054a
            r8 = r3
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r8 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r8
            int r8 = r8.getTvSys()
            r40 = r6
            r6 = r3
            com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo r6 = (com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo) r6
            int r6 = r6.getAudioSys()
            int r6 = r0.getSoundSystemIndex(r8, r6)
            goto L_0x054d
        L_0x054a:
            r40 = r6
            r6 = r7
        L_0x054d:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r7 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            android.content.Context r8 = r0.mContext
            r41 = r10
            r10 = 2131691581(0x7f0f083d, float:1.9012238E38)
            java.lang.String r18 = r8.getString(r10)
            r21 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r22 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r16 = r7
            r17 = r1
            r19 = r6
            r20 = r5
            r16.<init>((java.lang.String) r17, (java.lang.String) r18, (int) r19, (java.lang.String[]) r20, (boolean) r21, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r22)
            r2.add(r9)
            r2.add(r4)
            r2.add(r7)
        L_0x0572:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r0.mCommonIntegration
            int r4 = r4.getTunerMode()
            r5 = 1
            if (r4 != r5) goto L_0x0648
            if (r3 == 0) goto L_0x0648
            com.mediatek.wwtv.setting.util.TVContent r4 = r0.mTV
            boolean r4 = r4.isAnalog(r3)
            if (r4 != 0) goto L_0x0648
            android.content.Context r4 = r0.mContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2130903251(0x7f0300d3, float:1.7413315E38)
            java.lang.String[] r4 = r4.getStringArray(r5)
            java.lang.String r5 = ""
            r6 = 0
            java.lang.String r7 = "MenuDataHelper"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "start detail: symbolrate=="
            r8.append(r10)
            r8.append(r5)
            java.lang.String r10 = ",modu=="
            r8.append(r10)
            r8.append(r6)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r8)
            boolean r7 = r3 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r7 == 0) goto L_0x05d3
            r7 = r3
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r7 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r7
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = ""
            r8.append(r10)
            int r10 = r7.getSymRate()
            r8.append(r10)
            java.lang.String r5 = r8.toString()
            int r6 = r7.getMod()
        L_0x05d3:
            java.lang.String r7 = "MenuDataHelper"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "edit detail: symbolrate=="
            r8.append(r10)
            r8.append(r5)
            java.lang.String r10 = ",modu=="
            r8.append(r10)
            r8.append(r6)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r8)
            r7 = 4
            if (r6 == r7) goto L_0x0605
            r7 = 5
            if (r6 == r7) goto L_0x0605
            r7 = 6
            if (r6 == r7) goto L_0x0605
            r7 = 10
            if (r6 == r7) goto L_0x0605
            r7 = 14
            if (r6 != r7) goto L_0x0603
            goto L_0x0605
        L_0x0603:
            r6 = 0
            goto L_0x0612
        L_0x0605:
            r7 = 6
            if (r6 > r7) goto L_0x060b
            int r6 = r6 + -3
            goto L_0x0612
        L_0x060b:
            r7 = 10
            if (r6 != r7) goto L_0x0611
            r6 = 4
            goto L_0x0612
        L_0x0611:
            r6 = 5
        L_0x0612:
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r7 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r17 = "dvbc_single_rf_scan_modulation"
            android.content.Context r8 = r0.mContext
            r10 = 2131691571(0x7f0f0833, float:1.9012218E38)
            java.lang.String r18 = r8.getString(r10)
            r19 = r4[r6]
            r20 = 0
            r21 = 0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r22 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            r16 = r7
            r16.<init>((java.lang.String) r17, (java.lang.String) r18, (java.lang.String) r19, (boolean) r20, (boolean) r21, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r22)
            com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem r8 = new com.mediatek.wwtv.setting.base.scan.adapter.EditDetailAdapter$EditItem
            java.lang.String r17 = "dvbc_single_rf_scan_sym_rate"
            android.content.Context r10 = r0.mContext
            r12 = 2131691100(0x7f0f065c, float:1.9011262E38)
            java.lang.String r18 = r10.getString(r12)
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r22 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            r16 = r8
            r19 = r5
            r16.<init>((java.lang.String) r17, (java.lang.String) r18, (java.lang.String) r19, (boolean) r20, (boolean) r21, (com.mediatek.wwtv.setting.widget.detailui.Action.DataType) r22)
            r2.add(r7)
            r2.add(r8)
        L_0x0648:
        L_0x0649:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.MenuDataHelper.getChannelEditDetail(java.lang.String[]):java.util.List");
    }

    private int getSoundSystemIndex(int ui4_tv_sys, int ui4_audio_sys) {
        int index;
        MtkLog.i("soundsystem", "tv_sys:" + ui4_tv_sys + " audio_sys:" + ui4_audio_sys + "-------------------");
        int ui4_tv_sys2 = ui4_tv_sys & 65535;
        StringBuilder sb = new StringBuilder();
        sb.append("tv_sys:");
        sb.append(ui4_tv_sys2);
        MtkLog.i("soundsystem", sb.toString());
        if (ui4_tv_sys2 == 4096) {
            index = 2;
        } else if (ui4_tv_sys2 == 8192) {
            index = 3;
        } else if (ui4_tv_sys2 == 256) {
            index = 4;
        } else if (ui4_tv_sys2 == 49152) {
            index = 9;
        } else if (ui4_tv_sys2 == 1032) {
            if (ui4_audio_sys == 8) {
                index = 6;
            } else if (ui4_audio_sys == 16) {
                index = 7;
            } else if (ui4_audio_sys == 32) {
                index = 8;
            } else {
                index = 5;
            }
        } else if (ui4_audio_sys == 8) {
            index = 1;
        } else {
            index = 0;
        }
        MtkLog.i("soundsystem", "tv_sys:" + ui4_tv_sys2 + " audio_sys:" + ui4_audio_sys + " index:" + index);
        return index;
    }

    public void updateChannelName(String chId, String channelName) {
        MtkLog.d(TAG, "chId:" + chId + " chname:" + channelName);
        for (int i = 0; i < this.channelInfo.size(); i++) {
            if (this.channelInfo.get(i)[3].equals(chId)) {
                EditChannel.getInstance(this.mContext).setChannelName(Integer.parseInt(chId), channelName);
                this.channelInfo.get(i)[2] = channelName;
                MtkLog.d(TAG, " channelInfo.get(i)[2]:" + this.channelInfo.get(i)[2] + " channelInfo.get(i)[0]:" + this.channelInfo.get(i)[0]);
                return;
            }
        }
    }

    public void updateChannelFreq(String channelId, String channelFreq) {
        MtkLog.d(TAG, "chId:" + channelId + "  chfreq:" + channelFreq);
        for (int i = 0; i < this.channelInfo.size(); i++) {
            if (this.channelInfo.get(i)[3].equals(channelId)) {
                EditChannel.getInstance(this.mContext).setChannelFreq(Integer.parseInt(channelId), channelFreq);
                this.channelInfo.get(i)[4] = channelFreq;
                MtkLog.d(TAG, " channelInfo.get(i)[4]:" + this.channelInfo.get(i)[4]);
                return;
            }
        }
    }

    public void updateChannelNumber(String chId, String channelNum) {
        MtkLog.d(TAG, "chId:" + chId + "   channelNum:" + channelNum);
        int setPosition = -1;
        int position = -1;
        for (int i = 0; i < this.channelInfo.size(); i++) {
            String[] ch = this.channelInfo.get(i);
            if (-1 == position && Float.valueOf(ch[0]).floatValue() > Float.valueOf(channelNum).floatValue()) {
                position = i;
            }
            if (ch[3].equals(chId)) {
                try {
                    int nb = Integer.parseInt(channelNum);
                    this.channelInfo.get(i)[0] = String.valueOf(nb);
                    setPosition = i;
                    int currentId = EditChannel.getInstance(this.mContext).getCurrentChannelId();
                    int NowId = getCurrentIdFromIdAndNum(chId, channelNum);
                    this.ch_list.get(i).setChannelId(NowId);
                    this.channelInfo.get(i)[3] = "" + NowId;
                    if (Integer.parseInt(chId) == currentId) {
                        this.mCommonIntegration.setCurrentChannelId(NowId);
                    }
                    EditChannel.getInstance(this.mContext).setChannelNumber(Integer.parseInt(chId), NowId, nb);
                } catch (Exception e) {
                    return;
                }
            }
        }
        MtkLog.i(TAG, "position:" + position + " setPosition:" + setPosition);
        if (-1 == position) {
            position = this.channelInfo.size();
        }
        MtkLog.i(TAG, "position:" + position + " setPosition:" + setPosition);
        String[] ch2 = this.channelInfo.get(setPosition);
        MtkTvChannelInfoBase selChannel = this.ch_list.get(setPosition);
        if (setPosition < position) {
            int i2 = setPosition;
            while (i2 < position - 1) {
                this.channelInfo.set(i2, this.channelInfo.get(i2 + 1));
                this.ch_list.set(i2, this.ch_list.get(i2 + 1));
                i2++;
            }
            this.channelInfo.set(i2, ch2);
            this.ch_list.set(i2, selChannel);
            return;
        }
        int i3 = setPosition;
        while (i3 > position) {
            this.channelInfo.set(i3, this.channelInfo.get(i3 - 1));
            this.ch_list.set(i3, this.ch_list.get(i3 - 1));
            i3--;
        }
        this.channelInfo.set(i3, ch2);
        this.ch_list.set(i3, selChannel);
    }

    public void updateChannelNumberCnRegion(String chId, String channelNum) {
        MtkLog.d(TAG, "chId:" + chId + "   channelNum:" + channelNum);
        int setPosition = -1;
        int position = -1;
        int i = 0;
        while (true) {
            if (i >= this.channelInfo.size()) {
                break;
            }
            String[] ch = this.channelInfo.get(i);
            if (-1 == position && Float.valueOf(ch[0]).floatValue() > Float.valueOf(channelNum).floatValue()) {
                position = i;
            }
            if (ch[3].equals(chId)) {
                try {
                    int nb = Integer.parseInt(channelNum);
                    this.channelInfo.get(i)[0] = String.valueOf(nb);
                    setPosition = i;
                    int currentId = EditChannel.getInstance(this.mContext).getCurrentChannelId();
                    if (this.mTV.isCurrentSourceATV()) {
                        int NowId = getCurrentIdFromIdAndNum(chId, channelNum);
                        this.ch_list.get(i).setChannelId(NowId);
                        this.channelInfo.get(i)[3] = "" + NowId;
                        if (Integer.parseInt(chId) == currentId) {
                            this.mCommonIntegration.setCurrentChannelId(NowId);
                        }
                        EditChannel.getInstance(this.mContext).setChannelNumber(Integer.parseInt(chId), nb);
                    } else {
                        int NowId2 = Integer.parseInt(chId);
                        if (this.mCommonIntegration.getTunerMode() != 0) {
                            NowId2 = getCurrentIdFromIdAndNumForCNCE(chId, channelNum);
                        }
                        this.ch_list.get(i).setChannelId(NowId2);
                        this.channelInfo.get(i)[3] = "" + NowId2;
                        if (Integer.parseInt(chId) == currentId) {
                            this.mCommonIntegration.setCurrentChannelId(NowId2);
                        }
                        EditChannel.getInstance(this.mContext).setChannelNumber(Integer.parseInt(chId), nb);
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                i++;
            }
        }
        MtkLog.i(TAG, "position:" + position + " setPosition:" + setPosition);
        if (-1 == position) {
            position = this.channelInfo.size();
        }
        MtkLog.i(TAG, "position:" + position + " setPosition:" + setPosition);
        String[] ch2 = this.channelInfo.get(setPosition);
        MtkTvChannelInfoBase selChannel = this.ch_list.get(setPosition);
        if (setPosition < position) {
            int i2 = setPosition;
            while (i2 < position - 1) {
                this.channelInfo.set(i2, this.channelInfo.get(i2 + 1));
                this.ch_list.set(i2, this.ch_list.get(i2 + 1));
                i2++;
            }
            this.channelInfo.set(i2, ch2);
            this.ch_list.set(i2, selChannel);
            return;
        }
        int i3 = setPosition;
        while (i3 > position) {
            this.channelInfo.set(i3, this.channelInfo.get(i3 - 1));
            this.ch_list.set(i3, this.ch_list.get(i3 - 1));
            i3--;
        }
        this.channelInfo.set(i3, ch2);
        this.ch_list.set(i3, selChannel);
    }

    public int getCurrentIdFromIdAndNumForCNDTMB(String strId, String strNum) {
        int major = Integer.parseInt(strNum);
        int index = Integer.parseInt(strId);
        int currentid = ((major & 16383) << 18) | ((((index >> 8) & MtkTvScanCeBase.OPERATOR_SNN_OTHERS) & MtkTvScanCeBase.OPERATOR_SNN_OTHERS) << 8) | (index & 63 & 63) | 128;
        MtkLog.i(TAG, "getCurrentIdFromIdAndNum:currentid:" + currentid);
        return currentid;
    }

    public int getCurrentIdFromIdAndNumForCNCE(String strId, String strNum) {
        int major = Integer.parseInt(strNum);
        int index = Integer.parseInt(strId);
        int currentid = index;
        int lcn = ((currentid >> 8) & MtkTvScanCeBase.OPERATOR_SNN_OTHERS) | ((currentid & 96) << 5);
        int currentid2 = ((major & 16383) << 18) | ((lcn & MtkTvScanCeBase.OPERATOR_SNN_OTHERS) << 8) | (index & 15 & 15) | 128 | ((lcn & 3072) >> 5);
        MtkLog.i(TAG, "getCurrentIdFromIdAndNum:currentid:" + currentid2);
        return currentid2;
    }

    public int getCurrentIdFromIdAndNum(String strId, String strNum) {
        int major = Integer.parseInt(strNum);
        int index = Integer.parseInt(strId);
        int i = index;
        int currentid = ((major & 16383) << 18) | (index & 15 & 15) | 128;
        MtkLog.i(TAG, "getCurrentIdFromIdAndNum:currentid:" + currentid);
        return currentid;
    }

    public void updateChannelColorSystem(int ColorSystemIndex, String[] mData) {
        MtkLog.d(TAG, "ColorSystemIndex:" + ColorSystemIndex);
        MtkTvChannelInfoBase mCurrentEditChannel = this.mCommonIntegration.getChannelById(Integer.parseInt(mData[3]));
        if (mCurrentEditChannel != null && this.mTV.isAnalog(mCurrentEditChannel)) {
            ((MtkTvAnalogChannelInfo) mCurrentEditChannel).setColorSys(ColorSystemIndex - 1);
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(mCurrentEditChannel);
        this.mCommonIntegration.setChannelList(1, list);
        int chId = 0;
        if (mCurrentEditChannel != null) {
            chId = mCurrentEditChannel.getChannelId();
        }
        int i = 0;
        while (i < this.ch_list.size()) {
            MtkTvChannelInfoBase mTempChannel = this.ch_list.get(i);
            MtkLog.d(TAG, "updateChannelColorSystem,currID:" + mTempChannel.getChannelId());
            if (chId != mTempChannel.getChannelId() || !this.mTV.isAnalog(mTempChannel)) {
                i++;
            } else {
                ((MtkTvAnalogChannelInfo) mTempChannel).setColorSys(ColorSystemIndex - 1);
                return;
            }
        }
    }

    public void updateChannelSoundSystem(int ui2_idx, String[] mData) {
        int ui4_audio_sys;
        int ui4_tv_sys;
        int i = ui2_idx;
        MtkLog.i(TAG, "Analog setSoundSystem:ui2_idx:" + i);
        MtkTvChannelInfoBase mCurrentEditChannel = this.mCommonIntegration.getChannelById(Integer.parseInt(mData[3]));
        if (mCurrentEditChannel instanceof MtkTvAnalogChannelInfo) {
            int ui4_reserve_mask = ((MtkTvAnalogChannelInfo) mCurrentEditChannel).getTvSys() & SupportMenu.CATEGORY_MASK;
            switch (i) {
                case 0:
                    ui4_tv_sys = 66;
                    ui4_audio_sys = 130;
                    break;
                case 1:
                    ui4_tv_sys = 66;
                    ui4_audio_sys = 8;
                    break;
                case 2:
                    ui4_tv_sys = 4096;
                    ui4_audio_sys = Cea708CCParser.Const.CODE_C1_CW1;
                    break;
                case 3:
                    ui4_tv_sys = 8192;
                    ui4_audio_sys = Cea708CCParser.Const.CODE_C1_CW1;
                    break;
                case 4:
                    ui4_tv_sys = 256;
                    ui4_audio_sys = 130;
                    break;
                case 5:
                    ui4_tv_sys = 1032;
                    ui4_audio_sys = 130;
                    break;
                case 6:
                    ui4_tv_sys = 1032;
                    ui4_audio_sys = 8;
                    break;
                case 7:
                    ui4_tv_sys = 1032;
                    ui4_audio_sys = 16;
                    break;
                case 8:
                    ui4_tv_sys = 1032;
                    ui4_audio_sys = 32;
                    break;
                case 9:
                    ui4_tv_sys = 49152;
                    ui4_audio_sys = 256;
                    break;
                default:
                    ui4_tv_sys = 66;
                    ui4_audio_sys = 130;
                    break;
            }
            int ui4_tv_sys2 = ui4_tv_sys | ui4_reserve_mask;
            ((MtkTvAnalogChannelInfo) mCurrentEditChannel).setTvSys(ui4_tv_sys2);
            ((MtkTvAnalogChannelInfo) mCurrentEditChannel).setAudioSys(ui4_audio_sys);
            int chId = mCurrentEditChannel.getChannelId();
            int i2 = 0;
            while (true) {
                if (i2 < this.ch_list.size()) {
                    MtkTvChannelInfoBase mTempChannel = this.ch_list.get(i2);
                    MtkLog.d(TAG, "updateChannelSoundSystem,currID:" + mTempChannel.getChannelId());
                    if (chId != mTempChannel.getChannelId() || !this.mTV.isAnalog(mTempChannel)) {
                        i2++;
                    } else {
                        ((MtkTvAnalogChannelInfo) mTempChannel).setTvSys(ui4_tv_sys2);
                        ((MtkTvAnalogChannelInfo) mTempChannel).setAudioSys(ui4_audio_sys);
                    }
                }
            }
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(mCurrentEditChannel);
            this.mCommonIntegration.setChannelList(1, list);
            int i3 = 0;
            while (i3 < this.channelInfo.size()) {
                if (this.channelInfo.get(i3)[3].equals(mData[3])) {
                    int tvsys = ((MtkTvAnalogChannelInfo) mCurrentEditChannel).getTvSys();
                    int colorSys = ((MtkTvAnalogChannelInfo) mCurrentEditChannel).getColorSys();
                    int indexSound = getSoundSystemIndex(tvsys, ((MtkTvAnalogChannelInfo) mCurrentEditChannel).getAudioSys());
                    this.channelInfo.get(i3)[6] = this.mContext.getResources().getStringArray(R.array.menu_tv_sound_system_array)[indexSound];
                    MtkLog.d(TAG, " channelInfo.get(i)[6]:" + this.channelInfo.get(i3)[6] + " channelInfo.get(i)[0]:" + this.channelInfo.get(i3)[0]);
                    return;
                }
                i3++;
                int i4 = ui2_idx;
            }
        }
    }

    public String[] updateChannelData(int ch_num_src, int ch_num_dst) {
        int dst = -1;
        int src = -1;
        for (int i = 0; i < this.channelInfo.size(); i++) {
            if (this.channelInfo.get(i)[3].equals(String.valueOf(ch_num_src))) {
                src = i;
                MtkLog.d(TAG, "channelSort src:" + src + ",[" + i + "]=" + this.channelInfo.get(i)[2]);
                if (dst != -1) {
                    break;
                }
            }
            if (this.channelInfo.get(i)[3].equals(String.valueOf(ch_num_dst))) {
                dst = i;
                MtkLog.d(TAG, "channelSort dst:" + dst + ",[" + i + "]=" + this.channelInfo.get(i)[2]);
                if (src != -1) {
                    break;
                }
            }
        }
        String[] newChannelIds = new String[2];
        if (!(src == -1 || dst == -1)) {
            int srcid = EditChannel.getInstance(this.mContext).getNewChannelId(ch_num_src, ch_num_dst);
            int dstid = EditChannel.getInstance(this.mContext).getNewChannelId(ch_num_dst, ch_num_src);
            this.channelInfo.get(src)[3] = "" + srcid;
            this.channelInfo.get(dst)[3] = "" + dstid;
            this.ch_list.get(src).setChannelId(srcid);
            this.ch_list.get(dst).setChannelId(dstid);
            String[] termpStr = this.channelInfo.get(src);
            MtkLog.d(TAG, "channelSort src:" + termpStr[3] + ",dest==" + this.channelInfo.get(dst)[3]);
            this.channelInfo.set(src, this.channelInfo.get(dst));
            this.channelInfo.set(dst, termpStr);
            MtkLog.d(TAG, "channelSort 22src:" + this.channelInfo.get(src)[3] + ",dest==" + this.channelInfo.get(dst)[3]);
            newChannelIds[0] = this.channelInfo.get(src)[3];
            newChannelIds[1] = this.channelInfo.get(dst)[3];
            this.ch_list.set(src, this.ch_list.get(dst));
            this.ch_list.set(dst, this.ch_list.get(src));
            String listno = this.channelInfo.get(src)[0];
            this.channelInfo.get(src)[0] = this.channelInfo.get(dst)[0];
            this.channelInfo.get(dst)[0] = listno;
        }
        for (int i2 = 0; i2 < this.channelInfo.size(); i2++) {
            MtkLog.d(TAG, "dump num:" + this.channelInfo.get(i2)[3] + ",name:" + this.channelInfo.get(i2)[2]);
        }
        return newChannelIds;
    }

    public void updateChannelIsFine(String chId, int isFinetune) {
        MtkLog.d(TAG, "chId:" + chId + "  isFinetune:" + isFinetune);
        boolean z = false;
        for (int i = 0; i < this.channelInfo.size(); i++) {
            if (this.channelInfo.get(i)[3].equals(chId)) {
                MtkTvChannelInfoBase selChannel = this.mCommonIntegration.getChannelById(Integer.parseInt(chId));
                if (selChannel instanceof MtkTvAnalogChannelInfo) {
                    MtkTvAnalogChannelInfo mtkTvAnalogChannelInfo = (MtkTvAnalogChannelInfo) selChannel;
                    if (isFinetune == 0) {
                        z = true;
                    }
                    mtkTvAnalogChannelInfo.setNoAutoFineTune(z);
                    List<MtkTvChannelInfoBase> list = new ArrayList<>();
                    list.add(selChannel);
                    this.mCommonIntegration.setChannelList(1, list);
                }
                MtkLog.d(TAG, " channelInfo.get(i)[4]:" + this.channelInfo.get(i)[4]);
                return;
            }
        }
    }

    public void updateChannelSkip(String chId, int skip) {
        MtkTvChannelInfoBase selChannel = this.mCommonIntegration.getChannelById(Integer.parseInt(chId));
        if (selChannel != null) {
            MtkLog.d(TAG, "setSkip:" + skip);
            selChannel.setSkip(skip != 0);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonIntegration.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelSkip selChannel is null");
    }

    public void storeChannel(int channelNumber, String channelName, String channelFrequency, int colorSystem, int soundSystem, int autoFineTune, int skip) {
        MtkLog.d(TAG, "channelNumber>>" + channelNumber + ">>" + channelName + ">>" + channelFrequency + ">>>" + colorSystem + ">>>" + soundSystem + ">>>" + autoFineTune + ">>>" + skip);
        MtkTvChannelInfoBase currentChannel = this.mCommonIntegration.getCurChInfo();
        if (currentChannel != null) {
            currentChannel.setChannelNumber(channelNumber);
            currentChannel.setServiceName(channelName);
            boolean z = false;
            if (currentChannel instanceof MtkTvAnalogChannelInfo) {
                currentChannel.setFrequency((int) (Float.parseFloat(channelFrequency) * 1000000.0f));
                ((MtkTvAnalogChannelInfo) currentChannel).setNoAutoFineTune(autoFineTune == 0);
            }
            if (skip != 0) {
                z = true;
            }
            currentChannel.setSkip(z);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(currentChannel);
            this.mCommonIntegration.setChannelList(1, list);
        }
    }

    public List<SatListAdapter.SatItem> buildDVBSInfoItem(Action parentItem, SatelliteInfo info) {
        Action action = parentItem;
        List<SatListAdapter.SatItem> lists = new ArrayList<>();
        action.mSubChildGroup = new ArrayList();
        MtkLog.d(TAG, "info.getEnable()>>" + info.getEnable());
        String title = "";
        if (action.mItemID == MenuConfigManager.DVBS_SAT_MANUAL_TURNING) {
            title = MenuConfigManager.DVBS_SAT_MANUAL_TURNING;
        }
        action.satID = info.getSatlRecId();
        SatListAdapter.SatItem item = null;
        int antennaType = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type");
        if (antennaType == 0) {
            int satlRecId = info.getSatlRecId();
            item = new SatListAdapter.SatItem(satlRecId, "" + 0, info.getSatName(), buildStatusString(info), info.getEnable(), action, this.mContext.getResources().getString(R.string.dvbs_satellite_detail));
        } else if (antennaType == 1) {
            int satlRecId2 = info.getSatlRecId();
            item = new SatListAdapter.SatItem(satlRecId2, "" + 0, info.getSatName(), info.getType(), info.getEnable(), action, this.mContext.getResources().getString(R.string.dvbs_satellite_detail));
        } else if (antennaType == 2) {
            int satlRecId3 = info.getSatlRecId();
            item = new SatListAdapter.SatItem(satlRecId3, "" + 0, info.getSatName(), info.getType(), info.getEnable(), action, this.mContext.getResources().getString(R.string.dvbs_satellite_detail));
        }
        lists.add(item);
        return lists;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0081, code lost:
        if (r1.getEnable() == false) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0084, code lost:
        r18 = r4;
        r25 = r6;
        r26 = r7;
        r29 = r12;
        r30 = r13;
        r31 = r14;
        r14 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0095, code lost:
        r8 = r2 + 1;
        r2 = "";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x009d, code lost:
        if (r9.mItemID != com.mediatek.wwtv.setting.util.MenuConfigManager.DVBS_SAT_MANUAL_TURNING) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x009f, code lost:
        r2 = com.mediatek.wwtv.setting.util.MenuConfigManager.DVBS_SAT_MANUAL_TURNING;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00a1, code lost:
        r19 = r2;
        r9.satID = r1.getSatlRecId();
        r20 = null;
        r17 = java.lang.Math.min(java.lang.Math.max(r1.getPosition() - 1, 0), r7.length - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00be, code lost:
        if (r15 != 0) goto L_0x011d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00c0, code lost:
        r29 = r12;
        r12 = r1;
        r18 = r4;
        r25 = r6;
        r26 = r7;
        r30 = r13;
        r31 = r14;
        r14 = 2;
        r13 = r8;
        r20 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter.SatItem(r1.getSatlRecId(), "" + r8, r1.getSatName(), buildStatusString(r1), r1.getEnable(), r9, r0.mContext.getResources().getString(com.mediatek.wwtv.tvcenter.R.string.dvbs_satellite_detail));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0117, code lost:
        r1 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x011d, code lost:
        r18 = r4;
        r25 = r6;
        r26 = r7;
        r29 = r12;
        r30 = r13;
        r31 = r14;
        r14 = 2;
        r12 = r1;
        r13 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x012e, code lost:
        if (r15 != 1) goto L_0x016b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0130, code lost:
        r16 = r26[r17];
        r20 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter.SatItem(r12.getSatlRecId(), "" + r13, r12.getSatName(), r16, r12.getEnable(), r9, r0.mContext.getResources().getString(com.mediatek.wwtv.tvcenter.R.string.dvbs_satellite_detail));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0168, code lost:
        r1 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x016b, code lost:
        if (r15 != 2) goto L_0x0117;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x016d, code lost:
        r16 = r26[r17];
        r20 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter.SatItem(r12.getSatlRecId(), "" + r13, r12.getSatName(), r16, r12.getEnable(), r9, r0.mContext.getResources().getString(com.mediatek.wwtv.tvcenter.R.string.dvbs_satellite_detail));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x01a6, code lost:
        r11.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x01a9, code lost:
        if (r10 != r14) goto L_0x01ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x01ac, code lost:
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x007a, code lost:
        if (r1.getEnable() != false) goto L_0x0084;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter.SatItem> buildDVBSSATDetailInfo(com.mediatek.wwtv.setting.widget.detailui.Action r33, java.util.List<com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo> r34, int r35) {
        /*
            r32 = this;
            r0 = r32
            r9 = r33
            r10 = r35
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r11 = r1
            r12 = 0
            r13 = 1
            r14 = 2
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r9.mSubChildGroup = r1
            com.mediatek.twoworlds.tv.MtkTvConfig r1 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r2 = "g_bs__bs_sat_antenna_type"
            int r15 = r1.getConfigValue(r2)
            android.content.Context r1 = r0.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2130903067(0x7f03001b, float:1.7412942E38)
            java.lang.String[] r1 = r1.getStringArray(r2)
            r8 = 2
            if (r15 != r8) goto L_0x003d
            android.content.Context r2 = r0.mContext
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2130903058(0x7f030012, float:1.7412923E38)
            java.lang.String[] r1 = r2.getStringArray(r3)
        L_0x003d:
            r7 = r1
            r1 = 0
            int r6 = r34.size()
            r2 = r1
            r1 = 0
        L_0x0045:
            r4 = r1
            if (r4 >= r6) goto L_0x01bc
            r3 = r34
            java.lang.Object r1 = r3.get(r4)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r1 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r1
            java.lang.String r8 = "MenuDataHelper"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r3 = "info.getEnable()>>"
            r5.append(r3)
            boolean r3 = r1.getEnable()
            r5.append(r3)
            java.lang.String r3 = ">>>"
            r5.append(r3)
            r5.append(r10)
            java.lang.String r3 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r3)
            switch(r10) {
                case 1: goto L_0x007d;
                case 2: goto L_0x0076;
                default: goto L_0x0075;
            }
        L_0x0075:
            goto L_0x0095
        L_0x0076:
            boolean r3 = r1.getEnable()
            if (r3 == 0) goto L_0x0095
            goto L_0x0084
        L_0x007d:
            boolean r3 = r1.getEnable()
            if (r3 != 0) goto L_0x0095
        L_0x0084:
            r18 = r4
            r25 = r6
            r26 = r7
            r29 = r12
            r30 = r13
            r31 = r14
            r14 = 2
            r22 = 0
            goto L_0x01ad
        L_0x0095:
            int r8 = r2 + 1
            java.lang.String r2 = ""
            java.lang.String r3 = r9.mItemID
            java.lang.String r5 = "DVBS_SAT_MANUAL_TURNING"
            if (r3 != r5) goto L_0x00a1
            java.lang.String r2 = "DVBS_SAT_MANUAL_TURNING"
        L_0x00a1:
            r19 = r2
            int r2 = r1.getSatlRecId()
            r9.satID = r2
            r20 = 0
            r21 = 0
            int r2 = r1.getPosition()
            r3 = 1
            int r2 = r2 - r3
            r5 = 0
            int r2 = java.lang.Math.max(r2, r5)
            int r5 = r7.length
            int r5 = r5 - r3
            int r17 = java.lang.Math.min(r2, r5)
            if (r15 != 0) goto L_0x011d
            com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem r23 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem
            int r3 = r1.getSatlRecId()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r2 = ""
            r5.append(r2)
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            java.lang.String r25 = r1.getSatName()
            java.lang.String r26 = r0.buildStatusString(r1)
            boolean r27 = r1.getEnable()
            android.content.Context r2 = r0.mContext
            android.content.res.Resources r2 = r2.getResources()
            r28 = r1
            r1 = 2131689852(0x7f0f017c, float:1.9008731E38)
            java.lang.String r24 = r2.getString(r1)
            r2 = r28
            r1 = r23
            r29 = r12
            r12 = r2
            r2 = r3
            r3 = r5
            r18 = r4
            r4 = r25
            r22 = 0
            r5 = r26
            r25 = r6
            r6 = r27
            r26 = r7
            r7 = r9
            r30 = r13
            r31 = r14
            r14 = 2
            r13 = r8
            r8 = r24
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            r20 = r23
        L_0x0117:
            r1 = r20
            r16 = r21
            goto L_0x01a6
        L_0x011d:
            r18 = r4
            r25 = r6
            r26 = r7
            r29 = r12
            r30 = r13
            r31 = r14
            r14 = 2
            r22 = 0
            r12 = r1
            r13 = r8
            if (r15 != r3) goto L_0x016b
            r16 = r26[r17]
            com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem r21 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem
            int r2 = r12.getSatlRecId()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = ""
            r1.append(r3)
            r1.append(r13)
            java.lang.String r3 = r1.toString()
            java.lang.String r4 = r12.getSatName()
            boolean r6 = r12.getEnable()
            android.content.Context r1 = r0.mContext
            android.content.res.Resources r1 = r1.getResources()
            r5 = 2131689852(0x7f0f017c, float:1.9008731E38)
            java.lang.String r8 = r1.getString(r5)
            r1 = r21
            r5 = r16
            r7 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            r20 = r21
        L_0x0168:
            r1 = r20
            goto L_0x01a6
        L_0x016b:
            if (r15 != r14) goto L_0x0117
            r16 = r26[r17]
            com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem r21 = new com.mediatek.wwtv.setting.base.scan.adapter.SatListAdapter$SatItem
            int r2 = r12.getSatlRecId()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = ""
            r1.append(r3)
            r1.append(r13)
            java.lang.String r3 = r1.toString()
            java.lang.String r4 = r12.getSatName()
            boolean r6 = r12.getEnable()
            android.content.Context r1 = r0.mContext
            android.content.res.Resources r1 = r1.getResources()
            r5 = 2131689852(0x7f0f017c, float:1.9008731E38)
            java.lang.String r8 = r1.getString(r5)
            r1 = r21
            r5 = r16
            r7 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            r20 = r21
            goto L_0x0168
        L_0x01a6:
            r11.add(r1)
            if (r10 != r14) goto L_0x01ac
            goto L_0x01c7
        L_0x01ac:
            r2 = r13
        L_0x01ad:
            int r1 = r18 + 1
            r8 = r14
            r6 = r25
            r7 = r26
            r12 = r29
            r13 = r30
            r14 = r31
            goto L_0x0045
        L_0x01bc:
            r25 = r6
            r26 = r7
            r29 = r12
            r30 = r13
            r31 = r14
            r13 = r2
        L_0x01c7:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.MenuDataHelper.buildDVBSSATDetailInfo(com.mediatek.wwtv.setting.widget.detailui.Action, java.util.List, int):java.util.List");
    }

    private String buildStatusString(SatelliteInfo info) {
        int motoType;
        int diseqc10Port;
        int diseqc11Port;
        if (MtkLog.logOnFlag) {
            MtkLog.d(TAG, "sat:" + info.toString());
        }
        String[] diseqc10PortList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_10_port_sub_arrays);
        String[] diseqc11PortList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_11_port_sub_arrays);
        String[] diseqcMotorList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_motor_arrays);
        if (info.getMotorType() == 5) {
            motoType = 1;
        } else {
            motoType = 0;
        }
        if (info.getDiseqcType() == 0 || info.getPort() == 255) {
            diseqc10Port = 0;
        } else {
            diseqc10Port = info.getPort() + 1 > diseqc10PortList.length + -1 ? diseqc10PortList.length - 1 : info.getPort() + 1;
        }
        if (info.getDiseqcTypeEx() == 0 || info.getPortEx() == 255) {
            diseqc11Port = 0;
        } else {
            diseqc11Port = info.getPortEx() + 1 > diseqc11PortList.length + -1 ? diseqc11PortList.length - 1 : info.getPortEx() + 1;
        }
        MtkLog.d(TAG, "id:" + info.getSatlRecId() + ",10port:" + diseqc10Port + ",11port:" + diseqc11Port + ",MotorType:" + motoType + ",enable:" + info.getEnable());
        return diseqcMotorList[motoType] + "+" + diseqc11PortList[diseqc11Port] + "+" + diseqc10PortList[diseqc10Port];
    }

    private List<Action> buildDVBSScanItem(Action parentItem, int satID) {
        Action action = parentItem;
        int i = satID;
        List<Action> items = new ArrayList<>();
        List<String> scanModeList = ScanContent.getDVBSScanMode(this.mContext);
        List<String> scanChannels = ScanContent.getDVBSConfigInfoChannels(this.mContext);
        Action action2 = new Action(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG, this.mContext.getResources().getString(R.string.dvbs_scan_mode), 10004, 10004, 0, (String[]) scanModeList.toArray(new String[scanModeList.size()]), 1, Action.DataType.OPTIONVIEW);
        action2.satID = i;
        action2.mLocationId = action.mLocationId;
        action2.setmParent(action);
        action2.setmParentGroup(action.mParent.mSubChildGroup);
        items.add(action2);
        if (scanModeList.size() > 1) {
            action2.setEnabled(true);
        } else {
            action2.setEnabled(false);
        }
        Action action3 = new Action(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG, this.mContext.getResources().getString(R.string.dvbs_satellite_channel), 10004, 10004, 1, (String[]) scanChannels.toArray(new String[scanChannels.size()]), 1, Action.DataType.OPTIONVIEW);
        action3.satID = i;
        action3.mLocationId = action.mLocationId;
        action3.setmParent(action);
        action3.setmParentGroup(action.mParent.mSubChildGroup);
        items.add(action3);
        Action action4 = new Action(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_START_SCAN, this.mContext.getResources().getString(R.string.menu_c_scan), 10004, 10004, 10004, (String[]) null, 1, Action.DataType.HAVESUBCHILD);
        action4.satID = i;
        action4.mLocationId = action.mLocationId;
        action4.setmParent(action);
        action4.setmParentGroup(action.mParent.mSubChildGroup);
        items.add(action4);
        return items;
    }

    public boolean checkDuplicate(String chno) {
        if (this.channelInfo == null) {
            return false;
        }
        float f = Float.valueOf(chno).floatValue();
        boolean isduplicate = false;
        for (int i = 0; i < this.channelInfo.size(); i++) {
            String num = this.channelInfo.get(i)[0];
            MtkLog.i("check", "num:" + num);
            if (num != null && f == Float.valueOf(num).floatValue()) {
                MtkLog.i("check", "num:" + num + " chno:" + chno + "--------------------");
                isduplicate = true;
            }
        }
        return isduplicate;
    }

    public void changeItemAfterTurn(ActionFragment frag, Action tvFirstVoice, Action tvFirstLanguageEU, Action tvSecondLanguageEU) {
        if (frag != null) {
            ActionAdapter adapter = (ActionAdapter) frag.getAdapter();
            List<Action> group = adapter.getActions();
            if (!CommonIntegration.isCNRegion()) {
                if (this.mCommonIntegration.isCurrentSourceDTV()) {
                    group.remove(tvFirstVoice);
                }
                if ((this.mCommonIntegration.isCurrentSourceATV() || !this.mTV.iCurrentInputSourceHasSignal()) && !group.contains(tvFirstVoice)) {
                    if (this.mTV.isAusCountry()) {
                        group.add(2, tvFirstVoice);
                    } else {
                        group.add(1, tvFirstVoice);
                    }
                }
                if (!this.mTV.isConfigVisible("g_menu__audio_lang_attr")) {
                    group.remove(tvFirstLanguageEU);
                    group.remove(tvSecondLanguageEU);
                } else if (!group.contains(tvFirstLanguageEU)) {
                    if (this.mTV.isAusCountry()) {
                        group.add(2, tvFirstLanguageEU);
                        group.add(3, tvSecondLanguageEU);
                    } else {
                        group.add(1, tvFirstLanguageEU);
                        group.add(2, tvSecondLanguageEU);
                    }
                }
                adapter.notifyDataSetChanged();
            } else if (!this.mTV.isConfigVisible("g_menu__audio_lang_attr")) {
                group.remove(tvFirstLanguageEU);
                group.remove(tvSecondLanguageEU);
            } else if (!group.contains(tvFirstLanguageEU)) {
                group.add(1, tvFirstLanguageEU);
                group.add(2, tvSecondLanguageEU);
            }
        }
    }

    public void saveDVBSSatTPInfo(Context context, int satID, ScrollAdapterView listview) {
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = new MtkTvScanDvbsBase.MtkTvScanTpInfo();
        int[] transInfos = new int[3];
        for (int i = 0; i < 3; i++) {
            Action action = (Action) listview.getChildAt(i).getTag(R.id.action_title);
            transInfos[i] = action.mInitValue;
            if (i < 2) {
                action.setDescription("" + transInfos[i]);
            }
        }
        tpInfo.i4Frequency = transInfos[0];
        tpInfo.i4Symbolrate = transInfos[1];
        switch (transInfos[2]) {
            case 0:
                tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_LIN_HORIZONTAL;
                break;
            case 1:
                tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_LIN_VERTICAL;
                break;
            case 2:
                tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_CIR_LEFT;
                break;
            case 3:
                tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_CIR_RIGHT;
                break;
        }
        ScanContent.setDVBSTPInfo(satID, tpInfo);
    }

    public void saveDVBSSatTPInfo(Context context, int satID, String value) {
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = ScanContent.getDVBSTransponder(satID);
        String[] tpStrings = context.getResources().getStringArray(R.array.dvbs_tp_pol_arrays);
        if (tpStrings[0].equals(value)) {
            tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_LIN_HORIZONTAL;
        } else if (tpStrings[1].equals(value)) {
            tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_LIN_VERTICAL;
        } else if (tpStrings[2].equals(value)) {
            tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_CIR_LEFT;
        } else if (tpStrings[3].equals(value)) {
            tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_CIR_RIGHT;
        }
        MtkLog.d(TAG, "tpInfo.ePol=" + tpInfo.ePol + ",tpInfo=" + tpInfo);
        ScanContent.setDVBSTPInfo(satID, tpInfo);
    }

    public void setDiseqc10TunerPort(int value) {
        final int initValue = value - 1;
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
                MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_INTERNAL_ERROR;
                boolean isNeedReset = SatDetailUI.getInstance(MenuDataHelper.this.mContext).mDvbsNeedTunerReset;
                switch (initValue) {
                    case -1:
                        MtkLog.d(MenuDataHelper.TAG, "setDiseqc12TunerPort>>dvbsSetTunerDiseqc10Disable>dvbsRet>" + dvbsRet);
                        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet2 = dvbsScan.dvbsSetTunerDiseqc10Disable();
                        return;
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet3 = dvbsScan.dvbsSetTunerDiseqc10Port(initValue);
                        MtkLog.d(MenuDataHelper.TAG, "setDiseqc12TunerPort>>dvbsSetTunerDiseqc10Port>dvbsRet>" + dvbsRet3);
                        return;
                    case 4:
                        if (isNeedReset) {
                            MtkTvScanDvbsBase.ScanDvbsRet dvbsRet4 = dvbsScan.dvbsSetTunerDiseqc10Reset();
                        }
                        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet5 = dvbsScan.dvbsSetTunerDiseqc10ToneBurst(0);
                        MtkLog.d(MenuDataHelper.TAG, "setDiseqc12TunerPort>>mDvbsNeedTunerReset dvbsSetTunerDiseqc10ToneBurst A>dvbsRet>" + dvbsRet5 + ">>>" + isNeedReset);
                        return;
                    case 5:
                        if (isNeedReset) {
                            MtkTvScanDvbsBase.ScanDvbsRet dvbsRet6 = dvbsScan.dvbsSetTunerDiseqc10Reset();
                        }
                        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet7 = dvbsScan.dvbsSetTunerDiseqc10ToneBurst(1);
                        MtkLog.d(MenuDataHelper.TAG, "setDiseqc12TunerPort>>mDvbsNeedTunerReset dvbsSetTunerDiseqc10ToneBurst B>dvbsRet>" + dvbsRet7 + ">>>" + isNeedReset);
                        return;
                    case 6:
                        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet8 = dvbsScan.dvbsSetTunerDiseqc10Disable();
                        MtkLog.d(MenuDataHelper.TAG, "setDiseqc12TunerPort>>dvbsSetTunerDiseqc10Disable>dvbsRet>" + dvbsRet8);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    public void setDiseqc11TunerPort(final int initValue) {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                MtkLog.d(MenuDataHelper.TAG, "setDiseqc11TunerPort initValue:" + initValue);
                MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
                if (initValue == 0) {
                    dvbsScan.dvbsSetTunerDiseqc11Disable();
                } else {
                    dvbsScan.dvbsSetTunerDiseqc11Port(initValue - 1);
                }
            }
        });
    }

    public void setDiseqc12MotorPageTuner(String mId, int initValue) {
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_INTERNAL_ERROR;
        if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12DisableLimits();
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12LimitEast();
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12LimitWest();
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12StorePos(initValue + 1);
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12GotoPos(initValue + 1);
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12GotoPos(0);
        }
        MtkLog.d(TAG, "setDiseqc12MotorPageTuner>>dataItem>dvbsRet>" + dvbsRet + ">>" + mId + ">>>" + initValue);
    }

    public void setDiseqc12MovementControlPageTuner(String mId) {
        SaveValue savevalue = SaveValue.getInstance(this.mContext);
        int defaultMovementControl = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL);
        int stepSize = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE);
        int timeout = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS);
        if (stepSize == 0) {
            stepSize = 1;
        }
        if (timeout == 0) {
            timeout = 1;
        }
        MtkLog.d(TAG, "setDiseqc12MovementControlPageTuner>>dataItem>>" + defaultMovementControl + ">>>" + stepSize + ">>" + timeout);
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        MtkTvScanDvbsBase.ScanDvbsRet dvbsRet = MtkTvScanDvbsBase.ScanDvbsRet.SCAN_DVBS_RET_INTERNAL_ERROR;
        if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST)) {
            if (defaultMovementControl == 0) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveEast(0);
            } else if (defaultMovementControl == 1) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveEast(stepSize - (stepSize * 2));
            } else if (defaultMovementControl == 2) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveEast(timeout);
            }
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST)) {
            if (defaultMovementControl == 0) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveWest(0);
            } else if (defaultMovementControl == 1) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveWest(stepSize - (stepSize * 2));
            } else if (defaultMovementControl == 2) {
                dvbsRet = dvbsScan.dvbsSetTunerDiseqc12MoveWest(timeout);
            }
        } else if (mId.equals(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT)) {
            dvbsRet = dvbsScan.dvbsSetTunerDiseqc12StopMove();
        }
        MtkLog.d(TAG, "setDiseqc12MovementControlPageTuner>>dataItem>dvbsRet>" + dvbsRet + ">>>" + defaultMovementControl + ">>" + mId + ">>>");
    }

    public void setTimeZone(int value) {
        int value2;
        if (value >= 0 && value < 13) {
            value2 = value + 22;
        } else if (value == 13) {
            value2 = 0;
        } else if (value <= 13 || value >= 35) {
            value2 = 13;
        } else {
            value2 = value - 13;
        }
        int tz_offset = MenuConfigManager.zoneValue[value2];
        boolean dsl = true;
        if (value2 == 0) {
            MtkLog.v(TAG, "*etConfigValue(MtkTvConfigType.CFG_TIME_TZ_SYNC_WITH_TS*");
            TVContent.getInstance(this.mContext).setConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 1);
        } else {
            TVContent.getInstance(this.mContext).setConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS, 0);
            if (TVContent.getInstance(this.mContext).getConfigValue(MtkTvConfigTypeBase.CFG_TIME_AUTO_DST) != 1) {
                dsl = false;
            }
            if (CommonIntegration.isSARegion() && (tz_offset = tz_offset + 10800) == 57600) {
                tz_offset = -57600;
            }
            MtkLog.d(TAG, "value:" + value2 + "dsl:" + dsl);
            TVContent.getInstance(this.mContext).setConfigValue("g_time__time_zone", tz_offset, dsl);
        }
        MtkLog.d("zone", "tz_offset:" + tz_offset);
        TVContent.getInstance(this.mContext).setConfigValue("g_time__time_zone", tz_offset);
    }

    public int[] getSupportScreenModes() {
        return MtkTvAVMode.getInstance().getAllScreenMode();
    }

    public String[] getScreenMode() {
        String[] mScreenMode = this.mConfigManager.getSupporScreenMode(getSupportScreenModes());
        if (mScreenMode != null) {
            for (String s : mScreenMode) {
                MtkLog.d(TAG, "screen mode is :" + s);
            }
        }
        return mScreenMode;
    }

    public List<MtkTvBisskeyInfoBase> getBisskeyInfoList() {
        List<MtkTvBisskeyInfoBase> list = new ArrayList<>();
        MtkTvBisskeyBase bisskeyBase = new MtkTvBisskeyBase();
        int num = bisskeyBase.getRecordsNumber(3);
        Log.d(TAG, "getBisskeyInfoList:num==" + num);
        for (int i = 0; i < num; i++) {
            MtkTvBisskeyInfoBase bisskeyInfo = bisskeyBase.getRecordByIndex(3, i);
            if (bisskeyInfo != null) {
                Log.d(TAG, "getBisskeyInfoList:info progID==" + bisskeyInfo.getProgramId());
            }
            list.add(bisskeyInfo);
        }
        return list;
    }

    public List<BissListAdapter.BissItem> convertToBissItemList() {
        String threePry;
        List<BissListAdapter.BissItem> bissList = new ArrayList<>();
        List<MtkTvBisskeyInfoBase> list = getBisskeyInfoList();
        for (int i = 0; i < list.size(); i++) {
            MtkTvBisskeyInfoBase info = list.get(i);
            int recId = info.getBslRecId();
            int progId = info.getProgramId();
            int freq = info.getFrequency();
            int symRate = info.getSymRate();
            int pola = info.getPolarization();
            byte[] cwkeyarr = info.getServiceCwKey();
            if (pola <= 0) {
                threePry = "H";
            } else {
                threePry = MtkTvRatingConvert2Goo.SUB_RATING_STR_V;
            }
            String threePry2 = freq + threePry + symRate;
            String cwKey = byte2HexStr(cwkeyarr);
            Log.d(TAG, "convertToBissItemList:recId,progId,threePry,cwKey,pola:" + recId + "|" + progId + "|" + threePry2 + "|" + cwKey + "|" + pola);
            bissList.add(new BissListAdapter.BissItem(recId, progId, threePry2, cwKey));
        }
        return bissList;
    }

    public BissListAdapter.BissItem getDefaultBissItem() {
        String threePry;
        MtkTvBisskeyInfoBase info = new MtkTvBisskeyBase().bisskeyGetDefaultRecord();
        int recId = 0;
        int progId = 0;
        int freq = 0;
        int symRate = 0;
        int pola = 0;
        byte[] cwkeyarr = null;
        if (info != null) {
            recId = info.getBslRecId();
            progId = info.getProgramId();
            freq = info.getFrequency();
            symRate = info.getSymRate();
            pola = info.getPolarization();
            cwkeyarr = info.getServiceCwKey();
        }
        if (pola <= 0) {
            threePry = "H";
        } else {
            threePry = MtkTvRatingConvert2Goo.SUB_RATING_STR_V;
        }
        String threePry2 = freq + threePry + symRate;
        if (cwkeyarr != null) {
            String cwKey = byte2HexStr(cwkeyarr);
            Log.d(TAG, "getDefaultBissItem:progId,threePry,cwKey:" + recId + "|" + progId + "|" + threePry2 + "|" + cwKey);
            return new BissListAdapter.BissItem(recId, progId, threePry2, cwKey);
        }
        Log.d(TAG, "cwkeyarr == null ");
        return null;
    }

    private boolean checkBissKeyInfoExist(MtkTvBisskeyBase bisskeyBase, MtkTvBisskeyInfoBase info) {
        List<MtkTvBisskeyInfoBase> list = getBisskeyInfoList();
        if (list == null) {
            return false;
        }
        for (MtkTvBisskeyInfoBase beComp : list) {
            if (info.getProgramId() == beComp.getProgramId() && bisskeyBase.bisskeyIsSameRecord(info, beComp)) {
                return true;
            }
        }
        return false;
    }

    public int operateBissKeyinfo(BissListAdapter.BissItem item, int flag) {
        MtkTvBisskeyInfoBase info;
        int pola;
        int symrate;
        int freq;
        MtkTvBisskeyBase bisskeyBase = new MtkTvBisskeyBase();
        if (flag > 0) {
            info = new MtkTvBisskeyInfoBase(3, item.bnum);
        } else {
            info = new MtkTvBisskeyInfoBase();
        }
        if (item.threePry.contains("H")) {
            String[] spls = item.threePry.split("H");
            freq = Integer.parseInt(spls[0]);
            symrate = Integer.parseInt(spls[1]);
            pola = 0;
        } else {
            String[] spls2 = item.threePry.split(MtkTvRatingConvert2Goo.SUB_RATING_STR_V);
            freq = Integer.parseInt(spls2[0]);
            symrate = Integer.parseInt(spls2[1]);
            pola = 1;
        }
        Log.d(TAG, "setup value pola:" + pola + ",freq:" + freq + ",symrate:" + symrate);
        info.setFrequency(freq);
        info.setSymRate(symrate);
        info.setPolarization(pola);
        info.setProgramId(item.progId);
        hexStr2Bytes(item.cwKey);
        info.setServiceCwKey(hexStr2Bytes(item.cwKey));
        if (flag == 0 && checkBissKeyInfoExist(bisskeyBase, info)) {
            return -2;
        }
        int ret = bisskeyBase.setBisskeyInfo(3, info, flag);
        bisskeyBase.bisskeySetKeyForCurrentChannel();
        return ret;
    }

    private byte uniteBytes(String src0, String src1) {
        byte b0 = (byte) (Byte.decode("0x" + src0).byteValue() << 4);
        return (byte) (b0 | Byte.decode("0x" + src1).byteValue());
    }

    public byte[] hexStr2Bytes(String src) {
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            int m = (i * 2) + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, m + 1));
        }
        return ret;
    }

    public String byte2HexStr(byte[] b) {
        String hs = "";
        for (byte b2 : b) {
            String stmp = Integer.toHexString(b2 & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public void startSleepTimerTask(String text) {
        long mills;
        MtkLog.d(TAG, "sleep timer text is :" + text);
        if (text == null || !text.contains(" ")) {
            Intent intent = new Intent("com.mediatek.ui.menu.util.sleep.timer");
            intent.putExtra("itemId", "SETUP_sleep_timer");
            intent.putExtra("mills", 0);
            this.mContext.sendBroadcast(intent);
            MtkLog.d(TAG, "sleep timer sendbroadcast :" + text);
            return;
        }
        try {
            mills = (long) ((Integer.parseInt(text.substring(0, text.indexOf(" "))) - 5) * 60 * 1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mills = 300000;
        }
        Intent intent2 = new Intent("com.mediatek.ui.menu.util.sleep.timer");
        intent2.putExtra("itemId", "SETUP_sleep_timer");
        intent2.putExtra("mills", mills);
        this.mContext.sendBroadcast(intent2);
        MtkLog.d(TAG, "sleep timer sendbroadcast :" + text);
    }

    public void startAutoSleepTask(String text) {
        MtkLog.d(TAG, "auto sleep text is :" + text);
        if (!TextUtils.isEmpty(text)) {
            char ch = text.charAt(0);
            if (ch > '9' || ch < '0') {
                Intent intent = new Intent("com.mediatek.ui.menu.util.sleep.timer");
                intent.putExtra("itemId", "g_misc__auto_sleep");
                intent.putExtra("mills", 0);
                this.mContext.sendBroadcast(intent);
                return;
            }
            Intent intent2 = new Intent("com.mediatek.ui.menu.util.sleep.timer");
            intent2.putExtra("itemId", "g_misc__auto_sleep");
            intent2.putExtra("mills", (((long) (Integer.parseInt(String.valueOf(ch)) * 60)) - 5) * 60 * 1000);
            this.mContext.sendBroadcast(intent2);
        }
    }

    public void resetCallFlashStore() {
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    private List<TkgsLocatorListAdapter.TkgsLocatorItem> tempTKGSLocList() {
        TkgsLocatorListAdapter.TkgsLocatorItem data1 = new TkgsLocatorListAdapter.TkgsLocatorItem(1, 8001, "12423V27500");
        TkgsLocatorListAdapter.TkgsLocatorItem data2 = new TkgsLocatorListAdapter.TkgsLocatorItem(2, 8002, "11593H27500");
        TkgsLocatorListAdapter.TkgsLocatorItem data3 = new TkgsLocatorListAdapter.TkgsLocatorItem(3, 8181, "12559V27500");
        if (!this.tkgsList.contains(data1)) {
            this.tkgsList.add(data1);
        }
        if (!this.tkgsList.contains(data2)) {
            this.tkgsList.add(data2);
        }
        if (!this.tkgsList.contains(data3)) {
            this.tkgsList.add(data3);
        }
        return this.tkgsList;
    }

    public List<TkgsLocatorListAdapter.TkgsLocatorItem> convertToTKGSLocatorList() {
        String threePry;
        this.tkgsList.clear();
        this.TKGSVisibleLocSize = 0;
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        if (dvbsScan.dvbsTKGSGetAllVisibleLocators().ordinal() != 0) {
            return this.tkgsList;
        }
        MtkTvScanDvbsBase.TKGSOneLocator[] locArrays = dvbsScan.TKGS_visibleLocatorsList;
        sortLocArrays(locArrays);
        if (locArrays == null) {
            return this.tkgsList;
        }
        for (MtkTvScanDvbsBase.TKGSOneLocator oneLoc : locArrays) {
            int recId = oneLoc.recordID;
            int progId = oneLoc.PID;
            int freq = oneLoc.tpInfo.i4Frequency;
            int symRate = oneLoc.tpInfo.i4Symbolrate;
            if (oneLoc.tpInfo.ePol.ordinal() <= 1) {
                threePry = "H";
            } else {
                threePry = MtkTvRatingConvert2Goo.SUB_RATING_STR_V;
            }
            String threePry2 = freq + threePry + symRate;
            MtkLog.d(TAG, "convertToTKGSItemList:recId,progId,threePry:" + recId + "|" + progId + "|" + threePry2);
            this.tkgsList.add(new TkgsLocatorListAdapter.TkgsLocatorItem(recId, progId, threePry2));
            this.TKGSVisibleLocSize = this.TKGSVisibleLocSize + 1;
        }
        List<TkgsLocatorListAdapter.TkgsLocatorItem> retList = new ArrayList<>();
        retList.addAll(this.tkgsList);
        return retList;
    }

    private MtkTvScanDvbsBase.TKGSOneLocator[] sortLocArrays(MtkTvScanDvbsBase.TKGSOneLocator[] locArrays) {
        if (!(locArrays == null || locArrays.length == 0)) {
            for (int i = 0; i < locArrays.length - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < locArrays.length; j++) {
                    if (locArrays[j].tpInfo.i4Frequency < locArrays[minIndex].tpInfo.i4Frequency) {
                        minIndex = j;
                    }
                }
                if (minIndex != i) {
                    MtkTvScanDvbsBase.TKGSOneLocator temp = locArrays[i];
                    locArrays[i] = locArrays[minIndex];
                    locArrays[minIndex] = temp;
                }
            }
        }
        return locArrays;
    }

    public List<TkgsLocatorListAdapter.TkgsLocatorItem> getHiddenTKGSLocatorListDev() {
        List<TkgsLocatorListAdapter.TkgsLocatorItem> hiddList = new ArrayList<>();
        TkgsLocatorListAdapter.TkgsLocatorItem data1 = new TkgsLocatorListAdapter.TkgsLocatorItem(1, 8101, "11413V27411");
        TkgsLocatorListAdapter.TkgsLocatorItem data2 = new TkgsLocatorListAdapter.TkgsLocatorItem(2, 8112, "12573H27412");
        TkgsLocatorListAdapter.TkgsLocatorItem data3 = new TkgsLocatorListAdapter.TkgsLocatorItem(3, 8091, "13599V27413");
        data1.setEnabled(false);
        data2.setEnabled(false);
        data3.setEnabled(false);
        if (!hiddList.contains(data1)) {
            hiddList.add(data1);
        }
        if (!hiddList.contains(data2)) {
            hiddList.add(data2);
        }
        if (!hiddList.contains(data3)) {
            hiddList.add(data3);
        }
        return hiddList;
    }

    public List<TkgsLocatorListAdapter.TkgsLocatorItem> getHiddenTKGSLocatorList() {
        MtkTvScanDvbsBase.TKGSOneLocator[] locArrays;
        String threePry;
        List<TkgsLocatorListAdapter.TkgsLocatorItem> hiddList = new ArrayList<>();
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        if (dvbsScan.dvbsTKGSGetAllHiddenLocators().ordinal() != 0 || (locArrays = dvbsScan.TKGS_hiddenLocatorsList) == null) {
            return hiddList;
        }
        for (MtkTvScanDvbsBase.TKGSOneLocator oneLoc : locArrays) {
            int recId = oneLoc.recordID;
            int progId = oneLoc.PID;
            int freq = oneLoc.tpInfo.i4Frequency;
            int symRate = oneLoc.tpInfo.i4Symbolrate;
            if (oneLoc.tpInfo.ePol.ordinal() <= 1) {
                threePry = "H";
            } else {
                threePry = MtkTvRatingConvert2Goo.SUB_RATING_STR_V;
            }
            String threePry2 = freq + threePry + symRate;
            MtkLog.d(TAG, "getHiddenTKGSLocatorList:recId,progId,threePry:" + recId + "|" + progId + "|" + threePry2);
            TkgsLocatorListAdapter.TkgsLocatorItem item = new TkgsLocatorListAdapter.TkgsLocatorItem(recId, progId, threePry2);
            item.setEnabled(false);
            hiddList.add(item);
        }
        return hiddList;
    }

    public TkgsLocatorListAdapter.TkgsLocatorItem getDefaultTKGSLocItem() {
        String threePry = 12423 + MtkTvRatingConvert2Goo.SUB_RATING_STR_V + 27500;
        MtkLog.d(TAG, "getDefaultTKGSItem:progId,threePry:" + -1 + "|" + 8181 + "|" + threePry);
        return new TkgsLocatorListAdapter.TkgsLocatorItem(-1, 8181, threePry);
    }

    private boolean checkTKGSLocatorInfoExist(MtkTvScanDvbsBase.TKGSOneLocator oneLoc) {
        MtkTvScanDvbsBase.TKGSOneLocator[] locArrays;
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        int dvbsRet = dvbsScan.dvbsTKGSGetAllVisibleLocators().ordinal();
        if (dvbsRet != 0 || (locArrays = dvbsScan.TKGS_visibleLocatorsList) == null) {
            return false;
        }
        int dvbsRet2 = dvbsRet;
        for (int i = 0; i < locArrays.length; i++) {
            if (oneLoc.recordID != locArrays[i].recordID && (dvbsRet2 = dvbsScan.dvbsTKGSIsSameVisibleLocator(locArrays[i], oneLoc).ordinal()) == 0 && dvbsScan.TKGS_isSameLocator.booleanValue()) {
                return true;
            }
        }
        int i2 = dvbsRet2;
        return false;
    }

    public int operateTKGSLocatorinfo(TkgsLocatorListAdapter.TkgsLocatorItem item, int flag) {
        int pola;
        int symrate;
        int freq;
        TkgsLocatorListAdapter.TkgsLocatorItem tkgsLocatorItem = item;
        int i = flag;
        if (tkgsLocatorItem.threePry.contains("H")) {
            String[] spls = tkgsLocatorItem.threePry.split("H");
            freq = Integer.parseInt(spls[0]);
            symrate = Integer.parseInt(spls[1]);
            pola = 1;
        } else {
            String[] spls2 = tkgsLocatorItem.threePry.split(MtkTvRatingConvert2Goo.SUB_RATING_STR_V);
            freq = Integer.parseInt(spls2[0]);
            symrate = Integer.parseInt(spls2[1]);
            pola = 2;
        }
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        Objects.requireNonNull(dvbsScan);
        MtkTvScanDvbsBase.TKGSOneLocator oneLoc = new MtkTvScanDvbsBase.TKGSOneLocator(tkgsLocatorItem.progId, freq, symrate, pola);
        for (int i2 = 0; i2 < this.tkgsList.size(); i2++) {
            MtkLog.d(TAG, "tkgsList:" + this.tkgsList.get(i2).getTitle());
        }
        if (i == 0) {
            if (checkTKGSLocatorInfoExist(oneLoc)) {
                return -2;
            }
            if (this.tkgsList.size() >= 5) {
                return -9;
            }
            return dvbsScan.dvbsTKGSAddOneVisibleLocator(oneLoc).ordinal();
        } else if (i == 1) {
            oneLoc.recordID = tkgsLocatorItem.bnum;
            if (checkTKGSLocatorInfoExist(oneLoc)) {
                return -2;
            }
            return dvbsScan.dvbsTKGSUpdOneVisibleLocator(oneLoc).ordinal();
        } else if (i == 2) {
            return dvbsScan.dvbsTKGSDelOneVisibleLocator(tkgsLocatorItem.bnum).ordinal();
        } else {
            MtkLog.d(TAG, "operateTKGSLocatorinfo:should not be here ");
            return -1;
        }
    }

    public boolean operateTKGSLocatorinfoDev(TkgsLocatorListAdapter.TkgsLocatorItem item, int flag) {
        boolean ret = false;
        if (flag == 0) {
            if (this.tkgsList.contains(item)) {
                return false;
            }
            this.tkgsList.add(item);
            return true;
        } else if (flag == 1) {
            for (TkgsLocatorListAdapter.TkgsLocatorItem eitem : this.tkgsList) {
                MtkLog.d(TAG, "operateTKGSLocatorinfoDev num=" + item.bnum);
                if (eitem.bnum == item.bnum) {
                    MtkLog.d(TAG, "operateTKGSLocatorinfoDev match:" + item.toString());
                    ret = true;
                }
            }
            return ret;
        } else if (flag == 2) {
            return this.tkgsList.remove(item);
        } else {
            return false;
        }
    }

    public int getTKGSTableVersion() {
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        int dvbsRet = dvbsScan.dvbsGetTableVersion(MtkTvScanDvbsBase.DvbsTableType.DVBS_TABLE_TYPE_TKGS).ordinal();
        MtkLog.d(TAG, "getTKGSTableVersion dvbsRet ==" + dvbsRet);
        if (dvbsRet == 0) {
            return dvbsScan.getTable_version;
        }
        return -1;
    }

    public boolean resetTKGSTableVersion(int version) {
        int dvbsRet = new MtkTvScanDvbsBase().dvbsSetTableVersion(MtkTvScanDvbsBase.DvbsTableType.DVBS_TABLE_TYPE_TKGS, version).ordinal();
        MtkLog.d(TAG, "resetTKGSTableVersion dvbsRet ==" + dvbsRet);
        if (dvbsRet == 0) {
            return true;
        }
        return false;
    }

    public int cleanAllHiddenLocs() {
        return new MtkTvScanDvbsBase().dvbsTKGSCleanAllHiddenLocators().ordinal();
    }

    public int disableMonitor() {
        return new MtkTvScanDvbsBase().dvbsSetDisableAutoUpdateForCrnt().ordinal();
    }

    public int enableMonitor() {
        return new MtkTvScanDvbsBase().dvbsSetEnableAutoUpdateForCrnt().ordinal();
    }

    public List<MtkTvScanDvbsBase.TKGSOneSvcList> getTKGSOneSvcList() {
        MtkTvScanDvbsBase dvbsScan = new MtkTvScanDvbsBase();
        int ret = dvbsScan.dvbsGetNfyGetInfo().ordinal();
        new ArrayList();
        if (ret != 0) {
            return null;
        }
        MtkLog.d(TAG, "getTKGSOneSvcList nfyGetInfo_mask:" + dvbsScan.nfyGetInfo_mask);
        if (dvbsScan.dvbsTKGSGetAllSvcList().ordinal() != 0) {
            return null;
        }
        int num = dvbsScan.TKGS_SvclistNum;
        this.tkgsSvcListSelPos = dvbsScan.TKGS_PrefSvcListNo;
        MtkLog.d(TAG, "getTKGSOneSvcList num:" + num);
        this.tkgsSvcList = Arrays.asList(dvbsScan.TKGS_AllSvcLists);
        return this.tkgsSvcList;
    }

    public List<String> getTKGSOneServiceStrList(List<MtkTvScanDvbsBase.TKGSOneSvcList> svclist) {
        List<String> list = new ArrayList<>();
        if (svclist != null) {
            for (int i = 0; i < svclist.size(); i++) {
                if (this.tkgsSvcListSelPos == svclist.get(i).svcListNo) {
                    this.tkgsSvcListSelPos = i;
                }
                list.add(svclist.get(i).svcListName);
            }
        }
        return list;
    }

    public int getTKGSOneServiceSelectValue() {
        return this.tkgsSvcListSelPos;
    }

    public void setTKGSOneServiceListValue(int pos) {
        int svcListNo = -1;
        if (this.tkgsSvcList != null && this.tkgsSvcList.size() > 0) {
            svcListNo = this.tkgsSvcList.get(pos).svcListNo;
        }
        if (new MtkTvScanDvbsBase().dvbsTKGSSelSvcList(svcListNo).ordinal() == 0) {
            MtkLog.d(TAG, "setTKGSOneServiceListValue set svcListNo to:" + svcListNo);
        }
    }

    public void setTKGSUserMessage(String message) {
        this.tkgsUserMessage = message;
    }

    public String getTKGSUserMessage() {
        return this.tkgsUserMessage;
    }

    public static void setMySelfNull() {
        mSelf = null;
    }
}
