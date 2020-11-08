package com.mediatek.twoworlds.tv;

import com.mediatek.twoworlds.tv.MtkTvCecBase;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvMultiViewBase;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyHeader;
import com.mediatek.twoworlds.tv.model.MtkTvBisskeyInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecActiveSourceBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecDevDiscoveryInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecRecordSouceInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvCecTimeInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvDvbScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcManualScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsSatelliteSettingBase;
import com.mediatek.twoworlds.tv.model.MtkTvEASParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvExternalUIStatusBase;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFreqChgParamBase;
import com.mediatek.twoworlds.tv.model.MtkTvGingaAppInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvParserIniInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvPipPopFucusInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.model.MtkTvRectangle;
import com.mediatek.twoworlds.tv.model.MtkTvRegionCapability;
import com.mediatek.twoworlds.tv.model.MtkTvScreenModeOverscan;
import com.mediatek.twoworlds.tv.model.MtkTvSrcVideoResolution;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopBlockBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvTeletextTopPageBase;
import com.mediatek.twoworlds.tv.model.MtkTvTimeRawDataBase;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeDeliveryTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvUpgradeFirmwareInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvVideoInfoBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import java.util.List;

public class TVNative {
    private static final String TAG = "TVNative";

    protected static native boolean AppTVAutoClockPhasePostionCondSuccess_native(String str);

    protected static native boolean AppTVAutoColorCondSuccess_native(String str);

    protected static native int AppTV_swapTmpUnlockInfo_native(int i);

    protected static native int AppTVchangeFreq_native(String str, MtkTvFreqChgParamBase mtkTvFreqChgParamBase);

    protected static native int AppTVgetMatchedChannel_native(int i, boolean z, int i2, int i3);

    protected static native boolean AppTVgetVideoMute_native(String str);

    protected static native int AppTVsaveTimestamp_native(String str);

    protected static native int AppTVsetAutoClockPhasePostion_native(String str);

    protected static native int AppTVsetAutoColor_native(String str);

    protected static native int AppTVsetFinetuneFreq_native(String str, int i, boolean z);

    protected static native int AppTVsetVideoMute_native(String str, boolean z);

    protected static native int AppTVunblockSvc_native(String str, boolean z);

    protected static native int AppTVunlockService_native(String str);

    protected static native int AppTVupdatedSysStatus_native(int i);

    protected static native int AppTVupdatedSysStatus_native(String str, int i);

    protected static native long GetConnAtrrAGC_native(String str);

    protected static native int GetConnAttrBER_native(String str);

    protected static native long GetConnAttrDBMSNR_native(String str);

    protected static native long GetConnAttrUEC_native(String str);

    protected static native String GetCurrentChannelServiceText_native();

    protected static native boolean GetDivXHDSupport_native();

    protected static native boolean GetDivXPlusSupport_native();

    protected static native String GetDrmRegistrationCode_native();

    protected static native long GetDrmUiHelpInfo_native();

    protected static native int GetModulation_native(String str);

    protected static native String GetRecordDisk_native();

    protected static native int GetSignalBER_native(String str);

    protected static native long GetSymRate_native(String str);

    protected static native int GetVideoSrcTag3DType_native(String str);

    protected static native int HighLevel_native(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    protected static native int ISDBCCEnable_native(boolean z);

    protected static native int ISDBCCGetCCString_native();

    protected static native int ISDBCCNextStream_native();

    protected static native int PWDShow_native();

    protected static native int ScanATSCExchangeData_native(int[] iArr);

    protected static native int ScanCeExchangeData_native(int[] iArr);

    protected static native int ScanDtmbExchangeData_native(int[] iArr);

    protected static native int ScanDvbcExchangeData_native(int[] iArr);

    protected static native String ScanDvbcGetStrData_native(int i);

    protected static native int ScanDvbsExchangeData_native(int[] iArr);

    protected static native String ScanDvbsgetUserMessage_native(String str);

    protected static native int ScanDvbtExchangeData_native(int[] iArr);

    protected static native int ScanISDBExchangeData_native(int[] iArr);

    protected static native int ScanPalSecamExchangeData_native(int[] iArr);

    protected static native void SendKeyClick_native(int i);

    protected static native void SendKey_native(int i, int i2);

    protected static native void SendMouseButton_native(int i, int i2);

    protected static native void SendMouseMove_native(int i, int i2, int i3, int i4);

    protected static native String SetDrmDeactivation_native();

    protected static native boolean SetRecordDiskByHandle_native(int i, String str);

    protected static native boolean SetRecordDisk_native(String str);

    protected static native int SubtitleGetTracks_native(int[] iArr);

    protected static native boolean VideoSrcIsProgressive_native(String str);

    protected static native int acceptRestart_native();

    protected static native int acceptSchedule_native();

    protected static native int activateCIKey_navtive();

    protected static native boolean addBooking_native(int i, int i2);

    protected static native boolean addBooking_native(int i, long j, int i2, String str);

    protected static native int addConfigListener_native(String str);

    public static native int addFavoritelistChannelByIndex_native(int i);

    public static native int addFavoritelistChannel_native();

    protected static native int analogCCEnable_native(boolean z);

    protected static native int analogCCGetCcIndex_native();

    protected static native int analogCCNextStream_native();

    protected static native int analogCCSetCcVisible_native(boolean z);

    protected static native int applyGpioStatus_native(int i, int i2);

    protected static native int atscCCDemoSet_native(int i, int i2);

    protected static native int atscCCEnable_native(boolean z);

    protected static native int atscCCGetCCIndex_native();

    protected static native int atscCCNextStream_native();

    protected static native int atscCCSetCcVisible_native(boolean z);

    public static native int bisskeyClean_native(int i);

    public static native MtkTvBisskeyInfoBase bisskeyGetDefaultRecord_native();

    public static native int bisskeyIsSameRecord_native(MtkTvBisskeyInfoBase mtkTvBisskeyInfoBase, MtkTvBisskeyInfoBase mtkTvBisskeyInfoBase2);

    public static native int bisskeySetKeyForCurrentChannel_native();

    protected static native int block_native(int i, boolean z);

    protected static native int cancelCamScan_navtive(int i);

    protected static native int cancelDownloadFirmware_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase);

