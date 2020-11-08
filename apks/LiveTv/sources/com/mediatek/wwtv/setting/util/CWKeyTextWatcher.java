package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

public class CWKeyTextWatcher implements TextWatcher {
    static final String TAG = "CWKeyTextWatcher";
    Context mContext;

    public CWKeyTextWatcher() {
    }

    public CWKeyTextWatcher(Context context) {
        this.mContext = context;
    }

    public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged-editString :" + s.toString());
        if (s.length() > 0) {
            int pos = s.length() - 1;
            char ch = s.charAt(pos);
            if (ch >= '0' && ch <= '9') {
                return;
            }
            if (ch >= 'A' && ch <= 'F') {
                return;
            }
            if (ch < 'a' || ch > 'f') {
                s.delete(pos, pos + 1);
                Toast.makeText(this.mContext, genTipStr(ch), 1).show();
            }
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, "beforeTextChanged :" + s.toString());
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    private String genTipStr(char ch) {
        return "Charactor '" + ch + "' is invalid ! you only can input 0-9 or a-f or A-F";
    }
}
