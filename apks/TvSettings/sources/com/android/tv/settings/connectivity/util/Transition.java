package com.android.tv.settings.connectivity.util;

public class Transition {
    public State destination;
    public int event;
    public State source;

    public Transition(State source2, int event2, State destination2) {
        this.source = source2;
        this.event = event2;
        this.destination = destination2;
    }
}
