package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvInputInfo;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class AVInput extends AbstractInput {
    private static final String TAG = "ISource^AVInput";
    private int portId;

    public int getPortId() {
        return this.portId;
    }

    public AVInput(TvInputInfo tvInputInfo) {
        super(tvInputInfo, 1001);
    }

    /* access modifiers changed from: protected */
    public void preInit(TvInputInfo tvInputInfo, int type) {
        this.portId = -1;
        if (tvInputInfo != null) {
            for (MtkTvInputSourceBase.InputSourceRecord record : mHardwareList) {
                String id = tvInputInfo.getId();
                if (id.contains("/HW" + record.getId())) {
                    this.portId = record.getInternalIdx() + 1;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isCurrentHardwareInfo(MtkTvInputSourceBase.InputSourceRecord record) {
        MtkLog.d(TAG, this.portId + " record.getId():" + record.getId() + " record.getInternalIdx():" + record.getInternalIdx() + " record.getInputType():" + record.getInputType());
        if (this.portId < 0) {
            if (this.mTvInputInfo != null) {
                String id = this.mTvInputInfo.getId();
                if (id.contains("/HW" + record.getId())) {
                    this.portId = record.getInternalIdx() + 1;
                    return true;
                }
            }
            return false;
        } else if (this.portId == record.getInternalIdx() + 1) {
            return true;
        } else {
            return false;
        }
    }

    public String getSourceName(Context context) {
        if (this.mTvInputInfo == null) {
            return "Composite";
        }
        String name = String.valueOf(this.mTvInputInfo.loadLabel(context));
        if (!InputUtil.isMultiAVInputs) {
            return name;
        }
        return name + " " + this.portId;
    }
}
