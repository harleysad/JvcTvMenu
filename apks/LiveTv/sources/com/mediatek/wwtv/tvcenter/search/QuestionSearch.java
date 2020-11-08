package com.mediatek.wwtv.tvcenter.search;

import android.content.Context;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.media.tv.TvContractCompat;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.search.LocalSearchProvider;
import java.util.ArrayList;
import java.util.List;

public final class QuestionSearch implements SearchInterface {
    private static final String TAG = "QuestionSearch";
    private static final String[] mQuestions = {"what's on tv", "what is on tv"};
    private final Context mContext;

    QuestionSearch(Context context) {
        this.mContext = context;
    }

    public List<LocalSearchProvider.SearchResult> search(String query, int limit, int action) {
        List<LocalSearchProvider.SearchResult> results = new ArrayList<>();
        Log.d(TAG, "query:" + query + ", " + limit + ", " + action);
        if (query == null) {
            return results;
        }
        for (String str : mQuestions) {
            if (query.toLowerCase().contains(str)) {
                return add(query, results);
            }
        }
        return results;
    }

    private List<LocalSearchProvider.SearchResult> add(String query, List<LocalSearchProvider.SearchResult> list) {
        LocalSearchProvider.SearchResult result = new LocalSearchProvider.SearchResult();
        result.channelId = 0;
        result.channelNumber = "1";
        result.title = this.mContext.getResources().getString(R.string.menu_tv_channels);
        result.description = this.mContext.getResources().getString(R.string.channels_item_program_guide);
        result.imageUri = Uri.parse("android.resource://com.mediatek.wwtv.tvcenter/drawable/icon").toString();
        result.intentAction = "android.intent.action.VIEW";
        result.intentData = TvContract.Programs.CONTENT_URI.toString();
        result.contentType = TvContractCompat.Programs.CONTENT_ITEM_TYPE;
        result.isLive = true;
        result.progressPercentage = -1;
        list.add(result);
        return list;
    }
}
