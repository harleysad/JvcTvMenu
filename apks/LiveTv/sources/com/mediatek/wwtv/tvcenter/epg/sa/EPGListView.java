package com.mediatek.wwtv.tvcenter.epg.sa;

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
    private EPGSaActivity mEPGAcivity;
    private int mFirstEnableItemPosition;
    private Handler mHandler;
    private int mLastEnableItemPosition;
    private int mLastRightSelectedPosition = 0;
    private EPGProgramInfo mLastSelectedTVProgram;
    private EPGListViewAdapter mListViewAdpter;
    private LinearLayout mListViewChildView;
    private EPGLinearLayout mNextSelectedItemView;
    private PageImp mPageImp;
    private DataReader mReader;
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
        if (context instanceof EPGSaActivity) {
            this.mEPGAcivity = (EPGSaActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof EPGSaActivity) {
            this.mEPGAcivity = (EPGSaActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context) {
        super(context);
        if (context instanceof EPGSaActivity) {
            this.mEPGAcivity = (EPGSaActivity) context;
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

    /* JADX WARNING: Code restructure failed: missing block: B:143:0x044b, code lost:
        return super.onKeyDown(r18, r19);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyDown(int r18, android.view.KeyEvent r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = 0
            boolean r3 = r17.hasFocus()
            r4 = 0
            if (r3 == 0) goto L_0x0450
            boolean r3 = r0.mCanChangeChannel
            if (r3 != 0) goto L_0x0012
            goto L_0x0450
        L_0x0012:
            r3 = 66
            if (r1 == r3) goto L_0x003f
            r3 = 93
            if (r1 == r3) goto L_0x044d
            r3 = -1
            r5 = 0
            r9 = 260(0x104, float:3.64E-43)
            r10 = 1
            switch(r1) {
                case 19: goto L_0x0360;
                case 20: goto L_0x0215;
                case 21: goto L_0x014e;
                case 22: goto L_0x0042;
                case 23: goto L_0x003f;
                default: goto L_0x0022;
            }
        L_0x0022:
            switch(r1) {
                case 166: goto L_0x0033;
                case 167: goto L_0x0027;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x0447
        L_0x0027:
            android.view.KeyEvent r3 = new android.view.KeyEvent
            r5 = 19
            r3.<init>(r4, r5)
            r0.dispatchKeyEvent(r3)
            goto L_0x0447
        L_0x0033:
            android.view.KeyEvent r3 = new android.view.KeyEvent
            r5 = 20
            r3.<init>(r4, r5)
            r0.dispatchKeyEvent(r3)
            goto L_0x0447
        L_0x003f:
            r3 = r4
            goto L_0x044f
        L_0x0042:
            boolean r3 = r0.mCanChangeChannel
            if (r3 == 0) goto L_0x0447
            r0.mCanChangeChannel = r4
            int r3 = r17.getSelectedItemPosition()
            r0.mCurrentSelectedPosition = r3
            int r3 = r0.mCurrentSelectedPosition
            if (r3 >= 0) goto L_0x0056
            int r3 = r0.mLastRightSelectedPosition
            r0.mCurrentSelectedPosition = r3
        L_0x0056:
            int r3 = r0.mCurrentSelectedPosition
            r0.mLastRightSelectedPosition = r3
            int r3 = r0.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r3 = r0.getSelectedDynamicLinearLayout(r3)
            r0.mSelectedItemView = r3
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r11 = "mCurrentSelectedPosition--->"
            r5.append(r11)
            int r11 = r0.mCurrentSelectedPosition
            r5.append(r11)
            java.lang.String r11 = "  mSelectedItemView>>>"
            r5.append(r11)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r11 = r0.mSelectedItemView
            r5.append(r11)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r5)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r3 = r0.mSelectedItemView
            if (r3 == 0) goto L_0x0143
            r11 = 0
            r13 = 0
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r3 = r0.mListViewAdpter
            int r3 = r3.getDayNum()
            r5 = 8
            if (r3 != r5) goto L_0x00b2
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r3 = r0.mListViewAdpter
            int r3 = r3.getDayNum()
            long r11 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getEpgLastTimeMills(r3, r4, r10)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r3 = r0.mListViewAdpter
            int r3 = r3.getDayNum()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r15 = r0.mListViewAdpter
            int r15 = r15.getStartHour()
            int r15 = r15 + 2
            long r13 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getEpgLastTimeMills(r3, r15, r4)
        L_0x00b2:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r3 = r0.mSelectedItemView
            int r3 = r3.getmCurrentSelectPosition()
            java.lang.String r15 = "EPGListView"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r4 = "KeyEvent.KEYCODE_DPAD_RIGHT---------index--->"
            r6.append(r4)
            r6.append(r3)
            java.lang.String r4 = "     getChildCount:>>"
            r6.append(r4)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getChildCount()
            r6.append(r4)
            java.lang.String r4 = "    mListViewAdpter.getDayNum()>>"
            r6.append(r4)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r4 = r0.mListViewAdpter
            int r4 = r4.getDayNum()
            r6.append(r4)
            java.lang.String r4 = "   "
            r6.append(r4)
            long r7 = r13 - r11
            r6.append(r7)
            java.lang.String r4 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r4)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r4 = r0.mListViewAdpter
            int r4 = r4.getDayNum()
            if (r4 != r5) goto L_0x0114
            int r4 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r4 < 0) goto L_0x0114
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getChildCount()
            int r4 = r4 - r10
            if (r3 == r4) goto L_0x0111
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getChildCount()
            if (r4 != 0) goto L_0x0114
        L_0x0111:
            r0.mCanChangeChannel = r10
            return r10
        L_0x0114:
            android.os.Handler r4 = r0.mHandler
            r4.removeMessages(r9)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            boolean r2 = r4.onKeyRight()
            if (r2 != 0) goto L_0x0131
            boolean r2 = r17.changeTimeZoom(r18)
            if (r2 != 0) goto L_0x0141
            android.os.Handler r4 = r0.mHandler
            r5 = 300(0x12c, double:1.48E-321)
            r4.sendEmptyMessageDelayed(r9, r5)
            r0.mCanChangeChannel = r10
            goto L_0x0141
        L_0x0131:
            r4 = 0
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r4 = 27
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r4
            android.os.Handler r4 = r0.mHandler
            r5 = 300(0x12c, double:1.48E-321)
            r4.sendEmptyMessageDelayed(r9, r5)
            r0.mCanChangeChannel = r10
        L_0x0141:
            goto L_0x0447
        L_0x0143:
            r5 = 300(0x12c, double:1.48E-321)
            android.os.Handler r3 = r0.mHandler
            r3.sendEmptyMessageDelayed(r9, r5)
            r0.mCanChangeChannel = r10
            goto L_0x0447
        L_0x014e:
            boolean r3 = r0.mCanChangeChannel
            if (r3 == 0) goto L_0x0447
            r3 = 0
            r0.mCanChangeChannel = r3
            int r3 = r17.getSelectedItemPosition()
            r0.mCurrentSelectedPosition = r3
            int r3 = r0.mCurrentSelectedPosition
            if (r3 >= 0) goto L_0x0163
            int r3 = r0.mLastRightSelectedPosition
            r0.mCurrentSelectedPosition = r3
        L_0x0163:
            int r3 = r0.mCurrentSelectedPosition
            r0.mLastRightSelectedPosition = r3
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "mCurrentSelectedPosition--->"
            r4.append(r5)
            int r5 = r0.mCurrentSelectedPosition
            r4.append(r5)
            java.lang.String r5 = "  mSelectedItemView>>>"
            r4.append(r5)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r5 = r0.mSelectedItemView
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            int r3 = r0.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r3 = r0.getSelectedDynamicLinearLayout(r3)
            r0.mSelectedItemView = r3
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r3 = r0.mSelectedItemView
            if (r3 == 0) goto L_0x020a
            r3 = 0
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r4 = r0.mListViewAdpter
            int r4 = r4.getDayNum()
            if (r4 != 0) goto L_0x01a2
            int r3 = com.mediatek.wwtv.tvcenter.epg.EPGUtil.getCurrentHour()
        L_0x01a2:
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getmCurrentSelectPosition()
            java.lang.String r5 = "EPGListView"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "KeyEvent.KEYCODE_DPAD_LEFT---------index--->"
            r6.append(r7)
            r6.append(r4)
            java.lang.String r7 = "     boundHours>>"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r5 = r0.mListViewAdpter
            int r5 = r5.getDayNum()
            if (r5 != 0) goto L_0x01db
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r5 = r0.mListViewAdpter
            int r5 = r5.getStartHour()
            if (r5 > r3) goto L_0x01db
            if (r4 > 0) goto L_0x01db
            r0.mCanChangeChannel = r10
            return r10
        L_0x01db:
            android.os.Handler r5 = r0.mHandler
            r5.removeMessages(r9)
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r5 = r0.mSelectedItemView
            boolean r2 = r5.onKeyLeft()
            if (r2 != 0) goto L_0x01f8
            boolean r2 = r17.changeTimeZoom(r18)
            if (r2 != 0) goto L_0x0208
            android.os.Handler r5 = r0.mHandler
            r6 = 300(0x12c, double:1.48E-321)
            r5.sendEmptyMessageDelayed(r9, r6)
            r0.mCanChangeChannel = r10
            goto L_0x0208
        L_0x01f8:
            r5 = 0
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r5
            r5 = 27
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r5
            android.os.Handler r5 = r0.mHandler
            r6 = 300(0x12c, double:1.48E-321)
            r5.sendEmptyMessageDelayed(r9, r6)
            r0.mCanChangeChannel = r10
        L_0x0208:
            goto L_0x0447
        L_0x020a:
            r6 = 300(0x12c, double:1.48E-321)
            android.os.Handler r3 = r0.mHandler
            r3.sendEmptyMessageDelayed(r9, r6)
            r0.mCanChangeChannel = r10
            goto L_0x0447
        L_0x0215:
            boolean r4 = r0.mCanChangeChannel
            if (r4 == 0) goto L_0x035f
            r4 = 0
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r0.mCanChangeChannel = r4
            r0.mCanKeyUp = r10
            r4 = 27
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r4
            android.os.Handler r4 = r0.mHandler
            r4.removeMessages(r9)
            int r4 = r17.getSelectedItemPosition()
            r0.mCurrentSelectedPosition = r4
            int r4 = r0.mCurrentSelectedPosition
            if (r4 >= 0) goto L_0x0237
            int r4 = r0.mLastRightSelectedPosition
            r0.mCurrentSelectedPosition = r4
        L_0x0237:
            int r4 = r0.mCurrentSelectedPosition
            r0.mLastRightSelectedPosition = r4
            int r4 = r0.mCurrentSelectedPosition
            java.lang.Object r4 = r0.getItemAtPosition(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r4 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r4
            r0.mCurrentChannel = r4
            int r4 = r0.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.getSelectedDynamicLinearLayout(r4)
            r0.mSelectedItemView = r4
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            if (r4 == 0) goto L_0x0284
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getmCurrentSelectPosition()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r6 = r0.mSelectedItemView
            r6.clearSelected()
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r6 = r0.mCurrentChannel
            if (r6 == 0) goto L_0x0282
            if (r4 == r3) goto L_0x0282
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r3 = r0.mCurrentChannel
            java.util.List r3 = r3.getmTVProgramInfoList()
            if (r3 == 0) goto L_0x027f
            int r6 = r3.size()
            if (r6 <= 0) goto L_0x027f
            int r6 = r3.size()
            if (r4 >= r6) goto L_0x027f
            java.lang.Object r5 = r3.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r5 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r5
            r0.mLastSelectedTVProgram = r5
            goto L_0x0281
        L_0x027f:
            r0.mLastSelectedTVProgram = r5
        L_0x0281:
            goto L_0x0284
        L_0x0282:
            r0.mLastSelectedTVProgram = r5
        L_0x0284:
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_DOWN mCurrentSelectedPosition>>"
            r4.append(r5)
            int r5 = r0.mCurrentSelectedPosition
            r4.append(r5)
            java.lang.String r5 = "   "
            r4.append(r5)
            int r5 = r0.mLastEnableItemPosition
            r4.append(r5)
            java.lang.String r5 = "  "
            r4.append(r5)
            int r5 = r0.mFirstEnableItemPosition
            r4.append(r5)
            java.lang.String r5 = "  "
            r4.append(r5)
            int r5 = r0.pageNum
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "KEYCODE_DPAD_DOWN>> mPageImp.getCurrentPage()"
            r4.append(r5)
            com.mediatek.wwtv.tvcenter.util.PageImp r5 = r0.mPageImp
            if (r5 == 0) goto L_0x02d5
            com.mediatek.wwtv.tvcenter.util.PageImp r5 = r0.mPageImp
            int r5 = r5.getCurrentPage()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            goto L_0x02d7
        L_0x02d5:
            com.mediatek.wwtv.tvcenter.util.PageImp r5 = r0.mPageImp
        L_0x02d7:
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            int r3 = r0.mCurrentSelectedPosition
            int r4 = r0.mLastEnableItemPosition
            if (r3 != r4) goto L_0x0338
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            if (r3 == 0) goto L_0x0338
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            int r3 = r3.getCurrentPage()
            int r4 = r0.pageNum
            if (r3 == r4) goto L_0x0306
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            r3.nextPage()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView$UpDateListView r3 = r0.mUpdate
            r3.updata(r10)
            int r3 = r0.mFirstEnableItemPosition
            r0.setSelection(r3)
            goto L_0x0447
        L_0x0306:
            int r3 = r0.pageNum
            if (r3 != r10) goto L_0x0319
            r3 = 0
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r3
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r3 = r0.mListViewAdpter
            r0.setAdapter((android.widget.ListAdapter) r3)
            int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            r0.setSelection(r3)
            goto L_0x0447
        L_0x0319:
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            int r3 = r3.getCurrentPage()
            com.mediatek.wwtv.tvcenter.util.PageImp r4 = r0.mPageImp
            int r4 = r4.getPageNum()
            if (r3 != r4) goto L_0x0447
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            r3.headPage()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView$UpDateListView r3 = r0.mUpdate
            r3.updata(r10)
            int r3 = r0.mFirstEnableItemPosition
            r0.setSelection(r3)
            goto L_0x0447
        L_0x0338:
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            if (r3 == 0) goto L_0x0447
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "key dowm>getSelectedItemPosition>"
            r4.append(r5)
            int r5 = r17.getSelectedItemPosition()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            int r3 = r17.getSelectedItemPosition()
            int r3 = r3 + r10
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r3
            goto L_0x0447
        L_0x035f:
            return r10
        L_0x0360:
            boolean r4 = r0.mCanChangeChannel
            if (r4 == 0) goto L_0x044c
            r4 = 0
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.init = r4
            r0.mCanChangeChannel = r4
            r0.mCanKeyUp = r10
            r4 = 27
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.FROM_WHERE = r4
            android.os.Handler r4 = r0.mHandler
            r4.removeMessages(r9)
            int r4 = r17.getSelectedItemPosition()
            r0.mCurrentSelectedPosition = r4
            int r4 = r0.mCurrentSelectedPosition
            if (r4 >= 0) goto L_0x0382
            int r4 = r0.mLastRightSelectedPosition
            r0.mCurrentSelectedPosition = r4
        L_0x0382:
            int r4 = r0.mCurrentSelectedPosition
            r0.mLastRightSelectedPosition = r4
            int r4 = r0.mCurrentSelectedPosition
            java.lang.Object r4 = r0.getItemAtPosition(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r4 = (com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo) r4
            r0.mCurrentChannel = r4
            int r4 = r0.mCurrentSelectedPosition
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.getSelectedDynamicLinearLayout(r4)
            r0.mSelectedItemView = r4
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            if (r4 == 0) goto L_0x03cf
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r4 = r0.mSelectedItemView
            int r4 = r4.getmCurrentSelectPosition()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGLinearLayout r6 = r0.mSelectedItemView
            r6.clearSelected()
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r6 = r0.mCurrentChannel
            if (r6 == 0) goto L_0x03cd
            if (r4 == r3) goto L_0x03cd
            com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo r3 = r0.mCurrentChannel
            java.util.List r3 = r3.getmTVProgramInfoList()
            if (r3 == 0) goto L_0x03ca
            int r6 = r3.size()
            if (r6 <= 0) goto L_0x03ca
            int r6 = r3.size()
            if (r4 >= r6) goto L_0x03ca
            java.lang.Object r5 = r3.get(r4)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r5 = (com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r5
            r0.mLastSelectedTVProgram = r5
            goto L_0x03cc
        L_0x03ca:
            r0.mLastSelectedTVProgram = r5
        L_0x03cc:
            goto L_0x03cf
        L_0x03cd:
            r0.mLastSelectedTVProgram = r5
        L_0x03cf:
            int r3 = r0.mCurrentSelectedPosition
            int r4 = r0.mFirstEnableItemPosition
            if (r3 != r4) goto L_0x0422
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            if (r3 == 0) goto L_0x0422
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            int r3 = r3.getCurrentPage()
            if (r3 == r10) goto L_0x03f2
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            r3.prePage()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView$UpDateListView r3 = r0.mUpdate
            r4 = 0
            r3.updata(r4)
            int r3 = r0.mLastEnableItemPosition
            r0.setSelection(r3)
            goto L_0x0447
        L_0x03f2:
            int r3 = r0.pageNum
            if (r3 != r10) goto L_0x0405
            int r3 = r0.mLastEnableItemPosition
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r3
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListViewAdapter r3 = r0.mListViewAdpter
            r0.setAdapter((android.widget.ListAdapter) r3)
            int r3 = com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION
            r0.setSelection(r3)
            goto L_0x0447
        L_0x0405:
            int r3 = r0.pageNum
            if (r3 <= r10) goto L_0x0447
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            int r3 = r3.getCurrentPage()
            if (r3 != r10) goto L_0x0447
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            r3.lastPage()
            com.mediatek.wwtv.tvcenter.epg.sa.EPGListView$UpDateListView r3 = r0.mUpdate
            r4 = 0
            r3.updata(r4)
            int r3 = r0.mLastEnableItemPosition
            r0.setSelection(r3)
            goto L_0x0447
        L_0x0422:
            com.mediatek.wwtv.tvcenter.util.PageImp r3 = r0.mPageImp
            if (r3 == 0) goto L_0x0447
            java.lang.String r3 = "EPGListView"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "key up>getSelectedItemPosition>"
            r4.append(r5)
            int r5 = r17.getSelectedItemPosition()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            int r3 = r17.getSelectedItemPosition()
            int r3 = r3 - r10
            com.mediatek.wwtv.tvcenter.epg.EPGConfig.SELECTED_CHANNEL_POSITION = r3
        L_0x0447:
            boolean r3 = super.onKeyDown(r18, r19)
            return r3
        L_0x044c:
            return r10
        L_0x044d:
            r3 = 0
            return r3
        L_0x044f:
            return r3
        L_0x0450:
            r3 = r4
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.sa.EPGListView.onKeyDown(int, android.view.KeyEvent):boolean");
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
        if (!hasFocus() || !this.mCanKeyUp) {
            return false;
        }
        switch (keyCode) {
            case 19:
            case 20:
                if (this.mCanKeyUp) {
                    this.mCanKeyUp = false;
                    EPGConfig.avoidFoucsChange = true;
                    EPGConfig.SELECTED_CHANNEL_POSITION = getSelectedItemPosition();
                    if (EPGConfig.SELECTED_CHANNEL_POSITION < 0) {
                        EPGConfig.SELECTED_CHANNEL_POSITION = 0;
                    }
                    this.mCurrentChannel = (EPGChannelInfo) getItemAtPosition(EPGConfig.SELECTED_CHANNEL_POSITION);
                    if (CommonIntegration.getInstance().is3rdTVSource()) {
                        this.mReader.selectChannelByTIF(this.mCurrentChannel.mId);
                        MtkLog.d(TAG, "selectChannelByTIF");
                    }
                    if (this.mCurrentChannel != null && this.mCurrentChannel.getTVChannel() != null && this.mCurrentChannel.getTVChannel().getChannelId() != this.mReader.getCurrentChId()) {
                        this.mNextSelectedItemView = getSelectedDynamicLinearLayout(EPGConfig.SELECTED_CHANNEL_POSITION);
                        int postion = this.mCurrentChannel.getNextPosition(this.mLastSelectedTVProgram);
                        if (this.mNextSelectedItemView != null) {
                            this.mNextSelectedItemView.setSelectedPosition(postion);
                        }
                        this.mReader.selectChannelByTIF(this.mCurrentChannel.mId);
                        this.mHandler.sendEmptyMessage(EPGConfig.EPG_SELECT_CHANNEL_COMPLETE);
                        break;
                    } else {
                        this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                        this.mCanChangeChannel = true;
                        break;
                    }
                } else {
                    return true;
                }
                break;
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                dispatchKeyEvent(new KeyEvent(1, 20));
                break;
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                dispatchKeyEvent(new KeyEvent(1, 19));
                break;
        }
        return super.onKeyUp(keyCode, event);
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

    public EPGLinearLayout getListItemProgramView(int position) {
        LinearLayout mListViewChildView2;
        if (position < 0 || position >= getChildCount() || (mListViewChildView2 = (LinearLayout) getChildAt(position)) == null) {
            return null;
        }
        return (EPGLinearLayout) mListViewChildView2.getChildAt(1);
    }

    public void rawChangedOfChannel() {
        List<EPGProgramInfo> childTVProgram;
        this.mSelectedItemView = getSelectedDynamicLinearLayout(getSelectedItemPosition());
        if (this.mListViewAdpter != null && this.mSelectedItemView != null) {
            int _position = this.mSelectedItemView.getmCurrentSelectPosition();
            if (!(this.mCurrentChannel == null || _position == -1 || (childTVProgram = this.mCurrentChannel.getmTVProgramInfoList()) == null || childTVProgram.size() <= 0)) {
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
