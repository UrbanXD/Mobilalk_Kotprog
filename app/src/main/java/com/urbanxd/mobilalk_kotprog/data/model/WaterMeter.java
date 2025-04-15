package com.urbanxd.mobilalk_kotprog.data.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class WaterMeter {
    private final String id;
    private final String userId;
    private final ArrayList<WaterMeterState> states = new ArrayList<>();

    public WaterMeter(String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
    }

    public WaterMeter(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    @Exclude
    public ArrayList<WaterMeterState> getStates() {
        return states;
    }

    public void addState(WaterMeterState newState) {
        states.add(newState);
        Collections.sort(states, (a, b) -> Long.compare(b.getDate().getSeconds(), a.getDate().getSeconds()));
    }

    public void addStates(ArrayList<WaterMeterState> newStates) {
        states.addAll(newStates);
        Collections.sort(states, (a, b) -> Long.compare(b.getDate().getSeconds(), a.getDate().getSeconds()));
    }
}