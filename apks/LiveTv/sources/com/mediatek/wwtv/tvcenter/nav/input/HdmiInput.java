package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.tv.TvInputInfo;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public final class HdmiInput extends AbstractInput {
    private static final String TAG = "ISource^HdmiInput";
    private int hdmiDevicePhysicalAddress = 0;
    private int portId;

    public int getPortId() {
        return this.portId;
    }

    public HdmiInput(TvInputInfo tvInputInfo) {
        super(tvInputInfo, 1007);
    }

    /* access modifiers changed from: protected */
    public void preInit(TvInputInfo tvInputInfo, int type) {
        MtkLog.d(TAG, "TvInputInfo." + tvInputInfo.getHdmiDeviceInfo());
        if (tvInputInfo.getHdmiDeviceInfo() != null) {
            this.hdmiDevicePhysicalAddress = tvInputInfo.getHdmiDeviceInfo().getPhysicalAddress();
            String parentIdString = tvInputInfo.getParentId();
            for (MtkTvInputSourceBase.InputSourceRecord record : mHardwareList) {
                if (parentIdString.contains("/HW" + record.getId())) {
                    this.portId = record.getInternalIdx() + 1;
                }
            }
            MtkLog.d(TAG, "TvInputInfo." + this.portId);
            return;
        }
        this.portId = -1;
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

    /* access modifiers changed from: protected */
    public int getHardwareId(MtkTvInputSourceBase.InputSourceRecord record) {
        return (record.getId() << 16) | this.hdmiDevicePhysicalAddress;
    }

    public boolean isCEC() {
        if (!isHDMI() || this.mTvInputInfo == null || this.mTvInputInfo.getHdmiDeviceInfo() == null) {
            return false;
        }
        return true;
    }

    public String getParentHDMISourceName(Context context) {
        if (!isCEC()) {
            return "";
        }
        TvInputInfo parentInfo = InputUtil.getTvInputManager().getTvInputInfo(this.mTvInputInfo.getParentId());
        String result = String.valueOf(parentInfo.loadCustomLabel(context));
        if (TextUtils.isEmpty(result) || TextUtils.equals(result, "null")) {
            return String.valueOf(parentInfo.loadLabel(context));
        }
        return result;
    }

    public String getSourceName(Context context) {
        if (this.mTvInputInfo == null) {
            return "WEI";
        }
        HdmiDeviceInfo mHdmiDeviceInfo = this.mTvInputInfo.getHdmiDeviceInfo();
        String name = "";
        if (mHdmiDeviceInfo != null) {
            name = mHdmiDeviceInfo.getDisplayName();
        }
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        String name2 = String.valueOf(this.mTvInputInfo.loadLabel(context));
        MtkLog.d(TAG, "loadLabel()=" + name2);
        return name2;
    }

    public boolean isHidden(Context context) {
        if (this.mTvInputInfo == null) {
            return false;
        }
        if (this.mTvInputInfo.isHidden(context) || isHiddenForParent(context)) {
            return true;
        }
        return false;
    }

    private boolean isHiddenForParent(Context context) {
        if (this.mTvInputInfo == null) {
            return false;
        }
        String parentId = this.mTvInputInfo.getParentId();
        if (TextUtils.isEmpty(parentId) || TextUtils.equals(parentId, "null") || InputUtil.getTvInputManager() == null) {
            return false;
        }
        for (TvInputInfo tvInputInfo : InputUtil.getTvInputManager().getTvInputList()) {
            if (tvInputInfo.getType() == 1007 && TextUtils.equals(parentId, tvInputInfo.getId())) {
                return tvInputInfo.isHidden(context);
            }
        }
        return false;
    }

    public String toString(Context context) {
        return "HDMI Id:" + getHardwareId() + ", portId=" + this.portId + ",TvInputInfo=" + getTvInputInfo() + ", State=" + getState() + ", isHidden=" + isHidden(context) + ", CustomSourceName=" + getCustomSourceName(context) + ", SourceName=" + getSourceName(context) + ", isBlock=" + isBlock();
    }
}
