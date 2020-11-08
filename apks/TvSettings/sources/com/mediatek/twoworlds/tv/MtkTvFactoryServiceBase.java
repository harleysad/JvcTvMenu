package com.mediatek.twoworlds.tv;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class MtkTvFactoryServiceBase {
    public static final String TAG = "MtkTvFactoryService";
    private static Map<Integer, Integer> internalMapTable = new HashMap();

    public int openUARTSerial(int uartSerialID, int[] uartSerialSetting) {
        int[] handle = new int[1];
        if (internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has been opend");
            return 1;
        }
        Log.d(TAG, "enter openUARTSerial");
        int ret = TVNativeWrapper.openUARTSerial_native(uartSerialID, uartSerialSetting, handle);
        Log.d(TAG, "openUARTSerial_native return " + ret + ", handle 0x" + Integer.toHexString(handle[0]));
        return handle[0];
    }

    public int closeUARTSerial(int uartSerialID) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return -1;
        }
        int handle = internalMapTable.get(new Integer(uartSerialID)).intValue();
        Log.d(TAG, "closeUARTSerial the handle 0x" + handle);
        int ret = TVNativeWrapper.closeUARTSerial_native(handle);
        Log.d(TAG, "closeUARTSerial_native return " + ret);
        internalMapTable.remove(new Integer(uartSerialID));
        return ret;
    }

    public int[] getUARTSerialSetting(int uartSerialID) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return null;
        }
        int[] uartSerialSetting = new int[4];
        int ret = TVNativeWrapper.getUARTSerialSetting_native(internalMapTable.get(new Integer(uartSerialID)).intValue(), uartSerialSetting);
        Log.d(TAG, "getUARTSerialSetting uartSerialSetting[0] = " + uartSerialSetting[0]);
        Log.d(TAG, "getUARTSerialSetting uartSerialSetting[1] = " + uartSerialSetting[1]);
        Log.d(TAG, "getUARTSerialSetting uartSerialSetting[2] = " + uartSerialSetting[2]);
        Log.d(TAG, "getUARTSerialSetting uartSerialSetting[3] = " + uartSerialSetting[3]);
        return uartSerialSetting;
    }

    public int setUARTSerialSetting(int uartSerialID, int[] uartSerialSetting) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return -1;
        }
        Log.d(TAG, "setUARTSerialSetting uartSerialSetting[0] = " + uartSerialSetting[0]);
        Log.d(TAG, "setUARTSerialSetting uartSerialSetting[1] = " + uartSerialSetting[1]);
        Log.d(TAG, "setUARTSerialSetting uartSerialSetting[2] = " + uartSerialSetting[2]);
        Log.d(TAG, "setUARTSerialSetting uartSerialSetting[3] = " + uartSerialSetting[3]);
        int ret = TVNativeWrapper.setUARTSerialSetting_native(internalMapTable.get(new Integer(uartSerialID)).intValue(), uartSerialSetting);
        if (ret != 0) {
            Log.d(TAG, "setUARTSerialSetting ret= " + ret);
        }
        return ret;
    }

    public int getUARTSerialOperationMode(int uartSerialID) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return -1;
        }
        int[] operationMode = new int[1];
        Log.d(TAG, "enter getUARTSerialSetting");
        int ret = TVNativeWrapper.getUARTSerialOperationMode_native(internalMapTable.get(new Integer(uartSerialID)).intValue(), operationMode);
        if (ret != 0) {
            Log.d(TAG, "getUARTSerialOperationMode ret= " + ret);
        }
        Log.d(TAG, "leave getUARTSerialSetting operationMode[0]=" + operationMode[0]);
        return operationMode[0];
    }

    public int setUARTSerialOperationMode(int uartSerialID, int operationMode) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return -1;
        }
        Log.d(TAG, "enter setUARTSerialSetting");
        int ret = TVNativeWrapper.setUARTSerialOperationMode_native(internalMapTable.get(new Integer(uartSerialID)).intValue(), operationMode);
        if (ret != 0) {
            Log.d(TAG, "setUARTSerialOperationMode ret= " + ret);
        }
        Log.d(TAG, "leave setUARTSerialOperationMode operationMode=" + operationMode);
        return ret;
    }

    public int outputUARTSerial(int uartSerialID, byte[] uartSerialData) {
        if (!internalMapTable.containsKey(new Integer(uartSerialID))) {
            Log.d(TAG, "the " + uartSerialID + " serial has not been found");
            return -1;
        }
        Log.d(TAG, "enter outputUARTSerial, uartSerialID=" + uartSerialID);
        Log.d(TAG, "enter outputUARTSerial, handle=" + internalMapTable.get(new Integer(uartSerialID)).intValue());
        int ret = TVNativeWrapper.outputUARTSerial_native(internalMapTable.get(new Integer(uartSerialID)).intValue(), uartSerialData);
        if (ret != 0) {
            Log.d(TAG, "outputUARTSerial_native ret= " + ret);
        }
        Log.d(TAG, "leave outputUARTSerial");
        return ret;
    }

    public int factoryWriteKey(String keyType, String srcPath) {
        Log.d(TAG, "Enter factoryWriteKey(keyType" + keyType + ", srcPath=" + srcPath + "\n");
        return TVNativeWrapper.factoryWriteKey_native(keyType, srcPath);
    }

    public int factoryWriteKeyFinish() {
        Log.d(TAG, "Enter factoryWriteKeyFinish\n");
        return TVNativeWrapper.factoryWriteKeyFinish_native();
    }

    public int factoryCheckKey(String keyType) {
        Log.d(TAG, "Enter factoryCheckKey(keyType" + keyType + "\n");
        if (!keyType.equals("KM_ATTEST_KEY")) {
            return TVNativeWrapper.factoryCheckKey_native(keyType);
        }
        try {
            return MtkTvKeymasterKeyAttestation.testEcAttestationChain() | MtkTvKeymasterKeyAttestation.testRsaAttestationChain();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
