package com.android.tv.settings.dialog.old;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.widget.ScrollAdapter;
import com.android.tv.settings.widget.ScrollAdapterBase;
import com.android.tv.settings.widget.ScrollAdapterView;
import java.util.ArrayList;
import java.util.List;

public class ActionAdapter extends BaseAdapter implements ScrollAdapter, ScrollAdapterView.OnScrollListener, View.OnKeyListener, View.OnClickListener {
    private static final float CHECKMARK_ANIM_SELECTED_ALPHA = 1.0f;
    private static final float CHECKMARK_ANIM_UNSELECTED_ALPHA = 0.0f;
    private static final boolean DEBUG = false;
    private static final int FX_KEYPRESS_INVALID = 9;
    private static final int SELECT_ANIM_DELAY = 0;
    private static final int SELECT_ANIM_DURATION = 100;
    private static final float SELECT_ANIM_SELECTED_ALPHA = 0.2f;
    private static final float SELECT_ANIM_UNSELECTED_ALPHA = 1.0f;
    private static final String TAG = "ActionAdapter";
    private static Integer sDescriptionMaxHeight = null;
    private final List<Action> mActions;
    private final int mAnimationDuration;
    private final Context mContext;
    private final float mDisabledChevronAlpha;
    private final float mDisabledDescriptionAlpha;
    private final float mDisabledTitleAlpha;
    private boolean mKeyPressed;
    /* access modifiers changed from: private */
    public Listener mListener;
    private OnFocusListener mOnFocusListener;
    /* access modifiers changed from: private */
    public OnKeyListener mOnKeyListener;
    private ScrollAdapterView mScrollAdapterView;
    private final float mSelectedChevronAlpha;
    private final float mSelectedDescriptionAlpha;
    private final float mSelectedTitleAlpha;
    private View mSelectedView = null;
    private final float mUnselectedAlpha;
    private final float mUnselectedDescriptionAlpha;

    public interface Listener {
        void onActionClicked(Action action);
    }

    public interface OnFocusListener {
        void onActionFocused(Action action);
    }

    public interface OnKeyListener {
        void onActionSelect(Action action);

        void onActionUnselect(Action action);
    }

    public ActionAdapter(Context context) {
        this.mContext = context;
        this.mAnimationDuration = context.getResources().getInteger(R.integer.dialog_animation_duration);
        this.mUnselectedAlpha = getFloat(R.dimen.list_item_unselected_text_alpha);
        this.mSelectedTitleAlpha = getFloat(R.dimen.list_item_selected_title_text_alpha);
        this.mDisabledTitleAlpha = getFloat(R.dimen.list_item_disabled_title_text_alpha);
        this.mSelectedDescriptionAlpha = getFloat(R.dimen.list_item_selected_description_text_alpha);
        this.mUnselectedDescriptionAlpha = getFloat(R.dimen.list_item_unselected_description_text_alpha);
        this.mDisabledDescriptionAlpha = getFloat(R.dimen.list_item_disabled_description_text_alpha);
        this.mSelectedChevronAlpha = getFloat(R.dimen.list_item_selected_chevron_background_alpha);
        this.mDisabledChevronAlpha = getFloat(R.dimen.list_item_disabled_chevron_background_alpha);
        this.mActions = new ArrayList();
        this.mKeyPressed = false;
    }

    public void viewRemoved(View view) {
    }

    public View getScrapView(ViewGroup parent) {
        return LayoutInflater.from(this.mContext).inflate(R.layout.settings_list_item, parent, false);
    }

    public int getCount() {
        return this.mActions.size();
    }

