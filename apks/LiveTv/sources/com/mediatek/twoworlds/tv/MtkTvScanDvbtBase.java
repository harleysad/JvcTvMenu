package com.mediatek.twoworlds.tv;

import android.util.Log;
import java.util.ArrayList;

public class MtkTvScanDvbtBase {
    private static final int EXCHANGE_GET_TYPE_RF_INFO = 1;
    private static final int EXCHANGE_GET_TYPE_UI_OP_ITA_GROUP = 4;
    private static final int EXCHANGE_GET_TYPE_UI_OP_LCNV2_CH_LST = 6;
    private static final int EXCHANGE_GET_TYPE_UI_OP_MASK = 2;
    private static final int EXCHANGE_GET_TYPE_UI_OP_NOR_FAV_NWK = 5;
    private static final int EXCHANGE_GET_TYPE_UI_OP_UK_REGION = 3;
    private static final int EXCHANGE_GET_TYPE_UNKNOWN = 0;
    private static final int EXCHANGE_HEADER_LEN = 3;
    private static final int EXCHANGE_HEADER_LEN_IDX = 1;
    private static final int EXCHANGE_HEADER_TYPE_IDX = 2;
    private static final int EXCHANGE_MAX_LEN = 50;
    private static final int EXCHANGE_PAYLOAD_1ST_IDX = 3;
    private static final int EXCHANGE_RF_DIRECTION_CURRENT = 0;
    private static final int EXCHANGE_RF_DIRECTION_NEXT = 1;
    private static final int EXCHANGE_RF_DIRECTION_PREVIOUS = -1;
    private static final int EXCHANGE_SET_TYPE_AUTO_SCAN = 1002;
    private static final int EXCHANGE_SET_TYPE_BGM_SCAN = 1014;
    private static final int EXCHANGE_SET_TYPE_CANCEL_SCAN = 1001;
    private static final int EXCHANGE_SET_TYPE_DISABLE_NETWORK_CHANGE = 1015;
    private static final int EXCHANGE_SET_TYPE_LOAD_CONFLICT_LCN = 1013;
    private static final int EXCHANGE_SET_TYPE_MANUAL_FREQ_SCAN = 1010;
    private static final int EXCHANGE_SET_TYPE_QUICK_SCAN = 1011;
    private static final int EXCHANGE_SET_TYPE_RANGE_SCAN = 1005;
    private static final int EXCHANGE_SET_TYPE_RF_SCAN = 1004;
    private static final int EXCHANGE_SET_TYPE_STORE_CONFLICT_LCN = 1012;
    private static final int EXCHANGE_SET_TYPE_UI_OP_ITA_GROUP = 1007;
    private static final int EXCHANGE_SET_TYPE_UI_OP_LCNV2_CH_LST = 1009;
    private static final int EXCHANGE_SET_TYPE_UI_OP_NOR_FAV_NWK = 1008;
    private static final int EXCHANGE_SET_TYPE_UI_OP_UK_REGION = 1006;
    private static final int EXCHANGE_SET_TYPE_UNKNOWN = 1000;
    private static final int EXCHANGE_SET_TYPE_UPDATE_SCAN = 1003;
    private static final int EXCHANGE_TOTAL_LEN_IDX = 0;
    private static final String TAG = "MtkTvScanDvbt";
    MtkTvScanDvbtBaseDebug debugModule = new MtkTvScanDvbtBaseDebug(this, false);

    public enum RfDirection {
        PREVIOUS,
        CURRENT,
        NEXT
    }

    public enum ScanDvbtRet {
        SCAN_DVBT_RET_OK,
        SCAN_DVBT_RET_INTERNAL_ERROR
    }

