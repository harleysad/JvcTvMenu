package com.mediatek.wwtv.tvcenter.epg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.widget.Toast;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.cn.EPGCnActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEu2ndActivity;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.epg.sa.EPGSaActivity;
import com.mediatek.wwtv.tvcenter.epg.us.EPGUsActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import java.util.ArrayList;
import java.util.List;

public class EPGManager {
    public static final String BroadCast_Type_CND = "CND";
    public static final String BroadCast_Type_COL = "COL";
    public static final String BroadCast_Type_EU = "EU";
    public static final String BroadCast_Type_PAD = "PAD";
    private static final String TAG = "EPGManager";
    private static EPGManager staticManager;
    private Context mContext;

    public static EPGManager getInstance(Activity activity) {
        if (staticManager == null) {
            staticManager = new EPGManager(activity);
        }
        return staticManager;
    }

    public EPGManager(Context context) {
        this.mContext = context;
        if (!CommonIntegration.getInstance().isContextInit()) {
            CommonIntegration.getInstance().setContext(this.mContext.getApplicationContext());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x010e A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEnterEPGEnable() {
        /*
            r4 = this;
            r0 = 0
            r1 = 37
            boolean r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r1)
            if (r1 == 0) goto L_0x010f
            com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil r1 = com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil.getInstance()
            boolean r1 = r1.isAtvOnly()
            if (r1 == 0) goto L_0x0015
            goto L_0x010f
        L_0x0015:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.is3rdTVSource()
            r2 = 1
            if (r1 == 0) goto L_0x0021
            return r2
        L_0x0021:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isPipOrPopState()
            if (r1 == 0) goto L_0x002c
            return r0
        L_0x002c:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurrentSourceTv()
            if (r1 != 0) goto L_0x0037
            return r0
        L_0x0037:
            com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager r1 = com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager.getInstance()
            boolean r1 = r1.isPvrDialogShow()
            if (r1 == 0) goto L_0x0049
            java.lang.String r1 = "EPGManager"
            java.lang.String r3 = "Pvr dialog is  showing! Also need show banner,return true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r1, r3)
            return r2
        L_0x0049:
            int r1 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.getCurrentMarketRegion()
            switch(r1) {
                case 0: goto L_0x00ed;
                case 1: goto L_0x00c5;
                case 2: goto L_0x009d;
                case 3: goto L_0x0052;
                default: goto L_0x0050;
            }
        L_0x0050:
            goto L_0x010e
        L_0x0052:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurCHAnalog()
            if (r1 == 0) goto L_0x0064
            java.lang.String r1 = "EPGManager"
            java.lang.String r2 = "ATV. Do not support EPG!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r1, r2)
            return r0
        L_0x0064:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurrentSourceATVforEuPA()
            if (r1 == 0) goto L_0x006f
            return r0
        L_0x006f:
            com.mediatek.twoworlds.tv.MtkTvConfig r1 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()
            java.lang.String r1 = r1.getCountry()
            r3 = 3
            boolean r3 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r3)
            if (r3 == 0) goto L_0x0087
            java.lang.String r3 = "NZL"
            boolean r3 = r1.equalsIgnoreCase(r3)
            if (r3 == 0) goto L_0x0087
            return r0
        L_0x0087:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r3 = r3.isMenuInputTvBlock()
            if (r3 == 0) goto L_0x0092
            return r0
        L_0x0092:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            int r3 = r3.getAllEPGChannelLength()
            if (r3 > 0) goto L_0x010e
            return r0
        L_0x009d:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurCHAnalog()
            if (r1 == 0) goto L_0x00af
            java.lang.String r1 = "EPGManager"
            java.lang.String r2 = "ATV. Do not support EPG!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r1, r2)
            return r0
        L_0x00af:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isMenuInputTvBlock()
            if (r1 == 0) goto L_0x00ba
            return r0
        L_0x00ba:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            int r1 = r1.getAllEPGChannelLength(r2)
            if (r1 > 0) goto L_0x010e
            return r0
        L_0x00c5:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isMenuInputTvBlock()
            if (r1 == 0) goto L_0x00d0
            return r0
        L_0x00d0:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurCHAnalog()
            if (r1 == 0) goto L_0x00e2
            java.lang.String r1 = "EPGManager"
            java.lang.String r2 = "US ATV. Do not support EPG!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r1, r2)
            return r0
        L_0x00e2:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            int r1 = r1.getAllEPGChannelLength(r2)
            if (r1 > 0) goto L_0x010e
            return r0
        L_0x00ed:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isCurrentSourceDTV()
            if (r1 != 0) goto L_0x00f8
            return r0
        L_0x00f8:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.isMenuInputTvBlock()
            if (r1 == 0) goto L_0x0103
            return r0
        L_0x0103:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r1 = r1.hasDTVChannels()
            if (r1 != 0) goto L_0x010e
            return r0
        L_0x010e:
            return r2
        L_0x010f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.EPGManager.isEnterEPGEnable():boolean");
    }

    public boolean startEpg(Activity activity, int requestCode) {
        boolean supportEPG = MarketRegionInfo.isFunctionSupport(37);
        MtkLog.d(TAG, "supportEPG=" + supportEPG);
        if (!supportEPG || !isEnterEPGEnable()) {
            MtkLog.w(TAG, "EnterEPGEnable is false or Do not support EPG!");
            Toast.makeText(this.mContext, this.mContext.getString(R.string.no_support_epg), 0).show();
            return false;
        }
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getRealSize(outSize);
        ScreenConstant.SCREEN_WIDTH = outSize.x;
        ScreenConstant.SCREEN_HEIGHT = outSize.y;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
                startActivity(activity, EPGCnActivity.class, requestCode);
                return true;
            case 1:
                startActivity(activity, EPGUsActivity.class, requestCode);
                return true;
            case 2:
                startActivity(activity, EPGSaActivity.class, requestCode);
                return true;
            case 3:
                boolean support1DEPG = MarketRegionInfo.isFunctionSupport(46);
                MtkLog.w(TAG, "support1DEPG=" + support1DEPG);
                if (support1DEPG) {
                    startActivity(activity, EPGEu2ndActivity.class, requestCode);
                    return true;
                }
                startActivity(activity, EPGEuActivity.class, requestCode);
                return true;
            default:
                return true;
        }
    }

    public void openLockedSourceAndChannel(MtkTvChannelInfoBase currrntChannel) {
        MtkLog.e(TAG, "openLockedSourceAndChannel");
        if (currrntChannel != null && currrntChannel.isBlock()) {
            currrntChannel.setBlock(false);
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(currrntChannel);
            CommonIntegration.getInstance().setChannelList(1, list);
            MtkLog.e(TAG, "after set currrntChannel>>>>>" + currrntChannel + "   " + currrntChannel.isBlock());
        }
    }

    public void startActivity(Activity startActivity, Class<?> finishActivity, int requestCode) {
        Intent intent = new Intent(startActivity, finishActivity);
        intent.putExtra(finishActivity.getSimpleName(), startActivity.getClass().getSimpleName());
        MtkLog.e("xinsheng", "startActivity," + finishActivity.getSimpleName() + "," + startActivity.getClass().getSimpleName());
        startActivity.startActivityForResult(intent, requestCode);
    }
}
