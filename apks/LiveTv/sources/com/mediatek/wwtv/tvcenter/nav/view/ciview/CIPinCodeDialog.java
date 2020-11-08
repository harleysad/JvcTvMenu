package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.PinDialogFragment;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class CIPinCodeDialog extends Dialog {
    private static final String TAG = "CIPinCodeDialog";
    private static CIPinCodeDialog mDialog;
    private final int PIN_CODE_LEN = 4;
    private LinearLayout dialogLayout;
    private boolean isKeyShowDialog = false;
    /* access modifiers changed from: private */
    public CIStateChangedCallBack mCIState;
    private Context mContext;
    private TextView mTitle;
    private PinDialogFragment pinDialogFragment;
    private String realPwd = "";
    private String showPwd;

    private CIPinCodeDialog(Context context) {
        super(context, 2131755419);
        this.mContext = context;
        mDialog = this;
    }

    public static CIPinCodeDialog getInstance(Context context) {
        if (mDialog == null) {
            mDialog = new CIPinCodeDialog(context);
        }
        return mDialog;
    }

    public void setCIStateChangedCallBack(CIStateChangedCallBack state) {
        this.mCIState = state;
        this.mCIState.setPinCodeDialog(this);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        setContentView(getLayoutInflater().inflate(R.layout.menu_ci_pin_code_dialog, (ViewGroup) null));
        setWindowPosition();
        this.dialogLayout = (LinearLayout) findViewById(R.id.pin_code_dialog);
        this.mTitle = (TextView) findViewById(R.id.ci_input_pin_code_title);
        this.pinDialogFragment = (PinDialogFragment) ((TurnkeyUiMainActivity) this.mContext).getFragmentManager().findFragmentById(R.id.ci_input_pin_code_num);
        this.pinDialogFragment.setResultListener(new PinDialogFragment.ResultListener() {
            public void done(String pinCode) {
                MtkTvCI ci = CIPinCodeDialog.this.mCIState.getCIHandle();
                if (ci != null) {
                    int camPinCode = ci.setCamPinCode(pinCode);
                    CIPinCodeDialog.this.dismiss();
                }
            }
        });
        this.mTitle.setText(R.string.menu_setup_ci_pin_code_input_tip);
    }

    public void show() {
        MtkLog.d(TAG, "show");
        this.isKeyShowDialog = true;
        super.show();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MtkLog.d(TAG, "dispatchKeyEvent event.getKeyCode() " + event.getKeyCode());
        if (event.getAction() == 0) {
            switch (event.getKeyCode()) {
                case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                    MtkLog.d(TAG, "TurnkeyUiMainActivity");
                    dismiss();
                    break;
            }
        }
        if (!this.isKeyShowDialog) {
            return super.dispatchKeyEvent(event);
        }
        this.isKeyShowDialog = false;
        return true;
    }

    private void setWindowPosition() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        TypedValue sca = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.nav_ci_window_size_width, sca, true);
        float w = sca.getFloat();
        this.mContext.getResources().getValue(R.dimen.nav_ci_window_size_height, sca, true);
        float h = sca.getFloat();
        lp.width = (int) (((float) display.getWidth()) * w);
        lp.height = (int) (((float) display.getHeight()) * h);
        lp.gravity = 17;
        window.setAttributes(lp);
    }
}
