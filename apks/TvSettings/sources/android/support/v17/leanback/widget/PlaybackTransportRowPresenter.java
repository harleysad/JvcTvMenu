package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R;
import android.support.v17.leanback.widget.ControlBarPresenter;
import android.support.v17.leanback.widget.PlaybackControlsPresenter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.support.v17.leanback.widget.PlaybackSeekUi;
import android.support.v17.leanback.widget.PlaybackTransportRowView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SeekBar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.Arrays;

public class PlaybackTransportRowPresenter extends PlaybackRowPresenter {
    float mDefaultSeekIncrement = 0.01f;
    Presenter mDescriptionPresenter;
    OnActionClickedListener mOnActionClickedListener;
    private final ControlBarPresenter.OnControlClickedListener mOnControlClickedListener = new ControlBarPresenter.OnControlClickedListener() {
        public void onControlClicked(Presenter.ViewHolder itemViewHolder, Object item, ControlBarPresenter.BoundData data) {
            ViewHolder vh = ((BoundData) data).mRowViewHolder;
            if (vh.getOnItemViewClickedListener() != null) {
                vh.getOnItemViewClickedListener().onItemClicked(itemViewHolder, item, vh, vh.getRow());
            }
            if (PlaybackTransportRowPresenter.this.mOnActionClickedListener != null && (item instanceof Action)) {
                PlaybackTransportRowPresenter.this.mOnActionClickedListener.onActionClicked((Action) item);
            }
        }
    };
    private final ControlBarPresenter.OnControlSelectedListener mOnControlSelectedListener = new ControlBarPresenter.OnControlSelectedListener() {
        public void onControlSelected(Presenter.ViewHolder itemViewHolder, Object item, ControlBarPresenter.BoundData data) {
            ViewHolder vh = ((BoundData) data).mRowViewHolder;
            if (vh.mSelectedViewHolder != itemViewHolder || vh.mSelectedItem != item) {
                vh.mSelectedViewHolder = itemViewHolder;
                vh.mSelectedItem = item;
                vh.dispatchItemSelection();
            }
        }
    };
    ControlBarPresenter mPlaybackControlsPresenter;
    int mProgressColor = 0;
    boolean mProgressColorSet;
    ControlBarPresenter mSecondaryControlsPresenter;
    int mSecondaryProgressColor = 0;
    boolean mSecondaryProgressColorSet;

    static class BoundData extends PlaybackControlsPresenter.BoundData {
        ViewHolder mRowViewHolder;

        BoundData() {
        }
    }

    public class ViewHolder extends PlaybackRowPresenter.ViewHolder implements PlaybackSeekUi {
        BoundData mControlsBoundData = new BoundData();
        final ViewGroup mControlsDock;
        ControlBarPresenter.ViewHolder mControlsVh;
        final TextView mCurrentTime;
        long mCurrentTimeInMs = Long.MIN_VALUE;
        final ViewGroup mDescriptionDock;
        final Presenter.ViewHolder mDescriptionViewHolder;
        final ImageView mImageView;
        boolean mInSeek;
        final PlaybackControlsRow.OnPlaybackProgressCallback mListener = new PlaybackControlsRow.OnPlaybackProgressCallback() {
            public void onCurrentPositionChanged(PlaybackControlsRow row, long ms) {
                ViewHolder.this.setCurrentPosition(ms);
            }

            public void onDurationChanged(PlaybackControlsRow row, long ms) {
                ViewHolder.this.setTotalTime(ms);
            }

            public void onBufferedPositionChanged(PlaybackControlsRow row, long ms) {
                ViewHolder.this.setBufferedPosition(ms);
            }
        };
        PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
        long[] mPositions;
        int mPositionsLength;
        final SeekBar mProgressBar;
        BoundData mSecondaryBoundData = new BoundData();
        final ViewGroup mSecondaryControlsDock;
        ControlBarPresenter.ViewHolder mSecondaryControlsVh;
        long mSecondaryProgressInMs;
        PlaybackSeekUi.Client mSeekClient;
        PlaybackSeekDataProvider mSeekDataProvider;
        Object mSelectedItem;
        Presenter.ViewHolder mSelectedViewHolder;
        final StringBuilder mTempBuilder = new StringBuilder();
        int mThumbHeroIndex = -1;
        PlaybackSeekDataProvider.ResultCallback mThumbResult = new PlaybackSeekDataProvider.ResultCallback() {
            public void onThumbnailLoaded(Bitmap bitmap, int index) {
                int childIndex = index - (ViewHolder.this.mThumbHeroIndex - (ViewHolder.this.mThumbsBar.getChildCount() / 2));
                if (childIndex >= 0 && childIndex < ViewHolder.this.mThumbsBar.getChildCount()) {
                    ViewHolder.this.mThumbsBar.setThumbBitmap(childIndex, bitmap);
                }
            }
        };
        final ThumbsBar mThumbsBar;
        final TextView mTotalTime;
        long mTotalTimeInMs = Long.MIN_VALUE;

