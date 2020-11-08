package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.setting.base.scan.adapter.SetConfigListViewAdapter;
import com.mediatek.wwtv.setting.base.scan.model.OnValueChangedListener;
import com.mediatek.wwtv.setting.base.scan.model.RespondedKeyEvent;
import com.mediatek.wwtv.setting.base.scan.model.UpdateTime;
import com.mediatek.wwtv.tvcenter.R;

public class DateTimeInputView extends ListViewItemView implements RespondedKeyEvent {
    public static final String AUTO_SYNC = "SETUP_auto_syn";
    public static final int INVALID_VALUE = 10004;
    public static final String SCHEDULE_PVR_CHANNELLIST = "SCHEDULE_PVR_CHANNELLIST";
    public static final String SCHEDULE_PVR_REMINDER_TYPE = "SCHEDULE_PVR_REMINDER_TYPE";
    public static final String SCHEDULE_PVR_REPEAT_TYPE = "SCHEDULE_PVR_REPEAT_TYPE";
    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
    public static final int STEP_VALUE = 1;
    public static final String TIME_DATE = "SETUP_date";
    public static final String TIME_TIME = "SETUP_time";
    public final int DATETYPE = 0;
    public final int TIMETYPE = 1;
    /* access modifiers changed from: private */
    public DateTimeView mDateTimeView;
    public Handler mHandler;
    public int mHour;
    public int mMinutes;
    private TextView mTextViewName;
    private OnValueChangedListener mValueChangedListener;
    public int second = 0;
    public UpdateTime updateProcess;

    public OnValueChangedListener getValueChangedListener() {
        return this.mValueChangedListener;
    }

    public void setValueChangedListener(OnValueChangedListener mValueChangedListener2) {
        this.mValueChangedListener = mValueChangedListener2;
    }

    public DateTimeInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public DateTimeInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DateTimeInputView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setAdapter(SetConfigListViewAdapter.DataItem mDataItem) {
        this.mDataItem = mDataItem;
        this.mTextViewName.setText(mDataItem.getmName());
        if (!mDataItem.isAutoUpdate()) {
            this.mDateTimeView.setDateStr(mDataItem.getmDateTimeStr(), new UpdateTime());
            this.mDateTimeView.postInvalidate();
        } else if ("SETUP_time".equals(mDataItem.getmItemID())) {
            final UpdateTime updateProcess2 = new UpdateTime("SETUP_time");
            updateProcess2.startprocess(new UpdateTime.UpdateListener() {
                public void update(String mString) {
                    DateTimeInputView.this.mDateTimeView.setDateStr(mString, updateProcess2);
                    DateTimeInputView.this.mDateTimeView.postInvalidate();
                }
            }, this.context);
        } else if ("SETUP_date".equals(mDataItem.getmItemID())) {
            final UpdateTime updateProcess3 = new UpdateTime("SETUP_date");
            updateProcess3.startprocess(new UpdateTime.UpdateListener() {
                public void update(String mString) {
                    DateTimeInputView.this.mDateTimeView.setDateStr(mString, updateProcess3);
                    DateTimeInputView.this.mDateTimeView.postInvalidate();
                }
            }, this.context);
        }
        this.mDateTimeView.mType = mDataItem.getmDateTimeType();
    }

    private void init() {
        addView((LinearLayout) inflate(this.context, R.layout.menu_datetime_input_view, (ViewGroup) null), new LinearLayout.LayoutParams(-1, -1));
        this.mTextViewName = (TextView) findViewById(R.id.common_itemname);
        this.mDateTimeView = (DateTimeView) findViewById(R.id.common_datetimeview);
    }

    public void setWhiteColor() {
        this.mDateTimeView.setDrawDone(true);
        this.mDateTimeView.postInvalidate();
    }

    public void setFlag() {
        this.mDateTimeView.flag = true;
        this.mDateTimeView.setDrawDone(false);
        this.mDateTimeView.postInvalidate();
    }

    public int getValue() {
        return 0;
    }

    public void onKeyEnter() {
    }

    public void onKeyLeft() {
        if (this.mDateTimeView != null) {
            this.mDateTimeView.onKeyLeft();
        }
    }

    public void onKeyRight() {
        if (this.mDateTimeView != null) {
            this.mDateTimeView.onKeyRight();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mDateTimeView.postInvalidate();
        super.onDraw(canvas);
    }

    public void setCurrentSelectedPosition(int mCurrentSelectedPosition) {
        this.mDateTimeView.setCurrentSelectedPosition(mCurrentSelectedPosition);
    }

    public void setValue(int value) {
    }

    public DateTimeView getmDateTimeView() {
        return this.mDateTimeView;
    }

    public void setmDateTimeView(DateTimeView mDateTimeView2) {
        this.mDateTimeView = mDateTimeView2;
    }

    public void showValue(int value) {
    }

    public TextView getmTextViewName() {
        return this.mTextViewName;
    }

    public void setmTextViewName(TextView mTextViewName2) {
        this.mTextViewName = mTextViewName2;
    }

    class LooperThread extends Thread {
        LooperThread() {
        }

        public void run() {
            super.run();
            while (true) {
                try {
                    Time t = new Time();
                    t.setToNow();
                    DateTimeInputView.this.mHour = t.hour;
                    DateTimeInputView.this.mMinutes = t.minute;
                    DateTimeInputView.this.second = t.second;
                    Thread.sleep(1000);
                    Message m = DateTimeInputView.this.mHandler.obtainMessage();
                    m.what = 1;
                    DateTimeInputView.this.mHandler.sendMessage(m);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
