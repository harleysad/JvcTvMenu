package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import java.io.Serializable;
import java.util.Arrays;

public class MtkTvChannelInfoBase implements Serializable {
    public static final int CUSTOM_DATA_LEN = 8;
    public static final int PRIVATE_DATA_LEN = 20;
    private static final String TAG = "MtkTvChannelInfo";
    protected int brdcstMedium;
    protected int brdcstType;
    protected int channelId;
    protected int channelNumber;
    protected byte[] customData = new byte[8];
    protected int frequency;
    protected int nwMask;
    protected int optionMask;
    protected byte[] privateData = new byte[20];
    protected String serviceName;
    protected int serviceType;
    protected int svlId;
    protected int svlRecId;

    private void channelInfoSetDefault() {
        this.nwMask = MtkTvChCommonBase.SB_VNET_ALL | MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_EPG | MtkTvChCommonBase.SB_VNET_VISIBLE;
        this.serviceType = MtkTvChCommonBase.SB_VNET_TV_SERVICE;
        this.frequency = 0;
        this.serviceType = 0;
    }

    public MtkTvChannelInfoBase() {
        channelInfoSetDefault();
    }

    public MtkTvChannelInfoBase(int svlId2, int svlRecId2) {
        channelInfoSetDefault();
        this.svlId = svlId2;
        this.svlRecId = svlRecId2;
    }

    public int getSvlId() {
        return this.svlId;
    }

    /* access modifiers changed from: protected */
    public void setSvlId(int svlId2) {
        this.svlId = svlId2;
    }

    public int getSvlRecId() {
        return this.svlRecId;
    }