        /* access modifiers changed from: package-private */
        public void updateProgressInSeek(boolean forward) {
            long newPos;
            int thumbHeroIndex;
            long newPos2;
            long pos = this.mCurrentTimeInMs;
            if (this.mPositionsLength > 0) {
                int i = 0;
                int index = Arrays.binarySearch(this.mPositions, 0, this.mPositionsLength, pos);
                if (!forward) {
                    if (index < 0) {
                        int insertIndex = -1 - index;
                        if (insertIndex > 0) {
                            int thumbHeroIndex2 = insertIndex - 1;
                            newPos = this.mPositions[insertIndex - 1];
                            thumbHeroIndex = thumbHeroIndex2;
                        } else {
                            newPos2 = 0;
                        }
                    } else if (index > 0) {
                        newPos = this.mPositions[index - 1];
                        thumbHeroIndex = index - 1;
                    } else {
                        newPos = 0;
                        thumbHeroIndex = 0;
                    }
                    updateThumbsInSeek(thumbHeroIndex, forward);
                } else if (index >= 0) {
                    if (index < this.mPositionsLength - 1) {
                        newPos = this.mPositions[index + 1];
                        thumbHeroIndex = index + 1;
                    } else {
                        newPos = this.mTotalTimeInMs;
                        thumbHeroIndex = index;
                    }
                    updateThumbsInSeek(thumbHeroIndex, forward);
                } else {
                    int insertIndex2 = -1 - index;
                    if (insertIndex2 <= this.mPositionsLength - 1) {
                        i = insertIndex2;
                        newPos2 = this.mPositions[insertIndex2];
                    } else {
                        newPos2 = this.mTotalTimeInMs;
                        if (insertIndex2 > 0) {
                            i = insertIndex2 - 1;
                        }
                    }
                }
                long j = newPos2;
                thumbHeroIndex = i;
                newPos = j;
                updateThumbsInSeek(thumbHeroIndex, forward);
            } else {
                long interval = (long) (((float) this.mTotalTimeInMs) * PlaybackTransportRowPresenter.this.getDefaultSeekIncrement());
                long newPos3 = (forward ? interval : -interval) + pos;
                if (newPos3 > this.mTotalTimeInMs) {
                    newPos3 = this.mTotalTimeInMs;
                } else if (newPos3 < 0) {
                    newPos = 0;
                }
                newPos = newPos3;
            }
            this.mProgressBar.setProgress((int) (2.147483647E9d * (((double) newPos) / ((double) this.mTotalTimeInMs))));
            this.mSeekClient.onSeekPositionChanged(newPos);
        }

