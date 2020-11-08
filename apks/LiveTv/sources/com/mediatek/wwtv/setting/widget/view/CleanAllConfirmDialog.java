package com.mediatek.wwtv.setting.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class CleanAllConfirmDialog extends Dialog {
    public static final int BTN_NO_CLICK = 1;
    public static final int BTN_YES_CLICK = 0;
    private static final String TAG = "ConfirmDialog";
    private int dialogResult;
    private WindowManager.LayoutParams lp;
    private String mButtonNoName;
    private String mButtonYesName;
    private IResultCallback mCallback;
    private Handler mHandler;
    View.OnKeyListener mKeyListener;
    private Button mNButton;
    private int mSeconds;
    private String mTips;
    private String mTitle;
    private Button mYButton;

    public interface IResultCallback {
        void handleUserSelection(int i);
    }

    public CleanAllConfirmDialog(Context context, String tips) {
        this(context, tips, context.getString(R.string.common_dialog_msg_yes), context.getString(R.string.common_dialog_msg_no));
    }

    public CleanAllConfirmDialog(Context context, String tips, String btnYesStr, String btnNoStr) {
        super(context, R.layout.menu_comm_long_dialog);
        this.mTitle = "";
        this.mTips = "";
        this.mButtonYesName = "";
        this.mButtonNoName = "";
        this.mSeconds = -1;
        this.dialogResult = -1;
        this.mCallback = null;
        this.mTips = tips;
        this.mButtonYesName = btnYesStr;
        this.mButtonNoName = btnNoStr;
        this.lp = getWindow().getAttributes();
        this.lp.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
        this.lp.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.2d);
        getWindow().setAttributes(this.lp);
        init();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.mTitle.length() == 0) {
            ((TextView) findViewById(R.id.comm_dialog_title)).setVisibility(8);
        } else {
            ((TextView) findViewById(R.id.comm_dialog_title)).setText(this.mTitle);
        }
        ((TextView) findViewById(R.id.comm_dialog_text)).setText(this.mTips);
        this.mYButton.setText(this.mButtonYesName);
        this.mYButton.setOnKeyListener(this.mKeyListener);
        this.mNButton.setText(this.mButtonNoName);
        this.mNButton.setOnKeyListener(this.mKeyListener);
        getWindow().setWindowAnimations(0);
    }

    public void init() {
        setContentView(R.layout.menu_comm_long_dialog);
        this.mYButton = (Button) findViewById(R.id.comm_dialog_buttonYes);
        this.mNButton = (Button) findViewById(R.id.comm_dialog_buttonNo);
    }

    public Button getButtonYes() {
        return this.mYButton;
    }

    public Button getButtonNo() {
        return this.mNButton;
    }

    public void setButtonYesName(String buttonYesName) {
        this.mButtonYesName = buttonYesName;
    }

    public void setButtonNoName(String buttonNoName) {
        this.mButtonNoName = buttonNoName;
    }

    public void setTips(String tips) {
        this.mTips = tips;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setCallback(IResultCallback callback) {
        this.mCallback = callback;
    }

    public void setPositon(int xoff, int yoff) {
        this.lp.x = xoff;
        this.lp.y = yoff;
        getWindow().setAttributes(this.lp);
    }

    public void setSize(int width, int height) {
        this.lp.width = width;
        this.lp.height = height;
        getWindow().setAttributes(this.lp);
    }

    public void setTimeout(int second) {
        this.mSeconds = second;
    }

    public void bindKeyListener(View.OnKeyListener keyListener) {
        this.mKeyListener = keyListener;
    }

    /* access modifiers changed from: private */
    public void endDialog(int result) {
        dismiss();
        this.dialogResult = result;
        if (this.mHandler != null) {
            this.mHandler.removeMessages(1);
        }
        if (this.mCallback != null) {
            this.mCallback.handleUserSelection(this.dialogResult);
        }
    }

    public void showDialog() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                MtkLog.v(CleanAllConfirmDialog.TAG, "msg.what:" + msg.what);
                if (msg.what == 1) {
                    CleanAllConfirmDialog.this.endDialog(1);
                }
            }
        };
        if (this.mSeconds > 0) {
            MtkLog.v(TAG, "mSeconds:" + this.mSeconds);
            Message msg = Message.obtain();
            msg.what = 1;
            this.mHandler.sendMessageDelayed(msg, (long) this.mSeconds);
        }
        super.show();
    }
}
