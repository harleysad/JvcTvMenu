package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;

public class FacVideo implements Preference.OnPreferenceClickListener {
    public static final int BUTTON_COUNT = 5;
    public static final int MESSAGE_AUTOADJUST = 1000;
    public static final int MESSAGE_AUTOCOLOR = 1001;
    public static final int ONE_SECOND = 1000;
    protected static final String TAG = "FacVideo";
    private static FacVideo mInstance = null;
    /* access modifiers changed from: private */
    public MtkTvAppTVBase appTV;
    /* access modifiers changed from: private */
    public LiveTVDialog autoAdjustDialog;
    /* access modifiers changed from: private */
    public int autoTimeOut = 0;
    /* access modifiers changed from: private */
    public CommonIntegration mCommonIntegration;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    FacVideo.access$008(FacVideo.this);
                    boolean flag = FacVideo.this.appTV.AutoClockPhasePostionCondSuccess(FacVideo.this.mCommonIntegration.getCurrentFocus());
                    MtkLog.d(FacVideo.TAG, "MESSAGE_AUTOADJUST: " + flag + "," + FacVideo.this.autoTimeOut);
                    if (flag || FacVideo.this.autoTimeOut >= 10) {
                        int unused = FacVideo.this.autoTimeOut = 0;
                        FacVideo.this.autoAdjustDialog.dismiss();
                        Toast.makeText(FacVideo.this.mContext, "Adjust Success", 1).show();
                        FacVideo.this.mHandler.removeMessages(1000);
                        return;
                    } else if (FacVideo.this.autoTimeOut >= 10) {
                        int unused2 = FacVideo.this.autoTimeOut = 0;
                        FacVideo.this.autoAdjustDialog.dismiss();
                        Toast.makeText(FacVideo.this.mContext, "Adjust Fail!!", 1).show();
                        return;
                    } else {
                        Message message = obtainMessage();
                        message.copyFrom(msg);
                        sendMessageDelayed(message, 1000);
                        return;
                    }
                case 1001:
                    FacVideo.access$008(FacVideo.this);
                    boolean flag2 = FacVideo.this.appTV.AutoColorCondSuccess(FacVideo.this.mCommonIntegration.getCurrentFocus());
                    MtkLog.d(FacVideo.TAG, "MESSAGE_AUTOCOLOR:" + flag2 + "," + FacVideo.this.autoTimeOut);
                    if (flag2 || FacVideo.this.autoTimeOut >= 10) {
                        int unused3 = FacVideo.this.autoTimeOut = 0;
                        FacVideo.this.autoAdjustDialog.dismiss();
                        Toast.makeText(FacVideo.this.mContext, "Adjust Success", 1).show();
                        FacVideo.this.mHandler.removeMessages(1001);
                        return;
                    } else if (FacVideo.this.autoTimeOut >= 10) {
                        int unused4 = FacVideo.this.autoTimeOut = 0;
                        FacVideo.this.autoAdjustDialog.dismiss();
                        Toast.makeText(FacVideo.this.mContext, "Adjust Fail!!", 1).show();
                        return;
                    } else {
                        Message message2 = obtainMessage();
                        message2.copyFrom(msg);
                        sendMessageDelayed(message2, 1000);
                        return;
                    }
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public PreferenceData mPrefData;
    private final Handler mSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1879048198) {
                MtkLog.d(FacVideo.TAG, "msg.what:" + msg.what);
                int i = ((TvCallbackData) msg.obj).param1;
                int i2 = 0;
                if (i == 9) {
                    PreferenceScreen mScreen = FacVideo.this.mPrefData.getData();
                    MenuConfigManager menuConfigManager = FacVideo.this.mPrefData.mConfigManager;
                    if (MenuConfigManager.FACTORY_VIDEO.equals(mScreen.getKey())) {
                        for (int i3 = 0; i3 < mScreen.getPreferenceCount(); i3++) {
                            Preference tempPre = mScreen.getPreference(i3);
                            MenuConfigManager menuConfigManager2 = FacVideo.this.mPrefData.mConfigManager;
                            if (!"g_video__vid_pos_h".equals(tempPre.getKey())) {
                                MenuConfigManager menuConfigManager3 = FacVideo.this.mPrefData.mConfigManager;
                                if (!"g_video__vid_pos_v".equals(tempPre.getKey())) {
                                }
                            }
                            tempPre.setEnabled(false);
                        }
                    }
                } else if (i != 12) {
                    switch (i) {
                        case 4:
                            PreferenceScreen mScreen2 = FacVideo.this.mPrefData.getData();
                            MenuConfigManager menuConfigManager4 = FacVideo.this.mPrefData.mConfigManager;
                            if (MenuConfigManager.FACTORY_VIDEO.equals(mScreen2.getKey())) {
                                while (true) {
                                    int i4 = i2;
                                    if (i4 < mScreen2.getPreferenceCount()) {
                                        Preference tempPre2 = mScreen2.getPreference(i4);
                                        MenuConfigManager menuConfigManager5 = FacVideo.this.mPrefData.mConfigManager;
                                        if (MenuConfigManager.FV_AUTOCOLOR.equals(tempPre2.getKey()) && ((FacVideo.this.mTv.isCurrentSourceVGA() || FacVideo.this.mTv.isCurrentSourceComponent() || FacVideo.this.mTv.isCurrentSourceScart()) && FacVideo.this.mTv.iCurrentInputSourceHasSignal())) {
                                            tempPre2.setEnabled(true);
                                        }
                                        i2 = i4 + 1;
                                    } else {
                                        return;
                                    }
                                }
                            } else {
                                return;
                            }
                        case 5:
                            PreferenceScreen mScreen3 = FacVideo.this.mPrefData.getData();
                            MenuConfigManager menuConfigManager6 = FacVideo.this.mPrefData.mConfigManager;
                            if (MenuConfigManager.FACTORY_VIDEO.equals(mScreen3.getKey())) {
                                for (int i5 = 0; i5 < mScreen3.getPreferenceCount(); i5++) {
                                    Preference tempPre3 = mScreen3.getPreference(i5);
                                    MenuConfigManager menuConfigManager7 = FacVideo.this.mPrefData.mConfigManager;
                                    if (MenuConfigManager.FV_AUTOCOLOR.equals(tempPre3.getKey())) {
                                        tempPre3.setEnabled(false);
                                    }
                                }
                            }
                            if (FacVideo.this.autoAdjustDialog != null && FacVideo.this.autoAdjustDialog.isShowing()) {
                                FacVideo.this.autoAdjustDialog.dismiss();
                            }
                            FacVideo.this.mHandler.removeMessages(1000);
                            FacVideo.this.mHandler.removeMessages(1001);
                            return;
                        default:
                            return;
                    }
                } else {
                    PreferenceScreen mScreen4 = FacVideo.this.mPrefData.getData();
                    MenuConfigManager menuConfigManager8 = FacVideo.this.mPrefData.mConfigManager;
                    if (MenuConfigManager.FACTORY_VIDEO.equals(mScreen4.getKey())) {
                        while (true) {
                            int i6 = i2;
                            if (i6 < mScreen4.getPreferenceCount()) {
                                Preference tempPre4 = mScreen4.getPreference(i6);
                                MenuConfigManager menuConfigManager9 = FacVideo.this.mPrefData.mConfigManager;
                                if (!"g_video__vid_pos_h".equals(tempPre4.getKey())) {
                                    MenuConfigManager menuConfigManager10 = FacVideo.this.mPrefData.mConfigManager;
                                    if (!"g_video__vid_pos_v".equals(tempPre4.getKey())) {
                                        i2 = i6 + 1;
                                    }
                                }
                                tempPre4.setEnabled(true);
                                i2 = i6 + 1;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public TVContent mTv;

    static /* synthetic */ int access$008(FacVideo x0) {
        int i = x0.autoTimeOut;
        x0.autoTimeOut = i + 1;
        return i;
    }

    private FacVideo(Context context) {
        this.mContext = context;
        this.appTV = new MtkTvAppTVBase();
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mPrefData = PreferenceData.getInstance(this.mContext);
        this.mTv = TVContent.getInstance(this.mContext);
    }

    public static FacVideo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacVideo(context);
        }
        mInstance.mContext = context;
        return mInstance;
    }

    public boolean onPreferenceClick(Preference preference) {
        MtkLog.d(TAG, "onPreferenceClick, " + preference);
        String key = preference.getKey();
        MenuConfigManager menuConfigManager = this.mPrefData.mConfigManager;
        if (key.equals(MenuConfigManager.FV_AUTOCOLOR)) {
            autoAdjustInfo(this.mContext.getString(R.string.menu_video_auto_color_info));
            this.appTV.setAutoColor(this.mCommonIntegration.getCurrentFocus());
            Message message = this.mHandler.obtainMessage();
            message.obj = preference;
            message.what = 1001;
            this.mHandler.sendMessageDelayed(message, 1000);
            return true;
        }
        String key2 = preference.getKey();
        MenuConfigManager menuConfigManager2 = this.mPrefData.mConfigManager;
        if (!key2.equals(MenuConfigManager.AUTO_ADJUST)) {
            return true;
        }
        autoAdjustInfo(this.mContext.getString(R.string.menu_video_auto_adjust_info));
        this.appTV.setAutoClockPhasePostion(this.mCommonIntegration.getCurrentFocus());
        Message message2 = this.mHandler.obtainMessage();
        message2.what = 1000;
        this.mHandler.sendMessageDelayed(message2, 1000);
        return true;
    }

    public void addListener() {
        this.mPrefData.mTV.addSingleLevelCallBackListener(this.mSignalHandler);
    }

    public void removeListener() {
        this.mPrefData.mTV.removeSingleLevelCallBackListener(this.mSignalHandler);
    }

    private void autoAdjustInfo(String mShowMessage) {
        this.autoAdjustDialog = new LiveTVDialog(this.mContext, 5);
        this.autoAdjustDialog.setMessage(mShowMessage);
        this.autoAdjustDialog.show();
        this.autoAdjustDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }
}
