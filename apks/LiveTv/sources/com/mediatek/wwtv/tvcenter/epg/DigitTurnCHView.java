package com.mediatek.wwtv.tvcenter.epg;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class DigitTurnCHView extends FrameLayout {
    private static final int MSG_INPUT_DIGIT_NUM = 1;
    private static final int NAV_TIMEOUT = 3000;
    private static final String TAG = "DigitTurnCHView";
    private Context mContext;
    /* access modifiers changed from: private */
    public OnDigitTurnCHCallback mDigitTurnCHCallback;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkLog.d(DigitTurnCHView.TAG, "msg.what=" + msg.what);
            if (msg.what == 1) {
                try {
                    int inputDigit = Integer.parseInt(DigitTurnCHView.this.mStrInputNum);
                    MtkLog.d(DigitTurnCHView.TAG, "inputDigit=" + inputDigit);
                    DigitTurnCHView.this.mDigitTurnCHCallback.onTurnCH(inputDigit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DigitTurnCHView.this.hideView();
            }
        }
    };
    /* access modifiers changed from: private */
    public String mStrInputNum = "";
    private TextView mTxtNumKey;

    public interface OnDigitTurnCHCallback {
        void onTurnCH(int i);
    }

    public DigitTurnCHView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public DigitTurnCHView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DigitTurnCHView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.mTxtNumKey = new TextView(this.mContext);
        this.mTxtNumKey.setTextColor(this.mContext.getResources().getColor(R.color.white));
        this.mTxtNumKey.setTextSize((float) this.mContext.getResources().getDimensionPixelOffset(R.dimen.digit_turn_text_size));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1);
        this.mTxtNumKey.setGravity(17);
        addView(this.mTxtNumKey, lp);
    }

    public void setOnDigitTurnCHCallback(OnDigitTurnCHCallback callback) {
        this.mDigitTurnCHCallback = callback;
    }

    public void keyHandler(int keyCode, KeyEvent event) {
        MtkLog.d(TAG, "keyCode=" + keyCode);
        inputNumKey(keyCode);
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        this.mTxtNumKey.setText(this.mStrInputNum);
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    public void resetData() {
        this.mStrInputNum = "";
    }

    private void inputNumKey(int keyCode) {
        int realNum = keyCode - 7;
        if (this.mStrInputNum.startsWith("0")) {
            this.mStrInputNum = realNum + "";
        } else {
            this.mStrInputNum += realNum;
        }
        this.mStrInputNum = trimStartsWith0(this.mStrInputNum);
        this.mStrInputNum = this.mStrInputNum.length() > 4 ? this.mStrInputNum.substring(1, 5) : this.mStrInputNum;
        MtkLog.d(TAG, "mStrInputNum=" + this.mStrInputNum);
    }

    public void hideView() {
        setVisibility(8);
        resetData();
        this.mHandler.removeMessages(1);
    }

    private String trimStartsWith0(String inputStr) {
        if (!inputStr.startsWith("0")) {
            return inputStr;
        }
        String inputStr2 = inputStr.substring(1);
        trimStartsWith0(inputStr2);
        return inputStr2;
    }
}
