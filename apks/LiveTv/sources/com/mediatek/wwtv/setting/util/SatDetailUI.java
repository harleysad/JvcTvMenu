package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SatDetailUI implements SpecialOptionDealer {
    private static int[][] LNB_CONFIG = null;
    private static final int LNB_DUAL_FREQ = 2;
    private static final int LNB_SINGLE_FREQ = 1;
    public static final String SAT_ANTENNA_TYPE = "g_bs__bs_sat_antenna_type";
    public static final String SAT_BRDCSTER = "g_bs__bs_sat_brdcster";
    public static String TAG = "SatDetailUI";
    static SatDetailUI mSelf;
    String LNBFreqTitle;
    String Tone22KHZTitle;
    Action.OptionValuseChangedCallBack channgeCallBack = new Action.OptionValuseChangedCallBack() {
        public void afterOptionValseChanged(String afterName) {
        }
    };
    String diseqcInputTitle;
    String diseqcSetTitle;
    Context mContext;
    public boolean mDvbsNeedTunerReset;
    int mRecID;

    public SatDetailUI(Context mContext2) {
        this.mContext = mContext2;
        ScanContent.getInstance(mContext2);
    }

    public static SatDetailUI getInstance(Context mContext2) {
        if (mSelf == null) {
            mSelf = new SatDetailUI(mContext2);
        }
        return mSelf;
    }

    private static void initLNBConfig() {
        LNB_CONFIG = (int[][]) Array.newInstance(int.class, new int[]{11, 3});
        LNB_CONFIG[0] = new int[]{9750, 10600, 11700};
        LNB_CONFIG[1] = new int[]{9750, 10700, 11700};
        LNB_CONFIG[2] = new int[]{9750, 10750, 11700};
        LNB_CONFIG[3] = new int[]{5150, 0, 0};
        LNB_CONFIG[4] = new int[]{5750, 0, 0};
        LNB_CONFIG[5] = new int[]{9750, 0, 0};
        LNB_CONFIG[6] = new int[]{10600, 0, 0};
        LNB_CONFIG[7] = new int[]{10750, 0, 0};
        LNB_CONFIG[8] = new int[]{11250, 0, 0};
        LNB_CONFIG[9] = new int[]{11300, 0, 0};
        LNB_CONFIG[10] = new int[]{11475, 0, 0};
    }

    /* access modifiers changed from: private */
    public SatelliteInfo updateValue(SatelliteInfo info, Action parentAction) {
        if (!(parentAction == null || parentAction.mSubChildGroup == null)) {
            int antennaType = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type");
            boolean isSingleCable = antennaType == 1;
            int status = parentAction.mSubChildGroup.get(0).mInitValue;
            int positionOrlnbPower = parentAction.mSubChildGroup.get(2).mInitValue;
            int lnbFreq = parentAction.mSubChildGroup.get(3).mInitValue;
            int tone22 = parentAction.mSubChildGroup.get(5).mInitValue;
            int toneburst = parentAction.mSubChildGroup.get(6).mInitValue;
            if (toneburst == 2) {
                toneburst = 255;
            }
            String str = TAG;
            Log.d(str, "updateValue status=" + status + ",positionOrlnbPower=" + positionOrlnbPower + ",lnbFreq=" + lnbFreq + ",tone22=" + tone22 + ",toneburst=" + toneburst);
            info.setEnabled(status == 0);
            if (isSingleCable || antennaType == 2) {
                String str2 = TAG;
                MtkLog.d(str2, "set position=" + (positionOrlnbPower + 1));
                info.setPosition(positionOrlnbPower + 1);
            } else {
                info.setLnbPower(positionOrlnbPower);
                info.setToneBurst(toneburst);
                info.setM22k(tone22);
            }
            if (lnbFreq < LNB_CONFIG.length) {
                info.setLnbLowFreq(LNB_CONFIG[lnbFreq][0]);
                info.setLnbHighFreq(LNB_CONFIG[lnbFreq][1]);
                info.setLnbSwitchFreq(LNB_CONFIG[lnbFreq][2]);
                if (lnbFreq > 2) {
                    info.setLnbType(1);
                } else {
                    info.setLnbType(2);
                }
            }
        }
        return info;
    }

    public void updateOnlySatelliteName(String newName) {
        String str = TAG;
        MtkLog.d(str, "updateOnlySatelliteName,mRecID=" + this.mRecID + ",new Name=" + newName);
        SatelliteInfo satInfo = new SatelliteInfo(ScanContent.getDVBSsatellitesBySatID(this.mContext, this.mRecID));
        satInfo.setSatName(newName);
        satInfo.setEnabled(true);
        ScanContent.saveDVBSSatelliteToSatl(satInfo);
    }

    public void updateOnlySatelliteName(String newName, int salId) {
        String str = TAG;
        MtkLog.d(str, "updateOnlySatelliteName,salId=" + salId + ",new Name=" + newName);
        SatelliteInfo satInfo = new SatelliteInfo(ScanContent.getDVBSsatellitesBySatID(this.mContext, salId));
        satInfo.setSatName(newName);
        satInfo.setEnabled(true);
        ScanContent.saveDVBSSatelliteToSatl(satInfo);
    }

    public SatelliteInfo updateDVBSSatInfoDiseqc12(SatelliteInfo satInfo, Action parentAction) {
        Action action = parentAction;
        if (satInfo == null) {
            return null;
        }
        int typeInt = action.mSubChildGroup.get(0).mInitValue;
        boolean z = true;
        int index = 0 + 1 + 1;
        int LNBPowerInt = action.mSubChildGroup.get(index).mInitValue;
        int antennaType = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type");
        if (antennaType == 1) {
        }
        boolean isUnCable = antennaType == 0;
        int index2 = index + 1;
        int LNBFreqInt = action.mSubChildGroup.get(index2).mInitValue;
        int Tone22KHZInt = 0;
        if (isUnCable) {
            Tone22KHZInt = action.mSubChildGroup.get(index2 + 1 + 1).mInitValue;
        }
        SatelliteInfo info = satInfo;
        if (typeInt != 0) {
            z = false;
        }
        info.setEnabled(z);
        if (!isUnCable) {
            info.setPosition(LNBPowerInt);
        } else {
            info.setLnbPower(LNBPowerInt);
            info.setM22k(Tone22KHZInt);
        }
        if (LNBFreqInt < LNB_CONFIG.length) {
            info.setLnbLowFreq(LNB_CONFIG[LNBFreqInt][0]);
            info.setLnbHighFreq(LNB_CONFIG[LNBFreqInt][1]);
            info.setLnbSwitchFreq(LNB_CONFIG[LNBFreqInt][2]);
            if (LNBFreqInt > 2) {
                info.setLnbType(1);
            } else {
                info.setLnbType(2);
            }
        }
        return info;
    }

    public SatelliteInfo updateValueForDiseqc12Set(SatelliteInfo satInfo, Action parentAction) {
        int diseqcInputIntType;
        int diseqc10Port;
        int diseqc11Port;
        int diseqc11InputIntType;
        int motorPortIntType;
        int motorPort;
        SatelliteInfo satelliteInfo = satInfo;
        Action action = parentAction;
        if (satelliteInfo == null) {
            return null;
        }
        int diseqc10Port2 = action.mSubChildGroup.get(0).mInitValue;
        if (diseqc10Port2 == 0) {
            diseqcInputIntType = 0;
            diseqc10Port = 255;
        } else {
            diseqcInputIntType = 2;
            diseqc10Port = diseqc10Port2 - 1;
        }
        int diseqc11Port2 = action.mSubChildGroup.get(1).mInitValue;
        if (diseqc11Port2 == 0) {
            diseqc11InputIntType = 0;
            diseqc11Port = 255;
        } else {
            diseqc11Port = diseqc11Port2 - 1;
            diseqc11InputIntType = 4;
        }
        if (action.mSubChildGroup.get(2).mInitValue == 0) {
            motorPortIntType = 0;
            motorPort = 255;
        } else {
            motorPortIntType = 5;
            motorPort = 0;
        }
        MtkLog.d(TAG, "diseqc10Port:" + diseqc10Port + "diseqc11Port:" + diseqc11Port + "motor:" + motorPort);
        satelliteInfo.setDiseqcType(diseqcInputIntType);
        satelliteInfo.setPort(diseqc10Port);
        satelliteInfo.setDiseqcTypeEx(diseqc11InputIntType);
        satelliteInfo.setPortEx(diseqc11Port);
        satelliteInfo.setMotorType(motorPortIntType);
        MtkLog.d(TAG, "updateDVBSSatInfoDiseqcSet12>>" + satInfo.toString());
        return satelliteInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:67:0x02a0, code lost:
        if (LNB_CONFIG.length < 11) goto L_0x02a5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x04ac  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x04b4  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x04df  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x051d  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0572  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x058d  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x05b5  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x05e1  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x05e9  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x03b4  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x03fd  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x044b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> initSatelliteInfoViews(com.mediatek.wwtv.setting.widget.detailui.Action r75, int r76) {
        /*
            r74 = this;
            r11 = r74
            r12 = r75
            r13 = r76
            r11.mRecID = r13
            r0 = 24
            boolean r14 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r0)
            if (r14 == 0) goto L_0x0017
            android.content.Context r0 = r11.mContext
            java.util.List r0 = r11.initSatelliteInfoViewsDiseqc12(r12, r13, r0)
            return r0
        L_0x0017:
            r15 = 1
            r10 = 0
            if (r12 != 0) goto L_0x001d
            r0 = r10
            goto L_0x001e
        L_0x001d:
            r0 = r15
        L_0x001e:
            r16 = r0
            if (r16 == 0) goto L_0x0030
            if (r12 == 0) goto L_0x0030
            java.lang.String r0 = r12.mItemID
            java.lang.String r1 = "DVBS_SAT_MANUAL_TURNING"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L_0x0030
            r0 = 1
            goto L_0x0031
        L_0x0030:
            r0 = r10
        L_0x0031:
            r17 = r0
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r1 = "g_bs__bs_sat_antenna_type"
            int r9 = r0.getConfigValue(r1)
            if (r9 != 0) goto L_0x0041
            r0 = r15
            goto L_0x0042
        L_0x0041:
            r0 = r10
        L_0x0042:
            r18 = r0
            if (r9 != r15) goto L_0x0048
            r0 = r15
            goto L_0x0049
        L_0x0048:
            r0 = r10
        L_0x0049:
            r19 = r0
            r8 = 2
            if (r9 != r8) goto L_0x0050
            r0 = r15
            goto L_0x0051
        L_0x0050:
            r0 = r10
        L_0x0051:
            r20 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            android.content.Context r0 = r11.mContext
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r6 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSsatellitesBySatID(r0, r13)
            if (r6 != 0) goto L_0x0062
            return r7
        L_0x0062:
            int r0 = r6.getSatlRecId()
            java.lang.String r5 = java.lang.String.valueOf(r0)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903064(0x7f030018, float:1.7412935E38)
            java.lang.String[] r30 = r0.getStringArray(r1)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903067(0x7f03001b, float:1.7412942E38)
            java.lang.String[] r0 = r0.getStringArray(r1)
            if (r9 != r8) goto L_0x0093
            android.content.Context r1 = r11.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2130903058(0x7f030012, float:1.7412923E38)
            java.lang.String[] r0 = r1.getStringArray(r2)
        L_0x0093:
            r4 = r0
            java.lang.String[] r0 = new java.lang.String[r8]
            android.content.Context r1 = r11.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131689552(0x7f0f0050, float:1.9008123E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r10] = r1
            android.content.Context r1 = r11.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131689553(0x7f0f0051, float:1.9008125E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r15] = r1
            r3 = r0
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903063(0x7f030017, float:1.7412933E38)
            java.lang.String[] r31 = r0.getStringArray(r1)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903054(0x7f03000e, float:1.7412915E38)
            java.lang.String[] r2 = r0.getStringArray(r1)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903068(0x7f03001c, float:1.7412944E38)
            java.lang.String[] r1 = r0.getStringArray(r1)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r8 = 2130903069(0x7f03001d, float:1.7412946E38)
            java.lang.String[] r8 = r0.getStringArray(r8)
            android.content.Context r0 = r11.mContext
            java.lang.Integer r10 = java.lang.Integer.valueOf(r5)
            int r10 = r10.intValue()
            java.lang.String r10 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSTransponderStrTitle(r0, r10)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r15 = 2131689858(0x7f0f0182, float:1.9008743E38)
            java.lang.String r15 = r0.getString(r15)
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r34 = r1
            r1 = 2131689853(0x7f0f017d, float:1.9008733E38)
            java.lang.String r35 = r0.getString(r1)
            boolean r0 = r6.getEnable()
            r1 = 1
            r0 = r0 ^ r1
            r1 = r0
            if (r16 == 0) goto L_0x0123
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12.mSubChildGroup = r0
        L_0x0123:
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r22 = r15
            r23 = r15
            r26 = r1
            r27 = r30
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r7.add(r0)
            r36 = r1
            java.lang.String r1 = TAG
            r37 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r38 = r5
            java.lang.String r5 = "initSatelliteInfoViews parentid ="
            r2.append(r5)
            java.lang.String r5 = r12.mItemID
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            boolean r1 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.isPreferedSat()
            if (r1 == 0) goto L_0x0180
            if (r12 == 0) goto L_0x017c
            java.lang.String r1 = r12.mItemID
            java.lang.String r2 = "Satellite Re-scan"
            boolean r1 = r1.equalsIgnoreCase(r2)
            if (r1 != 0) goto L_0x0177
            java.lang.String r1 = r12.mItemID
            java.lang.String r2 = "DVBS_SAT_OP"
            boolean r1 = r1.equalsIgnoreCase(r2)
            if (r1 == 0) goto L_0x017c
        L_0x0177:
            r1 = 0
            r0.setEnabled(r1)
            goto L_0x0180
        L_0x017c:
            r1 = 1
            r0.setEnabled(r1)
        L_0x0180:
            if (r16 == 0) goto L_0x018c
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r1 = r12.mSubChildGroup
            r0.setmParentGroup(r1)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r1 = r12.mSubChildGroup
            r1.add(r0)
        L_0x018c:
            com.mediatek.wwtv.setting.widget.detailui.Action r1 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r26 = 0
            r2 = 1
            java.lang.String[] r5 = new java.lang.String[r2]
            java.lang.String r2 = r6.getSatName()
            r21 = 0
            r5[r21] = r2
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            r21 = r1
            r22 = r35
            r23 = r35
            r27 = r5
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r5 = r1
            r1 = 0
            r5.setEnabled(r1)
            if (r16 == 0) goto L_0x01c6
            int r1 = r12.satID
            r5.satID = r1
            r7.add(r5)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r1 = r12.mSubChildGroup
            r5.setmParentGroup(r1)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r1 = r12.mSubChildGroup
            r1.add(r5)
        L_0x01c6:
            android.content.Context r1 = r11.mContext
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131689866(0x7f0f018a, float:1.900876E38)
            java.lang.String r39 = r1.getString(r2)
            int r1 = r6.getPosition()
            r2 = 1
            int r1 = r1 - r2
            r2 = 0
            int r1 = java.lang.Math.max(r1, r2)
            int r2 = r4.length
            r21 = 1
            int r2 = r2 + -1
            int r2 = java.lang.Math.min(r1, r2)
            java.lang.String r1 = TAG
            r41 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r42 = r5
            java.lang.String r5 = "defaultPosition:"
            r0.append(r5)
            r0.append(r2)
            java.lang.String r5 = ",info.getPosition():"
            r0.append(r5)
            int r5 = r6.getPosition()
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r0)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r22 = "DVBS_DETAIL_POSITION"
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r23 = r39
            r26 = r2
            r27 = r4
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r5 = r0
            if (r19 != 0) goto L_0x022a
            r0 = 2
            if (r9 != r0) goto L_0x0245
        L_0x022a:
            r7.add(r5)
            if (r16 == 0) goto L_0x023d
            int r0 = r12.satID
            r5.satID = r0
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r5.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r5)
        L_0x023d:
            com.mediatek.wwtv.setting.util.SatDetailUI$2 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$2
            r0.<init>(r13, r12)
            r5.setOptionValueChangedCallBack(r0)
        L_0x0245:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689789(0x7f0f013d, float:1.9008603E38)
            java.lang.String r40 = r0.getString(r1)
            int r0 = r6.getLnbPower()
            int r1 = r3.length
            r21 = 1
            int r1 = r1 + -1
            int r0 = java.lang.Math.min(r0, r1)
            r1 = 0
            int r43 = java.lang.Math.max(r0, r1)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r22 = "DVBS_DETAIL_LNB_POWER"
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r23 = r40
            r26 = r43
            r27 = r3
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r1 = r0
            if (r18 == 0) goto L_0x0295
            r7.add(r1)
            if (r16 == 0) goto L_0x028d
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r1.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r1)
        L_0x028d:
            com.mediatek.wwtv.setting.util.SatDetailUI$3 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$3
            r0.<init>(r13, r12, r1)
            r1.setOptionValueChangedCallBack(r0)
        L_0x0295:
            int[][] r0 = LNB_CONFIG
            if (r0 == 0) goto L_0x02a3
            int[][] r0 = LNB_CONFIG
            int r0 = r0.length
            r44 = r1
            r1 = 11
            if (r0 >= r1) goto L_0x02af
            goto L_0x02a5
        L_0x02a3:
            r44 = r1
        L_0x02a5:
            java.lang.String r0 = TAG
            java.lang.String r1 = "initSatelliteInfoViews>>initLNBConfig>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            initLNBConfig()
        L_0x02af:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689788(0x7f0f013c, float:1.9008601E38)
            java.lang.String r0 = r0.getString(r1)
            r11.LNBFreqTitle = r0
            int[][] r0 = LNB_CONFIG
            int r45 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDefaultLnbFreqIndex(r6, r0)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r1 = r11.LNBFreqTitle
            r46 = r2
            java.lang.String r2 = r11.LNBFreqTitle
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r22 = r1
            r23 = r2
            r26 = r45
            r27 = r31
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r2 = r0
            r7.add(r2)
            if (r16 == 0) goto L_0x02f1
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r2.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r2)
        L_0x02f1:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689786(0x7f0f013a, float:1.9008597E38)
            java.lang.String r47 = r0.getString(r1)
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            if (r16 == 0) goto L_0x0304
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
        L_0x0304:
            r48 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r22 = "DVBS_DETAIL_DISEQC12_SET"
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r26 = 0
            java.lang.String r1 = ""
            java.lang.String[] r27 = new java.lang.String[]{r1}
            r28 = 1
            r21 = r0
            r23 = r47
            r29 = r48
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r1 = r0
            if (r9 != 0) goto L_0x0374
            r7.add(r1)
            if (r16 == 0) goto L_0x0374
            java.lang.String r0 = r12.mLocationId
            r1.mLocationId = r0
            int r0 = r12.satID
            r1.satID = r0
            r1.mParent = r12
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r1.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r1)
            if (r17 != 0) goto L_0x0374
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.mSubChildGroup = r0
            android.content.Context r0 = r11.mContext
            r21 = r0
            r49 = r14
            r14 = r41
            r0 = r11
            r53 = r1
            r50 = r15
            r15 = r34
            r51 = r36
            r52 = r44
            r1 = r21
            r55 = r2
            r54 = r14
            r14 = r37
            r34 = r46
            r2 = r13
            r36 = r3
            r3 = r53
            r37 = r4
            r4 = r12
            r56 = r5
            r41 = r42
            r5 = r6
            r0.initDiseqcSetPageSubItems(r1, r2, r3, r4, r5)
            goto L_0x0390
        L_0x0374:
            r53 = r1
            r55 = r2
            r56 = r5
            r49 = r14
            r50 = r15
            r15 = r34
            r51 = r36
            r14 = r37
            r54 = r41
            r41 = r42
            r52 = r44
            r34 = r46
            r36 = r3
            r37 = r4
        L_0x0390:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689778(0x7f0f0132, float:1.900858E38)
            java.lang.String r0 = r0.getString(r1)
            r11.diseqcInputTitle = r0
            int r0 = r6.getDiseqcType()
            int r1 = r14.length
            r2 = 1
            int r1 = r1 - r2
            int r0 = java.lang.Math.min(r0, r1)
            r1 = 0
            int r5 = java.lang.Math.max(r0, r1)
            int r0 = r14.length
            int r0 = r0 - r2
            r1 = 2
            if (r5 != r1) goto L_0x03be
            int r0 = r6.getPort()
            int r1 = r14.length
            int r1 = r1 - r2
            int r0 = java.lang.Math.min(r0, r1)
        L_0x03be:
            r42 = r0
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689872(0x7f0f0190, float:1.9008772E38)
            java.lang.String r0 = r0.getString(r1)
            r11.Tone22KHZTitle = r0
            int r0 = r6.getM22k()
            int r1 = r15.length
            r2 = 1
            int r1 = r1 - r2
            int r0 = java.lang.Math.min(r0, r1)
            r1 = 0
            int r44 = java.lang.Math.max(r0, r1)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r1 = r11.Tone22KHZTitle
            java.lang.String r2 = r11.Tone22KHZTitle
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r22 = r1
            r23 = r2
            r26 = r44
            r27 = r15
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r4 = r0
            if (r18 == 0) goto L_0x0414
            r7.add(r4)
            com.mediatek.wwtv.setting.util.SatDetailUI$4 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$4
            r0.<init>(r4, r13, r12)
            r4.setOptionValueChangedCallBack(r0)
            if (r16 == 0) goto L_0x0414
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r4.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r4)
        L_0x0414:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689876(0x7f0f0194, float:1.900878E38)
            java.lang.String r46 = r0.getString(r1)
            int r0 = r6.getToneBurst()
            int r1 = r8.length
            r2 = 1
            int r1 = r1 - r2
            int r0 = java.lang.Math.min(r0, r1)
            r3 = 0
            int r33 = java.lang.Math.max(r0, r3)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 10004(0x2714, float:1.4019E-41)
            r28 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r29 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r21 = r0
            r22 = r46
            r23 = r46
            r26 = r33
            r27 = r8
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)
            r2 = r0
            if (r18 == 0) goto L_0x0462
            r7.add(r2)
            com.mediatek.wwtv.setting.util.SatDetailUI$5 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$5
            r0.<init>(r2, r13, r12)
            r2.setOptionValueChangedCallBack(r0)
            if (r16 == 0) goto L_0x0462
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r2.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r2)
        L_0x0462:
            if (r17 != 0) goto L_0x0483
            if (r18 == 0) goto L_0x0483
            com.mediatek.wwtv.setting.util.SatDetailUI$6 r1 = new com.mediatek.wwtv.setting.util.SatDetailUI$6
            r0 = r1
            r57 = r6
            r6 = r1
            r1 = r11
            r58 = r2
            r2 = r55
            r21 = r3
            r3 = r4
            r59 = r4
            r4 = r13
            r22 = r5
            r5 = r12
            r0.<init>(r2, r3, r4, r5)
            r5 = r55
            r5.setOptionValueChangedCallBack(r6)
            goto L_0x049b
        L_0x0483:
            r58 = r2
            r21 = r3
            r59 = r4
            r22 = r5
            r57 = r6
            r5 = r55
            if (r19 != 0) goto L_0x0493
            if (r20 == 0) goto L_0x049b
        L_0x0493:
            com.mediatek.wwtv.setting.util.SatDetailUI$7 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$7
            r0.<init>(r13, r12)
            r5.setOptionValueChangedCallBack(r0)
        L_0x049b:
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131689888(0x7f0f01a0, float:1.9008804E38)
            java.lang.String r23 = r0.getString(r1)
            java.lang.String r0 = "DVBS_SAT_COMMON_TP"
            if (r17 == 0) goto L_0x04ae
            java.lang.String r0 = "DVBS_SAT_MANUAL_TURNING_TP"
        L_0x04ae:
            r24 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            if (r16 == 0) goto L_0x04b6
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
        L_0x04b6:
            r25 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r63 = 10004(0x2714, float:1.4019E-41)
            r64 = 10004(0x2714, float:1.4019E-41)
            r65 = 10004(0x2714, float:1.4019E-41)
            r66 = 0
            r67 = 1
            r60 = r0
            r61 = r24
            r62 = r23
            r68 = r25
            r60.<init>(r61, r62, r63, r64, r65, r66, r67, r68)
            r6 = r0
            r6.setDescription((java.lang.String) r10)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6.mSubChildGroup = r0
            r7.add(r6)
            if (r16 == 0) goto L_0x04ef
            java.lang.String r0 = r12.mLocationId
            r6.mLocationId = r0
            r6.mParent = r12
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r6.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r6)
        L_0x04ef:
            if (r16 == 0) goto L_0x04f6
            android.content.Context r0 = r11.mContext
            initTransponderItems(r0, r13, r6, r12)
        L_0x04f6:
            android.content.Context r0 = r11.mContext
            java.util.List r0 = initSignalLevelAndQualityItems(r0, r13)
            r7.addAll(r0)
            int r0 = r7.size()
            r26 = 2
            int r0 = r0 + -2
            java.lang.Object r0 = r7.get(r0)
            r4 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r4 = (com.mediatek.wwtv.setting.widget.detailui.Action) r4
            int r0 = r7.size()
            r1 = 1
            int r0 = r0 - r1
            java.lang.Object r0 = r7.get(r0)
            r3 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r3 = (com.mediatek.wwtv.setting.widget.detailui.Action) r3
            if (r16 == 0) goto L_0x053d
            java.lang.String r0 = r12.mLocationId
            r4.mLocationId = r0
            r4.mParent = r12
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r4.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r4)
            java.lang.String r0 = r12.mLocationId
            r3.mLocationId = r0
            r3.mParent = r12
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r3.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r12.mSubChildGroup
            r0.add(r3)
        L_0x053d:
            if (r17 != 0) goto L_0x0572
            com.mediatek.wwtv.setting.util.SatDetailUI$8 r2 = new com.mediatek.wwtv.setting.util.SatDetailUI$8
            r0 = r2
            r1 = r11
            r11 = r2
            r2 = r12
            r27 = r3
            r3 = r56
            r28 = r4
            r4 = r52
            r69 = r5
            r70 = r6
            r29 = r57
            r6 = r58
            r71 = r14
            r14 = r7
            r7 = r53
            r72 = r15
            r15 = r26
            r26 = r8
            r8 = r70
            r32 = r9
            r9 = r59
            r21 = r10
            r10 = r13
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r0 = r54
            r0.setOptionValueChangedCallBack(r11)
            goto L_0x058b
        L_0x0572:
            r27 = r3
            r28 = r4
            r69 = r5
            r70 = r6
            r32 = r9
            r21 = r10
            r71 = r14
            r72 = r15
            r15 = r26
            r0 = r54
            r29 = r57
            r14 = r7
            r26 = r8
        L_0x058b:
            if (r17 != 0) goto L_0x05b5
            r1 = r51
            r2 = 1
            if (r1 != r2) goto L_0x0598
            r4 = r59
            r3 = r69
            r2 = 0
            goto L_0x05bc
        L_0x0598:
            r3 = r69
            int r4 = r3.mInitValue
            if (r4 <= r15) goto L_0x05ae
            r4 = r59
            r4.setEnabled(r2)
        L_0x05a3:
            r6 = r52
            r7 = r53
            r5 = r56
            r8 = r58
            r9 = r70
            goto L_0x05e7
        L_0x05ae:
            r4 = r59
            r2 = 0
            r4.setEnabled(r2)
            goto L_0x05a3
        L_0x05b5:
            r1 = r51
            r4 = r59
            r3 = r69
            r2 = 0
        L_0x05bc:
            r0.setEnabled(r2)
            r5 = r56
            r5.setEnabled(r2)
            r6 = r52
            r6.setEnabled(r2)
            r3.setEnabled(r2)
            r4.setEnabled(r2)
            r7 = r53
            r7.setEnabled(r2)
            r8 = r58
            r8.setEnabled(r2)
            r9 = r70
            r10 = 1
            r9.setEnabled(r10)
            if (r1 != r10) goto L_0x05e7
            r0.setEnabled(r10)
            r9.setEnabled(r2)
        L_0x05e7:
            if (r16 == 0) goto L_0x05fc
            java.util.Iterator r2 = r14.iterator()
        L_0x05ed:
            boolean r10 = r2.hasNext()
            if (r10 == 0) goto L_0x05fc
            java.lang.Object r10 = r2.next()
            com.mediatek.wwtv.setting.widget.detailui.Action r10 = (com.mediatek.wwtv.setting.widget.detailui.Action) r10
            r10.mParent = r12
            goto L_0x05ed
        L_0x05fc:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.SatDetailUI.initSatelliteInfoViews(com.mediatek.wwtv.setting.widget.detailui.Action, int):java.util.List");
    }

    public static List<Action> initTransponderItems(Context context, int satID, Action transponderItem, Action parentItem) {
        final Context context2 = context;
        final int i = satID;
        Action action = transponderItem;
        Action action2 = parentItem;
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = ScanContent.getDVBSTransponder(satID);
        List<Action> items = new ArrayList<>();
        Action frequency = new Action(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_TP_ITEMS, context2.getString(R.string.dvbs_tp_fre), 0, 99999, tpInfo.i4Frequency, new String[0], 1, Action.DataType.NUMVIEW);
        frequency.setInputLength(5);
        frequency.satID = i;
        if (action == null || action2 == null) {
            items.add(frequency);
        } else {
            frequency.setmParentGroup(action.mSubChildGroup);
            frequency.mSubChildGroup = action2.mSubChildGroup;
            frequency.mLocationId = action2.mLocationId;
            frequency.mParent = action;
            action.mSubChildGroup.add(frequency);
        }
        Action action3 = new Action(MenuConfigManager.DVBS_SAT_DEDATIL_INFO_TP_ITEMS, context2.getString(R.string.dvbs_tp_sys_rate), 1000, 99999, tpInfo.i4Symbolrate, new String[0], 1, Action.DataType.NUMVIEW);
        action3.setInputLength(5);
        action3.satID = i;
        if (action == null || action2 == null) {
            items.add(action3);
        } else {
            action3.setmParentGroup(action.mSubChildGroup);
            action3.mSubChildGroup = action2.mSubChildGroup;
            action3.mLocationId = action2.mLocationId;
            action3.mParent = action;
            action.mSubChildGroup.add(action3);
        }
        MtkTvScanDvbsBase.TunerPolarizationType position = tpInfo.ePol;
        MtkLog.d(TAG, "tpInfo.ePol:" + position.getValue());
        int positionInt = 0;
        if (position.getValue() > 0) {
            positionInt = position.getValue() - 1;
        }
        Action polarization = new Action(SettingsUtil.SPECIAL_SAT_DETAIL_INFO_ITEM_POL, context2.getString(R.string.dvbs_tp_pol), 10004, 10004, positionInt, context.getResources().getStringArray(R.array.dvbs_tp_pol_arrays), 1, Action.DataType.OPTIONVIEW);
        polarization.satID = i;
        polarization.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                MenuDataHelper.getInstance(context2).saveDVBSSatTPInfo(context2, i, afterName);
            }
        });
        if (action == null || action2 == null) {
            items.add(polarization);
        } else {
            polarization.setmParentGroup(action.mSubChildGroup);
            polarization.mSubChildGroup = action2.mSubChildGroup;
            polarization.mLocationId = action2.mLocationId;
            polarization.mParent = action;
            action.mSubChildGroup.add(polarization);
        }
        if (action == null || action2 == null) {
            return items;
        }
        return action.mSubChildGroup;
    }

    private static List<Action> initSignalLevelAndQualityItems(Context mContext2, int satID) {
        List<Action> items = new ArrayList<>();
        String signalQualityTitle = mContext2.getResources().getString(R.string.menu_tv_single_signal_quality);
        ScanContent.setDVBSFreqToGetSignalQuality(satID);
        MtkLog.d("xinsheng", "quality>>>" + 0 + ">>" + 0);
        String str = signalQualityTitle;
        Action action = new Action(MenuConfigManager.DVBS_SIGNAL_QULITY, str, 0, 100, 0, new String[]{String.valueOf(0)}, 1, Action.DataType.PROGRESSBAR);
        action.setSupportModify(false);
        action.signalType = 1;
        items.add(action);
        String signalLevelTitle = mContext2.getResources().getString(R.string.menu_tv_single_signal_level);
        String str2 = signalLevelTitle;
        Action signalLevelItem = new Action(MenuConfigManager.DVBS_SIGNAL_LEVEL, str2, 0, 100, 0, new String[]{String.valueOf(0)}, 1, Action.DataType.PROGRESSBAR);
        signalLevelItem.setSupportModify(false);
        signalLevelItem.signalType = 2;
        items.add(signalLevelItem);
        return items;
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0476  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> initSatelliteInfoViewsDiseqc12(com.mediatek.wwtv.setting.widget.detailui.Action r72, int r73, android.content.Context r74) {
        /*
            r71 = this;
            r12 = r71
            r13 = r72
            r14 = r73
            r15 = r74
            r11 = 1
            if (r13 != 0) goto L_0x000d
            r0 = 0
            goto L_0x000e
        L_0x000d:
            r0 = r11
        L_0x000e:
            r16 = r0
            if (r16 == 0) goto L_0x0020
            java.lang.String r0 = r72.getTitle()
            java.lang.String r1 = "DVBS_SAT_MANUAL_TURNING"
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            r17 = r0
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r1 = "g_bs__bs_sat_antenna_type"
            int r9 = r0.getConfigValue(r1)
            if (r9 != r11) goto L_0x0031
            r0 = r11
            goto L_0x0032
        L_0x0031:
            r0 = 0
        L_0x0032:
            r18 = r0
            if (r9 != 0) goto L_0x0038
            r0 = r11
            goto L_0x0039
        L_0x0038:
            r0 = 0
        L_0x0039:
            r19 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r7 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSsatellitesBySatID(r15, r14)
            if (r7 != 0) goto L_0x0048
            return r8
        L_0x0048:
            int r0 = r7.getSatlRecId()
            java.lang.String r6 = java.lang.String.valueOf(r0)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903064(0x7f030018, float:1.7412935E38)
            java.lang.String[] r29 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903060(0x7f030014, float:1.7412927E38)
            java.lang.String[] r30 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903062(0x7f030016, float:1.7412931E38)
            java.lang.String[] r5 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903063(0x7f030017, float:1.7412933E38)
            java.lang.String[] r31 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903054(0x7f03000e, float:1.7412915E38)
            java.lang.String[] r32 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903068(0x7f03001c, float:1.7412944E38)
            java.lang.String[] r4 = r0.getStringArray(r1)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2130903069(0x7f03001d, float:1.7412946E38)
            java.lang.String[] r33 = r0.getStringArray(r1)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r6)
            int r0 = r0.intValue()
            java.lang.String r3 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSTransponderStrTitle(r15, r0)
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2131689858(0x7f0f0182, float:1.9008743E38)
            java.lang.String r34 = r0.getString(r1)
            r0 = 0
            int r1 = r7.getMask()
            if (r1 != r11) goto L_0x00be
            r0 = 0
        L_0x00bc:
            r2 = r0
            goto L_0x00c0
        L_0x00be:
            r0 = 1
            goto L_0x00bc
        L_0x00c0:
            if (r16 == 0) goto L_0x00c9
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13.mSubChildGroup = r0
        L_0x00c9:
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r20 = r0
            r21 = r34
            r22 = r34
            r25 = r2
            r26 = r29
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r1 = r0
            r8.add(r1)
            if (r16 == 0) goto L_0x00f0
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r1.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r1)
        L_0x00f0:
            com.mediatek.wwtv.setting.util.SatDetailUI$10 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$10
            r0.<init>(r1, r7)
            r1.setOptionValueChangedCallBack(r0)
            android.content.res.Resources r0 = r74.getResources()
            r10 = 2131689853(0x7f0f017d, float:1.9008733E38)
            java.lang.String r36 = r0.getString(r10)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 0
            java.lang.String[] r10 = new java.lang.String[r11]
            java.lang.String r20 = r7.getSatName()
            r21 = 0
            r10[r21] = r20
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            r20 = r0
            r21 = r36
            r22 = r36
            r26 = r10
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r10 = r0
            r0 = 0
            r10.setEnabled(r0)
            if (r16 == 0) goto L_0x013c
            int r0 = r13.satID
            r10.satID = r0
            r8.add(r10)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r10.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r10)
        L_0x013c:
            android.content.res.Resources r0 = r74.getResources()
            r11 = 2131689866(0x7f0f018a, float:1.900876E38)
            java.lang.String r38 = r0.getString(r11)
            int r0 = r7.getPosition()
            r11 = 0
            int r0 = java.lang.Math.max(r0, r11)
            r11 = 2
            int r39 = java.lang.Math.min(r0, r11)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r21 = "DVBS_DETAIL_POSITION"
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r20 = r0
            r22 = r38
            r25 = r39
            r26 = r30
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            if (r18 == 0) goto L_0x0189
            r8.add(r0)
            if (r16 == 0) goto L_0x0181
            int r11 = r13.satID
            r0.satID = r11
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r11 = r13.mSubChildGroup
            r0.setmParentGroup(r11)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r11 = r13.mSubChildGroup
            r11.add(r0)
        L_0x0181:
            com.mediatek.wwtv.setting.util.SatDetailUI$11 r11 = new com.mediatek.wwtv.setting.util.SatDetailUI$11
            r11.<init>(r15, r14, r13)
            r0.setOptionValueChangedCallBack(r11)
        L_0x0189:
            android.content.res.Resources r11 = r74.getResources()
            r41 = r0
            r0 = 2131689789(0x7f0f013d, float:1.9008603E38)
            java.lang.String r42 = r11.getString(r0)
            int r0 = r7.getLnbPower()
            int r11 = r5.length
            r20 = 1
            int r11 = r11 + -1
            int r0 = java.lang.Math.min(r0, r11)
            r11 = 0
            int r43 = java.lang.Math.max(r0, r11)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r21 = "DVBS_DETAIL_LNB_POWER"
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r20 = r0
            r22 = r42
            r25 = r43
            r26 = r5
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r11 = r0
            if (r19 == 0) goto L_0x01ef
            r8.add(r11)
            if (r16 == 0) goto L_0x01d1
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r11.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r11)
        L_0x01d1:
            com.mediatek.wwtv.setting.util.SatDetailUI$12 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$12
            r45 = r0
            r44 = r41
            r46 = r1
            r1 = r12
            r47 = r2
            r2 = r15
            r48 = r10
            r10 = r3
            r3 = r14
            r49 = r9
            r9 = r4
            r4 = r13
            r41 = r5
            r5 = r11
            r0.<init>(r2, r3, r4, r5)
            r11.setOptionValueChangedCallBack(r0)
            goto L_0x01fd
        L_0x01ef:
            r46 = r1
            r47 = r2
            r49 = r9
            r48 = r10
            r44 = r41
            r10 = r3
            r9 = r4
            r41 = r5
        L_0x01fd:
            int[][] r0 = LNB_CONFIG
            if (r0 == 0) goto L_0x0208
            int[][] r0 = LNB_CONFIG
            int r0 = r0.length
            r1 = 11
            if (r0 >= r1) goto L_0x0212
        L_0x0208:
            java.lang.String r0 = TAG
            java.lang.String r1 = "initSatelliteInfoViewsDiseqc12>>initLNBConfig>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            initLNBConfig()
        L_0x0212:
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2131689788(0x7f0f013c, float:1.9008601E38)
            java.lang.String r45 = r0.getString(r1)
            int[][] r0 = LNB_CONFIG
            int r50 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDefaultLnbFreqIndex(r7, r0)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r20 = r0
            r21 = r45
            r22 = r45
            r25 = r50
            r26 = r31
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r5 = r0
            r8.add(r5)
            if (r16 == 0) goto L_0x024a
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r5.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r5)
        L_0x024a:
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2131689786(0x7f0f013a, float:1.9008597E38)
            java.lang.String r51 = r0.getString(r1)
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            if (r16 == 0) goto L_0x025b
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
        L_0x025b:
            r52 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            java.lang.String r21 = "DVBS_DETAIL_DISEQC12_SET"
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r25 = 0
            java.lang.String r1 = ""
            java.lang.String[] r26 = new java.lang.String[]{r1}
            r27 = 1
            r20 = r0
            r22 = r51
            r28 = r52
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r4 = r0
            if (r19 == 0) goto L_0x02a0
            r8.add(r4)
            if (r16 == 0) goto L_0x02a0
            java.lang.String r0 = r13.mLocationId
            r4.mLocationId = r0
            int r0 = r13.satID
            r4.satID = r0
            r4.mParent = r13
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r4.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r4)
            if (r17 != 0) goto L_0x02a0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4.mSubChildGroup = r0
            r12.initDiseqcSetPageItems(r15, r14, r4, r13)
        L_0x02a0:
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2131689872(0x7f0f0190, float:1.9008772E38)
            java.lang.String r53 = r0.getString(r1)
            int r0 = r7.getM22k()
            int r1 = r9.length
            r2 = 1
            int r1 = r1 - r2
            int r0 = java.lang.Math.min(r0, r1)
            r3 = 0
            int r35 = java.lang.Math.max(r0, r3)
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r23 = 10004(0x2714, float:1.4019E-41)
            r24 = 10004(0x2714, float:1.4019E-41)
            r27 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r28 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.OPTIONVIEW
            r20 = r0
            r21 = r53
            r22 = r53
            r25 = r35
            r26 = r9
            r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28)
            r2 = r0
            if (r19 == 0) goto L_0x02ec
            r8.add(r2)
            com.mediatek.wwtv.setting.util.SatDetailUI$13 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$13
            r0.<init>(r15, r14, r13)
            r2.setOptionValueChangedCallBack(r0)
            if (r16 == 0) goto L_0x02ec
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r2.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r2)
        L_0x02ec:
            if (r17 != 0) goto L_0x030f
            if (r19 == 0) goto L_0x030f
            com.mediatek.wwtv.setting.util.SatDetailUI$14 r1 = new com.mediatek.wwtv.setting.util.SatDetailUI$14
            r0 = r1
            r54 = r7
            r7 = r1
            r1 = r12
            r55 = r2
            r2 = r5
            r20 = r3
            r3 = r55
            r56 = r4
            r4 = r15
            r57 = r9
            r9 = r5
            r5 = r14
            r21 = r6
            r6 = r13
            r0.<init>(r2, r3, r4, r5, r6)
            r9.setOptionValueChangedCallBack(r7)
            goto L_0x0326
        L_0x030f:
            r55 = r2
            r20 = r3
            r56 = r4
            r21 = r6
            r54 = r7
            r57 = r9
            r9 = r5
            if (r18 == 0) goto L_0x0326
            com.mediatek.wwtv.setting.util.SatDetailUI$15 r0 = new com.mediatek.wwtv.setting.util.SatDetailUI$15
            r0.<init>(r15, r14, r13)
            r9.setOptionValueChangedCallBack(r0)
        L_0x0326:
            android.content.res.Resources r0 = r74.getResources()
            r1 = 2131689888(0x7f0f01a0, float:1.9008804E38)
            java.lang.String r22 = r0.getString(r1)
            java.lang.String r0 = "DVBS_SAT_COMMON_TP"
            if (r17 == 0) goto L_0x0337
            java.lang.String r0 = "DVBS_SAT_MANUAL_TURNING_TP"
        L_0x0337:
            r23 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.TEXTCOMMVIEW
            if (r16 == 0) goto L_0x033f
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r0 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.HAVESUBCHILD
        L_0x033f:
            r24 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r0 = new com.mediatek.wwtv.setting.widget.detailui.Action
            r61 = 10004(0x2714, float:1.4019E-41)
            r62 = 10004(0x2714, float:1.4019E-41)
            r63 = 10004(0x2714, float:1.4019E-41)
            r64 = 0
            r65 = 1
            r58 = r0
            r59 = r23
            r60 = r22
            r66 = r24
            r58.<init>(r59, r60, r61, r62, r63, r64, r65, r66)
            r7 = r0
            r7.setDescription((java.lang.String) r10)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.mSubChildGroup = r0
            r8.add(r7)
            if (r16 == 0) goto L_0x0378
            java.lang.String r0 = r13.mLocationId
            r7.mLocationId = r0
            r7.mParent = r13
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r7.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r7)
        L_0x0378:
            if (r16 == 0) goto L_0x037d
            initTransponderItems(r15, r14, r7, r13)
        L_0x037d:
            java.util.List r0 = initSignalLevelAndQualityItems(r15, r14)
            r8.addAll(r0)
            int r0 = r8.size()
            r25 = 2
            int r0 = r0 + -2
            java.lang.Object r0 = r8.get(r0)
            r6 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r6 = (com.mediatek.wwtv.setting.widget.detailui.Action) r6
            int r0 = r8.size()
            r26 = 1
            int r0 = r0 + -1
            java.lang.Object r0 = r8.get(r0)
            r5 = r0
            com.mediatek.wwtv.setting.widget.detailui.Action r5 = (com.mediatek.wwtv.setting.widget.detailui.Action) r5
            if (r16 == 0) goto L_0x03c4
            java.lang.String r0 = r13.mLocationId
            r6.mLocationId = r0
            r6.mParent = r13
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r6.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r6)
            java.lang.String r0 = r13.mLocationId
            r5.mLocationId = r0
            r5.mParent = r13
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r5.setmParentGroup(r0)
            java.util.List<com.mediatek.wwtv.setting.widget.detailui.Action> r0 = r13.mSubChildGroup
            r0.add(r5)
        L_0x03c4:
            if (r17 != 0) goto L_0x03f9
            com.mediatek.wwtv.setting.util.SatDetailUI$16 r4 = new com.mediatek.wwtv.setting.util.SatDetailUI$16
            r0 = r4
            r1 = r12
            r2 = r13
            r3 = r44
            r12 = r4
            r4 = r11
            r27 = r5
            r5 = r9
            r28 = r6
            r6 = r56
            r67 = r7
            r37 = r54
            r13 = r8
            r8 = r55
            r68 = r13
            r40 = r49
            r49 = r57
            r13 = r9
            r9 = r15
            r15 = r20
            r20 = r10
            r10 = r14
            r69 = r11
            r15 = r26
            r11 = r46
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r0 = r46
            r0.setOptionValueChangedCallBack(r12)
            goto L_0x0410
        L_0x03f9:
            r27 = r5
            r28 = r6
            r67 = r7
            r68 = r8
            r13 = r9
            r20 = r10
            r69 = r11
            r15 = r26
            r0 = r46
            r40 = r49
            r37 = r54
            r49 = r57
        L_0x0410:
            if (r17 != 0) goto L_0x0434
            r1 = r47
            if (r1 != r15) goto L_0x041a
            r2 = r55
            r3 = 0
            goto L_0x0439
        L_0x041a:
            int r2 = r13.mInitValue
            r3 = 2
            if (r2 <= r3) goto L_0x042d
            r2 = r55
            r2.setEnabled(r15)
        L_0x0424:
            r4 = r44
            r6 = r56
            r3 = r67
            r5 = r69
            goto L_0x0456
        L_0x042d:
            r2 = r55
            r3 = 0
            r2.setEnabled(r3)
            goto L_0x0424
        L_0x0434:
            r1 = r47
            r2 = r55
            r3 = 0
        L_0x0439:
            r0.setEnabled(r3)
            r4 = r44
            r4.setEnabled(r3)
            r5 = r69
            r5.setEnabled(r3)
            r13.setEnabled(r3)
            r6 = r56
            r6.setEnabled(r3)
            r2.setEnabled(r3)
            r3 = r67
            r3.setEnabled(r15)
        L_0x0456:
            if (r16 == 0) goto L_0x0476
            r7 = r68
            java.util.Iterator r8 = r7.iterator()
        L_0x045e:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x0472
            java.lang.Object r9 = r8.next()
            com.mediatek.wwtv.setting.widget.detailui.Action r9 = (com.mediatek.wwtv.setting.widget.detailui.Action) r9
            r10 = r7
            r7 = r72
            r9.mParent = r7
            r7 = r10
            goto L_0x045e
        L_0x0472:
            r10 = r7
            r7 = r72
            goto L_0x047a
        L_0x0476:
            r10 = r68
            r7 = r72
        L_0x047a:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.SatDetailUI.initSatelliteInfoViewsDiseqc12(com.mediatek.wwtv.setting.widget.detailui.Action, int, android.content.Context):java.util.List");
    }

    public List<Action> initDiseqcSetPageItems(Context context, int satID, Action diseqcSetItem, Action granPaItem) {
        Action diseqcMotorItem;
        final Action diseqc11PortItem = diseqcSetItem;
        Action action = granPaItem;
        List<Action> items = new ArrayList<>();
        SatelliteInfo info = ScanContent.getDVBSsatellitesBySatID(context, satID);
        if (info == null) {
            return items;
        }
        String[] diseqc10PortList = context.getResources().getStringArray(R.array.dvbs_diseqc_10port_arrays);
        String diseqc10portTitle = context.getResources().getString(R.string.dvbs_diseqc10_port);
        int defaultDiseqcInput = info.getDiseqcType();
        int defaultDiseqc10Port = diseqc10PortList.length - 1;
        if (defaultDiseqcInput == 2) {
            defaultDiseqc10Port = Math.min(info.getPort(), 3);
        } else {
            int defaultToneBurstItem = info.getToneBurst();
            if (defaultToneBurstItem == 0) {
                defaultDiseqc10Port = 4;
            } else if (defaultToneBurstItem == 1) {
                defaultDiseqc10Port = 5;
            }
        }
        final Action diseqc10portItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC10_PORT, diseqc10portTitle, 10004, 10004, defaultDiseqc10Port, diseqc10PortList, 1, Action.DataType.LEFTRIGHT_VIEW);
        if (diseqc11PortItem == null || action == null) {
            items.add(diseqc10portItem);
        } else {
            diseqc10portItem.setmParentGroup(action.mSubChildGroup);
            diseqc10portItem.mParent = diseqc11PortItem;
            diseqc11PortItem.mSubChildGroup.add(diseqc10portItem);
        }
        diseqc10portItem.setBeforeChangedCallBack(new Action.BeforeValueChangeCallback() {
            public void beforeValueChanged(int lastValue) {
                if (diseqc10portItem.mInitValue == 4 && lastValue == 5) {
                    SatDetailUI.this.mDvbsNeedTunerReset = true;
                } else if (diseqc10portItem.mInitValue == 5 && lastValue == 4) {
                    SatDetailUI.this.mDvbsNeedTunerReset = true;
                } else {
                    SatDetailUI.this.mDvbsNeedTunerReset = false;
                }
            }
        });
        final Action action2 = diseqc10portItem;
        final Context context2 = context;
        int i = defaultDiseqcInput;
        final int defaultDiseqcInput2 = satID;
        String[] strArr = diseqc10PortList;
        final Action action3 = diseqc11PortItem;
        diseqc10portItem.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "diseqc10portItem>afterOptionValseChanged>" + action2.mInitValue);
                String str2 = SatDetailUI.TAG;
                MtkLog.d(str2, "diseqc10portItem>afterOptionValseChanged2>" + action2.mInitValue);
                ScanContent.saveDVBSSatelliteToSatl(SatDetailUI.this.updateValueForDiseqc12Set(ScanContent.getDVBSsatellitesBySatID(context2, defaultDiseqcInput2), action3));
            }
        });
        String[] diseqc11PortList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_input_arrays);
        String diseqc11portTitle = context.getResources().getString(R.string.dvbs_diseqc11_port);
        int defaultDiseqc11Input = Math.max(Math.min(info.getDiseqcTypeEx(), diseqc11PortList.length - 1), 0);
        int defaultDiseqcInput11Port = diseqc11PortList.length - 1;
        if (defaultDiseqc11Input == 2) {
            defaultDiseqcInput11Port = Math.min(info.getPortEx(), diseqc11PortList.length - 1);
        }
        final Action diseqc11PortItem2 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC11_PORT, diseqc11portTitle, 10004, 10004, defaultDiseqcInput11Port, diseqc11PortList, 1, Action.DataType.OPTIONVIEW);
        if (diseqc11PortItem == null || action == null) {
            items.add(diseqc11PortItem2);
        } else {
            diseqc11PortItem2.setmParentGroup(action.mSubChildGroup);
            diseqc11PortItem2.mParent = diseqc11PortItem;
            diseqc11PortItem.mSubChildGroup.add(diseqc11PortItem2);
        }
        final int i2 = satID;
        diseqc11PortItem2.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                diseqc11PortItem2.mInitValue = Arrays.asList(diseqc11PortItem2.mOptionValue).indexOf(afterName);
                ScanContent.saveDVBSSatelliteToSatl(SatDetailUI.this.updateValueForDiseqc12Set(ScanContent.getDVBSsatellitesBySatID(SatDetailUI.this.mContext, i2), diseqc11PortItem));
            }
        });
        String[] diseqcMotorList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_motor_arrays);
        Action diseqcMotorItem2 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR, context.getResources().getString(R.string.dvbs_diseqc_motor), 10004, 10004, diseqcMotorList.length - 1, diseqcMotorList, 1, Action.DataType.LEFTRIGHT_HASCHILDVIEW);
        if (diseqc11PortItem == null || action == null) {
            String[] strArr2 = diseqcMotorList;
            Action action4 = diseqc11PortItem2;
            int i3 = defaultDiseqc11Input;
            diseqcMotorItem = diseqcMotorItem2;
            items.add(diseqcMotorItem);
        } else {
            diseqcMotorItem2.mParent = diseqc11PortItem;
            diseqcMotorItem2.setmParentGroup(diseqc11PortItem.mSubChildGroup);
            diseqc11PortItem.mSubChildGroup.add(diseqcMotorItem2);
            diseqcMotorItem2.mSubChildGroup = new ArrayList();
            Action diseqcMotorItem3 = diseqcMotorItem2;
            String[] strArr3 = diseqcMotorList;
            Action action5 = diseqc11PortItem2;
            int i4 = defaultDiseqc11Input;
            initDiseqc12MotorPageItems(context, i2, diseqcMotorItem3, diseqc11PortItem, info);
            diseqcMotorItem = diseqcMotorItem3;
        }
        diseqcMotorItem.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
            }
        });
        items.addAll(initSignalLevelAndQualityItems(context, satID));
        Action signalQualityItem = items.get(items.size() - 2);
        Action signalLevelItem = items.get(items.size() - 1);
        if (!(diseqc11PortItem == null || action == null)) {
            signalQualityItem.mParent = diseqc11PortItem;
            signalQualityItem.setmParentGroup(action.mSubChildGroup);
            diseqc11PortItem.mSubChildGroup.add(signalQualityItem);
            signalLevelItem.mParent = diseqc11PortItem;
            signalLevelItem.setmParentGroup(action.mSubChildGroup);
            diseqc11PortItem.mSubChildGroup.add(signalLevelItem);
        }
        if (diseqc11PortItem == null || action == null) {
            return items;
        }
        for (Action tempItem : diseqc11PortItem.mSubChildGroup) {
            tempItem.mLocationId = diseqc11PortItem.mLocationId;
        }
        return diseqc11PortItem.mSubChildGroup;
    }

    public List<Action> initDiseqcSetPageSubItems(Context context, int satID, Action diseqcSetItem, Action granPaItem, SatelliteInfo info) {
        int defaultDiseqc10Port;
        int defaultDiseqcInput11Port;
        int defaultMoto;
        Action action = diseqcSetItem;
        Action action2 = granPaItem;
        ScanContent.setDVBSFreqToGetSignalQuality(satID);
        List<Action> items = new ArrayList<>();
        if (info == null) {
            return items;
        }
        MtkLog.d(TAG, "name:" + info.getName() + "type:" + info.getType() + "enable:" + info.getEnable());
        String[] diseqc10PortList = context.getResources().getStringArray(R.array.dvbs_diseqc_10_port_sub_arrays);
        String diseqc10portTitle = context.getResources().getString(R.string.dvbs_diseqc10_port);
        if (info.getDiseqcType() == 0) {
            defaultDiseqc10Port = 0;
        } else {
            defaultDiseqc10Port = info.getPort() + 1;
        }
        MtkLog.d(TAG, "defaultDiseqc10Port:" + defaultDiseqc10Port + ",info.getDiseqcType():" + info.getDiseqcType());
        final Action diseqc10portItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC10_PORT, diseqc10portTitle, 10004, 10004, defaultDiseqc10Port, diseqc10PortList, 1, Action.DataType.LEFTRIGHT_VIEW);
        if (action == null || action2 == null) {
            items.add(diseqc10portItem);
        } else {
            diseqc10portItem.setmParentGroup(action2.mSubChildGroup);
            diseqc10portItem.mParent = action;
            action.mSubChildGroup.add(diseqc10portItem);
        }
        diseqc10portItem.setBeforeChangedCallBack(new Action.BeforeValueChangeCallback() {
            public void beforeValueChanged(int lastValue) {
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "defaultDiseqc10Port lastValue:" + lastValue);
                if (diseqc10portItem.mInitValue == 4 && lastValue == 5) {
                    SatDetailUI.this.mDvbsNeedTunerReset = true;
                } else if (diseqc10portItem.mInitValue == 5 && lastValue == 4) {
                    SatDetailUI.this.mDvbsNeedTunerReset = true;
                } else {
                    SatDetailUI.this.mDvbsNeedTunerReset = false;
                }
            }
        });
        final Context context2 = context;
        final Action action3 = diseqc10portItem;
        final int i = satID;
        final Action action4 = action;
        diseqc10portItem.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                MenuDataHelper.getInstance(context2).setDiseqc10TunerPort(action3.mInitValue);
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "diseqc10portItem>afterOptionValseChanged>" + action3.mInitValue);
                String str2 = SatDetailUI.TAG;
                MtkLog.d(str2, "diseqc10portItem>afterOptionValseChanged2>" + action3.mInitValue);
                String str3 = SatDetailUI.TAG;
                MtkLog.d(str3, "defaultDiseqc10Port afterName:" + afterName);
                ScanContent.saveDVBSSatelliteToSatl(SatDetailUI.this.updateValueForDiseqc12Set(ScanContent.getDVBSsatellitesBySatID(context2, i), action4));
            }
        });
        String[] diseqc11PortList = this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_11_port_sub_arrays);
        String diseqc11portTitle = context.getResources().getString(R.string.dvbs_diseqc11_port);
        if (info.getDiseqcTypeEx() == 0) {
            defaultDiseqcInput11Port = 0;
        } else {
            defaultDiseqcInput11Port = info.getPortEx() + 1;
        }
        MtkLog.d(TAG, "defaultDiseqcInput11Port:" + defaultDiseqcInput11Port);
        Action diseqc11PortItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC11_PORT, diseqc11portTitle, 10004, 10004, defaultDiseqcInput11Port, diseqc11PortList, 1, Action.DataType.LEFTRIGHT_VIEW);
        if (action == null || action2 == null) {
            items.add(diseqc11PortItem);
        } else {
            diseqc11PortItem.setmParentGroup(action2.mSubChildGroup);
            diseqc11PortItem.mParent = action;
            action.mSubChildGroup.add(diseqc11PortItem);
        }
        final Action action5 = diseqc10portItem;
        final Context context3 = context;
        String[] strArr = diseqc11PortList;
        AnonymousClass23 r13 = r0;
        final Action action6 = diseqc11PortItem;
        String str = diseqc11portTitle;
        Action diseqc11PortItem2 = diseqc11PortItem;
        final int i2 = satID;
        int i3 = defaultDiseqc10Port;
        final Action action7 = action;
        AnonymousClass23 r0 = new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                if (!afterName.equalsIgnoreCase("Disable")) {
                    action5.setDescription(0);
                    action5.mInitValue = 0;
                }
                MenuDataHelper.getInstance(context3).setDiseqc11TunerPort(action6.mInitValue);
                action6.mInitValue = Arrays.asList(action6.mOptionValue).indexOf(afterName);
                ScanContent.saveDVBSSatelliteToSatl(SatDetailUI.this.updateValueForDiseqc12Set(ScanContent.getDVBSsatellitesBySatID(SatDetailUI.this.mContext, i2), action7));
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "diseqc11PortItem>afterOptionValseChanged>" + action6.mInitValue);
                String str2 = SatDetailUI.TAG;
                MtkLog.d(str2, "diseqc11PortItem afterName:" + afterName);
            }
        };
        diseqc11PortItem2.setOptionValueChangedCallBack(r13);
        if (info.getMotorType() == 5) {
            defaultMoto = 1;
        } else {
            defaultMoto = 0;
        }
        int defaultMoto2 = defaultMoto;
        MtkLog.d(TAG, "defaultMoto:" + defaultMoto2);
        Action diseqcMotorItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR, context.getResources().getString(R.string.dvbs_diseqc_motor), 10004, 10004, defaultMoto2, this.mContext.getResources().getStringArray(R.array.dvbs_diseqc_motor_arrays), 1, defaultMoto2 == 0 ? Action.DataType.LEFTRIGHT_VIEW : Action.DataType.LEFTRIGHT_HASDETAILVIEW);
        boolean isFunctionSupport = MarketRegionInfo.isFunctionSupport(24);
        if (action == null || action2 == null) {
            items.add(diseqcMotorItem);
        } else {
            diseqcMotorItem.mParent = action;
            diseqcMotorItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(diseqcMotorItem);
            diseqcMotorItem.mSubChildGroup = new ArrayList();
            initDiseqc12MotorPageItems(context, satID, diseqcMotorItem, action, info);
        }
        MtkLog.d(TAG, "motor item init...");
        final Action action8 = diseqcMotorItem;
        final Action action9 = diseqc10portItem;
        final Action action10 = diseqc11PortItem2;
        AnonymousClass24 r7 = r0;
        final int i4 = satID;
        Action diseqcMotorItem2 = diseqcMotorItem;
        final Action diseqcMotorItem3 = action;
        AnonymousClass24 r02 = new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "motor item aftername:" + afterName);
                action8.setmDataType(afterName.equalsIgnoreCase("Disable") ? Action.DataType.LEFTRIGHT_VIEW : Action.DataType.LEFTRIGHT_HASDETAILVIEW);
                SatDetailUI.this.switchMotorType(afterName.equalsIgnoreCase("Disable") ? 0 : 5);
                action9.mInitValue = 0;
                action10.mInitValue = 0;
                ScanContent.saveDVBSSatelliteToSatl(SatDetailUI.this.updateValueForDiseqc12Set(ScanContent.getDVBSsatellitesBySatID(SatDetailUI.this.mContext, i4), diseqcMotorItem3));
            }
        };
        diseqcMotorItem2.setOptionValueChangedCallBack(r7);
        if (action == null || action2 == null) {
            return items;
        }
        for (Action tempItem : action.mSubChildGroup) {
            tempItem.mLocationId = action.mLocationId;
        }
        return action.mSubChildGroup;
    }

    /* access modifiers changed from: private */
    public void switchMotorType(int type) {
        List<MtkTvDvbsConfigInfoBase> list;
        MtkTvDvbsConfigBase mSatl = new MtkTvDvbsConfigBase();
        int svlID = CommonIntegration.getInstance().getSvl();
        MtkTvChannelInfoBase info = CommonIntegration.getInstance().getCurChInfo();
        if (info != null && (info instanceof MtkTvDvbChannelInfo) && (list = mSatl.getSatlRecord(svlID, ((MtkTvDvbChannelInfo) info).getSatRecId())) != null && list.size() > 0) {
            MtkTvDvbsConfigInfoBase dvbsConfigInfoBase = list.get(0);
            dvbsConfigInfoBase.setMotorType(type);
            mSatl.updateSatlRecord(svlID, dvbsConfigInfoBase, true);
        }
    }

    private int getMotorType() {
        List<MtkTvDvbsConfigInfoBase> list;
        MtkTvDvbsConfigBase mSatl = new MtkTvDvbsConfigBase();
        int svlID = CommonIntegration.getInstance().getSvl();
        MtkTvChannelInfoBase info = CommonIntegration.getInstance().getCurChInfo();
        if (info == null || !(info instanceof MtkTvDvbChannelInfo) || (list = mSatl.getSatlRecord(svlID, ((MtkTvDvbChannelInfo) info).getSatRecId())) == null || list.size() <= 0) {
            return 0;
        }
        int motorType = list.get(0).getMotorType();
        String str = TAG;
        MtkLog.d(str, "motorType:" + motorType);
        if (5 == motorType) {
            return 1;
        }
        return motorType;
    }

    public List<Action> initDiseqc12MotorPageItems(Context context, int satID, Action diseqcMotorItem, Action parentItem, SatelliteInfo info) {
        Action action = diseqcMotorItem;
        List<Action> items = new ArrayList<>();
        final SaveValue savevalue = SaveValue.getInstance(this.mContext);
        int defaultMovementControl = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL);
        String[] movementControlList = context.getResources().getStringArray(R.array.dvbs_diseqc_movement_control_arrays);
        String movementControlTitle = context.getResources().getString(R.string.dvbs_diseqc12_motor_movementcontrol);
        Action.DataType MCType = Action.DataType.TEXTCOMMVIEW;
        if (!(action == null || parentItem == null)) {
            MCType = Action.DataType.HAVESUBCHILD;
        }
        Action movementControlItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL, movementControlTitle, MCType);
        movementControlItem.setEnabled(true);
        if (action == null || parentItem == null) {
            Context context2 = context;
            int i = satID;
            items.add(movementControlItem);
        } else {
            movementControlItem.mParent = action;
            movementControlItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(movementControlItem);
            movementControlItem.mSubChildGroup = new ArrayList();
            initDiseqc12MovementControlPageItmes(context, satID, movementControlItem, action);
        }
        Action action2 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS, context.getResources().getString(R.string.dvbs_diseqc12_motor_disablelimits), 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(action2);
        } else {
            action2.mParent = action;
            action2.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action2);
        }
        int i2 = defaultMovementControl;
        Action limitEastItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST, context.getResources().getString(R.string.dvbs_diseqc12_motor_limiteast), 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(limitEastItem);
        } else {
            limitEastItem.mParent = action;
            limitEastItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(limitEastItem);
        }
        Action action3 = limitEastItem;
        String limitWestTitle = context.getResources().getString(R.string.dvbs_diseqc12_motor_limitwest);
        String[] strArr = movementControlList;
        Action limitWestItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST, limitWestTitle, 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(limitWestItem);
        } else {
            limitWestItem.mParent = action;
            limitWestItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(limitWestItem);
        }
        String str = limitWestTitle;
        int defaultStorePos = Math.max(0, info.getMotorPosition() - 1);
        Action action4 = limitWestItem;
        String[] storePositionList = context.getResources().getStringArray(R.array.dvbs_diseqc_motor_store_position_arrays);
        String str2 = movementControlTitle;
        String storePositionTitle = context.getResources().getString(R.string.dvbs_diseqc12_motor_storeposition);
        final Action action5 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION, storePositionTitle, 10004, 10004, defaultStorePos, storePositionList, 1, Action.DataType.LEFTRIGHT_VIEW);
        if (action == null || parentItem == null) {
            items.add(action5);
        } else {
            action5.mParent = action;
            int i3 = defaultStorePos;
            action5.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action5);
        }
        String str3 = storePositionTitle;
        final SatelliteInfo satelliteInfo = info;
        action5.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                action5.mInitValue = Arrays.asList(action5.mOptionValue).indexOf(afterName);
                savevalue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION, action5.mInitValue);
                satelliteInfo.setMotorPosition(action5.mInitValue + 1);
                satelliteInfo.setMotorType(5);
                String str = SatDetailUI.TAG;
                MtkLog.d(str, "info.toString():" + satelliteInfo.toString());
                ScanContent.saveDVBSSatelliteToSatl(satelliteInfo);
            }
        });
        int defaultGotoPos = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION);
        Action action6 = action5;
        String[] gotoPositionList = context.getResources().getStringArray(R.array.dvbs_diseqc_motor_store_position_arrays);
        Action.DataType dataType = MCType;
        final Action action7 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION, context.getResources().getString(R.string.dvbs_diseqc12_motor_gotoposition), 10004, 10004, defaultGotoPos, gotoPositionList, 1, Action.DataType.LEFTRIGHT_VIEW);
        if (action == null || parentItem == null) {
            items.add(action7);
        } else {
            action7.mParent = action;
            int i4 = defaultGotoPos;
            action7.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action7);
        }
        action7.setOptionValueChangedCallBack(new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                action7.mInitValue = Arrays.asList(action7.mOptionValue).indexOf(afterName);
                savevalue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION, action7.mInitValue);
            }
        });
        String gotoReferenceTitle = context.getResources().getString(R.string.dvbs_diseqc12_motor_gotoreference);
        SaveValue saveValue = savevalue;
        Action gotoReferenceItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE, gotoReferenceTitle, 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(gotoReferenceItem);
        } else {
            gotoReferenceItem.mParent = action;
            gotoReferenceItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(gotoReferenceItem);
        }
        if (action == null || parentItem == null) {
            return items;
        }
        for (Action tempItem : action.mSubChildGroup) {
            tempItem.mLocationId = action.mLocationId;
            gotoReferenceTitle = gotoReferenceTitle;
        }
        return action.mSubChildGroup;
    }

    public List<Action> initDiseqc12MovementControlPageItmes(Context context, int satID, Action diseqcMCItem, Action parentItem) {
        Action action = diseqcMCItem;
        List<Action> items = new ArrayList<>();
        SaveValue savevalue = SaveValue.getInstance(this.mContext);
        int defaultMovementControl = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL);
        String[] movementControlList = context.getResources().getStringArray(R.array.dvbs_diseqc_movement_control_arrays);
        Action movementControlItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL, context.getResources().getString(R.string.dvbs_diseqc12_motor_movementcontrol), 10004, 10004, defaultMovementControl, movementControlList, 1, Action.DataType.OPTIONVIEW);
        if (action == null || parentItem == null) {
            items.add(movementControlItem);
        } else {
            movementControlItem.mParent = action;
            movementControlItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(movementControlItem);
        }
        int defaultStepSize = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE);
        if (defaultStepSize == 0) {
            defaultStepSize = 1;
        }
        final Action stepSizeItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE, context.getResources().getString(R.string.dvbs_diseqc12_movement_stepsize), 1, 127, defaultStepSize, new String[0], 1, Action.DataType.NUMVIEW);
        stepSizeItem.setInputLength(3);
        if (action == null || parentItem == null) {
            items.add(stepSizeItem);
        } else {
            stepSizeItem.mParent = action;
            stepSizeItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(stepSizeItem);
        }
        int defaultTimeout = savevalue.readValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS);
        if (defaultTimeout == 0) {
            defaultTimeout = 1;
        }
        Action timeoutSItem = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS, context.getResources().getString(R.string.dvbs_diseqc12_movement_timeout), 1, 126, defaultTimeout, new String[0], 1, Action.DataType.NUMVIEW);
        timeoutSItem.setInputLength(3);
        if (action == null || parentItem == null) {
            items.add(timeoutSItem);
        } else {
            timeoutSItem.mParent = action;
            timeoutSItem.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(timeoutSItem);
        }
        final Action action2 = movementControlItem;
        final SaveValue saveValue = savevalue;
        final Action action3 = action;
        AnonymousClass27 r11 = r0;
        final Action action4 = parentItem;
        Action timeoutSItem2 = timeoutSItem;
        final String[] strArr = movementControlList;
        Action stepSizeItem2 = stepSizeItem;
        SaveValue saveValue2 = savevalue;
        final Action action5 = timeoutSItem2;
        AnonymousClass27 r0 = new Action.OptionValuseChangedCallBack() {
            public void afterOptionValseChanged(String afterName) {
                action2.mInitValue = Arrays.asList(action2.mOptionValue).indexOf(afterName);
                saveValue.saveValue(MenuConfigManager.DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL, action2.mInitValue);
                if (!(action3 == null || action4 == null)) {
                    Action action = action3;
                    action.mOptionValue = new String[]{strArr[action2.mInitValue]};
                }
                switch (action2.mInitValue) {
                    case 0:
                        stepSizeItem.setEnabled(false);
                        action5.setEnabled(false);
                        return;
                    case 1:
                        stepSizeItem.setEnabled(true);
                        action5.setEnabled(false);
                        return;
                    case 2:
                        stepSizeItem.setEnabled(false);
                        action5.setEnabled(true);
                        return;
                    default:
                        return;
                }
            }
        };
        movementControlItem.setOptionValueChangedCallBack(r11);
        if (movementControlItem.mInitValue == 0) {
            stepSizeItem2.setEnabled(false);
            timeoutSItem2.setEnabled(false);
        } else {
            Action timeoutSItem3 = timeoutSItem2;
            Action stepSizeItem3 = stepSizeItem2;
            if (movementControlItem.mInitValue == 1) {
                stepSizeItem3.setEnabled(true);
                timeoutSItem3.setEnabled(false);
            } else if (movementControlItem.mInitValue == 2) {
                stepSizeItem3.setEnabled(false);
                timeoutSItem3.setEnabled(true);
            }
        }
        Action action6 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST, context.getResources().getString(R.string.dvbs_diseqc12_movement_moveeast), 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(action6);
        } else {
            action6.mParent = action;
            action6.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action6);
        }
        Action action7 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST, context.getResources().getString(R.string.dvbs_diseqc12_movement_movewest), 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(action7);
        } else {
            action7.mParent = action;
            action7.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action7);
        }
        Action action8 = new Action(MenuConfigManager.DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT, context.getResources().getString(R.string.dvbs_diseqc12_movement_stopmovement), 10004, 10004, 0, new String[]{""}, 1, Action.DataType.DISEQC12_SAVEINFO);
        if (action == null || parentItem == null) {
            items.add(action8);
        } else {
            action8.mParent = action;
            action8.setmParentGroup(action.mSubChildGroup);
            action.mSubChildGroup.add(action8);
        }
        if (action == null || parentItem == null) {
            return items;
        }
        for (Action tempItem : action.mSubChildGroup) {
            tempItem.mLocationId = action.mLocationId;
        }
        return action.mSubChildGroup;
    }

    public void specialOptionClick(Action currAction) {
    }

    public static void setMySelfNull() {
        mSelf = null;
    }
}
