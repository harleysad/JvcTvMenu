package com.mediatek.wwtv.tvcenter.epg.sa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.commonview.TurnkeyCommDialog;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DialogActivity extends Activity {
    /* access modifiers changed from: private */
    public static String TAG = "DialogActivity";
    /* access modifiers changed from: private */
    public TurnkeyCommDialog mBookProgramConfirmDialog;
    /* access modifiers changed from: private */
    public Context mContext;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epg_alarm_layout);
        this.mContext = getApplicationContext();
        Intent intent = getIntent();
        long longExtra = intent.getLongExtra("currentMills", 0);
        String programName = intent.getStringExtra("programname");
        int channelid = intent.getIntExtra("channelid", 0);
        String str = TAG;
        MtkLog.d(str, "DialogActivity   come in>>" + programName + "   " + channelid);
        showConfirmDlg(programName, channelid);
    }

    private void showConfirmDlg(String programName, final int channelid) {
        if (this.mBookProgramConfirmDialog == null) {
            this.mBookProgramConfirmDialog = new TurnkeyCommDialog(this, 3);
            TurnkeyCommDialog turnkeyCommDialog = this.mBookProgramConfirmDialog;
            turnkeyCommDialog.setMessage(programName + getString(R.string.nav_epg_book_program_coming_tip));
        } else {
            TurnkeyCommDialog turnkeyCommDialog2 = this.mBookProgramConfirmDialog;
            turnkeyCommDialog2.setMessage(programName + this.mContext.getString(R.string.nav_epg_book_program_coming_tip));
            this.mBookProgramConfirmDialog.setText();
        }
        this.mBookProgramConfirmDialog.setButtonYesName(getString(R.string.menu_ok));
        this.mBookProgramConfirmDialog.setButtonNoName(getString(R.string.menu_cancel));
        this.mBookProgramConfirmDialog.show();
        this.mBookProgramConfirmDialog.getButtonYes().requestFocus();
        this.mBookProgramConfirmDialog.setPositon(-20, 70);
        this.mBookProgramConfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if (keyCode != 4 || action != 0) {
                    return false;
                }
                DialogActivity.this.mBookProgramConfirmDialog.dismiss();
                DialogActivity.this.finish();
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
                if (channelid != 0) {
                    if (!CommonIntegration.getInstance().isCurrentSourceTv()) {
                        CommonIntegration.getInstance().iSetSourcetoTv();
                        MtkLog.d(DialogActivity.TAG, "change to TV Source");
                    }
                    CommonIntegration.getInstanceWithContext(DialogActivity.this.mContext.getApplicationContext()).selectChannelById(channelid);
                }
                DialogActivity.this.mBookProgramConfirmDialog.dismiss();
                DialogActivity.this.mContext.getApplicationContext().sendBroadcast(new Intent("mtk_intent_choose_the_booked_event"));
                DialogActivity.this.finish();
                return true;
            }
        };
        this.mBookProgramConfirmDialog.getButtonNo().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                if (keyCode != 66 && keyCode != 23) {
                    return false;
                }
                DialogActivity.this.mBookProgramConfirmDialog.dismiss();
                DialogActivity.this.finish();
                return true;
            }
        });
        this.mBookProgramConfirmDialog.getButtonYes().setOnKeyListener(yesListener);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }
}
