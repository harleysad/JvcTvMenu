package com.mediatek.wwtv.setting.base.scan.model;

import android.os.RemoteException;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Arrays;

public class ScanCallback extends MtkTvTVCallbackHandler {
    public static final int TYPE_UI_OP_DVBS_BOUQUET_INFO = 16;
    public static final int TYPE_UI_OP_DVBS_TRICOLOR = 17;
    public static final int TYPE_UI_OP_ITA_GROUP = 1;
    public static final int TYPE_UI_OP_LCNV2_CH_LST = 4;
    public static final int TYPE_UI_OP_NOR_FAV_NWK = 2;
    public static final int TYPE_UI_OP_SAVE_CHANNEL = 8;
    public static final int TYPE_UI_OP_UK_REGION = 0;
    public static ScanCallback mScanCallBack;
    public static int opType = -1;
    public final int ABORT = 8;
    public final int ARG_DVBC_NWK_UPDATE = 8;
    private final int CANCEL = 4;
    private final int COMPLETE = 1;
    private final int DVBT_France_TNT = 16;
    public final int DVBT_France_TNT_arg4 = 4;
    private final int DVBT_THD_arg4 = 4;
    private final int FREQUENCY = 16;
    public final int HAS_OP = 3;
    private final int PROGRESS = 2;
    private final String TAG = "ScanCallback";
    private final int UNKONOWN = 0;
    private int channelType = -1;
    private boolean hasOpTODO = false;
    private boolean mCanStartMduScan;
    public volatile boolean mCheckDVBSInfo = false;
    public volatile boolean mCheckDVBSInfoGet = false;
    public ScanCallbackMsg mLastMsg;
    public volatile boolean mScanComplete = false;
    private volatile ScannerManager mScannerManager;
    private int tkgsNfyInfoMask = 0;

