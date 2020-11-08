package com.mediatek.wwtv.setting.base.scan.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.mediatek.wwtv.setting.base.scan.model.OnValueChangedListener;
import com.mediatek.wwtv.setting.view.DateTimeInputView;
import com.mediatek.wwtv.setting.view.OptionView;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SetConfigListViewAdapter extends BaseAdapter {
    public static final String DTV_TSHIFT_OPTION = "DTV_TSHIFT_OPTION";
    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
    protected static final String TAG = "SetConfigListViewAdapter";
    private Context mContext;
    private List<DataItem> mGroup;
    private int mSelectPos = -1;
    /* access modifiers changed from: private */
    public SrctypeChangeListener srctypeChangelistener;
    private String[] viewNames = {"OptionView", "PositionView", "ProgressView", "HaveSubPageView", "EffectOptionView", "SwitchOptionView", "ChannelListView", "TextCommView", "ScanChannelsOptionView", "NumView", "NumAdjustView", "IPInputBox", "DateTimeInputView", "ChannelListPoweronView", "PasswordView", "FactoryOptionView", "FactoryProgressView", "TVSourceView"};

    public interface SrctypeChangeListener {
        void srcTypeChange(int i);
    }

    public List<DataItem> getmGroup() {
        return this.mGroup;
    }

    public void setmGroup(List<DataItem> mGroup2) {
        this.mGroup = mGroup2;
    }

    public SetConfigListViewAdapter(Context mContext2) {
        this.mContext = mContext2;
    }

    public int getCount() {
        if (this.mGroup == null) {
            return 0;
        }
        return this.mGroup.size();
    }

    public Object getItem(int position) {
        if (this.mGroup == null) {
            return null;
        }
        return this.mGroup.get(position);
    }

    public long getItemId(int position) {
        if (this.mGroup == null) {
            return 0;
        }
        return (long) position;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return this.mGroup.get(position).isEnable;
    }

    public int getLastEnableItemPosition() {
        int position = getCount() - 1;
        for (int i = getCount() - 1; i >= 0; i--) {
            if (isEnabled(i)) {
                return i;
            }
        }
        return position;
    }

    public int getFirstEnableItemPosition() {
        for (int i = 0; i <= getCount() - 1; i++) {
            if (isEnabled(i)) {
                return i;
            }
        }
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder hodler = new ViewHolder();
        if (this.mGroup != null && this.mGroup.size() > 0) {
            DataItem dataItem = this.mGroup.get(position);
            dataItem.setPosition(position);
            if (dataItem.getDataType() == DataItem.DataType.OPTIONVIEW) {
                if (hodler.mOptionView == null) {
                    hodler.mOptionView = new OptionView(this.mContext);
                }
                if (!isEnabled(position)) {
                    hodler.mOptionView.getNameView().setTextColor(-7829368);
                    hodler.mOptionView.getValueView().setTextColor(-7829368);
                }
                hodler.mOptionView.setAdapter(dataItem);
                hodler.mOptionView.setmId(dataItem.mItemID);
                hodler.mOptionView.setValueChangedListener(new OnValueChangedListener() {
                    public void onValueChanged(View v, int value) {
                        String selId = ((OptionView) v).getmId();
                        if (!selId.equals("DTV_TSHIFT_OPTION") && selId.equals("SCHEDULE_PVR_SRCTYPE")) {
                            SetConfigListViewAdapter.this.srctypeChangelistener.srcTypeChange(value);
                        }
                    }
                });
                if (position != this.mSelectPos || !isEnabled(position)) {
                    hodler.mOptionView.setRightImageSource(false);
                } else {
                    hodler.mOptionView.setRightImageSource(true);
                }
                return hodler.mOptionView;
            } else if (dataItem.getDataType() == DataItem.DataType.DATETIMEVIEW) {
                if (hodler.mDateTimeInputView == null) {
                    hodler.mDateTimeInputView = new DateTimeInputView(this.mContext);
                }
                if (isEnabled(position)) {
                    hodler.mDateTimeInputView.getmDateTimeView().flag = false;
                } else {
                    hodler.mDateTimeInputView.getmTextViewName().setTextColor(-7829368);
                    hodler.mDateTimeInputView.getmDateTimeView().flag = false;
                }
                hodler.mDateTimeInputView.setAdapter(dataItem);
                hodler.mDateTimeInputView.setmId(dataItem.mItemID);
                if (convertView != null) {
                    convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            hodler.mDateTimeInputView.setCurrentSelectedPosition(-1);
                        }
                    });
                }
                return hodler.mDateTimeInputView;
            }
        }
        return convertView;
    }

    private void printMotionEvent(MotionEvent event) {
        MtkLog.v(TAG, "************************************");
        MtkLog.v(TAG, "history size:" + event.getHistorySize());
        MtkLog.v(TAG, "histoty count:" + event.getPointerCount());
        for (int i = 0; i < event.getHistorySize(); i++) {
            MtkLog.v(TAG, "histoty " + i + " time:" + event.getHistoricalEventTime(i));
            for (int j = 0; j < event.getPointerCount(); j++) {
                MtkLog.v(TAG, "history " + i + " and point " + j + " positon:" + event.getHistoricalX(j, i) + ":" + event.getHistoricalY(j, i));
            }
        }
        MtkLog.v(TAG, "current time:" + event.getEventTime());
        for (int j2 = 0; j2 < event.getPointerCount(); j2++) {
            MtkLog.v(TAG, "point " + j2 + " positon:" + event.getX(j2) + ":" + event.getY(j2));
        }
        MtkLog.v(TAG, "which edge:" + event.getEdgeFlags());
        MtkLog.v(TAG, "************************************");
    }

    private class ViewHolder {
        DateTimeInputView mDateTimeInputView;
        OptionView mOptionView;

        private ViewHolder() {
        }
    }

    public static class DataItem {
        private boolean autoUpdate = true;
        public boolean isEnable = true;
        public boolean mBrightBackGroundFlag = false;
        private volatile DataType mDataType = DataType.OPTIONVIEW;
        public String mDateTimeStr;
        public int mDateTimeType;
        public List<DataItem> mEffectGroup;
        public int mEndValue;
        public HashMap<Integer, int[]> mHashMap;
        public int mInitValue;
        public String mItemID;
        public String mName;
        public String[] mOptionValue;
        public DataItem mParent;
        public List<DataItem> mParentGroup;
        public int mPosition;
        public int mStartValue;
        public int mStepValue;
        public List<DataItem> mSubChildGroup;
        public HashMap<Integer, Boolean[]> mSwitchHashMap;
        public int userDefined;

        public enum DataType {
            OPTIONVIEW,
            POSITIONVIEW,
            PROGRESSBAR,
            HAVESUBCHILD,
            INPUTBOX,
            EFFECTOPTIONVIEW,
            SWICHOPTIONVIEW,
            CHANNELLISTVIEW,
            TEXTCOMMVIEW,
            SCANCHANNELSOPTIONVIEW,
            NUMVIEW,
            NUMADJUSTVIEW,
            DATETIMEVIEW,
            CHANNELPOWERONVIEW,
            PASSWORDVIEW,
            FACTORYOPTIONVIEW,
            FACTORYPROGRESSVIEW,
            TVSOURCEVIEW,
            CHANNELPOWERNOCAHNNEL,
            CHANNELEUEDIT
        }

        public boolean ismBrightBackGroundFlag() {
            return this.mBrightBackGroundFlag;
        }

        public void setmBrightBackGroundFlag(boolean mBrightBackGroundFlag2) {
            this.mBrightBackGroundFlag = mBrightBackGroundFlag2;
        }

        public int getPosition() {
            return this.mPosition;
        }

        public void setPosition(int mPosition2) {
            this.mPosition = mPosition2;
        }

        public int getmDateTimeType() {
            return this.mDateTimeType;
        }

        public void setmDateTimeType(int mDateTimeType2) {
            this.mDateTimeType = mDateTimeType2;
        }

        public String getmDateTimeStr() {
            return this.mDateTimeStr;
        }

        public void setmDateTimeStr(String mDateTimeStr2) {
            this.mDateTimeStr = mDateTimeStr2;
        }

        public DataItem(String mItemID2, String mName2, int mStartValue2, int mEndValue2, int mInitValue2, String[] mOptionVaule, int mStepValue2, DataType mDataType2) {
            this.mItemID = mItemID2;
            this.mName = mName2;
            this.mDataType = mDataType2;
            if (mDataType2 == DataType.POSITIONVIEW || mDataType2 == DataType.PROGRESSBAR || mDataType2 == DataType.NUMVIEW || mDataType2 == DataType.FACTORYPROGRESSVIEW || mDataType2 == DataType.NUMADJUSTVIEW) {
                this.mStartValue = mStartValue2;
                this.mEndValue = mEndValue2;
                this.mInitValue = mInitValue2;
                this.mStepValue = mStepValue2;
            }
            if (mDataType2 == DataType.OPTIONVIEW || mDataType2 == DataType.EFFECTOPTIONVIEW || mDataType2 == DataType.SWICHOPTIONVIEW || mDataType2 == DataType.CHANNELLISTVIEW || mDataType2 == DataType.TEXTCOMMVIEW || mDataType2 == DataType.CHANNELPOWERNOCAHNNEL || mDataType2 == DataType.SCANCHANNELSOPTIONVIEW || mDataType2 == DataType.CHANNELPOWERONVIEW || mDataType2 == DataType.FACTORYOPTIONVIEW || mDataType2 == DataType.FACTORYPROGRESSVIEW || mDataType2 == DataType.TVSOURCEVIEW) {
                this.mOptionValue = mOptionVaule;
                this.mInitValue = mInitValue2;
            }
            if (mDataType2 == DataType.INPUTBOX || mDataType2 == DataType.NUMVIEW) {
                this.mOptionValue = mOptionVaule;
            }
        }

        public String toString() {
            return "DataItem [mDataType=" + this.mDataType + ", mEndValue=" + this.mEndValue + ", mInitValue=" + this.mInitValue + ", mStepValue=" + this.mStepValue + ", mItemID=" + this.mItemID + ", mName=" + this.mName + ", mOptionValue=" + Arrays.toString(this.mOptionValue) + ", mStartValue=" + this.mStartValue + ", mSubChildGroup=" + this.mSubChildGroup + "]";
        }

        public int getUserDefined() {
            return this.userDefined;
        }

        public void setUserDefined(int userDefined2) {
            this.userDefined = userDefined2;
        }

        public String getmItemID() {
            return this.mItemID;
        }

        public void setmItemID(String mItemID2) {
            this.mItemID = mItemID2;
        }

        public boolean isEnable() {
            return this.isEnable;
        }

        public void setEnable(boolean isEnable2) {
            this.isEnable = isEnable2;
        }

        public String getmName() {
            return this.mName;
        }

        public void setmName(String mName2) {
            this.mName = mName2;
        }

        public String[] getmOptionValue() {
            return this.mOptionValue;
        }

        public void setmOptionValue(String[] mOptionValue2) {
            this.mOptionValue = mOptionValue2;
        }

        public int getmStartValue() {
            return this.mStartValue;
        }

        public void setmStartValue(int mStartValue2) {
            this.mStartValue = mStartValue2;
        }

        public int getmEndValue() {
            return this.mEndValue;
        }

        public void setmEndValue(int mEndValue2) {
            this.mEndValue = mEndValue2;
        }

        public int getmInitValue() {
            return this.mInitValue;
        }

        public void setmInitValue(int mInitValue2) {
            this.mInitValue = mInitValue2;
        }

        public int getmStepValue() {
            return this.mStepValue;
        }

        public void setmStepValue(int mStepValue2) {
            this.mStepValue = mStepValue2;
        }

        public DataItem getmParent() {
            return this.mParent;
        }

        public void setmParent(DataItem mParent2) {
            this.mParent = mParent2;
        }

        public List<DataItem> getmEffectGroup() {
            return this.mEffectGroup;
        }

        public void setmEffectGroup(List<DataItem> mEffectGroup2) {
            this.mEffectGroup = mEffectGroup2;
        }

        public HashMap<Integer, Boolean[]> getmSwitchHashMap() {
            return this.mSwitchHashMap;
        }

        public void setmSwitchHashMap(HashMap<Integer, Boolean[]> mSwitchHashMap2) {
            this.mSwitchHashMap = mSwitchHashMap2;
        }

        public HashMap<Integer, int[]> getmHashMap() {
            return this.mHashMap;
        }

        public void setmHashMap(HashMap<Integer, int[]> mHashMap2) {
            this.mHashMap = mHashMap2;
        }

        public List<DataItem> getmSubChildGroup() {
            return this.mSubChildGroup;
        }

        public void setmSubChildGroup(List<DataItem> mSubChildGroup2) {
            this.mSubChildGroup = mSubChildGroup2;
        }

        public List<DataItem> getmParentGroup() {
            return this.mParentGroup;
        }

        public void setmParentGroup(List<DataItem> mParentGroup2) {
            this.mParentGroup = mParentGroup2;
        }

        public DataType getDataType() {
            return this.mDataType;
        }

        public void setDataType(DataType mDataType2) {
            this.mDataType = mDataType2;
        }

        public boolean isAutoUpdate() {
            return this.autoUpdate;
        }

        public void setAutoUpdate(boolean autoUpdate2) {
            this.autoUpdate = autoUpdate2;
        }
    }

    public void setSelectPos(int position) {
        this.mSelectPos = position;
    }

    public SrctypeChangeListener getSrctypeChangelistener() {
        return this.srctypeChangelistener;
    }

    public void setSrctypeChangelistener(SrctypeChangeListener srctypeChangelistener2) {
        this.srctypeChangelistener = srctypeChangelistener2;
    }
}
