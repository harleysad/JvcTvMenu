package com.mediatek.wwtv.tvcenter.util;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.SparseArray;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;
import java.util.ArrayList;
import java.util.List;

public class TvCallbackHandler {
    private static final String TAG = "TvCallbackHandler";
    private static TvCallbackHandler mCallback = null;
    /* access modifiers changed from: private */
    public DataSeparaterUtil dataSeparaterUtil;
    private IntenalCallbackHandler mHandler = null;

    private TvCallbackHandler() {
        try {
            this.mHandler = new IntenalCallbackHandler();
            this.dataSeparaterUtil = DataSeparaterUtil.getInstance();
        } catch (Exception ex) {
            MtkLog.d(TAG, "TvCallbackHandler, " + ex);
        }
    }

    public static synchronized TvCallbackHandler getInstance() {
        TvCallbackHandler tvCallbackHandler;
        synchronized (TvCallbackHandler.class) {
            if (mCallback == null) {
                mCallback = new TvCallbackHandler();
            }
            tvCallbackHandler = mCallback;
        }
        return tvCallbackHandler;
    }

    public boolean addCallBackListener(Handler listener) {
        return addCallBackListener(0, listener);
    }

    public boolean addCallBackListener(int callbackId, Handler listener) {
        int key = 65535 & callbackId;
        try {
            if (this.mHandler == null) {
                this.mHandler = new IntenalCallbackHandler();
            }
            List<Handler> handlers = this.mHandler.mHandlers.get(key);
            MtkLog.d(TAG, "AddCallBackListener, key=" + key);
            if (handlers != null) {
                for (Handler handler : handlers) {
                    if (listener == handler) {
                        MtkLog.d(TAG, "AddCallBackListener, already existed");
                        return false;
                    }
                }
            } else {
                MtkLog.d(TAG, "AddCallBackListener, new ArrayList");
                handlers = new ArrayList<>();
                this.mHandler.mHandlers.append(key, handlers);
            }
            return handlers.add(listener);
        } catch (Exception ex) {
            MtkLog.d(TAG, "addCallBackListener, " + ex);
            return false;
        }
    }

    public boolean removeCallBackListener(Handler listener) {
        if (listener == null) {
            return true;
        }
        return removeCallBackListener(0, listener);
    }

    public boolean removeAll() {
        mCallback = null;
        try {
            this.mHandler.mHandlers.clear();
            this.mHandler = null;
            return true;
        } catch (Exception ex) {
            MtkLog.d(TAG, "removeAll, " + ex);
            return true;
        }
    }

    public boolean removeCallBackListener(int callbackId, Handler listener) {
        int key = 65535 & callbackId;
        try {
            List<Handler> handlers = this.mHandler.mHandlers.get(key);
            MtkLog.d(TAG, "removeCallBackListener, key=" + key);
            synchronized (IntenalCallbackHandler.class) {
                if (handlers != null) {
                    handlers.remove(listener);
                }
            }
            return true;
        } catch (Exception ex) {
            MtkLog.d(TAG, "removeCallBackListener, " + ex);
            return true;
        } catch (Throwable th) {
            throw th;
        }
    }

    private class IntenalCallbackHandler extends MtkTvTVCallbackHandler {
        private boolean isClear = false;
        public SparseArray<List<Handler>> mHandlers = new SparseArray<>();

        public IntenalCallbackHandler() {
        }