    protected static native int cancelScan_native();

    public static native int changeFreq_native(String str, MtkTvFreqChgParamBase mtkTvFreqChgParamBase);

    protected static native int changeInputSourcebySourceid_native(int i);

    protected static native int changeInputSourcebySourceid_native(int i, String str);

    protected static native boolean changeRecordPBAudioByIndex_native(int i);

    protected static native boolean changeRecordPBCaptionByIndex_native(int i);

    public static native int channelSelectByChannelId_native(int i, boolean z);

    public static native int channelSelectByChannelNumber_native(int i, int i2, boolean z);

    public static native int channelSelectByKeys_native(int i);

    public static native int channelSelectSilently_native(MtkTvChannelInfoBase mtkTvChannelInfoBase, int i);

    public static native int channelSelect_native(MtkTvChannelInfoBase mtkTvChannelInfoBase, boolean z);

    public static native int channelSelect_native(MtkTvChannelInfoBase mtkTvChannelInfoBase, boolean z, int i);

    protected static native boolean checkAtscEventBlock_native(int i, int i2);

    protected static native int[] checkEITStauts_native(int i, int i2);

    protected static native boolean checkEventBlock_native(int i, int i2);

    protected static native boolean checkPCLWakeupReasonToBGM_native();

    protected static native boolean checkPWD_native(String str);

    protected static native boolean checkRecordRegisterFile_native(String str);

    public static native int cleanChannelList_native(int i, boolean z);

    protected static native int clearActiveWindow_native();

    protected static native int clearOadVersion_native();

    protected static native int closeUARTSerial_native(int i);

    protected static native long convertDTGToSeconds_native(MtkTvTimeFormatBase mtkTvTimeFormatBase);

    protected static native long convertLocalTimeToMillis_native(MtkTvTimeFormatBase mtkTvTimeFormatBase);

    protected static native void convertMillisToLocalTime_native(long j, MtkTvTimeFormatBase mtkTvTimeFormatBase);

    protected static native int convertTime_native(int i, MtkTvTimeFormatBase mtkTvTimeFormatBase, MtkTvTimeFormatBase mtkTvTimeFormatBase2);

    protected static native int convertUTCSecToDTG_native(long j, MtkTvTimeFormatBase mtkTvTimeFormatBase);

    protected static native void createMonitorInst_native(byte b);

    public static native int createSatlSnapshot_native(int i);

    public static native int createSnapshot_native(int i);

    protected static native int decrVolume_native();

    protected static native int decrVolume_native(int i);

    protected static native int delBooking_native(int i);

    public static native int deleteChannelByBrdcstType_native(int i, int i2, boolean z);

    protected static native void deleteMonitorInst_native(byte b);

    protected static native boolean deletePlaybackListItem_native(int i);

    protected static native int deletePvrBrowserFileByIndex_native(int i);

    protected static native int discoveryDevice_native(MtkTvCecDevDiscoveryInfoBase mtkTvCecDevDiscoveryInfoBase);

    protected static native void enterMMI_navtive(int i);

    protected static native int enter_native();

    protected static native int eraseCIKey_navtive();

    protected static native int factoryCheckKey_native(String str);

    protected static native int factoryWriteKeyFinish_native();

    protected static native int factoryWriteKey_native(String str, String str2);

    protected static native int freeEvent_native(int i);

    public static native int freeSatlSnapshot_native(int i);

    public static native int freeSnapshot_native(int i);

    protected static native int getActiveSourceInfo_native(MtkTvCecActiveSourceBase mtkTvCecActiveSourceBase);

    protected static native boolean getAdbStatus_native();

    protected static native int[] getAllPictureMode_native();

    protected static native int[] getAllScreenMode_native();

    protected static native int[] getAllSoundEffect_native();

    public static native int getAllTvprovider_native(List<TvProviderChannelInfoBase> list);

    protected static native int getApplicationInfoList_native(List<MtkTvGingaAppInfoBase> list);

    protected static native int getApplicationInfo_native();

    protected static native void getAudioAvailableRecord_native(List<TvProviderAudioTrackBase> list);

    protected static native int getAudioDecType_native();

    protected static native int getAudioFocus_native();

    protected static native String getAudioInfo_native(int i, int i2);

    protected static native void getAudioLang_native(MtkTvAVModeBase mtkTvAVModeBase);

    protected static native boolean getBlockUnrated_native();

    protected static native int getBookingChannelId_native(int i);

    protected static native int getBookingCount_native();

    protected static native int getBookingDeviceIndex_native(int i);

    protected static native String getBookingEventTitle_native(int i);

    protected static native int getBookingGenre_native(int i);

    protected static native int getBookingID_native(int i);

    protected static native int getBookingInfoData_native(int i);

    protected static native int getBookingRecordDelay_native(int i);

    protected static native long getBookingRecordDuration_native(int i);

    protected static native int getBookingRecordMode_native(int i);

    protected static native int getBookingRepeatMode_native(int i);

    protected static native int getBookingSourceType_native(int i);

    protected static native long getBookingStartTime_native(int i);

    protected static native int getBookingTunerType_native(int i);

    protected static native MtkTvTimeRawDataBase getBrdcstRawData_native();

    protected static native long getBroadcastTime_native(MtkTvTimeFormatBase mtkTvTimeFormatBase);

    protected static native int getCANEngRatingSettingInfo_native();

    protected static native int getCANFreRatingSettingInfo_native();

    protected static native int getCIHostID_navtive(int[] iArr);

    protected static native boolean getCIKeyStatusEx_navtive(int i);

    protected static native boolean getCIKeyStatus_navtive();

    protected static native String getCIKeyinfo_navtive();

    protected static native int getCaSystemIDMatch_navtive(int i);

    protected static native String getCamID_navtive(int i);

    protected static native String getCamName_navtive(int i);

    protected static native int getCamPinCaps_navtive();

