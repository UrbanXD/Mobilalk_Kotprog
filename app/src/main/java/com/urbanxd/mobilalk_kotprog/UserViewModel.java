package com.urbanxd.mobilalk_kotprog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void loadUser(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            userLiveData.setValue(null);
            return;
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database
                .collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        userLiveData.setValue(null);
                        return;
                    }

                    String firstname = documentSnapshot.getString("firstname");
                    String lastname = documentSnapshot.getString("lastname");

                    User user = new User(firebaseUser, firstname, lastname);
                    userLiveData.setValue(user);
                });

        database
            .collection("watermeters")
            .whereEqualTo("uuid", firebaseUser.getUid())
            .limit(1)
            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.isEmpty()) return;

                String waterMeterId = queryDocumentSnapshots.getDocuments().get(0).getId();
                WaterMeter waterMeter = new WaterMeter(waterMeterId);

                User currentUser = userLiveData.getValue();
                if(currentUser != null) {
                    currentUser.setWaterMeter(waterMeter);
                    userLiveData.setValue(currentUser);
                }
            });
    }
}
