package com.android.tv.parental;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvContentRatingSystemInfo;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.android.tv.parental.ContentRatingSystem;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentRatingsManager {
    private static final String TAG = "ContentRatingsManager";
    private final List<ContentRatingSystem> mContentRatingSystems = new ArrayList();
    private final Context mContext;
    private final TvInputManagerHelper.TvInputManagerInterface mTvInputManager;

    public ContentRatingsManager(Context context, TvInputManagerHelper.TvInputManagerInterface tvInputManager) {
        this.mContext = context;
        this.mTvInputManager = tvInputManager;
    }

    public void update() {
        this.mContentRatingSystems.clear();
        ContentRatingsParser parser = new ContentRatingsParser(this.mContext);
        for (TvContentRatingSystemInfo info : this.mTvInputManager.getTvContentRatingSystemList()) {
            List<ContentRatingSystem> list = parser.parse(info);
            if (list != null) {
                this.mContentRatingSystems.addAll(list);
            }
        }
    }

    @Nullable
    public ContentRatingSystem getContentRatingSystemByCountry(String country) {
        MtkLog.d(TAG, "mContentRatingSystems=" + this.mContentRatingSystems + ",country=" + country);
        for (ContentRatingSystem s : this.mContentRatingSystems) {
            if (s.getCountries() != null && s.getCountries().contains(country)) {
                return s;
            }
        }
        return getContentRatingSystem("com.android.tv/DVB");
    }

    @Nullable
    public ContentRatingSystem getContentRatingSystem(String contentRatingSystemId) {
        MtkLog.d(TAG, "mContentRatingSystems=" + this.mContentRatingSystems + ",contentRatingSystemId=" + contentRatingSystemId);
        for (ContentRatingSystem ratingSystem : this.mContentRatingSystems) {
            MtkLog.d(TAG, "atingSystem.getId().getId()=" + ratingSystem.getId());
            if (TextUtils.equals(ratingSystem.getId(), contentRatingSystemId)) {
                return ratingSystem;
            }
        }
        return null;
    }

    public List<ContentRatingSystem> getContentRatingSystems() {
        return new ArrayList(this.mContentRatingSystems);
    }

    public String getDisplayNameForRating(TvContentRating canonicalRating) {
        if (TvContentRating.UNRATED.equals(canonicalRating)) {
            return this.mContext.getResources().getString(R.string.unrated_rating_name);
        }
        ContentRatingSystem.Rating rating = getRating(canonicalRating);
        if (rating == null) {
            return null;
        }
        List<ContentRatingSystem.SubRating> subRatings = getSubRatings(rating, canonicalRating);
        if (subRatings.isEmpty()) {
            return rating.getTitle();
        }
        StringBuilder builder = new StringBuilder();
        for (ContentRatingSystem.SubRating subRating : subRatings) {
            builder.append(subRating.getTitle());
            builder.append(", ");
        }
        return rating.getTitle() + " (" + builder.substring(0, builder.length() - 2) + ")";
    }

    private ContentRatingSystem.Rating getRating(TvContentRating canonicalRating) {
        if (canonicalRating == null || this.mContentRatingSystems == null) {
            return null;
        }
        for (ContentRatingSystem system : this.mContentRatingSystems) {
            if (system.getDomain().equals(canonicalRating.getDomain()) && system.getName().equals(canonicalRating.getRatingSystem())) {
                for (ContentRatingSystem.Rating rating : system.getRatings()) {
                    if (rating.getName().equals(canonicalRating.getMainRating())) {
                        return rating;
                    }
                }
                continue;
            }
        }
        return null;
    }

    private List<ContentRatingSystem.SubRating> getSubRatings(ContentRatingSystem.Rating rating, TvContentRating canonicalRating) {
        List<ContentRatingSystem.SubRating> subRatings = new ArrayList<>();
        if (rating == null || rating.getSubRatings() == null || canonicalRating == null || canonicalRating.getSubRatings() == null) {
            return subRatings;
        }
        for (String subRatingString : canonicalRating.getSubRatings()) {
            Iterator<ContentRatingSystem.SubRating> it = rating.getSubRatings().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ContentRatingSystem.SubRating subRating = it.next();
                if (subRating.getName().equals(subRatingString)) {
                    subRatings.add(subRating);
                    break;
                }
            }
        }
        return subRatings;
    }
}
