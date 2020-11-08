package androidx.slice.widget;

import android.app.RemoteInput;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.slice.SliceItem;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class ActionRow extends FrameLayout {
    private static final int MAX_ACTIONS = 5;
    private static final String TAG = "ActionRow";
    private final LinearLayout mActionsGroup;
    private int mColor = ViewCompat.MEASURED_STATE_MASK;
    private final boolean mFullActions;
    private final int mIconPadding;
    private final int mSize;

    public ActionRow(Context context, boolean fullActions) {
        super(context);
        this.mFullActions = fullActions;
        this.mSize = (int) TypedValue.applyDimension(1, 48.0f, context.getResources().getDisplayMetrics());
        this.mIconPadding = (int) TypedValue.applyDimension(1, 12.0f, context.getResources().getDisplayMetrics());
        this.mActionsGroup = new LinearLayout(context);
        this.mActionsGroup.setOrientation(0);
        this.mActionsGroup.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        addView(this.mActionsGroup);
    }

    private void setColor(int color) {
        this.mColor = color;
        for (int i = 0; i < this.mActionsGroup.getChildCount(); i++) {
            View view = this.mActionsGroup.getChildAt(i);
            if (((Integer) view.getTag()).intValue() == 0) {
                ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(this.mColor));
            }
        }
    }

    private ImageView addAction(IconCompat icon, boolean allowTint) {
        ImageView imageView = new ImageView(getContext());
        imageView.setPadding(this.mIconPadding, this.mIconPadding, this.mIconPadding, this.mIconPadding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(icon.loadDrawable(getContext()));
        if (allowTint) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(this.mColor));
        }
        imageView.setBackground(SliceViewUtil.getDrawable(getContext(), 16843534));
        imageView.setTag(Boolean.valueOf(allowTint));
        addAction(imageView);
        return imageView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r2v0 */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: type inference failed for: r2v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setActions(@android.support.annotation.NonNull java.util.List<androidx.slice.core.SliceAction> r11, int r12) {
        /*
            r10 = this;
            r10.removeAllViews()
            android.widget.LinearLayout r0 = r10.mActionsGroup
            r0.removeAllViews()
            android.widget.LinearLayout r0 = r10.mActionsGroup
            r10.addView(r0)
            r0 = -1
            if (r12 == r0) goto L_0x0013
            r10.setColor(r12)
        L_0x0013:
            java.util.Iterator r0 = r11.iterator()
        L_0x0017:
            boolean r1 = r0.hasNext()
            r2 = 0
            if (r1 == 0) goto L_0x0090
            java.lang.Object r1 = r0.next()
            androidx.slice.core.SliceAction r1 = (androidx.slice.core.SliceAction) r1
            android.widget.LinearLayout r3 = r10.mActionsGroup
            int r3 = r3.getChildCount()
            r4 = 5
            if (r3 < r4) goto L_0x002e
            return
        L_0x002e:
            r3 = r1
            androidx.slice.core.SliceActionImpl r3 = (androidx.slice.core.SliceActionImpl) r3
            androidx.slice.SliceItem r3 = r3.getSliceItem()
            r4 = r1
            androidx.slice.core.SliceActionImpl r4 = (androidx.slice.core.SliceActionImpl) r4
            androidx.slice.SliceItem r4 = r4.getActionItem()
            java.lang.String r5 = "input"
            androidx.slice.SliceItem r5 = androidx.slice.core.SliceQuery.find((androidx.slice.SliceItem) r3, (java.lang.String) r5)
            java.lang.String r6 = "image"
            androidx.slice.SliceItem r6 = androidx.slice.core.SliceQuery.find((androidx.slice.SliceItem) r3, (java.lang.String) r6)
            if (r5 == 0) goto L_0x006d
            if (r6 == 0) goto L_0x006d
            int r2 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r2 < r7) goto L_0x0056
            r10.handleSetRemoteInputActions(r5, r6, r4)
            goto L_0x008f
        L_0x0056:
            java.lang.String r2 = "ActionRow"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Received RemoteInput on API <20 "
            r7.append(r8)
            r7.append(r5)
            java.lang.String r7 = r7.toString()
            android.util.Log.w(r2, r7)
            goto L_0x008f
        L_0x006d:
            android.support.v4.graphics.drawable.IconCompat r7 = r1.getIcon()
            if (r7 == 0) goto L_0x008f
            android.support.v4.graphics.drawable.IconCompat r7 = r1.getIcon()
            if (r7 == 0) goto L_0x008f
            if (r4 == 0) goto L_0x008f
            int r8 = r1.getImageMode()
            if (r8 != 0) goto L_0x0083
            r2 = 1
        L_0x0083:
            android.widget.ImageView r8 = r10.addAction(r7, r2)
            androidx.slice.widget.ActionRow$1 r9 = new androidx.slice.widget.ActionRow$1
            r9.<init>(r4)
            r8.setOnClickListener(r9)
        L_0x008f:
            goto L_0x0017
        L_0x0090:
            int r0 = r10.getChildCount()
            if (r0 == 0) goto L_0x0097
            goto L_0x0099
        L_0x0097:
            r2 = 8
        L_0x0099:
            r10.setVisibility(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.ActionRow.setActions(java.util.List, int):void");
    }

    private void addAction(View child) {
        this.mActionsGroup.addView(child, new LinearLayout.LayoutParams(this.mSize, this.mSize, 1.0f));
    }

    @RequiresApi(21)
    private void handleSetRemoteInputActions(final SliceItem input, SliceItem image, final SliceItem action) {
        if (input.getRemoteInput().getAllowFreeFormInput()) {
            addAction(image.getIcon(), !image.hasHint("no_tint")).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean unused = ActionRow.this.handleRemoteInputClick(v, action, input.getRemoteInput());
                }
            });
            createRemoteInputView(this.mColor, getContext());
        }
    }

    @RequiresApi(21)
    private void createRemoteInputView(int color, Context context) {
        View riv = RemoteInputView.inflate(context, this);
        riv.setVisibility(4);
        addView(riv, new FrameLayout.LayoutParams(-1, -1));
        riv.setBackgroundColor(color);
    }

    /* access modifiers changed from: private */
    @RequiresApi(21)
    public boolean handleRemoteInputClick(View view, SliceItem action, RemoteInput input) {
        if (input == null) {
            return false;
        }
        ViewParent p = view.getParent().getParent();
        RemoteInputView riv = null;
        while (p != null && (!(p instanceof View) || (riv = findRemoteInputView((View) p)) == null)) {
            p = p.getParent();
        }
        if (riv == null) {
            return false;
        }
        int width = view.getWidth();
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (tv.getLayout() != null) {
                width = Math.min(width, ((int) tv.getLayout().getLineWidth(0)) + tv.getCompoundPaddingLeft() + tv.getCompoundPaddingRight());
            }
        }
        int cx = view.getLeft() + (width / 2);
        int cy = view.getTop() + (view.getHeight() / 2);
        int w = riv.getWidth();
        int h = riv.getHeight();
        riv.setRevealParameters(cx, cy, Math.max(Math.max(cx + cy, (h - cy) + cx), Math.max((w - cx) + cy, (w - cx) + (h - cy))));
        riv.setAction(action);
        riv.setRemoteInput(new RemoteInput[]{input}, input);
        riv.focusAnimated();
        return true;
    }

    @RequiresApi(21)
    private RemoteInputView findRemoteInputView(View v) {
        if (v == null) {
            return null;
        }
        return (RemoteInputView) v.findViewWithTag(RemoteInputView.VIEW_TAG);
    }
}
