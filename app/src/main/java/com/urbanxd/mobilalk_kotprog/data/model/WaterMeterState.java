package com.urbanxd.mobilalk_kotprog.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WaterMeterState {
    private String id;
    private String waterMeterId;
    private long state;
    private Timestamp date;

    public WaterMeterState() { }

    public WaterMeterState(String waterMeterId, long state) {
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

    @Exclude
    public String getFormatedDate() {
        Date date = getDate().toDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+2"));

        return sdf.format(date);
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
