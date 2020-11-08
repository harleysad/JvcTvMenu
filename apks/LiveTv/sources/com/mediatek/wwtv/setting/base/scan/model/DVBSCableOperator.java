package com.mediatek.wwtv.setting.base.scan.model;

public enum DVBSCableOperator {
    OTHERS(0),
    ASTRA_HD_PLUS(1),
    SKY_DEUTSCHLAND(2),
    AUSTRIASAT(3),
    CANNALDIGITAAL_HD(4),
    CANNALDIGITAAL_SD(5),
    TV_VLAANDEREN_HD(6),
    TV_VLAANDEREN_SD(7),
    SEZNAM_KANALU_PRO_CESKO(8),
    SEZNAM_KANALU_PRO_SLOVENSKO(9),
    ORS(10),
    TELESAT_BELGIUM(11),
    TELESAT_LUXEMBOURG(12),
    CANNAL_DIGITAL(13),
    N_NA_KARTE(14),
    DIGITURK_TURKSAT(15),
    DIGITURK_EUTELSAT(16),
    FRANSAT(17),
    CYFRAPLUS(18),
    CYFROWY_POLSAT(19),
    DSMART(20),
    NTVPLUS(21),
    ASTRA_INTERNATIONAL_LCN(22),
    NAME_SMART_HD_PLUS(23),
    NAME_NC_PLUS(24),
    NAME_TIVUSAT(25),
    NAME_HELLO(26),
    NAME_TKGS(27),
    FREEVIEW(28),
    OPERATOR_RDS(29),
    OPERATOR_FSD(30),
    OPERATOR_TIVIBU(31);

    private DVBSCableOperator(int index) {
    }

    public static DVBSCableOperator getOperatorFromIndex(int index) {
        return values()[index];
    }

    public static int covertToTVAPIOp(DVBSCableOperator op) {
        switch (op) {
            case OTHERS:
                return 0;
            case ASTRA_HD_PLUS:
                return 1;
            case SKY_DEUTSCHLAND:
                return 2;
            case AUSTRIASAT:
                return 3;
            case CANNALDIGITAAL_HD:
                return 4;
            case CANNALDIGITAAL_SD:
                return 5;
            case TV_VLAANDEREN_HD:
                return 6;
            case TV_VLAANDEREN_SD:
                return 7;
            case SEZNAM_KANALU_PRO_CESKO:
                return 8;
            case SEZNAM_KANALU_PRO_SLOVENSKO:
                return 9;
            case ORS:
                return 10;
            case TELESAT_BELGIUM:
                return 11;
            case TELESAT_LUXEMBOURG:
                return 12;
            case CANNAL_DIGITAL:
                return 13;
            case N_NA_KARTE:
                return 14;
            case DIGITURK_TURKSAT:
                return 15;
            case DIGITURK_EUTELSAT:
                return 16;
            case FRANSAT:
                return 17;
            case CYFRAPLUS:
                return 18;
            case CYFROWY_POLSAT:
                return 19;
            case DSMART:
                return 20;
            case NTVPLUS:
                return 21;
            case ASTRA_INTERNATIONAL_LCN:
                return 22;
            case NAME_SMART_HD_PLUS:
                return 23;
            case NAME_NC_PLUS:
                return 24;
            case NAME_TIVUSAT:
                return 25;
            case NAME_HELLO:
                return 26;
            case NAME_TKGS:
                return 27;
            case FREEVIEW:
                return 28;
            case OPERATOR_RDS:
                return 29;
            case OPERATOR_FSD:
                return 30;
            case OPERATOR_TIVIBU:
                return 31;
            default:
                return 0;
        }
    }

    public static DVBSCableOperator coverTKUIOP(int tvapiOp) {
        switch (tvapiOp) {
            case 0:
                return OTHERS;
            case 1:
                return ASTRA_HD_PLUS;
            case 2:
                return SKY_DEUTSCHLAND;
            case 3:
                return AUSTRIASAT;
            case 4:
                return CANNALDIGITAAL_HD;
            case 5:
                return CANNALDIGITAAL_SD;
            case 6:
                return TV_VLAANDEREN_HD;
            case 7:
                return TV_VLAANDEREN_SD;
            case 8:
                return SEZNAM_KANALU_PRO_CESKO;
            case 9:
                return SEZNAM_KANALU_PRO_SLOVENSKO;
            case 10:
                return ORS;
            case 11:
                return TELESAT_BELGIUM;
            case 12:
                return TELESAT_LUXEMBOURG;
            case 13:
                return CANNAL_DIGITAL;
            case 14:
                return N_NA_KARTE;
            case 15:
                return DIGITURK_TURKSAT;
            case 16:
                return DIGITURK_EUTELSAT;
            case 17:
                return FRANSAT;
            case 18:
                return CYFRAPLUS;
            case 19:
                return CYFROWY_POLSAT;
            case 20:
                return DSMART;
            case 21:
                return NTVPLUS;
            case 22:
                return ASTRA_INTERNATIONAL_LCN;
            case 23:
                return NAME_SMART_HD_PLUS;
            case 24:
                return NAME_NC_PLUS;
            case 25:
                return NAME_TIVUSAT;
            case 26:
                return NAME_HELLO;
            case 27:
                return NAME_TKGS;
            case 28:
                return FREEVIEW;
            case 29:
                return OPERATOR_RDS;
            case 30:
                return OPERATOR_FSD;
            case 31:
                return OPERATOR_TIVIBU;
            default:
                return OTHERS;
        }
    }
}
