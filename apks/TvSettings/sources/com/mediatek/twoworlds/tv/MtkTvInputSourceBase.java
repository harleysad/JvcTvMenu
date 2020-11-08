package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;

public class MtkTvInputSourceBase {
    public static final String INPUT_OUTPUT_MAIN = "main";
    public static final String INPUT_OUTPUT_SUB = "sub";
    public static final int INPUT_RET_FAIL = -1;
    public static final int INPUT_RET_OK = 0;
    public static final String INPUT_TYPE_ATV = "atv";
    public static final String INPUT_TYPE_AV = "av";
    public static final String INPUT_TYPE_COMPONENT = "component";
    public static final String INPUT_TYPE_COMPOSITE = "composite";
    public static final String INPUT_TYPE_DTV = "dtv";
    public static final String INPUT_TYPE_HDMI = "hdmi";
    public static final String INPUT_TYPE_SCART = "scart";
    public static final String INPUT_TYPE_SVIDEO = "svideo";
    public static final String INPUT_TYPE_TV = "tv";
    public static final String INPUT_TYPE_VGA = "vga";
    public static final int MAX_SOURCE_NUM = 12;
    public static final String TAG = "TV_MtkTvInputSource";
    private static InputSourceRecord[] mtkTvInputSourceRecord;
    private static int sourceNumber;

    public enum InputState {
        INPUT_STATE_UNKNOWN(0),
        INPUT_STATE_NORMAL(1),
        INPUT_STATE_PIP(2),
        INPUT_STATE_POP(3);
        
        private int mInputValue;

        private InputState(int InputState) {
            this.mInputValue = InputState;
        }

        public int getInputValue() {
            return this.mInputValue;
        }
    }

    public enum InputSourceType {
        INP_SRC_TYPE_UNKNOWN(0),
        INP_SRC_TYPE_TV(1),
        INP_SRC_TYPE_AV(2),
        INP_SRC_TYPE_1394(3),
        INP_SRC_TYPE_VTRL(4),
        INP_SRC_TYPE_MM(5);
        
        private int mInputValue;

        private InputSourceType(int InputSourceType) {
            this.mInputValue = InputSourceType;
        }

        public int getInputValue() {
            return this.mInputValue;
        }
    }

    public enum InputDeviceType {
        TV(0),
        COMPOSITE(1),
        SVIDEO(2),
        SCART(3),
        COMPONENT(4),
        HDMI(5),
        VGA(6),
        RESERVED(7),
        MAX_NUM(8);
        
        private int mInputValue;

        private InputDeviceType(int InputDeviceType) {
            this.mInputValue = InputDeviceType;
        }

        public int getInputValue() {
            return this.mInputValue;
        }
    }

    public static class InputSourceRecord {
        public static final int INPS_TYPE_NUMERICAL_COMPONENT = 4;
        public static final int INPS_TYPE_NUMERICAL_COMPOSITE = 1;
        public static final int INPS_TYPE_NUMERICAL_HDMI = 5;
        public static final int INPS_TYPE_NUMERICAL_MAX_NUM = 8;
        public static final int INPS_TYPE_NUMERICAL_RESERVED = 7;
        public static final int INPS_TYPE_NUMERICAL_SCART = 3;
        public static final int INPS_TYPE_NUMERICAL_SVIDEO = 2;
        public static final int INPS_TYPE_NUMERICAL_TV = 0;
        public static final int INPS_TYPE_NUMERICAL_VGA = 6;
        private static final String TAG = "InputSourceRecord";
        private int id;
        private int iidCount;
        private int inputType;
        private int internalIdx;
        private String sourceName;
        private InputSourceType sourceType;

        public InputSourceRecord() {
        }

        public InputSourceRecord(int id2, int internalIdx2, int iidCount2, String sourceName2, InputSourceType sourceType2, int inputType2) {
            this.id = id2;
            this.internalIdx = internalIdx2;
            this.iidCount = iidCount2;
            this.sourceName = sourceName2;
            this.sourceType = sourceType2;
            this.inputType = inputType2;
        }

