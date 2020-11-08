package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.mediatek.wwtv.setting.base.scan.adapter.ChannelInfoAdapter;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Timer;
import java.util.TimerTask;

public class ChannelInfoListView extends ListView {
    private ListAdapter adapter;
    int currentItemPosition;
    private boolean isScrollTop;
    /* access modifiers changed from: private */
    public int itemHeight;
    private int itemsCount;
    int lastItemPosition;
    Context mContext;
    private OnScrollBottomListener onScrollBottomListener;
    private OnScrollTopListener onScrollTopListener;
    /* access modifiers changed from: private */
    public int scrollDuration;
    private Timer timer;

    public interface OnScrollBottomListener {
        void onScrollBottom();
    }

    public interface OnScrollTopListener {
        void onScrollTop();
    }

    public ChannelInfoListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ChannelInfoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scrollDuration = 1000;
        setSmoothScrollbarEnabled(true);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.adapter != null) {
            this.itemHeight = getChildAt(0).getHeight();
        }
    }

    public void setAdapter(ListAdapter adapter2) {
        super.setAdapter(adapter2);
        this.adapter = adapter2;
        this.itemsCount = adapter2.getCount();
        this.currentItemPosition = 0;
        this.lastItemPosition = 0;
    }

    public void setScrollDuration(int scrollDutation) {
        this.scrollDuration = scrollDutation;
    }

    public int getCurrentItemPosition() {
        return this.currentItemPosition;
    }

    public void setCurrentItemPosition(int currentItemPosition2) {
        this.currentItemPosition = currentItemPosition2;
    }

    public void smoothScrollTo(final int pos) {
        postDelayed(new Runnable() {
            public void run() {
                MtkLog.d("View", "itemHeight:" + ChannelInfoListView.this.itemHeight);
                ChannelInfoListView.this.smoothScrollBy(ChannelInfoListView.this.itemHeight * (pos + 1), ChannelInfoListView.this.scrollDuration);
            }
        }, 500);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MtkLog.d("View", "onKeyDown.....keyCode:" + keyCode + ",");
        if (keyCode == 21) {
            if (this.adapter instanceof ChannelInfoAdapter) {
                ((ChannelInfoAdapter) this.adapter).goToPrevPage();
            }
            return true;
        } else if (keyCode == 22) {
            if (this.adapter instanceof ChannelInfoAdapter) {
                ((ChannelInfoAdapter) this.adapter).goToNextPage();
            }
            return true;
        } else if (keyCode == 20) {
            setCurr_LastPos();
            if (this.currentItemPosition == this.itemsCount - 1) {
                if (this.onScrollBottomListener != null) {
                    this.onScrollBottomListener.onScrollBottom();
                }
                return true;
            }
            this.currentItemPosition++;
            MtkLog.d("View", "onKey-dpadDown scroll:" + (this.currentItemPosition - this.lastItemPosition));
            smoothScrollBy(this.itemHeight * (this.currentItemPosition - this.lastItemPosition), this.scrollDuration);
            return false;
        } else if (keyCode != 19) {
            return super.onKeyDown(keyCode, event);
        } else {
            setCurr_LastPos();
            if (this.currentItemPosition == 0) {
                if (this.onScrollTopListener != null) {
                    this.onScrollTopListener.onScrollTop();
                }
                return this.isScrollTop;
            }
            this.currentItemPosition--;
            MtkLog.d("View", "onKey-dpadUp scroll:" + (this.currentItemPosition - this.lastItemPosition));
            if (this.currentItemPosition > this.lastItemPosition) {
                smoothScrollBy(this.itemHeight * (this.currentItemPosition - this.lastItemPosition), this.scrollDuration);
            } else {
                smoothScrollBy(-this.itemHeight, this.scrollDuration);
            }
            return false;
        }
    }

    private void setCurr_LastPos() {
        this.lastItemPosition = this.currentItemPosition;
        if (this.adapter instanceof ChannelInfoAdapter) {
            int selecpos = ((ChannelInfoAdapter) this.adapter).getSelectPos();
            if (this.currentItemPosition == 0 && selecpos > this.currentItemPosition) {
                this.currentItemPosition = selecpos;
            }
        }
        MtkLog.d("View", "lastItemPosition:" + this.lastItemPosition + ",currentItemPosition==" + this.currentItemPosition);
    }

    private void smoothScrollToBottom() {
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                ChannelInfoListView.this.post(new Runnable() {
                    public void run() {
                        ChannelInfoListView.this.setSelection(ChannelInfoListView.this.getLastVisiblePosition());
                    }
                });
            }
        }, (long) (this.scrollDuration / 3));
    }

    public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener2) {
        this.onScrollBottomListener = onScrollBottomListener2;
    }

    public void setOnScrollTopListener(OnScrollTopListener onScrollTopListener2) {
        this.isScrollTop = true;
        this.onScrollTopListener = onScrollTopListener2;
    }
}
