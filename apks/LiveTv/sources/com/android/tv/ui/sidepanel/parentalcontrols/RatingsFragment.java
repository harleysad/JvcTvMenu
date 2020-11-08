package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.android.tv.dialog.WebDialogFragment;
import com.android.tv.parental.ContentRatingSystem;
import com.android.tv.parental.ParentalControlSettings;
import com.android.tv.ui.sidepanel.CheckBoxItem;
import com.android.tv.ui.sidepanel.DividerItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.RadioButtonItem;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RatingsFragment extends SideFragment {
    private static final String TAG = "RatingsFragment";
    private static final String TRACKER_LABEL = "Ratings";
    /* access modifiers changed from: private */
    public static final SparseIntArray sDescriptionResourceIdMap = new SparseIntArray(sLevelResourceIdMap.size());
    /* access modifiers changed from: private */
    public static final SparseIntArray sLevelResourceIdMap = new SparseIntArray(5);
    /* access modifiers changed from: private */
    public CheckBoxItem mBlockUnratedItem;
    private final Map<String, List<RatingItem>> mContentRatingSystemItemMap = new ArrayMap();
    private int mItemsSize;
    /* access modifiers changed from: private */
    public ParentalControlSettings mParentalControlSettings;
    /* access modifiers changed from: private */
    public final List<RatingLevelItem> mRatingLevelItems = new ArrayList();
    /* access modifiers changed from: private */
    public TVContent mTvContent;

    static {
        sLevelResourceIdMap.put(0, R.string.menu_arrays_None);
        sLevelResourceIdMap.put(1, R.string.option_rating_high);
        sLevelResourceIdMap.put(2, R.string.option_rating_medium);
        sLevelResourceIdMap.put(3, R.string.option_rating_low);
        sLevelResourceIdMap.put(4, R.string.option_rating_custom);
        sDescriptionResourceIdMap.put(1, R.string.option_rating_high_description);
        sDescriptionResourceIdMap.put(2, R.string.option_rating_medium_description);
        sDescriptionResourceIdMap.put(3, R.string.option_rating_low_description);
        sDescriptionResourceIdMap.put(4, R.string.option_rating_custom_description);
    }

    public static String getDescription(Activity tvActivity) {
        int currentLevel = TvSingletons.getSingletons().getParentalControlSettings().getContentRatingLevel();
        if (sLevelResourceIdMap.indexOfKey(currentLevel) >= 0) {
            return tvActivity.getString(sLevelResourceIdMap.get(currentLevel));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_ratings);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        RatingItem item;
        TvSingletons.getSingletons().getParentalControlSettings().loadRatings();
        List<Item> items = new ArrayList<>();
        boolean hasContentRatingSystem = this.mParentalControlSettings.hasContentRatingSystemSet();
        if (this.mBlockUnratedItem != null) {
            items.add(this.mBlockUnratedItem);
            items.add(new DividerItem());
        }
        this.mRatingLevelItems.clear();
        for (int i = 0; i < sLevelResourceIdMap.size(); i++) {
            this.mRatingLevelItems.add(new RatingLevelItem(sLevelResourceIdMap.keyAt(i)));
        }
        updateRatingLevels();
        updateRatingLevelsEnable(hasContentRatingSystem);
        items.addAll(this.mRatingLevelItems);
        this.mContentRatingSystemItemMap.clear();
        List<ContentRatingSystem> contentRatingSystems = TvSingletons.getSingletons().getContentRatingsManager().getContentRatingSystems();
        Collections.sort(contentRatingSystems, ContentRatingSystem.DISPLAY_NAME_COMPARATOR);
        if (hasContentRatingSystem) {
            for (ContentRatingSystem s : contentRatingSystems) {
                if (this.mParentalControlSettings.isContentRatingSystemEnabled(s)) {
                    List<RatingItem> ratingItems = new ArrayList<>();
                    items.add(new DividerItem(s.getDisplayName()));
                    for (ContentRatingSystem.Rating rating : s.getRatings()) {
                        if (rating.getSubRatings().isEmpty()) {
                            item = new RatingItem(s, rating);
                        } else {
                            item = new RatingWithSubItem(s, rating);
                        }
                        items.add(item);
                        if (rating.getSubRatings().isEmpty()) {
                            ratingItems.add(item);
                        }
                    }
                    this.mContentRatingSystemItemMap.put(s.getId(), ratingItems);
                }
            }
        }
        this.mItemsSize = items.size();
        return items;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mTvContent = TVContent.getInstance(getActivity());
        this.mParentalControlSettings = TvSingletons.getSingletons().getParentalControlSettings();
        this.mBlockUnratedItem = new CheckBoxItem(getResources().getString(R.string.option_block_unrated_programs)) {
            /* access modifiers changed from: protected */
            public void onUpdate() {
                super.onUpdate();
                boolean z = true;
                if (RatingsFragment.this.mTvContent.getBlockUnrated() != 1) {
                    z = false;
                }
                setChecked(z);
            }

            /* access modifiers changed from: protected */
            public void onSelected() {
                super.onSelected();
                if (RatingsFragment.this.mParentalControlSettings.setUnratedBlocked(isChecked())) {
                    RatingsFragment.this.updateRatingLevels();
                }
            }
        };
    }

    public void onResume() {
        super.onResume();
        if (getSelectedPosition() >= this.mItemsSize) {
            setSelectedPosition(this.mItemsSize - 1);
        }
    }

    /* access modifiers changed from: private */
    public void updateRatingLevels() {
        int ratingLevel = this.mParentalControlSettings.getContentRatingLevel();
        for (RatingLevelItem ratingLevelItem : this.mRatingLevelItems) {
            ratingLevelItem.setChecked(ratingLevel == ratingLevelItem.mRatingLevel);
        }
    }

    private void updateRatingLevelsEnable(boolean enabled) {
        for (RatingLevelItem ratingLevelItem : this.mRatingLevelItems) {
            ratingLevelItem.setEnabled(enabled);
        }
    }

    private class RatingLevelItem extends RadioButtonItem {
        /* access modifiers changed from: private */
        public final int mRatingLevel;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private RatingLevelItem(int r4) {
            /*
                r2 = this;
                com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment.this = r3
                android.util.SparseIntArray r0 = com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment.sLevelResourceIdMap
                int r0 = r0.get(r4)
                java.lang.String r0 = r3.getString(r0)
                android.util.SparseIntArray r1 = com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment.sDescriptionResourceIdMap
                int r1 = r1.indexOfKey(r4)
                if (r1 < 0) goto L_0x0026
                android.util.SparseIntArray r1 = com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment.sDescriptionResourceIdMap
                int r1 = r1.get(r4)
                java.lang.String r3 = r3.getString(r1)
                goto L_0x0027
            L_0x0026:
                r3 = 0
            L_0x0027:
                r2.<init>(r0, r3)
                r2.mRatingLevel = r4
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment.RatingLevelItem.<init>(com.android.tv.ui.sidepanel.parentalcontrols.RatingsFragment, int):void");
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            RatingsFragment.this.mParentalControlSettings.setContentRatingLevel(TvSingletons.getSingletons().getContentRatingsManager(), this.mRatingLevel);
            CheckBoxItem unused = RatingsFragment.this.mBlockUnratedItem;
            RatingsFragment.this.notifyItemsChanged(RatingsFragment.this.mRatingLevelItems.size());
        }
    }

    private class RatingItem extends CheckBoxItem {
        private CompoundButton mCompoundButton;
        protected final ContentRatingSystem mContentRatingSystem;
        private final Drawable mIcon;
        protected final ContentRatingSystem.Rating mRating;

        private RatingItem(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating) {
            super(rating.getTitle(), rating.getDescription());
            this.mContentRatingSystem = contentRatingSystem;
            this.mRating = rating;
            this.mIcon = rating.getIcon();
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            this.mCompoundButton = (CompoundButton) view.findViewById(getCompoundButtonId());
            this.mCompoundButton.setVisibility(0);
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            if (this.mIcon != null) {
                imageView.setVisibility(0);
                imageView.setImageDrawable(this.mIcon);
                return;
            }
            imageView.setVisibility(8);
        }

        /* access modifiers changed from: protected */
        public void onUnbind() {
            super.onUnbind();
            this.mCompoundButton = null;
        }

        /* access modifiers changed from: protected */
        public int getDescriptionViewId() {
            return R.id.description;
        }

        /* access modifiers changed from: protected */
        public int getTitleViewId() {
            return R.id.title;
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            this.mCompoundButton.setButtonDrawable(getButtonDrawable());
            setChecked(RatingsFragment.this.mParentalControlSettings.isRatingBlocked(this.mContentRatingSystem, this.mRating));
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            if (RatingsFragment.this.mParentalControlSettings.setRatingBlocked(this.mContentRatingSystem, this.mRating, isChecked())) {
                RatingsFragment.this.updateRatingLevels();
            }
            RatingsFragment.this.mParentalControlSettings.setRelativeRatingsEnabled(this.mContentRatingSystem, this.mRating, isChecked());
            RatingsFragment.this.notifyDataSetChanged();
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_rating;
        }

        /* access modifiers changed from: protected */
        public int getButtonDrawable() {
            return R.drawable.btn_lock_material_anim;
        }

        private void setRatingBlocked(boolean isChecked) {
            if (isChecked() != isChecked) {
                RatingsFragment.this.mParentalControlSettings.setRatingBlocked(this.mContentRatingSystem, this.mRating, isChecked);
                notifyUpdated();
            }
        }
    }

    private class RatingWithSubItem extends RatingItem {
        private RatingWithSubItem(ContentRatingSystem contentRatingSystem, ContentRatingSystem.Rating rating) {
            super(contentRatingSystem, rating);
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            RatingsFragment.this.mSideFragmentManager.show(SubRatingsFragment.create(this.mContentRatingSystem, this.mRating.getName()));
        }

        /* access modifiers changed from: protected */
        public int getButtonDrawable() {
            int blockedStatus = RatingsFragment.this.mParentalControlSettings.getBlockedStatus(this.mContentRatingSystem, this.mRating);
            if (blockedStatus == 0) {
                return R.drawable.btn_lock_material;
            }
            if (blockedStatus == 1) {
                return R.drawable.btn_partial_lock_material;
            }
            return R.drawable.btn_unlock_material;
        }
    }

    public static class AttributionItem extends Item {
        public static final String DIALOG_TAG = AttributionItem.class.getSimpleName();
        public static final String TRACKER_LABEL = "Sources for content rating systems";
        private final Activity mMainActivity;

        public AttributionItem(Activity mainActivity) {
            this.mMainActivity = mainActivity;
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_attribution;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            WebDialogFragment newInstance = WebDialogFragment.newInstance("file:///android_asset/rating_sources.html", this.mMainActivity.getString(R.string.option_attribution), TRACKER_LABEL);
        }
    }
}