        public int getId() {
            return this.id;
        }

        public int getInternalIdx() {
            return this.internalIdx;
        }

        public int getIidCount() {
            return this.iidCount;
        }

        public InputSourceType getSourceType() {
            return this.sourceType;
        }

        public InputDeviceType getInputType() {
            switch (this.inputType) {
                case 0:
                    return InputDeviceType.TV;
                case 1:
                    return InputDeviceType.COMPOSITE;
                case 2:
                    return InputDeviceType.SVIDEO;
                case 3:
                    return InputDeviceType.SCART;
                case 4:
                    return InputDeviceType.COMPONENT;
                case 5:
                    return InputDeviceType.HDMI;
                case 6:
                    return InputDeviceType.VGA;
                case 7:
                    return InputDeviceType.RESERVED;
                default:
                    return InputDeviceType.MAX_NUM;
            }
        }

        public String getSourceName() {
            return this.sourceName;
        }

        public void setId(int id2) {
            this.id = id2;
        }

        public void setInternalIdx(int internalIdx2) {
            this.internalIdx = internalIdx2;
        }

        public void setIidCount(int iidCount2) {
            this.iidCount = iidCount2;
        }

        public void setSourceType(InputSourceType sourceType2) {
            this.sourceType = sourceType2;
        }

        public void setInputType(int inputType2) {
            this.inputType = inputType2;
        }

        public void setSourceName(String sourceName2) {
            this.sourceName = sourceName2;
        }

        public void clone(InputSourceRecord another) {
            this.id = another.id;
            this.internalIdx = another.internalIdx;
            this.iidCount = another.iidCount;
            this.sourceName = another.sourceName;
            this.sourceType = another.sourceType;
            this.inputType = another.inputType;
        }
    }

