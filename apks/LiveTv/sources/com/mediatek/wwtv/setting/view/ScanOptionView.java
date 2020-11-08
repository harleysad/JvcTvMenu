package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;

public class ScanOptionView extends TextView {
    int initValue;
    boolean isEnable;
    MenuConfigManager mConfigManager;
    Context mContext;
    String mItemID;
    String[] mOptionValues;
    TVContent mTv;

    public ScanOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void bindData(String id, String[] optionValues, int defVal) {
        this.mConfigManager = MenuConfigManager.getInstance(this.mContext);
        this.mTv = TVContent.getInstance(this.mContext);
        this.mItemID = id;
        this.mOptionValues = optionValues;
        this.initValue = defVal;
        setText(this.mOptionValues[this.initValue]);
    }

    public void updateValue(int initVal) {
        this.initValue = initVal;
        setText(this.mOptionValues[this.initValue]);
    }

    public void onKeyLeft() {
        if (this.mItemID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
            this.mOptionValues[this.initValue] = this.mTv.getRFChannel(1);
            setText(this.mOptionValues[this.initValue]);
            return;
        }
        this.initValue--;
        if (this.initValue < 0) {
            this.initValue = this.mOptionValues.length - 1;
        }
        setText(this.mOptionValues[this.initValue]);
    }

    public void onKeyRight() {
        if (this.mItemID.equals(MenuConfigManager.TV_SINGLE_RF_SCAN_CHANNELS)) {
            this.mOptionValues[this.initValue] = this.mTv.getRFChannel(2);
            setText(this.mOptionValues[this.initValue]);
            return;
        }
        this.initValue++;
        if (this.initValue > this.mOptionValues.length - 1) {
            this.initValue = 0;
        }
        setText(this.mOptionValues[this.initValue]);
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    public void setEnable(boolean isEnable2) {
        this.isEnable = isEnable2;
    }
}