    protected static native String getCamPinCode_navtive(int i);

    protected static native int getCamRatingValue_navtive(int i);

    protected static native String getCaption_native(int i, int i2);

    protected static native int getCecDevInfo_native(int i, MtkTvCecBase.CecDevInfo cecDevInfo);

    protected static native int getCecDevListInfo_native(List<MtkTvCecBase.CecDevInfo> list);

    public static native int getChannelCountByFilter_native(int i, int i2);

    public static native int getChannelCountByMask_native(int i, int i2, int i3, int i4);

    public static native int getChannelCountByQueryInfo_native(int i, MtkTvChannelQuery mtkTvChannelQuery);

    public static native int getChannelInfoByChannelId_native(int i, MtkTvChannelInfoBase mtkTvChannelInfoBase);

    public static native MtkTvChannelInfoBase getChannelInfoBySvlRecId_native(int i, int i2);

    public static native int getChannelListByFilter_native(int i, int i2, int i3, int i4, int i5, List<MtkTvChannelInfoBase> list);

    public static native int getChannelListByMask_native(int i, int i2, int i3, int i4, int i5, int i6, int i7, List<MtkTvChannelInfoBase> list);

    public static native int getChannelListByQueryInfo_native(int i, MtkTvChannelQuery mtkTvChannelQuery, List<MtkTvChannelInfoBase> list, int i2);

    public static native int getChannelListMode_native();

    public static native int getChannelList_native(int i, List<MtkTvChannelInfoBase> list);

    protected static native String getChannelName_native(int i, int i2);

    protected static native String getChannelNumber_native(int i, int i2);

    public static native int getChannelPumpVer_native(int i);

    public static native int getChannelRFInfo_native(int i, int i2, int i3);

    public static native int getChannelType_native(int i);

    protected static native String getConfigString_native(String str);

    protected static native int getConfigValue_native(int i, String str);

    public static native int getConnectAttr_native(String str, int i);

    protected static native int getCrntOverscan_native(MtkTvScreenModeOverscan mtkTvScreenModeOverscan);

    protected static native int getCrntRatingInfo_native(MtkTvRatingConvert2Goo mtkTvRatingConvert2Goo);

    protected static native int[] getCurrentActiveWinChannelList_native();

    protected static native long getCurrentActiveWinEndTime_native();

    protected static native long getCurrentActiveWinStartTime_native();

    protected static native int getCurrentAudio_native(TvProviderAudioTrackBase tvProviderAudioTrackBase);

    public static native MtkTvChannelInfoBase getCurrentChannelFromNav_native(int i);

    public static native int getCurrentChannelId_native();

    public static native int getCurrentChannel_native(MtkTvChannelInfoBase mtkTvChannelInfoBase);

    protected static native String getCurrentInputSourceName_native();

    protected static native String getCurrentInputSourceName_native(String str);

    protected static native long getCurrentPosition_native();

    protected static native int getCurrentTeletextPage_native(MtkTvTeletextPageBase mtkTvTeletextPageBase);

    protected static native int getDVBAgeRatingSetting_native();

    protected static native int getDataInfo_native(MtkTvOADBase mtkTvOADBase, int i);

    public static native int getDigitalFavoritesList_native(int i, int i2, int i3, List<MtkTvChannelInfoBase> list);

    protected static native int getDispRegionCapability_native(String str, MtkTvRegionCapability mtkTvRegionCapability);

    protected static native boolean getEASAndroidLaunchStatus_native();

    protected static native int getEASCurrentStatus_native(MtkTvEASParaBase mtkTvEASParaBase);

    protected static native int getEnqID_navtive();

    protected static native boolean getEthernetWolCtl_native();

    protected static native void getEventByIndex_native(int i, int i2, MtkTvEventInfoBase mtkTvEventInfoBase);

    protected static native String getEventDetailByIndex_native(int i, int i2);

    protected static native void getEventInfoByEventId_native(int i, int i2, MtkTvEventInfoBase mtkTvEventInfoBase);

    public static native int getEventListByChannelId_native(int i, long j, long j2, int i2, List<MtkTvEventInfoBase> list);

    protected static native int getEventNumberInEIT_native(int i);

    protected static native int getEventRatingMapById_native(int i, int i2, MtkTvRatingConvert2Goo mtkTvRatingConvert2Goo);

    protected static native int getEventRatingMapByIndex_native(int i, int i2, MtkTvRatingConvert2Goo mtkTvRatingConvert2Goo);

    protected static native void getEvent_native(int i, MtkTvEventInfoBase mtkTvEventInfoBase);

    protected static native boolean getExternalDeviceHasSignal_native(MtkTvInputSourceBase.InputDeviceType inputDeviceType, int i);

    public static native int getFavoritelistByFilter_native(List<MtkTvFavoritelistInfoBase> list);

    protected static native String getFileBasePath_native();

    protected static native int getFirmwareInfo_native(MtkTvUpgradeFirmwareInfoBase mtkTvUpgradeFirmwareInfoBase);

    protected static native boolean getGingaScreenModeisEnable_native();

    protected static native int getHostQuietTuneStatus_navtive(int i);

    protected static native int getHostTuneBrdcstStatus_navtive(int i);

    protected static native int getHostTuneStatus_navtive(int i);

    protected static native int getISDBAgeRatingSetting_native();

    protected static native int getISDBContentRatingSetting_native();

    protected static native void getInfo_native(byte b, int i, int[] iArr);

    protected static native int getInputLabelIdx_native(int i);

    protected static native String getInputLabelUserDefName_native(int i);

    protected static native void getInputSourceAudioAvailableRecord_native(List<TvProviderAudioTrackBase> list);

    protected static native int getInputSourceCurrentAudio_native(TvProviderAudioTrackBase tvProviderAudioTrackBase);

    protected static native String getInputSourceNamebySourceid_native(int i);

    protected static native int getInputSourceRecbyidx_native(int i, MtkTvInputSourceBase.InputSourceRecord inputSourceRecord);

    protected static native int getInputSourceTotalNumber_native();

