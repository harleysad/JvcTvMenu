package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvChannelListBase {
    public static final int CHLST_ITERATE_DIR_FROM_FIRST = 0;
    public static final int CHLST_ITERATE_DIR_FROM_LAST = 1;
    public static final int CHLST_ITERATE_DIR_NEXT = 2;
    public static final int CHLST_ITERATE_DIR_PREV = 3;
    public static final int CHLST_OPERATOR_ADD = 0;
    public static final int CHLST_OPERATOR_DEL = 2;
    public static final int CHLST_OPERATOR_MOD = 1;
    public static final int CHLST_RET_FAIL = -1;
    public static final int CHLST_RET_OK = 0;
    static final String TAG = "MtkTvChannelList";

    public static int getCurrentChannelId() {
        return TVNativeWrapper.getCurrentChannelId_native();
    }

    public static MtkTvChannelInfoBase getCurrentChannel() {
        MtkTvChannelInfoBase channelInfo;
        Log.d(TAG, "Enter getCurrentChannel:\n");
        int channelId = TVNativeWrapper.getCurrentChannelId_native();
        if (channelId != -1) {
            int channelType = TVNativeWrapper.getChannelType_native(channelId);
            if (1 == channelType) {
                channelInfo = new MtkTvAnalogChannelInfo();
            } else if (2 == channelType) {
                channelInfo = new MtkTvDvbChannelInfo();
            } else if (3 == channelType) {
                channelInfo = new MtkTvATSCChannelInfo();
            } else if (5 == channelType) {
                channelInfo = new MtkTvISDBChannelInfo();
            } else {
                Log.e(TAG, "Invalid channel type--" + channelType + "!\n");
            }
            Log.d(TAG, "channelId = " + channelId + ", channelType = " + channelType + "...\n");
            if (TVNativeWrapper.getCurrentChannel_native(channelInfo) != 0) {
                Log.e(TAG, "TVNativeWrapper.getCurrentChannel_native failed!\n");
            } else {
                Log.i(TAG, channelInfo.toString());
                return channelInfo;
            }
        }
        MtkTvChannelInfoBase channelInfo2 = new MtkTvChannelInfoBase();
        channelInfo2.setChannelId(-1);
        return channelInfo2;
    }

    public static int setCurrentChannel(MtkTvChannelInfoBase channelInfo) {
        return TVNativeWrapper.setCurrentChannel_native(channelInfo.getChannelId());
    }

    public static int set2ndCurrentChannelBySvlId(MtkTvChannelInfoBase channelInfo) {
        return TVNativeWrapper.set2ndCurrentChannelBySvlId_native(channelInfo.getSvlId(), channelInfo.getChannelId());
    }

    public static int setCurrentChannelBySvlId(MtkTvChannelInfoBase channelInfo) {
        return TVNativeWrapper.setCurrentChannelBySvlId_native(channelInfo.getSvlId(), channelInfo.getChannelId());
    }

    public MtkTvChannelInfoBase getCurrentChannelFromNav(int winId) {
        Log.d(TAG, "Enter getCurrentChannelFromNav: winId = " + winId);
        return TVNativeWrapper.getCurrentChannelFromNav_native(winId);
    }

    public void bookChannel(MtkTvChannelInfoBase channel, long dateTime) {
    }

    public List<MtkTvChannelInfoBase> getChannelListByFilter(int svlId, int filter, int channelId, int prevCount, int nextCount) {
        Log.d(TAG, "Enter getChannelListByFilter(" + svlId + "," + filter + "," + channelId + "," + prevCount + "," + nextCount + ")\n");
        ArrayList arrayList = new ArrayList();
        synchronized (this) {
            int ret = TVNativeWrapper.getChannelListByFilter_native(svlId, filter, channelId, prevCount, nextCount, arrayList);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getChannelListByFilter_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave getChannelListByFilter(), size=" + arrayList.size() + "\n");
        return arrayList;
    }

    public int getChannelCountByFilter(int svlId, int filter) {
        Log.d(TAG, "Enter getChannelCountByFilter(" + svlId + "," + filter + ")\n");
        int ret = TVNativeWrapper.getChannelCountByFilter_native(svlId, filter);
        if (ret < 0) {
            return -1;
        }
        return ret;
    }

    public List<MtkTvChannelInfoBase> getChannelListByQueryInfo(int svlId, MtkTvChannelQuery info) {
        Log.d(TAG, "Enter getChannelListByQueryInfo(" + svlId + "," + info + ")\n");
        List<MtkTvChannelInfoBase> channelList = new ArrayList<>();
        synchronized (this) {
            int ret = TVNativeWrapper.getChannelListByQueryInfo_native(svlId, info, channelList, 0);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getChannelListByQueryInfo_native failed! return " + ret + ".\n");
            }
        }
        return channelList;
    }

    public List<MtkTvChannelInfoBase> getChannelListByQueryInfo(int svlId, MtkTvChannelQuery info, int count) {
        Log.d(TAG, "Enter getChannelListByQueryInfo(" + svlId + "," + info + "," + count + ")\n");
        List<MtkTvChannelInfoBase> channelList = new ArrayList<>();
        synchronized (this) {
            int ret = TVNativeWrapper.getChannelListByQueryInfo_native(svlId, info, channelList, count);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getChannelListByQueryInfo_native failed! return " + ret + ".\n");
            }
        }
        return channelList;
    }

    public int getChannelCountByQueryInfo(int svlId, MtkTvChannelQuery info) {
        Log.d(TAG, "Enter getChannelCountByQueryInfo(" + svlId + "," + info + ")\n");
        int ret = TVNativeWrapper.getChannelCountByQueryInfo_native(svlId, info);
        if (ret >= 0) {
            return ret;
        }
        Log.e(TAG, "TVNativeWrapper.getChannelCountByQueryInfo_native failed! return " + ret + ".\n");
        return -1;
    }

    public int getChannelCountByMask(int svlId, int mask, int value) {
        Log.d(TAG, "Enter getChannelCountByMask(" + svlId + "," + mask + "," + value + ")\n");
        int ret = TVNativeWrapper.getChannelCountByMask_native(svlId, mask, value, 0);
        if (ret < 0) {
            return -1;
        }
        return ret;
    }

    public int getChannelCountByMaskAndSat(int svlId, int mask, int value, int satRecId) {
        Log.d(TAG, "Enter getChannelCountByMaskAndSat(" + svlId + "," + mask + "," + value + "," + satRecId + ")\n");
        int ret = TVNativeWrapper.getChannelCountByMask_native(svlId, mask, value, satRecId);
        if (ret < 0) {
            return -1;
        }
        return ret;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0088, code lost:
        if (r9 == 1) goto L_0x0091;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x008b, code lost:
        if (r9 != 3) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x008d, code lost:
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0090, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0091, code lost:
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0092, code lost:
        r0 = new java.util.ArrayList<>();
        r3 = r1.size() - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009d, code lost:
        if (r3 < 0) goto L_0x00ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009f, code lost:
        r0.add(r1.get(r3));
        r3 = r3 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ab, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> getChannelListByMask(int r17, int r18, int r19, int r20, int r21, int r22) {
        /*
            r16 = this;
            r9 = r20
            r10 = 0
            java.lang.String r0 = "MtkTvChannelList"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Enter getChannelListByMask("
            r1.append(r2)
            r11 = r17
            r1.append(r11)
            java.lang.String r2 = ","
            r1.append(r2)
            r12 = r18
            r1.append(r12)
            java.lang.String r2 = ","
            r1.append(r2)
            r13 = r19
            r1.append(r13)
            java.lang.String r2 = ","
            r1.append(r2)
            r1.append(r9)
            java.lang.String r2 = ","
            r1.append(r2)
            r14 = r21
            r1.append(r14)
            java.lang.String r2 = ","
            r1.append(r2)
            r8 = r22
            r1.append(r8)
            java.lang.String r2 = ")\n"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            monitor-enter(r16)
            r4 = 0
            r1 = r11
            r2 = r12
            r3 = r13
            r5 = r9
            r6 = r14
            r15 = r7
            r7 = r8
            r8 = r15
            int r0 = com.mediatek.twoworlds.tv.TVNativeWrapper.getChannelListByMask_native(r1, r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00ac }
            r10 = r0
            if (r10 == 0) goto L_0x0086
            java.lang.String r0 = "MtkTvChannelList"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0083 }
            r1.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = "TVNativeWrapper.getChannelListByMask_native failed! return "
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            r1.append(r10)     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = ".\n"
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0083 }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x0083 }
            goto L_0x0086
        L_0x0083:
            r0 = move-exception
            r1 = r15
            goto L_0x00ae
        L_0x0086:
            monitor-exit(r16)     // Catch:{ all -> 0x00ac }
            r0 = 1
            if (r9 == r0) goto L_0x0091
            r0 = 3
            if (r9 != r0) goto L_0x008f
            r1 = r15
            goto L_0x0092
        L_0x008f:
            r1 = r15
            return r1
        L_0x0091:
            r1 = r15
        L_0x0092:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r2 = r1.size()
            int r3 = r2 + -1
        L_0x009d:
            if (r3 < 0) goto L_0x00ab
            java.lang.Object r4 = r1.get(r3)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r4
            r0.add(r4)
            int r3 = r3 + -1
            goto L_0x009d
        L_0x00ab:
            return r0
        L_0x00ac:
            r0 = move-exception
            r1 = r15
        L_0x00ae:
            monitor-exit(r16)     // Catch:{ all -> 0x00b0 }
            throw r0
        L_0x00b0:
            r0 = move-exception
            goto L_0x00ae
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.twoworlds.tv.MtkTvChannelListBase.getChannelListByMask(int, int, int, int, int, int):java.util.List");
    }

    public int slideForOnePartChannelId(int svlId, int chNumbegin, int chNumEnd) {
        Log.d(TAG, "Enter slideForOneChannelId(" + svlId + "," + chNumbegin + "," + chNumEnd + ")\n");
        return TVNativeWrapper.slideForOnePartChannelId_native(svlId, chNumbegin, chNumEnd);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0093, code lost:
        if (r9 == 1) goto L_0x009c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0096, code lost:
        if (r9 != 3) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0098, code lost:
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x009b, code lost:
        return r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x009c, code lost:
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x009d, code lost:
        r0 = new java.util.ArrayList<>();
        r3 = r1.size() - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a8, code lost:
        if (r3 < 0) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00aa, code lost:
        r0.add(r1.get(r3));
        r3 = r3 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00b6, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase> getChannelListByMaskAndSat(int r17, int r18, int r19, int r20, int r21, int r22, int r23) {
        /*
            r16 = this;
            r9 = r21
            r10 = 0
            java.lang.String r0 = "MtkTvChannelList"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Enter getChannelListByMaskAndSat("
            r1.append(r2)
            r11 = r17
            r1.append(r11)
            java.lang.String r2 = ","
            r1.append(r2)
            r12 = r18
            r1.append(r12)
            java.lang.String r2 = ","
            r1.append(r2)
            r13 = r19
            r1.append(r13)
            java.lang.String r2 = ","
            r1.append(r2)
            r14 = r20
            r1.append(r14)
            java.lang.String r2 = ","
            r1.append(r2)
            r1.append(r9)
            java.lang.String r2 = ","
            r1.append(r2)
            r8 = r22
            r1.append(r8)
            java.lang.String r2 = ","
            r1.append(r2)
            r7 = r23
            r1.append(r7)
            java.lang.String r2 = ")\n"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            monitor-enter(r16)
            r1 = r11
            r2 = r12
            r3 = r13
            r4 = r14
            r5 = r9
            r15 = r6
            r6 = r8
            r7 = r23
            r8 = r15
            int r0 = com.mediatek.twoworlds.tv.TVNativeWrapper.getChannelListByMask_native(r1, r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00b7 }
            r10 = r0
            if (r10 == 0) goto L_0x0091
            java.lang.String r0 = "MtkTvChannelList"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x008e }
            r1.<init>()     // Catch:{ all -> 0x008e }
            java.lang.String r2 = "TVNativeWrapper.getChannelListByMask_native failed! return "
            r1.append(r2)     // Catch:{ all -> 0x008e }
            r1.append(r10)     // Catch:{ all -> 0x008e }
            java.lang.String r2 = ".\n"
            r1.append(r2)     // Catch:{ all -> 0x008e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x008e }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x008e }
            goto L_0x0091
        L_0x008e:
            r0 = move-exception
            r1 = r15
            goto L_0x00b9
        L_0x0091:
            monitor-exit(r16)     // Catch:{ all -> 0x00b7 }
            r0 = 1
            if (r9 == r0) goto L_0x009c
            r0 = 3
            if (r9 != r0) goto L_0x009a
            r1 = r15
            goto L_0x009d
        L_0x009a:
            r1 = r15
            return r1
        L_0x009c:
            r1 = r15
        L_0x009d:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r2 = r1.size()
            int r3 = r2 + -1
        L_0x00a8:
            if (r3 < 0) goto L_0x00b6
            java.lang.Object r4 = r1.get(r3)
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r4 = (com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r4
            r0.add(r4)
            int r3 = r3 + -1
            goto L_0x00a8
        L_0x00b6:
            return r0
        L_0x00b7:
            r0 = move-exception
            r1 = r15
        L_0x00b9:
            monitor-exit(r16)     // Catch:{ all -> 0x00bb }
            throw r0
        L_0x00bb:
            r0 = move-exception
            goto L_0x00b9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.twoworlds.tv.MtkTvChannelListBase.getChannelListByMaskAndSat(int, int, int, int, int, int, int):java.util.List");
    }

    public int setChannelList(int channelOperator, List<MtkTvChannelInfoBase> channelsToSet) {
        Log.d(TAG, "Enter setChannelList:\n");
        if (channelsToSet == null) {
            Log.e(TAG, "The channelsToSet is null!\n");
            return -1;
        }
        int channelNumToSet = channelsToSet.size();
        if (channelNumToSet == 0) {
            Log.e(TAG, "There is no any channel in 'channelsToSet'!\n");
            return -1;
        } else if (channelOperator > 2 || channelOperator < 0) {
            Log.e(TAG, "Invalid channelOperator " + channelOperator + "!\n");
            return -1;
        } else {
            MtkTvChannelInfoBase channelInfo = channelsToSet.get(0);
            Log.d(TAG, "" + channelNumToSet + " channels will be " + channelOperator + " in 'channelList'!\n");
            StringBuilder sb = new StringBuilder();
            sb.append("svlId = ");
            sb.append(channelInfo.getSvlId());
            sb.append("...\n");
            Log.d(TAG, sb.toString());
            if (TVNativeWrapper.setChannelList_native(channelOperator, channelInfo.getSvlId(), channelsToSet) == 0) {
                return 0;
            }
            return -1;
        }
    }

    public static int cleanChannelList(int svlID) {
        return TVNativeWrapper.cleanChannelList_native(svlID, true);
    }

    public static int cleanChannelList(int svlID, boolean clearLOL) {
        return TVNativeWrapper.cleanChannelList_native(svlID, clearLOL);
    }

    public static int createSnapshot(int svlID) {
        return TVNativeWrapper.createSnapshot_native(svlID);
    }

    public static int restoreSnapshot(int snapshotID) {
        return TVNativeWrapper.restoreSnapshot_native(snapshotID);
    }

    public static int freeSnapshot(int snapshotID) {
        return TVNativeWrapper.freeSnapshot_native(snapshotID);
    }

    public List<MtkTvFavoritelistInfoBase> getFavoritelistByFilter() {
        Log.d(TAG, "+ getFavoritelistByFilter.");
        List<MtkTvFavoritelistInfoBase> favoriteList = new ArrayList<>();
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "getFavoritelistByFilter_native begin");
                ret = TVNativeWrapper.getFavoritelistByFilter_native(favoriteList);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "getFavoritelistByFilter_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getFavoritelistByFilter: count=" + favoriteList.size());
        for (int i = 0; i < favoriteList.size(); i++) {
            Log.d(TAG, " getFavoritelistByFilter: svlId=" + favoriteList.get(i).getSvlId() + ",svlRecId=" + favoriteList.get(i).getSvlRecId() + ",ChannelId=" + favoriteList.get(i).getChannelId() + ",ChannelName=" + favoriteList.get(i).getChannelName());
        }
        Log.d(TAG, "- getFavoritelistByFilter. ret=" + ret);
        return favoriteList;
    }

    public int addFavoritelistChannel() {
        Log.d(TAG, "+ addFavoritelistChannel.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "addFavoritelistChannel_native begin");
                ret = TVNativeWrapper.addFavoritelistChannel_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "addFavoritelistChannel_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- addFavoritelistChannel. ret=" + ret);
        return ret;
    }

    public int addFavoritelistChannelByIndex(int index) {
        Log.d(TAG, "+ addFavoritelistChannelByIndex: index=" + index);
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "addFavoritelistChannelByIndex_native begin");
                ret = TVNativeWrapper.addFavoritelistChannelByIndex_native(index);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "addFavoritelistChannelByIndex_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- addFavoritelistChannelByIndex. ret=" + ret);
        return ret;
    }

    public int removeFavoritelistChannelByIndexWithoutShowFavIcon(int index) {
        Log.d(TAG, "+ removeFavoritelistChannelByIndexWithoutShowFavIcon. index=" + index);
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "removeFavoritelistChannelByIndexWithoutShowFavIcon_native begin");
                ret = TVNativeWrapper.removeFavoritelistChannelByIndexWithoutShowFavIcon_native(index);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "removeFavoritelistChannelByIndexWithoutShowFavIcon_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- removeFavoritelistChannelByIndexWithoutShowFavIcon. ret=" + ret);
        return ret;
    }

    public int removeFavoritelistChannel(int index) {
        Log.d(TAG, "+ removeFavoritelistChannel. index=" + index);
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "removeFavoritelistChannel_native begin");
                ret = TVNativeWrapper.removeFavoritelistChannel_native(index);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "removeFavoritelistChannel_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- removeFavoritelistChannel. ret=" + ret);
        return ret;
    }

    public int storeFavoritelistChannel() {
        Log.d(TAG, "+ storeFavoritelistChannel.");
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "storeFavoritelistChannel_native begin");
                ret = TVNativeWrapper.storeFavoritelistChannel_native();
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "storeFavoritelistChannel_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- storeFavoritelistChannel. ret=" + ret);
        return ret;
    }

    public int swapFavoritelistByIndex(int index1, int index2) {
        Log.d(TAG, "+ swapFavoritelistByIndex. index1=" + index1 + ", index2=" + index2);
        int ret = 0;
        try {
            synchronized (this) {
                Log.d(TAG, "swapFavoritelistByIndex_native begin");
                ret = TVNativeWrapper.swapFavoritelistByIndex_native(index1, index2);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "swapFavoritelistByIndex_native fail");
                }
            }
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
        }
        Log.d(TAG, "- swapFavoritelistByIndex. ret=" + ret);
        return ret;
    }

    public List<TvProviderChannelInfoBase> getTvproviderBySvlId(int svlId) {
        Log.d(TAG, "Enter getTvproviderBySvlId \n");
        List<TvProviderChannelInfoBase> channelList = new ArrayList<>();
        synchronized (this) {
            int ret = TVNativeWrapper.getTvproviderBySvlId_native(svlId, channelList);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getTvproviderBySvlId_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave getTvproviderBySvlId() \n");
        return channelList;
    }

    public List<TvProviderChannelInfoBase> getAllTvprovider() {
        Log.d(TAG, "Enter getAllTvprovider\n");
        List<TvProviderChannelInfoBase> channelList = new ArrayList<>();
        synchronized (this) {
            int ret = TVNativeWrapper.getAllTvprovider_native(channelList);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getAllTvprovider_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave getAllTvprovider() \n");
        return channelList;
    }

    public TvProviderChannelInfoBase getTvproviderBySvlRecId(int svlId, int svlRecId) {
        Log.d(TAG, "Enter getTvproviderBySvlRecId\n");
        TvProviderChannelInfoBase record = new TvProviderChannelInfoBase();
        synchronized (this) {
            int ret = TVNativeWrapper.getTvproviderBySvlRecId_native(svlId, svlRecId, record);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getTvproviderBySvlRecId_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave getTvproviderBySvlRecId() \n");
        return record;
    }

    public MtkTvChannelInfoBase getChannelInfoBySvlRecId(int svlId, int svlRecId) {
        MtkTvChannelInfoBase record;
        Log.d(TAG, "Enter getChannelInfoBySvlRecId_native\n");
        new MtkTvChannelInfoBase();
        synchronized (this) {
            record = TVNativeWrapper.getChannelInfoBySvlRecId_native(svlId, svlRecId);
        }
        Log.d(TAG, "leave getChannelInfoBySvlRecId_native() \n");
        return record;
    }

    public int deleteChannelByBrdcstType(int svlId, int brdcstType) {
        Log.d(TAG, "Enter deleteChannelByBrdcstType(svlId:%d, brdcstType:%d).\n");
        return TVNativeWrapper.deleteChannelByBrdcstType_native(svlId, brdcstType, false);
    }

    public int deleteChannelByBrdcstType(int svlId, int brdcstType, boolean clearLOL) {
        Log.d(TAG, "Enter deleteChannelByBrdcstType(svlId:%d, brdcstType:%d).\n");
        return TVNativeWrapper.deleteChannelByBrdcstType_native(svlId, brdcstType, clearLOL);
    }

    public boolean isChannelNumberExsit(int svlId, int ch_num) {
        Log.d(TAG, "Enter isChannelNumberExsit(svlId:%d, ch_num:%d).\n");
        return TVNativeWrapper.isChannelNumberExsit_native(svlId, ch_num);
    }

    public List<TvProviderChannelInfoBase> getTvproviderOcl() {
        Log.d(TAG, "Enter getTvproviderOcl \n");
        List<TvProviderChannelInfoBase> channelList = new ArrayList<>();
        synchronized (this) {
            int ret = TVNativeWrapper.getTvproviderOcl_native(channelList);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.getTvproviderOcl_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave getTvproviderOcl() \n");
        return channelList;
    }

    public int oneChannelListArrange(int svlIdT, int svlIdC) {
        int ret;
        Log.d(TAG, "Enter oneChannelListArrange, svlIdT=" + svlIdT + ", svlIdC=" + svlIdC);
        synchronized (this) {
            ret = TVNativeWrapper.oneChannelListArrange_native(svlIdT, svlIdC);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.oneChannelListArrange_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave oneChannelListArrange() \n");
        return ret;
    }

    public int getChannelListMode() {
        int ret;
        Log.d(TAG, "Enter getChannelListMode. \n");
        synchronized (this) {
            ret = TVNativeWrapper.getChannelListMode_native();
            if (ret < 0) {
                Log.e(TAG, "TVNativeWrapper.getChannelListMode_native failed! return " + ret + ".\n");
                ret = -1;
            }
        }
        Log.d(TAG, "leave getChannelListMode() \n");
        return ret;
    }

    public int getChannelPumpVer(int svlId) {
        int ret;
        Log.d(TAG, "Enter getChannelPumpVer. svlId = " + svlId);
        synchronized (this) {
            ret = TVNativeWrapper.getChannelPumpVer_native(svlId);
            if (ret < 0) {
                Log.e(TAG, "TVNativeWrapper.getChannelPumpVer_native failed! return " + ret + ".\n");
                ret = -1;
            }
        }
        Log.d(TAG, "leave getChannelPumpVer() svlId = " + svlId + ", pumpVersion = " + ret);
        return ret;
    }

    public int setChannelPumpVer(int svlId, int pumpVer) {
        int ret;
        Log.d(TAG, "Enter setChannelPumpVer. svlId = " + svlId + ",pumpVer = " + pumpVer);
        synchronized (this) {
            ret = TVNativeWrapper.setChannelPumpVer_native(svlId, pumpVer);
            if (ret < 0) {
                Log.e(TAG, "TVNativeWrapper.setChannelPumpVer_native failed! return " + ret + ".\n");
                ret = -1;
            }
        }
        Log.d(TAG, "leave setChannelPumpVer(). svlId = " + svlId + ",pumpVer = " + pumpVer);
        return ret;
    }

    public int oneChannelListAddToMax(int svlId, int svlRecId) {
        int ret;
        Log.d(TAG, "Enter oneChannelListAddToMax. svlId = " + svlId + ",svlRecId = " + svlRecId);
        synchronized (this) {
            ret = TVNativeWrapper.oneChannelListAddToMax_native(svlId, svlRecId);
            if (ret < 0) {
                Log.e(TAG, "TVNativeWrapper.oneChannelListAddToMax_native failed! return " + ret + ".\n");
                ret = -1;
            }
        }
        Log.d(TAG, "leave oneChannelListAddToMax(). svlId = " + svlId + ",svlRecId = " + svlRecId);
        return ret;
    }

    public int swapChannelInfo(int channelIdA, int channelIdB, int svlId) {
        int ret;
        Log.d(TAG, "Enter getTvproviderOcl \n");
        synchronized (this) {
            ret = TVNativeWrapper.swapChannelInfo_native(channelIdA, channelIdB, svlId);
            if (ret != 0) {
                Log.e(TAG, "TVNativeWrapper.swapChannelInfo_native failed! return " + ret + ".\n");
            }
        }
        Log.d(TAG, "leave swapChannelInfo() \n");
        return ret;
    }
}
