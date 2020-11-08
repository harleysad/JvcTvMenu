package com.mediatek.wwtv.tvcenter.epg.sa;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.sa.db.DBMgrProgramList;
import com.mediatek.wwtv.tvcenter.epg.sa.db.EPGBookListViewDataItem;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Iterator;
import java.util.List;

public class EpgProgramItemView extends LinearLayout {
    private static String TAG = "EpgProgramItemView";
    private ImageView mBookIcon;
    private Context mContext;
    private EPGTextView mEPGTextView;
    private EPGProgramInfo tvProgramInfo;

    public EpgProgramItemView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public EpgProgramItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public EpgProgramItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public EpgProgramItemView(Context context, EPGProgramInfo mTVProgramInfo) {
        super(context);
        this.mContext = context;
        this.tvProgramInfo = mTVProgramInfo;
        init();
    }

    private void init() {
        LinearLayout lv = (LinearLayout) inflate(this.mContext, R.layout.epg_program_text_item_layout, (ViewGroup) null);
        addView(lv, new LinearLayout.LayoutParams(-1, -1));
        this.mBookIcon = (ImageView) lv.findViewById(R.id.epg_book_icon);
        this.mBookIcon.setVisibility(4);
        this.mEPGTextView = (EPGTextView) lv.findViewById(R.id.epg_textview);
        this.mEPGTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.mEPGTextView.setProgram(this.tvProgramInfo);
    }

    public void setBackground(boolean isDrawLeftIcon, boolean isDrawRightwardIcon, boolean selected, boolean isBgHighLight) {
        if (isBgHighLight) {
            if (!isDrawRightwardIcon || !isDrawLeftIcon) {
                if (isDrawLeftIcon) {
                    if (selected) {
                        setBackgroundResource(R.drawable.epg_left_hi);
                    } else {
                        setBackgroundResource(R.drawable.epg_img_pbb_cate_1_l_ar);
                    }
                } else if (isDrawRightwardIcon) {
                    if (selected) {
                        setBackgroundResource(R.drawable.epg_right_hi);
                    } else {
                        setBackgroundResource(R.drawable.epg_img_pbb_cate_1_r_ar);
                    }
                } else if (selected) {
                    setBackgroundResource(R.drawable.epg_list_item);
                } else {
                    setBackgroundResource(R.drawable.epg_img_pbb_cate_1);
                }
            } else if (selected) {
                setBackgroundResource(R.drawable.epg_left_right);
            } else {
                setBackgroundResource(R.drawable.epg_img_pbb_cate_1_lr_ar);
            }
        } else if (!isDrawRightwardIcon || !isDrawLeftIcon) {
            if (isDrawLeftIcon) {
                if (selected) {
                    setBackgroundResource(R.drawable.epg_left_hi);
                } else {
                    setBackgroundResource(R.drawable.epg_left_no);
                }
            } else if (isDrawRightwardIcon) {
                if (selected) {
                    setBackgroundResource(R.drawable.epg_right_hi);
                } else {
                    setBackgroundResource(R.drawable.epg_right_no);
                }
            } else if (selected) {
                setBackgroundResource(R.drawable.epg_list_item);
            } else {
                setBackgroundResource(R.drawable.epg_list_item_normal);
            }
        } else if (selected) {
            setBackgroundResource(R.drawable.epg_left_right);
        } else {
            setBackgroundResource(R.drawable.epg_left_right_no);
        }
    }

    public String getShowTitle() {
        return this.mEPGTextView.getShowTitle();
    }

    public void setText(String showTitle) {
        this.mEPGTextView.setText(showTitle);
    }

    public int getMainTypeStr() {
        return this.mEPGTextView.getMainTypeStr();
    }

    public int getSubTypeStr() {
        return this.mEPGTextView.getSubTypeStr();
    }

    public void setTextColor(int red) {
        this.mEPGTextView.setTextColor(red);
    }

    public void setBookVisibility() {
        if (this.tvProgramInfo != null) {
            DBMgrProgramList mDBMgrProgramList = DBMgrProgramList.getInstance(this.mContext);
            mDBMgrProgramList.getReadableDB();
            List<EPGBookListViewDataItem> mBookedList = mDBMgrProgramList.getProgramList();
            mDBMgrProgramList.closeDB();
            String str = TAG;
            MtkLog.d(str, "setBookVisibility>>>" + mBookedList.size());
            boolean hasFind = false;
            Iterator<EPGBookListViewDataItem> it = mBookedList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                EPGBookListViewDataItem tempInfo = it.next();
                if (tempInfo.mProgramStartTime == this.tvProgramInfo.getmStartTime().longValue() && tempInfo.mChannelId == this.tvProgramInfo.getChannelId() && tempInfo.mProgramId == this.tvProgramInfo.getProgramId()) {
                    hasFind = true;
                    break;
                }
            }
            if (hasFind) {
                this.mBookIcon.setVisibility(0);
            } else {
                this.mBookIcon.setVisibility(4);
            }
        }
    }
}
