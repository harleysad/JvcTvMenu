package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.MtkTvUpgrade;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DownloadFirmwareDialog extends Dialog {
    private static final int DOWNLOAD_COMPLETE = 2;
    private static final int DOWNLOAD_DOWNLOADING = 1;
    private static final int DOWNLOAD_EXIT = 5;
    private static final int DOWNLOAD_EXIT_CANCEL_DOWN = 1;
    private static final int DOWNLOAD_EXIT_FAIL = 4;
    private static final int DOWNLOAD_EXIT_NETWORK_ERROR = 2;
    private static final int DOWNLOAD_EXIT_NONE = 0;
    private static final int DOWNLOAD_EXIT_VALIDATE_ERROR = 3;
    private static final int DOWNLOAD_START = 0;
    private static final int DOWNLOAD_STATE = 0;
    private static final int DOWNLOAD_VALIDATE_COMPLETE = 4;
    private static final int DOWNLOAD_VALIDATING = 3;
    private static final int MSG_DOWNLOAD_FIRMWARE_STATE = 4545;
    private static final int MSG_UPDATE_DOWNLOAD = 2;
    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_UPDATE_STATE = 0;
    private static final int UPGRADE_STATE = 2;
    private static final int VALIDATING_STATE = 1;
    private final String TAG = "DownloadFirmwareDialog";
    /* access modifiers changed from: private */
    public Button cancelButton;
    /* access modifiers changed from: private */
    public int currentState;
    /* access modifiers changed from: private */
    public Handler downloadFirmWareHandler;
    private TextView downloadOperateEnterTv;
    /* access modifiers changed from: private */
    public TextView downloadOperateExitTv;
    private TextView downloadProgressTv;
    private TextView downloadProgressValueTv;
    private TextView downloadStatusTv;
    private TextView downloadStatusValueTv;
    /* access modifiers changed from: private */
    public TextView downloadUserToastTv;
    /* access modifiers changed from: private */
    public String firmwareDownloadPath = "http://www.mediatek.com/upgrade/upgrade_loader.pkg";
    /* access modifiers changed from: private */
    public String firmwareStorePath = "/upgrade/upgrade_loader.pkg";
    public int height = 0;
    private boolean isDownloadingOrValidating = false;
    WindowManager.LayoutParams lp = this.window.getAttributes();
    private FrimwareUpgradeCallBack mUpgradeCallBack;
    /* access modifiers changed from: private */
    public Button okButton;
    /* access modifiers changed from: private */
    public LinearLayout progressAndStatusLy;
    /* access modifiers changed from: private */
    public MtkTvUpgrade tvUpgrade;
    public int width = 0;
    Window window = getWindow();

    public boolean isDownloadingOrValidating() {
        return this.isDownloadingOrValidating;
    }

    public void setDownloadingOrValidating(boolean isDownloadingOrValidating2) {
        this.isDownloadingOrValidating = isDownloadingOrValidating2;
    }

    public DownloadFirmwareDialog(Context context) {
        super(context, 2131755419);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d("DownloadFirmwareDialog", "come in onCreate");
        setContentView(R.layout.menu_download_firmware_layout);
        this.downloadFirmWareHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == DownloadFirmwareDialog.MSG_DOWNLOAD_FIRMWARE_STATE) {
                    MtkLog.d("DownloadFirmwareDialog", "come in downloadFirmWareHandler,MSG_DOWNLOAD_FIRMWARE_STATE");
                    Bundle bundleData = msg.getData();
                    DownloadFirmwareDialog.this.firmwareUpgradeStateChange(bundleData.getInt("msgType"), bundleData.getInt("argv1"), bundleData.getInt("argv2"), bundleData.getInt("argv3"));
                }
                super.handleMessage(msg);
            }
        };
        initView();
        this.tvUpgrade = MtkTvUpgrade.getInstance();
        this.mUpgradeCallBack = new FrimwareUpgradeCallBack();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        MtkLog.d("DownloadFirmwareDialog", "come in onStart");
        showDialogWithState(0);
    }

    private void initView() {
        this.progressAndStatusLy = (LinearLayout) findViewById(R.id.download_firmware_progress_and_status_ly);
        this.okButton = (Button) findViewById(R.id.download_firmware_ok_bt);
        this.okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                MtkLog.d("DownloadFirmwareDialog", "okButton click, " + DownloadFirmwareDialog.this.currentState);
                switch (DownloadFirmwareDialog.this.currentState) {
                    case 0:
                        DownloadFirmwareDialog.this.progressAndStatusLy.setVisibility(0);
                        DownloadFirmwareDialog.this.downloadUserToastTv.setVisibility(8);
                        DownloadFirmwareDialog.this.okButton.setEnabled(false);
                        DownloadFirmwareDialog.this.okButton.setFocusable(false);
                        DownloadFirmwareDialog.this.okButton.setTextColor(-7829368);
                        DownloadFirmwareDialog.this.cancelButton.setEnabled(true);
                        DownloadFirmwareDialog.this.cancelButton.setFocusable(true);
                        DownloadFirmwareDialog.this.cancelButton.setTextColor(DownloadFirmwareDialog.this.getContext().getResources().getColor(R.color.white));
                        DownloadFirmwareDialog.this.cancelButton.requestFocus();
                        DownloadFirmwareDialog.this.tvUpgrade.startDownloadFirmware(MtkTvUpgradeDeliveryTypeBase.INTERNET, DownloadFirmwareDialog.this.firmwareDownloadPath, DownloadFirmwareDialog.this.firmwareStorePath);
                        DownloadFirmwareDialog.this.setDownloadingOrValidating(true);
                        DownloadFirmwareDialog.this.downloadOperateExitTv.setVisibility(4);
                        return;
                    case 2:
                        DownloadFirmwareDialog.this.tvUpgrade.startRebootUpgrade(MtkTvUpgradeDeliveryTypeBase.INTERNET);
                        return;
                    default:
                        return;
                }
            }
        });
        this.cancelButton = (Button) findViewById(R.id.download_firmware_cancel_bt);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                MtkLog.d("DownloadFirmwareDialog", "cancelButton click, " + DownloadFirmwareDialog.this.currentState);
                switch (DownloadFirmwareDialog.this.currentState) {
                    case 0:
                        DownloadFirmwareDialog.this.cancelButton.setEnabled(false);
                        DownloadFirmwareDialog.this.cancelButton.setFocusable(false);
                        DownloadFirmwareDialog.this.cancelButton.setTextColor(-7829368);
                        DownloadFirmwareDialog.this.tvUpgrade.cancelDownloadFirmware(MtkTvUpgradeDeliveryTypeBase.INTERNET);
                        return;
                    default:
                        return;
                }
            }
        });
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                MtkLog.d("DownloadFirmwareDialog", "onKey, " + keyCode);
                if (keyCode == 4) {
                    if (DownloadFirmwareDialog.this.isDownloadingOrValidating()) {
                        return true;
                    }
                    DownloadFirmwareDialog.this.dismiss();
                }
                return false;
            }
        });
        this.downloadProgressTv = (TextView) findViewById(R.id.download_firmware_progress_tv);
        this.downloadStatusTv = (TextView) findViewById(R.id.download_firmware_status_tv);
        this.downloadUserToastTv = (TextView) findViewById(R.id.download_firmware_user_toast);
        this.downloadProgressValueTv = (TextView) findViewById(R.id.download_firmware_progress_value_tv);
        this.downloadStatusValueTv = (TextView) findViewById(R.id.download_firmware_status_value_tv);
        this.downloadOperateEnterTv = (TextView) findViewById(R.id.download_firmware_enter_toast);
        this.downloadOperateExitTv = (TextView) findViewById(R.id.download_firmware_exit_toast);
        Point outSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        this.width = (int) (((double) outSize.x) * 0.45d);
        this.lp.width = this.width;
        this.height = (int) (((double) outSize.y) * 0.2d);
        this.lp.height = this.height;
        this.window.setAttributes(this.lp);
        showDialogWithState(0);
    }

    private void showDialogWithState(int dialogState) {
        MtkLog.d("DownloadFirmwareDialog", "showDialogWithState, " + dialogState);
        switch (dialogState) {
            case 0:
                this.progressAndStatusLy.setVisibility(8);
                this.okButton.setText(R.string.download_firmware_start_button_text);
                this.cancelButton.setText(R.string.menu_cancel);
                this.cancelButton.setFocusable(false);
                this.cancelButton.setEnabled(false);
                this.cancelButton.setTextColor(-7829368);
                this.downloadProgressTv.setText(R.string.download_firmware_progress_textview_text);
                this.downloadStatusTv.setText(R.string.download_firmware_status_textview_text);
                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_to_start_text);
                this.downloadUserToastTv.setVisibility(0);
                this.currentState = 0;
                return;
            case 1:
                this.okButton.setText(R.string.download_firmware_reboot_button_text);
                this.okButton.setEnabled(false);
                this.okButton.setFocusable(false);
                this.okButton.setTextColor(-7829368);
                this.cancelButton.setEnabled(false);
                this.cancelButton.setFocusable(false);
                this.cancelButton.setTextColor(-7829368);
                if (this.downloadUserToastTv.getVisibility() != 0) {
                    this.downloadUserToastTv.setVisibility(0);
                    this.progressAndStatusLy.setVisibility(8);
                }
                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_validating_text);
                this.downloadOperateEnterTv.setVisibility(4);
                this.downloadOperateExitTv.setVisibility(4);
                this.currentState = 1;
                return;
            case 2:
                this.okButton.setEnabled(true);
                this.okButton.setFocusable(true);
                this.okButton.requestFocus();
                this.okButton.setTextColor(getContext().getResources().getColor(R.color.white));
                this.cancelButton.setEnabled(true);
                this.cancelButton.setFocusable(true);
                this.cancelButton.setTextColor(getContext().getResources().getColor(R.color.white));
                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_upgrade_text);
                this.downloadOperateEnterTv.setVisibility(0);
                this.downloadOperateExitTv.setVisibility(0);
                this.currentState = 2;
                return;
            default:
                return;
        }
    }

    public Button getButtonOk() {
        return this.okButton;
    }

    public Button getButtonCancel() {
        return this.cancelButton;
    }

    public void setPositon(int xoff, int yoff) {
        WindowManager.LayoutParams lp2 = this.window.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        this.window.setAttributes(lp2);
    }

    /* access modifiers changed from: private */
    public void firmwareUpgradeStateChange(int msgType, int argv1, int argv2, int argv3) {
        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange,msgType =" + msgType + ", argv1 =" + argv1 + ", argv2 =" + argv2 + ", argv3 =" + argv3);
        switch (msgType) {
            case 2:
                switch (argv1) {
                    case 0:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_START");
                        this.downloadStatusValueTv.setText(R.string.download_firmware_status_value_start);
                        setDownloadingOrValidating(true);
                        return;
                    case 1:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_DOWNLOADING");
                        this.downloadStatusValueTv.setText(R.string.download_firmware_status_value_downloading);
                        TextView textView = this.downloadProgressValueTv;
                        textView.setText("" + argv2 + "%");
                        setDownloadingOrValidating(true);
                        return;
                    case 2:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_COMPLETE");
                        this.downloadStatusValueTv.setText(R.string.download_firmware_status_value_complete);
                        setDownloadingOrValidating(true);
                        return;
                    case 3:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_VALIDATING");
                        showDialogWithState(1);
                        setDownloadingOrValidating(true);
                        return;
                    case 4:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_VALIDATE_COMPLETE");
                        this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_validate_complete);
                        setDownloadingOrValidating(true);
                        return;
                    case 5:
                        MtkLog.d("DownloadFirmwareDialog", "come in firmwareUpgradeStateChange MSG_UPDATE_DOWNLOAD,DOWNLOAD_EXIT,argv2 =" + argv2);
                        switch (argv2) {
                            case 0:
                                showDialogWithState(2);
                                return;
                            case 1:
                                this.downloadStatusValueTv.setText(R.string.download_firmware_status_value_cancel_down);
                                this.okButton.setEnabled(true);
                                this.okButton.setFocusable(true);
                                this.okButton.setTextColor(getContext().getResources().getColor(R.color.white));
                                this.okButton.requestFocus();
                                this.downloadOperateExitTv.setVisibility(0);
                                setDownloadingOrValidating(false);
                                return;
                            case 2:
                                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_download_exit_network_error);
                                downloadExitWithError();
                                return;
                            case 3:
                                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_download_exit_validate_error);
                                downloadExitWithError();
                                return;
                            case 4:
                                this.downloadUserToastTv.setText(R.string.download_firmware_toast_textview_download_exit_fail);
                                downloadExitWithError();
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            default:
                return;
        }
    }

    private void downloadExitWithError() {
        setDownloadingOrValidating(false);
        if (this.downloadOperateExitTv.getVisibility() != 0) {
            this.downloadOperateExitTv.setVisibility(0);
        }
        this.okButton.setVisibility(4);
        this.cancelButton.setVisibility(4);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    private class FrimwareUpgradeCallBack extends MtkTvTVCallbackHandler {
        public FrimwareUpgradeCallBack() {
        }

        public int notifyUpgradeMessage(int msgType, int argv1, int argv2, int argv3) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putInt("msgType", msgType);
            bundle.putInt("argv1", argv1);
            bundle.putInt("argv2", argv2);
            bundle.putInt("argv3", argv3);
            Message msg = Message.obtain();
            msg.setData(bundle);
            msg.what = DownloadFirmwareDialog.MSG_DOWNLOAD_FIRMWARE_STATE;
            DownloadFirmwareDialog.this.downloadFirmWareHandler.sendMessage(msg);
            return super.notifyUpgradeMessage(msgType, argv1, argv2, argv3);
        }
    }
}
