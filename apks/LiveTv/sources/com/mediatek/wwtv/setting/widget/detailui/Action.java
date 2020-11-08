package com.mediatek.wwtv.setting.widget.detailui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Action implements Parcelable {
    public static Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        public Action createFromParcel(Parcel source) {
            boolean z = false;
            Builder checked = new Builder().key(source.readString()).title(source.readString()).description(source.readString()).intent((Intent) source.readParcelable(Intent.class.getClassLoader())).resourcePackageName(source.readString()).drawableResource(source.readInt()).iconUri((Uri) source.readParcelable(Uri.class.getClassLoader())).checked(source.readInt() != 0);
            if (source.readInt() != 0) {
                z = true;
            }
            return checked.multilineDescription(z).checkSetId(source.readInt()).build();
        }

        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
    public static final int DEFAULT_CHECK_SET_ID = 1;
    public static final int NO_CHECK_SET = 0;
    public static final int NO_DRAWABLE = 0;
    public static final int SIGNAL_LEVEL = 2;
    public static final int SIGNAL_QUALITY = 1;
    private static final String TAG = "Action";
    public static HashMap<String, String[]> mHashMapForMax = new HashMap<>();
    public boolean hasRealChild;
    private int inputLength;
    private BeforeValueChangeCallback mBeforeChangedCallBack;
    /* access modifiers changed from: private */
    public int mCheckSetId;
    /* access modifiers changed from: private */
    public boolean mChecked;
    public DataType mDataType;
    /* access modifiers changed from: private */
    public String mDescription;
    /* access modifiers changed from: private */
    public int mDrawableResource;
    public List<Action> mEffectGroup;
    /* access modifiers changed from: private */
    public boolean mEnabled;
    public int mEndValue;
    private boolean mHasLeftRight;
    /* access modifiers changed from: private */
    public boolean mHasNext;
    public HashMap<Integer, int[]> mHashMap;
    /* access modifiers changed from: private */
    public Uri mIconUri;
    /* access modifiers changed from: private */
    public boolean mInfoOnly;
    public int mInitValue;
    /* access modifiers changed from: private */
    public Intent mIntent;
    public String mItemID;
    /* access modifiers changed from: private */
    public String mKey;
    public String mLocationId;
    /* access modifiers changed from: private */
    public boolean mMultilineDescription;
    private OptionValuseChangedCallBack mOptionChangedCallBack;
    public String[] mOptionValue;
    public Action mParent;
    public List<Action> mParentGroup;
    public int mPrevInitValue;
    /* access modifiers changed from: private */
    public String mResourcePackageName;
    public int mStartValue;
    public int mStepValue;
    public List<Action> mSubChildGroup;
    public HashMap<Integer, Boolean[]> mSwitchHashMap;
    /* access modifiers changed from: private */
    public String mTitle;
    public int satID;
    public int signalType;
    private boolean supportModify;
    private int userDefined;

    public interface BeforeValueChangeCallback {
        void beforeValueChanged(int i);
    }

    public enum DataType {
        OPTIONVIEW,
        POSITIONVIEW,
        PROGRESSBAR,
        HAVESUBCHILD,
        HAVETEXTSUBCHILD,
        INPUTBOX,
        EFFECTOPTIONVIEW,
        SWICHOPTIONVIEW,
        CHANNELLISTVIEW,
        TEXTCOMMVIEW,
        SCANCHANNELSOPTIONVIEW,
        NUMVIEW,
        NUMADJUSTVIEW,
        DATETIMEVIEW,
        CHANNELPOWERONVIEW,
        PASSWORDVIEW,
        FACTORYOPTIONVIEW,
        FACTORYPROGRESSVIEW,
        TVSOURCEVIEW,
        CHANNELPOWERNOCAHNNEL,
        SATELITEINFO,
        CHANNELEUEDIT,
        BISSITEMVIEW,
        BISSKEYVIEW,
        TKGSLOCITEMVIEW,
        EDITTEXTVIEW,
        LEFTRIGHTITEMVIEW,
        TOPVIEW,
        LASTVIEW,
        SCANROOTVIEW,
        DIALOGPOP,
        SATELITEDETAIL,
        LICENSEINFO,
        DEVICE_INFO,
        SCHEDULE_PVR,
        SAVEDATA,
        LEFTRIGHT_HASDETAILVIEW,
        DISEQC12_SAVEINFO,
        LEFTRIGHT_VIEW,
        LEFTRIGHT_HASCHILDVIEW
    }

    public interface OptionValuseChangedCallBack {
        void afterOptionValseChanged(String str);
    }

    public boolean isSupportModify() {
        return this.supportModify;
    }

    public void setSupportModify(boolean supportModify2) {
        this.supportModify = supportModify2;
    }

    public static class Builder {
        private int mCheckSetId = 0;
        private boolean mChecked;
        private String mDescription;
        private int mDrawableResource = 0;
        private boolean mEnabled = true;
        private boolean mHasNext;
        private Uri mIconUri;
        private boolean mInfoOnly;
        private Intent mIntent;
        private String mKey;
        private boolean mMultilineDescription;
        private String mResourcePackageName;
        private String mTitle;

        public Action build() {
            Action action = new Action();
            String unused = action.mKey = this.mKey;
            String unused2 = action.mTitle = this.mTitle;
            String unused3 = action.mDescription = this.mDescription;
            Intent unused4 = action.mIntent = this.mIntent;
            String unused5 = action.mResourcePackageName = this.mResourcePackageName;
            int unused6 = action.mDrawableResource = this.mDrawableResource;
            Uri unused7 = action.mIconUri = this.mIconUri;
            boolean unused8 = action.mChecked = this.mChecked;
            boolean unused9 = action.mMultilineDescription = this.mMultilineDescription;
            boolean unused10 = action.mHasNext = this.mHasNext;
            boolean unused11 = action.mInfoOnly = this.mInfoOnly;
            int unused12 = action.mCheckSetId = this.mCheckSetId;
            boolean unused13 = action.mEnabled = this.mEnabled;
            return action;
        }

        public Builder key(String key) {
            this.mKey = key;
            return this;
        }

        public Builder title(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder description(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder intent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder resourcePackageName(String resourcePackageName) {
            this.mResourcePackageName = resourcePackageName;
            return this;
        }

        public Builder drawableResource(int drawableResource) {
            this.mDrawableResource = drawableResource;
            return this;
        }

        public Builder iconUri(Uri iconUri) {
            this.mIconUri = iconUri;
            return this;
        }

        public Builder checked(boolean checked) {
            this.mChecked = checked;
            return this;
        }

        public Builder multilineDescription(boolean multilineDescription) {
            this.mMultilineDescription = multilineDescription;
            return this;
        }

        public Builder hasNext(boolean hasNext) {
            this.mHasNext = hasNext;
            return this;
        }

        public Builder infoOnly(boolean infoOnly) {
            this.mInfoOnly = infoOnly;
            return this;
        }

        public Builder checkSetId(int checkSetId) {
            this.mCheckSetId = checkSetId;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }
    }

    private Action() {
        this.hasRealChild = true;
        this.inputLength = 3;
        this.userDefined = 0;
        this.supportModify = true;
    }

    protected Action(String key, String title, String description, String resourcePackageName, int drawableResource, boolean checked, boolean multilineDescription, boolean hasNext, boolean infoOnly, Intent intent, int checkSetId) {
        this.hasRealChild = true;
        this.inputLength = 3;
        this.userDefined = 0;
        this.supportModify = true;
        this.mKey = key;
        this.mTitle = title;
        this.mDescription = description;
        this.mResourcePackageName = resourcePackageName;
        this.mDrawableResource = drawableResource;
        this.mChecked = checked;
        this.mMultilineDescription = multilineDescription;
        this.mHasNext = hasNext;
        this.mInfoOnly = infoOnly;
        this.mIntent = intent;
        this.mCheckSetId = checkSetId;
        this.mEnabled = true;
    }

    public Action(String mItemID2, String mName, int mStartValue2, int mEndValue2, int mInitValue2, String[] mOptionVaule, int mStepValue2, DataType mDataType2) {
        this.hasRealChild = true;
        this.inputLength = 3;
        this.userDefined = 0;
        this.supportModify = true;
        this.mItemID = mItemID2;
        this.mKey = mItemID2;
        this.mTitle = mName;
        this.mInfoOnly = false;
        this.mEnabled = true;
        this.mDataType = mDataType2;
        if (mItemID2.startsWith(MenuConfigManager.PARENTAL_OPEN_VCHIP_LEVEL)) {
            this.mStartValue = mStartValue2;
            this.mEndValue = mEndValue2;
        }
        if (mItemID2.equals(MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE)) {
            this.mStartValue = mStartValue2;
        }
        if (mItemID2.equals(MenuConfigManager.TIME_END_TIME) || mItemID2.equals(MenuConfigManager.TIME_END_DATE) || mItemID2.equals(MenuConfigManager.TIME_START_DATE) || mItemID2.equals(MenuConfigManager.TIME_START_TIME)) {
            this.mInitValue = mInitValue2;
        }
        if (mDataType2 == DataType.POSITIONVIEW || mDataType2 == DataType.PROGRESSBAR || mDataType2 == DataType.NUMVIEW || mDataType2 == DataType.FACTORYPROGRESSVIEW || mDataType2 == DataType.NUMADJUSTVIEW) {
            this.mStartValue = mStartValue2;
            this.mEndValue = mEndValue2;
            this.mInitValue = mInitValue2;
            this.mStepValue = mStepValue2;
            this.mDescription = "" + mInitValue2;
        }
        if (mDataType2 == DataType.OPTIONVIEW || mDataType2 == DataType.EFFECTOPTIONVIEW || mDataType2 == DataType.SWICHOPTIONVIEW || mDataType2 == DataType.CHANNELLISTVIEW || mDataType2 == DataType.TEXTCOMMVIEW || mDataType2 == DataType.CHANNELPOWERNOCAHNNEL || mDataType2 == DataType.SCANCHANNELSOPTIONVIEW || mDataType2 == DataType.CHANNELPOWERONVIEW || mDataType2 == DataType.FACTORYOPTIONVIEW || mDataType2 == DataType.FACTORYPROGRESSVIEW || mDataType2 == DataType.CHANNELEUEDIT || mDataType2 == DataType.TVSOURCEVIEW || mDataType2 == DataType.HAVETEXTSUBCHILD || mDataType2 == DataType.BISSITEMVIEW || mDataType2 == DataType.BISSKEYVIEW || mDataType2 == DataType.TKGSLOCITEMVIEW || mDataType2 == DataType.EDITTEXTVIEW || mDataType2 == DataType.LEFTRIGHTITEMVIEW || mDataType2 == DataType.SAVEDATA) {
            this.mOptionValue = mOptionVaule;
            this.mInitValue = mInitValue2;
            if (this.mOptionValue != null && this.mOptionValue.length > mInitValue2) {
                this.mDescription = this.mOptionValue[mInitValue2];
            }
        }
        if (mDataType2 == DataType.INPUTBOX || mDataType2 == DataType.NUMVIEW || mDataType2 == DataType.SATELITEINFO) {
            this.mOptionValue = mOptionVaule;
        }
        if (mDataType2 == DataType.LEFTRIGHT_HASCHILDVIEW || mDataType2 == DataType.LEFTRIGHT_VIEW || mDataType2 == DataType.LEFTRIGHT_HASDETAILVIEW) {
            this.mOptionValue = mOptionVaule;
            this.mInitValue = mInitValue2;
            if (this.mOptionValue != null && this.mOptionValue.length > mInitValue2) {
                this.mDescription = this.mOptionValue[mInitValue2];
            }
            this.mHasLeftRight = true;
            if (mDataType2 == DataType.LEFTRIGHT_HASDETAILVIEW) {
                this.mDataType = DataType.HAVESUBCHILD;
            }
        }
    }

    public Action(String mItemId, String mName, DataType mDataType2) {
        this.hasRealChild = true;
        this.inputLength = 3;
        this.userDefined = 0;
        this.supportModify = true;
        this.mItemID = mItemId;
        this.mKey = this.mItemID;
        this.mTitle = mName;
        this.mDataType = mDataType2;
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles) {
        return createActionsFromArrays(keys, titles, 0, (String) null);
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles, String checkedItemKey) {
        return createActionsFromArrays(keys, titles, 1, checkedItemKey);
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles, int checkSetId) {
        return createActionsFromArrays(keys, titles, checkSetId, (String) null);
    }

    public static ArrayList<Action> createActionsFromArrays(String[] keys, String[] titles, int checkSetId, String checkedItemKey) {
        int keysLength = keys.length;
        if (keysLength == titles.length) {
            ArrayList<Action> actions = new ArrayList<>();
            for (int i = 0; i < keysLength; i++) {
                Builder builder = new Builder();
                builder.key(keys[i]).title(titles[i]).checkSetId(checkSetId);
                if (checkedItemKey != null) {
                    if (checkedItemKey.equals(keys[i])) {
                        builder.checked(true);
                    } else {
                        builder.checked(false);
                    }
                }
                actions.add(builder.build());
            }
            return actions;
        }
        throw new IllegalArgumentException("Keys and titles dimensions must match");
    }

    public String getKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(int val) {
        if (this.mOptionValue != null && this.mOptionValue.length > val) {
            this.mDescription = this.mOptionValue[val];
        }
        if (this.mDataType == DataType.PROGRESSBAR || this.mDataType == DataType.POSITIONVIEW) {
            this.mDescription = "" + val;
        }
    }

    public void setDescription(String des) {
        this.mDescription = des;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public int getDrawableResource() {
        return this.mDrawableResource;
    }

    public Uri getIconUri() {
        return this.mIconUri;
    }

    public String getResourcePackageName() {
        return this.mResourcePackageName;
    }

    public int getCheckSetId() {
        return this.mCheckSetId;
    }

    public boolean hasMultilineDescription() {
        return this.mMultilineDescription;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean hasNext() {
        return this.mHasNext;
    }

    public void setHasNext(boolean mNext) {
        this.mHasNext = mNext;
    }

    public boolean hasLeftRight() {
        return this.mHasLeftRight;
    }

    public boolean infoOnly() {
        return this.mInfoOnly;
    }

    public String getmItemId() {
        return this.mItemID;
    }

    public void setmItemId(String mItemId) {
        this.mItemID = mItemId;
    }

    public DataType getmDataType() {
        return this.mDataType;
    }

    public void setmDataType(DataType mDataType2) {
        this.mDataType = mDataType2 == DataType.LEFTRIGHT_HASDETAILVIEW ? DataType.HAVESUBCHILD : mDataType2;
    }

    public String[] getmOptionValue() {
        return this.mOptionValue;
    }

    public void setmOptionValue(String[] mOptionValue2) {
        this.mOptionValue = mOptionValue2;
    }

    public int getmStartValue() {
        return this.mStartValue;
    }

    public void setmStartValue(int mStartValue2) {
        this.mStartValue = mStartValue2;
    }

    public int getmEndValue() {
        return this.mEndValue;
    }

    public void setmEndValue(int mEndValue2) {
        this.mEndValue = mEndValue2;
    }

    public int getmInitValue() {
        return this.mInitValue;
    }

    public void setmInitValue(int mInitValue2) {
        this.mInitValue = mInitValue2;
    }

    public int getmStepValue() {
        return this.mStepValue;
    }

    public void setmStepValue(int mStepValue2) {
        this.mStepValue = mStepValue2;
    }

    public Action getmParent() {
        return this.mParent;
    }

    public void setmParent(Action mParent2) {
        this.mParent = mParent2;
    }

    public List<Action> getmEffectGroup() {
        return this.mEffectGroup;
    }

    public void setmEffectGroup(List<Action> mEffectGroup2) {
        this.mEffectGroup = mEffectGroup2;
    }

    public HashMap<Integer, Boolean[]> getmSwitchHashMap() {
        return this.mSwitchHashMap;
    }

    public void setmSwitchHashMap(HashMap<Integer, Boolean[]> mSwitchHashMap2) {
        this.mSwitchHashMap = mSwitchHashMap2;
    }

    public List<Action> getmSubChildGroup() {
        return this.mSubChildGroup;
    }

    public void setmSubChildGroup(List<Action> mSubChildGroup2) {
        this.mSubChildGroup = mSubChildGroup2;
    }

    public List<Action> getmParentGroup() {
        return this.mParentGroup;
    }

    public void setmParentGroup(List<Action> mParentGroup2) {
        this.mParentGroup = mParentGroup2;
    }

    public String getmKey() {
        return this.mKey;
    }

    public void setmKey(String mKey2) {
        this.mKey = mKey2;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle2) {
        this.mTitle = mTitle2;
    }

    public Intent getmIntent() {
        return this.mIntent;
    }

    public void setmIntent(Intent mIntent2) {
        this.mIntent = mIntent2;
    }

    public boolean ismChecked() {
        return this.mChecked;
    }

    public void setmChecked(boolean mChecked2) {
        this.mChecked = mChecked2;
    }

    public int getmCheckSetId() {
        return this.mCheckSetId;
    }

    public void setmCheckSetId(int mCheckSetId2) {
        this.mCheckSetId = mCheckSetId2;
    }

    public int getInputLength() {
        return this.inputLength;
    }

    public void setInputLength(int inputLength2) {
        this.inputLength = inputLength2;
    }

    public int getUserDefined() {
        return this.userDefined;
    }

    public void setUserDefined(int defind) {
        this.userDefined = defind;
    }

    public void setOptionValueChangedCallBack(OptionValuseChangedCallBack back) {
        this.mOptionChangedCallBack = back;
    }

    public OptionValuseChangedCallBack getCallBack() {
        return this.mOptionChangedCallBack;
    }

    public void setBeforeChangedCallBack(BeforeValueChangeCallback back) {
        this.mBeforeChangedCallBack = back;
    }

    public BeforeValueChangeCallback getBeforeCallBack() {
        return this.mBeforeChangedCallBack;
    }

    public Drawable getIndicator(Context context) {
        if (this.mDrawableResource == 0) {
            return null;
        }
        if (this.mResourcePackageName == null) {
            return context.getResources().getDrawable(this.mDrawableResource);
        }
        try {
            return context.createPackageContext(this.mResourcePackageName, 0).getResources().getDrawable(this.mDrawableResource);
        } catch (PackageManager.NameNotFoundException e) {
            if (!Log.isLoggable(TAG, 5)) {
                return null;
            }
            Log.w(TAG, "No icon for this action.");
            return null;
        } catch (Resources.NotFoundException e2) {
            if (!Log.isLoggable(TAG, 5)) {
                return null;
            }
            Log.w(TAG, "No icon for this action.");
            return null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mKey);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeParcelable(this.mIntent, flags);
        dest.writeString(this.mResourcePackageName);
        dest.writeInt(this.mDrawableResource);
        dest.writeParcelable(this.mIconUri, flags);
        dest.writeInt(this.mChecked ? 1 : 0);
        dest.writeInt(this.mMultilineDescription ? 1 : 0);
        dest.writeInt(this.mCheckSetId);
    }
}
