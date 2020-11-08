package com.android.tv.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.tv.parental.ContentRatingsManager;
import com.android.tv.parental.ParentalControlSettings;
import com.android.tv.util.images.ImageCache;
import com.android.tv.util.images.ImageLoader;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TvInputManagerHelper {
    private static final boolean DEBUG = false;
    /* access modifiers changed from: private */
    public static final ArrayList<Integer> DEFAULT_TV_INPUT_PRIORITY = new ArrayList<>();
    private static final String META_LABEL_SORT_KEY = "input_sort_key";
    private static final String[] PARTNER_TUNER_INPUT_PREFIX_BLACKLIST = new String[0];
    private static final String PERMISSION_ACCESS_ALL_EPG_DATA = "com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA";
    private static final String TAG = "TvInputManagerHelper";
    private static final String[] TESTABLE_INPUTS = {"com.android.tv.testinput/.TestTvInputService"};
    public static final int TYPE_BUNDLED_TUNER = -3;
    public static final int TYPE_CEC_DEVICE = -2;
    public static final int TYPE_CEC_DEVICE_PLAYBACK = -5;
    public static final int TYPE_CEC_DEVICE_RECORDER = -4;
    public static final int TYPE_MHL_MOBILE = -6;
    private static final String[] mPhysicalTunerBlackList = new String[0];
    /* access modifiers changed from: private */
    public final HashSet<TvInputManager.TvInputCallback> mCallbacks;
    /* access modifiers changed from: private */
    public final ContentRatingsManager mContentRatingsManager;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public final Map<String, Boolean> mInputIdToPartnerInputMap;
    /* access modifiers changed from: private */
    public final Map<String, TvInputInfo> mInputMap;
    /* access modifiers changed from: private */
    public final Map<String, Integer> mInputStateMap;
    private final TvInputManager.TvInputCallback mInternalCallback;
    private final PackageManager mPackageManager;
    private final ParentalControlSettings mParentalControlSettings;
    private boolean mStarted;
    /* access modifiers changed from: private */
    public final Map<String, Drawable> mTvInputAppliactionBanners;
    /* access modifiers changed from: private */
    public final Map<String, Drawable> mTvInputApplicationIcons;
    /* access modifiers changed from: private */
    public final Map<String, CharSequence> mTvInputApplicationLabels;
    /* access modifiers changed from: private */
    public final Map<String, String> mTvInputCustomLabels;
    private final Comparator<TvInputInfo> mTvInputInfoComparator;
    /* access modifiers changed from: private */
    public final Map<String, String> mTvInputLabels;
    protected final TvInputManagerInterface mTvInputManager;

    public interface TvInputManagerInterface {
        Integer getInputState(String str);

        List<TvContentRatingSystemInfo> getTvContentRatingSystemList();

        TvInputInfo getTvInputInfo(String str);

        List<TvInputInfo> getTvInputList();

        void registerCallback(TvInputManager.TvInputCallback tvInputCallback, Handler handler);

        void unregisterCallback(TvInputManager.TvInputCallback tvInputCallback);
    }

    private static final class TvInputManagerImpl implements TvInputManagerInterface {
        private final TvInputManager delegate;

        private TvInputManagerImpl(TvInputManager delegate2) {
            this.delegate = delegate2;
        }

        public TvInputInfo getTvInputInfo(String inputId) {
            return this.delegate.getTvInputInfo(inputId);
        }

        public Integer getInputState(String inputId) {
            return Integer.valueOf(this.delegate.getInputState(inputId));
        }

        public void registerCallback(TvInputManager.TvInputCallback internalCallback, Handler handler) {
            this.delegate.registerCallback(internalCallback, handler);
        }

        public void unregisterCallback(TvInputManager.TvInputCallback internalCallback) {
            this.delegate.unregisterCallback(internalCallback);
        }

        public List<TvInputInfo> getTvInputList() {
            return this.delegate.getTvInputList();
        }

        public List<TvContentRatingSystemInfo> getTvContentRatingSystemList() {
            return this.delegate.getTvContentRatingSystemList();
        }
    }

    static {
        DEFAULT_TV_INPUT_PRIORITY.add(-3);
        DEFAULT_TV_INPUT_PRIORITY.add(0);
        DEFAULT_TV_INPUT_PRIORITY.add(-2);
        DEFAULT_TV_INPUT_PRIORITY.add(-4);
        DEFAULT_TV_INPUT_PRIORITY.add(-5);
        DEFAULT_TV_INPUT_PRIORITY.add(-6);
        DEFAULT_TV_INPUT_PRIORITY.add(1007);
        DEFAULT_TV_INPUT_PRIORITY.add(1006);
        DEFAULT_TV_INPUT_PRIORITY.add(1004);
        DEFAULT_TV_INPUT_PRIORITY.add(1002);
        DEFAULT_TV_INPUT_PRIORITY.add(1001);
        DEFAULT_TV_INPUT_PRIORITY.add(1008);
        DEFAULT_TV_INPUT_PRIORITY.add(1005);
        DEFAULT_TV_INPUT_PRIORITY.add(1003);
        DEFAULT_TV_INPUT_PRIORITY.add(1000);
    }

    public TvInputManagerHelper(Context context) {
        this(context, createTvInputManagerWrapper(context));
    }

    @Nullable
    protected static TvInputManagerImpl createTvInputManagerWrapper(Context context) {
        TvInputManager tvInputManager = (TvInputManager) context.getSystemService("tv_input");
        if (tvInputManager == null) {
            return null;
        }
        return new TvInputManagerImpl(tvInputManager);
    }

    @VisibleForTesting
    protected TvInputManagerHelper(Context context, @Nullable TvInputManagerInterface tvInputManager) {
        this.mInputStateMap = new HashMap();
        this.mInputMap = new HashMap();
        this.mTvInputLabels = new ArrayMap();
        this.mTvInputCustomLabels = new ArrayMap();
        this.mInputIdToPartnerInputMap = new HashMap();
        this.mTvInputApplicationLabels = new ArrayMap();
        this.mTvInputApplicationIcons = new ArrayMap();
        this.mTvInputAppliactionBanners = new ArrayMap();
        this.mInternalCallback = new TvInputManager.TvInputCallback() {
            public void onInputStateChanged(String inputId, int state) {
                if (!TvInputManagerHelper.this.isInBlackList(inputId)) {
                    TvInputManagerHelper.this.mInputStateMap.put(inputId, Integer.valueOf(state));
                    Iterator it = TvInputManagerHelper.this.mCallbacks.iterator();
                    while (it.hasNext()) {
                        ((TvInputManager.TvInputCallback) it.next()).onInputStateChanged(inputId, state);
                    }
                }
            }

            public void onInputAdded(String inputId) {
                if (!TvInputManagerHelper.this.isInBlackList(inputId)) {
                    TvInputInfo info = TvInputManagerHelper.this.mTvInputManager.getTvInputInfo(inputId);
                    if (info != null) {
                        TvInputManagerHelper.this.mInputMap.put(inputId, info);
                        CharSequence label = info.loadLabel(TvInputManagerHelper.this.mContext);
                        TvInputManagerHelper.this.mTvInputLabels.put(inputId, label != null ? label.toString() : inputId);
                        CharSequence inputCustomLabel = info.loadCustomLabel(TvInputManagerHelper.this.mContext);
                        if (inputCustomLabel != null) {
                            TvInputManagerHelper.this.mTvInputCustomLabels.put(inputId, inputCustomLabel.toString());
                        }
                        TvInputManagerHelper.this.mInputStateMap.put(inputId, TvInputManagerHelper.this.mTvInputManager.getInputState(inputId));
                        TvInputManagerHelper.this.mInputIdToPartnerInputMap.put(inputId, Boolean.valueOf(TvInputManagerHelper.this.isPartnerInput(info)));
                    }
                    TvInputManagerHelper.this.mContentRatingsManager.update();
                    Iterator it = TvInputManagerHelper.this.mCallbacks.iterator();
                    while (it.hasNext()) {
                        ((TvInputManager.TvInputCallback) it.next()).onInputAdded(inputId);
                    }
                }
            }

            public void onInputRemoved(String inputId) {
                TvInputManagerHelper.this.mInputMap.remove(inputId);
                TvInputManagerHelper.this.mTvInputLabels.remove(inputId);
                TvInputManagerHelper.this.mTvInputCustomLabels.remove(inputId);
                TvInputManagerHelper.this.mTvInputApplicationLabels.remove(inputId);
                TvInputManagerHelper.this.mTvInputApplicationIcons.remove(inputId);
                TvInputManagerHelper.this.mTvInputAppliactionBanners.remove(inputId);
                TvInputManagerHelper.this.mInputStateMap.remove(inputId);
                TvInputManagerHelper.this.mInputIdToPartnerInputMap.remove(inputId);
                TvInputManagerHelper.this.mContentRatingsManager.update();
                Iterator it = TvInputManagerHelper.this.mCallbacks.iterator();
                while (it.hasNext()) {
                    ((TvInputManager.TvInputCallback) it.next()).onInputRemoved(inputId);
                }
                ImageCache.getInstance().remove(ImageLoader.LoadTvInputLogoTask.getTvInputLogoKey(inputId));
            }

            public void onInputUpdated(String inputId) {
                if (!TvInputManagerHelper.this.isInBlackList(inputId)) {
                    TvInputInfo info = TvInputManagerHelper.this.mTvInputManager.getTvInputInfo(inputId);
                    TvInputManagerHelper.this.mInputMap.put(inputId, info);
                    TvInputManagerHelper.this.mTvInputLabels.put(inputId, info.loadLabel(TvInputManagerHelper.this.mContext).toString());
                    CharSequence inputCustomLabel = info.loadCustomLabel(TvInputManagerHelper.this.mContext);
                    if (inputCustomLabel != null) {
                        TvInputManagerHelper.this.mTvInputCustomLabels.put(inputId, inputCustomLabel.toString());
                    }
                    TvInputManagerHelper.this.mTvInputApplicationLabels.remove(inputId);
                    TvInputManagerHelper.this.mTvInputApplicationIcons.remove(inputId);
                    TvInputManagerHelper.this.mTvInputAppliactionBanners.remove(inputId);
                    Iterator it = TvInputManagerHelper.this.mCallbacks.iterator();
                    while (it.hasNext()) {
                        ((TvInputManager.TvInputCallback) it.next()).onInputUpdated(inputId);
                    }
                    ImageCache.getInstance().remove(ImageLoader.LoadTvInputLogoTask.getTvInputLogoKey(inputId));
                }
            }

            public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
                TvInputManagerHelper.this.mInputMap.put(inputInfo.getId(), inputInfo);
                TvInputManagerHelper.this.mTvInputLabels.put(inputInfo.getId(), inputInfo.loadLabel(TvInputManagerHelper.this.mContext).toString());
                CharSequence inputCustomLabel = inputInfo.loadCustomLabel(TvInputManagerHelper.this.mContext);
                if (inputCustomLabel != null) {
                    TvInputManagerHelper.this.mTvInputCustomLabels.put(inputInfo.getId(), inputCustomLabel.toString());
                }
                Iterator it = TvInputManagerHelper.this.mCallbacks.iterator();
                while (it.hasNext()) {
                    ((TvInputManager.TvInputCallback) it.next()).onTvInputInfoUpdated(inputInfo);
                }
                ImageCache.getInstance().remove(ImageLoader.LoadTvInputLogoTask.getTvInputLogoKey(inputInfo.getId()));
            }
        };
        this.mHandler = new Handler();
        this.mCallbacks = new HashSet<>();
        this.mContext = context.getApplicationContext();
        this.mPackageManager = context.getPackageManager();
        this.mTvInputManager = tvInputManager;
        this.mContentRatingsManager = new ContentRatingsManager(context, tvInputManager);
        this.mParentalControlSettings = new ParentalControlSettings(context);
        this.mTvInputInfoComparator = new InputComparatorInternal(this);
    }

    public void start() {
        if (hasTvInputManager() && !this.mStarted) {
            this.mStarted = true;
            this.mTvInputManager.registerCallback(this.mInternalCallback, this.mHandler);
            this.mInputMap.clear();
            this.mTvInputLabels.clear();
            this.mTvInputCustomLabels.clear();
            this.mTvInputApplicationLabels.clear();
            this.mTvInputApplicationIcons.clear();
            this.mTvInputAppliactionBanners.clear();
            this.mInputStateMap.clear();
            this.mInputIdToPartnerInputMap.clear();
            for (TvInputInfo input : this.mTvInputManager.getTvInputList()) {
                String inputId = input.getId();
                if (!isInBlackList(inputId)) {
                    this.mInputMap.put(inputId, input);
                    this.mInputStateMap.put(inputId, Integer.valueOf(this.mTvInputManager.getInputState(inputId).intValue()));
                    this.mInputIdToPartnerInputMap.put(inputId, Boolean.valueOf(isPartnerInput(input)));
                }
            }
            this.mContentRatingsManager.update();
        }
    }

    public void stop() {
        if (this.mStarted) {
            this.mTvInputManager.unregisterCallback(this.mInternalCallback);
            this.mStarted = false;
            this.mInputStateMap.clear();
            this.mInputMap.clear();
            this.mTvInputLabels.clear();
            this.mTvInputCustomLabels.clear();
            this.mTvInputApplicationLabels.clear();
            this.mTvInputApplicationIcons.clear();
            this.mTvInputAppliactionBanners.clear();
            this.mInputIdToPartnerInputMap.clear();
        }
    }

    public void clearTvInputLabels() {
        this.mTvInputLabels.clear();
        this.mTvInputCustomLabels.clear();
        this.mTvInputApplicationLabels.clear();
    }

    public List<TvInputInfo> getTvInputInfos(boolean availableOnly, boolean tunerOnly) {
        ArrayList<TvInputInfo> list = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : this.mInputStateMap.entrySet()) {
            if (!availableOnly || pair.getValue().intValue() != 2) {
                TvInputInfo input = getTvInputInfo(pair.getKey());
                if (!tunerOnly || input.getType() == 0) {
                    list.add(input);
                }
            }
        }
        Collections.sort(list, this.mTvInputInfoComparator);
        return list;
    }

    public Comparator<TvInputInfo> getDefaultTvInputInfoComparator() {
        return this.mTvInputInfoComparator;
    }

    @VisibleForTesting
    public boolean isPartnerInput(TvInputInfo inputInfo) {
        return isSystemInput(inputInfo) && !isBundledInput(inputInfo);
    }

    public boolean isSystemInput(TvInputInfo inputInfo) {
        if (inputInfo == null || (inputInfo.getServiceInfo().applicationInfo.flags & 1) == 0) {
            return false;
        }
        return true;
    }

    public boolean isBundledInput(TvInputInfo inputInfo) {
        return inputInfo != null && inputInfo.getServiceInfo().applicationInfo.packageName.equalsIgnoreCase("com.android.tv");
    }

    public boolean isPartnerInput(String inputId) {
        Boolean isPartnerInput = this.mInputIdToPartnerInputMap.get(inputId);
        if (isPartnerInput != null) {
            return isPartnerInput.booleanValue();
        }
        return false;
    }

    public boolean hasTvInputManager() {
        return this.mTvInputManager != null;
    }

    public String loadLabel(TvInputInfo info) {
        String label = this.mTvInputLabels.get(info.getId());
        if (label != null) {
            return label;
        }
        String label2 = info.loadLabel(this.mContext).toString();
        this.mTvInputLabels.put(info.getId(), label2);
        return label2;
    }

    public String loadCustomLabel(TvInputInfo info) {
        CharSequence customLabelCharSequence;
        String customLabel = this.mTvInputCustomLabels.get(info.getId());
        if (customLabel != null || (customLabelCharSequence = info.loadCustomLabel(this.mContext)) == null) {
            return customLabel;
        }
        String customLabel2 = customLabelCharSequence.toString();
        this.mTvInputCustomLabels.put(info.getId(), customLabel2);
        return customLabel2;
    }

    public CharSequence getTvInputApplicationLabel(CharSequence inputId) {
        return this.mTvInputApplicationLabels.get(inputId);
    }

    public void setTvInputApplicationLabel(String inputId, CharSequence label) {
        this.mTvInputApplicationLabels.put(inputId, label);
    }

    public Drawable getTvInputApplicationIcon(String inputId) {
        return this.mTvInputApplicationIcons.get(inputId);
    }

    public void setTvInputApplicationIcon(String inputId, Drawable icon) {
        this.mTvInputApplicationIcons.put(inputId, icon);
    }

    public Drawable getTvInputApplicationBanner(String inputId) {
        return this.mTvInputAppliactionBanners.get(inputId);
    }

    public void setTvInputApplicationBanner(String inputId, Drawable banner) {
        this.mTvInputAppliactionBanners.put(inputId, banner);
    }

    public boolean hasTvInputInfo(String inputId) {
        return this.mStarted && !TextUtils.isEmpty(inputId) && this.mInputMap.get(inputId) != null;
    }

    public TvInputInfo getTvInputInfo(String inputId) {
        if (this.mStarted && inputId != null) {
            return this.mInputMap.get(inputId);
        }
        return null;
    }

    public ApplicationInfo getTvInputAppInfo(String inputId) {
        TvInputInfo info = getTvInputInfo(inputId);
        if (info == null) {
            return null;
        }
        return info.getServiceInfo().applicationInfo;
    }

    public int getTunerTvInputSize() {
        int size = 0;
        for (TvInputInfo input : this.mInputMap.values()) {
            if (input.getType() == 0) {
                size++;
            }
        }
        return size;
    }

    public int getInputState(@Nullable TvInputInfo inputInfo) {
        if (inputInfo == null) {
            return 2;
        }
        return getInputState(inputInfo.getId());
    }

    public int getInputState(String inputId) {
        if (!this.mStarted) {
            return 2;
        }
        Integer state = this.mInputStateMap.get(inputId);
        if (state != null) {
            return state.intValue();
        }
        Log.w(TAG, "getInputState: no such input (id=" + inputId + ")");
        return 2;
    }

    public void addCallback(TvInputManager.TvInputCallback callback) {
        this.mCallbacks.add(callback);
    }

    public void removeCallback(TvInputManager.TvInputCallback callback) {
        this.mCallbacks.remove(callback);
    }

    public ParentalControlSettings getParentalControlSettings() {
        return this.mParentalControlSettings;
    }

    public ContentRatingsManager getContentRatingsManager() {
        return this.mContentRatingsManager;
    }

    /* access modifiers changed from: private */
    public int getInputSortKey(TvInputInfo input) {
        return input.getServiceInfo().metaData.getInt(META_LABEL_SORT_KEY, Integer.MAX_VALUE);
    }

    /* access modifiers changed from: private */
    public boolean isInputPhysicalTuner(TvInputInfo input) {
        if (Arrays.asList(mPhysicalTunerBlackList).contains(input.getServiceInfo().packageName) || input.createSetupIntent() == null) {
            return false;
        }
        if (!(this.mPackageManager.checkPermission(PERMISSION_ACCESS_ALL_EPG_DATA, input.getServiceInfo().packageName) == 0)) {
            try {
                if ((this.mPackageManager.getApplicationInfo(input.getServiceInfo().packageName, 0).flags & Cea708CCParser.Const.CODE_C1_CW1) == 0) {
                    return false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isInBlackList(String inputId) {
        return false;
    }

    @VisibleForTesting
    static class InputComparatorInternal implements Comparator<TvInputInfo> {
        private final TvInputManagerHelper mInputManager;

        public InputComparatorInternal(TvInputManagerHelper inputManager) {
            this.mInputManager = inputManager;
        }

        public int compare(TvInputInfo lhs, TvInputInfo rhs) {
            if (this.mInputManager.isPartnerInput(lhs) != this.mInputManager.isPartnerInput(rhs)) {
                return this.mInputManager.isPartnerInput(lhs) ? -1 : 1;
            }
            return this.mInputManager.loadLabel(lhs).compareTo(this.mInputManager.loadLabel(rhs));
        }
    }

    public static class HardwareInputComparator implements Comparator<TvInputInfo> {
        private final Context mContext;
        private final TvInputManagerHelper mTvInputManagerHelper;
        private Map<Integer, Integer> mTypePriorities = new HashMap();

        public HardwareInputComparator(Context context, TvInputManagerHelper tvInputManagerHelper) {
            this.mContext = context;
            this.mTvInputManagerHelper = tvInputManagerHelper;
            setupDeviceTypePriorities();
        }

        public int compare(TvInputInfo lhs, TvInputInfo rhs) {
            String parentLabelL;
            String parentLabelR;
            boolean isPhysicalL;
            boolean enabledR = false;
            if (lhs == null) {
                if (rhs == null) {
                    return 0;
                }
                return 1;
            } else if (rhs == null) {
                return -1;
            } else {
                boolean enabledL = this.mTvInputManagerHelper.getInputState(lhs) != 2;
                if (this.mTvInputManagerHelper.getInputState(rhs) != 2) {
                    enabledR = true;
                }
                if (enabledL == enabledR) {
                    int priorityL = getPriority(lhs);
                    int priorityR = getPriority(rhs);
                    if (priorityL != priorityR) {
                        return priorityL - priorityR;
                    }
                    if (lhs.getType() != 0 || rhs.getType() != 0 || (isPhysicalL = this.mTvInputManagerHelper.isInputPhysicalTuner(lhs)) == this.mTvInputManagerHelper.isInputPhysicalTuner(rhs)) {
                        int sortKeyL = this.mTvInputManagerHelper.getInputSortKey(lhs);
                        int sortKeyR = this.mTvInputManagerHelper.getInputSortKey(rhs);
                        if (sortKeyL != sortKeyR) {
                            return sortKeyR - sortKeyL;
                        }
                        if (lhs.getParentId() != null) {
                            parentLabelL = getLabel(this.mTvInputManagerHelper.getTvInputInfo(lhs.getParentId()));
                        } else {
                            parentLabelL = getLabel(this.mTvInputManagerHelper.getTvInputInfo(lhs.getId()));
                        }
                        if (rhs.getParentId() != null) {
                            parentLabelR = getLabel(this.mTvInputManagerHelper.getTvInputInfo(rhs.getParentId()));
                        } else {
                            parentLabelR = getLabel(this.mTvInputManagerHelper.getTvInputInfo(rhs.getId()));
                        }
                        if (!TextUtils.equals(parentLabelL, parentLabelR)) {
                            return parentLabelL.compareToIgnoreCase(parentLabelR);
                        }
                        return getLabel(lhs).compareToIgnoreCase(getLabel(rhs));
                    } else if (isPhysicalL) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (enabledL) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        private String getLabel(TvInputInfo input) {
            if (input == null) {
                return "";
            }
            String label = this.mTvInputManagerHelper.loadCustomLabel(input);
            if (TextUtils.isEmpty(label)) {
                return this.mTvInputManagerHelper.loadLabel(input);
            }
            return label;
        }

        private int getPriority(TvInputInfo info) {
            Integer priority = null;
            if (this.mTypePriorities != null) {
                priority = this.mTypePriorities.get(Integer.valueOf(getTvInputTypeForPriority(info)));
            }
            if (priority != null) {
                return priority.intValue();
            }
            return Integer.MAX_VALUE;
        }

        private void setupDeviceTypePriorities() {
            this.mTypePriorities = Partner.getInstance(this.mContext).getInputsOrderMap();
            int priority = this.mTypePriorities.size();
            Iterator it = TvInputManagerHelper.DEFAULT_TV_INPUT_PRIORITY.iterator();
            while (it.hasNext()) {
                int type = ((Integer) it.next()).intValue();
                if (!this.mTypePriorities.containsKey(Integer.valueOf(type))) {
                    this.mTypePriorities.put(Integer.valueOf(type), Integer.valueOf(priority));
                    priority++;
                }
            }
        }

        private int getTvInputTypeForPriority(TvInputInfo info) {
            if (info.getHdmiDeviceInfo() != null) {
                if (info.getHdmiDeviceInfo().isCecDevice()) {
                    int deviceType = info.getHdmiDeviceInfo().getDeviceType();
                    if (deviceType == 1) {
                        return -4;
                    }
                    if (deviceType != 4) {
                        return -2;
                    }
                    return -5;
                } else if (info.getHdmiDeviceInfo().isMhlDevice()) {
                    return -6;
                }
            }
            return info.getType();
        }
    }

    public static String loadLabel(Context context, TvInputInfo input) {
        String label = null;
        if (input == null) {
            return null;
        }
        TvInputManagerHelper inputManager = TvSingletons.getSingletons().getTvInputManagerHelper();
        CharSequence customLabel = inputManager.loadCustomLabel(input);
        if (customLabel != null) {
            label = customLabel.toString();
        }
        if (TextUtils.isEmpty(label)) {
            return inputManager.loadLabel(input).toString();
        }
        return label;
    }
}
