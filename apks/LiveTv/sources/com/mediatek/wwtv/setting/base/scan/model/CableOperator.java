package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.wwtv.tvcenter.util.MtkLog;

public enum CableOperator {
    UPC,
    TELNET,
    OTHER,
    NULL,
    Unitymedia,
    Stofa,
    Yousee,
    Canal_Digital,
    Numericable,
    Ziggo,
    Comhem,
    TELE2,
    VOLIA,
    TELEMACH,
    ONLIME,
    AKADO,
    TKT,
    DIVAN_TV,
    NET1,
    KDG,
    KBW,
    BLIZOO,
    GLENTEN,
    TELECOLUMBUS,
    RCS_RDS,
    VOO,
    KDG_HD,
    KRS,
    TELEING,
    MTS,
    TVOE_STP,
    TVOE_EKA,
    TELEKOM;
    
    private static final int Canal_Digital_index = 5;
    private static final int Comhem_index = 8;
    public static final int DVBC_OPERATOR_NAME_AKADO = 13;
    public static final int DVBC_OPERATOR_NAME_BLIZOO = 19;
    public static final int DVBC_OPERATOR_NAME_CANAL_DIGITAL = 3;
    public static final int DVBC_OPERATOR_NAME_COMHEM = 2;
    public static final int DVBC_OPERATOR_NAME_DIVAN_TV = 15;
    public static final int DVBC_OPERATOR_NAME_GLENTEN = 21;
    public static final int DVBC_OPERATOR_NAME_KBW = 18;
    public static final int DVBC_OPERATOR_NAME_KDG = 17;
    public static final int DVBC_OPERATOR_NAME_KDG_HD = 25;
    public static final int DVBC_OPERATOR_NAME_KRS = 26;
    public static final int DVBC_OPERATOR_NAME_MTS = 28;
    public static final int DVBC_OPERATOR_NAME_NET1 = 16;
    public static final int DVBC_OPERATOR_NAME_NUMERICABLE = 9;
    public static final int DVBC_OPERATOR_NAME_ONLIME = 12;
    public static final int DVBC_OPERATOR_NAME_OTHERS = 0;
    public static final int DVBC_OPERATOR_NAME_RCS_RDS = 23;
    public static final int DVBC_OPERATOR_NAME_STOFA = 5;
    public static final int DVBC_OPERATOR_NAME_TELE2 = 4;
    public static final int DVBC_OPERATOR_NAME_TELECOLUMBUS = 22;
    public static final int DVBC_OPERATOR_NAME_TELEING = 27;
    public static final int DVBC_OPERATOR_NAME_TELEKOM = 31;
    public static final int DVBC_OPERATOR_NAME_TELEMACH = 11;
    public static final int DVBC_OPERATOR_NAME_TELENET = 20;
    public static final int DVBC_OPERATOR_NAME_TKT = 14;
    public static final int DVBC_OPERATOR_NAME_TVOE_EKA = 30;
    public static final int DVBC_OPERATOR_NAME_TVOE_STP = 29;
    public static final int DVBC_OPERATOR_NAME_UNITYMEDIA = 8;
    public static final int DVBC_OPERATOR_NAME_UPC = 1;
    public static final int DVBC_OPERATOR_NAME_VOLIA = 10;
    public static final int DVBC_OPERATOR_NAME_VOO = 24;
    public static final int DVBC_OPERATOR_NAME_YOUSEE = 6;
    public static final int DVBC_OPERATOR_NAME_ZIGGO = 7;
    private static final int Numericable_index = 6;
    private static final int OTHER_index = 9;
    private static final int Stofa_index = 3;
    private static final int TELNET_index = 1;
    private static final int UPC_index = 0;
    private static final int Unitymedia_index = 2;
    private static final int Yousee_index = 4;
    private static final int Ziggo_index = 7;

    public static CableOperator getOperatorFromIndex(int operatorIndex) {
        MtkLog.d("getOperatorFromIndex():index: " + operatorIndex);
        CableOperator cableOperator = OTHER;
        switch (operatorIndex) {
            case 0:
                return OTHER;
            case 1:
                return UPC;
            case 2:
                return Comhem;
            case 3:
                return Canal_Digital;
            case 4:
                return TELE2;
            case 5:
                return Stofa;
            case 6:
                return Yousee;
            case 7:
                return Ziggo;
            case 8:
                return Unitymedia;
            case 9:
                return Numericable;
            case 10:
                return VOLIA;
            case 11:
                return TELEMACH;
            case 12:
                return ONLIME;
            case 13:
                return AKADO;
            case 14:
                return TKT;
            case 15:
                return DIVAN_TV;
            case 16:
                return NET1;
            case 17:
                return KDG;
            case 18:
                return KBW;
            case 19:
                return BLIZOO;
            case 20:
                return TELNET;
            case 21:
                return GLENTEN;
            case 22:
                return TELECOLUMBUS;
            case 23:
                return RCS_RDS;
            case 24:
                return VOO;
            case 25:
                return KDG_HD;
            case 26:
                return KRS;
            case 27:
                return TELEING;
            case 28:
                return MTS;
            case 29:
                return TVOE_STP;
            case 30:
                return TVOE_EKA;
            case 31:
                return TELEKOM;
            default:
                return OTHER;
        }
    }
}
