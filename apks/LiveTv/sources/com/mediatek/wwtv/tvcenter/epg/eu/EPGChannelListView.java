package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import com.mediatek.wwtv.tvcenter.epg.IPageCallback;
import com.mediatek.wwtv.tvcenter.epg.NoScrollListView;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class EPGChannelListView extends NoScrollListView {
    private static final String TAG = "EPGChannelListView";
    private Context mContext;
    private IPageCallback mPageCallback;

    public EPGChannelListView(Context context) {
        super(context);
        init(context);
    }

    public void addPageCallback(IPageCallback pageCallback) {
        this.mPageCallback = pageCallback;
    }

    public EPGChannelListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EPGChannelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        return adapter.getCount();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 19:
            case KeyMap.KEYCODE_MTKIR_CHDN /*167*/:
                int selectedItemPos = getSelectedItemPosition() % 6;
                MtkLog.d(TAG, "onKeyDown:selectedItemPos=" + selectedItemPos);
                if (selectedItemPos == 0) {
                    if (this.mPageCallback.hasPrePage()) {
                        MtkLog.d(TAG, "goto pre page");
                        this.mPageCallback.onRefreshPage();
                        setSelection(getCount() - 1);
                    }
                    return true;
                }
                break;
            case 20:
            case KeyMap.KEYCODE_MTKIR_CHUP /*166*/:
                int selectedItemPos2 = getSelectedItemPosition() % 6;
                MtkLog.d(TAG, "onKeyDown:selectedItemPos=" + selectedItemPos2);
                if (selectedItemPos2 == getCount() - 1 && this.mPageCallback.hasNextPage()) {
                    MtkLog.d(TAG, "goto next page");
                    this.mPageCallback.onRefreshPage();
                    setSelection(0);
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }
}