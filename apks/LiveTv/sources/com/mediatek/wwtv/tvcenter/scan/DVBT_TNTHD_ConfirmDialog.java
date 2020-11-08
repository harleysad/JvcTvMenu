package com.mediatek.wwtv.tvcenter.scan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.android.tv.dialog.PinDialogFragment;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.wwtv.setting.base.scan.ui.ScanDialogActivity;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.TwinkleView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DVBT_TNTHD_ConfirmDialog {
    /* access modifiers changed from: private */
    public TurnkeyCommDialog mConfirmDialog;
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mHandler;

    public DVBT_TNTHD_ConfirmDialog(Context mContext2) {
        this.mContext = mContext2;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public boolean isConfirmDialogShowing() {
        if (this.mConfirmDialog == null) {
            return false;
        }
        return this.mConfirmDialog.isShowing();
    }

    public void showConfirmDialog() {
        this.mConfirmDialog = new TurnkeyCommDialog(this.mContext, 3);
        this.mConfirmDialog.setMessage(this.mContext.getString(R.string.tnt_hd_nw_chg));
        this.mConfirmDialog.setButtonYesName(this.mContext.getString(R.string.menu_setup_button_yes));
        this.mConfirmDialog.setButtonNoName(this.mContext.getString(R.string.menu_setup_button_no));
        this.mConfirmDialog.show();
        this.mConfirmDialog.getButtonNo().requestFocus();
        this.mConfirmDialog.setPositon(-20, 70);
        this.mConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                DVBT_TNTHD_ConfirmDialog.this.mConfirmDialog.dismiss();
                return true;
            }
        });
        View.OnKeyListener yesListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                DVBT_TNTHD_ConfirmDialog.this.mConfirmDialog.dismiss();
                if (TVContent.getInstance(DVBT_TNTHD_ConfirmDialog.this.mContext).isTvInputBlock() || EditChannel.getInstance(DVBT_TNTHD_ConfirmDialog.this.mContext).getBlockChannelNumForSource() > 0) {
                    Log.d("DVBT_INTHD_ConfirmDialog", "show Pwd");
                    PinDialogFragment.create(7).show(((Activity) DVBT_TNTHD_ConfirmDialog.this.mContext).getFragmentManager(), "PinDialogFragment");
                    return true;
                }
                DVBT_TNTHD_ConfirmDialog.this.startMenuFullScan();
                return true;
            }
        };
        this.mConfirmDialog.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                DVBT_TNTHD_ConfirmDialog.this.mConfirmDialog.dismiss();
                MtkTvScan.getInstance().getScanDvbtInstance().setDisableNetworkChangeForCrnt(true);
                return true;
            }
        });
        this.mConfirmDialog.getButtonYes().setOnKeyListener(yesListener);
        this.mConfirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                MtkLog.d("DVBT_TNTHD_ConfirmDialog", "bannerView.isVisible():" + bannerView.isVisible());
                if (bannerView != null) {
                    bannerView.setVisibility(4);
                    bannerView.isKeyHandler(KeyMap.KEYCODE_MTKIR_INFO);
                }
                DVBT_TNTHD_ConfirmDialog.this.showTwinkle();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showTwinkle() {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                ((TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)).showHandler();
            }
        }, MessageType.delayMillis5);
    }

    /* access modifiers changed from: private */
    public void startMenuFullScan() {
        showDVBTAutoOrUpdateScan();
    }

    public void showDVBTAutoOrUpdateScan() {
        TvInputInfo tvInputInfo = InputSourceManager.getInstance().getTvInputInfo("main");
        int tuneMode = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
        if (tuneMode == 0) {
            Intent intent = new Intent(this.mContext, ScanDialogActivity.class);
            intent.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBT);
            this.mContext.startActivity(intent);
        } else if (tuneMode == 1) {
            Intent intent2 = new Intent(this.mContext, ScanViewActivity.class);
            intent2.putExtra("ActionID", MenuConfigManager.TV_CHANNEL_SCAN_DVBC);
            this.mContext.startActivity(intent2);
        }
        MtkLog.d("showDTVScan(MtkTvScanBase param))");
    }
}
