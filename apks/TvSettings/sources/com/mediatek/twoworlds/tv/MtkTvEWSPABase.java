package com.mediatek.twoworlds.tv;

import android.util.Log;
import java.util.Arrays;

public class MtkTvEWSPABase {
    private static final int EWS_GET_HAS_EWS = 2;
    private static final int EWS_GET_INFO_MSG_NUM = 3;
    private static final int EWS_GET_MORE_INFO_MSG = 4;
    private static final int EWS_GET_NONE = 0;
    private static final int EWS_GET_SVC_INFO = 1;
    private static final int EWS_SET_LOCATION_CODE = 1;
    private static final int EWS_SET_NONE = 0;
    private static final int MAX_INFO_MSG_NUM_ONCE = 1;
    private static final int MAX_INFO_MSG_STRING_LEN = 2200;
    private static final int MAX_STRING_LEN = 256;
    public static final String TAG = "MtkTvEWSPABase";

    public enum EwspaRet {
        EWSPA_RET_OK,
        EWSPA_RET_INTERNAL_ERROR
    }

    public MtkTvEWSPABase() {
        Log.d(TAG, "MtkTvEWSPABase object created");
    }

    public class TmdwInfoMsg {
        public String charInfoMsg;
        public short charInfoMsgLen;

        public TmdwInfoMsg() {
        }
    }

    public class EwsInfo {
        public byte authority;
        public String charDisasterCharacterstic;
        public byte charDisasterCharactersticLen;
        public String charDisasterCode;
        public byte charDisasterCodeLen;
        public String charDisasterDate;
        public byte charDisasterDateLen;
        public String charDisasterPosition;
        public byte charDisasterPositionLen;
        public short charInfoMsgNum;
        public String charLocationCode;
        public byte charLocationCodeLen;
        public short disasterCode;
        public TmdwInfoMsg[] infoMsg;
        public byte[] locationCode = {0, 0, 0};
        public byte locationTypeCode;

        public EwsInfo() {
        }
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

    public void createMonitorInst(byte monitorType) {
        Log.d(TAG, "createMonitorInst entered, monitorType:" + monitorType);
        TVNativeWrapper.createMonitorInst_native(monitorType);
        Log.d(TAG, "createMonitorInst exit");
    }

    public void deleteMonitorInst(byte monitorType) {
        Log.d(TAG, "deleteMonitorInst entered, monitorType:" + monitorType);
        TVNativeWrapper.deleteMonitorInst_native(monitorType);
        Log.d(TAG, "deleteMonitorInst exit");
    }

