package com.mediatek.wwtv.tvcenter.util.tif;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.support.media.tv.TvContractCompat;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TIFChannelInfo implements Serializable {
    public static final int APP_LINK_TYPE_APP = 2;
    public static final int APP_LINK_TYPE_CHANNEL = 1;
    public static final int APP_LINK_TYPE_NONE = -1;
    private static final int APP_LINK_TYPE_NOT_SET = 0;
    public static final int LOAD_IMAGE_TYPE_APP_LINK_ICON = 2;
    public static final int LOAD_IMAGE_TYPE_APP_LINK_POSTER_ART = 3;
    public static final int LOAD_IMAGE_TYPE_CHANNEL_LOGO = 1;
    private static final String TAG = "TIFChannelInfo";
    public int mAppLinkColor;
    public String mAppLinkIconUri;
    private Intent mAppLinkIntent;
    public String mAppLinkIntentUri;
    public String mAppLinkPosterArtUri;
    public String mAppLinkText;
    private int mAppLinkType;
    public String mData;
    public long[] mDataValue;
    public String mDescription;
    public String mDisplayName;
    public String mDisplayNumber;
    public long mId;
    public String mInputServiceName;
    public int mInternalProviderFlag1;
    public int mInternalProviderFlag2;
    public int mInternalProviderFlag3;
    public int mInternalProviderFlag4;
    public boolean mIsBrowsable;
    public boolean mLocked;
    public MtkTvChannelInfoBase mMtkTvChannelInfo;
    public String mNetworkAffiliation;
    public int mOriginalNetworkId;
    public String mPackageName = "invalid package name";
    public boolean mSearchable;
    public int mServiceId;
    public String mServiceType;
    public int mTransportStreamId;
    public String mType;
    public int mVersionNumber;
    public String mVideoFormat;

    public String toString() {
        return "[TIFChannelInfo] mId:" + this.mId + ",  mInputServiceName:" + this.mInputServiceName + ",  mType:" + this.mType + ",  mServiceType:" + this.mServiceType + ",  mOriginalNetworkId:" + this.mOriginalNetworkId + ",  mTransportStreamId:" + this.mTransportStreamId + ",  mServiceId:" + this.mServiceId + ",  mDisplayNumber:" + this.mDisplayNumber + ",  mDisplayName:" + this.mDisplayName + ",  mNetworkAffiliation:" + this.mNetworkAffiliation + ",  mDescription:" + this.mDescription + ",  mVideoFormat:" + this.mVideoFormat + ",  mIsBrowsable:" + this.mIsBrowsable + ",  mSearchable:" + this.mSearchable + ",  mLocked:" + this.mLocked + ",  mVersionNumber:" + this.mVersionNumber + ",  mAppLinkIconUri:" + this.mAppLinkIconUri + ",  mAppLinkPosterArtUri:" + this.mAppLinkPosterArtUri + ",  mAppLinkText:" + this.mAppLinkText + ",  mAppLinkColor:" + this.mAppLinkColor + ",  mAppLinkIntentUri:" + this.mAppLinkIntentUri + ",  mAppLinkIntent:" + this.mAppLinkIntent + ",  mInternalProviderFlag1:" + this.mInternalProviderFlag1 + ",  mInternalProviderFlag2:" + this.mInternalProviderFlag2 + ",  mInternalProviderFlag3:" + this.mInternalProviderFlag3 + ",  mInternalProviderFlag4:" + this.mInternalProviderFlag4 + ",  mData:" + this.mData;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TIFChannelInfo) || ((TIFChannelInfo) o).mId != this.mId) {
            return super.equals(o);
        }
        return true;
    }

    public int getAppLinkType(Context context) {
        if (this.mAppLinkType == 0) {
            initAppLinkTypeAndIntent(context);
        }
        return this.mAppLinkType;
    }

    public Intent getAppLinkIntent(Context context) {
        if (this.mAppLinkType == 0) {
            initAppLinkTypeAndIntent(context);
        }
        return this.mAppLinkIntent;
    }

    private void initAppLinkTypeAndIntent(Context context) {
        this.mAppLinkType = -1;
        this.mAppLinkIntent = null;
        PackageManager pm = context.getPackageManager();
        if (!TextUtils.isEmpty(this.mAppLinkText) && !TextUtils.isEmpty(this.mAppLinkIntentUri)) {
            try {
                Intent intent = Intent.parseUri(this.mAppLinkIntentUri, 1);
                if (intent.resolveActivityInfo(pm, 0) != null) {
                    this.mAppLinkIntent = intent;
                    this.mAppLinkIntent.putExtra("app_link_channel_uri", getUri().toString());
                    this.mAppLinkType = 1;
                    return;
                }
            } catch (URISyntaxException e) {
            }
        }
        if (!this.mPackageName.equals(context.getApplicationContext().getPackageName())) {
            this.mAppLinkIntent = pm.getLeanbackLaunchIntentForPackage(this.mPackageName);
            if (this.mAppLinkIntent != null) {
                this.mAppLinkIntent.putExtra("app_link_channel_uri", getUri().toString());
                this.mAppLinkType = 2;
            }
        }
    }

    public Uri getUri() {
        return TvContract.buildChannelUriForPassthroughInput(this.mInputServiceName);
    }

    public long getId() {
        return this.mId;
    }

    public boolean isBrowsable() {
        return this.mIsBrowsable;
    }

    public String getDisplayNumber() {
        return this.mDisplayNumber;
    }

    public String getImageUriString(int type) {
        switch (type) {
            case 1:
                return TvContract.buildChannelLogoUri(this.mId).toString();
            case 2:
                return this.mAppLinkIconUri;
            case 3:
                return this.mAppLinkPosterArtUri;
            default:
                return null;
        }
    }

    public boolean hasSameReadOnlyInfo(TIFChannelInfo other) {
        return other != null && Objects.equals(Long.valueOf(this.mId), Long.valueOf(other.mId)) && Objects.equals(this.mPackageName, other.mPackageName) && Objects.equals(this.mInputServiceName, other.mInputServiceName) && Objects.equals(this.mType, other.mType) && Objects.equals(this.mDisplayNumber, other.mDisplayNumber) && Objects.equals(this.mDisplayName, other.mDisplayName) && Objects.equals(this.mDescription, other.mDescription) && Objects.equals(this.mVideoFormat, other.mVideoFormat) && Objects.equals(this.mAppLinkText, other.mAppLinkText) && this.mAppLinkColor == other.mAppLinkColor && Objects.equals(this.mAppLinkIconUri, other.mAppLinkIconUri) && Objects.equals(this.mAppLinkPosterArtUri, other.mAppLinkPosterArtUri) && Objects.equals(this.mAppLinkIntentUri, other.mAppLinkIntentUri);
    }

    /* access modifiers changed from: package-private */
    public void copyFrom(TIFChannelInfo other) {
        if (this != other) {
            this.mId = other.mId;
            this.mPackageName = other.mPackageName;
            this.mInputServiceName = other.mInputServiceName;
            this.mType = other.mType;
            this.mServiceType = other.mServiceType;
            this.mOriginalNetworkId = other.mOriginalNetworkId;
            this.mTransportStreamId = other.mTransportStreamId;
            this.mServiceId = other.mServiceId;
            this.mDisplayNumber = other.mDisplayNumber;
            this.mDisplayName = other.mDisplayName;
            this.mNetworkAffiliation = other.mNetworkAffiliation;
            this.mDescription = other.mDescription;
            this.mVideoFormat = other.mVideoFormat;
            this.mIsBrowsable = other.mIsBrowsable;
            this.mSearchable = other.mSearchable;
            this.mLocked = other.mLocked;
            this.mVersionNumber = other.mVersionNumber;
            this.mAppLinkIconUri = other.mAppLinkIconUri;
            this.mAppLinkPosterArtUri = other.mAppLinkPosterArtUri;
            this.mAppLinkText = other.mAppLinkText;
            this.mAppLinkColor = other.mAppLinkColor;
            this.mAppLinkIntentUri = other.mAppLinkIntentUri;
            this.mInternalProviderFlag1 = other.mInternalProviderFlag1;
            this.mInternalProviderFlag2 = other.mInternalProviderFlag2;
            this.mInternalProviderFlag3 = other.mInternalProviderFlag3;
            this.mInternalProviderFlag4 = other.mInternalProviderFlag4;
            this.mData = other.mData;
        }
    }

    public static TIFChannelInfo parse(Cursor c) {
        TIFChannelInfo info = new TIFChannelInfo();
        parse(info, c);
        return info;
    }

    public static void parse(TIFChannelInfo temTIFChannel, Cursor c) {
        if (temTIFChannel != null && c != null) {
            temTIFChannel.mId = c.getLong(c.getColumnIndex("_id"));
            temTIFChannel.mPackageName = c.getString(c.getColumnIndex(TvContractCompat.BaseTvColumns.COLUMN_PACKAGE_NAME));
            temTIFChannel.mInputServiceName = c.getString(c.getColumnIndex("input_id"));
            temTIFChannel.mType = c.getString(c.getColumnIndex("type"));
            temTIFChannel.mServiceType = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_SERVICE_TYPE));
            temTIFChannel.mOriginalNetworkId = c.getInt(c.getColumnIndex(TvContractCompat.Channels.COLUMN_ORIGINAL_NETWORK_ID));
            temTIFChannel.mTransportStreamId = c.getInt(c.getColumnIndex(TvContractCompat.Channels.COLUMN_TRANSPORT_STREAM_ID));
            temTIFChannel.mServiceId = c.getInt(c.getColumnIndex(TvContractCompat.Channels.COLUMN_SERVICE_ID));
            if (CommonIntegration.getInstance().isCurrentSourceATVforEuPA()) {
                temTIFChannel.mDisplayNumber = "" + CommonIntegration.getInstance().getAnalogChannelDisplayNumInt(c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_DISPLAY_NUMBER)));
            } else {
                temTIFChannel.mDisplayNumber = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_DISPLAY_NUMBER));
            }
            temTIFChannel.mDisplayName = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_DISPLAY_NAME));
            if (temTIFChannel.mDisplayName == null) {
                temTIFChannel.mDisplayName = "";
            } else {
                temTIFChannel.mDisplayName = TvSingletons.getSingletons().getCommonIntegration().getAvailableString(temTIFChannel.mDisplayName);
            }
            temTIFChannel.mNetworkAffiliation = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_NETWORK_AFFILIATION));
            temTIFChannel.mDescription = c.getString(c.getColumnIndex("description"));
            temTIFChannel.mVideoFormat = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_VIDEO_FORMAT));
            if (c.getInt(c.getColumnIndex("browsable")) == 1) {
                temTIFChannel.mIsBrowsable = true;
            } else {
                temTIFChannel.mIsBrowsable = false;
            }
            if (c.getInt(c.getColumnIndex("searchable")) == 1) {
                temTIFChannel.mSearchable = true;
            } else {
                temTIFChannel.mSearchable = false;
            }
            if (c.getInt(c.getColumnIndex(TvContractCompat.Channels.COLUMN_LOCKED)) == 1) {
                temTIFChannel.mLocked = true;
            } else {
                temTIFChannel.mLocked = false;
            }
            temTIFChannel.mVersionNumber = c.getInt(c.getColumnIndex("version_number"));
            if (c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_ICON_URI) > -1) {
                temTIFChannel.mAppLinkIconUri = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_ICON_URI));
                temTIFChannel.mAppLinkPosterArtUri = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_POSTER_ART_URI));
                temTIFChannel.mAppLinkText = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_TEXT));
                temTIFChannel.mAppLinkColor = c.getInt(c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_COLOR));
                temTIFChannel.mAppLinkIntentUri = c.getString(c.getColumnIndex(TvContractCompat.Channels.COLUMN_APP_LINK_INTENT_URI));
                temTIFChannel.mInternalProviderFlag1 = c.getInt(c.getColumnIndex("internal_provider_flag1"));
                temTIFChannel.mInternalProviderFlag2 = c.getInt(c.getColumnIndex("internal_provider_flag2"));
                temTIFChannel.mInternalProviderFlag3 = c.getInt(c.getColumnIndex("internal_provider_flag3"));
                temTIFChannel.mInternalProviderFlag4 = c.getInt(c.getColumnIndex("internal_provider_flag4"));
            }
            try {
                temTIFChannel.mData = new String(c.getBlob(c.getColumnIndex("internal_provider_data")));
                parserTIFChannelData(temTIFChannel, temTIFChannel.mData);
            } catch (Exception e) {
            }
        }
    }

    public static void parserTIFChannelData(TIFChannelInfo temTIFChannel, String data) {
        String str = data;
        MtkLog.d(TAG, "data:" + str);
        if (str != null) {
            String[] value = str.split(",");
            if (value.length == 9 || value.length == 6) {
                long[] v = new long[value.length];
                long mSvlId = Long.parseLong(value[1]);
                long mSvlRecId = Long.parseLong(value[2]);
                long channelId = Long.parseLong(value[3]);
                long j = (mSvlId << 16) + mSvlRecId;
                v[0] = mSvlId;
                v[1] = mSvlRecId;
                v[2] = channelId;
                v[4] = (mSvlId << 16) + mSvlRecId;
                int i = 5;
                while (i < value.length) {
                    v[i] = (long) Integer.parseInt(value[i]);
                    i++;
                    value = value;
                    String str2 = data;
                }
                temTIFChannel.mDataValue = v;
                return;
            }
            MtkLog.d(TAG, "parserTIFChannelData data.length != 6 or 9");
        }
    }

    public boolean isUserDelete() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[7] & ((long) MtkTvChCommonBase.SB_VOPT_DELETED_BY_USER)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isVisible() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_VISIBLE)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isEpgVisible() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_EPG)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isRadioService() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_RADIO_SERVICE)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isAnalogService() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isTvService() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_TV_SERVICE)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDigitalFavorites1Service() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_FAVORITE1)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDigitalFavorites2Service() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_FAVORITE2)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDigitalFavorites3Service() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_FAVORITE3)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDigitalFavorites4Service() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_FAVORITE4)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isDigitalFavoritesService() {
        return isDigitalFavorites1Service() || isDigitalFavorites2Service() || isDigitalFavorites3Service() || isDigitalFavorites4Service();
    }

    public boolean isSkip() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_VISIBLE)) > 0) {
            return false;
        }
        return true;
    }

    public boolean isNumberSelectable() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_NUMERIC_SELECTABLE)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isBlock() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[6] & ((long) MtkTvChCommonBase.SB_VNET_BLOCKED)) <= 0) {
            return false;
        }
        return true;
    }

    public boolean isUserTmpLock() {
        if (this.mDataValue == null || this.mDataValue.length != 9 || (this.mDataValue[7] & ((long) MtkTvChCommonBase.SB_VOPT_USER_TMP_UNLOCK)) <= 0) {
            return false;
        }
        return true;
    }

    private static void printProviderInfo(TIFChannelInfo temTIFChannel) {
        Log.d(TAG, "parserTIFRowChannelInfo[temTIFChannel.mId:" + temTIFChannel.mId + "  temTIFChannel.mInputServiceName:" + temTIFChannel.mInputServiceName + "  temTIFChannel.mType:" + temTIFChannel.mType + "  temTIFChannel.mServiceType:" + temTIFChannel.mServiceType + "  temTIFChannel.mOriginalNetworkId:" + temTIFChannel.mOriginalNetworkId + "  temTIFChannel.mTransportStreamId:" + temTIFChannel.mTransportStreamId + "  temTIFChannel.mServiceId:" + temTIFChannel.mServiceId + "  temTIFChannel.mDisplayNumber:" + temTIFChannel.mDisplayNumber + "  temTIFChannel.mDisplayName:" + temTIFChannel.mDisplayName + "  temTIFChannel.mNetworkAffiliation:" + temTIFChannel.mNetworkAffiliation + "  temTIFChannel.mDescription:" + temTIFChannel.mDescription + "  temTIFChannel.mVideoFormat:" + temTIFChannel.mVideoFormat + "  temTIFChannel.mIsBrowsable:" + temTIFChannel.mIsBrowsable + "  temTIFChannel.mSearchable:" + temTIFChannel.mSearchable + "  temTIFChannel.mLocked:" + temTIFChannel.mLocked + "  temTIFChannel.mVersionNumber:" + temTIFChannel.mVersionNumber + "  temTIFChannel.mData:" + temTIFChannel.mData);
    }

    public static class DefaultComparator implements Comparator<TIFChannelInfo> {
        private final Context mContext;
        private boolean mDetectDuplicatesEnabled;
        private final Map<String, String> mInputIdToLabelMap = new HashMap();
        private final TvInputManagerHelper mInputManager;

        public DefaultComparator(Context context, TvInputManagerHelper inputManager) {
            this.mContext = context;
            this.mInputManager = inputManager;
        }

        public void setDetectDuplicatesEnabled(boolean detectDuplicatesEnabled) {
            this.mDetectDuplicatesEnabled = detectDuplicatesEnabled;
        }

        public int compare(TIFChannelInfo lhs, TIFChannelInfo rhs) {
            if (lhs == rhs) {
                return 0;
            }
            boolean lhsIsPartner = this.mInputManager.isPartnerInput(lhs.mInputServiceName);
            int result = 1;
            if (lhsIsPartner == this.mInputManager.isPartnerInput(rhs.mInputServiceName)) {
                String lhsLabel = getInputLabelForChannel(lhs);
                String rhsLabel = getInputLabelForChannel(rhs);
                if (lhsLabel != null) {
                    result = rhsLabel == null ? -1 : lhsLabel.compareTo(rhsLabel);
                } else if (rhsLabel == null) {
                    result = 0;
                }
                if (result != 0) {
                    return result;
                }
                int result2 = lhs.mInputServiceName.compareTo(rhs.mInputServiceName);
                if (result2 != 0) {
                    return result2;
                }
                if (lhs.mDisplayNumber == null || rhs.mDisplayNumber == null) {
                    return 0;
                }
                int result3 = lhs.mDisplayNumber.compareTo(rhs.mDisplayNumber);
                int result4 = this.mDetectDuplicatesEnabled;
                return result3;
            } else if (lhsIsPartner) {
                return -1;
            } else {
                return 1;
            }
        }

        /* access modifiers changed from: package-private */
        public String getInputLabelForChannel(TIFChannelInfo channel) {
            TvInputInfo info;
            String label = this.mInputIdToLabelMap.get(channel.mInputServiceName);
            if (label == null && (info = this.mInputManager.getTvInputInfo(channel.mInputServiceName)) != null) {
                TvInputManagerHelper tvInputManagerHelper = this.mInputManager;
                label = TvInputManagerHelper.loadLabel(this.mContext, info);
                if (label != null) {
                    this.mInputIdToLabelMap.put(channel.mInputServiceName, label);
                }
            }
            return label;
        }
    }

    public static class CustomerComparator implements Comparator<TIFChannelInfo> {
        private final Context mContext;
        private boolean mDetectDuplicatesEnabled = false;
        private final Map<String, String> mInputIdToLabelMap = new HashMap();
        private int mSortType;

        public CustomerComparator(Context context, int sortType) {
            this.mContext = context;
            this.mSortType = sortType;
        }

        public void setSortType(int sortType) {
            this.mSortType = sortType;
        }

        public void setDetectDuplicatesEnabled(boolean detectDuplicatesEnabled) {
            this.mDetectDuplicatesEnabled = detectDuplicatesEnabled;
        }

        public int compare(TIFChannelInfo lhs, TIFChannelInfo rhs) {
            if (lhs == rhs) {
                return 0;
            }
            if (this.mSortType == 0) {
                if (lhs.mDisplayNumber == null || rhs.mDisplayNumber == null) {
                    return 0;
                }
                String[] lhsNumberlist = lhs.mDisplayNumber.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                String[] rhsNumberlist = rhs.mDisplayNumber.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                if (lhsNumberlist.length == 1 && rhsNumberlist.length == 1) {
                    return channelnum(lhsNumberlist[0]) - channelnum(rhsNumberlist[0]);
                }
                if (lhsNumberlist.length != 1 || rhsNumberlist.length <= 1) {
                    if (lhsNumberlist.length <= 1 || rhsNumberlist.length != 1) {
                        if (lhsNumberlist.length <= 1 || rhsNumberlist.length <= 1) {
                            return 0;
                        }
                        if (channelnum(lhsNumberlist[0]) == channelnum(rhsNumberlist[0])) {
                            return channelnum(lhsNumberlist[1]) - channelnum(rhsNumberlist[1]);
                        }
                        return channelnum(lhsNumberlist[0]) - channelnum(rhsNumberlist[0]);
                    } else if (channelnum(lhsNumberlist[0]) == channelnum(rhsNumberlist[0])) {
                        return channelnum(lhsNumberlist[1]) - 0;
                    } else {
                        return channelnum(lhsNumberlist[0]) - channelnum(rhsNumberlist[0]);
                    }
                } else if (channelnum(lhsNumberlist[0]) == channelnum(rhsNumberlist[0])) {
                    return 0 - channelnum(rhsNumberlist[1]);
                } else {
                    return channelnum(lhsNumberlist[0]) - channelnum(rhsNumberlist[0]);
                }
            } else if (this.mSortType == 1) {
                return lhs.mDisplayName.compareTo(rhs.mDisplayName);
            } else {
                if (this.mSortType == 2) {
                    return rhs.mDisplayName.compareTo(lhs.mDisplayName);
                }
                if (this.mSortType == 3) {
                    if (lhs.mMtkTvChannelInfo != null && rhs.mMtkTvChannelInfo != null) {
                        boolean lhsScrambled = TIFFunctionUtil.checkChMask(lhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_SCRAMBLED_MASK, TIFFunctionUtil.CH_SCRAMBLED_VAL);
                        boolean rhsScrambled = TIFFunctionUtil.checkChMask(rhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_SCRAMBLED_MASK, TIFFunctionUtil.CH_SCRAMBLED_VAL);
                        if (lhsScrambled && rhsScrambled) {
                            return 0;
                        }
                        if (lhsScrambled) {
                            return -1;
                        }
                        if (rhsScrambled) {
                            return 1;
                        }
                        return 0;
                    } else if (lhs.mMtkTvChannelInfo != null || rhs.mMtkTvChannelInfo == null || !TIFFunctionUtil.checkChMask(rhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_SCRAMBLED_MASK, TIFFunctionUtil.CH_SCRAMBLED_VAL)) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else if (this.mSortType == 4) {
                    if (lhs.mMtkTvChannelInfo == null || rhs.mMtkTvChannelInfo == null) {
                        if (lhs.mMtkTvChannelInfo == null && rhs.mMtkTvChannelInfo != null) {
                            if (rhs.mMtkTvChannelInfo.getServiceType() == 1 || rhs.mMtkTvChannelInfo.getServiceType() == 3 || rhs.mMtkTvChannelInfo.getServiceType() == 13 || rhs.mMtkTvChannelInfo.getServiceType() == 15 || rhs.mMtkTvChannelInfo.getServiceType() == 2) {
                                return 1;
                            }
                            if (rhs.mMtkTvChannelInfo.getServiceType() == 0 || !TIFFunctionUtil.checkChMask(rhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_ANALOG_VAL)) {
                                return 0;
                            }
                            return 1;
                        }
                    } else if (lhs.mMtkTvChannelInfo.getServiceType() == rhs.mMtkTvChannelInfo.getServiceType()) {
                        return 0;
                    } else {
                        if (lhs.mMtkTvChannelInfo.getServiceType() == 1) {
                            return -1;
                        }
                        if (rhs.mMtkTvChannelInfo.getServiceType() == 1) {
                            return 1;
                        }
                        if (lhs.mMtkTvChannelInfo.getServiceType() == 3 || lhs.mMtkTvChannelInfo.getServiceType() == 13 || lhs.mMtkTvChannelInfo.getServiceType() == 15) {
                            return -1;
                        }
                        if (rhs.mMtkTvChannelInfo.getServiceType() == 3 || rhs.mMtkTvChannelInfo.getServiceType() == 13 || rhs.mMtkTvChannelInfo.getServiceType() == 15) {
                            return 1;
                        }
                        if (lhs.mMtkTvChannelInfo.getServiceType() == 2) {
                            return -1;
                        }
                        if (rhs.mMtkTvChannelInfo.getServiceType() == 2) {
                            return 1;
                        }
                        if (lhs.mMtkTvChannelInfo.getServiceType() != 0 && TIFFunctionUtil.checkChMask(lhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_ANALOG_VAL)) {
                            return -1;
                        }
                        if (rhs.mMtkTvChannelInfo.getServiceType() != 0 && TIFFunctionUtil.checkChMask(rhs.mMtkTvChannelInfo, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_ANALOG_VAL)) {
                            return 1;
                        }
                        if (lhs.mMtkTvChannelInfo.getServiceType() == 0 || rhs.mMtkTvChannelInfo.getServiceType() == 0) {
                            return -1;
                        }
                    }
                    return 0;
                } else {
                    if (this.mSortType == 5) {
                        if (lhs.mDisplayName != null && rhs.mDisplayName != null) {
                            boolean lhshd = lhs.mDisplayName.contains("HD");
                            boolean rhshd = rhs.mDisplayName.contains("HD");
                            if (lhshd && rhshd) {
                                return 0;
                            }
                            if (lhshd) {
                                return -1;
                            }
                            if (rhshd) {
                                return 1;
                            }
                            boolean lhssd = lhs.mDisplayName.contains("SD");
                            boolean rhssd = rhs.mDisplayName.contains("SD");
                            if (lhssd && rhssd) {
                                return 0;
                            }
                            if (lhssd) {
                                return -1;
                            }
                            if (rhssd) {
                                return 1;
                            }
                        } else if (lhs.mDisplayName != null || rhs.mDisplayName == null || (!rhs.mDisplayName.contains("HD") && !rhs.mDisplayName.contains("SD"))) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    return 0;
                }
            }
        }

        private int channelnum(String numstr) {
            if (numstr == null || numstr.trim().length() <= 0) {
                return 0;
            }
            return (int) Double.parseDouble(numstr.trim());
        }
    }
}