        private int sendMessage(Message msg) {
            try {
                synchronized (IntenalCallbackHandler.class) {
                    List<Handler> generalHandlers = this.mHandlers.get(0);
                    if (generalHandlers != null) {
                        for (Handler handler : generalHandlers) {
                            Message temp = Message.obtain();
                            temp.copyFrom(msg);
                            if (handler != null) {
                                handler.sendMessage(temp);
                            } else {
                                this.isClear = true;
                            }
                        }
                    }
                    List<Handler> handlers = this.mHandlers.get(msg.what & 65535);
                    if (handlers != null) {
                        for (Handler handler2 : handlers) {
                            if (generalHandlers == null || !generalHandlers.contains(handler2)) {
                                Message temp2 = Message.obtain();
                                temp2.copyFrom(msg);
                                if (handler2 != null) {
                                    handler2.sendMessage(temp2);
                                } else {
                                    this.isClear = true;
                                }
                            }
                        }
                    }
                    if (this.isClear) {
                        this.isClear = false;
                        for (int i = 0; i < this.mHandlers.size(); i++) {
                            List<Handler> tmps = this.mHandlers.get(this.mHandlers.keyAt(i));
                            MtkLog.d(TvCallbackHandler.TAG, "keyAt:" + i + ", key = " + this.mHandlers.keyAt(i));
                            for (int j = 0; j < tmps.size(); j++) {
                                Handler handler3 = tmps.get(j);
                                if (handler3 == null) {
                                    MtkLog.d(TvCallbackHandler.TAG, "handler : (null)");
                                    tmps.remove(j);
                                } else {
                                    MtkLog.d(TvCallbackHandler.TAG, "handler :" + handler3);
                                }
                            }
                        }
                    }
                }
                return 0;
            } catch (Exception ex) {
                ex.printStackTrace();
                return -1;
            }
        }

        public String toString() {
            String str = "";
            try {
                if (this.mHandlers == null) {
                    return str + "empty";
                }
                String str2 = str;
                int i = 0;
                while (i < this.mHandlers.size()) {
                    try {
                        str2 = str2 + "* key:" + this.mHandlers.keyAt(i) + "\n";
                        List<Handler> temp = this.mHandlers.get(this.mHandlers.keyAt(i));
                        if (temp != null) {
                            String str3 = str2;
                            int j = 0;
                            while (j < temp.size()) {
                                try {
                                    if (temp.get(j) != null) {
                                        str3 = str3 + "handler: " + temp.get(j).toString() + "\n";
                                    } else {
                                        str3 = str3 + "handler: null\n";
                                    }
                                    j++;
                                } catch (Exception e) {
                                    ex = e;
                                    str = str3;
                                    ex.printStackTrace();
                                    return str;
                                }
                            }
                            str2 = str3;
                        }
                        i++;
                    } catch (Exception e2) {
                        ex = e2;
                        str = str2;
                        ex.printStackTrace();
                        return str;
                    }
                }
                return str2;
            } catch (Exception e3) {
                ex = e3;
                ex.printStackTrace();
                return str;
            }
        }

        private TvCallbackData formatData(int param1) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            return backData;
        }