    public MtkTvInputSourceBase() {
        try {
            sourceNumber = getInputSourceTotalNumber();
            mtkTvInputSourceRecord = new InputSourceRecord[sourceNumber];
            int idx = 0;
            while (idx < sourceNumber) {
                mtkTvInputSourceRecord[idx] = new InputSourceRecord();
                int ret = getInputSourceRecbyidx(idx, mtkTvInputSourceRecord[idx]);
                if (ret == 0) {
                    idx++;
                } else {
                    Log.d(TAG, "!!!!!FAIL to Create a instance of class MtkTvInputSource\n");
                    throw new MtkTvExceptionBase(ret, "getCurrentChannel_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
    }

    public int getInputSourceTotalNumber() {
        Log.d(TAG, "Enter getInputSourceTotalNumber\n");
        int totalnum = TVNativeWrapper.getInputSourceTotalNumber_native();
        if (totalnum < 0) {
            try {
                Log.d(TAG, "Fail to getInputSourceTotalNumber, get totalnum fail \n");
                throw new MtkTvExceptionBase("getInputSourceTotalNumber fail");
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        return totalnum;
    }

    public int getInputSourceRecbyidx(int idx, InputSourceRecord rec) {
        Log.d(TAG, "Enter getInputSourceRecbyidx, idx = " + idx + "\n");
        return TVNativeWrapper.getInputSourceRecbyidx_native(idx, rec);
    }

    public String getCurrentInputSourceName() {
        return TVNativeWrapper.getCurrentInputSourceName_native();
    }

    public String getCurrentInputSourceName(String path) {
        return TVNativeWrapper.getCurrentInputSourceName_native(path);
    }

    public String getInputSourceNamebySourceid(int source_id) {
        String srcname = null;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    srcname = TVNativeWrapper.getInputSourceNamebySourceid_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return srcname;
        }
        Log.d(TAG, "Fail to getInputSourceNamebySourceid, source_id illegal \n");
        throw new MtkTvExceptionBase("getInputSourceNamebySourceid fail");
    }

    public int changeInputSourcebySourceid(int source_id) {
        Log.d(TAG, "Enter changeInputSourcebySourceid, source_id = " + source_id + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    ret = TVNativeWrapper.changeInputSourcebySourceid_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to changeInputSourcebySourceid, source_id illegal \n");
        throw new MtkTvExceptionBase("changeInputSourcebySourceid fail");
    }

    public int changeInputSourcebySourceid(int source_id, String path) {
        Log.d(TAG, "Enter changeInputSourcebySourceid, source_id = " + source_id + ", designate path = " + path + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id != mtkTvInputSourceRecord[idx].getId() || (!path.equals("main") && !path.equals("sub"))) {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        ret = TVNativeWrapper.changeInputSourcebySourceid_native(source_id, path);
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to changeInputSourcebySourceid, source_id or path illegal \n");
        throw new MtkTvExceptionBase("changeInputSourcebySourceid fail");
    }

    public boolean isBlockEx(int source_id) {
        boolean isblock = false;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    isblock = TVNativeWrapper.isBlockEx_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            Log.d(TAG, "Leave isBlockEx, source_id = " + source_id + " isblock = " + isblock + "\n");
            return isblock;
        }
        Log.d(TAG, "Fail to isBlock, source_id illegal \n");
        throw new MtkTvExceptionBase("isBlock fail");
    }

    public boolean isBlock(int source_id) {
        boolean isblock = false;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    isblock = TVNativeWrapper.isBlock_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            Log.d(TAG, "Leave isBlock, source_id = " + source_id + " isblock = " + isblock + "\n");
            return isblock;
        }
        Log.d(TAG, "Fail to isBlock, source_id illegal \n");
        throw new MtkTvExceptionBase("isBlock fail");
    }

    public int block(int source_id, boolean block) {
        Log.d(TAG, "Enter block, source_id = " + source_id + ", block = " + block + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    ret = TVNativeWrapper.block_native(source_id, block);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to block, source_id illegal \n");
        throw new MtkTvExceptionBase("block fail");
    }

    public boolean isAutoDetectPlugStatus(int source_id) {
        boolean autodetect = false;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    autodetect = TVNativeWrapper.isAutoDetectPlugStatus_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            Log.d(TAG, "Leave isAutoDetectPlugStatus, source_id = " + source_id + " autodetect = " + autodetect + "\n");
            return autodetect;
        }
        Log.d(TAG, "Fail to isAutoDetectPlugStatus, source_id illegal \n");
        throw new MtkTvExceptionBase("isAutoDetectPlugStatus fail");
    }

    public int setAutoDetectPlugStatus(int source_id, boolean needDetect) {
        Log.d(TAG, "Enter setAutoDetectPlugStatus, source_id = " + source_id + ", needDetect = " + needDetect + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    ret = TVNativeWrapper.setAutoDetectPlugStatus_native(source_id, needDetect);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to setAutoDetectPlugStatus, source_id illegal \n");
        throw new MtkTvExceptionBase("setAutoDetectPlugStatus fail");
    }

    public int getInputLabelIdx(int source_id) {
        int labelidx = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    labelidx = TVNativeWrapper.getInputLabelIdx_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            Log.d(TAG, "Leave getInputLabelIdx, labelidx = " + labelidx + "\n");
            return labelidx;
        }
        Log.d(TAG, "Fail to getInputLabelIdx, source_id illegal \n");
        throw new MtkTvExceptionBase("getInputLabelIdx fail");
    }

    public int setInputLabelIdx(int source_id, int labelidx) {
        Log.d(TAG, "Enter setInputLabelIdx, source_id = " + source_id + ", labelidex = " + labelidx + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    ret = TVNativeWrapper.setInputLabelIdx_native(source_id, labelidx);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to setInputLabelIdx, source_id illegal \n");
        throw new MtkTvExceptionBase("setInputLabelIdx fail");
    }

    public String getInputLabelUserDefName(int source_id) {
        String userdefname = new String("");
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    userdefname = TVNativeWrapper.getInputLabelUserDefName_native(source_id);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            Log.d(TAG, "Leave getInputLabelUserDefName, source_id = " + source_id + " userdefname = " + userdefname + "\n");
            return userdefname;
        }
        Log.d(TAG, "Fail to getInputLabelUserDefName, source_id illegal \n");
        throw new MtkTvExceptionBase("getInputLabelUserDefName fail");
    }

    public int setInputLabelUserDefName(int source_id, String userdef) {
        Log.d(TAG, "Enter setInputLabelUserDefName, source_id = " + source_id + ", userdef = " + userdef + "\n");
        int ret = 0;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (source_id == mtkTvInputSourceRecord[idx].getId()) {
                    ret = TVNativeWrapper.setInputLabelUserDefName_native(source_id, userdef);
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            return ret;
        }
        Log.d(TAG, "Fail to setInputLabelUserDefName, source_id illegal \n");
        throw new MtkTvExceptionBase("setInputLabelUserDefName fail");
    }

    public boolean getExternalDeviceHasSignal(InputDeviceType inputType, int internalIdx) {
        boolean b_find = false;
        int idx = 0;
        while (true) {
            try {
                if (idx < sourceNumber) {
                    if (inputType == mtkTvInputSourceRecord[idx].getInputType() && internalIdx == mtkTvInputSourceRecord[idx].getInternalIdx()) {
                        switch (inputType) {
                            case COMPOSITE:
                            case SVIDEO:
                            case SCART:
                            case COMPONENT:
                            case HDMI:
                            case VGA:
                                b_find = true;
                                break;
                        }
                        if (b_find) {
                        }
                    }
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (b_find) {
            boolean hasSignal = TVNativeWrapper.getExternalDeviceHasSignal_native(inputType, internalIdx);
            Log.d(TAG, "Leave getExternalDeviceHasSignal, hasSignal = " + hasSignal + "\n");
            return hasSignal;
        }
        Log.d(TAG, "Fail to getExternalDeviceHasSignal, param illegal \n");
        throw new MtkTvExceptionBase("getExternalDeviceHasSignal fail");
    }

    public int init() {
        return 0;
    }

    public int inputSyncStop(String path, boolean force) {
        Log.d(TAG, "Enter inputSyncStop\n");
        return TVNativeWrapper.inputSyncStop_native(path, force);
    }

    public boolean queryConflict(int sourceId1, int sourceId2) {
        boolean isConflict = false;
        int idx = 0;
        while (true) {
            try {
                if (idx >= sourceNumber) {
                    break;
                } else if (sourceId1 == mtkTvInputSourceRecord[idx].getId()) {
                    break;
                } else {
                    idx++;
                }
            } catch (MtkTvExceptionBase e) {
                e.printStackTrace();
            }
        }
        if (idx < sourceNumber) {
            int idx2 = 0;
            while (true) {
                if (idx2 >= sourceNumber) {
                    break;
                } else if (sourceId2 == mtkTvInputSourceRecord[idx2].getId()) {
                    break;
                } else {
                    idx2++;
                }
            }
            if (idx2 < sourceNumber) {
                isConflict = TVNativeWrapper.queryConflict_native(sourceId1, sourceId2);
                Log.d(TAG, "Leave queryConflict, sourceId1 = " + sourceId1 + "sourceId2 = " + sourceId2 + "isConflict = " + isConflict + "\n");
                return isConflict;
            }
            Log.d(TAG, "Fail to queryConflict, sourceId2 illegal \n");
            throw new MtkTvExceptionBase("queryConflict fail");
        }
        Log.d(TAG, "Fail to queryConflict, sourceId1 illegal \n");
        throw new MtkTvExceptionBase("queryConflict fail");
    }

    public int getMhlPortNum() {
        return TVNativeWrapper.getMhlPortNum_native();
    }

    public boolean checkIsMenuTvBlock() {
        Log.d(TAG, "Enter checkIsMenuTvBlock\n");
        return TVNativeWrapper.menuIsTvBlock_native();
    }

    public int setScartAutoJump(boolean enable) {
        Log.d(TAG, "Enter setScartAutoJump, enable = " + enable + "\n");
        return TVNativeWrapper.setScartAutoJump_native(enable);
    }
}
