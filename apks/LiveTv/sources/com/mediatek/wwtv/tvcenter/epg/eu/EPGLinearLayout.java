package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
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

    /* JADX WARNING: Removed duplicated region for block: B:53:0x021c  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0258  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAdpter(java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r20, boolean r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r0.childViewData = r1
            int r2 = r0.mWidth
            r19.removeAllViews()
            r3 = 0
            r4 = r3
        L_0x000d:
            int r5 = r20.size()
            if (r4 >= r5) goto L_0x0266
            android.content.Context r5 = r0.mContext
            r6 = 2131492917(0x7f0c0035, float:1.86093E38)
            r7 = 0
            android.view.View r5 = inflate(r5, r6, r7)
            com.mediatek.wwtv.tvcenter.epg.eu.EPGTextView r5 = (com.mediatek.wwtv.tvcenter.epg.eu.EPGTextView) r5
            java.lang.Object r6 = r1.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r6 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r6
            r5.setProgramInfo(r6)
            float r6 = (float) r2
            java.lang.Object r7 = r1.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            float r7 = r7.getmScale()
            float r6 = r6 * r7
            int r6 = (int) r6
            int r7 = r20.size()
            int r7 = r7 + -2
            if (r4 != r7) goto L_0x0043
            int r7 = r2 + -10
            if (r6 <= r7) goto L_0x0043
            int r6 = r6 + -20
        L_0x0043:
            r7 = 1
            if (r6 > 0) goto L_0x0056
            int r8 = r20.size()
            int r8 = r8 - r7
            if (r4 != r8) goto L_0x0056
            r6 = 21
            java.lang.String r8 = "EPGLinearLayout"
            java.lang.String r9 = "childWidth <= 0, fixed"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
        L_0x0056:
            android.widget.LinearLayout$LayoutParams r8 = new android.widget.LinearLayout$LayoutParams
            r9 = -1
            r8.<init>(r6, r9)
            java.lang.Object r10 = r1.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r10 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r10
            float r10 = r10.getLeftMargin()
            float r11 = (float) r2
            float r10 = r10 * r11
            int r10 = (int) r10
            r8.leftMargin = r10
            java.lang.String r10 = "EPGLinearLayout"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "setAdpter-----layoutParams.leftMargin---->"
            r11.append(r12)
            int r12 = r8.leftMargin
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r11)
            java.lang.String r10 = r5.getShowTitle()
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 == 0) goto L_0x009b
            android.content.Context r10 = r0.mContext
            android.content.res.Resources r10 = r10.getResources()
            r11 = 2131692035(0x7f0f0a03, float:1.9013159E38)
            java.lang.String r10 = r10.getString(r11)
            goto L_0x009f
        L_0x009b:
            java.lang.String r10 = r5.getShowTitle()
        L_0x009f:
            r5.setText(r10)
            int r11 = r5.getMainTypeStr()
            int r12 = r5.getSubTypeStr()
            r13 = 0
            r14 = 0
            if (r7 > r11) goto L_0x016c
            if (r11 < 0) goto L_0x00e8
            android.content.Context r15 = r0.mContext
            com.mediatek.wwtv.tvcenter.epg.DataReader r15 = com.mediatek.wwtv.tvcenter.epg.DataReader.getInstance(r15)
            java.lang.String[] r15 = r15.getMainType()
            int r15 = r15.length
            if (r11 >= r15) goto L_0x00e8
            android.content.Context r15 = r0.mContext
            com.mediatek.wwtv.tvcenter.epg.DataReader r15 = com.mediatek.wwtv.tvcenter.epg.DataReader.getInstance(r15)
            java.lang.String[] r15 = r15.getMainType()
            int r16 = r11 + -1
            r13 = r15[r16]
            if (r12 < 0) goto L_0x00e8
            android.content.Context r15 = r0.mContext
            com.mediatek.wwtv.tvcenter.epg.DataReader r15 = com.mediatek.wwtv.tvcenter.epg.DataReader.getInstance(r15)
            java.lang.String[][] r15 = r15.getSubType()
            int r15 = r15.length
            if (r12 >= r15) goto L_0x00e8
            android.content.Context r15 = r0.mContext
            com.mediatek.wwtv.tvcenter.epg.DataReader r15 = com.mediatek.wwtv.tvcenter.epg.DataReader.getInstance(r15)
            java.lang.String[][] r15 = r15.getSubType()
            r15 = r15[r11]
            r14 = r15[r12]
        L_0x00e8:
            java.lang.String r15 = "EPGLinearLayout"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r7 = "mainStr="
            r9.append(r7)
            r9.append(r13)
            java.lang.String r7 = ",subStr="
            r9.append(r7)
            r9.append(r14)
            java.lang.String r7 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r7)
            if (r13 == 0) goto L_0x0160
            android.content.Context r7 = r0.mContext
            com.mediatek.wwtv.tvcenter.util.SaveValue r7 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r7)
            boolean r7 = r7.readBooleanValue(r13, r3)
            if (r7 == 0) goto L_0x012e
            boolean r7 = r0.subHasSelected(r11)
            if (r7 != 0) goto L_0x012e
            java.lang.String r7 = "EPGLinearLayout"
            java.lang.String r9 = "subHasSelected"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r7 = r0.childViewData
            java.lang.Object r7 = r7.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            r9 = 1
            r7.setBgHighLigth(r9)
            goto L_0x0177
        L_0x012e:
            android.content.Context r7 = r0.mContext
            com.mediatek.wwtv.tvcenter.util.SaveValue r7 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r7)
            boolean r7 = r7.readBooleanValue(r13, r3)
            if (r7 == 0) goto L_0x0154
            boolean r7 = r0.thisSubSelected(r11, r12)
            if (r7 == 0) goto L_0x0154
            java.lang.String r7 = "EPGLinearLayout"
            java.lang.String r9 = "thisSubSelected"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r7 = r0.childViewData
            java.lang.Object r7 = r7.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            r9 = 1
            r7.setBgHighLigth(r9)
            goto L_0x0177
        L_0x0154:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r7 = r0.childViewData
            java.lang.Object r7 = r7.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            r7.setBgHighLigth(r3)
            goto L_0x0177
        L_0x0160:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r7 = r0.childViewData
            java.lang.Object r7 = r7.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            r7.setBgHighLigth(r3)
            goto L_0x0177
        L_0x016c:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r7 = r0.childViewData
            java.lang.Object r7 = r7.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r7 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r7
            r7.setBgHighLigth(r3)
        L_0x0177:
            java.lang.String r7 = "EPGLinearLayout"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r15 = "setAdpter-----title---->"
            r9.append(r15)
            java.lang.String r15 = r5.getShowTitle()
            r9.append(r15)
            java.lang.String r15 = "===>"
            r9.append(r15)
            int r15 = mCurrentSelectPosition
            r9.append(r15)
            java.lang.String r15 = "    "
            r9.append(r15)
            r9.append(r11)
            java.lang.String r15 = "   "
            r9.append(r15)
            r9.append(r12)
            java.lang.String r15 = "\t "
            r9.append(r15)
            r9.append(r13)
            java.lang.String r15 = "\t"
            r9.append(r15)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)
            int r7 = mCurrentSelectPosition
            if (r4 != r7) goto L_0x01e3
            r7 = r21
            r9 = 1
            if (r7 != r9) goto L_0x01e5
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r9 = r0.childViewData
            java.lang.Object r9 = r9.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r9 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r9
            boolean r9 = r9.isDrawLeftIcon()
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r15 = r0.childViewData
            java.lang.Object r15 = r15.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r15 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r15
            boolean r15 = r15.isDrawRightwardIcon()
            r17 = r2
            r2 = 1
            r5.setBackground(r9, r15, r2, r3)
            goto L_0x020e
        L_0x01e3:
            r7 = r21
        L_0x01e5:
            r17 = r2
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r2 = r0.childViewData
            java.lang.Object r2 = r2.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r2 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r2
            boolean r2 = r2.isDrawLeftIcon()
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r9 = r0.childViewData
            java.lang.Object r9 = r9.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r9 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r9
            boolean r9 = r9.isDrawRightwardIcon()
            java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> r15 = r0.childViewData
            java.lang.Object r15 = r15.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r15 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r15
            boolean r15 = r15.isBgHighLight()
            r5.setBackground(r2, r9, r3, r15)
        L_0x020e:
            int r2 = r20.size()
            if (r2 <= 0) goto L_0x0258
            int r2 = r20.size()
            r9 = 1
            int r2 = r2 - r9
            if (r4 >= r2) goto L_0x0258
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            android.content.Context r9 = r0.mContext
            r2.<init>(r9)
            android.widget.LinearLayout$LayoutParams r9 = new android.widget.LinearLayout$LayoutParams
            r15 = -2
            r3 = -1
            r9.<init>(r15, r3)
            int r15 = r6 + -1
            r8.width = r15
            r15 = 0
            r2.addView(r5, r15, r8)
            android.widget.LinearLayout$LayoutParams r15 = new android.widget.LinearLayout$LayoutParams
            r1 = 1
            r15.<init>(r1, r3)
            r1 = r15
            android.view.View r3 = new android.view.View
            android.content.Context r15 = r0.mContext
            r3.<init>(r15)
            android.content.Context r15 = r0.mContext
            android.content.res.Resources r15 = r15.getResources()
            r7 = 2131099798(0x7f060096, float:1.781196E38)
            int r7 = r15.getColor(r7)
            r3.setBackgroundColor(r7)
            r7 = 1
            r2.addView(r3, r7, r1)
            r0.addView(r2, r4, r9)
            goto L_0x025d
        L_0x0258:
            r8.width = r6
            r0.addView(r5, r4, r8)
        L_0x025d:
            int r4 = r4 + 1
            r2 = r17
            r1 = r20
            r3 = 0
            goto L_0x000d
        L_0x0266:
            r17 = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout.setAdpter(java.util.List, boolean):void");
    }

    private EPGTextView getEPGTextView(View childView) {
        if (childView instanceof EPGTextView) {
            return (EPGTextView) childView;
        }
        return (EPGTextView) ((LinearLayout) childView).getChildAt(0);
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
            getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
        }
        View childView2 = getChildAt(index);
        if (childView2 != null) {
            EPGProgramInfo childProgramInfo2 = this.childViewData.get(index);
            getEPGTextView(childView2).setBackground(childProgramInfo2.isDrawLeftIcon(), childProgramInfo2.isDrawRightwardIcon(), true, false);
            mCurrentSelectPosition = index;
        }
    }

    public void clearSelected() {
        View childView;
        if (mCurrentSelectPosition != -1 && (childView = getChildAt(mCurrentSelectPosition)) != null && mCurrentSelectPosition < this.childViewData.size()) {
            EPGProgramInfo childProgramInfo = this.childViewData.get(mCurrentSelectPosition);
            getEPGTextView(childView).setBackground(childProgramInfo.isDrawLeftIcon(), childProgramInfo.isDrawRightwardIcon(), false, childProgramInfo.isBgHighLight());
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

    public void refreshTextLayout(int currentChannelIndex) {
        if (this.childViewData != null) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                EPGTextView textView = getEPGTextView(getChildAt(i));
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
}
