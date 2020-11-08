package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class InvisibleRowPresenter extends RowPresenter {
    public InvisibleRowPresenter() {
        setHeaderPresenter((RowHeaderPresenter) null);
    }

    /* access modifiers changed from: protected */
    public RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        RelativeLayout root = new RelativeLayout(parent.getContext());
        root.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        return new RowPresenter.ViewHolder(root);
    }
}
