package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.widget.view.LiveTVDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class SaveChannelsConfirmDialog {
    private Context mContext;

    public SaveChannelsConfirmDialog(Context mContext2) {
        this.mContext = mContext2;
    }

    public void showConfirmDialog() {
        final LiveTVDialog confirmDialog = new LiveTVDialog(this.mContext, 3);
        confirmDialog.setMessage(this.mContext.getString(R.string.scan_trd_pt_save_channel));
        confirmDialog.setButtonYesName(this.mContext.getString(R.string.menu_setup_button_yes));
        confirmDialog.setButtonNoName(this.mContext.getString(R.string.menu_setup_button_no));
        confirmDialog.show();
        confirmDialog.getButtonYes().requestFocus();
        confirmDialog.setPositon(-20, 70);
        confirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                confirmDialog.dismiss();
                SaveChannelsConfirmDialog.this.saveChannels();
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
                SaveChannelsConfirmDialog.this.saveChannels();
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
                SaveChannelsConfirmDialog.this.clearChannels();
                return true;
            }
        });
        confirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }

    /* access modifiers changed from: private */
    public void saveChannels() {
        MtkLog.d("saveChannels()");
        TVContent.releaseChanneListSnapshot();
    }

    /* access modifiers changed from: private */
    public void clearChannels() {
        MtkLog.d("clearChannels()");
        TVContent.restoreChanneListSnapshot();
        TVContent.releaseChanneListSnapshot();
    }
}
