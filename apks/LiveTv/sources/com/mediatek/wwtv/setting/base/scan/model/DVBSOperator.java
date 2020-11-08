package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import com.mediatek.wwtv.tvcenter.R;
import java.util.HashMap;
import java.util.Map;

public class DVBSOperator {
    private Map<String, Integer> operators = new HashMap();

    public DVBSOperator(Context context) {
        this.operators.put(context.getString(R.string.dvbs_operator_others), 0);
        this.operators.put(context.getString(R.string.dvbs_operator_astra_hd_plus), 1);
        this.operators.put(context.getString(R.string.dvbs_operator_sky_deutschland), 2);
        this.operators.put(context.getString(R.string.dvbs_operator_austriasat), 3);
        this.operators.put(context.getString(R.string.dvbs_operator_cannaldigitaal), 4);
        this.operators.put(context.getString(R.string.dvbs_operator_cannaldigitaal_sd), 5);
        this.operators.put(context.getString(R.string.dvbs_operator_tv_vlaanderen), 6);
        this.operators.put(context.getString(R.string.dvbs_operator_tv_vlaanderen_sd), 7);
        this.operators.put(context.getString(R.string.dvbs_operator_seznam_kanalu_pro_cesko), 8);
        this.operators.put(context.getString(R.string.dvbs_operator_seznam_kanalu_pro_slovensko), 9);
        this.operators.put(context.getString(R.string.dvbs_operator_ors), 10);
        this.operators.put(context.getString(R.string.dvbs_operator_telesat), 11);
        this.operators.put(context.getString(R.string.dvbs_operator_telesat_luxembourg), 12);
        this.operators.put(context.getString(R.string.dvbs_operator_cannal_digital), 13);
        this.operators.put(context.getString(R.string.dvbs_operator_n_na_karte), 14);
        this.operators.put(context.getString(R.string.dvbs_operator_digiturk_turksat), 15);
        this.operators.put(context.getString(R.string.dvbs_operator_digiturk_eutelsat), 16);
        this.operators.put(context.getString(R.string.dvbs_operator_fransat), 17);
        this.operators.put(context.getString(R.string.dvbs_operator_cyfraplus), 18);
        this.operators.put(context.getString(R.string.dvbs_operator_cyfrowy_polsat), 19);
        this.operators.put(context.getString(R.string.dvbs_operator_dsmart), 20);
        this.operators.put(context.getString(R.string.dvbs_operator_ntvplus), 21);
        this.operators.put(context.getString(R.string.dvbs_operator_astra_international_lcn), 22);
        this.operators.put(context.getString(R.string.dvbs_operator_name_smart_hd_plus), 23);
        this.operators.put(context.getString(R.string.dvbs_operator_name_nc_plus), 24);
        this.operators.put(context.getString(R.string.dvbs_operator_name_tivusat), 25);
        this.operators.put(context.getString(R.string.dvbs_operator_name_turksat_hello), 26);
        this.operators.put(context.getString(R.string.dvbs_operator_name_tkgs), 27);
        this.operators.put(context.getString(R.string.dvbs_operator_name_freeview), 28);
        this.operators.put(context.getString(R.string.dvbs_operator_name_digi), 29);
        this.operators.put(context.getString(R.string.dvbs_operator_name_diveo), 30);
        this.operators.put(context.getString(R.string.dvbs_operator_name_tivibu), 31);
        this.operators.put(context.getString(R.string.dvbs_operator_name_tricolor), 32);
        this.operators.put(context.getString(R.string.dvbs_operator_name_simpliTV), 33);
        this.operators.put(context.getString(R.string.dvbs_operator_name_telekarta), 35);
        this.operators.put(context.getString(R.string.dvbs_operator_name_white_label_platform_lcn), 36);
        this.operators.put(context.getString(R.string.dvbs_operator_name_digi_tv_cze), 114);
        this.operators.put(context.getString(R.string.dvbs_operator_name_digi_tv_svk), 115);
    }

    public int getOperatorByName(String name) {
        return this.operators.get(name).intValue();
    }

    public String getNameByOperator(int id) {
        for (Map.Entry<String, Integer> entry : this.operators.entrySet()) {
            if (entry.getValue().intValue() == id) {
                return entry.getKey();
            }
        }
        return "OTHERS";
    }
}
