package com.mediatek.wwtv.tvcenter.nav.view.ciview;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class PwdPincodeFragment extends Fragment {
    private static boolean DEBUG = true;
    private static final String TAG = "PwdPincodeFragment";
    final int MAXINPUTS = 4;
    final String PWD_CHAR = "*";
    private Context context;
    int currentFocusViewIndex = 0;
    private CancelBackListener mCancelListener;
    private final Handler mHandler = new Handler();
    private ResultListener mListener;
    TextView[] pwdInputViews = new TextView[4];
    View.OnKeyListener pwdKeyListener = new View.OnKeyListener() {
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
            return true;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onKey(android.view.View r6, int r7, android.view.KeyEvent r8) {
            /*
                r5 = this;
                int r0 = r8.getAction()
                r1 = 0
                if (r0 != 0) goto L_0x009b
                r0 = 4
                r2 = 1
                if (r7 == r0) goto L_0x0091
                r3 = 66
                if (r7 == r3) goto L_0x0081
                switch(r7) {
                    case 7: goto L_0x001b;
                    case 8: goto L_0x001b;
                    case 9: goto L_0x001b;
                    case 10: goto L_0x001b;
                    case 11: goto L_0x001b;
                    case 12: goto L_0x001b;
                    case 13: goto L_0x001b;
                    case 14: goto L_0x001b;
                    case 15: goto L_0x001b;
                    case 16: goto L_0x001b;
                    default: goto L_0x0012;
                }
            L_0x0012:
                switch(r7) {
                    case 19: goto L_0x001a;
                    case 20: goto L_0x001a;
                    case 21: goto L_0x001a;
                    case 22: goto L_0x001a;
                    case 23: goto L_0x0081;
                    default: goto L_0x0015;
                }
            L_0x0015:
                switch(r7) {
                    case 166: goto L_0x001a;
                    case 167: goto L_0x001a;
                    default: goto L_0x0018;
                }
            L_0x0018:
                goto L_0x009a
            L_0x001a:
                return r2
            L_0x001b:
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r1 = r1.currentFocusViewIndex
                if (r1 >= r0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                android.widget.TextView[] r1 = r1.pwdInputViews
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r3 = r3.currentFocusViewIndex
                r1 = r1[r3]
                java.lang.String r3 = "*"
                r1.setText(r3)
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r3 = r1.currentFocusViewIndex
                int r3 = r3 + r2
                r1.currentFocusViewIndex = r3
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                java.lang.String r4 = r3.realPwd
                r1.append(r4)
                java.lang.String r4 = ""
                r1.append(r4)
                int r4 = r7 + -7
                r1.append(r4)
                java.lang.String r1 = r1.toString()
                r3.realPwd = r1
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r1 = r1.currentFocusViewIndex
                if (r1 >= r0) goto L_0x0066
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r0 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                android.widget.TextView[] r0 = r0.pwdInputViews
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r1 = r1.currentFocusViewIndex
                r0 = r0[r1]
                r0.requestFocus()
            L_0x0066:
                java.lang.String r0 = "PwdPincodeFragment"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "realPwd=="
                r1.append(r3)
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r3 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                java.lang.String r3 = r3.realPwd
                r1.append(r3)
                java.lang.String r1 = r1.toString()
                com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
                return r2
            L_0x0081:
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                int r1 = r1.currentFocusViewIndex
                if (r1 != r0) goto L_0x0090
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r0 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r1 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                java.lang.String r1 = r1.realPwd
                r0.done(r1)
            L_0x0090:
                return r2
            L_0x0091:
                com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment r0 = com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.this
                boolean r0 = r0.cancelback()
                if (r0 == 0) goto L_0x009a
                return r2
            L_0x009a:
                return r1
            L_0x009b:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.ciview.PwdPincodeFragment.AnonymousClass1.onKey(android.view.View, int, android.view.KeyEvent):boolean");
        }
    };
    TextView pwdValue1;
    TextView pwdValue2;
    TextView pwdValue3;
    TextView pwdValue4;
    String realPwd = "";

    public interface CancelBackListener {
        void cancel();
    }

    public interface ResultListener {
        void done(String str);
    }

    public void requestFirstShowFcous() {
        this.pwdInputViews[0].requestFocus();
    }

    public void setResultListener(ResultListener listener) {
        this.mListener = listener;
    }

    public void setCancelBackListener(CancelBackListener listener) {
        this.mCancelListener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MtkLog.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.ci_pwd_pincode_fragment, container, false);
        initViews(v);
        return v;
    }

    private void initViews(View v) {
        this.pwdValue1 = (TextView) v.findViewById(R.id.first);
        this.pwdValue2 = (TextView) v.findViewById(R.id.second);
        this.pwdValue3 = (TextView) v.findViewById(R.id.third);
        this.pwdValue4 = (TextView) v.findViewById(R.id.fourth);
        this.pwdInputViews[0] = this.pwdValue1;
        this.pwdInputViews[1] = this.pwdValue2;
        this.pwdInputViews[2] = this.pwdValue3;
        this.pwdInputViews[3] = this.pwdValue4;
        this.pwdValue1.setOnKeyListener(this.pwdKeyListener);
        this.pwdValue2.setOnKeyListener(this.pwdKeyListener);
        this.pwdValue3.setOnKeyListener(this.pwdKeyListener);
        this.pwdValue4.setOnKeyListener(this.pwdKeyListener);
    }

    /* access modifiers changed from: private */
    public void done(String pin) {
        if (this.mListener != null) {
            this.mListener.done(pin);
        }
        resetPinInput();
    }

    /* access modifiers changed from: private */
    public boolean cancelback() {
        resetPinInput();
        if (this.mCancelListener == null) {
            return false;
        }
        this.mCancelListener.cancel();
        return true;
    }

    private void resetPinInput() {
        this.realPwd = "";
        this.currentFocusViewIndex = 0;
        for (TextView tv : this.pwdInputViews) {
            tv.setText("");
        }
    }
}