    protected static native MtkTvParserIniInfoBase getIntConfigData_native(String str, String str2);

    protected static native void getInternalScrnMode_native(MtkTvMHEG5Base mtkTvMHEG5Base);

    protected static native String getIptChannelNumber_native(int i, int i2);

    protected static native String getIptsCC_native(int i, int i2);

    protected static native String getIptsName_native(int i, int i2);

    protected static native String getIptsRating_native(int i, int i2);

    protected static native String getIptsRslt_native(int i, int i2);

    protected static native String getMacAddress_native();

    protected static native int getMarketRegion();

    protected static native int getMenuListID_navtive();

    protected static native int getMhlPortNum_native();

    protected static native int getMinMaxConfigValue_native(int i, String str);

    protected static native String getMsg_native(int i, int i2);

    protected static native boolean getMute_native();

    protected static native boolean getMute_native(int i);

    protected static native String getNextProgCategoryIdx_native(int i, int i2);

    protected static native String getNextProgDetail_native(int i, int i2);

    protected static native String getNextProgTime_native(int i, int i2);

    protected static native String getNextProgTitle_native(int i, int i2);

    protected static native String getNextRating_native(int i, int i2);

    protected static native int getOpenVCHIPInfo_native(MtkTvOpenVCHIPParaBase mtkTvOpenVCHIPParaBase, MtkTvOpenVCHIPInfoBase mtkTvOpenVCHIPInfoBase);

    protected static native int getOpenVCHIPSettingInfo_native(MtkTvOpenVCHIPSettingInfoBase mtkTvOpenVCHIPSettingInfoBase);

    protected static native int getPFEventInfoByChannel_native(int i, boolean z, MtkTvEventInfoBase mtkTvEventInfoBase);

    protected static native int getPOPTunerFocus_native();

    protected static native int getPclWakeupSetup_native();

    protected static native void getPfgInfo_native(MtkTvMHEG5Base mtkTvMHEG5Base);

    protected static native int getPictureMode_native();

    protected static native int getPlaybackItemCount_native();

    protected static native int getPowerOnStatus_native();

    protected static native String getProfileISO639LangCode_navtive(int i);

    protected static native String getProfileName_navtive(int i);

    protected static native int getProfileResourceIsOpen_navtive(int i);

    protected static native int getProfileSupport_navtive(int i, int i2);

    protected static native int getProfileValid_navtive(int i);

    protected static native String getProgCategoryIdx_native(int i, int i2);

    protected static native String getProgCategory_native(int i, int i2);

    protected static native String getProgDetailPageIdx_native(int i, int i2);

    protected static native String getProgDetail_native(int i, int i2);

    protected static native String getProgTime_native(int i, int i2);

    protected static native String getProgTitle_native(int i, int i2);

    protected static native long getPvrBrowserItemChannelId_native(int i);

    protected static native long getPvrBrowserItemChannelId_native(String str);

    protected static native String getPvrBrowserItemChannelName_native(int i);

    protected static native String getPvrBrowserItemChannelName_native(String str);

    protected static native int getPvrBrowserItemCount_native();

    protected static native String getPvrBrowserItemDate_native(int i);

    protected static native String getPvrBrowserItemDate_native(String str);

    protected static native long getPvrBrowserItemDuration_native(int i);

    protected static native long getPvrBrowserItemDuration_native(String str);

    protected static native long getPvrBrowserItemFirstRatingRange_native(int i);

    protected static native long getPvrBrowserItemFirstRatingRange_native(String str);

    protected static native int getPvrBrowserItemMajorChannelNum_native(int i);

    protected static native int getPvrBrowserItemMajorChannelNum_native(String str);

    protected static native int getPvrBrowserItemMinorChannelNum_native(int i);

    protected static native int getPvrBrowserItemMinorChannelNum_native(String str);

    protected static native String getPvrBrowserItemPath_native(int i);

    protected static native String getPvrBrowserItemProgramName_native(int i);

    protected static native String getPvrBrowserItemProgramName_native(String str);

    protected static native long getPvrBrowserItemRentention_native(int i);

    protected static native long getPvrBrowserItemRentention_native(String str);

    protected static native long getPvrBrowserItemStartTime_native(int i);

    protected static native long getPvrBrowserItemStartTime_native(String str);

    protected static native String getPvrBrowserItemTime_native(int i);

    protected static native String getPvrBrowserItemTime_native(String str);

    protected static native String getPvrBrowserItemWeek_native(int i);

    protected static native String getPvrBrowserItemWeek_native(String str);

    protected static native int getPvrBrowserRecordingFileCount_native();

    protected static native String getPvrBrowserRecordingFileName_native(int i);

    protected static native boolean getRatingEnable_native();

    protected static native String getRating_native(int i, int i2);

    public static native MtkTvBisskeyInfoBase getRecordByBslRecId_native(int i, int i2);

    public static native MtkTvBisskeyInfoBase getRecordByHeader_native(int i, MtkTvBisskeyHeader mtkTvBisskeyHeader, int i2);

    public static native MtkTvBisskeyInfoBase getRecordByIndex_native(int i, int i2);

    public static native int getRecordNumByHeader_native(int i, MtkTvBisskeyHeader mtkTvBisskeyHeader);

    protected static native int getRecordPBAudioNum_native();

    protected static native int getRecordPBCaptionNum_native();

    protected static native String getRecordPBDetailsInformation_native(String str);

    protected static native String getRecordPBItemChannelName_native(int i);

    protected static native long getRecordPBItemDuration_native(int i);

    protected static native int getRecordPBItemMajorChannelNum_native(int i);

    protected static native int getRecordPBItemMinorChannelNum_native(int i);

    protected static native String getRecordPBItemPath_native(int i);

    protected static native String getRecordPBItemProgramName_native(int i);

    protected static native int getRecordPBSpeed_native();

    protected static native MtkTvTimeshiftBase.TimeshiftRecordStatus getRecordStatus_native();

    protected static native String getRecordingFileByHandle_native(int i);

    protected static native String getRecordingFile_native();

