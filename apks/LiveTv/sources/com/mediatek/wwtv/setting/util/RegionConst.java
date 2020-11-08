package com.mediatek.wwtv.setting.util;

import android.support.v4.media.subtitle.Cea708CCParser;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.KeyMap;
import com.mediatek.wwtv.tvcenter.util.MessageType;

public class RegionConst {
    public static int[][] ecuadorCityAreaCode = {ecuador_pro_azuay_area_code, ecuador_pro_bol_area_code, ecuador_pro_can_area_code, ecuador_pro_bol_car_code, ecuador_pro_chi_area_code, ecuador_pro_cot_area_code, ecuador_pro_elo_area_code, ecuador_pro_esm_area_code, ecuador_pro_gal_area_code, ecuador_pro_gua_area_code, ecuador_pro_imb_area_code, ecuador_pro_pic_area_code, ecuador_pro_loj_area_code, ecuador_pro_los_area_code, ecuador_pro_man_area_code, ecuador_pro_mor_area_code, ecuador_pro_nac_area_code, ecuador_pro_nap_area_code, ecuador_pro_ore_area_code, ecuador_pro_pas_area_code, ecuador_pro_san_area_code, ecuador_pro_sdd_area_code, ecuador_pro_suc_area_code, ecuador_pro_tun_area_code, ecuador_pro_zam_area_code, ecuador_pro_zon_area_code};
    public static int[] ecuadorProsCity = {R.array.menu_setup_region_setting_ecuador_pro_azuay_array, R.array.menu_setup_region_setting_ecuador_pro_bolivar_array, R.array.menu_setup_region_setting_ecuador_pro_canar_array, R.array.menu_setup_region_setting_ecuador_pro_carchi_array, R.array.menu_setup_region_setting_ecuador_pro_chimborazo_array, R.array.menu_setup_region_setting_ecuador_pro_cotopaxi_array, R.array.menu_setup_region_setting_ecuador_pro_elo_array, R.array.menu_setup_region_setting_ecuador_pro_esm_array, R.array.menu_setup_region_setting_ecuador_pro_gal_array, R.array.menu_setup_region_setting_ecuador_pro_gua_array, R.array.menu_setup_region_setting_ecuador_pro_imb_array, R.array.menu_setup_region_setting_ecuador_pro_pic_array, R.array.menu_setup_region_setting_ecuador_pro_loj_array, R.array.menu_setup_region_setting_ecuador_pro_los_array, R.array.menu_setup_region_setting_ecuador_pro_man_array, R.array.menu_setup_region_setting_ecuador_pro_mor_array, R.array.menu_setup_region_setting_ecuador_pro_nac_array, R.array.menu_setup_region_setting_ecuador_pro_nap_array, R.array.menu_setup_region_setting_ecuador_pro_ore_array, R.array.menu_setup_region_setting_ecuador_pro_pas_array, R.array.menu_setup_region_setting_ecuador_pro_san_array, R.array.menu_setup_region_setting_ecuador_pro_sdd_array, R.array.menu_setup_region_setting_ecuador_pro_suc_array, R.array.menu_setup_region_setting_ecuador_pro_tun_array, R.array.menu_setup_region_setting_ecuador_pro_zam_array, R.array.menu_setup_region_setting_ecuador_pro_zon_array};
    public static int[] ecuador_pro_azuay_area_code = {25, 41, 47, 57, 68, 72, 73, 119, 126, Cea708CCParser.Const.CODE_C1_DLC, Cea708CCParser.Const.CODE_C1_DF3, KeyMap.KEYCODE_MTKIR_SOURCE, 192, 200, MessageType.NAV_SHORTTIP_TEXTVIEW_DIMISS};
    public static int[] ecuador_pro_bol_area_code = {23, 37, 38, 53, 77, 96, 182};
    public static int[] ecuador_pro_bol_car_code = {20, 65, 111, 116, KeyMap.KEYCODE_MTKIR_YELLOW, 215};
    public static int[] ecuador_pro_can_area_code = {12, 19, 26, 51, 60, 92, MessageType.NAV_CURRENT_CHANNEL_NO_SIGNAL};
    public static int[] ecuador_pro_chi_area_code = {3, 35, 42, 44, 48, 75, 76, Cea708CCParser.Const.CODE_C1_CW6, 147, 169};
    public static int[] ecuador_pro_cot_area_code = {91, 97, Cea708CCParser.Const.CODE_C1_DSW, 159, 173, 198, MessageType.NAV_ADUST_VOLUME_DIMISS};
    public static int[] ecuador_pro_elo_area_code = {9, 11, 16, 36, 56, 80, 95, 104, 107, Cea708CCParser.Const.CODE_C1_TGW, Cea708CCParser.Const.CODE_C1_SWA, Cea708CCParser.Const.CODE_C1_DF1, 194, 224};
    public static int[] ecuador_pro_esm_area_code = {10, 62, 64, 88, 118, KeyMap.KEYCODE_MTKIR_CHUP, 170, 181};
    public static int[] ecuador_pro_gal_area_code = {82, 177, 190};
    public static int[] ecuador_pro_gua_area_code = {4, 15, 17, 43, 46, 50, 52, 61, 63, 69, 78, 83, 101, 110, MessageType.MESSAGE_REFRESH_SIGNAL_QUALITY_LEVEL, MessageType.MESSAGE_AFTER_SCAN_SELECT_CHANNEL, MessageType.MESSAGE_INACTIVE_CHANNELS, Cea708CCParser.Const.CODE_C1_CW5, Cea708CCParser.Const.CODE_C1_SPA, Cea708CCParser.Const.CODE_C1_DF0, KeyMap.KEYCODE_MTKIR_MTKIR_CC, 176, 179, 193, MessageType.NAV_ZOOMVIEW_DIMISS};
    public static int[] ecuador_pro_imb_area_code = {6, 45, 81, 128, 149, KeyMap.KEYCODE_MTKIR_GREEN};
    public static int[] ecuador_pro_loj_area_code = {24, 29, 31, 34, 66, 71, 100, 103, MessageType.MESSAGE_START_SCAN, 136, 150, 161, KeyMap.KEYCODE_MTKIR_INFO, 199, MessageType.NAV_SHOW_SPECIALVIEW, KeyMap.KEYCODE_MTKIR_SLEEP};
    public static int[] ecuador_pro_los_area_code = {13, 14, 22, 112, 114, Cea708CCParser.Const.CODE_C1_CW4, Cea708CCParser.Const.CODE_C1_DF4, 163, KeyMap.KEYCODE_MTKIR_CHDN, 216, 217, MessageType.NAV_CHANGE_IS_FIRST, MessageType.FORM_TK_TO_MENUMAIN};
    public static int[] ecuador_pro_man_area_code = {1, 21, 40, 54, 67, 84, 85, 86, 87, 106, 115, 124, 130, 143, 148, Cea708CCParser.Const.CODE_C1_DF2, Cea708CCParser.Const.CODE_C1_DF5, KeyMap.KEYCODE_MTKIR_PIPPOP, 187, 188, MessageType.NAV_CURRENT_SOURCE_LOCKED, 214};
    public static int[] ecuador_pro_mor_area_code = {74, 79, 98, 99, 117, Cea708CCParser.Const.CODE_C1_CW1, 135, 180, 195, MessageType.NAV_CURRENT_SOURCE_NO_SIGNAL, MessageType.NAV_CURRENT_CHANNEL_LOCKED, 213};
    public static int[] ecuador_pro_nac_area_code = {0};
    public static int[] ecuador_pro_nap_area_code = {8, 27, 55, KeyMap.KEYCODE_MTKIR_MUTE, MessageType.NAV_TV_SOURCE_NO_SIGNAL};
    public static int[] ecuador_pro_ore_area_code = {2, 89, 102, 127};
    public static int[] ecuador_pro_pas_area_code = {7, 109, Cea708CCParser.Const.CODE_C1_DLW, 189};
    public static int[] ecuador_pro_pic_area_code = {30, 108, Cea708CCParser.Const.CODE_C1_SPC, Cea708CCParser.Const.CODE_C1_SPL, Cea708CCParser.Const.CODE_C1_DF6, 168, KeyMap.KEYCODE_MTKIR_GUIDE, KeyMap.KEYCODE_MTKIR_RED};
    public static int[] ecuador_pro_san_area_code = {90, 174, 191};
    public static int[] ecuador_pro_sdd_area_code = {197};
    public static int[] ecuador_pro_suc_area_code = {28, 49, 70, 93, 160, MessageType.NAV_SOURCE_LISTVIEW_DIMISS, MessageType.NAV_SCANN_CHANNEL};
    public static int[] ecuador_pro_tun_area_code = {5, 18, 33, 113, Cea708CCParser.Const.CODE_C1_DLY, 162, KeyMap.KEYCODE_MTKIR_BLUE, 196, 212};
    public static int[] ecuador_pro_zam_area_code = {32, 39, 58, 120, Cea708CCParser.Const.CODE_C1_CW3, Cea708CCParser.Const.CODE_C1_HDW, 220, 221, 222};
    public static int[] ecuador_pro_zon_area_code = {59, 94, 105};
    public static int[][] phiCityAreaCodeLuzong = {phi_pro_ncr_area_code, phi_pro_car_area_code, phi_pro_reg1_area_code, phi_pro_reg2_area_code, phi_pro_reg3_area_code, phi_pro_reg4a_area_code, phi_pro_reg4b_area_code, phi_pro_reg5_area_code};
    public static int[][] phiCityAreaCodeMindanao = {phi_pro_reg9_area_code, phi_pro_reg10_area_code, phi_pro_reg11_area_code, phi_pro_reg12_area_code, phi_pro_reg13_area_code, phi_pro_armm_area_code};
    public static int[][] phiCityAreaCodeVisayas = {phi_pro_reg6_area_code, phi_pro_reg7_area_code, phi_pro_reg8_area_code};
    public static int[] phiProsCityLuzong = {R.array.menu_setup_region_setting_phi_pro_ncr_array, R.array.menu_setup_region_setting_phi_pro_car_array, R.array.menu_setup_region_setting_phi_pro_reg1_array, R.array.menu_setup_region_setting_phi_pro_reg2_array, R.array.menu_setup_region_setting_phi_pro_reg3_array, R.array.menu_setup_region_setting_phi_pro_reg4a_array, R.array.menu_setup_region_setting_phi_pro_reg4b_array, R.array.menu_setup_region_setting_phi_pro_reg5_array};
    public static int[] phiProsCityMindanao = {R.array.menu_setup_region_setting_phi_pro_reg9_array, R.array.menu_setup_region_setting_phi_pro_reg10_array, R.array.menu_setup_region_setting_phi_pro_reg11_array, R.array.menu_setup_region_setting_phi_pro_reg12_array, R.array.menu_setup_region_setting_phi_pro_reg13_array, R.array.menu_setup_region_setting_phi_pro_armm_array};
    public static int[] phiProsCityVisayas = {R.array.menu_setup_region_setting_phi_pro_reg6_array, R.array.menu_setup_region_setting_phi_pro_reg7_array, R.array.menu_setup_region_setting_phi_pro_reg8_array};
    public static int[] phi_pro_armm_area_code = {2241, 2242, 2243, 2244, 2245};
    public static int[] phi_pro_car_area_code = {1089, 1090, 1091, 1092, 1093, 1094, 1095};
    public static int[] phi_pro_ncr_area_code = {1057, 1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073};
    public static int[] phi_pro_reg10_area_code = {2113, 2114, 2115, 2116, 2117, 2118, 2119};
    public static int[] phi_pro_reg11_area_code = {2145, 2146, 2147, 2148, 2149};
    public static int[] phi_pro_reg12_area_code = {2177, 2178, 2179, 2180, 2181, 2182};
    public static int[] phi_pro_reg13_area_code = {2209, 2210, 2211, 2212, 2213, 2214};
    public static int[] phi_pro_reg1_area_code = {1121, 1122, 1123, 1124};
    public static int[] phi_pro_reg2_area_code = {1153, 1154, 1155, 1156, 1157, 1158};
    public static int[] phi_pro_reg3_area_code = {1185, 1186, 1187, 1188, 1189, 1190, 1191, 1192, 1193};
    public static int[] phi_pro_reg4a_area_code = {1217, 1218, 1219, 1220, 1221, 1222};
    public static int[] phi_pro_reg4b_area_code = {1249, 1250, 1251, 1252, 1253, 1254};
    public static int[] phi_pro_reg5_area_code = {1281, 1282, 1283, 1284, 1285, 1286, 1287};
    public static int[] phi_pro_reg6_area_code = {1569, 1570, 1571, 1572, 1573, 1574, 1575, 1576};
    public static int[] phi_pro_reg7_area_code = {1601, 1602, 1603, 1604, 1605, 1606};
    public static int[] phi_pro_reg8_area_code = {1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640};
    public static int[] phi_pro_reg9_area_code = {2081, 2082, 2083, 2084, 2085};

    public static int getEcuadorCityArray(int position) {
        return ecuadorProsCity[position];
    }

    public static int[] getEcuadorAreaCodeArray(int position) {
        return ecuadorCityAreaCode[position];
    }
}
