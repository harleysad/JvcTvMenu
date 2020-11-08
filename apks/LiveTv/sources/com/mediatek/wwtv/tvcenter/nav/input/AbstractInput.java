package com.mediatek.wwtv.tvcenter.nav.input;

import android.content.Context;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvView;
import android.net.Uri;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class AbstractInput implements ISource, Comparable<AbstractInput> {
    private static final String TAG = "ISource^AbstractInput";
    private static final List<ConflictInputInfo> mConflictList = new ArrayList();
    protected static final List<MtkTvInputSourceBase.InputSourceRecord> mHardwareList = new ArrayList();
    private static final int mSize = mTvInputSource.getInputSourceTotalNumber();
    protected static final MtkTvInputSource mTvInputSource = MtkTvInputSource.getInstance();
    protected int mHardwareId;
    protected int mInputState = 0;
    protected TvInputInfo mTvInputInfo;
    private int mType;

    public AbstractInput(TvInputInfo tvInputInfo, int type) {
        preInit(tvInputInfo, type);
        init(tvInputInfo, type);
    }

    /* access modifiers changed from: protected */
    public void preInit(TvInputInfo tvInputInfo, int type) {
    }

    /* access modifiers changed from: protected */
    public void init(TvInputInfo tvInputInfo, int type) {
        MtkTvInputSourceBase.InputSourceRecord record;
        this.mTvInputInfo = tvInputInfo;
        this.mType = type;
        if (this.mTvInputInfo != null && this.mTvInputInfo.getType() >= 1001 && this.mTvInputInfo.getType() <= 1007) {
            this.mType = this.mTvInputInfo.getType();
        }
        if (this.mTvInputInfo != null) {
            this.mInputState = InputUtil.mTvInputManager.getInputState(this.mTvInputInfo.getId());
        }
        int isJump = 0;
        for (int i = 0; i < mSize; i++) {
            if (mHardwareList.size() <= i) {
                record = new MtkTvInputSourceBase.InputSourceRecord();
                mTvInputSource.getInputSourceRecbyidx(i, record);
                mHardwareList.add(record);
            } else {
                record = mHardwareList.get(i);
            }
            if (record != null) {
                int i2 = this.mType;
                if (i2 != 0) {
                    if (i2 != 1001) {
                        if (i2 != 1007) {
                            if (i2 != 10000) {
                                if (i2 != 20000) {
                                    switch (i2) {
                                        case 1003:
                                            if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.SCART) {
                                                this.mHardwareId = getHardwareId(record);
                                                isJump = 1;
                                                break;
                                            }
                                            break;
                                        case 1004:
                                            if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.COMPONENT) {
                                                this.mHardwareId = getHardwareId(record);
                                                isJump = 1;
                                                break;
                                            }
                                            break;
                                        case 1005:
                                            if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.VGA) {
                                                this.mHardwareId = getHardwareId(record);
                                                isJump = 1;
                                                break;
                                            }
                                            break;
                                    }
                                } else if (mTvInputSource.getInputSourceNamebySourceid(record.getId()).equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_DTV)) {
                                    this.mTvInputInfo = null;
                                    this.mHardwareId = getHardwareId(record);
                                    isJump = 1;
                                }
                            } else if (mTvInputSource.getInputSourceNamebySourceid(record.getId()).equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_ATV)) {
                                this.mTvInputInfo = null;
                                this.mHardwareId = getHardwareId(record);
                                isJump = 1;
                            }
                        } else if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.HDMI && isCurrentHardwareInfo(record)) {
                            this.mHardwareId = getHardwareId(record);
                            isJump = 1;
                        }
                    } else if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.COMPOSITE && isCurrentHardwareInfo(record)) {
                        this.mHardwareId = getHardwareId(record);
                        isJump = 1;
                    }
                } else if (record.getInputType() == MtkTvInputSourceBase.InputDeviceType.TV) {
                    this.mTvInputInfo = null;
                    this.mHardwareId = getHardwareId(record);
                    isJump = 1;
                }
                if (isJump > 0) {
                    return;
                }
            }
        }
    }

    public void updateState(int state) {
        this.mInputState = state;
    }

    /* access modifiers changed from: protected */
    public boolean isCurrentHardwareInfo(MtkTvInputSourceBase.InputSourceRecord record) {
        return false;
    }

    /* access modifiers changed from: protected */
    public int getHardwareId(MtkTvInputSourceBase.InputSourceRecord record) {
        return record.getId() << 16;
    }

    public int getHardwareId() {
        return this.mHardwareId;
    }

    public String getId() {
        if (this.mTvInputInfo != null) {
            return this.mTvInputInfo.getId();
        }
        MtkLog.d(TAG, "getId failed! " + toString());
        return "<INVALID>";
    }

    public int getType() {
        return this.mType;
    }

    public boolean isHidden(Context context) {
        if (this.mTvInputInfo != null) {
            return this.mTvInputInfo.isHidden(context);
        }
        return false;
    }

    public int getState() {
        return this.mInputState;
    }

    public boolean getConflict(ISource source) {
        if (source.getHardwareId() == getHardwareId()) {
            if (!MarketRegionInfo.isFunctionSupport(32) || (getType() != 0 && getType() != 20000 && getType() != 10000)) {
                return true;
            }
            return false;
        } else if (source.getType() == getType()) {
            return true;
        } else {
            for (ConflictInputInfo info : mConflictList) {
                if (info.isMap(source.getHardwareId(), getHardwareId())) {
                    return info.getConflictInfo();
                }
            }
            ConflictInputInfo crnt = new ConflictInputInfo(source.getHardwareId(), getHardwareId(), mTvInputSource.queryConflict(source.getHardwareId() >> 16, getHardwareId() >> 16));
            mConflictList.add(crnt);
            return crnt.getConflictInfo();
        }
    }

    public String getSourceName(Context context) {
        if (this.mTvInputInfo != null) {
            return String.valueOf(this.mTvInputInfo.loadLabel(context));
        }
        return "WEI";
    }

    public String getCustomSourceName(Context context) {
        if (this.mTvInputInfo != null) {
            return String.valueOf(this.mTvInputInfo.loadCustomLabel(context));
        }
        return "WEI";
    }

    public int tune(TvView tvView) {
        if (tvView == null) {
            MtkLog.d(TAG, "tune, tvView is null!");
            return -1;
        } else if (this.mTvInputInfo != null) {
            tvView.tune(this.mTvInputInfo.getId(), TvContract.buildChannelUriForPassthroughInput(this.mTvInputInfo.getId()));
            return 0;
        } else {
            MtkLog.d(TAG, "tune, mTvInputInfo is null!");
            return -1;
        }
    }

    public int tune(TvView tvView, String sourceId, Uri channelId) {
        if (tvView != null) {
            tvView.tune(sourceId, channelId);
            return 0;
        }
        MtkLog.d(TAG, "tune, tvView is null!");
        return -1;
    }

    public TvInputInfo getTvInputInfo() {
        return this.mTvInputInfo;
    }

    public TvInputInfo getTvInputInfo(long mId) {
        return null;
    }

    public boolean isBlock() {
        return mTvInputSource.isBlock(getHardwareId() >> 16);
    }

    public boolean isBlockEx() {
        return mTvInputSource.isBlockEx(getHardwareId() >> 16);
    }

    public boolean isCurrentBlock() {
        if (getType() == 0 || getType() == 20000 || getType() == 10000) {
            return mTvInputSource.checkIsMenuTvBlock();
        }
        return mTvInputSource.isBlockEx(getHardwareId() >> 16);
    }

    public int block(boolean block) {
        return mTvInputSource.block(getHardwareId() >> 16, block);
    }

    public int compareTo(AbstractInput another) {
        return this.mHardwareId - another.getHardwareId();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AbstractInput) || this.mHardwareId != ((AbstractInput) o).mHardwareId) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mHardwareId;
    }

    public boolean isNeedAbortTune() {
        return false;
    }

    public boolean isTV() {
        return getType() == 0;
    }

    public boolean isATV() {
        return getType() == 10000;
    }

    public boolean isDTV() {
        return getType() == 20000;
    }

    public boolean isVGA() {
        return getType() == 1005;
    }

    public boolean isHDMI() {
        return getType() == 1007;
    }

    public boolean isComponent() {
        return getType() == 1004;
    }

    public boolean isComposite() {
        return getType() == 1001;
    }

    public String toString(Context context) {
        return "Source:" + getType() + " Id:" + getHardwareId() + ",TvInputInfo=" + getTvInputInfo() + ", State=" + getState() + ", isHidden=" + isHidden(context) + ", CustomSourceName=" + getCustomSourceName(context) + ", SourceName=" + getSourceName(context) + ", isBlock=" + isBlock();
    }

    private class ConflictInputInfo {
        protected boolean mConflict = false;
        protected int mhardwareId1 = -1;
        protected int mhardwareId2 = -1;

        public ConflictInputInfo(int id1, int id2, boolean conflict) {
            this.mhardwareId1 = id1;
            this.mhardwareId2 = id2;
            this.mConflict = conflict;
        }

        public boolean isMap(int id1, int id2) {
            return (id1 == this.mhardwareId1 && id2 == this.mhardwareId2) || (id1 == this.mhardwareId2 && id2 == this.mhardwareId1);
        }

        public boolean getConflictInfo() {
            return this.mConflict;
        }
    }
}
