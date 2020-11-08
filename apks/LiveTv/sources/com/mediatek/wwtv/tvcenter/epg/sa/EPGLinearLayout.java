package com.mediatek.wwtv.tvcenter.epg.sa;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
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
    private int mDayNum;
    private int mMax;
    private int mStartTime;
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
        MtkLog.d("EPGSaActivity", "mCurrentSelectPosition>>>>" + mCurrentSelectPosition);
        return mCurrentSelectPosition;
    }

    public void setmCurrentSelectPosition(int mCurrentSelectPosition2) {
        mCurrentSelectPosition = mCurrentSelectPosition2;
    }

    public void setAdapterByEpgProgramItemView(List<EPGProgramInfo> mChildViewData, boolean flag) {
        int width;
        List<EPGProgramInfo> list = mChildViewData;
        boolean z = flag;
        this.childViewData = list;
        int width2 = this.mWidth;
        removeAllViews();
        boolean z2 = false;
        int i = 0;
        while (i < mChildViewData.size()) {
            EpgProgramItemView eTextView = new EpgProgramItemView(this.mContext, list.get(i));
            int childWidth = (int) (((float) width2) * list.get(i).getmScale());
            MtkLog.e(TAG, "setAdpter-----layoutParams.width---->" + width2 + "==>" + list.get(i).getmScale() + "===>" + (((float) width2) * list.get(i).getmScale()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(childWidth, -1);
            layoutParams.leftMargin = (int) (list.get(i).getLeftMargin() * ((float) width2));
            MtkLog.d(TAG, "setAdpter-----layoutParams.leftMargin---->" + layoutParams.leftMargin + "   " + z + "   " + mCurrentSelectPosition + "  " + this.childViewData.get(i).isDrawLeftIcon());
            String title = eTextView.getShowTitle();
            eTextView.setText((title == null || title.equals("")) ? this.mContext.getString(R.string.nav_epg_no_title) : title);
            int mainType = eTextView.getMainTypeStr();
            int subType = eTextView.getSubTypeStr();
            String mainStr = null;
            String subStr = null;
            if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType];
                if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                    subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                }
            }
            if (mainStr == null) {
                this.childViewData.get(i).setBgHighLigth(z2);
            } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, z2) && !subHasSelected(mainType, subType)) {
                this.childViewData.get(i).setBgHighLigth(true);
            } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, z2) || !thisSubSelected(mainType, subType)) {
                this.childViewData.get(i).setBgHighLigth(z2);
            } else {
                this.childViewData.get(i).setBgHighLigth(true);
            }
            eTextView.setBookVisibility();
            MtkLog.d(TAG, "setAdpter-----title---->" + eTextView.getShowTitle() + "===>" + mCurrentSelectPosition + "    " + mainType + "   " + subType + "    " + mainStr + "    " + subStr);
            if (i == mCurrentSelectPosition && z) {
                eTextView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), true, false);
            } else {
                eTextView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), false, this.childViewData.get(i).isBgHighLight());
            }
            if (this.childViewData.size() > 0) {
                LinearLayout viewGroup = new LinearLayout(this.mContext);
                LinearLayout.LayoutParams viewGroupLp = new LinearLayout.LayoutParams(-2, -1);
                layoutParams.width = childWidth - 1;
                viewGroup.addView(eTextView, 0, layoutParams);
                LinearLayout.LayoutParams diverLp = new LinearLayout.LayoutParams(1, -1);
                View diverView = new View(this.mContext);
                width = width2;
                diverView.setBackgroundColor(this.mContext.getResources().getColor(R.color.epg_divider));
                viewGroup.addView(diverView, 1, diverLp);
                addView(viewGroup, i, viewGroupLp);
            } else {
                width = width2;
                layoutParams.width = childWidth;
                addView(eTextView, i, layoutParams);
            }
            i++;
            width2 = width;
            list = mChildViewData;
            z = flag;
            z2 = false;
        }
    }

    public void setAdpter(List<EPGProgramInfo> mChildViewData, boolean flag) {
        this.childViewData = mChildViewData;
        int width = this.mWidth;
        for (int i = 0; i < mChildViewData.size(); i++) {
            EPGTextView textView = new EPGTextView(this.mContext, mChildViewData.get(i));
            MtkLog.e(TAG, "setAdpter-----layoutParams.width---->" + width + "==>" + mChildViewData.get(i).getmScale() + "===>" + (((float) width) * mChildViewData.get(i).getmScale()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (((float) width) * mChildViewData.get(i).getmScale()), -1);
            layoutParams.leftMargin = (int) (mChildViewData.get(i).getLeftMargin() * ((float) width));
            StringBuilder sb = new StringBuilder();
            sb.append("setAdpter-----layoutParams.leftMargin---->");
            sb.append(layoutParams.leftMargin);
            MtkLog.d(TAG, sb.toString());
            if (i == mCurrentSelectPosition && flag) {
                textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), true);
            } else {
                textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), false);
            }
            textView.setText(textView.getShowTitle());
            int mainType = textView.getMainTypeStr();
            int subType = textView.getSubTypeStr();
            String mainStr = null;
            String subStr = null;
            if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType];
                if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                    subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                }
            }
            if (mainStr != null) {
                if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType, subType)) {
                    textView.setTextColor(SupportMenu.CATEGORY_MASK);
                } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && thisSubSelected(mainType, subType)) {
                    textView.setTextColor(SupportMenu.CATEGORY_MASK);
                }
            }
            MtkLog.d(TAG, "setAdpter-----title---->" + textView.getShowTitle() + "===>" + mCurrentSelectPosition + "    " + mainType + "   " + subType + "    " + mainStr + "    " + subStr);
            addView(textView, i, layoutParams);
        }
    }

    private boolean subHasSelected(int mainTypeIndex, int subTypeIndex) {
        String[] subType = DataReader.getInstance(this.mContext).getSubType()[mainTypeIndex];
        if (subTypeIndex >= 0 && subType != null) {
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

    public void setAdpterByLayout(int startTime, int dayNum) {
        this.childViewData = null;
        removeAllViews();
        this.mStartTime = startTime;
        this.mDayNum = dayNum;
        int i = this.mWidth;
        EpgProgramItemView textView = new EpgProgramItemView(this.mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, -1);
        layoutParams.leftMargin = 20;
        textView.setText(this.mContext.getString(R.string.nav_epg_no_program_data));
        addView(textView, 0, layoutParams);
    }

    public void setAdpter() {
        int i = this.mWidth;
        EPGTextView textView = new EPGTextView(this.mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, -1);
        layoutParams.leftMargin = 20;
        textView.setText(this.mContext.getString(R.string.nav_epg_no_program_data));
        addView(textView, 0, layoutParams);
    }

    public void setSelectedPosition(int index) {
        if (mCurrentSelectPosition != -1) {
            View childView = getChildAt(mCurrentSelectPosition);
            if (this.childViewData != null) {
                if (childView != null && mCurrentSelectPosition < this.childViewData.size()) {
                    EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
                    getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
                }
            } else {
                return;
            }
        }
        View childView2 = getChildAt(index);
        if (childView2 == null) {
            return;
        }
        if (this.childViewData != null) {
            EPGProgramInfo childProgramInfo2 = this.childViewData.get(index);
            getEPGTextView(childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
            mCurrentSelectPosition = index;
            return;
        }
        mCurrentSelectPosition = 0;
    }

    public void clearSelected() {
        if (mCurrentSelectPosition != -1) {
            View childView = getChildAt(mCurrentSelectPosition);
            if (this.childViewData != null) {
                if (childView != null && mCurrentSelectPosition < this.childViewData.size()) {
                    EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
                    getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
                }
                mCurrentSelectPosition = -1;
            }
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
        getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
        if (mCurrentSelectPosition <= 0) {
            return false;
        }
        mCurrentSelectPosition--;
        View childView2 = getChildAt(mCurrentSelectPosition);
        if (childView2 == null || mCurrentSelectPosition >= this.childViewData.size()) {
            return false;
        }
        EPGProgramInfo childProgramInfo2 = this.childViewData.get(mCurrentSelectPosition);
        getEPGTextView(childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
        return true;
    }

    public boolean onKeyRight() {
        int count = getChildCount();
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
        getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
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
        getEPGTextView(childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
        return true;
    }

    private EpgProgramItemView getEPGTextView(View childView) {
        if (childView instanceof EpgProgramItemView) {
            return (EpgProgramItemView) childView;
        }
        return (EpgProgramItemView) ((LinearLayout) childView).getChildAt(0);
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

    public String getProgramTypeByProgram(EPGProgramInfo program) {
        int index;
        if (program == null) {
            MtkLog.d(TAG, "null == program");
            return "";
        }
        String[] mainType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_sa_type);
        if (program.getCategoryType() == null || program.getCategoryType().length < 1) {
            MtkLog.d(TAG, "null == getCategoryType");
            return "";
        }
        int index2 = program.getCategoryType()[0];
        if (index2 != 0 && (index = (index2 & 240) >> 4) >= 0 && index < mainType.length) {
            return mainType[index];
        }
        return "";
    }

    public void refreshTextLayout(int currentChannelIndex) {
        if (this.childViewData != null) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                EpgProgramItemView textView = getEPGTextView(getChildAt(i));
                if (textView != null) {
                    int mainType = textView.getMainTypeStr();
                    int subType = textView.getSubTypeStr();
                    String mainStr = null;
                    if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                        mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType];
                        if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                            String subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                        }
                    }
                    if (mainStr == null) {
                        this.childViewData.get(i).setBgHighLigth(false);
                    } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType, subType)) {
                        this.childViewData.get(i).setBgHighLigth(true);
                    } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) || !thisSubSelected(mainType, subType)) {
                        this.childViewData.get(i).setBgHighLigth(false);
                    } else {
                        this.childViewData.get(i).setBgHighLigth(true);
                    }
                    textView.setBookVisibility();
                    if (i == mCurrentSelectPosition && EPGConfig.SELECTED_CHANNEL_POSITION == currentChannelIndex) {
                        textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), true, false);
                    } else {
                        textView.setBackground(this.childViewData.get(i).isDrawLeftIcon(), this.childViewData.get(i).isDrawRightwardIcon(), false, this.childViewData.get(i).isBgHighLight());
                    }
                }
            }
        }
    }

    public void refreshText() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            EPGTextView textView = (EPGTextView) getChildAt(i);
            int mainType = textView.getMainTypeStr();
            int subType = textView.getSubTypeStr();
            String mainStr = null;
            if (mainType >= 0 && mainType < DataReader.getInstance(this.mContext).getMainType().length) {
                mainStr = DataReader.getInstance(this.mContext).getMainType()[mainType];
                if (subType >= 0 && subType < DataReader.getInstance(this.mContext).getSubType().length) {
                    String subStr = DataReader.getInstance(this.mContext).getSubType()[mainType][subType];
                }
            }
            if (mainStr == null) {
                textView.setTextColor(-1);
            } else if (SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) && !subHasSelected(mainType, subType)) {
                textView.setTextColor(SupportMenu.CATEGORY_MASK);
            } else if (!SaveValue.getInstance(this.mContext).readBooleanValue(mainStr, false) || !thisSubSelected(mainType, subType)) {
                textView.setTextColor(-1);
            } else {
                textView.setTextColor(SupportMenu.CATEGORY_MASK);
            }
        }
    }
}
