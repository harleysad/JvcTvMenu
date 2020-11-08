package com.mediatek.wwtv.tvcenter.epg.cn;

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
    public boolean mCanChangeChannel;
    public boolean mCanKeyUp;
    private EPGChannelInfo mCurrentChannel;
    private int mCurrentSelectedPosition = 0;
    private EPGCnActivity mEPGAcivity;
    private int mFirstEnableItemPosition;
    private Handler mHandler;
    private int mLastEnableItemPosition;
    private int mLastRightSelectedPosition = 0;
    private EPGProgramInfo mLastSelectedTVProgram;
    private EPGListViewAdapter mListViewAdpter;
    private LinearLayout mListViewChildView;
    private EPGLinearLayout mNextSelectedItemView;
    private PageImp mPageImp = new PageImp();
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
        if (context instanceof EPGCnActivity) {
            this.mEPGAcivity = (EPGCnActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof EPGCnActivity) {
            this.mEPGAcivity = (EPGCnActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public EPGListView(Context context) {
        super(context);
        if (context instanceof EPGCnActivity) {
            this.mEPGAcivity = (EPGCnActivity) context;
        }
        this.mReader = DataReader.getInstance(context);
        this.mCanChangeChannel = true;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mFirstEnableItemPosition = 0;
        this.mLastEnableItemPosition = adapter.getCount() - 1;
        this.mListViewAdpter = (EPGListViewAdapter) adapter;
        this.mListViewAdpter.setEPGListView(this);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int i = keyCode;
        if (!hasFocus() || !this.mCanChangeChannel) {
            return false;
        }
        if (i == 66) {
            return false;
        }
        switch (i) {
            case 19:
                if (this.mCanChangeChannel) {
                    EPGConfig.init = false;
                    this.mCanChangeChannel = false;
                    this.mCanKeyUp = true;
                    EPGConfig.FROM_WHERE = 27;
                    this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                    this.mCurrentSelectedPosition = getSelectedItemPosition();
                    if (this.mCurrentSelectedPosition < 0) {
                        this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
                    }
                    this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
                    this.mCurrentChannel = (EPGChannelInfo) getItemAtPosition(this.mCurrentSelectedPosition);
                    this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
                    if (this.mSelectedItemView != null) {
                        int _position = this.mSelectedItemView.getmCurrentSelectPosition();
                        this.mSelectedItemView.clearSelected();
                        if (this.mCurrentChannel == null || _position == -1) {
                            this.mLastSelectedTVProgram = null;
                        } else {
                            List<EPGProgramInfo> childTVProgram = this.mCurrentChannel.getmTVProgramInfoList();
                            if (childTVProgram != null && childTVProgram.size() > 0) {
                                this.mLastSelectedTVProgram = childTVProgram.get(_position);
                            }
                        }
                    }
                    MtkLog.d(TAG, "KEYCODE_DPAD_UP mCurrentSelectedPosition>>" + this.mCurrentSelectedPosition + "   " + this.mLastEnableItemPosition + "  " + this.mFirstEnableItemPosition + "  " + this.pageNum);
                    StringBuilder sb = new StringBuilder();
                    sb.append("KEYCODE_DPAD_DOWN>> mPageImp.getCurrentPage()");
                    sb.append(this.mPageImp.getCurrentPage());
                    MtkLog.d(TAG, sb.toString());
                    if (this.mCurrentSelectedPosition == this.mFirstEnableItemPosition) {
                        if (this.mPageImp.getCurrentPage() == 1) {
                            if (this.pageNum != 1) {
                                if (this.pageNum > 1 && this.mPageImp.getCurrentPage() == 1) {
                                    this.mPageImp.lastPage();
                                    this.mUpdate.updata(false);
                                    setSelection(this.mLastEnableItemPosition);
                                    break;
                                }
                            } else {
                                EPGConfig.SELECTED_CHANNEL_POSITION = this.mLastEnableItemPosition;
                                setAdapter((ListAdapter) this.mListViewAdpter);
                                setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
                                break;
                            }
                        } else {
                            this.mPageImp.prePage();
                            this.mUpdate.updata(false);
                            setSelection(this.mLastEnableItemPosition);
                            break;
                        }
                    } else {
                        MtkLog.d(TAG, "key up>getSelectedItemPosition>" + getSelectedItemPosition());
                        EPGConfig.SELECTED_CHANNEL_POSITION = getSelectedItemPosition() - 1;
                        break;
                    }
                } else {
                    return true;
                }
            case 20:
                if (this.mCanChangeChannel) {
                    EPGConfig.init = false;
                    this.mCanChangeChannel = false;
                    this.mCanKeyUp = true;
                    EPGConfig.FROM_WHERE = 27;
                    this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                    this.mCurrentSelectedPosition = getSelectedItemPosition();
                    if (this.mCurrentSelectedPosition < 0) {
                        this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
                    }
                    this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
                    this.mCurrentChannel = (EPGChannelInfo) getItemAtPosition(this.mCurrentSelectedPosition);
                    this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
                    if (this.mSelectedItemView != null) {
                        int _position2 = this.mSelectedItemView.getmCurrentSelectPosition();
                        this.mSelectedItemView.clearSelected();
                        if (this.mCurrentChannel == null || _position2 == -1) {
                            this.mLastSelectedTVProgram = null;
                        } else {
                            List<EPGProgramInfo> childTVProgram2 = this.mCurrentChannel.getmTVProgramInfoList();
                            if (childTVProgram2 != null && childTVProgram2.size() > 0) {
                                this.mLastSelectedTVProgram = childTVProgram2.get(_position2);
                            }
                        }
                    }
                    MtkLog.d(TAG, "KEYCODE_DPAD_DOWN mCurrentSelectedPosition>>" + this.mCurrentSelectedPosition + "   " + this.mLastEnableItemPosition + "  " + this.mFirstEnableItemPosition + "  " + this.pageNum);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("KEYCODE_DPAD_DOWN>> mPageImp.getCurrentPage()");
                    sb2.append(this.mPageImp.getCurrentPage());
                    MtkLog.d(TAG, sb2.toString());
                    if (this.mCurrentSelectedPosition == this.mLastEnableItemPosition) {
                        if (this.mPageImp.getCurrentPage() == this.pageNum) {
                            if (this.pageNum != 1) {
                                if (this.mPageImp.getCurrentPage() == this.mPageImp.getPageNum()) {
                                    this.mPageImp.headPage();
                                    this.mUpdate.updata(true);
                                    setSelection(this.mFirstEnableItemPosition);
                                    break;
                                }
                            } else {
                                EPGConfig.SELECTED_CHANNEL_POSITION = 0;
                                setAdapter((ListAdapter) this.mListViewAdpter);
                                setSelection(EPGConfig.SELECTED_CHANNEL_POSITION);
                                break;
                            }
                        } else {
                            this.mPageImp.nextPage();
                            this.mUpdate.updata(true);
                            setSelection(this.mFirstEnableItemPosition);
                            break;
                        }
                    } else {
                        MtkLog.d(TAG, "key dowm>getSelectedItemPosition>" + getSelectedItemPosition());
                        EPGConfig.SELECTED_CHANNEL_POSITION = getSelectedItemPosition() + 1;
                        break;
                    }
                } else {
                    return true;
                }
                break;
            case 21:
                if (this.mCanChangeChannel) {
                    this.mCanChangeChannel = false;
                    this.mCurrentSelectedPosition = getSelectedItemPosition();
                    if (this.mCurrentSelectedPosition < 0) {
                        this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
                    }
                    this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
                    this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
                    if (this.mSelectedItemView == null) {
                        this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                        this.mCanChangeChannel = true;
                        break;
                    } else {
                        int boundHours = 0;
                        if (this.mListViewAdpter.getDayNum() == 0) {
                            boundHours = EPGUtil.getCurrentHour();
                        }
                        int index = this.mSelectedItemView.getmCurrentSelectPosition();
                        MtkLog.d(TAG, "KeyEvent.KEYCODE_DPAD_LEFT---------index--->" + index);
                        if (this.mListViewAdpter.getDayNum() != 0 || this.mListViewAdpter.getStartHour() > boundHours || index > 0) {
                            this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                            if (!this.mSelectedItemView.onKeyLeft()) {
                                if (!changeTimeZoom(keyCode)) {
                                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                                    this.mCanChangeChannel = true;
                                    break;
                                }
                            } else {
                                EPGConfig.init = false;
                                EPGConfig.FROM_WHERE = 27;
                                this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                                this.mCanChangeChannel = true;
                                break;
                            }
                        } else {
                            this.mCanChangeChannel = true;
                            return true;
                        }
                    }
                }
                break;
            case 22:
                if (this.mCanChangeChannel) {
                    this.mCanChangeChannel = false;
                    this.mCurrentSelectedPosition = getSelectedItemPosition();
                    if (this.mCurrentSelectedPosition < 0) {
                        this.mCurrentSelectedPosition = this.mLastRightSelectedPosition;
                    }
                    this.mLastRightSelectedPosition = this.mCurrentSelectedPosition;
                    this.mSelectedItemView = getSelectedDynamicLinearLayout(this.mCurrentSelectedPosition);
                    if (this.mSelectedItemView == null) {
                        this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                        this.mCanChangeChannel = true;
                        break;
                    } else {
                        long boundMills = 0;
                        long lastMills = 0;
                        if (this.mListViewAdpter.getDayNum() == 8) {
                            boundMills = EPGUtil.getEpgLastTimeMills(this.mListViewAdpter.getDayNum(), 0, true);
                            lastMills = EPGUtil.getEpgLastTimeMills(this.mListViewAdpter.getDayNum(), this.mListViewAdpter.getStartHour() + 2, false);
                        }
                        int index2 = this.mSelectedItemView.getmCurrentSelectPosition();
                        MtkLog.d(TAG, "KeyEvent.KEYCODE_DPAD_RIGHT---------index--->" + index2 + "     getChildCount:>>" + this.mSelectedItemView.getChildCount() + "    mListViewAdpter.getDayNum()>>" + this.mListViewAdpter.getDayNum() + "   " + (lastMills - boundMills));
                        if (this.mListViewAdpter.getDayNum() != 8 || lastMills < boundMills || (index2 != this.mSelectedItemView.getChildCount() - 1 && this.mSelectedItemView.getChildCount() != 0)) {
                            this.mHandler.removeMessages(EPGConfig.EPG_PROGRAMINFO_SHOW);
                            if (!this.mSelectedItemView.onKeyRight()) {
                                if (!changeTimeZoom(keyCode)) {
                                    this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                                    this.mCanChangeChannel = true;
                                    break;
                                }
                            } else {
                                EPGConfig.init = false;
                                this.mHandler.sendEmptyMessageDelayed(EPGConfig.EPG_PROGRAMINFO_SHOW, 1000);
                                EPGConfig.FROM_WHERE = 27;
                                this.mCanChangeChannel = true;
                                break;
                            }
                        } else {
                            this.mCanChangeChannel = true;
                            return true;
                        }
                    }
                }
                break;
            case 23:
                return false;
            default:
                switch (i) {
                    case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                        dispatchKeyEvent(new KeyEvent(0, 20));
                        break;
                    case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                        dispatchKeyEvent(new KeyEvent(0, 19));
                        break;
                }
        }
        return super.onKeyDown(keyCode, event);
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
        this.mListViewAdpter.clearWindowList();
        this.mHandler.removeMessages(263);
        this.mHandler.removeMessages(EPGConfig.EPG_GET_EVENT_LIST_DELAY_LONG);
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
                    MtkLog.d(TAG, "getSelectedItemPosition()>>>" + getSelectedItemPosition());
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
