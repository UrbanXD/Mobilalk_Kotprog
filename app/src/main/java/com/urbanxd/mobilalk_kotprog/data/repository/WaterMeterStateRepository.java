package com.urbanxd.mobilalk_kotprog.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeter;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.Callback;
import com.urbanxd.mobilalk_kotprog.data.repository.callback.CallbackResult;
import com.urbanxd.mobilalk_kotprog.utils.StateBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
                ArrayList<Task<Void>> boundTasks = new ArrayList<>();

                if(stateQuery.isEmpty()) {
                    callback.onComplete(new CallbackResult<>(waterMeterStates));
                    return;
                }

                for (DocumentSnapshot state : stateQuery.getDocuments()) {
                    WaterMeterState newState = state.toObject(WaterMeterState.class);
                    if(newState == null) continue;

                    newState.setId(state.getId());
                    waterMeterStates.add(newState);

                    Task<Void> task = getStateBoundsTask(newState)
                        .continueWith(t -> {
                            StateBounds bounds = t.getResult();
                            newState.setStateBounds(bounds);

                            return null;
                        });
                    boundTasks.add(task);
                }

                Tasks.whenAllSuccess(boundTasks)
                    .addOnSuccessListener(ignored -> {
                        callback.onComplete(new CallbackResult<>(waterMeterStates));
                    });
            });
    }

    private Task<StateBounds> getStateBoundsTask(WaterMeterState waterMeterState) {
        Task<QuerySnapshot> minBoundTask = waterMeterStateCollection
            .whereEqualTo("waterMeterId", waterMeterState.getWaterMeterId())
            .whereLessThan("state", waterMeterState.getState())
            .orderBy("state", Query.Direction.DESCENDING)
            .limit(1)
            .get();

        Task<QuerySnapshot> maxBoundTask = waterMeterStateCollection
            .whereEqualTo("waterMeterId", waterMeterState.getWaterMeterId())
            .whereGreaterThan("state", waterMeterState.getState())
            .orderBy("state", Query.Direction.ASCENDING)
            .limit(1)
            .get();

        return Tasks
            .whenAllSuccess(minBoundTask, maxBoundTask)
            .continueWith(task -> {
                StateBounds stateBounds = new StateBounds();

                QuerySnapshot minSnapshot = (QuerySnapshot) task.getResult().get(0);
                QuerySnapshot maxSnapshot = (QuerySnapshot) task.getResult().get(1);

                if (!minSnapshot.isEmpty()) {
                    Long min = minSnapshot.getDocuments().get(0).getLong("state");
                    if (min != null) stateBounds.setMinBound(min);
                }

                if (!maxSnapshot.isEmpty()) {
                    Long max = maxSnapshot.getDocuments().get(0).getLong("state");
                    if (max != null) stateBounds.setMaxBound(max);
                }

                return stateBounds;
            });
    }

    public void isNewStateCreatedInTheLast10Minutes(String waterMeterId, Callback<Boolean> callback) {
        long fifteenMinutesAgoMillis = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15);
        Timestamp tenMinutesAgo = new Timestamp(fifteenMinutesAgoMillis / 1000, 0);
        waterMeterStateCollection
            .whereEqualTo("waterMeterId", waterMeterId)
            .whereGreaterThanOrEqualTo("date", tenMinutesAgo)
            .limit(1)
            .get()
            .addOnSuccessListener(stateQuery -> {
                callback.onComplete(new CallbackResult<>(!stateQuery.isEmpty()));
            })
            .addOnFailureListener(_void -> {
                callback.onComplete(new CallbackResult<>(true));
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
