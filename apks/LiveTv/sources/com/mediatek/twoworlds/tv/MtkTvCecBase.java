package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecActiveSourceBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecDevDiscoveryInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecRecordSouceInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecTimeInfoBase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MtkTvCecBase {
    /* access modifiers changed from: private */
    public static int CEC_DEV_VNDR_ID_SIZE = 3;
    /* access modifiers changed from: private */
    public static int CEC_USER_CTRL_OPERAND_SIZE = 4;
    /* access modifiers changed from: private */
    public static int CEC_VNDR_CMD_WITH_ID_SIZE = 11;
    private static final String TAG = "MtkTvCec";

    public enum CecDevType {
        CEC_DEV_TYPE_TV,
        CEC_DEV_TYPE_REC_DEV,
        CEC_DEV_TYPE_RESERVED,
        CEC_DEV_TYPE_TUNER,
        CEC_DEV_TYPE_PLAYBACK_DEV,
        CEC_DEV_TYPE_AUD_SYS,
        CEC_DEV_TYPE_NONE
    }

    public enum CecLogAddr {
        CEC_LOG_ADDR_TV,
        CEC_LOG_ADDR_REC_DEV_1,
        CEC_LOG_ADDR_REC_DEV_2,
        CEC_LOG_ADDR_TUNER_1,
        CEC_LOG_ADDR_PLAYBACK_DEV_1,
        CEC_LOG_ADDR_AUD_SYS,
        CEC_LOG_ADDR_TUNER_2,
        CEC_LOG_ADDR_TUNER_3,
        CEC_LOG_ADDR_PLAYBACK_DEV_2,
        CEC_LOG_ADDR_REC_DEV_3,
        CEC_LOG_ADDR_TUNER_4,
        CEC_LOG_ADDR_PLAYBACK_DEV_3,
        CEC_LOG_ADDR_RESERVED_1,
        CEC_LOG_ADDR_RESERVED_2,
        CEC_LOG_ADDR_FREE_USE,
        CEC_LOG_ADDR_UNREGED_BRDCST,
        CEC_LOG_ADDR_MAX
    }

    public enum CecUIMsgType {
        CEC_UI_MSG_CEC_OTP,
        CEC_UI_MSG_SAC_AUD_STS,
        CEC_UI_MSG_CEC_SAC_STS
    }

    public int setStandby(int logAddr) {
        Log.d(TAG, "+ setStandby.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setStandby_native begin");
                ret = TVNativeWrapper.setStandby_native(logAddr);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setStandby_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setStandby.");
        return ret;
    }

    public class CecUserCtrlInfo {
        private int ilogAddr;
        private CecLogAddr logAddr;
        private int userCtrlCode;
        private int[] userCtrlOperand = new int[MtkTvCecBase.CEC_USER_CTRL_OPERAND_SIZE];
        private int userCtrlOperandSize;

        public CecUserCtrlInfo() {
        }

        public CecLogAddr getLogAddr() {
            return this.logAddr;
        }

        public int getiLogAddr() {
            return this.ilogAddr;
        }

        public void setLogAddr(CecLogAddr logAddr2) {
            this.ilogAddr = logAddr2.ordinal();
            Log.d(MtkTvCecBase.TAG, "ilogAddr " + this.ilogAddr);
            this.logAddr = logAddr2;
        }

        public int getUserCtrlCode() {
            return this.userCtrlCode;
        }

        public void setUserCtrlCode(int userCtrlCode2) {
            this.userCtrlCode = userCtrlCode2;
        }

        public int getUserCtrlOperandSize() {
            return this.userCtrlOperandSize;
        }

        public void setUserCtrlOperandSize(int userCtrlOperandSize2) {
            this.userCtrlOperandSize = userCtrlOperandSize2;
        }

        public int[] getUserCtrlOperand() {
            return this.userCtrlOperand;
        }

        public void setUserCtrlOperand(int[] userCtrlOperand2) {
            this.userCtrlOperand = userCtrlOperand2;
            Log.d(MtkTvCecBase.TAG, "userCtrlOperand " + Arrays.toString(this.userCtrlOperand));
        }
    }

    public int setUserCtrlPressed(CecUserCtrlInfo userCtrlInfo) {
        Log.d(TAG, "Enter setUserCtrlPressed.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setUserCtrlPressed_native begin");
                ret = TVNativeWrapper.setUserCtrlPressed_native(userCtrlInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setUserCtrlPressed_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Leave setUserCtrlPressed.");
        return ret;
    }

    public int setUserCtrlReleased(CecUserCtrlInfo userCtrlInfo) {
        Log.d(TAG, "+ setUserCtrlReleased.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setUserCtrlReleased_native begin");
                ret = TVNativeWrapper.setUserCtrlReleased_native(userCtrlInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setUserCtrlReleased_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setUserCtrlReleased.");
        return ret;
    }

    public int setSystemAudioModeRequest(int sysAudioMode) {
        Log.d(TAG, "+ setSystemAudioModeRequest.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setSystemAudioModeRequest_native begin");
                ret = TVNativeWrapper.setSystemAudioModeRequest_native(sysAudioMode);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setSystemAudioModeRequest_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setSystemAudioModeRequest.");
        return ret;
    }

    public int setTimer(int logAddr, MtkTvCecTimeInfoBase timerInfo) {
        Log.d(TAG, "+ setTimer.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setTimer_native begin");
                ret = TVNativeWrapper.setTimer_native(logAddr, timerInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setTimer_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setTimer.");
        return ret;
    }

    public int setRecordOn(int logAddr, MtkTvCecRecordSouceInfoBase recordSourceInfo) {
        Log.d(TAG, "+ setRecordOn.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setRecordOn_native begin");
                ret = TVNativeWrapper.setRecordOn_native(logAddr, recordSourceInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setRecordOn_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setRecordOn.");
        return ret;
    }

    public int setRecordOff(int logAddr) {
        Log.d(TAG, "+ setRecordOff.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setRecordOff_native begin");
                ret = TVNativeWrapper.setRecordOff_native(logAddr);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setRecordOff_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setRecordOff.");
        return ret;
    }

    public class CecVndrCmdWithIdInfo {
        private CecLogAddr destLogAddr;
        private CecLogAddr initLogAddr;
        private int[] vndrCmdWithId = new int[MtkTvCecBase.CEC_VNDR_CMD_WITH_ID_SIZE];
        private int vndrCmdWithIdSize;
        private int[] vndrId = new int[MtkTvCecBase.CEC_DEV_VNDR_ID_SIZE];

        public CecVndrCmdWithIdInfo() {
        }

        public CecLogAddr getInitLogAddr() {
            return this.initLogAddr;
        }

        public int getiInitLogAddr() {
            return this.initLogAddr.ordinal();
        }

        public void setInitLogAddr(CecLogAddr initLogAddr2) {
            this.initLogAddr = initLogAddr2;
        }

        public CecLogAddr getDestLogAddr() {
            return this.destLogAddr;
        }

        public int getiDestLogAddr() {
            return this.destLogAddr.ordinal();
        }

        public void setDestLogAddr(CecLogAddr destLogAddr2) {
            this.destLogAddr = destLogAddr2;
        }

        public int[] getVndrId() {
            return this.vndrId;
        }

        public void setVndrId(int[] vndrId2) {
            this.vndrId = vndrId2;
            Log.d(MtkTvCecBase.TAG, "vndrId " + Arrays.toString(this.vndrId));
        }

        public int getvndrCmdWithIdSize() {
            return this.vndrCmdWithIdSize;
        }

        public void setvndrCmdWithIdSize(int vndrCmdWithIdSize2) {
            this.vndrCmdWithIdSize = vndrCmdWithIdSize2;
        }

        public int[] getVndrCmdWithId() {
            return this.vndrCmdWithId;
        }

        public void setVndrCmdWithId(int[] vndrCmdWithId2) {
            this.vndrCmdWithId = vndrCmdWithId2;
            Log.d(MtkTvCecBase.TAG, "vndrCmdWithId " + Arrays.toString(this.vndrCmdWithId));
        }
    }

    public int setVendorCmdWithId(CecVndrCmdWithIdInfo vndrCmdWithIdInfo) {
        Log.d(TAG, "+ setVendorCmdWithId.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setVendorCmdWithId_native begin");
                ret = TVNativeWrapper.setVendorCmdWithId_native(vndrCmdWithIdInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setVendorCmdWithId_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setVendorCmdWithId.");
        return ret;
    }

    public int isDeviceExist(int logAddr) {
        Log.d(TAG, "+ isDeviceExist.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "isDeviceExist_native begin");
                ret = TVNativeWrapper.isDeviceExist_native(logAddr);
                if (ret != 0) {
                    if (ret != 1) {
                        throw new MtkTvExceptionBase(ret, "TVNativeWrapper isDeviceExist_native fail");
                    }
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- isDeviceExist.");
        return ret;
    }

    public class CecDevInfo {
        private boolean ampconnected;
        private String devName;
        protected byte[] devVndrId = new byte[MtkTvCecBase.CEC_DEV_VNDR_ID_SIZE];
        private CecDevType devtype;
        private String hdmiPort;
        private CecLogAddr logaddr;
        private String osdName;
        private int phyaddr;

        public CecDevInfo() {
        }

        public String toString() {
            return "devType= " + this.devtype + "\ndevName= " + this.devName + "\nhdmiPort= " + this.hdmiPort + "\nosdName= " + this.osdName + "\nampconnected= " + this.ampconnected + "\nlogaddr= " + this.logaddr + "\nphyaddr= 0x" + Integer.toHexString(this.phyaddr) + "\ndevVndrId= " + Arrays.toString(this.devVndrId);
        }

        public CecDevType getDevType() {
            return this.devtype;
        }

        public void setDevType(CecDevType devType) {
            this.devtype = devType;
        }

        public String getDevName() {
            return this.devName;
        }

        public void setDevName(String devName2) {
            this.devName = devName2;
        }

        public String getHdmiPort() {
            return this.hdmiPort;
        }

        public void setHdmiPort(String hdmiPort2) {
            this.hdmiPort = hdmiPort2;
        }

        public String getOsdName() {
            return this.osdName;
        }

        public void setOsdName(String osdName2) {
            this.osdName = osdName2;
        }

        public boolean getAmpConnected() {
            return this.ampconnected;
        }

        public void setAmpConnected(boolean ampConnected) {
            this.ampconnected = ampConnected;
        }

        public CecLogAddr getLogAddr() {
            return this.logaddr;
        }

        public void setLogAddr(CecLogAddr logAddr) {
            this.logaddr = logAddr;
        }

        public int getPhyAddr() {
            return this.phyaddr;
        }

        public void setPhyAddr(int phyAddr) {
            this.phyaddr = phyAddr;
        }

        public byte[] getVndrId() {
            return this.devVndrId;
        }

        public void setVndrId(byte[] vndrId) {
            this.devVndrId = vndrId;
        }
    }

    public CecDevInfo getCecDevInfo(int logAddr) {
        Log.d(TAG, "+ getCecDevInfo.");
        CecDevInfo cecDevInfo = new CecDevInfo();
        try {
            synchronized (this) {
                Log.d(TAG, "getCecDevInfo_native begin");
                int ret = TVNativeWrapper.getCecDevInfo_native(logAddr, cecDevInfo);
                if (ret == 0) {
                    Log.d(TAG, "The device:\n" + cecDevInfo.toString() + "\n");
                } else {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getCecDevInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- getCecDevInfo.");
        return cecDevInfo;
    }

    public List<CecDevInfo> getCecDevListInfo() {
        Log.d(TAG, "Enter getCecDevListInfo.");
        List<CecDevInfo> cecDevList = new ArrayList<>();
        try {
            synchronized (this) {
                Log.d(TAG, "getCecDevListInfo_native begin");
                int ret = TVNativeWrapper.getCecDevListInfo_native(cecDevList);
                if (ret == 0) {
                    Log.d(TAG, "cecDevList TotalNumber = " + cecDevList.size() + "\n");
                    for (int i = 0; i < cecDevList.size(); i++) {
                        Log.d(TAG, "The " + i + " device:\n" + cecDevList.get(i) + "\n");
                    }
                } else {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getCecDevListInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Leave getCecDevListInfo.");
        return cecDevList;
    }

    public MtkTvCecActiveSourceBase getActiveSourceInfo() {
        Log.d(TAG, "+ getActiveSourceInfo.");
        MtkTvCecActiveSourceBase cecActSrcInfo = new MtkTvCecActiveSourceBase();
        try {
            synchronized (this) {
                Log.d(TAG, "getActiveSourceInfo_native begin");
                int ret = TVNativeWrapper.getActiveSourceInfo_native(cecActSrcInfo);
                if (ret == 0) {
                    Log.d(TAG, "The device:\n" + cecActSrcInfo.toString() + "\n");
                } else {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getActiveSourceInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- getActiveSourceInfo.");
        return cecActSrcInfo;
    }

    public int notifyCecCompInfo(String notifyType, int notifyData, String notifyString) {
        Log.d(TAG, "+ notifyCecCompInfo.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "notifyCecCompInfo_native begin");
                ret = TVNativeWrapper.notifyCecCompInfo_native(notifyType, notifyData, notifyString);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "notifyCecCompInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- notifyCecCompInfo.");
        return ret;
    }

    public int discoveryDevice(MtkTvCecDevDiscoveryInfoBase cecDiscoveryInfo) {
        Log.d(TAG, "+ discoveryDevice.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "discoveryDevice_native begin");
                ret = TVNativeWrapper.discoveryDevice_native(cecDiscoveryInfo);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "discoveryDevice_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- discoveryDevice.");
        return ret;
    }

    public int powerOnDeviceByLogicAddr(int logAddr) {
        Log.d(TAG, "+ powerOnDeviceByLogicAddr.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "powerOnDeviceByLogicAddr_native begin");
                ret = TVNativeWrapper.powerOnDeviceByLogicAddr_native(logAddr);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "powerOnDeviceByLogicAddr_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- powerOnDeviceByLogicAddr.");
        return ret;
    }

    public int setStandbyToAll(boolean sync) {
        Log.d(TAG, "+ setStandbyToAll.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setStandbyToAll_native begin");
                ret = TVNativeWrapper.setStandbyToAll_native(sync);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setStandbyToAll_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setStandbyToAll.");
        return ret;
    }
}