    public Object getItem(int position) {
        return this.mActions.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getScrapView(parent);
        }
        Action action = this.mActions.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.action_title);
        TextView description = (TextView) convertView.findViewById(R.id.action_description);
        description.setText(action.getDescription());
        int i = 8;
        description.setVisibility(TextUtils.isEmpty(action.getDescription()) ? 8 : 0);
        title.setText(action.getTitle());
        int i2 = 4;
        ((ImageView) convertView.findViewById(R.id.action_checkmark)).setVisibility(action.isChecked() ? 0 : 4);
        setIndicator((ImageView) convertView.findViewById(R.id.action_icon), action);
        ImageView chevronView = (ImageView) convertView.findViewById(R.id.action_next_chevron);
        if (action.hasNext()) {
            i = 0;
        }
        chevronView.setVisibility(i);
        View chevronBackgroundView = convertView.findViewById(R.id.action_next_chevron_background);
        if (action.hasNext()) {
            i2 = 0;
        }
        chevronBackgroundView.setVisibility(i2);
        Resources res = convertView.getContext().getResources();
        if (action.hasMultilineDescription()) {
            title.setMaxLines(res.getInteger(R.integer.action_title_max_lines));
            description.setMaxHeight(getDescriptionMaxHeight(convertView.getContext(), title, description).intValue());
        } else {
            title.setMaxLines(res.getInteger(R.integer.action_title_min_lines));
            description.setMaxLines(res.getInteger(R.integer.action_description_min_lines));
        }
        convertView.setTag(R.id.action_title, action);
        convertView.setOnKeyListener(this);
        convertView.setOnClickListener(this);
        changeFocus(convertView, false, false);
        return convertView;
    }

    public ScrollAdapterBase getExpandAdapter() {
        return null;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        this.mOnFocusListener = onFocusListener;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
    }

    public void addAction(Action action) {
        this.mActions.add(action);
        notifyDataSetChanged();
    }

    public ArrayList<Action> getActions() {
        return new ArrayList<>(this.mActions);
    }

    public void setActions(ArrayList<Action> actions) {
        changeFocus(this.mSelectedView, false, false);
        this.mActions.clear();
        this.mActions.addAll(actions);
        notifyDataSetChanged();
    }

    public void onScrolled(View view, int position, float mainPosition, float secondPosition) {
        if (((double) mainPosition) == 0.0d) {
            if (view != null) {
                changeFocus(view, true, true);
                this.mSelectedView = view;
            }
        } else if (this.mSelectedView != null) {
            changeFocus(this.mSelectedView, false, true);
            this.mSelectedView = null;
        }
    }

    private void changeFocus(View v, boolean hasFocus, boolean shouldAnimate) {
        float descriptionAlpha;
        if (v != null) {
            Action action = (Action) v.getTag(R.id.action_title);
            float titleAlpha = (!action.isEnabled() || action.infoOnly()) ? this.mDisabledTitleAlpha : hasFocus ? this.mSelectedTitleAlpha : this.mUnselectedAlpha;
            if (!hasFocus || action.infoOnly()) {
                descriptionAlpha = this.mUnselectedDescriptionAlpha;
            } else {
                descriptionAlpha = action.isEnabled() ? this.mSelectedDescriptionAlpha : this.mDisabledDescriptionAlpha;
            }
            float chevronAlpha = (!action.hasNext() || action.infoOnly()) ? 0.0f : action.isEnabled() ? this.mSelectedChevronAlpha : this.mDisabledChevronAlpha;
            setAlpha((TextView) v.findViewById(R.id.action_title), shouldAnimate, titleAlpha);
            setAlpha((TextView) v.findViewById(R.id.action_description), shouldAnimate, descriptionAlpha);
            setAlpha((ImageView) v.findViewById(R.id.action_checkmark), shouldAnimate, titleAlpha);
            setAlpha((ImageView) v.findViewById(R.id.action_icon), shouldAnimate, titleAlpha);
            setAlpha(v.findViewById(R.id.action_next_chevron_background), shouldAnimate, chevronAlpha);
            if (this.mOnFocusListener != null && hasFocus) {
                this.mOnFocusListener.onActionFocused((Action) v.getTag(R.id.action_title));
            }
        }
    }

    private void setIndicator(ImageView indicatorView, Action action) {
        Drawable indicator = action.getIndicator(this.mContext);
        if (indicator != null) {
            indicatorView.setImageDrawable(indicator);
            indicatorView.setVisibility(0);
            return;
        }
        indicatorView.setVisibility(8);
    }

    private void setAlpha(View view, boolean shouldAnimate, float alpha) {
        if (shouldAnimate) {
            view.animate().alpha(alpha).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f)).start();
        } else {
            view.setAlpha(alpha);
        }
    }

    /* access modifiers changed from: package-private */
    public void setScrollAdapterView(ScrollAdapterView scrollAdapterView) {
        this.mScrollAdapterView = scrollAdapterView;
    }

    public void onClick(View v) {
        if (v != null && v.getWindowToken() != null && this.mListener != null) {
            this.mListener.onActionClicked((Action) v.getTag(R.id.action_title));
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        View view = v;
        int i = keyCode;
        if (view == null) {
            return false;
        }
        Action action = (Action) view.getTag(R.id.action_title);
        if (!(i == 23 || i == 66 || i == 160)) {
            switch (i) {
                case 99:
                case 100:
                    break;
                default:
                    return false;
            }
        }
        AudioManager manager = (AudioManager) view.getContext().getSystemService("audio");
        if (!action.isEnabled() || action.infoOnly()) {
            if (view.isSoundEffectsEnabled() && event.getAction() == 0) {
                manager.playSoundEffect(9);
            }
            return true;
        }
        switch (event.getAction()) {
            case 0:
                if (this.mKeyPressed) {
                    return false;
                }
                this.mKeyPressed = true;
                if (view.isSoundEffectsEnabled()) {
                    manager.playSoundEffect(0);
                }
                prepareAndAnimateView(view, 1.0f, SELECT_ANIM_SELECTED_ALPHA, 100, 0, (Interpolator) null, this.mKeyPressed);
                return true;
            case 1:
                if (!this.mKeyPressed) {
                    return false;
                }
                this.mKeyPressed = false;
                prepareAndAnimateView(view, SELECT_ANIM_SELECTED_ALPHA, 1.0f, 100, 0, (Interpolator) null, this.mKeyPressed);
                return true;
            default:
                return false;
        }
    }

    private void prepareAndAnimateView(final View v, float initAlpha, float destAlpha, int duration, int delay, Interpolator interpolator, final boolean pressed) {
        if (v != null && v.getWindowToken() != null) {
            final Action action = (Action) v.getTag(R.id.action_title);
            if (!pressed) {
                fadeCheckmarks(v, action, duration, delay, interpolator);
            }
            v.setAlpha(initAlpha);
            v.setLayerType(2, (Paint) null);
            v.buildLayer();
            v.animate().alpha(destAlpha).setDuration((long) duration).setStartDelay((long) delay);
            if (interpolator != null) {
                v.animate().setInterpolator(interpolator);
            }
            v.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    v.setLayerType(0, (Paint) null);
                    if (!pressed) {
                        if (ActionAdapter.this.mOnKeyListener != null) {
                            ActionAdapter.this.mOnKeyListener.onActionUnselect(action);
                        }
                        if (ActionAdapter.this.mListener != null) {
                            ActionAdapter.this.mListener.onActionClicked(action);
                        }
                    } else if (ActionAdapter.this.mOnKeyListener != null) {
                        ActionAdapter.this.mOnKeyListener.onActionSelect(action);
                    }
                }
            });
            v.animate().start();
        }
    }

    private void fadeCheckmarks(View v, Action action, int duration, int delay, Interpolator interpolator) {
        View viewToAnimateOut;
        int actionCheckSetId = action.getCheckSetId();
        if (actionCheckSetId != 0) {
            int size = this.mActions.size();
            for (int i = 0; i < size; i++) {
                Action a = this.mActions.get(i);
                if (a != action && a.getCheckSetId() == actionCheckSetId && a.isChecked()) {
                    a.setChecked(false);
                    if (!(this.mScrollAdapterView == null || (viewToAnimateOut = this.mScrollAdapterView.getItemView(i)) == null)) {
                        final View checkView = viewToAnimateOut.findViewById(R.id.action_checkmark);
                        checkView.animate().alpha(0.0f).setDuration((long) duration).setStartDelay((long) delay);
                        if (interpolator != null) {
                            checkView.animate().setInterpolator(interpolator);
                        }
                        checkView.animate().setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                checkView.setVisibility(4);
                            }
                        });
                    }
                }
            }
            if (action.isChecked() == 0) {
                action.setChecked(true);
                if (this.mScrollAdapterView != null) {
                    View checkView2 = v.findViewById(R.id.action_checkmark);
                    checkView2.setVisibility(0);
                    checkView2.setAlpha(0.0f);
                    checkView2.animate().alpha(1.0f).setDuration((long) duration).setStartDelay((long) delay);
                    if (interpolator != null) {
                        checkView2.animate().setInterpolator(interpolator);
                    }
                    checkView2.animate().setListener((Animator.AnimatorListener) null);
                }
            }
        }
    }

    private static Integer getDescriptionMaxHeight(Context context, TextView title, TextView description) {
        if (sDescriptionMaxHeight == null) {
            Resources res = context.getResources();
            sDescriptionMaxHeight = Integer.valueOf((int) ((((float) ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight()) - (2.0f * res.getDimension(R.dimen.list_item_vertical_padding))) - ((float) ((2 * res.getInteger(R.integer.action_title_max_lines)) * title.getLineHeight()))));
        }
        return sDescriptionMaxHeight;
    }

    private float getFloat(int resourceId) {
        TypedValue buffer = new TypedValue();
        this.mContext.getResources().getValue(resourceId, buffer, true);
        return buffer.getFloat();
    }
}