        private TvCallbackData formatData(int param1, int param2) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            backData.param2 = param2;
            return backData;
        }

        private TvCallbackData formatData(int param1, int param2, int param3) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            backData.param2 = param2;
            backData.param3 = param3;
            return backData;
        }

        private TvCallbackData formatData(int param1, int param2, int param3, int param4) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            backData.param2 = param2;
            backData.param3 = param3;
            backData.param4 = param4;
            return backData;
        }

        private TvCallbackData formatData(int param1, int param2, int param3, long param4) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            backData.param2 = param2;
            backData.param3 = param3;
            backData.paramLong1 = param4;
            return backData;
        }

        private TvCallbackData formatData(int param1, int param2, String paramStr1, int param3) {
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = param1;
            backData.param2 = param2;
            backData.paramStr1 = paramStr1;
            backData.param3 = param3;
            return backData;
        }

        public int notifySvctxNotificationCode(int code) {
            MtkLog.d(TvCallbackHandler.TAG, "notifySvctxNotificationCode>>>" + code);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_SVCTX_NOTIFY;
            msg.arg1 = TvCallbackConst.MSG_CB_SVCTX_NOTIFY;
            msg.what = TvCallbackConst.MSG_CB_SVCTX_NOTIFY;
            msg.obj = formatData(code);
            return sendMessage(msg);
        }

        public int notifyConfigMessage(int notifyId, int data) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CONFIG;
            msg.arg1 = TvCallbackConst.MSG_CB_CONFIG;
            msg.what = TvCallbackConst.MSG_CB_CONFIG;
            msg.obj = formatData(notifyId, data);
            return sendMessage(msg);
        }

        public int notifyChannelListUpdateMsg(int condition, int reason, int data) {
            MtkLog.d(TvCallbackHandler.TAG, "notifyChannelListUpdateMsg>>" + condition + "  " + reason + "  " + data);
            if (reason == 0 || condition == 1) {
                return -1;
            }
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CHANNELIST;
            msg.arg1 = TvCallbackConst.MSG_CB_CHANNELIST;
            msg.what = TvCallbackConst.MSG_CB_CHANNELIST;
            msg.obj = formatData(condition, reason, data);
            return sendMessage(msg);
        }

        public int notifySatlListUpdateMsg(int condition, int reason, int data) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_UPDATE_SATELLITE_LIST;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_UPDATE_SATELLITE_LIST;
            msg.what = TvCallbackConst.MSG_CB_NFY_UPDATE_SATELLITE_LIST;
            msg.obj = formatData(condition, reason, data);
            return sendMessage(msg);
        }

        public int notifyShowOSDMessage(int stringId, int msgType) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_SHOW_OSD;
            msg.arg1 = TvCallbackConst.MSG_CB_SHOW_OSD;
            msg.what = TvCallbackConst.MSG_CB_SHOW_OSD;
            msg.obj = formatData(stringId, msgType);
            return sendMessage(msg);
        }

        public int notifyHideOSDMessage() {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_HIDE_OSD;
            msg.arg1 = TvCallbackConst.MSG_CB_HIDE_OSD;
            msg.what = TvCallbackConst.MSG_CB_HIDE_OSD;
            return sendMessage(msg);
        }

        public int notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_SCAN_NOTIFY;
            msg.arg1 = TvCallbackConst.MSG_CB_SCAN_NOTIFY;
            msg.what = TvCallbackConst.MSG_CB_SCAN_NOTIFY;
            msg.obj = formatData(msg_id, scanProgress, channelNum, argv4);
            return sendMessage(msg);
        }

        public int notifyGingaMessage(int updateType, String sAppID, String sAppName) {
            Message msg = Message.obtain();
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = updateType;
            backData.paramStr1 = sAppID;
            backData.paramObj1 = sAppName;
            msg.arg2 = TvCallbackConst.MSG_CB_GINGA_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_GINGA_MSG;
            msg.what = TvCallbackConst.MSG_CB_GINGA_MSG;
            msg.obj = backData;
            return sendMessage(msg);
        }

        public int notifyGingaVolumeChanged(int updateType, int level) throws RemoteException {
            MtkLog.d(TvCallbackHandler.TAG, "(Default Handler) notifyGingaVolumeChanged updateType=" + updateType + " level=" + level);
            Message msg = Message.obtain();
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = updateType;
            backData.param2 = level;
            msg.arg2 = TvCallbackConst.MSG_CB_GINGA_VOLUME_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_GINGA_VOLUME_MSG;
            msg.what = TvCallbackConst.MSG_CB_GINGA_VOLUME_MSG;
            msg.obj = backData;
            return sendMessage(msg);
        }

        public int notifyEASMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_EAS_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_EAS_MSG;
            msg.what = TvCallbackConst.MSG_CB_EAS_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyPipPopMessage(int updateType, int argv1, int argv2, int argv3) {
            return 0;
        }

        public int notifyTeletextMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_TTX_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_TTX_MSG;
            msg.what = TvCallbackConst.MSG_CB_TTX_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyUpgradeMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_UPGRADE_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_UPGRADE_MSG;
            msg.what = TvCallbackConst.MSG_CB_UPGRADE_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyChannelListUpdateMsgByType(int svlid, int condition, int reason, int data) {
            MtkLog.d(TvCallbackHandler.TAG, "notifyChannelListUpdateMsgByType>>" + svlid + "  " + condition + "  " + reason + "   " + data);
            if (reason == 0 || condition == 1) {
                return -1;
            }
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE;
            msg.arg1 = TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE;
            msg.what = TvCallbackConst.MSG_CB_CHANNEL_LIST_UPDATE;
            msg.obj = formatData(svlid, reason, condition, data);
            return sendMessage(msg);
        }

        public int notifyRecordNotification(int updateType, int argv1, int argv2) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.arg1 = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.what = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.obj = formatData(updateType, argv1, argv2);
            return sendMessage(msg);
        }

        public int notifyAVModeMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_AV_MODE_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_AV_MODE_MSG;
            msg.what = TvCallbackConst.MSG_CB_AV_MODE_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyMHEG5Message(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_MHEG5_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_MHEG5_MSG;
            msg.what = TvCallbackConst.MSG_CB_MHEG5_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyEWSPAMessage(int updateType) {
            MtkLog.d(TvCallbackHandler.TAG, "notifyEWSPAMessage,updateType:" + updateType);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_EWS_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_EWS_MSG;
            msg.what = TvCallbackConst.MSG_CB_EWS_MSG;
            msg.obj = formatData(updateType);
            return sendMessage(msg);
        }

        public int notifyMHPMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_MHP_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_MHP_MSG;
            msg.what = TvCallbackConst.MSG_CB_MHP_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyCIMessage(int slot_id, int messageType, int arg3, int arg4, MtkTvCIMMIMenuBase MMIMenu, MtkTvCIMMIEnqBase MMIEnq) {
            if (!TvCallbackHandler.this.dataSeparaterUtil.isSupportCI()) {
                MtkLog.d(TvCallbackHandler.TAG, "notifyCIMessage !dataSeparaterUtil.isSupportCI()");
                return -1;
            }
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CI_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_CI_MSG;
            msg.what = TvCallbackConst.MSG_CB_CI_MSG;
            msg.obj = formatData(slot_id, messageType, arg3, arg4);
            ((TvCallbackData) msg.obj).paramObj1 = MMIMenu;
            ((TvCallbackData) msg.obj).paramObj2 = MMIEnq;
            return sendMessage(msg);
        }

        public int notifyFeatureMessage(int updateType, int argv1, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_FEATURE_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_FEATURE_MSG;
            msg.what = TvCallbackConst.MSG_CB_FEATURE_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyScreenSaverMessage(int updateType, int argv1, int argv2, int argv3) {
            MtkLog.d(TvCallbackHandler.TAG, "notifyScreenSaverMessage updateType =" + updateType + "argv1 =" + argv1 + "argv2 =" + argv2 + "argv3 =" + argv3);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_SCREEN_SAVER_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_SCREEN_SAVER_MSG;
            msg.what = TvCallbackConst.MSG_CB_SCREEN_SAVER_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyPWDDialogMessage(int updateType, int argv1, int argv2, int argv3) throws RemoteException {
            MtkLog.d(TvCallbackHandler.TAG, "notifyPWDDialogMessage updateType =" + updateType + "argv1 =" + argv1 + "argv2 =" + argv2 + "argv3 =" + argv3);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_PWD_DLG_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_PWD_DLG_MSG;
            msg.what = TvCallbackConst.MSG_CB_PWD_DLG_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyOADMessage(int updateType, String oadSchedule, int progress, boolean autoDld, int argv5) {
            Message msg = Message.obtain();
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = updateType;
            backData.param2 = progress;
            backData.param3 = argv5;
            backData.paramStr1 = oadSchedule;
            backData.paramBool2 = autoDld;
            msg.arg2 = TvCallbackConst.MSG_CB_OAD_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_OAD_MSG;
            msg.what = TvCallbackConst.MSG_CB_OAD_MSG;
            msg.obj = backData;
            return sendMessage(msg);
        }

        public int notifyDeviceDiscovery() {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_DEVICE_DISCOVERY;
            msg.arg1 = TvCallbackConst.MSG_CB_DEVICE_DISCOVERY;
            msg.what = TvCallbackConst.MSG_CB_DEVICE_DISCOVERY;
            return sendMessage(msg);
        }

        public int notifyBannerMessage(int msgType, int msgName, int argv2, int argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_BANNER_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_BANNER_MSG;
            msg.what = TvCallbackConst.MSG_CB_BANNER_MSG;
            msg.obj = formatData(msgType, msgName, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyTimeshiftNotification(int updateType, long argv1) {
            Message msg = Message.obtain();
            TvCallbackData backData = new TvCallbackData();
            backData.param1 = updateType;
            backData.paramLong1 = argv1;
            msg.obj = backData;
            msg.arg2 = TvCallbackConst.MSG_CB_TIME_SHIFT_NFY;
            msg.arg1 = TvCallbackConst.MSG_CB_TIME_SHIFT_NFY;
            msg.what = TvCallbackConst.MSG_CB_TIME_SHIFT_NFY;
            return sendMessage(msg);
        }

        public int notifyHBBTVMessage(int callbackType, int[] callbackData, int callbackDataLen) {
            Message msg = Message.obtain();
            int message = 0;
            try {
                message = callbackData[0];
            } catch (Exception e) {
            }
            msg.arg2 = TvCallbackConst.MSG_CB_HBBTV_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_HBBTV_MSG;
            msg.what = TvCallbackConst.MSG_CB_HBBTV_MSG;
            msg.obj = formatData(callbackType, message);
            return sendMessage(msg);
        }

        public int notifyWarningMessage(int updateType, int channelID, String eventInfo, int duration, int args5) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_WARNING_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_WARNING_MSG;
            msg.what = TvCallbackConst.MSG_CB_WARNING_MSG;
            msg.obj = formatData(updateType, channelID, eventInfo, duration);
            return sendMessage(msg);
        }

        public int notifyCecNotificationCode(int code) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CEC_NFY;
            msg.arg1 = TvCallbackConst.MSG_CB_CEC_NFY;
            msg.what = TvCallbackConst.MSG_CB_CEC_NFY;
            msg.obj = formatData(code);
            return sendMessage(msg);
        }

        public int notifyAmpVolCtrlMessage(int volume, boolean isMute) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_AMP_VOL_CTRL;
            msg.arg1 = TvCallbackConst.MSG_CB_AMP_VOL_CTRL;
            msg.what = TvCallbackConst.MSG_CB_AMP_VOL_CTRL;
            return sendMessage(msg);
        }

        public int notifyCecFrameInfo(int initLA, int destLA, int opcode, int[] operand, int operandSize) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CEC_FRAME_INFO;
            msg.arg1 = TvCallbackConst.MSG_CB_CEC_FRAME_INFO;
            msg.what = TvCallbackConst.MSG_CB_CEC_FRAME_INFO;
            return sendMessage(msg);
        }

        public int notifyCecActiveSource(int destLA, int destPA, boolean activeRoutingPath) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_CEC_ACTIVE_SRC;
            msg.arg1 = TvCallbackConst.MSG_CB_CEC_ACTIVE_SRC;
            msg.what = TvCallbackConst.MSG_CB_CEC_ACTIVE_SRC;
            return sendMessage(msg);
        }

        public int notifyEventNotification(int updateType, int argv1, int argv2, long argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_EVENT_NFY;
            msg.arg1 = TvCallbackConst.MSG_CB_EVENT_NFY;
            msg.what = TvCallbackConst.MSG_CB_EVENT_NFY;
            msg.obj = formatData(updateType, argv1, argv2);
            ((TvCallbackData) msg.obj).paramLong1 = argv3;
            return sendMessage(msg);
        }

        public int notifyNoUsedkeyMessage(int updateType, int argv1, int argv2, long argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NO_USED_KEY_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_NO_USED_KEY_MSG;
            msg.what = TvCallbackConst.MSG_CB_NO_USED_KEY_MSG;
            msg.obj = formatData(updateType, argv1);
            return sendMessage(msg);
        }

        public int notifyRecordPBNotification(int updateType, int argv1, int argv2, long argv3) {
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.arg1 = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.what = TvCallbackConst.MSG_CB_RECORD_NFY;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyATSCEventMessage(int updateType, int argv1, int argv2, long argv3) throws RemoteException {
            MtkLog.d("TVCallBackHandler", "notifyATSCEventMessage");
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_ATSC_EVENT_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_ATSC_EVENT_MSG;
            msg.what = TvCallbackConst.MSG_CB_ATSC_EVENT_MSG;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyCDTLogoMessage(int updateType, int argv1, int argv2, int argv3) {
            MtkLog.d(TvCallbackHandler.TAG, "come in notifyCDTLogoMessage,updateType = " + updateType + ",argv1 = " + argv1 + ", argv2 = " + argv2 + ", argv3 = " + argv3);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO;
            msg.arg1 = TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO;
            msg.what = TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO;
            msg.obj = formatData(updateType, argv1, argv2, argv3);
            return sendMessage(msg);
        }

        public int notifyUiMsDisplay(int uiType, boolean show) {
            MtkLog.d(TvCallbackHandler.TAG, "come in notifyUiMsDisplay,uiType=" + uiType + ",show=" + show);
            TvCallbackData backData = new TvCallbackData();
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_CEC_UI_DISP;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_CEC_UI_DISP;
            msg.what = TvCallbackConst.MSG_CB_NFY_CEC_UI_DISP;
            backData.param1 = uiType;
            backData.paramBool1 = show;
            msg.obj = backData;
            return sendMessage(msg);
        }

        public int notifyNativeAppStatus(int nativeAppId, boolean show) {
            MtkLog.d(TvCallbackHandler.TAG, "come in notifyNativeAppStatus,nativeAppId=" + nativeAppId + ",show=" + show);
            TvCallbackData backData = new TvCallbackData();
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_NATIVE_APP_STATUS;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_NATIVE_APP_STATUS;
            msg.what = TvCallbackConst.MSG_CB_NFY_NATIVE_APP_STATUS;
            backData.param1 = nativeAppId;
            backData.paramBool1 = show;
            msg.obj = backData;
            return sendMessage(msg);
        }

        public int notifyTvproviderUpdateMsg(int svlid, int count, int[] eventType, int[] svlRecId) {
            MtkLog.d(TvCallbackHandler.TAG, "(Default Handler) notifyTvproviderUpdateMsg: svlid = " + svlid + ", count = " + count + "\n  eventType>>" + eventType.length + "   svlRecId>>>" + svlRecId.length);
            new TvCallbackData();
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_UPDATE_TV_PROVIDER_LIST;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_UPDATE_TV_PROVIDER_LIST;
            msg.what = TvCallbackConst.MSG_CB_NFY_UPDATE_TV_PROVIDER_LIST;
            msg.obj = formatData(svlid, count);
            return sendMessage(msg);
        }

        public int notifyTslIdUpdateMsg(int condition, int reason, int data) throws RemoteException {
            MtkLog.d(TvCallbackHandler.TAG, "(Default Handler) notifyTslIdUpdateMsg: condition = " + condition + ",reason = " + reason + ",data = " + data + "\n");
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_TSL_ID_UPDATE_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_TSL_ID_UPDATE_MSG;
            msg.what = TvCallbackConst.MSG_CB_NFY_TSL_ID_UPDATE_MSG;
            msg.obj = formatData(condition, reason, data);
            return sendMessage(msg);
        }

        public int notifyBroadcastMessage(int msgType, int argv1, int argv2, int argv3) throws RemoteException {
            MtkLog.d(TvCallbackHandler.TAG, "1111(Default Handler) notifyBroadcastMessage type=" + msgType + "argv1=" + argv1);
            Message msg = Message.obtain();
            msg.arg2 = TvCallbackConst.MSG_CB_NFY_TUNE_CHANNEL_BROADCAST_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_NFY_TUNE_CHANNEL_BROADCAST_MSG;
            msg.what = TvCallbackConst.MSG_CB_NFY_TUNE_CHANNEL_BROADCAST_MSG;
            msg.obj = formatData(msgType, argv1);
            return sendMessage(msg);
        }

        public int notifyHtmlAgentMessage(int callbackType, int[] callbackData, int callbackDataLen) throws RemoteException {
            MtkLog.d(TvCallbackHandler.TAG, "(Default Handler) notifyHtmlAgentMessage=" + callbackType);
            Message msg = Message.obtain();
            int message = 0;
            try {
                message = callbackData[0];
            } catch (Exception e) {
            }
            msg.arg2 = TvCallbackConst.MSG_CB_FVP_MSG;
            msg.arg1 = TvCallbackConst.MSG_CB_FVP_MSG;
            msg.what = TvCallbackConst.MSG_CB_FVP_MSG;
            msg.obj = formatData(callbackType, message);
            return sendMessage(msg);
        }
    }
}
