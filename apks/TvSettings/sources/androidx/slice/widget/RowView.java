package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceHints;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R;
import androidx.slice.widget.SliceView;
import com.android.tv.settings.dialog.old.BaseDialogFragment;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class RowView extends SliceChildView implements View.OnClickListener {
    private static final int MAX_END_ITEMS = 3;
    private static final String TAG = "RowView";
    private LinearLayout mContent;
    private View mDivider;
    private LinearLayout mEndContainer;
    private List<SliceAction> mHeaderActions;
    private int mIconSize = getContext().getResources().getDimensionPixelSize(R.dimen.abc_slice_icon_size);
    private int mImageSize = getContext().getResources().getDimensionPixelSize(R.dimen.abc_slice_small_image_size);
    private boolean mIsHeader;
    private boolean mIsSingleItem;
    private TextView mLastUpdatedText;
    private TextView mPrimaryText;
    private ProgressBar mRangeBar;
    private int mRangeHeight;
    private LinearLayout mRootView;
    private SliceActionImpl mRowAction;
    /* access modifiers changed from: private */
    public RowContent mRowContent;
    /* access modifiers changed from: private */
    public int mRowIndex;
    private TextView mSecondaryText;
    private View mSeeMoreView;
    private LinearLayout mStartContainer;
    private ArrayMap<SliceActionImpl, SliceActionView> mToggles = new ArrayMap<>();

    public RowView(Context context) {
        super(context);
        this.mRootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.abc_slice_small_template, this, false);
        addView(this.mRootView);
        this.mStartContainer = (LinearLayout) findViewById(R.id.icon_frame);
        this.mContent = (LinearLayout) findViewById(16908290);
        this.mPrimaryText = (TextView) findViewById(16908310);
        this.mSecondaryText = (TextView) findViewById(16908304);
        this.mLastUpdatedText = (TextView) findViewById(R.id.last_updated);
        this.mDivider = findViewById(R.id.divider);
        this.mEndContainer = (LinearLayout) findViewById(16908312);
        this.mRangeHeight = context.getResources().getDimensionPixelSize(R.dimen.abc_slice_row_range_height);
    }

    public void setSingleItem(boolean isSingleItem) {
        this.mIsSingleItem = isSingleItem;
    }

    public int getSmallHeight() {
        if (this.mRowContent == null || !this.mRowContent.isValid()) {
            return 0;
        }
        return this.mRowContent.getSmallHeight();
    }

    public int getActualHeight() {
        if (this.mIsSingleItem) {
            return getSmallHeight();
        }
        if (this.mRowContent == null || !this.mRowContent.isValid()) {
            return 0;
        }
        return this.mRowContent.getActualHeight();
    }

    private int getRowContentHeight() {
        int rowHeight;
        if (getMode() == 1 || this.mIsSingleItem) {
            rowHeight = getSmallHeight();
        } else {
            rowHeight = getActualHeight();
        }
        if (this.mRangeBar != null) {
            return rowHeight - this.mRangeHeight;
        }
        return rowHeight;
    }

    public void setTint(@ColorInt int tintColor) {
        super.setTint(tintColor);
        if (this.mRowContent != null) {
            populateViews();
        }
    }

    public void setSliceActions(List<SliceAction> actions) {
        this.mHeaderActions = actions;
        if (this.mRowContent != null) {
            populateViews();
        }
    }

    public void setShowLastUpdated(boolean showLastUpdated) {
        super.setShowLastUpdated(showLastUpdated);
        if (this.mRowContent != null) {
            populateViews();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight = getMode() == 1 ? getSmallHeight() : getActualHeight();
        int rowHeight = getRowContentHeight();
        if (rowHeight != 0) {
            this.mRootView.setVisibility(0);
            measureChild(this.mRootView, widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(rowHeight, 1073741824));
        } else {
            this.mRootView.setVisibility(8);
        }
        if (this.mRangeBar != null) {
            measureChild(this.mRangeBar, widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mRangeHeight, 1073741824));
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mRootView.layout(0, 0, this.mRootView.getMeasuredWidth(), getRowContentHeight());
        if (this.mRangeBar != null) {
            this.mRangeBar.layout(0, getRowContentHeight(), this.mRangeBar.getMeasuredWidth(), getRowContentHeight() + this.mRangeHeight);
        }
    }

    public void setSliceItem(SliceItem slice, boolean isHeader, int index, int rowCount, SliceView.OnSliceActionListener observer) {
        setSliceActionListener(observer);
        this.mRowIndex = index;
        this.mIsHeader = ListContent.isValidHeader(slice);
        this.mHeaderActions = null;
        this.mRowContent = new RowContent(getContext(), slice, this.mIsHeader);
        populateViews();
    }

    private void populateViews() {
        SliceItem subtitleItem;
        SliceItem endItem;
        resetView();
        if (this.mRowContent.getLayoutDirItem() != null) {
            setLayoutDirection(this.mRowContent.getLayoutDirItem().getInt());
        }
        if (this.mRowContent.isDefaultSeeMore()) {
            showSeeMore();
            return;
        }
        CharSequence contentDescr = this.mRowContent.getContentDescription();
        if (contentDescr != null) {
            this.mContent.setContentDescription(contentDescr);
        }
        SliceItem startItem = this.mRowContent.getStartItem();
        boolean z = false;
        boolean showStart = startItem != null && this.mRowIndex > 0;
        if (showStart) {
            showStart = addItem(startItem, this.mTintColor, true);
        }
        this.mStartContainer.setVisibility(showStart ? 0 : 8);
        SliceItem titleItem = this.mRowContent.getTitleItem();
        if (titleItem != null) {
            this.mPrimaryText.setText(titleItem.getText());
        }
        this.mPrimaryText.setTextSize(0, (float) (this.mIsHeader ? this.mHeaderTitleSize : this.mTitleSize));
        this.mPrimaryText.setTextColor(this.mTitleColor);
        this.mPrimaryText.setVisibility(titleItem != null ? 0 : 8);
        if (getMode() == 1) {
            subtitleItem = this.mRowContent.getSummaryItem();
        } else {
            subtitleItem = this.mRowContent.getSubtitleItem();
        }
        addSubtitle(subtitleItem);
        SliceItem primaryAction = this.mRowContent.getPrimaryAction();
        if (!(primaryAction == null || primaryAction == startItem)) {
            this.mRowAction = new SliceActionImpl(primaryAction);
            if (this.mRowAction.isToggle()) {
                addAction(this.mRowAction, this.mTintColor, this.mEndContainer, false);
                setViewClickable(this.mRootView, true);
                return;
            }
        }
        SliceItem range = this.mRowContent.getRange();
        if (range != null) {
            if (this.mRowAction != null) {
                setViewClickable(this.mRootView, true);
            }
            addRange(range);
            return;
        }
        List endItems = this.mRowContent.getEndItems();
        if (this.mHeaderActions != null && this.mHeaderActions.size() > 0) {
            endItems = this.mHeaderActions;
        }
        boolean firstItemIsADefaultToggle = false;
        SliceItem endAction = null;
        int endItemCount = 0;
        int i = 0;
        while (i < endItems.size()) {
            if (endItems.get(i) instanceof SliceItem) {
                endItem = (SliceItem) endItems.get(i);
            } else {
                endItem = ((SliceActionImpl) endItems.get(i)).getSliceItem();
            }
            if (endItemCount < 3 && addItem(endItem, this.mTintColor, z)) {
                if (endAction == null && SliceQuery.find(endItem, BaseDialogFragment.TAG_ACTION) != null) {
                    endAction = endItem;
                }
                endItemCount++;
                if (endItemCount == 1) {
                    firstItemIsADefaultToggle = !this.mToggles.isEmpty() && SliceQuery.find(endItem.getSlice(), "image") == null;
                }
            }
            i++;
            z = false;
        }
        this.mDivider.setVisibility((this.mRowAction == null || !firstItemIsADefaultToggle) ? 8 : 0);
        boolean hasStartAction = (startItem == null || SliceQuery.find(startItem, BaseDialogFragment.TAG_ACTION) == null) ? false : true;
        boolean hasEndItemAction = endAction != null;
        if (this.mRowAction != null) {
            setViewClickable((hasEndItemAction || hasStartAction) ? this.mContent : this.mRootView, true);
        } else if (hasEndItemAction == hasStartAction) {
        } else {
            if (endItemCount == 1 || hasStartAction) {
                if (!this.mToggles.isEmpty()) {
                    this.mRowAction = this.mToggles.keySet().iterator().next();
                } else {
                    this.mRowAction = new SliceActionImpl(endAction != null ? endAction : startItem);
                }
                setViewClickable(this.mRootView, true);
            }
        }
    }

    private void addSubtitle(SliceItem subtitleItem) {
        CharSequence subtitleTimeString = null;
        boolean subtitleExists = true;
        int i = 0;
        if (this.mShowLastUpdated && this.mLastUpdated != -1) {
            subtitleTimeString = getResources().getString(R.string.abc_slice_updated, new Object[]{SliceViewUtil.getRelativeTimeString(this.mLastUpdated)});
        }
        CharSequence subtitle = subtitleItem != null ? subtitleItem.getText() : null;
        if (TextUtils.isEmpty(subtitle) && (subtitleItem == null || !subtitleItem.hasHint("partial"))) {
            subtitleExists = false;
        }
        if (subtitleExists) {
            this.mSecondaryText.setText(subtitle);
            this.mSecondaryText.setTextSize(0, (float) (this.mIsHeader ? this.mHeaderSubtitleSize : this.mSubtitleSize));
            this.mSecondaryText.setTextColor(this.mSubtitleColor);
            this.mSecondaryText.setPadding(0, this.mIsHeader ? this.mVerticalHeaderTextPadding : this.mVerticalTextPadding, 0, 0);
        }
        if (subtitleTimeString != null) {
            if (!TextUtils.isEmpty(subtitle)) {
                subtitleTimeString = " · " + subtitleTimeString;
            }
            SpannableString sp = new SpannableString(subtitleTimeString);
            sp.setSpan(new StyleSpan(2), 0, subtitleTimeString.length(), 0);
            this.mLastUpdatedText.setText(sp);
            this.mLastUpdatedText.setTextSize(0, (float) (this.mIsHeader ? this.mHeaderSubtitleSize : this.mSubtitleSize));
            this.mLastUpdatedText.setTextColor(this.mSubtitleColor);
        }
        this.mLastUpdatedText.setVisibility(TextUtils.isEmpty(subtitleTimeString) ? 8 : 0);
        TextView textView = this.mSecondaryText;
        if (!subtitleExists) {
            i = 8;
        }
        textView.setVisibility(i);
        this.mSecondaryText.requestLayout();
        this.mLastUpdatedText.requestLayout();
    }

    private void addRange(final SliceItem range) {
        ProgressBar progressBar;
        Drawable d;
        boolean isSeekBar = BaseDialogFragment.TAG_ACTION.equals(range.getFormat());
        if (isSeekBar) {
            progressBar = new SeekBar(getContext());
        } else {
            progressBar = new ProgressBar(getContext(), (AttributeSet) null, 16842872);
        }
        Drawable progressDrawable = DrawableCompat.wrap(progressBar.getProgressDrawable());
        if (!(this.mTintColor == -1 || progressDrawable == null)) {
            DrawableCompat.setTint(progressDrawable, this.mTintColor);
            progressBar.setProgressDrawable(progressDrawable);
        }
        SliceItem min = SliceQuery.findSubtype(range, "int", SliceHints.SUBTYPE_MIN);
        int minValue = 0;
        if (min != null) {
            minValue = min.getInt();
        }
        SliceItem max = SliceQuery.findSubtype(range, "int", "max");
        if (max != null) {
            progressBar.setMax(max.getInt() - minValue);
        }
        SliceItem progress = SliceQuery.findSubtype(range, "int", SaveValue.GLOBAL_VALUE_VALUE);
        if (progress != null) {
            progressBar.setProgress(progress.getInt() - minValue);
        }
        progressBar.setVisibility(0);
        addView(progressBar);
        this.mRangeBar = progressBar;
        if (isSeekBar) {
            SliceItem thumb = this.mRowContent.getInputRangeThumb();
            SeekBar seekBar = (SeekBar) this.mRangeBar;
            if (!(thumb == null || (d = thumb.getIcon().loadDrawable(getContext())) == null)) {
                seekBar.setThumb(d);
            }
            Drawable thumbDrawable = DrawableCompat.wrap(seekBar.getThumb());
            if (!(this.mTintColor == -1 || thumbDrawable == null)) {
                DrawableCompat.setTint(thumbDrawable, this.mTintColor);
                seekBar.setThumb(thumbDrawable);
            }
            final int finalMinValue = minValue;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        range.fireAction(RowView.this.getContext(), new Intent().putExtra("android.app.slice.extra.RANGE_VALUE", progress + finalMinValue));
                    } catch (PendingIntent.CanceledException e) {
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void addAction(SliceActionImpl actionContent, int color, ViewGroup container, boolean isStart) {
        SliceActionView sav = new SliceActionView(getContext());
        container.addView(sav);
        int isToggle = actionContent.isToggle();
        EventInfo info = new EventInfo(getMode(), (int) (isToggle ^ 1), isToggle != 0 ? 3 : 0, this.mRowIndex);
        if (isStart) {
            info.setPosition(0, 0, 1);
        }
        sav.setAction(actionContent, info, this.mObserver, color);
        if (isToggle != 0) {
            this.mToggles.put(actionContent, sav);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: android.widget.ImageView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: android.widget.TextView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean addItem(androidx.slice.SliceItem r12, int r13, boolean r14) {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            if (r14 == 0) goto L_0x0008
            android.widget.LinearLayout r3 = r11.mStartContainer
            goto L_0x000a
        L_0x0008:
            android.widget.LinearLayout r3 = r11.mEndContainer
        L_0x000a:
            java.lang.String r4 = "slice"
            java.lang.String r5 = r12.getFormat()
            boolean r4 = r4.equals(r5)
            r5 = 1
            r6 = 0
            if (r4 != 0) goto L_0x0024
            java.lang.String r4 = "action"
            java.lang.String r7 = r12.getFormat()
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x0044
        L_0x0024:
            java.lang.String r4 = "shortcut"
            boolean r4 = r12.hasHint(r4)
            if (r4 == 0) goto L_0x0035
            androidx.slice.core.SliceActionImpl r4 = new androidx.slice.core.SliceActionImpl
            r4.<init>(r12)
            r11.addAction(r4, r13, r3, r14)
            return r5
        L_0x0035:
            androidx.slice.Slice r4 = r12.getSlice()
            java.util.List r4 = r4.getItems()
            java.lang.Object r4 = r4.get(r6)
            r12 = r4
            androidx.slice.SliceItem r12 = (androidx.slice.SliceItem) r12
        L_0x0044:
            java.lang.String r4 = "image"
            java.lang.String r7 = r12.getFormat()
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x005c
            android.support.v4.graphics.drawable.IconCompat r0 = r12.getIcon()
            java.lang.String r4 = "no_tint"
            boolean r4 = r12.hasHint(r4)
            r1 = r4
            goto L_0x0069
        L_0x005c:
            java.lang.String r4 = "long"
            java.lang.String r7 = r12.getFormat()
            boolean r4 = r4.equals(r7)
            if (r4 == 0) goto L_0x0069
            r2 = r12
        L_0x0069:
            r4 = 0
            if (r0 == 0) goto L_0x00ae
            if (r1 != 0) goto L_0x0070
            r7 = r5
            goto L_0x0071
        L_0x0070:
            r7 = r6
        L_0x0071:
            android.widget.ImageView r8 = new android.widget.ImageView
            android.content.Context r9 = r11.getContext()
            r8.<init>(r9)
            android.content.Context r9 = r11.getContext()
            android.graphics.drawable.Drawable r9 = r0.loadDrawable(r9)
            r8.setImageDrawable(r9)
            if (r7 == 0) goto L_0x008d
            r9 = -1
            if (r13 == r9) goto L_0x008d
            r8.setColorFilter(r13)
        L_0x008d:
            r3.addView(r8)
            android.view.ViewGroup$LayoutParams r9 = r8.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r9 = (android.widget.LinearLayout.LayoutParams) r9
            int r10 = r11.mImageSize
            r9.width = r10
            int r10 = r11.mImageSize
            r9.height = r10
            r8.setLayoutParams(r9)
            if (r7 == 0) goto L_0x00a8
            int r10 = r11.mIconSize
            int r10 = r10 / 2
            goto L_0x00a9
        L_0x00a8:
            r10 = r6
        L_0x00a9:
            r8.setPadding(r10, r10, r10, r10)
            r4 = r8
            goto L_0x00d3
        L_0x00ae:
            if (r2 == 0) goto L_0x00d3
            android.widget.TextView r7 = new android.widget.TextView
            android.content.Context r8 = r11.getContext()
            r7.<init>(r8)
            long r8 = r12.getTimestamp()
            java.lang.CharSequence r8 = androidx.slice.widget.SliceViewUtil.getRelativeTimeString(r8)
            r7.setText(r8)
            int r8 = r11.mSubtitleSize
            float r8 = (float) r8
            r7.setTextSize(r6, r8)
            int r8 = r11.mSubtitleColor
            r7.setTextColor(r8)
            r3.addView(r7)
            r4 = r7
        L_0x00d3:
            if (r4 == 0) goto L_0x00d6
            goto L_0x00d7
        L_0x00d6:
            r5 = r6
        L_0x00d7:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.addItem(androidx.slice.SliceItem, int, boolean):boolean");
    }

    private void showSeeMore() {
        Button b = (Button) LayoutInflater.from(getContext()).inflate(R.layout.abc_slice_row_show_more, this, false);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (RowView.this.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(RowView.this.getMode(), 4, 0, RowView.this.mRowIndex), RowView.this.mRowContent.getSlice());
                    }
                    RowView.this.mRowContent.getSlice().fireAction((Context) null, (Intent) null);
                } catch (PendingIntent.CanceledException e) {
                    Log.e(RowView.TAG, "PendingIntent for slice cannot be sent", e);
                }
            }
        });
        if (this.mTintColor != -1) {
            b.setTextColor(this.mTintColor);
        }
        this.mSeeMoreView = b;
        this.mRootView.addView(this.mSeeMoreView);
    }

    public void onClick(View view) {
        if (this.mRowAction != null && this.mRowAction.getActionItem() != null) {
            if (!this.mRowAction.isToggle() || (view instanceof SliceActionView)) {
                try {
                    this.mRowAction.getActionItem().fireAction((Context) null, (Intent) null);
                    if (this.mObserver != null) {
                        this.mObserver.onSliceAction(new EventInfo(getMode(), 3, 0, this.mRowIndex), this.mRowAction.getSliceItem());
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.e(TAG, "PendingIntent for slice cannot be sent", e);
                }
            } else {
                SliceActionView sav = this.mToggles.get(this.mRowAction);
                if (sav != null) {
                    sav.toggle();
                }
            }
        }
    }

    private void setViewClickable(View layout, boolean isClickable) {
        Drawable drawable = null;
        layout.setOnClickListener(isClickable ? this : null);
        if (isClickable) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        layout.setBackground(drawable);
        layout.setClickable(isClickable);
    }

    public void resetView() {
        this.mRootView.setVisibility(0);
        setLayoutDirection(2);
        setViewClickable(this.mRootView, false);
        setViewClickable(this.mContent, false);
        this.mStartContainer.removeAllViews();
        this.mEndContainer.removeAllViews();
        this.mPrimaryText.setText((CharSequence) null);
        this.mSecondaryText.setText((CharSequence) null);
        this.mLastUpdatedText.setText((CharSequence) null);
        this.mLastUpdatedText.setVisibility(8);
        this.mToggles.clear();
        this.mRowAction = null;
        this.mDivider.setVisibility(8);
        if (this.mRangeBar != null) {
            removeView(this.mRangeBar);
            this.mRangeBar = null;
        }
        if (this.mSeeMoreView != null) {
            this.mRootView.removeView(this.mSeeMoreView);
            this.mSeeMoreView = null;
        }
    }
}