    public EwsInfo getEWSInfo(byte monitorType) {
        byte b = monitorType;
        int[] intEwsString = new int[256];
        int[] intEwsInfoMsgString = new int[MAX_INFO_MSG_STRING_LEN];
        int[] numData = new int[3];
        EwsInfo ewsInfo = new EwsInfo();
        Log.d(TAG, "getEWSInfo entered");
        TVNativeWrapper.getInfo_native(b, 3, numData);
        int idx = 0 + 1;
        if (numData[0] != 0) {
            return null;
        }
        ewsInfo.charInfoMsgNum = (short) numData[idx];
        Log.d(TAG, "infoMsgNum:" + ewsInfo.charInfoMsgNum);
        Log.d(TAG, "MaxLen[2200]:2200");
        Log.d(TAG, "ews information total len:" + 1293);
        int[] svcData = new int[1293];
        svcData[1] = ewsInfo.charInfoMsgNum;
        svcData[2] = 1;
        TVNativeWrapper.getInfo_native(b, 1, svcData);
        int idx2 = 0 + 1;
        int mwEWSPARet = svcData[0];
        if (mwEWSPARet != 0) {
            return null;
        }
        int idx3 = idx2 + 1;
        ewsInfo.disasterCode = (short) svcData[idx2];
        int idx4 = idx3 + 1;
        ewsInfo.charInfoMsgNum = (short) svcData[idx3];
        int idx5 = idx4 + 1;
        ewsInfo.locationTypeCode = (byte) svcData[idx4];
        int idx6 = idx5 + 1;
        ewsInfo.authority = (byte) svcData[idx5];
        int idx7 = idx6 + 1;
        ewsInfo.locationCode[0] = (byte) svcData[idx6];
        int idx8 = idx7 + 1;
        ewsInfo.locationCode[1] = (byte) svcData[idx7];
        int idx9 = idx8 + 1;
        ewsInfo.locationCode[2] = (byte) svcData[idx8];
        int idx10 = idx9 + 1;
        ewsInfo.charDisasterCodeLen = (byte) svcData[idx9];
        int idx11 = idx10 + 1;
        ewsInfo.charDisasterDateLen = (byte) svcData[idx10];
        int idx12 = idx11 + 1;
        ewsInfo.charDisasterPositionLen = (byte) svcData[idx11];
        int idx13 = idx12 + 1;
        ewsInfo.charDisasterCharactersticLen = (byte) svcData[idx12];
        int idx14 = idx13 + 1;
        ewsInfo.charLocationCodeLen = (byte) svcData[idx13];
        Log.d(TAG, "disasterCode:" + ewsInfo.disasterCode);
        Log.d(TAG, "ewsInfo.charInfoMsgNum:" + ewsInfo.charInfoMsgNum);
        Log.d(TAG, "ewsInfo.locationTypeCode:" + ewsInfo.locationTypeCode);
        Log.d(TAG, "ewsInfo.authority:" + ewsInfo.authority);
        Log.d(TAG, "ewsInfo.locationCode:" + ewsInfo.locationCode);
        Log.d(TAG, "ewsInfo.charDisasterCodeLen:" + ewsInfo.charDisasterCodeLen);
        Log.d(TAG, "ewsInfo.charDisasterDateLen:" + ewsInfo.charDisasterDateLen);
        Log.d(TAG, "ewsInfo.charDisasterPositionLen:" + ewsInfo.charDisasterPositionLen);
        Log.d(TAG, "ewsInfo.charDisasterCharactersticLen:" + ewsInfo.charDisasterCharactersticLen);
        Log.d(TAG, "ewsInfo.charLocationCodeLen:" + ewsInfo.charLocationCodeLen);
        Log.d(TAG, "idx:" + idx14);
        for (int i = 0; i < intEwsString.length; i++) {
            intEwsString[i] = svcData[idx14 + i];
            if (i < 20) {
                Log.d(TAG, "intEwsString[]:" + intEwsString[i]);
            }
        }
        ewsInfo.charDisasterCode = _toolConvertAsciiArrayToString(intEwsString);
        int idx15 = idx14 + intEwsString.length;
        Log.d(TAG, "ewsInfo.charDisasterCode:" + ewsInfo.charDisasterCode);
        Log.d(TAG, "idx:" + idx15);
        for (int i2 = 0; i2 < intEwsString.length; i2++) {
            intEwsString[i2] = svcData[idx15 + i2];
        }
        ewsInfo.charDisasterDate = _toolConvertAsciiArrayToString(intEwsString);
        int idx16 = idx15 + intEwsString.length;
        Log.d(TAG, "ewsInfo.charDisasterDate:" + ewsInfo.charDisasterDate);
        Log.d(TAG, "idx:" + idx16);
        for (int i3 = 0; i3 < intEwsString.length; i3++) {
            intEwsString[i3] = svcData[idx16 + i3];
        }
        ewsInfo.charDisasterPosition = _toolConvertAsciiArrayToString(intEwsString);
        int idx17 = idx16 + intEwsString.length;
        Log.d(TAG, "ewsInfo.charDisasterPosition:" + ewsInfo.charDisasterPosition);
        Log.d(TAG, "idx:" + idx17);
        for (int i4 = 0; i4 < intEwsString.length; i4++) {
            intEwsString[i4] = svcData[idx17 + i4];
        }
        ewsInfo.charDisasterCharacterstic = _toolConvertAsciiArrayToString(intEwsString);
        int idx18 = idx17 + intEwsString.length;
        Log.d(TAG, "ewsInfo.charDisasterCharacterstic:" + ewsInfo.charDisasterCharacterstic);
        Log.d(TAG, "idx:" + idx18);
        for (int i5 = 0; i5 < intEwsString.length; i5++) {
            intEwsString[i5] = svcData[idx18 + i5];
        }
        ewsInfo.charLocationCode = _toolConvertAsciiArrayToString(intEwsString);
        Log.d(TAG, "ewsInfo.charLocationCode:" + ewsInfo.charLocationCode);
        Log.d(TAG, "idx:" + (idx18 + intEwsString.length));
        if (ewsInfo.charInfoMsgNum > 0) {
            ewsInfo.infoMsg = new TmdwInfoMsg[ewsInfo.charInfoMsgNum];
            int infoMsfIdx = 0;
            short leftInfoMsgNum = ewsInfo.charInfoMsgNum;
            int[] moreInfoMsg = new int[2201];
            while (true) {
                StringBuilder sb = new StringBuilder();
                int i6 = mwEWSPARet;
                sb.append("leftInfoMsgNum:");
                sb.append(leftInfoMsgNum);
                Log.d(TAG, sb.toString());
                Log.d(TAG, "Current InfoMsg Index:" + leftInfoMsgNum);
                moreInfoMsg[1] = infoMsfIdx;
                moreInfoMsg[2] = 2201;
                Arrays.fill(intEwsInfoMsgString, 0);
                TVNativeWrapper.getInfo_native(b, 4, moreInfoMsg);
                int mwEWSPARet2 = moreInfoMsg[0];
                if (mwEWSPARet2 != 0) {
                    return null;
                }
                ewsInfo.infoMsg[infoMsfIdx] = new TmdwInfoMsg();
                ewsInfo.infoMsg[infoMsfIdx].charInfoMsgLen = (short) moreInfoMsg[1];
                Log.d(TAG, "Current InfoMsg Length:" + ewsInfo.infoMsg[infoMsfIdx].charInfoMsgLen);
                for (int i7 = 0; i7 < ewsInfo.infoMsg[infoMsfIdx].charInfoMsgLen; i7++) {
                    intEwsInfoMsgString[i7] = moreInfoMsg[i7 + 2];
                }
                ewsInfo.infoMsg[infoMsfIdx].charInfoMsg = _toolConvertAsciiArrayToString(intEwsInfoMsgString);
                Log.d(TAG, "Current InfoMsg:" + ewsInfo.infoMsg[infoMsfIdx].charInfoMsg);
                infoMsfIdx++;
                leftInfoMsgNum = (short) (leftInfoMsgNum + -1);
                if (leftInfoMsgNum <= 0) {
                    Log.d(TAG, "getEWSInfo exit");
                    return ewsInfo;
                }
                mwEWSPARet = mwEWSPARet2;
                b = monitorType;
            }
        } else {
            Log.d(TAG, "ewsInfo.charInfoMsgNum is 0! return NULL!");
            return null;
        }
    }

    public boolean bWithEWSInfo(byte monitorType) {
        int[] data = new int[2];
        boolean bWith = false;
        Log.d(TAG, "bWithEWSInfo entered");
        TVNativeWrapper.getInfo_native(monitorType, 2, data);
        if (data[0] != 0) {
            return false;
        }
        if (data[1] == 1) {
            bWith = true;
        }
        Log.d(TAG, "bWithEWSInfo exit");
        return bWith;
    }

    public EwspaRet setLocationCode(byte[] locationCode) {
        int[] data = new int[4];
        Log.d(TAG, "setLocationCode entered");
        data[1] = locationCode[0];
        data[2] = locationCode[1];
        data[3] = locationCode[2];
        TVNativeWrapper.setInfo_native((byte) -1, 1, data);
        if (data[0] != 0) {
            return EwspaRet.EWSPA_RET_INTERNAL_ERROR;
        }
        Log.d(TAG, "setLocationCode exit");
        return EwspaRet.EWSPA_RET_OK;
    }
}
