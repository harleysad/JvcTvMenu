package com.android.tv.parental;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import com.android.tv.parental.ContentRatingSystem;
import com.android.tv.util.TvSettings;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParentalControlSettings {
    public static final int RATING_BLOCKED = 0;
    public static final int RATING_BLOCKED_PARTIAL = 1;
    public static final int RATING_NOT_BLOCKED = 2;
    private static final String TAG = "ParentalControlSettings";
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

    public boolean hasContentRatingSystemSet() {
        return TvSettings.hasContentRatingSystem(this.mContext);
    }

    public void loadRatings() {
        this.mRatings = new HashSet(this.mTvInputManager.getBlockedRatings());
        if (this.mRatings != null) {
            MtkLog.d(TAG, "mRatings=" + this.mRatings.size());
        }
    }

    public Set<TvContentRating> getRatings() {
        return new HashSet<>(this.mTvInputManager.getBlockedRatings());
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
            if (currentLevel != 0) {
                this.mRatings.add(TvContentRating.UNRATED);
            }
            storeRatings();
        }
    }

    public void setContentRatingLevel(ContentRatingsManager manager, int level) {
        int currentLevel = getContentRatingLevel();
        MtkLog.d(TAG, "currentLevel=" + currentLevel + ",level=" + level);
        if (level != currentLevel) {
            if (currentLevel == 4) {
                this.mCustomRatings = this.mRatings;
            }
            TvSettings.setContentRatingLevel(this.mContext, level);
            if (level != 4) {
                this.mRatings = ContentRatingLevelPolicy.getRatingsForLevel(this, manager, level);
                if (level != 0) {
                    this.mRatings.add(TvContentRating.UNRATED);
                } else {
                    this.mRatings.clear();
                }
            } else if (this.mCustomRatings != null) {
                this.mRatings = new HashSet(this.mCustomRatings);
            }
            storeRatings();
        }
    }

    public int getContentRatingLevel() {
        int currentLevel = TvSettings.getContentRatingLevel(this.mContext);
        if (currentLevel < 0 || currentLevel > 4) {
            return 0;
        }
        return currentLevel;
    }

    public boolean setUnratedBlocked(boolean blocked) {
        TVContent.getInstance(this.mContext).setBlockUnrated(blocked);
        return false;
    }

    public boolean setRatingBlocked(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating, boolean blocked) {
        return setRatingBlockedInternal(contentRatingSystem, rating, (ContentRatingSystem.SubRating) null, blocked);
    }

    public boolean isRatingBlocked(TvContentRating[] ratings) {
        return getBlockedRating(ratings) != null;
    }

    public TvContentRating getBlockedRating(TvContentRating[] ratings) {
        if (ratings != null && ratings.length > 0) {
            for (TvContentRating rating : ratings) {
                if (this.mTvInputManager.isRatingBlocked(rating)) {
                    return rating;
                }
            }
            return null;
        } else if (this.mTvInputManager.isRatingBlocked(TvContentRating.UNRATED)) {
            return TvContentRating.UNRATED;
        } else {
            return null;
        }
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
            MtkLog.d(TAG, "add rating block :" + tvContentRating.flattenToString());
            this.mTvInputManager.addBlockedRating(tvContentRating);
        } else {
            changed = this.mRatings.remove(tvContentRating);
            MtkLog.d(TAG, "remove rating block :" + tvContentRating.flattenToString());
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

    public void setRelativeRatingsEnabled(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating selectRating, boolean enabled) {
        List<ContentRatingSystem.Order> orders = contentRatingSystem.getOrders();
        if (orders == null || orders.size() <= 0) {
            MtkLog.d(TAG, "orders==null or orders.size<=0");
            return;
        }
        ContentRatingSystem.Order order = orders.get(0);
        for (ContentRatingSystem.Rating rating : contentRatingSystem.getRatings()) {
            int selectedRatingOrderIndex = order.getRatingIndex(selectRating);
            int ratingOrderIndex = order.getRatingIndex(rating);
            MtkLog.d(TAG, "selectedRatingOrderIndex=" + selectedRatingOrderIndex + ",ratingOrderIndex=" + ratingOrderIndex);
            if (!(ratingOrderIndex == -1 || selectedRatingOrderIndex == -1)) {
                if ((ratingOrderIndex > selectedRatingOrderIndex && enabled) || (ratingOrderIndex < selectedRatingOrderIndex && !enabled)) {
                    setRatingBlocked(contentRatingSystem, rating, enabled);
                    setRelativesetSubRatingEnabled(contentRatingSystem, enabled, rating);
                }
            }
        }
    }

    private void setRelativesetSubRatingEnabled(ContentRatingSystem contentRatingSystem, boolean enabled, ContentRatingSystem.Rating rating) {
        for (ContentRatingSystem.SubRating subRating : rating.getSubRatings()) {
            setSubRatingBlocked(contentRatingSystem, rating, subRating, enabled);
        }
    }

    public void setRelativeRating2SubRatingEnabled(ContentRatingSystem contentRatingSystem, boolean enabled, ContentRatingSystem.Rating relativeRating, ContentRatingSystem.SubRating subRating) {
        List<ContentRatingSystem.Order> orders = contentRatingSystem.getOrders();
        if (orders != null && orders.size() > 0) {
            ContentRatingSystem.Order order = orders.get(0);
            for (ContentRatingSystem.Rating rating : contentRatingSystem.getRatings()) {
                int selectedRatingOrderIndex = order.getRatingIndex(relativeRating);
                int ratingOrderIndex = order.getRatingIndex(rating);
                if (!(ratingOrderIndex == -1 || selectedRatingOrderIndex == -1)) {
                    if ((ratingOrderIndex > selectedRatingOrderIndex && enabled) || (ratingOrderIndex < selectedRatingOrderIndex && !enabled)) {
                        List<ContentRatingSystem.SubRating> subRatingslist = rating.getSubRatings();
                        if (subRatingslist.contains(subRating)) {
                            ContentRatingSystem.SubRating relativeSub = subRatingslist.get(subRatingslist.indexOf(subRating));
                            if (isSubRatingEnabled(contentRatingSystem, rating, relativeSub) != enabled) {
                                setSubRatingBlocked(contentRatingSystem, rating, relativeSub, enabled);
                            }
                        }
                    }
                }
            }
        }
    }
}
