package com.mediatek.wwtv.tvcenter.nav.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicDialog;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class InfoBarDialog extends NavBasicDialog implements ComponentStatusListener.ICStatusListener {
    public static final int EDGE_INFO = 1;
    public static final int INFO_BAR = 0;
    public static final int LEVER_INFO = 0;
    private static final String TAG = "InfoBarDialog";
    public static final int URGENT_BAR = 2;
    public static final int WARNING_BAR = 1;
    private static int mHeight = 50;
    private static InfoBarDialog mInfoBarDialog = null;
    private static int mWidth = 576;
    private int mCurrentShowType;
    private ImageView mImageView;
    private String mInfo;
    private TextView mTextView;
    private int mTheme;
    private int mTriggerType;

    private InfoBarDialog(Context context) {
        this(context, R.style.nav_dialog);
    }

    private InfoBarDialog(Context context, int theme) {
        super(context, R.style.nav_dialog);
        this.mCurrentShowType = -1;
        this.mTriggerType = -1;
        this.mInfo = "";
        this.mImageView = null;
        this.mTextView = null;
        this.mTheme = 0;
        this.componentID = NavBasic.NAV_COMP_ID_INFO_BAR;
        this.mTheme = theme;
        ComponentStatusListener.getInstance().addListener(1, this);
    }

    public boolean isKeyHandler(int keyCode) {
        return false;
    }

    public boolean isCoExist(int componentID) {
        return false;
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        if (keyCode != 4) {
            if (keyCode == 255) {
                return true;
            }
        } else if (this.mTriggerType == 0) {
            return true;
        } else {
            dismiss();
        }
        if (TurnkeyUiMainActivity.getInstance() != null) {
            return TurnkeyUiMainActivity.getInstance().KeyHandler(keyCode, event);
        }
        return false;
    }

    public boolean initView() {
        super.initView();
        setContentView(R.layout.nav_ib_view);
        this.mImageView = (ImageView) findViewById(R.id.ib_image_icon);
        this.mTextView = (TextView) findViewById(R.id.ib_text);
        setWindowPosition();
        return true;
    }

    public static InfoBarDialog getInstance(Context context) {
        if (mInfoBarDialog == null) {
            mInfoBarDialog = new InfoBarDialog(context, R.layout.nav_ib_view);
        }
        return mInfoBarDialog;
    }

    public boolean show(int showType, String info) {
        super.show();
        this.mTriggerType = 0;
        return attachData(showType, info);
    }

    public boolean show(int showType, String info, int timeout) {
        super.show();
        this.mTriggerType = 1;
        startTimeout(timeout);
        return attachData(showType, info);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        dismiss(1);
    }

    public void dismiss(int type) {
        if (this.mTriggerType == type) {
            this.mCurrentShowType = -1;
            this.mTriggerType = -1;
        }
        super.dismiss();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        super.dispatchKeyEvent(event);
        return true;
    }

    private void setWindowPosition() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (mWidth * ScreenConstant.SCREEN_WIDTH) / 1280;
        lp.height = (mHeight * ScreenConstant.SCREEN_HEIGHT) / 720;
        lp.x = 0;
        lp.y = ((ScreenConstant.SCREEN_HEIGHT / 2) - (lp.height / 2)) - 100;
        MtkLog.d(TAG, "ScreenConstant.SCREEN_WIDTH=" + ScreenConstant.SCREEN_WIDTH + ",ScreenConstant.SCREEN_HEIGHT=" + ScreenConstant.SCREEN_HEIGHT + ",lp.width=" + lp.width + "," + lp.x + ", lp.height=" + lp.height + "," + lp.y);
        window.setAttributes(lp);
    }

    private boolean attachData(int showType, String info) {
        this.mCurrentShowType = showType;
        this.mInfo = info;
        switch (this.mCurrentShowType) {
            case 0:
                this.mImageView.setImageResource(R.drawable.nav_ib_info_icon);
                break;
            case 1:
                this.mImageView.setImageResource(R.drawable.nav_ib_warning_icon);
                break;
            case 2:
                this.mImageView.setImageResource(R.drawable.nav_ib_urgent_icon);
                break;
            default:
                MtkLog.d(TAG, "/can not find type~");
                return false;
        }
        this.mTextView.setText(this.mInfo);
        return true;
    }

    public boolean isInfoIn() {
        if (this.mTriggerType == 0) {
            MtkLog.d(TAG, " isInfoIn true");
            return true;
        }
        MtkLog.d(TAG, " isInfoIn false");
        return false;
    }

    public boolean showInfoIn() {
        if (this.mTriggerType != 0) {
            return false;
        }
        MtkLog.d(TAG, " showInfoIn LEVER_INFO");
        return show(this.mCurrentShowType, this.mInfo);
    }

    public void updateComponentStatus(int statusID, int value) {
        if (this.mTriggerType == 0 && !ComponentsManager.getInstance().isComponentsShow()) {
            super.show();
        }
    }

    public boolean deinitView() {
        mInfoBarDialog = null;
        this.mCurrentShowType = -1;
        this.mTriggerType = -1;
        return true;
    }

    public void handlerMessage(int code) {
        MtkLog.d(TAG, " handlerMessage code = " + code);
        if ((code == 4 || code == 5 || code == 10 || code == 11 || code == 18) && this.mTriggerType == 0) {
            this.mCurrentShowType = -1;
            this.mTriggerType = -1;
            if (isVisible()) {
                dismiss();
            }
        }
    }
}
