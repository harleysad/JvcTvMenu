package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvDvbsConfigBase {
    public static final int DVBS_CFG_SATL_MASK_LNB_SINGCAB = 32768;
    public static final int DVBS_CFG_SATL_MASK_LNB_UNIVER = 16384;
    public static final int DVBS_CFG_SATL_MASK_STATUS_ON = 8192;
    static final String TAG = "MtkTvDvbsConfigBase";

    public int satllistLockDatabase(int satlId) {
        Log.d(TAG, "+ satllistLockDatabase.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "satllistLockDatabase_native begin");
                ret = TVNativeWrapper.satllistLockDatabase_native(satlId);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "satllistLockDatabase_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- satllistLockDatabase.");
        return ret;
    }

    public int satllistUnLockDatabase(int satlId) {
        Log.d(TAG, "+ satllistUnLockDatabase.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "satllistUnLockDatabase_native begin");
                ret = TVNativeWrapper.satllistUnLockDatabase_native(satlId);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "satllistUnLockDatabase_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- satllistUnLockDatabase.");
        return ret;
    }

    public int satllistReadLockDatabase(int satlId) {
        Log.d(TAG, "+ satllistReadLockDatabase.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "satllistReadLockDatabase_native begin");
                ret = TVNativeWrapper.satllistReadLockDatabase_native(satlId);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "satllistReadLockDatabase_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- satllistReadLockDatabase.");
        return ret;
    }

    public int satllistReadUnLockDatabase(int satlId) {
        Log.d(TAG, "+ satllistReadUnLockDatabase.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "satllistReadUnLockDatabase_native begin");
                ret = TVNativeWrapper.satllistReadUnLockDatabase_native(satlId);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "satllistReadUnLockDatabase_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- satllistReadUnLockDatabase.");
        return ret;
    }

    public int satllistCleanDatabase(int satlId) {
        Log.d(TAG, "+ satllistCleanDatabase.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "satllistCleanDatabase_native begin");
                ret = TVNativeWrapper.satllistCleanDatabase_native(satlId);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "satllistCleanDatabase_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- satllistCleanDatabase.");
        return ret;
    }

    public List<MtkTvDvbsConfigInfoBase> getSatlRecord(int satlId, int satlRecId) {
        Log.d(TAG, "Enter getSatlRecord(" + satlId + "," + satlRecId + ")\n");
        List<MtkTvDvbsConfigInfoBase> satlList = new ArrayList<>();
        try {
            synchronized (this) {
                Log.d(TAG, "getSatlRecord_native begin");
                int ret = TVNativeWrapper.getSatlRecord_native(satlId, satlRecId, satlList);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "getSatlRecord_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        return satlList;
    }

    public List<MtkTvDvbsConfigInfoBase> getSatlRecordByRecIdx(int satlId, int idx) {
        Log.d(TAG, "Enter getSatlRecord(" + satlId + "," + idx + ")\n");
        List<MtkTvDvbsConfigInfoBase> satlList = new ArrayList<>();
        try {
            synchronized (this) {
                Log.d(TAG, "getSatlRecordByRecIdx_native begin");
                int ret = TVNativeWrapper.getSatlRecordByRecIdx_native(satlId, idx, satlList);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "getSatlRecordByRecIdx_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        return satlList;
    }

    public int getSatlNumRecs(int satlId) {
        int ret = 0;
        Log.d(TAG, "Enter getSatlRecord(" + satlId + ")\n");
        try {
            synchronized (this) {
                Log.d(TAG, "getSatlNumRecs_native begin");
                ret = TVNativeWrapper.getSatlNumRecs_native(satlId);
                if (ret < 0) {
                    throw new MtkTvExceptionBase(ret, "getSatlNumRecs_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updateSatlRecord(int satlId, MtkTvDvbsConfigInfoBase satlList, boolean mustExist) {
        int ret = 0;
        Log.d(TAG, "Enter updateSatlRecord(" + satlId + "," + mustExist + ")\n");
        try {
            synchronized (this) {
                Log.d(TAG, "updateSatlRecord_native begin");
                ret = TVNativeWrapper.updateSatlRecord_native(satlId, satlList, mustExist);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "updateSatlRecord_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int createSatlSnapshot(int satlID) {
        return TVNativeWrapper.createSatlSnapshot_native(satlID);
    }

    public static int restoreSatlSnapshot(int satlsnapshotID) {
        return TVNativeWrapper.restoreSatlSnapshot_native(satlsnapshotID);
    }

    public static int freeSatlSnapshot(int satlsnapshotID) {
        return TVNativeWrapper.freeSatlSnapshot_native(satlsnapshotID);
    }
}
