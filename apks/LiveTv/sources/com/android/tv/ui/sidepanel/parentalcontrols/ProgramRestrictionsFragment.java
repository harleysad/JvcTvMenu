package com.android.tv.ui.sidepanel.parentalcontrols;

import android.app.Activity;
import android.os.Bundle;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.Item;
import com.android.tv.ui.sidepanel.SideFragment;
import com.android.tv.ui.sidepanel.SubMenuItem;
import com.android.tv.ui.sidepanel.SwitchItem;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class ProgramRestrictionsFragment extends SideFragment {
    private static final String TAG = "ProgramRestrictionsFragment";
    private List<ActionItem> mActionItems;
    /* access modifiers changed from: private */
    public boolean mRatingEnable = false;
    /* access modifiers changed from: private */
    public final SideFragment.SideFragmentListener mSideFragmentListener = new SideFragment.SideFragmentListener() {
        public void onSideFragmentViewDestroyed() {
            ProgramRestrictionsFragment.this.notifyDataSetChanged();
        }
    };

    public static String getDescription(Activity tvActivity) {
        return RatingsFragment.getDescription(tvActivity);
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getString(R.string.option_program_restrictions);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TvSingletons.getSingletons().getParentalControlSettings().loadRatings();
    }

    /* access modifiers changed from: protected */
    public List<Item> getItemList() {
        int ratingEnable = TVContent.getInstance(getActivity()).getRatingEnable();
        MtkLog.d(TAG, "ratingEnable=" + ratingEnable);
        boolean z = true;
        if (ratingEnable != 1) {
            z = false;
        }
        this.mRatingEnable = z;
        MtkLog.d(TAG, "mRatingEnable=" + this.mRatingEnable);
        List<Item> items = new ArrayList<>();
        this.mActionItems = new ArrayList();
        items.add(new SwitchItem(getString(R.string.common_on), getString(R.string.common_off)) {
            /* access modifiers changed from: protected */
            public void onUpdate() {
                super.onUpdate();
                setChecked(ProgramRestrictionsFragment.this.mRatingEnable);
            }

            /* access modifiers changed from: protected */
            public void onSelected() {
                super.onSelected();
                boolean checked = isChecked();
                TVContent.getInstance(ProgramRestrictionsFragment.this.getActivity()).setRatingEnable(checked);
                ProgramRestrictionsFragment.this.enableActionItems(checked);
            }
        });
        this.mActionItems.add(new SubMenuItem(getString(R.string.option_country_rating_systems), RatingSystemsFragment.getDescription(getMainActivity()), getMainActivity().getSideFragmentManager()) {
            /* access modifiers changed from: protected */
            public SideFragment getFragment() {
                SideFragment fragment = new RatingSystemsFragment();
                fragment.setListener(ProgramRestrictionsFragment.this.mSideFragmentListener);
                return fragment;
            }
        });
        SubMenuItem ratingsItem = new SubMenuItem(getString(R.string.option_ratings), RatingsFragment.getDescription(getMainActivity()), getMainActivity().getSideFragmentManager()) {
            /* access modifiers changed from: protected */
            public SideFragment getFragment() {
                SideFragment fragment = new RatingsFragment();
                fragment.setListener(ProgramRestrictionsFragment.this.mSideFragmentListener);
                return fragment;
            }
        };
        if (RatingSystemsFragment.getDescription(getMainActivity()).equals(getString(R.string.menu_arrays_None))) {
            ratingsItem.setEnabled(false);
        }
        this.mActionItems.add(ratingsItem);
        if (CommonIntegration.isUSRegion()) {
            this.mActionItems.add(new SubMenuItem(getString(R.string.option_program_open_vchip), "", this.mSideFragmentManager) {
                /* access modifiers changed from: protected */
                public SideFragment getFragment() {
                    SideFragment fragment = new OpenVchipRegionFragment();
                    fragment.setListener(ProgramRestrictionsFragment.this.mSideFragmentListener);
                    return fragment;
                }
            });
        }
        items.addAll(this.mActionItems);
        enableActionItems(this.mRatingEnable);
        return items;
    }

    /* access modifiers changed from: private */
    public void enableActionItems(boolean enabled) {
        for (ActionItem actionItem : this.mActionItems) {
            actionItem.setEnabled(enabled);
        }
    }
}
