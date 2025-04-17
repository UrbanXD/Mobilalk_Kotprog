package com.urbanxd.mobilalk_kotprog.data.repository;

import android.util.Log;

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

    public void getStateBounds(WaterMeterState waterMeterState, Callback<StateBounds> callback) {
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

        Tasks.whenAllSuccess(minBoundTask, maxBoundTask)
            .addOnSuccessListener(results -> {
                StateBounds stateBounds = new StateBounds();

                QuerySnapshot minSnapshot = (QuerySnapshot) results.get(0);
                QuerySnapshot maxSnapshot = (QuerySnapshot) results.get(1);

                if (!minSnapshot.isEmpty()) {
                    Long stateValue = minSnapshot.getDocuments().get(0).getLong("state");
                    if (stateValue != null) {
                        stateBounds.setMinBound(stateValue);
                    }
                }

                if (!maxSnapshot.isEmpty()) {
                    stateBounds.setMaxBound(maxSnapshot.getDocuments().get(0).getLong("state"));
                }

                Log.d("BORDER_VALUES", "" + waterMeterState.getState()  + "minValue: " + stateBounds.minBound + ", maxValue: " + stateBounds.maxBound);
                callback.onComplete(new CallbackResult<>(stateBounds));
            });
    }

    public void isNewStateCreatedInTheLast10Minutes(String waterMeterId, Callback<Boolean> callback) {
        long tenMinutesAgoMillis = System.currentTimeMillis() - (10 * 60 * 1000); // 10 perc
        Timestamp tenMinutesAgo = new Timestamp(tenMinutesAgoMillis / 1000, 0);
        waterMeterStateCollection
            .whereEqualTo("waterMeterId", waterMeterId)
            .whereGreaterThanOrEqualTo("date", tenMinutesAgo)
            .limit(1)
            .get()
            .addOnSuccessListener(stateQuery -> {
                boolean hasNewState = !stateQuery.isEmpty();
                Log.d("JOBSERVICE", "APAD FASZA " + stateQuery.size());
                callback.onComplete(new CallbackResult<>(hasNewState));            })
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
