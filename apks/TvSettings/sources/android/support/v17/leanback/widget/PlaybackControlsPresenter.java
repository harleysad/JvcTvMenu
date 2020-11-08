package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R;
import android.support.v17.leanback.util.MathUtil;
import android.support.v17.leanback.widget.ControlBarPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settingslib.accessibility.AccessibilityUtils;

class PlaybackControlsPresenter extends ControlBarPresenter {
    private static int sChildMarginBigger;
    private static int sChildMarginBiggest;
    private boolean mMoreActionsEnabled = true;

    static class BoundData extends ControlBarPresenter.BoundData {
        ObjectAdapter secondaryActionsAdapter;

        BoundData() {
        }
    }

    class ViewHolder extends ControlBarPresenter.ViewHolder {
        final TextView mCurrentTime;
        long mCurrentTimeInMs = -1;
        int mCurrentTimeMarginStart;
        StringBuilder mCurrentTimeStringBuilder = new StringBuilder();
        ObjectAdapter mMoreActionsAdapter;
        final FrameLayout mMoreActionsDock;
        ObjectAdapter.DataObserver mMoreActionsObserver;
        boolean mMoreActionsShowing;
        Presenter.ViewHolder mMoreActionsViewHolder;
        final ProgressBar mProgressBar;
        long mSecondaryProgressInMs = -1;
        final TextView mTotalTime;
        long mTotalTimeInMs = -1;
        int mTotalTimeMarginEnd;
        StringBuilder mTotalTimeStringBuilder = new StringBuilder();

        ViewHolder(View rootView) {
            super(rootView);
            this.mMoreActionsDock = (FrameLayout) rootView.findViewById(R.id.more_actions_dock);
            this.mCurrentTime = (TextView) rootView.findViewById(R.id.current_time);
            this.mTotalTime = (TextView) rootView.findViewById(R.id.total_time);
            this.mProgressBar = (ProgressBar) rootView.findViewById(R.id.playback_progress);
            this.mMoreActionsObserver = new ObjectAdapter.DataObserver(PlaybackControlsPresenter.this) {
                public void onChanged() {
                    if (ViewHolder.this.mMoreActionsShowing) {
                        ViewHolder.this.showControls(ViewHolder.this.mPresenter);
                    }
                }

                public void onItemRangeChanged(int positionStart, int itemCount) {
                    if (ViewHolder.this.mMoreActionsShowing) {
                        for (int i = 0; i < itemCount; i++) {
                            ViewHolder.this.bindControlToAction(positionStart + i, ViewHolder.this.mPresenter);
                        }
                    }
                }
            };
            this.mCurrentTimeMarginStart = ((ViewGroup.MarginLayoutParams) this.mCurrentTime.getLayoutParams()).getMarginStart();
            this.mTotalTimeMarginEnd = ((ViewGroup.MarginLayoutParams) this.mTotalTime.getLayoutParams()).getMarginEnd();
        }

        /* access modifiers changed from: package-private */
        public void showMoreActions(boolean show) {
            if (show) {
                if (this.mMoreActionsViewHolder == null) {
                    Action action = new PlaybackControlsRow.MoreActions(this.mMoreActionsDock.getContext());
                    this.mMoreActionsViewHolder = this.mPresenter.onCreateViewHolder(this.mMoreActionsDock);
                    this.mPresenter.onBindViewHolder(this.mMoreActionsViewHolder, action);
                    this.mPresenter.setOnClickListener(this.mMoreActionsViewHolder, new View.OnClickListener() {
                        public void onClick(View v) {
                            ViewHolder.this.toggleMoreActions();
                        }
                    });
                }
                if (this.mMoreActionsViewHolder.view.getParent() == null) {
                    this.mMoreActionsDock.addView(this.mMoreActionsViewHolder.view);
                }
            } else if (this.mMoreActionsViewHolder != null && this.mMoreActionsViewHolder.view.getParent() != null) {
                this.mMoreActionsDock.removeView(this.mMoreActionsViewHolder.view);
            }
        }

