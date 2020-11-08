package com.android.tv.common;

import android.media.tv.TvContentRating;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.tv.common.memory.MemoryManageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class TvContentRatingCache implements MemoryManageable {
    private static final TvContentRatingCache INSTANCE = new TvContentRatingCache();
    private static final String TAG = "TvContentRatings";
    private final Map<String, TvContentRating[]> mRatingsMultiMap = new ArrayMap();

    public static TvContentRatingCache getInstance() {
        return INSTANCE;
    }

    @Nullable
    public TvContentRating[] getRatings(String commaSeparatedRatings) {
        TvContentRating[] tvContentRatings;
        if (TextUtils.isEmpty(commaSeparatedRatings)) {
            return null;
        }
        if (this.mRatingsMultiMap.containsKey(commaSeparatedRatings)) {
            return this.mRatingsMultiMap.get(commaSeparatedRatings);
        }
        String normalizedRatings = TextUtils.join(",", getSortedSetFromCsv(commaSeparatedRatings));
        if (this.mRatingsMultiMap.containsKey(normalizedRatings)) {
            tvContentRatings = this.mRatingsMultiMap.get(normalizedRatings);
        } else {
            tvContentRatings = stringToContentRatings(commaSeparatedRatings);
            this.mRatingsMultiMap.put(normalizedRatings, tvContentRatings);
        }
        if (!normalizedRatings.equals(commaSeparatedRatings)) {
            this.mRatingsMultiMap.put(commaSeparatedRatings, tvContentRatings);
        }
        return tvContentRatings;
    }

    @VisibleForTesting
    static TvContentRating[] stringToContentRatings(String commaSeparatedRatings) {
        if (TextUtils.isEmpty(commaSeparatedRatings)) {
            return null;
        }
        Set<String> ratingStrings = getSortedSetFromCsv(commaSeparatedRatings);
        List<TvContentRating> contentRatings = new ArrayList<>();
        for (String rating : ratingStrings) {
            try {
                contentRatings.add(TvContentRating.unflattenFromString(rating));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Can't parse the content rating: '" + rating + "'", e);
            }
        }
        if (contentRatings.size() == 0) {
            return null;
        }
        return (TvContentRating[]) contentRatings.toArray(new TvContentRating[contentRatings.size()]);
    }

    private static Set<String> getSortedSetFromCsv(String commaSeparatedRatings) {
        return toSortedSet(commaSeparatedRatings.split("\\s*,\\s*"));
    }

    private static Set<String> toSortedSet(String[] ratingStrings) {
        if (ratingStrings.length == 0) {
            return Collections.EMPTY_SET;
        }
        if (ratingStrings.length == 1) {
            return Collections.singleton(ratingStrings[0]);
        }
        SortedSet<String> set = new TreeSet<>();
        Collections.addAll(set, ratingStrings);
        return set;
    }

    public static String contentRatingsToString(TvContentRating[] contentRatings) {
        if (contentRatings == null || contentRatings.length == 0) {
            return null;
        }
        String[] ratingStrings = new String[contentRatings.length];
        for (int i = 0; i < contentRatings.length; i++) {
            ratingStrings[i] = contentRatings[i].flattenToString();
        }
        if (ratingStrings.length == 1) {
            return ratingStrings[0];
        }
        return TextUtils.join(",", toSortedSet(ratingStrings));
    }

    public void performTrimMemory(int level) {
        this.mRatingsMultiMap.clear();
    }

    private TvContentRatingCache() {
    }
}
