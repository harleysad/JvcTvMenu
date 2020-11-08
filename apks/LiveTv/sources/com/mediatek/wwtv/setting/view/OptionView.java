package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.setting.base.scan.model.OnValueChangedListener;
import com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OptionView extends ListViewItemView implements RespondedKeyEvent {
    private static final String TAG = "OptionView";
    private static List<Timer> timers = new ArrayList();
    private TextView mNameView;
    private ImageView mRightImageIv;
    /* access modifiers changed from: private */
    public OnValueChangedListener mValueChangedListener;
    private TextView mValueView;
    private SaveValue saveV;

    private class MyTimerTask extends TimerTask {
        private MyTimerTask() {
        }

        public void run() {
            if (OptionView.this.mValueChangedListener != null) {
                OptionView.this.mValueChangedListener.onValueChanged(OptionView.this, OptionView.this.mDataItem.mInitValue);
            }
        }
    }

    public OptionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public OptionView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public int getValue() {
        return this.mDataItem.getmInitValue();
    }

    public void setValue(int mPositon) {
        this.mDataItem.setmInitValue(mPositon);
        this.mValueView.setText(this.mDataItem.getmOptionValue()[mPositon]);
        if (this.mValueChangedListener != null) {
            this.mValueChangedListener.onValueChanged(this, mPositon);
        }
    }

    private void setViewNameEx(String viewName) {
        this.mNameView.setText(viewName);
    }

    public TextView getNameView() {
        return this.mNameView;
    }

    public TextView getValueView() {
        return this.mValueView;
    }

    public void setValueChangedListener(OnValueChangedListener mValueChangedListener2) {
        this.mValueChangedListener = mValueChangedListener2;
    }

    private void init() {
        addView((LinearLayout) inflate(this.context, R.layout.menu_option_view, (ViewGroup) null), new LinearLayout.LayoutParams(-1, -1));
        this.mNameView = (TextView) findViewById(R.id.common_tv_itemname);
        this.mValueView = (TextView) findViewById(R.id.common_tv_itemshow);
        this.mRightImageIv = (ImageView) findViewById(R.id.common_iv_itemimage);
        this.saveV = SaveValue.getInstance(this.context);
    }

    public void setAdapter(SetConfigListViewAdapter.DataItem mDataItem) {
        this.mDataItem = mDataItem;
        this.mId = mDataItem.mItemID;
        setViewNameEx(this.mDataItem.getmName());
        try {
            this.mValueView.setText(this.mDataItem.mOptionValue[this.mDataItem.mInitValue]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void switchValueNext() {
        try {
            if (this.mDataItem.mInitValue != this.mDataItem.mOptionValue.length - 1) {
                TextView textView = this.mValueView;
                String[] strArr = this.mDataItem.mOptionValue;
                SetConfigListViewAdapter.DataItem dataItem = this.mDataItem;
                int i = dataItem.mInitValue + 1;
                dataItem.mInitValue = i;
                textView.setText(strArr[i]);
            } else {
                this.mDataItem.mInitValue = 0;
                this.mValueView.setText(this.mDataItem.mOptionValue[this.mDataItem.mInitValue]);
            }
            this.mValueChangedListener.onValueChanged(this, this.mDataItem.mInitValue);
            MtkLog.v(TAG, "" + this.mDataItem.mInitValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void switchValuePrevious() {
        try {
            if (this.mDataItem.mInitValue != 0) {
                TextView textView = this.mValueView;
                String[] strArr = this.mDataItem.mOptionValue;
                SetConfigListViewAdapter.DataItem dataItem = this.mDataItem;
                int i = dataItem.mInitValue - 1;
                dataItem.mInitValue = i;
                textView.setText(strArr[i]);
            } else {
                this.mDataItem.mInitValue = this.mDataItem.mOptionValue.length - 1;
                this.mValueView.setText(this.mDataItem.mOptionValue[this.mDataItem.mInitValue]);
            }
            this.mValueChangedListener.onValueChanged(this, this.mDataItem.mInitValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onKeyEnter() {
    }

    public void onKeyLeft() {
        if (this.mDataItem.isEnable) {
            switchValuePrevious();
        }
    }

    public void onKeyRight() {
        if (this.mDataItem.isEnable) {
            switchValueNext();
        }
    }

    public void showValue(int value) {
        if (value < 0 || value > this.mDataItem.mOptionValue.length - 1) {
            throw new IllegalArgumentException("value is Illegal value");
        }
        this.mDataItem.mInitValue = value;
        this.mValueView.setText(this.mDataItem.mOptionValue[value]);
    }

    public void setRightImageSource(boolean isHighlight) {
        if (isHighlight) {
            this.mRightImageIv.setImageResource(R.drawable.menu_icon_select_hi);
        } else {
            this.mRightImageIv.setImageResource(R.drawable.menu_icon_select_nor);
        }
    }
}
