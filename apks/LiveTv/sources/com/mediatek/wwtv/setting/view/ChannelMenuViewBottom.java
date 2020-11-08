package com.mediatek.wwtv.setting.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;

public class ChannelMenuViewBottom extends LinearLayout {
    private Context mContext;
    private TextView tDelete;
    private TextView tInsert;
    private TextView tSwap;

    public ChannelMenuViewBottom(Context context) {
        super(context);
        this.mContext = context;
    }

    public ChannelMenuViewBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.menu_channelpage_bottom, this);
        this.tSwap = (TextView) findViewById(R.id.common_menuview_text_swap);
        this.tInsert = (TextView) findViewById(R.id.common_menuview_text_insert);
        this.tDelete = (TextView) findViewById(R.id.common_menuview_text_delete);
    }
}
