package com.urbanxd.mobilalk_kotprog.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.urbanxd.mobilalk_kotprog.data.model.WaterMeter;
import com.urbanxd.mobilalk_kotprog.data.model.WaterMeterState;
import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterRepository;

public class WaterMeterViewModel extends ViewModel {
    private final MutableLiveData<WaterMeter> waterMeterLiveData = new MutableLiveData<>();
    private final WaterMeterRepository waterMeterRepository = new WaterMeterRepository();

    public LiveData<WaterMeter> getWaterMeterLiveData() {
        return waterMeterLiveData;
    }

    public void loadWaterMeter(String userId) {
        waterMeterRepository.getWaterMeter(userId, result -> {
            waterMeterLiveData.setValue(result.data);
        });
    }

    public void addNewState(String userId, WaterMeterState state) {
        WaterMeter waterMeter = waterMeterLiveData.getValue();
        if (waterMeter == null) {
            waterMeter = new WaterMeter(state.getWaterMeterId(), userId);
        }
        waterMeter.addState(state);
        waterMeterLiveData.setValue(waterMeter);

        waterMeterRepository.addNewStateToWaterMeter(state);
    }
}