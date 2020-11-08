package com.mediatek.wwtv.tvcenter.nav.view.common;

import android.view.KeyEvent;

public interface NavBasic {
    public static final String NAV_COMPONENT_HIDE_FLAG = "NavComponentHide";
    public static final String NAV_COMPONENT_SHOW_FLAG = "NavComponentShow";
    public static final int NAV_COMP_ID_BANNER = 16777218;
    public static final int NAV_COMP_ID_BASIC = 16777216;
    public static final int NAV_COMP_ID_CEC = 16777220;
    public static final int NAV_COMP_ID_CH_LIST = 16777221;
    public static final int NAV_COMP_ID_CI = 16777236;
    public static final int NAV_COMP_ID_CI_DIALOG = 16777245;
    public static final int NAV_COMP_ID_DIALOG_MSG = 16777224;
    public static final int NAV_COMP_ID_EAS = 16777217;
    public static final int NAV_COMP_ID_EWS = 16777225;
    public static final int NAV_COMP_ID_FAV_LIST = 16777226;
    public static final int NAV_COMP_ID_GINGA_TV = 16777228;
    public static final int NAV_COMP_ID_INFO_BAR = 16777229;
    public static final int NAV_COMP_ID_INPUT_SRC = 16777230;
    public static final int NAV_COMP_ID_MASK = 251658240;
    public static final int NAV_COMP_ID_MENU_OPTION_DIALOG = 16777244;
    public static final int NAV_COMP_ID_MISC = 16777240;
    public static final int NAV_COMP_ID_MUTE = 16777219;
    public static final int NAV_COMP_ID_OAD = 16777237;
    public static final int NAV_COMP_ID_POP = 16777235;
    public static final int NAV_COMP_ID_POWER_OFF = 16777242;
    public static final int NAV_COMP_ID_PVR_TIMESHIFT = 16777241;
    public static final int NAV_COMP_ID_PWD_DLG = 16777234;
    public static final int NAV_COMP_ID_SAT_SEL = 16777238;
    public static final int NAV_COMP_ID_SCART_MONITOR = 16777239;
    public static final int NAV_COMP_ID_SUNDRY = 16777222;
    public static final int NAV_COMP_ID_SUNDRY_DIALOG = 16777243;
    public static final int NAV_COMP_ID_TELETEXT = 16777233;
    public static final int NAV_COMP_ID_TIFTIMESHIFT_VIEW = 16777246;
    public static final int NAV_COMP_ID_TWINKLE_MSG = 16777232;
    public static final int NAV_COMP_ID_UPDATER = 16777231;
    public static final int NAV_COMP_ID_VOL_CTRL = 16777227;
    public static final int NAV_COMP_ID_ZOOM_PAN = 16777223;
    public static final int NAV_NATIVE_COMP_ID_BASIC = 33554432;
    public static final int NAV_NATIVE_COMP_ID_FVP = 33554438;
    public static final int NAV_NATIVE_COMP_ID_GINGA = 33554435;
    public static final int NAV_NATIVE_COMP_ID_HBBTV = 33554434;
    public static final int NAV_NATIVE_COMP_ID_MHEG5 = 33554433;
    public static final int NAV_NATIVE_COMP_ID_MHP = 33554436;
    public static final int NAV_NATIVE_COMP_ID_SUBTITLE_INFO = 33554437;
    public static final int NAV_PRIORITY_DEFAULT = 10;
    public static final int NAV_PRIORITY_HIGH_1 = 11;
    public static final int NAV_PRIORITY_HIGH_2 = 12;
    public static final int NAV_PRIORITY_HIGH_3 = 13;
    public static final int NAV_PRIORITY_LOW_1 = 9;
    public static final int NAV_PRIORITY_LOW_2 = 8;
    public static final int NAV_PRIORITY_LOW_3 = 7;
    public static final int NAV_REQUEST_CODE = 50331648;
    public static final int NAV_RESULT_CODE_MENU = 50331649;
    public static final int NAV_TIMEOUT_1 = 1000;
    public static final int NAV_TIMEOUT_10 = 10000;
    public static final int NAV_TIMEOUT_120 = 120000;
    public static final int NAV_TIMEOUT_2 = 2000;
    public static final int NAV_TIMEOUT_3 = 3000;
    public static final int NAV_TIMEOUT_5 = 5000;

    boolean KeyHandler(int i, KeyEvent keyEvent);

    boolean KeyHandler(int i, KeyEvent keyEvent, boolean z);

    boolean deinitView();

    int getComponentID();

    int getPriority();

    boolean initView();

    boolean isCoExist(int i);

    boolean isKeyHandler(int i);

    boolean isVisible();

    boolean startComponent();
}
