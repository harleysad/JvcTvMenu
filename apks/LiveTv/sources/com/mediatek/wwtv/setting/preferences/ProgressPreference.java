package com.mediatek.wwtv.setting.preferences;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.mediatek.wwtv.setting.util.Util;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;

public class ProgressPreference extends Preference {
    static final String TAG = "ProgressPreference";
    private boolean isClickable;
    /* access modifiers changed from: private */
    public boolean isPositionView;
    /* access modifiers changed from: private */
    public boolean isSeekbar;
    /* access modifiers changed from: private */
    public boolean isTTSEnable;
    private View.AccessibilityDelegate mAccDelegate;
    /* access modifiers changed from: private */
    public int mCurrentValue;
    /* access modifiers changed from: private */
    public int mMaxValue;
    /* access modifiers changed from: private */
    public int mMinValue;
    /* access modifiers changed from: private */
    public ProgressBar mProgressView;
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    /* access modifiers changed from: private */
    public SeekBar mSeekBarView;
    private int mStep;
    View.OnKeyListener mTextKeyListener;
    /* access modifiers changed from: private */
    public TextView mValueView;
    /* access modifiers changed from: private */
    public final TextToSpeechUtil ttUtil;

    public ProgressPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isPositionView = false;
        this.isSeekbar = true;
        this.isTTSEnable = false;
        this.isClickable = true;
        this.mCurrentValue = 50;
        this.mMinValue = 0;
        this.mMaxValue = 100;
        this.mStep = 1;
        this.mTextKeyListener = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                MtkLog.d(ProgressPreference.TAG, "onKey." + keyCode);
                if (event.getAction() == 0) {
                    int step = 0;
                    StringBuilder sb = new StringBuilder();
                    sb.append("isSeekbar.");
                    sb.append(!ProgressPreference.this.isSeekbar);
                    MtkLog.d(ProgressPreference.TAG, sb.toString());
                    if (ProgressPreference.this.isTTSEnable) {
                        if (keyCode == 183) {
                            step = -1;
                        } else if (keyCode == 184) {
                            step = 1;
                        }
                    } else if (keyCode == 21) {
                        step = -1;
                    } else if (keyCode != 22) {
                        return false;
                    } else {
                        step = 1;
                    }
                    int step2 = ProgressPreference.this.mCurrentValue + (ProgressPreference.this.getmStep() * step);
                    MtkLog.d(ProgressPreference.TAG, "step = " + step2 + "  mCurrentValue = " + ProgressPreference.this.mCurrentValue);
                    if (step2 > ProgressPreference.this.mMaxValue || step2 < ProgressPreference.this.mMinValue) {
                        MtkLog.d(ProgressPreference.TAG, "invalid new value." + step2);
                        return false;
                    }
                    ProgressPreference.this.setValue(step2);
                    if (ProgressPreference.this.isTTSEnable) {
                        TextToSpeechUtil access$500 = ProgressPreference.this.ttUtil;
                        access$500.speak(step2 + "");
                    }
                    if (ProgressPreference.this.isPositionView) {
                        ProgressPreference.this.mSeekBarView.setProgress(ProgressPreference.this.getCurrentValue() - ProgressPreference.this.getMinValue());
                    } else {
                        ProgressPreference.this.mProgressView.setProgress(ProgressPreference.this.getCurrentValue() - ProgressPreference.this.getMinValue());
                    }
                    ProgressPreference.this.mValueView.setText(String.valueOf(ProgressPreference.this.getCurrentValue()));
                    ProgressPreference.this.callChangeListener(String.valueOf(ProgressPreference.this.getCurrentValue()));
                }
                return false;
            }
        };
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MtkLog.d(ProgressPreference.TAG, "onProgressChanged. " + progress + ", " + fromUser);
                ProgressPreference.this.setCurrentValue(progress);
                ProgressPreference.this.bindData();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        this.mAccDelegate = new View.AccessibilityDelegate() {
            public void sendAccessibilityEvent(View host, int eventType) {
                MtkLog.d(ProgressPreference.TAG, "sendAccessibilityEvent.1 " + eventType + ",isSeekbar " + ProgressPreference.this.isSeekbar + " , " + host);
                if (host.getId() == R.id.preference_seekbar_view) {
                    boolean unused = ProgressPreference.this.isSeekbar = true;
                } else {
                    boolean unused2 = ProgressPreference.this.isSeekbar = false;
                }
                MtkLog.d(ProgressPreference.TAG, "sendAccessibilityEvent. 2 " + eventType + ",isSeekbar " + ProgressPreference.this.isSeekbar + " , " + host);
            }
        };
        this.isTTSEnable = Util.isTTSEnabled(context);
        this.ttUtil = new TextToSpeechUtil(context);
        setLayoutResource(R.layout.progress_preference);
        setPositionView(false);
    }

    public ProgressPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }

    public ProgressPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setCurrentValue(int value) {
        this.mCurrentValue = value;
    }

    public int getCurrentValue() {
        return this.mCurrentValue;
    }

    public void setMinValue(int value) {
        this.mMinValue = value;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMaxValue(int value) {
        this.mMaxValue = value;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setPositionView(boolean value) {
        this.isPositionView = value;
    }

    public boolean isPositionView() {
        return this.isPositionView;
    }

    public int getmStep() {
        return this.mStep;
    }

    public void setmStep(int mStep2) {
        this.mStep = mStep2;
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        MtkLog.d(TAG, "onBindViewHolder.");
        this.mProgressView = (ProgressBar) holder.findViewById(R.id.preference_progress_view);
        this.mSeekBarView = (SeekBar) holder.findViewById(R.id.preference_seekbar_view);
        this.mValueView = (TextView) holder.findViewById(R.id.preference_progress_value);
        this.mSeekBarView.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        this.mSeekBarView.setAccessibilityDelegate(this.mAccDelegate);
        this.mProgressView.setFocusableInTouchMode(false);
        this.mProgressView.setAccessibilityDelegate(this.mAccDelegate);
        if (this.isClickable) {
            holder.itemView.setOnKeyListener(this.mTextKeyListener);
        } else {
            holder.itemView.setOnKeyListener((View.OnKeyListener) null);
        }
        MtkLog.d(TAG, "onBindViewHolder " + this.isPositionView + " mCurrentValue: " + this.mCurrentValue);
        if (this.isPositionView) {
            this.mSeekBarView.setVisibility(0);
            this.mProgressView.setVisibility(8);
        } else {
            this.mSeekBarView.setVisibility(8);
            this.mProgressView.setVisibility(0);
        }
        bindData();
    }

    /* access modifiers changed from: private */
    public void bindData() {
        MtkLog.d(TAG, "bindData.");
        if (this.mValueView == null || this.mProgressView == null) {
            MtkLog.d(TAG, "retrun..........");
            return;
        }
        this.mValueView.setText(String.valueOf(getCurrentValue()));
        if (this.isPositionView) {
            this.mSeekBarView.setMax(getMaxValue() - getMinValue());
            this.mSeekBarView.setProgress(getCurrentValue() - getMinValue());
            return;
        }
        this.mProgressView.setMax(getMaxValue() - getMinValue());
        this.mProgressView.setProgress(getCurrentValue() - getMinValue());
    }

    public void setValue(int value) {
        setCurrentValue(value);
        bindData();
    }
}
