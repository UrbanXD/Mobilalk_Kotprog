package com.urbanxd.mobilalk_kotprog.data.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeter;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.Callback;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.CallbackResult;

public class WaterMeterRepository {
    private final CollectionReference waterMeterCollection;
    private final WaterMeterStateRepository waterMeterStateRepository;

    public WaterMeterRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waterMeterCollection = db.collection("waterMeters");
        waterMeterStateRepository = new WaterMeterStateRepository();
    }

    public void getWaterMeter(String userId, Callback<WaterMeter> callback) {
        waterMeterCollection
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener(waterMeterQuery -> {
               if (waterMeterQuery.isEmpty()) {
                   callback.onComplete(new CallbackResult<>(null));
                   return;
               }

               String waterMeterId = waterMeterQuery.getDocuments().get(0).getId();
               WaterMeter waterMeter = new WaterMeter(waterMeterId, userId);

               waterMeterStateRepository.getWaterMeterStates(waterMeter, result -> {
                   waterMeter.addStates(result.data);

                   callback.onComplete(new CallbackResult<>(waterMeter));
               });
            });
    }

    public void createWaterMeter(String userId) {
        WaterMeter waterMeter = new WaterMeter(userId);

        waterMeterCollection
            .document(waterMeter.getId())
            .set(waterMeter)
            .addOnSuccessListener(aVoid -> {
                addNewStateToWaterMeter(new WaterMeterState(waterMeter.getId(), 0));
            });
    }

    public void addNewStateToWaterMeter(WaterMeterState state) {
        waterMeterStateRepository.createWaterMeterState(state);

    }
}