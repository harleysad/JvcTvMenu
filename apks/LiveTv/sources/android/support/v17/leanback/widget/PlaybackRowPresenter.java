package android.support.v17.leanback.widget;

import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;

public abstract class PlaybackRowPresenter extends RowPresenter {

    public static class ViewHolder extends RowPresenter.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public void onReappear(RowPresenter.ViewHolder rowViewHolder) {
    }
}