        /* access modifiers changed from: package-private */
        public void updateThumbsInSeek(int thumbHeroIndex, boolean forward) {
            int newRequestEnd;
            int newRequestStart;
            int newRequestStart2;
            int i = thumbHeroIndex;
            if (this.mThumbHeroIndex != i) {
                int totalNum = this.mThumbsBar.getChildCount();
                if (totalNum < 0 || (totalNum & 1) == 0) {
                    throw new RuntimeException();
                }
                int heroChildIndex = totalNum / 2;
                int start = Math.max(i - (totalNum / 2), 0);
                int end = Math.min((totalNum / 2) + i, this.mPositionsLength - 1);
                if (this.mThumbHeroIndex < 0) {
                    newRequestStart = start;
                    newRequestEnd = end;
                    newRequestStart2 = forward;
                } else {
                    newRequestStart2 = i > this.mThumbHeroIndex ? 1 : 0;
                    int oldStart = Math.max(this.mThumbHeroIndex - (totalNum / 2), 0);
                    int oldEnd = Math.min(this.mThumbHeroIndex + (totalNum / 2), this.mPositionsLength - 1);
                    if (newRequestStart2 != 0) {
                        newRequestStart = Math.max(oldEnd + 1, start);
                        newRequestEnd = end;
                        for (int i2 = start; i2 <= newRequestStart - 1; i2++) {
                            this.mThumbsBar.setThumbBitmap((i2 - i) + heroChildIndex, this.mThumbsBar.getThumbBitmap((i2 - this.mThumbHeroIndex) + heroChildIndex));
                        }
                    } else {
                        int newRequestEnd2 = Math.min(oldStart - 1, end);
                        int newRequestStart3 = start;
                        for (int i3 = end; i3 >= newRequestEnd2 + 1; i3--) {
                            this.mThumbsBar.setThumbBitmap((i3 - i) + heroChildIndex, this.mThumbsBar.getThumbBitmap((i3 - this.mThumbHeroIndex) + heroChildIndex));
                        }
                        newRequestEnd = newRequestEnd2;
                        newRequestStart = newRequestStart3;
                    }
                }
                this.mThumbHeroIndex = i;
                if (newRequestStart2 != 0) {
                    for (int i4 = newRequestStart; i4 <= newRequestEnd; i4++) {
                        this.mSeekDataProvider.getThumbnail(i4, this.mThumbResult);
                    }
                } else {
                    for (int i5 = newRequestEnd; i5 >= newRequestStart; i5--) {
                        this.mSeekDataProvider.getThumbnail(i5, this.mThumbResult);
                    }
                }
                int childIndex = 0;
                while (true) {
                    int childIndex2 = childIndex;
                    if (childIndex2 >= (heroChildIndex - this.mThumbHeroIndex) + start) {
                        break;
                    }
                    this.mThumbsBar.setThumbBitmap(childIndex2, (Bitmap) null);
                    childIndex = childIndex2 + 1;
                }
                for (int childIndex3 = ((heroChildIndex + end) - this.mThumbHeroIndex) + 1; childIndex3 < totalNum; childIndex3++) {
                    this.mThumbsBar.setThumbBitmap(childIndex3, (Bitmap) null);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean onForward() {
            if (!startSeek()) {
                return false;
            }
            updateProgressInSeek(true);
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean onBackward() {
            if (!startSeek()) {
                return false;
            }
            updateProgressInSeek(false);
            return true;
        }

        public ViewHolder(View rootView, Presenter descriptionPresenter) {
            super(rootView);
            Presenter.ViewHolder viewHolder;
            this.mImageView = (ImageView) rootView.findViewById(R.id.image);
            this.mDescriptionDock = (ViewGroup) rootView.findViewById(R.id.description_dock);
            this.mCurrentTime = (TextView) rootView.findViewById(R.id.current_time);
            this.mTotalTime = (TextView) rootView.findViewById(R.id.total_time);
            this.mProgressBar = (SeekBar) rootView.findViewById(R.id.playback_progress);
            this.mProgressBar.setOnClickListener(new View.OnClickListener(PlaybackTransportRowPresenter.this) {
                public void onClick(View view) {
                    PlaybackTransportRowPresenter.this.onProgressBarClicked(ViewHolder.this);
                }
            });
            this.mProgressBar.setOnKeyListener(new View.OnKeyListener(PlaybackTransportRowPresenter.this) {
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    boolean z = false;
                    if (keyCode != 4) {
                        if (keyCode != 66) {
                            if (keyCode != 69) {
                                if (keyCode != 81) {
                                    if (keyCode != 111) {
                                        switch (keyCode) {
                                            case 19:
                                            case 20:
                                                return ViewHolder.this.mInSeek;
                                            case 21:
                                                break;
                                            case 22:
                                                break;
                                            case 23:
                                                break;
                                            default:
                                                switch (keyCode) {
                                                    case 89:
                                                        break;
                                                    case 90:
                                                        break;
                                                    default:
                                                        return false;
                                                }
                                        }
                                    }
                                }
                                if (keyEvent.getAction() == 0) {
                                    ViewHolder.this.onForward();
                                }
                                return true;
                            }
                            if (keyEvent.getAction() == 0) {
                                ViewHolder.this.onBackward();
                            }
                            return true;
                        }
                        if (!ViewHolder.this.mInSeek) {
                            return false;
                        }
                        if (keyEvent.getAction() == 1) {
                            ViewHolder.this.stopSeek(false);
                        }
                        return true;
                    }
                    if (!ViewHolder.this.mInSeek) {
                        return false;
                    }
                    if (keyEvent.getAction() == 1) {
                        ViewHolder viewHolder = ViewHolder.this;
                        if (Build.VERSION.SDK_INT < 21 || !ViewHolder.this.mProgressBar.isAccessibilityFocused()) {
                            z = true;
                        }
                        viewHolder.stopSeek(z);
                    }
                    return true;
                }
            });
            this.mProgressBar.setAccessibilitySeekListener(new SeekBar.AccessibilitySeekListener(PlaybackTransportRowPresenter.this) {
                public boolean onAccessibilitySeekForward() {
                    return ViewHolder.this.onForward();
                }

                public boolean onAccessibilitySeekBackward() {
                    return ViewHolder.this.onBackward();
                }
            });
            this.mProgressBar.setMax(Integer.MAX_VALUE);
            this.mControlsDock = (ViewGroup) rootView.findViewById(R.id.controls_dock);
            this.mSecondaryControlsDock = (ViewGroup) rootView.findViewById(R.id.secondary_controls_dock);
            if (descriptionPresenter == null) {
                viewHolder = null;
            } else {
                viewHolder = descriptionPresenter.onCreateViewHolder(this.mDescriptionDock);
            }
            this.mDescriptionViewHolder = viewHolder;
            if (this.mDescriptionViewHolder != null) {
                this.mDescriptionDock.addView(this.mDescriptionViewHolder.view);
            }
            this.mThumbsBar = (ThumbsBar) rootView.findViewById(R.id.thumbs_row);
        }

        public final Presenter.ViewHolder getDescriptionViewHolder() {
            return this.mDescriptionViewHolder;
        }

        public void setPlaybackSeekUiClient(PlaybackSeekUi.Client client) {
            this.mSeekClient = client;
        }

        /* access modifiers changed from: package-private */
        public boolean startSeek() {
            if (this.mInSeek) {
                return true;
            }
            if (this.mSeekClient == null || !this.mSeekClient.isSeekEnabled() || this.mTotalTimeInMs <= 0) {
                return false;
            }
            this.mInSeek = true;
            this.mSeekClient.onSeekStarted();
            this.mSeekDataProvider = this.mSeekClient.getPlaybackSeekDataProvider();
            this.mPositions = this.mSeekDataProvider != null ? this.mSeekDataProvider.getSeekPositions() : null;
            if (this.mPositions != null) {
                int pos = Arrays.binarySearch(this.mPositions, this.mTotalTimeInMs);
                if (pos >= 0) {
                    this.mPositionsLength = pos + 1;
                } else {
                    this.mPositionsLength = -1 - pos;
                }
            } else {
                this.mPositionsLength = 0;
            }
            this.mControlsVh.view.setVisibility(8);
            this.mSecondaryControlsVh.view.setVisibility(4);
            this.mDescriptionViewHolder.view.setVisibility(4);
            this.mThumbsBar.setVisibility(0);
            return true;
        }

        /* access modifiers changed from: package-private */
        public void stopSeek(boolean cancelled) {
            if (this.mInSeek) {
                this.mInSeek = false;
                this.mSeekClient.onSeekFinished(cancelled);
                if (this.mSeekDataProvider != null) {
                    this.mSeekDataProvider.reset();
                }
                this.mThumbHeroIndex = -1;
                this.mThumbsBar.clearThumbBitmaps();
                this.mSeekDataProvider = null;
                this.mPositions = null;
                this.mPositionsLength = 0;
                this.mControlsVh.view.setVisibility(0);
                this.mSecondaryControlsVh.view.setVisibility(0);
                this.mDescriptionViewHolder.view.setVisibility(0);
                this.mThumbsBar.setVisibility(4);
            }
        }

        /* access modifiers changed from: package-private */
        public void dispatchItemSelection() {
            if (isSelected()) {
                if (this.mSelectedViewHolder == null) {
                    if (getOnItemViewSelectedListener() != null) {
                        getOnItemViewSelectedListener().onItemSelected((Presenter.ViewHolder) null, (Object) null, this, getRow());
                    }
                } else if (getOnItemViewSelectedListener() != null) {
                    getOnItemViewSelectedListener().onItemSelected(this.mSelectedViewHolder, this.mSelectedItem, this, getRow());
                }
            }
        }

        /* access modifiers changed from: package-private */
        public Presenter getPresenter(boolean primary) {
            ObjectAdapter adapter;
            if (primary) {
                adapter = ((PlaybackControlsRow) getRow()).getPrimaryActionsAdapter();
            } else {
                adapter = ((PlaybackControlsRow) getRow()).getSecondaryActionsAdapter();
            }
            Object obj = null;
            if (adapter == null) {
                return null;
            }
            if (adapter.getPresenterSelector() instanceof ControlButtonPresenterSelector) {
                return ((ControlButtonPresenterSelector) adapter.getPresenterSelector()).getSecondaryPresenter();
            }
            if (adapter.size() > 0) {
                obj = adapter.get(0);
            }
            return adapter.getPresenter(obj);
        }

        public final TextView getDurationView() {
            return this.mTotalTime;
        }

        /* access modifiers changed from: protected */
        public void onSetDurationLabel(long totalTimeMs) {
            if (this.mTotalTime != null) {
                PlaybackTransportRowPresenter.formatTime(totalTimeMs, this.mTempBuilder);
                this.mTotalTime.setText(this.mTempBuilder.toString());
            }
        }

        /* access modifiers changed from: package-private */
        public void setTotalTime(long totalTimeMs) {
            if (this.mTotalTimeInMs != totalTimeMs) {
                this.mTotalTimeInMs = totalTimeMs;
                onSetDurationLabel(totalTimeMs);
            }
        }

        public final TextView getCurrentPositionView() {
            return this.mCurrentTime;
        }

        /* access modifiers changed from: protected */
        public void onSetCurrentPositionLabel(long currentTimeMs) {
            if (this.mCurrentTime != null) {
                PlaybackTransportRowPresenter.formatTime(currentTimeMs, this.mTempBuilder);
                this.mCurrentTime.setText(this.mTempBuilder.toString());
            }
        }

        /* access modifiers changed from: package-private */
        public void setCurrentPosition(long currentTimeMs) {
            if (currentTimeMs != this.mCurrentTimeInMs) {
                this.mCurrentTimeInMs = currentTimeMs;
                onSetCurrentPositionLabel(currentTimeMs);
            }
            if (!this.mInSeek) {
                int progressRatio = 0;
                if (this.mTotalTimeInMs > 0) {
                    progressRatio = (int) (2.147483647E9d * (((double) this.mCurrentTimeInMs) / ((double) this.mTotalTimeInMs)));
                }
                this.mProgressBar.setProgress(progressRatio);
            }
        }

        /* access modifiers changed from: package-private */
        public void setBufferedPosition(long progressMs) {
            this.mSecondaryProgressInMs = progressMs;
            this.mProgressBar.setSecondaryProgress((int) (2.147483647E9d * (((double) progressMs) / ((double) this.mTotalTimeInMs))));
        }
    }

    static void formatTime(long ms, StringBuilder sb) {
        sb.setLength(0);
        if (ms < 0) {
            sb.append("--");
            return;
        }
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long seconds2 = seconds - (minutes * 60);
        long minutes2 = minutes - (60 * hours);
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

    public PlaybackTransportRowPresenter() {
        setHeaderPresenter((RowHeaderPresenter) null);
        setSelectEffectEnabled(false);
        this.mPlaybackControlsPresenter = new ControlBarPresenter(R.layout.lb_control_bar);
        this.mPlaybackControlsPresenter.setDefaultFocusToMiddle(false);
        this.mSecondaryControlsPresenter = new ControlBarPresenter(R.layout.lb_control_bar);
        this.mSecondaryControlsPresenter.setDefaultFocusToMiddle(false);
        this.mPlaybackControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
        this.mSecondaryControlsPresenter.setOnControlSelectedListener(this.mOnControlSelectedListener);
        this.mPlaybackControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
        this.mSecondaryControlsPresenter.setOnControlClickedListener(this.mOnControlClickedListener);
    }

    public void setDescriptionPresenter(Presenter descriptionPresenter) {
        this.mDescriptionPresenter = descriptionPresenter;
    }

    public void setOnActionClickedListener(OnActionClickedListener listener) {
        this.mOnActionClickedListener = listener;
    }

    public OnActionClickedListener getOnActionClickedListener() {
        return this.mOnActionClickedListener;
    }

    public void setProgressColor(@ColorInt int color) {
        this.mProgressColor = color;
        this.mProgressColorSet = true;
    }

    @ColorInt
    public int getProgressColor() {
        return this.mProgressColor;
    }

    public void setSecondaryProgressColor(@ColorInt int color) {
        this.mSecondaryProgressColor = color;
        this.mSecondaryProgressColorSet = true;
    }

    @ColorInt
    public int getSecondaryProgressColor() {
        return this.mSecondaryProgressColor;
    }

    public void onReappear(RowPresenter.ViewHolder rowViewHolder) {
        ViewHolder vh = (ViewHolder) rowViewHolder;
        if (vh.view.hasFocus()) {
            vh.mProgressBar.requestFocus();
        }
    }

    private static int getDefaultProgressColor(Context context) {
        TypedValue outValue = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.playbackProgressPrimaryColor, outValue, true)) {
            return context.getResources().getColor(outValue.resourceId);
        }
        return context.getResources().getColor(R.color.lb_playback_progress_color_no_theme);
    }

    private static int getDefaultSecondaryProgressColor(Context context) {
        TypedValue outValue = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.playbackProgressSecondaryColor, outValue, true)) {
            return context.getResources().getColor(outValue.resourceId);
        }
        return context.getResources().getColor(R.color.lb_playback_progress_secondary_color_no_theme);
    }

