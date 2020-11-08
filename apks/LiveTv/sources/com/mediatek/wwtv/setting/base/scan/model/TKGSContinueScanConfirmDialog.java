package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class TKGSContinueScanConfirmDialog {
    private final Context mContext;
    private final int mSatId;

    public TKGSContinueScanConfirmDialog(Context mContext2, int satId) {
        this.mContext = mContext2;
        this.mSatId = satId;
    }

    public void showConfirmDialog() {
        final LiveTVDialog confirmDialog = new LiveTVDialog(this.mContext, 3);
        confirmDialog.setMessage(this.mContext.getString(R.string.scan_trd_turkey_tkgs_update_scan));
        confirmDialog.setButtonYesName(this.mContext.getString(R.string.menu_setup_button_yes));
        confirmDialog.setButtonNoName(this.mContext.getString(R.string.menu_setup_button_no));
        confirmDialog.show();
        confirmDialog.getButtonNo().requestFocus();
        confirmDialog.setPositon(-20, 70);
        confirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                confirmDialog.dismiss();
                TKGSContinueScanConfirmDialog.this.cancelContinueScan();
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
                confirmDialog.dismiss();
                TKGSContinueScanConfirmDialog.this.resetTableVersion();
                TKGSContinueScanConfirmDialog.this.continueScan();
                return true;
            }
        };
        confirmDialog.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                confirmDialog.dismiss();
                TKGSContinueScanConfirmDialog.this.cancelContinueScan();
                return true;
            }
        });
        confirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }

    /* access modifiers changed from: private */
    public void resetTableVersion() {
        new MtkTvScanDvbsBase().dvbsSetTableVersion(MtkTvScanDvbsBase.DvbsTableType.DVBS_TABLE_TYPE_TKGS, 255);
    }

    /* access modifiers changed from: private */
    public void continueScan() {
        MtkLog.d("continueScan()");
        ((ScanViewActivity) this.mContext).startDVBSFullScan(this.mSatId, -1, 1, MenuConfigManager.DVBS_SAT_UPDATE_SCAN);
    }

    /* access modifiers changed from: private */
    public void cancelContinueScan() {
        MtkLog.d("cancelContinueScan()");
    }
}
