package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.MessageType;

public class DVBT_Inactivechannel_ConfirmDialog {
    private Context mContext;

    public DVBT_Inactivechannel_ConfirmDialog(Context mContext2) {
        this.mContext = mContext2;
    }

    public void showConfirmDialog() {
        final TurnkeyCommDialog confirmDialog = new TurnkeyCommDialog(this.mContext, 0);
        confirmDialog.setMessage(this.mContext.getString(R.string.dvbt_inactivechannel_nw_chg));
        confirmDialog.show();
        confirmDialog.setPositon(-20, 70);
        confirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                confirmDialog.dismiss();
                return true;
            }
        });
        ((TurnkeyUiMainActivity) this.mContext).getHandler().postDelayed(new Runnable() {
            public void run() {
                confirmDialog.dismiss();
            }
        }, MessageType.delayMillis4);
        showInactive();
    }

    private void showInactive() {
        new InactiveChannelDialog(this.mContext).deleteAllInactiveChannels();
    }
}
