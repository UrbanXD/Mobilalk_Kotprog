package com.urbanxd.mobilalk_kotprog;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class WaterMeter {
    private final String id;
    private final ArrayList<WaterMeterState> states = new ArrayList<>();

    public WaterMeter(String id) {
        this.id = id;

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database
            .collection("watermeterStates")
            .whereEqualTo("watermeterID", id)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.isEmpty()) return;

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    states.add(document.toObject(WaterMeterState.class));
                }
            });
    }

    public String getId() {
        return id;
    }

    public ArrayList<WaterMeterState> getStates() {
        return states;
    }
}
