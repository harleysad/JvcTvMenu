package com.android.tv.dialog.picker;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v17.leanback.widget.picker.Picker;
import android.support.v17.leanback.widget.picker.PickerColumn;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public final class PinPicker extends Picker {
    private static final String TAG = "PinPicker";
    private boolean mDisableFirstCenterKey;
    private boolean mIsFirstEnter;
    private OnKeyDownCallback mKeyDownCallback;
    private View.OnClickListener mOnClickListener;
    private final List<PickerColumn> mPickers;

    public interface OnKeyDownCallback {
        void onKeyDown(int i);
    }

    public PinPicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public void setDisableFirstCenterKey(boolean disableFirstCenterKey) {
        this.mDisableFirstCenterKey = disableFirstCenterKey;
    }

    public void setFirstEnter(boolean isFirstEnter) {
        this.mIsFirstEnter = isFirstEnter;
    }

    public void setOnKeyDownCallback(OnKeyDownCallback keyDownCallback) {
        this.mKeyDownCallback = keyDownCallback;
    }

    public PinPicker(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.mPickers = new ArrayList();
        this.mDisableFirstCenterKey = false;
        this.mIsFirstEnter = true;
        this.mIsFirstEnter = true;
        for (int i = 0; i < 4; i++) {
            PickerColumn pickerColumn = new PickerColumn();
            pickerColumn.setMinValue(0);
            pickerColumn.setMaxValue(9);
            pickerColumn.setLabelFormat("%d");
            this.mPickers.add(pickerColumn);
        }
        setSeparator(" ");
        setColumns(this.mPickers);
        setActivated(true);
        setFocusable(true);
        super.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PinPicker.this.onClick(view);
            }
        });
        for (int j = 0; j < getChildCount(); j++) {
            getChildAt(j).setLayoutDirection(0);
        }
    }

    public String getPinInput() {
        String result = "";
        try {
            for (PickerColumn column : this.mPickers) {
                result = result + column.getCurrentValue();
            }
            return result;
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public void setOnClickListener(@Nullable View.OnClickListener l) {
        this.mOnClickListener = l;
    }

    /* access modifiers changed from: private */
    public void onClick(View v) {
        int selectedColumn = getSelectedColumn();
        if (selectedColumn > 0) {
            this.mIsFirstEnter = false;
        }
        if (!this.mDisableFirstCenterKey || !this.mIsFirstEnter) {
            int nextColumn = selectedColumn + 1;
            if (nextColumn != getColumnsCount()) {
                setSelectedColumn(nextColumn);
                onRequestFocusInDescendants(2, (Rect) null);
            } else if (this.mOnClickListener != null) {
                this.mOnClickListener.onClick(v);
            }
        } else {
            this.mIsFirstEnter = false;
        }
    }

    public void resetPinInput() {
        setActivated(false);
        for (int i = 0; i < 4; i++) {
            setColumnValue(i, 0, true);
        }
        setActivated(true);
        setSelectedColumn(0);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() != 1) {
            if (event.getAction() == 0) {
                switch (keyCode) {
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        if (this.mKeyDownCallback != null) {
                            this.mKeyDownCallback.onKeyDown(keyCode);
                            break;
                        }
                        break;
                }
            }
        } else {
            int digit = digitFromKeyCode(keyCode);
            if (digit != -1) {
                int selectedColumn = getSelectedColumn();
                setColumnValue(selectedColumn, digit, false);
                int nextColumn = selectedColumn + 1;
                int columnsCount = getColumnsCount();
                MtkLog.d(TAG, "nextColumn=" + nextColumn + ",selectedColumn=" + selectedColumn + ",columnsCount=" + columnsCount);
                if (nextColumn < columnsCount) {
                    setSelectedColumn(nextColumn);
                    onRequestFocusInDescendants(2, (Rect) null);
                } else {
                    MtkLog.d(TAG, "callOnClick is called!");
                    callOnClick();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @VisibleForTesting
    static int digitFromKeyCode(int keyCode) {
        if (keyCode >= 7 && keyCode <= 16) {
            return keyCode - 7;
        }
        if (keyCode < 144 || keyCode > 153) {
            return -1;
        }
        return keyCode - 144;
    }
}
