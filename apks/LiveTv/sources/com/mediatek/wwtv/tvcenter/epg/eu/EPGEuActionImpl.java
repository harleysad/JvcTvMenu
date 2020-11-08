package com.mediatek.wwtv.tvcenter.epg.eu;

import android.app.Activity;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class EPGEuActionImpl implements EPGEuIAction {
    private static final String TAG = "EPGEuActionImpl";
    /* access modifiers changed from: private */
    public EPGEuIView iUIView;
    /* access modifiers changed from: private */
    public Activity mContext;
    private boolean mIs3rdTVSource;
    private boolean mIsCountryUK;
    private boolean mIsCurrentSourceATV;
    /* access modifiers changed from: private */
    public DataReader mReader = DataReader.getInstance();
    private int timeType12_24;

    public EPGEuActionImpl(Activity context, EPGEuIView view) {
        this.mContext = context;
        this.iUIView = view;
        initData();
    }

    public boolean is3rdTVSource() {
        return this.mIs3rdTVSource;
    }

    public void getChannelList() {
        new Thread(new Runnable() {
            /* JADX WARNING: type inference failed for: r1v5, types: [java.util.List] */
            /* JADX WARNING: type inference failed for: r1v8, types: [java.util.List] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r7 = this;
                    r0 = 0
                    boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.supportTIFFunction()
                    r2 = 1
                    if (r1 == 0) goto L_0x0016
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelListByTIF(r2)
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                    goto L_0x0023
                L_0x0016:
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl r1 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r1 = r1.mReader
                    java.util.List r1 = r1.getAllChannelList(r2)
                    r0 = r1
                    java.util.ArrayList r0 = (java.util.ArrayList) r0
                L_0x0023:
                    if (r0 != 0) goto L_0x002d
                    java.lang.String r1 = "EPGEuActionImpl"
                    java.lang.String r2 = "getChannelList------->channelList==null"
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
                    return
                L_0x002d:
                    r1 = r0
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl r3 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl.this
                    com.mediatek.wwtv.tvcenter.epg.DataReader r3 = r3.mReader
                    int r3 = r3.getCurrentPlayChannelPosition()
                    int r4 = r3 / 6
                    int r4 = r4 + r2
                    java.lang.String r2 = "EPGEuActionImpl"
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder
                    r5.<init>()
                    java.lang.String r6 = "getChannelList------->selectIndex="
                    r5.append(r6)
                    r5.append(r3)
                    java.lang.String r6 = ",pageNum="
                    r5.append(r6)
                    r5.append(r4)
                    java.lang.String r6 = ",channelList.size="
                    r5.append(r6)
                    int r6 = r0.size()
                    r5.append(r6)
                    java.lang.String r5 = r5.toString()
                    com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r5)
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl r2 = com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl.this
                    android.app.Activity r2 = r2.mContext
                    com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl$1$1 r5 = new com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl$1$1
                    r5.<init>(r1, r3, r4)
                    r2.runOnUiThread(r5)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActionImpl.AnonymousClass1.run():void");
            }
        }).start();
    }

    private void initData() {
        this.mIsCurrentSourceATV = CommonIntegration.getInstance().isCurrentSourceATV();
        this.mIs3rdTVSource = CommonIntegration.getInstance().is3rdTVSource();
        this.timeType12_24 = EPGUtil.judgeFormatTime12_24(this.mContext);
        this.mReader.loadProgramType();
        this.mReader.loadMonthAndWeekRes();
        String country = MtkTvConfig.getInstance().getCountry();
        MtkLog.d(TAG, "country=" + country);
        if (TextUtils.equals("GBR", country)) {
            this.mIsCountryUK = true;
        } else {
            this.mIsCountryUK = false;
        }
    }

    public int getTimeType12_24() {
        return this.timeType12_24;
    }

    public void getProgramListByChId(final EPGChannelInfo channelInfo, final int dayNum, final int startHour) {
        this.iUIView.showLoading();
        new Thread(new Runnable() {
            public void run() {
                final List<EPGProgramInfo> programList = EPGEuActionImpl.this.mReader.getProgramListByChId(channelInfo, dayNum, startHour, 24 - startHour);
                EPGEuActionImpl.this.mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        EPGEuActionImpl.this.iUIView.dismissLoading();
                        EPGEuActionImpl.this.iUIView.updateProgramList(programList);
                    }
                });
            }
        }).start();
    }

    public void setActiveWindow(final EPGChannelInfo channel, final int dayNum, final int startHour) {
        MtkLog.d(TAG, "setActiceWindow------->channel=[id=" + channel.mId + ",name=" + channel.getName() + "],dayNum=" + dayNum + ",startHour=" + startHour);
        new Thread(new Runnable() {
            public void run() {
                EPGEuActionImpl.this.mReader.setActiveWindow(channel, dayNum, startHour, 24 - startHour);
            }
        }).start();
    }

    public void refreshDetailsInfo(final EPGProgramInfo info, final int channelId) {
        new Thread(new Runnable() {
            public void run() {
                final EPGProgramInfo newInfo = EPGEuActionImpl.this.regetProgramInfo(info, channelId);
                EPGEuActionImpl.this.mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        EPGEuActionImpl.this.iUIView.updateEventDetails(newInfo);
                    }
                });
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public EPGProgramInfo regetProgramInfo(EPGProgramInfo info, int channelId) {
        if (info == null) {
            MtkLog.w(TAG, "regetProgramInfo-------> info==null!");
            return info;
        }
        MtkLog.d(TAG, "regetProgramInfo------->MtkTvEventInfoBase_EventId=" + info.getProgramId() + ",MtkTvChannelInfoBase.channelId=" + channelId);
        if (this.mIsCountryUK && info.getMainType() > this.mReader.getMainType().length) {
            info.setProgramType(this.mContext.getString(R.string.nav_epg_not_support));
        } else if (info.getMainType() >= 1) {
            info.setProgramType(this.mReader.getMainType()[info.getMainType() - 1]);
        } else {
            info.setProgramType(this.mContext.getString(R.string.nav_epg_unclassified));
        }
        String strRating = this.mReader.mapRating2CustomerStr(info.getRatingValue(), info.getRatingType());
        MtkLog.d(TAG, "regetProgramInfo------->strRating=" + strRating);
        if (strRating != null) {
            info.setRatingType(strRating);
        }
        return info;
    }

    public boolean isCountryUK() {
        return this.mIsCountryUK;
    }

    public boolean isCurrentSourceATV() {
        return this.mIsCurrentSourceATV;
    }

    public void checkPWDShow() {
        new Thread(new Runnable() {
            public void run() {
                final int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
                MtkLog.w(EPGEuActionImpl.TAG, "checkPWDShow------->showFlag=" + showFlag);
                EPGEuActionImpl.this.mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        EPGEuActionImpl.this.iUIView.updateLockStatus(showFlag == 0);
                    }
                });
            }
        }).start();
    }

    public void clearActiveWindow() {
        new Thread(new Runnable() {
            public void run() {
                MtkTvEvent.getInstance().clearActiveWindows();
            }
        }).start();
    }
}
