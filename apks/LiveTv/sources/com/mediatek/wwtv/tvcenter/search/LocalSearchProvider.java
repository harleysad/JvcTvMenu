package com.mediatek.wwtv.tvcenter.search;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalSearchProvider extends ContentProvider {
    private static final boolean DEBUG = true;
    static final int DEFAULT_SEARCH_ACTION = 1;
    private static final int DEFAULT_SEARCH_LIMIT = 10;
    private static final String EXPECTED_PATH_PREFIX = "/search_suggest_query";
    private static final String LIVE_CONTENTS = "1";
    private static final String NO_LIVE_CONTENTS = "0";
    public static final int PROGRESS_PERCENTAGE_HIDE = -1;
    private static final String[] SEARCHABLE_COLUMNS = {"suggest_text_1", "suggest_text_2", "suggest_result_card_image", "suggest_intent_action", "suggest_intent_data", "suggest_content_type", "suggest_is_live", "suggest_video_width", "suggest_video_height", "suggest_duration", SUGGEST_COLUMN_PROGRESS_BAR_PERCENTAGE};
    private static final String SUGGEST_COLUMN_PROGRESS_BAR_PERCENTAGE = "progress_bar_percentage";
    static final String SUGGEST_PARAMETER_ACTION = "action";
    private static final String TAG = "LocalSearchProvider";
    private List<SearchInterface> mSearch;

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query(" + uri + ", [" + Arrays.toString(projection) + ", " + selection + ", " + Arrays.toString(selectionArgs) + ", " + sortOrder + "])");
        this.mSearch = new ArrayList();
        this.mSearch.add(new TvProviderSearch(getContext()));
        this.mSearch.add(new QuestionSearch(getContext()));
        String query = uri.getLastPathSegment();
        int limit = 10;
        int action = 1;
        try {
            limit = Integer.parseInt(uri.getQueryParameter("limit"));
            action = Integer.parseInt(uri.getQueryParameter("action"));
        } catch (NumberFormatException | UnsupportedOperationException e) {
        }
        List<SearchResult> results = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (SearchInterface search : this.mSearch) {
                results.addAll(search.search(query, limit, action));
            }
        }
        return createSuggestionsCursor(results);
    }

    private Cursor createSuggestionsCursor(List<SearchResult> results) {
        MatrixCursor cursor = new MatrixCursor(SEARCHABLE_COLUMNS, results.size());
        List<String> row = new ArrayList<>(SEARCHABLE_COLUMNS.length);
        for (SearchResult result : results) {
            Log.d(TAG, "title:" + result.title + ", " + result.description + ", " + result.imageUri + ", " + result.intentAction + ", " + result.intentData);
            row.clear();
            row.add(result.title);
            row.add(result.description);
            row.add(result.imageUri);
            row.add(result.intentAction);
            row.add(result.intentData);
            row.add(result.contentType);
            row.add(result.isLive ? LIVE_CONTENTS : NO_LIVE_CONTENTS);
            String str = null;
            row.add(result.videoWidth == 0 ? null : String.valueOf(result.videoWidth));
            row.add(result.videoHeight == 0 ? null : String.valueOf(result.videoHeight));
            if (result.duration != 0) {
                str = String.valueOf(result.duration);
            }
            row.add(str);
            row.add(String.valueOf(result.progressPercentage));
            cursor.addRow(row);
        }
        return cursor;
    }

    public String getType(Uri uri) {
        if (!checkUriCorrect(uri)) {
            return null;
        }
        return "vnd.android.cursor.dir/vnd.android.search.suggest";
    }

    private static boolean checkUriCorrect(Uri uri) {
        return uri != null && uri.getPath().startsWith(EXPECTED_PATH_PREFIX);
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    public static class SearchResult {
        public long channelId;
        public String channelNumber;
        public String contentType;
        public String description;
        public long duration;
        public String imageUri;
        public String intentAction;
        public String intentData;
        public boolean isLive;
        public int progressPercentage;
        public String title;
        public int videoHeight;
        public int videoWidth;

        public String toString() {
            return "channelId: " + this.channelId + ", channelNumber: " + this.channelNumber + ", title: " + this.title;
        }
    }
}