    public ScanDvbtRet startAutoScan() {
        _toolLog("Enter startAutoScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1002);
        _toolLog("Leave startAutoScan");
        return ret;
    }

    public ScanDvbtRet startUpdateScan() {
        _toolLog("Enter startUpdateScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1003);
        _toolLog("Leave startUpdateScan");
        return ret;
    }

    public ScanDvbtRet startRfScan() {
        _toolLog("Enter startRfScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1004);
        _toolLog("Leave startRfScan");
        return ret;
    }

    public ScanDvbtRet startQuickScan() {
        _toolLog("Enter startQuickScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1011);
        _toolLog("Leave startQuickScan");
        return ret;
    }

    public ScanDvbtRet startManualFreqScan(int freq) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, freq};
        _toolLog("Enter startManualFreqScan");
        _toolLog(freq + " ");
        _toolExchange(1010, data);
        int mwScanRet = data[0];
        if (mwScanRet != 0) {
            return null;
        }
        if (mwScanRet == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        _toolLog("Leave startManualFreqScan");
        return ret;
    }

    public ScanDvbtRet startBGMScan() {
        _toolLog("Enter startBGMScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1014);
        _toolLog("Leave startBGMScan");
        return ret;
    }

    public ScanDvbtRet startRangeScan(RfInfo fromRf, RfInfo toRf) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, fromRf.rfIndex, toRf.rfIndex};
        _toolLog("Enter startRangeScan");
        _toolLog(fromRf.rfChannelName + " " + fromRf.rfIndex + "--->" + toRf.rfChannelName + " " + toRf.rfIndex);
        _toolExchange(1005, data);
        int mwScanRet = data[0];
        if (mwScanRet != 0) {
            return null;
        }
        if (mwScanRet == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        _toolLog("Leave startRangeScan");
        return ret;
    }

    public ScanDvbtRet setDisableNetworkChangeForCrnt(boolean isDisNetworkChange) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, 0};
        _toolLog("Enter setDisableNetworkChangeForCrnt");
        _toolLog(isDisNetworkChange + " ");
        if (isDisNetworkChange) {
            data[1] = 1;
        }
        _toolExchange(1015, data);
        int mwScanRet = data[0];
        if (mwScanRet != 0) {
            return null;
        }
        if (mwScanRet == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        _toolLog("Leave setDisableNetworkChangeForCrnt");
        return ret;
    }

    public RfInfo[] getAllRf() {
        ArrayList<RfInfo> rfPool = new ArrayList<>();
        _toolLog("getAllRf enter");
        RfInfo currentRf = gotoDestinationRf(RfDirection.CURRENT);
        if (currentRf == null) {
            return null;
        }
        rfPool.add(currentRf);
        RfInfo nextRf = gotoDestinationRf(RfDirection.NEXT);
        while (currentRf.rfIndex != nextRf.rfIndex) {
            rfPool.add(nextRf);
            nextRf = gotoDestinationRf(RfDirection.NEXT);
        }
        RfInfo[] allRfPool = new RfInfo[rfPool.size()];
        RfInfo[] bakeupRfPool = new RfInfo[rfPool.size()];
        int minIdx = 0;
        int minRfIdx = rfPool.get(0).rfIndex;
        for (int i = 0; i < rfPool.size(); i++) {
            bakeupRfPool[i] = rfPool.get(i);
            if (minRfIdx > bakeupRfPool[i].rfIndex) {
                minRfIdx = bakeupRfPool[i].rfIndex;
                minIdx = i;
            }
        }
        for (int i2 = 0; i2 < allRfPool.length; i2++) {
            allRfPool[i2] = bakeupRfPool[(i2 + minIdx) % allRfPool.length];
        }
        return allRfPool;
    }

    public class TargetRegion {
        private static final int MAX_REGION_NAME_LEN = 35;
        public int internalIdx;
        public int level;
        public String name;
        public int primary;
        public int secondary;
        public int tertiary;

        public TargetRegion() {
        }
    }

    public TargetRegion[] uiOpGetTargetRegion() {
        int mwScanRet = 0;
        TargetRegion TR = new TargetRegion();
        ArrayList<TargetRegion> trPool = new ArrayList<>();
        UiOpSituation iiOpSituation = uiOpGetSituation();
        _toolLog("uiOpGetTargetRegion enter");
        int i = 0;
        if (iiOpSituation.targetRegionPopUp) {
            while (mwScanRet == 0) {
                int[] dataExceptName = new int[6];
                dataExceptName[i] = mwScanRet;
                dataExceptName[1] = TR.internalIdx;
                dataExceptName[2] = TR.level;
                dataExceptName[3] = TR.primary;
                dataExceptName[4] = TR.secondary;
                dataExceptName[5] = TR.tertiary;
                int[] data = new int[(dataExceptName.length + 35)];
                data[i] = mwScanRet;
                int i2 = TR.internalIdx;
                TR.internalIdx = i2 + 1;
                data[1] = i2;
                _toolExchange(3, data);
                mwScanRet = data[i];
                if (mwScanRet == 0) {
                    int[] intRegionName = new int[35];
                    for (int i3 = i; i3 < intRegionName.length; i3++) {
                        intRegionName[i3] = data[dataExceptName.length + i3];
                    }
                    TargetRegion currentTR = new TargetRegion();
                    currentTR.name = _toolConvertAsciiArrayToString(intRegionName);
                    currentTR.internalIdx = data[1];
                    currentTR.level = data[2];
                    currentTR.primary = data[3];
                    currentTR.secondary = data[4];
                    currentTR.tertiary = data[5];
                    _toolLog(data[1] + " " + data[2] + " " + data[3] + " " + data[4] + " " + data[5] + " " + currentTR.name);
                    trPool.add(currentTR);
                }
                i = 0;
            }
        }
        TargetRegion[] allTR = new TargetRegion[trPool.size()];
        int j = 0;
        while (true) {
            int j2 = j;
            if (j2 < trPool.size()) {
                allTR[j2] = trPool.get(j2);
                j = j2 + 1;
            } else {
                _toolLog("uiOpGetTargetRegion leave");
                return allTR;
            }
        }
    }

    public ScanDvbtRet uiOpSetTargetRegion(TargetRegion targetRegion) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, targetRegion.internalIdx};
        _toolLog(targetRegion.internalIdx + " " + targetRegion.level + " " + targetRegion.primary + " " + targetRegion.secondary + " " + targetRegion.tertiary + " " + targetRegion.name);
        _toolExchange(1006, data);
        if (data[0] == 0) {
            return ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        _toolLog("uiOpSetTargetRegion FAIL");
        return ret;
    }

    public class LcnConflictGroup {
        private static final int MAX_LCN_GROUP_CHANNEL_NAME = 35;
        public int LCN;
        public String[] channelName;
        public int groupIdx;

        public LcnConflictGroup() {
        }
    }

    public LcnConflictGroup[] uiOpGetLcnConflictGroup() {
        LcnConflictGroup lcnGroup = new LcnConflictGroup();
        ArrayList<LcnConflictGroup> lcnGroupPool = new ArrayList<>();
        UiOpSituation iiOpSituation = uiOpGetSituation();
        _toolLog("uiOpGetLcnConflictGroup enter");
        int i = 0;
        int mwScanRet = 0;
        int i2 = 0;
        boolean finish = false;
        if (iiOpSituation.lcnConflictPopUp) {
            while (mwScanRet == 0 && !finish) {
                ArrayList<String> channelNamePool = new ArrayList<>();
                int j = 0;
                while (mwScanRet == 0) {
                    int[] dataExceptName = new int[4];
                    dataExceptName[i] = mwScanRet;
                    dataExceptName[1] = i2;
                    dataExceptName[2] = j;
                    dataExceptName[3] = lcnGroup.LCN;
                    int[] data = new int[(dataExceptName.length + 35)];
                    data[i] = mwScanRet;
                    data[1] = i2;
                    data[2] = j;
                    data[3] = i;
                    _toolExchange(4, data);
                    mwScanRet = data[i];
                    if (mwScanRet == 0) {
                        int[] intchannelName = new int[35];
                        for (int k = i; k < intchannelName.length; k++) {
                            intchannelName[k] = data[dataExceptName.length + k];
                        }
                        String lcnGroupChannelName = _toolConvertAsciiArrayToString(intchannelName);
                        lcnGroup.LCN = data[3];
                        channelNamePool.add(lcnGroupChannelName);
                        _toolLog(lcnGroup.LCN + " " + lcnGroupChannelName);
                    } else if (j == 0) {
                        finish = true;
                    }
                    j++;
                    i = 0;
                }
                if (!finish) {
                    LcnConflictGroup currentLCNGroup = new LcnConflictGroup();
                    currentLCNGroup.LCN = lcnGroup.LCN;
                    currentLCNGroup.groupIdx = i2;
                    currentLCNGroup.channelName = new String[channelNamePool.size()];
                    for (int m = 0; m < currentLCNGroup.channelName.length; m++) {
                        currentLCNGroup.channelName[m] = channelNamePool.get(m);
                        _toolLog("LCN Group" + lcnGroup.LCN);
                    }
                    lcnGroupPool.add(currentLCNGroup);
                }
                mwScanRet = 0;
                i2++;
                i = 0;
            }
        }
        LcnConflictGroup[] allLCNGroup = new LcnConflictGroup[lcnGroupPool.size()];
        int n = 0;
        while (true) {
            int n2 = n;
            if (n2 < allLCNGroup.length) {
                allLCNGroup[n2] = lcnGroupPool.get(n2);
                n = n2 + 1;
            } else {
                _toolLog("uiOpGetLcnConflictGroup leave");
                return allLCNGroup;
            }
        }
    }

    public ScanDvbtRet uiOpSetLcnConflictGroup(LcnConflictGroup lcnConflictGroup, String channelName) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int channelIdx = 0;
        _toolLog(lcnConflictGroup.LCN + " " + lcnConflictGroup.groupIdx + " " + channelName);
        int i = 0;
        while (true) {
            if (i >= lcnConflictGroup.channelName.length) {
                break;
            } else if (lcnConflictGroup.channelName[i].equals(channelName)) {
                channelIdx = i;
                _toolLog("channel index" + channelIdx);
                break;
            } else {
                i++;
            }
        }
        int[] data = {0, lcnConflictGroup.groupIdx, channelIdx};
        _toolExchange(1007, data);
        if (data[0] == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        } else {
            _toolLog("uiOpSetLcnConflictGroup FAIL");
        }
        _toolLog("uiOpSetLcnConflictGroup leave");
        return ret;
    }

