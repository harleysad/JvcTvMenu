package com.mediatek.wwtv.setting.base.scan.model;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ListView;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelListBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.CountryConfigEntry;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScanContent {
    private static final boolean DVBS_DEV_ING = false;
    public static int[][] LNB_CONFIG = null;
    private static final int LNB_DUAL_FREQ = 2;
    private static final int LNB_SINGLE_FREQ = 1;
    public static final String SAT_ANTENNA_TYPE = "g_bs__bs_sat_antenna_type";
    public static final String SAT_BRDCSTER = "g_bs__bs_sat_brdcster";
    public static final String SUSPEND_KEY = "debug.mtk.tkui.cancel_scan";
    private static final String TAG = "ScanContent";
    private static int dvbsCurrOperator = -1;
    private static Context mContext;
    public static boolean mDvbsNeedTunerReset;
    private static ScanContent mTVContent;

    enum TunerMode {
        CABLE,
        DVBT,
        DVBS
    }

    ScanContent(Context activity) {
        mContext = activity;
    }

    public static ScanContent getInstance(Context activity) {
        if (mTVContent == null) {
            mTVContent = new ScanContent(activity);
            initLNBConfig();
        }
        return mTVContent;
    }

    public static void initLNBConfig() {
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

    public static List<String> getCableOperationList(Context context) {
        MtkLog.d(TAG, "getCableOperationList");
        String country = MtkTvConfig.getInstance().getCountry();
        CountryConfigEntry countryConfigEntry = PartnerSettingsConfig.getCountryConfigMap().get(country);
        List<String> result = new ArrayList<>();
        if (countryConfigEntry == null || TextUtils.isEmpty(countryConfigEntry.dvbc_operators)) {
            MtkLog.d(TAG, "CountryConfigEntry null or dvbc_operators empty");
        } else {
            String[] strings = countryConfigEntry.dvbc_operators.trim().split(",");
            String[] operats = context.getResources().getStringArray(R.array.dvbc_operators_eu);
            int length = strings.length;
            int i = 0;
            while (i < length) {
                try {
                    int operator = Integer.parseInt(strings[i]);
                    if (!country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_IRL) || operator != 1) {
                        if (operator >= 0 && operator < operats.length) {
                            result.add(operats[operator]);
                        }
                        i++;
                    } else {
                        result.add(context.getString(R.string.dvbc_operator_virgin_media));
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean isCountryPT() {
        return CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry()) == 22;
    }

    public static boolean isCountryBel() {
        return CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry()) == 2;
    }

    public static boolean isCountryIre() {
        return CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry()) == 20;
    }

    public static boolean isCountryUK() {
        return MtkTvConfig.getInstance().getCountry().equalsIgnoreCase("GBR");
    }

    public static List<String> getDVBSOperatorList(Context context) {
        if (!MarketRegionInfo.isFunctionSupport(4) && !MarketRegionInfo.isFunctionSupport(50)) {
            return new ArrayList();
        }
        CountryConfigEntry countryConfigEntry = PartnerSettingsConfig.getCountryConfigMap().get(MtkTvConfig.getInstance().getCountry());
        List<String> result = new ArrayList<>();
        if (countryConfigEntry == null || TextUtils.isEmpty(countryConfigEntry.dvbs_operators)) {
            MtkLog.d(TAG, "CountryConfigEntry null or dvbs_operators empty");
        } else {
            String[] strings = countryConfigEntry.dvbs_operators.trim().split(",");
            DVBSOperator dvbsOperator = new DVBSOperator(context);
            for (String string : strings) {
                if (!TextUtils.isEmpty(string)) {
                    try {
                        result.add(dvbsOperator.getNameByOperator(Integer.parseInt(string)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public static String[] initScanModesForOperator(Context context, CableOperator operator, List<CableOperator> list) {
        String advanceStr = context.getString(R.string.menu_arrays_Advance);
        String fullStr = context.getString(R.string.menu_arrays_Full);
        String quickStr = context.getString(R.string.menu_arrays_Quick);
        String[] scanMode = {advanceStr, quickStr, fullStr};
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        MtkLog.d(String.format("initScanModesForOperator(),operator:%s,countryID:%d", new Object[]{operator.name(), Integer.valueOf(countryID)}));
        switch (countryID) {
            case 1:
            case 3:
            case 20:
            case 21:
                int i = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i != 4) {
                    if (i == 9) {
                        scanMode = new String[]{advanceStr};
                        break;
                    }
                } else {
                    scanMode = new String[]{advanceStr, quickStr, fullStr};
                    break;
                }
                break;
            case 2:
                int i2 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i2 != 4) {
                    if (i2 == 15) {
                        scanMode = new String[]{advanceStr};
                        break;
                    }
                } else {
                    scanMode = new String[]{advanceStr, quickStr, fullStr};
                    break;
                }
                break;
            case 5:
                int i3 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i3 != 4) {
                    switch (i3) {
                        case 11:
                        case 12:
                            scanMode = new String[]{advanceStr, quickStr};
                            break;
                        case 13:
                            break;
                    }
                }
                scanMode = new String[]{quickStr, fullStr};
                break;
            case 6:
                int i4 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i4 == 2) {
                    scanMode = new String[]{advanceStr, fullStr};
                    break;
                } else {
                    switch (i4) {
                        case 4:
                            scanMode = new String[]{advanceStr, quickStr, fullStr};
                            break;
                        case 5:
                            scanMode = new String[]{advanceStr};
                            break;
                        case 6:
                            scanMode = new String[]{advanceStr};
                            break;
                        case 7:
                            scanMode = new String[]{quickStr, fullStr};
                            break;
                    }
                }
            case 8:
                int i5 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i5 != 2) {
                    if (i5 == 4) {
                        scanMode = new String[]{quickStr, fullStr};
                        break;
                    }
                } else {
                    scanMode = new String[]{advanceStr, fullStr};
                    break;
                }
                break;
            case 9:
                int i6 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i6 != 4) {
                    if (i6 == 14) {
                        scanMode = new String[]{advanceStr};
                        break;
                    }
                } else {
                    scanMode = new String[]{advanceStr, quickStr, fullStr};
                    break;
                }
                break;
            case 13:
                int i7 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i7 != 4) {
                    switch (i7) {
                        case 8:
                        case 9:
                            break;
                    }
                }
                scanMode = new String[]{advanceStr};
                break;
            case 14:
                if (AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()] == 2) {
                    scanMode = new String[]{advanceStr, fullStr};
                    break;
                } else {
                    scanMode = new String[]{advanceStr, fullStr};
                    break;
                }
            case 15:
                switch (operator) {
                    case Comhem:
                        scanMode = new String[]{advanceStr};
                        break;
                    case Canal_Digital:
                        scanMode = new String[]{advanceStr, fullStr};
                        break;
                    case TELE2:
                        scanMode = new String[]{quickStr};
                        break;
                    case OTHER:
                        scanMode = new String[]{advanceStr, quickStr, fullStr};
                        break;
                }
            case 16:
                switch (operator) {
                    case BLIZOO:
                    case NET1:
                        scanMode = new String[]{advanceStr};
                        break;
                }
            case 19:
                switch (operator) {
                    case UPC:
                        scanMode = new String[]{advanceStr};
                        break;
                    case RCS_RDS:
                        scanMode = new String[]{quickStr};
                        break;
                }
            case 23:
                int i8 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i8 == 4) {
                    scanMode = new String[]{advanceStr, quickStr, fullStr};
                    break;
                } else {
                    switch (i8) {
                        case 9:
                            scanMode = new String[]{advanceStr};
                            break;
                        case 10:
                            scanMode = new String[]{quickStr};
                            break;
                    }
                }
                break;
            case 24:
                switch (operator) {
                    case AKADO:
                    case ONLIME:
                    case TVOE_STP:
                        scanMode = new String[]{advanceStr};
                        break;
                    case DIVAN_TV:
                        scanMode = new String[]{quickStr};
                        break;
                }
            case 27:
                switch (operator) {
                    case TELEING:
                    case TELEMACH:
                        scanMode = new String[]{quickStr};
                        break;
                }
            case 36:
                if (AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()] == 24) {
                    scanMode = new String[]{quickStr};
                    break;
                }
                break;
            default:
                scanMode = new String[]{advanceStr, quickStr, fullStr};
                break;
        }
        MtkLog.d("ScanMode:" + Arrays.asList(scanMode).toString());
        return scanMode;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0234, code lost:
        r47 = r2;
        r54 = r3;
        r43 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
        r49 = r29;
        r52 = r30;
        r53 = r32;
        r50 = r33;
        r48 = r34;
        r5 = r35;
        r2 = r36;
        r46 = r37;
        r45 = r38;
        r44 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x02d4, code lost:
        r47 = r2;
        r54 = r3;
        r48 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
        r49 = r29;
        r52 = r30;
        r53 = r32;
        r50 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0320, code lost:
        r50 = r2;
        r54 = r3;
        r48 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
        r49 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0366, code lost:
        r50 = r2;
        r54 = r3;
        r49 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0378, code lost:
        r52 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0413, code lost:
        r50 = r2;
        r54 = r3;
        r52 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0425, code lost:
        r53 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0427, code lost:
        r5 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x04b1, code lost:
        r57 = r2;
        r54 = r3;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x04f4, code lost:
        r2 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x053b, code lost:
        r59 = r2;
        r2 = new java.lang.StringBuilder();
        r60 = r3;
        r2.append("info.getCountryID():");
        r2.append(r1);
        com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0553, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x01fb, code lost:
        r42 = r2;
        r54 = r3;
        r43 = r5;
        r58 = r19;
        r3 = r22;
        r56 = r23;
        r55 = r24;
        r57 = r27;
        r51 = r28;
        r49 = r29;
        r52 = r30;
        r53 = r32;
        r50 = r33;
        r48 = r34;
        r5 = r35;
        r2 = r36;
        r46 = r37;
        r45 = r38;
        r44 = r39;
        r47 = r40;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<java.lang.String> getDVBSOperatorList(android.content.Context r61, int r62) {
        /*
            r0 = r61
            r1 = r62
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getDVBSOperatorList():countryID: "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)
            r2 = 2131689792(0x7f0f0140, float:1.900861E38)
            java.lang.String r2 = r0.getString(r2)
            r3 = 2131689826(0x7f0f0162, float:1.9008678E38)
            java.lang.String r3 = r0.getString(r3)
            r4 = 2131689793(0x7f0f0141, float:1.9008611E38)
            java.lang.String r4 = r0.getString(r4)
            r5 = 2131689822(0x7f0f015e, float:1.900867E38)
            java.lang.String r5 = r0.getString(r5)
            r6 = 2131689794(0x7f0f0142, float:1.9008613E38)
            java.lang.String r6 = r0.getString(r6)
            r7 = 2131689795(0x7f0f0143, float:1.9008615E38)
            java.lang.String r7 = r0.getString(r7)
            r8 = 2131689829(0x7f0f0165, float:1.9008684E38)
            java.lang.String r8 = r0.getString(r8)
            r9 = 2131689830(0x7f0f0166, float:1.9008686E38)
            java.lang.String r9 = r0.getString(r9)
            r10 = 2131689827(0x7f0f0163, float:1.900868E38)
            java.lang.String r10 = r0.getString(r10)
            r11 = 2131689828(0x7f0f0164, float:1.9008682E38)
            java.lang.String r11 = r0.getString(r11)
            r12 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r12 = r0.getString(r12)
            r13 = 2131689825(0x7f0f0161, float:1.9008676E38)
            java.lang.String r13 = r0.getString(r13)
            r14 = 2131689796(0x7f0f0144, float:1.9008617E38)
            java.lang.String r14 = r0.getString(r14)
            r15 = 2131689797(0x7f0f0145, float:1.900862E38)
            java.lang.String r15 = r0.getString(r15)
            r16 = r9
            r9 = 2131689804(0x7f0f014c, float:1.9008634E38)
            java.lang.String r9 = r0.getString(r9)
            r17 = r11
            r11 = 2131689800(0x7f0f0148, float:1.9008626E38)
            java.lang.String r11 = r0.getString(r11)
            r18 = r15
            r15 = 2131689801(0x7f0f0149, float:1.9008628E38)
            java.lang.String r15 = r0.getString(r15)
            r19 = r6
            r6 = 2131689803(0x7f0f014b, float:1.9008632E38)
            java.lang.String r6 = r0.getString(r6)
            r20 = r6
            r6 = 2131689798(0x7f0f0146, float:1.9008622E38)
            java.lang.String r6 = r0.getString(r6)
            r21 = r6
            r6 = 2131689799(0x7f0f0147, float:1.9008624E38)
            java.lang.String r6 = r0.getString(r6)
            r22 = r5
            r5 = 2131689802(0x7f0f014a, float:1.900863E38)
            java.lang.String r5 = r0.getString(r5)
            r23 = r10
            r10 = 2131689821(0x7f0f015d, float:1.9008668E38)
            java.lang.String r10 = r0.getString(r10)
            r24 = r8
            r8 = 2131689823(0x7f0f015f, float:1.9008672E38)
            java.lang.String r8 = r0.getString(r8)
            r25 = r8
            r8 = 2131689858(0x7f0f0182, float:1.9008743E38)
            java.lang.String r8 = r0.getString(r8)
            r26 = r8
            r8 = 2131689811(0x7f0f0153, float:1.9008648E38)
            java.lang.String r8 = r0.getString(r8)
            r27 = r3
            r3 = 2131689813(0x7f0f0155, float:1.9008652E38)
            java.lang.String r3 = r0.getString(r3)
            r28 = r2
            r2 = 2131689817(0x7f0f0159, float:1.900866E38)
            java.lang.String r2 = r0.getString(r2)
            r29 = r14
            r14 = 2131689816(0x7f0f0158, float:1.9008658E38)
            java.lang.String r14 = r0.getString(r14)
            r30 = r14
            r14 = 2131689819(0x7f0f015b, float:1.9008664E38)
            java.lang.String r14 = r0.getString(r14)
            r31 = r14
            r14 = 2131689808(0x7f0f0150, float:1.9008642E38)
            java.lang.String r14 = r0.getString(r14)
            r32 = r14
            r14 = 2131689815(0x7f0f0157, float:1.9008656E38)
            java.lang.String r14 = r0.getString(r14)
            r33 = r7
            r7 = 2131689805(0x7f0f014d, float:1.9008636E38)
            java.lang.String r7 = r0.getString(r7)
            r34 = r6
            r6 = 2131689820(0x7f0f015c, float:1.9008666E38)
            java.lang.String r6 = r0.getString(r6)
            r35 = r6
            r6 = 2131689812(0x7f0f0154, float:1.900865E38)
            java.lang.String r6 = r0.getString(r6)
            r36 = r6
            r6 = 2131689806(0x7f0f014e, float:1.9008638E38)
            java.lang.String r6 = r0.getString(r6)
            r37 = r3
            r3 = 2131689807(0x7f0f014f, float:1.900864E38)
            java.lang.String r3 = r0.getString(r3)
            r38 = r9
            r9 = 2131689818(0x7f0f015a, float:1.9008662E38)
            java.lang.String r9 = r0.getString(r9)
            r39 = r8
            r8 = 2131689814(0x7f0f0156, float:1.9008654E38)
            java.lang.String r8 = r0.getString(r8)
            r40 = r7
            r7 = 2131689809(0x7f0f0151, float:1.9008644E38)
            java.lang.String r7 = r0.getString(r7)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r41 = r0
            java.util.LinkedList r0 = new java.util.LinkedList
            r0.<init>()
            switch(r1) {
                case 1: goto L_0x04f7;
                case 2: goto L_0x04be;
                case 3: goto L_0x0491;
                case 4: goto L_0x0462;
                case 5: goto L_0x042b;
                case 6: goto L_0x03f8;
                case 7: goto L_0x03e3;
                case 8: goto L_0x03c8;
                case 9: goto L_0x03b0;
                case 10: goto L_0x039a;
                case 11: goto L_0x037c;
                case 12: goto L_0x0350;
                case 13: goto L_0x0335;
                case 14: goto L_0x0309;
                case 15: goto L_0x02f0;
                case 16: goto L_0x02c6;
                case 17: goto L_0x02b7;
                case 18: goto L_0x02a8;
                case 19: goto L_0x0290;
                case 20: goto L_0x0281;
                case 21: goto L_0x025c;
                case 22: goto L_0x0230;
                case 23: goto L_0x0225;
                case 24: goto L_0x01ee;
                case 25: goto L_0x01ed;
                case 26: goto L_0x01da;
                case 27: goto L_0x01d6;
                case 28: goto L_0x01c3;
                case 29: goto L_0x01c2;
                case 30: goto L_0x0191;
                case 31: goto L_0x01bb;
                default: goto L_0x0191;
            }
        L_0x0191:
            r42 = r2
            r54 = r3
            r43 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r2 = r36
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            goto L_0x053b
        L_0x01bb:
            r0.clear()
            r0.add(r7)
            goto L_0x01fb
        L_0x01c2:
            goto L_0x01fb
        L_0x01c3:
            r0.clear()
            r0.add(r11)
            r0.add(r15)
            r0.add(r5)
            r0.add(r2)
            r0.add(r14)
            goto L_0x01fb
        L_0x01d6:
            r0.add(r4)
            goto L_0x01fb
        L_0x01da:
            r0.clear()
            r0.add(r13)
            r0.add(r12)
            r0.add(r4)
            r0.add(r3)
            r0.add(r6)
            goto L_0x01fb
        L_0x01ed:
            goto L_0x01fb
        L_0x01ee:
            r0.clear()
            r0.add(r10)
            r0.add(r9)
            r0.add(r8)
        L_0x01fb:
            r42 = r2
            r54 = r3
            r43 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r2 = r36
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            goto L_0x053b
        L_0x0225:
            r0.clear()
            r42 = r2
            r2 = r40
            r0.add(r2)
            goto L_0x0234
        L_0x0230:
            r42 = r2
            r2 = r40
        L_0x0234:
            r47 = r2
            r54 = r3
            r43 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r2 = r36
            r46 = r37
            r45 = r38
            r44 = r39
            goto L_0x053b
        L_0x025c:
            r42 = r2
            r2 = r40
            r0.clear()
            r43 = r5
            r5 = r39
            r0.add(r5)
            r44 = r5
            r5 = r38
            r0.add(r5)
            r45 = r5
            r5 = r37
            r0.add(r5)
            r46 = r5
            r5 = r34
            r0.add(r5)
            goto L_0x02d4
        L_0x0281:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
            goto L_0x02d4
        L_0x0290:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
            r0.clear()
            r0.add(r4)
            r0.add(r2)
            goto L_0x02d4
        L_0x02a8:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
            goto L_0x02d4
        L_0x02b7:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
            goto L_0x02d4
        L_0x02c6:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
        L_0x02d4:
            r47 = r2
            r54 = r3
            r48 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            goto L_0x0427
        L_0x02f0:
            r42 = r2
            r43 = r5
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r2 = r40
            r0.clear()
            r47 = r2
            r2 = r33
            r0.add(r2)
            goto L_0x0320
        L_0x0309:
            r42 = r2
            r43 = r5
            r2 = r33
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r0.add(r2)
        L_0x0320:
            r50 = r2
            r54 = r3
            r48 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
            r49 = r29
            goto L_0x0378
        L_0x0335:
            r42 = r2
            r43 = r5
            r2 = r33
            r5 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r48 = r5
            r5 = r29
            r0.add(r5)
            goto L_0x0366
        L_0x0350:
            r42 = r2
            r43 = r5
            r5 = r29
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
        L_0x0366:
            r50 = r2
            r54 = r3
            r49 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
        L_0x0378:
            r52 = r30
            goto L_0x0425
        L_0x037c:
            r42 = r2
            r43 = r5
            r5 = r29
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r49 = r5
            r5 = r30
            r0.add(r5)
            goto L_0x0413
        L_0x039a:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            goto L_0x0413
        L_0x03b0:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            goto L_0x0413
        L_0x03c8:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r0.add(r2)
            goto L_0x0413
        L_0x03e3:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            goto L_0x0413
        L_0x03f8:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r0.add(r2)
        L_0x0413:
            r50 = r2
            r54 = r3
            r52 = r5
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            r57 = r27
            r51 = r28
        L_0x0425:
            r53 = r32
        L_0x0427:
            r5 = r35
            goto L_0x04f4
        L_0x042b:
            r42 = r2
            r43 = r5
            r49 = r29
            r5 = r30
            r2 = r33
            r48 = r34
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r50 = r2
            r2 = r28
            r0.add(r2)
            r51 = r2
            r2 = r27
            r0.add(r2)
            r0.add(r4)
            r52 = r5
            r5 = r32
            r0.add(r5)
            r53 = r5
            r5 = r35
            r0.add(r5)
            goto L_0x04b1
        L_0x0462:
            r42 = r2
            r43 = r5
            r2 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r0.add(r12)
            r0.add(r13)
            r0.add(r4)
            r0.add(r6)
            r0.add(r3)
            goto L_0x04b1
        L_0x0491:
            r42 = r2
            r43 = r5
            r2 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.add(r4)
        L_0x04b1:
            r57 = r2
            r54 = r3
            r58 = r19
            r3 = r22
            r56 = r23
            r55 = r24
            goto L_0x04f4
        L_0x04be:
            r42 = r2
            r43 = r5
            r2 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r54 = r3
            r3 = r24
            r0.add(r3)
            r55 = r3
            r3 = r23
            r0.add(r3)
            r57 = r2
            r56 = r3
            r58 = r19
            r3 = r22
        L_0x04f4:
            r2 = r36
            goto L_0x053b
        L_0x04f7:
            r42 = r2
            r54 = r3
            r43 = r5
            r3 = r23
            r55 = r24
            r2 = r27
            r51 = r28
            r49 = r29
            r52 = r30
            r53 = r32
            r50 = r33
            r48 = r34
            r5 = r35
            r46 = r37
            r45 = r38
            r44 = r39
            r47 = r40
            r0.clear()
            r56 = r3
            r3 = r22
            r0.add(r3)
            r0.add(r2)
            r57 = r2
            r2 = r19
            r0.add(r2)
            r58 = r2
            r2 = r36
            r0.add(r2)
            r0.add(r4)
            r0.add(r5)
        L_0x053b:
            r59 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r60 = r3
            java.lang.String r3 = "info.getCountryID():"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSOperatorList(android.content.Context, int):java.util.List");
    }

    public static String getOperator(Context context) {
        return context.getResources().getStringArray(R.array.dvbc_operators_eu)[MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER)];
    }

    public static void setOperator(Context context, String operator) {
        String[] operats = context.getResources().getStringArray(R.array.dvbc_operators_eu);
        for (int i = 0; i < operats.length; i++) {
            if (operator.equalsIgnoreCase(operats[i])) {
                MtkLog.d("_EU", "setOperator()," + operator);
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER, i);
                return;
            }
        }
    }

    public void setOperator(CableOperator operator) {
        int operatorIndex;
        switch (operator) {
            case Comhem:
                operatorIndex = 2;
                break;
            case Canal_Digital:
                operatorIndex = 3;
                break;
            case TELE2:
                operatorIndex = 4;
                break;
            case OTHER:
                operatorIndex = 0;
                break;
            case Stofa:
                operatorIndex = 5;
                break;
            case Yousee:
                operatorIndex = 6;
                break;
            case GLENTEN:
                operatorIndex = 21;
                break;
            case Ziggo:
                operatorIndex = 7;
                break;
            case UPC:
                operatorIndex = 1;
                break;
            case RCS_RDS:
                operatorIndex = 23;
                break;
            case KDG:
                operatorIndex = 17;
                break;
            case Unitymedia:
                operatorIndex = 8;
                break;
            case TELECOLUMBUS:
                operatorIndex = 22;
                break;
            case Numericable:
                operatorIndex = 9;
                break;
            case TELNET:
                operatorIndex = 20;
                break;
            case AKADO:
                operatorIndex = 13;
                break;
            case ONLIME:
                operatorIndex = 12;
                break;
            case DIVAN_TV:
                operatorIndex = 15;
                break;
            case BLIZOO:
                operatorIndex = 19;
                break;
            case NET1:
                operatorIndex = 16;
                break;
            case TELEMACH:
                operatorIndex = 11;
                break;
            case VOLIA:
                operatorIndex = 10;
                break;
            case NULL:
                operatorIndex = 0;
                break;
            case TKT:
                operatorIndex = 14;
                break;
            case KBW:
                operatorIndex = 18;
                break;
            default:
                operatorIndex = 0;
                break;
        }
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER, operatorIndex);
    }

    public static String getCurrentOperatorStr(Context context) {
        CableOperator operator = getCurrentOperator();
        MtkLog.d("getCurrentOperatorStr()," + operator.name());
        return getOperatorStr(context, operator);
    }

    public static boolean isRCSRDSOp() {
        if (getCurrentOperator().equals(CableOperator.RCS_RDS)) {
            return true;
        }
        return false;
    }

    public static boolean isTELNETOp() {
        if (getCurrentOperator().equals(CableOperator.TELNET)) {
            return true;
        }
        return false;
    }

    public static CableOperator getCurrentOperator() {
        int operatorIndex = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER);
        CableOperator cableOperator = CableOperator.OTHER;
        switch (operatorIndex) {
            case 0:
                return CableOperator.OTHER;
            case 1:
                return CableOperator.UPC;
            case 2:
                return CableOperator.Comhem;
            case 3:
                return CableOperator.Canal_Digital;
            case 4:
                return CableOperator.TELE2;
            case 5:
                return CableOperator.Stofa;
            case 6:
                return CableOperator.Yousee;
            case 7:
                return CableOperator.Ziggo;
            case 8:
                return CableOperator.Unitymedia;
            case 9:
                return CableOperator.Numericable;
            case 10:
                return CableOperator.VOLIA;
            case 11:
                return CableOperator.TELEMACH;
            case 12:
                return CableOperator.ONLIME;
            case 13:
                return CableOperator.AKADO;
            case 14:
                return CableOperator.TKT;
            case 15:
                return CableOperator.DIVAN_TV;
            case 16:
                return CableOperator.NET1;
            case 17:
                return CableOperator.KDG;
            case 18:
                return CableOperator.KBW;
            case 19:
                return CableOperator.BLIZOO;
            case 20:
                return CableOperator.TELNET;
            case 21:
                return CableOperator.GLENTEN;
            case 22:
                return CableOperator.TELECOLUMBUS;
            case 23:
                return CableOperator.RCS_RDS;
            default:
                return CableOperator.OTHER;
        }
    }

    public static String getOperatorStr(Context context, CableOperator operator) {
        Context context2 = context;
        String upc = context2.getString(R.string.dvbc_operator_upc);
        String telent = context2.getString(R.string.dvbc_operator_telent_eu);
        String unitymedia = context2.getString(R.string.dvbc_operator_unitymedia);
        String stofa = context2.getString(R.string.dvbc_operator_stofa);
        String yousee = context2.getString(R.string.dvbc_operator_yousee);
        String canal_digital = context2.getString(R.string.dvbc_operator_canal_digital);
        String numericable = context2.getString(R.string.dvbc_operator_numericable);
        String ziggo = context2.getString(R.string.dvbc_operator_ziggo);
        String comhem = context2.getString(R.string.dvbc_operator_comhem);
        String others = context2.getString(R.string.menu_c_channelscan_oth);
        String glenten = context2.getString(R.string.dvbc_operator_glenten);
        String rcsrds = context2.getString(R.string.dvbc_operator_digi);
        String telecolumbus = context2.getString(R.string.dvbc_operator_tele_columbus);
        String tele2 = context2.getString(R.string.dvbc_operator_tele2);
        String volia = context2.getString(R.string.dvbc_operator_volia);
        String comhem2 = comhem;
        String telemach = context2.getString(R.string.dvbc_operator_telemach);
        String canal_digital2 = canal_digital;
        String onlime = context2.getString(R.string.dvbc_operator_onlime);
        String tele22 = tele2;
        String akado = context2.getString(R.string.dvbc_operator_akado);
        String others2 = others;
        String tkt = context2.getString(R.string.dvbc_operator_tkt);
        String stofa2 = stofa;
        String stofa3 = context2.getString(R.string.dvbc_operator_divan_tv);
        String yousee2 = yousee;
        String yousee3 = context2.getString(R.string.dvbc_operator_net1);
        String glenten2 = glenten;
        String glenten3 = context2.getString(R.string.dvbc_operator_kdg);
        String ziggo2 = ziggo;
        String kbw = context2.getString(R.string.dvbc_operator_kbw);
        String upc2 = upc;
        String upc3 = context2.getString(R.string.dvbc_operator_blizoo);
        String rcsrds2 = rcsrds;
        String rcsrds3 = context2.getString(R.string.dvbc_operator_voo);
        String kdg = glenten3;
        String kdghd = context2.getString(R.string.dvbc_operator_kdg_hd);
        String unitymedia2 = unitymedia;
        String teleing = context2.getString(R.string.dvbc_operator_teleing);
        String telecolumbus2 = telecolumbus;
        String krs = context2.getString(R.string.dvbc_operator_krs);
        String numericable2 = numericable;
        String mts = context2.getString(R.string.dvbc_operator_mts);
        String telent2 = telent;
        String tvoe_stp = context2.getString(R.string.dvbc_operator_tvoe_stp);
        String akado2 = akado;
        String akado3 = context2.getString(R.string.dvbc_operator_tvoe_eka);
        String onlime2 = onlime;
        String onlime3 = context2.getString(R.string.dvbc_operator_telekom);
        switch (operator) {
            case Comhem:
                return comhem2;
            case Canal_Digital:
                return canal_digital2;
            case TELE2:
                return tele22;
            case OTHER:
                return others2;
            case Stofa:
                return stofa2;
            case Yousee:
                return yousee2;
            case GLENTEN:
                return glenten2;
            case Ziggo:
                return ziggo2;
            case UPC:
                return upc2;
            case RCS_RDS:
                return rcsrds2;
            case KDG:
                return kdg;
            case Unitymedia:
                return unitymedia2;
            case TELECOLUMBUS:
                return telecolumbus2;
            case Numericable:
                return numericable2;
            case TELNET:
                return telent2;
            case AKADO:
                return akado2;
            case ONLIME:
                return onlime2;
            case TVOE_STP:
                return tvoe_stp;
            case DIVAN_TV:
                return stofa3;
            case BLIZOO:
                return upc3;
            case NET1:
                return yousee3;
            case TELEING:
                return teleing;
            case TELEMACH:
                return telemach;
            case VOLIA:
                return volia;
            case NULL:
                return "";
            case TKT:
                return tkt;
            case KBW:
                return kbw;
            case VOO:
                return rcsrds3;
            case KDG_HD:
                return kdghd;
            case KRS:
                return krs;
            case MTS:
                return mts;
            case TVOE_EKA:
                return akado3;
            case TELEKOM:
                return onlime3;
            default:
                return others2;
        }
    }

    public static List<CableOperator> convertStrOperator(Context context, List<String> operatorStrList) {
        String tvoe_eka;
        String tvoe_stp;
        String mts;
        String teleing;
        String krs;
        String kdghd;
        String voo;
        String blizoo;
        String kbw;
        String kdg;
        String net1;
        String divan_tv;
        String tkt;
        String akado;
        String onlime;
        String telemach;
        String volia;
        String tele2;
        String telecolumbus;
        String upc;
        String upc2;
        String telecolumbus2;
        String tele22;
        String volia2;
        String telemach2;
        String onlime2;
        String akado2;
        String tkt2;
        String divan_tv2;
        String net12;
        String kdg2;
        String kbw2;
        String blizoo2;
        String voo2;
        String kdghd2;
        String teleing2;
        String mts2;
        String tvoe_stp2;
        String tvoe_eka2;
        Context context2 = context;
        List<CableOperator> cableOperators = new ArrayList<>();
        String telekom = context2.getString(R.string.dvbc_operator_upc);
        String virginMedia = context2.getString(R.string.dvbc_operator_virgin_media);
        String telent = context2.getString(R.string.dvbc_operator_telent_eu);
        String unitymedia = context2.getString(R.string.dvbc_operator_unitymedia);
        String stofa = context2.getString(R.string.dvbc_operator_stofa);
        String yousee = context2.getString(R.string.dvbc_operator_yousee);
        String canal_digital = context2.getString(R.string.dvbc_operator_canal_digital);
        String numericable = context2.getString(R.string.dvbc_operator_numericable);
        String ziggo = context2.getString(R.string.dvbc_operator_ziggo);
        String comhem = context2.getString(R.string.dvbc_operator_comhem);
        String others = context2.getString(R.string.menu_c_channelscan_oth);
        String glenten = context2.getString(R.string.dvbc_operator_glenten);
        String rcsrds = context2.getString(R.string.dvbc_operator_digi);
        String telecolumbus3 = context2.getString(R.string.dvbc_operator_tele_columbus);
        String tele23 = context2.getString(R.string.dvbc_operator_tele2);
        String volia3 = context2.getString(R.string.dvbc_operator_volia);
        String telemach3 = context2.getString(R.string.dvbc_operator_telemach);
        String onlime3 = context2.getString(R.string.dvbc_operator_onlime);
        String akado3 = context2.getString(R.string.dvbc_operator_akado);
        String tkt3 = context2.getString(R.string.dvbc_operator_tkt);
        String divan_tv3 = context2.getString(R.string.dvbc_operator_divan_tv);
        String net13 = context2.getString(R.string.dvbc_operator_net1);
        String kdg3 = context2.getString(R.string.dvbc_operator_kdg);
        String kbw3 = context2.getString(R.string.dvbc_operator_kbw);
        String blizoo3 = context2.getString(R.string.dvbc_operator_blizoo);
        String voo3 = context2.getString(R.string.dvbc_operator_voo);
        String kdghd3 = context2.getString(R.string.dvbc_operator_kdg_hd);
        String teleing3 = context2.getString(R.string.dvbc_operator_teleing);
        String krs2 = context2.getString(R.string.dvbc_operator_krs);
        String mts3 = context2.getString(R.string.dvbc_operator_mts);
        String tvoe_stp3 = context2.getString(R.string.dvbc_operator_tvoe_stp);
        String tvoe_eka3 = context2.getString(R.string.dvbc_operator_tvoe_eka);
        String telekom2 = context2.getString(R.string.dvbc_operator_telekom);
        int i = 0;
        while (true) {
            String telekom3 = telekom2;
            int i2 = i;
            if (i2 < operatorStrList.size()) {
                String operatorName = operatorStrList.get(i2);
                if (operatorName.equalsIgnoreCase(telekom)) {
                    upc = telekom;
                    telecolumbus2 = telecolumbus3;
                    tele22 = tele23;
                    volia2 = volia3;
                    telemach2 = telemach3;
                    onlime2 = onlime3;
                    akado2 = akado3;
                    tkt2 = tkt3;
                    divan_tv2 = divan_tv3;
                    net12 = net13;
                    kdg2 = kdg3;
                    kbw2 = kbw3;
                    blizoo2 = blizoo3;
                    voo2 = voo3;
                    kdghd2 = kdghd3;
                    teleing2 = teleing3;
                    krs = krs2;
                    mts2 = mts3;
                    tvoe_stp2 = tvoe_stp3;
                    tvoe_eka2 = tvoe_eka3;
                    upc2 = telekom3;
                } else if (operatorName.equalsIgnoreCase(virginMedia)) {
                    String str = operatorName;
                    upc = telekom;
                    telecolumbus2 = telecolumbus3;
                    tele22 = tele23;
                    volia2 = volia3;
                    telemach2 = telemach3;
                    onlime2 = onlime3;
                    akado2 = akado3;
                    tkt2 = tkt3;
                    divan_tv2 = divan_tv3;
                    net12 = net13;
                    kdg2 = kdg3;
                    kbw2 = kbw3;
                    blizoo2 = blizoo3;
                    voo2 = voo3;
                    kdghd2 = kdghd3;
                    teleing2 = teleing3;
                    krs = krs2;
                    mts2 = mts3;
                    tvoe_stp2 = tvoe_stp3;
                    tvoe_eka2 = tvoe_eka3;
                    upc2 = telekom3;
                } else {
                    if (operatorName.equalsIgnoreCase(telent)) {
                        upc = telekom;
                        cableOperators.add(CableOperator.TELNET);
                    } else {
                        upc = telekom;
                        if (operatorName.equalsIgnoreCase(unitymedia)) {
                            cableOperators.add(CableOperator.Unitymedia);
                        } else if (operatorName.equalsIgnoreCase(stofa)) {
                            cableOperators.add(CableOperator.Stofa);
                        } else if (operatorName.equalsIgnoreCase(yousee)) {
                            cableOperators.add(CableOperator.Yousee);
                        } else if (operatorName.equalsIgnoreCase(canal_digital)) {
                            cableOperators.add(CableOperator.Canal_Digital);
                        } else if (operatorName.equalsIgnoreCase(numericable)) {
                            cableOperators.add(CableOperator.Numericable);
                        } else if (operatorName.equalsIgnoreCase(ziggo)) {
                            cableOperators.add(CableOperator.Ziggo);
                        } else if (operatorName.equalsIgnoreCase(comhem)) {
                            cableOperators.add(CableOperator.Comhem);
                        } else if (operatorName.equalsIgnoreCase(others)) {
                            cableOperators.add(CableOperator.OTHER);
                        } else if (operatorName.equalsIgnoreCase(glenten)) {
                            cableOperators.add(CableOperator.GLENTEN);
                        } else if (operatorName.equalsIgnoreCase(rcsrds)) {
                            cableOperators.add(CableOperator.RCS_RDS);
                        } else {
                            String telecolumbus4 = telecolumbus3;
                            if (operatorName.equalsIgnoreCase(telecolumbus4)) {
                                telecolumbus = telecolumbus4;
                                cableOperators.add(CableOperator.TELECOLUMBUS);
                                String str2 = operatorName;
                                tele2 = tele23;
                                volia = volia3;
                                telemach = telemach3;
                                onlime = onlime3;
                                akado = akado3;
                                tkt = tkt3;
                                divan_tv = divan_tv3;
                                net1 = net13;
                                kdg = kdg3;
                                kbw = kbw3;
                                blizoo = blizoo3;
                                voo = voo3;
                                kdghd = kdghd3;
                                teleing = teleing3;
                                krs = krs2;
                                mts = mts3;
                                tvoe_stp = tvoe_stp3;
                                tvoe_eka = tvoe_eka3;
                                upc2 = telekom3;
                                i = i2 + 1;
                                telekom2 = upc2;
                                telekom = upc;
                                telecolumbus3 = telecolumbus;
                                tele23 = tele2;
                                volia3 = volia;
                                telemach3 = telemach;
                                onlime3 = onlime;
                                akado3 = akado;
                                tkt3 = tkt;
                                divan_tv3 = divan_tv;
                                net13 = net1;
                                kdg3 = kdg;
                                kbw3 = kbw;
                                blizoo3 = blizoo;
                                voo3 = voo;
                                kdghd3 = kdghd;
                                krs2 = krs;
                                teleing3 = teleing;
                                mts3 = mts;
                                tvoe_stp3 = tvoe_stp;
                                tvoe_eka3 = tvoe_eka;
                                Context context3 = context;
                            } else {
                                telecolumbus = telecolumbus4;
                                String tele24 = tele23;
                                if (operatorName.equalsIgnoreCase(tele24)) {
                                    tele2 = tele24;
                                    cableOperators.add(CableOperator.TELE2);
                                    String str3 = operatorName;
                                    volia = volia3;
                                    telemach = telemach3;
                                    onlime = onlime3;
                                    akado = akado3;
                                    tkt = tkt3;
                                    divan_tv = divan_tv3;
                                    net1 = net13;
                                    kdg = kdg3;
                                    kbw = kbw3;
                                    blizoo = blizoo3;
                                    voo = voo3;
                                    kdghd = kdghd3;
                                    teleing = teleing3;
                                    krs = krs2;
                                    mts = mts3;
                                    tvoe_stp = tvoe_stp3;
                                    tvoe_eka = tvoe_eka3;
                                    upc2 = telekom3;
                                    i = i2 + 1;
                                    telekom2 = upc2;
                                    telekom = upc;
                                    telecolumbus3 = telecolumbus;
                                    tele23 = tele2;
                                    volia3 = volia;
                                    telemach3 = telemach;
                                    onlime3 = onlime;
                                    akado3 = akado;
                                    tkt3 = tkt;
                                    divan_tv3 = divan_tv;
                                    net13 = net1;
                                    kdg3 = kdg;
                                    kbw3 = kbw;
                                    blizoo3 = blizoo;
                                    voo3 = voo;
                                    kdghd3 = kdghd;
                                    krs2 = krs;
                                    teleing3 = teleing;
                                    mts3 = mts;
                                    tvoe_stp3 = tvoe_stp;
                                    tvoe_eka3 = tvoe_eka;
                                    Context context32 = context;
                                } else {
                                    tele2 = tele24;
                                    String tele25 = volia3;
                                    if (operatorName.equalsIgnoreCase(tele25)) {
                                        volia = tele25;
                                        cableOperators.add(CableOperator.VOLIA);
                                        String str4 = operatorName;
                                        telemach = telemach3;
                                        onlime = onlime3;
                                        akado = akado3;
                                        tkt = tkt3;
                                        divan_tv = divan_tv3;
                                        net1 = net13;
                                        kdg = kdg3;
                                        kbw = kbw3;
                                        blizoo = blizoo3;
                                        voo = voo3;
                                        kdghd = kdghd3;
                                        teleing = teleing3;
                                        krs = krs2;
                                        mts = mts3;
                                        tvoe_stp = tvoe_stp3;
                                        tvoe_eka = tvoe_eka3;
                                        upc2 = telekom3;
                                        i = i2 + 1;
                                        telekom2 = upc2;
                                        telekom = upc;
                                        telecolumbus3 = telecolumbus;
                                        tele23 = tele2;
                                        volia3 = volia;
                                        telemach3 = telemach;
                                        onlime3 = onlime;
                                        akado3 = akado;
                                        tkt3 = tkt;
                                        divan_tv3 = divan_tv;
                                        net13 = net1;
                                        kdg3 = kdg;
                                        kbw3 = kbw;
                                        blizoo3 = blizoo;
                                        voo3 = voo;
                                        kdghd3 = kdghd;
                                        krs2 = krs;
                                        teleing3 = teleing;
                                        mts3 = mts;
                                        tvoe_stp3 = tvoe_stp;
                                        tvoe_eka3 = tvoe_eka;
                                        Context context322 = context;
                                    } else {
                                        volia = tele25;
                                        String telemach4 = telemach3;
                                        if (operatorName.equalsIgnoreCase(telemach4)) {
                                            telemach = telemach4;
                                            cableOperators.add(CableOperator.TELEMACH);
                                            String str5 = operatorName;
                                            onlime = onlime3;
                                            akado = akado3;
                                            tkt = tkt3;
                                            divan_tv = divan_tv3;
                                            net1 = net13;
                                            kdg = kdg3;
                                            kbw = kbw3;
                                            blizoo = blizoo3;
                                            voo = voo3;
                                            kdghd = kdghd3;
                                            teleing = teleing3;
                                            krs = krs2;
                                            mts = mts3;
                                            tvoe_stp = tvoe_stp3;
                                            tvoe_eka = tvoe_eka3;
                                            upc2 = telekom3;
                                            i = i2 + 1;
                                            telekom2 = upc2;
                                            telekom = upc;
                                            telecolumbus3 = telecolumbus;
                                            tele23 = tele2;
                                            volia3 = volia;
                                            telemach3 = telemach;
                                            onlime3 = onlime;
                                            akado3 = akado;
                                            tkt3 = tkt;
                                            divan_tv3 = divan_tv;
                                            net13 = net1;
                                            kdg3 = kdg;
                                            kbw3 = kbw;
                                            blizoo3 = blizoo;
                                            voo3 = voo;
                                            kdghd3 = kdghd;
                                            krs2 = krs;
                                            teleing3 = teleing;
                                            mts3 = mts;
                                            tvoe_stp3 = tvoe_stp;
                                            tvoe_eka3 = tvoe_eka;
                                            Context context3222 = context;
                                        } else {
                                            telemach = telemach4;
                                            String onlime4 = onlime3;
                                            if (operatorName.equalsIgnoreCase(onlime4)) {
                                                onlime = onlime4;
                                                cableOperators.add(CableOperator.ONLIME);
                                                String str6 = operatorName;
                                                akado = akado3;
                                                tkt = tkt3;
                                                divan_tv = divan_tv3;
                                                net1 = net13;
                                                kdg = kdg3;
                                                kbw = kbw3;
                                                blizoo = blizoo3;
                                                voo = voo3;
                                                kdghd = kdghd3;
                                                teleing = teleing3;
                                                krs = krs2;
                                                mts = mts3;
                                                tvoe_stp = tvoe_stp3;
                                                tvoe_eka = tvoe_eka3;
                                                upc2 = telekom3;
                                                i = i2 + 1;
                                                telekom2 = upc2;
                                                telekom = upc;
                                                telecolumbus3 = telecolumbus;
                                                tele23 = tele2;
                                                volia3 = volia;
                                                telemach3 = telemach;
                                                onlime3 = onlime;
                                                akado3 = akado;
                                                tkt3 = tkt;
                                                divan_tv3 = divan_tv;
                                                net13 = net1;
                                                kdg3 = kdg;
                                                kbw3 = kbw;
                                                blizoo3 = blizoo;
                                                voo3 = voo;
                                                kdghd3 = kdghd;
                                                krs2 = krs;
                                                teleing3 = teleing;
                                                mts3 = mts;
                                                tvoe_stp3 = tvoe_stp;
                                                tvoe_eka3 = tvoe_eka;
                                                Context context32222 = context;
                                            } else {
                                                onlime = onlime4;
                                                String akado4 = akado3;
                                                if (operatorName.equalsIgnoreCase(akado4)) {
                                                    akado = akado4;
                                                    cableOperators.add(CableOperator.AKADO);
                                                    String str7 = operatorName;
                                                    tkt = tkt3;
                                                    divan_tv = divan_tv3;
                                                    net1 = net13;
                                                    kdg = kdg3;
                                                    kbw = kbw3;
                                                    blizoo = blizoo3;
                                                    voo = voo3;
                                                    kdghd = kdghd3;
                                                    teleing = teleing3;
                                                    krs = krs2;
                                                    mts = mts3;
                                                    tvoe_stp = tvoe_stp3;
                                                    tvoe_eka = tvoe_eka3;
                                                    upc2 = telekom3;
                                                    i = i2 + 1;
                                                    telekom2 = upc2;
                                                    telekom = upc;
                                                    telecolumbus3 = telecolumbus;
                                                    tele23 = tele2;
                                                    volia3 = volia;
                                                    telemach3 = telemach;
                                                    onlime3 = onlime;
                                                    akado3 = akado;
                                                    tkt3 = tkt;
                                                    divan_tv3 = divan_tv;
                                                    net13 = net1;
                                                    kdg3 = kdg;
                                                    kbw3 = kbw;
                                                    blizoo3 = blizoo;
                                                    voo3 = voo;
                                                    kdghd3 = kdghd;
                                                    krs2 = krs;
                                                    teleing3 = teleing;
                                                    mts3 = mts;
                                                    tvoe_stp3 = tvoe_stp;
                                                    tvoe_eka3 = tvoe_eka;
                                                    Context context322222 = context;
                                                } else {
                                                    akado = akado4;
                                                    String tkt4 = tkt3;
                                                    if (operatorName.equalsIgnoreCase(tkt4)) {
                                                        tkt = tkt4;
                                                        cableOperators.add(CableOperator.TKT);
                                                        String str8 = operatorName;
                                                        divan_tv = divan_tv3;
                                                        net1 = net13;
                                                        kdg = kdg3;
                                                        kbw = kbw3;
                                                        blizoo = blizoo3;
                                                        voo = voo3;
                                                        kdghd = kdghd3;
                                                        teleing = teleing3;
                                                        krs = krs2;
                                                        mts = mts3;
                                                        tvoe_stp = tvoe_stp3;
                                                        tvoe_eka = tvoe_eka3;
                                                        upc2 = telekom3;
                                                        i = i2 + 1;
                                                        telekom2 = upc2;
                                                        telekom = upc;
                                                        telecolumbus3 = telecolumbus;
                                                        tele23 = tele2;
                                                        volia3 = volia;
                                                        telemach3 = telemach;
                                                        onlime3 = onlime;
                                                        akado3 = akado;
                                                        tkt3 = tkt;
                                                        divan_tv3 = divan_tv;
                                                        net13 = net1;
                                                        kdg3 = kdg;
                                                        kbw3 = kbw;
                                                        blizoo3 = blizoo;
                                                        voo3 = voo;
                                                        kdghd3 = kdghd;
                                                        krs2 = krs;
                                                        teleing3 = teleing;
                                                        mts3 = mts;
                                                        tvoe_stp3 = tvoe_stp;
                                                        tvoe_eka3 = tvoe_eka;
                                                        Context context3222222 = context;
                                                    } else {
                                                        tkt = tkt4;
                                                        String tkt5 = divan_tv3;
                                                        if (operatorName.equalsIgnoreCase(tkt5)) {
                                                            divan_tv = tkt5;
                                                            cableOperators.add(CableOperator.DIVAN_TV);
                                                            String str9 = operatorName;
                                                            net1 = net13;
                                                            kdg = kdg3;
                                                            kbw = kbw3;
                                                            blizoo = blizoo3;
                                                            voo = voo3;
                                                            kdghd = kdghd3;
                                                            teleing = teleing3;
                                                            krs = krs2;
                                                            mts = mts3;
                                                            tvoe_stp = tvoe_stp3;
                                                            tvoe_eka = tvoe_eka3;
                                                            upc2 = telekom3;
                                                            i = i2 + 1;
                                                            telekom2 = upc2;
                                                            telekom = upc;
                                                            telecolumbus3 = telecolumbus;
                                                            tele23 = tele2;
                                                            volia3 = volia;
                                                            telemach3 = telemach;
                                                            onlime3 = onlime;
                                                            akado3 = akado;
                                                            tkt3 = tkt;
                                                            divan_tv3 = divan_tv;
                                                            net13 = net1;
                                                            kdg3 = kdg;
                                                            kbw3 = kbw;
                                                            blizoo3 = blizoo;
                                                            voo3 = voo;
                                                            kdghd3 = kdghd;
                                                            krs2 = krs;
                                                            teleing3 = teleing;
                                                            mts3 = mts;
                                                            tvoe_stp3 = tvoe_stp;
                                                            tvoe_eka3 = tvoe_eka;
                                                            Context context32222222 = context;
                                                        } else {
                                                            divan_tv = tkt5;
                                                            String divan_tv4 = net13;
                                                            if (operatorName.equalsIgnoreCase(divan_tv4)) {
                                                                net1 = divan_tv4;
                                                                cableOperators.add(CableOperator.NET1);
                                                                String str10 = operatorName;
                                                                kdg = kdg3;
                                                                kbw = kbw3;
                                                                blizoo = blizoo3;
                                                                voo = voo3;
                                                                kdghd = kdghd3;
                                                                teleing = teleing3;
                                                                krs = krs2;
                                                                mts = mts3;
                                                                tvoe_stp = tvoe_stp3;
                                                                tvoe_eka = tvoe_eka3;
                                                                upc2 = telekom3;
                                                                i = i2 + 1;
                                                                telekom2 = upc2;
                                                                telekom = upc;
                                                                telecolumbus3 = telecolumbus;
                                                                tele23 = tele2;
                                                                volia3 = volia;
                                                                telemach3 = telemach;
                                                                onlime3 = onlime;
                                                                akado3 = akado;
                                                                tkt3 = tkt;
                                                                divan_tv3 = divan_tv;
                                                                net13 = net1;
                                                                kdg3 = kdg;
                                                                kbw3 = kbw;
                                                                blizoo3 = blizoo;
                                                                voo3 = voo;
                                                                kdghd3 = kdghd;
                                                                krs2 = krs;
                                                                teleing3 = teleing;
                                                                mts3 = mts;
                                                                tvoe_stp3 = tvoe_stp;
                                                                tvoe_eka3 = tvoe_eka;
                                                                Context context322222222 = context;
                                                            } else {
                                                                net1 = divan_tv4;
                                                                String kdg4 = kdg3;
                                                                if (operatorName.equalsIgnoreCase(kdg4)) {
                                                                    kdg = kdg4;
                                                                    cableOperators.add(CableOperator.KDG);
                                                                    String str11 = operatorName;
                                                                    kbw = kbw3;
                                                                    blizoo = blizoo3;
                                                                    voo = voo3;
                                                                    kdghd = kdghd3;
                                                                    teleing = teleing3;
                                                                    krs = krs2;
                                                                    mts = mts3;
                                                                    tvoe_stp = tvoe_stp3;
                                                                    tvoe_eka = tvoe_eka3;
                                                                    upc2 = telekom3;
                                                                    i = i2 + 1;
                                                                    telekom2 = upc2;
                                                                    telekom = upc;
                                                                    telecolumbus3 = telecolumbus;
                                                                    tele23 = tele2;
                                                                    volia3 = volia;
                                                                    telemach3 = telemach;
                                                                    onlime3 = onlime;
                                                                    akado3 = akado;
                                                                    tkt3 = tkt;
                                                                    divan_tv3 = divan_tv;
                                                                    net13 = net1;
                                                                    kdg3 = kdg;
                                                                    kbw3 = kbw;
                                                                    blizoo3 = blizoo;
                                                                    voo3 = voo;
                                                                    kdghd3 = kdghd;
                                                                    krs2 = krs;
                                                                    teleing3 = teleing;
                                                                    mts3 = mts;
                                                                    tvoe_stp3 = tvoe_stp;
                                                                    tvoe_eka3 = tvoe_eka;
                                                                    Context context3222222222 = context;
                                                                } else {
                                                                    kdg = kdg4;
                                                                    String kbw4 = kbw3;
                                                                    if (operatorName.equalsIgnoreCase(kbw4)) {
                                                                        kbw = kbw4;
                                                                        cableOperators.add(CableOperator.KBW);
                                                                        String str12 = operatorName;
                                                                        blizoo = blizoo3;
                                                                        voo = voo3;
                                                                        kdghd = kdghd3;
                                                                        teleing = teleing3;
                                                                        krs = krs2;
                                                                        mts = mts3;
                                                                        tvoe_stp = tvoe_stp3;
                                                                        tvoe_eka = tvoe_eka3;
                                                                        upc2 = telekom3;
                                                                        i = i2 + 1;
                                                                        telekom2 = upc2;
                                                                        telekom = upc;
                                                                        telecolumbus3 = telecolumbus;
                                                                        tele23 = tele2;
                                                                        volia3 = volia;
                                                                        telemach3 = telemach;
                                                                        onlime3 = onlime;
                                                                        akado3 = akado;
                                                                        tkt3 = tkt;
                                                                        divan_tv3 = divan_tv;
                                                                        net13 = net1;
                                                                        kdg3 = kdg;
                                                                        kbw3 = kbw;
                                                                        blizoo3 = blizoo;
                                                                        voo3 = voo;
                                                                        kdghd3 = kdghd;
                                                                        krs2 = krs;
                                                                        teleing3 = teleing;
                                                                        mts3 = mts;
                                                                        tvoe_stp3 = tvoe_stp;
                                                                        tvoe_eka3 = tvoe_eka;
                                                                        Context context32222222222 = context;
                                                                    } else {
                                                                        kbw = kbw4;
                                                                        String kbw5 = blizoo3;
                                                                        if (operatorName.equalsIgnoreCase(kbw5)) {
                                                                            blizoo = kbw5;
                                                                            cableOperators.add(CableOperator.BLIZOO);
                                                                            String str13 = operatorName;
                                                                            voo = voo3;
                                                                            kdghd = kdghd3;
                                                                            teleing = teleing3;
                                                                            krs = krs2;
                                                                            mts = mts3;
                                                                            tvoe_stp = tvoe_stp3;
                                                                            tvoe_eka = tvoe_eka3;
                                                                            upc2 = telekom3;
                                                                            i = i2 + 1;
                                                                            telekom2 = upc2;
                                                                            telekom = upc;
                                                                            telecolumbus3 = telecolumbus;
                                                                            tele23 = tele2;
                                                                            volia3 = volia;
                                                                            telemach3 = telemach;
                                                                            onlime3 = onlime;
                                                                            akado3 = akado;
                                                                            tkt3 = tkt;
                                                                            divan_tv3 = divan_tv;
                                                                            net13 = net1;
                                                                            kdg3 = kdg;
                                                                            kbw3 = kbw;
                                                                            blizoo3 = blizoo;
                                                                            voo3 = voo;
                                                                            kdghd3 = kdghd;
                                                                            krs2 = krs;
                                                                            teleing3 = teleing;
                                                                            mts3 = mts;
                                                                            tvoe_stp3 = tvoe_stp;
                                                                            tvoe_eka3 = tvoe_eka;
                                                                            Context context322222222222 = context;
                                                                        } else {
                                                                            blizoo = kbw5;
                                                                            String voo4 = voo3;
                                                                            if (operatorName.equalsIgnoreCase(voo4)) {
                                                                                voo = voo4;
                                                                                cableOperators.add(CableOperator.VOO);
                                                                                String str14 = operatorName;
                                                                                kdghd = kdghd3;
                                                                                teleing = teleing3;
                                                                                krs = krs2;
                                                                                mts = mts3;
                                                                                tvoe_stp = tvoe_stp3;
                                                                                tvoe_eka = tvoe_eka3;
                                                                                upc2 = telekom3;
                                                                                i = i2 + 1;
                                                                                telekom2 = upc2;
                                                                                telekom = upc;
                                                                                telecolumbus3 = telecolumbus;
                                                                                tele23 = tele2;
                                                                                volia3 = volia;
                                                                                telemach3 = telemach;
                                                                                onlime3 = onlime;
                                                                                akado3 = akado;
                                                                                tkt3 = tkt;
                                                                                divan_tv3 = divan_tv;
                                                                                net13 = net1;
                                                                                kdg3 = kdg;
                                                                                kbw3 = kbw;
                                                                                blizoo3 = blizoo;
                                                                                voo3 = voo;
                                                                                kdghd3 = kdghd;
                                                                                krs2 = krs;
                                                                                teleing3 = teleing;
                                                                                mts3 = mts;
                                                                                tvoe_stp3 = tvoe_stp;
                                                                                tvoe_eka3 = tvoe_eka;
                                                                                Context context3222222222222 = context;
                                                                            } else {
                                                                                voo = voo4;
                                                                                String kdghd4 = kdghd3;
                                                                                if (operatorName.equalsIgnoreCase(kdghd4)) {
                                                                                    kdghd = kdghd4;
                                                                                    cableOperators.add(CableOperator.KDG_HD);
                                                                                    String str15 = operatorName;
                                                                                    teleing = teleing3;
                                                                                    krs = krs2;
                                                                                    mts = mts3;
                                                                                    tvoe_stp = tvoe_stp3;
                                                                                    tvoe_eka = tvoe_eka3;
                                                                                    upc2 = telekom3;
                                                                                    i = i2 + 1;
                                                                                    telekom2 = upc2;
                                                                                    telekom = upc;
                                                                                    telecolumbus3 = telecolumbus;
                                                                                    tele23 = tele2;
                                                                                    volia3 = volia;
                                                                                    telemach3 = telemach;
                                                                                    onlime3 = onlime;
                                                                                    akado3 = akado;
                                                                                    tkt3 = tkt;
                                                                                    divan_tv3 = divan_tv;
                                                                                    net13 = net1;
                                                                                    kdg3 = kdg;
                                                                                    kbw3 = kbw;
                                                                                    blizoo3 = blizoo;
                                                                                    voo3 = voo;
                                                                                    kdghd3 = kdghd;
                                                                                    krs2 = krs;
                                                                                    teleing3 = teleing;
                                                                                    mts3 = mts;
                                                                                    tvoe_stp3 = tvoe_stp;
                                                                                    tvoe_eka3 = tvoe_eka;
                                                                                    Context context32222222222222 = context;
                                                                                } else {
                                                                                    kdghd = kdghd4;
                                                                                    String krs3 = krs2;
                                                                                    if (operatorName.equalsIgnoreCase(krs3)) {
                                                                                        krs = krs3;
                                                                                        cableOperators.add(CableOperator.KRS);
                                                                                        String str16 = operatorName;
                                                                                        teleing = teleing3;
                                                                                    } else {
                                                                                        krs = krs3;
                                                                                        String teleing4 = teleing3;
                                                                                        if (operatorName.equalsIgnoreCase(teleing4)) {
                                                                                            teleing = teleing4;
                                                                                            cableOperators.add(CableOperator.TELEING);
                                                                                            String str17 = operatorName;
                                                                                        } else {
                                                                                            teleing = teleing4;
                                                                                            String mts4 = mts3;
                                                                                            if (operatorName.equalsIgnoreCase(mts4)) {
                                                                                                mts = mts4;
                                                                                                cableOperators.add(CableOperator.MTS);
                                                                                                String str18 = operatorName;
                                                                                                tvoe_stp = tvoe_stp3;
                                                                                                tvoe_eka = tvoe_eka3;
                                                                                                upc2 = telekom3;
                                                                                                i = i2 + 1;
                                                                                                telekom2 = upc2;
                                                                                                telekom = upc;
                                                                                                telecolumbus3 = telecolumbus;
                                                                                                tele23 = tele2;
                                                                                                volia3 = volia;
                                                                                                telemach3 = telemach;
                                                                                                onlime3 = onlime;
                                                                                                akado3 = akado;
                                                                                                tkt3 = tkt;
                                                                                                divan_tv3 = divan_tv;
                                                                                                net13 = net1;
                                                                                                kdg3 = kdg;
                                                                                                kbw3 = kbw;
                                                                                                blizoo3 = blizoo;
                                                                                                voo3 = voo;
                                                                                                kdghd3 = kdghd;
                                                                                                krs2 = krs;
                                                                                                teleing3 = teleing;
                                                                                                mts3 = mts;
                                                                                                tvoe_stp3 = tvoe_stp;
                                                                                                tvoe_eka3 = tvoe_eka;
                                                                                                Context context322222222222222 = context;
                                                                                            } else {
                                                                                                mts = mts4;
                                                                                                String tvoe_stp4 = tvoe_stp3;
                                                                                                if (operatorName.equalsIgnoreCase(tvoe_stp4)) {
                                                                                                    tvoe_stp = tvoe_stp4;
                                                                                                    cableOperators.add(CableOperator.TVOE_STP);
                                                                                                    String str19 = operatorName;
                                                                                                    tvoe_eka = tvoe_eka3;
                                                                                                    upc2 = telekom3;
                                                                                                    i = i2 + 1;
                                                                                                    telekom2 = upc2;
                                                                                                    telekom = upc;
                                                                                                    telecolumbus3 = telecolumbus;
                                                                                                    tele23 = tele2;
                                                                                                    volia3 = volia;
                                                                                                    telemach3 = telemach;
                                                                                                    onlime3 = onlime;
                                                                                                    akado3 = akado;
                                                                                                    tkt3 = tkt;
                                                                                                    divan_tv3 = divan_tv;
                                                                                                    net13 = net1;
                                                                                                    kdg3 = kdg;
                                                                                                    kbw3 = kbw;
                                                                                                    blizoo3 = blizoo;
                                                                                                    voo3 = voo;
                                                                                                    kdghd3 = kdghd;
                                                                                                    krs2 = krs;
                                                                                                    teleing3 = teleing;
                                                                                                    mts3 = mts;
                                                                                                    tvoe_stp3 = tvoe_stp;
                                                                                                    tvoe_eka3 = tvoe_eka;
                                                                                                    Context context3222222222222222 = context;
                                                                                                } else {
                                                                                                    tvoe_stp = tvoe_stp4;
                                                                                                    String tvoe_eka4 = tvoe_eka3;
                                                                                                    if (operatorName.equalsIgnoreCase(tvoe_eka4)) {
                                                                                                        tvoe_eka = tvoe_eka4;
                                                                                                        cableOperators.add(CableOperator.TVOE_EKA);
                                                                                                        String str20 = operatorName;
                                                                                                        upc2 = telekom3;
                                                                                                        i = i2 + 1;
                                                                                                        telekom2 = upc2;
                                                                                                        telekom = upc;
                                                                                                        telecolumbus3 = telecolumbus;
                                                                                                        tele23 = tele2;
                                                                                                        volia3 = volia;
                                                                                                        telemach3 = telemach;
                                                                                                        onlime3 = onlime;
                                                                                                        akado3 = akado;
                                                                                                        tkt3 = tkt;
                                                                                                        divan_tv3 = divan_tv;
                                                                                                        net13 = net1;
                                                                                                        kdg3 = kdg;
                                                                                                        kbw3 = kbw;
                                                                                                        blizoo3 = blizoo;
                                                                                                        voo3 = voo;
                                                                                                        kdghd3 = kdghd;
                                                                                                        krs2 = krs;
                                                                                                        teleing3 = teleing;
                                                                                                        mts3 = mts;
                                                                                                        tvoe_stp3 = tvoe_stp;
                                                                                                        tvoe_eka3 = tvoe_eka;
                                                                                                        Context context32222222222222222 = context;
                                                                                                    } else {
                                                                                                        tvoe_eka = tvoe_eka4;
                                                                                                        upc2 = telekom3;
                                                                                                        if (operatorName.equalsIgnoreCase(upc2)) {
                                                                                                            String str21 = operatorName;
                                                                                                            cableOperators.add(CableOperator.TELEKOM);
                                                                                                        }
                                                                                                        i = i2 + 1;
                                                                                                        telekom2 = upc2;
                                                                                                        telekom = upc;
                                                                                                        telecolumbus3 = telecolumbus;
                                                                                                        tele23 = tele2;
                                                                                                        volia3 = volia;
                                                                                                        telemach3 = telemach;
                                                                                                        onlime3 = onlime;
                                                                                                        akado3 = akado;
                                                                                                        tkt3 = tkt;
                                                                                                        divan_tv3 = divan_tv;
                                                                                                        net13 = net1;
                                                                                                        kdg3 = kdg;
                                                                                                        kbw3 = kbw;
                                                                                                        blizoo3 = blizoo;
                                                                                                        voo3 = voo;
                                                                                                        kdghd3 = kdghd;
                                                                                                        krs2 = krs;
                                                                                                        teleing3 = teleing;
                                                                                                        mts3 = mts;
                                                                                                        tvoe_stp3 = tvoe_stp;
                                                                                                        tvoe_eka3 = tvoe_eka;
                                                                                                        Context context322222222222222222 = context;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    mts = mts3;
                                                                                    tvoe_stp = tvoe_stp3;
                                                                                    tvoe_eka = tvoe_eka3;
                                                                                    upc2 = telekom3;
                                                                                    i = i2 + 1;
                                                                                    telekom2 = upc2;
                                                                                    telekom = upc;
                                                                                    telecolumbus3 = telecolumbus;
                                                                                    tele23 = tele2;
                                                                                    volia3 = volia;
                                                                                    telemach3 = telemach;
                                                                                    onlime3 = onlime;
                                                                                    akado3 = akado;
                                                                                    tkt3 = tkt;
                                                                                    divan_tv3 = divan_tv;
                                                                                    net13 = net1;
                                                                                    kdg3 = kdg;
                                                                                    kbw3 = kbw;
                                                                                    blizoo3 = blizoo;
                                                                                    voo3 = voo;
                                                                                    kdghd3 = kdghd;
                                                                                    krs2 = krs;
                                                                                    teleing3 = teleing;
                                                                                    mts3 = mts;
                                                                                    tvoe_stp3 = tvoe_stp;
                                                                                    tvoe_eka3 = tvoe_eka;
                                                                                    Context context3222222222222222222 = context;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    telecolumbus = telecolumbus3;
                    tele2 = tele23;
                    volia = volia3;
                    telemach = telemach3;
                    onlime = onlime3;
                    akado = akado3;
                    tkt = tkt3;
                    divan_tv = divan_tv3;
                    net1 = net13;
                    kdg = kdg3;
                    kbw = kbw3;
                    blizoo = blizoo3;
                    voo = voo3;
                    kdghd = kdghd3;
                    teleing = teleing3;
                    krs = krs2;
                    mts = mts3;
                    tvoe_stp = tvoe_stp3;
                    tvoe_eka = tvoe_eka3;
                    upc2 = telekom3;
                    i = i2 + 1;
                    telekom2 = upc2;
                    telekom = upc;
                    telecolumbus3 = telecolumbus;
                    tele23 = tele2;
                    volia3 = volia;
                    telemach3 = telemach;
                    onlime3 = onlime;
                    akado3 = akado;
                    tkt3 = tkt;
                    divan_tv3 = divan_tv;
                    net13 = net1;
                    kdg3 = kdg;
                    kbw3 = kbw;
                    blizoo3 = blizoo;
                    voo3 = voo;
                    kdghd3 = kdghd;
                    krs2 = krs;
                    teleing3 = teleing;
                    mts3 = mts;
                    tvoe_stp3 = tvoe_stp;
                    tvoe_eka3 = tvoe_eka;
                    Context context32222222222222222222 = context;
                }
                cableOperators.add(CableOperator.UPC);
                i = i2 + 1;
                telekom2 = upc2;
                telekom = upc;
                telecolumbus3 = telecolumbus;
                tele23 = tele2;
                volia3 = volia;
                telemach3 = telemach;
                onlime3 = onlime;
                akado3 = akado;
                tkt3 = tkt;
                divan_tv3 = divan_tv;
                net13 = net1;
                kdg3 = kdg;
                kbw3 = kbw;
                blizoo3 = blizoo;
                voo3 = voo;
                kdghd3 = kdghd;
                krs2 = krs;
                teleing3 = teleing;
                mts3 = mts;
                tvoe_stp3 = tvoe_stp;
                tvoe_eka3 = tvoe_eka;
                Context context322222222222222222222 = context;
            } else {
                String upc3 = telekom;
                String str22 = telecolumbus3;
                String str23 = tele23;
                String str24 = volia3;
                String str25 = telemach3;
                String str26 = onlime3;
                String str27 = akado3;
                String str28 = tkt3;
                String str29 = divan_tv3;
                String str30 = net13;
                String str31 = kdg3;
                String str32 = kbw3;
                String str33 = blizoo3;
                String str34 = voo3;
                String str35 = kdghd3;
                String str36 = teleing3;
                String str37 = krs2;
                String str38 = mts3;
                String str39 = tvoe_stp3;
                String str40 = tvoe_eka3;
                String upc4 = telekom3;
                return cableOperators;
            }
        }
    }

    static List<String> getCableOperationList(Context context, int countryID) {
        Context context2 = context;
        int i = countryID;
        MtkLog.d("getCableOperationList():countryID: " + i);
        String upc = context2.getString(R.string.dvbc_operator_upc);
        String virginMedia = context2.getString(R.string.dvbc_operator_virgin_media);
        String telent = context2.getString(R.string.dvbc_operator_telent_eu);
        String unitymedia = context2.getString(R.string.dvbc_operator_unitymedia);
        String stofa = context2.getString(R.string.dvbc_operator_stofa);
        String yousee = context2.getString(R.string.dvbc_operator_yousee);
        String canal_digital = context2.getString(R.string.dvbc_operator_canal_digital);
        String numericable = context2.getString(R.string.dvbc_operator_numericable);
        String ziggo = context2.getString(R.string.dvbc_operator_ziggo);
        String comhem = context2.getString(R.string.dvbc_operator_comhem);
        String others = context2.getString(R.string.menu_c_channelscan_oth);
        String glenten = context2.getString(R.string.dvbc_operator_glenten);
        String rcsrds = context2.getString(R.string.dvbc_operator_digi);
        String telecolumbus = context2.getString(R.string.dvbc_operator_tele_columbus);
        String telent2 = telent;
        String voo = context2.getString(R.string.dvbc_operator_voo);
        String blizoo = context2.getString(R.string.dvbc_operator_blizoo);
        String telecolumbus2 = telecolumbus;
        String telecolumbus3 = context2.getString(R.string.dvbc_operator_net1);
        String unitymedia2 = unitymedia;
        String kdg = context2.getString(R.string.dvbc_operator_kdg);
        String akado = context2.getString(R.string.dvbc_operator_akado);
        String glenten2 = glenten;
        String glenten3 = context2.getString(R.string.dvbc_operator_divan_tv);
        String yousee2 = yousee;
        String onlime = context2.getString(R.string.dvbc_operator_onlime);
        String stofa2 = stofa;
        String teleing = context2.getString(R.string.dvbc_operator_teleing);
        String numericable2 = numericable;
        String telemach = context2.getString(R.string.dvbc_operator_telemach);
        String ziggo2 = ziggo;
        String tele2 = context2.getString(R.string.dvbc_operator_tele2);
        String volia = context2.getString(R.string.dvbc_operator_volia);
        String tvoe_stp = context2.getString(R.string.dvbc_operator_tvoe_stp);
        List<String> list = new LinkedList<>();
        String canal_digital2 = canal_digital;
        if (MarketRegionInfo.isFunctionSupport(3)) {
            switch (i) {
                case 30:
                case 31:
                case 32:
                    list.clear();
                    break;
            }
        } else {
            if (i != 36) {
                switch (i) {
                    case 1:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{upc, others}));
                        break;
                    case 2:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{telent2, voo, others}));
                        break;
                    case 3:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{upc, others}));
                        break;
                    case 4:
                        list.clear();
                        break;
                    case 5:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{unitymedia2, telecolumbus2, kdg, others}));
                        break;
                    case 6:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{stofa2, yousee2, canal_digital2, glenten2, others}));
                        break;
                    case 7:
                        list.clear();
                        break;
                    case 8:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{canal_digital2, others}));
                        break;
                    case 9:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{numericable2, others}));
                        break;
                    case 10:
                        list.clear();
                        break;
                    case 11:
                        list.clear();
                        break;
                    case 12:
                        list.clear();
                        break;
                    case 13:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{ziggo2, others}));
                        break;
                    case 14:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{canal_digital2}));
                        break;
                    case 15:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{comhem, canal_digital2, tele2, others}));
                        break;
                    case 16:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{blizoo, telecolumbus3, others}));
                        break;
                    case 17:
                        list.clear();
                        break;
                    case 18:
                        list.clear();
                        break;
                    case 19:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{upc, rcsrds, others}));
                        break;
                    case 20:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{virginMedia, others}));
                        break;
                    case 21:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{upc, others}));
                        break;
                    case 22:
                        list.clear();
                        break;
                    case 23:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{upc, rcsrds, others}));
                        break;
                    case 24:
                        list.clear();
                        String str = akado;
                        list = new LinkedList<>(Arrays.asList(new String[]{akado, glenten3, onlime, tvoe_stp, others}));
                        break;
                    case 25:
                        list.clear();
                        break;
                    case 26:
                        list.clear();
                        break;
                    case 27:
                        list.clear();
                        list = new LinkedList<>(Arrays.asList(new String[]{teleing, telemach, others}));
                        break;
                    case 28:
                        list.clear();
                        break;
                    case 29:
                        list.clear();
                        break;
                }
            } else {
                list.clear();
                list = new LinkedList<>(Arrays.asList(new String[]{volia, others}));
            }
            MtkLog.d("info.getCountryID():" + countryID);
            return list;
        }
        MtkLog.d("info.getCountryID():" + countryID);
        return list;
    }

    public static List<String> getScanMode(Context context) {
        new ArrayList();
        return Arrays.asList(initScanModesForOperator(context, getCurrentOperator(), (List<CableOperator>) null));
    }

    public static List<String> getDVBSScanMode(Context context) {
        List<String> list = new ArrayList<>();
        String networkScanMode = context.getResources().getString(R.string.dvbs_scan_mode_network);
        String fullScanMode = context.getResources().getString(R.string.dvbs_scan_mode_full);
        int op = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
        MtkLog.d("getDVBSScanMode(),op:" + op);
        if (op != 0) {
            list.add(networkScanMode);
        } else {
            list.add(fullScanMode);
            list.add(networkScanMode);
        }
        return list;
    }

    public static List<String> getDVBSConfigInfoChannels(Context context) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_channel_arrays));
    }

    public static List<String> getDVBSConfigInfoChannelStoreTypes(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_channel_story_type_arrays));
    }

    public static List<String> getDVBSFrequencyList(Context context, int antenaType, int tunerType) {
        List<String> list = new ArrayList<>();
        if (antenaType == 0) {
            return list;
        }
        return getSingleCableFreqsList(context, tunerType);
    }

    public static List<String> getDVBSTunerList(Context context, int antenaType) {
        List<String> list = new ArrayList<>();
        switch (antenaType) {
            case 0:
                list.clear();
                return list;
            case 1:
                return getSingleCableUserBandList(context);
            default:
                return Arrays.asList(new String[0]);
        }
    }

    private static List<String> getSingleCableUserBandList(Context context) {
        new ArrayList();
        List<SatelliteInfo> dVBSsatellites = getDVBSsatellites(context);
        List<String> list = Arrays.asList(context.getResources().getStringArray(R.array.dvbs_user_band_arrays));
        MtkLog.d("getSingleCableUserBandList()," + list.toString());
        return list;
    }

    public static List<String> getSingleCableFreqsList(Context context, int index) {
        List<String> list;
        new ArrayList();
        String userDefine = context.getString(R.string.dvbs_band_freq_user_define);
        switch (index) {
            case -1:
            case 0:
                list = Arrays.asList(new String[]{"1210", "1284", "1400", userDefine});
                break;
            case 1:
                list = Arrays.asList(new String[]{"1400", "1420", "1516", userDefine});
                break;
            case 2:
                list = Arrays.asList(new String[]{"1516", "1632", "1680", userDefine});
                break;
            case 3:
                list = Arrays.asList(new String[]{"1632", "1748", "2040", userDefine});
                break;
            case 4:
                list = Arrays.asList(new String[]{"1748", userDefine});
                break;
            case 5:
                list = Arrays.asList(new String[]{"1864", userDefine});
                break;
            case 6:
                list = Arrays.asList(new String[]{"1980", userDefine});
                break;
            case 7:
                list = Arrays.asList(new String[]{"2096", userDefine});
                break;
            default:
                list = Arrays.asList(new String[]{userDefine});
                break;
        }
        MtkLog.d("getSingleCableFreqsList()," + list.toString());
        return list;
    }

    public static List<String> getDVBSAntennaTypeList(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_antenna_type_arrays));
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo> filterSatsbyAntennaType(android.content.Context r16, int r17) {
        /*
            r0 = r17
            java.util.List r1 = getDVBSALLsatellites()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r3 = 0
            r4 = 1
            r5 = 2
            android.content.res.Resources r6 = r16.getResources()
            r7 = 2130903067(0x7f03001b, float:1.7412942E38)
            java.lang.String[] r6 = r6.getStringArray(r7)
            r7 = 2
            if (r0 != r7) goto L_0x0027
            android.content.res.Resources r8 = r16.getResources()
            r9 = 2130903058(0x7f030012, float:1.7412923E38)
            java.lang.String[] r6 = r8.getStringArray(r9)
        L_0x0027:
            java.lang.String[] r8 = getDVBSDiseqcInput(r16)
            r9 = 0
            int r0 = java.lang.Math.max(r0, r9)
            int r10 = r1.size()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "filterSatsbyAntennaType(),antennaType:"
            r11.append(r12)
            r11.append(r0)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11)
            r11 = r9
        L_0x0049:
            if (r11 >= r10) goto L_0x0126
            switch(r0) {
                case 1: goto L_0x006f;
                case 2: goto L_0x005e;
                default: goto L_0x004e;
            }
        L_0x004e:
            java.lang.Object r12 = r1.get(r11)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r12 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r12
            int r12 = r12.getMask()
            r12 = r12 & 16384(0x4000, float:2.2959E-41)
            if (r12 != 0) goto L_0x0081
            goto L_0x0122
        L_0x005e:
            java.lang.Object r12 = r1.get(r11)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r12 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r12
            int r12 = r12.getMask()
            r13 = 65536(0x10000, float:9.18355E-41)
            r12 = r12 & r13
            if (r12 != 0) goto L_0x0081
            goto L_0x0122
        L_0x006f:
            java.lang.Object r12 = r1.get(r11)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r12 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r12
            int r12 = r12.getMask()
            r13 = 32768(0x8000, float:4.5918E-41)
            r12 = r12 & r13
            if (r12 != 0) goto L_0x0081
            goto L_0x0122
        L_0x0081:
            java.lang.Object r12 = r1.get(r11)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r12 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r12
            java.lang.Object r13 = r1.get(r11)
            com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo r13 = (com.mediatek.wwtv.setting.base.scan.model.SatelliteInfo) r13
            java.lang.String r13 = r13.getSatName()
            r12.setName(r13)
            r13 = 1
            if (r0 == r13) goto L_0x00c5
            if (r0 != r7) goto L_0x009a
            goto L_0x00c5
        L_0x009a:
            int r14 = r12.getDiseqcType()
            if (r14 != r7) goto L_0x00bd
            int r14 = r12.getPort()
            r15 = 255(0xff, float:3.57E-43)
            if (r14 == r15) goto L_0x00bd
            int r14 = r12.getPort()
            int r15 = r8.length
            if (r14 >= r15) goto L_0x00bd
            int r14 = r12.getPort()
            int r14 = java.lang.Math.max(r14, r9)
            r14 = r8[r14]
            r12.setType(r14)
            goto L_0x00da
        L_0x00bd:
            int r14 = r8.length
            int r14 = r14 - r13
            r14 = r8[r14]
            r12.setType(r14)
            goto L_0x00da
        L_0x00c5:
            int r14 = r12.getPosition()
            int r14 = r14 - r13
            int r14 = java.lang.Math.max(r14, r9)
            int r15 = r6.length
            int r15 = r15 - r13
            int r14 = java.lang.Math.min(r14, r15)
            r15 = r6[r14]
            r12.setType(r15)
        L_0x00da:
            int r14 = r12.getMask()
            r14 = r14 & 8192(0x2000, float:1.14794E-41)
            if (r14 == 0) goto L_0x00e6
            r12.setEnabled(r13)
            goto L_0x00e9
        L_0x00e6:
            r12.setEnabled(r9)
        L_0x00e9:
            boolean r13 = com.mediatek.wwtv.tvcenter.util.MtkLog.logOnFlag
            if (r13 == 0) goto L_0x011f
            java.lang.String r13 = "dvbs satelliteInfo"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "recId:"
            r14.append(r15)
            int r15 = r12.getSatlRecId()
            r14.append(r15)
            java.lang.String r15 = ",position:"
            r14.append(r15)
            int r15 = r12.getPosition()
            r14.append(r15)
            java.lang.String r15 = "info:"
            r14.append(r15)
            java.lang.String r15 = r12.toString()
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)
        L_0x011f:
            r2.add(r12)
        L_0x0122:
            int r11 = r11 + 1
            goto L_0x0049
        L_0x0126:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "filterSatsbyAntennaType(),list.size::"
            r7.append(r9)
            int r9 = r2.size()
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.model.ScanContent.filterSatsbyAntennaType(android.content.Context, int):java.util.List");
    }

    public void saveDVBSConfigSetting(Context context, int atnaType, int band, int freq) {
        MtkLog.d(TAG, "saveDVBSConfigSetting(),atnaType:" + atnaType + ",band:" + band + ",freq:" + freq);
        for (SatelliteInfo info : getDVBSsatellites(context)) {
            if (band != -1) {
                info.setUserBand(band);
            }
            if (freq != -1) {
                info.setBandFreq(freq);
            } else {
                MtkLog.d("saveDVBSConfigSetting() freq is -1");
            }
            saveDVBSSatelliteToSatl(info);
        }
    }

    public void saveDVBSConfigSetting(Context context, int atnaType, int band, int freq, int subBand, int subFreq) {
        MtkLog.d(TAG, "saveDVBSConfigSetting(),atnaType:" + atnaType + ",band:" + band + ",freq:" + freq + ",subBand:" + subBand + ",subFreq:" + subFreq);
        for (SatelliteInfo info : getDVBSsatellites(context)) {
            if (band != -1) {
                info.setUserBand(band);
            }
            if (freq != -1) {
                info.setBandFreq(freq);
            }
            if (subBand != -1) {
                info.setSubUserBand(subBand);
            }
            if (subFreq != -1) {
                info.setSubBandFreq(subFreq);
            }
            saveDVBSSatelliteToSatl(info);
        }
    }

    public static void saveDVBSSatelliteToSatl(SatelliteInfo info) {
        if (info != null) {
            MtkLog.d("saveDVBSSatelliteToSatl().info.Enable:" + info.getEnable() + ">>>>" + info.toString());
            MtkTvDvbsConfigBase base = new MtkTvDvbsConfigBase();
            int svlID = CommonIntegration.getInstance().getSvlFromACFG();
            if ((info.getMask() & 8192) == 0) {
                if (info.getEnable()) {
                    info.setMask(info.getMask() + 8192);
                }
            } else if (!info.getEnable()) {
                info.setMask(info.getMask() - 8192);
            }
            try {
                base.updateSatlRecord(svlID, info, true);
            } catch (Exception e) {
                MtkLog.d("saveDVBSSatelliteToSatl() Exception!!!!!," + e.toString());
            } catch (Error e2) {
                MtkLog.d("saveDVBSSatelliteToSatl() ERROR!!!!!!!," + e2.toString());
            }
            MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
            return;
        }
        MtkLog.d("saveDVBSSatelliteToSatl().info==null;");
    }

    public static int setSelectedSatelliteOPFromMenu(Context context, int position) {
        MtkLog.d(TAG, "setSelectedSatelliteOPFromMenu(),position:" + position);
        MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
        if (isPreferedSat()) {
            int currentOP = getDVBSCurrentOP();
            MtkLog.d(TAG, "setSelectedSatelliteOPFromMenu(),currentOP:" + currentOP);
            int currentOP2 = setSatOP(context, position);
            MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_brdcster", currentOP2);
            return currentOP2;
        }
        base.dvbsSetUIDeftSateInfoByOpt(0);
        MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_brdcster", 0);
        return 0;
    }

    public static int setSatOP(Context context, int position) {
        MtkLog.d(TAG, "setSatOP(),position:" + position);
        MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
        List asList = Arrays.asList(context.getResources().getStringArray(R.array.dvbs_operators));
        List<String> opList = getDVBSOperatorList(context);
        if (opList.size() >= 1 && position < opList.size() && position >= 0) {
            int value = new DVBSOperator(context).getOperatorByName(opList.get(position));
            MtkLog.d(TAG, "setSelectedSatelliteOP(),value:" + value);
            if (opList.get(position).equals(context.getString(R.string.dvbs_operator_name_digi))) {
                String country = MtkTvConfig.getInstance().getCountry();
                if (country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_HUN)) {
                    value = 112;
                } else if (country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_ROU)) {
                    value = 111;
                }
            }
            MtkLog.d(TAG, "setSelectedSatelliteOP(),op value:" + value);
            base.dvbsSetUIDeftSateInfoByOpt(value);
            return value;
        } else if (position == -1) {
            return getDVBSCurrentOP();
        } else {
            return 0;
        }
    }

    public static int setSelectedSatelliteOP(Context context, int position) {
        MtkLog.d(TAG, "setSelectedSatelliteOP(),position:" + position);
        MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_brdcster", setSatOP(context, position));
        int currentOP = getDVBSCurrentOP();
        MtkLog.d(TAG, "setSelectedSatelliteOP(),currentOP()," + currentOP);
        return currentOP;
    }

    public static String getDVBSCurrentOPStr(Context context) {
        int op = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
        MtkLog.d("getDVBSCurrentOPStr(),currentOP()," + op);
        return new DVBSOperator(context).getNameByOperator(op);
    }

    public static int getDVBSCurrentOP() {
        return MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
    }

    public static List<MtkTvScanDvbtBase.LcnConflictGroup> getLcnConflictGroup(Context context) {
        MtkTvScanDvbtBase.LcnConflictGroup[] lcnList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLcnConflictGroup();
        MtkLog.d("getLcnConflictGroup" + lcnList.toString());
        return Arrays.asList(lcnList);
    }

    public static List<String> getTRDChannelList(Context context, int index) {
        MtkTvScanDvbtBase.LcnConflictGroup nwkList = getLcnConflictGroup(context).get(index);
        List<String> networkList = new ArrayList<>();
        networkList.addAll(Arrays.asList(nwkList.channelName));
        MtkLog.d("getTRDChannelList-1" + Arrays.asList(nwkList.channelName));
        networkList.add(context.getString(R.string.scan_trd_lcn_CONFLICT_USE_DEFAULT));
        MtkLog.d("getTRDChannelList-2" + networkList.toString());
        return networkList;
    }

    public static void setAfterScanLCNForMenu(Context context, int groupIndex, int position) {
        MtkLog.d("setAfterScanLCN(),position:" + position);
        int position2 = Math.max(position, 0);
        if (getTRDChannelList(context, groupIndex).get(position2).equalsIgnoreCase(context.getString(R.string.scan_trd_lcn_CONFLICT_USE_DEFAULT))) {
            restoreForAllLCNChannelsForMenu(context, groupIndex);
            return;
        }
        int currentGroup = groupIndex;
        List<MtkTvScanDvbtBase.LcnConflictGroup> groups = getLcnConflictGroup(context);
        MtkTvScan.getInstance().getScanDvbtInstance().uiOpSetLcnConflictGroup(groups.get(currentGroup), groups.get(currentGroup).channelName[position2]);
    }

    public static void restoreForAllLCNChannelsForMenu(Context context, int groupIndex) {
        MtkLog.d("restoreForAllLCNChannels()");
        int currentGroup = groupIndex;
        List<MtkTvScanDvbtBase.LcnConflictGroup> groups = getLcnConflictGroup(context);
        if (currentGroup < groups.size()) {
            for (int i = currentGroup; i < groups.size(); i++) {
                MtkTvScanDvbtBase.LcnConflictGroup group = groups.get(i);
                MtkTvScan.getInstance().getScanDvbtInstance().uiOpSetLcnConflictGroup(group, group.channelName[0]);
            }
        }
    }

    private static String getLocaleLanguage() {
        Locale mLocale = Locale.getDefault();
        return String.format("%s-%s", new Object[]{mLocale.getLanguage(), mLocale.getCountry()});
    }

    private static String getLocaleLanguage(Locale mLocale) {
        return String.format("%s-%s", new Object[]{mLocale.getLanguage(), mLocale.getCountry()});
    }

    public static List<String> getRegionEULanguageCodeList() {
        return new ArrayList<>(Arrays.asList(new String[]{"en", "eu", "ca", "hr", "cs", "da", "nl", "fi", "fr", "gd", "gl", "de", "hu", "it", "no", "pl", "pt", "ro", "sr", "sk", "sl", "es", "sv", "tr", "cy", "et", "ru"}));
    }

    public static List<String> getRegionEUMTKLanguageList() {
        return new ArrayList<>(Arrays.asList(new String[]{MtkTvConfigTypeBase.S639_CFG_LANG_ENG, MtkTvConfigTypeBase.S639_CFG_LANG_BAQ, MtkTvConfigTypeBase.S639_CFG_LANG_CAT, MtkTvConfigTypeBase.S639_CFG_LANG_SCR, MtkTvConfigTypeBase.S639_CFG_LANG_CZE, MtkTvConfigTypeBase.S638_CFG_LANG_DAN, MtkTvConfigTypeBase.S639_CFG_LANG_DUT, MtkTvConfigTypeBase.S639_CFG_LANG_FIN, MtkTvConfigTypeBase.S639_CFG_LANG_FRA, "gla", "glg", MtkTvConfigTypeBase.S639_CFG_LANG_DEU, MtkTvConfigTypeBase.S639_CFG_LANG_HUN, MtkTvConfigTypeBase.S639_CFG_LANG_ITA, MtkTvConfigTypeBase.S639_CFG_LANG_NOR, MtkTvConfigTypeBase.S639_CFG_LANG_POL, MtkTvConfigTypeBase.S639_CFG_LANG_POR, MtkTvConfigTypeBase.S639_CFG_LANG_RUM, MtkTvConfigTypeBase.S639_CFG_LANG_SCC, MtkTvConfigTypeBase.S639_CFG_LANG_SLO, MtkTvConfigTypeBase.S639_CFG_LANG_SLV, MtkTvConfigTypeBase.S639_CFG_LANG_SPA, MtkTvConfigTypeBase.S639_CFG_LANG_SWE, MtkTvConfigTypeBase.S639_CFG_LANG_TUR, "wel", MtkTvConfigTypeBase.S639_CFG_LANG_EST, MtkTvConfigTypeBase.S639_CFG_LANG_RUS}));
    }

    public static LanguageUtil.MyLanguageData getRegionEULanguageStrForMenu(Context context, int position) {
        List<String> mLanguageOSDArray = getRegionEUMTKLanguageList();
        LanguageUtil.MyLanguageData data = new LanguageUtil.MyLanguageData();
        List<String> ISO2LanguageCodeList = getRegionEULanguageCodeList();
        if (position < ISO2LanguageCodeList.size()) {
            data.local = new Locale(ISO2LanguageCodeList.get(position), "");
        } else {
            data.local = Locale.ENGLISH;
        }
        if (position < mLanguageOSDArray.size()) {
            data.tvAPILanguageStr = mLanguageOSDArray.get(position);
        } else {
            data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
        }
        return data;
    }

    public void setDVBS_AntennaType(int value) {
        MtkLog.d("setDVBS_AntennaType()," + value);
        MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_antenna_type", value);
    }

    public void updateDVBSConfigInfo(Context context, String userBand, String bandFreq) {
        int userBandInt = Arrays.asList(context.getResources().getStringArray(R.array.dvbs_user_band_arrays)).indexOf(userBand);
        int bandFreqInt = 0;
        if (!bandFreq.equalsIgnoreCase(context.getString(R.string.dvbs_band_freq_user_define))) {
            try {
                bandFreqInt = Integer.valueOf(bandFreq).intValue();
            } catch (Exception e) {
            }
        }
        MtkLog.d("updateDVBSConfigInfo(),tunerBandInt:" + userBand + ",bandFreq:" + bandFreq);
        for (SatelliteInfo info : getDVBSsatellites(context)) {
            if (userBandInt != -1) {
                info.setUserBand(userBandInt);
            }
            if (bandFreqInt != -1) {
                info.setBandFreq(bandFreqInt);
            }
            saveDVBSSatelliteToSatl(info);
        }
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBS_SatelliteType(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_sat_on_off));
    }

    /* access modifiers changed from: package-private */
    public String[] getDVBS_SatelliteType(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_sat_on_off);
    }

    public String[] getDVBS_Position(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_position_arrays);
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBS_LNBPower(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_sat_on_off));
    }

    /* access modifiers changed from: package-private */
    public String[] getDVBS_LNBPower(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_sat_lnb_power);
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBSDiseqcInput(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(getDVBSDiseqcInput(context));
    }

    private static String[] getDVBSDiseqc10Port(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_diseqc_10port_arrays);
    }

    private static String[] getDVBSDiseqc11Port(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_diseqc_input_arrays);
    }

    private static String[] getDVBSDiseqcMotor(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_diseqc_motor_arrays);
    }

    static String[] getDVBSDiseqcInput(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_diseqc_input_arrays);
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBS_LNBFreq(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_sat_lnbfreq));
    }

    /* access modifiers changed from: package-private */
    public String[] getDVBS_LNBFreq(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_sat_lnbfreq);
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBS_Tone22KHZ(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_tone_22khz_arrays));
    }

    /* access modifiers changed from: package-private */
    public String[] getDVBS_Tone22KHZ(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_tone_22khz_arrays);
    }

    /* access modifiers changed from: package-private */
    public List<String> getDVBS_ToneBurst(Context context, String selectedSatelliteID) {
        new ArrayList();
        return Arrays.asList(context.getResources().getStringArray(R.array.dvbs_tone_burst_arrays));
    }

    /* access modifiers changed from: package-private */
    public String[] getDVBS_ToneBurst(Context context) {
        return context.getResources().getStringArray(R.array.dvbs_tone_burst_arrays);
    }

    public static void setDVBSTPInfo(int satID, MtkTvScanDvbsBase.MtkTvScanTpInfo tpinfo) {
        MtkLog.d("setDVBSTPInfo(),satID:" + satID);
        try {
            new MtkTvScanDvbsBase().dvbsSetUISateTransponder(satID, tpinfo);
        } catch (Exception e) {
            MtkLog.d("setDVBSTPInfo(),satID:" + e.toString());
        }
    }

    public static List<SatelliteTPInfo> getDVBSTransponderList(List<SatelliteInfo> satelliteList) {
        List<SatelliteTPInfo> tempList = new ArrayList<>();
        for (SatelliteInfo tempInfo : satelliteList) {
            SatelliteTPInfo satelliteTPInfo = new SatelliteTPInfo();
            satelliteTPInfo.mRescanSatLocalTPInfo = getDVBSTransponder(tempInfo.getSatlRecId());
            satelliteTPInfo.mSatRecId = tempInfo.getSatlRecId();
            tempList.add(satelliteTPInfo);
        }
        return tempList;
    }

    public static void restoreSatTpInfo(List<SatelliteInfo> satelliteList, List<SatelliteTPInfo> satelliteTPInfoList) {
        if (satelliteList != null && satelliteTPInfoList != null) {
            for (SatelliteInfo tempInfo : satelliteList) {
                Iterator<SatelliteTPInfo> it = satelliteTPInfoList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    SatelliteTPInfo tempTPInfo = it.next();
                    if (tempInfo.getSatlRecId() == tempTPInfo.mSatRecId) {
                        MtkLog.d("restoreSatTpInfo(),satID:" + tempTPInfo.mSatRecId);
                        setDVBSTPInfo(tempTPInfo.mSatRecId, tempTPInfo.mRescanSatLocalTPInfo);
                        satelliteTPInfoList.remove(tempTPInfo);
                        break;
                    }
                }
            }
        }
    }

    public static MtkTvScanDvbsBase.MtkTvScanTpInfo getDVBSTransponder(int satelliteID) {
        MtkLog.d("getDVBSTransponder(),satID:" + satelliteID);
        MtkTvScanDvbsBase scanDVBSbase = new MtkTvScanDvbsBase();
        Objects.requireNonNull(scanDVBSbase);
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = new MtkTvScanDvbsBase.MtkTvScanTpInfo();
        scanDVBSbase.dvbsGetUISateTransponder(satelliteID, tpInfo);
        if (tpInfo.ePol == null) {
            tpInfo.ePol = MtkTvScanDvbsBase.TunerPolarizationType.POL_LIN_HORIZONTAL;
        }
        MtkLog.d(String.format("getDVBSTransponder(),Freq:%d,Symb:%d,Pol:%s", new Object[]{Integer.valueOf(tpInfo.i4Frequency), Integer.valueOf(tpInfo.i4Symbolrate), tpInfo.ePol.name()}));
        return tpInfo;
    }

    public static String getDVBSTransponderStrTitle(Context context, int satelliteID) {
        return getDVBSTransponderStr(getDVBSTransponder(satelliteID));
    }

    public static String getDVBSTransponderStr(MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo) {
        if (tpInfo.ePol.name().equals("POL_LIN_VERTICAL")) {
            return String.format("%dV%d", new Object[]{Integer.valueOf(tpInfo.i4Frequency), Integer.valueOf(tpInfo.i4Symbolrate)});
        } else if (tpInfo.ePol.name().equals("POL_CIR_LEFT")) {
            return String.format("%dL%d", new Object[]{Integer.valueOf(tpInfo.i4Frequency), Integer.valueOf(tpInfo.i4Symbolrate)});
        } else if (tpInfo.ePol.name().equals("POL_CIR_RIGHT")) {
            return String.format("%dR%d", new Object[]{Integer.valueOf(tpInfo.i4Frequency), Integer.valueOf(tpInfo.i4Symbolrate)});
        } else {
            return String.format("%dH%d", new Object[]{Integer.valueOf(tpInfo.i4Frequency), Integer.valueOf(tpInfo.i4Symbolrate)});
        }
    }

    public void iSetStorageOne() {
    }

    public void iResetDefault() {
        MtkLog.d("iResetDefault()");
        int i = 1;
        if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
            while (true) {
                int i2 = i;
                if (i2 >= 8) {
                    break;
                }
                if (i2 == 3 || i2 == 4 || i2 == 7) {
                    MtkTvChannelListBase.cleanChannelList(i2, false);
                } else {
                    MtkTvChannelListBase.cleanChannelList(i2);
                }
                i = i2 + 1;
            }
        } else if (MarketRegionInfo.getCurrentMarketRegion() == 1 || MarketRegionInfo.getCurrentMarketRegion() == 2 || MarketRegionInfo.getCurrentMarketRegion() == 0) {
            while (true) {
                int i3 = i;
                if (i3 >= 3) {
                    break;
                }
                MtkTvChannelListBase.cleanChannelList(i3);
                i = i3 + 1;
            }
        }
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    public void setDefaultChannel() {
        if (CommonIntegration.getInstance().isCurrentSourceTv() && CommonIntegration.getInstance().getChannelAllNumByAPI() > 0) {
            MtkLog.d("setDefaultChannel()");
            int index = 0;
            int i = 0;
            List<MtkTvChannelInfoBase> chList = CommonIntegration.getInstance().getChList(0, 0, 3);
            if (chList != null && chList.size() > 0) {
                while (true) {
                    int i2 = i;
                    if (i2 < chList.size()) {
                        if ((chList.get(i2).getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE && (chList.get(i2).getNwMask() & MtkTvChCommonBase.SB_VNET_VISIBLE) != MtkTvChCommonBase.SB_VNET_VISIBLE) {
                            index = i2;
                            break;
                        }
                        i = i2 + 1;
                    } else {
                        break;
                    }
                }
                if (index < chList.size()) {
                    int channelID = chList.get(index).getChannelId();
                    MtkLog.d("setDefaultChannel(),ChannelID:" + channelID);
                    CommonIntegration.getInstance().selectChannelById(channelID);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int iGetChannelLength() {
        return 0;
    }

    public static int getFrequencyOperator(Context context, String scanMode) {
        return getFrequencyOperator(context, scanMode, getCurrentOperator());
    }

    public static int getFrequencyOperator(Context context, String scanMode, CableOperator operator) {
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        int freq = getDVBCOperatorFrequency(countryID, operator);
        String advanceStr = context.getString(R.string.menu_arrays_Advance);
        String fullStr = context.getString(R.string.menu_arrays_Full);
        String string = context.getString(R.string.menu_arrays_Quick);
        MtkLog.d(String.format("getFrequencyOperator(),scanMode:%s,operator:%s,freq:%d", new Object[]{scanMode, operator.name(), Integer.valueOf(freq)}));
        if (scanMode != null) {
            switch (countryID) {
                case 1:
                case 3:
                case 19:
                case 20:
                case 21:
                    int i = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i == 4) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i != 9) {
                    }
                    break;
                case 2:
                    if (AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()] == 4 && scanMode.equalsIgnoreCase(fullStr)) {
                        freq = -2;
                        break;
                    }
                case 5:
                    int i2 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i2 == 4) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else {
                        switch (i2) {
                            case 13:
                                if (scanMode.equalsIgnoreCase(fullStr)) {
                                    freq = -2;
                                    break;
                                }
                                break;
                        }
                    }
                    break;
                case 6:
                    int i3 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i3 == 2) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i3 == 4) {
                        if (!scanMode.equalsIgnoreCase(fullStr)) {
                            if (scanMode.equalsIgnoreCase(advanceStr)) {
                                freq = 143000;
                                break;
                            }
                        } else {
                            freq = -2;
                            break;
                        }
                    } else if (i3 == 7 && scanMode.equalsIgnoreCase(fullStr)) {
                        freq = -2;
                        break;
                    }
                    break;
                case 8:
                    int i4 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i4 == 2) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i4 == 4) {
                        freq = -2;
                        break;
                    }
                    break;
                case 9:
                    int i5 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i5 == 4) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i5 == 14) {
                        freq = -3;
                        break;
                    }
                    break;
                case 13:
                    int i6 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i6 == 4) {
                        if (scanMode.equalsIgnoreCase(advanceStr)) {
                            freq = 474000;
                            break;
                        }
                    } else {
                        switch (i6) {
                            case 8:
                                if (scanMode.equalsIgnoreCase(advanceStr)) {
                                    freq = 474000;
                                    break;
                                }
                                break;
                        }
                    }
                    break;
                case 14:
                    int i7 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i7 == 2) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i7 == 4) {
                        if (!scanMode.equalsIgnoreCase(advanceStr)) {
                            if (scanMode.equalsIgnoreCase(fullStr)) {
                                freq = -2;
                                break;
                            }
                        } else {
                            freq = 386000;
                            break;
                        }
                    }
                    break;
                case 15:
                    int i8 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i8 == 2) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i8 == 4) {
                        if (!scanMode.equalsIgnoreCase(fullStr)) {
                            if (scanMode.equalsIgnoreCase(advanceStr)) {
                                freq = 362000;
                                break;
                            }
                        } else {
                            freq = -2;
                            break;
                        }
                    }
                    break;
                case 23:
                    int i9 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                    if (i9 == 4) {
                        if (scanMode.equalsIgnoreCase(fullStr)) {
                            freq = -2;
                            break;
                        }
                    } else if (i9 != 9) {
                    }
                    break;
                default:
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        freq = -2;
                        break;
                    }
                    break;
            }
        }
        MtkLog.d(TAG, "getFrequency: " + freq);
        return freq;
    }

    public static int getNetWorkIDOperator(Context context, String scanMode) {
        return getNetWorkIDOperator(context, scanMode, getCurrentOperator());
    }

    public static int getNetWorkIDOperator(Context context, String scanMode, CableOperator operator) {
        int countryID = CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
        int netID = getDVBCOperatorNetWorkID(countryID, operator);
        MtkLog.d(String.format("getNetWorkIDOperator(),scanMode:%s,operator:%s,netID:%d", new Object[]{scanMode, operator.name(), Integer.valueOf(netID)}));
        String advanceStr = context.getString(R.string.menu_arrays_Advance);
        String fullStr = context.getString(R.string.menu_arrays_Full);
        String quickStr = context.getString(R.string.menu_arrays_Quick);
        if (scanMode == null) {
            return netID;
        }
        switch (countryID) {
            case 1:
            case 3:
            case 19:
            case 20:
            case 21:
                int i = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i != 4) {
                    if (i != 9) {
                    }
                    return netID;
                } else if (scanMode.equalsIgnoreCase(quickStr)) {
                    return -1;
                } else {
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    }
                    return netID;
                }
            case 2:
                if (AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()] != 4) {
                    return netID;
                }
                if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                }
                return -1;
            case 5:
                int i2 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i2 != 4) {
                    switch (i2) {
                        case 12:
                            if (scanMode.equalsIgnoreCase(quickStr)) {
                                return -1;
                            }
                            return netID;
                        case 13:
                            break;
                        default:
                            return netID;
                    }
                }
                if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                }
                if (scanMode.equalsIgnoreCase(quickStr)) {
                    return -1;
                }
                return netID;
            case 6:
                int i3 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i3 != 2) {
                    if (i3 != 7) {
                        switch (i3) {
                            case 4:
                                if (scanMode.equalsIgnoreCase(quickStr)) {
                                    return -1;
                                }
                                if (scanMode.equalsIgnoreCase(fullStr)) {
                                    return -2;
                                }
                                return netID;
                            case 5:
                                return -1;
                            default:
                                return netID;
                        }
                    } else if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    } else {
                        return netID;
                    }
                } else if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                } else {
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return -1;
                    }
                    return netID;
                }
            case 8:
                int i4 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i4 != 2) {
                    if (i4 != 4) {
                        return netID;
                    }
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    }
                    if (scanMode.equalsIgnoreCase(quickStr)) {
                        return -1;
                    }
                    return netID;
                } else if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                } else {
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return -1;
                    }
                    return netID;
                }
            case 9:
                int i5 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i5 != 4) {
                    if (i5 != 14) {
                        return netID;
                    }
                    return -3;
                } else if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                } else {
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return netID;
                    }
                    return -1;
                }
            case 13:
                if (AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()] != 9) {
                }
                return netID;
            case 14:
                int i6 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i6 != 2) {
                    if (i6 != 4) {
                        return netID;
                    }
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return -1;
                    }
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    }
                    return netID;
                } else if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                } else {
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return -1;
                    }
                    return netID;
                }
            case 15:
                int i7 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i7 != 2) {
                    if (i7 != 4) {
                        return netID;
                    }
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    }
                    if (scanMode.equalsIgnoreCase(quickStr)) {
                        return -1;
                    }
                    return netID;
                } else if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                } else {
                    if (scanMode.equalsIgnoreCase(advanceStr)) {
                        return -1;
                    }
                    return netID;
                }
            case 23:
                int i8 = AnonymousClass2.$SwitchMap$com$mediatek$wwtv$setting$base$scan$model$CableOperator[operator.ordinal()];
                if (i8 != 4) {
                    switch (i8) {
                        case 10:
                            return -1;
                        default:
                            return netID;
                    }
                } else if (scanMode.equalsIgnoreCase(quickStr)) {
                    return -1;
                } else {
                    if (scanMode.equalsIgnoreCase(fullStr)) {
                        return -2;
                    }
                    return netID;
                }
            default:
                if (scanMode.equalsIgnoreCase(fullStr)) {
                    return -2;
                }
                return -1;
        }
    }

    private static int getDVBCOperatorFrequency(int countryID, CableOperator operator) {
        int freq;
        MtkTvScanDvbcBase.ScanDvbcCountryId mwCountry = reflectCountryIntToMWObject(countryID);
        MtkTvScanDvbcBase.ScanDvbcOperator mwOperator = reflectOperatorToMWObject(operator);
        if (mwCountry == null || mwOperator == null) {
            freq = -1;
        } else {
            try {
                freq = new MtkTvScanDvbcBase().getDefaultFrequency(mwCountry, mwOperator) / 1000;
            } catch (Exception e) {
                freq = 101010;
            }
        }
        MtkLog.d(TAG, "operator: " + operator.name() + ",getFrequency: " + freq);
        return freq;
    }

    private static int getDVBCOperatorNetWorkID(int countryID, CableOperator operator) {
        MtkTvScanDvbcBase.ScanDvbcCountryId mwCountry = reflectCountryIntToMWObject(countryID);
        MtkTvScanDvbcBase.ScanDvbcOperator mwOperator = reflectOperatorToMWObject(operator);
        if (mwCountry == null || mwOperator == null) {
            return -1;
        }
        try {
            return new MtkTvScanDvbcBase().getDefaultNetworkId(mwCountry, mwOperator);
        } catch (Exception e) {
            return 123456;
        }
    }

    public static int getSystemRate() {
        CableOperator operator = getCurrentOperator();
        MtkTvScanDvbcBase.ScanDvbcCountryId mwCountry = reflectCountryIntToMWObject(CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry()));
        MtkTvScanDvbcBase.ScanDvbcOperator mwOperator = reflectOperatorToMWObject(operator);
        if (mwCountry == null || mwOperator == null) {
            return 6875;
        }
        try {
            return new MtkTvScanDvbcBase().getDefaultSymbolRate(mwCountry, mwOperator) / 1000;
        } catch (Exception e) {
            return 6875;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    private static MtkTvScanDvbcBase.ScanDvbcCountryId reflectCountryIntToMWObject(int index) {
        switch (index) {
            case 1:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_AUT;
            case 2:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_BEL;
            case 3:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_CHE;
            case 4:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_CZE;
            case 5:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_DEU;
            case 6:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_DNK;
            case 7:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_ESP;
            case 8:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_FIN;
            case 9:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_FRA;
            case 10:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_GBR;
            case 11:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_ITA;
            case 12:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_LUX;
            case 13:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_NLD;
            case 14:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_NOR;
            case 15:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_SWE;
            case 16:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_BGR;
            case 17:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_HRV;
            case 18:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_GRC;
            case 19:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_HUN;
            case 20:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_IRL;
            case 21:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_POL;
            case 22:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_PRT;
            case 23:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_ROU;
            case 24:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_RUS;
            case 25:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_SRB;
            case 26:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_SVK;
            case 27:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_SVN;
            case 28:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_TUR;
            case 29:
                return MtkTvScanDvbcBase.ScanDvbcCountryId.DVBC_COUNRY_EST;
            default:
                return null;
        }
    }

    private static MtkTvScanDvbcBase.ScanDvbcOperator reflectOperatorToMWObject(CableOperator operator) {
        switch (operator) {
            case Comhem:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_COMHEM;
            case Canal_Digital:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_CANAL_DIGITAL;
            case TELE2:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELE2;
            case OTHER:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
            case Stofa:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_STOFA;
            case Yousee:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_YOUSEE;
            case GLENTEN:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_GLENTEN;
            case Ziggo:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_ZIGGO;
            case UPC:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_UPC;
            case RCS_RDS:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_RCS_RDS;
            case KDG:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_KDG;
            case Unitymedia:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_UNITYMEDIA;
            case TELECOLUMBUS:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELECOLUMBUS;
            case Numericable:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_NUMERICABLE;
            case TELNET:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELENET;
            case AKADO:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_AKADO;
            case ONLIME:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_ONLIME;
            case TVOE_STP:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TVOE_STP;
            case DIVAN_TV:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_DIVAN_TV;
            case BLIZOO:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_BLIZOO;
            case NET1:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_NET1;
            case TELEING:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELEING;
            case TELEMACH:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELEMACH;
            case VOLIA:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_VOLIA;
            case NULL:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_OTHERS;
            case TKT:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TKT;
            case KBW:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_KBW;
            case VOO:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_VOO;
            case KDG_HD:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_KDG_HD;
            case KRS:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_KRS;
            case MTS:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_MTS;
            case TVOE_EKA:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TVOE_EKA;
            case TELEKOM:
                return MtkTvScanDvbcBase.ScanDvbcOperator.DVBC_OPERATOR_NAME_TELEKOM;
            default:
                return null;
        }
    }

    public TunerMode getTuneMode() {
        int tuneMode = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
        MtkLog.d("tuneMode?" + tuneMode);
        switch (tuneMode) {
            case 0:
                return TunerMode.DVBT;
            case 1:
                return TunerMode.CABLE;
            case 2:
            case 3:
                return TunerMode.DVBS;
            default:
                return TunerMode.DVBT;
        }
    }

    public static void disAllowSystemSuspend() {
        MtkLog.d("SystemProperties.set(SUSPEND_KEY, '0')");
        SystemProperties.set(SUSPEND_KEY, "0");
        if (MtkLog.logOnFlag) {
            MtkLog.printStackTrace();
        }
    }

    public static void allowSystemSuspend() {
        MtkLog.d("SystemProperties.set(SUSPEND_KEY, '1')");
        SystemProperties.set(SUSPEND_KEY, "1");
        MtkLog.d("SystemProperties.get(SUSPEND_KEY)" + SystemProperties.get(SUSPEND_KEY));
    }

    public static SatelliteInfo getDVBSsatellitesBySatID(Context context, int recID) {
        MtkLog.d("getDVBSsatellitesBySatID(),recID:" + recID);
        for (SatelliteInfo info : getDVBSsatellites(context)) {
            if (info.getSatlRecId() == recID) {
                return info;
            }
        }
        MtkLog.d("getDVBSsatellitesBySatID(),info==null");
        return null;
    }

    public static List<SatelliteInfo> getDVBSEnablesatellites(Context context) {
        List<SatelliteInfo> list = getDVBSsatellites(context);
        List<SatelliteInfo> enableList = new ArrayList<>();
        for (SatelliteInfo info : list) {
            if (info.getEnable()) {
                enableList.add(info);
            }
        }
        return enableList;
    }

    public static List<SatelliteInfo> getDVBSDisableSatellites(Context context) {
        List<SatelliteInfo> list = getDVBSsatellites(context);
        List<SatelliteInfo> disableList = new ArrayList<>();
        for (SatelliteInfo info : list) {
            if (!info.getEnable()) {
                disableList.add(info);
            }
        }
        return disableList;
    }

    public static List<SatelliteInfo> getDVBSsatellites(Context context) {
        int antennaType = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type");
        MtkLog.d("getDVBSsatellites(),antennaType:" + antennaType);
        return filterSatsbyAntennaType(context, antennaType);
    }

    public static boolean isPreferedSat() {
        return CommonIntegration.getInstance().getSvl() == 4;
    }

    public static boolean isSatScan() {
        return MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src") >= 2;
    }

    public static boolean isATVScanFirst() {
        int value = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_COUNTRY_ANLG_FIRST_COUNTRY);
        MtkLog.d("isATVScanFirst(),FirstCountry:" + value);
        return value == 1;
    }

    private static List<SatelliteInfo> getDVBSALLsatellites() {
        MtkTvDvbsConfigBase mSatl = new MtkTvDvbsConfigBase();
        int svlID = CommonIntegration.getInstance().getSvl();
        int count = mSatl.getSatlNumRecs(svlID);
        List<MtkTvDvbsConfigInfoBase> allSatellite = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            allSatellite.addAll(mSatl.getSatlRecordByRecIdx(svlID, i));
        }
        List<SatelliteInfo> sates = new ArrayList<>();
        for (MtkTvDvbsConfigInfoBase tempInfoBase : allSatellite) {
            sates.add(new SatelliteInfo(tempInfoBase));
        }
        if (MtkLog.logOnFlag) {
            MtkLog.d("getDVBSsatellites()" + String.format("svlID:%d,count:%d,size:%d", new Object[]{Integer.valueOf(svlID), Integer.valueOf(count), Integer.valueOf(allSatellite.size())}));
            MtkLog.d("getDVBSsatellites(),List:" + allSatellite.toString());
        }
        return sates;
    }

    public static int getCountryIndex() {
        return CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
    }

    public static int getDefaultLnbFreqIndex(SatelliteInfo info, int[][] LNB_CONFIG2) {
        int lowFreq = info.getLnbLowFreq();
        int highFreq = info.getLnbHighFreq();
        int switchFreq = info.getLnbSwitchFreq();
        for (int i = 0; i < LNB_CONFIG2.length; i++) {
            if (LNB_CONFIG2[i][0] == lowFreq && LNB_CONFIG2[i][1] == highFreq && LNB_CONFIG2[i][2] == switchFreq) {
                return i;
            }
        }
        return 0;
    }

    public static SatelliteInfo updateDVBSSatInfo(Activity context, ListView listview, SatelliteInfo satInfo, boolean atMenu) {
        int ToneBurstInt;
        int diseqcInputIntType;
        ListView listView = listview;
        boolean isDiseqc12 = MarketRegionInfo.isFunctionSupport(24);
        MtkLog.d("updateDVBSSatInfo for isDiseqc12!!" + isDiseqc12);
        if (isDiseqc12) {
            return updateDVBSSatInfoDiseqc12(context, listview, satInfo, atMenu);
        }
        if (satInfo == null) {
            return null;
        }
        MtkLog.d("updateDVBSSatInfo(),id:" + satInfo.getSatlRecId());
        int index = 0;
        if (listView != null) {
            boolean z = true;
            if (listView == null || listview.getCount() > 1) {
                Action typeAction = (Action) listView.getItemAtPosition(0);
                int typeInt = 0;
                if (typeAction != null) {
                    typeInt = typeAction.mInitValue;
                }
                if (atMenu) {
                    index = 0 + 1;
                }
                int index2 = index + 1;
                int LNBPowerInt = ((Action) listView.getItemAtPosition(index2)).mInitValue;
                boolean isSingleCable = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type") == 1;
                int index3 = index2 + 1;
                int LNBFreqInt = ((Action) listView.getItemAtPosition(index3)).mInitValue;
                int diseqcInputIntPort = 0;
                int diseqcInputIntType2 = 0;
                int Tone22KHZInt = 0;
                if (!isSingleCable) {
                    int index4 = index3 + 1;
                    Action diseqcItem = (Action) listView.getItemAtPosition(index4);
                    diseqcInputIntPort = diseqcItem.mInitValue;
                    boolean z2 = isDiseqc12;
                    if (diseqcInputIntPort == diseqcItem.mOptionValue.length - 1) {
                        diseqcInputIntType = 0;
                    } else {
                        diseqcInputIntType = 2;
                    }
                    diseqcInputIntType2 = diseqcInputIntType;
                    int index5 = index4 + 1;
                    Tone22KHZInt = ((Action) listView.getItemAtPosition(index5)).mInitValue;
                    ToneBurstInt = ((Action) listView.getItemAtPosition(index5 + 1)).mInitValue;
                } else {
                    ToneBurstInt = 0;
                }
                SatelliteInfo info = satInfo;
                if (typeInt != 0) {
                    z = false;
                }
                info.setEnabled(z);
                if (isSingleCable) {
                    info.setPosition(LNBPowerInt);
                } else {
                    info.setDiseqcType(diseqcInputIntType2);
                    info.setPort(diseqcInputIntPort);
                    info.setLnbPower(LNBPowerInt);
                    info.setToneBurst(ToneBurstInt);
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
                MtkLog.d("updateDVBSSatInfo>>" + info.toString());
                return info;
            }
            boolean z3 = isDiseqc12;
        }
        return null;
    }

    public static SatelliteInfo updateDVBSSatInfoDiseqc12(Activity context, ListView listview, SatelliteInfo satInfo, boolean atMenu) {
        ListView listView = listview;
        if (satInfo == null) {
            return null;
        }
        MtkLog.d("updateDVBSSatInfoDiseqc12(),id:" + satInfo.getSatlRecId());
        int index = 0;
        if (listView == null || (listView != null && listview.getCount() <= 1)) {
            return null;
        }
        Action typeAction = (Action) listView.getItemAtPosition(0);
        int typeInt = 0;
        if (typeAction != null) {
            typeInt = typeAction.mInitValue;
        }
        if (atMenu) {
            index = 0 + 1;
        }
        int index2 = index + 1;
        int LNBPowerInt = ((Action) listView.getItemAtPosition(index2)).mInitValue;
        boolean isSingleCable = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_antenna_type") == 1;
        int index3 = index2 + 1;
        int LNBFreqInt = ((Action) listView.getItemAtPosition(index3)).mInitValue;
        int Tone22KHZInt = 0;
        if (!isSingleCable) {
            Tone22KHZInt = ((Action) listView.getItemAtPosition(index3 + 1 + 1)).mInitValue;
        }
        SatelliteInfo info = satInfo;
        info.setEnabled(typeInt == 0);
        if (isSingleCable) {
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
        MtkLog.d("updateDVBSSatInfoDiseqc12>>" + info.toString());
        return info;
    }

    public static SatelliteInfo updateDVBSSatInfoDiseqcSet12(ListView listview, SatelliteInfo satInfo) {
        int diseqcInputIntType;
        int diseqc11InputIntType;
        if (listview == null || ((listview != null && listview.getCount() <= 1) || satInfo == null)) {
            return null;
        }
        MtkLog.d("updateDVBSSatInfoDiseqcSet12(),id:" + satInfo.getSatlRecId());
        Action diseqc10Item = (Action) listview.getItemAtPosition(0);
        if (diseqc10Item == null || diseqc10Item.mOptionValue == null) {
            return satInfo;
        }
        int diseqc10Port = diseqc10Item.mInitValue;
        int toneBrust = 2;
        if (diseqc10Port > 3) {
            diseqcInputIntType = 0;
            if (diseqc10Port == diseqc10Item.mOptionValue.length - 1) {
                toneBrust = 2;
            } else if (diseqc10Port == diseqc10Item.mOptionValue.length - 2) {
                toneBrust = 1;
            } else if (diseqc10Port == diseqc10Item.mOptionValue.length - 3) {
                toneBrust = 0;
            }
            diseqc10Port = 0;
            satInfo.setToneBurst(toneBrust);
        } else {
            diseqcInputIntType = 2;
            if (diseqc10Port == 0 || diseqc10Port == 2) {
                toneBrust = 0;
            } else {
                toneBrust = 1;
            }
        }
        Action diseqc11Item = (Action) listview.getItemAtPosition(0 + 1);
        int diseqc11Port = diseqc11Item.mInitValue;
        if (diseqc11Port == diseqc11Item.mOptionValue.length - 1) {
            diseqc11InputIntType = 0;
        } else {
            diseqc11InputIntType = 2;
        }
        MtkLog.d("updateDVBSSatInfoDiseqcSet12>diseqcInputIntType>" + diseqcInputIntType + ">>>" + diseqc10Port + ">>>" + toneBrust + ">>>" + diseqc11InputIntType + ">>>" + diseqc11Port);
        satInfo.setDiseqcType(diseqcInputIntType);
        satInfo.setPort(diseqc10Port);
        satInfo.setDiseqcTypeEx(diseqc11InputIntType);
        satInfo.setPortEx(diseqc11Port);
        StringBuilder sb = new StringBuilder();
        sb.append("updateDVBSSatInfoDiseqcSet12>>");
        sb.append(satInfo.toString());
        MtkLog.d(sb.toString());
        return satInfo;
    }

    public static void setDVBSFreqToGetSignalQualityFromLocal(int satID, MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo) {
        MtkLog.d("setDVBSFreqToGetSignalQualityFromLocal(),satID:" + satID);
        MtkTvFreqChgParamBase freqInfo = new MtkTvFreqChgParamBase(3, 2, tpInfo.i4Frequency, 0, tpInfo.i4Symbolrate);
        int satPol = Arrays.asList(MtkTvScanDvbsBase.TunerPolarizationType.values()).indexOf(tpInfo.ePol);
        int satLstId = CommonIntegration.getInstance().getSvl();
        freqInfo.setSatPol(satPol);
        freqInfo.setSatLstId(satLstId);
        freqInfo.setSatLstRecId(satID);
        MtkTvBroadcast.getInstance().changeFreq(CommonIntegration.getInstance().getCurrentFocus(), freqInfo);
    }

    public static void setDVBSFreqToGetSignalQuality(int satID) {
        MtkTvScanDvbsBase.MtkTvScanTpInfo tpInfo = getDVBSTransponder(satID);
        int frequency = tpInfo.i4Frequency;
        int symRate = tpInfo.i4Symbolrate;
        int satPol = Arrays.asList(MtkTvScanDvbsBase.TunerPolarizationType.values()).indexOf(tpInfo.ePol);
        MtkLog.d("setDVBSFreqToGetSignalQuality(),satID:" + satID + "  frequency:" + frequency + "  symRate:" + symRate + "  satPol:" + satPol);
        final MtkTvFreqChgParamBase freqInfo = new MtkTvFreqChgParamBase(3, 2, frequency, 0, symRate);
        int satLstId = CommonIntegration.getInstance().getSvl();
        freqInfo.setSatPol(satPol);
        freqInfo.setSatLstId(satLstId);
        freqInfo.setSatLstRecId(satID);
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                MtkTvBroadcast.getInstance().changeFreq(CommonIntegration.getInstance().getCurrentFocus(), MtkTvFreqChgParamBase.this);
            }
        });
    }

    public static String getCurrentSourceFocus() {
        int result = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_TV_FOCUS_WIN);
        if (result == 0) {
            return "main";
        }
        if (1 == result) {
            return "sub";
        }
        return "";
    }

    public static int getSignalLevel() {
        int level = MtkTvBroadcast.getInstance().getSignalLevel();
        if (level < 0) {
            level = 0;
        }
        MtkLog.d("getSignalLevel():" + level);
        return level;
    }

    public static int getSignalQuality() {
        int ber = MtkTvBroadcast.getInstance().getSignalQuality();
        MtkLog.d("getSignalQuality():" + ber);
        return ber;
    }

    public static void setDVBSCurroperator(int op) {
        dvbsCurrOperator = op;
    }

    public static int getDVBSCurroperator() {
        return dvbsCurrOperator;
    }

    public static boolean isSatOpHDAustria() {
        return dvbsCurrOperator == 3;
    }

    public static boolean isSatOpDiveo() {
        return dvbsCurrOperator == 30;
    }
}
