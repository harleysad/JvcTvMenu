package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class DividerPresenter extends Presenter {
    private final int mLayoutResourceId;

    public DividerPresenter() {
        this(R.layout.lb_divider);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public DividerPresenter(int layoutResourceId) {
        this.mLayoutResourceId = layoutResourceId;
    }

    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Presenter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.mLayoutResourceId, parent, false));
    }

    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
    }

    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }
}
