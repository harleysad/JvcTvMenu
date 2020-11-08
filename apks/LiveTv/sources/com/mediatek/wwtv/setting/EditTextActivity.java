package com.mediatek.wwtv.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.wwtv.setting.base.scan.ui.BaseCustomActivity;
import com.mediatek.wwtv.setting.util.CWKeyTextWatcher;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class EditTextActivity extends BaseCustomActivity implements TextWatcher, TextView.OnEditorActionListener {
    public static final String EXTRA_ALLOW_EMPTY = "allowEmpty";
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
    public static final String TYPE_CLASS_TEXT = "class_text";
    private String itemID;
    private EditText mEditText;
    private TextView.OnEditorActionListener mEditorActionListener = null;
    private TextWatcher mTextWatcher = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        TextView description;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittext_activity);
        this.mEditText = (EditText) findViewById(R.id.edittext);
        this.itemID = getIntent().getStringExtra("itemId");
        String descString = getIntent().getStringExtra("description");
        if (!TextUtils.isEmpty(descString) && (description = (TextView) findViewById(R.id.description)) != null) {
            description.setText(descString);
            description.setVisibility(0);
        }
        if (this.mEditText != null) {
            this.mEditText.setOnEditorActionListener(this);
            this.mEditText.addTextChangedListener(this);
            this.mEditText.requestFocus();
            if (getIntent().getBooleanExtra("password", false)) {
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            if (getIntent().getBooleanExtra("isDigit", false)) {
                this.mEditText.setInputType(2);
            }
            if (getIntent().getBooleanExtra("canFloat", false)) {
                this.mEditText.setInputType(8194);
            }
            if (getIntent().getBooleanExtra(TYPE_CLASS_TEXT, false)) {
                this.mEditText.setInputType(1);
            }
            int length = getIntent().getIntExtra("length", -1);
            if (length != -1) {
                this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            }
            String initialText = getIntent().getStringExtra("initialText");
            MtkLog.d("EditTextActivity", "initialText:" + initialText);
            if (!TextUtils.isEmpty(initialText)) {
                this.mEditText.setText(initialText);
                this.mEditText.setSelection(initialText.length());
            }
            if (getIntent().getBooleanExtra("canWatchText", false) && this.itemID != null && this.itemID.equals(MenuConfigManager.BISS_KEY_CW_KEY)) {
                setTextWatcher(new CWKeyTextWatcher(this));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.mTextWatcher = textWatcher;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        this.mEditorActionListener = listener;
    }

    public void afterTextChanged(Editable s) {
        if (this.mTextWatcher != null) {
            this.mTextWatcher.afterTextChanged(s);
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (this.mTextWatcher != null) {
            this.mTextWatcher.beforeTextChanged(s, start, count, after);
        }
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (this.mTextWatcher != null) {
            this.mTextWatcher.onTextChanged(s, start, before, count);
        }
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        MtkLog.d("EditTextActivity", "onEditorAction-actionId:" + actionId);
        if (actionId != 6) {
            if (this.mEditorActionListener != null) {
                return this.mEditorActionListener.onEditorAction(v, actionId, event);
            }
            return false;
        } else if (!checkLengthIsEnough()) {
            return true;
        } else {
            Intent data = new Intent();
            if (!"".equals(this.mEditText.getText().toString()) || !getIntent().getBooleanExtra(EXTRA_ALLOW_EMPTY, false)) {
                data.putExtra(SaveValue.GLOBAL_VALUE_VALUE, this.mEditText.getText().toString());
            } else {
                data.putExtra(SaveValue.GLOBAL_VALUE_VALUE, "-1");
            }
            setResult(-1, data);
            finish();
            return true;
        }
    }

    private boolean checkLengthIsEnough() {
        String ret = this.mEditText.getText().toString();
        if (getIntent().getBooleanExtra(EXTRA_ALLOW_EMPTY, false)) {
            return true;
        }
        if (TextUtils.isEmpty(ret)) {
            Toast.makeText(this, "Can't empty!", 0).show();
            return false;
        } else if (this.itemID == null || !this.itemID.equals(MenuConfigManager.BISS_KEY_CW_KEY) || ret.length() >= 16) {
            return true;
        } else {
            Toast.makeText(this, "cwkey can't less than 16", 0).show();
            return false;
        }
    }
}