    protected static native int getRecordingPosition();

    protected static native int getRecordingPositionByHandle(int i);

    public static native int getRecordsNumber_native(int i);

    protected static native int getSasCbctState_navtive(int i);

    protected static native int getSasItvState_navtive(int i);

    public static native int getSatlNumRecs_native(int i);

    public static native int getSatlRecordByRecIdx_native(int i, int i2, List<MtkTvDvbsConfigInfoBase> list);

    public static native int getSatlRecord_native(int i, int i2, List<MtkTvDvbsConfigInfoBase> list);

    protected static native int getScheduleInfo_native(MtkTvOADBase mtkTvOADBase);

    protected static native int getScreenMode_native();

    protected static native int getScreenOutputDispRect_native(String str, MtkTvRectangle mtkTvRectangle);

    protected static native int getScreenSourceRect_native(String str, MtkTvRectangle mtkTvRectangle);

    protected static native int getScrnSvrMsgID_native();

    protected static native String getSerialNumber_native();

    public static native int getSignalLevel_native();

    public static native int getSignalQuality_native();

    protected static native int getSleepTimerRemainingTime_native();

    protected static native int getSleepTimer_native(boolean z);

    protected static native boolean getSlotActive_navtive(int i);

    protected static native int getSlotNum_navtive();

    protected static native int getSoundEffect_native();

    protected static native int getSrcVideoResolution_native(String str, MtkTvSrcVideoResolution mtkTvSrcVideoResolution);

    protected static native long getStartPosition_native();

    protected static native long getStorageFreeSize_native();

    protected static native long getStorageSize_native();

    protected static native MtkTvParserIniInfoBase getStringConfigData_native(String str, String str2);

    protected static native String getSysVersion_native(int i, String str);

    protected static native String getTVSrc_native(int i, int i2);

    protected static native int getTeletextTopBlockList_native(List<MtkTvTeletextTopBlockBase> list);

    protected static native int getTeletextTopGroupList_native(MtkTvTeletextTopBlockBase mtkTvTeletextTopBlockBase, List<MtkTvTeletextTopGroupBase> list);

    protected static native int getTeletextTopPageList_native(MtkTvTeletextTopGroupBase mtkTvTeletextTopGroupBase, List<MtkTvTeletextTopPageBase> list);

    protected static native long getTimeOffset_native();

    protected static native int getTimeSyncSource_native();

    protected static native long getTimeZone_native();

    protected static native String getTimer_native(int i, int i2);

    protected static native int getTimeshiftDeviceStatus_native();

    protected static native long getTimeshiftDuration_native();

    protected static native int getTimeshiftErrorID_native();

    protected static native long getTimeshiftPosition_native();

    protected static native int getTimeshiftRegisterDeviceSchedule_native();

    public static native int getTvproviderBySvlId_native(int i, List<TvProviderChannelInfoBase> list);

    public static native int getTvproviderBySvlRecId_native(int i, int i2, TvProviderChannelInfoBase tvProviderChannelInfoBase);

    public static native int getTvproviderOcl_native(List<TvProviderChannelInfoBase> list);

    protected static native int getUARTSerialOperationMode_native(int i, int[] iArr);

    protected static native int getUARTSerialSetting_native(int i, int[] iArr);

    protected static native int getUIMode();

    protected static native int getUSMovieRatingSettingInfo_native();

    protected static native int getUSTvRatingSettingInfo_native(MtkTvUSTvRatingSettingInfoBase mtkTvUSTvRatingSettingInfoBase);

    protected static native long getUtcTime_native();

    protected static native int getVideoInfoData_native(int i, MtkTvVideoInfoBase mtkTvVideoInfoBase);

    protected static native int getVideoInfoValue_native(int i);

    protected static native String getVideoInfo_native(int i, int i2);

    protected static native int getVideoSrcRegionCapability_native(String str, MtkTvRegionCapability mtkTvRegionCapability);

    protected static native String getVirtualChannelInfo_navtive(int i);

    protected static native int getVolume_native();

    protected static native int getVolume_native(int i);

    protected static native int getWakeUpIrKey_native();

    protected static native int getWakeUpReason_native();

    protected static native boolean getWifiWolCtl_native();

    protected static native int hbbtvExchangeData_native(int[] iArr);

    protected static native int hbbtvSetAudioDescription_native(int i);

    protected static native int hbbtvSetDefaultAudioLang_native(String str);

    protected static native int hbbtvSetDefaultSubtitleLang_native(String str, int i);

    protected static native int hbbtvStmGetAudioCount_native();

    protected static native String hbbtvStmGetAudioLang_native(int i);

    protected static native int hbbtvStmGetAudio_native();

    protected static native int hbbtvStmSetAudioIndex_native(int i);

    protected static native int htmlAgentExchangeData_native(int[] iArr);

    protected static native int incrVolume_native();

    protected static native int incrVolume_native(int i);

    public static native int inputSyncStop_native(String str, boolean z);

    protected static native boolean isAutoDetectPlugStatus_native(int i);

    protected static native boolean isBGMapplicable_native(boolean z);

    protected static native boolean isBlockEx_native(int i);

    protected static native boolean isBlock_native(int i);

    public static native boolean isCamInstalled_native();

    protected static native boolean isCaptureLogo_native();

    public static native boolean isChannelNumberExsit_native(int i, int i2);

    protected static native int isConfigEnabled_native(int i, String str);

    protected static native int isConfigItemsEnabled_native(int i, String str, MtkTvConfigBase mtkTvConfigBase);

    protected static native int isConfigVisible_native(int i, String str);

    protected static native int isDeviceExist_native(int i);

    public static native boolean isDigitKeysHandled_native();

    protected static native boolean isDisplayADAtmos_native(int i, int i2);

    protected static native boolean isDisplayADEarIcon_native(int i, int i2);

    protected static native boolean isDisplayADEyeIcon_native(int i, int i2);

    protected static native boolean isDisplayBanner_native(int i, int i2);

    protected static native boolean isDisplayCaptionIcon_native(int i, int i2);

