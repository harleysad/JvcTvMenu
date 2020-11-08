package com.mediatek.wwtv.setting.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class ProgressBarFrag extends Fragment {
    boolean isPositionView = false;
    private int listPosition;
    private Action mAction;
    private MenuConfigManager mConfigManager;
    private Context mContext;
    private String mId;
    private int mOffset;
    private int mPostion;
    private ProgressBar mProgressView;
    private ResultListener mResultListener;
    private ViewGroup mRootView;
    private SeekBar mSeekBarView;
    private int mStepValue;
    private TextView mValueView;
    int pMax = 0;

    public interface ResultListener {
        void onCommitResult(List<String> list);
    }

    public void setAction(Action action) {
        this.mAction = action;
        if (action.mDataType == Action.DataType.POSITIONVIEW) {
            this.isPositionView = true;
        }
    }

    public String getActionId() {
        return this.mId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.progress_frag, (ViewGroup) null);
        this.mProgressView = (ProgressBar) this.mRootView.findViewById(R.id.progress_view);
        this.mSeekBarView = (SeekBar) this.mRootView.findViewById(R.id.seekbar_view);
        this.mValueView = (TextView) this.mRootView.findViewById(R.id.progress_value);
        if (this.isPositionView) {
            this.mSeekBarView.setVisibility(0);
            this.mProgressView.setVisibility(8);
        }
        bindData();
        return this.mRootView;
    }

    private void bindData() {
        this.mConfigManager = MenuConfigManager.getInstance(getActivity());
        this.mId = this.mAction.mItemID;
        this.mOffset = -this.mAction.getmStartValue();
        if (!this.isPositionView) {
            this.mProgressView.setMax(this.mAction.getmEndValue() - this.mAction.getmStartValue());
        } else {
            this.mSeekBarView.setMax(this.mAction.getmEndValue() - this.mAction.getmStartValue());
        }
        this.mPostion = this.mAction.getmInitValue() + this.mOffset;
        this.mStepValue = this.mAction.getmStepValue();
        setProgressAndValue(this.mPostion, false);
    }

    public void setValue(int mInitValue) {
        this.mPostion = this.mOffset + mInitValue;
        this.mAction.mInitValue = mInitValue;
        showValue(this.mAction.mInitValue);
        this.mConfigManager.setActionValue(this.mAction);
    }

    public void showValue(int value) {
        TextView textView = this.mValueView;
        textView.setText("" + value);
        if (MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_LEVEL.equals(this.mAction.mItemID) || MenuConfigManager.TV_SINGLE_SCAN_SIGNAL_QUALITY.equals(this.mAction.mItemID) || MenuConfigManager.DVBS_SIGNAL_QULITY.equals(this.mAction.mItemID) || MenuConfigManager.DVBS_SIGNAL_LEVEL.equals(this.mAction.mItemID)) {
            if (CommonIntegration.isEURegion()) {
                TextView textView2 = this.mValueView;
                textView2.setText(value + "%");
            } else {
                TextView textView3 = this.mValueView;
                textView3.setText("" + value);
            }
        }
        this.mAction.mInitValue = value;
        this.mPostion = this.mOffset + value;
        if (!this.isPositionView) {
            this.mProgressView.setProgress(this.mPostion);
        } else {
            MtkLog.d("ProgressFlag", "seekbar progress:" + this.mPostion);
            this.mSeekBarView.setProgress(this.mPostion);
        }
        this.mAction.setDescription(value);
        this.mAction.mItemID.startsWith("SETUP");
    }

    private void setProgressAndValue(int postion, boolean fromUser) {
        showValue(this.mAction.mInitValue);
    }

    public void onKeyLeft() {
        if (this.mAction.isSupportModify()) {
            switchValuePrevious();
        }
    }

    public void onKeyRight() {
        if (this.mAction.isSupportModify()) {
            switchValueNext();
        }
    }

    private void switchValuePrevious() {
        resetColorTempUser();
        MtkLog.d("ProgressView", "switchValuePrevious mPostion:" + this.mPostion);
        if (this.mPostion > 0) {
            MtkLog.d("ProgressView", "switchValuePrevious mAction.mInitValue >>" + this.mAction.mInitValue + " >>  " + this.mAction.mItemID + "   " + "g_vga__vga_pos_v");
            if (this.mAction.mItemID.equals("g_vga__vga_pos_v")) {
                setProgressAndValue(setVPositionPrevious(this.mAction.mInitValue), true);
                return;
            }
            this.mPostion -= this.mStepValue;
            this.mAction.mInitValue = this.mPostion - this.mOffset;
            MtkLog.d("ProgressFrag", "mAction.mInitValue==" + this.mAction.mInitValue);
            setProgressAndValue(this.mAction.mInitValue, true);
            this.mConfigManager.setActionValue(this.mAction);
        }
    }

    private void switchValueNext() {
        int max;
        resetColorTempUser();
        if (!this.isPositionView) {
            max = this.mProgressView.getMax();
        } else {
            max = this.mSeekBarView.getMax();
        }
        MtkLog.d("switchValueNext", "getMax():" + max + "----mPostion:" + this.mPostion);
        if (this.mPostion >= max) {
            return;
        }
        if (this.mAction.mItemID.equals("g_vga__vga_pos_v")) {
            setProgressAndValue(setVPositionNext(this.mAction.mInitValue), true);
            return;
        }
        this.mPostion += this.mStepValue;
        this.mAction.mInitValue = this.mPostion - this.mOffset;
        MtkLog.d("ProgressFrag", "mAction.mInitValue==" + this.mAction.mInitValue);
        setProgressAndValue(this.mAction.mInitValue, true);
        this.mConfigManager.setActionValue(this.mAction);
    }

    private void resetColorTempUser() {
        if ((this.mAction.mItemID.equals("g_video__clr_gain_r") || this.mAction.mItemID.equals("g_video__clr_gain_g") || this.mAction.mItemID.equals("g_video__clr_gain_b")) && this.mAction.mParentGroup.size() < 6) {
            this.mConfigManager.setValue("g_video__clr_temp", 0, this.mAction);
        }
    }

    public void removeCallback() {
    }

    private int setVPositionNext(int value) {
        int value2 = value + 1;
        MtkLog.d("ProgressFrag", "setVPositionNext value==" + value2);
        while (true) {
            this.mAction.mInitValue = value2;
            this.mConfigManager.setActionValue(this.mAction);
            int newValue = MenuConfigManager.getInstance(getActivity()).getDefault("g_vga__vga_pos_v");
            MtkLog.d("ProgressFrag", "setVPositionNext after newValue==" + newValue);
            if (newValue < value2) {
                value2++;
                if (value2 >= 100) {
                    break;
                }
            } else {
                value2 = newValue;
                MtkLog.d("Next", "biaoqing Next newValue==" + newValue + ",bigger value==" + value2);
                break;
            }
        }
        MtkLog.d("ProgressFrag", "setVPositionNext after value==" + value2);
        this.mAction.mInitValue = value2;
        return value2;
    }

    private int setVPositionPrevious(int value) {
        int value2 = value - 1;
        MtkLog.d("ProgressFrag", "setVPositionPrevious value==" + value2);
        while (true) {
            this.mAction.mInitValue = value2;
            this.mConfigManager.setActionValue(this.mAction);
            int newValue = MenuConfigManager.getInstance(getActivity()).getDefault("g_vga__vga_pos_v");
            MtkLog.d("ProgressFrag", "setVPositionPrevious getDefault==" + newValue + ",value==" + value2);
            if (newValue > value2) {
                value2--;
                if (value2 <= 0) {
                    break;
                }
            } else {
                value2 = newValue;
                MtkLog.d("Previous", "biaoqing newValue==" + newValue);
                break;
            }
        }
        this.mAction.mInitValue = value2;
        return value2;
    }
}