    public class UiOpSituation {
        public boolean favouriteNeteorkPopUp;
        public boolean lcnConflictPopUp;
        public boolean lcnv2PopUp;
        public boolean storeSvcPopUp;
        public boolean targetRegionPopUp;

        public UiOpSituation() {
        }
    }

    public UiOpSituation uiOpGetSituation() {
        boolean z = false;
        int[] data = {0, 0, 0, 0, 0, 0};
        UiOpSituation uiOpSituation = new UiOpSituation();
        _toolLog("uiOpGetSituation enter");
        _toolExchange(2, data);
        if (data[0] == 0) {
            uiOpSituation.storeSvcPopUp = data[1] != 0;
            uiOpSituation.targetRegionPopUp = data[2] != 0;
            uiOpSituation.lcnConflictPopUp = data[3] != 0;
            uiOpSituation.favouriteNeteorkPopUp = data[4] != 0;
            if (data[5] != 0) {
                z = true;
            }
            uiOpSituation.lcnv2PopUp = z;
            _toolLog("mwScanRet OK" + data[1] + data[2] + data[3] + data[4] + data[5]);
        } else {
            _toolLog("mwScanRet NG");
        }
        _toolLog("uiOpGetSituation leave");
        return uiOpSituation;
    }

    public class FavNwk {
        private static final int MAX_NETWORK_NAME_LEN = 35;
        public int index;
        public String networkName;

