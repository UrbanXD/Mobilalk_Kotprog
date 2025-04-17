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
                    WaterMeterState newState = state.toObject(WaterMeterState.class);
                    if(newState == null) break;

                    newState.setId(state.getId());
                    waterMeterStates.add(newState);
                }

               callback.onComplete(new CallbackResult<>(waterMeterStates));
            });
    }

    public void createWaterMeterState(WaterMeterState waterMeterState) {
        createWaterMeterState(waterMeterState, result -> {});
    }

    public void createWaterMeterState(WaterMeterState waterMeterState, Callback<?> callback) {
        waterMeterStateCollection
            .document(waterMeterState.getId())
            .set(waterMeterState)
            .addOnSuccessListener(_void -> {
                callback.onComplete(null);
            });
    }

    public void editWaterMeterState(WaterMeterState waterMeterState, Callback<?> callback) {
        waterMeterStateCollection
            .document(waterMeterState.getId())
            .set(waterMeterState)
            .addOnSuccessListener(_void -> {
                callback.onComplete(null);
            });
    }

    public void deleteWaterMeterState(String id, Callback<?> callback) {
        waterMeterStateCollection
            .document(id)
            .delete()
            .addOnSuccessListener(_void -> {
                callback.onComplete(null);
            });
    }
}
