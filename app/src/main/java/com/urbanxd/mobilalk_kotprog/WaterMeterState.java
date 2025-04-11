package com.urbanxd.mobilalk_kotprog;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WaterMeterState {
    private int state;
    private Timestamp date;

    public WaterMeterState() { }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getFormatedDate() {
        Date date = getDate().toDate();

        // Formázzuk a dátumot
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Állítsd be a megfelelő időzónát, ha szükséges

        return sdf.format(date);
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