        public FavNwk() {
        }
    }

    public FavNwk[] uiOpGetFavNwk() {
        int mwScanRet = 0;
        FavNwk favNwk = new FavNwk();
        ArrayList<FavNwk> favNwkPool = new ArrayList<>();
        UiOpSituation iiOpSituation = uiOpGetSituation();
        _toolLog("uiOpGetFavNwk enter");
        if (iiOpSituation.favouriteNeteorkPopUp) {
            while (mwScanRet == 0) {
                int[] dataExceptName = {mwScanRet, favNwk.index};
                int[] data = new int[(dataExceptName.length + 35)];
                data[0] = mwScanRet;
                int i = favNwk.index;
                favNwk.index = i + 1;
                data[1] = i;
                _toolExchange(5, data);
                mwScanRet = data[0];
                if (mwScanRet == 0) {
                    int[] intFavNwkName = new int[35];
                    for (int i2 = 0; i2 < intFavNwkName.length; i2++) {
                        intFavNwkName[i2] = data[dataExceptName.length + i2];
                    }
                    FavNwk currentfavNwk = new FavNwk();
                    currentfavNwk.networkName = _toolConvertAsciiArrayToString(intFavNwkName);
                    currentfavNwk.index = data[1];
                    _toolLog(currentfavNwk.index + " " + currentfavNwk.networkName);
                    favNwkPool.add(currentfavNwk);
                }
            }
        }
        FavNwk[] allFavNwk = new FavNwk[favNwkPool.size()];
        for (int j = 0; j < favNwkPool.size(); j++) {
            allFavNwk[j] = favNwkPool.get(j);
        }
        _toolLog("uiOpGetFavNwk leave");
        return allFavNwk;
    }

