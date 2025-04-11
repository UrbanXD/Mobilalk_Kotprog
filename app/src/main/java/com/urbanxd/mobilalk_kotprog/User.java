package com.urbanxd.mobilalk_kotprog;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String firstname;
    private String lastname;
    private FirebaseUser firebaseUser;
    private WaterMeter waterMeter;

    public User(FirebaseUser firebaseUser, String firstname, String lastname) {
        this.firebaseUser = firebaseUser;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getId() {
        return firebaseUser.getUid();
    }

    public String getEmail() {
        return firebaseUser.getEmail();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public WaterMeter getUserWaterMeter() {
        return waterMeter;
    }

    public void setWaterMeter(WaterMeter waterMeter) {
        this.waterMeter = waterMeter;
    }
}
