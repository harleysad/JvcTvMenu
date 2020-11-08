package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.mediatek.twoworlds.tv.MtkTvHBBTV;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.ConfirmDialog;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicMisc;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class Hbbtv extends NavBasicMisc implements ComponentStatusListener.ICStatusListener, ConfirmDialog.IResultCallback {
    private static final int FAILDIALOGDISS = 1;
    private static final int INACTIVE_HBBTV = 2;
    private static final int MTKTVAPI_HBBTV_FUNC_ACCEPT = 4;
    private static final int MTKTVAPI_HBBTV_FUNC_CANCEL = 5;
    private static final String TAG = "HbbtvView";
    AlertDialog.Builder builder = null;
    Dialog faildialog = null;
    /* access modifiers changed from: private */
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (Hbbtv.this.faildialog != null && Hbbtv.this.faildialog.isShowing()) {
                        Hbbtv.this.faildialog.dismiss();
                        return;
                    }
                    return;
                case 2:
                    if (ComponentsManager.getNativeActiveCompId() == 33554434) {
                        ComponentsManager.updateActiveCompId(true, 0);
                        ComponentStatusListener.getInstance().updateStatus(1, 0);
                        boolean unused = Hbbtv.this.is_START_STREAMING = false;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean is_START_STREAMING = false;
    private ProgressBar progressBar;

    public Hbbtv(Context mContext) {
        super(mContext);
        this.componentID = NavBasic.NAV_NATIVE_COMP_ID_HBBTV;
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        init(mContext);
    }

    public void init(Context mContext) {
        MtkLog.d(TAG, "init==progressbar");
        this.progressBar = new ProgressBar(mContext, (AttributeSet) null, 16842874);
        this.progressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        RelativeLayout rLayout = new RelativeLayout(mContext);
        rLayout.setGravity(17);
        rLayout.addView(this.progressBar);
        ((ViewGroup) ((Activity) mContext).findViewById(16908290).getRootView()).addView(rLayout, params);
        hide();
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean isKeyHandler(int keyCode) {
        MtkLog.d(TAG, "isKeyHandler||keycode =" + keyCode);
        if (ComponentsManager.getNativeActiveCompId() != 33554434 || !this.is_START_STREAMING) {
            return false;
        }
        Intent intents = new Intent("android.settings.SETTINGS");
        if (keyCode == 213) {
            intents.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_TYPE_AUDIO_SRC);
            this.mContext.startActivity(intents);
            return true;
        } else if (keyCode != 215) {
            return false;
        } else {
            intents.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_TYPE_SUBTITLE_SRC);
            this.mContext.startActivity(intents);
            return true;
        }
    }

    public void handlerHbbtvMessage(int type, int message) {
        MtkLog.d(TAG, "handlerHbbtvMessage||type =" + type);
        if (type != 8) {
            switch (type) {
                case 1:
                case 3:
                    this.handler.removeMessages(2);
                    ComponentsManager.updateActiveCompId(true, NavBasic.NAV_NATIVE_COMP_ID_HBBTV);
                    ComponentStatusListener.getInstance().updateStatus(2, 0);
                    return;
                case 2:
                case 4:
                    this.handler.sendEmptyMessageDelayed(2, 1000);
                    return;
                case 5:
                    this.is_START_STREAMING = true;
                    return;
                case 6:
                    this.is_START_STREAMING = false;
                    return;
                default:
                    switch (type) {
                        case EPGConfig.EPG_SYNCHRONIZATION_MESSAGE:
                            showLoading();
                            return;
                        case EPGConfig.EPG_PROGRAMINFO_SHOW:
                            hide();
                            return;
                        default:
                            switch (type) {
                                case 266:
                                    showHBBTVDialog();
                                    return;
                                case 267:
                                    this.handler.removeMessages(1);
                                    this.handler.sendEmptyMessage(1);
                                    return;
                                default:
                                    return;
                            }
                    }
            }
        } else {
            ConfirmDialog dialog = new ConfirmDialog(this.mContext, this.mContext.getString(R.string.hbbtv_show_confict_dialog_tips));
            dialog.setTimeout(5000);
            dialog.setCallback(this);
            dialog.showDialog();
        }
    }

    public Boolean getStreamBoolean() {
        return Boolean.valueOf(this.is_START_STREAMING);
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus =" + statusID + "||value =" + value);
        if (statusID == 1 && !ComponentsManager.getInstance().isComponentsShow()) {
            ComponentsManager.nativeComponentReActive();
        }
    }

    public void handleUserSelection(int result) {
        MtkTvHBBTV mtkTvHBBTV = MtkTvHBBTV.getInstance();
        MtkLog.d(TAG, "handleUserSelection, result==" + result);
        if (result == 0) {
            mtkTvHBBTV.exchangeData(4, (int[]) null);
        } else {
            mtkTvHBBTV.exchangeData(5, (int[]) null);
        }
    }

    public void hide() {
        MtkLog.d(TAG, "hide==progressbar");
        this.progressBar.setVisibility(4);
    }

    public void showLoading() {
        MtkLog.d(TAG, "show==progressbar");
        this.progressBar.setVisibility(0);
    }

    public void showHBBTVDialog() {
        this.builder = new AlertDialog.Builder(this.mContext);
        this.builder.setTitle(R.string.hbbtv_failed_title);
        this.builder.setMessage(R.string.hbbtv_failed_message);
        this.builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Hbbtv.this.handler.removeMessages(1);
            }
        });
        this.faildialog = this.builder.create();
        this.faildialog.getWindow().setType(DvrManager.ALLOW_SYSTEM_SUSPEND);
        this.faildialog.show();
        this.handler.sendEmptyMessageDelayed(1, MessageType.delayMillis10);
    }
}
