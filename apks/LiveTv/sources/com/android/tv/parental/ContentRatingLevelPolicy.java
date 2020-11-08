package com.android.tv.parental;

import android.media.tv.TvContentRating;
import com.android.tv.parental.ContentRatingSystem;
import java.util.HashSet;
import java.util.Set;

public class ContentRatingLevelPolicy {
    private static final int AGE_THRESHOLD_FOR_LEVEL_HIGH = 6;
    private static final int AGE_THRESHOLD_FOR_LEVEL_LOW = -1;
    private static final int AGE_THRESHOLD_FOR_LEVEL_MEDIUM = 12;

    private ContentRatingLevelPolicy() {
    }

    public static Set<TvContentRating> getRatingsForLevel(ParentalControlSettings settings, ContentRatingsManager manager, int level) {
        if (level == 0) {
            return getRatingForNone(settings);
        }
        if (level == 1) {
            return getRatingsForAge(settings, manager, 6);
        }
        if (level == 2) {
            return getRatingsForAge(settings, manager, 12);
        }
        if (level == 3) {
            return getRatingsForAge(settings, manager, -1);
        }
        throw new IllegalArgumentException("Unexpected rating level");
    }

    private static Set<TvContentRating> getRatingForNone(ParentalControlSettings settings) {
        return settings.getRatings();
    }

    private static Set<TvContentRating> getRatingsForAge(ParentalControlSettings settings, ContentRatingsManager manager, int age) {
        Set<TvContentRating> ratings = new HashSet<>();
        for (ContentRatingSystem contentRatingSystem : manager.getContentRatingSystems()) {
            if (settings.isContentRatingSystemEnabled(contentRatingSystem)) {
                int ageLimit = age;
                if (ageLimit == -1) {
                    ageLimit = getMaxAge(contentRatingSystem);
                }
                for (ContentRatingSystem.Rating rating : contentRatingSystem.getRatings()) {
                    if (rating.getAgeHint() >= ageLimit) {
                        ratings.add(TvContentRating.createRating(contentRatingSystem.getDomain(), contentRatingSystem.getName(), rating.getName(), new String[0]));
                        for (ContentRatingSystem.SubRating subRating : rating.getSubRatings()) {
                            ratings.add(TvContentRating.createRating(contentRatingSystem.getDomain(), contentRatingSystem.getName(), rating.getName(), new String[]{subRating.getName()}));
                        }
                    }
                }
            }
        }
        ParentalControlSettings parentalControlSettings = settings;
        return ratings;
    }

    private static int getMaxAge(ContentRatingSystem contentRatingSystem) {
        int maxAge = 0;
        for (ContentRatingSystem.Rating rating : contentRatingSystem.getRatings()) {
            if (maxAge < rating.getAgeHint()) {
                maxAge = rating.getAgeHint();
            }
        }
        return maxAge;
    }
}
