package com.urbanxd.mobilalk_kotprog.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.urbanxd.mobilalk_kotprog.utils.StateBounds;

import java.util.UUID;

public class WaterMeterState {
    private String id;
    private String waterMeterId;
    private long state;
    private StateBounds stateBounds;
    private Timestamp date;

    public WaterMeterState() { }

    public WaterMeterState(String waterMeterId, long state) {
        this.id = String.valueOf(UUID.randomUUID());
        this.waterMeterId = waterMeterId;
        this.state = state;
        this.date = Timestamp.now();
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWaterMeterId() {
        return waterMeterId;
    }

    public void setWaterMeterId(String waterMeterId) {
        this.waterMeterId = waterMeterId;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Exclude
    public StateBounds getStateBounds() {
        return stateBounds;
    }

    public void setStateBounds(StateBounds stateBounds) {
        this.stateBounds = stateBounds;
    }
}