    /* access modifiers changed from: protected */
    public RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        ViewHolder vh = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lb_playback_transport_controls_row, parent, false), this.mDescriptionPresenter);
        initRow(vh);
        return vh;
    }

    private void initRow(final ViewHolder vh) {
        int i;
        int i2;
        vh.mControlsVh = (ControlBarPresenter.ViewHolder) this.mPlaybackControlsPresenter.onCreateViewHolder(vh.mControlsDock);
        SeekBar seekBar = vh.mProgressBar;
        if (this.mProgressColorSet) {
            i = this.mProgressColor;
        } else {
            i = getDefaultProgressColor(vh.mControlsDock.getContext());
        }
        seekBar.setProgressColor(i);
        SeekBar seekBar2 = vh.mProgressBar;
        if (this.mSecondaryProgressColorSet) {
            i2 = this.mSecondaryProgressColor;
        } else {
            i2 = getDefaultSecondaryProgressColor(vh.mControlsDock.getContext());
        }
        seekBar2.setSecondaryProgressColor(i2);
        vh.mControlsDock.addView(vh.mControlsVh.view);
        vh.mSecondaryControlsVh = (ControlBarPresenter.ViewHolder) this.mSecondaryControlsPresenter.onCreateViewHolder(vh.mSecondaryControlsDock);
        vh.mSecondaryControlsDock.addView(vh.mSecondaryControlsVh.view);
        ((PlaybackTransportRowView) vh.view.findViewById(R.id.transport_row)).setOnUnhandledKeyListener(new PlaybackTransportRowView.OnUnhandledKeyListener() {
            public boolean onUnhandledKey(KeyEvent event) {
                if (vh.getOnKeyListener() == null || !vh.getOnKeyListener().onKey(vh.view, event.getKeyCode(), event)) {
                    return false;
                }
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        ViewHolder vh = (ViewHolder) holder;
        PlaybackControlsRow row = (PlaybackControlsRow) vh.getRow();
        if (row.getItem() == null) {
            vh.mDescriptionDock.setVisibility(8);
        } else {
            vh.mDescriptionDock.setVisibility(0);
            if (vh.mDescriptionViewHolder != null) {
                this.mDescriptionPresenter.onBindViewHolder(vh.mDescriptionViewHolder, row.getItem());
            }
        }
        if (row.getImageDrawable() == null) {
            vh.mImageView.setVisibility(8);
        } else {
            vh.mImageView.setVisibility(0);
        }
        vh.mImageView.setImageDrawable(row.getImageDrawable());
        vh.mControlsBoundData.adapter = row.getPrimaryActionsAdapter();
        vh.mControlsBoundData.presenter = vh.getPresenter(true);
        vh.mControlsBoundData.mRowViewHolder = vh;
        this.mPlaybackControlsPresenter.onBindViewHolder(vh.mControlsVh, vh.mControlsBoundData);
        vh.mSecondaryBoundData.adapter = row.getSecondaryActionsAdapter();
        vh.mSecondaryBoundData.presenter = vh.getPresenter(false);
        vh.mSecondaryBoundData.mRowViewHolder = vh;
        this.mSecondaryControlsPresenter.onBindViewHolder(vh.mSecondaryControlsVh, vh.mSecondaryBoundData);
        vh.setTotalTime(row.getDuration());
        vh.setCurrentPosition(row.getCurrentPosition());
        vh.setBufferedPosition(row.getBufferedPosition());
        row.setOnPlaybackProgressChangedListener(vh.mListener);
    }

    /* access modifiers changed from: protected */
    public void onUnbindRowViewHolder(RowPresenter.ViewHolder holder) {
        ViewHolder vh = (ViewHolder) holder;
        PlaybackControlsRow row = (PlaybackControlsRow) vh.getRow();
        if (vh.mDescriptionViewHolder != null) {
            this.mDescriptionPresenter.onUnbindViewHolder(vh.mDescriptionViewHolder);
        }
        this.mPlaybackControlsPresenter.onUnbindViewHolder(vh.mControlsVh);
        this.mSecondaryControlsPresenter.onUnbindViewHolder(vh.mSecondaryControlsVh);
        row.setOnPlaybackProgressChangedListener((PlaybackControlsRow.OnPlaybackProgressCallback) null);
        super.onUnbindRowViewHolder(holder);
    }

    /* access modifiers changed from: protected */
    public void onProgressBarClicked(ViewHolder vh) {
        if (vh != null) {
            if (vh.mPlayPauseAction == null) {
                vh.mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(vh.view.getContext());
            }
            if (vh.getOnItemViewClickedListener() != null) {
                vh.getOnItemViewClickedListener().onItemClicked(vh, vh.mPlayPauseAction, vh, vh.getRow());
            }
            if (this.mOnActionClickedListener != null) {
                this.mOnActionClickedListener.onActionClicked(vh.mPlayPauseAction);
            }
        }
    }

    public void setDefaultSeekIncrement(float ratio) {
        this.mDefaultSeekIncrement = ratio;
    }

    public float getDefaultSeekIncrement() {
        return this.mDefaultSeekIncrement;
    }

    /* access modifiers changed from: protected */
    public void onRowViewSelected(RowPresenter.ViewHolder vh, boolean selected) {
        super.onRowViewSelected(vh, selected);
        if (selected) {
            ((ViewHolder) vh).dispatchItemSelection();
        }
    }

    /* access modifiers changed from: protected */
    public void onRowViewAttachedToWindow(RowPresenter.ViewHolder vh) {
        super.onRowViewAttachedToWindow(vh);
        if (this.mDescriptionPresenter != null) {
            this.mDescriptionPresenter.onViewAttachedToWindow(((ViewHolder) vh).mDescriptionViewHolder);
        }
    }

    /* access modifiers changed from: protected */
    public void onRowViewDetachedFromWindow(RowPresenter.ViewHolder vh) {
        super.onRowViewDetachedFromWindow(vh);
        if (this.mDescriptionPresenter != null) {
            this.mDescriptionPresenter.onViewDetachedFromWindow(((ViewHolder) vh).mDescriptionViewHolder);
        }
    }
}
