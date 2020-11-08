package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class BissKeyEditDialog extends Dialog implements TextView.OnEditorActionListener, DialogInterface.OnShowListener {
    public static final String EXTRA_CANFLOAT = "canFloat";
    public static final String EXTRA_CAN_WATCH_TEXT = "canWatchText";
    public static final String EXTRA_DESC = "description";
    public static final String EXTRA_DIGIT = "isDigit";
    private static final String EXTRA_EDIT_TEXT_RES_ID = "edit_text_res_id";
    public static final String EXTRA_INITIAL_TEXT = "initialText";
    public static final String EXTRA_ITEMID = "itemId";
    private static final String EXTRA_LAYOUT_RES_ID = "layout_res_id";
    public static final String EXTRA_LENGTH = "length";
    public static final String EXTRA_PASSWORD = "password";
    public static final String TAG = "EditTextActivity";
    private String defaultValue;
    public int height = 0;
    private String itemID;
    private int length = -1;
    WindowManager.LayoutParams lp;
    private Context mContext;
    private String mDescString;
    private EditText mEditText;
    private TextView.OnEditorActionListener mEditorActionListener = null;
    private int mInputType = -1;
    private TextWatcher mTextWatcher = null;
    private TextView modelNameShow;
    private Preference preference;
    private TextView serialNumShow;
    private TextView versionShow;
    public int width = 0;
    Window window;
    private int xOff;
    private int yOff;

    public BissKeyEditDialog(Context context, String descString, String defaultValue2, String itemId) {
        super(context, 2131755419);
        this.mContext = context;
        this.mDescString = descString;
        this.defaultValue = defaultValue2;
        this.itemID = itemId;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittext_activity);
        this.width = (int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.55d);
        this.lp.width = this.width;
        this.height = (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.35d);
        this.lp.height = this.height;
        this.lp.x = 0 - (this.lp.width / 3);
        this.window.setAttributes(this.lp);
        setOnShowListener(this);
        init();
    }

    public void setPositon(int xoff, int yoff) {
        Window window2 = getWindow();
        WindowManager.LayoutParams lp2 = window2.getAttributes();
        lp2.x = xoff;
        lp2.y = yoff;
        this.xOff = xoff;
        this.yOff = yoff;
        window2.setAttributes(lp2);
    }

    public void setPreference(Preference preference2) {
        this.preference = preference2;
        if (!TextUtils.isEmpty(preference2.getSummary()) && this.mEditText != null) {
            this.mEditText.setText(preference2.getSummary());
            this.mEditText.setSelection(preference2.getSummary().length());
        }
    }

    public void setLength(int length2) {
        this.length = length2;
    }

    public int getLength() {
        return this.length;
    }

    public void init() {
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(this.mDescString);
        description.setVisibility(0);
        this.mEditText = (EditText) findViewById(R.id.edittext);
        this.mEditText.requestFocus();
        this.mEditText.setOnEditorActionListener(this);
        if (this.mInputType != -1) {
            this.mEditText.setInputType(this.mInputType);
        }
        if (this.preference != null && !TextUtils.isEmpty(this.preference.getSummary())) {
            this.mEditText.setText(this.preference.getSummary());
            this.mEditText.setSelection(this.preference.getSummary().length());
        }
        if (this.length != -1) {
            this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.length)});
        }
    }

    public void setInputType(int inputtype) {
        this.mInputType = inputtype;
    }

    public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
        if (6 != actionId) {
            return false;
        }
        if (!checkLengthIsEnough()) {
            return true;
        }
        if (this.itemID != null && this.itemID.equals(MenuConfigManager.BISS_KEY_FREQ)) {
            initPreferece(3000, MenuConfigManager.BISS_KEY_FREQ_MAX);
        } else if (this.itemID != null && this.itemID.equals(MenuConfigManager.BISS_KEY_SYMBOL_RATE)) {
            initPreferece(2000, MenuConfigManager.BISS_KEY_SYMBOL_RATE_MAX);
        } else if (this.itemID != null && this.itemID.equals(MenuConfigManager.BISS_KEY_SVC_ID)) {
            initPreferece(0, 65535);
        } else if (this.itemID != null && this.itemID.equals(MenuConfigManager.BISS_KEY_CW_KEY)) {
            this.preference.setSummary((CharSequence) String.valueOf(this.mEditText.getText().toString()));
        }
        cancel();
        return true;
    }

    private void initPreferece(int min, int max) {
        int now = Integer.parseInt(this.mEditText.getText().toString());
        if (now < min) {
            now = 3000;
        } else if (now > max) {
            now = max;
        }
        this.preference.setSummary((CharSequence) String.valueOf(now));
    }

    private boolean checkLengthIsEnough() {
        String ret = this.mEditText.getText().toString();
        if (TextUtils.isEmpty(ret)) {
            Toast.makeText(this.mContext, "Can't empty!", 0).show();
            return false;
        } else if (this.itemID == null || !this.itemID.equals(MenuConfigManager.BISS_KEY_CW_KEY) || ret.length() >= 16) {
            return true;
        } else {
            Toast.makeText(this.mContext, "cwkey can't less than 16", 0).show();
            return false;
        }
    }

    public void onShow(DialogInterface arg0) {
        if (this.preference != null && !TextUtils.isEmpty(this.preference.getSummary())) {
            this.mEditText.setText(this.preference.getSummary());
            this.mEditText.setSelection(this.preference.getSummary().length());
        }
    }
}