    protected static native boolean isDisplayFavIcon_native(int i, int i2);

    protected static native boolean isDisplayFrmCH_native(int i, int i2);

    protected static native boolean isDisplayFrmDetail_native(int i, int i2);

    protected static native boolean isDisplayFrmInfo_native(int i, int i2);

    protected static native boolean isDisplayGingaIcon_native(int i, int i2);

    protected static native boolean isDisplayIptsLockIcon_native(int i, int i2);

    protected static native boolean isDisplayLogoIcon_native(int i, int i2);

    protected static native boolean isDisplayProgDetailDownIcon_native(int i, int i2);

    protected static native boolean isDisplayProgDetailUpIcon_native(int i, int i2);

    protected static native boolean isDisplayTVLockIcon_native(int i, int i2);

    protected static native boolean isDisplayTtxIcon_native(int i, int i2);

    protected static native boolean isFreeze_native(String str);

    protected static native boolean isMultiviewInputsourceAvailable_native(int i, String str);

    protected static native boolean isOpenVCHIPInfoAvailable_native();

    public static native boolean isOrigScrambleStrm_native(String str, int i);

    protected static native boolean isReceivedTOT_native();

    protected static native boolean isScaning_native();

    public static native boolean isSignalLoss_native();

    public static native boolean isSupport4DigitsChannelNo_native();

    protected static native boolean isTimeEditable_native();

    protected static native boolean isTimeOffsetEditable_native();

    protected static native boolean isTimeZoneEditable_native();

    protected static native boolean isTvRunning_native(String str);

    protected static native boolean isZoomEnable_native();

    protected static native String isdbCDTGetChannelLogo_native(int i, int i2, int i3);

    protected static native int launchHbbtv_native(String str, String str2, String str3);

    protected static native int loadEventByEIT_native(int i, int i2);

    protected static native int[] loadEvents_native(int i, long j, int i2);

    protected static native boolean menuIsTvBlock_native();

    protected static native int nextStream_native();

    protected static native int notifyCecCompInfo_native(String str, int i, String str2);

    protected static native void notifySoftKeyboardStatusChange_native(int i);

    protected static native void notifyTextChange_native(String str);

    public static native int oneChannelListAddToMax_native(int i, int i2);

    public static native int oneChannelListArrange_native(int i, int i2);

    protected static native int onlyChgFocus_native(String str);

    protected static native int openUARTSerial_native(int i, int[] iArr, int[] iArr2);

    protected static native int outputUARTSerial_native(int i, byte[] bArr);

    protected static native boolean pauseTimeshift_native();

    protected static native int playStream_native(int i);

    protected static native boolean playTimeshift_native();

    protected static native int popEnterNextTvMode_native();

    protected static native int popGetFocusInfo_native(MtkTvPipPopFucusInfoBase mtkTvPipPopFucusInfoBase);

    protected static native boolean popGetPipFocusId_native();

    protected static native int popNextPipWindowPosition_native();

    protected static native int popNextPipWindowSize_native();

    protected static native int popSwapWindow_native();

    protected static native int popSwitchAudioFocus_native();

    protected static native int powerOnDeviceByLogicAddr_native(int i);

    protected static native boolean queryConflict_native(int i, int i2);

    protected static native int queryGpioStatus_native(int i, int i2);

