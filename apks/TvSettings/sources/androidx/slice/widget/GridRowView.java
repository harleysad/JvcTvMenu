package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.view.R;
import androidx.slice.widget.GridContent;
import androidx.slice.widget.SliceView;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class GridRowView extends SliceChildView implements View.OnClickListener {
    private static final int MAX_CELL_IMAGES = 1;
    private static final int MAX_CELL_TEXT = 2;
    private static final int MAX_CELL_TEXT_SMALL = 1;
    private static final String TAG = "GridView";
    private static final int TEXT_LAYOUT = R.layout.abc_slice_secondary_text;
    private static final int TITLE_TEXT_LAYOUT = R.layout.abc_slice_title;
    private GridContent mGridContent;
    private int mGutter;
    private int mIconSize;
    private int mLargeImageHeight;
    /* access modifiers changed from: private */
    public boolean mMaxCellUpdateScheduled;
    /* access modifiers changed from: private */
    public int mMaxCells;
    private ViewTreeObserver.OnPreDrawListener mMaxCellsUpdater;
    private int mRowCount;
    private int mRowIndex;
    private int mSmallImageMinWidth;
    private int mSmallImageSize;
    private int mTextPadding;
    private LinearLayout mViewContainer;

    public GridRowView(Context context) {
        this(context, (AttributeSet) null);
    }

    public GridRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxCells = -1;
        this.mMaxCellsUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int unused = GridRowView.this.mMaxCells = GridRowView.this.getMaxCells();
                GridRowView.this.populateViews();
                GridRowView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                boolean unused2 = GridRowView.this.mMaxCellUpdateScheduled = false;
                return true;
            }
        };
        Resources res = getContext().getResources();
        this.mViewContainer = new LinearLayout(getContext());
        this.mViewContainer.setOrientation(0);
        addView(this.mViewContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mViewContainer.setGravity(16);
        this.mIconSize = res.getDimensionPixelSize(R.dimen.abc_slice_icon_size);
        this.mSmallImageSize = res.getDimensionPixelSize(R.dimen.abc_slice_small_image_size);
        this.mLargeImageHeight = res.getDimensionPixelSize(R.dimen.abc_slice_grid_image_only_height);
        this.mSmallImageMinWidth = res.getDimensionPixelSize(R.dimen.abc_slice_grid_image_min_width);
        this.mGutter = res.getDimensionPixelSize(R.dimen.abc_slice_grid_gutter);
        this.mTextPadding = res.getDimensionPixelSize(R.dimen.abc_slice_grid_text_padding);
    }

    public int getSmallHeight() {
        if (this.mGridContent == null) {
            return 0;
        }
        return this.mGridContent.getSmallHeight() + getExtraTopPadding() + getExtraBottomPadding();
    }

    public int getActualHeight() {
        if (this.mGridContent == null) {
            return 0;
        }
        return this.mGridContent.getActualHeight() + getExtraTopPadding() + getExtraBottomPadding();
    }

    private int getExtraTopPadding() {
        if (this.mGridContent == null || !this.mGridContent.isAllImages() || this.mRowIndex != 0) {
            return 0;
        }
        return this.mGridTopPadding;
    }

    private int getExtraBottomPadding() {
        if (this.mGridContent == null || !this.mGridContent.isAllImages()) {
            return 0;
        }
        if (this.mRowIndex == this.mRowCount - 1 || getMode() == 1) {
            return this.mGridBottomPadding;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getMode() == 1 ? getSmallHeight() : getActualHeight();
        int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
        this.mViewContainer.getLayoutParams().height = height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2);
    }

    public void setTint(@ColorInt int tintColor) {
        super.setTint(tintColor);
        if (this.mGridContent != null) {
            resetView();
            populateViews();
        }
    }

    public void setSliceItem(SliceItem slice, boolean isHeader, int rowIndex, int rowCount, SliceView.OnSliceActionListener observer) {
        resetView();
        setSliceActionListener(observer);
        this.mRowIndex = rowIndex;
        this.mRowCount = rowCount;
        this.mGridContent = new GridContent(getContext(), slice);
        if (!scheduleMaxCellsUpdate()) {
            populateViews();
        }
        this.mViewContainer.setPadding(0, getExtraTopPadding(), 0, getExtraBottomPadding());
    }

    private boolean scheduleMaxCellsUpdate() {
        if (this.mGridContent == null || !this.mGridContent.isValid()) {
            return true;
        }
        if (getWidth() == 0) {
            this.mMaxCellUpdateScheduled = true;
            getViewTreeObserver().addOnPreDrawListener(this.mMaxCellsUpdater);
            return true;
        }
        this.mMaxCells = getMaxCells();
        return false;
    }

    /* access modifiers changed from: private */
    public int getMaxCells() {
        if (this.mGridContent == null || !this.mGridContent.isValid() || getWidth() == 0) {
            return -1;
        }
        if (this.mGridContent.getGridContent().size() <= 1) {
            return 1;
        }
        return getWidth() / (this.mGutter + (this.mGridContent.getLargestImageMode() == 2 ? this.mLargeImageHeight : this.mSmallImageMinWidth));
    }

    /* access modifiers changed from: private */
    public void populateViews() {
        if (this.mGridContent == null || !this.mGridContent.isValid()) {
            resetView();
        } else if (!scheduleMaxCellsUpdate()) {
            if (this.mGridContent.getLayoutDirItem() != null) {
                setLayoutDirection(this.mGridContent.getLayoutDirItem().getInt());
            }
            boolean hasSeeMore = true;
            if (this.mGridContent.getContentIntent() != null) {
                this.mViewContainer.setTag(new Pair<>(this.mGridContent.getContentIntent(), new EventInfo(getMode(), 3, 1, this.mRowIndex)));
                makeClickable(this.mViewContainer, true);
            }
            CharSequence contentDescr = this.mGridContent.getContentDescription();
            if (contentDescr != null) {
                this.mViewContainer.setContentDescription(contentDescr);
            }
            ArrayList<GridContent.CellContent> cells = this.mGridContent.getGridContent();
            if (this.mGridContent.getLargestImageMode() == 2) {
                this.mViewContainer.setGravity(48);
            } else {
                this.mViewContainer.setGravity(16);
            }
            int maxCells = this.mMaxCells;
            int i = 0;
            if (this.mGridContent.getSeeMoreItem() == null) {
                hasSeeMore = false;
            }
            while (true) {
                int i2 = i;
                if (i2 >= cells.size()) {
                    return;
                }
                if (this.mViewContainer.getChildCount() < maxCells) {
                    addCell(cells.get(i2), i2, Math.min(cells.size(), maxCells));
                    i = i2 + 1;
                } else if (hasSeeMore) {
                    addSeeMoreCount(cells.size() - maxCells);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    private void addSeeMoreCount(int numExtra) {
        TextView extraText;
        ViewGroup seeMoreView;
        View last = this.mViewContainer.getChildAt(this.mViewContainer.getChildCount() - 1);
        this.mViewContainer.removeView(last);
        SliceItem seeMoreItem = this.mGridContent.getSeeMoreItem();
        int index = this.mViewContainer.getChildCount();
        int total = this.mMaxCells;
        if ((SliceProviderCompat.EXTRA_SLICE.equals(seeMoreItem.getFormat()) || BaseDialogFragment.TAG_ACTION.equals(seeMoreItem.getFormat())) && seeMoreItem.getSlice().getItems().size() > 0) {
            addCell(new GridContent.CellContent(seeMoreItem), index, total);
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (this.mGridContent.isAllImages()) {
            seeMoreView = (FrameLayout) inflater.inflate(R.layout.abc_slice_grid_see_more_overlay, this.mViewContainer, false);
            seeMoreView.addView(last, 0, new FrameLayout.LayoutParams(-1, -1));
            extraText = (TextView) seeMoreView.findViewById(R.id.text_see_more_count);
        } else {
            seeMoreView = (LinearLayout) inflater.inflate(R.layout.abc_slice_grid_see_more, this.mViewContainer, false);
            extraText = (TextView) seeMoreView.findViewById(R.id.text_see_more_count);
            TextView moreText = (TextView) seeMoreView.findViewById(R.id.text_see_more);
            moreText.setTextSize(0, (float) this.mGridTitleSize);
            moreText.setTextColor(this.mTitleColor);
        }
        this.mViewContainer.addView(seeMoreView, new LinearLayout.LayoutParams(0, -1, 1.0f));
        extraText.setText(getResources().getString(R.string.abc_slice_more_content, new Object[]{Integer.valueOf(numExtra)}));
        EventInfo info = new EventInfo(getMode(), 4, 1, this.mRowIndex);
        info.setPosition(2, index, total);
        seeMoreView.setTag(new Pair<>(seeMoreItem, info));
        makeClickable(seeMoreView, true);
    }

    private void addCell(GridContent.CellContent cell, int index, int total) {
        int i;
        int textCount;
        int imageCount;
        SliceItem item;
        int i2 = index;
        int i3 = total;
        int maxCellText = getMode() == 1 ? 1 : 2;
        LinearLayout cellContainer = new LinearLayout(getContext());
        cellContainer.setOrientation(1);
        cellContainer.setGravity(1);
        ArrayList<SliceItem> cellItems = cell.getCellItems();
        SliceItem contentIntentItem = cell.getContentIntent();
        boolean isSingleItem = cellItems.size() == 1;
        List<SliceItem> textItems = null;
        if (!isSingleItem && getMode() == 1) {
            textItems = new ArrayList<>();
            Iterator<SliceItem> it = cellItems.iterator();
            while (it.hasNext()) {
                SliceItem cellItem = it.next();
                if ("text".equals(cellItem.getFormat())) {
                    textItems.add(cellItem);
                }
            }
            Iterator<SliceItem> iterator = textItems.iterator();
            while (textItems.size() > 1) {
                if (!iterator.next().hasAnyHints("title", "large")) {
                    iterator.remove();
                }
            }
        }
        List<SliceItem> textItems2 = textItems;
        int textCount2 = 0;
        int imageCount2 = 0;
        boolean added = false;
        SliceItem prevItem = null;
        int textCount3 = 0;
        while (true) {
            int i4 = textCount3;
            if (i4 >= cellItems.size()) {
                break;
            }
            SliceItem item2 = cellItems.get(i4);
            String itemFormat = item2.getFormat();
            int padding = determinePadding(prevItem);
            if (textCount2 >= maxCellText) {
                item = item2;
                i = i4;
                imageCount = imageCount2;
                textCount = textCount2;
            } else if (!"text".equals(itemFormat) && !"long".equals(itemFormat)) {
                String str = itemFormat;
                item = item2;
                i = i4;
                imageCount = imageCount2;
                textCount = textCount2;
            } else if (textItems2 == null || textItems2.contains(item2)) {
                String str2 = itemFormat;
                SliceItem item3 = item2;
                i = i4;
                imageCount = imageCount2;
                textCount = textCount2;
                if (addItem(item2, this.mTintColor, cellContainer, padding, isSingleItem)) {
                    textCount2 = textCount + 1;
                    prevItem = item3;
                    added = true;
                    imageCount2 = imageCount;
                    textCount3 = i + 1;
                }
                imageCount2 = imageCount;
                textCount2 = textCount;
                textCount3 = i + 1;
            } else {
                i = i4;
                imageCount = imageCount2;
                textCount = textCount2;
                imageCount2 = imageCount;
                textCount2 = textCount;
                textCount3 = i + 1;
            }
            if (imageCount < 1) {
                SliceItem item4 = item;
                if ("image".equals(item4.getFormat())) {
                    SliceItem item5 = item4;
                    if (addItem(item4, this.mTintColor, cellContainer, 0, isSingleItem)) {
                        imageCount2 = imageCount + 1;
                        prevItem = item5;
                        added = true;
                        textCount2 = textCount;
                        textCount3 = i + 1;
                    }
                }
            }
            imageCount2 = imageCount;
            textCount2 = textCount;
            textCount3 = i + 1;
        }
        int i5 = imageCount2;
        int i6 = textCount2;
        if (added) {
            CharSequence contentDescr = cell.getContentDescription();
            if (contentDescr != null) {
                cellContainer.setContentDescription(contentDescr);
            }
            this.mViewContainer.addView(cellContainer, new LinearLayout.LayoutParams(0, -2, 1.0f));
            if (i2 != i3 - 1) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) cellContainer.getLayoutParams();
                lp.setMarginEnd(this.mGutter);
                cellContainer.setLayoutParams(lp);
            }
            if (contentIntentItem != null) {
                EventInfo info = new EventInfo(getMode(), 1, 1, this.mRowIndex);
                info.setPosition(2, i2, i3);
                cellContainer.setTag(new Pair<>(contentIntentItem, info));
                makeClickable(cellContainer, true);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: android.widget.ImageView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: android.widget.TextView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean addItem(androidx.slice.SliceItem r10, int r11, android.view.ViewGroup r12, int r13, boolean r14) {
        /*
            r9 = this;
            java.lang.String r0 = r10.getFormat()
            r1 = 0
            java.lang.String r2 = "text"
            boolean r2 = r2.equals(r0)
            r3 = 1
            r4 = 0
            if (r2 != 0) goto L_0x0086
            java.lang.String r2 = "long"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0019
            goto L_0x0086
        L_0x0019:
            java.lang.String r2 = "image"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x00de
            android.widget.ImageView r2 = new android.widget.ImageView
            android.content.Context r5 = r9.getContext()
            r2.<init>(r5)
            android.support.v4.graphics.drawable.IconCompat r5 = r10.getIcon()
            android.content.Context r6 = r9.getContext()
            android.graphics.drawable.Drawable r5 = r5.loadDrawable(r6)
            r2.setImageDrawable(r5)
            java.lang.String r5 = "large"
            boolean r5 = r10.hasHint(r5)
            r6 = -1
            if (r5 == 0) goto L_0x0056
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER_CROP
            r2.setScaleType(r5)
            if (r14 == 0) goto L_0x004b
            r5 = r6
            goto L_0x004d
        L_0x004b:
            int r5 = r9.mLargeImageHeight
        L_0x004d:
            android.widget.LinearLayout$LayoutParams r7 = new android.widget.LinearLayout$LayoutParams
            r7.<init>(r6, r5)
            r5 = r7
            r8 = r5
            goto L_0x0073
        L_0x0056:
            java.lang.String r5 = "no_tint"
            boolean r5 = r10.hasHint(r5)
            r5 = r5 ^ r3
            if (r5 == 0) goto L_0x0062
            int r7 = r9.mIconSize
            goto L_0x0064
        L_0x0062:
            int r7 = r9.mSmallImageSize
        L_0x0064:
            if (r5 == 0) goto L_0x0069
            android.widget.ImageView$ScaleType r8 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            goto L_0x006b
        L_0x0069:
            android.widget.ImageView$ScaleType r8 = android.widget.ImageView.ScaleType.CENTER_CROP
        L_0x006b:
            r2.setScaleType(r8)
            android.widget.LinearLayout$LayoutParams r8 = new android.widget.LinearLayout$LayoutParams
            r8.<init>(r7, r7)
        L_0x0073:
            r5 = r8
            if (r11 == r6) goto L_0x0081
            java.lang.String r6 = "no_tint"
            boolean r6 = r10.hasHint(r6)
            if (r6 != 0) goto L_0x0081
            r2.setColorFilter(r11)
        L_0x0081:
            r12.addView(r2, r5)
            r1 = r2
            goto L_0x00de
        L_0x0086:
            java.lang.String r2 = "large"
            java.lang.String r5 = "title"
            java.lang.String[] r2 = new java.lang.String[]{r2, r5}
            boolean r2 = androidx.slice.core.SliceQuery.hasAnyHints(r10, r2)
            android.content.Context r5 = r9.getContext()
            android.view.LayoutInflater r5 = android.view.LayoutInflater.from(r5)
            if (r2 == 0) goto L_0x009f
            int r6 = TITLE_TEXT_LAYOUT
            goto L_0x00a1
        L_0x009f:
            int r6 = TEXT_LAYOUT
        L_0x00a1:
            r7 = 0
            android.view.View r5 = r5.inflate(r6, r7)
            android.widget.TextView r5 = (android.widget.TextView) r5
            if (r2 == 0) goto L_0x00ae
            int r6 = r9.mGridTitleSize
        L_0x00ac:
            float r6 = (float) r6
            goto L_0x00b1
        L_0x00ae:
            int r6 = r9.mGridSubtitleSize
            goto L_0x00ac
        L_0x00b1:
            r5.setTextSize(r4, r6)
            if (r2 == 0) goto L_0x00b9
            int r6 = r9.mTitleColor
            goto L_0x00bb
        L_0x00b9:
            int r6 = r9.mSubtitleColor
        L_0x00bb:
            r5.setTextColor(r6)
            java.lang.String r6 = "long"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x00cf
            long r6 = r10.getTimestamp()
            java.lang.CharSequence r6 = androidx.slice.widget.SliceViewUtil.getRelativeTimeString(r6)
            goto L_0x00d3
        L_0x00cf:
            java.lang.CharSequence r6 = r10.getText()
        L_0x00d3:
            r5.setText(r6)
            r12.addView(r5)
            r5.setPadding(r4, r13, r4, r4)
            r1 = r5
        L_0x00de:
            if (r1 == 0) goto L_0x00e1
            goto L_0x00e2
        L_0x00e1:
            r3 = r4
        L_0x00e2:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.GridRowView.addItem(androidx.slice.SliceItem, int, android.view.ViewGroup, int, boolean):boolean");
    }

    private int determinePadding(SliceItem prevItem) {
        if (prevItem == null) {
            return 0;
        }
        if ("image".equals(prevItem.getFormat())) {
            return this.mTextPadding;
        }
        if ("text".equals(prevItem.getFormat()) || "long".equals(prevItem.getFormat())) {
            return this.mVerticalGridTextPadding;
        }
        return 0;
    }

    private void makeClickable(View layout, boolean isClickable) {
        Drawable drawable = null;
        layout.setOnClickListener(isClickable ? this : null);
        if (isClickable) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        layout.setBackground(drawable);
        layout.setClickable(isClickable);
    }

    public void onClick(View view) {
        Pair<SliceItem, EventInfo> tagItem = (Pair) view.getTag();
        SliceItem actionItem = (SliceItem) tagItem.first;
        EventInfo info = (EventInfo) tagItem.second;
        if (actionItem != null && BaseDialogFragment.TAG_ACTION.equals(actionItem.getFormat())) {
            try {
                actionItem.fireAction((Context) null, (Intent) null);
                if (this.mObserver != null) {
                    this.mObserver.onSliceAction(info, actionItem);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    public void resetView() {
        if (this.mMaxCellUpdateScheduled) {
            this.mMaxCellUpdateScheduled = false;
            getViewTreeObserver().removeOnPreDrawListener(this.mMaxCellsUpdater);
        }
        this.mViewContainer.removeAllViews();
        setLayoutDirection(2);
        makeClickable(this.mViewContainer, false);
    }
}
