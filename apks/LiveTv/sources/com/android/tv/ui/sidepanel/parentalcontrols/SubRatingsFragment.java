package com.android.tv.ui.sidepanel.parentalcontrols;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.android.tv.parental.ContentRatingSystem;
import com.android.tv.ui.sidepanel.CheckBoxItem;
import com.android.tv.ui.sidepanel.DividerItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import java.util.ArrayList;
import java.util.List;

public class SubRatingsFragment extends SideFragment {
    private static final String ARGS_CONTENT_RATING_SYSTEM_ID = "args_content_rating_system_id";
    private static final String ARGS_RATING_NAME = "args_rating_name";
    private static final String TAG = "SubRatingsFragment";
    /* access modifiers changed from: private */
    public ContentRatingSystem mContentRatingSystem;
    /* access modifiers changed from: private */
    public ContentRatingSystem.Rating mRating;
    /* access modifiers changed from: private */
    public final List<SubRatingItem> mSubRatingItems = new ArrayList();

    public static SubRatingsFragment create(ContentRatingSystem contentRatingSystem, String ratingName) {
        SubRatingsFragment fragment = new SubRatingsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_CONTENT_RATING_SYSTEM_ID, contentRatingSystem.getId());
        args.putString(ARGS_RATING_NAME, ratingName);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContentRatingSystem = TvSingletons.getSingletons().getContentRatingsManager().getContentRatingSystem(getArguments().getString(ARGS_CONTENT_RATING_SYSTEM_ID));
        if (this.mContentRatingSystem != null) {
            this.mRating = this.mContentRatingSystem.getRating(getArguments().getString(ARGS_RATING_NAME));
        }
        if (this.mRating == null) {
            closeFragment();
        }
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_subrating_title, new Object[]{this.mRating.getTitle()});
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        List<Item> items = new ArrayList<>();
        items.add(new RatingItem());
        items.add(new DividerItem(getString(R.string.option_subrating_header)));
        this.mSubRatingItems.clear();
        for (ContentRatingSystem.SubRating subRating : this.mRating.getSubRatings()) {
            this.mSubRatingItems.add(new SubRatingItem(subRating));
        }
        items.addAll(this.mSubRatingItems);
        return items;
    }

    private class RatingItem extends CheckBoxItem {
        private RatingItem() {
            super(SubRatingsFragment.this.mRating.getTitle(), SubRatingsFragment.this.mRating.getDescription());
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            CompoundButton button = (CompoundButton) view.findViewById(getCompoundButtonId());
            button.setButtonDrawable(R.drawable.btn_lock_material_anim);
            button.setVisibility(0);
            Drawable icon = SubRatingsFragment.this.mRating.getIcon();
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            if (icon != null) {
                imageView.setVisibility(0);
                imageView.setImageDrawable(icon);
                return;
            }
            imageView.setVisibility(8);
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            setChecked(SubRatingsFragment.this.isRatingEnabled());
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            boolean checked = isChecked();
            SubRatingsFragment.this.setRatingEnabled(checked, SubRatingsFragment.this.mRating);
            TvSingletons.getSingletons().getParentalControlSettings().setRelativeRatingsEnabled(SubRatingsFragment.this.mContentRatingSystem, SubRatingsFragment.this.mRating, checked);
            for (ContentRatingSystem.SubRating subRating : SubRatingsFragment.this.mRating.getSubRatings()) {
                SubRatingsFragment.this.setSubRatingEnabled(subRating, checked, SubRatingsFragment.this.mRating);
            }
            for (SubRatingItem item : SubRatingsFragment.this.mSubRatingItems) {
                item.setChecked(checked);
            }
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_rating;
        }

        /* access modifiers changed from: protected */
        public int getDescriptionViewId() {
            return R.id.description;
        }

        /* access modifiers changed from: protected */
        public int getTitleViewId() {
            return R.id.title;
        }
    }

    private class SubRatingItem extends CheckBoxItem {
        private final ContentRatingSystem.SubRating mSubRating;

        private SubRatingItem(ContentRatingSystem.SubRating subRating) {
            super(subRating.getTitle(), subRating.getDescription());
            this.mSubRating = subRating;
        }

        /* access modifiers changed from: protected */
        public void onBind(View view) {
            super.onBind(view);
            CompoundButton button = (CompoundButton) view.findViewById(getCompoundButtonId());
            button.setButtonDrawable(R.drawable.btn_lock_material_anim);
            button.setVisibility(0);
            Drawable icon = this.mSubRating.getIcon();
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            if (icon != null) {
                imageView.setVisibility(0);
                imageView.setImageDrawable(icon);
                return;
            }
            imageView.setVisibility(8);
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            setChecked(SubRatingsFragment.this.isSubRatingEnabled(this.mSubRating));
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            SubRatingsFragment.this.setSubRatingEnabled(this.mSubRating, isChecked(), SubRatingsFragment.this.mRating);
            TvSingletons.getSingletons().getParentalControlSettings().setRelativeRating2SubRatingEnabled(SubRatingsFragment.this.mContentRatingSystem, isChecked(), SubRatingsFragment.this.mRating, this.mSubRating);
        }

        /* access modifiers changed from: protected */
        public int getResourceId() {
            return R.layout.option_item_rating;
        }

        /* access modifiers changed from: protected */
        public int getDescriptionViewId() {
            return R.id.description;
        }

        /* access modifiers changed from: protected */
        public int getTitleViewId() {
            return R.id.title;
        }
    }

    /* access modifiers changed from: private */
    public boolean isRatingEnabled() {
        return TvSingletons.getSingletons().getParentalControlSettings().isRatingBlocked(this.mContentRatingSystem, this.mRating);
    }

    /* access modifiers changed from: private */
    public boolean isSubRatingEnabled(ContentRatingSystem.SubRating subRating) {
        return TvSingletons.getSingletons().getParentalControlSettings().isSubRatingEnabled(this.mContentRatingSystem, this.mRating, subRating);
    }

    /* access modifiers changed from: private */
    public void setRatingEnabled(boolean enabled, ContentRatingSystem.Rating rating) {
        TvSingletons.getSingletons().getParentalControlSettings().setRatingBlocked(this.mContentRatingSystem, rating, enabled);
    }

    /* access modifiers changed from: private */
    public void setSubRatingEnabled(ContentRatingSystem.SubRating subRating, boolean enabled, ContentRatingSystem.Rating rating) {
        TvSingletons.getSingletons().getParentalControlSettings().setSubRatingBlocked(this.mContentRatingSystem, rating, subRating, enabled);
    }
}