        /* access modifiers changed from: package-private */
        public void toggleMoreActions() {
            this.mMoreActionsShowing = !this.mMoreActionsShowing;
            showControls(this.mPresenter);
        }

        /* access modifiers changed from: package-private */
        public ObjectAdapter getDisplayedAdapter() {
            return this.mMoreActionsShowing ? this.mMoreActionsAdapter : this.mAdapter;
        }

        /* access modifiers changed from: package-private */
        public int getChildMarginFromCenter(Context context, int numControls) {
            int margin = PlaybackControlsPresenter.this.getControlIconWidth(context);
            if (numControls < 4) {
                return margin + PlaybackControlsPresenter.this.getChildMarginBiggest(context);
            }
            if (numControls < 6) {
                return margin + PlaybackControlsPresenter.this.getChildMarginBigger(context);
            }
            return margin + PlaybackControlsPresenter.this.getChildMarginDefault(context);
        }

        /* access modifiers changed from: package-private */
        public void setTotalTime(long totalTimeMs) {
            if (totalTimeMs <= 0) {
                this.mTotalTime.setVisibility(8);
                this.mProgressBar.setVisibility(8);
                return;
            }
            this.mTotalTime.setVisibility(0);
            this.mProgressBar.setVisibility(0);
            this.mTotalTimeInMs = totalTimeMs;
            PlaybackControlsPresenter.formatTime(totalTimeMs / 1000, this.mTotalTimeStringBuilder);
            this.mTotalTime.setText(this.mTotalTimeStringBuilder.toString());
            this.mProgressBar.setMax(Integer.MAX_VALUE);
        }

        /* access modifiers changed from: package-private */
        public long getTotalTime() {
            return this.mTotalTimeInMs;
        }

        /* access modifiers changed from: package-private */
        public void setCurrentTime(long currentTimeMs) {
            long seconds = currentTimeMs / 1000;
            if (currentTimeMs != this.mCurrentTimeInMs) {
                this.mCurrentTimeInMs = currentTimeMs;
                PlaybackControlsPresenter.formatTime(seconds, this.mCurrentTimeStringBuilder);
                this.mCurrentTime.setText(this.mCurrentTimeStringBuilder.toString());
            }
            this.mProgressBar.setProgress((int) (2.147483647E9d * (((double) this.mCurrentTimeInMs) / ((double) this.mTotalTimeInMs))));
        }

        /* access modifiers changed from: package-private */
        public long getCurrentTime() {
            return this.mTotalTimeInMs;
        }

        /* access modifiers changed from: package-private */
        public void setSecondaryProgress(long progressMs) {
            this.mSecondaryProgressInMs = progressMs;
            this.mProgressBar.setSecondaryProgress((int) (2.147483647E9d * (((double) progressMs) / ((double) this.mTotalTimeInMs))));
        }

        /* access modifiers changed from: package-private */
        public long getSecondaryProgress() {
            return this.mSecondaryProgressInMs;
        }
    }

