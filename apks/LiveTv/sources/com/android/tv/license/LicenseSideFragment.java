package com.android.tv.license;

import android.content.Context;
import com.android.tv.ui.sidepanel.ActionItem;
import com.android.tv.ui.sidepanel.SideFragment;
import com.mediatek.wwtv.tvcenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LicenseSideFragment extends SideFragment {
    public static final String TRACKER_LABEL = "Open Source Licenses";
    private List<LicenseActionItem> licenses;

    public class LicenseActionItem extends ActionItem {
        private final License license;

        public LicenseActionItem(License license2) {
            super(license2.getLibraryName());
            this.license = license2;
        }

        /* access modifiers changed from: protected */
        public void onSelected() {
            LicenseDialogFragment newInstance = LicenseDialogFragment.newInstance(this.license);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.licenses = toActionItems(Licenses.getLicenses(context));
    }

    private List<LicenseActionItem> toActionItems(ArrayList<License> licenses2) {
        List<LicenseActionItem> items = new ArrayList<>(licenses2.size());
        Iterator<License> it = licenses2.iterator();
        while (it.hasNext()) {
            items.add(new LicenseActionItem(it.next()));
        }
        return items;
    }

    /* access modifiers changed from: protected */
    public String getTitle() {
        return getResources().getString(R.string.settings_menu_licenses);
    }

    public String getTrackerLabel() {
        return TRACKER_LABEL;
    }

    /* access modifiers changed from: protected */
    public List<LicenseActionItem> getItemList() {
        return this.licenses;
    }
}
