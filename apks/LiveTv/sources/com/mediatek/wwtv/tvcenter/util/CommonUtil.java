package com.mediatek.wwtv.tvcenter.util;

import android.os.SystemProperties;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvHBBTV;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommonUtil {
    public static final String COUNTRY_UK = "GBR";
    public static final String TAG = "CommonUtil";

    public static String getISO2CountryByISO3(String code) {
        String[] isoCountries = Locale.getISOCountries();
        Map<String, Locale> localeMap = new HashMap<>();
        for (String locale : isoCountries) {
            Locale locale2 = new Locale("", locale);
            localeMap.put(locale2.getISO3Country().toUpperCase(), locale2);
        }
        return (TextUtils.isEmpty(code) != 0 || localeMap.get(code) == null) ? "" : localeMap.get(code).getCountry();
    }

    public static boolean isSupportFVP() {
        return isSupportFVP(false);
    }

    public static boolean isSupportFVP(boolean ignoreTOU) {
        if (SystemProperties.getInt("vendor.mtk.tif.fvp", 0) != 1) {
            return false;
        }
        boolean[] isFVPSupportArray = {false};
        MtkTvHBBTV.getInstance().getFVPSupport(isFVPSupportArray);
        MtkLog.d(TAG, "isFVPSupport=" + isFVPSupportArray[0]);
        if (!isFVPSupportArray[0] || !MtkTvConfig.getInstance().getCountry().equals("GBR")) {
            return false;
        }
        if (!ignoreTOU) {
            int iou = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_MISC_MDS_TOU_STATE);
            MtkLog.d(TAG, "isenable=" + iou);
            if (1 != iou) {
                return false;
            }
        }
        return true;
    }
}
