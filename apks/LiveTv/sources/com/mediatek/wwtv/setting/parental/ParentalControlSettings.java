package com.mediatek.wwtv.setting.parental;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import com.mediatek.wwtv.setting.parental.ContentRatingSystem;
import java.util.HashSet;
import java.util.Set;

public class ParentalControlSettings {
    public static final int RATING_BLOCKED = 0;
    public static final int RATING_BLOCKED_PARTIAL = 1;
    public static final int RATING_NOT_BLOCKED = 2;
    private final Context mContext;
    private Set<TvContentRating> mCustomRatings;
    private Set<TvContentRating> mRatings;
    private final TvInputManager mTvInputManager = ((TvInputManager) this.mContext.getSystemService("tv_input"));

    public ParentalControlSettings(Context context) {
        this.mContext = context;
    }

    public boolean isParentalControlsEnabled() {
        return this.mTvInputManager.isParentalControlsEnabled();
    }

    public void setParentalControlsEnabled(boolean enabled) {
        this.mTvInputManager.setParentalControlsEnabled(enabled);
    }

    public void setContentRatingSystemEnabled(ContentRatingsManager manager, ContentRatingSystem contentRatingSystem, boolean enabled) {
        if (enabled) {
            TvSettings.addContentRatingSystem(this.mContext, contentRatingSystem.getId());
            updateRatingsForCurrentLevel(manager);
            return;
        }
        for (TvContentRating tvContentRating : this.mTvInputManager.getBlockedRatings()) {
            if (contentRatingSystem.ownsRating(tvContentRating)) {
                this.mTvInputManager.removeBlockedRating(tvContentRating);
            }
        }
        TvSettings.removeContentRatingSystem(this.mContext, contentRatingSystem.getId());
    }

    public boolean isContentRatingSystemEnabled(ContentRatingSystem contentRatingSystem) {
        return TvSettings.hasContentRatingSystem(this.mContext, contentRatingSystem.getId());
    }

    public void loadRatings() {
        this.mRatings = new HashSet(this.mTvInputManager.getBlockedRatings());
    }

    private void storeRatings() {
        Set<TvContentRating> removed = new HashSet<>(this.mTvInputManager.getBlockedRatings());
        removed.removeAll(this.mRatings);
        for (TvContentRating tvContentRating : removed) {
            this.mTvInputManager.removeBlockedRating(tvContentRating);
        }
        Set<TvContentRating> added = new HashSet<>(this.mRatings);
        added.removeAll(this.mTvInputManager.getBlockedRatings());
        for (TvContentRating tvContentRating2 : added) {
            this.mTvInputManager.addBlockedRating(tvContentRating2);
        }
    }

    private void updateRatingsForCurrentLevel(ContentRatingsManager manager) {
        int currentLevel = getContentRatingLevel();
        if (currentLevel != 4) {
            this.mRatings = ContentRatingLevelPolicy.getRatingsForLevel(this, manager, currentLevel);
            storeRatings();
        }
    }

    public void setContentRatingLevel(ContentRatingsManager manager, int level) {
        int currentLevel = getContentRatingLevel();
        if (level != currentLevel) {
            if (currentLevel == 4) {
                this.mCustomRatings = this.mRatings;
            }
            TvSettings.setContentRatingLevel(this.mContext, level);
            if (level != 4) {
                this.mRatings = ContentRatingLevelPolicy.getRatingsForLevel(this, manager, level);
            } else if (this.mCustomRatings != null) {
                this.mRatings = new HashSet(this.mCustomRatings);
            }
            storeRatings();
        }
    }

    public int getContentRatingLevel() {
        return TvSettings.getContentRatingLevel(this.mContext);
    }

    public boolean setRatingBlocked(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, boolean blocked) {
        return setRatingBlockedInternal(contentRatingSystem, rating, (ContentRatingSystem.SubRating) null, blocked);
    }

    public boolean isRatingBlocked(TvContentRating[] ratings) {
        return getBlockedRating(ratings) != null;
    }

    public TvContentRating getBlockedRating(TvContentRating[] ratings) {
        if (ratings == null) {
            return null;
        }
        for (TvContentRating rating : ratings) {
            if (this.mTvInputManager.isRatingBlocked(rating)) {
                return rating;
            }
        }
        return null;
    }

    public boolean isRatingBlocked(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating) {
        return this.mRatings.contains(toTvContentRating(contentRatingSystem, rating));
    }

    public boolean setSubRatingBlocked(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, ContentRatingSystem.SubRating subRating, boolean blocked) {
        return setRatingBlockedInternal(contentRatingSystem, rating, subRating, blocked);
    }

    public boolean isSubRatingEnabled(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, ContentRatingSystem.SubRating subRating) {
        return this.mRatings.contains(toTvContentRating(contentRatingSystem, rating, subRating));
    }

    private boolean setRatingBlockedInternal(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, ContentRatingSystem.SubRating subRating, boolean blocked) {
        TvContentRating tvContentRating;
        boolean changed;
        if (subRating == null) {
            tvContentRating = toTvContentRating(contentRatingSystem, rating);
        } else {
            tvContentRating = toTvContentRating(contentRatingSystem, rating, subRating);
        }
        if (blocked) {
            changed = this.mRatings.add(tvContentRating);
            this.mTvInputManager.addBlockedRating(tvContentRating);
        } else {
            changed = this.mRatings.remove(tvContentRating);
            this.mTvInputManager.removeBlockedRating(tvContentRating);
        }
        if (changed) {
            changeToCustomLevel();
        }
        return changed;
    }

    private void changeToCustomLevel() {
        if (getContentRatingLevel() != 4) {
            TvSettings.setContentRatingLevel(this.mContext, 4);
        }
    }

    public int getBlockedStatus(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating) {
        if (isRatingBlocked(contentRatingSystem, rating)) {
            return 0;
        }
        for (ContentRatingSystem.SubRating subRating : rating.getSubRatings()) {
            if (isSubRatingEnabled(contentRatingSystem, rating, subRating)) {
                return 1;
            }
        }
        return 2;
    }

    private TvContentRating toTvContentRating(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating) {
        return TvContentRating.createRating(contentRatingSystem.getDomain(), contentRatingSystem.getName(), rating.getName(), new String[0]);
    }

    private TvContentRating toTvContentRating(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, ContentRatingSystem.SubRating subRating) {
        return TvContentRating.createRating(contentRatingSystem.getDomain(), contentRatingSystem.getName(), rating.getName(), new String[]{subRating.getName()});
    }
}
