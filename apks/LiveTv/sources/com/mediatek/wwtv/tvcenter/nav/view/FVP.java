package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvHBBTV;
import com.mediatek.twoworlds.tv.MtkTvKeyEvent;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.ConfirmDialog;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class FVP extends NavBasicMisc implements ComponentStatusListener.ICStatusListener, ConfirmDialog.IResultCallback {
    public static final int Auto_Dismiss_List_Dialog_Timer = 4;
    private static final String TAG = "FVP";
    private static FVP fvp;
    AlertDialog.Builder builder = null;
    /* access modifiers changed from: private */
    public Context context;
    Dialog dialog = null;
    boolean first = false;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 4) {
                FVP.this.dialog.dismiss();
            }
        }
    };
    private boolean isActive = false;
    /* access modifiers changed from: private */
    public MtkTvKeyEvent mtkKeyEvent;

    public FVP(Context mContext) {
        super(mContext);
        this.componentID = NavBasic.NAV_NATIVE_COMP_ID_FVP;
        this.context = mContext;
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        this.mtkKeyEvent = MtkTvKeyEvent.getInstance();
    }

    public static FVP getInstance(Context context2) {
        if (fvp == null) {
            fvp = new FVP(context2);
        }
        return fvp;
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean isKeyHandler(int keyCode) {
        if (keyCode == 172 || keyCode == 212) {
            return updateStatus(keyCode);
        }
        return false;
    }

    public void handlerFVPMessage(int type, int message) {
        MtkLog.e(TAG, "handlerFVPMessage, type=" + type + ", message=" + message);
        switch (type) {
            case 0:
            case 2:
                ComponentsManager.updateActiveCompId(true, NavBasic.NAV_NATIVE_COMP_ID_FVP);
                ComponentStatusListener.getInstance().updateStatus(2, 0);
                break;
            case 1:
                if (ComponentsManager.getNativeActiveCompId() == 33554438) {
                    ComponentsManager.updateActiveCompId(true, 0);
                    ComponentStatusListener.getInstance().updateStatus(1, 0);
                    break;
                }
                break;
            case 3:
                MtkLog.e(TAG, "handlerFVPMessage  fail");
                break;
        }
        if (type == 2) {
            this.isActive = true;
        } else {
            this.isActive = false;
        }
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus>>" + statusID + ">>>" + value);
        if (statusID != 10) {
            if (statusID == 1 && !ComponentsManager.getInstance().isComponentsShow()) {
                ComponentsManager.nativeComponentReActive();
            }
        }
    }

    public void handleUserSelection(int result) {
        MtkLog.d(TAG, "handleUserSelection, result==" + result);
    }

    public boolean updateStatus(int keyCode) {
        int fvp2 = SystemProperties.getInt("vendor.mtk.tif.fvp", 0);
        boolean[] isFVPSupportArray = {false};
        MtkTvHBBTV.getInstance().getFVPSupport(isFVPSupportArray);
        MtkLog.d(TAG, "isFVPSupport===" + isFVPSupportArray[0] + " fvp===" + fvp2 + ",isenable=" + MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_MDS_TOU_STATE));
        if (fvp2 != 1 || MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_MDS_TOU_STATE) != 1 || !MtkTvConfig.getInstance().getCountry().equals("GBR") || !CommonIntegration.getInstance().isCurrentSourceTv() || !isFVPSupportArray[0]) {
            return false;
        }
        if (this.first || isNetworkConnected()) {
            this.mtkKeyEvent.sendKeyClick(this.mtkKeyEvent.androidKeyToDFBkey(keyCode));
            return true;
        }
        showInfoDialog(keyCode);
        return true;
    }

    private boolean isNetworkConnected() {
        NetworkInfo netInfo = ((ConnectivityManager) TurnkeyUiMainActivity.getInstance().getSystemService(ConnectivityManager.class)).getActiveNetworkInfo();
        if (netInfo == null) {
            MtkLog.d(TAG, "NetworkInfo is null; network is not connected");
            return false;
        }
        MtkLog.d(TAG, "isnetwork==" + netInfo.isConnected());
        return netInfo.isConnected();
    }

    private void showInfoDialog(final int keyCode) {
        this.builder = new AlertDialog.Builder(this.context);
        this.builder.setTitle(this.context.getResources().getString(R.string.fvp_alert_title));
        this.builder.setMessage(this.context.getResources().getString(R.string.fvp_alert_dialog));
        this.builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                FVP.this.mtkKeyEvent.sendKeyClick(FVP.this.mtkKeyEvent.androidKeyToDFBkey(keyCode));
            }
        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent("android.settings.SETTINGS");
                intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SRC, EventHelper.MTK_EVENT_EXTRA_SRC_LIVE_TV);
                FVP.this.context.startActivity(intent);
            }
        });
        this.dialog = this.builder.create();
        MtkLog.d(TAG, "FVP TYPE_SYSTEM_ALERT");
        this.dialog.show();
        this.first = true;
        this.handler.sendEmptyMessageDelayed(4, MessageType.delayMillis10);
    }

    public boolean isFVPActive() {
        return this.isActive;
    }

    public void setVisibility(int visibility) {
        MtkLog.d(TAG, "FVP visibility===" + visibility);
        if (this.isActive) {
            super.setVisibility(visibility);
        }
    }
}
