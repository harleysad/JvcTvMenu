package android.support.v17.leanback.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PinPicker extends Picker {
    private static final int DEFAULT_COLUMN_COUNT = 4;

    public PinPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.pinPickerStyle);
    }

    public PinPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbPinPicker, defStyleAttr, 0);
        try {
            setSeparator(" ");
            setNumberOfColumns(a.getInt(R.styleable.lbPinPicker_columnCount, 4));
        } finally {
            a.recycle();
        }
    }

    public void setNumberOfColumns(int count) {
        List<PickerColumn> columns = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            PickerColumn column = new PickerColumn();
            column.setMinValue(0);
            column.setMaxValue(9);
            column.setLabelFormat("%d");
            columns.add(column);
        }
        setColumns(columns);
    }

    public boolean performClick() {
        int column = getSelectedColumn();
        if (column == getColumnsCount() - 1) {
            return super.performClick();
        }
        setSelectedColumn(column + 1);
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() != 1 || keyCode < 7 || keyCode > 16) {
            return super.dispatchKeyEvent(event);
        }
        setColumnValue(getSelectedColumn(), keyCode - 7, false);
        performClick();
        return true;
    }

    public String getPin() {
        StringBuilder pin = new StringBuilder();
        int columnsCount = getColumnsCount();
        for (int i = 0; i < columnsCount; i++) {
            pin.append(Integer.toString(getColumnAt(i).getCurrentValue()));
        }
        return pin.toString();
    }

    public void resetPin() {
        int columnsCount = getColumnsCount();
        for (int i = 0; i < columnsCount; i++) {
            setColumnValue(i, 0, false);
        }
        setSelectedColumn(0);
    }
}
