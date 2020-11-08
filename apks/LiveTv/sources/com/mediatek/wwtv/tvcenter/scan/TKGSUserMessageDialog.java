package com.mediatek.wwtv.tvcenter.scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import com.mediatek.wwtv.tvcenter.util.MessageType;

public class TKGSUserMessageDialog {
    Runnable hide;
    private final Context mContext;
    Handler mHandler = new Handler();

    public TKGSUserMessageDialog(Context mContext2) {
        this.mContext = mContext2;
    }

    public void showConfirmDialog(String message) {
        final Dialog userMessageDialog = new AlertDialog.Builder(this.mContext).setTitle("TKGS User Message").setMessage(message).setPositiveButton("OK", (DialogInterface.OnClickListener) null).create();
        userMessageDialog.show();
        userMessageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface arg0) {
                TKGSUserMessageDialog.this.mHandler.removeCallbacks(TKGSUserMessageDialog.this.hide);
            }
        });
        userMessageDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return true;
                }
                dialog.dismiss();
                return true;
            }
        });
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                userMessageDialog.dismiss();
            }
        }, MessageType.delayMillis3);
    }
}
