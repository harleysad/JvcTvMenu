package androidx.slice.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.ImageView;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R;
import com.android.tv.settings.dialog.old.BaseDialogFragment;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public class ShortcutView extends SliceChildView {
    private static final String TAG = "ShortcutView";
    private SliceItem mActionItem;
    private SliceItem mIcon;
    private SliceItem mLabel;
    private int mLargeIconSize;
    private ListContent mListContent;
    private int mSmallIconSize;
    private Uri mUri;

    public ShortcutView(Context context) {
        super(context);
        Resources res = getResources();
        this.mSmallIconSize = res.getDimensionPixelSize(R.dimen.abc_slice_icon_size);
        this.mLargeIconSize = res.getDimensionPixelSize(R.dimen.abc_slice_shortcut_size);
    }

    public void setSliceContent(ListContent sliceContent) {
        int color;
        resetView();
        this.mListContent = sliceContent;
        if (this.mListContent != null) {
            determineShortcutItems(getContext());
            SliceItem colorItem = this.mListContent.getColorItem();
            if (colorItem == null) {
                colorItem = SliceQuery.findSubtype(sliceContent.getSlice(), "int", "color");
            }
            if (colorItem != null) {
                color = colorItem.getInt();
            } else {
                color = SliceViewUtil.getColorAccent(getContext());
            }
            Drawable circle = DrawableCompat.wrap(new ShapeDrawable(new OvalShape()));
            DrawableCompat.setTint(circle, color);
            ImageView iv = new ImageView(getContext());
            if (this.mIcon != null && !this.mIcon.hasHint("no_tint")) {
                iv.setBackground(circle);
            }
            addView(iv);
            if (this.mIcon != null) {
                boolean isImage = this.mIcon.hasHint("no_tint");
                SliceViewUtil.createCircledIcon(getContext(), isImage ? this.mLargeIconSize : this.mSmallIconSize, this.mIcon.getIcon(), isImage, this);
                this.mUri = sliceContent.getSlice().getUri();
                setClickable(true);
                return;
            }
            setClickable(false);
        }
    }

    public int getMode() {
        return 3;
    }

    public boolean performClick() {
        SliceItem interactedItem;
        if (this.mListContent == null) {
            return false;
        }
        if (!callOnClick()) {
            try {
                if (this.mActionItem != null) {
                    this.mActionItem.fireAction((Context) null, (Intent) null);
                } else {
                    Intent intent = new Intent("android.intent.action.VIEW").setData(this.mUri);
                    intent.addFlags(268435456);
                    getContext().startActivity(intent);
                }
                if (this.mObserver != null) {
                    EventInfo ei = new EventInfo(3, 1, -1, 0);
                    if (this.mActionItem != null) {
                        interactedItem = this.mActionItem;
                    } else {
                        interactedItem = new SliceItem((Object) this.mListContent.getSlice(), SliceProviderCompat.EXTRA_SLICE, (String) null, this.mListContent.getSlice().getHints());
                    }
                    this.mObserver.onSliceAction(ei, interactedItem);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "PendingIntent for slice cannot be sent", e);
            }
        }
        return true;
    }

    private void determineShortcutItems(Context context) {
        if (this.mListContent != null) {
            SliceItem primaryAction = this.mListContent.getPrimaryAction();
            Slice slice = this.mListContent.getSlice();
            if (primaryAction != null) {
                this.mActionItem = primaryAction.getSlice().getItems().get(0);
                String str = null;
                this.mIcon = SliceQuery.find(primaryAction.getSlice(), "image", str, (String) null);
                this.mLabel = SliceQuery.find(primaryAction.getSlice(), "text", str, (String) null);
            } else {
                this.mActionItem = SliceQuery.find(slice, BaseDialogFragment.TAG_ACTION, (String) null, (String) null);
            }
            if (this.mIcon == null) {
                this.mIcon = SliceQuery.find(slice, "image", "title", (String) null);
            }
            if (this.mLabel == null) {
                this.mLabel = SliceQuery.find(slice, "text", "title", (String) null);
            }
            if (this.mIcon == null) {
                this.mIcon = SliceQuery.find(slice, "image", (String) null, (String) null);
            }
            if (this.mLabel == null) {
                this.mLabel = SliceQuery.find(slice, "text", (String) null, (String) null);
            }
            if (this.mIcon == null || this.mLabel == null || this.mActionItem == null) {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo = pm.resolveContentProvider(slice.getUri().getAuthority(), 0).applicationInfo;
                if (appInfo != null) {
                    if (this.mIcon == null) {
                        Slice.Builder sb = new Slice.Builder(slice.getUri());
                        sb.addIcon(SliceViewUtil.createIconFromDrawable(pm.getApplicationIcon(appInfo)), "large", new String[0]);
                        this.mIcon = sb.build().getItems().get(0);
                    }
                    if (this.mLabel == null) {
                        Slice.Builder sb2 = new Slice.Builder(slice.getUri());
                        sb2.addText(pm.getApplicationLabel(appInfo), (String) null, new String[0]);
                        this.mLabel = sb2.build().getItems().get(0);
                    }
                    if (this.mActionItem == null) {
                        this.mActionItem = new SliceItem(PendingIntent.getActivity(context, 0, pm.getLaunchIntentForPackage(appInfo.packageName), 0), new Slice.Builder(slice.getUri()).build(), BaseDialogFragment.TAG_ACTION, (String) null, (String[]) null);
                    }
                }
            }
        }
    }

    public void resetView() {
        this.mListContent = null;
        this.mUri = null;
        this.mActionItem = null;
        this.mLabel = null;
        this.mIcon = null;
        setBackground((Drawable) null);
        removeAllViews();
    }
}
