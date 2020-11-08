package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.v17.leanback.R;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class AbstractMediaListHeaderPresenter extends RowPresenter {
    private int mBackgroundColor;
    private boolean mBackgroundColorSet;
    private final Context mContext;

    /* access modifiers changed from: protected */
    public abstract void onBindMediaListHeaderViewHolder(ViewHolder viewHolder, Object obj);

    public static class ViewHolder extends RowPresenter.ViewHolder {
        private final TextView mHeaderView;

        public ViewHolder(View view) {
            super(view);
            this.mHeaderView = (TextView) view.findViewById(R.id.mediaListHeader);
        }

        public TextView getHeaderView() {
            return this.mHeaderView;
        }
    }

    public AbstractMediaListHeaderPresenter(Context context, int mThemeResId) {
        this.mBackgroundColor = 0;
        this.mContext = new ContextThemeWrapper(context.getApplicationContext(), mThemeResId);
        setHeaderPresenter((RowHeaderPresenter) null);
    }

    public AbstractMediaListHeaderPresenter() {
        this.mBackgroundColor = 0;
        this.mContext = null;
        setHeaderPresenter((RowHeaderPresenter) null);
    }

    public boolean isUsingDefaultSelectEffect() {
        return false;
    }

    /* access modifiers changed from: protected */
    public RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(this.mContext != null ? this.mContext : parent.getContext()).inflate(R.layout.lb_media_list_header, parent, false);
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);
        ViewHolder vh = new ViewHolder(view);
        if (this.mBackgroundColorSet) {
            vh.view.setBackgroundColor(this.mBackgroundColor);
        }
        return vh;
    }

    /* access modifiers changed from: protected */
    public void onBindRowViewHolder(RowPresenter.ViewHolder vh, Object item) {
        super.onBindRowViewHolder(vh, item);
        onBindMediaListHeaderViewHolder((ViewHolder) vh, item);
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColorSet = true;
        this.mBackgroundColor = color;
    }
}
