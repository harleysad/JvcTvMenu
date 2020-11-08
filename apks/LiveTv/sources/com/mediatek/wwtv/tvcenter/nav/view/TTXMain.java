package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;
import com.mediatek.tv.ini.IniDocument;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.util.BannerImplement;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.util.TeletextImplement;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;

public class TTXMain extends NavBasicMisc {
    private static final String ACTION_INI_PATH = "/vendor/etc/customer_keymap.ini";
    public static final int BANNER_MSG_NAV = 0;
    private static final int KEY_CODE_SLEEP = 305;
    private static final int NO_SIGNAL = 1;
    private static final String SECTION_NAME = "IR";
    private static final String TAG = "TTXMain";
    private static TTXMain instance;
    private final String IS_TTX_SHOW = "is_ttx_show";
    private ComponentsManager comManager;
    private String exitReuse = "";
    public TTXFavListDialog favDialog;
    private String holdReuse = "";
    private String indexReuse = "";
    private IniDocument iniDoc;
    public boolean isActive = false;
    public boolean isNoTTX = false;
    private boolean isTTXSupported = false;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1879048226) {
                TvCallbackData specialMsgData = (TvCallbackData) msg.obj;
                if (specialMsgData.param1 == 0) {
                    MtkLog.d(TTXMain.TAG, "come in handleMessage value=== " + specialMsgData.param2);
                    if (specialMsgData.param2 == 1) {
                        if (TTXMain.this.topDialog != null && TTXMain.this.topDialog.isShowing()) {
                            TTXMain.this.topDialog.dismiss();
                        }
                        if (TTXMain.this.favDialog != null && TTXMain.this.favDialog.isShowing()) {
                            TTXMain.this.favDialog.dismiss();
                        }
                    }
                }
            }
        }
    };
    private BannerImplement mNavBannerImplement = null;
    private TvCallbackHandler mTTXCallbackHandler;
    private TeletextImplement mTTXImpl;
    private String mixReuse = "";
    private TTXToast noTopToast;
    private TTXToast noTxToast;
    private String revealReuse = "";
    private String sizeReuse = "";
    private String subcodeReuse = "";
    /* access modifiers changed from: private */
    public TTXTopDialog topDialog;

    public TTXMain(Context mContext) {
        super(mContext);
        this.componentID = NavBasic.NAV_COMP_ID_TELETEXT;
        init();
    }

    public static TTXMain getInstance(Context mContext) {
        if (instance == null) {
            instance = new TTXMain(mContext);
        }
        return instance;
    }

    private void init() {
        this.topDialog = new TTXTopDialog(this.mContext);
        this.favDialog = new TTXFavListDialog(this.mContext);
        this.mTTXImpl = TeletextImplement.getInstance();
        this.comManager = ComponentsManager.getInstance();
        this.mTTXCallbackHandler = TvCallbackHandler.getInstance();
        this.mTTXCallbackHandler.addCallBackListener(this.mHandler);
        this.mNavBannerImplement = BannerImplement.getInstanceNavBannerImplement(this.mContext);
        this.iniDoc = new IniDocument(ACTION_INI_PATH);
        this.mixReuse = readFromINI("MIX_KEYCODE");
        this.holdReuse = readFromINI("HOLD_KEYCODE");
        this.indexReuse = readFromINI("INDEX_KEYCODE");
        this.revealReuse = readFromINI("REVEAL_KEYCODE");
        this.subcodeReuse = readFromINI("SUBCODE_KEYCODE");
        this.sizeReuse = readFromINI("SIZE_KEYCODE");
        this.exitReuse = readFromINI("CANCEL_KEYCODE");
        MtkLog.d(TAG, "TTXMain init||mixReuse =" + this.mixReuse + "||holdReuse =" + this.holdReuse + "||indexReuse =" + this.indexReuse + "||revealReuse =" + this.revealReuse + "||subcodeReuse =" + this.subcodeReuse + "||sizeReuse =" + this.sizeReuse + "||exitReuse =" + this.exitReuse);
        this.isTTXSupported = DataSeparaterUtil.getInstance() != null && DataSeparaterUtil.getInstance().isSupportTeletext();
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.d(TAG, "isKeyHandler key:" + keyCode + "rewind:" + 89);
        if (keyCode == 233) {
            if (this.mNavBannerImplement != null && !this.mNavBannerImplement.isShowTtxIcon()) {
                MtkLog.d(TAG, "TTX not supported on current channel");
                Toast.makeText(this.mContext, R.string.menu_teletext_notsupport_tip, 1000).show();
                return false;
            } else if (!this.isTTXSupported) {
                MtkLog.d(TAG, "TTX not Supported");
                return false;
            } else if (CommonIntegration.getInstance().getCurrentSource().contains("HDMI")) {
                MtkLog.d(TAG, "HDMI SOurce");
                return false;
            } else {
                if (SundryImplement.getInstanceNavSundryImplement(this.mContext).isFreeze()) {
                    SundryImplement.getInstanceNavSundryImplement(this.mContext).setFreeze(false);
                }
                if (!this.isNoTTX && this.mTTXImpl.startTTX(keyCode) == 0) {
                    MtkLog.e(TAG, "ttxmain||isActive =" + this.isActive);
                    SaveValue.saveWorldBooleanValue(this.mContext, "is_ttx_show", true, false);
                    return this.isActive;
                } else if (this.isNoTTX) {
                    this.isNoTTX = false;
                }
            }
        }
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        MtkLog.d(TAG, "TTXMainKeyHandler keyCode =" + keyCode + "fromNative =" + fromNative);
        if (!TextUtils.isEmpty(this.mixReuse) && keyCode == Integer.valueOf(this.mixReuse).intValue()) {
            KeyDispatch.getInstance().passKeyToNative(KeyMap.KEYCODE_MTKIR_MIX, event);
            return true;
        } else if (!TextUtils.isEmpty(this.holdReuse) && keyCode == Integer.valueOf(this.holdReuse).intValue()) {
            MtkLog.e(TAG, "KeyHandler holdReuse");
            KeyDispatch.getInstance().passKeyToNative(10061, event);
            return true;
        } else if (!TextUtils.isEmpty(this.indexReuse) && keyCode == Integer.valueOf(this.indexReuse).intValue()) {
            MtkLog.e(TAG, "KeyHandler indexReuse");
            KeyDispatch.getInstance().passKeyToNative(KeyMap.KEYCODE_MTKIR_INDEX, event);
            return true;
        } else if (!TextUtils.isEmpty(this.revealReuse) && keyCode == Integer.valueOf(this.revealReuse).intValue()) {
            MtkLog.e(TAG, "KeyHandler revealReuse");
            KeyDispatch.getInstance().passKeyToNative(KeyMap.KEYCODE_MTKIR_REVEAL, event);
            return true;
        } else if (!TextUtils.isEmpty(this.subcodeReuse) && keyCode == Integer.valueOf(this.subcodeReuse).intValue()) {
            KeyDispatch.getInstance().passKeyToNative(212, event);
            return true;
        } else if (!TextUtils.isEmpty(this.sizeReuse) && keyCode == Integer.valueOf(this.sizeReuse).intValue()) {
            KeyDispatch.getInstance().passKeyToNative(10065, event);
            return true;
        } else if (TextUtils.isEmpty(this.exitReuse) || keyCode != Integer.valueOf(this.exitReuse).intValue()) {
            switch (keyCode) {
                case 4:
                    if (this.noTopToast != null && this.noTopToast.isShowing()) {
                        return true;
                    }
                    stopTTX();
                    return true;
                case 56:
                    if (this.mTTXImpl.hasTopInfo()) {
                        this.topDialog.show();
                        return true;
                    }
                    Toast.makeText(this.mContext, R.string.menu_teletext_no_top_tip, 1000).show();
                    return false;
                case 82:
                case KeyMap.KEYCODE_MTKIR_SOURCE /*178*/:
                case 213:
                case 251:
                case 10066:
                    return true;
                case 85:
                    this.favDialog.setPositionByPage(this.mTTXImpl.getCurrentTeletextPage());
                    this.favDialog.show();
                    return true;
                case 86:
                    this.favDialog.processExtFAVkey();
                    return true;
                case 93:
                    if (!CommonIntegration.getInstance().isCurrentSourceHDMI()) {
                        MtkLog.e(TAG, "Other Source");
                        this.favDialog.show();
                        this.favDialog.setFavPage(this.mTTXImpl.getCurrentTeletextPage());
                        return true;
                    }
                    MtkLog.e(TAG, "HDMI Source");
                    return true;
                case KeyMap.KEYCODE_MTKIR_GUIDE /*172*/:
                    return KeyDispatch.getInstance().passKeyToNative(3, event);
                case 222:
                    return KeyDispatch.getInstance().passKeyToNative(19, event);
                case 227:
                    return KeyDispatch.getInstance().passKeyToNative(21, event);
                case 10471:
                    return KeyDispatch.getInstance().passKeyToNative(20, event);
                default:
                    MtkLog.d(TAG, "KeyHandler dispatch native||keycode =" + keyCode);
                    return KeyDispatch.getInstance().passKeyToNative(keyCode, event);
            }
        } else {
            KeyDispatch.getInstance().passKeyToNative(KEY_CODE_SLEEP, event);
            return true;
        }
    }

    public void handlerTTXMessage(int message) {
        MtkLog.e(TAG, "handlerTTXMessage, message =" + message);
        switch (message) {
            case 2:
                this.isActive = true;
                ((BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER)).setVisibility(8);
                if (ComponentsManager.getActiveCompId() == 16777222) {
                    ((SundryShowTextView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_SUNDRY)).setVisibility(8);
                }
                if (MarketRegionInfo.isFunctionSupport(30)) {
                    DvrManager.getInstance().uiManager.hiddenAllViews();
                }
                if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                    StateDvrFileList.getInstance().dissmiss();
                }
                setVisibility(0);
                return;
            case 3:
                this.isActive = false;
                stopTTX();
                if (this.favDialog != null && this.favDialog.isShowing()) {
                    this.favDialog.dismiss();
                }
                if (this.topDialog != null && this.topDialog.isShowing()) {
                    this.topDialog.dismiss();
                    return;
                }
                return;
            case 4:
                this.isNoTTX = true;
                this.isActive = false;
                Toast.makeText(this.mContext, R.string.menu_teletext_notsupport_tip, 1000).show();
                setVisibility(8);
                if (this.favDialog != null && this.favDialog.isShowing()) {
                    this.favDialog.dismiss();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void stopTTX() {
        MtkLog.d(TAG, "stopTTX is called");
        SaveValue.saveWorldBooleanValue(this.mContext, "is_ttx_show", false, false);
        this.mTTXImpl.stopTTX(new TeletextImplement.OnStopTTXCallback() {
            public void onStopTTX(final int resultCode) {
                TTXMain.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (resultCode == 0) {
                            TTXMain.this.setVisibility(8);
                        }
                    }
                });
            }
        });
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    private String readFromINI(String keyName) {
        MtkLog.d(TAG, "readFromINI keyName =" + keyName);
        String key = "";
        if (TextUtils.isEmpty(keyName)) {
            MtkLog.d(TAG, "readFromINI keyName is null !!!");
            return "";
        } else if (this.iniDoc == null) {
            MtkLog.d(TAG, "iniDoc is null !!!");
            return "";
        } else {
            try {
                key = this.iniDoc.getTagValue(SECTION_NAME, keyName);
            } catch (Exception e) {
                MtkLog.d(TAG, "getTagValue exception !!!");
            }
            MtkLog.d(TAG, "readFromINI key =" + key);
            return key.trim();
        }
    }
}
