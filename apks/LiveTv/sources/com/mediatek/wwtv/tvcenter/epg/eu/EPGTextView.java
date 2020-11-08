package com.mediatek.wwtv.tvcenter.epg.eu;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;

public class EPGTextView extends TextView {
    private EPGProgramInfo tvProgramInfo;

    public EPGTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EPGTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EPGTextView(Context context) {
        super(context);
    }

    public EPGTextView(Context context, EPGProgramInfo mTVProgramInfo) {
        super(context);
        this.tvProgramInfo = mTVProgramInfo;
        setEllipsize(TextUtils.TruncateAt.END);
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
                    setBackgroundResource(R.drawable.epg_no_left_right_hi);
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
                setBackgroundResource(R.drawable.epg_no_left_right_hi);
            } else {
                setBackgroundResource(R.drawable.epg_no_left_right_normal);
            }
        } else if (selected) {
            setBackgroundResource(R.drawable.epg_left_right);
        } else {
            setBackgroundResource(R.drawable.epg_left_right_no);
        }
    }

    public void setProgramInfo(EPGProgramInfo tvProgramInfo2) {
        this.tvProgramInfo = tvProgramInfo2;
    }

    public String getShowTitle() {
        if (this.tvProgramInfo != null) {
            return this.tvProgramInfo.getTitle();
        }
        return null;
    }

    public int getMainTypeStr() {
        if (this.tvProgramInfo != null) {
            return this.tvProgramInfo.getMainType();
        }
        return -1;
    }

    public int getSubTypeStr() {
        if (this.tvProgramInfo != null) {
            return this.tvProgramInfo.getSubType();
        }
        return -1;
    }
}