    protected static native int queryUpgradeResult_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase);

    protected static native int recordClosePVR(int i);

    protected static native int recordGetPVRSrcType();

    protected static native int recordGetPVRSrcTypeByHandle(int i);

    protected static native int recordGetPVRStatus();

    protected static native int recordGetPVRStatusByHandle(int i);

    protected static native long recordGetRecordingFilesize();

    protected static native int recordOpenPVR(int i);

    protected static native int recordOpenPVREx(int i, boolean z);

    protected static native boolean recordPBPause_native();

    protected static native int recordPBPlay_native(String str, boolean z);

    protected static native boolean recordPBResume_native();

    protected static native int recordPBSeek_native(long j);

    protected static native boolean recordPBStop_native();

    protected static native int recordPB_TrickPlay_native(double d);

    protected static native long recordPB_getDuration_native();

    protected static native long recordPB_getPosition_native();

    protected static native int recordPVRSelectSvc(int i, int i2, int i3);

    protected static native int recordStartPVR(int i);

    protected static native int recordStartPVRByHandle(int i);

    protected static native boolean recordStartSpeedTest_native(String str, int i);

    protected static native int recordStopPVR();

    protected static native int recordStopPVRByHandle(int i);

    protected static native int recordgetErrorID();

    protected static native int recordgetErrorIDByhandle(int i);

    protected static native int registerDefaultCallback_native();

    protected static native int registerTimeshiftDevice_native(String str, long j);

    protected static native int remindMeLater_native();

    protected static native int removeConfigListener_native(String str);

    public static native int removeFavoritelistChannelByIndexWithoutShowFavIcon_native(int i);

    public static native int removeFavoritelistChannel_native(int i);

    protected static native int resetConfigValues_native(int i);

    public static native void resetDigitKeysHandledFlag_native();

    protected static native void resetFac_native();

    protected static native void resetPri_native();

    protected static native void resetPub_native();

    public static native int restoreSatlSnapshot_native(int i);

    public static native int restoreSnapshot_native(int i);

    protected static native void rpcCloseClient_native();

    public static native int satllistCleanDatabase_native(int i);

    public static native int satllistLockDatabase_native(int i);

    public static native int satllistReadLockDatabase_native(int i);

    public static native int satllistReadUnLockDatabase_native(int i);

    public static native int satllistUnLockDatabase_native(int i);

    protected static native void saveTimeStamp_native(String str);

    protected static native int sbAtscGetMajorChannelNum_native(int i);

    protected static native int sbAtscGetMinorChannelNum_native(int i);

    protected static native int sbIsdbGetChannelIndex_native(int i);

    protected static native int sbIsdbGetMajorNumber_native(int i);

    protected static native int sbIsdbGetMinorNumber_native(int i);

    protected static native int seekTimeshift_native(long j);

    protected static native int seekTo_native(long j);

    protected static native int selectAudioById_native(int i);

    protected static native int selectMainSubAudioById_native(int i, int i2);

    public static native int set2ndCurrentChannelBySvlId_native(int i, int i2);

    protected static native int setATSCScanParas(MtkTvATSCScanParaBase mtkTvATSCScanParaBase);

    protected static native int setAdbStatus_native(boolean z);

    protected static native int setAtscStorage_native(boolean z);

    protected static native int setAudioFocus_native(String str);

    protected static native int setAudioFocusbySourceid_native(int i);

    protected static native int setAutoDetectPlugStatus_native(int i, boolean z);

    protected static native int setAutoDownload_native(boolean z);

    protected static native int setAutoRecord_native(boolean z);

    public static native int setBisskeyInfo_native(int i, MtkTvBisskeyInfoBase mtkTvBisskeyInfoBase, int i2);

    protected static native int setBlockUnrated_native(boolean z);

    protected static native int setBookingChannelId_native(int i, int i2);

    protected static native int setBookingDeviceIndex_native(int i, int i2);

    protected static native int setBookingEventTitle_native(int i, String str);

    protected static native int setBookingGenre_native(int i, int i2);

    protected static native int setBookingInfoData_native(int i, int i2);

    protected static native int setBookingRecordDelay_native(int i, int i2);

    protected static native int setBookingRecordDuration_native(int i, long j);

    protected static native int setBookingRecordMode_native(int i, int i2);

    protected static native int setBookingRepeatMode_native(int i, int i2);

    protected static native int setBookingSourceType_native(int i, int i2);

    protected static native int setBookingStartTime_native(int i, long j);

    protected static native int setBookingTunerType_native(int i, int i2);

    protected static native int setCANEngRatingSettingInfo_native(int i);

    protected static native int setCANFreRatingSettingInfo_native(int i);

    protected static native int setCamPinCode_navtive(int i, String str);

    public static native int setChannelList_native(int i, int i2, List<MtkTvChannelInfoBase> list);

    public static native int setChannelPumpVer_native(int i, int i2);

    protected static native int setChgSource_native(boolean z);

    protected static native int setConfigString_native(String str, String str2);

    protected static native int setConfigValue_native(int i, String str, int i2, int i3);

    protected static native int setCrntOverscan_native(int i, int i2, int i3, int i4);

    protected static native void setCurrentActiveWindowsInfoEx_native(List<MtkTvChannelInfoBase> list, long j, long j2);

    protected static native void setCurrentActiveWindowsInfo_native(List<MtkTvChannelInfoBase> list, long j);

    public static native int setCurrentChannelBySvlId_native(int i, int i2);

    public static native int setCurrentChannel_native(int i);

    protected static native int setDVBAgeRatingSetting_native(int i);

    public static native int setDigitalFavoritesList_native(int i, int i2, int i3, List<MtkTvChannelInfoBase> list, int i4);

    protected static native int setDvbScanParas(MtkTvDvbScanParaBase mtkTvDvbScanParaBase);

    protected static native int setDvbcManualScanParas(MtkTvDvbcManualScanParaBase mtkTvDvbcManualScanParaBase);

    protected static native int setDvbcScanParas(MtkTvDvbcScanParaBase mtkTvDvbcScanParaBase);

    protected static native int setEASAndroidLaunchStatus_native(boolean z);

    protected static native int setEnqAnswer_navtive(int i, int i2, int i3, String str);

    protected static native boolean setEthernetWolCtl_native(boolean z);

    protected static native void setEventCallback_native(boolean z);

    protected static native int setFreeze_native(String str, boolean z);

    protected static native int setHDSConfirm_navtive(int i, int i2);

    protected static native void setHbbtvStatus_native(boolean z);

    protected static native int setISDBAgeRatingSetting_native(int i);

    protected static native int setISDBContentRatingSetting_native(int i);

    protected static native int setISDBScanParas(MtkTvISDBScanParaBase mtkTvISDBScanParaBase);

    protected static native void setInfo_native(byte b, int i, int[] iArr);

    protected static native int setInputLabelIdx_native(int i, int i2);

    protected static native int setInputLabelUserDefName_native(int i, String str);

    protected static native int setLinuxBGMStart_native();

    protected static native void setLinuxKeyReceive_native(int i);

    protected static native void setLocalTimeZone_native(String str);

    protected static native void setMMICloseDone_navtive(int i);

    protected static native void setMMIClose_navtive(int i);

    protected static native int setMenuAnswer_navtive(int i, int i2, int i3);

    protected static native void setMheg5Disable_native();

    protected static native void setMheg5Enable_native();

    protected static native void setMheg5Status_native(int i, int i2, int i3, int i4);

    protected static native int setMute_native(int i, boolean z);

    protected static native int setMute_native(boolean z);

    protected static native int setNTSCScanParas(MtkTvNTSCScanParaBase mtkTvNTSCScanParaBase);

    protected static native int setNewTvMode_native(int i);

    protected static native int setNextAudioLang_native();

    protected static native boolean setOSDOpacity_native(int i);

    protected static native int setOpenVCHIPSettingInfo_native(MtkTvOpenVCHIPSettingInfoBase mtkTvOpenVCHIPSettingInfoBase);

    protected static native int setPclWakeupSetup_native(int i);

    protected static native void setPfgResult_native(boolean z);

    protected static native int setPictureMode_native(int i);

    protected static native int setPipConfig_native(int i);

    protected static native int setPkgPathname_native(String str);

    protected static native int setPlaybackPause_native();

    protected static native int setPlaybackResume_native();

    protected static native int setPlaybackSpeed_native(MtkTvTimeshiftBase.TimeshiftPlaybackSpeed timeshiftPlaybackSpeed);

    protected static native int setRatingEnable_native(boolean z);

    protected static native int setRecordOff_native(int i);

    protected static native int setRecordOn_native(int i, MtkTvCecRecordSouceInfoBase mtkTvCecRecordSouceInfoBase);

    protected static native boolean setRecordRegisterFile_native(String str);

    protected static native int setSasForceItvExit_navtive(int i);

    protected static native int setSatelliteSetting(MtkTvDvbsSatelliteSettingBase mtkTvDvbsSatelliteSettingBase);

    protected static native int setScanComplete_navtive(int i);

    protected static native int setScartAutoJump_native(boolean z);

    protected static native int setScreenMode_native(int i);

    protected static native int setScreenOutputDispRect_native(String str, MtkTvRectangle mtkTvRectangle);

    protected static native int setScreenSourceRect_native(String str, MtkTvRectangle mtkTvRectangle);

    protected static native void setSignalMonitor_native(boolean z);

    protected static native void setSleepTimer_native(int i);

    protected static native int setSoundEffect_native(int i);

    protected static native int setStandbyToAll_native(boolean z);

    protected static native int setStandby_native(int i);

    protected static native int setSystemAudioModeRequest_native(int i);

    protected static native int setSystemProperties_native(String str, String str2);

    protected static native int setTeletextPage_native(MtkTvTeletextPageBase mtkTvTeletextPageBase);

    protected static native void setTimeOffset_native(long j);

    protected static native void setTimeSyncSource_native(int i);

    protected static native void setTimeZone_native(long j);

    protected static native int setTimer_native(int i, MtkTvCecTimeInfoBase mtkTvCecTimeInfoBase);

    protected static native int setUARTSerialOperationMode_native(int i, int i2);

    protected static native int setUARTSerialSetting_native(int i, int[] iArr);

    protected static native int setUSMovieRatingSettingInfo_native(int i);

    protected static native int setUSTvRatingSettingInfo_native(MtkTvUSTvRatingSettingInfoBase mtkTvUSTvRatingSettingInfoBase);

    protected static native int setUserCtrlPressed_native(MtkTvCecBase.CecUserCtrlInfo cecUserCtrlInfo);

    protected static native int setUserCtrlReleased_native(MtkTvCecBase.CecUserCtrlInfo cecUserCtrlInfo);

    protected static native void setUtcTime_native(long j);

    protected static native int setVendorCmdWithId_native(MtkTvCecBase.CecVndrCmdWithIdInfo cecVndrCmdWithIdInfo);

    protected static native int setVideoInfoData_native(int i, MtkTvVideoInfoBase mtkTvVideoInfoBase);

    protected static native int setVideoInfoValue_native(int i, int i2);

    protected static native int setVolume_native(int i);

    protected static native int setVolume_native(int i, int i2);

    protected static native boolean setWifiWolCtl_native(boolean z);

    protected static native int sifReadMultipleSubAddr_native(int i, int i2, byte b, byte[] bArr, byte[] bArr2);

    protected static native int sifWriteMultipleSubAddr_native(int i, int i2, byte b, byte[] bArr, byte[] bArr2);

    public static native int slideForOnePartChannelId_native(int i, int i2, int i3);

    protected static native void softKeyboardInit_native();

    protected static native int startApplication_native(String str);

    protected static native int startCamScan_navtive(int i, boolean z);

    protected static native int startDownloadFirmware_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase, String str, String str2);

    protected static native int startDownload_native();

    protected static native int startFlash_native();

    protected static native int startGinga_native();

    protected static native int startJumpChannel_native();

    protected static native int startMainVideo_native(int i, MtkTvMultiViewBase.Region_Info_T region_Info_T);

    protected static native int startManualDetect_native();

    protected static native int startRebootUpgrade_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase);

    protected static native int startScan_native(int i, int i2, boolean z);

    protected static native int startSubVideo_native(int i, MtkTvMultiViewBase.Region_Info_T region_Info_T);

    protected static native int startTimeshift_native();

    protected static native int startUpgrade_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase, boolean z);

    protected static native int stopApplication_native(String str);

    protected static native int stopDownload_native();

    protected static native int stopGinga_native();

    protected static native int stopMainVideo_native();

    protected static native int stopManualDetect_native();

    protected static native int stopSubVideo_native();

    protected static native int stopTimeshift_native();

    protected static native int stopTimeshift_native(MtkTvTimeshiftBase.TimeshiftStopFlag timeshiftStopFlag);

    public static native int storeFavoritelistChannel_native();

    public static native int swapChannelInfo_native(int i, int i2, int i3);

    public static native int swapDigitalFavorites_native(int i, int i2, int i3, int i4);

    public static native int swapFavoritelistByIndex_native(int i, int i2);

    public static native int switchInputToTvSource_native();

    public static native int syncStop_native(String str, boolean z);

    protected static native boolean teletextHasTopInfo_native();

    public static native int transferExternalUIStatus_native(MtkTvExternalUIStatusBase mtkTvExternalUIStatusBase);

    protected static native int trickPlayTimeshift_native(double d);

    protected static native int triggerUpgrade_native(MtkTvUpgradeDeliveryTypeBase mtkTvUpgradeDeliveryTypeBase);

    protected static native int tunerFacQuery_navtive(boolean z, List<String> list, List<String> list2);

    protected static native int unregisterDefaultCallback_native();

    protected static native int unselectAudio_native();

    protected static native void updateBooking_native();

    protected static native int updateCIKeyEx_navtive(int i);

    protected static native int updateCIKeyWithPathEx_navtive(String str, int i);

    protected static native int updateCIKeyWithPath_navtive(String str);

    protected static native int updateCIKey_navtive();

    protected static native int updateOverscanIni_native();

    public static native int updateSatlRecord_native(int i, MtkTvDvbsConfigInfoBase mtkTvDvbsConfigInfoBase, boolean z);

    protected static native int warningStartCC_native(boolean z);

    protected static native int warningStartGingaApp_native(boolean z);

    protected static native int writeCIKey_navtive(byte[] bArr, int i);

    static {
        TvDebugLog.i(TAG, "Load libcom_mediatek_twoworlds_tv_jni.so start !");
        System.loadLibrary("com_mediatek_twoworlds_tv_jni");
        TvDebugLog.i(TAG, "Load libcom_mediatek_twoworlds_tv_jni.so OK !");
    }
}
