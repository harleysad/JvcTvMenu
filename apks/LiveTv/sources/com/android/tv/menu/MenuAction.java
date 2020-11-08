package com.android.tv.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.setting.widget.view.DiskSettingDialog;
import com.mediatek.wwtv.setting.widget.view.ScheduleListDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateBase;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.view.GingaTvDialog;
import com.mediatek.wwtv.tvcenter.nav.view.SourceListView;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIMainDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.oad.NavOADActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;

public class MenuAction {
    public static final MenuAction BROADCAST_TV_CI_ACTION = new MenuAction((int) R.string.menu_advanced_ci, 14, (int) R.drawable.ic_ci);
    public static final MenuAction BROADCAST_TV_OAD_ACTION = new MenuAction((int) R.string.menu_advanced_oad, 13, (int) R.drawable.ic_oad);
    public static final MenuAction BROADCAST_TV_SETTINGS_ACTION = new MenuAction((int) R.string.menu_advanced_options, 11, (int) R.drawable.ic_advanced);
    public static final MenuAction DEVICE_INFO_ACTION = new MenuAction((int) R.string.menu_setup_device_info, 19, (int) R.drawable.ic_device_info);
    public static final MenuAction DEV_ACTION = new MenuAction((int) R.string.options_item_developer, 6, (int) R.drawable.ic_developer_mode_tv_white_48dp);
    public static final MenuAction DVR_LIST_ACTION = new MenuAction((int) R.string.pvr_playlist_record_list, 16, (int) R.drawable.ic_recordlist);
    public static final MenuAction GINGA_SELECTION = new MenuAction((int) R.string.options_item_ginga, 22, (int) R.drawable.ic_menu_ginga);
    public static final MenuAction MORE_CHANNELS_ACTION = new MenuAction((int) R.string.options_item_more_channels, 5, (int) R.drawable.ic_store);
    public static final MenuAction MY_FAVORITE_ACTION = new MenuAction((int) R.string.options_item_my_favorite, 8, (int) R.drawable.ic_favorite);
    public static final MenuAction PIP_IN_APP_ACTION = new MenuAction((int) R.string.options_item_pip, 2, (int) R.drawable.ic_tvoption_pip);
    public static final MenuAction POWER_ACTION = new MenuAction((int) R.string.nav_upgrader_power, 12, (int) R.drawable.ic_power);
    public static final MenuAction PVR_START_ACTION = new MenuAction((int) R.string.title_pvr_start, 20, (int) R.drawable.ic_advanced);
    public static final MenuAction PVR_START_ICON = new MenuAction((int) R.string.title_pvr_start, 20, (int) R.drawable.menu_Start_Record);
    public static final MenuAction PVR_STOP_ACTION = new MenuAction((int) R.string.title_pvr_stop, 21, (int) R.drawable.ic_advanced);
    public static final MenuAction SCHEDULE_LIST_ACTION = new MenuAction((int) R.string.menu_setup_schedule_list, 18, (int) R.drawable.ic_schedule_list);
    public static final MenuAction SELECT_AUDIO_LANGUAGE_ACTION = new MenuAction((int) R.string.options_item_multi_audio, 4, (int) R.drawable.ic_music_note);
    public static final MenuAction SELECT_AUTO_PICTURE_ACTION = new MenuAction((int) R.string.options_item_picture_style, 9, (int) R.drawable.ic_tvoption_auto_picture);
    public static final MenuAction SELECT_CLOSED_CAPTION_ACTION = new MenuAction((int) R.string.options_item_closed_caption, 0, (int) R.drawable.ic_tvoption_cc);
    public static final MenuAction SELECT_DISPLAY_MODE_ACTION = new MenuAction((int) R.string.options_item_display_mode, 1, (int) R.drawable.ic_tvoption_aspect);
    public static final MenuAction SELECT_SOURCE_ACTION = new MenuAction((int) R.string.option_item_source, 15, (int) R.drawable.ic_tvoption_sourcelist);
    public static final MenuAction SELECT_SPEAKERS_ACTION = new MenuAction((int) R.string.options_item_speakers, 10, (int) R.drawable.ic_tvoption_multi_track);
    public static final MenuAction SETTINGS_ACTION = new MenuAction((int) R.string.options_item_settings, 7, (int) R.drawable.ic_settings);
    public static final MenuAction SYSTEMWIDE_PIP_ACTION = new MenuAction((int) R.string.options_item_pip, 3, (int) R.drawable.ic_tvoption_pip);
    public static final MenuAction TSHIFT_MODE_ACTION = new MenuAction((int) R.string.menu_setup_time_shifting_mode, 17, (int) R.drawable.ic_advanced);
    private String mActionDescription;
    private String mActionName;
    private int mActionNameResId;
    private Drawable mDrawable;
    private int mDrawableResId;
    private boolean mEnabled;
    private final int mType;

    public static boolean setActionDescription(MenuAction action, String actionDescription) {
        String oldDescription = action.mActionDescription;
        action.mActionDescription = actionDescription;
        return !TextUtils.equals(action.mActionDescription, oldDescription);
    }

    public static boolean setEnabled(MenuAction action, boolean enabled) {
        boolean changed = action.mEnabled != enabled;
        action.mEnabled = enabled;
        return changed;
    }

