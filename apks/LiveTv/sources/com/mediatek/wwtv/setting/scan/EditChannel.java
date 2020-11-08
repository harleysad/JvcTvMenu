package com.mediatek.wwtv.setting.scan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelListBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvTimeFormat;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.setting.base.scan.model.DVBTCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBTScanner;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MessageType;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EditChannel {
    public static final int AFT = 5;
    public static final int COLOR = 3;
    private static final int FINECOUNT = 200;
    public static final int FREQUENCY = 2;
    /* access modifiers changed from: private */
    public static int SELECTCHANNEL = 999;
    public static final int SKIP = 7;
    public static final int SOUND = 4;
    private static final String TAG = "EditChannel";
    private static EditChannel mEditChannel = null;
    private final MtkTvAppTVBase appTV;
    private boolean isBlockForTest;
    private boolean isFineTuneDone = true;
    private boolean isStored = true;
    private float mCenterFrequency = -1.0f;
    /* access modifiers changed from: private */
    public final CommonIntegration mCommonInter;
    private final Context mContext;
    private final int mFineCount = 200;
    private final float mFineTuneMax = 1.5f;
    private final float mFineTuneStep = 0.065f;
    private Handler mHandler;
    private int mOriginalColorSystem;
    private float mOriginalFrequency;
    private int mOriginalSoundSystem;
    private float mRestoreHz = 0.0f;
    private final TVContent mTVContent;
    private final MenuConfigManager mcf;
    private int powerOnChannelNum = 0;
    /* access modifiers changed from: private */
    public final SaveValue sv;

    private EditChannel(Context context) {
        this.mContext = context;
        this.mTVContent = TVContent.getInstance(this.mContext);
        this.mCommonInter = CommonIntegration.getInstance();
        this.appTV = new MtkTvAppTVBase();
        this.sv = SaveValue.getInstance(context);
        this.mcf = MenuConfigManager.getInstance(this.mContext);
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == EditChannel.SELECTCHANNEL) {
                    EditChannel.this.mCommonInter.selectChannelByInfo((MtkTvChannelInfoBase) msg.obj);
                }
                super.handleMessage(msg);
            }
        };
    }

    public static synchronized EditChannel getInstance(Context context) {
        EditChannel editChannel;
        synchronized (EditChannel.class) {
            if (mEditChannel == null) {
                mEditChannel = new EditChannel(context);
            }
            editChannel = mEditChannel;
        }
        return editChannel;
    }

    public float getRestoreHZ() {
        return this.mRestoreHz;
    }

    public void setRestoreHZ(float restoreHZ) {
        this.mRestoreHz = restoreHZ;
    }

    public boolean getStoredFlag() {
        return this.isStored;
    }

    public void setOriginalTVSystem(int colorSystem, int soundSystem) {
        this.mOriginalColorSystem = colorSystem;
        this.mOriginalSoundSystem = soundSystem;
    }

    public void setOriginalFrequency(float frequency) {
        this.mOriginalFrequency = frequency;
    }

    public void setStoredFlag(boolean isStored2) {
        this.isStored = isStored2;
    }

    public void swapChannel(int from, int to) {
    }

    public void insertChannel(int from, int to) {
        if (MenuDataHelper.getInstance(this.mContext).getCh_list() == null || from < to) {
        }
    }

    public List<MtkTvChannelInfoBase> deleteChannel(int deleteId) {
        List<MtkTvChannelInfoBase> list = MenuDataHelper.getInstance(this.mContext).getCh_list();
        if (list != null && deleteId < list.size()) {
            deleteInactiveChannel(list.get(deleteId).getChannelId());
            list.remove(deleteId);
        }
        return list;
    }

    public boolean deleteInactiveChannel(int channelId) {
        MtkTvChannelInfoBase selChannel = TIFChannelManager.getInstance(this.mContext).getAPIChannelInfoById(channelId);
        MtkLog.d(TAG, "deleteInactiveChannel selChannel>>>" + selChannel);
        if (selChannel == null) {
            return false;
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        selChannel.setChannelDeleted(true);
        int operator = 1;
        if (selChannel.isAnalogService()) {
            operator = 2;
        }
        int nwMask = selChannel.getNwMask() & -241;
        int nwMask2 = nwMask;
        selChannel.setNwMask(nwMask);
        list.add(selChannel);
        this.mCommonInter.setChannelList(operator, list);
        List<TIFChannelInfo> tifChannelInfoList = null;
        if (channelId == this.mCommonInter.getCurrentChannelId()) {
            tifChannelInfoList = TIFChannelManager.getInstance(this.mContext).getTIFPreOrNextChannelList(channelId, false, false, 1, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        }
        if (tifChannelInfoList != null && tifChannelInfoList.size() > 0) {
            TIFChannelManager.getInstance(this.mContext).selectChannelByTIFId(tifChannelInfoList.get(0).mId);
        }
        return true;
    }

    public boolean deleteAllInactiveChannels(List<TIFChannelInfo> inactiveChannelList) {
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        for (TIFChannelInfo tempInfo : inactiveChannelList) {
            list.add(TIFChannelManager.getInstance(this.mContext).getAPIChannelInfoById(tempInfo.mMtkTvChannelInfo.getChannelId()));
        }
        MtkLog.d(TAG, "deleteAllInactiveChannels list.size()>>>" + list.size());
        if (list.size() <= 0) {
            return false;
        }
        this.mCommonInter.setChannelList(2, list);
        return true;
    }

    public void restoreFineTune() {
        MtkTvChannelInfoBase channel = this.mCommonInter.getCurChInfoByTIF();
        channel.setFrequency((int) (this.mRestoreHz * 1000000.0f));
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(channel);
        this.mCommonInter.setChannelList(1, list);
        exitFinetune(this.mRestoreHz);
    }

    public void saveFineTune() {
        MtkTvChannelInfoBase channel = this.mCommonInter.getCurChInfoByTIF();
        channel.setFrequency((int) (this.mOriginalFrequency * 1000000.0f));
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(channel);
        this.mCommonInter.setChannelList(1, list);
        exitFinetune(this.mOriginalFrequency);
    }

    public int getCurrentChannelNumber() {
        MtkTvChannelInfoBase currentChannel = this.mCommonInter.getCurChInfoByTIF();
        if (currentChannel != null) {
            return currentChannel.getChannelNumber();
        }
        return 0;
    }

    public int getCurrentChannelId() {
        return this.mCommonInter.getCurrentChannelId();
    }

    public void selectChannel(int lastChannelId) {
        if (isCurrentSourceTv() && this.mCommonInter.getChannelActiveNumByAPIForScan() > 0) {
            if (this.mCommonInter.selectChannelById(lastChannelId)) {
                MtkLog.i(TAG, "selectChannel lastChannelId:" + lastChannelId);
                return;
            }
            SaveValue instance = SaveValue.getInstance(this.mContext);
            instance.saveValue(CommonIntegration.CH_TYPE_BASE + CommonIntegration.getInstance().getSvl(), 0);
            List<TIFChannelInfo> tempList = TIFChannelManager.getInstance(this.mContext).getTIFPreOrNextChannelList(-1, false, false, 2, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
            StringBuilder sb = new StringBuilder();
            sb.append("selectChannel first channel:");
            sb.append(tempList == null ? null : Integer.valueOf(tempList.size()));
            MtkLog.i(TAG, sb.toString());
            if (tempList != null && tempList.size() > 0) {
                TIFChannelManager.getInstance(this.mContext).selectChannelByTIFId(tempList.get(0).mId);
            }
        }
    }

    public MtkTvChannelInfoBase getSelectChannel(int lastChannelId) {
        if (this.mCommonInter.getChannelAllNumByAPI() <= 0) {
            return null;
        }
        MtkTvChannelInfoBase channel = this.mCommonInter.getChannelById(lastChannelId);
        if (channel != null) {
            return channel;
        }
        MtkLog.i(TAG, "selectChannel first channel:");
        int i = 0;
        List<MtkTvChannelInfoBase> chList = this.mCommonInter.getChList(0, 0, 3);
        if (chList == null) {
            return null;
        }
        int index = 0;
        while (true) {
            int i2 = i;
            if (i2 >= chList.size()) {
                break;
            }
            MtkLog.i(TAG, "selectChannel first channel:" + chList.get(i2).getNwMask() + "SB_VNET_FAKE:" + MtkTvChCommonBase.SB_VNET_FAKE);
            if ((chList.get(i2).getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE) {
                index = i2;
                break;
            }
            i = i2 + 1;
        }
        if (index < chList.size()) {
            return chList.get(index);
        }
        return channel;
    }

    public void storeChannel(int channelNumber, String channelName, String channelFrequency, int colorSystem, int soundSystem, int autoFineTune, int skip) {
        MtkLog.d(TAG, "channelNumber>>" + channelNumber + ">>" + channelName + ">>" + channelFrequency + ">>>" + colorSystem + ">>>" + soundSystem + ">>>" + autoFineTune + ">>>" + skip);
        MtkTvChannelInfoBase currentChannel = this.mCommonInter.getCurChInfoByTIF();
        if (currentChannel != null) {
            currentChannel.setChannelNumber(channelNumber);
            currentChannel.setServiceName(channelName);
            int pwdShow = MtkTvPWDDialog.getInstance().PWDShow();
            boolean z = false;
            if (currentChannel instanceof MtkTvAnalogChannelInfo) {
                currentChannel.setFrequency((int) (Float.parseFloat(channelFrequency) * 1000000.0f));
                if (pwdShow != 1) {
                    ((MtkTvAnalogChannelInfo) currentChannel).setNoAutoFineTune(true);
                } else {
                    ((MtkTvAnalogChannelInfo) currentChannel).setNoAutoFineTune(autoFineTune == 0);
                }
            }
            if (skip != 0) {
                z = true;
            }
            currentChannel.setSkip(z);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(currentChannel);
            this.mCommonInter.setChannelList(1, list);
            this.isStored = true;
            this.mOriginalColorSystem = colorSystem;
            this.mOriginalSoundSystem = soundSystem;
        }
    }

    public boolean isFineTuneDone() {
        return this.isFineTuneDone;
    }

    public void exitFinetune() {
        int frequency = this.mCommonInter.getCurChInfoByTIF().getFrequency();
        this.mCenterFrequency = -1.0f;
        MtkLog.d(TAG, "++++++++ Call exitFinetune ++++++++");
        if (CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            MtkLog.i(TAG, " SUB TV");
            this.appTV.setFinetuneFreq("sub", frequency, true);
        } else {
            MtkLog.i(TAG, " MAIN TV");
            this.appTV.setFinetuneFreq("main", frequency, true);
        }
        this.isFineTuneDone = true;
    }

    public void exitFinetune(float frequency) {
        this.mCenterFrequency = -1.0f;
        MtkLog.d(TAG, "++++++++ Call exit fine tune ++++++++");
        if (CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            this.appTV.setFinetuneFreq("sub", (int) (1000000.0f * frequency), true);
        } else {
            this.appTV.setFinetuneFreq("main", (int) (1000000.0f * frequency), true);
        }
        this.isFineTuneDone = true;
    }

    public float fineTune(float originalMHZ, int keyCode) {
        float originalMHZ2;
        if (this.mCenterFrequency == -1.0f) {
            this.mCenterFrequency = ((float) ((MtkTvAnalogChannelInfo) this.mCommonInter.getCurChInfoByTIF()).getCentralFreq()) / 1000000.0f;
        }
        MtkLog.d(TAG, "keyCode:" + keyCode + "originalMHZ:" + originalMHZ);
        if (keyCode == 22) {
            originalMHZ2 = originalMHZ + 0.065f;
            if (originalMHZ2 >= this.mCenterFrequency + 1.5f) {
                originalMHZ2 = this.mCenterFrequency + 1.5f;
            }
        } else {
            originalMHZ2 = originalMHZ - 0.065f;
            if (originalMHZ2 < this.mCenterFrequency - 1.5f) {
                originalMHZ2 = this.mCenterFrequency - 1.5f;
            }
        }
        this.mOriginalFrequency = originalMHZ2;
        if (CommonIntegration.getInstance().getCurrentFocus().equalsIgnoreCase("sub")) {
            MtkLog.i(TAG, " SUB TV");
            this.appTV.setFinetuneFreq("sub", (int) (1000000.0f * originalMHZ2), false);
        } else {
            MtkLog.i(TAG, " MAIN TV");
            this.appTV.setFinetuneFreq("main", (int) (1000000.0f * originalMHZ2), false);
        }
        this.isFineTuneDone = false;
        return originalMHZ2;
    }

    public void saveFineTuneInfo() {
        SaveValue saveValue = this.sv;
        saveValue.saveStrValue("FineTune_RestoreHz", "" + this.mRestoreHz);
        SaveValue saveValue2 = this.sv;
        saveValue2.saveStrValue("FineTune_IsFineTuneDone", "" + this.isFineTuneDone);
    }

    public void cleanChannelList() {
        int i = this.mCommonInter.getSvl();
        if (CommonIntegration.isCNRegion()) {
            int brd_type = 7;
            if (i == 1) {
                brd_type = 7;
            } else if (InputSourceManager.getInstance(this.mContext).isCurrentATvSource(this.mCommonInter.getCurrentFocus())) {
                brd_type = 1;
            } else if (InputSourceManager.getInstance(this.mContext).isCurrentDTvSource(this.mCommonInter.getCurrentFocus())) {
                brd_type = 2;
            }
            MtkLog.d(TAG, "brd_type:" + brd_type);
            new MtkTvChannelListBase();
        } else {
            TvSingletons.getSingletons().getCommonIntegration();
            if (CommonIntegration.isEUPARegion()) {
                if (i == 1) {
                    MtkLog.d(TAG, "Svl == 1,Dvbt cleanChannelList");
                    MtkTvChannelListBase.cleanChannelList(i);
                } else {
                    int brd_type2 = 1;
                    String currFocus = this.mCommonInter.getCurrentFocus();
                    if (InputSourceManager.getInstance(this.mContext).isCurrentATvSource(currFocus)) {
                        brd_type2 = 1;
                    } else if (InputSourceManager.getInstance(this.mContext).isCurrentDTvSource(currFocus)) {
                        brd_type2 = 2;
                    }
                    MtkLog.d(TAG, "getSvl:" + i + "isEUPARegion brd_type" + brd_type2);
                    new MtkTvChannelListBase().deleteChannelByBrdcstType(i, brd_type2, true);
                }
            } else if (i == 3 || i == 4 || i == 7) {
                MtkTvChannelListBase.cleanChannelList(i, false);
            } else {
                MtkTvChannelListBase.cleanChannelList(i);
            }
        }
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    public boolean setPowerOnChannel(int channelid) {
        String cfgId;
        if (this.mTVContent.getCurrentTunerMode() == 0) {
            cfgId = "g_nav__air_on_time_ch";
        } else {
            cfgId = "g_nav__cable_on_time_ch";
        }
        if (this.mTVContent.getConfigValue(cfgId) <= 0) {
            this.powerOnChannelNum = channelid;
            this.mTVContent.setConfigValue(cfgId, channelid);
            return true;
        } else if (this.powerOnChannelNum == channelid) {
            this.mTVContent.setConfigValue(cfgId, new BigInteger("ffffffff", 16).intValue());
            return false;
        } else {
            this.powerOnChannelNum = channelid;
            this.mTVContent.setConfigValue(cfgId, channelid);
            return true;
        }
    }

    public void disablePowerOnChannel() {
        String cfgId;
        if (this.mTVContent.getCurrentTunerMode() == 0) {
            cfgId = "g_nav__air_on_time_ch";
        } else {
            cfgId = "g_nav__cable_on_time_ch";
        }
        this.mTVContent.setConfigValue(cfgId, new BigInteger("ffffffff", 16).intValue());
    }

    public void blockChannel(MtkTvChannelInfoBase selChannel, boolean blocked) {
        if (selChannel == null) {
            MtkLog.e(TAG, "selChannel can not equals null!");
            return;
        }
        MtkLog.d(TAG, " channel block previous state selChannel != null: " + blocked);
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(selChannel);
        blockChannel(list, blocked);
    }

    public void blockChannel(List<MtkTvChannelInfoBase> list, boolean blocked) {
        if (list == null || list.isEmpty()) {
            MtkLog.e(TAG, "list is null or empty!");
            return;
        }
        MtkTvChannelInfoBase selChannel = null;
        MtkTvChannelInfoBase currentChannel = this.mCommonInter.getCurChInfo();
        for (MtkTvChannelInfoBase channel : list) {
            channel.setBlock(blocked);
            if (currentChannel != null && currentChannel.equals(channel)) {
                selChannel = channel;
            }
        }
        this.mCommonInter.setChannelList(1, list);
        if (selChannel == null || this.mCommonInter.is3rdTVSource()) {
            MtkLog.e(TAG, "selChannel==null!");
            return;
        }
        this.mCommonInter.selectChannelByInfo(selChannel);
        this.appTV.unblockSvc(this.mCommonInter.getCurrentFocus(), false);
    }

    public void blockChannel(int chId, boolean blocked) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, " channel block previous state selChannel != null: " + blocked);
            selChannel.setBlock(blocked);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            MtkTvChannelInfoBase currentChannel2 = this.mCommonInter.getCurChInfo();
            if (currentChannel2 != null && currentChannel2.equals(selChannel)) {
                this.mCommonInter.selectChannelByInfo(selChannel);
                this.appTV.unblockSvc(this.mCommonInter.getCurrentFocus(), false);
            }
        }
    }

    public boolean isChannelBlock(int chId) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel == null) {
            return false;
        }
        boolean channelBlock = selChannel.isBlock();
        MtkLog.d(TAG, "channelBlock:" + channelBlock);
        return channelBlock;
    }

    public boolean isChannelSkip(int chId) {
        boolean channelSkip = false;
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            channelSkip = selChannel.isSkip();
        }
        MtkLog.d(TAG, "channelSkip:" + channelSkip);
        return channelSkip;
    }

    public boolean isChannelDecode(int chId) {
        boolean channelDecode = false;
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            if ((selChannel.getNwMask() & MtkTvChCommonBase.SB_VNET_USE_DECODER) == MtkTvChCommonBase.SB_VNET_USE_DECODER) {
                channelDecode = true;
            }
            MtkLog.d(TAG, channelDecode + "isChannelDecode");
        }
        return channelDecode;
    }

    public void channelSort(int ch_num_src, int ch_num_dst) {
        int firstchannelid;
        int secondchannelid;
        MtkLog.d(TAG, "channelSort:src:" + ch_num_src + ",dst:" + ch_num_dst);
        MtkTvChannelInfoBase selChannelSrc = this.mCommonInter.getChannelById(ch_num_src);
        MtkTvChannelInfoBase selChannelDst = this.mCommonInter.getChannelById(ch_num_dst);
        if (selChannelSrc == null || selChannelDst == null) {
            MtkLog.d(TAG, "channelSort channel == null:");
            return;
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        selChannelSrc.setOptionMask(selChannelSrc.getOptionMask() | 1024);
        selChannelDst.setOptionMask(selChannelDst.getOptionMask() | 1024);
        int currentId = this.mCommonInter.getCurrentChannelId();
        if (selChannelDst instanceof MtkTvAnalogChannelInfo) {
            firstchannelid = getNewChannelIdAna(ch_num_src, ch_num_dst);
        } else {
            firstchannelid = getNewChannelId(ch_num_src, ch_num_dst);
        }
        if (selChannelSrc instanceof MtkTvAnalogChannelInfo) {
            secondchannelid = getNewChannelIdAna(ch_num_dst, ch_num_src);
        } else {
            secondchannelid = getNewChannelId(ch_num_dst, ch_num_src);
        }
        selChannelSrc.setChannelId(firstchannelid);
        selChannelDst.setChannelId(secondchannelid);
        list.add(selChannelSrc);
        list.add(selChannelDst);
        MtkLog.d(TAG, "channelSort:curr:" + currentId + ",fst:" + firstchannelid + ",sec:" + secondchannelid);
        if (ch_num_src == currentId) {
            this.mCommonInter.setCurrentChannelId(firstchannelid);
        } else if (ch_num_dst == currentId) {
            this.mCommonInter.setCurrentChannelId(secondchannelid);
        }
        this.mCommonInter.setChannelList(1, list);
    }

    public void channelMoveForfusion(int ch_num_src, int ch_num_dst) {
        MtkLog.d(TAG, "channelSort:src:" + ch_num_src + ",dst:" + ch_num_dst);
        MtkTvChannelInfoBase selChannelSrc = this.mCommonInter.getChannelById(ch_num_src);
        MtkTvChannelInfoBase selChannelDst = this.mCommonInter.getChannelById(ch_num_dst);
        if (selChannelSrc == null || selChannelDst == null) {
            MtkLog.d(TAG, "channelSort channel == null:");
        } else {
            this.mCommonInter.setChannelListForfusion(ch_num_src, ch_num_dst);
        }
    }

    public int getNewChannelId(int firstsrc, int secDes) {
        MtkLog.i(TAG, "channelSort:getNewId firstsrc:" + firstsrc + ",secDes=" + secDes);
        int major = (secDes >> 18) & 16383;
        int i = (major & 16383) << 18;
        int newId = ((major & 16383) << 18) | (firstsrc & 15 & 15) | 128;
        MtkLog.i(TAG, "channelSort:getNewId:" + newId);
        return newId;
    }

    public int getNewChannelIdAna(int firstsrc, int secDes) {
        MtkLog.i(TAG, "channelSort:getNewId firstsrc:" + firstsrc + ",secDes=" + secDes);
        int major = (secDes >> 18) & 16383;
        int i = (major & 16383) << 18;
        int newId = ((major & 16383) << 18) | (0 & 63) | 128;
        MtkLog.i(TAG, "channelSort:getNewIdAna:" + newId);
        return newId;
    }

    public void setChannelSkip(int chId, boolean skip) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, "setSkip:" + skip);
            selChannel.setSkip(skip);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelSkip selChannel is null");
    }

    public void setChannelDecode(int chId, boolean decode) {
        int nwMask;
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, "setChannelDecode:" + decode);
            int nwMask2 = selChannel.getNwMask();
            if (decode) {
                nwMask = nwMask2 | MtkTvChCommonBase.SB_VNET_USE_DECODER;
            } else {
                nwMask = nwMask2 & (~MtkTvChCommonBase.SB_VNET_USE_DECODER);
            }
            selChannel.setNwMask(nwMask);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelDecode selChannel is null");
    }

    public int getSchBlockType(int chId) {
        return getSchBlockType(this.mCommonInter.getChannelById(chId));
    }

    public int getSchBlockType(MtkTvChannelInfoBase selChannel) {
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            int type = ((MtkTvAnalogChannelInfo) selChannel).getSchBlkType();
            MtkLog.d(TAG, "getSchBlkType MtkTvAnalogChannelInfo:" + type);
            return type;
        } else if ((selChannel instanceof MtkTvISDBChannelInfo) == 0) {
            return 0;
        } else {
            int type2 = ((MtkTvISDBChannelInfo) selChannel).getSchBlkType();
            MtkLog.d(TAG, "getSchBlkType MtkTvISDBChannelInfo:" + type2);
            return type2;
        }
    }

    private int getSchBlockFromUTCTime(MtkTvChannelInfoBase selChannel) {
        int time = 0;
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkEnable(1);
            time = ((MtkTvAnalogChannelInfo) selChannel).getSchBlkFromTime();
        } else if (selChannel instanceof MtkTvISDBChannelInfo) {
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkEnable(1);
            time = ((MtkTvISDBChannelInfo) selChannel).getSchBlkFromTime();
        }
        MtkLog.d(TAG, "getSchBlockFromUTCTime selChannel id:" + selChannel.getChannelId() + "time:" + time);
        return time;
    }

    public String getFromDate(MtkTvChannelInfoBase selChannel) {
        int mill = getSchBlockFromUTCTime(selChannel);
        MtkTvTimeFormat timeFormat = MtkTvTimeFormat.getInstance();
        timeFormat.set((long) mill);
        return dateFormat(timeFormat.year, timeFormat.month + 1, timeFormat.monthDay);
    }

    public String getFromTime(MtkTvChannelInfoBase selChannel) {
        int mill = getSchBlockFromUTCTime(selChannel);
        MtkTvTimeFormat timeFormat = MtkTvTimeFormat.getInstance();
        timeFormat.set((long) mill);
        return timeFormat(timeFormat.hour, timeFormat.minute);
    }

    public String getToDate(MtkTvChannelInfoBase selChannel) {
        int mill = getSchBlockTOUTCTime(selChannel);
        MtkTvTimeFormat timeFormat = MtkTvTimeFormat.getInstance();
        timeFormat.set((long) mill);
        return dateFormat(timeFormat.year, timeFormat.month + 1, timeFormat.monthDay);
    }

    public String getToTime(MtkTvChannelInfoBase selChannel) {
        int mill = getSchBlockTOUTCTime(selChannel);
        MtkTvTimeFormat timeFormat = MtkTvTimeFormat.getInstance();
        timeFormat.set((long) mill);
        return timeFormat(timeFormat.hour, timeFormat.minute);
    }

    private String dateFormat(int year, int month, int monthday) {
        String dateString;
        String dateString2;
        String dateString3 = year + "/";
        if (month < 10 && month > 0) {
            dateString = dateString3 + "0" + month;
        } else if (month == 0) {
            dateString = dateString3 + "01";
        } else {
            dateString = dateString3 + month;
        }
        if (monthday < 10) {
            dateString2 = dateString + "/0" + monthday;
        } else {
            dateString2 = dateString + "/" + monthday;
        }
        MtkLog.d(TAG, "dateFormat dateString:" + dateString2);
        return dateString2;
    }

    private String timeFormat(int hour, int minute) {
        String timeString;
        String timeString2 = "" + hour;
        if (hour < 10) {
            timeString2 = "0" + hour;
        }
        if (minute < 10) {
            timeString = timeString2 + ":0" + minute;
        } else {
            timeString = timeString2 + ":" + minute;
        }
        MtkLog.d(TAG, "timeFormat timeString:" + timeString);
        return timeString;
    }

    public int getSchBlockTOUTCTime(MtkTvChannelInfoBase selChannel) {
        int time = 0;
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkEnable(1);
            time = ((MtkTvAnalogChannelInfo) selChannel).getSchBlkToTime();
        } else if (selChannel instanceof MtkTvISDBChannelInfo) {
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkEnable(1);
            time = ((MtkTvISDBChannelInfo) selChannel).getSchBlkToTime();
        }
        MtkLog.d(TAG, "getSchBlockToUTCTime selChannel id:" + selChannel.getChannelId() + "time:" + time);
        return time;
    }

    public int setSchBlockFromUTCTime(int chId, long mill) {
        MtkLog.d(TAG, "setSchBlockFromUTCTime chId:" + chId + "mill:" + mill);
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkFromTime((int) mill);
        } else if (selChannel instanceof MtkTvISDBChannelInfo) {
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkFromTime((int) mill);
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(selChannel);
        this.mCommonInter.setChannelList(1, list);
        return 0;
    }

    public int setSchBlockTOUTCTime(int chId, long mill) {
        MtkLog.d(TAG, "setSchBlockTOUTCTime chId:" + chId + "mill:" + mill);
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkToTime((int) mill);
        } else if (selChannel instanceof MtkTvISDBChannelInfo) {
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkToTime((int) mill);
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(selChannel);
        this.mCommonInter.setChannelList(1, list);
        return 0;
    }

    public int setSchBlock(int chId, long from, long end, int type) {
        MtkLog.d(TAG, "setSchBlockTime chId:" + chId + "from:" + from + "end:" + end);
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel instanceof MtkTvAnalogChannelInfo) {
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkType(type);
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkFromTime((int) from);
            ((MtkTvAnalogChannelInfo) selChannel).setSchBlkToTime((int) end);
        } else if (selChannel instanceof MtkTvISDBChannelInfo) {
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkEnable(1);
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkType(type);
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkFromTime((int) from);
            ((MtkTvISDBChannelInfo) selChannel).setSchBlkToTime((int) end);
        }
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(selChannel);
        this.mCommonInter.setChannelList(1, list);
        return 0;
    }

    public void setChannelNumber(int chId, int number) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, "number:" + number + "  selChannel:" + selChannel);
            selChannel.setChannelNumber(number);
            MtkLog.d(TAG, "number:" + number + "  selChannel:" + selChannel);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelNumber selChannel is null");
    }

    public void setChannelNumber(int chId, int newId, int number) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, "number:" + number + "  selChannel:" + selChannel);
            selChannel.setChannelNumber(number);
            selChannel.setChannelId(newId);
            MtkLog.d(TAG, "number:" + number + "  selChannel:" + selChannel);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelNumber selChannel is null");
    }

    public void setChannelName(int chId, String name) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        MtkLog.d(TAG, "name:" + name + "  selChannel:" + selChannel);
        if (selChannel != null) {
            selChannel.setServiceName(name);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            return;
        }
        MtkLog.d(TAG, chId + "setChannelName selChannel is null");
    }

    public void setChannelFreq(int chId, String freq) {
        MtkTvChannelInfoBase selChannel = this.mCommonInter.getChannelById(chId);
        if (selChannel != null) {
            MtkLog.d(TAG, "freq:" + freq);
            selChannel.setFrequency((int) (Float.parseFloat(freq) * 1000000.0f));
            boolean z = selChannel instanceof MtkTvAnalogChannelInfo;
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(selChannel);
            this.mCommonInter.setChannelList(1, list);
            if (chId == getCurrentChannelId()) {
                MtkLog.d(TAG, "before stop:" + freq);
                this.mCommonInter.stopMainOrSubTv();
                selectChannel(getCurrentChannelId());
                MtkLog.d(TAG, " after select:" + freq);
                return;
            }
            return;
        }
        MtkLog.d(TAG, chId + "setChannelName selChannel is null");
    }

    public void setOpenVCHIP(int regionIndex, int dimIndex, int levIndex) {
        this.mTVContent.setOpenVChipSetting(regionIndex, dimIndex, levIndex);
    }

    public String getCurrentInput() {
        return "";
    }

    public boolean isCurrentSourceTv() {
        return this.mTVContent.isCurrentSourceTv();
    }

    public boolean isCurrentSourceBlocking() {
        return this.mTVContent.isCurrentSourceBlocking();
    }

    public boolean isTvInputBlock() {
        return this.mTVContent.isTvInputBlock();
    }

    public void setTVInput(String output) {
    }

    public void resetParental(final Context context, final Runnable runnable) {
        final List<String> inputSources = InputSourceManager.getInstance().getInputSourceList();
        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < inputSources.size(); i++) {
                }
                EditChannel.this.sv.saveStrValue("password", "1234");
                new Handler(context.getMainLooper()).post(runnable);
            }
        }).start();
    }

    public void setChannelAfterFreq() {
        new Handler(this.mContext.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                MtkLog.d(EditChannel.TAG, "biaoqing getCurrentChannelId:" + EditChannel.this.getCurrentChannelId());
                EditChannel.this.mCommonInter.stopMainOrSubTv();
                EditChannel.this.selectChannel(EditChannel.this.getCurrentChannelId());
            }
        }, MessageType.delayMillis5);
    }

    public int getBlockChannelNum() {
        return this.mCommonInter.getBlockChannelNum();
    }

    public int getBlockChannelNumForSource() {
        return this.mCommonInter.getBlockChannelNumForSource();
    }

    public List<MtkTvChannelInfoBase> getChannelList() {
        if (CommonIntegration.isSARegion()) {
            int length = this.mCommonInter.getChannelAllNumByAPI();
            MtkLog.d(TAG, "getChannelList length " + length);
            return this.mCommonInter.getChList(0, 0, length);
        }
        int length2 = this.mCommonInter.getChannelAllNumByAPI();
        MtkLog.d(TAG, "getChannelList length " + length2);
        return this.mCommonInter.getChannelList(0, 0, length2, MtkTvChCommonBase.SB_VNET_ALL);
    }

    public boolean isHasAnalog() {
        List<MtkTvChannelInfoBase> ch_list = getChannelList();
        if (ch_list == null) {
            return false;
        }
        for (int i = 0; i < ch_list.size(); i++) {
            if (this.mTVContent.isAnalog(ch_list.get(i))) {
                return true;
            }
        }
        return false;
    }

    public int getSignalLevel() {
        if (this.mTVContent == null) {
            return 0;
        }
        int level = this.mTVContent.getSignalLevel();
        MtkLog.d(TAG, "getSignalLevel: " + level);
        if (level < 0) {
            return 0;
        }
        if (level > 100) {
            return 100;
        }
        return level;
    }

    public int getSignalQuality() {
        if (this.mTVContent == null) {
            return 0;
        }
        if (CommonIntegration.isEURegion() || CommonIntegration.isUSRegion()) {
            int ber = this.mTVContent.getSignalQuality();
            MtkLog.d(TAG, "eu  or us signal quality --ber: " + ber);
            if (ber > 100) {
                return 100;
            }
            return ber;
        } else if (!CommonIntegration.isSARegion() && !CommonIntegration.isCNRegion()) {
            return 0;
        } else {
            int bervalue = this.appTV.GetSignalBER(CommonIntegration.getInstance().getCurrentFocus());
            MtkLog.d(TAG, "sa  or cn signal quality --bervalue: " + bervalue);
            if (bervalue >= 0 && bervalue <= 20) {
                return 2;
            }
            if (bervalue <= 20 || bervalue > 380) {
                return 0;
            }
            return 1;
        }
    }

    public void tuneDVBTRFSignal() {
        MtkLog.d("MenuMain", "tuneSignal enter>>" + DVBTScanner.selectedRFChannelFreq + ">>>" + DVBTCNScanner.selectedRFChannelFreq);
        int turn = this.mTVContent.getCurrentTunerMode();
        int conType = 2;
        int freqType = 1;
        if (turn == 0) {
            conType = 2;
            freqType = 0;
        } else if (turn == 1) {
            conType = 1;
            freqType = 1;
        }
        int frequency = DVBTScanner.selectedRFChannelFreq;
        if (CommonIntegration.isCNRegion()) {
            frequency = DVBTCNScanner.selectedRFChannelFreq;
        }
        MtkTvBroadcast.getInstance().changeFreq(this.mCommonInter.getCurrentFocus(), new MtkTvFreqChgParamBase(conType, freqType, frequency, 0, 0));
        MtkLog.d("MenuMain", "tuneSignal leave");
    }

    public void tuneDVBCRFSignal(int freq) {
        MtkLog.d(TAG, "tuneDVBCRFSignal enter>>" + freq);
        int turn = this.mTVContent.getCurrentTunerMode();
        int conType = 2;
        int freqType = 1;
        if (turn == 0) {
            conType = 2;
            freqType = 0;
        } else if (turn == 1) {
            conType = 1;
            freqType = 1;
        }
        MtkTvBroadcast.getInstance().changeFreq(this.mCommonInter.getCurrentFocus(), new MtkTvFreqChgParamBase(conType, freqType, freq, 0, 0));
        MtkLog.d(TAG, "tuneSignal leave");
    }

    public void tuneUSSAFacRFSignalLevel(int frqchannel) {
        int turn = this.mTVContent.getCurrentTunerMode();
        int modLation = 0;
        int freqType = 0;
        int frequency = frqchannel;
        if (CommonIntegration.isUSRegion()) {
            if (turn == 0) {
                frequency = this.mTVContent.calcATSCFreq(frqchannel);
            } else {
                frequency = this.mTVContent.calcCQAMFreq(frqchannel);
            }
        } else if (CommonIntegration.isSARegion()) {
            frequency = this.mTVContent.calcSAFreq(frqchannel);
        }
        int conType = 2;
        int freqType2 = 1;
        if (turn == 0) {
            conType = 2;
            freqType2 = 0;
            modLation = 2;
        } else if (turn == 1) {
            conType = 1;
            freqType2 = 1;
            if (CommonIntegration.isUSRegion()) {
                if (this.mTVContent.getModulation() == 0) {
                    modLation = 6;
                    freqType = 5056941;
                } else {
                    modLation = 14;
                    freqType = 5360537;
                }
            }
        }
        int modLation2 = modLation;
        int symRate = freqType;
        int conType2 = conType;
        int freqType3 = freqType2;
        MtkLog.d("MenuMain", "tuneSignal leave conType:" + conType2 + "   freqType:" + freqType3 + "  frequency:" + frequency + "  modLation:" + modLation2 + "  symRate:" + symRate);
        MtkTvBroadcast.getInstance().changeFreq(this.mCommonInter.getCurrentFocus(), new MtkTvFreqChgParamBase(conType2, freqType3, frequency, modLation2, symRate));
    }

    public void resetDefAfterClean() {
        SaveValue saveV = SaveValue.getInstance(this.mContext);
        this.mcf.setScanValue(MenuConfigManager.COLOR_SYSTEM, 0);
        this.mcf.setScanValue(MenuConfigManager.TV_SYSTEM, 0);
        InputSourceManager.getInstance().resetDefault();
        saveV.saveValue(MenuConfigManager.CAPTURE_LOGO_SELECT, 0);
        saveV.saveValue("g_misc__auto_sleep", 0);
        saveV.saveValue("SETUP_sleep_timer", 0);
        this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        saveV.saveBooleanValue("Zone_time", false);
        saveV.saveStrValue(MenuConfigManager.TIMER2, "00:00:00");
        saveV.saveStrValue(MenuConfigManager.TIMER1, "00:00:00");
    }
}
