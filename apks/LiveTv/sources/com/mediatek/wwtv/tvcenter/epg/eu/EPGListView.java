package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGConfig;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PageImp;
import java.util.List;

public class EPGListView extends ListView {
    private static final String TAG = "EPGListView";
    private boolean isFirst = true;
    public boolean mCanChangeChannel;
    public boolean mCanKeyUp;
    private EPGChannelInfo mCurrentChannel;
    private int mCurrentSelectedPosition = 0;
    private EPGEuActivity mEPGAcivity;
    private int mFirstEnableItemPosition;
    private Handler mHandler;
    private int mLastEnableItemPosition;
    private int mLastRightSelectedPosition = 0;
    private EPGProgramInfo mLastSelectedTVProgram;
    private EPGListViewAdapter mListViewAdpter;
    private LinearLayout mListViewChildView;
    private EPGLinearLayout mNextSelectedItemView;
    private PageImp mPageImp = new PageImp();
    private final DataReader mReader;
    private EPGLinearLayout mSelectedItemView;
    private UpDateListView mUpdate;
    private int pageNum;
    private int pageSize;

    public interface UpDateListView {
        void updata(boolean z);
    }

    public EPGProgramInfo getLastSelectedTVProgram() {
        return this.mLastSelectedTVProgram;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void setHandler(Handler mHandler2) {
        this.mHandler = mHandler2;
    }

    public EPGListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (context instanceof EPGEuActivity) {
            this.mEPGAcivity = (EPGEuActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof EPGEuActivity) {
            this.mEPGAcivity = (EPGEuActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context) {
        super(context);
        if (context instanceof EPGEuActivity) {
            this.mEPGAcivity = (EPGEuActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mFirstEnableItemPosition = 0;
        this.mLastEnableItemPosition = adapter.getCount() - 1;
        this.mListViewAdpter = (EPGListViewAdapter) adapter;
        this.mListViewAdpter.setEPGListView(this);
        if (this.isFirst) {
            this.isFirst = false;
            this.mListViewAdpter.setHeight((int) (((((float) getContext().getResources().getDisplayMetrics().heightPixels) * 0.52f) / 6.0f) + 0.5f));
        }
        super.setAdapter(adapter);
    }

    public void updateEnablePosition(ListAdapter adapter) {
        this.mFirstEnableItemPosition = 0;
        this.mLastEnableItemPosition = adapter.getCount() - 1;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void initData(List<?> list, int perPage, UpDateListView update, int pageIndex) {
        this.mPageImp = new PageImp(list, perPage);
        this.pageSize = perPage;
        this.pageNum = this.mPageImp.getPageNum();
        this.mUpdate = update;
        if (pageIndex > 1) {
            this.mPageImp.gotoPage(pageIndex);
        }
    }

    public void initData(List<?> list, int perPage) {
        this.mPageImp = new PageImp(list, perPage);
    }

    public List<?> getList() {
        return this.mPageImp.getList();
    }

    public List<?> getCurrentList() {
        return this.mPageImp.getCurrentList();
    }

    public int getCurrentPageNum() {
        return this.mPageImp.getCurrentPage();
    }

    public EPGChannelInfo getCurrentChannel() {
        return (EPGChannelInfo) getItemAtPosition(getSelectedItemPosition());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:89:0x02fc, code lost:
        return super.onKeyDown(r8, r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r8, android.view.KeyEvent r9) {
        /*
            r7 = this;
            r0 = 0
            java.util.List r1 = r7.getCurrentList()
            r2 = 0
            if (r1 == 0) goto L_0x0300
            java.util.List r1 = r7.getCurrentList()
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0014
            goto L_0x0300
        L_0x0014:
            r1 = 66
            if (r8 == r1) goto L_0x02ff
            r1 = 93
            if (r8 == r1) goto L_0x02fe
            r1 = 0
            r3 = -1
            r4 = 260(0x104, float:3.64E-43)
            r5 = 27
            r6 = 1
            switch(r8) {
                case 19: goto L_0x01d1;
                case 20: goto L_0x00a3;
                case 21: goto L_0x0073;
                case 22: goto L_0x0043;
                case 23: goto L_0x02ff;
                default: goto L_0x0026;
            }
        L_0x0026:
            switch(r8) {
                case 166: goto L_0x0037;
                case 167: goto L_0x002b;
                default: goto L_0x0029;
            }
        L_0x0029:
            goto L_0x02f8
        L_0x002b:
            android.view.KeyEvent r1 = new android.view.KeyEvent
            r3 = 19
            r1.<init>(r2, r3)
            r7.dispatchKeyEvent(r1)
            goto L_0x02f8
        L_0x0037:
            android.view.KeyEvent r1 = new android.view.KeyEvent
            r3 = 20
            r1.<init>(r2, r3)
            r7.dispatchKeyEvent(r1)
            goto L_0x02f8
        L_0x0043:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isRtl()
            java.lang.String r2 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "isRtl="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            if (r1 == 0) goto L_0x006a
            boolean r2 = r7.onLeftKey()
            if (r2 == 0) goto L_0x0071
            return r6
        L_0x006a:
            boolean r2 = r7.onRightKey()
            if (r2 == 0) goto L_0x0071
            return r6
        L_0x0071:
            goto L_0x02f8
        L_0x0073:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isRtl()
            java.lang.String r2 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "isRtl="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)
            if (r1 == 0) goto L_0x009a
            boolean r2 = r7.onRightKey()
            if (r2 == 0) goto L_0x00a1
            return r6
        L_0x009a:
            boolean r2 = r7.onLeftKey()
            if (r2 == 0) goto L_0x00a1
            return r6
        L_0x00a1:
            goto L_0x02f8
        L_0x00a3:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r2
            r7.mCanChangeChannel = r2
            r7.mCanKeyUp = r6
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r5
            android.os.Handler r5 = r7.mHandler
            r5.removeMessages(r4)
            int r4 = r7.getSelectedItemPosition()
            r7.mCurrentSelectedPosition = r4
            int r4 = r7.mCurrentSelectedPosition
            if (r4 >= 0) goto L_0x00be
            int r4 = r7.mLastRightSelectedPosition
            r7.mCurrentSelectedPosition = r4
        L_0x00be:
            int r4 = r7.mCurrentSelectedPosition
            r7.mLastRightSelectedPosition = r4
            int r4 = r7.mCurrentSelectedPosition
            java.lang.Object r4 = r7.getItemAtPosition(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r4 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r4
            r7.mCurrentChannel = r4
            int r4 = r7.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.getSelectedDynamicLinearLayout(r4)
            r7.mSelectedItemView = r4
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.mSelectedItemView
            if (r4 == 0) goto L_0x01d0
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.mSelectedItemView
            int r4 = r4.getmCurrentSelectPosition()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r5 = r7.mSelectedItemView
            r5.clearSelected()
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r5 = r7.mCurrentChannel
            if (r5 == 0) goto L_0x0107
            if (r4 == r3) goto L_0x0107
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r1 = r7.mCurrentChannel
            java.util.List r1 = r1.getmTVProgramInfoList()
            if (r1 == 0) goto L_0x0106
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x0106
            int r3 = r1.size()
            if (r4 >= r3) goto L_0x0106
            java.lang.Object r3 = r1.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r3
            r7.mLastSelectedTVProgram = r3
        L_0x0106:
            goto L_0x0109
        L_0x0107:
            r7.mLastSelectedTVProgram = r1
        L_0x0109:
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_DOWN mCurrentSelectedPosition>>"
            r3.append(r5)
            int r5 = r7.mCurrentSelectedPosition
            r3.append(r5)
            java.lang.String r5 = "   "
            r3.append(r5)
            int r5 = r7.mLastEnableItemPosition
            r3.append(r5)
            java.lang.String r5 = "  "
            r3.append(r5)
            int r5 = r7.mFirstEnableItemPosition
            r3.append(r5)
            java.lang.String r5 = "  "
            r3.append(r5)
            int r5 = r7.pageNum
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_DOWN>> mPageImp.getCurrentPage()"
            r3.append(r5)
            com.mediatek.wwtv.tvcenter.util.PageImp r5 = r7.mPageImp
            int r5 = r5.getCurrentPage()
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            int r1 = r7.mCurrentSelectedPosition
            int r3 = r7.mLastEnableItemPosition
            if (r1 != r3) goto L_0x01ad
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            int r1 = r1.getCurrentPage()
            int r3 = r7.pageNum
            if (r1 == r3) goto L_0x017c
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            r1.nextPage()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListView$UpDateListView r1 = r7.mUpdate
            r1.updata(r6)
            int r1 = r7.mFirstEnableItemPosition
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x017c:
            int r1 = r7.pageNum
            if (r1 != r6) goto L_0x018e
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r2
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r7.mListViewAdpter
            r7.setAdapter((android.widget.ListAdapter) r1)
            int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x018e:
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            int r1 = r1.getCurrentPage()
            com.mediatek.wwtv.tvcenter.util.PageImp r2 = r7.mPageImp
            int r2 = r2.getPageNum()
            if (r1 != r2) goto L_0x01ce
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            r1.headPage()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListView$UpDateListView r1 = r7.mUpdate
            r1.updata(r6)
            int r1 = r7.mFirstEnableItemPosition
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x01ad:
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "key dowm>getSelectedItemPosition>"
            r2.append(r3)
            int r3 = r7.getSelectedItemPosition()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            int r1 = r7.getSelectedItemPosition()
            int r1 = r1 + r6
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r1
        L_0x01ce:
            goto L_0x02f8
        L_0x01d0:
            return r6
        L_0x01d1:
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r2
            r7.mCanChangeChannel = r2
            r7.mCanKeyUp = r6
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r5
            android.os.Handler r5 = r7.mHandler
            r5.removeMessages(r4)
            int r4 = r7.getSelectedItemPosition()
            r7.mCurrentSelectedPosition = r4
            int r4 = r7.mCurrentSelectedPosition
            if (r4 >= 0) goto L_0x01ec
            int r4 = r7.mLastRightSelectedPosition
            r7.mCurrentSelectedPosition = r4
        L_0x01ec:
            int r4 = r7.mCurrentSelectedPosition
            r7.mLastRightSelectedPosition = r4
            int r4 = r7.mCurrentSelectedPosition
            java.lang.Object r4 = r7.getItemAtPosition(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r4 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r4
            r7.mCurrentChannel = r4
            int r4 = r7.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.getSelectedDynamicLinearLayout(r4)
            r7.mSelectedItemView = r4
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.mSelectedItemView
            if (r4 == 0) goto L_0x02fd
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r4 = r7.mSelectedItemView
            int r4 = r4.getmCurrentSelectPosition()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGLinearLayout r5 = r7.mSelectedItemView
            r5.clearSelected()
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r5 = r7.mCurrentChannel
            if (r5 == 0) goto L_0x0235
            if (r4 == r3) goto L_0x0235
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r1 = r7.mCurrentChannel
            java.util.List r1 = r1.getmTVProgramInfoList()
            if (r1 == 0) goto L_0x0234
            int r3 = r1.size()
            if (r3 <= 0) goto L_0x0234
            int r3 = r1.size()
            if (r4 >= r3) goto L_0x0234
            java.lang.Object r3 = r1.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r3 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r3
            r7.mLastSelectedTVProgram = r3
        L_0x0234:
            goto L_0x0237
        L_0x0235:
            r7.mLastSelectedTVProgram = r1
        L_0x0237:
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_UP mCurrentSelectedPosition>>"
            r3.append(r5)
            int r5 = r7.mCurrentSelectedPosition
            r3.append(r5)
            java.lang.String r5 = "   "
            r3.append(r5)
            int r5 = r7.mLastEnableItemPosition
            r3.append(r5)
            java.lang.String r5 = "  "
            r3.append(r5)
            int r5 = r7.mFirstEnableItemPosition
            r3.append(r5)
            java.lang.String r5 = "  "
            r3.append(r5)
            int r5 = r7.pageNum
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_DOWN>> mPageImp.getCurrentPage()"
            r3.append(r5)
            com.mediatek.wwtv.tvcenter.util.PageImp r5 = r7.mPageImp
            int r5 = r5.getCurrentPage()
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            int r1 = r7.mCurrentSelectedPosition
            int r3 = r7.mFirstEnableItemPosition
            if (r1 != r3) goto L_0x02d6
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            int r1 = r1.getCurrentPage()
            if (r1 == r6) goto L_0x02a7
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            r1.prePage()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListView$UpDateListView r1 = r7.mUpdate
            r1.updata(r2)
            int r1 = r7.mLastEnableItemPosition
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x02a7:
            int r1 = r7.pageNum
            if (r1 != r6) goto L_0x02ba
            int r1 = r7.mLastEnableItemPosition
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r1
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListViewAdapter r1 = r7.mListViewAdpter
            r7.setAdapter((android.widget.ListAdapter) r1)
            int r1 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x02ba:
            int r1 = r7.pageNum
            if (r1 <= r6) goto L_0x02f7
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            int r1 = r1.getCurrentPage()
            if (r1 != r6) goto L_0x02f7
            com.mediatek.wwtv.tvcenter.util.PageImp r1 = r7.mPageImp
            r1.lastPage()
            com.mediatek.wwtv.tvcenter.epg.eu.EPGListView$UpDateListView r1 = r7.mUpdate
            r1.updata(r2)
            int r1 = r7.mLastEnableItemPosition
            r7.setSelection(r1)
            goto L_0x02f8
        L_0x02d6:
            java.lang.String r1 = "EPGListView"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "key up>getSelectedItemPosition>"
            r2.append(r3)
            int r3 = r7.getSelectedItemPosition()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            int r1 = r7.getSelectedItemPosition()
            int r1 = r1 - r6
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r1
        L_0x02f7:
        L_0x02f8:
            boolean r1 = super.onKeyDown(r8, r9)
            return r1
        L_0x02fd:
            return r6
        L_0x02fe:
            return r2
        L_0x02ff:
            return r2
        L_0x0300:
            java.lang.String r1 = "EPGListView"
            java.lang.String r3 = "getCurrentList()==null or getCurrentList is empty!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r3)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGListView.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    private boolean onRightKey() {
        this.mCanChangeChannel = false;
        this.mCurrentSelectedPosition = getSelectedItemPosition();
        if (this.mCurrentSelectedPosition < 0) {
            this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
        }
        this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
        this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
        if (this.mSelectedItemView != null) {
            long boundMills = 0;
            long lastMills = 0;
            if (this.mListViewAdpter.getDayNum() == 8) {
                boundMills = EPGUtil.getEpgLastTimeMills(this.mListViewAdpter.getDayNum(), 0, true);
                lastMills = EPGUtil.getEpgLastTimeMills(this.mListViewAdpter.getDayNum(), this.mListViewAdpter.getStartHour() + 2, false);
            }
            int index = this.mSelectedItemView.getmCurrentSelectPosition();
            MtkLog.d(TAG, "KeyEvent.KEYCODE_DPAD_RIGHT---------index--->" + index + "     getChildCount:>>" + this.mSelectedItemView.getChildCount() + "    mListViewAdpter.getDayNum()>>" + this.mListViewAdpter.getDayNum() + "   " + (lastMills - boundMills));
            if (this.mListViewAdpter.getDayNum() == 8 && lastMills >= boundMills && (index == this.mSelectedItemView.getChildCount() - 1 || this.mSelectedItemView.getChildCount() == 0)) {
                this.mCanChangeChannel = true;
                return true;
            }
            this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
            if (this.mSelectedItemView.onKeyRight()) {
                EPGConfig.init = false;
                this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                EPGConfig.FROM_WHERE = 27;
                this.mCanChangeChannel = true;
            } else if (!changeTimeZoom(22)) {
                this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                this.mCanChangeChannel = true;
            }
        } else {
            this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
            this.mCanChangeChannel = true;
        }
        return false;
    }

    private boolean onLeftKey() {
        this.mCanChangeChannel = false;
        this.mCurrentSelectedPosition = getSelectedItemPosition();
        if (this.mCurrentSelectedPosition < 0) {
            this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
        }
        this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
        this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
        if (this.mSelectedItemView != null) {
            int boundHours = 0;
            if (this.mListViewAdpter.getDayNum() == 0) {
                boundHours = EPGUtil.getCurrentHour();
            }
            int index = this.mSelectedItemView.getmCurrentSelectPosition();
            MtkLog.d(TAG, "KeyEvent.KEYCODE_DPAD_LEFT---------index--->" + index);
            if (this.mListViewAdpter.getDayNum() != 0 || this.mListViewAdpter.getStartHour() > boundHours || index > 0) {
                this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                if (this.mSelectedItemView.onKeyLeft()) {
                    EPGConfig.init = false;
                    EPGConfig.FROM_WHERE = 27;
                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                    this.mCanChangeChannel = true;
                } else if (!changeTimeZoom(21)) {
                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                    this.mCanChangeChannel = true;
                }
            } else {
                this.mCanChangeChannel = true;
                return true;
            }
        } else {
            this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
            this.mCanChangeChannel = true;
        }
        return false;
    }

    private boolean changeTimeZoom(int keyCode) {
        int mStartTime = this.mListViewAdpter.getStartHour();
        int mDayNum = this.mListViewAdpter.getDayNum();
        if (keyCode == 21) {
            EPGConfig.FROM_WHERE = 21;
            if (mStartTime >= 2) {
                mStartTime -= 2;
                this.mListViewAdpter.setStartHour(mStartTime);
            } else if (mDayNum <= 0) {
                return false;
            } else {
                mDayNum--;
                this.mListViewAdpter.setDayNum(mDayNum);
                mStartTime += 22;
                this.mListViewAdpter.setStartHour(mStartTime);
            }
        } else if (keyCode == 22) {
            EPGConfig.FROM_WHERE = 22;
            if (mStartTime < 22) {
                mStartTime += 2;
                this.mListViewAdpter.setStartHour(mStartTime);
            } else if (mDayNum >= 8) {
                return false;
            } else {
                mDayNum++;
                this.mListViewAdpter.setDayNum(mDayNum);
                mStartTime = (mStartTime + 2) % 24;
                this.mListViewAdpter.setStartHour(mStartTime);
            }
        }
        this.mHandler.removeMessages(4);
        this.mHandler.sendEmptyMessage(4);
        Message message = this.mHandler.obtainMessage();
        message.what = EPGConfig.EPG_SYNCHRONIZATION_MESSAGE;
        message.arg1 = mDayNum;
        message.arg2 = mStartTime;
        this.mHandler.sendMessage(message);
        this.mListViewAdpter.setActiveWindow();
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 19:
            case 20:
                this.mCanKeyUp = false;
                EPGConfig.avoidFoucsChange = true;
                MtkLog.d(TAG, "getSelectedItemPosition()>>>" + getSelectedItemPosition());
                EPGConfig.SELECTED_CHANNEL_POSITION = getSelectedItemPosition();
                if (EPGConfig.SELECTED_CHANNEL_POSITION < 0) {
                    EPGConfig.SELECTED_CHANNEL_POSITION = 0;
                }
                this.mCurrentChannel = (EPGChannelInfo) getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
                if (CommonIntegration.getInstance().is3rdTVSource()) {
                    sendChangeCHMsg(this.mCurrentChannel);
                    MtkLog.d(TAG, "selectChannelByTIF");
                }
                if (this.mCurrentChannel == null) {
                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                    this.mCanChangeChannel = true;
                    break;
                } else {
                    this.mNextSelectedItemView = getSelectedDynamicLinearLayout(EPGConfig.SELECTED_CHANNEL_POSITION);
                    int postion = this.mCurrentChannel.getNextPosition(this.mLastSelectedTVProgram);
                    if (this.mNextSelectedItemView != null) {
                        this.mNextSelectedItemView.setSelectedPosition(postion);
                    }
                    sendChangeCHMsg(this.mCurrentChannel);
                    this.mHandler.sendEmptyMessage(EPGConfig.EPG_SELECT_CHANNEL_COMPLETE);
                    break;
                }
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                dispatchKeyEvent(new KeyEvent(1, 20));
                break;
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                dispatchKeyEvent(new KeyEvent(1, 19));
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void sendChangeCHMsg(EPGChannelInfo channelInfo) {
        this.mHandler.removeMessages(EPGConfig.EPG_CHANGING_CHANNEL);
        Message msg = Message.obtain();
        msg.what = EPGConfig.EPG_CHANGING_CHANNEL;
        msg.obj = channelInfo;
        this.mHandler.sendMessage(msg);
    }

    public EPGLinearLayout getSelectedDynamicLinearLayout(int mPosition) {
        this.mListViewChildView = (LinearLayout) getChildAt(mPosition);
        if (this.mListViewChildView != null) {
            return (EPGLinearLayout) this.mListViewChildView.getChildAt(1);
        }
        this.mListViewChildView = (LinearLayout) getSelectedView();
        if (this.mListViewChildView != null) {
            return (EPGLinearLayout) this.mListViewChildView.getChildAt(1);
        }
        return null;
    }

    public void rawChangedOfChannel() {
        List<EPGProgramInfo> childTVProgram;
        this.mSelectedItemView = getSelectedDynamicLinearLayout(getSelectedItemPosition());
        if (this.mListViewAdpter != null && this.mSelectedItemView != null) {
            int _position = this.mSelectedItemView.getmCurrentSelectPosition();
            if (!(this.mCurrentChannel == null || _position == -1 || (childTVProgram = this.mCurrentChannel.getmTVProgramInfoList()) == null || childTVProgram.size() <= 0 || _position >= childTVProgram.size())) {
                if (_position >= childTVProgram.size()) {
                    _position = childTVProgram.size() - 1;
                    this.mSelectedItemView.setmCurrentSelectPosition(_position);
                }
                this.mLastSelectedTVProgram = childTVProgram.get(_position);
            }
            this.mSelectedItemView.clearSelected();
            this.mListViewAdpter.setGroup(this.mListViewAdpter.getGroup());
            setAdapter((ListAdapter) this.mListViewAdpter);
            setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
            EPGConfig.SELECTED_CHANNEL_POSITION = getSelectedItemPosition();
            if (EPGConfig.avoidFoucsChange) {
                EPGConfig.FROM_WHERE = 25;
            } else {
                EPGConfig.FROM_WHERE = 26;
            }
            this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
        }
    }
}
