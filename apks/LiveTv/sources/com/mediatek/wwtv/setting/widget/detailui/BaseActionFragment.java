package com.mediatek.wwtv.setting.widget.detailui;

import android.os.Bundle;
import com.mediatek.wwtv.setting.widget.detailui.ActionAdapter;
import com.mediatek.wwtv.setting.widget.view.ScrollAdapterView;
import java.util.ArrayList;
import java.util.Iterator;

public class BaseActionFragment extends BaseScrollAdapterFragment implements ActionAdapter.Listener, ActionAdapter.OnFocusListener, ActionAdapter.OnKeyListener {
    private static final String EXTRA_ACTIONS = "actions";
    private static final String EXTRA_INDEX = "index";
    private static final String EXTRA_NAME = "name";
    private ActionAdapter mAdapter;
    private boolean mAddedSavedActions;
    private final LiteFragment mFragment;
    private int mIndexToSelect;
    private ActionAdapter.Listener mListener = null;
    private String mName;
    private boolean mSelectFirstChecked;

    public BaseActionFragment(LiteFragment fragment) {
        super(fragment);
        this.mFragment = fragment;
        this.mIndexToSelect = -1;
        this.mSelectFirstChecked = true;
    }

    public static Bundle buildArgs(ArrayList<Action> actions, String name) {
        return buildArgs(actions, name, -1);
    }

    public static Bundle buildArgs(ArrayList<Action> actions, int index) {
        return buildArgs(actions, (String) null, index);
    }

    public static Bundle buildArgs(ArrayList<Action> actions, String name, int index) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_ACTIONS, actions);
        args.putString(EXTRA_NAME, name);
        args.putInt(EXTRA_INDEX, index);
        return args;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mAdapter = new ActionAdapter(this.mFragment.getActivity());
        this.mAddedSavedActions = false;
        if (savedInstanceState != null) {
            ArrayList<Action> actions = savedInstanceState.getParcelableArrayList(EXTRA_ACTIONS);
            int savedIndex = savedInstanceState.getInt(EXTRA_INDEX, -1);
            if (actions != null) {
                Iterator<Action> it = actions.iterator();
                while (it.hasNext()) {
                    this.mAdapter.addAction(it.next());
                }
                if (savedIndex >= 0 && savedIndex < actions.size()) {
                    this.mIndexToSelect = savedIndex;
                }
                this.mAddedSavedActions = true;
            }
        } else {
            int startIndex = this.mFragment.getArguments().getInt(EXTRA_INDEX, -1);
            if (startIndex != -1) {
                this.mIndexToSelect = startIndex;
            }
        }
        this.mName = this.mFragment.getArguments().getString(EXTRA_NAME);
        loadActionsFromArgumentsIfNecessary();
        this.mAdapter.setListener(this);
        this.mAdapter.setOnFocusListener(this);
        this.mAdapter.setOnKeyListener(this);
    }

    public void onResume() {
        ScrollAdapterView sav = getScrollAdapterView();
        sav.addOnScrollListener(this.mAdapter);
        if (getAdapter() != this.mAdapter) {
            this.mAdapter.setScrollAdapterView(sav);
            setAdapter(this.mAdapter);
        }
        if (this.mIndexToSelect != -1) {
            getScrollAdapterView().setSelection(this.mIndexToSelect);
            this.mIndexToSelect = -1;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (hasCreatedView()) {
            outState.putParcelableArrayList(EXTRA_ACTIONS, this.mAdapter.getActions());
            outState.putInt(EXTRA_INDEX, getScrollAdapterView().getSelectedItemPosition());
        }
    }

    public void onActionClicked(Action action) {
        if (action.isEnabled() && !action.infoOnly()) {
            if (this.mListener != null) {
                this.mListener.onActionClicked(action);
            } else if (this.mFragment.getActivity() instanceof ActionAdapter.Listener) {
                ((ActionAdapter.Listener) this.mFragment.getActivity()).onActionClicked(action);
            }
        }
    }

    public void onActionFocused(Action action) {
        if (this.mFragment.getActivity() instanceof ActionAdapter.OnFocusListener) {
            ((ActionAdapter.OnFocusListener) this.mFragment.getActivity()).onActionFocused(action);
        }
    }

    public void onActionSelect(Action action) {
        if (this.mFragment.getActivity() instanceof ActionAdapter.OnKeyListener) {
            ((ActionAdapter.OnKeyListener) this.mFragment.getActivity()).onActionSelect(action);
        }
    }

    public void onActionUnselect(Action action) {
        if (this.mFragment.getActivity() instanceof ActionAdapter.OnKeyListener) {
            ((ActionAdapter.OnKeyListener) this.mFragment.getActivity()).onActionUnselect(action);
        }
    }

    public String getName() {
        return this.mName;
    }

    public void setListener(ActionAdapter.Listener listener) {
        this.mListener = listener;
    }

    public boolean hasListener() {
        return this.mListener != null;
    }

    public void setSelectFirstChecked(boolean selectFirstChecked) {
        this.mSelectFirstChecked = selectFirstChecked;
    }

    private void loadActionsFromArgumentsIfNecessary() {
        ArrayList<Action> actions;
        if (this.mFragment.getArguments() != null && !this.mAddedSavedActions && (actions = this.mFragment.getArguments().getParcelableArrayList(EXTRA_ACTIONS)) != null) {
            int size = actions.size();
            for (int index = 0; index < size; index++) {
                if (this.mSelectFirstChecked && actions.get(index).isChecked() && this.mIndexToSelect == -1) {
                    this.mIndexToSelect = index;
                }
                this.mAdapter.addAction(actions.get(index));
            }
        }
    }
}
