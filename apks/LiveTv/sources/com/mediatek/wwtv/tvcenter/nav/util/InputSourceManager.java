package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.support.media.tv.TvContractCompat;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.onboarding.SetupSourceActivity;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.setting.widget.detailui.BaseDialogFragment;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.HdmiInput;
import com.mediatek.wwtv.tvcenter.nav.input.ISource;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.SystemsApi;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputSourceManager {
    private static final String DEFAULT_ATV_INPUT_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW1";
    private static final String DEFAULT_INPUT_ID = "com.mediatek.tvinput/.tuner.TunerInputService/HW0";
    public static final int INPUT_DISABLE = 1;
    public static final int INPUT_ENABLE = 2;
    public static final int INPUT_HIDE = 0;
    public static final String MAIN = "main";
    private static final String MAIN_SOURCE_HARDWARE_ID = "multi_view_main_source_hardware_id";
    private static final String MAIN_SOURCE_NAME = "multi_view_main_source_name";
    private static final String PATH_CHANNEL = "channel";
    public static final String PORT = "Port";
    public static final String SUB = "sub";
    private static final String SUB_SOURCE_HARDWARE_ID = "multi_view_sub_source_hardware_id";
    private static final String SUB_SOURCE_NAME = "multi_view_sub_source_name";
    /* access modifiers changed from: private */
    public static final String TAG = InputSourceManager.class.getSimpleName();
    private static ArrayList<String> inputIDList = new ArrayList<>();
    private static InputSourceManager instance = null;
    private static String lastTvInputInfoId;
    private static final Map<String, Integer> mInputDeviceIDMap = new HashMap();
    /* access modifiers changed from: private */
    public static final Map<String, TvInputInfo> mInputMap = new HashMap();
    private static final Map<String, String> mSourceNameMap = new HashMap();
    public static final Uri mUriMain = Uri.parse("content://main");
    public static final Uri mUriSub = Uri.parse("content://sub");
    private static ArrayList<String> recordSourceNameRemovedList = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<String> sourceList = new ArrayList<>();
    private static ArrayList<Integer> sourceStatus = new ArrayList<>();
    String currentSourceName = "";
    private String focusWithChangeHdmi = "main";
    /* access modifiers changed from: private */
    public int index = 0;
    private final TvInputManager.TvInputCallback mAvailabilityListener = new TvInputManager.TvInputCallback() {
        public void onInputStateChanged(String inputId, int state) {
            String access$100 = InputSourceManager.TAG;
            MtkLog.d(access$100, "onInputStateChanged: inputId:" + inputId + ", state:" + state);
            InputUtil.updateState(inputId, state);
            int unused = InputSourceManager.this.index = 0;
            while (InputSourceManager.this.index < InputSourceManager.this.mRigister.size()) {
                ((ISourceListListener) InputSourceManager.this.mRigister.get(InputSourceManager.this.index)).onAvailabilityChanged(inputId, state);
                InputSourceManager.access$508(InputSourceManager.this);
            }
        }

        public void onInputAdded(String inputId) {
            TvInputInfo input;
            String access$100 = InputSourceManager.TAG;
            MtkLog.d(access$100, "onInputAdded: inputId:" + inputId);
            TvInputInfo inputInfo = InputSourceManager.this.getTvInputInfo(CommonIntegration.getInstance().getCurrentFocus());
            InputUtil.buildSourceList(InputSourceManager.this.mContext);
            int unused = InputSourceManager.this.index = 0;
            while (InputSourceManager.this.index < InputSourceManager.this.mRigister.size()) {
                ((ISourceListListener) InputSourceManager.this.mRigister.get(InputSourceManager.this.index)).onAvailabilityChanged(inputId, InputSourceManager.this.mTvInputManager.getInputState(inputId));
                InputSourceManager.access$508(InputSourceManager.this);
            }
            TvInputInfo newInputInfo = InputSourceManager.this.mTvInputManager.getTvInputInfo(inputId);
            if (InputSourceManager.this.needReset) {
                boolean unused2 = InputSourceManager.this.needReset = false;
                MtkLog.d(InputSourceManager.TAG, "onInputAdded: try to resetCurrentInput again.");
                TIFChannelManager.getInstance(InputSourceManager.this.mContext).handleUpdateChannels();
                InputSourceManager.this.resetCurrentInput();
            }
            if (!InputSourceManager.this.mSaveValue.readBooleanValue(TurnkeyUiMainActivity.TURNKEY_ACTIVE_STATE)) {
                MtkLog.d(InputSourceManager.TAG, "TV in the background, return.");
                return;
            }
            if (!(inputInfo == null || newInputInfo == null || newInputInfo.getType() != 1007 || newInputInfo.getHdmiDeviceInfo() == null)) {
                String access$1002 = InputSourceManager.TAG;
                MtkLog.d(access$1002, "onInputAdded:    inputInfo:" + inputInfo.toString());
                String access$1003 = InputSourceManager.TAG;
                MtkLog.d(access$1003, "onInputAdded: newInputInfo:" + newInputInfo.toString());
                if (!TextUtils.isEmpty(newInputInfo.getParentId()) && inputInfo.getId().equals(newInputInfo.getParentId())) {
                    InputSourceManager.this.deviceSelect(newInputInfo);
                    InputSourceManager.this.updateInputsourceListShowing();
                }
            }
            if (InputSourceManager.this.mTvInputManager != null && (input = InputSourceManager.this.mTvInputManager.getTvInputInfo(inputId)) != null && CommonIntegration.is3rdTVSource(input) && !DestroyApp.isCurActivityTkuiMainActivity() && SystemsApi.isUserSetupComplete(InputSourceManager.this.mContext)) {
                if (TurnkeyUiMainActivity.getInstance() != null && TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode()) {
                    MtkLog.d(InputSourceManager.TAG, "FINISH TURNKUI");
                    TurnkeyUiMainActivity.getInstance().finish();
                }
                Intent intent = new Intent(InputSourceManager.this.mContext, SetupSourceActivity.class);
                intent.addFlags(268435456);
                InputSourceManager.this.mContext.startActivity(intent);
                MtkLog.d(InputSourceManager.TAG, "launch SourceSetupActivity!!!");
            }
        }

        public void onInputUpdated(String inputId) {
            InputUtil.buildSourceList(InputSourceManager.this.mContext);
            int unused = InputSourceManager.this.index = 0;
            while (InputSourceManager.this.index < InputSourceManager.this.mRigister.size()) {
                ((ISourceListListener) InputSourceManager.this.mRigister.get(InputSourceManager.this.index)).onAvailabilityChanged(inputId, InputSourceManager.this.mTvInputManager.getInputState(inputId));
                InputSourceManager.access$508(InputSourceManager.this);
            }
        }

        public void onInputRemoved(String inputId) {
            String access$100 = InputSourceManager.TAG;
            MtkLog.d(access$100, "onInputRemoved: inputId:" + inputId);
            int currentHardwareId = -1;
            for (AbstractInput abstractInput : InputUtil.getSourceList()) {
                if (abstractInput.getTvInputInfo() != null && abstractInput.getTvInputInfo().getId().equals(inputId)) {
                    currentHardwareId = abstractInput.getHardwareId();
                }
            }
            InputUtil.buildSourceList(InputSourceManager.this.mContext);
            int unused = InputSourceManager.this.index = 0;
            while (InputSourceManager.this.index < InputSourceManager.this.mRigister.size()) {
                ((ISourceListListener) InputSourceManager.this.mRigister.get(InputSourceManager.this.index)).onAvailabilityChanged(inputId, InputSourceManager.this.mTvInputManager.getInputState(inputId));
                InputSourceManager.access$508(InputSourceManager.this);
            }
            Context access$300 = InputSourceManager.this.mContext;
            Context unused2 = InputSourceManager.this.mContext;
            boolean interactive = ((PowerManager) access$300.getSystemService("power")).isInteractive();
            String access$1002 = InputSourceManager.TAG;
            MtkLog.d(access$1002, "interactive:" + interactive);
            if (interactive && InputSourceManager.this.getCurrentInputSourceHardwareId() == currentHardwareId) {
                InputSourceManager.this.resetCurrentInput();
                InputSourceManager.this.updateInputsourceListShowing();
            }
        }
    };
    /* access modifiers changed from: private */
    public Context mContext = null;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private HdmiTvClient mHdmiControl = null;
    private TvInputInfo mMainTvInputInfo = null;
    private boolean mNeedShowSetup = false;
    /* access modifiers changed from: private */
    public final List<ISourceListListener> mRigister = new ArrayList();
    /* access modifiers changed from: private */
    public final SaveValue mSaveValue;
    private TvInputInfo mSubTvInputInfo = null;
    /* access modifiers changed from: private */
    public TvInputManager mTvInputManager = null;
    /* access modifiers changed from: private */
    public final MtkTvInputSource mTvInputSource = MtkTvInputSource.getInstance();
    private final MtkInputSourceData mtkInputSrcs = new MtkInputSourceData();
    /* access modifiers changed from: private */
    public boolean needReset = false;
    private Runnable retryLoadSourceListAfterStartSessionFailedRunnable = new Runnable() {
        public void run() {
            List<TvInputInfo> inputs = InputSourceManager.this.mTvInputManager.getTvInputList();
            String access$100 = InputSourceManager.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("inputs size:");
            sb.append(inputs == null ? -1 : inputs.size());
            MtkLog.d(access$100, sb.toString());
            if (inputs == null || inputs.size() == 0) {
                InputSourceManager.this.mHandler.postDelayed(this, 200);
                return;
            }
            InputSourceManager.this.setupInputAdapter();
            InputSourceManager.this.resetCurrentInput();
        }
    };
    private boolean screenOffFlag = false;
    private boolean shutDownFlag = false;
    private int sourceIndex = 0;
    private String sourceStartSessionFailedName = "";
    private boolean sourceStartSessionSuccess = false;
    private int specChannelID = -1;

    public interface ISourceListListener {
        void onAvailabilityChanged(String str, int i);
    }

    static /* synthetic */ int access$508(InputSourceManager x0) {
        int i = x0.index;
        x0.index = i + 1;
        return i;
    }

    private synchronized HdmiTvClient getHdmiControl() {
        if (this.mHdmiControl == null) {
            synchronized (InputSourceManager.class) {
                if (this.mHdmiControl == null) {
                    HdmiControlManager hdmiControlManager = (HdmiControlManager) this.mContext.getSystemService("hdmi_control");
                    if (hdmiControlManager != null) {
                        this.mHdmiControl = hdmiControlManager.getClient(0);
                    }
                    this.mHdmiControl.setInputChangeListener(new HdmiInputChangeListener());
                }
            }
        }
        return this.mHdmiControl;
    }

    public void deviceSelect(TvInputInfo info) {
        Log.d(TAG, "onHdmiDeviceAdded deviceSelect start:");
        getHdmiControl().deviceSelect(info.getHdmiDeviceInfo().getId(), new HdmiTvClient.SelectCallback() {
            public void onComplete(int result) {
                String access$100 = InputSourceManager.TAG;
                Log.d(access$100, "onHdmiDeviceAdded deviceSelect onComplete: result=" + result);
                if (result != 0) {
                    Log.d(InputSourceManager.TAG, "onHdmiDeviceAdded deviceSelect onComplete: RESULT_FAIL");
                    InputSourceManager.this.mHandler.post(new Runnable() {
                        public void run() {
                            InputSourceManager.this.resetCurrentInput();
                        }
                    });
                    return;
                }
                Log.d(InputSourceManager.TAG, "onHdmiDeviceAdded deviceSelect onComplete: RESULT_SUCCESS, mInputContext.state = STATE_DONE");
            }
        });
    }

    public String getFocusWithChangeHdmi() {
        return this.focusWithChangeHdmi;
    }

    public void setFocusWithChangeHdmi(String focusWithChangeHdmi2) {
        this.focusWithChangeHdmi = focusWithChangeHdmi2;
    }

    private final class HdmiInputChangeListener implements HdmiTvClient.InputChangeListener {
        private HdmiInputChangeListener() {
        }

        public void onChanged(HdmiDeviceInfo device) {
            String access$100 = InputSourceManager.TAG;
            Log.d(access$100, "HdmiInputChangeListener, onChanged,  device = " + device.toString());
            AbstractInput input = InputUtil.getInput(InputSourceManager.this.getCurrentInputSourceHardwareId());
            if (input != null) {
                MtkLog.d(InputSourceManager.TAG, input.toString(InputSourceManager.this.mContext));
            }
            if (!DestroyApp.isCurActivityTkuiMainActivity() || input == null || !input.isHDMI() || input.getTvInputInfo().getHdmiDeviceInfo() == null || input.getTvInputInfo().getHdmiDeviceInfo().getPortId() != device.getPortId()) {
                if (input != null && (input instanceof HdmiInput)) {
                    HdmiInput hdmiInput = (HdmiInput) input;
                    if (DestroyApp.isCurActivityTkuiMainActivity() && hdmiInput.getPortId() == device.getPortId()) {
                        Log.d(InputSourceManager.TAG, "HdmiInputChangeListener, onChanged, return 1");
                        return;
                    }
                }
                if (SystemsApi.isUserSetupComplete(InputSourceManager.this.mContext) && !MtkTvScan.getInstance().isScanning()) {
                    String inputId = "";
                    Iterator<TvInputInfo> it = InputSourceManager.this.mTvInputManager.getTvInputList().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        TvInputInfo tvInputInfo = it.next();
                        String access$1002 = InputSourceManager.TAG;
                        MtkLog.d(access$1002, "[TIF]" + tvInputInfo.toString());
                        if (tvInputInfo.getType() == 1007 && tvInputInfo.getHdmiDeviceInfo() != null && tvInputInfo.getHdmiDeviceInfo().getPortId() == device.getPortId()) {
                            inputId = tvInputInfo.getId();
                            break;
                        }
                    }
                    String access$1003 = InputSourceManager.TAG;
                    Log.d(access$1003, "input:" + inputId);
                    if (!TextUtils.isEmpty(inputId)) {
                        final Uri uri = TvContract.buildChannelUriForPassthroughInput(inputId);
                        if (DestroyApp.isCurActivityTkuiMainActivity()) {
                            InputSourceManager.this.mHandler.post(new Runnable() {
                                public void run() {
                                    BannerView banerView;
                                    InputSourceManager.this.processInputUri(uri);
                                    NavBasic navBasic = ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
                                    if ((navBasic instanceof BannerView) && (banerView = (BannerView) navBasic) != null) {
                                        MtkLog.d(InputSourceManager.TAG, "processInputUri, showSimpleBanner");
                                        banerView.showSimpleBanner();
                                    }
                                }
                            });
                            return;
                        }
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(uri);
                        intent.setFlags(268435456);
                        intent.putExtra("livetv", true);
                        InputSourceManager.this.mContext.startActivity(intent);
                        return;
                    }
                    return;
                }
                return;
            }
            Log.d(InputSourceManager.TAG, "HdmiInputChangeListener, onChanged, return");
        }
    }

    public void resetScreenOffFlag() {
        this.screenOffFlag = false;
    }

    public void setShutDownFlag(boolean flag) {
        this.shutDownFlag = flag;
    }

    /* access modifiers changed from: private */
    public void setupInputAdapter() {
        try {
            this.mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        } catch (Exception ex) {
            MtkLog.d(TAG, ex.toString());
        }
        List<TvInputInfo> inputs = this.mTvInputManager.getTvInputList();
        InputUtil.buildSourceList(this.mContext);
        if (inputs == null || inputs.size() == 0) {
            MtkLog.d(TAG, "setupInputAdapter failed");
        }
        mInputMap.clear();
        sourceList.clear();
        sourceStatus.clear();
        inputIDList.clear();
        TvSingletons.getSingletons().getTvInputManagerHelper().addCallback(this.mAvailabilityListener);
        if (inputs != null) {
            for (TvInputInfo input : inputs) {
                if (!CommonIntegration.is3rdTVSource(input)) {
                    String name = this.mtkInputSrcs.getSourceNameByTis(String.valueOf(input.loadLabel(this.mContext)), input);
                    String str = TAG;
                    MtkLog.d(str, "setupInputAdapter, loadLabel:" + input.loadLabel(this.mContext) + ", name:" + name + ", InputState:" + this.mTvInputManager.getInputState(input.getId()) + ", Type:" + input.getType());
                    if (name != null) {
                        if (1007 != input.getType() || input.getHdmiDeviceInfo() == null) {
                            mInputMap.put(name, input);
                            if (sourceList.contains(name)) {
                                sourceList.remove(name);
                            }
                            if (!mSourceNameMap.containsKey(input.getId())) {
                                mSourceNameMap.put(input.getId(), name);
                            }
                            sourceList.add(name);
                        } else {
                            int portID = input.getHdmiDeviceInfo().getPortId();
                            String str2 = TAG;
                            MtkLog.d(str2, "setupInputAdapter: portID:" + portID);
                            mInputDeviceIDMap.put(input.getId(), Integer.valueOf(portID));
                            Map<String, TvInputInfo> map = mInputMap;
                            map.put(PORT + portID + name, input);
                            ArrayList<String> arrayList = sourceList;
                            arrayList.add(PORT + portID + name);
                            StringBuilder sb = new StringBuilder();
                            sb.append("HDMI ");
                            sb.append(portID);
                            String inputName = sb.toString();
                            if (!recordSourceNameRemovedList.contains(inputName)) {
                                recordSourceNameRemovedList.add(inputName);
                            }
                        }
                        inputIDList.add(input.getId());
                        sourceStatus.add(0);
                    }
                }
            }
        }
        String str3 = TAG;
        MtkLog.d(str3, "setupInputAdapter: after added all, sourceList:" + sourceList);
        Iterator<String> it = recordSourceNameRemovedList.iterator();
        while (it.hasNext()) {
            String portNum = it.next().substring(5, 6);
            Iterator<String> it2 = sourceList.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                String curItem = it2.next();
                if (curItem.startsWith("HDMI " + portNum)) {
                    sourceList.remove(curItem);
                    break;
                }
            }
        }
        String str4 = TAG;
        MtkLog.d(str4, "reLayoutSourceList, before:" + mInputMap + "," + sourceList);
        this.mtkInputSrcs.reLayoutSourceList(sourceList);
        String str5 = TAG;
        MtkLog.d(str5, "InputSourceManager, after:" + sourceList);
    }

    private InputSourceManager(Context context) {
        this.mContext = context;
        this.mSaveValue = SaveValue.getInstance(context);
        MtkLog.d(TAG, "InputSourceManager() init");
        if (CommonIntegration.isEUPARegion()) {
            this.currentSourceName = "DTV";
        } else {
            this.currentSourceName = "TV";
        }
        if (MarketRegionInfo.isFunctionSupport(13)) {
            setupInputAdapter();
        } else {
            sourceList = this.mtkInputSrcs.getSourceList();
        }
        this.mHandler = new Handler(context.getMainLooper());
        getHdmiControl();
    }

    public static InputSourceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (InputSourceManager.class) {
                if (instance == null) {
                    MtkLog.d(TAG, "getInstance new");
                    instance = new InputSourceManager(context);
                }
            }
        }
        return instance;
    }

    public static InputSourceManager getInstance() {
        return getInstance(DestroyApp.appContext);
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x01b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processInputUri(android.net.Uri r11) {
        /*
            r10 = this;
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "processInputUri xiaojie>>"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            boolean r0 = r10.isChannelUriForTunerInput(r11)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0096
            java.lang.String r0 = TAG
            java.lang.String r3 = "processInputUri xiaojie1>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r3)
            java.lang.String r0 = "sys.mtk.tv.start"
            java.lang.String r3 = "0"
            java.lang.String r0 = com.mediatek.twoworlds.tv.SystemProperties.get(r0, r3)
            java.lang.String r3 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "isTvStartAfterReboot: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            java.lang.String r3 = "1"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0053
            java.lang.String r2 = "sys.mtk.tv.start"
            java.lang.String r3 = "0"
            com.mediatek.twoworlds.tv.SystemProperties.set(r2, r3)
            return r1
        L_0x0053:
            long r3 = android.content.ContentUris.parseId(r11)
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "processInputUri channel id>>"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            android.content.Context r5 = r10.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r5 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r5)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r5 = r5.getTIFChannelInfoPLusByProviderId(r3)
            java.lang.String r6 = "TAG"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "processInputUri mChannelInfo = "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            int r6 = r10.tuneChannelByTIFChannelInfoForAssistant(r5)
            if (r6 != 0) goto L_0x0095
            r1 = r2
        L_0x0095:
            return r1
        L_0x0096:
            boolean r0 = android.media.tv.TvContract.isChannelUriForPassthroughInput(r11)
            if (r0 == 0) goto L_0x0163
            java.lang.String r0 = TAG
            java.lang.String r3 = "processInputUri xiaojie2>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r3)
            java.util.List r0 = r11.getPathSegments()
            java.lang.Object r0 = r0.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r3 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "processInputUri input id>>"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = ">>"
            r4.append(r5)
            android.media.tv.TvInputInfo r5 = r10.mMainTvInputInfo
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0162
            java.util.List r3 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList()
            android.content.Context r4 = r10.mContext
            com.mediatek.wwtv.tvcenter.nav.input.InputUtil.dump((android.content.Context) r4, (java.util.List<com.mediatek.wwtv.tvcenter.nav.input.AbstractInput>) r3)
            java.util.Iterator r4 = r3.iterator()
        L_0x00e0:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0162
            java.lang.Object r5 = r4.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r5 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r5
            android.media.tv.TvInputInfo r6 = r5.getTvInputInfo()
            if (r6 == 0) goto L_0x0160
            android.media.tv.TvInputInfo r6 = r5.getTvInputInfo()
            java.lang.String r6 = r6.getId()
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x013f
            boolean r4 = r5.isNeedAbortTune()
            if (r4 == 0) goto L_0x0132
            android.content.Context r4 = r10.mContext
            android.content.res.Resources r4 = r4.getResources()
            r6 = 2131692509(0x7f0f0bdd, float:1.901412E38)
            java.lang.Object[] r7 = new java.lang.Object[r2]
            android.content.Context r8 = r10.mContext
            java.util.Map r8 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList(r8)
            int r9 = r5.getHardwareId()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            java.lang.Object r8 = r8.get(r9)
            r7[r1] = r8
            java.lang.String r4 = r4.getString(r6, r7)
            android.content.Context r6 = r10.mContext
            android.widget.Toast r6 = android.widget.Toast.makeText(r6, r4, r2)
            r6.show()
        L_0x0132:
            int r4 = r5.getHardwareId()
            int r4 = r10.changeInputSourceByHardwareId(r4)
            if (r4 != 0) goto L_0x013e
            r1 = r2
        L_0x013e:
            return r1
        L_0x013f:
            android.media.tv.TvInputInfo r6 = r5.getTvInputInfo()
            java.lang.String r6 = r6.getParentId()
            boolean r7 = android.text.TextUtils.isEmpty(r6)
            if (r7 != 0) goto L_0x0160
            boolean r7 = android.text.TextUtils.equals(r6, r0)
            if (r7 == 0) goto L_0x0160
            int r4 = r5.getHardwareId()
            int r4 = r10.changeInputSourceByHardwareId(r4)
            if (r4 != 0) goto L_0x015f
            r1 = r2
        L_0x015f:
            return r1
        L_0x0160:
            goto L_0x00e0
        L_0x0162:
            return r1
        L_0x0163:
            java.lang.String r0 = TAG
            java.lang.String r3 = "processInputUri xiaojie3>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r3)
            java.lang.String r0 = "input"
            java.lang.String r0 = r11.getQueryParameter(r0)
            if (r0 == 0) goto L_0x0184
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r0 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView r0 = r0.getTvView()
            java.lang.String r1 = "input"
            java.lang.String r1 = r11.getQueryParameter(r1)
            r0.tune(r1, r11)
            return r2
        L_0x0184:
            int r0 = r10.getCurrentInputSourceHardwareId()
            r3 = -1
            if (r0 == r3) goto L_0x01d2
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r3 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getInput((int) r0)
            if (r3 == 0) goto L_0x01d2
            boolean r4 = r3.isTV()
            if (r4 != 0) goto L_0x01d2
            boolean r4 = r3.isDTV()
            if (r4 != 0) goto L_0x01d2
            boolean r4 = r3.isATV()
            if (r4 != 0) goto L_0x01d2
            java.util.List r4 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList()
            java.util.Iterator r5 = r4.iterator()
        L_0x01ab:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x01d2
            java.lang.Object r6 = r5.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r6 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r6
            boolean r7 = r6.isTV()
            if (r7 != 0) goto L_0x01c5
            boolean r7 = r6.isDTV()
            if (r7 == 0) goto L_0x01c4
            goto L_0x01c5
        L_0x01c4:
            goto L_0x01ab
        L_0x01c5:
            int r5 = r6.getHardwareId()
            int r5 = r10.changeInputSourceByHardwareId(r5)
            if (r5 != 0) goto L_0x01d1
            r1 = r2
        L_0x01d1:
            return r1
        L_0x01d2:
            android.content.Context r3 = r10.mContext
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager r3 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getInstance(r3)
            java.util.List r3 = r3.getCurrentSVLChannelList()
            if (r3 == 0) goto L_0x01e4
            boolean r4 = r3.isEmpty()
            if (r4 == 0) goto L_0x0201
        L_0x01e4:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            int r4 = r4.getChannelActiveNumByAPIForScan()
            if (r4 != 0) goto L_0x0201
            android.content.Intent r4 = new android.content.Intent
            android.content.Context r5 = r10.mContext
            java.lang.Class<com.android.tv.onboarding.SetupSourceActivity> r6 = com.android.tv.onboarding.SetupSourceActivity.class
            r4.<init>(r5, r6)
            r5 = 268435456(0x10000000, float:2.5243549E-29)
            r4.addFlags(r5)
            android.content.Context r5 = r10.mContext
            r5.startActivity(r4)
        L_0x0201:
            android.net.Uri r4 = android.media.tv.TvContract.Programs.CONTENT_URI
            boolean r4 = r4.equals(r11)
            if (r4 == 0) goto L_0x0222
            java.lang.String r1 = TAG
            java.lang.String r4 = "processInputUri xiaojie4>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r4)
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.epg.EPGManager r1 = com.mediatek.wwtv.tvcenter.epg.EPGManager.getInstance(r1)
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r4 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            r5 = 50331648(0x3000000, float:3.761582E-37)
            r1.startEpg(r4, r5)
            return r2
        L_0x0222:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.processInputUri(android.net.Uri):boolean");
    }

    private final boolean isChannelUriForTunerInput(Uri uri) {
        return isTvUri(uri) && isTwoSegmentUriStartingWith(uri, "channel");
    }

    private final boolean isTvUri(Uri uri) {
        return uri != null && BaseDialogFragment.TAG_CONTENT.equals(uri.getScheme()) && TvContractCompat.AUTHORITY.equals(uri.getAuthority());
    }

    private final boolean isTwoSegmentUriStartingWith(Uri uri, String pathSegment) {
        List<String> pathSegments = uri.getPathSegments();
        return pathSegments.size() == 2 && pathSegment.equals(pathSegments.get(0));
    }

    public static void remove() {
        instance = null;
    }

    public int resetCurrentInput() {
        MtkLog.d(TAG, "resetCurrentInput");
        int hardwareId = getCurrentInputSourceHardwareId(CommonIntegration.getInstance().getCurrentFocus());
        AbstractInput input = InputUtil.getInput(hardwareId);
        if (input == null || !input.isNeedAbortTune()) {
            return changeInputSourceByHardwareId(hardwareId);
        }
        return changeToDefaultSource();
    }

    public List<Integer> getInputHardwareIdList() {
        List<AbstractInput> list = InputUtil.getSourceList();
        List<Integer> result = new ArrayList<>();
        for (AbstractInput input : list) {
            if (!input.isHidden(this.mContext)) {
                result.add(Integer.valueOf(input.getHardwareId()));
            }
        }
        return result;
    }

    public ArrayList<String> getInputSourceList() {
        List<ResolveInfo> services;
        if (MarketRegionInfo.isFunctionSupport(13) && ((services = this.mContext.getPackageManager().queryIntentServices(new Intent("android.media.tv.TvInputService"), 4)) == null || services.isEmpty())) {
            return sourceList;
        }
        String str = TAG;
        MtkLog.d(str, "getInputSourceList() sourceList:" + sourceList);
        return sourceList;
    }

    public ArrayList<Integer> getInputSourceStatus() {
        return sourceStatus;
    }

    public ArrayList<String> getConflictSourceList() {
        ArrayList<String> list = new ArrayList<>();
        String focus = CommonIntegration.getInstance().getCurrentFocus();
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            return getPVRConflictSourceList(DvrManager.getInstance().getController().getSrcType());
        }
        if (CommonIntegration.getInstance().isTVNormalState()) {
            return list;
        }
        String currentInputSource = getCurrentInputSourceName(focus.equalsIgnoreCase("main") ? "sub" : "main");
        if (currentInputSource == null) {
            String str = TAG;
            MtkLog.d(str, "focus:" + focus);
            return list;
        }
        Iterator<String> it = sourceList.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (this.mtkInputSrcs.queryConflict(currentInputSource, s)) {
                list.add(s);
            }
        }
        String str2 = TAG;
        MtkLog.d(str2, "getConflictSourceList: focus:" + focus + "," + list);
        return list;
    }

    public boolean getConflict(int hardwardId) {
        if (StateDvr.getInstance() != null && StateDvr.getInstance().isRunning()) {
            return getPVRConflict(hardwardId);
        }
        if (!CommonIntegration.getInstance().isPipOrPopState()) {
            return false;
        }
        AbstractInput cuInput = InputUtil.getInput(getCurrentInputSourceHardwareId());
        AbstractInput tarInput = InputUtil.getInput(hardwardId);
        if (cuInput == null || tarInput == null || !cuInput.getConflict(tarInput)) {
            return false;
        }
        return true;
    }

    public boolean getPVRConflict(int hardwardId) {
        if (!MarketRegionInfo.isFunctionSupport(30)) {
            return false;
        }
        AbstractInput curInput = InputUtil.getInput(getHardwareIdBySourceName(DvrManager.getInstance().getPVRRecordingSrc()));
        AbstractInput tarInput = InputUtil.getInput(hardwardId);
        if (curInput == null || tarInput == null || !curInput.isDTV() || !tarInput.isATV()) {
            return false;
        }
        return true;
    }

    public ArrayList<String> getPVRConflictSourceList(String recordSource) {
        MtkLog.d(TAG, "getPVRConflictSourceList()");
        ArrayList<String> list = new ArrayList<>();
        String currentInputSource = null;
        if (MarketRegionInfo.isFunctionSupport(30)) {
            currentInputSource = DvrManager.getInstance().getPVRRecordingSrc();
        }
        if (currentInputSource == null || currentInputSource.equalsIgnoreCase("")) {
            return list;
        }
        if (recordSource.equalsIgnoreCase("ATV")) {
            list.add("Composite");
            list.add("ATV");
            list.add("TV");
            list.add("Component");
            list.add("SVIDEO");
            list.add("SCART");
            list.add("VGA");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("MMP");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else if (recordSource.equalsIgnoreCase("TV")) {
            list.add("MMP");
            list.add("ATV");
        } else if (recordSource.equalsIgnoreCase("COMPOSITE")) {
            if (CommonIntegration.getInstance().getCurChInfo() != null && CommonIntegration.getInstance().getCurChInfo().getBrdcstType() == 1) {
                list.add("TV");
            }
            list.add("SCART");
            list.add("SVIDEO");
            list.add("MMP");
        } else if (recordSource.equalsIgnoreCase("SVIDEO")) {
            list.add("Composite");
            list.add("Component");
            list.add("SCART");
            list.add("VGA");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("TV");
            list.add("MMP");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else if (recordSource.equalsIgnoreCase("SCART")) {
            list.add("Composite");
            list.add("Component");
            list.add("SVIDEO");
            list.add("VGA");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("TV");
            list.add("MMP");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else if (recordSource.equalsIgnoreCase("Component")) {
            list.add("Composite");
            list.add("SCART");
            list.add("SVIDEO");
            list.add("VGA");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("TV");
            list.add("MMP");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else if (recordSource.equalsIgnoreCase("VGA")) {
            list.add("Composite");
            list.add("SCART");
            list.add("SVIDEO");
            list.add("Component");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("TV");
            list.add("MMP");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else if (recordSource.equalsIgnoreCase("HDMI")) {
            list.add("Composite");
            list.add("SCART");
            list.add("SVIDEO");
            list.add("Component");
            list.add("HDMI 1");
            list.add("HDMI 2");
            list.add("HDMI 3");
            list.add("HDMI 4");
            list.add("TV");
            list.add("MMP");
            list.add("HDMI");
            list.add("Port1Mobile");
            list.add("Port1BD");
            list.add("Port2BD");
            list.add("Port3BD");
            list.add("Port4BD");
        } else {
            list.addAll(sourceList);
            list.remove(currentInputSource);
        }
        String str = TAG;
        MtkLog.d(str, "getPVRConflictSourceList()," + list);
        return list;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x004b A[EDGE_INSN: B:17:0x004b->B:15:0x004b ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0022  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCurrentInputSourceHardwareId(java.lang.String r5) {
        /*
            r4 = this;
            r0 = 0
            java.lang.String r1 = "main"
            boolean r1 = r1.equals(r5)
            r2 = -1
            if (r1 == 0) goto L_0x003b
            com.mediatek.wwtv.tvcenter.util.SaveValue r1 = r4.mSaveValue
            java.lang.String r3 = "multi_view_main_source_hardware_id"
            int r0 = r1.readValue(r3, r2)
            if (r0 != r2) goto L_0x004b
            java.util.List r1 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList()
            java.util.Iterator r1 = r1.iterator()
        L_0x001c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x004b
            java.lang.Object r2 = r1.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r2 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r2
            boolean r3 = r2.isTV()
            if (r3 != 0) goto L_0x0036
            boolean r3 = r2.isDTV()
            if (r3 == 0) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            goto L_0x001c
        L_0x0036:
            int r0 = r2.getHardwareId()
            goto L_0x004b
        L_0x003b:
            java.lang.String r1 = "sub"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x004b
            com.mediatek.wwtv.tvcenter.util.SaveValue r1 = r4.mSaveValue
            java.lang.String r3 = "multi_view_sub_source_hardware_id"
            int r0 = r1.readValue(r3, r2)
        L_0x004b:
            java.lang.String r1 = TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getCurrentInputSourceHardwareId:"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            int r0 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.checkInvalideInput(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.getCurrentInputSourceHardwareId(java.lang.String):int");
    }

    public int getCurrentInputSourceHardwareId() {
        return getCurrentInputSourceHardwareId(CommonIntegration.getInstance().getCurrentFocus());
    }

    public void saveWorldInputType() {
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId());
        if (input != null) {
            if (input.isTV() || input.isDTV() || input.isATV()) {
                int inputType = 10;
                if (input.isTV()) {
                    inputType = 11;
                } else if (input.isDTV()) {
                    inputType = 12;
                } else if (input.isATV()) {
                    inputType = 13;
                }
                SaveValue.writeWorldInputType(this.mContext, inputType);
            }
        }
    }

    public void saveInputSourceHardwareId(int hardwareId, String path) {
        if ("main".equals(path)) {
            this.mSaveValue.saveValue(MAIN_SOURCE_HARDWARE_ID, hardwareId);
            SaveValue.writeWorldStringValue(this.mContext, "multi_view_main_source_name", InputUtil.getInput(hardwareId).getSourceName(this.mContext), true);
            AbstractInput input = InputUtil.getInput(hardwareId);
            int inputType = 10;
            if (input.isTV()) {
                inputType = 11;
            } else if (input.isDTV()) {
                inputType = 12;
            } else if (input.isATV()) {
                inputType = 13;
            } else if (input.isHDMI()) {
                inputType = 17;
            } else if (input.isComponent()) {
                inputType = 15;
            } else if (input.isComposite()) {
                inputType = 14;
            } else if (input.isVGA()) {
                inputType = 16;
            }
            SaveValue.writeWorldInputType(this.mContext, inputType);
        } else if ("sub".equals(path)) {
            this.mSaveValue.saveValue(SUB_SOURCE_HARDWARE_ID, hardwareId);
        }
    }

    public String getCurrentInputSourceName(String path) {
        String currentSourceName2 = InputUtil.getSourceList(this.mContext).get(Integer.valueOf(getCurrentInputSourceHardwareId(path)));
        String str = TAG;
        MtkLog.d(str, "getCurrentInputSourceName, path=" + path + ", SourceName=" + currentSourceName2);
        return currentSourceName2;
    }

    public String getTVApiSubSourceName() {
        String subSourceName = this.mtkInputSrcs.getSourceNameByTvapi("sub");
        String str = TAG;
        MtkLog.d(str, "come in getTVApiSubSourceName,subSourceName =" + subSourceName);
        return subSourceName;
    }

    public String getNextSourceName(String path) {
        ArrayList<String> temp = (ArrayList) sourceList.clone();
        String str = TAG;
        MtkLog.d(str, "autoChangeToNextInputSource, temp:" + temp + ", path:" + path);
        int count = temp.size();
        if (count == 0) {
            return "";
        }
        this.index = temp.indexOf(getCurrentInputSourceName(path));
        String str2 = TAG;
        MtkLog.d(str2, "index before:" + this.index);
        this.index = this.index + 1;
        if (this.index > count - 1) {
            this.index = 0;
        }
        return temp.get(this.index);
    }

    public int autoChangeToNextInputSource(String path) {
        int id = getCurrentInputSourceHardwareId(path);
        List<AbstractInput> list = InputUtil.getSourceList();
        int nextIndex = -1;
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                break;
            } else if (list.get(i).getHardwareId() == id) {
                nextIndex = i;
                break;
            } else {
                i++;
            }
        }
        Iterator<AbstractInput> it = list.iterator();
        while (true) {
            int nextIndex2 = nextIndex + 1;
            nextIndex = (nextIndex2 >= list.size() || nextIndex2 < 0) ? 0 : nextIndex2;
            if (!list.get(nextIndex).isNeedAbortTune() && !list.get(nextIndex).isHidden(this.mContext) && !getConflict(list.get(nextIndex).getHardwareId())) {
                return changeInputSourceByHardwareId(list.get(nextIndex).getHardwareId(), path);
            }
        }
    }

    public int changeToTVSource() {
        int targetType = 0;
        if (MarketRegionInfo.isFunctionSupport(39)) {
            boolean isAnalog = true;
            if (CommonIntegration.getInstance().getCurChInfo().getBrdcstType() != 1) {
                isAnalog = false;
            }
            targetType = isAnalog ? 10000 : ISource.TYPE_DTV;
        }
        AbstractInput input = InputUtil.getInputByType(targetType);
        if (input != null) {
            return changeInputSourceByHardwareId(input.getHardwareId());
        }
        return -1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002c A[EDGE_INSN: B:14:0x002c->B:8:0x002c ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x0013  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int changeToDefaultSource() {
        /*
            r6 = this;
            int r0 = r6.getCurrentInputSourceHardwareId()
            java.util.List r1 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList()
            r2 = -1
            java.util.Iterator r3 = r1.iterator()
        L_0x000d:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x002c
            java.lang.Object r4 = r3.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r4 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r4
            boolean r5 = r4.isTV()
            if (r5 != 0) goto L_0x0027
            boolean r5 = r4.isDTV()
            if (r5 == 0) goto L_0x0026
            goto L_0x0027
        L_0x0026:
            goto L_0x000d
        L_0x0027:
            int r2 = r4.getHardwareId()
        L_0x002c:
            java.lang.String r3 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "changeToDefaultSource targetId:"
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            r3 = -1
            if (r2 == r3) goto L_0x004c
            if (r2 == r0) goto L_0x004c
            int r3 = r6.changeInputSourceByHardwareId(r2)
            return r3
        L_0x004c:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.changeToDefaultSource():int");
    }

    public int changeCurrentInputSourceByName(String inputSourceName) {
        String str = TAG;
        MtkLog.d(str, "changeCurrentInputSourceByName inputSourceName:" + inputSourceName);
        return changeCurrentInputSourceByName(inputSourceName, CommonIntegration.getInstance().getCurrentFocus());
    }

    public void changeToTVAndSelectChannel(String inputSourceName, int channelID) {
        this.specChannelID = channelID;
        String str = TAG;
        MtkLog.d(str, "changeCurrentInputSourceByName specChannelID:" + this.specChannelID);
        changeCurrentInputSourceByName(inputSourceName, CommonIntegration.getInstance().getCurrentFocus());
    }

    public void autoChangeTestSourceChange(String inputSourceName, String path) {
        if (inputSourceName.startsWith("HDMI")) {
            inputSourceName = "HDMI " + inputSourceName.substring(4);
        }
        Log.d(TAG, "inputSourceAutoChangeTest,inputSourceName" + inputSourceName);
        if (!MarketRegionInfo.isFunctionSupport(39)) {
            if ("ATV".equalsIgnoreCase(inputSourceName)) {
                tuneChannelByTIFChannelInfoForAssistant(CommonIntegration.getInstance().getFirstATVChannelList());
                return;
            } else if ("DTV".equalsIgnoreCase(inputSourceName)) {
                tuneChannelByTIFChannelInfoForAssistant(CommonIntegration.getInstance().getFirstDTVChannelList());
                return;
            }
        }
        int id = -1;
        Iterator<AbstractInput> it = InputUtil.getSourceList().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AbstractInput input = it.next();
            if (input.getSourceName(this.mContext).equals(inputSourceName)) {
                id = input.getHardwareId();
                break;
            }
        }
        Log.d(TAG, "inputSourceAutoChangeTest,hardwardId:" + id);
        if (id == -1 && inputSourceName.startsWith("HDMI")) {
            id = getHardwareIdByOriginalSourceName(inputSourceName);
            Log.d(TAG, "getHardwareIdByOriginalSourceName,hardwardId:" + id);
        }
        if (changeInputSourceByHardwareId(id, path) == 0) {
            Log.d(TAG, "inputSourceAutoChangeTest,changeCurrentInputSourceByName success");
        }
    }

    public String autoChangeTestGetCurrentSourceName(String path) {
        String currentSourceName2 = getCurrentInputSourceName(path);
        String str = TAG;
        Log.d(str, "autoChangeTestGetCurrentSourceName, Name:" + currentSourceName2);
        if (!TextUtils.isEmpty(currentSourceName2)) {
            return currentSourceName2;
        }
        if (sourceList.size() == 0) {
            return "";
        }
        return sourceList.get(this.sourceIndex);
    }

    public int changeInputByInputId(String inputId, String path) {
        String str = TAG;
        MtkLog.d(str, "changeInputByInputId inputId:" + inputId + ",path:" + path);
        if (TextUtils.isEmpty(path)) {
            MtkLog.d(TAG, "path = null");
            return -1;
        } else if (path.equalsIgnoreCase("main")) {
            TvInputInfo mInputInfo = this.mTvInputManager.getTvInputInfo(inputId);
            if (mInputInfo != null) {
                startSession(mInputInfo);
                return 0;
            }
            MtkLog.d(TAG, "mInputInfo invalid");
            return -1;
        } else if (path.equalsIgnoreCase("sub")) {
            TvInputInfo mInputInfo2 = this.mTvInputManager.getTvInputInfo(inputId);
            if (mInputInfo2 != null) {
                startPipSession(mInputInfo2);
                return 0;
            }
            MtkLog.d(TAG, "mInputInfo invalid");
            return -1;
        } else {
            MtkLog.d(TAG, "path invalid");
            return -1;
        }
    }

    public int changeInputSourceByHardwareId(int hardwareId) {
        String str = TAG;
        MtkLog.d(str, "changeInputSourceByHardwareId hardwareId:" + hardwareId);
        return changeInputSourceByHardwareId(hardwareId, CommonIntegration.getInstance().getCurrentFocus());
    }

    public int changeInputSourceByHardwareId(int hardwareId, String path) {
        AbstractInput input = InputUtil.getInput(hardwareId);
        if (TextUtils.isEmpty(path) || TurnkeyUiMainActivity.getInstance() == null || TurnkeyUiMainActivity.getInstance().getTvView() == null) {
            String str = TAG;
            MtkLog.d(str, "changeInputSourceByHardwareId error, hardwareId:" + hardwareId + ",path:" + path);
            return -1;
        } else if (input == null) {
            String str2 = TAG;
            MtkLog.d(str2, "sourceList not ready hardwareId:" + hardwareId);
            this.needReset = true;
            return -1;
        } else {
            String str3 = TAG;
            MtkLog.d(str3, "changeInputSourceByHardwareId hardwareId:" + hardwareId + ",path:" + path);
            if (DvrManager.getInstance() != null && (DvrManager.getInstance().getState() instanceof StateDvrPlayback) && StateDvrPlayback.getInstance().isRunning()) {
                StateDvrPlayback.getInstance().stopDvrFilePlay();
            }
            if (MarketRegionInfo.isFunctionSupport(30) && StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
                StateDvr.getInstance().showSmallCtrlBar();
                StateDvr.getInstance().clearWindow(true);
            }
            if (MarketRegionInfo.isFunctionSupport(39)) {
                if (input.isATV()) {
                    if (StateDvr.getInstance() != null && StateDvr.getInstance().isRecording()) {
                        DvrManager.getInstance().stopDvr();
                    }
                    CommonIntegration.getInstance().setBrdcstType(1);
                } else if (input.isDTV()) {
                    CommonIntegration.getInstance().setBrdcstType(0);
                }
            }
            saveInputSourceHardwareId(hardwareId, path);
            saveOutputSourceName(input.getSourceName(this.mContext), path);
            if (input.isTV() || input.isDTV()) {
                int current3rdMId = SaveValue.getInstance(this.mContext).readValue(TIFFunctionUtil.current3rdMId, -1);
                String str4 = TAG;
                MtkLog.d(str4, "current3rdId:" + current3rdMId);
                if (current3rdMId != -1) {
                    TIFChannelInfo channelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(current3rdMId);
                    if (channelInfo == null) {
                        channelInfo = TIFChannelManager.getInstance(this.mContext).queryChannelById(current3rdMId);
                    }
                    if (channelInfo != null) {
                        MtkLog.d(TAG, "tune 3rd channel.");
                        MtkTvMultiView.getInstance().setChgSource(false);
                        TurnkeyUiMainActivity.getInstance().getTvView().tune(channelInfo.mInputServiceName, TvContract.buildChannelUri(channelInfo.mId));
                        return 0;
                    }
                }
            }
            if ("main".equalsIgnoreCase(path)) {
                TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(1.0f);
                if (input.isTV() || input.isDTV() || input.isATV()) {
                    TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
                    if (tifChannelInfo != null) {
                        String str5 = TAG;
                        MtkLog.d(str5, "tifChannelInfo.mInputServiceName:" + tifChannelInfo.mInputServiceName + "tifChannelInfo.mId:" + tifChannelInfo.mId);
                        input.tune(TurnkeyUiMainActivity.getInstance().getTvView(), tifChannelInfo.mInputServiceName, TvContract.buildChannelUri(tifChannelInfo.mId));
                    } else {
                        MtkLog.d(TAG, "no channel");
                        input.tune(TurnkeyUiMainActivity.getInstance().getTvView(), input.isATV() ? DEFAULT_ATV_INPUT_ID : "com.mediatek.tvinput/.tuner.TunerInputService/HW0", mUriMain);
                    }
                } else {
                    TurnkeyUiMainActivity.getInstance().getTvView().tune(input.getId(), mUriMain);
                }
            } else if ("sub".equalsIgnoreCase(path)) {
                TurnkeyUiMainActivity.getInstance().getPipView().setStreamVolume(0.0f);
            }
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00c7 A[EDGE_INSN: B:42:0x00c7->B:35:0x00c7 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0044  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int tuneChannelByTIFChannelInfoForAssistant(com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r9) {
        /*
            r8 = this;
            r0 = -1
            if (r9 == 0) goto L_0x0119
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r1 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView r1 = r1.getTvView()
            if (r1 != 0) goto L_0x000f
            goto L_0x0119
        L_0x000f:
            java.util.List r1 = com.mediatek.wwtv.tvcenter.nav.input.InputUtil.getSourceList()
            r2 = 0
            java.lang.String r3 = TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "channelInfo:"
            r4.append(r5)
            java.lang.String r5 = r9.toString()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r3 = r9.mMtkTvChannelInfo
            r4 = 0
            if (r3 != 0) goto L_0x005b
            java.lang.String r3 = TAG
            java.lang.String r5 = "tune 3rd channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r5)
            java.util.Iterator r3 = r1.iterator()
        L_0x003e:
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L_0x00c7
            java.lang.Object r5 = r3.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r5 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r5
            boolean r6 = r5.isTV()
            if (r6 != 0) goto L_0x0058
            boolean r6 = r5.isDTV()
            if (r6 == 0) goto L_0x0057
            goto L_0x0058
        L_0x0057:
            goto L_0x003e
        L_0x0058:
            r2 = r5
            goto L_0x00c7
        L_0x005b:
            java.lang.String r3 = TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "tifChannelInfo.mMtkTvChannelInfo:"
            r5.append(r6)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r6 = r9.mMtkTvChannelInfo
            java.lang.String r6 = r6.toString()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r5)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r3 = r9.mMtkTvChannelInfo
            int r3 = r3.getBrdcstType()
            r5 = 1
            if (r3 != r5) goto L_0x0081
            goto L_0x0082
        L_0x0081:
            r5 = r4
        L_0x0082:
            r3 = r5
            r5 = 39
            boolean r5 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r5)
            if (r5 == 0) goto L_0x00ae
            java.util.Iterator r5 = r1.iterator()
        L_0x008f:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x00c7
            java.lang.Object r6 = r5.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r6 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r6
            boolean r7 = r6.isATV()
            if (r7 == 0) goto L_0x00a3
            if (r3 != 0) goto L_0x00ab
        L_0x00a3:
            boolean r7 = r6.isDTV()
            if (r7 == 0) goto L_0x00ad
            if (r3 != 0) goto L_0x00ad
        L_0x00ab:
            r2 = r6
            goto L_0x00c7
        L_0x00ad:
            goto L_0x008f
        L_0x00ae:
            java.util.Iterator r5 = r1.iterator()
        L_0x00b2:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x00c7
            java.lang.Object r6 = r5.next()
            com.mediatek.wwtv.tvcenter.nav.input.AbstractInput r6 = (com.mediatek.wwtv.tvcenter.nav.input.AbstractInput) r6
            boolean r7 = r6.isTV()
            if (r7 == 0) goto L_0x00c6
            r2 = r6
            goto L_0x00c7
        L_0x00c6:
            goto L_0x00b2
        L_0x00c7:
            if (r2 != 0) goto L_0x00d1
            java.lang.String r3 = TAG
            java.lang.String r4 = "tuneChannelByTIFChannelInfoForAssistant error 2."
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            return r0
        L_0x00d1:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "input:"
            r3.append(r5)
            android.content.Context r5 = r8.mContext
            java.lang.String r5 = r2.toString(r5)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r3)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r0 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r0 = r0.getCurrentFocus()
            int r3 = r2.getHardwareId()
            r8.saveInputSourceHardwareId(r3, r0)
            android.content.Context r3 = r8.mContext
            java.lang.String r3 = r2.getSourceName(r3)
            r8.saveOutputSourceName(r3, r0)
            com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity r3 = com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity.getInstance()
            com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView r3 = r3.getTvView()
            java.lang.String r5 = r9.mInputServiceName
            long r6 = r9.mId
            android.net.Uri r6 = android.media.tv.TvContract.buildChannelUri(r6)
            r2.tune(r3, r5, r6)
            return r4
        L_0x0119:
            java.lang.String r1 = TAG
            java.lang.String r2 = "tuneChannelByTIFChannelInfoForAssistant error 1."
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.tuneChannelByTIFChannelInfoForAssistant(com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo):int");
    }

    public int changeCurrentInputSourceByName(String inputSourceName, String path) {
        String str = TAG;
        MtkLog.d(str, "changeCurrentInputSourceByName Name:" + inputSourceName + ",path:" + path);
        if (TextUtils.isEmpty(inputSourceName)) {
            return -1;
        }
        Map<Integer, String> map = InputUtil.getSourceList(this.mContext);
        int hardwardId = -1;
        Iterator<Integer> it = map.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Integer i = it.next();
            if (inputSourceName.equals(map.get(i))) {
                hardwardId = i.intValue();
                break;
            }
        }
        return changeInputSourceByHardwareId(hardwardId, path);
    }

    public boolean isCTSSource(String path) {
        TvInputInfo mInputInfo;
        String sourceName = getCurrentInputSourceName(path);
        if (sourceName == null || (mInputInfo = mInputMap.get(sourceName)) == null || mInputInfo.getType() != 0) {
            return false;
        }
        String sourceName2 = sourceName.toUpperCase();
        String str = TAG;
        MtkLog.d(str, "sourceName=" + sourceName2);
        if (sourceName2.contains("CTS")) {
            return true;
        }
        return false;
    }

    public boolean isCurrentHDMISource(String path) {
        MtkLog.d(TAG, "isCurrentHDMISource");
        AbstractInput input = InputUtil.getInputById(getCurrentInputSourceHardwareId(path));
        if (input == null || !input.isHDMI()) {
            return false;
        }
        String str = TAG;
        MtkLog.d(str, "isCurrentHDMISource inputType:" + input.getType());
        return true;
    }

    public boolean isCurrentTvSource(String path) {
        MtkLog.d(TAG, "isCurrentTvSource");
        AbstractInput input = InputUtil.getInputById(getCurrentInputSourceHardwareId(path));
        if (input == null) {
            return false;
        }
        if (!input.isTV() && !input.isDTV() && !input.isATV()) {
            return false;
        }
        String str = TAG;
        MtkLog.d(str, "isCurrentTvSource inputType:" + input.getType());
        return true;
    }

    public boolean isCurrentDTvSource(String path) {
        MtkLog.d(TAG, "isCurrentDTvSource");
        AbstractInput input = InputUtil.getInputById(getCurrentInputSourceHardwareId(path));
        if (input == null || !input.isDTV()) {
            return false;
        }
        return true;
    }

    public boolean isCurrentATvSource(String path) {
        MtkLog.d(TAG, "isCurrentATvSource");
        AbstractInput input = InputUtil.getInputById(getCurrentInputSourceHardwareId(path));
        if (input == null || !input.isATV()) {
            return false;
        }
        return true;
    }

    public boolean isCurrentAnalogSource(String path) {
        MtkTvChannelInfoBase info;
        if (!isCurrentTvSource(path) || (info = CommonIntegration.getInstance().getCurChInfo()) == null || 1 != info.getBrdcstType()) {
            return false;
        }
        MtkLog.e(TAG, "current source is analog Tv source");
        return true;
    }

    public int getHardwareIdBySourceName(String name) {
        if (TextUtils.isEmpty(name)) {
            return -1;
        }
        for (Map.Entry<Integer, String> entry : InputUtil.getSourceList(this.mContext).entrySet()) {
            if (name.equals(entry.getValue())) {
                return entry.getKey().intValue();
            }
        }
        return -1;
    }

    public boolean isBlock(String name) {
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId(name));
        return input != null && input.isBlock();
    }

    public boolean isBlockEx(String name) {
        AbstractInput input = InputUtil.getInput(getHardwareIdBySourceName(name));
        return input != null && input.isBlockEx();
    }

    public boolean isCurrentInputBlock(String path) {
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId(path));
        return input != null && input.isBlock();
    }

    public boolean isCurrentInputBlockEx(String path) {
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId(path));
        return input != null && input.isBlockEx();
    }

    public void setBlock(String name, boolean isBlock) {
        int ret = -1;
        AbstractInput input = InputUtil.getInput(getHardwareIdBySourceName(name));
        if (input != null) {
            ret = input.block(isBlock);
        }
        String str = TAG;
        MtkLog.d(str, "setBlock,ret is ====" + ret);
    }

    public boolean isCurrentSourceBlocked(String path) {
        return isCurrentInputBlock(path);
    }

    public boolean isTvInputBlocked() {
        return this.mTvInputSource.checkIsMenuTvBlock();
    }

    public void resetDefault() {
        for (AbstractInput input : InputUtil.getSourceList()) {
            if (input.isTV() || input.isDTV()) {
                changeInputSourceByHardwareId(input.getHardwareId());
            }
        }
    }

    public TvInputInfo getTvInputInfo(String path) {
        String str = TAG;
        MtkLog.d(str, "getTvInputInfo, path=" + path);
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId(path));
        if (input != null) {
            return input.getTvInputInfo();
        }
        if (path.equalsIgnoreCase("main")) {
            return this.mMainTvInputInfo;
        }
        if (path.equalsIgnoreCase("sub")) {
            return this.mSubTvInputInfo;
        }
        return null;
    }

    public String getTvInputId(String path) {
        String str = TAG;
        MtkLog.d(str, "getTvInputId, path=" + path);
        AbstractInput input = InputUtil.getInput(getCurrentInputSourceHardwareId(path));
        if (input != null && input.getTvInputInfo() != null) {
            return input.getTvInputInfo().getId();
        }
        if (path.equalsIgnoreCase("main") && this.mMainTvInputInfo != null) {
            return this.mMainTvInputInfo.getId();
        }
        if (path.equalsIgnoreCase("sub") && this.mSubTvInputInfo != null) {
            return this.mSubTvInputInfo.getId();
        }
        MtkLog.d(TAG, "getTvInputId=null");
        return null;
    }

    public String getInputSourceByAPI(String focus) {
        return this.mtkInputSrcs.getSourceNameByTvapi(focus);
    }

    private void startSession(TvInputInfo mInputInfo) {
        TIFChannelInfo tifInfo;
        TIFChannelInfo tifInfo2;
        if (mInputInfo == null) {
            String str = TAG;
            MtkLog.d(str, "startSession, " + mInputInfo);
            return;
        }
        String inputname = this.mTvInputSource.getCurrentInputSourceName("main");
        Uri uri = mUriMain;
        String str2 = TAG;
        MtkLog.d(str2, "startSession, " + mInputInfo + "," + inputname);
        this.mMainTvInputInfo = mInputInfo;
        if (TurnkeyUiMainActivity.getInstance() == null || TurnkeyUiMainActivity.getInstance().getTvView() == null) {
            MtkLog.d(TAG, "startSession, the TVView is null!");
        } else if (CommonIntegration.is3rdTVSource(mInputInfo)) {
            List<TIFChannelInfo> list = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoBySource(mInputInfo.getId());
            MtkTvMultiView.getInstance().setChgSource(false);
            if (list == null || list.size() <= 0) {
                SourceListView mSourceListView = (SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC);
                if (mSourceListView != null) {
                    MtkLog.d(TAG, "need Open Source Setup");
                    mSourceListView.sendMsgOpenSourceSetup();
                }
                TurnkeyUiMainActivity.getInstance().getTvView().reset();
                return;
            }
            TurnkeyUiMainActivity.getInstance().getTvView().tune(mInputInfo.getId(), TvContract.buildChannelUri(list.get(0).mId));
        } else {
            if (1007 == mInputInfo.getType()) {
                setFocusWithChangeHdmi("main");
            } else if (mInputInfo.getType() == 0 && !CommonIntegration.getInstance().isPipOrPopState()) {
                if (this.specChannelID != -1) {
                    tifInfo2 = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(this.specChannelID);
                } else {
                    tifInfo2 = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
                }
                if (tifInfo2 != null) {
                    uri = ContentUris.withAppendedId(TvContract.Channels.CONTENT_URI, tifInfo2.mId);
                }
            }
            if ("main" == CommonIntegration.getInstance().getCurrentFocus()) {
                MtkLog.d(TAG, "startSession TV_FOCUS_WIN_MAIN  setStreamVolume 1.0f");
                TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(1.0f);
            } else {
                MtkLog.d(TAG, "startSession TV_FOCUS_WIN_SUB setStreamVolume 0.0f");
                TurnkeyUiMainActivity.getInstance().getTvView().setStreamVolume(0.0f);
            }
            if (mInputInfo.getType() != 0 || CommonIntegration.getInstance().isPipOrPopState()) {
                TurnkeyUiMainActivity.getInstance().getTvView().tune(mInputInfo.getId(), uri);
            } else {
                if (this.specChannelID != -1) {
                    tifInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(this.specChannelID);
                } else {
                    tifInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
                }
                if (tifInfo == null || this.specChannelID != -1) {
                    this.specChannelID = -1;
                    TurnkeyUiMainActivity.getInstance().getTvView().tune(mInputInfo.getId(), uri);
                } else {
                    TurnkeyUiMainActivity.getInstance().getTvView().tune(mInputInfo.getId(), MtkTvTISMsgBase.createFilterChannelUri(tifInfo.mId));
                }
            }
            if (inputname != null && !inputname.equalsIgnoreCase("TV")) {
            }
        }
    }

    public void stopSession() {
        MtkLog.d(TAG, "stopSession");
        Context context = this.mContext;
        Context context2 = this.mContext;
        PowerManager pm = (PowerManager) context.getSystemService("power");
        String str = TAG;
        MtkLog.d(str, "stopSession: pm.isInteractive():" + pm.isInteractive());
        if (!pm.isInteractive()) {
            this.screenOffFlag = true;
        }
        String pathString = CommonIntegration.getInstance().getCurrentFocus();
        if ((pathString.equalsIgnoreCase("main") && this.mMainTvInputInfo != null) || (pathString.equalsIgnoreCase("sub") && this.mSubTvInputInfo != null)) {
            lastTvInputInfoId = (pathString.equalsIgnoreCase("main") ? this.mMainTvInputInfo : this.mSubTvInputInfo).getId();
        }
        TurnkeyUiMainActivity.getInstance().getTvView().reset();
        TurnkeyUiMainActivity.getInstance().requestVisibleBehind(false);
        SaveValue.writeWorldStringValue(this.mContext, "multi_view_main_source_name", "Null", true);
        SaveValue.writeWorldInputType(this.mContext, 10);
    }

    public void stopSession(boolean isSetTVInfoNull) {
        String str = TAG;
        MtkLog.d(str, "stopSession:" + isSetTVInfoNull);
        if (isSetTVInfoNull) {
            this.mMainTvInputInfo = null;
        }
        TurnkeyUiMainActivity.getInstance().getTvView().reset();
        SaveValue.writeWorldStringValue(this.mContext, "multi_view_main_source_name", "Null", true);
    }

    private void startPipSession(TvInputInfo mInputInfo) {
        String str = TAG;
        MtkLog.d(str, "startPipSession, mInputInfo:" + mInputInfo);
        if (mInputInfo == null) {
            MtkLog.d(TAG, "startPipSession mInputInfo is null!!!");
            return;
        }
        this.mSubTvInputInfo = mInputInfo;
        if (CommonIntegration.getInstance().isPipOrPopState()) {
            MtkTvMultiView.getInstance().setNewTvMode(CommonIntegration.getInstance().getCurrentTVState());
        }
        if (mInputInfo != null && 1007 == mInputInfo.getType()) {
            setFocusWithChangeHdmi("sub");
        }
        if ("sub" == CommonIntegration.getInstance().getCurrentFocus()) {
            TurnkeyUiMainActivity.getInstance().getPipView().setStreamVolume(1.0f);
            MtkLog.d(TAG, "setStreamVolume(1.0f) sub");
        } else {
            TurnkeyUiMainActivity.getInstance().getPipView().setStreamVolume(0.0f);
            MtkLog.d(TAG, "setStreamVolume(0.0f) sub");
        }
        TurnkeyUiMainActivity.getInstance().getPipView().tune(mInputInfo.getId(), mUriSub);
    }

    public void stopPipSession() {
        MtkLog.d(TAG, "stopPipSession");
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
    }

    public void stopPipSession(boolean isSetTVInfoNull) {
        MtkLog.d(TAG, "stopPipSession");
        if (isSetTVInfoNull) {
            this.mSubTvInputInfo = null;
        }
        TurnkeyUiMainActivity.getInstance().getPipView().reset();
    }

    public boolean addListener(ISourceListListener listener) {
        this.mRigister.add(listener);
        return true;
    }

    public boolean removeListener(ISourceListListener listener) {
        this.mRigister.remove(listener);
        return true;
    }

    private class MtkInputSourceData {
        public static final String ATV = "ATV";
        public static final String DTV = "DTV";
        public static final String TV = "TV";
        public static final String Tuner_ATV = "Tuner(ATV)";
        public static final String Tuner_DTV = "Tuner(DTV)";
        private String CNATVInfo = InputSourceManager.DEFAULT_ATV_INPUT_ID;
        private String DTVInfo = "com.mediatek.tvinput/.tuner.TunerInputService/HW0";
        private String TUNER = "Tuner";
        /* access modifiers changed from: private */
        public final boolean[][] conflicts = {new boolean[]{true, true, true, false, false, false}, new boolean[]{true, true, true, false, false, false}, new boolean[]{true, true, true, true, true, false}, new boolean[]{false, false, true, true, true, false}, new boolean[]{false, false, true, true, true, false}, new boolean[]{false, false, false, false, false, true}};
        public List<MtkTvInputSourceBase.InputSourceRecord> mRecordList = new ArrayList();
        private final ArrayList<String> mUiSourceList = new ArrayList<>();
        public int size = 0;
        /* access modifiers changed from: private */
        public final String[] sourceLiast = {MtkTvInputSourceBase.INPUT_TYPE_TV, MtkTvInputSourceBase.INPUT_TYPE_COMPOSITE, MtkTvInputSourceBase.INPUT_TYPE_SCART, MtkTvInputSourceBase.INPUT_TYPE_COMPONENT, MtkTvInputSourceBase.INPUT_TYPE_VGA, MtkTvInputSourceBase.INPUT_TYPE_HDMI};

        public MtkInputSourceData() {
            reload();
        }

        public void reload() {
            String sourceTypeName;
            this.size = InputSourceManager.this.mTvInputSource.getInputSourceTotalNumber();
            this.mRecordList.clear();
            this.mUiSourceList.clear();
            String access$100 = InputSourceManager.TAG;
            MtkLog.d(access$100, "reload, size == " + this.size);
            for (int i = 0; i < this.size; i++) {
                MtkTvInputSourceBase.InputSourceRecord record = new MtkTvInputSourceBase.InputSourceRecord();
                InputSourceManager.this.mTvInputSource.getInputSourceRecbyidx(i, record);
                this.mRecordList.add(record);
                if (MarketRegionInfo.getCurrentMarketRegion() == 0 || CommonIntegration.isEUPARegion()) {
                    MtkLog.d(InputSourceManager.TAG, "reload, MarketRegionInfo.REGION_CN");
                    sourceTypeName = InputSourceManager.this.mTvInputSource.getInputSourceNamebySourceid(record.getId());
                    String access$1002 = InputSourceManager.TAG;
                    MtkLog.d(access$1002, "reload, sourceTypeName == " + sourceTypeName);
                } else {
                    MtkLog.d(InputSourceManager.TAG, "reload, !MarketRegionInfo.REGION_CN");
                    sourceTypeName = record.getInputType().name();
                }
                if (!TextUtils.isEmpty(sourceTypeName)) {
                    if (MarketRegionInfo.getCurrentMarketRegion() == 0 || CommonIntegration.isEUPARegion()) {
                        this.mUiSourceList.add(sourceTypeName);
                    } else if (sourceTypeName.equalsIgnoreCase("TV")) {
                        this.mUiSourceList.add("TV");
                    } else if (sourceTypeName.equalsIgnoreCase("COMPOSITE")) {
                        int compositeNum = record.getInternalIdx();
                        if (compositeNum > 0) {
                            if (compositeNum == 1) {
                                this.mUiSourceList.set(this.mUiSourceList.size() - 1, "Composite " + compositeNum);
                            }
                            ArrayList<String> arrayList = this.mUiSourceList;
                            arrayList.add("Composite " + (compositeNum + 1));
                        } else {
                            this.mUiSourceList.add("Composite");
                        }
                    } else if (sourceTypeName.equalsIgnoreCase("COMPONENT")) {
                        this.mUiSourceList.add("Component");
                    } else if (sourceTypeName.equalsIgnoreCase("VGA")) {
                        this.mUiSourceList.add("VGA");
                    } else if (sourceTypeName.equalsIgnoreCase("HDMI")) {
                        String access$1003 = InputSourceManager.TAG;
                        MtkLog.d(access$1003, "reload, mTvInputSource.getMhlPortNum():" + InputSourceManager.this.mTvInputSource.getMhlPortNum());
                        ArrayList<String> arrayList2 = this.mUiSourceList;
                        arrayList2.add("HDMI " + (record.getInternalIdx() + 1));
                    } else if (sourceTypeName.equalsIgnoreCase("RESERVED")) {
                        this.mUiSourceList.add("RESERVED");
                    } else if (sourceTypeName.equalsIgnoreCase("SCART")) {
                        this.mUiSourceList.add("SCART");
                    } else if (sourceTypeName.equalsIgnoreCase("SVIDEO")) {
                        this.mUiSourceList.add("SVIDEO");
                    }
                }
            }
            String access$1004 = InputSourceManager.TAG;
            MtkLog.d(access$1004, "reload, mUiSourceList:" + this.mUiSourceList);
            reloadConflictSourceList();
        }

        public void reloadConflictSourceList() {
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    int i = 0;
                    while (i < MtkInputSourceData.this.sourceLiast.length) {
                        try {
                            int inputId1 = MtkInputSourceData.this.getSourceIdByName(MtkInputSourceData.this.sourceLiast[i]);
                            if (inputId1 != -1) {
                                for (int j = i; j < MtkInputSourceData.this.sourceLiast.length; j++) {
                                    int inputId2 = MtkInputSourceData.this.getSourceIdByName(MtkInputSourceData.this.sourceLiast[j]);
                                    if (inputId2 != -1) {
                                        boolean[] zArr = MtkInputSourceData.this.conflicts[i];
                                        boolean[] zArr2 = MtkInputSourceData.this.conflicts[j];
                                        boolean queryConflict = InputSourceManager.this.mTvInputSource.queryConflict(inputId1, inputId2);
                                        zArr2[i] = queryConflict;
                                        zArr[j] = queryConflict;
                                        String access$100 = InputSourceManager.TAG;
                                        MtkLog.d(access$100, "i:" + i + ", j:" + j + ", conflicts:" + MtkInputSourceData.this.conflicts[i][j]);
                                    }
                                }
                            }
                            i++;
                        } catch (Exception ex) {
                            MtkLog.d(InputSourceManager.TAG, ex.toString());
                            return;
                        }
                    }
                }
            });
        }

        public int getSourceIdByName(String name) {
            String tmpName;
            MtkLog.d(InputSourceManager.TAG, "getSourceIdByName, size:" + this.size + ", name:" + name);
            TvInputInfo mInputInfo = (TvInputInfo) InputSourceManager.mInputMap.get(name);
            if (!(mInputInfo == null || 1007 != mInputInfo.getType() || mInputInfo.getHdmiDeviceInfo() == null)) {
                name = "HDMI " + mInputInfo.getHdmiDeviceInfo().getPortId();
            }
            for (int i = 0; i < this.size; i++) {
                MtkTvInputSourceBase.InputSourceRecord record = this.mRecordList.get(i);
                if (MarketRegionInfo.getCurrentMarketRegion() == 0 || CommonIntegration.isEUPARegion()) {
                    tmpName = InputSourceManager.this.mTvInputSource.getInputSourceNamebySourceid(record.getId());
                    MtkLog.d(InputSourceManager.TAG, "getSourceIdByName, REGION_CN/PA, tmpName == " + tmpName);
                } else {
                    MtkLog.d(InputSourceManager.TAG, "getSourceIdByName, !REGION_CN/PA");
                    tmpName = record.getInputType().name();
                }
                if (tmpName == null) {
                    tmpName = "";
                }
                if (name.equalsIgnoreCase(tmpName)) {
                    return record.getId();
                }
                MtkLog.d(InputSourceManager.TAG, "tmpName:" + tmpName + "," + record.getInternalIdx() + "  name=" + name);
                if (name.toUpperCase().startsWith(tmpName) && name.contains(String.valueOf(record.getInternalIdx() + 1))) {
                    return record.getId();
                }
            }
            MtkLog.d(InputSourceManager.TAG, "can not found input source id");
            return -1;
        }

        public boolean queryConflict(String srcName1, String srcName2) {
            if (!this.mUiSourceList.contains(srcName1)) {
                srcName1 = MtkTvInputSourceBase.INPUT_TYPE_HDMI;
            }
            if (!this.mUiSourceList.contains(srcName2)) {
                srcName2 = MtkTvInputSourceBase.INPUT_TYPE_HDMI;
            }
            int index1 = getIndexByName(srcName1);
            int index2 = getIndexByName(srcName2);
            if (index1 < 0 || index1 >= this.conflicts.length || index2 < 0 || index2 >= this.conflicts.length) {
                return false;
            }
            return this.conflicts[index1][index2];
        }

        public String getSourceNameByTis(String tisName, TvInputInfo info) {
            String access$100 = InputSourceManager.TAG;
            MtkLog.d(access$100, "getSourceNameByTis, tisName:" + tisName);
            String param1 = tisName.toLowerCase();
            String access$1002 = InputSourceManager.TAG;
            MtkLog.d(access$1002, "getSourceNameByTis,MarketRegionInfo.getCurrentMarketRegion()==" + MarketRegionInfo.getCurrentMarketRegion());
            String access$1003 = InputSourceManager.TAG;
            MtkLog.d(access$1003, "info.getID()==" + info.getId());
            if (MarketRegionInfo.getCurrentMarketRegion() == 0 || CommonIntegration.isEUPARegion()) {
                if (info.getId().equalsIgnoreCase(this.DTVInfo) && this.TUNER.equalsIgnoreCase(param1)) {
                    MtkLog.d(InputSourceManager.TAG, "getSourceNameByTis, come in DTV");
                    return "DTV";
                } else if (info.getId().equalsIgnoreCase(this.CNATVInfo) && this.TUNER.equalsIgnoreCase(param1)) {
                    MtkLog.d(InputSourceManager.TAG, "getSourceNameByTis, come in ATV");
                    return "ATV";
                }
            } else if (info.getId().equalsIgnoreCase(this.DTVInfo) && this.TUNER.equalsIgnoreCase(param1)) {
                MtkLog.d(InputSourceManager.TAG, "return TV");
                return "TV";
            } else if (info.getId().startsWith("com.mediatek.tvinput/.tuner.TunerInputService") && this.TUNER.equalsIgnoreCase(param1)) {
                return null;
            }
            String access$1004 = InputSourceManager.TAG;
            MtkLog.d(access$1004, "getSourceNameByTis, mUiSourceList:" + this.mUiSourceList);
            for (int i = 0; i < this.mUiSourceList.size(); i++) {
                String param2 = this.mUiSourceList.get(i).toLowerCase();
                String access$1005 = InputSourceManager.TAG;
                MtkLog.d(access$1005, "getSourceNameByTis, param2:" + param2);
                if (param2.startsWith(param1) || param1.endsWith(param2)) {
                    String access$1006 = InputSourceManager.TAG;
                    MtkLog.d(access$1006, "getSourceNameByTis, tisName:" + tisName + "," + this.mUiSourceList.get(i));
                    return this.mUiSourceList.get(i);
                }
            }
            if (!MarketRegionInfo.isFunctionSupport(27)) {
                return null;
            }
            MtkLog.d(InputSourceManager.TAG, "getSourceNameByTis, MarketRegionInfo.F_NEW_APP");
            return tisName;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String getSourceNameByTvapi(java.lang.String r4) {
            /*
                r3 = this;
                com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager r0 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.this
                com.mediatek.twoworlds.tv.MtkTvInputSource r0 = r0.mTvInputSource
                java.lang.String r0 = r0.getCurrentInputSourceName(r4)
                if (r0 != 0) goto L_0x000f
                java.lang.String r1 = ""
                return r1
            L_0x000f:
                r1 = 0
            L_0x0010:
                java.util.ArrayList r2 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.sourceList
                int r2 = r2.size()
                if (r1 >= r2) goto L_0x0038
                java.util.ArrayList r2 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.sourceList
                java.lang.Object r2 = r2.get(r1)
                java.lang.String r2 = (java.lang.String) r2
                boolean r2 = r2.startsWith(r0)
                if (r2 == 0) goto L_0x0035
                java.util.ArrayList r2 = com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.sourceList
                java.lang.Object r2 = r2.get(r1)
                r0 = r2
                java.lang.String r0 = (java.lang.String) r0
            L_0x0035:
                int r1 = r1 + 1
                goto L_0x0010
            L_0x0038:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.MtkInputSourceData.getSourceNameByTvapi(java.lang.String):java.lang.String");
        }

        public void reLayoutSourceList(ArrayList<String> srcList) {
            MtkLog.d(InputSourceManager.TAG, "reLayoutSourceList,srcList1 ==" + srcList);
            ArrayList<String> temp = (ArrayList) srcList.clone();
            srcList.clear();
            MtkLog.d(InputSourceManager.TAG, "reLayoutSourceList,mUiSourceList ==" + this.mUiSourceList);
            int i = 0;
            for (int i2 = 0; i2 < this.mUiSourceList.size(); i2++) {
                if (temp.contains(this.mUiSourceList.get(i2))) {
                    srcList.add(this.mUiSourceList.get(i2));
                }
            }
            MtkLog.d(InputSourceManager.TAG, "reLayoutSourceList,srcList2 ==" + srcList);
            if (MarketRegionInfo.isFunctionSupport(27)) {
                for (int i3 = 0; i3 < srcList.size(); i3++) {
                    temp.remove(srcList.get(i3));
                }
                if (temp.size() > 0) {
                    while (true) {
                        int i4 = i;
                        if (i4 < temp.size() && i4 < srcList.size()) {
                            MtkLog.d(InputSourceManager.TAG, "new app, i = " + i4);
                            if (!temp.get(i4).startsWith("HDMI ")) {
                                srcList.add(i4, temp.get(i4));
                                MtkLog.d(InputSourceManager.TAG, "new app after, temp.get(i) = " + temp.get(i4));
                            }
                            i = i4 + 1;
                        } else {
                            return;
                        }
                    }
                }
            }
        }

        private int getIndexByName(String srcName) {
            for (int i = 0; i < this.sourceLiast.length; i++) {
                if (srcName.toLowerCase().contains(this.sourceLiast[i])) {
                    return i;
                }
            }
            return -1;
        }

        public ArrayList<String> getSourceList() {
            return this.mUiSourceList;
        }
    }

    public String querySourceNameWithAnother(String sourceName) {
        ArrayList<String> list = (ArrayList) sourceList.clone();
        Iterator<String> it = sourceList.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (this.mtkInputSrcs.queryConflict(sourceName, s) || CommonIntegration.is3rdTVSource(mInputMap.get(s)) || !isSourceEnable(s)) {
                list.remove(s);
            }
        }
        String str = TAG;
        MtkLog.d(str, "list: " + list);
        if (list == null || list.size() <= 0) {
            return "";
        }
        return list.get(0);
    }

    public void saveOutputSourceName(String inputSourceName, String path) {
        if ("main".equals(path)) {
            this.mSaveValue.saveStrValue("multi_view_main_source_name", inputSourceName);
            SaveValue.writeWorldStringValue(this.mContext, "multi_view_main_source_name", inputSourceName, true);
            return;
        }
        this.mSaveValue.saveStrValue("multi_view_sub_source_name", inputSourceName);
    }

    public boolean isSourceEnable(String sourceName) {
        TvInputInfo sourceInfo = mInputMap.get(sourceName);
        if (sourceInfo == null) {
            return false;
        }
        int sourceState = this.mTvInputManager.getInputState(sourceInfo.getId());
        String str = TAG;
        Log.d(str, "isSourceEnable,sourceName =" + sourceName + ", sourceState = " + sourceState);
        if (sourceState == 0) {
            return true;
        }
        return false;
    }

    public boolean isConflicted(String sourceName1, String sourceName2) {
        TvInputInfo inputInfo = mInputMap.get(sourceName2);
        String str = TAG;
        MtkLog.d(str, "sourceName2: " + sourceName2 + ",sourceName2 is3rdTVSource: " + CommonIntegration.is3rdTVSource(inputInfo));
        if (!sourceName1.contains("TV") || !CommonIntegration.is3rdTVSource(inputInfo)) {
            return this.mtkInputSrcs.queryConflict(sourceName1, sourceName2);
        }
        return true;
    }

    public void swapMainAndSubSource() {
        String mainSourceName = getCurrentInputSourceName("main");
        saveOutputSourceName(getCurrentInputSourceName("sub"), "main");
        saveOutputSourceName(mainSourceName, "sub");
    }

    public String getCurrentInputSourceNameByTVAPI(String path) {
        return this.mtkInputSrcs.getSourceNameByTvapi(path);
    }

    public List<String> getChannelSourcesList() {
        MtkLog.d(TAG, "getChannelSourcesList: channelSourcesList000");
        ArrayList<String> channelSourcesList = null;
        if (!(mSourceNameMap == null || mInputMap == null)) {
            channelSourcesList = new ArrayList<>();
            for (String obj : mSourceNameMap.keySet()) {
                String name = mSourceNameMap.get(obj.toString());
                MtkLog.d(TAG, "getChannelSourcesList: name:" + name);
                String lastName = name + getSourceDetail(mInputMap.get(name));
                channelSourcesList.add(lastName);
                MtkLog.d(TAG, "getChannelSourcesList lastName: " + lastName + ", name: " + name);
            }
            MtkLog.d(TAG, "getChannelSourcesList: channelSourcesList:" + channelSourcesList);
        }
        return channelSourcesList;
    }

    public String getCurrentChannelSourceName() {
        return getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus());
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateInputsourceListShowing() {
        /*
            r3 = this;
            r0 = 0
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            r2 = 16777230(0x100000e, float:2.3509926E-38)
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r1 = r1.getComponentById(r2)
            if (r1 == 0) goto L_0x0019
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic r1 = r1.getComponentById(r2)
            r0 = r1
            com.mediatek.wwtv.tvcenter.nav.view.SourceListView r0 = (com.mediatek.wwtv.tvcenter.nav.view.SourceListView) r0
        L_0x0019:
            if (r0 == 0) goto L_0x0027
            boolean r1 = r0.isShowing()
            if (r1 == 0) goto L_0x0027
            r0.updateSrcList()
            r0.updateSourceListSelection()
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager.updateInputsourceListShowing():void");
    }

    public TvInputInfo getTvInputInfo() {
        return mInputMap.get(getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus()));
    }

    public void updateConflictSourceList() {
        MtkLog.d(TAG, "updateConflictSourceList()");
        this.mtkInputSrcs.reloadConflictSourceList();
    }

    private String getSourceDetail(TvInputInfo tvInputInfo) {
        List<TIFChannelInfo> list;
        if (tvInputInfo == null || (list = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoBySource(tvInputInfo.getId())) == null || list.size() <= 0) {
            return " (Not set up)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        sb.append(list.size());
        sb.append(list.size() > 1 ? " channels)" : " channel)");
        return sb.toString();
    }

    public ApplicationInfo getTvInputAppInfo(String inputId) {
        return DestroyApp.getSingletons().getTvInputManagerHelper().getTvInputAppInfo(inputId);
    }

    public boolean isSourceSetupDisplay() {
        boolean z;
        synchronized (InputSourceManager.class) {
            z = this.mNeedShowSetup;
        }
        return z;
    }

    public void retryLoadSourceListAfterStartSession() {
        this.mHandler.postDelayed(this.retryLoadSourceListAfterStartSessionFailedRunnable, 200);
    }

    public int getHardwareIdByOriginalSourceName(String name) {
        if (TextUtils.isEmpty(name)) {
            return -1;
        }
        int size = this.mTvInputSource.getInputSourceTotalNumber();
        int i = 0;
        while (i < size) {
            MtkTvInputSourceBase.InputSourceRecord record = new MtkTvInputSourceBase.InputSourceRecord();
            this.mTvInputSource.getInputSourceRecbyidx(i, record);
            String sourceName = this.mTvInputSource.getInputSourceNamebySourceid(record.getId());
            String str = TAG;
            MtkLog.d(str, "each records, name:" + sourceName);
            if (TextUtils.isEmpty(sourceName) || !sourceName.equalsIgnoreCase(name)) {
                i++;
            } else {
                AbstractInput abstractInput = InputUtil.getInput(record.getId() << 16);
                if (abstractInput == null || abstractInput.isNeedAbortTune()) {
                    return -1;
                }
                return record.getId() << 16;
            }
        }
        return -1;
    }
}
