package com.urbanxd.mobilalk_kotprog.data.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeter;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.Callback;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.CallbackResult;

import java.util.ArrayList;

public class WaterMeterStateRepository {
    private final CollectionReference waterMeterStateCollection;

    public WaterMeterStateRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waterMeterStateCollection = db.collection("waterMeterStates");
    }

    public WaterMeterState getLastWaterMeterState() {
        return null;
    }


    public void getWaterMeterStates(WaterMeter waterMeter, Callback<ArrayList<WaterMeterState>> callback) {
        waterMeterStateCollection
            .whereEqualTo("waterMeterId", waterMeter.getId())
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(stateQuery -> {
                ArrayList<WaterMeterState> waterMeterStates = new ArrayList<>();

                if(stateQuery.isEmpty()) {
                    callback.onComplete(new CallbackResult<>(waterMeterStates));
                    return;
                }
                for (DocumentSnapshot state : stateQuery.getDocuments()) {
                    waterMeterStates.add(state.toObject(WaterMeterState.class));
                }

               callback.onComplete(new CallbackResult<>(waterMeterStates));
            });
    }

    public void createWaterMeterState(WaterMeterState waterMeterState) {
        waterMeterStateCollection.add(waterMeterState);
    }
}
