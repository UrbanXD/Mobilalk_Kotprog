package com.urbanxd.mobilalk_kotprog.data.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

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

    @Exclude
    public String getId() {
        return firebaseUser.getUid();
    }

    @Exclude
    public String getEmail() {
        return firebaseUser.getEmail();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    @Exclude
    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    @Exclude
    public WaterMeter getWaterMeter() {
        return waterMeter;
    }

    public void setWaterMeter(WaterMeter waterMeter) {
        this.waterMeter = waterMeter;
    }
}
