package com.mediatek.wwtv.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;

public class BissKeyPreferenceDialog extends Dialog implements TextView.OnEditorActionListener, DialogInterface.OnShowListener {
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
    public static final String TAG = "BissKeyPreferenceDialog";
    private int defaultValue;
    public int height = 0;
    private String itemID;
    private int length = -1;
    WindowManager.LayoutParams lp;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mDescString;
    private EditText mEditText;
    private TextView.OnEditorActionListener mEditorActionListener = null;
    private TextWatcher mTextWatcher = null;
    private TextView modelNameShow;
    /* access modifiers changed from: private */
    public Preference preference;
    private RadioButton rh;
    private RadioButton rv;
    private TextView serialNumShow;
    private TextView versionShow;
    public int width = 0;
    Window window;
    private int xOff;
    private int yOff;

    public BissKeyPreferenceDialog(Context context, int defaultValue2) {
        super(context, 2131755419);
        this.mContext = context;
        this.defaultValue = defaultValue2;
        this.window = getWindow();
        this.lp = this.window.getAttributes();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_dialog);
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
    }

    public void setDefaultValue(int defaultValue2) {
        this.defaultValue = defaultValue2;
        if (defaultValue2 == 0) {
            if (this.rh != null) {
                this.rh.setChecked(true);
            }
        } else if (this.rv != null) {
            this.rv.setChecked(true);
        }
        Log.d(TAG, "dialog set pola:" + defaultValue2);
        Log.d(TAG, "set defaultValue:" + defaultValue2);
    }

    public void init() {
        Log.d(TAG, "defaultValue:" + this.defaultValue);
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup_pola);
        this.rh = (RadioButton) findViewById(R.id.btn_h);
        this.rv = (RadioButton) findViewById(R.id.btn_v);
        if (this.defaultValue == 0) {
            this.rh.setChecked(true);
        } else {
            this.rv.setChecked(true);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                Log.d(BissKeyPreferenceDialog.TAG, "arg1:" + arg1);
                if (arg1 == R.id.btn_h) {
                    BissKeyPreferenceDialog.this.preference.setSummary((CharSequence) BissKeyPreferenceDialog.this.mContext.getResources().getString(R.string.menu_setup_biss_key_horizonal));
                } else {
                    BissKeyPreferenceDialog.this.preference.setSummary((CharSequence) BissKeyPreferenceDialog.this.mContext.getResources().getString(R.string.menu_setup_biss_key_vertical));
                }
                BissKeyPreferenceDialog.this.cancel();
            }
        });
    }

    public void onShow(DialogInterface arg0) {
    }

    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
        return false;
    }
}
