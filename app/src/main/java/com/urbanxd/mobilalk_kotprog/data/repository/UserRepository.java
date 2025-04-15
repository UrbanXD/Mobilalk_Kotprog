package com.urbanxd.mobilalk_kotprog.data.repository;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.urbanxd.mobilalk_kotprog.data.model.User;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.Callback;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.CallbackResult;

public class UserRepository {
    private final CollectionReference userCollection;
    private final WaterMeterRepository waterMeterRepository;

    public UserRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        waterMeterRepository = new WaterMeterRepository();
    }

    public void getUser(FirebaseUser firebaseUser, Callback<User> callback) {
        if(firebaseUser == null) {
            callback.onComplete(new CallbackResult<>(null));
            return;
        };

        userCollection
            .document(firebaseUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if(!documentSnapshot.exists()) {
                    callback.onComplete(new CallbackResult<>(null));
                    return;
                }

                String firstname = documentSnapshot.getString("firstname");
                String lastname = documentSnapshot.getString("lastname");

                User user = new User(firebaseUser, firstname, lastname);
                callback.onComplete(new CallbackResult<>(user));
            });
    }

    public void createUser(FirebaseUser firebaseUser, String firstname, String lastname) {
        User user = new User(firebaseUser, firstname, lastname);
        userCollection
            .document(user.getId())
            .set(user)
            .addOnSuccessListener(aVoid -> {
                waterMeterRepository.createWaterMeter(user.getId());
            });
    }
}

