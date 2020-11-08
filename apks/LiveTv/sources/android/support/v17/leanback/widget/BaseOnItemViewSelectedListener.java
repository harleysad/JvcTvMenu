package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;

public interface BaseOnItemViewSelectedListener<T> {
    void onItemSelected(Presenter.ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, T t);
}
