package com.mediatek.wwtv.tvcenter.epg.cn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.List;

public class EPGLinearLayout extends LinearLayout {
    private static final String TAG = "EPGLinearLayout";
    public static int mCurrentSelectPosition = 0;
    private List<EPGProgramInfo> childViewData;
    private Context mContext;
    private int mMax;
    private int mWidth;

    public EPGLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public EPGLinearLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setWidth(int mWidth2) {
        this.mWidth = mWidth2;
    }

    public int getmCurrentSelectPosition() {
        MtkLog.d("EPGEuActivity", "mCurrentSelectPosition>>>>" + mCurrentSelectPosition);
        return mCurrentSelectPosition;
    }

    public void setmCurrentSelectPosition(int mCurrentSelectPosition2) {
        mCurrentSelectPosition = mCurrentSelectPosition2;
    }

    public void setAdpter(List<EPGProgramInfo> mChildViewData, boolean flag) {
        List<EPGProgramInfo> list = mChildViewData;
        this.childViewData = list;
        int width = this.mWidth;
        removeAllViews();
        for (int i = 0; i < mChildViewData.size(); i++) {
            EPGTextView textView = (EPGTextView) inflate(this.mContext, R.layout.epg_cn_listitem_textview, (ViewGroup) null);
            textView.setProgramInfo(list.get(i));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (((float) width) * list.get(i).getmScale()), -1);
            layoutParams.leftMargin = (int) (list.get(i).getLeftMargin() * ((float) width));
            MtkLog.d(TAG, "setAdpter-----layoutParams.leftMargin---->" + layoutParams.leftMargin);
            textView.setText(textView.getShowTitle());
            int mainType = textView.getMainTypeStr();
            int subType = textView.getSubTypeStr();
            String mainStr = null;
            String subStr = null;
            if (1 <= mainType) {
                if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                    mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType - 1];
                    if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                        subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                    }
                }
                if (mainStr == null) {
                    this.childViewData.get(i).setBgHighLigth(false);
                } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType)) {
                    this.childViewData.get(i).setBgHighLigth(true);
                } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) || !thisSubSelected(mainType, subType)) {
                    this.childViewData.get(i).setBgHighLigth(false);
                } else {
                    this.childViewData.get(i).setBgHighLigth(true);
                }
            } else {
                this.childViewData.get(i).setBgHighLigth(false);
            }
            MtkLog.d(TAG, "setAdpter-----title---->" + textView.getShowTitle() + "===>" + mCurrentSelectPosition + "    " + mainType + "   " + subType + "\t " + mainStr + "\t" + subStr);
            if (i != mCurrentSelectPosition) {
                boolean z = flag;
            } else if (flag) {
                textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), true, false);
                addView(textView, i, layoutParams);
            }
            textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), false, this.childViewData.get(i).isBgHighLight());
            addView(textView, i, layoutParams);
        }
        boolean z2 = flag;
    }

    private boolean subHasSelected(int mainTypeIndex) {
        String[] subType = DataReader.getInstance(this.mContext).getSubType()[mainTypeIndex];
        if (subType != null) {
            for (String readBooleanValue : subType) {
                if (SaveValue.getInstance(this.mContext).readBooleanValue(readBooleanValue, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean thisSubSelected(int mainTypeIndex, int subTypeIndex) {
        String[] subType = DataReader.getInstance(this.mContext).getSubType()[mainTypeIndex];
        if (subType == null || subTypeIndex < 0 || subTypeIndex >= subType.length) {
            return false;
        }
        return SaveValue.getInstance(this.mContext).readBooleanValue(subType[subTypeIndex], false);
    }

    public void setSelectedPosition(int index) {
        View childView;
        if (!(mCurrentSelectPosition == -1 || (childView = getChildAt(mCurrentSelectPosition)) == null || mCurrentSelectPosition >= this.childViewData.size())) {
            EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
            ((EPGTextView) childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
        }
        View childView2 = getChildAt(index);
        if (childView2 != null) {
            EPGProgramInfo childProgramInfo2 = this.childViewData.get(index);
            ((EPGTextView) childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
            mCurrentSelectPosition = index;
        }
    }

    public void clearSelected() {
        if (mCurrentSelectPosition != -1) {
            View childView = getChildAt(mCurrentSelectPosition);
            if (childView != null && mCurrentSelectPosition < this.childViewData.size()) {
                EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
                ((EPGTextView) childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
            }
            mCurrentSelectPosition = -1;
        }
    }

    public boolean onKeyLeft() {
        if (this.childViewData == null) {
            return false;
        }
        if (mCurrentSelectPosition == -1) {
            return true;
        }
        View childView = getChildAt(mCurrentSelectPosition);
        if (childView == null || mCurrentSelectPosition >= this.childViewData.size()) {
            return false;
        }
        EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
        ((EPGTextView) childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
        if (mCurrentSelectPosition <= 0) {
            return false;
        }
        mCurrentSelectPosition--;
        View childView2 = getChildAt(mCurrentSelectPosition);
        if (childView2 == null || mCurrentSelectPosition >= this.childViewData.size()) {
            return false;
        }
        EPGProgramInfo childProgramInfo2 = this.childViewData.get(mCurrentSelectPosition);
        ((EPGTextView) childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
        return true;
    }

    public boolean onKeyRight() {
        if (this.childViewData == null) {
            return false;
        }
        int count = getChildCount();
        if (mCurrentSelectPosition == -1) {
            return true;
        }
        View childView = getChildAt(mCurrentSelectPosition);
        if (childView == null || mCurrentSelectPosition >= this.childViewData.size()) {
            return false;
        }
        EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
        ((EPGTextView) childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
        if (mCurrentSelectPosition >= count - 1 || mCurrentSelectPosition >= this.childViewData.size() - 1) {
            return false;
        }
        MtkLog.d(TAG, "=======onKeyRight=============count=========" + count);
        mCurrentSelectPosition = mCurrentSelectPosition + 1;
        View childView2 = getChildAt(mCurrentSelectPosition);
        if (childView2 == null) {
            return false;
        }
        EPGProgramInfo childProgramInfo2 = this.childViewData.get(mCurrentSelectPosition);
        ((EPGTextView) childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
        return true;
    }

    public void refreshTextLayout(int currentChannelIndex) {
        if (this.childViewData != null) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                EPGTextView textView = (EPGTextView) getChildAt(i);
                if (textView != null) {
                    int mainType = textView.getMainTypeStr();
                    int subType = textView.getSubTypeStr();
                    String mainStr = null;
                    if (1 <= mainType) {
                        if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                            mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType - 1];
                            if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                                String subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                            }
                        }
                        if (mainStr == null) {
                            this.childViewData.get(i).setBgHighLigth(false);
                        } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType)) {
                            this.childViewData.get(i).setBgHighLigth(true);
                        } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) || !thisSubSelected(mainType, subType)) {
                            this.childViewData.get(i).setBgHighLigth(false);
                        } else {
                            this.childViewData.get(i).setBgHighLigth(true);
                        }
                    } else {
                        this.childViewData.get(i).setBgHighLigth(false);
                    }
                    if (i == mCurrentSelectPosition && EPGConfig.SELECTED_CHANNEL_POSITION == currentChannelIndex) {
                        textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), true, false);
                    } else {
                        textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), false, this.childViewData.get(i).isBgHighLight());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public int getMax() {
        return this.mMax;
    }

    public void setMax(int mMax2) {
        this.mMax = mMax2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void refreshEventsLayout(EPGChannelInfo mChannel, List<EPGProgramInfo> mChildViewData, int channelIndex) {
        boolean z;
        List<EPGProgramInfo> list = mChildViewData;
        int i = channelIndex;
        this.childViewData = list;
        int width = this.mWidth;
        removeAllViews();
        if (list == null || mChildViewData.size() <= 0) {
            setBackgroundResource(R.drawable.epg_analog_channel_bg);
            return;
        }
        ViewGroup viewGroup = null;
        setBackground((Drawable) null);
        boolean z2 = false;
        int i2 = 0;
        while (i2 < mChildViewData.size()) {
            EPGTextView textView = (EPGTextView) inflate(this.mContext, R.layout.epg_cn_listitem_textview, viewGroup);
            textView.setProgramInfo(list.get(i2));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (((float) width) * list.get(i2).getmScale()), -1);
            layoutParams.leftMargin = (int) (list.get(i2).getLeftMargin() * ((float) width));
            MtkLog.d(TAG, "setAdpter-----layoutParams.leftMargin---->" + layoutParams.leftMargin);
            textView.setText(textView.getShowTitle());
            int mainType = textView.getMainTypeStr();
            int subType = textView.getSubTypeStr();
            String mainStr = null;
            String subStr = null;
            if (1 <= mainType) {
                if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                    mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType - 1];
                    if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                        subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                    }
                }
                if (mainStr == null) {
                    list.get(i2).setBgHighLigth(z2);
                } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, z2) && !subHasSelected(mainType)) {
                    list.get(i2).setBgHighLigth(true);
                } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, z2) || !thisSubSelected(mainType, subType)) {
                    list.get(i2).setBgHighLigth(z2);
                } else {
                    list.get(i2).setBgHighLigth(true);
                }
            } else {
                list.get(i2).setBgHighLigth(z2);
            }
            if (i == EPGConfig.SELECTED_CHANNEL_POSITION) {
                if (EPGConfig.init) {
                    mCurrentSelectPosition = mChannel.getPlayingTVProgramPositon();
                } else if (EPGConfig.FROM_WHERE == 21) {
                    mCurrentSelectPosition = mChildViewData.size() - 1;
                } else if (EPGConfig.FROM_WHERE == 22) {
                    mCurrentSelectPosition = z2;
                } else if (EPGConfig.FROM_WHERE == 24 || EPGConfig.FROM_WHERE == 23) {
                    mCurrentSelectPosition = z2 ? 1 : 0;
                } else if (EPGConfig.FROM_WHERE == 26) {
                    mChannel.getPlayingTVProgramPositon();
                } else if (EPGConfig.FROM_WHERE == 27) {
                    if (mCurrentSelectPosition < 0) {
                        mCurrentSelectPosition = z2;
                    } else if (mCurrentSelectPosition >= mChildViewData.size()) {
                        mCurrentSelectPosition = mChildViewData.size() - 1;
                    }
                }
            }
            MtkLog.d(TAG, "setAdpter-----title---->" + textView.getShowTitle() + "===>" + mCurrentSelectPosition + "    " + mainType + "   " + subType + "\t " + mainStr + "\t" + subStr);
            if (i2 == mCurrentSelectPosition && EPGConfig.SELECTED_CHANNEL_POSITION == i) {
                z = false;
                textView.setBackground(this.childViewData.get(i2).isDrawLeftIcon(), this.childViewData.get(i2).isDrawRightwardIcon(), true, false);
            } else {
                z = false;
                textView.setBackground(this.childViewData.get(i2).isDrawLeftIcon(), this.childViewData.get(i2).isDrawRightwardIcon(), false, this.childViewData.get(i2).isBgHighLight());
            }
            addView(textView, i2, layoutParams);
            i2++;
            z2 = z;
            viewGroup = null;
        }
    }
}
