package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import java.io.File;

public class MtkTvOADBase {
    private static final String TAG = "MtkTvOAD";
    public static int dataType = 0;
    public static String mscheduleInfo = null;
    private static final int typeOtherInfo = 3;
    private static final int typeScheduleInfo = 1;
    private static final int typeUriInfo = 2;

    public void setScheduleString(String str) {
        Log.i(TAG, "setScheduleString enterdstr:" + str);
        mscheduleInfo = str;
        Log.i(TAG, "setScheduleString exit");
    }

    public MtkTvOADBase() {
        Log.d(TAG, "MtkTvOADBase object created");
    }

    public int startManualDetect() {
        Log.d(TAG, "+ startManualDetect");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "startManualDetect_native begin");
                ret = TVNativeWrapper.startManualDetect_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper startManualDetect_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- startManualDetect");
        return ret;
    }

    public int getScheduleInfo() {
        Log.d(TAG, "+ startManualDetect");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "getScheduleInfo_native begin");
                ret = TVNativeWrapper.getScheduleInfo_native(this);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getScheduleInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- getScheduleInfo");
        return ret;
    }

    public String getDataInfo(int dataType2) {
        Log.d(TAG, "+ startManualDetect");
        try {
            synchronized (this) {
                Log.d(TAG, "getDataInfo_native begin");
                int ret = TVNativeWrapper.getDataInfo_native(this, dataType2);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getDataInfo_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- getDataInfo" + mscheduleInfo);
        return mscheduleInfo;
    }

    public int stopManualDetect() {
        Log.d(TAG, "+ stopManualDetect");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "stopManualDetect_native begin");
                ret = TVNativeWrapper.stopManualDetect_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper stopManualDetect_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- stopManualDetect");
        return ret;
    }

    public int startDownload() {
        Log.d(TAG, "+ startDownload");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "startDownload_native begin");
                ret = TVNativeWrapper.startDownload_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper startDownload_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- startDownload");
        return ret;
    }

    public int stopDownload() {
        Log.d(TAG, "+ stopDownload");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "stopDownload_native begin");
                ret = TVNativeWrapper.stopDownload_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper stopDownload_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- stopDownload");
        return ret;
    }

    public int setAutoDownload(boolean bAutoDownload) {
        Log.d(TAG, "+ setAutoDownload");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "setAutoDownload_native begin");
                ret = TVNativeWrapper.setAutoDownload_native(bAutoDownload);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setAutoDownload_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setAutoDownload");
        return ret;
    }

    public int startFlash() {
        Log.d(TAG, "+ startFlash");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "startFlash_native begin");
                ret = TVNativeWrapper.startFlash_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper startFlash_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- startFlash");
        return ret;
    }

    public int acceptRestart() {
        Log.d(TAG, "+ acceptRestart");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "acceptRestart_native begin");
                ret = TVNativeWrapper.acceptRestart_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper acceptRestart_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- acceptRestart");
        return ret;
    }

    public int remindMeLater() {
        Log.d(TAG, "+ remindMeLater");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "remindMeLater_native begin");
                ret = TVNativeWrapper.remindMeLater_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper remindMeLater_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- remindMeLater");
        return ret;
    }

    public int acceptSchedule() {
        Log.d(TAG, "+ acceptSchedule");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "acceptSchedule_native begin");
                ret = TVNativeWrapper.acceptSchedule_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper acceptSchedule_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- acceptSchedule");
        return ret;
    }

    public int startJumpChannel() {
        Log.d(TAG, "+ startJumpChannel");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "startJumpChannel_native begin");
                ret = TVNativeWrapper.startJumpChannel_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper startJumpChannel_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- startJumpChannel");
        return ret;
    }

    public int getPowerOnStatus() {
        Log.d(TAG, "+ getPowerOnStatus");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "getPowerOnStatus_native begin");
                ret = TVNativeWrapper.getPowerOnStatus_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getPowerOnStatus_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- getPowerOnStatus");
        return ret;
    }

    public int setPkgPathname(String pathname) {
        Log.d(TAG, "+ setPkgPathname,pathname:" + pathname);
        int ret = 0;
        String filePathStr = pathname.substring(0, pathname.lastIndexOf("/"));
        Log.d(TAG, "filePathStr:" + filePathStr);
        try {
            synchronized (this) {
                Log.d(TAG, "setPkgPathname_native begin");
                if (!new File(filePathStr).exists()) {
                    Log.e(TAG, "the file path is not exist,set PKG Pathname Fail!");
                    return -1;
                }
                ret = TVNativeWrapper.setPkgPathname_native(pathname);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper setPkgPathname fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- setPkgPathname");
        return ret;
    }

    public int clearOadVersion() {
        Log.d(TAG, "+ getPowerOnStatus");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "clearOadVersion_native begin");
                ret = TVNativeWrapper.clearOadVersion_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper clearOadVersion_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- clearOadVersion");
        return ret;
    }
}