    public ScanDvbtRet uiOpSetFavNwk(FavNwk favNwk) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, favNwk.index};
        _toolLog(favNwk.networkName + " " + favNwk.index);
        _toolExchange(1008, data);
        if (data[0] == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        } else {
            _toolLog("uiOpSetFavNwk FAIL");
        }
        _toolLog("uiOpSetFavNwk leave");
        return ret;
    }

    public class LCNv2ChannelList {
        private static final int MAX_LCNV2_CHANNEL_LIST_NAME_LEN = 35;
        public String channelListName;
        public int index;

        public LCNv2ChannelList() {
        }
    }

    public LCNv2ChannelList[] uiOpGetLCNv2ChannelList() {
        int mwScanRet = 0;
        LCNv2ChannelList lcnv2ChannelList = new LCNv2ChannelList();
        ArrayList<LCNv2ChannelList> lcnv2ChannelListPool = new ArrayList<>();
        UiOpSituation iiOpSituation = uiOpGetSituation();
        _toolLog("uiOpGetLCNv2ChannelList enter");
        if (iiOpSituation.lcnv2PopUp) {
            while (mwScanRet == 0) {
                int[] dataExceptName = {mwScanRet, lcnv2ChannelList.index};
                int[] data = new int[(dataExceptName.length + 35)];
                data[0] = mwScanRet;
                int i = lcnv2ChannelList.index;
                lcnv2ChannelList.index = i + 1;
                data[1] = i;
                _toolExchange(6, data);
                mwScanRet = data[0];
                if (mwScanRet == 0) {
                    int[] intLCNv2ChannelListName = new int[35];
                    for (int i2 = 0; i2 < intLCNv2ChannelListName.length; i2++) {
                        intLCNv2ChannelListName[i2] = data[dataExceptName.length + i2];
                    }
                    LCNv2ChannelList currentLCNv2ChannelList = new LCNv2ChannelList();
                    currentLCNv2ChannelList.channelListName = _toolConvertAsciiArrayToString(intLCNv2ChannelListName);
                    currentLCNv2ChannelList.index = data[1];
                    _toolLog(currentLCNv2ChannelList.index + " " + currentLCNv2ChannelList.channelListName);
                    lcnv2ChannelListPool.add(currentLCNv2ChannelList);
                }
            }
        }
        LCNv2ChannelList[] allLCNv2ChannelList = new LCNv2ChannelList[lcnv2ChannelListPool.size()];
        for (int j = 0; j < lcnv2ChannelListPool.size(); j++) {
            allLCNv2ChannelList[j] = lcnv2ChannelListPool.get(j);
        }
        _toolLog("uiOpGetLCNv2ChannelList leave");
        return allLCNv2ChannelList;
    }

    public ScanDvbtRet uiOpSetLCNv2ChannelList(LCNv2ChannelList lcnv2ChannelList) {
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0, lcnv2ChannelList.index};
        _toolLog(lcnv2ChannelList.channelListName + " " + lcnv2ChannelList.index);
        _toolExchange(1009, data);
        if (data[0] == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        } else {
            _toolLog("uiOpSetLCNv2ChannelList FAIL");
        }
        _toolLog("uiOpSetLCNv2ChannelList leave");
        return ret;
    }

    public ScanDvbtRet cancelScan() {
        _toolLog("Enter cancelScan");
        ScanDvbtRet ret = _toolOpOnlyTypeAndRet(1001);
        _toolLog("Leave cancelScan");
        return ret;
    }

    public class RfInfo {
        private static final int MAX_CHANNEL_NAME_LEN = 20;
        public String rfChannelName;
        public int rfFrequence;
        public int rfIndex;

        public RfInfo() {
        }
    }

    public RfInfo gotoDestinationRf(RfDirection rfDirection) {
        _toolLog("Enter getRfInfo" + rfDirection);
        int intRfDirection = _toolRfDirectionToInt(rfDirection);
        int[] intChannelName = new int[20];
        int[] dataExceptName = {0, intRfDirection, 0, 0};
        int[] data = new int[(dataExceptName.length + intChannelName.length)];
        data[0] = 0;
        data[1] = intRfDirection;
        _toolExchange(1, data);
        if (data[0] != 0) {
            return null;
        }
        for (int i = 0; i < intChannelName.length; i++) {
            intChannelName[i] = data[dataExceptName.length + i];
        }
        _toolLog("RF channel int:" + intChannelName[0] + intChannelName[1] + intChannelName[2] + intChannelName[3]);
        StringBuilder sb = new StringBuilder();
        sb.append("RF channel test:");
        sb.append(_toolConvertAsciiArrayToString(new int[]{48, 49, 50, 51, 52, 65, 98, 67}));
        _toolLog(sb.toString());
        RfInfo rfInfo = new RfInfo();
        rfInfo.rfChannelName = _toolConvertAsciiArrayToString(intChannelName);
        rfInfo.rfIndex = data[2];
        rfInfo.rfFrequence = data[3];
        _toolLog("RF channel string:" + rfInfo.rfChannelName + " " + rfInfo.rfIndex + " " + rfInfo.rfFrequence);
        _toolLog("Leave getRfInfo");
        this.debugModule.addDirection(rfDirection);
        return rfInfo;
    }

    private class MtkTvScanDvbtBaseDebug {
        RfDirection[] backDoor;
        RfDirection[] backDoorKey;
        boolean isDebugMode;
        int keyNum;
        MtkTvScanDvbtBase testDvbt;

        private MtkTvScanDvbtBaseDebug(MtkTvScanDvbtBase mtkTvScanDvbtBase, boolean isDebugMode2) {
            this.backDoor = new RfDirection[]{RfDirection.NEXT, RfDirection.NEXT, RfDirection.PREVIOUS, RfDirection.PREVIOUS};
            this.backDoorKey = new RfDirection[]{RfDirection.CURRENT, RfDirection.CURRENT, RfDirection.CURRENT, RfDirection.CURRENT};
            this.keyNum = 0;
            this.isDebugMode = isDebugMode2;
            resetKey();
            this.testDvbt = mtkTvScanDvbtBase;
        }

        private void resetKey() {
            for (int i = 0; i < this.backDoorKey.length; i++) {
                this.backDoorKey[i] = RfDirection.CURRENT;
            }
            this.keyNum = 0;
        }

        private void testDvbt() {
            testDvbtUiOp();
            testDvbtAllRfInfo();
        }

        private void testDvbtAllRfInfo() {
            RfInfo[] allRfInfo = this.testDvbt.getAllRf();
            MtkTvScanDvbtBase mtkTvScanDvbtBase = this.testDvbt;
            mtkTvScanDvbtBase._toolLog("allRfInfo:" + allRfInfo.length);
            for (int i = 0; i < allRfInfo.length; i++) {
                MtkTvScanDvbtBase mtkTvScanDvbtBase2 = this.testDvbt;
                mtkTvScanDvbtBase2._toolLog("RfInfo: " + allRfInfo[i].rfIndex + " " + allRfInfo[i].rfFrequence + " " + allRfInfo[i].rfChannelName);
            }
        }

        private void testDvbtUiOp() {
            UiOpSituation iiOpSituation = this.testDvbt.uiOpGetSituation();
            if (iiOpSituation.storeSvcPopUp) {
                this.testDvbt._toolLog("storeSvcPopUp");
            }
            if (iiOpSituation.targetRegionPopUp) {
                this.testDvbt._toolLog("targetRegionPopUp");
                TargetRegion[] targetRegions = this.testDvbt.uiOpGetTargetRegion();
                MtkTvScanDvbtBase mtkTvScanDvbtBase = this.testDvbt;
                mtkTvScanDvbtBase._toolLog("TargetRegion:" + targetRegions.length);
                for (int i = 0; i < targetRegions.length; i++) {
                    MtkTvScanDvbtBase mtkTvScanDvbtBase2 = this.testDvbt;
                    mtkTvScanDvbtBase2._toolLog("targetRegion: " + targetRegions[i].internalIdx + " " + targetRegions[i].level + " " + targetRegions[i].primary + " " + targetRegions[i].secondary + " " + targetRegions[i].tertiary + " " + targetRegions[i].name);
                }
            }
            if (iiOpSituation.lcnConflictPopUp) {
                this.testDvbt._toolLog("lcnConflictPopUp");
                LcnConflictGroup[] lcnConflictGroup = this.testDvbt.uiOpGetLcnConflictGroup();
                MtkTvScanDvbtBase mtkTvScanDvbtBase3 = this.testDvbt;
                mtkTvScanDvbtBase3._toolLog("LcnConflictGroup:" + lcnConflictGroup.length);
                for (int i2 = 0; i2 < lcnConflictGroup.length; i2++) {
                    MtkTvScanDvbtBase mtkTvScanDvbtBase4 = this.testDvbt;
                    mtkTvScanDvbtBase4._toolLog("LcnConflictGroup: " + lcnConflictGroup[i2].groupIdx + " " + lcnConflictGroup[i2].LCN);
                    for (int j = 0; j < lcnConflictGroup[i2].channelName.length; j++) {
                        this.testDvbt._toolLog(lcnConflictGroup[i2].channelName[j]);
                    }
                }
            }
            if (iiOpSituation.favouriteNeteorkPopUp) {
                this.testDvbt._toolLog("favouriteNeteorkPopUp");
                FavNwk[] favNwk = this.testDvbt.uiOpGetFavNwk();
                MtkTvScanDvbtBase mtkTvScanDvbtBase5 = this.testDvbt;
                mtkTvScanDvbtBase5._toolLog("FavNwk:" + favNwk.length);
                for (int i3 = 0; i3 < favNwk.length; i3++) {
                    MtkTvScanDvbtBase mtkTvScanDvbtBase6 = this.testDvbt;
                    mtkTvScanDvbtBase6._toolLog("FavNwk: " + favNwk[i3].index + " " + favNwk[i3].networkName);
                }
            }
            if (iiOpSituation.lcnv2PopUp) {
                this.testDvbt._toolLog("lcnv2PopUp");
                LCNv2ChannelList[] lcnv2ChannelList = this.testDvbt.uiOpGetLCNv2ChannelList();
                MtkTvScanDvbtBase mtkTvScanDvbtBase7 = this.testDvbt;
                mtkTvScanDvbtBase7._toolLog("LCNv2ChannelList:" + lcnv2ChannelList.length);
                for (int i4 = 0; i4 < lcnv2ChannelList.length; i4++) {
                    MtkTvScanDvbtBase mtkTvScanDvbtBase8 = this.testDvbt;
                    mtkTvScanDvbtBase8._toolLog("LCNv2ChannelList: " + lcnv2ChannelList[i4].index + " " + lcnv2ChannelList[i4].channelListName);
                }
            }
        }

        public void addDirection(RfDirection rfDirection) {
            if (!this.isDebugMode) {
                return;
            }
            if (RfDirection.CURRENT == rfDirection) {
                this.testDvbt._toolLog("reset back door key");
                resetKey();
                return;
            }
            this.backDoorKey[this.keyNum % this.backDoorKey.length] = rfDirection;
            this.keyNum = (this.keyNum + 1) % this.backDoorKey.length;
            boolean isCorrectKey = true;
            for (int i = 0; i < this.backDoor.length; i++) {
                isCorrectKey = isCorrectKey && this.backDoor[i] == this.backDoorKey[i];
            }
            this.testDvbt._toolLog("add key: " + rfDirection);
            if (isCorrectKey) {
                this.testDvbt._toolLog("open the door");
                testDvbt();
            }
        }
    }

    /* access modifiers changed from: private */
    public void _toolLog(String string) {
        Log.d(TAG, string + "\n");
    }

    private String _toolConvertAsciiArrayToString(int[] asciiArray) {
        StringBuilder stringBuilder = new StringBuilder();
        if (asciiArray == null) {
            return null;
        }
        for (int i : asciiArray) {
            stringBuilder.append((char) i);
        }
        return stringBuilder.toString();
    }

    private int _toolRfDirectionToInt(RfDirection rfDirection) {
        if (RfDirection.PREVIOUS == rfDirection) {
            return -1;
        }
        if (RfDirection.NEXT == rfDirection) {
            return 1;
        }
        return 0;
    }

    private ScanDvbtRet _toolOpOnlyTypeAndRet(int exchangeScanType) {
        _toolLog("Enter scanOp:" + exchangeScanType);
        ScanDvbtRet ret = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] data = {0};
        _toolExchange(exchangeScanType, data);
        int mwScanRet = data[0];
        _toolLog("ret scanOp:" + mwScanRet);
        if (mwScanRet == 0) {
            ret = ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        _toolLog("Leave startScan");
        return ret;
    }

    private ScanDvbtRet _toolExchange(int exchangeType, int[] data) {
        ScanDvbtRet scanDvbtRet = ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        int[] payload = data;
        if (payload == null) {
            payload = new int[]{0};
        }
        int totalLen = payload.length + 3;
        if (totalLen > 50) {
            _toolLog("[Error]bigger than EXCHANGE_MAX_LEN");
            return ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
        }
        int[] exchangeData = new int[totalLen];
        exchangeData[0] = totalLen;
        exchangeData[1] = 3;
        exchangeData[2] = exchangeType;
        int i = 0;
        for (int j = 3; j < totalLen; j++) {
            exchangeData[j] = payload[i];
            i++;
        }
        int retTVNativeWrapper = TVNativeWrapper.ScanDvbtExchangeData_native(exchangeData);
        int i2 = 0;
        for (int j2 = 3; j2 < totalLen; j2++) {
            payload[i2] = exchangeData[j2];
            i2++;
        }
        if (retTVNativeWrapper >= 0) {
            return ScanDvbtRet.SCAN_DVBT_RET_OK;
        }
        return ScanDvbtRet.SCAN_DVBT_RET_INTERNAL_ERROR;
    }
}
