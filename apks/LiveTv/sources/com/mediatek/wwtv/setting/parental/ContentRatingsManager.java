package com.mediatek.wwtv.setting.parental;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvInputManager;
import com.android.tv.util.TvInputManagerHelper;
import com.mediatek.wwtv.setting.parental.ContentRatingSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentRatingsManager {
    private final List<ContentRatingSystem> mContentRatingSystems;
    private final Context mContext;
    private TvInputManagerHelper.TvInputManagerInterface mTvInputManager;

    public ContentRatingsManager(Context context) {
        this(context, (TvInputManagerHelper.TvInputManagerInterface) null);
    }

    public ContentRatingsManager(Context context, TvInputManagerHelper.TvInputManagerInterface tvInputManager) {
        this.mContentRatingSystems = new ArrayList();
        this.mContext = context;
        this.mTvInputManager = tvInputManager;
    }

    public void update() {
        List<TvContentRatingSystemInfo> infos;
        this.mContentRatingSystems.clear();
        ContentRatingsParser parser = new ContentRatingsParser(this.mContext);
        if (this.mTvInputManager == null) {
            infos = ((TvInputManager) this.mContext.getSystemService("tv_input")).getTvContentRatingSystemList();
        } else {
            infos = this.mTvInputManager.getTvContentRatingSystemList();
        }
        for (TvContentRatingSystemInfo info : infos) {
            List<ContentRatingSystem> list = parser.parse(info);
            if (list != null) {
                this.mContentRatingSystems.addAll(list);
            }
        }
    }

    public List<ContentRatingSystem> getContentRatingSystems() {
        return new ArrayList(this.mContentRatingSystems);
    }

    public String getDisplayNameForRating(TvContentRating canonicalRating) {
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