    public MenuAction(int actionNameResId, int type, int drawableResId) {
        this.mEnabled = true;
        this.mActionName = null;
        this.mActionNameResId = actionNameResId;
        this.mType = type;
        this.mDrawable = null;
        this.mDrawableResId = drawableResId;
    }

    public MenuAction(String actionName, int type, Drawable drawable) {
        this.mEnabled = true;
        this.mActionName = actionName;
        this.mActionNameResId = 0;
        this.mType = type;
        this.mDrawable = drawable;
        this.mDrawableResId = 0;
    }

    public String getActionName(Context context) {
        if (!TextUtils.isEmpty(this.mActionName)) {
            return this.mActionName;
        }
        return context.getString(this.mActionNameResId);
    }

    public String getActionDescription() {
        return this.mActionDescription;
    }

    public int getType() {
        return this.mType;
    }

    public Drawable getDrawable(Context context) {
        if (this.mDrawable == null) {
            this.mDrawable = context.getDrawable(this.mDrawableResId);
        }
        return this.mDrawable;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public int getActionNameResId() {
        return this.mActionNameResId;
    }

    public void setActionNameResId(int resId) {
        this.mActionNameResId = resId;
    }

    public static void showSetting(Context context) {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SRC, EventHelper.MTK_EVENT_EXTRA_SRC_LIVE_TV);
        context.startActivity(intent);
    }

    public static void showBroadcastTvSetting(Activity activity) {
        Intent intent = new Intent(activity, LiveTvSetting.class);
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SRC, EventHelper.MTK_EVENT_EXTRA_SRC_LIVE_TV);
        activity.startActivityForResult(intent, NavBasic.NAV_REQUEST_CODE);
    }

    public static void enterAndroidPIP() {
        ComponentStatusListener.getInstance().updateStatus(14, 0);
    }

    public static void showCCSetting(Context context) {
        if (CommonIntegration.getInstance().is3rdTVSource() || CommonIntegration.isSARegion()) {
            Intent intent = new Intent(context, LiveTvSetting.class);
            intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_3RD_CAPTION_SRC);
            context.startActivity(intent);
            return;
        }
        Intent intent2 = new Intent("android.settings.SETTINGS");
        intent2.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_CAPTIONS_SRC);
        context.startActivity(intent2);
    }

    public static void showPictureFormatSetting(Context context) {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_DISPLAY_MODE_SRC);
        context.startActivity(intent);
    }

    public static void showPowerSetting(Context context) {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_POWER_SRC);
        context.startActivity(intent);
    }

    public static void showMultiAudioSetting(Context context) {
        Intent intent = new Intent(context, LiveTvSetting.class);
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_MULTI_AUDIO_SRC);
        context.startActivity(intent);
    }

    public static void showPictureModeSetting(Context context) {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_PICTURE_STYLE_SRC);
        context.startActivity(intent);
    }

    public static void showSoundSpeakersSetting(Context context) {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_SPEAKER_SRC);
        context.startActivity(intent);
    }

    public static void showOAD(Activity activity) {
        if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            DvrManager.getInstance().setStopDvrNotResumeLauncher(true);
            StateDvrPlayback.getInstance().saveStopMessages();
        }
        Intent intent = new Intent(activity, NavOADActivity.class);
        intent.setAction("start_detect_oad");
        intent.setFlags(1073741824);
        activity.startActivity(intent);
    }

    public static void showCI(Activity activity) {
        CIMainDialog dialog = (CIMainDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CI_DIALOG);
        if (dialog != null) {
            dialog.setCurrCIViewType(CIMainDialog.CIViewType.CI_DATA_TYPE_CAM_MENU);
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_CI_DIALOG);
        }
    }

    public static void enterAndroidSource() {
        if (((SourceListView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_INPUT_SRC)) != null) {
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_INPUT_SRC);
        }
    }

    public static void showRecordList() {
        if (DvrManager.getInstance().diskIsReady()) {
            ComponentsManager.getInstance().hideAllComponents();
            if (DvrManager.getInstance().setState((StateBase) StateDvrFileList.getInstance(DvrManager.getInstance()))) {
                StateDvrFileList.getInstance().showPVRlist();
                return;
            }
            return;
        }
        DvrManager.getInstance().showPromptInfo(DvrManager.PRO_TIME_SHIFT_DISK_NOT_READY);
    }

    public static void showRecordDeviceInfo(Context context) {
        ComponentsManager.getInstance().hideAllComponents();
        new DiskSettingDialog(context, R.layout.pvr_timeshfit_deviceinfo).show();
    }

    public static void showScheduleList(Context context) {
        ComponentsManager.getInstance().hideAllComponents();
        ScheduleListDialog scheduleListDialog = new ScheduleListDialog(context, 0);
        scheduleListDialog.setEpgFlag(false);
        scheduleListDialog.show();
    }

    public static void showTShiftMode(Context context) {
        Intent intent = new Intent(context, LiveTvSetting.class);
        intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_RECORD_TSHIFT_SRC);
        context.startActivity(intent);
    }

    public static void showStartPvr(Context context) {
        ComponentsManager.getInstance().hideAllComponents();
        InstrumentationHandler.getInstance().sendKeyDownUpSync(130);
    }

    public static void showGinga(Context context) {
        GingaTvDialog gingaDlg = (GingaTvDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_GINGA_TV);
        if (gingaDlg != null) {
            ComponentsManager.getInstance().hideAllComponents();
            gingaDlg.show();
        }
    }
}
