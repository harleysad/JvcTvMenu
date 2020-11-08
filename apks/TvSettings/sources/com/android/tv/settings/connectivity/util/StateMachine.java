package com.android.tv.settings.connectivity.util;

import android.arch.lifecycle.ViewModel;
import com.android.tv.settings.connectivity.util.State;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StateMachine extends ViewModel {
    public static final int ADD_PAGE_BASED_ON_NETWORK_CHOICE = 17;
    public static final int ADD_START = 0;
    public static final int ADVANCED_FLOW_COMPLETE = 23;
    public static final int CANCEL = 1;
    public static final int CONNECT = 5;
    public static final int CONTINUE = 2;
    public static final int EARLY_EXIT = 4;
    public static final int ENTER_ADVANCED_FLOW = 24;
    public static final int EXIT_ADVANCED_FLOW = 25;
    public static final int FAIL = 3;
    public static final int IP_SETTINGS = 19;
    public static final int IP_SETTINGS_INVALID = 20;
    public static final int KNOWN_NETWORK = 9;
    public static final int OPTIONS_OR_CONNECT = 18;
    public static final int OTHER_NETWORK = 8;
    public static final int PASSWORD = 7;
    public static final int PROXY_HOSTNAME = 21;
    public static final int PROXY_SETTINGS_INVALID = 22;
    public static final int RESULT_BAD_AUTH = 13;
    public static final int RESULT_FAILURE = 15;
    public static final int RESULT_REJECTED_BY_AP = 10;
    public static final int RESULT_SUCCESS = 14;
    public static final int RESULT_TIMEOUT = 12;
    public static final int RESULT_UNKNOWN_ERROR = 11;
    public static final int SELECT_WIFI = 6;
    public static final int TRY_AGAIN = 16;
    private Callback mCallback;
    private State.StateCompleteListener mCompletionListener = new State.StateCompleteListener() {
        public final void onComplete(int i) {
            StateMachine.this.updateState(i);
        }
    };
    private LinkedList<State> mStatesList = new LinkedList<>();
    private Map<State, List<Transition>> mTransitionMap = new HashMap();

    public interface Callback {
        void onFinish(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public StateMachine() {
    }

    public StateMachine(Callback callback) {
        this.mCallback = callback;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void addState(State state, int event, State destination) {
        if (!this.mTransitionMap.containsKey(state)) {
            this.mTransitionMap.put(state, new ArrayList());
        }
        this.mTransitionMap.get(state).add(new Transition(state, event, destination));
    }

    public void addTerminalState(State state) {
        this.mTransitionMap.put(state, new ArrayList());
    }

    public void setStartState(State startState) {
        this.mStatesList.addLast(startState);
    }

    public void start(boolean movingForward) {
        if (!this.mStatesList.isEmpty()) {
            State currentState = getCurrentState();
            if (movingForward) {
                currentState.processForward();
            } else {
                currentState.processBackward();
            }
        } else {
            throw new IllegalArgumentException("Start state not set");
        }
    }

    public void reset() {
        this.mStatesList = new LinkedList<>();
    }

    public void back() {
        updateState(1);
    }

    public State getCurrentState() {
        if (!this.mStatesList.isEmpty()) {
            return this.mStatesList.getLast();
        }
        return null;
    }

    public void finish(int result) {
        this.mCallback.onFinish(result);
    }

    /* access modifiers changed from: private */
    public void updateState(int event) {
        if (event == 4) {
            finish(-1);
        } else if (event == 3) {
            finish(0);
        } else if (event != 1) {
            State next = null;
            State currentState = getCurrentState();
            if (this.mTransitionMap.get(currentState) != null) {
                for (Transition transition : this.mTransitionMap.get(currentState)) {
                    if (transition.event == event) {
                        next = transition.destination;
                    }
                }
            }
            if (next != null) {
                addToStack(next);
                next.processForward();
            } else if (event == 2) {
                this.mCallback.onFinish(-1);
            } else {
                throw new IllegalArgumentException(getCurrentState().getClass() + "Invalid transition " + event);
            }
        } else if (this.mStatesList.size() < 2) {
            this.mCallback.onFinish(0);
        } else {
            this.mStatesList.removeLast();
            this.mStatesList.getLast().processBackward();
        }
    }

    private void addToStack(State state) {
        for (int i = this.mStatesList.size() - 1; i >= 0; i--) {
            if (state.getClass().equals(this.mStatesList.get(i).getClass())) {
                for (int j = this.mStatesList.size() - 1; j >= i; j--) {
                    this.mStatesList.removeLast();
                }
            }
        }
        this.mStatesList.addLast(state);
    }

    public State.StateCompleteListener getListener() {
        return this.mCompletionListener;
    }
}