    /* access modifiers changed from: protected */
    public void setSvlRecId(int svlRecId2) {
        this.svlRecId = svlRecId2;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void _setChannelNumberByChannelID(int chId) {
        this.channelNumber = MtkTvChCommonBase.SB_ATSC_GET_MAJOR_CHANNEL_NUM(chId);
    }

    public void _setChannelId(int chId) {
        this.channelId = chId;
    }

    public void setChannelId(int chId) {
        _setChannelId(chId);
        _setChannelNumberByChannelID(chId);
    }

    public int getBrdcstType() {
        return this.brdcstType;
    }

    public void setBrdcstType(int brdcstType2) {
        this.brdcstType = brdcstType2;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void _setFrequency(int frequency2) {
        this.frequency = frequency2;
    }

    public void setFrequency(int frequency2) {
        _setFrequency(frequency2);
        setFrequencyEdited(true);
    }

    public int getBrdcstMedium() {
        return this.brdcstMedium;
    }

    public void setBrdcstMedium(int brdcstMedium2) {
        this.brdcstMedium = brdcstMedium2;
    }

    public int getNwMask() {
        return this.nwMask;
    }

    public void setNwMask(int nwMask2) {
        this.nwMask = nwMask2;
    }

    public int getOptionMask() {
        return this.optionMask;
    }

    public void setOptionMask(int optionMask2) {
        this.optionMask = optionMask2;
    }

    public int getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(int serviceType2) {
        this.serviceType = serviceType2;
    }

    public int getChannelNumber() {
        return this.channelNumber;
    }

    public void _setChannelNumber(int channelNumber2) {
        this.channelNumber = channelNumber2;
    }

    public void setChannelNumber(int channelNumber2) {
        _setChannelNumber(channelNumber2);
        setChannelNumberEdited(true);
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void _setServiceName(String serviceName2) {
        this.serviceName = serviceName2;
    }

    public void setServiceName(String serviceName2) {
        _setServiceName(serviceName2);
        setChannelNameEdited(true);
    }

    public byte[] getCustomData() {
        return this.customData;
    }

    public void setCustomData(byte[] _customData) {
        if (_customData == null) {
            Log.d(TAG, "setCustomData fail!\n");
        } else if (_customData.length > 8) {
            Log.d(TAG, "setCustomData fail because data length more than 8!\n");
        } else {
            System.arraycopy(_customData, 0, this.customData, 0, _customData.length);
        }
    }

    public byte[] getPrivateData() {
        return this.privateData;
    }

    public void setPrivateData(byte[] _privateData) {
        if (_privateData == null) {
            Log.d(TAG, "setPrivateData fail!\n");
        } else if (_privateData.length > 20) {
            Log.d(TAG, "setPrivateData fail because data length more than 20!\n");
        } else {
            System.arraycopy(_privateData, 0, this.privateData, 0, _privateData.length);
        }
    }

    public boolean isUserDelete() {
        return (this.optionMask & MtkTvChCommonBase.SB_VOPT_DELETED_BY_USER) > 0;
    }

    public boolean isVisible() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_VISIBLE) > 0;
    }

    public boolean isEpgVisible() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_EPG) > 0;
    }

    public boolean isRadioService() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_RADIO_SERVICE) > 0;
    }

    public boolean isAnalogService() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE) > 0;
    }

    public boolean isTvService() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_TV_SERVICE) > 0;
    }

    public boolean isDigitalFavorites1Service() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_FAVORITE1) > 0;
    }

    public boolean isDigitalFavorites2Service() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_FAVORITE2) > 0;
    }

    public boolean isDigitalFavorites3Service() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_FAVORITE3) > 0;
    }

    public boolean isDigitalFavorites4Service() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_FAVORITE4) > 0;
    }

    public boolean isDigitalFavoritesService() {
        return isDigitalFavorites1Service() || isDigitalFavorites2Service() || isDigitalFavorites3Service() || isDigitalFavorites4Service();
    }

    public void setDigitalFavoritesMask(boolean bAdd, int favoritesIndex) {
        if (bAdd) {
            switch (favoritesIndex) {
                case 0:
                    this.nwMask |= MtkTvChCommonBase.SB_VNET_FAVORITE1;
                    return;
                case 1:
                    this.nwMask |= MtkTvChCommonBase.SB_VNET_FAVORITE2;
                    return;
                case 2:
                    this.nwMask |= MtkTvChCommonBase.SB_VNET_FAVORITE3;
                    return;
                case 3:
                    this.nwMask |= MtkTvChCommonBase.SB_VNET_FAVORITE4;
                    return;
                default:
                    Log.e(TAG, "Invalid favoritesIndex(" + favoritesIndex + ")!\n");
                    return;
            }
        } else {
            switch (favoritesIndex) {
                case 0:
                    this.nwMask &= ~MtkTvChCommonBase.SB_VNET_FAVORITE1;
                    return;
                case 1:
                    this.nwMask &= ~MtkTvChCommonBase.SB_VNET_FAVORITE2;
                    return;
                case 2:
                    this.nwMask &= ~MtkTvChCommonBase.SB_VNET_FAVORITE3;
                    return;
                case 3:
                    this.nwMask &= ~MtkTvChCommonBase.SB_VNET_FAVORITE4;
                    return;
                default:
                    Log.e(TAG, "Invalid favoritesIndex(" + favoritesIndex + ")!\n");
                    return;
            }
        }
    }

    public void setChannelNameEdited(boolean flag) {
        if (flag) {
            this.nwMask |= MtkTvChCommonBase.SB_VNET_CH_NAME_EDITED;
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_CH_NAME_EDITED;
            return;
        }
        this.nwMask &= ~MtkTvChCommonBase.SB_VNET_CH_NAME_EDITED;
        this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_CH_NAME_EDITED;
    }

    public void setFrequencyEdited(boolean flag) {
        if (flag) {
            this.nwMask |= MtkTvChCommonBase.SB_VNET_FREQ_EDITED;
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_FREQ_EDITED;
            return;
        }
        this.nwMask &= ~MtkTvChCommonBase.SB_VNET_FREQ_EDITED;
        this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_FREQ_EDITED;
    }

    public void setChannelNumberEdited(boolean flag) {
        if (flag) {
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_CH_NUM_EDITED;
        } else {
            this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_CH_NUM_EDITED;
        }
    }

    public void setChannelDeleted(boolean flag) {
        if (flag) {
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_DELETED_BY_USER;
        } else {
            this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_DELETED_BY_USER;
        }
    }

    public void setSkip(boolean skip) {
        if (skip) {
            this.nwMask &= ~MtkTvChCommonBase.SB_VNET_VISIBLE;
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_MODIFIED_VISIBLE;
            return;
        }
        this.nwMask |= MtkTvChCommonBase.SB_VNET_VISIBLE;
        this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_MODIFIED_VISIBLE;
    }

    public boolean isSkip() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_VISIBLE) <= 0;
    }

    public boolean isNumberSelectable() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_NUMERIC_SELECTABLE) > 0;
    }

    public void setBlock(boolean block) {
        if (block) {
            this.nwMask |= MtkTvChCommonBase.SB_VNET_BLOCKED;
        } else {
            this.nwMask &= ~MtkTvChCommonBase.SB_VNET_BLOCKED;
        }
    }

    public boolean isBlock() {
        return (this.nwMask & MtkTvChCommonBase.SB_VNET_BLOCKED) > 0;
    }

    public void setUserTmpLock(boolean lock) {
        if (lock) {
            this.optionMask |= MtkTvChCommonBase.SB_VOPT_USER_TMP_UNLOCK;
        } else {
            this.optionMask &= ~MtkTvChCommonBase.SB_VOPT_USER_TMP_UNLOCK;
        }
    }

    public boolean isUserTmpLock() {
        return (this.optionMask & MtkTvChCommonBase.SB_VOPT_USER_TMP_UNLOCK) > 0;
    }

    public String toString() {
        return "MtkTvChannelInfo      [svlId=" + this.svlId + " , svlRecId=" + this.svlRecId + " , channelId=" + this.channelId + " , brdcstType=" + BrdcstTypeToString(this.brdcstType) + " , nwMask=" + this.nwMask + " , optionMask=" + this.optionMask + " , serviceType=" + this.serviceType + " , channelNumber=" + this.channelNumber + " , serviceName=" + this.serviceName + " , privateData=" + Arrays.toString(this.privateData) + " , customData=" + Arrays.toString(this.customData) + " , frequency=" + this.frequency + " , brdcstMedium=" + this.brdcstMedium + "]\n";
    }

    public int hashCode() {
        return (31 * ((31 * 1) + this.channelId)) + this.svlId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MtkTvChannelInfoBase other = (MtkTvChannelInfoBase) obj;
        if (this.channelId == other.channelId && this.svlId == other.svlId && this.svlRecId == other.svlRecId) {
            return true;
        }
        return false;
    }

    public String BrdcstTypeToString(int mType) {
        switch (mType) {
            case 0:
                return " BRDCST_TYPE_UNKNOWN ";
            case 1:
                return " BRDCST_TYPE_ANALOG  ";
            case 2:
                return " BRDCST_TYPE_DVB     ";
            case 3:
                return " BRDCST_TYPE_ATSC    ";
            case 4:
                return " BRDCST_TYPE_SCTE    ";
            case 5:
                return " BRDCST_TYPE_ISDB    ";
            case 6:
                return " BRDCST_TYPE_FMRDO   ";
            case 7:
                return " BRDCST_TYPE_DTMB    ";
            case 8:
                return " BRDCST_TYPE_MHP     ";
            default:
                return " Undefined_TYPE      ";
        }
    }
}
