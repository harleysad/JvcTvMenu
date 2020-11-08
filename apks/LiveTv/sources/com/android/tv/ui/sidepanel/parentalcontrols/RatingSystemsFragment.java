package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.os.Bundle;
import com.android.tv.parental.ContentRatingSystem;
import com.android.tv.parental.ContentRatingsManager;
import com.android.tv.parental.ParentalControlSettings;
import com.android.tv.ui.sidepanel.CheckBoxItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.util.TvSettings;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatingSystemsFragment extends SideFragment {
    private static final String TAG = "RatingSystemsFragment";
    private static String mSelectCountry;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateCurrentCountry();
        setDefaultRatingSystemsIfNeeded(getActivity());
    }

    private static void updateCurrentCountry() {
        if (CommonIntegration.isUSRegion()) {
            mSelectCountry = "US";
        } else if (CommonIntegration.isEURegion()) {
            mSelectCountry = Util.convertConurty(MtkTvConfig.getInstance().getCountry());
        } else if (CommonIntegration.isSARegion()) {
            mSelectCountry = "BR";
        }
    }

    public static String getDescription(Activity tvActivity) {
        updateCurrentCountry();
        setDefaultRatingSystemsIfNeeded(tvActivity);
        List<ContentRatingSystem> contentRatingSystems = TvSingletons.getSingletons().getContentRatingsManager().getContentRatingSystems();
        Collections.sort(contentRatingSystems, ContentRatingSystem.DISPLAY_NAME_COMPARATOR);
        StringBuilder builder = new StringBuilder();
        for (ContentRatingSystem s : contentRatingSystems) {
            if (TvSingletons.getSingletons().getParentalControlSettings().isContentRatingSystemEnabled(s)) {
                builder.append(s.getDisplayName());
                builder.append(", ");
            }
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 2);
        }
        return tvActivity.getString(R.string.menu_arrays_None);
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_country_rating_systems);
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        ContentRatingsManager contentRatingsManager = TvSingletons.getSingletons().getContentRatingsManager();
        ParentalControlSettings parentalControlSettings = TvSingletons.getSingletons().getParentalControlSettings();
        List<ContentRatingSystem> contentRatingSystems = contentRatingsManager.getContentRatingSystems();
        Collections.sort(contentRatingSystems, ContentRatingSystem.DISPLAY_NAME_COMPARATOR);
        List<Item> items = new ArrayList<>();
        List<Item> itemsHidden = new ArrayList<>();
        List<Item> itemsHiddenMultipleCountries = new ArrayList<>();
        boolean hasMappingCountries = false;
        MtkLog.d(TAG, "mSelectCountry=" + mSelectCountry);
        for (ContentRatingSystem s : contentRatingSystems) {
            if (s.getCountries() != null && s.getCountries().contains(mSelectCountry)) {
                items.add(new RatingSystemItem(this, s));
                hasMappingCountries = true;
            } else if (s.isCustom()) {
                items.add(new RatingSystemItem(this, s));
            } else if (s.getCountries().size() > 2) {
                itemsHiddenMultipleCountries.add(new RatingSystemItem(s, ""));
            } else {
                itemsHidden.add(new RatingSystemItem(this, s));
            }
        }
        if (!hasMappingCountries) {
            items.addAll(itemsHiddenMultipleCountries);
        }
        return items;
    }

    private static void setDefaultRatingSystemsIfNeeded(Activity tvActivity) {
        if (!TvSettings.isContentRatingSystemSet(tvActivity)) {
            MtkLog.d(TAG, "mSelectCountry=" + mSelectCountry);
            List<ContentRatingSystem> contentRatingSystems = TvSingletons.getSingletons().getContentRatingsManager().getContentRatingSystems();
            ContentRatingsManager manager = TvSingletons.getSingletons().getContentRatingsManager();
            ParentalControlSettings settings = TvSingletons.getSingletons().getParentalControlSettings();
            ContentRatingSystem otherCountries = null;
            boolean hasMappingCountries = false;
            for (ContentRatingSystem s : contentRatingSystems) {
                if (!s.isCustom() && s.getCountries() != null && s.getCountries().contains(mSelectCountry)) {
                    settings.setContentRatingSystemEnabled(manager, s, true);
                    hasMappingCountries = true;
                }
                if (!s.isCustom() && s.getCountries() != null && s.getCountries().size() > 2) {
                    otherCountries = s;
                }
            }
            if (!hasMappingCountries && otherCountries != null) {
                settings.setContentRatingSystemEnabled(manager, otherCountries, true);
            }
        }
    }

    private class RatingSystemItem extends CheckBoxItem {
        private final ContentRatingSystem mContentRatingSystem;

        RatingSystemItem(RatingSystemsFragment ratingSystemsFragment, ContentRatingSystem contentRatingSystem) {
            this(contentRatingSystem, (String) null);
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        RatingSystemItem(ContentRatingSystem contentRatingSystem, String description) {
            super(contentRatingSystem.getDisplayName(), description, description != null);
            this.mContentRatingSystem = contentRatingSystem;
        }

        /* access modifiers changed from: protected */
        public void onUpdate() {
            super.onUpdate();
            setChecked(TvSingletons.getSingletons().getParentalControlSettings().isContentRatingSystemEnabled(this.mContentRatingSystem));
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            super.onSelected();
            TvSingletons.getSingletons().getParentalControlSettings().setContentRatingSystemEnabled(TvSingletons.getSingletons().getContentRatingsManager(), this.mContentRatingSystem, isChecked());
        }
    }
}
