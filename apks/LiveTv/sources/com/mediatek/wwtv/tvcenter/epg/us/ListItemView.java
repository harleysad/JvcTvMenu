package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class ListItemView extends RelativeLayout {
    private static final String TAG = "ListItemView";
    private ImageView imageView;
    private Context mContext;
    private String[] mData;
    private String mFreq = "";
    private ListItemData mItemData;
    private TextView mNameTextView;
    private String mNum = "0000";
    private int mPositon;
    private ImageView mProgramLockImageView;
    private String temp1 = "";
    private String temp2 = "";
    private TextView textContent;

    public ListItemView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public void init() {
        addView((LinearLayout) inflate(this.mContext, R.layout.epg_us_listitem_view, (ViewGroup) null), new LinearLayout.LayoutParams(-1, 70));
        setFocusable(true);
        setClickable(true);
        setEnabled(true);
        this.mNameTextView = (TextView) findViewById(R.id.common_text_itemname);
        this.textContent = (TextView) findViewById(R.id.common_text_content);
        this.imageView = (ImageView) findViewById(R.id.program_imageview);
        this.mProgramLockImageView = (ImageView) findViewById(R.id.program_lock_imageview);
    }

    public void setAdapter(ListItemData itemData) {
        this.mItemData = itemData;
        if (!itemData.isValid()) {
            this.mNameTextView.setVisibility(4);
            this.imageView.setVisibility(4);
        } else {
            this.mNameTextView.setVisibility(0);
            setViewNameEx(itemData.getItemTime());
        }
        if (this.mItemData.isCC()) {
            this.imageView.setVisibility(0);
        } else {
            this.imageView.setVisibility(4);
        }
        if (this.mItemData.isBlocked()) {
            this.mProgramLockImageView.setVisibility(0);
        } else {
            this.mProgramLockImageView.setVisibility(4);
        }
        String programStartTime = this.mItemData.getMillsStartTime() > 0 ? EPGUtil.formatTime(this.mItemData.getMillsStartTime(), this.mContext) : "";
        MtkLog.d(TAG, "programStartTime=" + programStartTime);
        TextView textView = this.textContent;
        textView.setText(programStartTime + "   " + this.mItemData.getItemProgramName());
    }

    public void setViewNameEx(String name) {
        this.mNameTextView.setText(name);
    }
}
