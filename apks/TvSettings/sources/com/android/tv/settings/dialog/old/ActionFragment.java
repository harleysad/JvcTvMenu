package com.android.tv.settings.dialog.old;

import android.os.Bundle;
import com.android.tv.settings.dialog.old.ActionAdapter;
import java.util.ArrayList;

public class ActionFragment extends ScrollAdapterFragment implements ActionAdapter.Listener, ActionAdapter.OnFocusListener, ActionAdapter.OnKeyListener, LiteFragment {
    private final BaseActionFragment mBase = new BaseActionFragment(this);

    public static ActionFragment newInstance(ArrayList<Action> actions) {
        return newInstance(actions, (String) null);
    }

    public static ActionFragment newInstance(ArrayList<Action> actions, String name) {
        ActionFragment fragment = new ActionFragment();
        fragment.setArguments(BaseActionFragment.buildArgs(actions, name));
        return fragment;
    }

    public static ActionFragment newInstance(ArrayList<Action> actions, int index) {
        ActionFragment fragment = new ActionFragment();
        fragment.setArguments(BaseActionFragment.buildArgs(actions, index));
        return fragment;
    }

    public static ActionFragment newInstance(ArrayList<Action> actions, String name, int index) {
        ActionFragment fragment = new ActionFragment();
        fragment.setArguments(BaseActionFragment.buildArgs(actions, name, index));
        return fragment;
    }

    public ActionFragment() {
        super.setBaseScrollAdapterFragment(this.mBase);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBase.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        this.mBase.onResume();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mBase.onSaveInstanceState(outState);
    }

    public void onActionClicked(Action action) {
        this.mBase.onActionClicked(action);
    }

    public void onActionFocused(Action action) {
        this.mBase.onActionFocused(action);
    }

    public void onActionSelect(Action action) {
        this.mBase.onActionSelect(action);
    }

    public void onActionUnselect(Action action) {
        this.mBase.onActionUnselect(action);
    }

    public void setListener(ActionAdapter.Listener listener) {
        this.mBase.setListener(listener);
    }

    public boolean hasListener() {
        return this.mBase.hasListener();
    }

    public String getName() {
        return this.mBase.getName();
    }
}
