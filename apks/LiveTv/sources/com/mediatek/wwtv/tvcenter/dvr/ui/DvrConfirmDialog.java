package com.mediatek.wwtv.tvcenter.dvr.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.media.MediaPlayer2;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;

public class DvrConfirmDialog extends Dialog implements View.OnClickListener {
    public static final String DISSMISS_DIALOG = "com.mediatek.dialog.dismiss";
    public static final int DVR_CONFIRM_TYPE_SCHEDULE = 1;
    private String TAG = "DvrConfirmDialog";
    private Button buttonNo;
    private Button buttonYes;
    private int confirmType = 0;
    private Context mContext;
    private String schedulePromt;
    private String scheduleTitle;
    private TextView textView;
    private TextView titleView;
    private int xOff;
    private int yOff;

    public DvrConfirmDialog(Context context) {
        super(context, 2131755420);
    }

    public DvrConfirmDialog(Context context, String prompt, String title, int confirmType2) {
        super(context, 2131755420);
        this.mContext = context;
        this.schedulePromt = prompt;
        this.scheduleTitle = title;
        this.confirmType = confirmType2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registReciver();
        setContentView(R.layout.pvr_dialog_one_button);
        initView(this.confirmType);
        setPositon(0, 0);
    }

    private void registReciver() {
        this.mContext.registerReceiver(new DialogDismissRecevier(), new IntentFilter("com.mtk.dialog.dismiss"));
        this.mContext.registerReceiver(new pvrReceiver(), new IntentFilter("com.mediatek.dialog.dismiss"));
    }

    private void initView(int type) {
        this.titleView = (TextView) findViewById(R.id.comm_dialog_title);
        this.textView = (TextView) findViewById(R.id.comm_dialog_text);
        this.buttonYes = (Button) findViewById(R.id.comm_dialog_buttonYes);
        this.buttonNo = (Button) findViewById(R.id.comm_dialog_buttonNo);
        this.buttonNo.setVisibility(0);
        this.textView.setText(this.schedulePromt);
        this.titleView.setText(this.scheduleTitle);
        this.buttonNo.setOnClickListener(this);
        this.buttonYes.setOnClickListener(this);
    }

    public void setPositon(int xoff, int yoff) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = xoff;
        lp.y = yoff;
        lp.width = MediaPlayer2.MEDIA_INFO_VIDEO_TRACK_LAGGING;
        lp.height = 400;
        this.xOff = xoff;
        this.yOff = yoff;
        window.setAttributes(lp);
    }

    class DialogDismissRecevier extends BroadcastReceiver {
        DialogDismissRecevier() {
        }

        public void onReceive(Context context, Intent intent) {
            DvrConfirmDialog.this.dismiss();
        }
    }

    class pvrReceiver extends BroadcastReceiver {
        pvrReceiver() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals("com.mediatek.dialog.dismiss")) {
                DvrConfirmDialog.this.dismiss();
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comm_dialog_buttonNo:
                if (this.confirmType == 1) {
                    DvrManager.getInstance().handleRecordNTF(false);
                    break;
                }
                break;
            case R.id.comm_dialog_buttonYes:
                TurnkeyUiMainActivity.resumeTurnkeyActivity(this.mContext);
                if (this.confirmType == 1) {
                    DvrManager.getInstance().handleRecordNTF(true);
                    break;
                }
                break;
        }
        dismiss();
    }
}
