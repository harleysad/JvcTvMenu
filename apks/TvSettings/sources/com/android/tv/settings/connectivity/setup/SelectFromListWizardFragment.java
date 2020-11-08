package com.android.tv.settings.connectivity.setup;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.WifiSecurityUtil;
import com.android.tv.settings.util.AccessibilityHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class SelectFromListWizardFragment extends Fragment {
    private static final String EXTRA_DESCRIPTION = "description";
    private static final String EXTRA_LAST_SELECTION = "last_selection";
    private static final String EXTRA_LIST_ELEMENTS = "list_elements";
    private static final String EXTRA_TITLE = "title";
    private static final int SELECT_ITEM_DELAY = 100;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public String mLastSelectedName;
    private VerticalGridView mListView;
    /* access modifiers changed from: private */
    public View mMainView;
    private ViewTreeObserver.OnPreDrawListener mOnListPreDrawListener;
    private Runnable mSelectItemRunnable;

    private interface ActionListener {
        void onClick(ListItem listItem);

        void onFocus(ListItem listItem);
    }

    public interface Listener {
        void onListFocusChanged(ListItem listItem);

        void onListSelectionComplete(ListItem listItem);
    }

    public static class ListItemComparator implements Comparator<ListItem> {
        public int compare(ListItem o1, ListItem o2) {
            int pinnedPos1 = o1.getPinnedPosition();
            int pinnedPos2 = o2.getPinnedPosition();
            if (pinnedPos1 != 0 && pinnedPos2 == 0) {
                if (pinnedPos1 == 1) {
                    return -1;
                }
                if (pinnedPos1 == 2) {
                    return 1;
                }
            }
            if (pinnedPos1 == 0 && pinnedPos2 != 0) {
                if (pinnedPos2 == 1) {
                    return 1;
                }
                if (pinnedPos2 == 2) {
                    return -1;
                }
            }
            if (pinnedPos1 == 0 || pinnedPos2 == 0) {
                ScanResult o1ScanResult = o1.getScanResult();
                ScanResult o2ScanResult = o2.getScanResult();
                if (o1ScanResult == null) {
                    if (o2ScanResult == null) {
                        return 0;
                    }
                    return 1;
                } else if (o2ScanResult == null) {
                    return -1;
                } else {
                    int levelDiff = o2ScanResult.level - o1ScanResult.level;
                    if (levelDiff != 0) {
                        return levelDiff;
                    }
                    return o1ScanResult.SSID.compareTo(o2ScanResult.SSID);
                }
            } else if (pinnedPos1 == pinnedPos2) {
                return ((PinnedListItem) o1).getPinnedPriority() - ((PinnedListItem) o2).getPinnedPriority();
            } else {
                return pinnedPos1 == 2 ? 1 : -1;
            }
        }
    }

    public static class ListItem implements Parcelable {
        public static Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {
            public ListItem createFromParcel(Parcel source) {
                ScanResult scanResult = (ScanResult) source.readParcelable(ScanResult.class.getClassLoader());
                if (scanResult == null) {
                    return new ListItem(source.readString(), source.readInt());
                }
                return new ListItem(scanResult);
            }

            public ListItem[] newArray(int size) {
                return new ListItem[size];
            }
        };
        private final boolean mHasIconLevel;
        private final int mIconLevel;
        private final int mIconResource;
        private final String mName;
        private final ScanResult mScanResult;

        public ListItem(String name, int iconResource) {
            this.mName = name;
            this.mIconResource = iconResource;
            this.mIconLevel = 0;
            this.mHasIconLevel = false;
            this.mScanResult = null;
        }

        public ListItem(ScanResult scanResult) {
            int i;
            this.mName = scanResult.SSID;
            if (WifiSecurityUtil.getSecurity(scanResult) == 0) {
                i = R.drawable.setup_wifi_signal_open;
            } else {
                i = R.drawable.setup_wifi_signal_lock;
            }
            this.mIconResource = i;
            this.mIconLevel = WifiManager.calculateSignalLevel(scanResult.level, 4);
            this.mHasIconLevel = true;
            this.mScanResult = scanResult;
        }

        public String getName() {
            return this.mName;
        }

        /* access modifiers changed from: package-private */
        public int getIconResource() {
            return this.mIconResource;
        }

        /* access modifiers changed from: package-private */
        public int getIconLevel() {
            return this.mIconLevel;
        }

        /* access modifiers changed from: package-private */
        public boolean hasIconLevel() {
            return this.mHasIconLevel;
        }

        /* access modifiers changed from: package-private */
        public ScanResult getScanResult() {
            return this.mScanResult;
        }

        public int getPinnedPosition() {
            return 0;
        }

        public String toString() {
            return this.mName;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.mScanResult, flags);
            if (this.mScanResult == null) {
                dest.writeString(this.mName);
                dest.writeInt(this.mIconResource);
            }
        }

        public boolean equals(Object o) {
            if (!(o instanceof ListItem)) {
                return false;
            }
            ListItem li = (ListItem) o;
            if (this.mScanResult == null && li.mScanResult == null) {
                return TextUtils.equals(this.mName, li.mName);
            }
            if (this.mScanResult == null || li.mScanResult == null || !TextUtils.equals(this.mName, li.mName) || WifiSecurityUtil.getSecurity(this.mScanResult) != WifiSecurityUtil.getSecurity(li.mScanResult)) {
                return false;
            }
            return true;
        }
    }

    public static class PinnedListItem extends ListItem {
        public static final int FIRST = 1;
        public static final int LAST = 2;
        public static final int UNPINNED = 0;
        private int mPinnedPosition;
        private int mPinnedPriority;

        public PinnedListItem(String name, int iconResource, int pinnedPosition, int pinnedPriority) {
            super(name, iconResource);
            this.mPinnedPosition = pinnedPosition;
            this.mPinnedPriority = pinnedPriority;
        }

        public int getPinnedPosition() {
            return this.mPinnedPosition;
        }

        public int getPinnedPriority() {
            return this.mPinnedPriority;
        }
    }

    private static class ListItemViewHolder extends RecyclerView.ViewHolder implements FacetProvider {
        public ListItemViewHolder(View v) {
            super(v);
        }

        public void init(ListItem item, View.OnClickListener onClick, View.OnFocusChangeListener onFocusChange) {
            ((TextView) this.itemView.findViewById(R.id.list_item_text)).setText(item.getName());
            this.itemView.setOnClickListener(onClick);
            this.itemView.setOnFocusChangeListener(onFocusChange);
            int iconResource = item.getIconResource();
            ImageView icon = (ImageView) this.itemView.findViewById(R.id.list_item_icon);
            if (iconResource == 0) {
                icon.setVisibility(8);
                return;
            }
            icon.setVisibility(0);
            icon.setImageResource(iconResource);
            if (item.hasIconLevel()) {
                icon.setImageLevel(item.getIconLevel());
            }
        }

        public Object getFacet(Class facet) {
            if (!facet.equals(ItemAlignmentFacet.class)) {
                return null;
            }
            ItemAlignmentFacet.ItemAlignmentDef alignedDef = new ItemAlignmentFacet.ItemAlignmentDef();
            alignedDef.setItemAlignmentViewId(R.id.list_item_text);
            alignedDef.setAlignedToTextViewBaseline(false);
            alignedDef.setItemAlignmentOffset(0);
            alignedDef.setItemAlignmentOffsetWithPadding(true);
            alignedDef.setItemAlignmentOffsetPercent(50.0f);
            ItemAlignmentFacet f = new ItemAlignmentFacet();
            f.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[]{alignedDef});
            return f;
        }
    }

    private class VerticalListAdapter extends RecyclerView.Adapter {
        /* access modifiers changed from: private */
        public final ActionListener mActionListener;
        private SortedList mItems;

        public VerticalListAdapter(ActionListener actionListener, List<ListItem> choices) {
            this.mActionListener = actionListener;
            final ListItemComparator comparator = new ListItemComparator();
            this.mItems = new SortedList(ListItem.class, new SortedListAdapterCallback<ListItem>(this, SelectFromListWizardFragment.this) {
                public int compare(ListItem t0, ListItem t1) {
                    return comparator.compare(t0, t1);
                }

                public boolean areContentsTheSame(ListItem oldItem, ListItem newItem) {
                    return comparator.compare(oldItem, newItem) == 0;
                }

                public boolean areItemsTheSame(ListItem item1, ListItem item2) {
                    return item1.equals(item2);
                }
            });
            this.mItems.addAll(choices.toArray(new ListItem[0]), false);
        }

        private View.OnClickListener createClickListener(final ListItem item) {
            return new View.OnClickListener() {
                public void onClick(View v) {
                    if (v != null && v.getWindowToken() != null && VerticalListAdapter.this.mActionListener != null) {
                        VerticalListAdapter.this.mActionListener.onClick(item);
                    }
                }
            };
        }

        private View.OnFocusChangeListener createFocusListener(final ListItem item) {
            return new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (v != null && v.getWindowToken() != null && VerticalListAdapter.this.mActionListener != null && hasFocus) {
                        VerticalListAdapter.this.mActionListener.onFocus(item);
                    }
                }
            };
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListItemViewHolder(((LayoutInflater) parent.getContext().getSystemService("layout_inflater")).inflate(R.layout.setup_list_item, parent, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder baseHolder, int position) {
            if (position < this.mItems.size()) {
                ListItem item = (ListItem) this.mItems.get(position);
                ((ListItemViewHolder) baseHolder).init(item, createClickListener(item), createFocusListener(item));
            }
        }

        public SortedList<ListItem> getItems() {
            return this.mItems;
        }

        public int getItemCount() {
            return this.mItems.size();
        }

        public void updateItems(List<ListItem> inputItems) {
            TreeSet<ListItem> newItemSet = new TreeSet<>(new ListItemComparator());
            for (ListItem item : inputItems) {
                newItemSet.add(item);
            }
            ArrayList<ListItem> toRemove = new ArrayList<>();
            for (int j = 0; j < this.mItems.size(); j++) {
                ListItem oldItem = (ListItem) this.mItems.get(j);
                if (!newItemSet.contains(oldItem)) {
                    toRemove.add(oldItem);
                }
            }
            Iterator<ListItem> it = toRemove.iterator();
            while (it.hasNext()) {
                this.mItems.remove(it.next());
            }
            this.mItems.addAll(inputItems.toArray(new ListItem[0]), true);
        }
    }

    public static SelectFromListWizardFragment newInstance(String title, String description, ArrayList<ListItem> listElements, ListItem lastSelection) {
        SelectFromListWizardFragment fragment = new SelectFromListWizardFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_DESCRIPTION, description);
        args.putParcelableArrayList(EXTRA_LIST_ELEMENTS, listElements);
        args.putParcelable(EXTRA_LAST_SELECTION, lastSelection);
        fragment.setArguments(args);
        return fragment;
    }

    private void updateSelected(String lastSelectionName) {
        SortedList<ListItem> items = ((VerticalListAdapter) this.mListView.getAdapter()).getItems();
        int i = 0;
        while (true) {
            if (i >= items.size()) {
                break;
            } else if (TextUtils.equals(lastSelectionName, items.get(i).getName())) {
                this.mListView.setSelectedPosition(i);
                break;
            } else {
                i++;
            }
        }
        this.mLastSelectedName = lastSelectionName;
    }

    public void update(List<ListItem> listElements) {
        if (this.mSelectItemRunnable != null) {
            this.mHandler.removeCallbacks(this.mSelectItemRunnable);
        }
        this.mSelectItemRunnable = new Runnable(this.mLastSelectedName) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SelectFromListWizardFragment.lambda$update$0(SelectFromListWizardFragment.this, this.f$1);
            }
        };
        if (this.mOnListPreDrawListener != null) {
            this.mListView.getViewTreeObserver().removeOnPreDrawListener(this.mOnListPreDrawListener);
        }
        this.mOnListPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public final boolean onPreDraw() {
                return SelectFromListWizardFragment.lambda$update$1(SelectFromListWizardFragment.this);
            }
        };
        this.mListView.getViewTreeObserver().addOnPreDrawListener(this.mOnListPreDrawListener);
        ((VerticalListAdapter) this.mListView.getAdapter()).updateItems(listElements);
    }

    public static /* synthetic */ void lambda$update$0(SelectFromListWizardFragment selectFromListWizardFragment, String lastSelected) {
        selectFromListWizardFragment.updateSelected(lastSelected);
        if (selectFromListWizardFragment.mOnListPreDrawListener != null) {
            selectFromListWizardFragment.mListView.getViewTreeObserver().removeOnPreDrawListener(selectFromListWizardFragment.mOnListPreDrawListener);
            selectFromListWizardFragment.mOnListPreDrawListener = null;
        }
        selectFromListWizardFragment.mSelectItemRunnable = null;
    }

    public static /* synthetic */ boolean lambda$update$1(SelectFromListWizardFragment selectFromListWizardFragment) {
        selectFromListWizardFragment.mHandler.removeCallbacks(selectFromListWizardFragment.mSelectItemRunnable);
        selectFromListWizardFragment.mHandler.postDelayed(selectFromListWizardFragment.mSelectItemRunnable, 100);
        return true;
    }

    private static float getKeyLinePercent(Context context) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme);
        float percent = ta.getFloat(45, 40.0f);
        ta.recycle();
        return percent;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        LayoutInflater layoutInflater = inflater;
        Resources resources = getContext().getResources();
        this.mHandler = new Handler();
        this.mMainView = layoutInflater.inflate(R.layout.account_content_area, container, false);
        ViewGroup descriptionArea = (ViewGroup) this.mMainView.findViewById(R.id.description);
        View content = layoutInflater.inflate(R.layout.wifi_content, descriptionArea, false);
        descriptionArea.addView(content);
        ViewGroup actionArea = (ViewGroup) this.mMainView.findViewById(R.id.action);
        TextView titleText = (TextView) content.findViewById(R.id.guidance_title);
        TextView descriptionText = (TextView) content.findViewById(R.id.guidance_description);
        Bundle args = getArguments();
        String title = args.getString(EXTRA_TITLE);
        String description = args.getString(EXTRA_DESCRIPTION);
        boolean forceFocusable = AccessibilityHelper.forceFocusableViews(getActivity());
        if (title != null) {
            titleText.setText(title);
            titleText.setVisibility(0);
            if (forceFocusable) {
                titleText.setFocusable(true);
                titleText.setFocusableInTouchMode(true);
            }
        } else {
            titleText.setVisibility(8);
        }
        if (description != null) {
            descriptionText.setText(description);
            descriptionText.setVisibility(0);
            if (forceFocusable) {
                descriptionText.setFocusable(true);
                descriptionText.setFocusableInTouchMode(true);
            }
        } else {
            descriptionText.setVisibility(8);
        }
        ArrayList<ListItem> listItems = args.getParcelableArrayList(EXTRA_LIST_ELEMENTS);
        this.mListView = (VerticalGridView) layoutInflater.inflate(R.layout.setup_list_view, actionArea, false);
        align(this.mListView, getActivity());
        actionArea.addView(this.mListView);
        this.mListView.setAdapter(new VerticalListAdapter(new ActionListener() {
            public void onClick(ListItem item) {
                Activity a = SelectFromListWizardFragment.this.getActivity();
                if ((a instanceof Listener) && SelectFromListWizardFragment.this.isResumed()) {
                    ((Listener) a).onListSelectionComplete(item);
                }
            }

            public void onFocus(ListItem item) {
                Activity a = SelectFromListWizardFragment.this.getActivity();
                String unused = SelectFromListWizardFragment.this.mLastSelectedName = item.getName();
                if (a instanceof Listener) {
                    ((Listener) a).onListFocusChanged(item);
                }
            }
        }, listItems));
        ListItem lastSelection = (ListItem) args.getParcelable(EXTRA_LAST_SELECTION);
        if (lastSelection != null) {
            updateSelected(lastSelection.getName());
        }
        return this.mMainView;
    }

    private static void align(VerticalGridView listView, Activity activity) {
        Context context = listView.getContext();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        float keyLinePercent = getKeyLinePercent(context);
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        listView.setItemSpacing(activity.getResources().getDimensionPixelSize(R.dimen.setup_list_item_margin));
        listView.setWindowAlignment(2);
        listView.setWindowAlignmentOffset(0);
        listView.setWindowAlignmentOffsetPercent(keyLinePercent);
    }

    public void onPause() {
        super.onPause();
        if (this.mSelectItemRunnable != null) {
            this.mHandler.removeCallbacks(this.mSelectItemRunnable);
            this.mSelectItemRunnable = null;
        }
        if (this.mOnListPreDrawListener != null) {
            this.mListView.getViewTreeObserver().removeOnPreDrawListener(this.mOnListPreDrawListener);
            this.mOnListPreDrawListener = null;
        }
    }

    public void onResume() {
        super.onResume();
        this.mHandler.post(new Runnable() {
            public void run() {
                ((InputMethodManager) SelectFromListWizardFragment.this.getActivity().getSystemService("input_method")).hideSoftInputFromWindow(SelectFromListWizardFragment.this.mMainView.getApplicationWindowToken(), 0);
            }
        });
    }
}