    public int notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) throws RemoteException {
        MtkLog.d("ScanCallback", String.format("msg_id:%4d,scanProgress:%4d,channelNum:%4d,argv4:%4d", new Object[]{Integer.valueOf(msg_id), Integer.valueOf(scanProgress), Integer.valueOf(channelNum), Integer.valueOf(argv4)}));
        dealScanMsg(msg_id, scanProgress, channelNum, argv4);
        return super.notifyScanNotification(msg_id, scanProgress, channelNum, argv4);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:183:0x04cd, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void dealScanMsg(int r18, int r19, int r20, int r21) {
        /*
            r17 = this;
            r7 = r17
            r8 = r18
            r9 = r19
            r10 = r20
            r11 = r21
            monitor-enter(r17)
            java.lang.Class r0 = r17.getClass()     // Catch:{ all -> 0x04ce }
            int r0 = r0.hashCode()     // Catch:{ all -> 0x04ce }
            r12 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r1 = "callbackHash=? "
            r0.append(r1)     // Catch:{ all -> 0x04ce }
            r0.append(r12)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg r0 = r7.mLastMsg     // Catch:{ all -> 0x04ce }
            r13 = 8
            if (r0 == 0) goto L_0x004a
            if (r8 == r13) goto L_0x004a
            com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg r0 = r7.mLastMsg     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg r14 = new com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg     // Catch:{ all -> 0x04ce }
            r1 = r14
            r2 = r7
            r3 = r8
            r4 = r9
            r5 = r10
            r6 = r11
            r1.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x04ce }
            boolean r0 = r0.equals(r14)     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x004a
            java.lang.String r0 = "This msg equals last msg."
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            monitor-exit(r17)
            return
        L_0x004a:
            com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg r0 = new com.mediatek.wwtv.setting.base.scan.model.ScanCallback$ScanCallbackMsg     // Catch:{ all -> 0x04ce }
            r1 = r0
            r2 = r7
            r3 = r8
            r4 = r9
            r5 = r10
            r6 = r11
            r1.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x04ce }
            r7.mLastMsg = r0     // Catch:{ all -> 0x04ce }
            r1 = 16
            r2 = 4
            if (r8 != r1) goto L_0x006d
            if (r9 != 0) goto L_0x006d
            if (r10 != 0) goto L_0x006d
            if (r11 == r2) goto L_0x0064
            if (r11 != r13) goto L_0x006d
        L_0x0064:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r17.getScannerManager()     // Catch:{ all -> 0x04ce }
            r0.showDVBT_THD_ConfirmDialog()     // Catch:{ all -> 0x04ce }
            monitor-exit(r17)
            return
        L_0x006d:
            if (r8 != 0) goto L_0x007e
            if (r9 != 0) goto L_0x007e
            if (r10 != 0) goto L_0x007e
            if (r11 != r13) goto L_0x007e
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r17.getScannerManager()     // Catch:{ all -> 0x04ce }
            r0.showDVBT_THD_ConfirmDialog()     // Catch:{ all -> 0x04ce }
            monitor-exit(r17)
            return
        L_0x007e:
            r3 = 27
            r4 = 2
            if (r8 != 0) goto L_0x010c
            if (r9 != 0) goto L_0x010c
            if (r10 != 0) goto L_0x010c
            com.mediatek.twoworlds.tv.MtkTvScanBase$CallBackType r0 = com.mediatek.twoworlds.tv.MtkTvScanBase.CallBackType.CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT     // Catch:{ all -> 0x04ce }
            int r0 = r0.ordinal()     // Catch:{ all -> 0x04ce }
            if (r11 != r0) goto L_0x010c
            java.lang.String r0 = "DVBS_INFO_GET_NFY_FCT"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()     // Catch:{ all -> 0x04ce }
            java.lang.String r5 = "g_bs__bs_sat_brdcster"
            int r0 = r0.getConfigValue(r5)     // Catch:{ all -> 0x04ce }
            r5 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r6 = "DVBS_INFO_GET_NFY_FCT:OperatorName="
            r0.append(r6)     // Catch:{ all -> 0x04ce }
            r0.append(r5)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            if (r5 != r3) goto L_0x010c
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase r0 = new com.mediatek.twoworlds.tv.MtkTvScanDvbsBase     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            r6 = r0
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase$ScanDvbsRet r0 = r6.dvbsGetNfyGetInfo()     // Catch:{ all -> 0x04ce }
            int r0 = r0.ordinal()     // Catch:{ all -> 0x04ce }
            r14 = r0
            if (r14 != 0) goto L_0x010c
            int r0 = r6.nfyGetInfo_mask     // Catch:{ all -> 0x04ce }
            r15 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r3 = "DVBS_INFO_GET_NFY_FCT nfyGet_mask=="
            r0.append(r3)     // Catch:{ all -> 0x04ce }
            r0.append(r15)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            if (r15 != r4) goto L_0x010c
            java.lang.String r0 = new java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            java.lang.String r3 = r6.nfyGetInfo_usrMessage     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            java.lang.String r4 = "UTF-8"
            byte[] r3 = r3.getBytes(r4)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            r0.<init>(r3)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            r3.<init>()     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            java.lang.String r4 = "DVBS_INFO_GET_NFY_FCT nfyGetInfo_umsg=="
            r3.append(r4)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            r3.append(r0)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            java.lang.String r3 = r3.toString()     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r3 = r17.getScannerManager()     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            r3.showDVBS_TKGS_UserMsgDialog(r0)     // Catch:{ UnsupportedEncodingException -> 0x0108 }
            goto L_0x010c
        L_0x0108:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x04ce }
        L_0x010c:
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r3 = 1
            if (r0 != 0) goto L_0x0134
            if (r8 == r3) goto L_0x0120
            if (r8 == r2) goto L_0x011a
            if (r8 == r13) goto L_0x011a
            goto L_0x012d
        L_0x011a:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.rollbackChannelsWhenScanNothingOnUIThread()     // Catch:{ all -> 0x04ce }
            goto L_0x012d
        L_0x0120:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            boolean r0 = r0.isScanTaskFinish()     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x012d
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.rollbackChannelsWhenScanNothingOnUIThread()     // Catch:{ all -> 0x04ce }
        L_0x012d:
            java.lang.String r0 = "getListener() == null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            monitor-exit(r17)
            return
        L_0x0134:
            r0 = 0
            if (r8 == r3) goto L_0x0142
            r4 = 100
            if (r9 == r4) goto L_0x0142
            java.lang.String r4 = "mScanComplete--1 = false"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4)     // Catch:{ all -> 0x04ce }
            r7.mScanComplete = r0     // Catch:{ all -> 0x04ce }
        L_0x0142:
            if (r8 != r3) goto L_0x0146
            r7.mScanComplete = r0     // Catch:{ all -> 0x04ce }
        L_0x0146:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r4.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r5 = "mScanComplete--2 = "
            r4.append(r5)     // Catch:{ all -> 0x04ce }
            boolean r5 = r7.mScanComplete     // Catch:{ all -> 0x04ce }
            r4.append(r5)     // Catch:{ all -> 0x04ce }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4)     // Catch:{ all -> 0x04ce }
            if (r8 == r2) goto L_0x0490
            if (r8 == r13) goto L_0x0481
            if (r8 == r1) goto L_0x0479
            r1 = -1
            switch(r8) {
                case 0: goto L_0x02ee;
                case 1: goto L_0x019d;
                case 2: goto L_0x0168;
                default: goto L_0x0166;
            }     // Catch:{ all -> 0x04ce }
        L_0x0166:
            goto L_0x04cc
        L_0x0168:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x018f
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x018f
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r0 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r0     // Catch:{ all -> 0x04ce }
            int r0 = r0.mduType     // Catch:{ all -> 0x04ce }
            if (r0 == r1) goto L_0x018f
            boolean r0 = r7.mCanStartMduScan     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x018f
            java.lang.String r0 = "ScanCallback"
            java.lang.String r1 = "PROGRESS need startMdu new scan, so return update progress"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)     // Catch:{ all -> 0x04ce }
            monitor-exit(r17)
            return
        L_0x018f:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.setChannelsNum(r10, r11)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r0.onProgress(r9, r10, r11)     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x019d:
            boolean r2 = r7.mScanComplete     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x01a8
            java.lang.String r0 = ".onCompleted(ScannerListener.COMPLETE_OK);mScanComplete==OK,return"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x01a8:
            java.lang.String r2 = ".onCompleted(ScannerListener.COMPLETE_OK);."
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x01e7
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x01e7
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r2 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r2     // Catch:{ all -> 0x04ce }
            int r2 = r2.mduType     // Catch:{ all -> 0x04ce }
            if (r2 == r1) goto L_0x01e7
            java.lang.String r2 = "ScanCallback"
            java.lang.String r3 = "onCompleted startMdu new scan"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)     // Catch:{ all -> 0x04ce }
            boolean r2 = r7.mCanStartMduScan     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x01db
            r7.mCanStartMduScan = r0     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.startSecondMduScan()     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x01db:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r0 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r0     // Catch:{ all -> 0x04ce }
            r0.mduType = r1     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x01e7:
            r7.mScanComplete = r3     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager$Region r2 = r2.region     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager$Region r4 = com.mediatek.wwtv.setting.base.scan.model.ScannerManager.Region.EU     // Catch:{ all -> 0x04ce }
            if (r2 != r4) goto L_0x02d0
            java.lang.String r2 = "dealScanMsg(),complete,has operation to do"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r2.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r4 = "complete mScannerManager.mDVBSData :"
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r4 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r4 = r4.mDVBSData     // Catch:{ all -> 0x04ce }
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x0261
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r2.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r4 = "complete mScannerManager.mDVBSData.params :"
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r4 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r4 = r4.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r4 = r4.params     // Catch:{ all -> 0x04ce }
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x0261
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r2.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r4 = "complete ((DVBSSettingsInfo) (mScannerManager.mDVBSData).params).isUpdateScan :"
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r4 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r4 = r4.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r4 = r4.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r4 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r4     // Catch:{ all -> 0x04ce }
            boolean r4 = r4.isUpdateScan     // Catch:{ all -> 0x04ce }
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r4 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r4 = r4.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r4 = r4.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r4 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r4     // Catch:{ all -> 0x04ce }
            int r4 = r4.tkgsType     // Catch:{ all -> 0x04ce }
            r2.append(r4)     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
        L_0x0261:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x02cc
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x02cc
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r2 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r2     // Catch:{ all -> 0x04ce }
            int r2 = r2.tkgsType     // Catch:{ all -> 0x04ce }
            if (r2 != r3) goto L_0x02cc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r2.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r3 = "complete tkgs mCheckDVBSInfoGet:"
            r2.append(r3)     // Catch:{ all -> 0x04ce }
            boolean r3 = r7.mCheckDVBSInfoGet     // Catch:{ all -> 0x04ce }
            r2.append(r3)     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2)     // Catch:{ all -> 0x04ce }
            boolean r2 = r7.mCheckDVBSInfoGet     // Catch:{ all -> 0x04ce }
            if (r2 == 0) goto L_0x02b8
            r7.mCheckDVBSInfoGet = r0     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r1 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r1 = r1.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r1 = r1.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r1 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r1     // Catch:{ all -> 0x04ce }
            boolean r1 = r1.isUpdateScan     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "TKGS_LOGO"
            if (r1 == 0) goto L_0x02a8
            java.lang.String r3 = "TKGS_LOGO_UPDATE"
            r2 = r3
        L_0x02a8:
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r3 = r17.getListener()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r4 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r4 = r4.mDVBSData     // Catch:{ all -> 0x04ce }
            int[] r4 = r4.satList     // Catch:{ all -> 0x04ce }
            r0 = r4[r0]     // Catch:{ all -> 0x04ce }
            r3.onDVBSInfoUpdated(r0, r2)     // Catch:{ all -> 0x04ce }
            goto L_0x02c2
        L_0x02b8:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r0 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r0     // Catch:{ all -> 0x04ce }
            r0.tkgsType = r1     // Catch:{ all -> 0x04ce }
        L_0x02c2:
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r1 = 2
            r0.onCompleted(r1)     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x02cc:
            r17.debugOP()     // Catch:{ all -> 0x04ce }
            goto L_0x02d5
        L_0x02d0:
            java.lang.String r0 = "dealScanMsg(),No operation!!!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
        L_0x02d5:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            boolean r0 = r0.isScanTaskFinish()     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x02e2
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.rollbackChannelsWhenScanNothingOnUIThread()     // Catch:{ all -> 0x04ce }
        L_0x02e2:
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r1 = 2
            r0.onCompleted(r1)     // Catch:{ all -> 0x04ce }
            r7.channelType = r11     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x02ee:
            int[] r0 = com.mediatek.wwtv.setting.base.scan.model.ScanCallback.AnonymousClass1.$SwitchMap$com$mediatek$twoworlds$tv$MtkTvScanBase$CallBackType     // Catch:{ all -> 0x04ce }
            com.mediatek.twoworlds.tv.MtkTvScanBase$CallBackType[] r1 = com.mediatek.twoworlds.tv.MtkTvScanBase.CallBackType.values()     // Catch:{ all -> 0x04ce }
            r1 = r1[r11]     // Catch:{ all -> 0x04ce }
            int r1 = r1.ordinal()     // Catch:{ all -> 0x04ce }
            r0 = r0[r1]     // Catch:{ all -> 0x04ce }
            switch(r0) {
                case 1: goto L_0x044d;
                case 2: goto L_0x043e;
                case 3: goto L_0x043d;
                case 4: goto L_0x043a;
                case 5: goto L_0x0432;
                case 6: goto L_0x0301;
                default: goto L_0x02ff;
            }     // Catch:{ all -> 0x04ce }
        L_0x02ff:
            goto L_0x0478
        L_0x0301:
            java.lang.String r0 = "CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT,"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.twoworlds.tv.MtkTvConfig r0 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()     // Catch:{ all -> 0x04ce }
            java.lang.String r1 = "g_bs__bs_sat_brdcster"
            int r0 = r0.getConfigValue(r1)     // Catch:{ all -> 0x04ce }
            r1 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT:"
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x037c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "DVBS_INFO_GET_NFY_FCT mScannerManager.mDVBSData.params :"
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x037c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "DVBS_INFO_GET_NFY_FCT ((DVBSSettingsInfo) (mScannerManager.mDVBSData).params).isUpdateScan :"
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r2 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r2     // Catch:{ all -> 0x04ce }
            boolean r2 = r2.isUpdateScan     // Catch:{ all -> 0x04ce }
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r2 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r2 = r2.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r2 = r2.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r2 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r2     // Catch:{ all -> 0x04ce }
            int r2 = r2.tkgsType     // Catch:{ all -> 0x04ce }
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
        L_0x037c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT OperatorName:"
            r0.append(r2)     // Catch:{ all -> 0x04ce }
            r0.append(r1)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            r2 = 27
            if (r1 != r2) goto L_0x0425
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x0425
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x0425
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r0 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r0     // Catch:{ all -> 0x04ce }
            r2 = r0
            boolean r0 = r2.isUpdateScan     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x03b5
            java.lang.String r0 = "CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT is tkgs update scan:"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            goto L_0x03ba
        L_0x03b5:
            java.lang.String r0 = "CALL_BACK_TYPE_DTV_DVBS_INFO_GET_NFY_FCT is tkgs re-scan:"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
        L_0x03ba:
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase r0 = new com.mediatek.twoworlds.tv.MtkTvScanDvbsBase     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            r4 = r0
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase$ScanDvbsRet r0 = r4.dvbsGetNfyGetInfo()     // Catch:{ all -> 0x04ce }
            int r0 = r0.ordinal()     // Catch:{ all -> 0x04ce }
            r5 = r0
            if (r5 != 0) goto L_0x0422
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r0.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r6 = "DVBS_INFO_GET_NFY_FCT nfyGetInfo_mask=="
            r0.append(r6)     // Catch:{ all -> 0x04ce }
            int r6 = r4.nfyGetInfo_mask     // Catch:{ all -> 0x04ce }
            r0.append(r6)     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            int r0 = r4.nfyGetInfo_mask     // Catch:{ all -> 0x04ce }
            r7.setTkgsNfyInfoMask(r0)     // Catch:{ all -> 0x04ce }
            int r0 = r4.nfyGetInfo_mask     // Catch:{ all -> 0x04ce }
            r6 = 2
            if (r0 != r6) goto L_0x0418
            java.lang.String r0 = new java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            java.lang.String r6 = r4.nfyGetInfo_usrMessage     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            java.lang.String r13 = "UTF-8"
            byte[] r6 = r6.getBytes(r13)     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            r0.<init>(r6)     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            r6.<init>()     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            java.lang.String r13 = "DVBS_INFO_GET_NFY_FCT nfyGetInfo_umsg=="
            r6.append(r13)     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            r6.append(r0)     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            java.lang.String r6 = r6.toString()     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6)     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r6 = r7.mScannerManager     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r6 = r6.mDVBSData     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            r6.tkgsUserMessage = r0     // Catch:{ UnsupportedEncodingException -> 0x0413 }
            goto L_0x0417
        L_0x0413:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x04ce }
        L_0x0417:
            goto L_0x0422
        L_0x0418:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DvbsScanningData r0 = r0.mDVBSData     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScanParams r0 = r0.params     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo r0 = (com.mediatek.wwtv.setting.base.scan.model.DVBSSettingsInfo) r0     // Catch:{ all -> 0x04ce }
            r0.tkgsType = r3     // Catch:{ all -> 0x04ce }
        L_0x0422:
            r7.mCheckDVBSInfoGet = r3     // Catch:{ all -> 0x04ce }
            goto L_0x0478
        L_0x0425:
            r0 = 32
            if (r1 != r0) goto L_0x0478
            r0 = 17
            r7.setOpType(r0)     // Catch:{ all -> 0x04ce }
            r7.setHasOpTODO(r3)     // Catch:{ all -> 0x04ce }
            goto L_0x0478
        L_0x0432:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.onMdu_detect_nfy()     // Catch:{ all -> 0x04ce }
            r7.mCanStartMduScan = r3     // Catch:{ all -> 0x04ce }
            goto L_0x0478
        L_0x043a:
            r7.mCheckDVBSInfo = r3     // Catch:{ all -> 0x04ce }
            goto L_0x0478
        L_0x043d:
            goto L_0x0478
        L_0x043e:
            java.lang.String r0 = "CALL_BACK_TYPE_DTV_DVBS_SVC_UPDATE_NFY_FCT,"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r1 = 5
            r2 = 0
            r0.onDVBSInfoUpdated(r1, r2)     // Catch:{ all -> 0x04ce }
            goto L_0x0478
        L_0x044d:
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            java.lang.String r0 = r0.getScanningSatName()     // Catch:{ all -> 0x04ce }
            if (r0 == 0) goto L_0x0478
            java.lang.String r1 = "null"
            boolean r1 = r0.equalsIgnoreCase(r1)     // Catch:{ all -> 0x04ce }
            if (r1 != 0) goto L_0x0478
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ce }
            r1.<init>()     // Catch:{ all -> 0x04ce }
            java.lang.String r2 = "SAT_NAME_NFY_FCT,name:"
            r1.append(r2)     // Catch:{ all -> 0x04ce }
            r1.append(r0)     // Catch:{ all -> 0x04ce }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r1 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r1.onDVBSInfoUpdated(r11, r0)     // Catch:{ all -> 0x04ce }
        L_0x0478:
            goto L_0x04cc
        L_0x0479:
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r0.onFrequence(r9)     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x0481:
            r7.mCheckDVBSInfo = r0     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r1 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r1.rollbackChannelsWhenScanNothingOnUIThread()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r1 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r1.onCompleted(r0)     // Catch:{ all -> 0x04ce }
            goto L_0x04cc
        L_0x0490:
            java.lang.String r1 = "onCompleted(ScannerListener.COMPLETE_CANCEL)"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r1 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager$Region r1 = r1.region     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager$Region r2 = com.mediatek.wwtv.setting.base.scan.model.ScannerManager.Region.EU     // Catch:{ all -> 0x04ce }
            if (r1 != r2) goto L_0x04b8
            java.lang.String r1 = "dealScanMsg(),complete,has operation to do"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1)     // Catch:{ all -> 0x04ce }
            r17.debugOP()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r1 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            boolean r1 = r1.needStartTVAfterATVScanUpDown()     // Catch:{ all -> 0x04ce }
            if (r1 == 0) goto L_0x04bd
            java.lang.String r1 = ",needStartTVAfterATVScanUpDown()"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1)     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r1 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r1.resumeTV()     // Catch:{ all -> 0x04ce }
            goto L_0x04bd
        L_0x04b8:
            java.lang.String r1 = "dealScanMsg(),No operation!!!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1)     // Catch:{ all -> 0x04ce }
        L_0x04bd:
            r7.mCheckDVBSInfo = r0     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerManager r0 = r7.mScannerManager     // Catch:{ all -> 0x04ce }
            r0.rollbackChannelsWhenScanNothingOnUIThread()     // Catch:{ all -> 0x04ce }
            com.mediatek.wwtv.setting.base.scan.model.ScannerListener r0 = r17.getListener()     // Catch:{ all -> 0x04ce }
            r0.onCompleted(r3)     // Catch:{ all -> 0x04ce }
        L_0x04cc:
            monitor-exit(r17)
            return
        L_0x04ce:
            r0 = move-exception
            monitor-exit(r17)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.base.scan.model.ScanCallback.dealScanMsg(int, int, int, int):void");
    }

    private void debugOP() {
        MtkLog.d("debugOP()");
        MtkTvScanDvbtBase.UiOpSituation opSituation = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetSituation();
        int i = 0;
        setHasOpTODO(false);
        if (getScannerManager().getTuneMode() < 2) {
            MtkTvScanDvbtBase.FavNwk[] nwkList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetFavNwk();
            if (!opSituation.favouriteNeteorkPopUp || nwkList == null || nwkList.length <= 0) {
                MtkLog.d("debugOP(),FavNwk==null || favouriteNeteorkPopUp=false");
                MtkTvScanDvbtBase.LcnConflictGroup[] lcnList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLcnConflictGroup();
                if (!opSituation.lcnConflictPopUp || lcnList == null || lcnList.length <= 0) {
                    MtkLog.d("debugOP(),LcnConflictGroup==null || lcnConflictPopUp=false");
                    MtkTvScanDvbtBase.LCNv2ChannelList[] lcnV2List = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetLCNv2ChannelList();
                    if (!opSituation.lcnv2PopUp || lcnV2List == null || lcnV2List.length <= 0) {
                        MtkLog.d("debugOP(),LCNv2ChannelList==null || lcnv2PopUp=false");
                        MtkTvScanDvbtBase.TargetRegion[] regionList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetTargetRegion();
                        if (!opSituation.targetRegionPopUp || regionList == null || regionList.length <= 0) {
                            MtkLog.d("debugOP(),TargetRegion==null || targetRegionPopUp=false");
                            if (!opSituation.storeSvcPopUp || !this.mScannerManager.showPTSaveChannelDialog()) {
                                MtkLog.d("debugOP(),not show PT save channel dialog");
                                return;
                            }
                            setHasOpTODO(true);
                            setOpType(8);
                            return;
                        }
                        MtkLog.d("regionList!=null,print----->");
                        setOpType(0);
                        for (int i2 = 0; i2 < regionList.length; i2++) {
                            String nwkName = regionList[i2].name;
                            MtkLog.d(String.format("-------regionList[%d]-------", new Object[]{Integer.valueOf(i2)}));
                            dumpRegionInfo(regionList[i2]);
                            MtkLog.d("name:" + nwkName);
                        }
                        setHasOpTODO(true);
                        this.mScannerManager.reMapRegions(Arrays.asList(regionList));
                        return;
                    }
                    MtkLog.d("lcnV2List!=null,print----->");
                    setOpType(4);
                    while (i < lcnV2List.length) {
                        MtkLog.d("channelListName:" + lcnV2List[i].channelListName);
                        i++;
                    }
                    setHasOpTODO(true);
                    return;
                }
                MtkLog.d("lcnList!=null,print----->");
                setOpType(1);
                for (MtkTvScanDvbtBase.LcnConflictGroup lcnConflictGroup : lcnList) {
                    MtkLog.d("channelName:" + lcnConflictGroup.channelName[0]);
                }
                setHasOpTODO(true);
                return;
            }
            MtkLog.d("nwkList!=null,print----->");
            setOpType(2);
            while (i < nwkList.length) {
                MtkLog.d("networkName:" + nwkList[i].networkName);
                i++;
            }
            setHasOpTODO(true);
        } else if (this.mCheckDVBSInfo) {
            this.mCheckDVBSInfo = false;
            setOpType(16);
            setHasOpTODO(true);
        }
    }

    private void dumpRegionInfo(MtkTvScanDvbtBase.TargetRegion targetRegion) {
        MtkLog.d("TargetRegion", "internalIdx," + targetRegion.internalIdx);
        MtkLog.d("TargetRegion", "level," + targetRegion.level);
        MtkLog.d("TargetRegion", "primary," + targetRegion.primary);
        MtkLog.d("TargetRegion", "secondary," + targetRegion.secondary);
        MtkLog.d("TargetRegion", "tertiary," + targetRegion.tertiary);
        MtkLog.d("TargetRegion", "name," + targetRegion.name);
    }

    public ScannerListener getListener() {
        return getScannerManager().getListener();
    }

    public ScannerManager getScannerManager() {
        return this.mScannerManager;
    }

    public void setScannerManager(ScannerManager scannerManager) {
        this.mScannerManager = scannerManager;
    }

    public static ScanCallback getInstance(ScannerManager scannerManager) {
        if (mScanCallBack == null) {
            mScanCallBack = new ScanCallback();
        }
        mScanCallBack.setScannerManager(scannerManager);
        return mScanCallBack;
    }

    public boolean isHasOpTODO() {
        return this.hasOpTODO;
    }

    public void setHasOpTODO(boolean hasOpTODO2) {
        this.hasOpTODO = hasOpTODO2;
    }

    public int getOpType() {
        MtkLog.d("getOpType()," + opType);
        return opType;
    }

    public void setOpType(int opType2) {
        MtkLog.d("setOpType()," + opType2);
        if (MtkLog.logOnFlag) {
            MtkLog.printStackTrace();
        }
        opType = opType2;
    }

    public int getTkgsNfyInfoMask() {
        return this.tkgsNfyInfoMask;
    }

    public void setTkgsNfyInfoMask(int tkgsNfyInfoMask2) {
        this.tkgsNfyInfoMask = tkgsNfyInfoMask2;
    }

    class ScanCallbackMsg {
        int argv4;
        int channelNum;
        int msg_id;
        int scanProgress;

        public ScanCallbackMsg(int msg_id2, int scanProgress2, int channelNum2, int argv42) {
            this.msg_id = msg_id2;
            this.scanProgress = scanProgress2;
            this.channelNum = channelNum2;
            this.argv4 = argv42;
        }

        public boolean equals(Object o) {
            if (!(o instanceof ScanCallbackMsg)) {
                return false;
            }
            ScanCallbackMsg tmp = (ScanCallbackMsg) o;
            if (tmp.msg_id == this.msg_id && tmp.scanProgress == this.scanProgress && tmp.channelNum == this.channelNum && tmp.argv4 == this.argv4) {
                return true;
            }
            return false;
        }
    }
}
