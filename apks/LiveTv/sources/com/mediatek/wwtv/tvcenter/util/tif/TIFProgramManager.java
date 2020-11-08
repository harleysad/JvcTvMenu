package com.mediatek.wwtv.tvcenter.util.tif;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.media.tv.TvContractCompat;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TIFProgramManager {
    private static final String TAG = "TIFProgramManager";
    private static TIFProgramManager mTIFProgramManagerInstance;
    private ContentResolver mContentResolver = this.mContext.getContentResolver();
    private Context mContext;
    private MtkTvEvent mMtkTvEvent = MtkTvEvent.getInstance();

    private TIFProgramManager(Context context) {
        this.mContext = context;
    }

    public static synchronized TIFProgramManager getInstance(Context context) {
        TIFProgramManager tIFProgramManager;
        synchronized (TIFProgramManager.class) {
            if (mTIFProgramManagerInstance == null) {
                mTIFProgramManagerInstance = new TIFProgramManager(context);
            }
            tIFProgramManager = mTIFProgramManagerInstance;
        }
        return tIFProgramManager;
    }

    public Map<Long, List<TIFProgramInfo>> queryProgramListWithGroup() {
        Map<Long, List<TIFProgramInfo>> channelProgramMap = new HashMap<>();
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, (String) null, (String[]) null, "channel_id,start_time_utc_millis");
        if (c == null) {
            return channelProgramMap;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, false);
            MtkLog.d(TAG, "tempProgramInfo>>" + tempProgramInfo.mChannelId + "  " + tempProgramInfo.mTitle + "  " + tempProgramInfo.mStartTimeUtcSec + "  " + tempProgramInfo.mEndTimeUtcSec + "   " + tempProgramInfo.mEventId);
            if (channelProgramMap.containsKey(Long.valueOf(tempProgramInfo.mChannelId))) {
                channelProgramMap.get(Long.valueOf(tempProgramInfo.mChannelId)).add(tempProgramInfo);
            } else {
                List<TIFProgramInfo> tempProgramList = new ArrayList<>();
                tempProgramList.add(tempProgramInfo);
                channelProgramMap.put(Long.valueOf(tempProgramInfo.mChannelId), tempProgramList);
            }
        }
        c.close();
        MtkLog.d(TAG, "queryProgramListWithGroup channelProgramMap:" + channelProgramMap.size());
        return channelProgramMap;
    }

    public List<TIFProgramInfo> queryProgramListWithGroupFor3rd(int channelId) {
        TIFProgramManager tIFProgramManager = this;
        int i = channelId;
        List<TIFProgramInfo> tempProgramList = new ArrayList<>();
        MtkLog.d(TAG, "queryProgramListWithGroupFor3rd channelId == " + i);
        TIFChannelInfo tIFChannelInfo = TIFChannelManager.getInstance(tIFProgramManager.mContext).getTIFChannelInfoById(i);
        if (tIFChannelInfo == null) {
            MtkLog.e(TAG, "tIFChannelInfo==null");
            return tempProgramList;
        }
        EPGChannelInfo ePGChannelInfo = new EPGChannelInfo(tIFChannelInfo);
        long startTime = EPGUtil.getCurrentTime() * 1000;
        long endTime = 7200000 + startTime;
        MtkLog.d(TAG, "startTime>> " + startTime + " endTime > " + endTime);
        MtkLog.d(TAG, "ePGChannelInfo>>" + ePGChannelInfo.mId);
        boolean z = false;
        Cursor c = tIFProgramManager.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, "channel_id = " + ("" + ePGChannelInfo.mId) + " and " + "end_time_utc_millis" + " > ? and " + "start_time_utc_millis" + " < ?", new String[]{String.valueOf(startTime), String.valueOf(endTime)}, "channel_id,start_time_utc_millis");
        if (c == null) {
            return tempProgramList;
        }
        MtkLog.d(TAG, "ePGChannelInfo Cursor c.size   " + c.getCount());
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            tIFProgramManager.parserTIFPrograminfo(tempProgramInfo, c, z);
            MtkLog.d(TAG, "queryProgramListWithGroupFor3rd tempProgramInfo>>" + tempProgramInfo.mChannelId + "  " + tempProgramInfo.mTitle + "  " + tempProgramInfo.mStartTimeUtcSec + "  " + tempProgramInfo.mEndTimeUtcSec + "   " + tempProgramInfo.mEventId);
            tempProgramList.add(tempProgramInfo);
            tIFProgramManager = this;
            int i2 = channelId;
            z = false;
        }
        c.close();
        MtkLog.d(TAG, " queryProgramListWithGroupFor3rd tempProgramList:" + tempProgramList.size());
        return tempProgramList;
    }

    public List<EPGProgramInfo> queryProgramByChannelId(int mtkChId, long chId, long startTime, long endTime) {
        long j = chId;
        List<EPGProgramInfo> programList = new ArrayList<>();
        boolean z = false;
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, "channel_id=" + j + " and " + "end_time_utc_millis" + ">= ? and " + "start_time_utc_millis" + " <= ?", new String[]{String.valueOf(startTime), String.valueOf(endTime)}, "channel_id,start_time_utc_millis");
        if (c == null) {
            MtkLog.e(TAG, " queryProgramByChannelId c == null!");
            return programList;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, z);
            MtkLog.d(TAG, " queryProgramByChannelId tempProgramInfo =" + tempProgramInfo.toString());
            if (TIFFunctionUtil.containsEvent(programList, tempProgramInfo)) {
                int i = mtkChId;
            } else {
                int i2 = tempProgramInfo.mEventId;
                long j2 = tempProgramInfo.mStartTimeUtcSec;
                long j3 = j2;
                EPGProgramInfo tempEPGProgramInfo = new EPGProgramInfo((int) j, i2, j3, tempProgramInfo.mEndTimeUtcSec, tempProgramInfo.mTitle, tempProgramInfo.mRating);
                tempEPGProgramInfo.setDescribe(tempProgramInfo.getmDescription());
                tempEPGProgramInfo.setLongDescription(tempProgramInfo.mLongDescription);
                tempEPGProgramInfo.setmStartTimeStr(EPGTimeConvert.converTimeByLong2Str(tempEPGProgramInfo.getmStartTime().longValue()));
                tempEPGProgramInfo.setmEndTimeStr(EPGTimeConvert.converTimeByLong2Str(tempEPGProgramInfo.getmEndTime().longValue()));
                regetEPGProgramInfo(tempEPGProgramInfo, mtkChId);
                programList.add(tempEPGProgramInfo);
            }
            j = chId;
            z = false;
        }
        int i3 = mtkChId;
        c.close();
        return programList;
    }

    private void regetEPGProgramInfo(EPGProgramInfo tempEPGProgramInfo, int mtkChId) {
        MtkTvEventInfoBase apiEPGInfo = MtkTvEvent.getInstance().getEventInfoByEventId(tempEPGProgramInfo.getProgramId(), mtkChId);
        if (apiEPGInfo != null && apiEPGInfo.getStartTime() > 0) {
            DataReader.getInstance().setMStype(tempEPGProgramInfo, apiEPGInfo.getEventCategory());
            tempEPGProgramInfo.setCategoryType(apiEPGInfo.getEventCategory());
            tempEPGProgramInfo.setHasSubTitle(apiEPGInfo.isCaption());
            tempEPGProgramInfo.setRatingType(apiEPGInfo.getEventRating());
            tempEPGProgramInfo.setRatingValue(apiEPGInfo.getEventRatingType());
            tempEPGProgramInfo.setAppendDescription(CommonIntegration.getInstance().getAvailableString(DataReader.getInstance().getResultDetail(apiEPGInfo.getGuidanceText(), tempEPGProgramInfo.getDescribe(), tempEPGProgramInfo.getLongDescription())));
        }
    }

    public Map<Long, List<TIFProgramInfo>> queryProgramListWithGroupByChannelId(List<EPGChannelInfo> channels, long startTime) {
        List<EPGChannelInfo> ciChannels = new ArrayList<>();
        ArrayList<EPGChannelInfo> arrayList = new ArrayList<>();
        for (EPGChannelInfo tempItem : channels) {
            if (tempItem.isCiVirturalCh()) {
                ciChannels.add(tempItem);
            } else {
                arrayList.add(tempItem);
            }
        }
        long startTime2 = 1000 * startTime;
        long endTime = 7200000 + startTime2;
        Map<Long, List<TIFProgramInfo>> channelProgramMap = new HashMap<>();
        String tempStr = "(";
        for (EPGChannelInfo tempInfo : arrayList) {
            if (!tempStr.equals("(")) {
                tempStr = tempStr + ",";
            }
            tempStr = tempStr + String.valueOf(tempInfo.mId);
            MtkLog.d(TAG, "tempProgramInfo>>" + tempInfo.mId);
        }
        ArrayList arrayList2 = arrayList;
        boolean z = false;
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, "channel_id in " + (tempStr + ")") + " and " + "end_time_utc_millis" + " > ? and " + "start_time_utc_millis" + " < ?", new String[]{String.valueOf(startTime2), String.valueOf(endTime)}, "channel_id,start_time_utc_millis");
        if (c == null) {
            return channelProgramMap;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, z);
            MtkLog.d(TAG, "tempProgramInfo normalChannels>>" + tempProgramInfo.toString());
            long startTime3 = startTime2;
            if (channelProgramMap.containsKey(Long.valueOf(tempProgramInfo.mChannelId))) {
                channelProgramMap.get(Long.valueOf(tempProgramInfo.mChannelId)).add(tempProgramInfo);
            } else {
                List<TIFProgramInfo> tempProgramList = new ArrayList<>();
                tempProgramList.add(tempProgramInfo);
                channelProgramMap.put(Long.valueOf(tempProgramInfo.mChannelId), tempProgramList);
            }
            startTime2 = startTime3;
            z = false;
        }
        c.close();
        MtkLog.d(TAG, "queryProgramListWithGroup channelProgramMap:" + channelProgramMap.size());
        MtkLog.d(TAG, "queryProgramListWithGroup ciChannels:" + ciChannels.size());
        if (ciChannels.size() > 0) {
            String ciTmpStr = "(";
            for (EPGChannelInfo tempInfo2 : ciChannels) {
                if (!ciTmpStr.equals("(")) {
                    ciTmpStr = ciTmpStr + ",";
                }
                ciTmpStr = ciTmpStr + String.valueOf(tempInfo2.mId);
                MtkLog.d(TAG, "tempProgramInfo>>" + tempInfo2.mId);
            }
            ContentResolver contentResolver = this.mContentResolver;
            Uri uri = TvContract.Programs.CONTENT_URI;
            Cursor c2 = contentResolver.query(uri, (String[]) null, "channel_id in " + (ciTmpStr + ")"), (String[]) null, "channel_id");
            if (c2 == null) {
                return channelProgramMap;
            }
            TIFProgramInfo tempProgramInfo2 = null;
            while (true) {
                TIFProgramInfo tIFProgramInfo = tempProgramInfo2;
                if (!c2.moveToNext()) {
                    break;
                }
                TIFProgramInfo tempProgramInfo3 = new TIFProgramInfo();
                parserTIFPrograminfo(tempProgramInfo3, c2, true);
                MtkLog.d(TAG, "tempProgramInfo ciChannels>>" + tempProgramInfo3.toString());
                if (channelProgramMap.containsKey(Long.valueOf(tempProgramInfo3.mChannelId))) {
                    channelProgramMap.get(Long.valueOf(tempProgramInfo3.mChannelId)).add(tempProgramInfo3);
                } else {
                    List<TIFProgramInfo> tempProgramList2 = new ArrayList<>();
                    tempProgramList2.add(tempProgramInfo3);
                    channelProgramMap.put(Long.valueOf(tempProgramInfo3.mChannelId), tempProgramList2);
                }
                tempProgramInfo2 = tempProgramInfo3;
            }
            c2.close();
        }
        MtkLog.d(TAG, "queryProgramListWithGroup channelProgramMap:" + channelProgramMap.size());
        return channelProgramMap;
    }

    public Map<Long, List<TIFProgramInfo>> queryProgramListWithGroupCondition(String[] projection, String selection, String[] selectionArgs, String order) {
        Map<Long, List<TIFProgramInfo>> channelProgramMap = new HashMap<>();
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, projection, selection, selectionArgs, order);
        if (c == null) {
            return channelProgramMap;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, false);
            if (channelProgramMap.containsKey(Long.valueOf(tempProgramInfo.mChannelId))) {
                channelProgramMap.get(Long.valueOf(tempProgramInfo.mChannelId)).add(tempProgramInfo);
            } else {
                List<TIFProgramInfo> tempProgramList = new ArrayList<>();
                tempProgramList.add(tempProgramInfo);
                channelProgramMap.put(Long.valueOf(tempProgramInfo.mChannelId), tempProgramList);
            }
        }
        c.close();
        MtkLog.d(TAG, "queryProgramListWithGroupCondition channelProgramMap:" + channelProgramMap.size());
        return channelProgramMap;
    }

    public List<TIFProgramInfo> queryProgramListAll() {
        List<TIFProgramInfo> tempProgramList = new ArrayList<>();
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, (String) null, (String[]) null, "channel_id,start_time_utc_millis");
        if (c == null) {
            return tempProgramList;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, false);
            tempProgramList.add(tempProgramInfo);
        }
        c.close();
        MtkLog.d(TAG, "queryProgramListAll tempProgramList:" + tempProgramList.size());
        return tempProgramList;
    }

    public long queryCurrentProgram() {
        long programsId = -1;
        TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
        if (tifChannelInfo == null) {
            return -1;
        }
        long current = System.currentTimeMillis();
        MtkLog.d(TAG, "current = " + current);
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, (String[]) null, "channel_id=" + tifChannelInfo.mId + " and " + current + " >= " + "start_time_utc_millis" + " and " + current + " < " + "end_time_utc_millis", (String[]) null, (String) null);
        if (c == null) {
            return -1;
        }
        while (c.moveToNext()) {
            programsId = parserTIFProgramId(c);
        }
        c.close();
        MtkLog.d(TAG, "queryProgramId programsId:" + programsId);
        return programsId;
    }

    public List<TIFProgramInfo> getTIFProgramListByWhereCondition(String[] projection, String selection, String[] selectionArgs, String order) {
        List<TIFProgramInfo> tempProgramList = new ArrayList<>();
        Cursor c = this.mContentResolver.query(TvContract.Programs.CONTENT_URI, projection, selection, selectionArgs, order);
        if (c == null) {
            return tempProgramList;
        }
        while (c.moveToNext()) {
            TIFProgramInfo tempProgramInfo = new TIFProgramInfo();
            parserTIFPrograminfo(tempProgramInfo, c, false);
            tempProgramList.add(tempProgramInfo);
        }
        c.close();
        MtkLog.d(TAG, "getTIFProgramListByWhereCondition tempProgramList:" + tempProgramList.size());
        return tempProgramList;
    }

    private void parserTIFPrograminfo(TIFProgramInfo tempProgramInfo, Cursor c, boolean isVirCh) {
        tempProgramInfo.mChannelId = c.getLong(c.getColumnIndex("channel_id"));
        tempProgramInfo.mTitle = c.getString(c.getColumnIndex("title"));
        tempProgramInfo.mSeasonNumber = c.getInt(c.getColumnIndex(TvContractCompat.Programs.COLUMN_SEASON_NUMBER));
        tempProgramInfo.mEpisodeNumber = c.getInt(c.getColumnIndex(TvContractCompat.Programs.COLUMN_EPISODE_NUMBER));
        tempProgramInfo.mEpisodeTitle = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_EPISODE_TITLE));
        tempProgramInfo.mStartTimeUtcSec = c.getLong(c.getColumnIndex("start_time_utc_millis"));
        tempProgramInfo.mStartTimeUtcSec /= 1000;
        tempProgramInfo.mEndTimeUtcSec = c.getLong(c.getColumnIndex("end_time_utc_millis"));
        tempProgramInfo.mEndTimeUtcSec /= 1000;
        if (isVirCh) {
            tempProgramInfo.mStartTimeUtcSec = EPGUtil.getCurrentTime() / 1000;
            tempProgramInfo.mEndTimeUtcSec = (EPGUtil.getCurrentTime() / 1000) + 691200;
        }
        tempProgramInfo.mBroadcastGenre = c.getString(c.getColumnIndex("broadcast_genre"));
        tempProgramInfo.mCanonicalGenre = c.getString(c.getColumnIndex("canonical_genre"));
        tempProgramInfo.mDescription = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_SHORT_DESCRIPTION));
        tempProgramInfo.mLongDescription = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_LONG_DESCRIPTION));
        tempProgramInfo.mVideoWidth = c.getInt(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_VIDEO_WIDTH));
        tempProgramInfo.mVideoHeight = c.getInt(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_VIDEO_HEIGHT));
        tempProgramInfo.mAudioLanguage = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_AUDIO_LANGUAGE));
        tempProgramInfo.mRating = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_CONTENT_RATING));
        tempProgramInfo.mPosterArtUri = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_POSTER_ART_URI));
        tempProgramInfo.mThumbnailUri = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_THUMBNAIL_URI));
        try {
            tempProgramInfo.mData = c.getString(c.getColumnIndex("internal_provider_data"));
            parserTIFProgramData(tempProgramInfo, tempProgramInfo.mData);
        } catch (Exception e) {
        }
    }

    private long parserTIFProgramId(Cursor c) {
        return c.getLong(c.getColumnIndex("_id"));
    }

    private int[] parserTIFProgramData(TIFProgramInfo tempProgramInfo, String data) {
        int[] v = new int[3];
        if (data == null) {
            return v;
        }
        String[] value = data.split(",");
        if (value.length < 3) {
            return v;
        }
        tempProgramInfo.mEventId = Integer.parseInt(value[1]);
        return v;
    }

    public MtkTvEventInfoBase getApiEventInfoById(int channelId, int eventId) {
        return this.mMtkTvEvent.getEventInfoByEventId(eventId, channelId);
    }
}