    static void formatTime(long seconds, StringBuilder sb) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long seconds2 = seconds - (minutes * 60);
        long minutes2 = minutes - (60 * hours);
        sb.setLength(0);
        if (hours > 0) {
            sb.append(hours);
            sb.append(AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);
            if (minutes2 < 10) {
                sb.append('0');
            }
        }
        sb.append(minutes2);
        sb.append(AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);
        if (seconds2 < 10) {
            sb.append('0');
        }
        sb.append(seconds2);
    }

    public PlaybackControlsPresenter(int layoutResourceId) {
        super(layoutResourceId);
    }

    public void enableSecondaryActions(boolean enable) {
        this.mMoreActionsEnabled = enable;
    }

    public boolean areMoreActionsEnabled() {
        return this.mMoreActionsEnabled;
    }

    public void setProgressColor(ViewHolder vh, @ColorInt int color) {
        ((LayerDrawable) vh.mProgressBar.getProgressDrawable()).setDrawableByLayerId(16908301, new ClipDrawable(new ColorDrawable(color), 3, 1));
    }

    public void setTotalTime(ViewHolder vh, int ms) {
        setTotalTimeLong(vh, (long) ms);
    }

    public void setTotalTimeLong(ViewHolder vh, long ms) {
        vh.setTotalTime(ms);
    }

    public int getTotalTime(ViewHolder vh) {
        return MathUtil.safeLongToInt(getTotalTimeLong(vh));
    }

    public long getTotalTimeLong(ViewHolder vh) {
        return vh.getTotalTime();
    }

    public void setCurrentTime(ViewHolder vh, int ms) {
        setCurrentTimeLong(vh, (long) ms);
    }

    public void setCurrentTimeLong(ViewHolder vh, long ms) {
        vh.setCurrentTime(ms);
    }

    public int getCurrentTime(ViewHolder vh) {
        return MathUtil.safeLongToInt(getCurrentTimeLong(vh));
    }

    public long getCurrentTimeLong(ViewHolder vh) {
        return vh.getCurrentTime();
    }

    public void setSecondaryProgress(ViewHolder vh, int progressMs) {
        setSecondaryProgressLong(vh, (long) progressMs);
    }

    public void setSecondaryProgressLong(ViewHolder vh, long progressMs) {
        vh.setSecondaryProgress(progressMs);
    }

    public int getSecondaryProgress(ViewHolder vh) {
        return MathUtil.safeLongToInt(getSecondaryProgressLong(vh));
    }

    public long getSecondaryProgressLong(ViewHolder vh) {
        return vh.getSecondaryProgress();
    }

    public void showPrimaryActions(ViewHolder vh) {
        if (vh.mMoreActionsShowing) {
            vh.toggleMoreActions();
        }
    }

    public void resetFocus(ViewHolder vh) {
        vh.mControlBar.requestFocus();
    }

    public void enableTimeMargins(ViewHolder vh, boolean enable) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) vh.mCurrentTime.getLayoutParams();
        int i = 0;
        lp.setMarginStart(enable ? vh.mCurrentTimeMarginStart : 0);
        vh.mCurrentTime.setLayoutParams(lp);
        ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) vh.mTotalTime.getLayoutParams();
        if (enable) {
            i = vh.mTotalTimeMarginEnd;
        }
        lp2.setMarginEnd(i);
        vh.mTotalTime.setLayoutParams(lp2);
    }

    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutResourceId(), parent, false));
    }

    public void onBindViewHolder(Presenter.ViewHolder holder, Object item) {
        ViewHolder vh = (ViewHolder) holder;
        BoundData data = (BoundData) item;
        if (vh.mMoreActionsAdapter != data.secondaryActionsAdapter) {
            vh.mMoreActionsAdapter = data.secondaryActionsAdapter;
            vh.mMoreActionsAdapter.registerObserver(vh.mMoreActionsObserver);
            vh.mMoreActionsShowing = false;
        }
        super.onBindViewHolder(holder, item);
        vh.showMoreActions(this.mMoreActionsEnabled);
    }

    public void onUnbindViewHolder(Presenter.ViewHolder holder) {
        super.onUnbindViewHolder(holder);
        ViewHolder vh = (ViewHolder) holder;
        if (vh.mMoreActionsAdapter != null) {
            vh.mMoreActionsAdapter.unregisterObserver(vh.mMoreActionsObserver);
            vh.mMoreActionsAdapter = null;
        }
    }

    /* access modifiers changed from: package-private */
    public int getChildMarginBigger(Context context) {
        if (sChildMarginBigger == 0) {
            sChildMarginBigger = context.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_child_margin_bigger);
        }
        return sChildMarginBigger;
    }

    /* access modifiers changed from: package-private */
    public int getChildMarginBiggest(Context context) {
        if (sChildMarginBiggest == 0) {
            sChildMarginBiggest = context.getResources().getDimensionPixelSize(R.dimen.lb_playback_controls_child_margin_biggest);
        }
        return sChildMarginBiggest;
    }
}
